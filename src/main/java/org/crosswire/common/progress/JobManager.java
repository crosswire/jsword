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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * JobManager is responsible for creating jobs and informing listeners about the
 * progress they make to completion.
 * 
 * <p>
 * Example code:
 * 
 * <pre>
 * final Thread worker = new Thread(&quot;DisplayPreLoader&quot;)
 * {
 *     public void run()
 *     {
 *         URL predictURI = Project.instance().getWritablePropertiesURI(&quot;save-name&quot;);
 *         Progress job = JobManager.createJob(&quot;Job Title&quot;, predictURI, this, true);
 *         try
 *         {
 *             job.setProgress(&quot;Step 1&quot;);
 *             ...
 *             job.setProgress(&quot;Step 2&quot;);
 *             ...
 *         }
 *         catch (Exception ex)
 *         {
 *             ...
 *             job.ignoreTimings();
 *         }
 *         finally
 *         {
 *             job.done();
 *         }
 *     }
 * };
 * worker.setPriority(Thread.MIN_PRIORITY);
 * worker.start();
 * </pre>
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public final class JobManager {
    /**
     * Prevent instantiation
     */
    private JobManager() {
    }

    /**
     * Create a new Job that cannot be canceled.
     * 
     * @param jobName the name of the Job
     * @return the job
     */
    public static Progress createJob(String jobName) {
        return createJob(UUID.randomUUID().toString(), jobName, null);
    }

    /**
     * Create a new Job that can be canceled.
     * 
     * @param jobID a unique identifier for the job
     * @param jobName the name of the Job
     * @param workerThread the thread on which this job runs
     * @return the job
     */
    public static Progress createJob(String jobID, String jobName, Thread workerThread) {
        Progress job = new Job(jobID, jobName, workerThread);
        jobs.add(job);

        log.debug("job starting: {}", job.getJobName());

        return job;
    }

    /**
     * Add a listener to the list
     * 
     * @param li the interested listener
     */
    public static void addWorkListener(WorkListener li) {
        listeners.add(li);
    }

    /**
     * Remove a listener from the list
     * 
     * @param li the disinterested listener
     */
    public static void removeWorkListener(WorkListener li) {
        listeners.remove(li);
    }

    /**
     * Accessor for the currently known jobs
     * 
     * @return an iterator over the jobs
     */
    public static Iterator<Progress> iterator() {
        return jobs.iterator();
    }

    /**
     * @return the number of current jobs
     */
    public static int getJobCount() {
        return jobs.size();
    }

    /**
     * Inform the listeners that a title has changed.
     * 
     * @param job the job that has made progress
     */
    protected static void fireWorkProgressed(Progress job) {
        final WorkEvent ev = new WorkEvent(job);

        // We ought only to tell listeners about jobs that are in our
        // list of jobs so we need to fire before delete.
        for (WorkListener worker : listeners) {
            worker.workProgressed(ev);
        }

        // Do we need to remove the job? Note that the section above will
        // probably execute after this so we will be firing events for jobs
        // that are no longer in our list of jobs. ho hum.
        if (job.isFinished()) {
            log.debug("job finished: {}", job.getJobName());
            jobs.remove(job);
        }
    }

    /**
     * List of listeners using thread safe list
     */
    private static List<WorkListener> listeners = new CopyOnWriteArrayList<WorkListener>();

    /**
     * List of current jobs
     */
    private static Set<Progress> jobs = new CopyOnWriteArraySet<Progress>();

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(JobManager.class);
}
