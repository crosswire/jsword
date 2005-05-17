/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
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
import javax.swing.SwingUtilities;

import org.crosswire.common.progress.Job;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.progress.WorkEvent;
import org.crosswire.common.progress.WorkListener;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.util.Logger;

/**
 * JobsViewPane is a large(ish) viewer for current jobs.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
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
        for (Iterator it = current.iterator(); it.hasNext(); )
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
        noJobLabel = new JLabel(Msg.NO_JOBS.toString());
        jobs = new HashMap();
        positions = new ArrayList();
        jobsPanel = new JPanel(new GridBagLayout());
        jobsPanel.setBorder(null);

        JPanel pnl = new JPanel(new BorderLayout());
        pnl.add(jobsPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(pnl);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        this.setPreferredSize(new Dimension(500, 300));
        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(new JPanel(), BorderLayout.SOUTH);
        this.add(new JPanel(), BorderLayout.EAST);
        this.add(new JPanel(), BorderLayout.WEST);
        this.add(new JPanel(), BorderLayout.NORTH);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.WorkListener#workProgressed(org.crosswire.common.progress.WorkEvent)
     */
    public synchronized void workProgressed(final WorkEvent ev)
    {
        SwingUtilities.invokeLater(new JobRunner(this, ev));
    }

    /**
     * Create a new set of components for the new Job
     */
    protected void addJob(final Job job)
    {
        int i = findEmptyPosition();
        log.debug("adding job to panel at " + i + ": " + job.getJobDescription()); //$NON-NLS-1$ //$NON-NLS-2$

        JProgressBar progress = new JProgressBar();
        progress.setStringPainted(true);
        progress.setString("0%"); //$NON-NLS-1$
        progress.setToolTipText(job.getJobDescription());
        progress.setValue(0);

        JLabel label = new JLabel(job.getJobDescription() + ":"); //$NON-NLS-1$

        // It is clumsy to use an ActionFactory for these buttons,
        // since there is one cancel button per job.
        // An ActionFactory creates actions to be shared, and whose behavior is shared.
        // Each cancel must:
        // 1) have its own cancel
        // 2) not have a mnemonic
        // 3) not have an accelerator
        JButton cancel = new JButton(Msg.CANCEL.toString());
        if (!job.isInterruptable())
        {
            cancel.setEnabled(false);
        }
        cancel.addActionListener(new CancelListener(job));

        jobsPanel.add(label, new GridBagConstraints(0, i, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        jobsPanel.add(progress, new GridBagConstraints(1, i, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        jobsPanel.add(cancel, new GridBagConstraints(2, i, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        GuiUtil.refresh(this);

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
    protected void updateJob(Job job)
    {
        JobData jobdata = (JobData) jobs.get(job);

        int percent = job.getPercent();
        jobdata.getProgress().setString(percent + "%"); //$NON-NLS-1$
        jobdata.getProgress().setToolTipText(job.getStateDescription());
        jobdata.getProgress().setValue(percent);
    }

    /**
     * Remove the set of components from the panel
     */
    protected void removeJob(Job job)
    {
        JobData jobdata = (JobData) jobs.get(job);

        log.debug("removing job from panel at " + jobdata.getIndex() + ": " + job.getJobDescription()); //$NON-NLS-1$ //$NON-NLS-2$

        positions.set(jobdata.getIndex(), null);
        jobs.remove(job);

        jobsPanel.remove(jobdata.getLabel());
        jobsPanel.remove(jobdata.getProgress());
        jobsPanel.remove(jobdata.getCancel());

        GuiUtil.refresh(this);

        jobdata.invalidate();
    }

    /**
     * Add the "no jobs" label
     */
    protected void addEmptyLabel()
    {
        jobsPanel.add(noJobLabel, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        GuiUtil.refresh(this);
    }

    /**
     * Get rid of the "no jobs" label
     */
    protected void removeEmptyLabel()
    {
        jobsPanel.remove(noJobLabel);
        GuiUtil.refresh(this);
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
     * Map of Jobs to JobDatas
     */
    protected Map jobs;

    /**
     * Array telling us what y position the jobs have in the window
     */
    private List positions;

    /**
     * The panel containing jobs
     */
    private JPanel jobsPanel;

    /**
     * A label stating that there are no jobs running
     */
    private JLabel noJobLabel;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(JobsViewPane.class);

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3546366136561315891L;

    /**
     *
     */
    private static final class JobRunner implements Runnable
    {
        /**
         * @param jvp
         * @param ev
         */
        public JobRunner(JobsViewPane jvp, WorkEvent ev)
        {
            pane = jvp;
            event = ev;
        }

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            Job job = event.getJob();

            if (!pane.jobs.containsKey(job))
            {
                // do we need an 'empty' label
                if (pane.jobs.isEmpty())
                {
                    pane.removeEmptyLabel();
                }

                pane.addJob(job);
            }

            pane.updateJob(job);

            if (job.isFinished())
            {
                pane.removeJob(job);

                // do we need an 'empty' label
                if (pane.jobs.isEmpty())
                {
                    pane.addEmptyLabel();
                }
            }
        }

        private JobsViewPane pane;
        private WorkEvent event;
    }

    /**
     * Listen for cancel events and do the cancel.
     */
    private static final class CancelListener implements ActionListener
    {
        /**
         * @param theJob
         */
        public CancelListener(Job theJob)
        {
            job = theJob;
        }

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent ev)
        {
            job.interrupt();
        }

        private Job job;
    }
    /**
     * A simple struct to group information about a Job
     */
    private static class JobData
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
         * Make sure we can't be used any more
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
         * Accessor for the job
         */
        Job getJob()
        {
            return job;
        }

        /**
         * Accessor for the label for this job
         */
        JLabel getLabel()
        {
            return label;
        }

        /**
         * Accessor for the gui progress component
         */
        JProgressBar getProgress()
        {
            return progress;
        }

        /**
         * Accessor for the cancel button
         */
        JButton getCancel()
        {
            return cancel;
        }

        /**
         * Accessor for the index in the list of jobs
         */
        int getIndex()
        {
            return index;
        }

        private Job job;
        private JLabel label;
        private JProgressBar progress;
        private JButton cancel;
        private int index = -1;
    }
}
