package org.crosswire.jsword.book;

import java.io.Serializable;

/**
 * An Enumeration of the possible Features a Book may have.
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class FeatureType implements Serializable
{
    /**
     * The book is one of Greek Definitions. AKA, Strongs.
     */
    public static final FeatureType GREEK_DEFINITIONS = new FeatureType("GreekDef"); //$NON-NLS-1$

    /**
     * The book is one of Greek word parsings. AKA, Robinson.
     */
    public static final FeatureType GREEK_PARSE = new FeatureType("GreekParse"); //$NON-NLS-1$

    /**
     * The book is one of Hebrew Definitions. AKA, Strongs.
     */
    public static final FeatureType HEBREW_DEFINITIONS = new FeatureType("HebrewDef"); //$NON-NLS-1$

    /**
     * The book is one of Hebrew word parsings. AKA, ???.
     */
    public static final FeatureType HEBREW_PARSE = new FeatureType("HebrewParse"); //$NON-NLS-1$


    /**
     * The book is one of Daily Devotions. 
     */
    public static final FeatureType DAILY_DEVOTIONS = new FeatureType("DailyDevotions"); //$NON-NLS-1$

    /**
     * The book is glossary of translations from one language to another. 
     */
    public static final FeatureType GLOSSARY = new FeatureType("Glossary"); //$NON-NLS-1$

    /**
     * @param name The name of the BookType
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
        for (int i = 0; i < VALUES.length; i++)
        {
            FeatureType o = VALUES[i];
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
    public static FeatureType fromInteger(int i)
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

    private static final FeatureType[] VALUES =
    {
        GREEK_DEFINITIONS,
        GREEK_PARSE,
        HEBREW_DEFINITIONS,
        HEBREW_PARSE,
        DAILY_DEVOTIONS,
        GLOSSARY
    };

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3256727260177708345L;
}
