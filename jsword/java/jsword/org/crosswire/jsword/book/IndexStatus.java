/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
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
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class IndexStatus implements Serializable
{
    /**
     * There is a complete and ready to use search index
     */
    public static final IndexStatus DONE = new IndexStatus("Indexed"); //$NON-NLS-1$

    /**
     * There is no search index, and no plans to create one
     */
    public static final IndexStatus UNDONE = new IndexStatus("No Index");  //$NON-NLS-1$

    /**
     * This Book has been scheduled for index creation
     */
    public static final IndexStatus SCHEDULED = new IndexStatus("Scheduled"); //$NON-NLS-1$

    /**
     * An index is currently being generated for this Book
     */
    public static final IndexStatus CREATING = new IndexStatus("Creating"); //$NON-NLS-1$

    /**
     * All the known values
     */
    private static final IndexStatus[] VALUES =
    {
        DONE,
        UNDONE,
        SCHEDULED,
        CREATING,
    };

    /**
     * @param name The name of the BookType
     */
    private IndexStatus(String name)
    {
        this.name = name;
    }

    /**
     * Lookup method to convert from a String
     */
    public static IndexStatus fromString(String name)
    {
        for (int i = 0; i < VALUES.length; i++)
        {
            IndexStatus o = VALUES[i];
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
    public static IndexStatus fromInteger(int i)
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
     * The name of the BookType
     */
    private String name;

    // Support for serialization
    private static int nextObj;
    private final int obj = nextObj++;

    Object readResolve()
    {
        return VALUES[obj];
    }

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3256718472791537204L;
}
