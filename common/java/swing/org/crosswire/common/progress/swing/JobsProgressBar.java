package org.crosswire.common.progress.swing;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.crosswire.common.progress.Job;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.progress.WorkEvent;
import org.crosswire.common.progress.WorkListener;
import org.crosswire.common.swing.GuiUtil;
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
public class JobsProgressBar extends JPanel implements WorkListener, PropertyChangeListener
{
    /**
     * Simple ctor
     */
    public JobsProgressBar(boolean small)
    {
        jobs = new HashMap();
        positions = new ArrayList();
        if (small)
        {
            // They start off at 15pt (on Windows at least)
            font = new Font("SansSerif", Font.PLAIN, 10); //$NON-NLS-1$
        }

        JobManager.addWorkListener(this);

        Set current = JobManager.getJobs();
        for (Iterator it = current.iterator(); it.hasNext(); )
        {
            Job job = (Job) it.next();
            addJob(job);
        }

        this.setLayout(new GridLayout(1, 0, 2, 0));
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.progress.WorkListener#workProgressed(org.crosswire.common.progress.WorkEvent)
     */
    public synchronized void workProgressed(final WorkEvent ev)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
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
        });
    }

    /**
     * Create a new set of components for the new Job
     */
    protected synchronized void addJob(Job job)
    {
        job.addPropertyChangeListener(this);

        int i = findEmptyPosition();
        log.debug("adding job to panel at " + i + ": " + job.getJobDescription()); //$NON-NLS-1$ //$NON-NLS-2$

        JProgressBar progress = new JProgressBar();
        progress.setStringPainted(true);
        progress.setToolTipText(job.getJobDescription());
        progress.setBorder(null);
        progress.setBackground(getBackground());
        progress.setForeground(getForeground());
        if (font != null)
        {
            progress.setFont(font);
        }
        // Dimension preferred = progress.getPreferredSize();
        // preferred.width = 50;
        // progress.setPreferredSize(preferred);

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

        this.add(jobdata.getComponent(), i);
        GuiUtil.refresh(this);
    }

    public void propertyChange(PropertyChangeEvent evt)
    {
        Job job = (Job) evt.getSource();
        JobData jobdata = (JobData) jobs.get(job);
        jobdata.propertyChange(evt);
    }

    /**
     * Update the job details because it has just progressed
     */
    protected synchronized void updateJob(Job job)
    {
        JobData jobdata = (JobData) jobs.get(job);

        int percent = job.getPercent();
        jobdata.getProgress().setString(job.getStateDescription() + ": (" + percent + "%)"); //$NON-NLS-1$ //$NON-NLS-2$
        jobdata.getProgress().setValue(percent);
    }

    /**
     * Remove the set of components from the panel
     */
    protected synchronized void removeJob(Job job)
    {
        job.addPropertyChangeListener(this);

        JobData jobdata = (JobData) jobs.get(job);

        positions.set(jobdata.getIndex(), null);
        jobs.remove(job);
        log.debug("removing job from panel: " + jobdata.getJob().getJobDescription()); //$NON-NLS-1$

        this.remove(jobdata.getComponent());
        GuiUtil.refresh(this);
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
     * Where we store the currently displayed jobs
     */
    protected Map jobs;

    /**
     * Array telling us what y position the jobs have in the window
     */
    private List positions;

    /**
     * The font for the progress-bars
     */
    private Font font;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(JobsProgressBar.class);

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3257563988660663606L;

    /**
     * A simple struct to group information about a Job
     */
    private static class JobData implements PropertyChangeListener
    {
        /**
         * Simple ctor
         */
        JobData(Job job, int index, JProgressBar progress)
        {
            this.job = job;
            this.index = index;
            this.progress = progress;
            this.comp = decorateProgressBar();
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
         * 
         */
        public Component getComponent()
        {
            return comp;
        }

        /**
         * @return Returns the cancelButton.
         */
        public JButton getCancelButton()
        {
            if (cancelButton == null)
            {
                cancelButton = createCancelButton();
            }
            return cancelButton;
        }

        /**
         * Accessor for the index
         */
        int getIndex()
        {
            return index;
        }

        public void propertyChange(PropertyChangeEvent evt)
        {
            if (cancelButton != null)
            {
                cancelButton.setEnabled(job.isInterruptable());
            }
        }

        /**
         * Create a cancel button that only shows the cancel icon.
         * When the button is pressed the job is interrupted.
         * @return a custom cancel button
         */
        private JButton createCancelButton()
        {
            Icon stop = GuiUtil.getIcon("toolbarButtonGraphics/general/Stop16.gif"); //$NON-NLS-1$

            // Create a cancel button
            cancelButton = new JButton(stop);
            // Only paint the icon not the button
            cancelButton.setContentAreaFilled(false);
            // Make the button as small as possible
            cancelButton.setMargin(new Insets(0, 0, 0, 0));
            // We don't need no stinkin' border
            cancelButton.setBorderPainted(false);
            // Under WinXP this does nothing
            cancelButton.setRolloverEnabled(true);
            cancelButton.setToolTipText(Msg.CANCEL.toString());
            cancelButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent ev)
                {
                    getJob().interrupt();
                }
            });
            return cancelButton;
        }

        /**
         * Decorate the progress bar if the job can be interrupted.
         * We put the cancel button in a 1 row, 2 column grid
         * where the button is in a minimally sized fixed cell
         * and the progress meter follows in a horizontally stretchy cell
         */
        private Component decorateProgressBar()
        {
            if (!job.isInterruptable())
            {
                return progress;
            }

            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = 1;
            gbc.fill = GridBagConstraints.NONE;
            panel.add(createCancelButton(), gbc);
            gbc.weightx = 1.0;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(progress, gbc);
            return panel;
        }

        private Job job;
        private int index;
        private JProgressBar progress;
        private Component comp;
        private JButton cancelButton;
    }

}
