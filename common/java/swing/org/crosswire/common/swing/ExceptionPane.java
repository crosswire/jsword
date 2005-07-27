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
package org.crosswire.common.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.util.ReporterEvent;
import org.crosswire.common.util.ReporterListener;
import org.crosswire.common.util.StackTrace;
import org.crosswire.common.xml.XMLUtil;

/**
 * A simple way of reporting problems to the user.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class ExceptionPane extends JPanel
{
    /**
     * Use showExceptionDialog for the time being
     */
    private ExceptionPane(Throwable ex)
    {
        this.ex = ex;
        initialise();
        setDisplayedException(ex);
    }

    /**
     * Setup the GUI
     */
    private void initialise()
    {
        String exmsg = "<html><font size=\"-1\">" + Msg.ERROR_OCCURED + "</font> " + ExceptionPane.getHTMLDescription(ex); //$NON-NLS-1$ //$NON-NLS-2$

        // The upper pane
        JLabel message = new JLabel();
        message.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        message.setText(exmsg);
        message.setIcon(GuiUtil.getIcon("toolbarButtonGraphics/general/Stop24.gif")); //$NON-NLS-1$
        message.setIconTextGap(20);

        JPanel banner = new JPanel(new BorderLayout());
        banner.add(message, BorderLayout.CENTER);
        list = new JList();
        list.setVisibleRowCount(6);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        Font courier = new Font("Monospaced", Font.PLAIN, 12); //$NON-NLS-1$
        list.setFont(courier);

        // The buttons at the bottom
        ok = new JButton();
        ok.setText(Msg.OK.toString());
        ok.setMnemonic(Msg.OK.toString().charAt(0));

        detail = new JCheckBox();
        detail.addItemListener(new SelectedItemListener(this));
        detail.setText(Msg.DETAILS.toString());

        JPanel spacer = new JPanel(new FlowLayout());
        spacer.add(ok);

        JPanel buttons = new JPanel(new BorderLayout());
        buttons.add(spacer, BorderLayout.CENTER);
        buttons.add(detail, BorderLayout.WEST);

        upper = new JPanel(new BorderLayout());
        upper.add(banner, BorderLayout.NORTH);
        upper.add(buttons, BorderLayout.CENTER);

        // The lower pane
        label = new JLabel();
        label.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        label.setFont(courier);
        label.setText(Msg.NO_FILE.toString());
        text = new JTextArea();
        text.setEditable(false);
        text.setFont(courier);
        JScrollPane textScroll = new JScrollPane(text);
        textScroll.setColumnHeaderView(label);

        List causes = new ArrayList();
        Throwable throwable = ex;
        while (throwable != null)
        {
            causes.add(throwable);
            throwable = throwable.getCause();
        }
        Throwable[] exs = (Throwable[]) causes.toArray(new Throwable[causes.size()]);

        JComboBox traces = new JComboBox();
        traces.setModel(new DefaultComboBoxModel(exs));
        traces.addActionListener(new SelectActionListener(this, traces));

        JPanel heading = new JPanel(new BorderLayout());
        heading.add(traces, BorderLayout.CENTER);

        JSplitPane split = new FixedSplitPane();
        // Make the top 20% of the total
        split.setResizeWeight(0.2D);
        split.setOrientation(JSplitPane.VERTICAL_SPLIT);
        split.setContinuousLayout(true);
        split.setTopComponent(new JScrollPane(list));
        split.setBottomComponent(textScroll);
        split.setBorder(BorderFactory.createEmptyBorder());
        split.setPreferredSize(new Dimension(500, 300));

        lower = new JPanel(new BorderLayout());
        lower.add(split, BorderLayout.CENTER);
        lower.add(heading, BorderLayout.NORTH);

        this.setLayout(new BorderLayout());
        this.add(upper, BorderLayout.NORTH);
    }

    /**
     * Is the detail area shown?
     */
    protected void changeDetail()
    {
        if (detail.isSelected())
        {
            ExceptionPane.this.add(lower, BorderLayout.CENTER);
        }
        else
        {
            ExceptionPane.this.remove(lower);
        }

        GuiUtil.getDialog(ExceptionPane.this).pack();
    }

    /**
     * Display a different nested exception
     */
    protected void setDisplayedException(Throwable ex)
    {
        StackTrace st = new StackTrace(ex);
        list.addListSelectionListener(new ExceptionPane.CustomLister(st, text, label));
        list.setModel(new StackTraceListModel(st));
    }

    /**
     * Show a dialog containing the exception
     * @param parent Something to attach the Dialog to
     * @param ex The Exception to display
     */
    public static void showExceptionDialog(Component parent, Throwable ex)
    {
        final ExceptionPane pane = new ExceptionPane(ex);

        // Setting for the whole dialog
        final JDialog dialog = new JDialog(GuiUtil.getFrame(parent));
        dialog.getRootPane().setDefaultButton(pane.ok);
        dialog.getRootPane().setLayout(new BorderLayout());
        dialog.getRootPane().setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, pane.upper.getBackground()));
        dialog.getRootPane().add(pane, BorderLayout.CENTER);
        dialog.setTitle(Msg.ERROR.toString());

        pane.ok.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                dialog.dispose();
            }
        });

        // If this dialog is not modal then if we display an exception dialog
        // where there is a modal dialog displayed then although this dialog
        // is to the front, we can't interract with it until the modal dialog
        // has been closed.
        dialog.setModal(true);

        GuiUtil.centerWindow(dialog);
        dialog.pack();
        dialog.setVisible(true);

        // When it has closed
        //dialog.dispose();
        //dialog = null;
    }

    /**
     * Set the directories to search for source files.
     * @param sourcePath A string array of the source directories
     */
    public static void setSourcePath(File[] sourcePath)
    {
        ExceptionPane.sources = (File[]) sourcePath.clone();
    }

    /**
     * Get the directories searched for source files.
     * @return A string array of the source directories
     */
    public static File[] getSourcePath()
    {
        return sources;
    }

    /**
     * You must call setJoinHelpDesk() in order to start displaying
     * Exceptions sent to the Log, and in order to properly
     * close this class you must call it again (with false).
     * @param joined Are we listening to the Log
     */
    public static void setHelpDeskListener(boolean joined)
    {
        if (joined && li == null)
        {
            li = new ExceptionPaneReporterListener();
            Reporter.addReporterListener(li);
        }

        if (!joined && li != null)
        {
            Reporter.removeReporterListener(li);
            li = null;
        }
    }

    /**
     * You must call setJoinHelpDesk() in order to start displaying
     * Exceptions sent to the Log, and in order to properly
     * close this class you must call it again (with false).
     */
    public static boolean isHelpDeskListener()
    {
        return li != null;
    }

    /**
     * Gets a short HTML description of an Exception for display in a
     * window
     */
    public static String getHTMLDescription(Throwable ex)
    {
        StringBuffer retcode = new StringBuffer();

        // The message in the exception
        String msg = ex.getMessage();
        if (msg == null || msg.equals("")) //$NON-NLS-1$
        {
            msg = Msg.NO_DESC.toString();
        }
        String orig = XMLUtil.escape(msg);
        msg = orig.replaceAll("\n", "<br>"); //$NON-NLS-1$ //$NON-NLS-2$

        // The name of the exception
        /*
        String classname = ex.getClass().getName();
        int lastdot = classname.lastIndexOf('.');
        if (lastdot != -1)
            classname = classname.substring(lastdot+1);
        if (classname.endsWith("Exception") && classname.length() > "Exception".length())
            classname = classname.substring(0, classname.length() - "Exception".length());
        if (classname.endsWith("Error") && classname.length() > "Error".length())
            classname = classname.substring(0, classname.length() - "Error".length());
        classname = StringUtil.createTitle(classname);
        if (classname.equals("IO")) classname = "Input / Output";

        retcode.append("<font size=\"-1\"><strong>");
        retcode.append(classname);
        retcode.append("</strong></font>");
        */
        retcode.append("<br>"); //$NON-NLS-1$
        retcode.append(msg);

        // If this is a LucidException with a nested Exception
        Throwable nex = ex.getCause();
        if (nex != null)
        {
            retcode.append("<p><br><font size=\"-1\">"); //$NON-NLS-1$
            retcode.append(Msg.CAUSED_BY);
            retcode.append("</font>"); //$NON-NLS-1$
            retcode.append(getHTMLDescription(nex));
        }

        return retcode.toString();
    }

    /**
     * Make the default to be an error listener
     */
    static
    {
        setHelpDeskListener(true);
    }

    /**
     *
     */
    private static final class SelectedItemListener implements ItemListener
    {
        /**
         * @param ep
         */
        public SelectedItemListener(ExceptionPane ep)
        {
            pane = ep;
        }

        /* (non-Javadoc)
         * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
         */
        public void itemStateChanged(ItemEvent ev)
        {
            pane.changeDetail();
        }

        private ExceptionPane pane;
    }

    /**
     *
     */
    private static final class SelectActionListener implements ActionListener
    {
        /**
         * @param ep
         * @param cb
         */
        public SelectActionListener(ExceptionPane ep, JComboBox cb)
        {
            pane = ep;
            traces = cb;
        }

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent ev)
        {
            Throwable th = (Throwable) traces.getSelectedItem();
            pane.setDisplayedException(th);
        }

        private ExceptionPane pane;
        private JComboBox traces;
    }

    /**
     * List listener to update the contents of the text area
     * whenever someone clicks in the list
     */
    private static final class CustomLister implements ListSelectionListener
    {
        /**
         * Initialize with the stuff we need to act on the
         * change, when the list is clicked.
         * @param st The list of elements in the exception
         * @param text The editable file
         * @param label The filename label
         */
        public CustomLister(StackTrace st, JTextArea text, JLabel label)
        {
            this.st = st;
            this.mytext = text;
            this.mylabel = label;
        }

        /* (non-Javadoc)
         * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
         */
        public void valueChanged(ListSelectionEvent ev)
        {
            if (ev.getValueIsAdjusting())
            {
                return;
            }

            // Wait cursor
            SwingUtilities.getRoot(mylabel).setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            // Get a stack trace
            JList lst = (JList) ev.getSource();
            int level = lst.getSelectedIndex();
            String name = st.getClassName(level);

            if (name.indexOf('$') != -1)
            {
                name = name.substring(0, name.indexOf('$'));
            }

            int line_num = st.getLineNumber(level);
            String orig = name;
            Integer errorLine = new Integer(line_num);
            mylabel.setText(Msg.NO_FILE.toString());

            // Find a file
            name = File.separator + orig.replace('.', File.separatorChar) + FileUtil.EXTENSION_JAVA; //$NON-NLS-1$ //$NON-NLS-2$
            for (int i = 0; i < sources.length; i++)
            {
                File file = new File(sources[i], name);
                if (file.isFile() && file.canRead())
                {
                    // Found the file, load it into the window
                    StringBuffer data = new StringBuffer();

                    // Attempt to note the line to highlight
                    int selection_start = 0;
                    int selection_end = 0;

                    LineNumberReader in = null;
                    try
                    {
                        String found = Msg.SOURCE_FOUND.toString(new Object[] { errorLine, file.getCanonicalPath() });
                        mylabel.setText(found);
                        in = new LineNumberReader(new FileReader(file));
                        while (true)
                        {
                            String line = in.readLine();
                            if (line == null)
                            {
                                break;
                            }
                            data.append(line).append("\n"); //$NON-NLS-1$

                            int current_line = in.getLineNumber();
                            if (current_line == line_num - 1)
                            {
                                selection_start = data.length();
                            }
                            if (current_line == line_num)
                            {
                                selection_end = data.length() - 1;
                            }
                        }
                    }
                    catch (Exception ex)
                    {
                        data.append(ex.getMessage());
                    }
                    finally
                    {
                        if (in != null)
                        {
                            try
                            {
                                in.close();
                            }
                            catch (IOException e)
                            {
                                data.append(e.getMessage());
                            }
                        }
                    }

                    // Actually set the text
                    mytext.setText(data.toString());
                    mytext.setSelectionStart(selection_start);
                    mytext.setSelectionEnd(selection_end);

                    SwingUtilities.getRoot(mylabel).setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    return;
                }
            }

            // If we can't find a matching file
            String error = Msg.SOURCE_NOT_FOUND.toString(new Object[] { st.getClassName(level), errorLine });
            for (int i = 0; i < sources.length; i++)
            {
                error += Msg.SOURCE_ATTEMPT.toString(new Object[] { sources[i].getAbsolutePath() + name });
            }

            mytext.setText(error);
            SwingUtilities.getRoot(mylabel).setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }

        /**
         * The StackTrace
         */
        private StackTrace st;

        /**
         * The Text to write to
         */
        private JTextArea mytext;

        /**
         * The Text to write to
         */
        private JLabel mylabel;
    }

    /**
     * The ExceptionPane instance that we add to the Log
     */
    private static final class ExceptionPaneReporterListener implements ReporterListener
    {
        /**
         * Called whenever Reporter.informUser() is passed an Exception
         * @param ev The event describing the Exception
         */
        public void reportException(ReporterEvent ev)
        {
            // This faf is to ensure that we don't break any SwingThread rules
            SwingUtilities.invokeLater(new ExceptionRunner(ev));
        }

        /**
         * Called whenever Reporter.informUser() is passed a message
         * @param ev The event describing the message
         */
        public void reportMessage(ReporterEvent ev)
        {
            // This faf is to ensure that we don't break any SwingThread rules
            SwingUtilities.invokeLater(new MessageRunner(ev));
        }
    }

    /**
    *
    */
    private static final class ExceptionRunner implements Runnable
    {
        /**
         * @param ev
         */
        public ExceptionRunner(ReporterEvent ev)
        {
            event = ev;
        }

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            if (event.getSource() instanceof Component)
            {
                showExceptionDialog((Component) event.getSource(), event.getException());
            }
            else
            {
                showExceptionDialog(null, event.getException());
            }
        }

        private ReporterEvent event;
    }

   /**
     *
     */
    private static final class MessageRunner implements Runnable
    {
        /**
         * @param ev
         */
        public MessageRunner(ReporterEvent ev)
        {
            event = ev;
        }

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            if (event.getSource() instanceof Component)
            {
                JOptionPane.showMessageDialog((Component) event.getSource(), event.getMessage());
            }
            else
            {
                JOptionPane.showMessageDialog(null, event.getMessage());
            }
        }

        private ReporterEvent event;
    }
    /**
     * The exception we are displaying
     */
    private Throwable ex;

    // The components - contained, top to containing, bottom
    private JList list;
    private JPanel upper;
    private JLabel label;
    private JTextArea text;
    private JButton ok;
    private JCheckBox detail;
    private JPanel lower;

    /**
     * The directories searched for source
     */
    protected static File[] sources = new File[0];

    /**
     * The listener that pops up the ExceptionPanes
     */
    private static ExceptionPaneReporterListener li;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3258126947203495219L;
}
