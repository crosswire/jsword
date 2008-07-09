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

import java.io.Serializable;
import java.util.regex.Pattern;

import org.crosswire.common.util.Language;
import org.crosswire.jsword.book.BookCategory;


/**
 * Constants for the keys in a Sword Config file.
 * Taken from
 * http://sword.sourceforge.net/cgi-bin/twiki/view/Swordapi/ConfFileLayout
 * now located at
 * http://www.crosswire.org/ucgi-bin/twiki/view/Swordapi/ConfFileLayout
 * now located at
 * http://www.crosswire.org/wiki/index.php/DevTools:Modules
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class ConfigEntryType implements Serializable
{
    /**
     * Constants for direction
     */
    public static final String DIRECTION_LTOR = "LtoR"; //$NON-NLS-1$
    public static final String DIRECTION_RTOL = "RtoL"; //$NON-NLS-1$
    public static final String DIRECTION_BIDI = "bidi"; //$NON-NLS-1$

    private static final String[] BLOCK_TYPE_PICKS
        = new String[]
          {
              "BOOK", //$NON-NLS-1$
              "CHAPTER", //$NON-NLS-1$
              "VERSE", //$NON-NLS-1$
          };

    private static final String[] BOOLEAN_PICKS
        = new String[]
          {
              "true", //$NON-NLS-1$
              "false", //$NON-NLS-1$
          };

    private static final String[] KEY_TYPE_PICKS
        = new String[]
          {
              "TreeKey", //$NON-NLS-1$
              "VerseKey", //$NON-NLS-1$
          };

    private static final String[] CATEGORY_PICKS
        = new String[]
          {
              "Daily Devotional", //$NON-NLS-1$
              "Glossaries", //$NON-NLS-1$
              "Cults / Unorthodox / Questionable Material", //$NON-NLS-1$
              "Essays", //$NON-NLS-1$
              "Maps", //$NON-NLS-1$
              "Images", //$NON-NLS-1$
              "Biblical Texts", //$NON-NLS-1$
              "Commentaries", //$NON-NLS-1$
              "Lexicons / Dictionaries", //$NON-NLS-1$
              "Generic Books", //$NON-NLS-1$
          };

    private static final String[] COMPRESS_TYPE_PICKS
        = new String[]
          {
              "LZSS", //$NON-NLS-1$
              "ZIP", //$NON-NLS-1$
          };

    private static final String[] DIRECTION_PICKS
        = new String[]
          {
              DIRECTION_LTOR,
              DIRECTION_RTOL,
              DIRECTION_BIDI,
          };

    private static final String[] LICENSE_PICKS
        = new String[]
          {
              "Public Domain", //$NON-NLS-1$
              "Copyrighted", //$NON-NLS-1$
              "Copyrighted; Free non-commercial distribution", //$NON-NLS-1$
              "Copyrighted; Permission to distribute granted to CrossWire", //$NON-NLS-1$
              "Copyrighted; Freely distributable", //$NON-NLS-1$
              "Creative Commons: by-nc-nd", //$NON-NLS-1$
              "Creative Commons: by-nc-sa", //$NON-NLS-1$
              "Creative Commons: by-nc", //$NON-NLS-1$
              "Creative Commons: by-nd", //$NON-NLS-1$
              "Creative Commons: by-sa", //$NON-NLS-1$
              "Creative Commons: by", //$NON-NLS-1$
              "GFDL", //$NON-NLS-1$
              "GPL", //$NON-NLS-1$
          };

    private static final String[] ENCODING_PICKS
        = new String[]
          {
              "Latin-1", //$NON-NLS-1$
              "UTF-8", //$NON-NLS-1$
          };

    private static final String[] GLOBAL_OPTION_FILTER_PICKS
        = new String[]
          {
              "GBFStrongs", //$NON-NLS-1$
              "GBFFootnotes", //$NON-NLS-1$
              "GBFScripref", //$NON-NLS-1$
              "GBFMorph", //$NON-NLS-1$
              "GBFHeadings", //$NON-NLS-1$
              "GBFRedLetterWords", //$NON-NLS-1$
              "ThMLStrongs", //$NON-NLS-1$
              "ThMLFootnotes", //$NON-NLS-1$
              "ThMLScripref", //$NON-NLS-1$
              "ThMLMorph", //$NON-NLS-1$
              "ThMLHeadings", //$NON-NLS-1$
              "ThMLVariants", //$NON-NLS-1$
              "ThMLLemma", //$NON-NLS-1$
              "UTF8Cantillation", //$NON-NLS-1$
              "UTF8GreekAccents", //$NON-NLS-1$
              "UTF8HebrewPoints", //$NON-NLS-1$
              "OSISFootnotes", //$NON-NLS-1$
              "OSISHeadings", //$NON-NLS-1$
              "OSISLemma", //$NON-NLS-1$
              "OSISMorph", //$NON-NLS-1$
              "OSISRedLetterWords", //$NON-NLS-1$
              "OSISRuby", //$NON-NLS-1$
              "OSISScripref", //$NON-NLS-1$
              "OSISStrongs", //$NON-NLS-1$
          };

    private static final String[] FEATURE_PICKS
        = new String[]
          {
              "StrongsNumbers", //$NON-NLS-1$
              "GreekDef", //$NON-NLS-1$
              "HebrewDef", //$NON-NLS-1$
              "GreekParse", //$NON-NLS-1$
              "HebrewParse", //$NON-NLS-1$
              "DailyDevotion", //$NON-NLS-1$
              "Glossary", //$NON-NLS-1$
              "Images", //$NON-NLS-1$
          };

    private static final String[] MOD_DRV_PICKS
        = new String[]
          {
              "RawText", //$NON-NLS-1$
              "zText", //$NON-NLS-1$
              "RawCom", //$NON-NLS-1$
              "RawCom4", //$NON-NLS-1$
              "zCom", //$NON-NLS-1$
              "HREFCom", //$NON-NLS-1$
              "RawFiles", //$NON-NLS-1$
              "RawLD", //$NON-NLS-1$
              "RawLD4", //$NON-NLS-1$
              "zLD", //$NON-NLS-1$
              "RawGenBook", //$NON-NLS-1$
          };

    private static final String[] SOURCE_TYPE_PICKS
        = new String[]
          {
              "Plaintext", //$NON-NLS-1$
              "GBF", //$NON-NLS-1$
              "ThML", //$NON-NLS-1$
              "OSIS", //$NON-NLS-1$
              "TEI", //$NON-NLS-1$
          };

    /**
     * A ConfigEntryPickType is a ConfigEntryType that allows values from a pick list.
     * Matching is expected to be case-sensitive, but data problems dictate a more flexible approach. 
     * 
     */
    public static class ConfigEntryPickType extends ConfigEntryType
    {
        /**
         * Simple ctor
         */
        public ConfigEntryPickType(String name, String[] picks)
        {
            this(name, picks, null);
        }

        /**
         * Simple ctor
         */
        public ConfigEntryPickType(String name, String[] picks, Object defaultPick)
        {
            super(name, defaultPick);
            choiceArray = (String[]) picks.clone();
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#hasChoices()
         */
        protected boolean hasChoices()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        public boolean isAllowed(String value)
        {
            for (int i = 0; i < choiceArray.length; i++)
            {
                if (choiceArray[i].equalsIgnoreCase(value))
                {
                    return true;
                }
            }

            return false;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#filter(java.lang.String)
         */
        public String filter(String value)
        {
            // Do we have an exact match?
            for (int i = 0; i < choiceArray.length; i++)
            {
                if (choiceArray[i].equals(value))
                {
                    return value;
                }
            }

            // Do we have a case insensitive match?
            for (int i = 0; i < choiceArray.length; i++)
            {
                if (choiceArray[i].equalsIgnoreCase(value))
                {
                    return choiceArray[i];
                }
            }

            // No match at all!
            return value;
        }

        /**
         * The array of choices.
         */
        private final String[] choiceArray;

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 5642668733730291463L;
    }

    /**
     * Represents a ConfigEntryType that is not actually represented by the Sword Config file.
     *
     */
    public static class ConfigEntrySyntheticType extends ConfigEntryType
    {
        /**
         * Simple ctor
         */
        public ConfigEntrySyntheticType(String name)
        {
            super(name);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isSynthetic()
         */
        public boolean isSynthetic()
        {
            return true;
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = -2468890875139856087L;
    }

    /**
     * Contains rtf that describes the book.
     */
    public static final ConfigEntryType ABOUT = new ConfigEntryType("About") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#allowsContinuation()
         */
        public boolean allowsContinuation()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#allowsRTF()
         */
        public boolean allowsRTF()
        {
            return true;
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3258416110121334073L;
    };

    /**
     * single value integer, unknown use, some indications that we ought to be using it
     */
    public static final ConfigEntryType BLOCK_COUNT = new ConfigEntryType("BlockCount", new Integer(200)) //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        public boolean isAllowed(String aValue)
        {
            try
            {
                Integer.parseInt(aValue);
                return true;
            }
            catch (NumberFormatException e)
            {
                return false;
            }
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#convert(java.lang.String)
         */
        public Object convert(String input)
        {
            try
            {
                return new Integer(input);
            }
            catch (NumberFormatException e)
            {
                return getDefault();
            }
        }

        /**
         * Comment for <code>serialVersionUID</code>
         */
        private static final long serialVersionUID = 3978711675019212341L;
    };

    /**
     * The level at which compression is applied, BOOK, CHAPTER, or VERSE
     */
    public static final ConfigEntryType BLOCK_TYPE = new ConfigEntryPickType("BlockType", BLOCK_TYPE_PICKS, BLOCK_TYPE_PICKS[0]); //$NON-NLS-1$

    /**
     * The kind of key that a Generic Book uses.
     */
    public static final ConfigEntryType KEY_TYPE = new ConfigEntryPickType("KeyType", KEY_TYPE_PICKS, KEY_TYPE_PICKS[0]); //$NON-NLS-1$

    /**
     * The Category of the book. Used on the web to classify books into a tree.
     */
    public static final ConfigEntryType CATEGORY = new ConfigEntryPickType("Category", CATEGORY_PICKS, BookCategory.OTHER) //$NON-NLS-1$
    {
        /*
         * (non-Javadoc)
         * 
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#convert(java.lang.String)
         */
        public Object convert(String input)
        {
            return BookCategory.fromString(input);
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3258412850174571569L;
    };

    /**
     * If this exists in the conf, then the book is encrypted. The value is used to
     * unlock the book. The encryption algorithm is Sapphire.
     */
    public static final ConfigEntryType CIPHER_KEY = new ConfigEntryType("CipherKey"); //$NON-NLS-1$

    /**
     * The type of compression in use. JSword does not support LZSS. While it is the default,
     * it is not used. At least so far.
     */
    public static final ConfigEntryType COMPRESS_TYPE = new ConfigEntryPickType("CompressType", COMPRESS_TYPE_PICKS, COMPRESS_TYPE_PICKS[0]); //$NON-NLS-1$

    /**
     * Informational copyright notice.
     */
    public static final ConfigEntryType COPYRIGHT = new ConfigEntryType("Copyright") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#allowsContinuation()
         */
        public boolean allowsContinuation()
        {
            return true;
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3256441412957517110L;
    };

    /**
     * Copyright info. Informational only.
     */
    public static final ConfigEntryType COPYRIGHT_CONTACT_ADDRESS = new ConfigEntryType("CopyrightContactAddress") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#allowsContinuation()
         */
        public boolean allowsContinuation()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#allowsRTF()
         */
        public boolean allowsRTF()
        {
            return true;
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3256721784077365556L;
    };

    /**
     * Copyright info. Informational only.
     */
    public static final ConfigEntryType COPYRIGHT_CONTACT_EMAIL = new ConfigEntryType("CopyrightContactEmail"); //$NON-NLS-1$

    /**
     * Copyright info. Informational only.
     */
    public static final ConfigEntryType COPYRIGHT_CONTACT_NAME = new ConfigEntryType("CopyrightContactName") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#allowsContinuation()
         */
        public boolean allowsContinuation()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#allowsRTF()
         */
        public boolean allowsRTF()
        {
            return true;
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3257001060181620787L;
    };

    /**
     * Copyright info. Informational only.
     */
    public static final ConfigEntryType COPYRIGHT_CONTACT_NOTES = new ConfigEntryType("CopyrightContactNotes") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#allowsContinuation()
         */
        public boolean allowsContinuation()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#allowsRTF()
         */
        public boolean allowsRTF()
        {
            return true;
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3257001060181620787L;
    };

    /**
     * Copyright info. Informational only. This is a year, a year range or a comma separated list of these.
     */
    public static final ConfigEntryType COPYRIGHT_DATE = new ConfigEntryType("CopyrightDate") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        public boolean isAllowed(String value)
        {
            return validDatePattern.matcher(value).matches();
        }

        private Pattern validDatePattern = Pattern.compile("\\d{4}(\\s*-\\s*\\d{4})?(\\s*,\\s*\\d{4}(\\s*-\\s*\\d{4})?)*"); //$NON-NLS-1$

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3258126977217935671L;
    };

    /**
     * single value string, unknown use
     */
    public static final ConfigEntryType COPYRIGHT_HOLDER = new ConfigEntryType("CopyrightHolder"); //$NON-NLS-1$

    /**
     * Copyright info. Informational only.
     */
    public static final ConfigEntryType COPYRIGHT_NOTES = new ConfigEntryType("CopyrightNotes") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#allowsContinuation()
         */
        public boolean allowsContinuation()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#allowsRTF()
         */
        public boolean allowsRTF()
        {
            return true;
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3906926794258199608L;
    };

    /**
     * Relative path to the data files, some issues with this
     */
    public static final ConfigEntryType DATA_PATH = new ConfigEntryType("DataPath") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        public boolean isAllowed(String value)
        {
            return true;
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3546926870244309296L;
    };

    /**
     * The full name of this book
     */
    public static final ConfigEntryType DESCRIPTION = new ConfigEntryType("Description"); //$NON-NLS-1$

    /**
     * The layout direction of the text in the book. Hebrew, Arabic and Farsi RtoL. Most are 'LtoR'.
     * Some are 'bidi', bi-directional. E.g. hebrew-english glossary.
     */
    public static final ConfigEntryType DIRECTION = new ConfigEntryPickType("Direction", DIRECTION_PICKS, DIRECTION_PICKS[0]); //$NON-NLS-1$

    /**
     * Display level is used by GenBooks to do auto expansion in the tree.
     * A level of 2 indicates that the first two levels should be shown.
     */
    public static final ConfigEntryType DISPLAY_LEVEL = new ConfigEntryType("DisplayLevel") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        public boolean isAllowed(String value)
        {
            try
            {
                Integer.parseInt(value);
                return true;
            }
            catch (NumberFormatException e)
            {
                return false;
            }
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#convert(java.lang.String)
         */
        public Object convert(String input)
        {
            try
            {
                return new Integer(input);
            }
            catch (NumberFormatException e)
            {
                return null;
            }
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3979274654953451830L;
    };

    /**
     * Copyright info. Informational only.
     */
    public static final ConfigEntryType DISTRIBUTION_LICENSE = new ConfigEntryPickType("DistributionLicense", LICENSE_PICKS, LICENSE_PICKS[0]); //$NON-NLS-1$

    /**
     * Copyright info. Informational only.
     */
    public static final ConfigEntryType DISTRIBUTION_NOTES = new ConfigEntryType("DistributionNotes") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#allowsContinuation()
         */
        public boolean allowsContinuation()
        {
            return true;
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3257005453916518196L;
    };

    /**
     * Similar to DataPath. It gives where on the CrossWire server the book can be found.
     * Informational only.
     */
    public static final ConfigEntryType DISTRIBUTION_SOURCE = new ConfigEntryType("DistributionSource") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#allowsContinuation()
         */
        public boolean allowsContinuation()
        {
            return true;
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3763093051127904307L;
    };

    /**
     * The character encoding. Only Latin-1 and UTF-8 are supported.
     */
    public static final ConfigEntryType ENCODING = new ConfigEntryPickType("Encoding", ENCODING_PICKS, ENCODING_PICKS[0]); //$NON-NLS-1$

    /**
     * Global Option Filters are the names of routines in Sword that can be used to display the data.
     * These are not used by JSword.
     */
    public static final ConfigEntryType GLOBAL_OPTION_FILTER = new ConfigEntryPickType("GlobalOptionFilter", GLOBAL_OPTION_FILTER_PICKS)//$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#mayRepeat()
         */
        public boolean mayRepeat()
        {
            return true;
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3258417209599931960L;
    };

    /**
     * Books with a Feature of Glossary are used to map words FROM one language TO another.
     */
    public static final ConfigEntryType GLOSSARY_FROM = new ConfigEntryType("GlossaryFrom") //$NON-NLS-1$
    {
        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 6619179970516935818L;

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#convert(java.lang.String)
         */
        public Object convert(String input)
        {
            return new Language(input);
        }

    };

    /**
     * Books with a Feature of Glossary are used to map words FROM one language TO another.
     */
    public static final ConfigEntryType GLOSSARY_TO = new ConfigEntryType("GlossaryTo") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#convert(java.lang.String)
         */
        public Object convert(String input)
        {
            return new Language(input);
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3273532519245386866L;
    };

    /**
     * multiple values starting with History, some sort of change-log.
     * In the conf these are of the form History_x.y. We strip off the x.y and prefix the value with it.
     * The x.y corresponds to a current or prior Version value.
     */
    public static final ConfigEntryType HISTORY = new ConfigEntryType("History") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#mayRepeat()
         */
        public boolean mayRepeat()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#reportDetails()
         */
        public boolean reportDetails()
        {
            return false;
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3979272443195830835L;
    };

    /**
     * The installed size of the book in bytes. This is not the size of the zip that is downloaded.
     */
    public static final ConfigEntryType INSTALL_SIZE = new ConfigEntryType("InstallSize") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        public boolean isAllowed(String value)
        {
            try
            {
                Integer.parseInt(value);
                return true;
            }
            catch (NumberFormatException e)
            {
                return false;
            }
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#convert(java.lang.String)
         */
        public Object convert(String input)
        {
            try
            {
                return new Integer(input);
            }
            catch (NumberFormatException e)
            {
                return null;
            }
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3256720680388408370L;
    };

    /**
     * A Feature describes a characteristic of the Book.
     */
    public static final ConfigEntryType FEATURE = new ConfigEntryPickType("Feature", FEATURE_PICKS) //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#mayRepeat()
         */
        public boolean mayRepeat()
        {
            return true;
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3833181424051172401L;
    };

    /**
     * A recommended font to use for the book.
     */
    public static final ConfigEntryType FONT = new ConfigEntryType("Font"); //$NON-NLS-1$

    /**
     * single value string, defaults to en, the language of the book
     */
    public static final ConfigEntryType LANG = new ConfigEntryType("Lang", new Language(null)) //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#convert(java.lang.String)
         */
        public Object convert(String input)
        {
            return new Language(input);
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3257008752317379897L;
    };

    /**
     * Library of Congress Subject Heading.
     * Typically this is of the form BookCategory Scope Language, where scope is typically O.T., N.T.
     */
    public static final ConfigEntryType LCSH = new ConfigEntryType("LCSH"); //$NON-NLS-1$

    /**
     * This indicates how the book was stored.
     */
    public static final ConfigEntryType MOD_DRV = new ConfigEntryPickType("ModDrv", MOD_DRV_PICKS); //$NON-NLS-1$

    /**
     * single value version number, lowest sword c++ version that can read this
     * book JSword does not use this value.
     */
    public static final ConfigEntryType MINIMUM_VERSION = new ConfigEntryType("MinimumVersion", "1.5.1a"); //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * A list of prior "initials" for the current book.
     * TODO(dms): when a user installs a book with an obsoletes that matches an installed book,
     *            offer the user the opportunity to delete the old book.
     */
    public static final ConfigEntryType OBSOLETES = new ConfigEntryType("Obsoletes") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#mayRepeat()
         */
        public boolean mayRepeat()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#reportDetails()
         */
        public boolean reportDetails()
        {
            return false;
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3258412850157400372L;
    };

    /**
     * This indicates the kind of markup used for the book.
     */
    public static final ConfigEntryType SOURCE_TYPE = new ConfigEntryPickType("SourceType", SOURCE_TYPE_PICKS, SOURCE_TYPE_PICKS[0]); //$NON-NLS-1$

    /**
     * The date that this version of the book was last updated. Informational only.
     */
    public static final ConfigEntryType SWORD_VERSION_DATE = new ConfigEntryType("SwordVersionDate") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        public boolean isAllowed(String value)
        {
            return validDatePattern.matcher(value).matches();
        }

        private Pattern validDatePattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}"); //$NON-NLS-1$

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3618697504682948150L;
    };

    /**
     * Information on where the book's text was obtained.
     */
    public static final ConfigEntryType TEXT_SOURCE = new ConfigEntryType("TextSource") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#allowsContinuation()
         */
        public boolean allowsContinuation()
        {
            return true;
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3258126968594772272L;
    };

    /**
     * An informational string indicating the current version of the book.
     */
    public static final ConfigEntryType VERSION = new ConfigEntryType("Version", "1.0") //$NON-NLS-1$ //$NON-NLS-2$
    {
        public boolean isAllowed(String aValue)
        {
            try
            {
                Float.parseFloat(aValue);
                return true;
            }
            catch (NumberFormatException e)
            {
                return false;
            }

        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3256443616242055221L;
    };

    /**
     * When false do not show quotation marks for OSIS text that has <q> elements.
     */
    public static final ConfigEntryType OSIS_Q_TO_TICK = new ConfigEntryPickType("OSISqToTick", BOOLEAN_PICKS, Boolean.TRUE) //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#convert(java.lang.String)
         */
        public Object convert(String input)
        {
            return Boolean.valueOf(input);
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3258412850174373936L;
    };

    /**
     * single value version number, lowest sword c++ version that can read this book
     * JSword does not use this value.
     */
    public static final ConfigEntryType OSIS_VERSION = new ConfigEntryType("OSISVersion", "2.0"); //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * The abbreviated name by which this book is known. This is in the [] on the first non-blank
     * line of the conf. JSword uses this for display and access purposes.
     */
    public static final ConfigEntryType INITIALS = new ConfigEntrySyntheticType("Initials"); //$NON-NLS-1$

    /**
     * A one line promo statement, required by Lockman for NASB
     */
    public static final ConfigEntryType SHORT_PROMO = new ConfigEntryType("ShortPromo"); //$NON-NLS-1$

    /**
     * A one line copyright statement, required by Lockman for NASB
     */
    public static final ConfigEntryType SHORT_COPYRIGHT = new ConfigEntryType("ShortCopyright"); //$NON-NLS-1$

    /**
     * The location of a collection of modules. JSword uses this to install and delete a module.
     */
    public static final ConfigEntryType LIBRARY_URL = new ConfigEntrySyntheticType("LibraryURL"); //$NON-NLS-1$

    /**
     * The location of the module. JSword uses this to access a module.
     */
    public static final ConfigEntryType LOCATION_URL = new ConfigEntrySyntheticType("LocationURL"); //$NON-NLS-1$

    /**
     * Simple ctor
     */
    protected ConfigEntryType(String name)
    {
        this(name, null);
    }

    /**
     * Simple ctor
     */
    protected ConfigEntryType(String name, Object defaultValue)
    {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    /**
     * Returns the normalized name of this ConfigEntry.
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Determines whether the string is allowed. For some config entries,
     * the value is expected to be one of a group, for others the format is defined.
     *
     * @param value the string to be checked
     * @return true if the string is allowed
     */
    public boolean isAllowed(String value)
    {
        return value != null;
    }

    /**
     * Modify the value if necessary.
     * @param value the input
     * @return either value or a modified version of it.
     */
    public String filter(String value)
    {
        return value;
    }

    /**
     * RTF is allowed in a few config entries.
     * @return true if rtf is allowed
     */
    public boolean allowsRTF()
    {
        return false;
    }

    /**
     * While most fields are single line or single value, some allow continuation.
     * A continuation mark is a backslash at the end of a line. It is not to be followed by whitespace.
     * @return true if continuation is allowed
     */
    public boolean allowsContinuation()
    {
        return false;
    }

    /**
     * Some keys can repeat. When this happens each is a single value pick from a list of choices.
     * @return true if this ConfigEntryType can occur more than once
     */
    public boolean mayRepeat()
    {
        return false;
    }

    /**
     * Determines the level of detail stored in the histogram.
     *
     * @return true if the ConfigEntry should report histogram for repetitions
     */
    public boolean reportDetails()
    {
        return true;
    }

    /**
     * Some keys can repeat. When this happens each is a single value pick from a list of choices.
     * @return true if this ConfigEntryType can occur more than once
     */
    protected boolean hasChoices()
    {
        return false;
    }

    /**
     * Synthetic keys are those that are not in the Sword Book's conf,
     * but are needed by the program. Typically, these are derived by the program
     * from the other entries.
     * @return true if this is synthetic
     */
    public boolean isSynthetic()
    {
        return false;
    }

    /**
     * Some ConfigEntryTypes have defaults.
     * @return the default, if there is one, null otherwise
     */
    public Object getDefault()
    {
        return defaultValue;
    }

    /**
     * Convert the string value from the conf into the representation
     * of this ConfigEntryType.
     * @return the converted object
     */
    public Object convert(String input)
    {
        return input;
    }

    /**
     * Lookup method to convert from a String
     */
    public static ConfigEntryType fromString(String name)
    {
        if (name != null)
        {
            // special case
            if (name.startsWith(ConfigEntryType.HISTORY.toString()))
            {
                return ConfigEntryType.HISTORY;
            }

            for (int i = 0; i < VALUES.length; i++)
            {
                ConfigEntryType o = VALUES[i];
                if (name.equals(o.name))
                {
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

    /**
     * Lookup method to convert from an integer
     */
    public static ConfigEntryType fromInteger(int i)
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
     * The name of the ConfigEntryType
     */
    private String name;

    /**
     * The default for the ConfigEntryType
     */
    private Object defaultValue;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3258125873411273014L;

    // Support for serialization
    private static int nextObj;
    private final int obj = nextObj++;

    Object readResolve()
    {
        return VALUES[obj];
    }

    private static final ConfigEntryType[] VALUES =
    {
        ABOUT,
        BLOCK_COUNT,
        BLOCK_TYPE,
        CATEGORY,
        CIPHER_KEY,
        COMPRESS_TYPE,
        COPYRIGHT,
        COPYRIGHT_CONTACT_ADDRESS,
        COPYRIGHT_CONTACT_EMAIL,
        COPYRIGHT_CONTACT_NAME,
        COPYRIGHT_CONTACT_NOTES,
        COPYRIGHT_DATE,
        COPYRIGHT_HOLDER,
        COPYRIGHT_NOTES,
        DATA_PATH,
        DESCRIPTION,
        DIRECTION,
        DISPLAY_LEVEL,
        DISTRIBUTION_LICENSE,
        DISTRIBUTION_NOTES,
        DISTRIBUTION_SOURCE,
        ENCODING,
        GLOBAL_OPTION_FILTER,
        GLOSSARY_FROM,
        GLOSSARY_TO,
        HISTORY,
        INSTALL_SIZE,
        FEATURE,
        FONT,
        LANG,
        LCSH,
        MOD_DRV,
        MINIMUM_VERSION,
        OBSOLETES,
        SOURCE_TYPE,
        SWORD_VERSION_DATE,
        TEXT_SOURCE,
        VERSION,
        OSIS_Q_TO_TICK,
        OSIS_VERSION,
        INITIALS,
        SHORT_PROMO,
        SHORT_COPYRIGHT,
        LOCATION_URL,
        KEY_TYPE,
    };
}
