package org.crosswire.jsword.view.swing.desktop;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.crosswire.common.progress.Job;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.util.Logger;
import org.crosswire.jsword.util.Project;
import org.crosswire.jsword.view.swing.book.BibleViewPane;

/**
 * Various debug actions, for easy editing to help us hack over time.
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
public class DebugPane extends JPanel
{
    /**
     * Simple ctor
     */
    public DebugPane(Desktop desktop)
    {
        this.desktop = desktop;

        Method[] methods = getClass().getMethods();
        for (int i = 0; i < methods.length; i++)
        {
            Method method = methods[i];
            if (method.getParameterTypes().length == 0
                && method.getDeclaringClass() == getClass()
                && Modifier.isPublic(method.getModifiers()))
            {
                mdlMethods.addElement(method);
            }
        }

        jbInit();
    }

    /**
     * Setup the GUI
     */
    private void jbInit()
    {
        lblMethod.setDisplayedMnemonic('M');
        lblMethod.setLabelFor(cboMethod);
        lblMethod.setText("Method: ");
        cboMethod.setModel(mdlMethods);
        cboMethod.setRenderer(new CustomListCellRenderer());
        btnMethod.setMnemonic('G');
        btnMethod.setText("GO");
        btnMethod.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                action();
            }
        });
        pnlMethod.setLayout(new BorderLayout(5, 5));
        pnlMethod.add(lblMethod, BorderLayout.WEST);
        pnlMethod.add(cboMethod, BorderLayout.CENTER);
        pnlMethod.add(btnMethod, BorderLayout.EAST);

        scrResults.getViewport().add(txtResults);

        this.setLayout(new BorderLayout(5, 5));
        this.add(scrResults, BorderLayout.CENTER);
        this.add(pnlMethod, BorderLayout.NORTH);
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    /**
     * Call the chosen method
     */
    protected void action()
    {
        try
        {
            Method method = (Method) cboMethod.getSelectedItem();
            Object reply = method.invoke(this, new Object[0]);
            if (reply == null)
            {
                txtResults.setText("");
            }
            else
            {
                txtResults.setText(reply.toString());
            }
        }
        catch (Exception ex)
        {
            StringWriter sout = new StringWriter();
            PrintWriter out = new PrintWriter(sout);
            ex.printStackTrace(out);
            txtResults.setText(sout.toString());
        }
    }

    /**
     * Some debug action that we can configure
     */
    public String showViews()
    {
        StringBuffer reply = new StringBuffer();

        reply.append("\nViews:");
        int i = 0;
        Iterator it = desktop.iterateBibleViewPanes();
        while (it.hasNext())
        {
            BibleViewPane view = (BibleViewPane) it.next();
            reply.append(""+(i++)+": "+view.getTitle()+" "+view.toString());
        }

        return reply.toString();
    }

    /**
     * Create some test jobs
     */
    public void createTestJobs() throws MalformedURLException
    {
        createTestJob(30000, "test1", 20, false);
        createTestJob(30000, "test2", 3, false);
        createTestJob(30000, "test3", 3, true);
    }
    
    /**
     * Create a test job
     */
    public static void createTestJob(final long millis, final String predictbase, final int steps, final boolean fake) throws MalformedURLException
    {
        final URL predicturl = Project.instance().getWritablePropertiesURL(predictbase);
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
                        // ignore
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
    protected static final Logger log = Logger.getLogger(DebugPane.class);

    /**
     * The main window
     */
    private Desktop desktop;

    /*
     * GUI Components
     */
    private DefaultComboBoxModel mdlMethods = new DefaultComboBoxModel();
    private JScrollPane scrResults = new JScrollPane();
    private JPanel pnlMethod = new JPanel();
    private JLabel lblMethod = new JLabel();
    private JComboBox cboMethod = new JComboBox();
    private JButton btnMethod = new JButton();
    private JTextPane txtResults = new JTextPane();

    /**
     * Simpler method name display
     */
    private static final class CustomListCellRenderer extends DefaultListCellRenderer
    {
        /* (non-Javadoc)
         * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
         */
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
        {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Method)
            {
                setText(((Method) value).getName()+"()");
            }
            return this;
        }
    }
}
