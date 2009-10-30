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

/**
 * An ArgumentType indicates whether and/or how an Option is followed by an
 * argument.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class ArgumentType implements Serializable {
    /**
     * The option is not followed by an argument.
     */
    public static final ArgumentType NO_ARGUMENT = new ArgumentType("NO"); //$NON-NLS-1$

    /**
     * The option is followed by an argument.
     */
    public static final ArgumentType REQUIRED_ARGUMENT = new ArgumentType("Required"); //$NON-NLS-1$

    /**
     * The option may be followed by an argument.
     */
    public static final ArgumentType OPTIONAL_ARGUMENT = new ArgumentType("Optional"); //$NON-NLS-1$

    /**
     * @param name
     *            The name of the DataType
     */
    protected ArgumentType(String name) {
        this.name = name;
    }

    /**
     * Lookup method to convert from a String
     */
    public static ArgumentType fromString(String name) {
        for (int i = 0; i < VALUES.length; i++) {
            ArgumentType o = VALUES[i];
            if (o.name.equalsIgnoreCase(name)) {
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
    public static ArgumentType fromInteger(int i) {
        return VALUES[i];
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return name;
    }

    /**
     * The name of the DataType
     */
    private String name;

    // Support for serialization
    private static int nextObj;
    private final int obj = nextObj++;

    Object readResolve() {
        return VALUES[obj];
    }

    private static final ArgumentType[] VALUES = {
            NO_ARGUMENT, REQUIRED_ARGUMENT, OPTIONAL_ARGUMENT
    };

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3256727260177708345L;

}
