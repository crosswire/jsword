
package org.crosswire.common.progress.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.crosswire.common.progress.Job;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.progress.WorkEvent;
import org.crosswire.common.progress.WorkListener;
import org.crosswire.common.util.Logger;

/**
 * JobsViewPane is a large(ish) viewer for current jobs.
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
public class JobsViewPane extends JPanel implements WorkListener
{
    /**
     * Simple ctor
     */
    public JobsViewPane()
    {
        init();

        JobManager.addWorkListener(this);

        Set current = JobManager.getJobs();
        for (Iterator it = current.iterator(); it.hasNext();)
        {
            Job job = (Job) it.next();
            addJob(job);
        }
    }

    /**
     * GUI initializer
     */
    private void init()
    {
        lbl_nojobs.setText("No active jobs.");

        pnl_ijobs.setBorder(null);
        pnl_ijobs.setLayout(new GridBagLayout());
        
        pnl_ojobs.setLayout(new BorderLayout());
        pnl_ojobs.add(pnl_ijobs, BorderLayout.NORTH);
        
        scr_jobs.setBorder(null);
        scr_jobs.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scr_jobs.setViewportView(pnl_ojobs);

        this.setPreferredSize(new Dimension(500, 300));
        this.setLayout(new BorderLayout());
        this.add(scr_jobs, BorderLayout.CENTER);
        this.add(new JPanel(), BorderLayout.SOUTH);
        this.add(new JPanel(), BorderLayout.EAST);
        this.add(new JPanel(), BorderLayout.WEST);
        this.add(new JPanel(), BorderLayout.NORTH);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.WorkListener#workProgressed(org.crosswire.common.progress.WorkEvent)
     */
    public synchronized void workProgressed(WorkEvent ev)
    {
        Job job = ev.getJob();

        if (!jobs.containsKey(job))
        {
            // do we need an 'empty' label
            if (jobs.isEmpty())
            {
                removeEmptyLabel();
            }

            addJob(job);
        }

        updateJob(job);

        if (job.isFinished())
        {
            removeJob(job);

            // do we need an 'empty' label
            if (jobs.isEmpty())
            {
                addEmptyLabel();
            }
        }
    }

    /**
     * Create a new set of components for the new Job
     */
    private void addJob(final Job job)
    {
        int i = findEmptyPosition();
        log.debug("adding job to panel at "+i+": "+job.getJobDescription());

        JProgressBar progress = new JProgressBar();
        progress.setStringPainted(true);
        progress.setString("0%");
        progress.setToolTipText(job.getJobDescription());
        progress.setValue(0);

        JLabel label = new JLabel(job.getJobDescription() + ":");

        JButton cancel = new JButton("Cancel");
        if (!job.canInterrupt())
        {
            cancel.setEnabled(false);
        }
        cancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                job.interrupt();
            }
        });

        pnl_ijobs.add(label, new GridBagConstraints(0, i, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        pnl_ijobs.add(progress, new GridBagConstraints(1, i, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        pnl_ijobs.add(cancel, new GridBagConstraints(2, i, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.revalidate();

        JobData jobdata = new JobData(job, i, label, progress, cancel);
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
     * Update the job details because it have just progressed
     */
    private void updateJob(Job job)
    {
        JobData jobdata = (JobData) jobs.get(job);

        int percent = job.getPercent();
        jobdata.getProgress().setString(""+percent+"%");
        jobdata.getProgress().setToolTipText(job.getStateDescription());
        jobdata.getProgress().setValue(percent);
    }

    /**
     * Remove the set of components from the panel
     */
    private void removeJob(Job job)
    {
        JobData jobdata = (JobData) jobs.get(job);

        log.debug("removing job from panel at "+jobdata.getIndex()+": "+job.getJobDescription());

        positions.set(jobdata.getIndex(), null);
        jobs.remove(job);
        
        pnl_ijobs.remove(jobdata.getLabel());
        pnl_ijobs.remove(jobdata.getProgress());
        pnl_ijobs.remove(jobdata.getCancel());
        
        this.revalidate();

        jobdata.invalidate();
    }

    /**
     * Add the "no jobs" label
     */
    private void addEmptyLabel()
    {
        pnl_ijobs.add(lbl_nojobs, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        this.revalidate();
    }

    /**
     * Get rid of the "no jobs" label
     */
    private void removeEmptyLabel()
    {
        pnl_ijobs.remove(lbl_nojobs);
        this.revalidate();
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
    private static final Logger log = Logger.getLogger(JobsViewPane.class);

    /**
     * Map of Jobs to JobDatas
     */
    private Map jobs = new HashMap();

    /**
     * Array telling us what y position the jobs have in the window
     */
    private List positions = new ArrayList();

    /**
     * 
     */
    private JPanel pnl_ojobs = new JPanel();

    /**
     * 
     */
    private JPanel pnl_ijobs = new JPanel();

    /**
     * 
     */
    private JScrollPane scr_jobs = new JScrollPane();

    /**
     * 
     */
    private JLabel lbl_nojobs = new JLabel();

    /**
     * A simple struct to group information about a Job
     */
    private class JobData
    {
        /**
         * Simple ctor
         */
        public JobData(Job job, int index, JLabel label, JProgressBar progress, JButton cancel)
        {
            this.job = job;
            this.index = index;
            this.label = label;
            this.progress = progress;
            this.cancel = cancel;
        }

        /**
         * 
         */
        void invalidate()
        {
            this.job = null;
            this.label = null;
            this.progress = null;
            this.cancel = null;
            this.index = -1;
        }

        /**
         * 
         */
        Job getJob()
        {
            return job;
        }

        /**
         * 
         */
        JLabel getLabel()
        {
            return label;
        }

        /**
         * 
         */
        JProgressBar getProgress()
        {
            return progress;
        }

        /**
         * 
         */
        JButton getCancel()
        {
            return cancel;
        }

        /**
         * 
         */
        int getIndex()
        {
            return index;
        }

        private Job job = null;
        private JLabel label = null;
        private JProgressBar progress = null;
        private JButton cancel = null;
        private int index = -1;
    }
}
