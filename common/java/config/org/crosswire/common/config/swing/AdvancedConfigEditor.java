package org.crosswire.common.config.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.crosswire.common.config.Choice;
import org.crosswire.common.util.Reporter;

/**
 * A mutable view of Fields setting array.
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
public class AdvancedConfigEditor extends TreeConfigEditor
{
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
        comps = new HashMap();

        // Hack: tree depends on it being a Color not a sub of it.
        Color orig = UIManager.getColor("control"); //$NON-NLS-1$
        Color bg = new Color(orig.getRed(), orig.getGreen(), orig.getBlue());

        // This seems to be broken ...
        render.setLeafIcon(TASK_ICON_SMALL);
        render.setBackgroundNonSelectionColor(bg);

        scroll.setPreferredSize(new Dimension(150, 150));
        scroll.setViewportView(tree);

        tree.putClientProperty("JTree.lineStyle", "Angled");  //$NON-NLS-1$//$NON-NLS-2$
        tree.setBackground(bg);
        tree.setCellRenderer(render);
        tree.setShowsRootHandles(true);
        tree.setRootVisible(false);
        tree.setModel(ctm);
        tree.setSelectionRow(0);
        tree.setEditable(true);

        setLayout(new BorderLayout(5, 10));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        add(BorderLayout.CENTER, scroll);
        add(BorderLayout.SOUTH, new ButtonPane(this));
    }

    /**
     * Updates to the tree that we need to do on any change
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

        title.setText(obj + Msg.PROPERTIES.toString());

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
     * A hash of components
     */
    protected Map comps = null;

    /**
     * A custom data model for the TreeConfig Tree
     * @author Joe Walker
     */
    private class AdvancedConfigureTreeModel extends ConfigureTreeModel
    {
        /* (non-Javadoc)
         * @see org.crosswire.common.config.swing.TreeConfigEditor.ConfigureTreeModel#getChildren(java.lang.String)
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
            if (parent instanceof CompNode)
            {
                return null;
            }

            String path = ((Node) parent).getFullName();
            List children = getChildren(path);

            if (children.size() == 0)
            {
                return new CompNode(path);
            }

            String name = (String) children.get(index);
            return new Node(path, name);
        }

        /* (non-Javadoc)
         * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
         */
        public int getChildCount(Object parent)
        {
            if (parent instanceof CompNode)
            {
                return 0;
            }

            String path = ((Node) parent).getFullName();
            int children = getChildren(path).size();
            if (children == 0)
            {
                children = 1;
            }

            return children;
        }

        /* (non-Javadoc)
         * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
         */
        public boolean isLeaf(Object node)
        {
            return node instanceof CompNode;
        }
    }

    /**
     * Simple Tree Node
     */
    private static class CompNode
    {
        /**
         * Create a node with a name and path
         */
        public CompNode(String path)
        {
            this.path = path;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
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

        /**
         * The path to us
         */
        private String path;
    }

    /**
     * The renderer for our tree
     */
    private class CustomTreeCellRenderer extends DefaultTreeCellRenderer
    {
        /* (non-Javadoc)
         * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
         */
        public Component getTreeCellRendererComponent(JTree jtree, Object value, boolean isselected, boolean expanded, boolean leaf, int row, boolean focus)
        {
            if (!(value instanceof CompNode))
            {
                return super.getTreeCellRendererComponent(jtree, value, isselected, expanded, leaf, row, focus);
            }

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
    }
}
