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
package org.crosswire.jsword.book.sword;

import java.util.regex.Pattern;

import org.crosswire.common.util.Language;
import org.crosswire.common.util.Version;
import org.crosswire.jsword.book.BookCategory;

/**
 * Constants for the keys in a Sword Config file. Taken from
 * http://sword.sourceforge.net/cgi-bin/twiki/view/Swordapi/ConfFileLayout<br/>
 * now located at
 * http://www.crosswire.org/ucgi-bin/twiki/view/Swordapi/ConfFileLayout<br/>
 * now located at http://www.crosswire.org/wiki/index.php/DevTools:Modules<br/>
 * now located at http://www.crosswire.org/wiki/DevTools:confFiles<br/>
 * <p>
 * Note: This file is organized the same as the latest wiki documentation.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public enum ConfigEntryType {
    /**
     * The abbreviated name by which this book is known. This is in the [] on
     * the first non-blank line of the conf. JSword uses this for display and
     * access purposes.
     */
    INITIALS("Initials") {
        @Override
        public boolean isSynthetic() {
            return true;
        }
    },

    /**
     * Relative path to the data files, some issues with this
     */
    DATA_PATH("DataPath") {
        @Override
        public boolean isAllowed(String value) {
            return true;
        }
    },

    /**
     * The full name of this book
     */
    DESCRIPTION("Description"),

    /**
     * This indicates how the book was stored.
     */
    MOD_DRV("ModDrv", -1,
        "RawText",
        "zText",
        "RawCom",
        "RawCom4",
        "zCom",
        "HREFCom",
        "RawFiles",
        "RawLD",
        "RawLD4",
        "zLD",
        "RawGenBook"
    ),

    /**
     * The type of compression in use. JSword does not support LZSS. While it is
     * the default, it is not used. At least so far.
     */
    COMPRESS_TYPE("CompressType", 0,
        "LZSS",
        "ZIP"
    ),

    /**
     * The level at which compression is applied, BOOK, CHAPTER, or VERSE
     */
    BLOCK_TYPE("BlockType", 1,
        "BOOK",
        "CHAPTER",
        "VERSE"
    ),

    /**
     * single value integer, unknown use, some indications that we ought to be
     * using it
     */
    BLOCK_COUNT("BlockCount", "200") {
        @Override
        public boolean isAllowed(String aValue) {
            try {
                Integer.parseInt(aValue);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        @Override
        public Object convert(String input) {
            try {
                return Integer.valueOf(input);
            } catch (NumberFormatException e) {
                return getDefault();
            }
        }
    },

    /**
     * The kind of key that a Generic Book uses.
     */
    KEY_TYPE("KeyType", 0,
        "TreeKey",
        "VerseKey"
    ),

    /**
     * If this exists in the conf, then the book is encrypted. The value is used
     * to unlock the book. The encryption algorithm is Sapphire.
     */
    CIPHER_KEY("CipherKey"),

    /**
     * This indicates the versification of the book, with KJV being the default.
     */
    VERSIFICATION("Versification", 3,
        "Catholic",
        "Catholic2",
        "German",
        "KJV",
        "KJVA",
        "Leningrad",
        "Luther",
        "MT",
        "NRSV",
        "NRSVA",
        "Synodal",
        "SynodalP",
        "Vulg"
    ),

    /**
     * Global Option Filters are the names of routines in Sword that can be used
     * to display the data. These are not used by JSword.
     */
    GLOBAL_OPTION_FILTER("GlobalOptionFilter", -1,
        "GBFStrongs",
        "GBFFootnotes",
        "GBFMorph",
        "GBFHeadings",
        "GBFRedLetterWords",
        "GBFScripref",
        "ThMLStrongs",
        "ThMLFootnotes",
        "ThMLScripref",
        "ThMLMorph",
        "ThMLHeadings",
        "ThMLVariants",
        "ThMLLemma",
        "UTF8Cantillation",
        "UTF8GreekAccents",
        "UTF8HebrewPoints",
        "OSISStrongs",
        "OSISFootnotes",
        "OSISScripref",
        "OSISMorph",
        "OSISHeadings",
        "OSISVariants",
        "OSISRedLetterWords",
        "OSISLemma",
        "OSISRuby"
    ) {
        @Override
        public boolean mayRepeat() {
            return true;
        }
    },

    /**
     * The layout direction of the text in the book. Hebrew, Arabic and Farsi
     * RtoL. Most are 'LtoR'. Some are 'bidi', bi-directional. E.g.
     * hebrew-english glossary.
     */
    DIRECTION("Direction", 0,
        "LtoR",
        "RtoL",
        "bidi"
    ),

    /**
     * This indicates the kind of markup used for the book.
     */
    SOURCE_TYPE("SourceType", 0,
        "Plaintext",
        "GBF",
        "ThML",
        "OSIS",
        "TEI",
        "OSIS",
        "TEI"
    ),

    /**
     * The character encoding. Only Latin-1 and UTF-8 are supported.
     */
    ENCODING("Encoding", 0,
        "Latin-1",
        "UTF-8"
    ),

    /**
     * Display level is used by GenBooks to do auto expansion in the tree. A
     * level of 2 indicates that the first two levels should be shown.
     */
    DISPLAY_LEVEL("DisplayLevel", "1") {
        @Override
        public boolean isAllowed(String value) {
            try {
                Integer.parseInt(value);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        @Override
        public Object convert(String input) {
            try {
                return Integer.valueOf(input);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    },

    /**
     * A recommended font to use for the book.
     */
    FONT("Font"),

    /**
     * When false do not show quotation marks for OSIS text that has <q>
     * elements.
     */
    OSIS_Q_TO_TICK("OSISqToTick", 0,
        "true",
        "false"
    ) {
        @Override
        public Object convert(String input) {
            return Boolean.valueOf(input);
        }
    },

    /**
     * A Feature describes a characteristic of the Book.
     */
    FEATURE("Feature", -1,
        "StrongsNumbers",
        "GreekDef",
        "HebrewDef",
        "GreekParse",
        "HebrewParse",
        "DailyDevotion",
        "Glossary",
        "Images"
    ) {
        @Override
        public boolean mayRepeat() {
            return true;
        }
    },

    /**
     * Books with a Feature of Glossary are used to map words FROM one language
     * TO another.
     */
    GLOSSARY_FROM("GlossaryFrom") {
        @Override
        public Object convert(String input) {
            return new Language(input);
        }

        @Override
        public String unconvert(Object internal) {
            if (internal instanceof Language) {
                return ((Language) internal).getCode();
            }
            return super.unconvert(internal);
        }
    },

    /**
     * Books with a Feature of Glossary are used to map words FROM one language
     * TO another.
     */
    GLOSSARY_TO("GlossaryTo") {
        @Override
        public Object convert(String input) {
            return new Language(input);
        }

        @Override
        public String unconvert(Object internal) {
            if (internal instanceof Language) {
                return ((Language) internal).getCode();
            }
            return super.unconvert(internal);
        }
    },

    /**
     * The short name of this book.
     */
    ABBREVIATION("Abbreviation"),

    /**
     * Contains rtf that describes the book.
     */
    ABOUT("About") {
        @Override
        public boolean allowsContinuation() {
            return true;
        }

        @Override
        public boolean allowsRTF() {
            return true;
        }
    },

    /**
     * An informational string indicating the current version of the book.
     */
    VERSION("Version", "1.0") {
        @Override
        public boolean isAllowed(String aValue) {
            try {
                new Version(aValue);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        @Override
        public Object convert(String input) {
            try {
                return new Version(input);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    },

    /**
     * multiple values starting with History, some sort of change-log. In the
     * conf these are of the form History_x.y. We strip off the x.y and prefix
     * the value with it. The x.y corresponds to a current or prior Version
     * value.
     */
    HISTORY("History") {
        @Override
        public boolean mayRepeat() {
            return true;
        }

        @Override
        public boolean reportDetails() {
            return false;
        }
    },

    /**
     * single value version number, lowest sword c++ version that can read this
     * book JSword does not use this value.
     */
    MINIMUM_VERSION("MinimumVersion", "1.5.1a"),

    /**
     * The Category of the book. Used on the web to classify books into a tree.
     */
    CATEGORY("Category", 0,
        "Other",
        "Daily Devotional",
        "Glossaries",
        "Cults / Unorthodox / Questionable Material",
        "Essays",
        "Maps",
        "Images",
        "Biblical Texts",
        "Commentaries",
        "Lexicons / Dictionaries",
        "Generic Books"
    ) {
        @Override
        public Object convert(String input) {
            return BookCategory.fromString(input);
        }
    },

    /**
     * Library of Congress Subject Heading. Typically this is of the form
     * BookCategory Scope Language, where scope is typically O.T., N.T.
     */
    LCSH("LCSH"),

    /**
     * single value string, defaults to en, the language of the book
     */
    LANG("Lang", "en") {
        @Override
        public Object convert(String input) {
            return new Language(input);
        }

        @Override
        public String unconvert(Object internal) {
            if (internal instanceof Language) {
                return ((Language) internal).getCode();
            }
            return super.unconvert(internal);
        }
    },

    /**
     * The installed size of the book in bytes. This is not the size of the zip
     * that is downloaded.
     */
    INSTALL_SIZE("InstallSize") {
        @Override
        public boolean isAllowed(String value) {
            try {
                Integer.parseInt(value);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        @Override
        public Object convert(String input) {
            try {
                return Integer.valueOf(input);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    },

    /**
     * The date that this version of the book was last updated. Informational
     * only.
     */
    SWORD_VERSION_DATE("SwordVersionDate") {
        @Override
        public boolean isAllowed(String value) {
            return validDatePattern.matcher(value).matches();
        }

        private Pattern validDatePattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    },

    /**
     * A list of prior "initials" for the current book.
     * TODO(dms): when a user installs a book with an obsoletes that matches
     * an installed book, offer the user the opportunity to delete the old book.
     */
    OBSOLETES("Obsoletes") {
        @Override
        public boolean mayRepeat() {
            return true;
        }

        @Override
        public boolean reportDetails() {
            return false;
        }
    },

    /**
     * Informational copyright notice.
     */
    COPYRIGHT("Copyright") {
        @Override
        public boolean allowsContinuation() {
            return true;
        }
    },

    /**
     * single value string, unknown use
     */
    COPYRIGHT_HOLDER("CopyrightHolder"),

    /**
     * Copyright info. Informational only.
     * This is a year, a year range or a comma separated list of these.
     */
    COPYRIGHT_DATE("CopyrightDate") {
        @Override
        public boolean isAllowed(String value) {
            return validDatePattern.matcher(value).matches();
        }

        private Pattern validDatePattern = Pattern.compile("\\d{4}(\\s*-\\s*\\d{4})?(\\s*,\\s*\\d{4}(\\s*-\\s*\\d{4})?)*");
    },

    /**
     * Copyright info. Informational only.
     */
    COPYRIGHT_NOTES("CopyrightNotes") {
        @Override
        public boolean allowsContinuation() {
            return true;
        }

        @Override
        public boolean allowsRTF() {
            return true;
        }
    },

    /**
     * Copyright info. Informational only.
     */
    COPYRIGHT_CONTACT_NAME("CopyrightContactName") {
        @Override
        public boolean allowsContinuation() {
            return true;
        }

        @Override
        public boolean allowsRTF() {
            return true;
        }
    },

    /**
     * Copyright info. Informational only.
     */
    COPYRIGHT_CONTACT_NOTES("CopyrightContactNotes") {

        @Override
        public boolean allowsContinuation() {
            return true;
        }

        @Override
        public boolean allowsRTF() {
            return true;
        }
    },

    /**
     * Copyright info. Informational only.
     */
    COPYRIGHT_CONTACT_ADDRESS("CopyrightContactAddress") {
        @Override
        public boolean allowsContinuation() {
            return true;
        }

        @Override
        public boolean allowsRTF() {
            return true;
        }
    },

    /**
     * Copyright info. Informational only.
     */
    COPYRIGHT_CONTACT_EMAIL("CopyrightContactEmail"),

    /**
     * A one line promo statement, required by Lockman for NASB
     */
    SHORT_PROMO("ShortPromo"),

    /**
     * A one line copyright statement, required by Lockman for NASB
     */
    SHORT_COPYRIGHT("ShortCopyright"),

    /**
     * Copyright info. Informational only.
     */
    DISTRIBUTION_LICENSE("DistributionLicense", 0,
        "Public Domain",
        "Copyrighted",
        "Copyrighted; Free non-commercial distribution",
        "Copyrighted; Permission to distribute granted to CrossWire",
        "Copyrighted; Freely distributable",
        "Copyrighted; Permission granted to distribute non-commercially in Sword format",
        "GFDL",
        "GPL",
        "Creative Commons: by-nc-nd",
        "Creative Commons: by-nc-sa",
        "Creative Commons: by-nc",
        "Creative Commons: by-nd",
        "Creative Commons: by-sa",
        "Creative Commons: by"
    ),

    /**
     * Copyright info. Informational only.
     */
    DISTRIBUTION_NOTES("DistributionNotes") {

        @Override
        public boolean allowsContinuation() {
            return true;
        }
    },

    /**
     * Information on where the book's text was obtained.
     */
    TEXT_SOURCE("TextSource") {

        @Override
        public boolean allowsContinuation() {
            return true;
        }
    },
    /**
     * Contains the URL (a bare URL, not an HTML <a> link) of a web page for unlocking instructions/payment.
     */
    UNLOCK_URL("UnlockURL"),
    /**
     * Similar to DataPath. It gives where on the CrossWire server the book can
     * be found. Informational only.
     */
    DISTRIBUTION_SOURCE("DistributionSource") {

        @Override
        public boolean allowsContinuation() {
            return true;
        }
    },

    /**
     * Single value version number, lowest sword c++ version that can read this
     * book JSword does not use this value.
     */
    OSIS_VERSION("OSISVersion", "2.0"),

    /**
     * The location of a collection of modules. JSword uses this to install and
     * delete a module.
     */
    LIBRARY_URL("LibraryURL") {
        @Override
        public boolean isSynthetic() {
            return true;
        }
    },

    /**
     * The location of the module. JSword uses this to access a module.
     */
    LOCATION_URL("LocationURL") {
        @Override
        public boolean isSynthetic() {
            return true;
        }
    };

    /**
     * Simple ctor
     */
    private ConfigEntryType(String name) {
        this.name = name;
        this.defaultValue = null;
        this.picks = null;
    }

    /**
     * Simple ctor
     */
    private ConfigEntryType(String name, String defaultValue) {
        this.name = name;
        this.defaultValue = convert(defaultValue);
        this.picks = null;
    }

    /**
     * Simple ctor
     */
    private ConfigEntryType(String name, int defaultPick, String... picks) {
        this.name = name;
        if (defaultPick >= 0 && defaultPick < picks.length) {
            this.defaultValue = convert(picks[defaultPick]);
        } else {
            this.defaultValue = null;
        }
        this.picks = picks;
    }

    /**
     * Returns the normalized name of this ConfigEntry.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Determines whether the string is allowed. For some config entries, the
     * value is expected to be one of a group, for others the format is defined.
     * 
     * @param value
     *            the string to be checked
     * @return true if the string is allowed
     */
    public boolean isAllowed(String value) {
        if (hasChoices()) {
            for (String pick : picks) {
                if (pick.equalsIgnoreCase(value)) {
                    return true;
                }
            }

            return false;
        }
        return value != null;
    }

    /**
     * Modify the value if necessary.
     * 
     * @param value
     *            the input
     * @return either value or a modified version of it.
     */
    public String filter(String value) {
        // Look through the choice array, if present, for matches.
        if (hasChoices()) {
            // Do we have an exact match?
            for (String pick : picks) {
                if (pick.equals(value)) {
                    return value;
                }
            }

            // Do we have a case insensitive match?
            for (String pick : picks) {
                if (pick.equalsIgnoreCase(value)) {
                    return pick;
                }
            }
        }

        return value;
    }

    /**
     * RTF is allowed in a few config entries.
     * 
     * @return true if RTF is allowed
     */
    public boolean allowsRTF() {
        return false;
    }

    /**
     * While most fields are single line or single value, some allow
     * continuation. A continuation mark is a backslash at the end of a line. It
     * is not to be followed by whitespace.
     * 
     * @return true if continuation is allowed
     */
    public boolean allowsContinuation() {
        return false;
    }

    /**
     * Some keys can repeat. When this happens each is a single value pick from
     * a list of choices.
     * 
     * @return true if this ConfigEntryType can occur more than once
     */
    public boolean mayRepeat() {
        return false;
    }

    /**
     * Determines the level of detail stored in the histogram.
     * 
     * @return true if the ConfigEntry should report histogram for repetitions
     */
    public boolean reportDetails() {
        return true;
    }

    /**
     * Some keys can repeat. When this happens each is a single value pick from
     * a list of choices.
     * 
     * @return true if this ConfigEntryType can occur more than once
     */
    protected boolean hasChoices() {
        return picks != null;
    }

    /**
     * Synthetic keys are those that are not in the Sword Book's conf, but are
     * needed by the program. Typically, these are derived by the program from
     * the other entries.
     * 
     * @return true if this is synthetic
     */
    public boolean isSynthetic() {
        return false;
    }

    /**
     * Some ConfigEntryTypes have defaults.
     * 
     * @return the default, if there is one, null otherwise
     */
    public Object getDefault() {
        return defaultValue;
    }

    /**
     * Convert the string value from the conf into the representation of this
     * ConfigEntryType.
     * 
     * @return the converted object
     */
    public Object convert(String input) {
        return input;
    }

    /**
     * Return the original representation of the object.
     * 
     * @return the converted object
     */
    public String unconvert(Object internal) {
        if (internal == null) {
            return null;
        }
        return internal.toString();
    }

    /**
     * Lookup method to convert from a String
     */
    public static ConfigEntryType fromString(String name) {
        if (name != null) {
            // special case
            if (name.startsWith(ConfigEntryType.HISTORY.toString())) {
                return ConfigEntryType.HISTORY;
            }

            for (ConfigEntryType o : ConfigEntryType.values()) {
                if (name.equals(o.name)) {
                    return o;
                }
            }
        }

        // should not get here.
        // But there are typos in the keys in the book conf files
        // And this allows for the addition of new fields in
        // advance of changing JSword
        return null;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * The name of the ConfigEntryType
     */
    private final String name;

    /**
     * The default for the ConfigEntryType
     */
    private final Object defaultValue;

    /**
     * The array of choices.
     */
    private final String[] picks;

    /**
     * Constants for direction
     */
    public static final String DIRECTION_LTOR = "LtoR";
    public static final String DIRECTION_RTOL = "RtoL";
    public static final String DIRECTION_BIDI = "bidi";

}
