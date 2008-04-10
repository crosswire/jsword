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
 * ID: $Id$
 */
package org.crosswire.jsword.book.sword;

import org.crosswire.jsword.passage.DefaultLeafKeyList;
import org.crosswire.jsword.passage.Key;

/**
 * A Key that knows where the data is in the real file.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
class IndexKey extends DefaultLeafKeyList
{
    /**
     * Setup with the key name and positions of data in the file
     */
    IndexKey(String text, DataIndex position, Key parent)
    {
        super(text, text, parent);

        this.position = position;
    }

    /**
     * Setup with the key name. Use solely for searching.
     */
    IndexKey(String text)
    {
        this(text, NULL_INDEX, null);
    }

    /**
     * @return
     */
    public DataIndex getDataIndex()
    {
        return position;
    }

    /**
     * @return the offset
     */
    public int getOffset()
    {
        return position.getOffset();
    }

    /**
     * @param newOffset the offset to set
     */
    public void setIndex(DataIndex newPosition)
    {
        position = newPosition;
    }

    /**
     * @return the size
     */
    public int getSize()
    {
        return position.getSize();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    public Object clone()
    {
        return super.clone();
    }

    /**
     * The position of the data in the data file.
     */
    private DataIndex position;

    /**
     * A marker used for search.
     */
    private static final DataIndex NULL_INDEX = new DataIndex(-1, -1);

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = -2472601787934480762L;
}
