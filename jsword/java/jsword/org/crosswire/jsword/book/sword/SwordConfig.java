
package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.data.Filter;
import org.crosswire.jsword.book.data.Filters;

/**
 * A utility class for loading and representing Sword module configs.
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
 * @author Mark Goodwin [mark at thorubio dot org]
 * @author Joe Walker [joe at eireneh dot com]
 * @author Jacky Cheung
 * @version $Id$
 */
public class SwordConfig
{
    /**
     * Loads a sword config from a given URL.
     */
    public SwordConfig(SwordBookDriver driver, File parent, String bookdir) throws IOException
    {
        this.driver = driver;
        this.url = new File(parent, bookdir).toURL();
        this.name = bookdir.substring(0, bookdir.indexOf(".conf"));
        this.reader = new ConfigReader(url.openStream());

        setAllProperties();
    }

    /**
     * Sets the members of the Config from the current properties set.
     */
    private void setAllProperties()
    {
        setMandatoryProperties();
        setModuleAccessProperties();
        setRenderingProperties();
        setFeatureProperties();
        setInformaticProperties();
        setRenderingProperties();
        setLicensingProperties();
        sanityCheck();
    }

    /**
     * Method sanityCheck.  This method should check certain contitions are set
     */
    private void sanityCheck()
    {
        if (dataPath == null)
        {
            log.warn("No data path could be found in the config: " + url.toString());
            // PENDING(mark): assume something like <sword root>/modules/type/name
        }

        if (description == null)
        {
            log.warn("No description could be found in the config: " + url.toString());
        }

        if (modDrv == -1)
        {
            log.warn("No data path could be found in the config: " + url.toString());
            // PENDING(mark): Throw - we can't sensibly assume a name.
        }
    }

    /**
     * Method setLicensingProperties.
     */
    private void setLicensingProperties()
    {
        copyrightHolder = reader.getFirstValue("CopyrightHolder");
        copyrightDate = reader.getFirstValue("CopyrightDate");
        copyrightNotes = reader.getFirstValue("CopyrightNotes");
        copyrightContactName = reader.getFirstValue("CopyrightContactName");
        copyrightContactAddress = reader.getFirstValue("CopyrightContactAddress");
        copyrightContactEmail = reader.getFirstValue("CopyrightContactEmail");
        String licensesString = reader.getFirstValue("DistributionLicense");

        // distribution license is a special case - exactly one key-value, where value is a ';' separated string of license
        // attributes.  There are mis-spellings, etc. so there is a need for a distributionLicenseAdditionalInfo field. (Ugh!)
        if (licensesString != null)
        {
            StringTokenizer tok = new StringTokenizer(licensesString, ";");
            while (tok.hasMoreTokens())
            {
                String distributionLicenseString = tok.nextToken().trim();
                int index = matchingIndex(SwordConstants.DISTIBUTION_LICENSE_STRINGS, distributionLicenseString, -1);
                if (index != -1)
                {
                    distributionLicense |= 1 << index;
                }
                else
                {
                    if (!distributionLicenseAdditionalInfo.equals(""))
                    {
                        distributionLicenseAdditionalInfo += "; ";
                    }
                    distributionLicenseAdditionalInfo += distributionLicenseString;
                }
            }
        }

        distributionSource = reader.getFirstValue("DistributionSource");
        distributionNotes = reader.getFirstValue("DistributionNotes");
        textSource = reader.getFirstValue("TextSource");
    }

    /**
     * Method setInformaticProperties.
     */
    private void setInformaticProperties()
    {
        about = reader.getFirstValue("About");
        version = reader.getFirstValue("Version");
        // PENDING(mark): History - perhaps iterate over the keys and do a startsWith("History") thing.
        // PENDING(mark): Minimum version - discuss version emulation
        category = reader.getFirstValue("Category");
        // PENDING(mark): LCSH - what on earth is this?
        lang = reader.getFirstValue("Lang");
        try
        {
            String installSizeString = reader.getFirstValue("InstallSize");
            if (installSizeString != null)
            {
                Integer.parseInt(installSizeString);
            }
        }
        catch (NumberFormatException nfe)
        {
            log.warn("There was a problem parsing the InstallSize: " + url.toString(), nfe);
        }
        // PENDING(mark): SwordVersionDate - see MinimumVersion
    }

