package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.crosswire.common.swing.MapTableModel;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.install.InstallException;
import org.crosswire.jsword.book.install.Installer;

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
     * For local installations
     */
    public SitePane()
    {
        this.installer = null;

        initialize();

        pnlActions.add(btnDelete);
        lblAvailable.setLabelFor(treAvailable);
        lblAvailable.setText("Installed Books:");
        lblAvailable.setDisplayedMnemonic('B');

        TreeModel model = new BooksTreeModel();
        treAvailable.setModel(model);
    }

    /**
     * For remote installations
     */
    public SitePane(Installer installer)
    {
        this.installer = installer;

        initialize();

        pnlActions.add(btnInstall);
        pnlActions.add(btnRefresh);
        lblAvailable.setLabelFor(treAvailable);
        lblAvailable.setText("Available Books:");
        lblAvailable.setDisplayedMnemonic('B');

        TreeModel model = getTreeModel(installer.getIndex());
        treAvailable.setModel(model);
    }

    /**
     * Build the GUI components
     */
    private void initialize()
    {
        btnDelete.setMnemonic('D');
        btnDelete.setText("Delete");
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                delete();
            }
        });
        btnInstall.setMnemonic('I');
        btnInstall.setText("Install");
        btnInstall.setEnabled(false);
        btnInstall.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                install();
            }
        });
        btnRefresh.setMnemonic('R');
        btnRefresh.setText("Refresh List");
        btnRefresh.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                refresh();
            }
        });
        pnlAvailable.setLayout(new BorderLayout());
        pnlAvailable.add(lblAvailable, BorderLayout.NORTH);
        pnlAvailable.add(scrAvailable, BorderLayout.CENTER);
        pnlAvailable.add(pnlActions, BorderLayout.SOUTH);
        treAvailable.setCellEditor(null);
        treAvailable.setRootVisible(false);
        treAvailable.setShowsRootHandles(true);
        treAvailable.setCellRenderer(new CustomTreeCellRenderer());
        treAvailable.addTreeSelectionListener(new TreeSelectionListener()
        {
            public void valueChanged(TreeSelectionEvent ev)
            {
                selected();
            }
        });
        scrAvailable.getViewport().add(treAvailable);
        scrAvailable.setPreferredSize(new Dimension(200, 400));

        lblSelected.setDisplayedMnemonic('S');
        lblSelected.setLabelFor(tblSelected);
        lblSelected.setText("Selected Book:");
        pnlSelected.setLayout(new BorderLayout());
        pnlSelected.add(scrSelected, BorderLayout.CENTER);
        pnlSelected.add(lblSelected, BorderLayout.NORTH);
        scrSelected.getViewport().add(tblSelected);
        scrAvailable.setPreferredSize(new Dimension(300, 400));

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

    /**
     * 
     */
    protected void delete()
    {
    }

    /**
     * 
     */
    protected void refresh()
    {
        if (installer != null)
        {
            try
            {
                TreeModel model = getTreeModel(installer.reloadIndex());
                treAvailable.setModel(model);
            }
            catch (InstallException ex)
            {
                Reporter.informUser(this, ex);
            }
        }
    }

    /**
     * 
     */
    protected void install()
    {
        if (installer != null)
        {
            TreePath path = treAvailable.getSelectionPath();
            if (path != null)
            {
                try
                {
                    Object last = path.getLastPathComponent();
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) last;
                    String name = (String) node.getUserObject();

                    installer.install(name);
                }
                catch (InstallException ex)
                {
                    Reporter.informUser(this, ex);
                }
            }
        }
    }

    /**
     * Something has been selected in the tree
     */
    protected void selected()
    {
        TreePath path = treAvailable.getSelectionPath();

        btnDelete.setEnabled(path != null);
        btnInstall.setEnabled(path != null);

        if (path != null)
        {
            Object last = path.getLastPathComponent();

            if (last instanceof BookMetaData)
            {
                BookMetaData bmd = (BookMetaData) last;
                tblSelected.setModel(new BookMetaDataTableModel(bmd));
            }
            else if (last instanceof DefaultMutableTreeNode && installer != null)
            {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) last;
                String name = (String) node.getUserObject();
                Properties prop = installer.getEntry(name);
                tblSelected.setModel(new MapTableModel(prop));
            }
            else
            {
                tblSelected.setModel(new MapTableModel(null));
            }
        }
    }

    /**
     * Convert an Installer index list into a Tree model
     */
    private TreeModel getTreeModel(List entries)
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Modules");
        for (Iterator it = entries.iterator(); it.hasNext();)
        {
            String entry = (String) it.next();
            root.add(new DefaultMutableTreeNode(entry));
        }
        DefaultTreeModel dtm = new DefaultTreeModel(root);
        return dtm;
    }

    /**
     * Display the BookMetaData as something better than toString()
     */
    private static final class CustomTreeCellRenderer extends DefaultTreeCellRenderer
    {
        /* (non-Javadoc)
         * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
         */
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row, boolean focus)
        {
            super.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, focus);

            if (value instanceof BookMetaData)
            {
                BookMetaData bmd = (BookMetaData) value;
                setText(bmd.getFullName());
            }
            else if (value instanceof String)
            {
                setText((String) value);
            }

            return this;
        }
    }

    /**
     * From which we get our list of installable modules
     */
    private Installer installer;

    /*
     * GUI Components
     */
    private JScrollPane scrSelected = new JScrollPane();
    private JLabel lblAvailable = new JLabel();
    private JScrollPane scrAvailable = new JScrollPane();
    private JSplitPane sptMain = new JSplitPane();
    private JButton btnInstall = new JButton();
    private JButton btnRefresh = new JButton();
    private JButton btnDelete = new JButton();
    private JPanel pnlActions = new JPanel();
    private JLabel lblSelected = new JLabel();
    private JTree treAvailable = new JTree();
    private JPanel pnlSelected = new JPanel();
    private JPanel pnlAvailable = new JPanel();
    private JTable tblSelected = new JTable();
}
