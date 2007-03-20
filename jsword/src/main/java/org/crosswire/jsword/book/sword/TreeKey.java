package org.crosswire.jsword.book.sword;
/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: LZSSBackend.java 1143 2006-10-04 22:07:23 -0400 (Wed, 04 Oct 2006) dmsmith $
 */
import org.crosswire.jsword.passage.DefaultLeafKeyList;
import org.crosswire.jsword.passage.Key;

/**
 * A Key that knows where the data is in the real file.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
class TreeKey extends DefaultLeafKeyList
{
    /**
     * Setup with the key name and positions of data in the file
     */
    TreeKey(TreeNode node, Key parent)
    {
        super(node.getName(), node.getName(), parent);

        this.node = node;
    }

    /**
     * Setup with the key name. Use solely for searching.
     */
    TreeKey(String text)
    {
        super(text, text, null);

        this.node = null;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return node.getName();
    }

    /**
     * @param newName the offset to set
     */
    public void setName(String newName)
    {
        node.setName(newName);
    }

    /**
     * @return the offset
     */
    public int getOffset()
    {
        return node.getOffset();
    }

    /**
     * @param newOffset the offset to set
     */
    public void setOffset(int newOffset)
    {
        node.setOffset(newOffset);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    public Object clone()
    {
        return super.clone();
    }

    private TreeNode node;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = -6560408145705717977L;

}
