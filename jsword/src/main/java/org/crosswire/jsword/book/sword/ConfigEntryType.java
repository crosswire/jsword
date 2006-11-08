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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import org.crosswire.jsword.book.basic.AbstractBookMetaData;


/**
 * Constants for the keys in a SwordConfig file.
 * Taken from
 * http://sword.sourceforge.net/cgi-bin/twiki/view/Swordapi/ConfFileLayout
 * now located at
 * http://www.crosswire.org/ucgi-bin/twiki/view/Swordapi/ConfFileLayout
 *
 * <p>Keys that might be available that we are ignoring for now:
 * <li>DistributionLicense: single value coded string, a ';' separated string of license attributes
 *     There are mis-spellings, etc. so there is a need for a distributionLicenseAdditionalInfo field. (Ugh!)
 *     See also SwordConstants.DISTIBUTION_LICENSE_STRINGS.
 *     <pre>
 *     // Returns the distributionLicense - this is a 'flag type' field - the value
 *     // will be the result of several constants ORed. See the
 *     // DISTRIBUTION_LICENSE* constants in SwordConstants. It appears some
 *     // versions do not stick to this convention, because of this, there is an
 *     // additional menber distributionLicenseAdditionInfo, to store additional
 *     // information.
 *     private int distributionLicense;
 *     private String distributionLicenseAdditionalInfo = "";
 *     String licensesString = reader.getFirstValue("DistributionLicense");
 *     if (licensesString != null)
 *     {
 *         StringTokenizer tok = new StringTokenizer(licensesString, ";");
 *         while (tok.hasMoreTokens())
 *         {
 *             String distributionLicenseString = tok.nextToken().trim();
 *             int index = matchingIndex(SwordConstants.DISTIBUTION_LICENSE_STRINGS, distributionLicenseString, -1);
 *             if (index != -1)
 *             {
 *                 distributionLicense |= 1 << index;
 *             }
 *             else
 *             {
 *                 if (!distributionLicenseAdditionalInfo.equals(""))
 *                 {
 *                     distributionLicenseAdditionalInfo += "; ";
 *                 }
 *                 distributionLicenseAdditionalInfo += distributionLicenseString;
 *             }
 *         }
 *     }
 *     </pre>
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com\
 */
public class ConfigEntryType implements Serializable
{
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
    public static final ConfigEntryType BLOCK_COUNT = new ConfigEntryType("BlockCount") //$NON-NLS-1$
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
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#getDefault()
         */
        public Object getDefault()
        {
            return defaultValue;
        }

        private Integer defaultValue = new Integer(200);

