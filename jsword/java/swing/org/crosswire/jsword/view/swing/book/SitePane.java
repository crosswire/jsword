package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;

/**
 * A panel for use within a SitesPane to display one set of Books that are
 * installed or could be installed.
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
public class SitePane extends JPanel
{
    /**
     * Simple ctor
     */
    public SitePane()
    {
        jbInit();
    }

    /**
     * Build the GUI components
     */
    private void jbInit()
    {
        pnlActions.add(btnInstall);
        btnInstall.setMnemonic('I');
        btnInstall.setText("Install");
        lblAvailable.setDisplayedMnemonic('B');
        lblAvailable.setLabelFor(treAvailable);
        lblAvailable.setText("Installed Books:");
        pnlAvailable.setLayout(new BorderLayout());
        pnlAvailable.add(scrAvailable, BorderLayout.CENTER);
        pnlAvailable.add(pnlActions, BorderLayout.SOUTH);
        pnlAvailable.add(lblAvailable, BorderLayout.NORTH);
        scrAvailable.add(treAvailable);

        lblSelected.setDisplayedMnemonic('S');
        lblSelected.setLabelFor(tblSelected);
        lblSelected.setText("Selected Book:");
        pnlSelected.setLayout(new BorderLayout());
        pnlSelected.add(scrSelected, BorderLayout.CENTER);
        pnlSelected.add(lblSelected, BorderLayout.NORTH);
        scrSelected.add(tblSelected);

        sptMain.setResizeWeight(0.5);
        sptMain.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        sptMain.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        sptMain.setDividerSize(10);
        sptMain.setDividerLocation(200);
        sptMain.add(pnlAvailable, JSplitPane.LEFT);
        sptMain.add(pnlSelected, JSplitPane.RIGHT);

        this.setLayout(new BorderLayout());
        this.add(sptMain, BorderLayout.CENTER);
    }

    /*
     * GUI Components
     */
    private JScrollPane scrSelected = new JScrollPane();
    private JLabel lblAvailable = new JLabel();
    private JScrollPane scrAvailable = new JScrollPane();
    private JSplitPane sptMain = new JSplitPane();
    private JButton btnInstall = new JButton();
    private JPanel pnlActions = new JPanel();
    private JLabel lblSelected = new JLabel();
    private JTree treAvailable = new JTree();
    private JPanel pnlSelected = new JPanel();
    private JPanel pnlAvailable = new JPanel();
    private JTable tblSelected = new JTable();
}
