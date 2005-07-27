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
package org.crosswire.jsword.book;

import java.io.Serializable;

/**
 * An Enumeration of the possible types of Book.
 * 
 * <p>NOTE(joe): consider giving each a number (1,2,4,8) and allowing combinations
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class BookCategory implements Serializable
{
    /**
     * Books that are Bibles
     */
    public static final BookCategory BIBLE = new BookCategory("Bible"); //$NON-NLS-1$

    /**
     * Books that are Dictionaries
     */
    public static final BookCategory DICTIONARY = new BookCategory("Dictionary"); //$NON-NLS-1$

    /**
     * Books that are Commentaries
     */
    public static final BookCategory COMMENTARY = new BookCategory("Commentary"); //$NON-NLS-1$

    /**
     * Books that are not any of the above
     */
    public static final BookCategory OTHER = new BookCategory("Other"); //$NON-NLS-1$

    /**
     * @param name The name of the BookCategory
     */
    private BookCategory(String name)
    {
        this.name = name;
    }

    /**
     * Lookup method to convert from a String
     */
    public static BookCategory fromString(String name)
    {
        for (int i = 0; i < VALUES.length; i++)
        {
            BookCategory o = VALUES[i];
            if (o.name.equalsIgnoreCase(name))
            {
                return o;
            }
        }
        return OTHER;
    }

    /**
     * Lookup method to convert from an integer
     */
    public static BookCategory fromInteger(int i)
    {
        return VALUES[i];
    }

    /**
     * Prevent subclasses from overriding canonical identity based Object methods
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o)
    {
        return super.equals(o);
    }

    /**
     * Prevent subclasses from overriding canonical identity based Object methods
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
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
     * The name of the BookCategory
     */
    private String name;

    // Support for serialization
    private static int nextObj;
    private final int obj = nextObj++;

    Object readResolve()
    {
        return VALUES[obj];
    }

    private static final BookCategory[] VALUES =
    {
        BIBLE,
        DICTIONARY,
        COMMENTARY,
        OTHER,
    };

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3256727260177708345L;
}
