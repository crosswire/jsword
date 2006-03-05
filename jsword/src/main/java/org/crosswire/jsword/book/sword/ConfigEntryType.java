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

import java.util.Arrays;
import java.util.HashSet;
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
public enum ConfigEntryType
{
    /**
     * Contains rtf that describes the book.
     */
    ABOUT ("About") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#allowsContinuation()
         */
        @Override
        public boolean allowsContinuation()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#allowsRTF()
         */
        @Override
        public boolean allowsRTF()
        {
            return true;
        }
    },

    /**
     * single value integer, unknown use, some indications that we ought to be using it
     */
    BLOCK_COUNT ("BlockCount") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        @Override
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
        @Override
        public Object getDefault()
        {
            return defaultValue;
        }

        private Integer defaultValue = new Integer(200);
    },

    /**
     * The level at which compression is applied, BOOK, CHAPTER, or VERSE
     */
    BLOCK_TYPE ("BlockType") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        @Override
        public boolean isAllowed(String value)
        {
            return choices.contains(filter(value));
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#filter(java.lang.String)
         */
        @Override
        public String filter(String value)
        {
            return value.toUpperCase();
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#hasChoices()
         */
        @Override
        protected boolean hasChoices()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#getDefault()
         */
        @Override
        public Object getDefault()
        {
            return "CHAPTER"; //$NON-NLS-1$
        }

        /**
         * The set of choices.
         */
        private final Set<String> choices = new HashSet<String>(Arrays.asList(new String[]
        {
            "BOOK", //$NON-NLS-1$
            "CHAPTER", //$NON-NLS-1$
            "VERSE", //$NON-NLS-1$
        }));
    },

    /**
     * The Category of the book. Used on the web to classify books into a tree.
     */
    CATEGORY ("Category") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        @Override
        public boolean isAllowed(String value)
        {
            return choices.contains(value);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#hasChoices()
         */
        @Override
        protected boolean hasChoices()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#getDefault()
         */
        @Override
        public Object getDefault()
        {
            return "Other"; //$NON-NLS-1$
        }

        /**
         * The set of choices.
         */
        private final Set<String> choices = new HashSet<String>(Arrays.asList(new String[]
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
    },

    /**
     * If this exists in the conf, then the book is encrypted. The value is used to
     * unlock the book. The encryption algorithm is Sapphire.
     */
    CIPHER_KEY ("CipherKey"), //$NON-NLS-1$

    /**
     * The type of compression in use. JSword does not support LZSS. While it is the default,
     * it is not used. At least so far.
     */
    COMPRESS_TYPE ("CompressType") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        @Override
        public boolean isAllowed(String value)
        {
            return choices.contains(filter(value));
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#filter(java.lang.String)
         */
        @Override
        public String filter(String value)
        {
            return value.toUpperCase();
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#hasChoices()
         */
        @Override
        protected boolean hasChoices()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#getDefault()
         */
        @Override
        public Object getDefault()
        {
            return "LZSS"; //$NON-NLS-1$
        }

        /**
         * The set of choices.
         */
        private final Set<String> choices = new HashSet<String>(Arrays.asList(new String[]
        {
            "LZSS", //$NON-NLS-1$
            "ZIP", //$NON-NLS-1$
        }));
    },

    /**
     * Informational copyright notice.
     */
    COPYRIGHT ("Copyright") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#allowsContinuation()
         */
        @Override
        public boolean allowsContinuation()
        {
            return true;
        }
    },

    /**
     * Copyright info. Informational only.
     */
    COPYRIGHT_CONTACT_ADDRESS ("CopyrightContactAddress") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#allowsContinuation()
         */
        @Override
        public boolean allowsContinuation()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#allowsRTF()
         */
        @Override
        public boolean allowsRTF()
        {
            return true;
        }
    },

    /**
     * Copyright info. Informational only.
     */
    COPYRIGHT_CONTACT_EMAIL ("CopyrightContactEmail"), //$NON-NLS-1$

    /**
     * Copyright info. Informational only.
     */
    COPYRIGHT_CONTACT_NAME ("CopyrightContactName") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#allowsContinuation()
         */
        @Override
        public boolean allowsContinuation()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#allowsRTF()
         */
        @Override
        public boolean allowsRTF()
        {
            return true;
        }
    },

    /**
     * Copyright info. Informational only. This is a year, a year range or a comma separated list of these.
     */
    COPYRIGHT_DATE ("CopyrightDate") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        @Override
        public boolean isAllowed(String value)
        {
            return validDatePattern.matcher(value).matches();
        }

        private Pattern validDatePattern = Pattern.compile("\\d{4}(\\s*-\\s*\\d{4})?(\\s*,\\s*\\d{4}(\\s*-\\s*\\d{4})?)*"); //$NON-NLS-1$
    },

    /**
     * single value string, unknown use
     */
    COPYRIGHT_HOLDER ("CopyrightHolder"), //$NON-NLS-1$

    /**
     * Copyright info. Informational only.
     */
    COPYRIGHT_NOTES ("CopyrightNotes") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#allowsContinuation()
         */
        @Override
        public boolean allowsContinuation()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#allowsRTF()
         */
        @Override
        public boolean allowsRTF()
        {
            return true;
        }
    },

    /**
     * Relative path to the data files, some issues with this
     */
    DATA_PATH ("DataPath") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        @Override
        public boolean isAllowed(String value)
        {
            return true;
        }
    },

    /**
     * The full name of this book
     */
    DESCRIPTION ("Description"), //$NON-NLS-1$

    /**
     * The layout direction of the text in the book. Hebrew and Arabic is RtoL. Most are 'LtoR'.
     * Some are 'bidi', bi-directional. E.g. hebrew-english glossary.
     */
    DIRECTION ("Direction") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        @Override
        public boolean isAllowed(String value)
        {
            return choices.contains(value);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#hasChoices()
         */
        @Override
        protected boolean hasChoices()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#getDefault()
         */
        @Override
        public Object getDefault()
        {
            return DIRECTION_LTOR;
        }

        /**
         * The set of choices.
         */
        private final Set<String> choices = new HashSet<String>(Arrays.asList(new String[]
        {
            DIRECTION_LTOR,
            DIRECTION_RTOL,
            DIRECTION_BIDI,
        }));
    },

    /**
     * single value integer, unknown use, some indications that we ought to be using it
     */
    DISPLAY_LEVEL ("DisplayLevel") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        @Override
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
    },

    /**
     * Copyright info. Informational only.
     */
    DISTRIBUTION_LICENSE ("DistributionLicense") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        @Override
        public boolean isAllowed(String value)
        {
            return choices.contains(value);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#hasChoices()
         */
        @Override
        protected boolean hasChoices()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#getDefault()
         */
        @Override
        public Object getDefault()
        {
            return "Public Domain"; //$NON-NLS-1$
        }

        /**
         * The set of choices.
         */
        private final Set<String> choices = new HashSet<String>(Arrays.asList(new String[]
        {
            "Public Domain", //$NON-NLS-1$
            "Copyrighted; Free non-commercial distribution", //$NON-NLS-1$
            "Copyrighted; Permission to distribute granted to CrossWire", //$NON-NLS-1$
            "Copyrighted", //$NON-NLS-1$
        }));
    },

    /**
     * Copyright info. Informational only.
     */
    DISTRIBUTION_NOTES ("DistributionNotes") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#allowsContinuation()
         */
        @Override
        public boolean allowsContinuation()
        {
            return true;
        }
    },

    /**
     * Similar to DataPath. It gives where on the Crosswire server the book can be found.
     * Informational only.
     */
    DISTRIBUTION_SOURCE ("DistributionSource") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#allowsContinuation()
         */
        @Override
        public boolean allowsContinuation()
        {
            return true;
        }
    },

    /**
     * The character encoding. Only Latin-1 and UTF-8 are supported.
     */
    ENCODING ("Encoding") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        @Override
        public boolean isAllowed(String value)
        {
            return choices.contains(value);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#hasChoices()
         */
        @Override
        protected boolean hasChoices()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#getDefault()
         */
        @Override
        public Object getDefault()
        {
            return "Latin-1"; //$NON-NLS-1$
        }

        /**
         * The set of choices.
         */
        private final Set<String> choices = new HashSet<String>(Arrays.asList(new String[]
        {
            "Latin-1", //$NON-NLS-1$
            "UTF-8", //$NON-NLS-1$
        }));
    },

    /**
     * Global Option Filters are the names of routines in Sword that can be used to display the data.
     * These are not used by JSword.
     */
    GLOBAL_OPTION_FILTER ("GlobalOptionFilter") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        @Override
        public boolean isAllowed(String value)
        {
            return choices.contains(value);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#hasChoices()
         */
        @Override
        protected boolean hasChoices()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#mayRepeat()
         */
        @Override
        public boolean mayRepeat()
        {
            return true;
        }

        /**
         * The set of choices.
         */
        private final Set<String> choices = new HashSet<String>(Arrays.asList(new String[]
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
    },

    /**
     * Books with a Feature of Glossary are used to map words FROM one language TO another.
     * TODO(DMS): layout the glossary based on the direction of the FROM.
     */
    GLOSSARY_FROM ("GlossaryFrom"), //$NON-NLS-1$

    /**
     * Books with a Feature of Glossary are used to map words FROM one language TO another.
     */
    GLOSSARY_TO ("GlossaryTo"), //$NON-NLS-1$

    /**
     * multiple values starting with History, some sort of change-log.
     * In the conf these are of the form History_x.y. We strip off the x.y and prefix the value with it.
     * The x.y corresponds to a current or prior Version value.
     */
    HISTORY ("History") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#mayRepeat()
         */
        @Override
        public boolean mayRepeat()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#reportDetails()
         */
        @Override
        public boolean reportDetails()
        {
            return false;
        }
    },

    /**
     * The installed size of the book in bytes. This is not the size of the zip that is downloaded.
     */
    INSTALL_SIZE ("InstallSize") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        @Override
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
    },

    /**
     * A Feature describes a characteristic of the Book.
     * TODO(DMS): use this to present the user with a pick list of books for Strongs,
     * and Heb/Greek Def/Parse. We should also use DailyDevotional to map the days to a date.
     */
    FEATURE ("Feature") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        @Override
        public boolean isAllowed(String value)
        {
            return choices.contains(value);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#hasChoices()
         */
        @Override
        protected boolean hasChoices()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#mayRepeat()
         */
        @Override
        public boolean mayRepeat()
        {
            return true;
        }

        /**
         * The set of choices.
         */
        private final Set<String> choices = new HashSet<String>(Arrays.asList(new String[]
        {
            "StrongsNumbers", //$NON-NLS-1$
            "GreekDef", //$NON-NLS-1$
            "HebrewDef", //$NON-NLS-1$
            "GreekParse", //$NON-NLS-1$
            "HebrewParse", //$NON-NLS-1$
            "DailyDevotion", //$NON-NLS-1$
            "Glossary", //$NON-NLS-1$
        }));
    },

    /**
     * A recommended font to use for the book.
     * TODO(DMS): Use this font.
     * TODO(DMS): Allow a user to associate a font with a book.
     */
    FONT ("Font"), //$NON-NLS-1$

    /**
     * single value string, defaults to en, the language of the book
     */
    LANG ("Lang") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#getDefault()
         */
        @Override
        public Object getDefault()
        {
            return AbstractBookMetaData.DEFAULT_LANG_CODE;
        }
    },

    /**
     * Library of Congress Subject Heading.
     * Typically this is of the form BookCategory Scope Language, where scope is typically O.T., N.T.
     */
    LCSH ("LCSH"), //$NON-NLS-1$

    /**
     * This indicates how the book was stored.
     */
    MOD_DRV ("ModDrv") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        @Override
        public boolean isAllowed(String value)
        {
            return choices.contains(value);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#hasChoices()
         */
        @Override
        protected boolean hasChoices()
        {
            return true;
        }

        /**
         * The set of choices.
         */
        private final Set<String> choices = new HashSet<String>(Arrays.asList(new String[]
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
    },

    /**
     * single value version number, lowest sword c++ version that can read this book
     * JSword does not use this value.
     */
    MINIMUM_VERSION ("MinimumVersion") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#getDefault()
         */
        @Override
        public Object getDefault()
        {
            // This value is unimportant to JSword, but is to Sword
            return "1.5.1a"; //$NON-NLS-1$
        }
    },


    /**
     * A list of prior "initials" for the current book. Informational only.
     */
    OBSOLETES ("Obsoletes") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#mayRepeat()
         */
        @Override
        public boolean mayRepeat()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#reportDetails()
         */
        @Override
        public boolean reportDetails()
        {
            return false;
        }
    },

    /**
     * This indicates the kind of markup used for the book.
     */
    SOURCE_TYPE ("SourceType") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        @Override
        public boolean isAllowed(String value)
        {
            return choices.contains(filter(value));
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#filter(java.lang.String)
         */
        @Override
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
        @Override
        protected boolean hasChoices()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#getDefault()
         */
        @Override
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
        private final Set<String> choices = new HashSet<String>(Arrays.asList(choiceArray));
    },

    /**
     * The date that this version of the book was last updated. Informational only.
     */
    SWORD_VERSION_DATE ("SwordVersionDate") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        @Override
        public boolean isAllowed(String value)
        {
            return validDatePattern.matcher(value).matches();
        }

        private Pattern validDatePattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}"); //$NON-NLS-1$
    },

    /**
     * Information on where the book's text was obtained.
     */
    TEXT_SOURCE ("TextSource") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#allowsContinuation()
         */
        @Override
        public boolean allowsContinuation()
        {
            return true;
        }
    },

    /**
     * An informational string indicating the current version of the book.
     */
    VERSION ("Version") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        @Override
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
        @Override
        public Object getDefault()
        {
            return "1.0"; //$NON-NLS-1$
        }
    },

    /**
     * When false do not show quotation marks for OSIS text that has <q> elements.
     */
    OSIS_Q_TO_TICK ("OSISqToTick") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isAllowed(java.lang.String)
         */
        @Override
        public boolean isAllowed(String aValue)
        {
            return aValue != null
                && (aValue.equalsIgnoreCase("true") //$NON-NLS-1$
                || aValue.equalsIgnoreCase("false")); //$NON-NLS-1$
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#getDefault()
         */
        @Override
        public Object getDefault()
        {
            return Boolean.TRUE; //$NON-NLS-1$
        }
    },

    /**
     * The abbreviated name by which this book is known. This is in the [] on the first non-blank
     * line of the conf. JSword uses this for display and access purposes.
     */
    INITIALS ("Initials") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isSynthetic()
         */
        @Override
        public boolean isSynthetic()
        {
            return true;
        }
    },

    /**
     * single value string, unknown use
     * While Lang is an IS0-639 or ethnolog value, this is a friendly representation
     * of the same.
     */
    LANGUAGE ("Language") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isSynthetic()
         */
        @Override
        public boolean isSynthetic()
        {
            return true;
        }
    },

    /**
     * For a GLOSSARY_FROM, this is the friendly version of the same.
     */
    LANGUAGE_FROM ("LanguageFrom") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isSynthetic()
         */
        @Override
        public boolean isSynthetic()
        {
            return true;
        }
    },

    /**
     * For a GLOSSARY_TO, this is the friendly version of the same.
     */
    LANGUAGE_TO ("LanguageTo") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.ConfigEntryType#isSynthetic()
         */
        @Override
        public boolean isSynthetic()
        {
            return true;
        }
    },

    /**
     * A one line promo statement, required by Lockman for NASB
     */
    SHORT_PROMO ("ShortPromo"), //$NON-NLS-1$

    /**
     * A one line copyright statement, required by Lockman for NASB
     */
    SHORT_COPYRIGHT ("ShortCopyright"); //$NON-NLS-1$

    /**
     * Simple ctor
     */
    private ConfigEntryType(String name)
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
            if (name.startsWith(HISTORY.toString()))
            {
                return ConfigEntryType.HISTORY;
            }
            for (ConfigEntryType t : ConfigEntryType.values())
            {
                if (t.name.equals(name))
                {
                    return t;
                }
            }
        }
        // should not get here.
        // But there are typos in the keys in the book conf files
        // And this allows for the addition of new fields in
        // advance of changing JSword
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
     * Constants for direction
     */
    public static final String DIRECTION_LTOR = "LtoR"; //$NON-NLS-1$
    public static final String DIRECTION_RTOL = "RtoL"; //$NON-NLS-1$
    public static final String DIRECTION_BIDI = "bidi"; //$NON-NLS-1$

    /**
     * The name of the ConfigEntryType
     */
    private String name;

}
