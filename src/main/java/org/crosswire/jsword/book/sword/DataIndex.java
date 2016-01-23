/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2007 - 2016
 *
 */
package org.crosswire.jsword.book.sword;

/**
 * Data files are indexed by offset and size.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class DataIndex {
    /**
     * This data index is defined by an offset into a file and the size of the
     * data to retrieve.
     * 
     * @param offset
     *            The position in the file to which to seek
     * @param size
     *            The number of bytes to read from the file.
     */
    public DataIndex(int offset, int size) {
        this.offset = offset;
        this.size = size;
    }

    /**
     * @return the offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    private int offset;
    private int size;
}
