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
package org.crosswire.jsword.book.sword;

import java.util.regex.Pattern;

import org.crosswire.common.util.Language;
import org.crosswire.common.util.Version;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookMetaData;

/**
 * Enumeration of SWORD config file keys and their characteristics.
 * The purpose of this enumeration is to allow validation of a SWORD config file.
 * <pre>
 * Originally from:
 *     http://sword.sourceforge.net/cgi-bin/twiki/view/Swordapi/ConfFileLayout
 * Then located at:
 *     http://www.crosswire.org/ucgi-bin/twiki/view/Swordapi/ConfFileLayout
 * Then located at:
 *     http://www.crosswire.org/wiki/index.php/DevTools:Modules
 * Now located at:
 *     http://www.crosswire.org/wiki/DevTools:confFiles
 * </pre>
 * <p>
 * Note: This file is organized the same as the latest wiki documentation.
 * </p>
 * Key characteristics:
 * <ul>
 * <li><b>Data Type</b> -- Most values are text, but some are integer, date, boolean.</li>
 * <li><b>Data Format</b> -- Some text is constrained to patterns.</li>
 * <li><b>Multiple Values</b> -- Some keys allow more than one value. Others only allow only one</li>
 * <li><b>RTF</b> -- Some keys allow RTF formatting of values.</li>
 * <li><b>HTML</b> -- Some keys allow HTML.</li>
 * <li><b>Pick list</b> -- Some keys allow a value from a pick list</li>
 * <li><b>Default values</b> -- Some keys have default values.</li>
 * <li><b>Localization</b> -- Some keys can be internationalized and localized.</li>
 * </ul>
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
 */
