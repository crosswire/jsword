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

/**
 * Block types indicates the grain of compression.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public enum BlockType
{
    /** The level of compression is the Book */
    BOOK ('b'),

    /** The level of compression is the Chapter */
    CHAPTER ('c'),

    /** The level of compression is the Verse */
    VERSE ('v');

    private BlockType(char indicator)
    {
        this.indicator = indicator;
    }

    /**
     * Return a character indicating the grain of compression.
     * This is used in the names of compressed sword books.
     *
     * @return the indicator
     */
    public char getIndicator()
    {
        return indicator;
    }
    
    private char indicator;
}