    /**
     * Method setFeatureProperties.
     */
    private void setFeatureProperties()
    {
        Iterator features = reader.getAllValues("Feature");
        while (features.hasNext())
        {
            String featureString = (String) features.next();
            feature |= 1 << matchingIndexManual(SwordConstants.FEATURE_STRINGS, featureString);
        }
        // PENDING(mark): lexicon from
        // PENDING(mark): lexicon to
    }

    /**
     * Method setRenderingProperties.
     */
    private void setRenderingProperties()
    {
        // URGENT(joe): Change to set flags for each value
        Iterator it = reader.getAllValues("GlobalOptionFilter");
        while (it.hasNext())
        {
            String gofString = (String) it.next();
            globalOptionFilter |= 1 << matchingIndexManual(SwordConstants.GOF_STRINGS, gofString);
        }

        direction = matchingIndex(SwordConstants.DIRECTION_STRINGS, "Direction", 0);

        int sourceType = matchingIndex(SwordConstants.SOURCE_STRINGS, "SourceType", 0);
        switch (sourceType)
        {
        case SwordConstants.SOURCE_PLAINTEXT:
            filter = Filters.PLAIN_TEXT;
            break;

        case SwordConstants.SOURCE_GBF:
            filter = Filters.GBF;
            break;

        case SwordConstants.SOURCE_THML:
            filter = Filters.THML;
            break;

        case SwordConstants.SOURCE_OSIS:
            filter = Filters.OSIS;
            break;

        default:
            log.warn("SourceType set to invalid value, using default");
            filter = Filters.PLAIN_TEXT;
            break;
        }

        encoding = matchingIndex(SwordConstants.ENCODING_STRINGS, "Encoding", 0);
        if (encoding < 0)
        {
            encoding = 1; // default is Latin-1, but why not encoding is a String object?
        }

        try
        {
            String displayLevelString = reader.getFirstValue("DisplayLevel");
            if (displayLevelString != null)
            {
                displayLevel = Integer.parseInt(displayLevelString);
            }
        }
        catch (NumberFormatException nfe)
        {
            log.warn("DisplayLevel was broken: " + url.toString());
            // probably not fatal - a default is specified in documentation, but if one was specified it may cause problems
        }

        font = reader.getFirstValue("Font");
    }

    /**
     * Method setModuleAccess.
     */
    private void setModuleAccessProperties()
    {
        cipherKey = reader.getFirstValue("CipherKey");

        blockType = matchingIndex(SwordConstants.BLOCK_STRINGS, "BlockType", 0);
        
        if (isCompressed())
        {
            compressType = matchingIndex(SwordConstants.COMPRESSION_STRINGS, "CompressType");
        }
        else
        {
            compressType = -1;
        }

        try
        {
            String blockCountString = reader.getFirstValue("BlockCount");
            if (blockCountString != null)
            {
                blockCount = Integer.parseInt(blockCountString);
            }
        }
        catch (NumberFormatException nfe)
        {
            log.warn("BlockCount should be a valid integer value: " + url.toString(), nfe);
            // probably not fatal - a default is specified in documentation, but if one was specified it may cause problems
        }
    }

    /**
     * Method setMandatory.
     */
    private void setMandatoryProperties()
    {
        dataPath = reader.getFirstValue("DataPath");
        description = reader.getFirstValue("Description");
        modDrv = matchingIndex(SwordConstants.DRIVER_STRINGS, "ModDrv");
    }

    /**
     * returns the index of the array element that matches the specified string
     */
    private int matchingIndexManual(String[] array, String s)
    {
        if (s == null)
        {
            log.error("Null string not found in array: "+StringUtils.join(array, ", "));
            return -1;
        }
        
        for (int i = 0; i < array.length; i++)
        {
            if (s.equalsIgnoreCase(array[i]))
            {
                return i;
            }
        }

        // Some debug to say: no match
        log.error("String "+s+" not found in array: "+StringUtils.join(array, ", "));
        return -1;
    }

