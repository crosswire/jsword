
package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.crosswire.jsword.book.Book;
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
 * @see docs.Licence
 * @author Mark Goodwin [mark at thorubio dot org]
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class SwordConfig
{
    /**
     * Loads a sword config from a given URL.
     */
    public SwordConfig(File parent, String bookdir) throws IOException
    {
        URL url = new File(parent, bookdir).toURL();

        this.url = url;
        this.name = bookdir.substring(0, bookdir.indexOf(".conf"));;
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
            log.warn("No description could be found in the config: " + url.toString());
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
                int index = matchingIndex(SwordConstants.DISTIBUTION_LICENSE_STRINGS, distributionLicenseString);
                if (index != -1)
                    distributionLicense |= 1 << index;
                else
                {
                    if (!distributionLicenseAdditionalInfo.equals(""))
                        distributionLicenseAdditionalInfo += "; ";
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
                Integer.parseInt(installSizeString);
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
            feature |= 1 << matchingIndex(SwordConstants.FEATURE_STRINGS, featureString);
        }
        // PENDING(mark): lexicon from
        // PENDING(mark): lexicon to
    }

    /**
     * Method setRenderingProperties.
     */
    private void setRenderingProperties()
    {
        // PENDING(mark): Change to set flags for each value
        Iterator it = reader.getAllValues("GlobalOptionFilter");
        while (it.hasNext())
        {
            String gofString = (String) it.next();
            globalOptionFilter |= 1 << matchingIndex(SwordConstants.GOF_STRINGS, gofString);
        }

        String directionString = reader.getFirstValue("Direction");
        direction = matchingIndex(SwordConstants.DIRECTION_STRINGS, directionString);

        String sourceTypeString = reader.getFirstValue("SourceType");
        int sourceType = matchingIndex(SwordConstants.SOURCE_STRINGS, sourceTypeString);
        switch (sourceType)
        {
        case SwordConstants.SOURCE_PLAINTEXT:
            filter = Filters.PLAIN_TEXT;
            break;

        case SwordConstants.SOURCE_GBF:
            log.debug("Found GBF source: "+getName()+" desire="+(++desire_gbf));
            filter = Filters.GBF;
            break;

        case SwordConstants.SOURCE_THML:
            log.debug("Found THML source: "+getName()+" desire="+(++desire_thml));
            filter = Filters.THML;
            break;

        case SwordConstants.SOURCE_OSIS:
            log.debug("Found OSIS source: "+getName()+" desire="+(++desire_osis));
            filter = Filters.OSIS;
            break;

        default:
            log.warn("SourceType set to invalid value: " + sourceTypeString);
            filter = Filters.PLAIN_TEXT;
            break;

        }

        String encodingString = reader.getFirstValue("Encoding");
        encoding = matchingIndex(SwordConstants.ENCODING_STRINGS, encodingString);

        try
        {
            String displayLevelString = reader.getFirstValue("DisplayLevel");
            if (displayLevelString != null)
                displayLevel = Integer.parseInt(displayLevelString);
        }
        catch (NumberFormatException nfe)
        {
            log.warn("DisplayLevel was broken: " + url.toString());
            // probably not fatal - a default is specified in documentation, but if one was specified it may cause problems
        }

        font = reader.getFirstValue("Font");
    }

    private static int desire_gbf = 0;
    private static int desire_thml = 0;
    private static int desire_osis = 0;

    /**
     * Method setModuleAccess.
     */
    private void setModuleAccessProperties()
    {
        cipherKey = reader.getFirstValue("CipherKey");
        String blockString = reader.getFirstValue("BlockType");
        blockType = matchingIndex(SwordConstants.BLOCK_STRINGS, blockString);
        String compressString = reader.getFirstValue("CompressType");
        compressType = matchingIndex(SwordConstants.COMPRESSION_STRINGS, compressString);
        try
        {
            String blockCountString = reader.getFirstValue("BlockCount");
            if (blockCountString != null)
                blockCount = Integer.parseInt(blockCountString);
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
        String modDrvString = reader.getFirstValue("ModDrv");
        modDrv = matchingIndex(SwordConstants.DRIVER_STRINGS, modDrvString);
    }

    /**
     * returns the index of the array element that matches the specified string
     */
    private int matchingIndex(String[] array, String s)
    {
        int matchNo = -1;
        if (array == null || s == null)
            return -1;
        for (int i = 0; i < array.length; i++)
        {
            if (s.equalsIgnoreCase(array[i]))
                matchNo = i;
        }
        return matchNo;
    }

    /**
     * @return SwordBookMetaData
     */
    public SwordBookMetaData getMetaData() throws IOException, BookException
    {
        int type = getModDrv();
        switch (type)
        {
        case SwordConstants.DRIVER_RAW_TEXT:
            return new SwordBibleMetaData(this)
            {
                public Book createBook() throws BookException
                {
                    return new RawSwordBible(this, SwordConfig.this);
                }
            };

        case SwordConstants.DRIVER_Z_TEXT:
            int ctype = getCompressType(); 
            if (ctype == SwordConstants.COMPRESSION_LZSS)
            {
                return new SwordBibleMetaData(this)
                {
                    public Book createBook() throws BookException
                    {
                        return new LZSSCompressedSwordBible(this, SwordConfig.this);
                    }
                };
            }
            else if (ctype == SwordConstants.COMPRESSION_ZIP)
            {
                return new SwordBibleMetaData(this)
                {
                    public Book createBook() throws BookException
                    {
                        return new ZipCompressedSwordBible(this, SwordConfig.this);
                    }
                };
            }
            else
            {
                throw new BookException("Unsupported compression type: "+ctype);
            }

        case SwordConstants.DRIVER_RAW_COM:
            return new SwordCommentaryMetaData(this)
            {
                public Book createBook() throws BookException
                {
                    return new RawSwordCommentary(this, SwordConfig.this);
                }
            };

        case SwordConstants.DRIVER_Z_COM:
            return new SwordCommentaryMetaData(this)
            {
                public Book createBook() throws BookException
                {
                    return new CompressedSwordCommentary(this, SwordConfig.this);
                }
            };
    
        case SwordConstants.DRIVER_HREF_COM:
            return new SwordCommentaryMetaData(this)
            {
                public Book createBook() throws BookException
                {
                    return new HRefSwordCommentary(this, SwordConfig.this);
                }
            };

        case SwordConstants.DRIVER_RAW_FILES:
            return new SwordCommentaryMetaData(this)
            {
                public Book createBook() throws BookException
                {
                    return new RawFilesSwordCommentary(this, SwordConfig.this);
                }
            };

        case SwordConstants.DRIVER_RAW_LD:
            return new SwordDictionaryMetaData(this)
            {
                public Book createBook() throws BookException
                {
                    return new RawSwordDictionary(this, SwordConfig.this);
                }
            };

        case SwordConstants.DRIVER_RAW_LD4:
            return new SwordDictionaryMetaData(this)
            {
                public Book createBook() throws BookException
                {
                    return new LD4SwordDictionary(this, SwordConfig.this);
                }
            };
    
        case SwordConstants.DRIVER_Z_LD:
            return new SwordDictionaryMetaData(this)
            {
                public Book createBook() throws BookException
                {
                    return new CompressedSwordDictionary(this, SwordConfig.this);
                }
            };

        case SwordConstants.DRIVER_RAW_GEN_BOOK:
            // PENDING(joe): what is this?
            log.warn("No support for book type: DRIVER_RAW_GEN_BOOK in "+this.getName()+" desire="+(++desire_rawgenbook));
            throw new BookException("Unsupported type: "+type);

        default:
            throw new BookException("Unknown type: "+type);
        }
    }

    private static int desire_rawgenbook = 0;

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
    public URL getUrl()
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
    protected static Logger log = Logger.getLogger(SwordConfig.class);

    // we use java.util.Properties to save us from having to parse.
    private ConfigReader reader;
    private URL url;
    private String name;

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
