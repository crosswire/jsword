package org.crosswire.jsword.view.swing.book;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.TreeNode;

import org.crosswire.common.util.IteratorEnumeration;
import org.crosswire.jsword.book.basic.DefaultKeyList;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyList;

/**
 * An implementation of TreeNode that reads from Keys and KeyLists.
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
public class KeyTreeNode implements TreeNode
{
    /**
     * Simple ctor
     */
    public KeyTreeNode(Key key, TreeNode parent)
    {
        this.key = key;
        this.parent = parent;

        if (key instanceof KeyList)
        {
            this.keylist = DefaultKeyList.getKeyList(key);
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeNode#getChildCount()
     */
    public int getChildCount()
    {
        if (key instanceof KeyList)
        {
            return keylist.size();
        }
        else
        {
            return 0;
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeNode#getAllowsChildren()
     */
    public boolean getAllowsChildren()
    {
        return key instanceof KeyList;
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeNode#isLeaf()
     */
    public boolean isLeaf()
    {
        return !(key instanceof KeyList);
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeNode#children()
     */
    public Enumeration children()
    {
        if (key instanceof KeyList)
        {
            return new IteratorEnumeration(keylist.iterator());
        }
        else
        {
            return new Vector().elements();
        }
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
    public TreeNode getChildAt(int index)
    {
        if (key instanceof KeyList)
        {
            Key child = keylist.get(index);
            return new KeyTreeNode(child, this);
        }
        else
        {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeNode#getIndex(javax.swing.tree.TreeNode)
     */
    public int getIndex(TreeNode node)
    {
        if (node instanceof KeyTreeNode)
        {
            KeyTreeNode keynode = (KeyTreeNode) node;
            Key that = keynode.getKey();
            
            return keylist.indexOf(that);
        }
        else
        {
            return -1;
        }
    }

    /**
     * Accessor for the key
     */
    public Key getKey()
    {
        return key;
    }

    private Key key;
    private KeyList keylist;
    private TreeNode parent;
}
