/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.common.progress;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;

/**
 * A Generic method of keeping track of Threads and monitoring their progress.
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class Job implements Progress
{
    /**
     * Create a new Job. This will automatically fire a workProgressed event to
     * all WorkListeners, with the work property of this job set to 0.
     * @param description Short description of this job
     * @param predictURI Optional URI to save/load prediction times from
     * @param worker Optional thread to use in request to stop worker
     * @param totalWork the size of the work to do
     */
    protected Job(String description, URI predictURI, Thread worker, int totalWork)
    {
        this.predictURI = predictURI;
        this.workerThread = worker;
        this.listeners = new ArrayList();
        this.start = -1;
        this.predictedLength = -1;
        beginJob(description, totalWork);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#beginJob(java.lang.String, int)
     */
    public void beginJob(String name, int workToDo)
    {
        if (this.finished)
        {
            return;
        }

        synchronized (this)
        {
            this.totalWork = workToDo;
            this.sectionName = name;
            this.jobName = name;
            this.work = 0;
            this.finished = false;
            this.cancelable = workerThread != null;

            if (totalWork == UNKNOWN)
            {
                updater = new Timer();
                updater.schedule(new PredictTask(), 0, 100);
            }

            // Set-up the timings files. It's not a disaster if it doesn't load
            if (predictURI != null)
            {
                loadPredictions();
            }

            // And the predictions for next time
            current = new HashMap();
            start = System.currentTimeMillis();
        }
        JobManager.fireWorkProgressed(this);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#getTotalWork()
     */
    public synchronized int getTotalWork()
    {
        return totalWork;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#setSectionName(java.lang.String)
     */
    public void setSectionName(String statedesc)
    {
        if (this.finished)
        {
            return;
        }

        boolean doUpdate = false;
        synchronized (this)
        {
            this.sectionName = statedesc;

            doUpdate = updater != null;
            if (doUpdate)
            {
                if (predictedLength != 0)
                {
                    setWork(100 * getAgeFromMap(predicted, statedesc) / predictedLength);
                }
                else
                {
                    setWork(0);
                }
            }
            predictSection(statedesc);

            current.put(statedesc, new Integer((int) (System.currentTimeMillis() - start)));
        }

        if (doUpdate)
        {
            JobManager.fireWorkProgressed(this);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#setWork(int)
     */
    public void setWork(int work)
    {
        if (this.finished)
        {
            return;
        }

        synchronized (this)
        {
            if (this.work == work)
            {
                return;
            }

            this.work = work;

            predictSection(sectionName);

            current.put(sectionName, new Integer((int) (System.currentTimeMillis() - start)));
        }

        JobManager.fireWorkProgressed(this);
    }

    /**
     * We have moved onto another section so update the percentage complete
     * and the section title.
     */
    public void setProgress(int work, String statedesc)
    {
        setSectionName(statedesc);
        setWork(work);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#done()
     */
    public void done()
    {
        synchronized (this)
        {
            finished = true;
            sectionName = UserMsg.DONE.toString();
            work = 100;

            if (updater != null)
            {
                updater.cancel();
                updater = null;
            }

            current.put(sectionName, new Integer((int) (System.currentTimeMillis() - start)));
        }

        JobManager.fireWorkProgressed(this);

        synchronized (this)
        {
            if (predictURI != null)
            {
                savePredictions();
            }
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#getSectionName()
     */
    public synchronized String getSectionName()
    {
        return sectionName;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#cancel()
     */
    public void cancel()
    {
        if (!finished)
        {
            ignoreTimings();
            done();
            if (workerThread != null)
            {
                workerThread.interrupt();
            }
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#isCancelable()
     */
    public boolean isCancelable()
    {
        return cancelable;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#setCancelable(boolean)
     */
    public void setCancelable(boolean newInterruptable)
    {
        if (workerThread == null || finished)
        {
            return;
        }
        cancelable = newInterruptable;
        fireStateChanged();
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#isFinished()
     */
    public boolean isFinished()
    {
        return finished;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#getWork()
     */
    public synchronized int getWork()
    {
        return work;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.Progress#getJobName()
     */
    public synchronized String getJobName()
    {
        return jobName;
    }

    /**
     * Typically called from in a catch block, this ensures that we don't save
     * the timing file because we have a messed up run.
     */
    private synchronized void ignoreTimings()
    {
        predictURI = null;
    }

    /**
     * Add a listener to the list
     */
    public synchronized void addWorkListener(WorkListener li)
    {
        List temp = new ArrayList();
        temp.addAll(listeners);

        if (!temp.contains(li))
        {
            temp.add(li);
            listeners = temp;
        }
    }

    /**
     * Remote a listener from the list
     */
    public synchronized void removeWorkListener(WorkListener li)
    {
        if (listeners.contains(li))
        {
            List temp = new ArrayList();
            temp.addAll(listeners);
            temp.remove(li);
            listeners = temp;
        }
    }

    protected void fireStateChanged()
    {
        final WorkEvent ev = new WorkEvent(this);

        // we need to keep the synchronized section very small to avoid deadlock
        // certainly keep the event dispatch clear of the synchronized block or
        // there will be a deadlock
        final List temp = new ArrayList();
        synchronized (this)
        {
            if (listeners != null)
            {
                temp.addAll(listeners);
            }
        }

        // We ought only to tell listeners about jobs that are in our
        // list of jobs so we need to fire before delete.
        int count = temp.size();
        for (int i = 0; i < count; i++)
        {
            ((WorkListener) temp.get(i)).workStateChanged(ev);
        }
    }

    /**
     * Predict a percentage complete
     */
    private synchronized int getAgeFromMap(Map props, String message)
    {
        if (props == null)
        {
            return 0;
        }

        Integer time = (Integer) props.get(message);
        if (time != null)
        {
            return time.intValue();
        }

        return 0;
    }

    /**
     * Get estimated the percent progress, extrapolating between sections
     */
    protected synchronized void guessProgress()
    {
        long now = System.currentTimeMillis();

        if (now < sectionStart)
        {
            log.warn("now before started: now=" + new Date(now) + " started=" + new Date(sectionStart)); //$NON-NLS-1$ //$NON-NLS-2$
            return;
        }

        if (now == sectionStart)
        {
            return;
        }

        if (now > sectionEnd)
        {
            // the prediction went wrong and we are ahead of ourselves
            work = percentEnd;
            return;
        }

        // how long is this section
        int sectlen = (int) (sectionEnd - sectionStart);
        // what percent of the way through it are we?
        int sectpc = (int) (100 * (now - sectionStart) / sectlen);
        // so what do we need to add to the current percentage
        int boost = sectpc * (percentEnd - work) / 100;
        // so we guess at progress at:
        int total = work + boost;
        // but check this is not more than 100
        total = total <= 100 ? total : 100;

        work = total;
    }

    /**
     * Predict a percentage complete
     */
    private synchronized void predictSection(String message)
    {
        sectionStart = System.currentTimeMillis();

        // if we have nothing to go on assume 10 sections of 10 sec each.
        if (predicted == null || predictedLength == 0)
        {
            sectionEnd = 10000;
            percentEnd = 10;
            return;
        }

        // from the predictions get this section starts and ends and the final finish
        int predsectstart = getAgeFromMap(predicted, sectionName);
        int predsectend = Integer.MAX_VALUE;

        // find better values for predsectend and predallend
        Iterator iter = predicted.keySet().iterator();
        while (iter.hasNext())
        {
            String title = (String) iter.next();
            int age = ((Integer) predicted.get(title)).intValue();

            // if this is a later section (than the current) but early than the current earliest
            if (age > predsectstart && age < predsectend)
            {
                predsectend = age;
            }
        }
        int predsecttime = predsectend - predsectstart;

        sectionEnd = sectionStart + predsecttime;

        // And what is the end percentage?
        int pcstart = 100 * predsectstart / predictedLength;
        int pcend = 100 * predsectend / predictedLength;
        int pcdiff = pcend - pcstart;

        percentEnd = work + pcdiff;

        log.debug("Predicting " + predsecttime + "ms (" + work + '-' + percentEnd + "%) for section " + message); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    /**
     * Load the predictive timings if any
     */
    private synchronized void loadPredictions()
    {
        try
        {
            predicted = new HashMap();
            Properties temp = NetUtil.loadProperties(predictURI);

            Iterator iter = temp.keySet().iterator();
            while (iter.hasNext())
            {
                String title = (String) iter.next();
                String timestr = temp.getProperty(title);

                try
                {
                    Integer time = new Integer(timestr);
                    predicted.put(title, time);

                    // if this time is later than the latest
                    int age = time.intValue();
                    if (age > predictedLength)
                    {
                        predictedLength = age;
                    }
                }
                catch (NumberFormatException ex)
                {
                    log.error("Time format error", ex); //$NON-NLS-1$
                }
            }
        }
        catch (IOException ex)
        {
            log.debug("Failed to load prediction times - guessing"); //$NON-NLS-1$
        }
    }

    /**
     * Save the known timings to a properties file.
     */
    private synchronized void savePredictions()
    {
        // We need to create a new prediction file. Work out the end point
        long end = start;
        Iterator iter = current.keySet().iterator();
        while (iter.hasNext())
        {
            String message = (String) iter.next();
            int age = getAgeFromMap(current, message);
            if (age > end)
            {
                end = age;
            }
        }
        //long length = end - start;

        // Now we know the start and the end we can convert all times to percents
        Properties predictions = new Properties();
        iter = current.keySet().iterator();
        while (iter.hasNext())
        {
            String message = (String) iter.next();
            int age = getAgeFromMap(current, message);
            predictions.setProperty(message, Integer.toString(age));
        }

        // And save. It's not a disaster if this goes wrong
        try
        {
            NetUtil.storeProperties(predictions, predictURI, "Predicted Startup Times"); //$NON-NLS-1$
        }
        catch (IOException ex)
        {
            log.error("Failed to save prediction times", ex); //$NON-NLS-1$
        }
    }

    /**
     * Total amount of work to do.
     */
    private int totalWork;

    /**
     * Does this job allow interruptions?
     */
    private boolean cancelable;

    /**
     * Have we just finished?
     */
    private boolean finished;

    /**
     * The officially reported progress
     */
    private int work;

    /**
     * When do we expect this section to end
     */
    private long sectionEnd;

    /**
     * When did this section start?
     */
    private long sectionStart;

    /**
     * The percentage at the end of this section
     */
    private int percentEnd;

    /**
     * A short descriptive phrase
     */
    private String jobName;

    /**
     * Optional thread to monitor progress
     */
    private Thread workerThread;

    /**
     * Description of what we are doing
     */
    private String sectionName;

    /**
     * When did this job start?
     */
    private long start;

    /**
     * The timings as measured this time
     */
    private Map current;

    /**
     * The timings loaded from where they were saved after the last run
     */
    private Map predicted;

    /**
     * How long to we predict this job is going to last?
     */
    private int predictedLength;

    /**
     * The URI to which we load and save timings
     */
    private URI predictURI;

    /**
     * The timer that lets us post fake progress events
     */
    private Timer updater;

    /**
     * People that want to know about "cancelable" changes
     */
    private List listeners;

    /**
     * So we can fake progress for Jobs that don't tell us how they are doing
     */
    final class PredictTask extends TimerTask
    {
        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        /* @Override */
        public void run()
        {
            guessProgress();
            JobManager.fireWorkProgressed(Job.this);
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3256721784160924983L;
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(Job.class);
}
