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

/**
 * An Enumeration of the possible types of Book.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public enum BookCategory
{
    /** Books that are Bibles */
    BIBLE ("Bible"), //$NON-NLS-1$

    /** Books that are Dictionaries */
    DICTIONARY ("Dictionary"), //$NON-NLS-1$

    /** Books that are Commentaries */
    COMMENTARY ("Commentary"), //$NON-NLS-1$

    /** Books that are indexed by day. AKA, Daily Devotions */
    DAILY_DEVOTIONS ("Daily Devotional"), //$NON-NLS-1$

    /** Books that map words from one language to another. */
    GLOSSARY ("Glossaries"), //$NON-NLS-1$

    /** Books that are questionable. */
    QUESTIONABLE ("Cults / Unorthodox / Questionable Material"), //$NON-NLS-1$

    /**
     * Books that are not any of the above
     */
    OTHER ("Other"); //$NON-NLS-1$

    /**
     * A BookCategory has a user friendly name.
     *
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
        for (BookCategory bc : BookCategory.values())
        {
            if (bc.name.equalsIgnoreCase(name))
            {
                return bc;
            }
        }
        return OTHER;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return name;
    }

    /**
     * The name of the BookCategory
     */
    private String name;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3256727260177708345L;
}
