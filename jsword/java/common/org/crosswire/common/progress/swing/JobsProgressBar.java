
package org.crosswire.common.progress.swing;

import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.crosswire.common.progress.Job;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.progress.WorkEvent;
import org.crosswire.common.progress.WorkListener;
import org.crosswire.common.util.Logger;

/**
 * JobsViewPane is a small JProgressBar based viewer for current jobs.
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
public class JobsProgressBar extends JPanel implements WorkListener
{
    /**
     * Simple ctor
     */
    public JobsProgressBar(boolean small)
    {
        if (small)
        {
            // They start of at 15pt (on Windows at least)
            font = new Font("SansSerif", Font.PLAIN, 10);
        }

        JobManager.addWorkListener(this);

        Set current = JobManager.getJobs();
        for (Iterator it = current.iterator(); it.hasNext();)
        {
            Job job = (Job) it.next();
            addJob(job);
        }

        this.setLayout(new GridLayout(1, 0, 2, 0));
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.WorkListener#workProgressed(org.crosswire.common.progress.WorkEvent)
     */
    public synchronized void workProgressed(WorkEvent ev)
    {
        Job job = ev.getJob();

        if (!jobs.containsKey(job))
        {
            addJob(job);
        }

        updateJob(job);

        if (job.isFinished())
        {
            removeJob(job);
        }
    }

    /**
     * Create a new set of components for the new Job
     */
    private synchronized void addJob(Job job)
    {
        int i = findEmptyPosition();
        log.debug("adding job to panel at "+i+": "+job.getJobDescription());

        JProgressBar progress = new JProgressBar();
        progress.setStringPainted(true);
        progress.setToolTipText(job.getJobDescription());
        if (font != null)
        {
            progress.setFont(font);
        }
        // Dimension preferred = progress.getPreferredSize();
        // preferred.width = 50;
        // progress.setPreferredSize(preferred);

        this.add(progress, i);
        this.revalidate();

        JobData jobdata = new JobData(job, i, progress);
        jobs.put(job, jobdata);
        if (i >= positions.size())
        {
            positions.add(jobdata);
        }
        else
        {
            positions.set(i, jobdata);
        }
    }

    /**
     * Update the job details because it has just progressed
     */
    private synchronized void updateJob(Job job)
    {
        JobData jobdata = (JobData) jobs.get(job);

        int percent = job.getPercent();
        jobdata.getProgress().setString(job.getStateDescription()+": ("+percent+"%)");
        jobdata.getProgress().setValue(percent);
    }

    /**
     * Remove the set of components from the panel
     */
    private synchronized void removeJob(Job job)
    {
        JobData jobdata = (JobData) jobs.get(job);

        positions.set(jobdata.getIndex(), null);
        jobs.remove(job);
        log.debug("removing job from panel: "+jobdata.getJob().getJobDescription());

        this.remove(jobdata.getProgress());
        this.revalidate();
        jobdata.invalidate();
    }

    /**
     * Where is the next hole in the positions array
     */
    private int findEmptyPosition()
    {
        int i = 0;
        while (true)
        {
            if (i >= positions.size())
            {
                break;
            }

            if (positions.get(i) == null)
            {
                break;
            }

            i++;
        }

        return i;
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(JobsProgressBar.class);

    /**
     * Where we store the currently displayed jobs
     */
    private Map jobs = new HashMap();

    /**
     * Array telling us what y position the jobs have in the window
     */
    private List positions = new ArrayList();

    /**
     * The font for the progress-bars
     */
    private Font font;

    /**
     * A simple struct to group information about a Job
     */
    private class JobData
    {
        /**
         * Simple ctor
         */
        JobData(Job job, int index, JProgressBar progress)
        {
            this.job = job;
            this.progress = progress;
            this.index = index;
        }

        /**
         * ensure we can't be used again
         */
        void invalidate()
        {
            job = null;
            progress = null;
            index = -1;
        }

        /**
         * Accessor for the Job
         */
        Job getJob()
        {
            return job;
        }

        /**
         * Accessor for the Progress Bar
         */
        JProgressBar getProgress()
        {
            return progress;
        }

        /**
         * Accessor for the index
         */
        int getIndex()
        {
            return index;
        }

        private Job job = null;
        private JProgressBar progress = null;
        private int index = -1;
    }
}
