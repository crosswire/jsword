package org.crosswire.common.config.swing;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.crosswire.common.config.Choice;
import org.crosswire.common.config.Config;
import org.crosswire.common.swing.FormPane;

/**
 * A Tabbed view of a config class.
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
public class TreeConfigEditor extends AbstractConfigEditor
{
    /**
     * <br />Danger - this method is not called by the TreeConfigEditor
     * constructor, it is called by the AbstractConfigEditor constructor so
     * any field initializers will be called AFTER THIS METHOD EXECUTES
     * so don't use field initializers.
     */
    protected void initializeGUI()
    {
        JPanel panel = new JPanel();
        JPanel blank = new JPanel();
        DefaultTreeCellRenderer dtcr = new DefaultTreeCellRenderer();

        ctm = new ConfigureTreeModel();
        tree = new JTree();
        title = new JLabel();
        deck = new JPanel();
        layout = new CardLayout();

        blank.add(new JLabel(Msg.SELECT_SUBNODE.toString()));

        deck.setLayout(layout);
        deck.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        deck.add(blank, BLANK);

        dtcr.setLeafIcon(TASK_ICON_SMALL);

        // These settings will need to change if we have a true tree.
        tree.setModel(ctm);
        tree.setCellRenderer(dtcr);
        tree.setPreferredSize(new Dimension(150, 150));
        tree.setShowsRootHandles(false);
        tree.setRootVisible(false);
        tree.putClientProperty("JTree.lineStyle", "None"); //$NON-NLS-1$ //$NON-NLS-2$
        tree.setSelectionRow(0);
        tree.addTreeSelectionListener(new TreeSelectionListener()
        {
            public void valueChanged(TreeSelectionEvent ev)
            {
                selectCard();
            }
        });

        title.setIcon(TASK_ICON_LARGE);
        title.setFont(new Font(getFont().getName(), Font.PLAIN, 16));
        title.setPreferredSize(new Dimension(30, 30));
        title.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        title.setBackground(Color.gray);
        title.setForeground(Color.white);
        title.setOpaque(true);
        title.setText(Msg.PROPERTIES.toString());
        title.setAlignmentX(SwingConstants.LEFT);

        // Use this if you want to have the tree touch the bottom. Then add
        // the button panel to content.South
        // JPanel content = new JPanel();
        // content.setLayout(new BorderLayout());
        // content.add(BorderLayout.CENTER, deck);

        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        panel.add(BorderLayout.NORTH, title);
        panel.add(BorderLayout.CENTER, deck);

        setLayout(new BorderLayout(5, 10));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        //if (cards > 1)
            add(BorderLayout.WEST, new JScrollPane(tree));

        add(BorderLayout.CENTER, panel);
        add(BorderLayout.SOUTH, new ButtonPane(this));
    }

    /**
     * Updates to the tree that we need to do on any change
     */
    protected void updateTree()
    {
        // expand the tree
        int row = 0;
        while (row < tree.getRowCount())
        {
            tree.expandRow(row++);
        }

        ctm.fireTreeStructureChanged(this);
    }

    /**
     * Add a Choice to our set of panels
     */
    protected void addChoice(String key, Choice model)
    {
        super.addChoice(key, model);

        // Sort the tree out
        String path = Config.getPath(model.getFullPath());
        FormPane card = (FormPane) decks.get(path);
        if (card != null && card.getParent() == null)
        {
            JScrollPane scroll = new JScrollPane(card);
            scroll.setBorder(BorderFactory.createEmptyBorder());
            deck.add(path, scroll);
        }
    }

    /**
     * Add a Choice to our set of panels
     */
    protected void removeChoice(String key, Choice model)
    {
        super.removeChoice(key, model);

        // Sort the tree out
        String path = Config.getPath(model.getFullPath());
        FormPane card = (FormPane) decks.get(path);
        if (card != null && card.isEmpty())
        {
            deck.remove(card.getParent());
        }
    }

    /**
     * Used to update the configuration panel whenever someone
     * selects a different item form the tree on the LHS of the
     * configuation dialog.
     */
    public void selectCard()
    {
        Object obj = tree.getLastSelectedPathComponent();
        if (obj == null)
        {
            return;
        }

        title.setText(obj + " " + Msg.PROPERTIES); //$NON-NLS-1$

        // Get the name of the current deck
        Object[] list = tree.getSelectionPath().getPath();
        StringBuffer path = new StringBuffer();

        for (int i = 1; i < list.length; i++)
        {
            if (i > 1)
            {
                path.append("."); //$NON-NLS-1$
            }

            path.append(list[i].toString());
        }

        String key = path.toString();
        if (decks.containsKey(key))
        {
            layout.show(deck, key);
        }
        else
        {
            layout.show(deck, BLANK);
        }

        deck.repaint();
    }

    /**
     * The name of the blank tab
     */
    protected static final String BLANK = "$$BLANK$$"; //$NON-NLS-1$

    /**
     * The tree containing the Field sets
     */
    protected JTree tree;

    /**
     * The custom tree model for the tree
     */
    protected ConfigureTreeModel ctm;

    /**
     * The title for the config panels
     */
    protected JLabel title;

    /**
     * Contains the configuration panels
     */
    protected JPanel deck;

    /**
     * Layout for the config panels
     */
    protected CardLayout layout;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3256720688860576049L;

    /**
     * A custom data model for the TreeConfig Tree
     * @author Claude Duguay
     * @author Joe Walker
     */
    protected class ConfigureTreeModel implements TreeModel
    {
        /* (non-Javadoc)
         * @see javax.swing.tree.TreeModel#getRoot()
         */
        public Object getRoot()
        {
            return root;
        }

        /**
         * Get a Vector of the children rooted at path
         */
        protected List getChildren(String path)
        {
            List retcode = new ArrayList();

            Iterator it = config.getPaths();
            while (it.hasNext())
            {
                String temp = (String) it.next();

                if (temp.startsWith(path) && !temp.equals(path))
                {
                    // Chop off the similar start
                    temp = temp.substring(path.length());
                    if (temp.startsWith(".")) //$NON-NLS-1$
                    {
                        temp = temp.substring(1);
                    }

                    // Chop off all after the first dot
                    int dot_pos = temp.indexOf("."); //$NON-NLS-1$
                    if (dot_pos != -1)
                    {
                        temp = temp.substring(0, dot_pos);
                    }

                    // Add it to the list if needed
                    if (temp.length() > 0 && !retcode.contains(temp))
                    {
                        retcode.add(temp);
                    }
                }
            }

            return retcode;
        }

        /* (non-Javadoc)
         * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
         */
        public Object getChild(Object parent, int index)
        {
            String path = ((Node) parent).getFullName();
            String name = (String) getChildren(path).get(index);
            return new Node(path, name);
        }

        /* (non-Javadoc)
         * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
         */
        public int getChildCount(Object parent)
        {
            String path = ((Node) parent).getFullName();
            return getChildren(path).size();
        }

        /* (non-Javadoc)
         * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
         */
        public boolean isLeaf(Object node)
        {
            String path = ((Node) node).getFullName();
            return getChildren(path).size() == 0;
        }

        /* (non-Javadoc)
         * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath, java.lang.Object)
         */
        public void valueForPathChanged(TreePath path, Object value)
        {
        }

        /* (non-Javadoc)
         * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
         */
        public int getIndexOfChild(Object parent, Object child)
        {
            String path = ((Node) parent).getFullName();
            List children = getChildren(path);
            return children.indexOf(child);
        }

        /* (non-Javadoc)
         * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
         */
        public void addTreeModelListener(TreeModelListener li)
        {
            listeners.add(TreeModelListener.class, li);
        }

        /* (non-Javadoc)
         * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
         */
        public void removeTreeModelListener(TreeModelListener li)
        {
            listeners.remove(TreeModelListener.class, li);
        }

        /**
         * Notify all listeners that have registered interest for
         * notification on this event type.  The event instance
         * is lazily created using the parameters passed into
         * the fire method.
         * @see EventListenerList
         */
        protected void fireTreeStructureChanged(Object source)
        {
            fireTreeStructureChanged(source, new Object[] { root });
        }

        /**
         * Notify all listeners that have registered interest for
         * notification on this event type.  The event instance
         * is lazily created using the parameters passed into
         * the fire method.
         * @see EventListenerList
         */
        protected void fireTreeStructureChanged(Object source, Object[] path)
        {
            // Guaranteed to return a non-null array
            Object[] array = listeners.getListenerList();
            TreeModelEvent ev = null;

            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = array.length - 2; i >= 0; i -= 2)
            {
                if (array[i] == TreeModelListener.class)
                {
                    // Lazily create the event:
                    if (ev == null)
                    {
                        ev = new TreeModelEvent(source, path);
                    }

                    ((TreeModelListener) array[i + 1]).treeStructureChanged(ev);
                }
            }
        }

        /**
         * The Listeners.
         */
        protected EventListenerList listeners = new EventListenerList();

        /**
         * The root node
         */
        private Node root = new Node("", "");  //$NON-NLS-1$//$NON-NLS-2$
    }

    /**
     * Simple Tree Node
     */
    protected static class Node
    {
        /**
         * Create a node with a name and path
         */
        protected Node(String path, String name)
        {
            this.path = path;
            this.name = name;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        public String toString()
        {
            return name;
        }

        /**
         * The path to us
         */
        public String getFullName()
        {
            if (path.length() == 0 || name.length() == 0)
            {
                return path + name;
            }
            return path + "." + name; //$NON-NLS-1$
        }

        /**
         * The displayed string
         */
        private String name;

        /**
         * The path to us
         */
        private String path;
    }
}