    /**
     * returns the index of the array element that matches the specified string
     */
    private int matchingIndex(String[] array, String title)
    {
        String value = reader.getFirstValue(title);

        if (value == null)
        {
            log.error("Null string (title="+title+") in array: "+StringUtils.join(array, ", ")+" while reading: "+url);
            return -1;
        }
        
        for (int i = 0; i < array.length; i++)
        {
            if (value.equalsIgnoreCase(array[i]))
            {
                return i;
            }
        }

        // Some debug to say: no match
        log.error("String "+value+" (title="+title+") not found in array: "+StringUtils.join(array, ", ")+" while reading: "+url);
        return -1;
    }

    /**
     * returns the index of the array element that matches the specified string
     */
    private int matchingIndex(String[] array, String title, int deft)
    {
        String value = reader.getFirstValue(title);
        if (value == null)
        {
            return deft;
        }

        for (int i = 0; i < array.length; i++)
        {
            if (value.equalsIgnoreCase(array[i]))
            {
                return i;
            }
        }

        // Some debug to say: no match
        log.error("String "+value+" (title="+title+") not found in array: "+StringUtils.join(array, ", ")+" while reading: "+url);
        return deft;
    }

    /**
     * @return SwordBookMetaData
     */
    public SwordBookMetaData getMetaData() throws IOException, BookException
    {
        switch (modDrv)
        {
        case SwordConstants.DRIVER_RAW_TEXT:
        case SwordConstants.DRIVER_Z_TEXT:
            return new SwordBibleMetaData(driver, this);

        case SwordConstants.DRIVER_RAW_COM:
        case SwordConstants.DRIVER_Z_COM:
        case SwordConstants.DRIVER_HREF_COM:
        case SwordConstants.DRIVER_RAW_FILES:
            return new SwordCommentaryMetaData(driver, this);
    
        case SwordConstants.DRIVER_RAW_LD:
        case SwordConstants.DRIVER_RAW_LD4:
        case SwordConstants.DRIVER_Z_LD:
            return new SwordDictionaryMetaData(driver, this);

        case SwordConstants.DRIVER_RAW_GEN_BOOK:
            // LATER(joe): how do we support books?
            log.warn("No support for book type: DRIVER_RAW_GEN_BOOK in "+this.getName());
            throw new BookException(Msg.TYPE_UNSUPPORTED, new Object[] { new Integer(modDrv), url });

        default:
            throw new BookException(Msg.TYPE_UNKNOWN, new Object[] { new Integer(modDrv), url });
        }
    }

    /**
     * Is this one of the supported book types?
     */
    public boolean isSupported()
    {
        switch (modDrv)
        {
        case SwordConstants.DRIVER_RAW_TEXT:
        case SwordConstants.DRIVER_Z_TEXT:
        case SwordConstants.DRIVER_RAW_COM:
        case SwordConstants.DRIVER_Z_COM:
        case SwordConstants.DRIVER_HREF_COM:
        case SwordConstants.DRIVER_RAW_FILES:
        case SwordConstants.DRIVER_RAW_LD:
        case SwordConstants.DRIVER_RAW_LD4:
        case SwordConstants.DRIVER_Z_LD:
            return true;

        case SwordConstants.DRIVER_RAW_GEN_BOOK:
        default:
            return false;
        }
    }

    /**
     * Get the configured method of reading a block of data from disk.
     */
    public Backend getBackend() throws BookException
    {
        int ctype = getCompressType(); 
        switch (ctype)
        {
        case SwordConstants.COMPRESSION_NONE:
            return new RawBackend();

        case SwordConstants.COMPRESSION_ZIP:
            return new GZIPBackend();

        case SwordConstants.COMPRESSION_LZSS:
            return new LZSSBackend();

        default:
            throw new BookException(Msg.COMPRESSION_UNSUPPORTED, new Object[] { new Integer(ctype) });
        }
    }

