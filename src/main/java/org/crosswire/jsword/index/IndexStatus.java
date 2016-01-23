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
package org.crosswire.jsword.index;


/**
 * An Enumeration of the possible states of an index.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
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
    IndexStatus(String name) {
        this.name = name;
    }

    /**
     * Lookup method to convert from a String
     * 
     * @param name 
     * @return the matching index status
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
