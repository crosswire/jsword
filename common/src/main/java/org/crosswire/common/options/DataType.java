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
 * Copyright: 2008
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: org.eclipse.jdt.ui.prefs 1178 2006-11-06 12:48:02Z dmsmith $
 */

package org.crosswire.common.options;

import java.io.Serializable;

import org.crosswire.common.util.Convert;

/**
 * A DataType provides the ability to marshal a String value to an object.
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public abstract class DataType implements Serializable
{
    /**
     * A string argument.
     */
    public static final DataType STRING = new DataType("String") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.common.options.DataType#convertFromString(java.lang.String)
         */
        public Object convertFromString(String value)
        {
            return value;
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = -2521783846509171308L;
    };

    /**
     * An integer argument.
     */
    public static final DataType INTEGER = new DataType("Integer") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.common.options.DataType#convertFromString(java.lang.String)
         */
        public Object convertFromString(String value)
        {
            return new Integer(Convert.string2Int(value));
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = -2521783846509171308L;
    };
    
    /**
     * An boolean argument that allows various values for 'true'.
     */
    public static final DataType BOOLEAN = new DataType("Boolean") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.common.options.DataType#convertFromString(java.lang.String)
         */
        public Object convertFromString(String value)
        {
            return Boolean.valueOf(Convert.string2Boolean(value));
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = -2521783846509171308L;
    };
    
    /**
     * @param name The name of the DataType
     */
    protected DataType(String name)
    {
        this.name = name;
    }

    /**
     * Convert a String to an Arguments expected value.
     */
    public abstract Object convertFromString(String input);

    /**
     * Lookup method to convert from a String
     */
    public static DataType fromString(String name)
    {
        for (int i = 0; i < VALUES.length; i++)
        {
            DataType o = VALUES[i];
            if (o.name.equalsIgnoreCase(name))
            {
                return o;
            }
        }
        // cannot get here
        assert false;
        return null;
    }

    /**
     * Lookup method to convert from an integer
     */
    public static DataType fromInteger(int i)
    {
        return VALUES[i];
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return name;
    }

    /**
     * The name of the DataType
     */
    private String name;

    // Support for serialization
    private static int nextObj;
    private final int obj = nextObj++;

    Object readResolve()
    {
        return VALUES[obj];
    }

    private static final DataType[] VALUES =
    {
    };

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3256727260177708345L;

}