    /**
     * Returns the about.
     * @return String
     */
    public String getAbout()
    {
        return about;
    }

    /**
     * Returns the blockCount.
     * @return int
     */
    public int getBlockCount()
    {
        return blockCount;
    }

    /**
     * Returns the blockType.
     * @return int
     */
    public int getBlockType()
    {
        return blockType;
    }

    /**
     * Returns the category.
     * @return String
     */
    public String getCategory()
    {
        return category;
    }

    /**
     * Returns the cipherKey.
     * @return String
     */
    public String getCipherKey()
    {
        return cipherKey;
    }

    /**
     * Returns the compressType.
     * @return int
     */
    public int getCompressType()
    {
        return compressType;
    }

    /**
     * It this a compressed driver - do we need a valid compressType.
     */
    public boolean isCompressed()
    {
        switch (modDrv)
        {
        case SwordConstants.DRIVER_RAW_TEXT:
        case SwordConstants.DRIVER_RAW_COM:
        case SwordConstants.DRIVER_HREF_COM:
        case SwordConstants.DRIVER_RAW_FILES:
        case SwordConstants.DRIVER_RAW_LD:
        case SwordConstants.DRIVER_RAW_LD4:
        case SwordConstants.DRIVER_RAW_GEN_BOOK:
            return false;

        case SwordConstants.DRIVER_Z_TEXT:
        case SwordConstants.DRIVER_Z_COM:
        case SwordConstants.DRIVER_Z_LD:
            return true;

        default:
            throw new IllegalArgumentException(Msg.TYPE_UNKNOWN.toString());
        }
    }

    /**
     * Returns the copyrightContactAddress.
     * @return String
     */
    public String getCopyrightContactAddress()
    {
        return copyrightContactAddress;
    }

    /**
     * Returns the copyrightContactEmail.
     * @return String
     */
    public String getCopyrightContactEmail()
    {
        return copyrightContactEmail;
    }

    /**
     * Returns the copyrightContactName.
     * @return String
     */
    public String getCopyrightContactName()
    {
        return copyrightContactName;
    }

    /**
     * Returns the copyrightHolder.
     * @return String
     */
    public String getCopyrightHolder()
    {
        return copyrightHolder;
    }

    /**
     * Returns the copyrightNotes.
     * @return String
     */
    public String getCopyrightNotes()
    {
        return copyrightNotes;
    }

    /**
     * Returns the copyrightYear.
     * @return int
     */
    public String getCopyrightDate()
    {
        return copyrightDate;
    }

    /**
     * Returns the dataPath.
     * @return String
     */
    public String getDataPath()
    {
        return dataPath;
    }

    /**
     * Returns the description.
     * @return String
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Returns the direction.
     * @return int
     */
    public int getDirection()
    {
        return direction;
    }

    /**
     * Returns the displayLevel.
     * @return int
     */
    public int getDisplayLevel()
    {
        return displayLevel;
    }

    /**
     * Returns the distributionLicense - this is a 'flag type' field - the value
     * will be the result of several constants ORed. See the
     * DISTRIBUTION_LICENSE* constants in SwordConstants. It appears some
     * versions do not stick to this convention, because of this, there is an
     * additional menber distributionLicenseAdditionInfo, to store additional
     * information.
     * @see org.crosswire.jsword.book.sword.SwordConstants
     * @see #getDistributionLicenseAdditionalInfo()
     * @return int
     */
    public int getDistributionLicense()
    {
        return distributionLicense;
    }

    /**
     * Returns the distributionLicenseAdditionalInfo.  This is any information
     * that does not conform to the description of the possible values as found
     * in this document
     * (http://sword.sourceforge.net/cgi-bin/twiki/view/Swordapi/ConfFileLayout);
     * @return String
     */
    public String getDistributionLicenseAdditionalInfo()
    {
        return distributionLicenseAdditionalInfo;
    }

    /**
     * Returns the distributionNotes.
     * @return String
     */
    public String getDistributionNotes()
    {
        return distributionNotes;
    }

