
package org.crosswire.bible.view.swing.test;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import org.crosswire.bible.control.test.TestList;
import org.crosswire.swing.DocumentWriter;
import org.crosswire.swing.EirPanel;
import org.crosswire.util.StringUtil;
import org.crosswire.util.TestBase;

/**
* Tester creates a list of the tests that can be run on the system,
* allows the user to select which of the tests to run, and then
* kicks them off in a separate thread.
*
* <table border='1' cellPadding='3' cellSpacing='0' width="100%">
* <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
* Distribution Licence:<br />
* Project B is free software; you can redistribute it
* and/or modify it under the terms of the GNU General Public License,
* version 2 as published by the Free Software Foundation.<br />
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* General Public License for more details.<br />
* The License is available on the internet
* <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
* <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
* MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
* The copyright to this program is held by it's authors.
* </font></td></tr></table>
* @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
* @see <{docs.Licence}>
* @author Joe Walker
*/
public class TesterPane extends EirPanel
{
    /**
    * Construct a Test tool, this simply calls jbInit
    */
    public TesterPane()
    {
        jbInit();
    }

    /**
    * Create the GUI components. Possible optimization - start the split
    * pane with only the list showing, and then bring the text area into
    * view when the results start appearing.
    */
    private void jbInit()
    {
        lst_tests.setVisibleRowCount(5);
        scr_tests.getViewport().add(lst_tests, null);
        pnl_tests.setBorder(BorderFactory.createTitledBorder("Available Tests"));
        pnl_tests.setLayout(new BorderLayout());
        pnl_tests.add(scr_tests, BorderLayout.CENTER);

        txt_results.setRows(10);
        txt_results.setEditable(false);
        scr_results.getViewport().add(txt_results, null);
        pnl_results.setBorder(BorderFactory.createTitledBorder("Test Results"));
        pnl_results.setLayout(new BorderLayout());
        pnl_results.add(scr_results, BorderLayout.CENTER);

        spl_main.setContinuousLayout(true);
        spl_main.setBorder(BorderFactory.createEmptyBorder());
        spl_main.setOrientation(JSplitPane.VERTICAL_SPLIT);
        spl_main.add(pnl_tests, JSplitPane.TOP);
        spl_main.add(pnl_results, JSplitPane.BOTTOM);

        btn_test.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev)
            {
                test();
            }
        });
        btn_test.setText("Run Tests");
        btn_test.setMnemonic('R');
        btn_clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev)
            {
                clear();
            }
        });
        btn_clear.setText("Clear Results");
        btn_clear.setMnemonic('C');
        lay_buttons.setAlignment(FlowLayout.RIGHT);
        pnl_buttons.setLayout(lay_buttons);
        pnl_buttons.add(btn_test, null);
        pnl_buttons.add(btn_clear, null);

        this.setLayout(new BorderLayout());
        this.add(spl_main, BorderLayout.CENTER);
        this.add(pnl_buttons, BorderLayout.SOUTH);
    }

    /**
     * Show this Panel in a new dialog
     */
    public void showInDialog(Component parent)
    {
        showInDialog(parent, "Unit Tests", false);
    }

    /**
    * Run the tests selected in a separate thread, placing the results
    * in the results window.
    */
    public void test()
    {
        Object[] objs = lst_tests.getSelectedValues();
        String[] selected = StringUtil.getStringArray(objs);
        TestThread runnable = new TestThread(selected);

        Thread work = new Thread(runnable);
        work.setPriority(Thread.MIN_PRIORITY);
        work.start();
    }

    /**
    * Clear the test results window
    */
    public void clear()
    {
        txt_results.setText("");
    }

    /** The packages to be tested */
    private Hashtable testers = TestList.getTesters();

    /** The button bar at the bottom of the panel */
    private JPanel pnl_buttons = new JPanel();

    /** The layout for the button bar */
    private FlowLayout lay_buttons = new FlowLayout();

    /** The run tests button */
    private JButton btn_test = new JButton();

    /** The clear tests results button */
    private JButton btn_clear = new JButton();

    /** The central split pane */
    private JSplitPane spl_main = new JSplitPane();

    /** Panel holding the test scroller */
    private JPanel pnl_tests = new JPanel();

    /** Scroller for the test list */
    private JScrollPane scr_tests = new JScrollPane();

    /** The list of available test */
    private JList lst_tests = new JList(TestList.getNames());

    /** Panel holding the result scroller */
    private JPanel pnl_results = new JPanel();

    /** Scroller for the result text area */
    private JScrollPane scr_results = new JScrollPane();

    /** The text output from the tests */
    private JTextArea txt_results = new JTextArea();

    /**
    * A thread for running tests in
    */
    class TestThread implements Runnable
    {
        /**
        * Configure a TestThread with a set of tests to run
        */
        TestThread(String[] selected)
        {
            this.selected = selected;
        }

        /**
        * Run the configured tests
        */
        public void run()
        {
            btn_test.setEnabled(false);
            btn_clear.setEnabled(false);

            PrintWriter out = new PrintWriter(new DocumentWriter(txt_results.getDocument()));

            for (int i=0; i<selected.length; i++)
            {
                TestBase test = (TestBase) testers.get(selected[i]);
                test.test(out, false);
            }

            btn_clear.setEnabled(true);
            btn_test.setEnabled(true);
        }

        /** The tests to run */
        private String[] selected;
    }
}

