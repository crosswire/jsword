
package org.crosswire.common.config.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.crosswire.common.config.Choice;
import org.crosswire.common.config.Config;
import org.crosswire.common.util.Reporter;

/**
 * A mutable view of Fields setting array.
 * <p>A few of the ideas in this code came from an article in the JDJ about
 * configuration. However the Config package has a number of huge
 * differences, the biggest being what it does with its config info. The
 * JDJ article assumed that you'd only ever want to edit a properties file
 * and that the rest of the app didn't care much, and that the tree style
 * view was the only one you would ever need. This package is a re-write
 * that addresses these shortcomings and others.
 * <p>The JDJ article uses a <code>DeckLayout</code> instead of the
 * <code>java.awt.CardLayout</code> because there are supposedly some focus
 * problems in the CardLayout code. I have not noticed these, and so I have
 * used the more standard CardLayout, however a copy of the DeckLayout code
 * is in the <code>org.crosswire.common.swing</code> package. Maybe we should remove it
 * from here - the redistribution status of it is not clear.
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
public class AdvancedConfigPane extends TreeConfigPane
{
    /**
     * Create a TreeConfig panel Field set
     * @param config The set of Fields us display
     */
    public AdvancedConfigPane(Config config)
    {
        super(config);
    }

    /**
     * Now this wasn't created with JBuilder but maybe, just maybe, by
     * calling my method this, JBuilder may grok it.
     */
    protected void jbInit()
    {
        ctm = new AdvancedConfigureTreeModel();
        tree = new JTree();
        JScrollPane scroll = new JScrollPane();
        CustomTreeCellRenderer render = new CustomTreeCellRenderer();
        comps = new Hashtable();

        // Hack: tree depends on it being a Color not a sub of it.
        Color orig = UIManager.getColor("control");
        Color bg = new Color(orig.getRed(), orig.getGreen(), orig.getBlue());

        // This seems to be broken ...
        render.setLeafIcon(tasksm);
        render.setBackgroundNonSelectionColor(bg);

        scroll.setPreferredSize(new Dimension(150, 150));
        scroll.setViewportView(tree);

        tree.putClientProperty("JTree.lineStyle", "Angled");
        tree.setCellEditor(new CustomTreeCellEditor(tree, render));
        tree.setBackground(bg);
        tree.setCellRenderer(render);
        tree.setShowsRootHandles(true);
        tree.setRootVisible(false);
        tree.setModel(ctm);
        tree.setSelectionRow(0);
        tree.setEditable(true);

        setLayout(new BorderLayout(5, 10));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        add("Center", scroll);
        add("South", getButtonPane());
    }

    /**
     * Updates to the tree that we need to do on any change
     * @return A button panel
     */
    protected void updateTree()
    {
        // expand the tree
        /*
        int row = 0;
        while (row < tree.getRowCount())
        {
        tree.expandRow(row++);
        }
        */

        ctm.fireTreeStructureChanged(this);
    }

    /**
     * Add a Choice to our set of panels
     */
    protected void addChoice(String key, Choice model)
    {
        try
        {
            Field field = FieldMap.getField(model);
            fields.put(key, field);

            // Add the Field to the FieldPanel
            JComponent comp = field.getComponent();
            comp.setToolTipText(model.getHelpText());
            comps.put(key, comp);

            // Fill in the current value
            String value = config.getLocal(key);
            field.setValue(value);
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Add a Choice to our set of panels
     */
    protected void removeChoice(String key)
    {
        try
        {
            Field field = (Field) fields.get(key);
            if (field != null)
            {
                fields.remove(field);
            }

            Component comp = (Component) comps.get(key);
            if (comp != null)
            {
                comps.remove(key);
            }
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
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
        if (obj == null) return;

        title.setText(""+obj+" Properties");

        // Get the name of the current deck
        Object[] list = tree.getSelectionPath().getPath();
        StringBuffer path = new StringBuffer();

        for (int i=1; i<list.length; i++)
        {
            if (i > 1)
            {
                path.append(".");
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

    /** A hash of components */
    protected Hashtable comps = null;

    /**
     * A custom data model for the TreeConfig Tree
     * @author Claude Duguay
     * @author Joe Walker
     */
    class AdvancedConfigureTreeModel extends ConfigureTreeModel
    {
        /**
         * Get a Vector of the children rooted at path
         */
        protected List getChildren(String path)
        {
            List retcode = new ArrayList();

            Iterator it = config.getNames();
            while (it.hasNext())
            {
                String temp = (String) it.next();

                if (temp.startsWith(path) && !temp.equals(path))
                {
                    // Chop off the similar start
                    temp = temp.substring(path.length());
                    if (temp.startsWith("."))
                    {
                        temp = temp.substring(1);
                    }

                    // Chop off all after the first dot
                    int dot_pos = temp.indexOf(".");
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

        /**
         * Returns the child of <I>parent</I> at index <I>index</I> in the parent's
         * child array.  <I>parent</I> must be a node previously obtained from
         * this data source. This should not return null if <i>index</i>
         * is a valid index for <i>parent</i> (that is <i>index</i> >= 0 &&
         * <i>index</i> < getChildCount(<i>parent</i>)).
         * @param   parent  a node in the tree, obtained from this data source
         * @return  the child of <I>parent</I> at index <I>index</I>
         */
        public Object getChild(Object parent, int index)
        {
            if (parent instanceof CompNode)
                return null;

            String path = ((Node) parent).getFullName();
            List children = getChildren(path);

            if (children.size() == 0)
                return new CompNode(path);

            String name = (String) children.get(index);
            return new Node(path, name);
        }

        /**
         * Returns the number of children of <I>parent</I>.  Returns 0 if the node
         * is a leaf or if it has no children.  <I>parent</I> must be a node
         * previously obtained from this data source.
         * @param   parent  a node in the tree, obtained from this data source
         * @return  the number of children of the node <I>parent</I>
         */
        public int getChildCount(Object parent)
        {
            if (parent instanceof CompNode)
                return 0;

            String path = ((Node) parent).getFullName();
            int children = getChildren(path).size();
            if (children == 0)
                children = 1;

            return children;
        }

        /**
         * Returns true if <I>node</I> is a leaf.  It is possible for this method
         * to return false even if <I>node</I> has no children.  A directory in a
         * filesystem, for example, may contain no files; the node representing
         * the directory is not a leaf, but it also has no children.
         * @param   node    a node in the tree, obtained from this data source
         * @return  true if <I>node</I> is a leaf
         */
        public boolean isLeaf(Object node)
        {
            return node instanceof CompNode;
        }
    }

    /**
     * Simple Tree Node
     */
    static class CompNode
    {
        /**
         * Create a node with a name and path
         */
        public CompNode(String path)
        {
            this.path = path;
        }

        /**
         * How we are displayed
         */
        public String toString()
        {
            return path;
        }

        /**
         * The path to us
         */
        public String getFullName()
        {
            return path;
        }

        /** The path to us */
        private String path;
    }

    /**
     *
     */
    class CustomTreeCellRenderer extends DefaultTreeCellRenderer
    {
        /**
         *
         */
        public Component getTreeCellRendererComponent(JTree jtree, Object value, boolean isselected, boolean expanded, boolean leaf, int row, boolean focus)
        {
            if (value instanceof CompNode)
            {
                JComponent comp = (JComponent) comps.get(value.toString());

                if (comp == null)
                {
                    return super.getTreeCellRendererComponent(jtree, value, isselected, expanded, leaf, row, focus);
                }

                if (isselected)
                {
                    comp.setBorder(BorderFactory.createLineBorder(Color.black));
                }
                else
                {
                    comp.setBorder(BorderFactory.createEmptyBorder());
                }

                return comp;
            }
            else
            {
                return super.getTreeCellRendererComponent(jtree, value, isselected, expanded, leaf, row, focus);
            }
        }
    }

    /**
     *
     */
    static class CustomTreeCellEditor extends DefaultTreeCellEditor
    {
        public CustomTreeCellEditor(JTree tree, DefaultTreeCellRenderer render)
        {
            super(tree, render);
        }

        /*
        *
        *
        public boolean isCellEditable(Event ev)
        {
        }

        /**
        *
        */
        public Component getTreeCellEditorComponent(JTree jtree, Object value, boolean selected, boolean expanded, boolean leaf, int row)
        {
            /*
            if (value instanceof FieldTreeNode)
            {
                return ((FieldTreeNode) value).getJComponent();
            }
            else
            */
            {
                return super.getTreeCellEditorComponent(jtree, value, selected, expanded, leaf, row);
            }
        }
    }
}
