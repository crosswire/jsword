/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.jsword.book;



/**
 * An Enumeration of the possible Features a Book may have.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public enum FeatureType {
    /**
     * The book is one of Greek Definitions. AKA, Strong's.
     */
    GREEK_DEFINITIONS ("GreekDef"),

    /**
     * The book is one of Greek word parsings. AKA, Robinson.
     */
    GREEK_PARSE ("GreekParse"),

    /**
     * The book is one of Hebrew Definitions. AKA, Strong's.
     */
    HEBREW_DEFINITIONS ("HebrewDef"),

    /**
     * The book is one of Hebrew word parsings. AKA, ???.
     */
    HEBREW_PARSE ("HebrewParse"),

    /**
     * The book is one of Daily Devotions.
     */
    DAILY_DEVOTIONS ("DailyDevotions"),

    /**
     * The book is glossary of translations from one language to another.
     */
    GLOSSARY ("Glossary"),

    /**
     * The book contains Strong's Numbers.
     * The alias is used to match GlobalOptionFilters.
     */
    STRONGS_NUMBERS ("StrongsNumbers", "Strongs"),

    /**
     * The book contains footnotes
     */
    FOOTNOTES ("Footnotes"),

    /**
     * The book contains Scripture cross references
     */
    SCRIPTURE_REFERENCES ("Scripref"),

    /**
     * The book marks the Word's of Christ
     */
    WORDS_OF_CHRIST ("RedLetterWords"),

    /**
     * The book contains Morphology info
     */
    MORPHOLOGY ("Morph"),

    /**
     * The book contains Headings
     */
    HEADINGS ("Headings");

    /**
     * @param name
     *            The name of the FeatureType
     */
    FeatureType(String name) {
        this(name, "");
    }

    /**
     * @param name
     *            The name of the FeatureType
     * @param alias
     *            The alias of the FeatureType
     */
    FeatureType(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }

    /**
     * Lookup method to convert from a String
     * 
     * @param name the name of a FeatureType
     * @return the matching FeatureType
     */
    public static FeatureType fromString(String name) {
        for (FeatureType v : values()) {
            if (v.name.equalsIgnoreCase(name)) {
                return v;
            }
        }

        // cannot get here
        assert false;
        return null;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Get the alias for this feature.
     * 
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * The name of the FeatureType
     */
    private String name;

    /**
     * The alias of the FeatureType
     */
    private String alias;
}
