package org.crosswire.jsword.view.swing.reference;

import java.util.Enumeration;

import javax.swing.tree.TreeNode;

import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.passage.Key;

/**
 * A TreeNode that wraps a Key.
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
public class ReferenceKeyTreeNode implements TreeNode
{
    /**
     * Simple ctor
     */
    public ReferenceKeyTreeNode(ReferenceTreeModel model, BookMetaData bmd, TreeNode parent, Key key)
    {
        this.model = model;
        this.bmd = bmd;
        this.parent = parent;
        this.key = key;

        // NOWARN: this just shuts eclipse up - remove it later.
        this.bmd.hashCode();
        this.model.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return key.getName();
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeNode#getChildCount()
     */
    public int getChildCount()
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeNode#getAllowsChildren()
     */
    public boolean getAllowsChildren()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeNode#isLeaf()
     */
    public boolean isLeaf()
    {
        return true;
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeNode#children()
     */
    public Enumeration children()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeNode#getParent()
     */
    public TreeNode getParent()
    {
        return parent;
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeNode#getChildAt(int)
     */
    public TreeNode getChildAt(int childIndex)
    {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeNode#getIndex(javax.swing.tree.TreeNode)
     */
    public int getIndex(TreeNode node)
    {
        return 0;
    }

    /**
     * The key that we are representing
     */
    public Key getKey()
    {
        return key;
    }

    private ReferenceTreeModel model;
    private BookMetaData bmd;
    private TreeNode parent;
    private Key key;
}
