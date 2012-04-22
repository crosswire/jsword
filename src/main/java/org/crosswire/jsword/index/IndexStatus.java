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
package org.crosswire.jsword.index;


/**
 * An Enumeration of the possible states of an index.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public enum IndexStatus {
    /**
     * There is a complete and ready to use search index
     */
    DONE("Indexed"),

    /**
     * There is no search index, and no plans to create one
     */
    UNDONE("No Index"),

    /**
     * This Book has been scheduled for index creation
     */
    SCHEDULED("Scheduled"),

    /**
     * An index is currently being generated for this Book
     */
    CREATING("Creating"),

    /**
     * An index is no longer valid and needs to be discarded.
     */
    INVALID("Invalid");

    /**
     * @param name
     *            The name of the BookCategory
     */
    private IndexStatus(String name) {
        this.name = name;
    }

    /**
     * Lookup method to convert from a String
     */
    public static IndexStatus fromString(String name) {
        for (IndexStatus o : IndexStatus.values()) {
            if (o.name.equalsIgnoreCase(name)) {
                return o;
            }
        }
        // cannot get here
        assert false;
        return null;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * The name of the IndexStatus
     */
    private String name;

}
