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
 * © CrossWire Bible Society, 2008 - 2016
 *
 */
package org.crosswire.common.options;

import org.crosswire.common.util.Convert;

/**
 * A DataType provides the ability to marshal a String value to an object.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public enum DataType {
    /**
     * A string argument.
     */
    STRING  ("String") {
        @Override
        public Object convertFromString(String value) {
            return value;
        }
    },

    /**
     * An integer argument.
     */
    INTEGER  ("Integer") {
        @Override
        public Object convertFromString(String value) {
            return Integer.valueOf(Convert.string2Int(value));
        }
    },

    /**
     * An boolean argument that allows various values for 'true'.
     */
    BOOLEAN ("Boolean") {
        @Override
        public Object convertFromString(String value) {
            return Boolean.valueOf(Convert.string2Boolean(value));
        }
    };

    /**
     * @param name
     *            The name of the DataType
     */
    DataType(String name) {
        this.name = name;
    }

    /**
     * Convert a String to an DataType's expected value.
     * @param input the string to convert
     * @return the converted value
     */
    public abstract Object convertFromString(String input);

    /**
     * Find a DataType by name
     * 
     * @param name the name of the DataType
     * @return the DataType or null
     */
    public static DataType fromString(String name) {
        for (DataType v : values()) {
            if (v.name().equalsIgnoreCase(name)) {
                return v;
            }
        }
        // cannot get here
        assert false;
        return null;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * The name of the DataType
     */
    private String name;
}
