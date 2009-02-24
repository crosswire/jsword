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
 * ID: $Id: CaseType.java 1890 2008-07-09 12:15:15Z dmsmith $
 */
package org.crosswire.jsword.book;

import java.io.Serializable;

/**
 * Types of Key that a Book uses, either verse, list, or tree.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class KeyType implements Serializable
{
    /**
     * Book contains verses and can be understood as book, chapter and verse.
     */
    public static final KeyType VERSE = new KeyType("verse"); //$NON-NLS-1$

    /**
     * Book organizes its entries in a list, as in a dictionary.
     */
    public static final KeyType LIST = new KeyType("list"); //$NON-NLS-1$

    /**
     * Book organizes its entries in a tree, as in a general book.
     */
    public static final KeyType TREE = new KeyType("tree"); //$NON-NLS-1$

    /**
     * Simple ctor
     */
    public KeyType(String name)
    {
        this.name = name;
    }

    /**
     * Get an integer representation for this CaseType
     */
    public int toInteger()
    {
        for (int i = 0; i < VALUES.length; i++)
        {
            if (equals(VALUES[i]))
            {
                return i;
            }
        }
        // cannot get here
        assert false;
        return -1;
    }

    /**
     * Lookup method to convert from a String
     */
    public static KeyType fromString(String name)
    {
        for (int i = 0; i < VALUES.length; i++)
        {
            KeyType o = VALUES[i];
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
    public static KeyType fromInteger(int i)
    {
        return VALUES[i];
    }

    /**
     * Prevent subclasses from overriding canonical identity based Object methods
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public final boolean equals(Object o)
    {
        return super.equals(o);
    }

    /**
     * Prevent subclasses from overriding canonical identity based Object methods
     * @see java.lang.Object#hashCode()
     */
    public final int hashCode()
    {
        return super.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return name;
    }

    /**
     * The name of the type
     */
    private transient String name;

    // Support for serialization
    private static int nextObj;
    private final int obj = nextObj++;

    Object readResolve()
    {
        return VALUES[obj];
    }

    private static final KeyType[] VALUES =
    {
        VERSE,
        LIST,
        TREE,
    };

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 8856576924393105712L;

}
