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
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
/**
 *
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public final class BookCategory implements Serializable, Comparable
{
    /**
     * Books that are Bibles
     */
    public static final BookCategory BIBLE = new BookCategory("Bible", Msg.BIBLE); //$NON-NLS-1$

    /**
     * Books that are Dictionaries
     */
    public static final BookCategory DICTIONARY = new BookCategory("Dictionary", Msg.DICTIONARY); //$NON-NLS-1$

    /**
     * Books that are Commentaries
     */
    public static final BookCategory COMMENTARY = new BookCategory("Commentary", Msg.COMMENTARY); //$NON-NLS-1$

    /**
     * Books that are indexed by day. AKA, Daily Devotions
     */
    public static final BookCategory DAILY_DEVOTIONS = new BookCategory("Daily Devotional", Msg.READINGS); //$NON-NLS-1$

    /**
     * Books that map words from one language to another.
     */
    public static final BookCategory GLOSSARY = new BookCategory("Glossaries", Msg.GLOSSARIES); //$NON-NLS-1$

    /**
     * Books that are questionable.
     */
    public static final BookCategory QUESTIONABLE = new BookCategory("Cults / Unorthodox / Questionable Material", Msg.UNORTHODOX); //$NON-NLS-1$

    /**
     * Books that are not any of the above
     */
    public static final BookCategory GENERAL_BOOK = new BookCategory("General Books", Msg.GENERAL); //$NON-NLS-1$

    /**
     * Books that are not any of the above
     */
    public static final BookCategory OTHER = new BookCategory("Other", Msg.OTHER); //$NON-NLS-1$

    /**
     * @param name The name of the BookCategory
     */
    private BookCategory(String name, Msg externalName)
    {
        this.name = name;
        this.externalName = externalName;
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
     * Lookup method to convert from a String
     */
    public static BookCategory fromExternalString(String name)
    {
        for (int i = 0; i < VALUES.length; i++)
        {
            BookCategory o = VALUES[i];
            if (o.externalName.toString().equalsIgnoreCase(name))
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

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o)
    {
        BookCategory that = (BookCategory) o;
        return this.name.compareTo(that.name);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o)
    {
        return super.equals(o);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return super.hashCode();
    }

    /**
     * @return the internal name.
     */
    public String getName()
    {
        return name;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return externalName.toString();
    }

    /**
     * The names of the BookCategory
     */
    private String name;
    private Msg externalName;

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
        DAILY_DEVOTIONS,
        GLOSSARY,
        QUESTIONABLE,
        GENERAL_BOOK,
        OTHER,
    };

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3256727260177708345L;
}
