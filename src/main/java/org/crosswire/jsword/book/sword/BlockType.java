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
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.jsword.book.sword;

import org.crosswire.jsword.JSOtherMsg;


/**
 * Block types indicates the grain of compression.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
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
    BlockType(String name, char indicator) {
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
     * 
     * @param name the string representation of the block type
     * @return the matching block type
     */
    public static BlockType fromString(String name) {
        for (BlockType v : values()) {
            if (v.name.equalsIgnoreCase(name)) {
                return v;
            }
        }

        throw new ClassCastException(JSOtherMsg.lookupText("BlockType {0} is not defined!", name));
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
