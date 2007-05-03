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

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.crosswire.common.util.Logger;

/**
 * JobManager is responsible for creating jobs and informing listeners about
 * the progress they make to completion.
 * 
 * <p>Example code:
 * <pre>
 * final Thread worker = new Thread("DisplayPreLoader")
 * {
 *     public void run()
 *     {
 *         URL predicturl = Project.instance().getWritablePropertiesURL("save-name");
 *         Progress job = JobManager.createJob("Job Title", predicturl, this, true);
 * 
 *         try
 *         {
 *             job.setProgress("Step 1");
 *             ...
 *             job.setProgress("Step 2");
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
 * 
 * worker.setPriority(Thread.MIN_PRIORITY);
 * worker.start();
 * </pre>
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class JobManager
{
    /**
     * Prevent Instansiation
     */
    private JobManager()
    {
    }

    /**
     * Create a new Job
     */
    public static Progress createJob(String description, URI predictURI, Thread work, boolean fakeupdates)
    {
        Progress job = new Job(description, predictURI, work, fakeupdates ? Progress.UNKNOWN : 100);
        jobs.add(job);

        log.debug("job starting: " + job.getJobName()); //$NON-NLS-1$

        return job;
    }

    /**
     * Create a new Job
     */
    public static Progress createJob(String description, Thread work, boolean fakeupdates)
    {
        return createJob(description, null, work, fakeupdates);
    }

    /**
     * Create a new Job
     */
    public static Progress createJob(String description, URI predictURI, boolean fakeupdates)
    {
        return createJob(description, predictURI, null, fakeupdates);
    }

    /**
     * Create a new Job
     */
    public static Progress createJob(String description, boolean fakeupdates)
    {
        return createJob(description, null, null, fakeupdates);
    }

    /**
     * Add a listener to the list
     */
    public static synchronized void addWorkListener(WorkListener li)
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
    public static synchronized void removeWorkListener(WorkListener li)
    {
        if (listeners.contains(li))
        {
            List temp = new ArrayList();
            temp.addAll(listeners);
            temp.remove(li);
            listeners = temp;
        }
    }

    /**
     * Accessor for the currently known jobs
     */
    public static synchronized Set getJobs()
    {
        Set reply = new HashSet();
        reply.addAll(jobs);
        return reply;
    }

    /**
     * Inform the listeners that a title has changed.
     */
    protected static void fireWorkProgressed(Progress job)
    {
        final WorkEvent ev = new WorkEvent(job);

        // we need to keep the synchronized section very small to avoid deadlock
        // certainly keep the event dispatch clear of the synchronized block or
        // there will be a deadlock
        final List temp = new ArrayList();
        synchronized (JobManager.class)
        {
            temp.addAll(listeners);
        }

        // We ought only to tell listeners about jobs that are in our
        // list of jobs so we need to fire before delete.
        if (listeners != null)
        {
            Iterator iter = temp.iterator();
            while (iter.hasNext())
            {
                WorkListener worker = (WorkListener) iter.next();
                worker.workProgressed(ev);
            }
        }

        // Do we need to remove the job? Note that the section above will
        // proably execute after this so we will be firing events for jobs
        // that are no longer in our list of jobs. ho hum.
        synchronized (JobManager.class)
        {
            if (job.isFinished())
            {
                log.debug("job finished: " + job.getJobName()); //$NON-NLS-1$
                jobs.remove(job);
            }
        }
    }

    /**
     * List of listeners
     */
    private static List listeners = new ArrayList();

    /**
     * List of current jobs
     */
    private static Set jobs = new HashSet();

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(JobManager.class);
}