/* protected */ enum ConfigEntryType {
    /**
     * Relative path to the data files, some issues with this
     */
    DATA_PATH(SwordBookMetaData.KEY_DATA_PATH) {
        @Override
        public boolean isAllowed(String value) {
            return true;
        }
    },

    /**
     * The full name of this book
     */
    DESCRIPTION(SwordBookMetaData.KEY_DESCRIPTION),

    /**
     * This indicates how the book was stored.
     */
    MOD_DRV(SwordBookMetaData.KEY_MOD_DRV,
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
     * The type of compression in use. While LZSS is the default, it is not used.
     * At least so far. GZIP, BZIP2 and XZ were just added by SWORD.
     */
    COMPRESS_TYPE(SwordBookMetaData.KEY_COMPRESS_TYPE,
        "LZSS",
        "ZIP",
        "GZIP",
        "BZIP2",
        "XZ"
    ),

    /**
     * The level at which compression is applied, BOOK, CHAPTER, or VERSE
     */
    BLOCK_TYPE(SwordBookMetaData.KEY_BLOCK_TYPE,
        "BOOK",
        "CHAPTER",
        "VERSE"
    ),

    /**
     * single value integer, unknown use, some indications that we ought to be
     * using it
     */
    BLOCK_COUNT(SwordBookMetaData.KEY_BLOCK_COUNT) {
        @Override
        public boolean isText() {
            return false;
        }

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
    KEY_TYPE(SwordBookMetaData.KEY_KEY_TYPE,
        "TreeKey",
        "VerseKey"
    ),

    /**
     * The kind of key that a Generic Book uses.
     */
    CASE_SENSITIVE_KEYS(SwordBookMetaData.KEY_CASE_SENSITIVE_KEYS,
        "true",
        "false"
    )
    {
        @Override
        public boolean isText() {
            return false;
        }

        @Override
        public Object convert(String input) {
            return Boolean.valueOf(input);
        }
    },

    /**
     * If this exists in the conf, then the book is encrypted. The value is used
     * to unlock the book. The encryption algorithm is Sapphire.
     */
    CIPHER_KEY(SwordBookMetaData.KEY_CIPHER_KEY),

    /**
     * This indicates the versification of the book, with KJV being the default.
     */
    VERSIFICATION(BookMetaData.KEY_VERSIFICATION,
        "Catholic",
        "Catholic2",
        "German",
        "KJV",
        "KJVA",
        "LXX",
        "Leningrad",
        "Luther",
        "MT",
        "NRSV",
        "NRSVA",
        "Orthodox",
        "Synodal",
        "SynodalProt",
        "Vulg"
    ),

    /**
     * Global Option Filters are the names of routines in SWORD that can be used
     * to display the data. These are not used by JSword.
     */
    GLOBAL_OPTION_FILTER(SwordBookMetaData.KEY_GLOBAL_OPTION_FILTER,
        "GBFStrongs",
        "GBFFootnotes",
        "GBFMorph",
        "GBFHeadings",
        "GBFRedLetterWords",
        "GBFScripref", // no longer in wiki
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
        "UTF8ArabicPoints",
        "OSISLemma",
        "OSISMorphSegmentation",
        "OSISStrongs",
        "OSISFootnotes",
        "OSISScripref",
        "OSISMorph",
        "OSISHeadings",
        "OSISVariants",
        "OSISRedLetterWords",
        "OSISGlosses",
        "OSISRuby", // Deprecated, replaced by OSISGlosses
        "OSISXlit",
        "OSISEnum",
        "OSISReferenceLinks|*",
        "OSISDictionary" // Deprecated, no replacement
    )
    {
        @Override
        public boolean mayRepeat() {
            return true;
        }
    },

    /**
     * SiglumN defines the n-th label for an OSISGlosses. Used for variant readings
     */
    SIGLUM1(SwordBookMetaData.KEY_SIGLUM1),
    SIGLUM2(SwordBookMetaData.KEY_SIGLUM2),
    SIGLUM3(SwordBookMetaData.KEY_SIGLUM3),
    SIGLUM4(SwordBookMetaData.KEY_SIGLUM4),
    SIGLUM5(SwordBookMetaData.KEY_SIGLUM5),
    /**
     * The layout direction of the text in the book. Hebrew, Arabic and Farsi
     * RtoL. Most are 'LtoR'. Some are 'bidi', bidirectional. E.g.
     * Hebrew-English glossary.
     */
    DIRECTION(SwordBookMetaData.KEY_DIRECTION,
        "LtoR",
        "RtoL",
        "bidi"
    ),

    /**
     * This indicates the kind of markup used for the book.
     * In SWORD, Plaintext uses the GBF filter.
     */
    SOURCE_TYPE(SwordBookMetaData.KEY_SOURCE_TYPE,
        "Plaintext",
        "GBF",
        "ThML",
        "OSIS",
        "TEI"
    ),

    /**
     * The character encoding. Only Latin-1 and UTF-8 are supported.
     * Internally, SWORD supports SCSU and UTF-16.
     * Currently, there is no way to build these modules.
     * JSword does not support SCSU.
     */
    ENCODING(SwordBookMetaData.KEY_ENCODING,
        "Latin-1",
        "UTF-8",
        "UTF-16",
        "SCSU"
    ),

    /**
     * Display level is used by GenBooks to do auto expansion in the tree. A
     * level of 2 indicates that the first two levels should be shown.
     */
    DISPLAY_LEVEL(SwordBookMetaData.KEY_DISPLAY_LEVEL) {
        @Override
        public boolean isText() {
            return false;
        }

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
    FONT(BookMetaData.KEY_FONT),

    /**
     * When false do not show quotation marks for OSIS text that has &lt;q&gt;
     * elements.
     */
    OSIS_Q_TO_TICK(SwordBookMetaData.KEY_OSIS_Q_TO_TICK,
        "true",
        "false"
    )
    {
        @Override
        public boolean isText() {
            return false;
        }

        @Override
        public Object convert(String input) {
            return Boolean.valueOf(input);
        }
    },

    /**
     * A Feature describes a characteristic of the Book.
     */
    FEATURE(SwordBookMetaData.KEY_FEATURE,
        "StrongsNumbers",
        "GreekDef",
        "HebrewDef",
        "GreekParse",
        "HebrewParse",
        "DailyDevotion",
        "Glossary",
        "Images",
        "NoParagraphs"
    )
    {
        @Override
        public boolean mayRepeat() {
            return true;
        }
    },

    /**
     * Books with a Feature of Glossary are used to map words FROM one language
     * TO another.
     */
    GLOSSARY_FROM(SwordBookMetaData.KEY_GLOSSARY_FROM) {
        @Override
        public boolean isText() {
            return false;
        }

        @Override
        public Object convert(String input) {
            return new Language(input);
        }

        @Override
        public String unconvert(Object internal) {
            if (internal instanceof Language) {
                return ((Language) internal).getGivenSpecification();
            }
            return super.unconvert(internal);
        }
    },

    /**
     * Books with a Feature of Glossary are used to map words FROM one language
     * TO another.
     */
    GLOSSARY_TO(SwordBookMetaData.KEY_GLOSSARY_TO) {
        @Override
        public boolean isText() {
            return false;
        }

        @Override
        public Object convert(String input) {
            return new Language(input);
        }

        @Override
        public String unconvert(Object internal) {
            if (internal instanceof Language) {
                return ((Language) internal).getGivenSpecification();
            }
            return super.unconvert(internal);
        }
    },

    /**
     * Names a file in the module's DataPath that should be referenced for the renderer as CSS display controls.
     * Generality is advised: Use controls that are not specific to any particular rendering engine, e.g. WebKit.
     */
    PREFERRED_CSS_XHTML(SwordBookMetaData.KEY_PREFERRED_CSS_XHTML),

    /**
     * Names a file in the module's DataPath that should be referenced for the renderer as CSS display controls.
     * Generality is advised: Use controls that are not specific to any particular rendering engine, e.g. WebKit.
     */
    STRONGS_PADDING(SwordBookMetaData.KEY_STRONGS_PADDING),

    /**
     * The short name of this book.
     */
    ABBREVIATION(SwordBookMetaData.KEY_ABBREVIATION),

    /**
     * Contains RTF that describes the book.
     */
    ABOUT(SwordBookMetaData.KEY_ABOUT) {
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
    VERSION(SwordBookMetaData.KEY_VERSION) {
        @Override
        public boolean isText() {
            return false;
        }

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
    HISTORY(SwordBookMetaData.KEY_HISTORY) {
        @Override
        public boolean mayRepeat() {
            return true;
        }
    },

    /**
     * single value version number, lowest sword c++ version that can read this
     * book JSword does not use this value.
     */
    MINIMUM_VERSION(SwordBookMetaData.KEY_MINIMUM_VERSION),

    /**
     * The Category of the book. Used on the web to classify books into a tree.
     */
    CATEGORY(BookMetaData.KEY_CATEGORY,
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
    )
    {
        @Override
        public Object convert(String input) {
            return BookCategory.fromString(input);
        }
    },

    /**
     * Library of Congress Subject Heading. Typically this is of the form
     * BookCategory Scope Language, where scope is typically O.T., N.T.
     */
    LCSH(SwordBookMetaData.KEY_LCSH),

    /**
     * single value string, defaults to en, the language of the book
     */
    LANG(BookMetaData.KEY_LANG) {
        @Override
        public boolean isText() {
            return false;
        }

        @Override
        public Object convert(String input) {
            return new Language(input);
        }

        @Override
        public String unconvert(Object internal) {
            if (internal instanceof Language) {
                return ((Language) internal).getGivenSpecification();
            }
            return super.unconvert(internal);
        }
    },

    /**
     * The installed size of the book in bytes. This is not the size of the zip
     * that is downloaded.
     */
    INSTALL_SIZE(SwordBookMetaData.KEY_INSTALL_SIZE) {
        @Override
        public boolean isText() {
            return false;
        }

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
    SWORD_VERSION_DATE(SwordBookMetaData.KEY_SWORD_VERSION_DATE) {
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
    OBSOLETES(SwordBookMetaData.KEY_OBSOLETES) {
        @Override
        public boolean mayRepeat() {
            return true;
        }
    },

    /**
     * Single value version number, lowest sword c++ version that can read this
     * book JSword does not use this value.
     */
    OSIS_VERSION(SwordBookMetaData.KEY_OSIS_VERSION),

    /**
     * Informational copyright notice.
     */
    COPYRIGHT(SwordBookMetaData.KEY_COPYRIGHT) {
        @Override
        public boolean allowsContinuation() {
            return true;
        }
    },

    /**
     * single value string, unknown use
     */
    COPYRIGHT_HOLDER(SwordBookMetaData.KEY_COPYRIGHT_HOLDER),

    /**
     * Copyright info. Informational only.
     * This is a year, a year range or a comma separated list of these.
     */
    COPYRIGHT_DATE(SwordBookMetaData.KEY_COPYRIGHT_DATE) {
        @Override
        public boolean isAllowed(String value) {
            return validDatePattern.matcher(value).matches();
        }

        private Pattern validDatePattern = Pattern.compile("\\d{4}(\\s*-\\s*\\d{4})?(\\s*,\\s*\\d{4}(\\s*-\\s*\\d{4})?)*");
    },

    /**
     * Copyright info. Informational only.
     */
    COPYRIGHT_NOTES(SwordBookMetaData.KEY_COPYRIGHT_NOTES) {
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
    COPYRIGHT_CONTACT_NAME(SwordBookMetaData.KEY_COPYRIGHT_CONTACT_NAME) {
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
    COPYRIGHT_CONTACT_NOTES(SwordBookMetaData.KEY_COPYRIGHT_CONTACT_NOTES) {
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
    COPYRIGHT_CONTACT_ADDRESS(SwordBookMetaData.KEY_COPYRIGHT_CONTACT_ADDRESS) {
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
    COPYRIGHT_CONTACT_EMAIL(SwordBookMetaData.KEY_COPYRIGHT_CONTACT_EMAIL),

    /**
     * A one line promo statement, required by Lockman for NASB
     */
    SHORT_PROMO(SwordBookMetaData.KEY_SHORT_PROMO) {
        @Override
        public boolean allowsHTML() {
            return true;
        }
    },

    /**
     * A one line copyright statement, required by Lockman for NASB
     */
    SHORT_COPYRIGHT(SwordBookMetaData.KEY_SHORT_COPYRIGHT),

    /**
     * Copyright info. Informational only.
     */
    DISTRIBUTION_LICENSE(SwordBookMetaData.KEY_DISTRIBUTION_LICENSE,
        "Public Domain",
        "Copyrighted",
        "Copyrighted; Free non-commercial distribution",
        "Copyrighted; Permission to distribute granted to *",
        "Copyrighted; Freely distributable",
        "Copyrighted; Permission granted to distribute non-commercially in SWORD format",
        "GFDL",
        "GPL",
        "Creative Commons: by-nc-nd*",
        "Creative Commons: by-nc-sa*",
        "Creative Commons: by-nc*",
        "Creative Commons: by-nd*",
        "Creative Commons: by-sa*",
        "Creative Commons: by*",
        "Creative Commons: CC0*",
        "General public license for distribution for any purpose" // In kjv.conf
    ),

    /**
     * Copyright info. Informational only.
     */
    DISTRIBUTION_NOTES(SwordBookMetaData.KEY_DISTRIBUTION_NOTES) {
        @Override
        public boolean allowsContinuation() {
            return true;
        }
    },

    /**
     * Information on where the book's text was obtained.
     */
    TEXT_SOURCE(SwordBookMetaData.KEY_TEXT_SOURCE) {
        @Override
        public boolean allowsContinuation() {
            return true;
        }
    },

    /**
     * Contains the URL (a bare URL, not an HTML &lt;a&gt; link) of a web page for unlocking instructions/payment.
     */
    UNLOCK_URL(SwordBookMetaData.KEY_UNLOCK_URL),

    /**
     * Deliberately not in wiki. Similar to DataPath. It gives where on the CrossWire server the book can
     * be found. Informational only.
     */
    DISTRIBUTION_SOURCE(SwordBookMetaData.KEY_DISTRIBUTION_SOURCE) {
        @Override
        public boolean allowsContinuation() {
            return true;
        }
    },

    /**
     * New. Not in wiki. Present in SWORD engine. Present in hesychius.conf w/ PapyriPlain
     */
    LOCAL_STRIP_FILTER(SwordBookMetaData.KEY_LOCAL_STRIP_FILTER),

    /**
     * New. Not in wiki. Present in SWORD engine. Present in hesychius.conf w/ IncludeKeyInSearch
     */
    SEARCH_OPTION(SwordBookMetaData.KEY_SEARCH_OPTION),

    /**
     * New. Not supported by Sword but supported by IBT. Scope is an OSIS Reference of all keys
     * contained in the book
     */
    SCOPE(BookMetaData.KEY_SCOPE),

    /**
     * New. Not supported by Sword. Lists of books contained in the module. Usually derived and cached in the JSword
     * configuration files.
     */
    BOOK_LIST(BookMetaData.KEY_BOOKLIST);

    /**
     * Simple ctor
     */
    ConfigEntryType(String name) {
        this.name = name;
        this.picks = null;
        String defValue = SwordBookMetaData.DEFAULTS.get(name);
        this.defaultValue = defValue == null ? null : convert(defValue);
    }

    /**
     * Simple ctor
     */
    ConfigEntryType(String name, String... picks) {
        this.name = name;
        this.picks = picks;
        String defValue = SwordBookMetaData.DEFAULTS.get(name);
        this.defaultValue = defValue == null ? null : convert(defValue);
    }

    /**
     * Some keys can be converted to something other than a string.
     * 
     * @return true if this ConfigEntryType is a string
     */
    public boolean isText() {
        return true;
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
        if (value == null) {
            return false;
        }
        if (hasChoices()) {
            for (String item : picks) {
                String val = value;
                String pick = item;
                // If the pick ends with *
                // then we want to do a "startsWithCaseInsensitive"
                // with what is before the *
                if (pick.endsWith("*")) {
                    int len = pick.length() - 1;
                    pick = pick.substring(0, len);
                    if (val.length() > len) {
                        val = val.substring(0, len);
                    }
                }
                if (pick.equalsIgnoreCase(val)) {
                    return true;
                }
            }

            return false;
        }
        return true;
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
     * HTML is allowed in a few config entries.
     * 
     * @return true if HTML is allowed
     */
    public boolean allowsHTML() {
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
     * Some keys can repeat. When this happens each is a single value pick from
     * a list of choices.
     * 
     * @return true if this ConfigEntryType can occur more than once
     */
    protected boolean hasChoices() {
        return picks != null;
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
     * @param input the text to convert
     * @return the converted object
     */
    public Object convert(String input) {
        return input;
    }

    /**
     * Return the original representation of the object.
     * 
     * @param internal the object to convert
     * @return the original string
     */
    public String unconvert(Object internal) {
        if (internal == null) {
            return null;
        }
        return internal.toString();
    }

    /**
     * Lookup method to convert from a String
     * 
     * @param name the key for the entry
     * @return the matching type
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
