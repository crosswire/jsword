package org.crosswire.jsword.book.sword;

import org.apache.commons.lang.enum.Enum;

/**
 * Constants for the keys in a SwordConfig file.
 * Taken from
 * http://sword.sourceforge.net/cgi-bin/twiki/view/Swordapi/ConfFileLayout
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
public class ConfigEntry extends Enum
{
    /**
     * single value string, unknown use
     */
    public static final ConfigEntry ABOUT = new ConfigEntry("About"); //$NON-NLS-1$

    /**
     * single value integer, unknown use, some indications that we ought to be using it
     */
    public static final ConfigEntry BLOCK_COUNT = new ConfigEntry("BlockCount"); //$NON-NLS-1$

    /**
     * The block type in use
     */
    public static final ConfigEntry BLOCK_TYPE = new ConfigEntry("BlockType"); //$NON-NLS-1$

    /**
     * single value string, unknown use
     */
    public static final ConfigEntry CATEGORY = new ConfigEntry("Category"); //$NON-NLS-1$

    /**
     * single value string, for encryption
     */
    public static final ConfigEntry CIPHER_KEY = new ConfigEntry("CipherKey"); //$NON-NLS-1$

    /**
     * The type of compression in use
     */
    public static final ConfigEntry COMPRESS_TYPE = new ConfigEntry("CompressType"); //$NON-NLS-1$

    /**
     * single value string, unknown use
     */
    public static final ConfigEntry COPYRIGHT = new ConfigEntry("Copyright"); //$NON-NLS-1$

    /**
     * single value string, unknown use
     */
    public static final ConfigEntry COPYRIGHT_CONTACT_ADDRESS = new ConfigEntry("CopyrightContactAddress"); //$NON-NLS-1$

    /**
     * single value string, unknown use
     */
    public static final ConfigEntry COPYRIGHT_CONTACT_EMAIL = new ConfigEntry("CopyrightContactEmail"); //$NON-NLS-1$

    /**
     * single value string, unknown use
     */
    public static final ConfigEntry COPYRIGHT_CONTACT_NAME = new ConfigEntry("CopyrightContactName"); //$NON-NLS-1$

    /**
     * single value string, unknown use
     */
    public static final ConfigEntry COPYRIGHT_DATE = new ConfigEntry("CopyrightDate"); //$NON-NLS-1$

    /**
     * single value string, unknown use
     */
    public static final ConfigEntry COPYRIGHT_HOLDER = new ConfigEntry("CopyrightHolder"); //$NON-NLS-1$

    /**
     * single value string, unknown use
     */
    public static final ConfigEntry COPYRIGHT_NOTES = new ConfigEntry("CopyrightNotes"); //$NON-NLS-1$

    /**
     * Relative path to the data files, some issues with this
     */
    public static final ConfigEntry DATA_PATH = new ConfigEntry("DataPath"); //$NON-NLS-1$

    /**
     * The full name of this module
     */
    public static final ConfigEntry DESCRIPTION = new ConfigEntry("Description"); //$NON-NLS-1$

    /**
     * single value choice, from SwordConstants.DIRECTION_STRINGS we should probably use it?
     */
    public static final ConfigEntry DIRECTION = new ConfigEntry("Direction"); //$NON-NLS-1$

    /**
     * single value integer, unknown use, some indications that we ought to be using it
     */
    public static final ConfigEntry DISPLAY_LEVEL = new ConfigEntry("DisplayLevel"); //$NON-NLS-1$

    /**
     * unknown
     */
    public static final ConfigEntry DISTRIBUTION = new ConfigEntry("Distribution"); //$NON-NLS-1$

    /**
     * single value coded string, a ';' separated string of license attributes
     */
    public static final ConfigEntry DISTRIBUTION_LICENSE = new ConfigEntry("DistributionLicense"); //$NON-NLS-1$

    /**
     * single value string, unknown use
     */
    public static final ConfigEntry DISTRIBUTION_NOTES = new ConfigEntry("DistributionNotes"); //$NON-NLS-1$

    /**
     * single value string, unknown use
     */
    public static final ConfigEntry DISTRIBUTION_SOURCE = new ConfigEntry("DistributionSource"); //$NON-NLS-1$

    /**
     * The character encoding
     */
    public static final ConfigEntry ENCODING = new ConfigEntry("Encoding"); //$NON-NLS-1$

    /**
     * multiple value flagset, from SwordConstants.GOF_STRINGS
     */
    public static final ConfigEntry GLOBAL_OPTION_FILTER = new ConfigEntry("GlobalOptionFilter"); //$NON-NLS-1$

    /**
     * unknown
     */
    public static final ConfigEntry GLOSSARY_FROM = new ConfigEntry("GlossaryFrom"); //$NON-NLS-1$

    /**
     * unknown
     */
    public static final ConfigEntry GLOSSARY_TO = new ConfigEntry("GlossaryTo"); //$NON-NLS-1$

    /**
     * multiple values starting with History, some sort of change-log?
     */
    public static final ConfigEntry HISTORY = new ConfigEntry("History"); //$NON-NLS-1$

    /**
     * single value integer, the installed size (in bytes?)
     */
    public static final ConfigEntry INSTALL_SIZE = new ConfigEntry("InstallSize"); //$NON-NLS-1$

    /**
     * multiple value flagset, from SwordConstants.FEATURE_STRINGS
     */
    public static final ConfigEntry FEATURE = new ConfigEntry("Feature"); //$NON-NLS-1$

    /**
     * single value string
     */
    public static final ConfigEntry FONT = new ConfigEntry("Font"); //$NON-NLS-1$

    /**
     * unknown
     */
    public static final ConfigEntry HISTORY_0_1 = new ConfigEntry("History_0.1"); //$NON-NLS-1$

    /**
     * unknown
     */
    public static final ConfigEntry HISTORY_0_2 = new ConfigEntry("History_0.2"); //$NON-NLS-1$

    /**
     * unknown
     */
    public static final ConfigEntry HISTORY_0_3 = new ConfigEntry("History_0.3"); //$NON-NLS-1$

    /**
     * unknown
     */
    public static final ConfigEntry HISTORY_0_9 = new ConfigEntry("History_0.9"); //$NON-NLS-1$

    /**
     * unknown
     */
    public static final ConfigEntry HISTORY_0_91 = new ConfigEntry("History_0.91"); //$NON-NLS-1$

    /**
     * unknown
     */
    public static final ConfigEntry HISTORY_0_92 = new ConfigEntry("History_0.92"); //$NON-NLS-1$

    /**
     * unknown
     */
    public static final ConfigEntry HISTORY_1_0 = new ConfigEntry("History_1.0"); //$NON-NLS-1$

    /**
     * unknown
     */
    public static final ConfigEntry HISTORY_1_1 = new ConfigEntry("History_1.1"); //$NON-NLS-1$

    /**
     * unknown
     */
    public static final ConfigEntry HISTORY_1_2 = new ConfigEntry("History_1.2"); //$NON-NLS-1$

    /**
     * unknown
     */
    public static final ConfigEntry HISTORY_1_3 = new ConfigEntry("History_1.3"); //$NON-NLS-1$

    /**
     * unknown
     */
    public static final ConfigEntry HISTORY_1_4 = new ConfigEntry("History_1.4"); //$NON-NLS-1$

    /**
     * unknown
     */
    public static final ConfigEntry HISTORY_1_5 = new ConfigEntry("History_1.5"); //$NON-NLS-1$

    /**
     * unknown
     */
    public static final ConfigEntry HISTORY_1_6 = new ConfigEntry("History_1.6"); //$NON-NLS-1$

    /**
     * unknown
     */
    public static final ConfigEntry HISTORY_1_7 = new ConfigEntry("History_1.7"); //$NON-NLS-1$

    /**
     * unknown
     */
    public static final ConfigEntry HISTORY_1_8 = new ConfigEntry("History_1.8"); //$NON-NLS-1$

    /**
     * unknown
     */
    public static final ConfigEntry HISTORY_1_9 = new ConfigEntry("History_1.9"); //$NON-NLS-1$

    /**
     * unknown
     */
    public static final ConfigEntry HISTORY_2_0 = new ConfigEntry("History_2.0"); //$NON-NLS-1$

    /**
     * unknown
     */
    public static final ConfigEntry HISTORY_2_1 = new ConfigEntry("History_2.1"); //$NON-NLS-1$

    /**
     * unknown
     */
    public static final ConfigEntry HISTORY_2_2 = new ConfigEntry("History_2.2"); //$NON-NLS-1$

    /**
     * unknown
     */
    public static final ConfigEntry HISTORY_2_5 = new ConfigEntry("History_2.5"); //$NON-NLS-1$

    /**
     * single value string, defaults to en, the language of the module
     */
    public static final ConfigEntry LANG = new ConfigEntry("Lang"); //$NON-NLS-1$

    /**
     * unknown
     */
    public static final ConfigEntry LCSH = new ConfigEntry("LCSH"); //$NON-NLS-1$

    /**
     * unknown
     */
    public static final ConfigEntry LEXICON_FROM = new ConfigEntry("LexiconFrom"); //$NON-NLS-1$

    /**
     * unknown
     */
    public static final ConfigEntry LEXICON_TO = new ConfigEntry("LexiconTo"); //$NON-NLS-1$

    /**
     * The type of module
     */
    public static final ConfigEntry MOD_DRV = new ConfigEntry("ModDrv"); //$NON-NLS-1$

    /**
     * single value version number, lowest sword c++ version that can read this module
     */
    public static final ConfigEntry MINIMUM_VERSION = new ConfigEntry("MinimumVersion"); //$NON-NLS-1$

    /**
     * unknown
     */
    public static final ConfigEntry MINIMUM_SWORD_VERSION = new ConfigEntry("MinimumSwordVersion"); //$NON-NLS-1$

    /**
     * unknown
     */
    public static final ConfigEntry OBSOLETES = new ConfigEntry("Obsoletes"); //$NON-NLS-1$

    /**
     * unknown
     */
    public static final ConfigEntry SOURCE_TYPE = new ConfigEntry("SourceType"); //$NON-NLS-1$

    /**
     * unknown
     */
    public static final ConfigEntry SWORD_VERSION_DATE = new ConfigEntry("SwordVersionDate"); //$NON-NLS-1$

    /**
     * single value string, unknown use
     */
    public static final ConfigEntry TEXT_SOURCE = new ConfigEntry("TextSource"); //$NON-NLS-1$

    /**
     * single value string, unknown use
     */
    public static final ConfigEntry VERSION = new ConfigEntry("Version"); //$NON-NLS-1$

    /**
     * Find a ConfigEntry for a given name.
     */
    public static ConfigEntry getConfigEntry(String name)
    {
        return (ConfigEntry) Enum.getEnum(ConfigEntry.class, name);
    }

    /**
     * Simple ctor
     */
    private ConfigEntry(String name)
    {
        super(name);
    }

    /**
     * SERIALUID(dms): A placeholder for the ultimate version id.
     */
    private static final long serialVersionUID = 1L;
}
