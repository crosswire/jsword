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
 * Copyright: 2007
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.common.diff;

/**
 * An Enumeration of the possible Edits.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith
 */
public enum EditType  {
    /**
     * Delete a sequence.
     */
    DELETE  ("Delete", '-'),

    /**
     * Insert a sequence
     */
    INSERT  ("Insert", '+'),

    /**
     * Equal sequences
     */
    EQUAL ("Equal", ' ');

    /**
     * @param name
     *            The name of the FeatureType
     */
    private EditType(String name, char symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    /**
     * @return te symbol for this EditType
     */
    public char getSymbol() {
        return symbol;
    }

    /**
     * Get a CompressorType from a String
     * 
     * @param name the case insensitive representation of the desired CompressorType
     * @return the desired compressor or null if not found.
     */
    public static EditType fromString(String name) {
        for (EditType v : values()) {
            if (v.name().equalsIgnoreCase(name)) {
                return v;
            }
        }

        // cannot get here
        assert false;
        return null;
    }

    /**
     * Lookup method to convert from a String
     */
    public static EditType fromSymbol(char symbol) {
        for (EditType v : values()) {
            if (v.symbol == symbol) {
                return v;
            }
        }

        // cannot get here
        assert false;
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * The name of the FeatureType
     */
    private String name;

    /**
     * The symbol representing the EditType
     */
    private char symbol;
}
