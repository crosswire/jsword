
package org.crosswire.common.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.crosswire.common.util.LucidException;
import org.crosswire.common.util.StackTrace;
import org.crosswire.common.util.StringUtil;

/**
 * A more detailed way of reporting problems to the user
 * This is probably too complex for a full-on public app
 * but it is probably simple enough for us to use here.
 * <p>TODO: Think about allowing the users to give us some feedback
 * TODO: Allow the configure system to set the source dirs
 * based on this dialog - even down to passing on edited source.
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
public class DetailedExceptionPane
{
    /**
    * Show a dialog containing the exception
    * @param parent Something to attach the Dialog to
    * @param ex The Exception to display
    */
    public static void showExceptionDialog(Component parent, Throwable ex)
    {
        // The components - contained, top to containing, bottom
        JLabel message = new JLabel();
        JPanel heading = new JPanel();
        Box top = new Box(BoxLayout.Y_AXIS);
        Font courier = new Font("Monospaced", Font.PLAIN, 12);
        StackTrace st = new StackTrace(ex);
        JList list = new JList();
        JScrollPane list_scroll = new JScrollPane(list);
        JPanel upper = new JPanel();
        JLabel label = new JLabel();
        JTextArea text = new JTextArea();
        JScrollPane text_scroll = new JScrollPane(text);
        JPanel lower = new JPanel();
        JSplitPane split = new JSplitPane();
        JButton ok = new JButton();
        JPanel buttons = new JPanel();
        final JDialog dialog = new JDialog(JOptionPane.getFrameForComponent(parent));

        // The upper pane
        message.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        message.setText(ex.getMessage());
        heading.setLayout(new BorderLayout());
        heading.add(message, BorderLayout.CENTER);
        list.setVisibleRowCount(6);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(new CustomLister(st, text, label));
        list.setFont(courier);
        list.setModel(getListModel(st));
        upper.setLayout(new BorderLayout());
        upper.add(heading, BorderLayout.NORTH);
        upper.add(list_scroll, BorderLayout.CENTER);

        // If this is a LucidException with a nested Exception
        if (ex instanceof LucidException)
        {
            LucidException lex = (LucidException) ex;
            final Throwable nex = lex.getException();
            if (nex != null)
            {
                JButton nest = new JButton();
                nest.setText("Cause ...");
                nest.setMnemonic('C');
                nest.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ev)
                    {
                        DetailedExceptionPane.showExceptionDialog(dialog, nex);
                    }
                });
                heading.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
                heading.add(nest, BorderLayout.EAST);
            }
        }

        // The lower pane
        label.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        label.setFont(courier);
        label.setText("No File");
        text.setEditable(false);
        text.setFont(courier);
        lower.setLayout(new BorderLayout());
        lower.add(label, BorderLayout.NORTH);
        lower.add(text_scroll, BorderLayout.CENTER);

        split.setOrientation(JSplitPane.VERTICAL_SPLIT);
        split.setContinuousLayout(true);
        split.setTopComponent(upper);
        split.setBottomComponent(lower);
        split.setBorder(BorderFactory.createEmptyBorder());

        /*
        // The explanation display
        JPanel feedback = new JPanel();
        JButton contrib = new JButton();
        JTextArea desc = new JTextArea();
        CustomAction action = new CustomAction(dialog);

        desc.setEditable(false);
        desc.setBackground(feedback.getBackground());
        desc.setPreferredSize(new Dimension(25, 25));
        desc.setWrapStyleWord(true);
        desc.setLineWrap(true);
        desc.setFont(label.getFont());
        desc.setForeground(label.getForeground());
        desc.setMinimumSize(new Dimension(0, 0));
        desc.setText(MESSAGE);

        contrib.addActionListener(action);
        contrib.setText("Contribute");

        // The contribution pane (lower rhs)
        feedback.setLayout(new BorderLayout());
        feedback.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        feedback.add("Center", desc);
        feedback.add("South", contrib);

        lower.add("East", feedback);
        */

        // The buttons at the bottom
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) { dialog.dispose(); }
        });
        ok.setText("OK");
        ok.setMnemonic('O');

        buttons.setLayout(new FlowLayout());
        buttons.add(ok);

        // Setting for the whole dialog
        dialog.getRootPane().setDefaultButton(ok);
        dialog.getRootPane().setLayout(new BorderLayout());
        dialog.getRootPane().setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, lower.getBackground()));
        dialog.getRootPane().add(top, BorderLayout.NORTH);
        dialog.getRootPane().add(split, BorderLayout.CENTER);
        dialog.getRootPane().add(buttons, BorderLayout.SOUTH);
        dialog.setTitle(ex.getClass().getName());
        dialog.setSize(700, 500);
        //dialog.setModal(true);

        GuiUtil.centerWindow(dialog);
        dialog.setVisible(true);

        // When it has closed
        //dialog.dispose();
        //dialog = null;
    }

    /**
    * To itterate over the full function names
    * @param st The stack trace to model
    * @see javax.swing.ListModel
    * @see javax.swing.JList
    */
    public static final ListModel getListModel(final StackTrace st)
    {
        return new ListModel()
        {
            /** Returns the length of the list */
            public int getSize()
            {
                return st.countStackElements();
            }

            /** Returns the value at the specified index */
            public Object getElementAt(int index)
            {
                return st.getFullFunctionName(index);
            }

            /** Ignore this because the trace will not change */
            public void addListDataListener(ListDataListener li) { }

            /** Ignore this because the trace will not change */
            public void removeListDataListener(ListDataListener li) { }
        };
    }

    /**
    * Someone clicked on contribute, so we need to bail
    */
    static class CustomAction implements ActionListener
    {
        /**
        * @param dialog The JDialog to close
        */
        public CustomAction(JDialog dialog)
        {
            this.dialog = dialog;
        }

        /**
        * Close the dialog in response to the button click
        * @param ev The data about the click
        */
        public void actionPerformed(ActionEvent ev)
        {
            JOptionPane.showMessageDialog(dialog,
                                          "The contribute system is not working yet. Sorry.",
                                          "Contribute",
                                          JOptionPane.ERROR_MESSAGE);
        }

        /** The dialog to close */
        private JDialog dialog;
    }

    /**
    * List listener to update the contents of the text area
    * whenever someone clicks in the list
    */
    static class CustomLister implements ListSelectionListener
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
            this.text = text;
            this.label = label;
        }

        /**
        * Update the contents of the text area and label
        * @param ev The data about the click
        */
        public void valueChanged(ListSelectionEvent ev)
        {
            if (ev.getValueIsAdjusting() == true) return;

            // Wait cursor
            SwingUtilities.getRoot(label).setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            // Get a stack trace
            JList list = (JList) ev.getSource();
            int level = list.getSelectedIndex();
            String name = st.getClassName(level);

            if (name.indexOf('$') != -1)
                name = name.substring(0, name.indexOf('$'));
            int line_num = st.getLineNumber(level);

            // Find a file
            name = File.separator + StringUtil.swap(name, ".", ""+File.separatorChar) + ".java";
            for (int i=0; i<source_path.length; i++)
            {
                File file = new File(source_path[i] + name);
                if (file.isFile() && file.canRead())
                {
                    // Found the file, load it into the window
                    StringBuffer data = new StringBuffer();

                    // Attempt to note the line to highlight
                    int selection_start = 0;
                    int selection_end = 0;

                    try
                    {
                        label.setText(file.getCanonicalPath());
                        LineNumberReader in = new LineNumberReader(new FileReader(file));
                        while (true)
                        {
                            String line = in.readLine();
                            if (line == null) break;
                            data.append(line).append("\n");

                            int current_line = in.getLineNumber();
                            if (current_line == line_num-1) selection_start = data.length();
                            if (current_line == line_num) selection_end = data.length()-1;
                        }
                    }
                    catch (Exception ex)
                    {
                        data.append(ex.getMessage());
                    }

                    // Actually set the text
                    text.setText(data.toString());
                    text.setSelectionStart(selection_start);
                    text.setSelectionEnd(selection_end);

                    SwingUtilities.getRoot(label).setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    return;
                }
            }

            // If we can't find a matching file
            String error = "Can't open source for: '"+st.getClassName(level)+"' line: "+line_num+"\n";
            for (int i=0; i<source_path.length; i++)
            {
                error += "Tried: "+source_path[i]+name+"\n";
            }

            text.setText(error);
            SwingUtilities.getRoot(label).setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }

        /** The StackTrace */
        private StackTrace st;

        /** The Text to write to */
        private JTextArea text;

        /** The Text to write to */
        private JLabel label;
    }

    /**
    * Set the directories to search for source files.
    * @param source_path A string array of the source directories
    */
    public static void setSourcePath(String[] source_path)
    {
        DetailedExceptionPane.source_path = source_path;
    }

    /**
    * Get the directories searched for source files.
    * @return A string array of the source directories
    */
    public static String[] getSourcePath()
    {
        return source_path;
    }

    /** The message for the user about the source code */
    private static final String MESSAGE = "This area contains the source code for this program. You can see the "+
                                          "section that caused this message. Don't worry you can't break anything "+
                                          "from here.";

    /** The StackTrace */
    protected static String[] source_path = new String[0];
}
