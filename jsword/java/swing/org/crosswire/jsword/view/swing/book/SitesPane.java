package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.crosswire.common.swing.EirAbstractAction;

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
public class SitesPane extends JPanel
{
    /**
     * Simple ctor
     */
    public SitesPane()
    {
        jbInit();
    }

    /**
     * Build the GUI components
     */
    private void jbInit()
    {
        btnOK.setMnemonic('O');
        btnOK.setText("OK");
        btnOK.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                close();
            }
        });

        btnAdd.setMnemonic('S');
        btnAdd.setText("Add Site ...");
        btnAdd.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                addSite();
            }
        });

        pnlButtons.setLayout(new FlowLayout());
        pnlButtons.add(btnAdd, null);
        pnlButtons.add(btnOK);

        tabMain.add(steLocal, "Local");

        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.add(tabMain, BorderLayout.CENTER);
    }

    /**
     * Add a site to the list of install sources.
     */
    protected void addSite()
    {
        System.out.println("add site ...");
    }

    /**
     * We are done, close the window
     */
    protected void close()
    {
        if (dlgMain != null)
        {
            dlgMain.setVisible(false);
        }
    }

    /**
     * Open this Panel in it's own dialog box.
     */
    public void showInDialog(Component parent)
    {
        dlgMain = new JDialog(JOptionPane.getFrameForComponent(parent));
        dlgMain.getContentPane().setLayout(new BorderLayout());
        dlgMain.getContentPane().add(this, BorderLayout.CENTER);
        dlgMain.getContentPane().add(pnlButtons, BorderLayout.SOUTH);
        dlgMain.setTitle("Available Books");
        dlgMain.setModal(true);
        dlgMain.addWindowListener(new WindowAdapter()
        {
            public void windowClosed(WindowEvent ev)
            {
                close();
            }
        });
        dlgMain.pack();
        dlgMain.setLocationRelativeTo(parent);
        dlgMain.setVisible(true);
    }

    /*
     * GUI Components
     */
    private JDialog dlgMain = null;
    private JPanel pnlButtons = new JPanel();
    private JButton btnOK = new JButton();
    private JTabbedPane tabMain = new JTabbedPane();
    private JButton btnAdd = new JButton();
    private SitePane steLocal = new SitePane();

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
    public static class OpenAction extends EirAbstractAction
    {
        /**
         * Simple ctor
         */
        public OpenAction(Component parent)
        {
            super("Books ...",
                  "toolbarButtonGraphics/general/Import16.gif",
                  "toolbarButtonGraphics/general/Import24.gif",
                  "Display/Install Books", "Investigate Books and link to sites that allow new downloads",
                  'A', null);

            this.parent = parent;
            sites = new SitesPane();
        }

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent ev)
        {
            sites.showInDialog(parent);
        }

        private Component parent;
        private SitesPane sites = null;
    }
}
