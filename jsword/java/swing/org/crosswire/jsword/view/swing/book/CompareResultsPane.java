
package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.Document;

import org.crosswire.jsword.book.basic.Verifier;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.common.progress.Job;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.progress.WorkEvent;
import org.crosswire.common.progress.WorkListener;
import org.crosswire.common.swing.DocumentWriter;
import org.crosswire.common.swing.ExceptionPane;
import org.crosswire.common.swing.GuiUtil;

/**
 * This displays the results of a comparision that occurs in a separate
 * thread.
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
public class CompareResultsPane extends JPanel implements Runnable
{
    /**
     * Basic Constructor
     */
    public CompareResultsPane(Verifier ver)
    {
        this.ver = ver;
        jbInit();
    }

    /**
     * Create the GUI
     */
    private void jbInit()
    {
        setTitles();
        box_bibles = Box.createVerticalBox();
        box_bibles.add(lbl_bible1, null);
        box_bibles.add(lbl_bible2, null);

        bar_progress.setString("");
        bar_progress.setStringPainted(true);
        txt_results.setRows(5);
        txt_results.setColumns(40);
        scr_results.getViewport().add(txt_results, null);
        pnl_results.setLayout(new BorderLayout(5, 5));
        pnl_results.setBorder(new TitledBorder("Results"));
        pnl_results.add(scr_results, BorderLayout.CENTER);
        pnl_results.add(bar_progress, BorderLayout.NORTH);

        btn_stop.setMnemonic('S');
        btn_stop.setText("Start");
        btn_stop.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                startStop();
            }
        });
        pnl_buttons.add(btn_stop, null);

        this.setLayout(new BorderLayout());
        this.add(box_bibles, BorderLayout.NORTH);
        this.add(pnl_results, BorderLayout.CENTER);
        this.add(pnl_buttons, BorderLayout.SOUTH);
    }

    /**
     * This allows up to easily display this component in a window and
     * have the 2 work together on close actions and so on.
     */
    public void showInFrame(Frame parent)
    {
        final JDialog frame = new JDialog(parent, "Verify Results");

        btn_close = new JButton("Close");
        btn_close.setMnemonic('C');
        btn_close.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                if (work != null)
                    startStop();
                frame.setVisible(false);
                frame.dispose();
            }
        });
        pnl_buttons.add(btn_close, null);

        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

        frame.addWindowListener(new WindowAdapter()
        {
            public void windowClosed(WindowEvent ev)
            {
                if (work != null)
                    startStop();
            }
        });
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(this, BorderLayout.CENTER);

        frame.pack();
        GuiUtil.centerWindow(frame);
        frame.setVisible(true);
    }

    /**
     * Start running the tests
     */
    public void startStop()
    {
        if (work == null)
        {
            // New thread to do the real work
            work = new Thread(this);
            work.start();
            work.setPriority(Thread.MIN_PRIORITY);
        }
        else
        {
            work.interrupt();
            work = null;
        }
    }

    /**
     * The text that we will check, null for no check, we apply startsWith
     * to the given word before we run the check.
     */
    public void setCheckText(String check_text)
    {
        this.check_text = check_text;
        setTitles();
    }

    /**
     * The Passage that we will check, null for no check.
     */
    public void setCheckPassages(Passage check_ref)
    {
        this.check_ref = check_ref;
        setTitles();
    }

    /**
     * Set the title of the pane to what we are doing
     */
    private void setTitles()
    {
        lbl_bible1.setText("<html><b>Books:</b> "
                           +ver.getBible1().getBookMetaData().getName()+" / "
                           +ver.getBible2().getBookMetaData().getName());

        String compare = "<html><b>Comparing:</b> ";
        if (check_ref != null)
        {
            compare += "Passage=" + check_ref + " ";
        }

        if (check_text != null)
        {
            compare += "Word=" + (check_text.equals("") ? "*" : check_text);
        }

        lbl_bible2.setText(compare);
    }

    /**
     * A class to be run in a Thread to do the real work of comparing the
     * selected Books
     */
    public void run()
    {
        // While we are working stop anyone editing the values
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                btn_stop.setText("Stop");
            }
        });

        Document doc = txt_results.getDocument();
        dout.setDocument(doc);
        PrintWriter out = new PrintWriter(dout);
        alive = true;

        try
        {
            JobManager.addWorkListener(cpl);

            if (check_text != null && check_text.equals("") && alive)
            {
                ver.checkPassage(check_text, out);
            }

            if (check_ref != null && check_ref.isEmpty() && alive)
            {
                ver.checkText(check_ref, out);
            }
        }
        catch (final Exception ex)
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    ExceptionPane.showExceptionDialog(CompareResultsPane.this, ex);
                }
            });
        }
        finally
        {
            JobManager.removeWorkListener(cpl);
        }

        // Re-enable the values
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                btn_stop.setText("Start");
            }
        });
    }

    /** Are we being told to die */
    private boolean alive = true;

    /** The text to check */
    private String check_text = null;

    /** The passage to check */
    private Passage check_ref = null;

    /** The Bible verifier */
    private Verifier ver;

    /** The DocumentWriter that the comparison can write to */
    private DocumentWriter dout = new DocumentWriter();

    /** Work in progress */
    protected Thread work;

    /** The progress listener */
    private CustomProgressListener cpl = new CustomProgressListener();

    /* GUI components */
    private JPanel pnl_results = new JPanel();
    private JScrollPane scr_results = new JScrollPane();
    private JTextArea txt_results = new JTextArea();
    protected JProgressBar bar_progress = new JProgressBar();
    private Box box_bibles;
    private JLabel lbl_bible1 = new JLabel();
    private JPanel pnl_buttons = new JPanel();
    protected JButton btn_stop = new JButton();
    private JButton btn_close = null;
    private JLabel lbl_bible2 = new JLabel();

    /**
     * Report progress changes to the screen
     */
    class CustomProgressListener implements WorkListener
    {
        /* (non-Javadoc)
         * @see org.crosswire.common.progress.WorkListener#progressMade(org.crosswire.common.progress.WorkEvent)
         */
        public void workProgressed(final WorkEvent ev)
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    Job job = ev.getJob();
                    int percent = job.getPercent();
                    bar_progress.setString(job.getStateDescription() + ": (" + percent + "%)");
                    bar_progress.setValue(percent);
                }
            });
        }
    }
}
