
package org.crosswire.jsword.view.swing.desktop;

import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import org.crosswire.common.progress.Job;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.util.Project;
import org.crosswire.jsword.view.swing.book.BibleViewPane;

/**
 * For creating a new window.
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
public class DebugAction extends DesktopAbstractAction
{
    /**
     * Setup configuration
     */
    public DebugAction(Desktop tools)
    {
        super(tools,
              "Debug",
              null,
              null,
              "Debug", "Debug",
              'G', null);
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent ev)
    {
        debug();
    }

    /**
     * Some debug action that we can configure
     */
    protected void debug()
    {
        System.out.println("\nViews:");
        int i = 0;
        Iterator it = getDesktop().iterateBibleViewPanes();
        while (it.hasNext())
        {
            BibleViewPane view = (BibleViewPane) it.next();
            System.out.println(""+(i++)+": "+view.getTitle()+" "+view.toString());
        }

        try
        {
            createTestJob(30000, "test1", 20, false);
            createTestJob(30000, "test2", 3, false);
            createTestJob(30000, "test3", 3, true);
        }
        catch (MalformedURLException ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Create a test job
     */
    public static void createTestJob(final long millis, final String predictbase, final int steps, final boolean fake) throws MalformedURLException
    {
        final URL predicturl = Project.resource().getWritablePropertiesURL(predictbase);
        final Thread test = new Thread()
        {
            /* (non-Javadoc)
             * @see java.lang.Thread#run()
             */
            public synchronized void run()
            {
                Job job = JobManager.createJob(predictbase, predicturl, Thread.currentThread(), fake);

                job.setProgress(0, "Step 0/"+steps);
                log.debug("starting test job:");

                for (int i=1; i<=steps && !Thread.interrupted(); i++)
                {
                    try
                    {
                        wait(millis/steps);
                    }
                    catch (InterruptedException ex)
                    {
                    }

                    job.setProgress((i * 100) / steps, "Step "+i+"/"+steps);
                }

                job.done();
                log.debug("finishing test job:");
            }
        };
        test.start();
    }

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(JobManager.class);
}
