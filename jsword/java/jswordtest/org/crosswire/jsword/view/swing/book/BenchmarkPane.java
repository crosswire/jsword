
package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.control.test.Speed;
import org.crosswire.common.swing.EirPanel;
import org.crosswire.common.util.Reporter;

/**
 * BenchmarkPane allows an application to test the speed of a Bible by
 * giving it some stress tests.
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
public class BenchmarkPane extends EirPanel
{
    /**
     * Basic constructor
     */
    public BenchmarkPane()
    {
        jbInit();
    }

    /**
     * Create the GUI components
     */
    private void jbInit()
    {
        cbo_bible.setModel(mdl_bible);
        btn_go.setMnemonic('G');
        btn_go.setText("Go");
        btn_go.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev) { benchmark(); }
        });

        pnl_north.setLayout(new BorderLayout(5, 5));
        pnl_north.add(btn_go, BorderLayout.EAST);
        pnl_north.add(cbo_bible, BorderLayout.CENTER);

        txt_results.setColumns(30);
        txt_results.setRows(10);
        scr_results.getViewport().add(txt_results, null);

        this.setLayout(new BorderLayout(5, 5));
        this.add(pnl_north, BorderLayout.NORTH);
        this.add(scr_results, BorderLayout.CENTER);
    }

    /**
     * Show this Panel in a new dialog
     */
    public void showInDialog(Component parent)
    {
        showInDialog(parent, "Benchmark", false);
    }

    /**
     * Run the benchmark on the selected Bible
     */
    private void benchmark()
    {
        Bible bible = mdl_bible.getSelectedBible();

        Speed speed = new Speed(bible);
        speed.run();

        try
        {
            float time = speed.getBenchmark() / 1000;
            txt_results.append("Benchmark for '" + bible.getMetaData().getName() + "': " + time + "s\n");
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
            txt_results.append("Benchmark failed. No timing available.\n");
        }
    }

    private BiblesComboBoxModel mdl_bible = new BiblesComboBoxModel();
    private JPanel pnl_north = new JPanel();
    private JButton btn_go = new JButton();
    private JComboBox cbo_bible = new JComboBox();
    private JScrollPane scr_results = new JScrollPane();
    private JTextArea txt_results = new JTextArea();
}

