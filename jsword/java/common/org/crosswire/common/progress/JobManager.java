
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class JobManager
{
    /**
     * Create a new Job
     */
    public static Job createJob(String description, URL predicturl, Thread work)
    {
        return new Job(description, predicturl, work);
    }

    /**
     * Create a new Job
     */
    public static Job createJob(String description, Thread work)
    {
        return new Job(description, null, work);
    }

    /**
     * Create a new Job
     */
    public static Job createJob(String description, URL predicturl)
    {
        return new Job(description, predicturl, null);
    }

    /**
     * Create a new Job
     */
    public static Job createJob(String description)
    {
        return new Job(description, null, null);
    }

    /**
     * Create a test job
     */
    public static void createTestJob(final long millis)
    {
        final Thread test = new Thread()
        {
            /* (non-Javadoc)
             * @see java.lang.Thread#run()
             */
            public synchronized void run()
            {
                Job job = JobManager.createJob("Test Job", Thread.currentThread());

                job.setProgress(0, "Step 0/"+STEPS);
                log.debug("starting test job:");

                for (int i=1; i<=STEPS && !Thread.interrupted(); i++)
                {
                    try
                    {
                        wait(millis/STEPS);
                    }
                    catch (InterruptedException ex)
                    {
                    }

                    job.setProgress((i * 100) / STEPS, "Step "+i+"/"+STEPS);
                }

                job.done();
                log.debug("finishing test job:");
            }
            private final static int STEPS = 50;
        };
        test.start();
    }

    /**
     * Add a listener to the list
     */
    public static synchronized void addWorkListener(WorkListener li)
    {
        List temp = new ArrayList();
        if (listeners == null)
        {
            temp.add(li);
            listeners = temp;
        }
        else
        {
            temp.addAll(listeners);

            if (!temp.contains(li))
            {
                temp.add(li);
                listeners = temp;
            }
        }
    }

    /**
     * Remote a listener from the list
     */
    public static synchronized void removeWorkListener(WorkListener li)
    {
        if (listeners != null && listeners.contains(li))
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
    public static Set getJobs()
    {
        Set reply = new HashSet();
        reply.addAll(jobs);
        return reply;
    }

    /**
     * Allow Jobs to relay to us how they are doing
     */
    protected static void setProgress(WorkEvent ev)
    {
        Job job = ev.getJob();
        if (ev.isFinished())
        {
            jobs.remove(job);
        }
        else
        {
            // the job is not done so check it is in the list
            jobs.add(job);
        }

        try
        {
            Runnable setter = new SplashUpdater(ev);
            if (SwingUtilities.isEventDispatchThread())
            {
                setter.run();
            }
            else
            {
                SwingUtilities.invokeAndWait(setter);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(JobManager.class);

    /**
     * List of listeners
     */
    protected static List listeners;

    /**
     * List of current jobs
     */
    private static Set jobs = new HashSet();

    /**
     * A class to update the dialog with progress.
     */
    private static final class SplashUpdater implements Runnable
    {
        SplashUpdater(WorkEvent ev)
        {
            this.ev = ev;
        }

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            fireWorkProgressed();
        }

        /**
         * Inform the listeners that a title has changed. This was static within
         * JobManager itself, but it feels less open to abuse here.
         */
        protected void fireWorkProgressed()
        {
            if (listeners != null)
            {
                List temp = listeners;

                int count = temp.size();
                for (int i = 0; i < count; i++)
                {
                    ((WorkListener) temp.get(i)).workProgressed(ev);
                }
            }
        }

        private WorkEvent ev;
    }
}