    /**
     * Returns the distributionSource.
     * @return String
     */
    public String getDistributionSource()
    {
        return distributionSource;
    }

    /**
     * Returns the encoding.
     * @return int
     */
    public int getEncoding()
    {
        return encoding;
    }
    
    /**
     * Returns the Charset of the module based on the encoding attribute
     * @return the charset of the module.
     */
    public String getModuleCharset()
    {
        return SwordConstants.ENCODING_STRINGS[encoding];
    }

    /**
     * Returns the feature - this is a 'flag field' and is the result of one of
     * a number of constants ORed; see SwordConstants (in particular FEATURE_*
     * fields).
     * @see org.crosswire.jsword.book.sword.SwordConstants 
     * @return int
     */
    public int getFeature()
    {
        return feature;
    }

    /**
     * Returns the font.
     * @return String
     */
    public String getFont()
    {
        return font;
    }

    /**
     * Returns the globalOptionFilter - this is a 'flag field' and is the result
     * of one of a number of constants ORed; see SwordConstants (in particular
     * GOF_* fields).
     * @see org.crosswire.jsword.book.sword.SwordConstants
     * @return int
     */
    public int getGlobalOptionFilter()
    {
        return globalOptionFilter;
    }

    /**
     * Returns the installSize.
     * @return int
     */
    public int getInstallSize()
    {
        return installSize;
    }

    /**
     * Returns the lang.
     * @return String
     */
    public String getLang()
    {
        return lang;
    }

    /**
     * Returns the minimumVersion.
     * @return String
     */
    public String getMinimumVersion()
    {
        return minimumVersion;
    }

    /**
     * Returns the modDrv.
     * @return int
     */
    public int getModDrv()
    {
        return modDrv;
    }

    /**
     * Returns the name;
     * @return int
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the sourceType.
     * @return int
     */
    public Filter getFilter()
    {
        return filter;
    }

    /**
     * Returns the textSource.
     * @return String
     */
    public String getTextSource()
    {
        return textSource;
    }

    /**
     * Returns the url.
     * @return URL
     */
    public URL getConfigFileURL()
    {
        return url;
    }

    /**
     * Returns the version.
     * @return String
     */
    public String getVersion()
    {
        return version;
    }

    /** The log stream */
    private static final Logger log = Logger.getLogger(SwordConfig.class);

    // we use java.util.Properties to save us from having to parse.
    private ConfigReader reader;
    private URL url;
    private String name;

    private SwordBookDriver driver;

    // the elements of a sword config
    // mandatory
    private String dataPath;
    private String description;
    private int modDrv;

    // required for proper module access (defaults taken from summary on wiki).
    private String cipherKey;
    private int blockType = SwordConstants.BLOCK_CHAPTER;
    private int compressType = SwordConstants.COMPRESSION_LZSS;
    private int blockCount = 200;

    // required for proper rendering
    private int globalOptionFilter;
    private int direction;
    private Filter filter;
    private int encoding;
    private int displayLevel = 1;
    private String font;

    // elements to indicate features
    private int feature;
    //private int lexiconFrom; // WHAT IS <OSIS: Lang identifier> ( Apparently something to do with ISO 639)
    //private int lexiconTo;

    // general informatic and installer elements
    private String about;
    private String version = "1.0";
    // private String[] history; // Will need to manually parse for this - not worth the hassle for now
    private String minimumVersion = "1.5.1a"; // we'll have to work out sword version compatibilty
    private String category;
    // private String LCSH; // I haven't got a clue what this is.
    private String lang = "en"; // what are the rest of the OSIS Lang identifiers?
    private int installSize; // bytes on disk
    //private Date swordVersionDate; // see minimumVersion

    // copyright and licensing related elements
    private String copyrightHolder;
    private String copyrightDate;
    private String copyrightNotes;
    private String copyrightContactName;
    private String copyrightContactAddress;
    private String copyrightContactEmail;
    private String distributionLicenseAdditionalInfo = "";
    private int distributionLicense;
    private String distributionSource;
    private String distributionNotes;
    private String textSource;
}
