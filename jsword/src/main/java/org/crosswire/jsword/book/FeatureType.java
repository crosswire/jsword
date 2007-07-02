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
 * An Enumeration of the possible Features a Book may have.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class FeatureType implements Serializable
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
     * The book contains Strong's Numbers
     */
    public static final FeatureType STRONGS_NUMBERS = new FeatureType("StrongsNumbers"); //$NON-NLS-1$

    /**
     * The book contains footnotes
     */
    public static final FeatureType FOOTNOTES = new FeatureType("Footnotes"); //$NON-NLS-1$

    /**
     * The book contains Scripture cross references
     */
    public static final FeatureType SCRIPTURE_REFERENCES = new FeatureType("Scripref"); //$NON-NLS-1$

    /**
     * The book marks the Word's of Christ
     */
    public static final FeatureType WORDS_OF_CHRIST = new FeatureType("RedLetterText"); //$NON-NLS-1$

    /**
     * The book contains Morphology info
     */
    public static final FeatureType MORPHOLOGY = new FeatureType("Morph"); //$NON-NLS-1$

    /**
     * The book contains Headings
     */
    public static final FeatureType HEADINGS = new FeatureType("Headings"); //$NON-NLS-1$

    /**
     * @param name The name of the FeatureType
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
        GLOSSARY,
        STRONGS_NUMBERS,
        FOOTNOTES,
        SCRIPTURE_REFERENCES,
        WORDS_OF_CHRIST,
        MORPHOLOGY,
        HEADINGS,
    };

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3256727260177708345L;
}
