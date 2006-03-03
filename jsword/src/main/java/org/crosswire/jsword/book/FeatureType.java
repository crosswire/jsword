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
 * An Enumeration of the possible Features a Book may have.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public enum FeatureType
{
    /** The book is one of Greek Definitions. AKA, Strongs. */
    GREEK_DEFINITIONS ("GreekDef"), //$NON-NLS-1$

    /** The book is one of Greek word parsings. AKA, Robinson. */
    GREEK_PARSE ("GreekParse"), //$NON-NLS-1$

    /** The book is one of Hebrew Definitions. AKA, Strongs. */
    HEBREW_DEFINITIONS ("HebrewDef"), //$NON-NLS-1$

    /** The book is one of Hebrew word parsings. AKA, ???. */
    HEBREW_PARSE ("HebrewParse"), //$NON-NLS-1$

    /** The book is one of Daily Devotions. */
    DAILY_DEVOTIONS ("DailyDevotions"), //$NON-NLS-1$

    /** The book is glossary of translations from one language to another. */
    GLOSSARY ("Glossary"); //$NON-NLS-1$

    /**
     * @param name The name of the BookCategory
     */
    private FeatureType(String name)
    {
        this.name = name;
    }

    /**
     * Lookup method to convert from a String
     */
    public static FeatureType fromString(String name)
    {
        for (FeatureType t : FeatureType.values())
        {
            if (t.name.equalsIgnoreCase(name))
            {
                return t;
            }
        }
        // cannot get here
        assert false;
        return null;
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

}
