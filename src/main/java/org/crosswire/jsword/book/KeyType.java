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
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.book;


/**
 * Types of Key that a Book uses, either verse, list, or tree.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith
 */
public enum KeyType {
    /**
     * Book contains verses and can be understood as book, chapter and verse.
     */
    VERSE,

    /**
     * Book organizes its entries in a list, as in a dictionary.
     */
    LIST,

    /**
     * Book organizes its entries in a tree, as in a general book.
     */
    TREE;

    /**
     * Get an integer representation for this KeyType
     * 
     * @return the ordinal of a KeyType
     */
    public int toInteger() {
        return ordinal();
    }

    /**
     * Lookup method to convert from a String
     * 
     * @param name the name of a KeyType
     * @return the matching KeyType
     */
    public static KeyType fromString(String name) {
        for (KeyType v : values()) {
            if (v.name().equalsIgnoreCase(name)) {
                return v;
            }
        }

        // cannot get here
        assert false;
        return null;
    }

}
