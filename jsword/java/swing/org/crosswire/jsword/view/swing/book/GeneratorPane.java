
package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.crosswire.common.progress.Job;
import org.crosswire.common.progress.WorkEvent;
import org.crosswire.common.progress.WorkListener;
import org.crosswire.common.swing.ComponentAbstractAction;
import org.crosswire.common.swing.EirPanel;
import org.crosswire.common.swing.ExceptionPane;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.basic.Verifier;

/**
 * Bible Generator allows the creation of new Books - although it
 * really only converts from one implementation of Book to another.
 * This is needed because I drivers like JDBCBook and GBMLBook will not
 * be very speed optimized.
 * <p>Since this code has been edited by JBuilder I have changed it
 * and expect problems if it is edited that way again. The code that JB
 * created did not compile with JDK 1.1 and Swing 1.1 because it uses a
 * constructor special to AWT in JDK 1.2, So I have changed code that read
 *   <code>new GridBagConstraints</code>
 * to
 *   <code>GuiUtil.getConstraints</code>
 * to fix this.
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
public class GeneratorPane extends EirPanel
{
    /**
     * Construct a Bible Generator tool, this simply calls jbInit
     */
    public GeneratorPane()
    {
        jbInit();
    }

    /**
     * Create the GUI components.
     */
    private void jbInit()
    {
        cbo_source.setModel(mdl_source);
        cbo_source.setRenderer(new BookListCellRenderer());
        lbl_source.setText("  Source Bible: ");
        pnl_source.setLayout(new BorderLayout());
        pnl_source.setBorder(BorderFactory.createTitledBorder("Source"));
        pnl_source.add(lbl_source, BorderLayout.WEST);
        pnl_source.add(cbo_source, BorderLayout.CENTER);

        lbl_name.setText("New Name:");
        lbl_driver.setText("Driver Class:");

        cbo_driver.setModel(mdl_driver);
        pnl_dest.setLayout(lay_dest);
        pnl_dest.setBorder(BorderFactory.createTitledBorder("Destination"));

        pnl_dest.add(lbl_name, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
        pnl_dest.add(lbl_driver, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
        pnl_dest.add(cbo_driver, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));

        bar_prog.setBorderPainted(true);
        bar_prog.setMaximum(100);
        bar_prog.setString("");
        bar_prog.setStringPainted(true);
        pnl_prog.setLayout(new BorderLayout());
        pnl_prog.setBorder(BorderFactory.createTitledBorder("Progress"));
        pnl_prog.add(bar_prog, BorderLayout.CENTER);

        box_main = Box.createVerticalBox();
        box_main.add(pnl_source, null);
        box_main.add(pnl_dest, null);

        btn_generate.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                generate();
            }
        });
        btn_generate.setText("Generate");
        btn_generate.setMnemonic('G');

        chk_verify.setText("Verify After Generation");
        chk_verify.setMnemonic('V');
        chk_verify.setSelected(false);
        lay_buttons.setAlignment(FlowLayout.RIGHT);
        pnl_buttons.setLayout(lay_buttons);
        pnl_buttons.add(chk_verify, null);
        pnl_buttons.add(btn_generate, null);

        this.setLayout(new BorderLayout());
        this.add(box_main, BorderLayout.NORTH);
        this.add(pnl_prog, BorderLayout.CENTER);
        this.add(pnl_buttons, BorderLayout.SOUTH);
    }

    /**
     * Create an 'open' Action
     */
    public static Action createOpenAction(Component parent)
    {
        return new OpenAction(parent);
    }

    /**
     * An Action to open a new one of these
     */
    public static class OpenAction extends ComponentAbstractAction
    {
        public OpenAction(Component comp)
        {
            super(comp,
                  "Generate ...",
                  "toolbarButtonGraphics/development/BeanAdd16.gif",
                  "toolbarButtonGraphics/development/BeanAdd24.gif",
                  "Generate", "Generate a new version file set.",
                  'G', null);
        }
    
        public void actionPerformed(ActionEvent ev)
        {
            MaintenancePane pnl_maint = new MaintenancePane();
            pnl_maint.showInDialog(getComponent());
        }
    }

    /**
     * Show this Panel in a new dialog
     */
    public void showInDialog(Component parent)
    {
        showInDialog(parent, "Generator", false);
    }

    /**
     * This allows up to easily display this component in a window and
     * have the 2 work together on close actions and so on.
     */
    public void showInFrame(Frame parent)
    {
        final JDialog frame = new JDialog(parent, "Bible Generator");

        btn_close = new JButton("Close");
        btn_close.setMnemonic('C');
        btn_close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev)
            {
                frame.setVisible(false);
                frame.dispose();
            }
        });
        pnl_buttons.add(btn_close, null);

        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent ev)
            {
                if (work != null)
                    work.interrupt();
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
     * Actually start generating the new Book
     */
    public void generate()
    {
        // New thread to do the real work
        work = new Thread(new GeneratorRunnable());
        work.start();
        work.setPriority(Thread.MIN_PRIORITY);
    }
    
    /**
     * Holder for the source and destination area
     */
    private Box box_main;
    
    /**
     * The Source area
     */
    private JPanel pnl_source = new JPanel();
    
    /**
     * The destination area
     */
    private JPanel pnl_dest = new JPanel();
    
    /**
     * The source book label
     */
    private JLabel lbl_source = new JLabel();
    
    /**
     * The source picker
     */
    protected JComboBox cbo_source = new JComboBox();

    /**
     * The model for the sources.
     * Bibles are required in GeneratorRunnable.run()
     */
    protected BooksComboBoxModel mdl_source = new BooksComboBoxModel(BookFilters.getBibles());

    /**
     * Layout for the destination panel
     */
    private GridBagLayout lay_dest = new GridBagLayout();

    /**
     * The new version name label
     */
    private JLabel lbl_name = new JLabel();

    /**
     * Label for the new name class
     */
    private JLabel lbl_driver = new JLabel();

    /**
     * Input field for the name class
     */
    protected JComboBox cbo_driver = new JComboBox();

    /**
     * The model for the drivers
     */
    protected DriversComboBoxModel mdl_driver = new DriversComboBoxModel(false);

    /**
     * The progress area
     */
    private JPanel pnl_prog = new JPanel();

    /**
     * The progress bar
     */
    protected JProgressBar bar_prog = new JProgressBar();

    /**
     * The button bar
     */
    private JPanel pnl_buttons = new JPanel();

    /**
     * Layout for the button bar
     */
    private FlowLayout lay_buttons = new FlowLayout();

    /**
     * The generate button
     */
    protected JButton btn_generate = new JButton();

    /**
     * The close button, only used if we are in our own Frame
     */
    protected JButton btn_close = null;

    /**
     * The verify checkbox
     */
    protected JCheckBox chk_verify = new JCheckBox();

    /**
     * Work in progress
     */
    protected Thread work;

    /**
     * The progress listener
     */
    protected CustomProgressListener cpl = new CustomProgressListener();

    /**
     * A class to be run in a Thread to do the real work of generating the
     * new Bible
     */
    class GeneratorRunnable implements Runnable
    {
        public void run()
        {
            // While we are working stop anyone editing the values
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    cbo_source.setEnabled(false);
                    cbo_driver.setEnabled(false);
                    btn_generate.setEnabled(false);
                    chk_verify.setEnabled(false);
                    btn_close.setText("Cancel");
                }
            });

            try
            {
                // Get the values
                // This cast is safe because the ctor filers for Bibles
                Bible source = (Bible) mdl_source.getSelectedBookMetaData().getBook();
                BookDriver dest_driver = mdl_driver.getSelectedDriver();

                // The real work
                // This cast is safe because we passed in a Bible
                Bible dest_version = (Bible) dest_driver.create(source);

                // Check
                if (chk_verify.isEnabled())
                {
                    Verifier ver = new Verifier(source, dest_version);

                    CompareResultsPane results = new CompareResultsPane(ver);
                    results.setCheckText("");
                    results.setCheckPassages(Verifier.WHOLE);
                    results.showInFrame(GuiUtil.getFrame(GeneratorPane.this));
                    results.startStop();
                }
            }
            catch (final Exception ex)
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        ExceptionPane.showExceptionDialog(GeneratorPane.this, ex);
                    }
                });
            }

            // Re-enable the values
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    cbo_source.setEnabled(true);
                    cbo_driver.setEnabled(true);
                    btn_generate.setEnabled(true);
                    chk_verify.setEnabled(true);
                    btn_close.setText("Close");
                }
            });
        }
    }

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
                    bar_prog.setString(job.getStateDescription() + ": (" + percent + "%)");
                    bar_prog.setValue(percent);
                }
            });
        }
    }
}
