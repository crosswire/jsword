package org.crosswire.jsword.view.swing.book;

import javax.swing.tree.DefaultTreeModel;

import org.crosswire.jsword.passage.Key;

/**
 * A TreeModel that helps with working with Keys.
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
public class KeyTreeModel extends DefaultTreeModel
{
    /**
     * Simple ctor
     * @param key The root TreeNode
     */
    public KeyTreeModel(Key key)
    {
        super(new KeyTreeNode(key, null));
        this.key = key;
    }

    /**
     * What key is this tree editing
     * @return Returns the key.
     */
    public Key getKey()
    {
        return key;
    }

    /**
     * Sets the key is this tree editing
     */
    public void setKey(Key key)
    {
        this.key = key;
        setRoot(new KeyTreeNode(key, null));
    }

    /**
     * The key that this tree is displaying.
     */
    private Key key;
}
