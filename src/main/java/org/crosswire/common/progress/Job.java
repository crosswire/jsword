/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.common.progress;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.PropertyMap;
import org.crosswire.jsword.JSMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Generic method of keeping track of Threads and monitoring their progress.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
 */
public final class Job implements Progress {
    /**
     * Create a new Job. This will automatically fire a workProgressed event to
     * all WorkListeners, with the work property of this job set to 0.
     * 
     * @param jobID the job identifier
     * @param jobName
     *            Short description of this job
     * @param worker
     *            Optional thread to use in request to stop worker
     */
    protected Job(String jobID, String jobName, Thread worker) {
        this.jobName = jobName;
        this.jobID = jobID;
        this.workerThread = worker;
        this.listeners = new ArrayList<WorkListener>();
        this.cancelable = workerThread != null;
        this.jobMode = ProgressMode.PREDICTIVE;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#beginJob(java.lang.String)
     */
    public void beginJob(String sectionName) {
        beginJob(sectionName, 100);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#beginJob(java.lang.String, int)
     */
    public void beginJob(String sectionName, int totalWork) {
        if (this.finished) {
            return;
        }

        synchronized (this) {
            finished = false;
            currentSectionName = sectionName;
            totalUnits = totalWork;
            jobMode = totalUnits == 100 ? ProgressMode.PERCENT : ProgressMode.UNITS;
        }

        // Report that the Job has started.
        JobManager.fireWorkProgressed(this);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#beginJob(java.lang.String, java.net.URI)
     */
    public void beginJob(String sectionName, URI predictURI) {
        if (finished) {
            return;
        }

        synchronized (this) {
            currentSectionName = sectionName;
            predictionMapURI = predictURI;
            jobMode = ProgressMode.PREDICTIVE;
            startTime = System.currentTimeMillis();

            fakingTimer = new Timer();
            fakingTimer.schedule(new PredictTask(), 0, REPORTING_INTERVAL);

            // Load currentPredictionMap. It's not a disaster if it doesn't load
            totalUnits = loadPredictions();

            // There were no prior predictions so punt.
            if (totalUnits == Progress.UNKNOWN) {
                // if we have nothing to go on use our assumption
                totalUnits = EXTRA_TIME;
                jobMode = ProgressMode.UNKNOWN;
            }

            // And the predictions for next time
            nextPredictionMap = new HashMap<String, Integer>();
        }

        // Report that the Job has started.
        JobManager.fireWorkProgressed(this);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#getJobName()
     */
    public synchronized String getJobName() {
        return jobName;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#getProgressMode()
     */
    public ProgressMode getProgressMode() {
        return jobMode;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#getTotalWork()
     */
    public synchronized int getTotalWork() {
        return totalUnits;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#setTotalWork(int)
     */
    public void setTotalWork(int totalWork) {
        this.totalUnits = totalWork;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#getWork()
     */
    public int getWork() {
        return percent;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#setWork(int)
     */
    public void setWork(int work) {
        setWorkDone(work);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#getWorkDone()
     */
    public int getWorkDone() {
        return workUnits;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#setWork(int)
     */
    public void setWorkDone(int work) {
        if (finished) {
            return;
        }

        synchronized (this) {
            if (workUnits == work) {
                return;
            }

            workUnits = work;

            int oldPercent = percent;
            percent = 100 * workUnits / totalUnits;
            if (oldPercent == percent) {
                return;
            }
        }

        JobManager.fireWorkProgressed(this);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#incrementWorkDone(int)
     */
    public void incrementWorkDone(int step) {
        if (finished) {
            return;
        }

        synchronized (this) {
            workUnits += step;

            int oldPercent = percent;
            // use long in arithmetic to avoid integer overflow 
            percent = (int) (100L * workUnits / totalUnits);
            if (oldPercent == percent) {
                return;
            }
        }

        JobManager.fireWorkProgressed(this);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#getSectionName()
     */
    public String getSectionName() {
        return currentSectionName;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#setSectionName(java.lang.String)
     */
    public void setSectionName(String sectionName) {
        if (finished) {
            return;
        }

        boolean doUpdate = false;
        synchronized (this) {
            // If we are in some kind of predictive mode, then measure progress toward the expected end.
            if (jobMode == ProgressMode.PREDICTIVE || jobMode == ProgressMode.UNKNOWN) {
                doUpdate = updateProgress(System.currentTimeMillis());

                // We are done with the current section and are starting another
                // So record the length of the last section
                if (nextPredictionMap != null) {
                    nextPredictionMap.put(currentSectionName, Integer.valueOf(workUnits));
                }
            }

            currentSectionName = sectionName;
        }

        // Don't automatically tell listeners that the label changed.
        // Only do so if it is time to do an update.
        if (doUpdate) {
            JobManager.fireWorkProgressed(this);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#done()
     */
    public void done() {
        // TRANSLATOR: This shows up in a progress bar when progress is finished.
        String sectionName = JSMsg.gettext("Done");

        synchronized (this) {
            finished = true;

            currentSectionName = sectionName;

            // Turn off the timer
            if (fakingTimer != null) {
                fakingTimer.cancel();
                fakingTimer = null;
            }

            workUnits = totalUnits;
            percent = 100;

            if (nextPredictionMap != null) {
                nextPredictionMap.put(currentSectionName, Integer.valueOf((int) (System.currentTimeMillis() - startTime)));
            }
        }

        // Report that the job is done.
        JobManager.fireWorkProgressed(this);

        synchronized (this) {
            if (predictionMapURI != null) {
                savePredictions();
            }
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#cancel()
     */
    public void cancel() {
        if (!finished) {
            ignoreTimings();
            done();
            if (workerThread != null) {
                workerThread.interrupt();
            }
        }
    }

   /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#isFinished()
     */
    public boolean isFinished() {
        return finished;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#isCancelable()
     */
    public boolean isCancelable() {
        return cancelable;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#setCancelable(boolean)
     */
    public void setCancelable(boolean newInterruptable) {
        if (workerThread == null || finished) {
            return;
        }
        cancelable = newInterruptable;
        fireStateChanged();
    }

    /**
     * Add a listener to the list
     * 
     * @param li the interested listener
     */
    public synchronized void addWorkListener(WorkListener li) {
        List<WorkListener> temp = new ArrayList<WorkListener>();
        temp.addAll(listeners);

        if (!temp.contains(li)) {
            temp.add(li);
            listeners = temp;
        }
    }

    /**
     * Remote a listener from the list
     * 
     * @param li the disinterested listener
     */
    public synchronized void removeWorkListener(WorkListener li) {
        if (listeners.contains(li)) {
            List<WorkListener> temp = new ArrayList<WorkListener>();
            temp.addAll(listeners);
            temp.remove(li);
            listeners = temp;
        }
    }

    protected void fireStateChanged() {
        final WorkEvent ev = new WorkEvent(this);

        // we need to keep the synchronized section very small to avoid deadlock
        // certainly keep the event dispatch clear of the synchronized block or
        // there will be a deadlock
        final List<WorkListener> temp = new ArrayList<WorkListener>();
        synchronized (this) {
            if (listeners != null) {
                temp.addAll(listeners);
            }
        }

        // We ought only to tell listeners about jobs that are in our
        // list of jobs so we need to fire before delete.
        int count = temp.size();
        for (int i = 0; i < count; i++) {
            temp.get(i).workStateChanged(ev);
        }
    }

    /**
     * Get estimated the percent progress
     * 
     * @param now the current point in progress
     * @return true if there is an update to progress.
     */
    protected synchronized boolean updateProgress(long now) {
        int oldPercent = percent;
        workUnits = (int) (now - startTime);

        // Are we taking more time than expected?
        // Then we are at 100%
        if (workUnits > totalUnits) {
            workUnits = totalUnits;
            percent = 100;
        } else {
            percent = 100 * workUnits / totalUnits;
        }
        return oldPercent != percent;
    }

    /**
     * Load the predictive timings if any
     * 
     * @return the length of progress
     */
    private int loadPredictions() {
        int maxAge = UNKNOWN;
        try {
            currentPredictionMap = new HashMap<String, Integer>();
            PropertyMap temp = NetUtil.loadProperties(predictionMapURI);

            // Determine the predicted time from the current prediction map
            for (String title : temp.keySet()) {
                String timestr = temp.get(title);

                try {
                    Integer time = Integer.valueOf(timestr);
                    currentPredictionMap.put(title, time);

                    // if this time is later than the latest
                    int age = time.intValue();
                    if (maxAge < age) {
                        maxAge = age;
                    }
                } catch (NumberFormatException ex) {
                    log.error("Time format error", ex);
                }
            }
        } catch (IOException ex) {
            log.debug("Failed to load prediction times - guessing");
        }

        return maxAge;
    }

    /**
     * Save the known timings to a properties file.
     */
    private void savePredictions() {
        // Now we know the start and the end we can convert all times to
        // percents
        PropertyMap predictions = new PropertyMap();
        for (String sectionName : nextPredictionMap.keySet()) {
            Integer age = nextPredictionMap.get(sectionName);
            predictions.put(sectionName, age.toString());
        }

        // And save. It's not a disaster if this goes wrong
        try {
            NetUtil.storeProperties(predictions, predictionMapURI, "Predicted Startup Times");
        } catch (IOException ex) {
            log.error("Failed to save prediction times", ex);
        }
    }

    /**
     * Typically called from in a catch block, this ensures that we don't save
     * the timing file because we have a messed up run.
     */
    private synchronized void ignoreTimings() {
        predictionMapURI = null;
    }

    private static final int REPORTING_INTERVAL = 100;

    /**
     * The amount of extra time if the predicted time was off and more time is needed.
     */
    private static final int EXTRA_TIME = 2 * REPORTING_INTERVAL;

    /**
     * The type of job being performed. This is used to simplify code.
     */
    private ProgressMode jobMode;

    /**
     * Total amount of work to do.
     */
    private int totalUnits;

    /**
     * Does this job allow interruptions?
     */
    private boolean cancelable;

    /**
     * Have we just finished?
     */
    private boolean finished;

    /**
     * The amount of work done against the total.
     */
    private int workUnits;

    /**
     * The officially reported progress
     */
    private int percent;

    /**
     * A short descriptive phrase
     */
    private String jobName;

    private final String jobID;
    /**
     * Optional thread to monitor progress
     */
    private Thread workerThread;

    /**
     * Description of what we are doing
     */
    private String currentSectionName;

    /**
     * The URI to which we load and save timings
     */
    private URI predictionMapURI;

    /**
     * The timings loaded from where they were saved after the last run
     */
    private Map<String, Integer> currentPredictionMap;

    /**
     * The timings as measured this time
     */
    private Map<String, Integer> nextPredictionMap;

    /**
     * When did this job start? Measured in milliseconds since beginning of epoch.
     */
    private long startTime;

    /**
     * The timer that lets us post fake progress events.
     */
    private Timer fakingTimer;

    /**
     * People that want to know about "cancelable" changes
     */
    private List<WorkListener> listeners;

    /**
     * The Job ID associated with this job
     * @return the job ID
     */
    public String getJobID() {
        return jobID;
    }

    /**
     * So we can fake progress for Jobs that don't tell us how they are doing
     */
    final class PredictTask extends TimerTask {
        /* (non-Javadoc)
         * @see java.util.TimerTask#run()
         */
        @Override
        public void run() {
            if (updateProgress(System.currentTimeMillis())) {
                JobManager.fireWorkProgressed(Job.this);
            }
        }
    }

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(Job.class);
}