        /**
         * Comment for <code>serialVersionUID</code>
         */
        private static final long serialVersionUID = 3978711675019212341L;
    };

    /**
     * The level at which compression is applied, BOOK, CHAPTER, or VERSE
     */
    public static final ConfigEntryType BLOCK_TYPE = new ConfigEntryType("BlockType") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        public boolean isAllowed(String value)
        {
            return choices.contains(filter(value));
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#filter(java.lang.String)
         */
        public String filter(String value)
        {
            return value.toUpperCase(Locale.ENGLISH);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#hasChoices()
         */
        protected boolean hasChoices()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#getDefault()
         */
        public Object getDefault()
        {
            return "CHAPTER"; //$NON-NLS-1$
        }

        /**
         * The set of choices.
         */
        private final Set choices = new HashSet(Arrays.asList(new String[]
        {
            "BOOK", //$NON-NLS-1$
            "CHAPTER", //$NON-NLS-1$
            "VERSE", //$NON-NLS-1$
        }));

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3763101864299935031L;
    };

    /**
     * The Category of the book. Used on the web to classify books into a tree.
     */
    public static final ConfigEntryType CATEGORY = new ConfigEntryType("Category") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        public boolean isAllowed(String value)
        {
            return choices.contains(value);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#hasChoices()
         */
        protected boolean hasChoices()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#getDefault()
         */
        public Object getDefault()
        {
            return "Other"; //$NON-NLS-1$
        }

        /**
         * The set of choices.
         */
        private final Set choices = new HashSet(Arrays.asList(new String[]
        {
            "Daily Devotional", //$NON-NLS-1$
            "Glossaries", //$NON-NLS-1$
            "Cults / Unorthodox / Questionable Material", //$NON-NLS-1$
            "Essays", //$NON-NLS-1$
            // The following are not actually in the conf,
            // but are deduced from other fields
            "Bible", //$NON-NLS-1$
            "Dictionary", //$NON-NLS-1$
            "Commentary", //$NON-NLS-1$
        }));

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
    public static final ConfigEntryType COMPRESS_TYPE = new ConfigEntryType("CompressType") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        public boolean isAllowed(String value)
        {
            return choices.contains(filter(value));
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#filter(java.lang.String)
         */
        public String filter(String value)
        {
            return value.toUpperCase(Locale.ENGLISH);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#hasChoices()
         */
        protected boolean hasChoices()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#getDefault()
         */
        public Object getDefault()
        {
            return "LZSS"; //$NON-NLS-1$
        }

        /**
         * The set of choices.
         */
        private final Set choices = new HashSet(Arrays.asList(new String[]
        {
            "LZSS", //$NON-NLS-1$
            "ZIP", //$NON-NLS-1$
        }));

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3256726182190920496L;
    };

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
     * The layout direction of the text in the book. Hebrew and Arabic is RtoL. Most are 'LtoR'.
     * Some are 'bidi', bi-directional. E.g. hebrew-english glossary.
     */
    public static final ConfigEntryType DIRECTION = new ConfigEntryType("Direction") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        public boolean isAllowed(String value)
        {
            return choices.contains(value);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#hasChoices()
         */
        protected boolean hasChoices()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#getDefault()
         */
        public Object getDefault()
        {
            return DIRECTION_LTOR;
        }

        /**
         * The set of choices.
         */
        private final Set choices = new HashSet(Arrays.asList(new String[]
        {
            DIRECTION_LTOR,
            DIRECTION_RTOL,
            DIRECTION_BIDI,
        }));

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3257283651765940536L;
    };

    /**
     * single value integer, unknown use, some indications that we ought to be using it
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

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3979274654953451830L;
    };

    /**
     * Copyright info. Informational only.
     */
    public static final ConfigEntryType DISTRIBUTION_LICENSE = new ConfigEntryType("DistributionLicense") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        public boolean isAllowed(String value)
        {
            return choices.contains(value);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#hasChoices()
         */
        protected boolean hasChoices()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#getDefault()
         */
        public Object getDefault()
        {
            return "Public Domain"; //$NON-NLS-1$
        }

        /**
         * The set of choices.
         */
        private final Set choices = new HashSet(Arrays.asList(new String[]
        {
            "Public Domain", //$NON-NLS-1$
            "Copyrighted; Free non-commercial distribution", //$NON-NLS-1$
            "Copyrighted; Permission to distribute granted to CrossWire", //$NON-NLS-1$
            "Copyrighted", //$NON-NLS-1$
        }));

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3257289110669505585L;
    };

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
     * Similar to DataPath. It gives where on the Crosswire server the book can be found.
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
    public static final ConfigEntryType ENCODING = new ConfigEntryType("Encoding") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        public boolean isAllowed(String value)
        {
            return choices.contains(value);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#hasChoices()
         */
        protected boolean hasChoices()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#getDefault()
         */
        public Object getDefault()
        {
            return "Latin-1"; //$NON-NLS-1$
        }

        /**
         * The set of choices.
         */
        private final Set choices = new HashSet(Arrays.asList(new String[]
        {
            "Latin-1", //$NON-NLS-1$
            "UTF-8", //$NON-NLS-1$
        }));

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3761121643874891315L;
    };

    /**
     * Global Option Filters are the names of routines in Sword that can be used to display the data.
     * These are not used by JSword.
     */
    public static final ConfigEntryType GLOBAL_OPTION_FILTER = new ConfigEntryType("GlobalOptionFilter") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        public boolean isAllowed(String value)
        {
            return choices.contains(value);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#hasChoices()
         */
        protected boolean hasChoices()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#mayRepeat()
         */
        public boolean mayRepeat()
        {
            return true;
        }

        /**
         * The set of choices.
         */
        private final Set choices = new HashSet(Arrays.asList(new String[]
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
            "OSISStrongs", //$NON-NLS-1$
            "OSISFootnotes", //$NON-NLS-1$
            "OSISScripref", //$NON-NLS-1$
            "OSISMorph", //$NON-NLS-1$
            "OSISHeadings", //$NON-NLS-1$
            "OSISRedLetterWords", //$NON-NLS-1$
        }));

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3258417209599931960L;
    };

    /**
     * Books with a Feature of Glossary are used to map words FROM one language TO another.
     * TODO(DMS): layout the glossary based on the direction of the FROM.
     */
    public static final ConfigEntryType GLOSSARY_FROM = new ConfigEntryType("GlossaryFrom"); //$NON-NLS-1$

    /**
     * Books with a Feature of Glossary are used to map words FROM one language TO another.
     */
    public static final ConfigEntryType GLOSSARY_TO = new ConfigEntryType("GlossaryTo"); //$NON-NLS-1$

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

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3256720680388408370L;
    };

    /**
     * A Feature describes a characteristic of the Book.
     * TODO(DMS): use this to present the user with a pick list of books for Strongs,
     * and Heb/Greek Def/Parse. We should also use DailyDevotional to map the days to a date.
     */
    public static final ConfigEntryType FEATURE = new ConfigEntryType("Feature") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        public boolean isAllowed(String value)
        {
            return choices.contains(value);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#hasChoices()
         */
        protected boolean hasChoices()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#mayRepeat()
         */
        public boolean mayRepeat()
        {
            return true;
        }

        /**
         * The set of choices.
         */
        private final Set choices = new HashSet(Arrays.asList(new String[]
        {
            "StrongsNumbers", //$NON-NLS-1$
            "GreekDef", //$NON-NLS-1$
            "HebrewDef", //$NON-NLS-1$
            "GreekParse", //$NON-NLS-1$
            "HebrewParse", //$NON-NLS-1$
            "DailyDevotion", //$NON-NLS-1$
            "Glossary", //$NON-NLS-1$
        }));

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3833181424051172401L;
    };

    /**
     * A recommended font to use for the book.
     * TODO(DMS): Use this font.
     * TODO(DMS): Allow a user to associate a font with a book.
     */
    public static final ConfigEntryType FONT = new ConfigEntryType("Font"); //$NON-NLS-1$

    /**
     * single value string, defaults to en, the language of the book
     */
    public static final ConfigEntryType LANG = new ConfigEntryType("Lang") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#getDefault()
         */
        public Object getDefault()
        {
            return AbstractBookMetaData.DEFAULT_LANG_CODE;
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
    public static final ConfigEntryType MOD_DRV = new ConfigEntryType("ModDrv") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        public boolean isAllowed(String value)
        {
            return choices.contains(value);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#hasChoices()
         */
        protected boolean hasChoices()
        {
            return true;
        }

        /**
         * The set of choices.
         */
        private final Set choices = new HashSet(Arrays.asList(new String[]
        {
            "RawText",  //$NON-NLS-1$
            "zText",  //$NON-NLS-1$
            "RawCom", //$NON-NLS-1$
            "zCom", //$NON-NLS-1$
            "HREFCom", //$NON-NLS-1$
            "RawFiles", //$NON-NLS-1$
            "RawLD", //$NON-NLS-1$
            "RawLD4", //$NON-NLS-1$
            "zLD", //$NON-NLS-1$
            "RawGenBook", //$NON-NLS-1$
        }));

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3617569405685412913L;
    };

    /**
     * single value version number, lowest sword c++ version that can read this book
     * JSword does not use this value.
     */
    public static final ConfigEntryType MINIMUM_VERSION = new ConfigEntryType("MinimumVersion") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#getDefault()
         */
        public Object getDefault()
        {
            // This value is unimportant to JSword, but is to Sword
            return "1.5.1a"; //$NON-NLS-1$
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 4051044181290266680L;
    };


    /**
     * A list of prior "initials" for the current book. Informational only.
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
    public static final ConfigEntryType SOURCE_TYPE = new ConfigEntryType("SourceType") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        public boolean isAllowed(String value)
        {
            return choices.contains(filter(value));
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#filter(java.lang.String)
         */
        public String filter(String value)
        {

            for (int i = 0; i < choiceArray.length; i++)
            {
                if (choiceArray[i].equalsIgnoreCase(value))
                {
                    return choiceArray[i];
                }
            }
            return value;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#hasChoices()
         */
        protected boolean hasChoices()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#getDefault()
         */
        public Object getDefault()
        {
            return "Plaintext"; //$NON-NLS-1$
        }

        /**
         * The array of choices.
         */
        private final String[] choiceArray = new String[]
        {
            "Plaintext", //$NON-NLS-1$
            "GBF", //$NON-NLS-1$
            "ThML", //$NON-NLS-1$
            "OSIS", //$NON-NLS-1$
        };
        /**
         * The set of choices.
         */
        private final Set choices = new HashSet(Arrays.asList(choiceArray));

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3834025853343774774L;
    };

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
    public static final ConfigEntryType VERSION = new ConfigEntryType("Version") //$NON-NLS-1$
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

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#getDefault()
         */
        public Object getDefault()
        {
            return "1.0"; //$NON-NLS-1$
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3256443616242055221L;
    };

    /**
     * When false do not show quotation marks for OSIS text that has <q> elements.
     */
    public static final ConfigEntryType OSIS_Q_TO_TICK = new ConfigEntryType("OSISqToTick") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        public boolean isAllowed(String aValue)
        {
            return aValue != null
                && (aValue.equalsIgnoreCase("true") //$NON-NLS-1$
                || aValue.equalsIgnoreCase("false")); //$NON-NLS-1$
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#getDefault()
         */
        public Object getDefault()
        {
            return Boolean.TRUE;
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
    public static final ConfigEntryType OSIS_VERSION = new ConfigEntryType("OSISVersion") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#getDefault()
         */
        public Object getDefault()
        {
            // This value is unimportant to JSword, but is to Sword
            return "1.5.1a"; //$NON-NLS-1$
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = -6381507058727637134L;
    };


    /**
     * The abbreviated name by which this book is known. This is in the [] on the first non-blank
     * line of the conf. JSword uses this for display and access purposes.
     */
    public static final ConfigEntryType INITIALS = new ConfigEntryType("Initials") //$NON-NLS-1$
    {
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
        private static final long serialVersionUID = 3257009838994108467L;
    };

    /**
     * single value string, unknown use
     * While Lang is an IS0-639 or ethnolog value, this is a friendly representation
     * of the same.
     */
    public static final ConfigEntryType LANGUAGE = new ConfigEntryType("Language") //$NON-NLS-1$
    {
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
        private static final long serialVersionUID = 3834029147533226546L;
    };

    /**
     * For a GLOSSARY_FROM, this is the friendly version of the same.
     */
    public static final ConfigEntryType LANGUAGE_FROM = new ConfigEntryType("LanguageFrom") //$NON-NLS-1$
    {
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
        private static final long serialVersionUID = 3905243407495214134L;
    };

    /**
     * For a GLOSSARY_TO, this is the friendly version of the same.
     */
    public static final ConfigEntryType LANGUAGE_TO = new ConfigEntryType("LanguageTo") //$NON-NLS-1$
    {
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
        private static final long serialVersionUID = 3257850961078007856L;
    };

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
    public static final ConfigEntryType LIBRARY_URL = new ConfigEntryType("LibraryURL") //$NON-NLS-1$
    {
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
    };

    /**
     * The location of the module. JSword uses this to access a module.
     */
    public static final ConfigEntryType LOCATION_URL = new ConfigEntryType("LocationURL") //$NON-NLS-1$
    {
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
    };

    /**
     * Simple ctor
     */
    protected ConfigEntryType(String name)
    {
        this.name = name;
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
        return null;
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
     * Constants for direction
     */
    public static final String DIRECTION_LTOR = "LtoR"; //$NON-NLS-1$
    public static final String DIRECTION_RTOL = "RtoL"; //$NON-NLS-1$
    public static final String DIRECTION_BIDI = "bidi"; //$NON-NLS-1$

    /**
     * The name of the ConfigEntryType
     */
    private String name;

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
        LANGUAGE,
        LANGUAGE_FROM,
        LANGUAGE_TO,
        SHORT_PROMO,
        SHORT_COPYRIGHT,
        LOCATION_URL,
    };
}
