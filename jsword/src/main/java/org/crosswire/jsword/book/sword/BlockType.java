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
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public enum BlockType {
    /**
     * The level of compression is the Book
     */
    BLOCK_BOOK ("BOOK", 'b'),

    /**
     * The level of compression is the Book
     */
    BLOCK_CHAPTER ("CHAPTER", 'c'),

    /**
     * The level of compression is the Book
     */
    BLOCK_VERSE ("VERSE", 'v');

    /**
     * Simple ctor
     */
    private BlockType(String name, char indicator) {
        this.name = name;
        this.indicator = indicator;
    }

    /**
     * Return a character indicating the grain of compression. This is used in
     * the names of compressed sword books.
     * 
     * @return the indicator
     */
    public char getIndicator() {
        return indicator;
    }

    /**
     * Lookup method to convert from a String
     */
    public static BlockType fromString(String name) {
        for (BlockType v : values()) {
            if (v.name.equalsIgnoreCase(name)) {
                return v;
            }
        }

        throw new ClassCastException(Msg.UNDEFINED_DATATYPE.toString(name));
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * The name of the BlockType
     */
    private String name;
    /**
     * The indicator for the BlockType
     */
    private char indicator;
}
