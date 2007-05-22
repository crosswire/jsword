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
 * Copyright: 2007
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: FeatureType.java 1318 2007-05-06 11:36:35 -0400 (Sun, 06 May 2007) dmsmith $
 */
package org.crosswire.common.diff;

import java.io.Serializable;

/**
 * An Enumeration of the possible Edits.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public final class EditType implements Serializable
{
    /**
     * Delete a sequence.
     */
    public static final EditType DELETE = new EditType("Delete", '-'); //$NON-NLS-1$

    /**
     * Insert a sequence
     */
    public static final EditType INSERT = new EditType("Insert", '+'); //$NON-NLS-1$

    /**
     * Equal sequences
     */
    public static final EditType EQUAL = new EditType("Equal", ' '); //$NON-NLS-1$

    /**
     * @param name The name of the FeatureType
     */
    private EditType(String name, char symbol)
    {
        this.name = name;
        this.symbol = symbol;
    }

    /**
     * @return te symbol for this EditType
     */
    public char getSymbol()
    {
        return symbol;
    }

    /**
     * Lookup method to convert from a String
     */
    public static EditType fromString(String name)
    {
        for (int i = 0; i < VALUES.length; i++)
        {
            EditType o = VALUES[i];
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
     * Lookup method to convert from a String
     */
    public static EditType fromSymbol(char symbol)
    {
        for (int i = 0; i < VALUES.length; i++)
        {
            EditType o = VALUES[i];
            if (o.symbol == symbol)
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
    public static EditType fromInteger(int i)
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
     * The name of the FeatureType
     */
    private String name;

    /**
     * The symbol representing the EditType
     */
    private char symbol;

    // Support for serialization
    private static int nextObj;
    private final int obj = nextObj++;

    Object readResolve()
    {
        return VALUES[obj];
    }

    private static final EditType[] VALUES =
    {
        DELETE,
        INSERT,
        EQUAL,
    };

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3256727260177708345L;
}
