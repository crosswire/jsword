
package org.crosswire.common.progress;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.SwingUtilities;

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
 *         URL predicturl = Project.instance().getWritablePropertiesURL("display");
 *         Job job = JobManager.createJob("Display Pre-load", predicturl, this, true);
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
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class JobManager
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
    public static Job createJob(String description, URL predicturl, Thread work, boolean fakeupdates)
    {
        Job job = new Job(description, predicturl, work, fakeupdates);
        jobs.add(job);

        log.debug("job starting: "+job.getJobDescription());

        return job;
    }

    /**
     * Create a new Job
     */
    public static Job createJob(String description, Thread work, boolean fakeupdates)
    {
        return createJob(description, null, work, fakeupdates);
    }

    /**
     * Create a new Job
     */
    public static Job createJob(String description, URL predicturl, boolean fakeupdates)
    {
        return createJob(description, predicturl, null, fakeupdates);
    }

    /**
     * Create a new Job
     */
    public static Job createJob(String description, boolean fakeupdates)
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
    protected static void fireWorkProgressed(Job job, boolean predicted)
    {
        final WorkEvent ev = new WorkEvent(job, predicted);

        // we need to keep the synchronized section very small to avoid deadlock
        // certainly keep the event dispatch clear of the synchronized block or
        // there will be a deadlock
        final List temp = new ArrayList();
        synchronized (JobManager.class)
        {
            temp.addAll(listeners);
        }

        Runnable firer = new Runnable()
        {
            public void run()
            {
                // We ought only to tell listeners about jobs that are in our
                // list of jobs so we need to fire before delete.
                if (listeners != null)
                {
                    int count = temp.size();
                    for (int i = 0; i < count; i++)
                    {
                        ((WorkListener) temp.get(i)).workProgressed(ev);
                    }
                }
            }
        };
        
        try
        {
            if (SwingUtilities.isEventDispatchThread())
            {
                firer.run();
            }
            else
            {
                try
                {
                    SwingUtilities.invokeAndWait(firer);
                }
                catch (Exception ex)
                {
                    log.warn("failed to propogate work progressed message", ex);
                }
            }
        }
        catch (Exception ex)
        {
            // This can happen in a headerless environment, and we don't care
            // because we never need to invoke there, so just ignore.
            log.debug("ignoring error (assuming headerless): "+ex);
            firer.run();
        }

        // Do we need to remove the job? Note that the section above will
        // proably execute after this so we will be firing events for jobs
        // that are no longer in our list of jobs. ho hum.
        synchronized (JobManager.class)
        {
            if (job.isFinished())
            {
                log.debug("job finished: "+job.getJobDescription());
                jobs.remove(job);
            }
        }
    }

    /**
     * List of listeners
     */
    protected static List listeners = new ArrayList();

    /**
     * List of current jobs
     */
    protected static Set jobs = new HashSet();

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(JobManager.class);
}
