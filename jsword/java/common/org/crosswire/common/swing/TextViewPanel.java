
package org.crosswire.common.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ProgressMonitorInputStream;
import javax.swing.SwingUtilities;

import org.crosswire.common.util.Reporter;
import org.crosswire.common.util.StringUtil;

/**
 * TextViewPanel allow viewing of some text in its own standalone frame.
 * The text to be viewed can be grabbed from a String, a URL, or a file.
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
public class TextViewPanel extends JPanel
{
    /**
     * Construct a TextViewPanel by calling jbInit()
     */
    public TextViewPanel()
    {
        jbInit();
    }

    /**
     * Construct a TextViewPanel with some string contents
     * @param text The contents of the text area
     */
    public TextViewPanel(String text)
    {
        jbInit();
        setText(text);
    }

    /**
     * Construct a TextViewPanel with a URL from which to read the text
     * @param url A pointer to the contents of the text area
     */
    public TextViewPanel(URL url) throws IOException
    {
        jbInit();
        setText(url);
    }

    /**
     * Construct a TextViewPanel with a File from which to read the text
     * @param file A pointer to the contents of the text area
     */
    public TextViewPanel(File file) throws IOException
    {
        jbInit();
        setText(file);
    }

    /**
     * Construct a TextViewPanel with some string contents
     * @param text The contents of the text area
     * @param header The string for the header area of the window
     */
    public TextViewPanel(String text, String header)
    {
        jbInit();
        setText(text);
        setHeader(header);
    }

    /**
     * Construct a TextViewPanel with a URL from which to read the text
     * @param url A pointer to the contents of the text area
     * @param header The string for the header area of the window
     */
    public TextViewPanel(URL url, String header) throws IOException
    {
        jbInit();
        setText(url);
        setHeader(header);
    }

    /**
     * Construct a TextViewPanel with a File from which to read the text
     * @param file A pointer to the contents of the text area
     * @param header The string for the header area of the window
     */
    public TextViewPanel(File file, String header) throws IOException
    {
        jbInit();
        setText(file);
        setHeader(header);
    }

    /**
     * Actually create the GUI
     */
    private void jbInit()
    {
        scr_text.getViewport().add(txt_text, null);
        txt_text.setEditable(false);
        txt_text.setColumns(80);
        txt_text.setRows(24);

        btn_clipboard.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                clipboard();
            }
        });
        btn_clipboard.setMnemonic('C');
        btn_clipboard.setText("Copy to Clipboard");

        lay_buttons.setAlignment(FlowLayout.RIGHT);
        pnl_buttons.setLayout(lay_buttons);
        pnl_buttons.add(btn_clipboard, null);

        this.setLayout(new BorderLayout());
        this.add(scr_text, BorderLayout.CENTER);
        this.add(pnl_buttons, BorderLayout.SOUTH);
    }

    /**
     * Display this Panel in a new JFrame
     */
    public void showInFrame(Frame parent)
    {
        frame = new JDialog(parent, "Text Viewer");

        btn_close = new JButton("Close");
        btn_close.setMnemonic('L');
        btn_close.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                frame.setVisible(false);
                frame.dispose();
            }
        });
        pnl_buttons.add(btn_close, null);

        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(this, BorderLayout.CENTER);

        frame.pack();
        GuiUtil.centerWindow(frame);
        frame.setVisible(true);
    }

    /**
     * Copy the current text into the system clipboard
     */
    public void clipboard()
    {
        StringSelection ss = new StringSelection(getText());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
    }

    /**
     * Setter for the text in the header area
     * @param new_header The new header text
     */
    public void setHeader(String new_header)
    {
        String old_header = lbl_main.getText();
        lbl_main.setText(new_header);

        if (new_header != null)
        {
            this.add(lbl_main, BorderLayout.NORTH);
        }
        else
        {
            this.remove(lbl_main);
        }

        listeners.firePropertyChange("header", old_header, new_header);
    }

    /**
     * Getter for the text in the header area
     * @return The current header
     */
    public String getHeader()
    {
        return lbl_main.getText();
    }

    /**
     * Setter for the main body of text.
     * @param new_text The text to display
     */
    public void setText(String new_text)
    {
        String old_text = txt_text.getText();
        txt_text.setText(new_text);
        txt_text.setCaretPosition(0);

        if (frame != null)
        {
            GuiUtil.restrainedRePack(frame);
        }

        listeners.firePropertyChange("text", old_text, new_text);
    }

    /**
     * Setter for the main body of text
     * @param url A pointer to the text to display
     */
    public void setText(URL url) throws IOException
    {
        setText(url.openStream());
    }

    /**
     * Setter for the main body of text
     * @param file A pointer to the text to display
     */
    public void setText(File file) throws IOException
    {
        setText(new FileInputStream(file));
    }

    /**
     * Setter for the main body of text
     * @param file A pointer to the text to display
     */
    public void setText(final InputStream in) throws IOException
    {
        // Yes this is twisted, however there is some kind of perverse
        // pleasure in writing this kind of code.
        // But for the setPriority() I might have dispensed with the
        // "Thread work = " bit and just tacked ".start()" to the end
        // This simply creates a thread to read the file, and then a
        // Runnable to update the GUI (swing is single threaded)
        Thread work = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    InputStream pmin = new ProgressMonitorInputStream(TextViewPanel.this, "Loading text ...",in);
                    Reader rin = new InputStreamReader(pmin);
                    final String data = StringUtil.read(rin);

                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            setText(data);
                        }
                    });
                }
                catch (IOException ex)
                {
                    Reporter.informUser(TextViewPanel.this, ex);
                }
            }
        });

        work.start();
        work.setPriority(Thread.MIN_PRIORITY);
    }

    /**
     * Getter for the main body of text
     * @return The string from the main text area
     */
    public String getText()
    {
        return txt_text.getText();
    }

    /**
     * Add a property change listener
     * @param li The property change listener to add
     */
    public synchronized void removePropertyChangeListener(PropertyChangeListener li)
    {
        super.removePropertyChangeListener(li);
        listeners.removePropertyChangeListener(li);
    }

    /**
     * Remove a property change listener
     * @param li The property change listener to remove
     */
    public synchronized void addPropertyChangeListener(PropertyChangeListener li)
    {
        super.addPropertyChangeListener(li);
        listeners.addPropertyChangeListener(li);
    }

    /** Optional header label */
    private JLabel lbl_main = new JLabel();

    /** Scroller for the text area */
    private JScrollPane scr_text = new JScrollPane();

    /** The main text area */
    private JTextArea txt_text = new JTextArea();

    /** The button bar */
    private JPanel pnl_buttons = new JPanel();

    /** Button bar layout */
    private FlowLayout lay_buttons = new FlowLayout();

    /** Copy text to clipboard button */
    private JButton btn_clipboard = new JButton();

    /** Close button */
    private JButton btn_close = null;

    /** The frame that we are displayed in */
    protected JDialog frame = null;

    /** Property change listener collection */
    private transient PropertyChangeSupport listeners = new PropertyChangeSupport(this);
}
