package org.crosswire.jsword.book.sword;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.filter.Filter;
import org.crosswire.jsword.book.filter.FilterFactory;

/**
 * A utility class for loading and representing Sword module configs.
 * 
 * <p>Config file format. See also:
 * <a href="http://sword.sourceforge.net/cgi-bin/twiki/view/Swordapi/ConfFileLayout">
 * http://sword.sourceforge.net/cgi-bin/twiki/view/Swordapi/ConfFileLayout</a>
 * 
 * <p>Keys that might be available that we are ignoring for now:
 * <li>About: single value string, unknown use
 * <li>BlockCount: single value integer, unknown use, some indications that we ought to be using it
 * <li>Category: single value string, unknown use
 * <li>CipherKey: single value string, for encryption
 * <li>CopyrightContactAddress: single value string, unknown use
 * <li>CopyrightContactEmail: single value string, unknown use
 * <li>CopyrightContactName: single value string, unknown use
 * <li>CopyrightDate: single value string, unknown use
 * <li>CopyrightHolder: single value string, unknown use
 * <li>CopyrightNotes: single value string, unknown use
 * <li>Direction: single value choice, from SwordConstants.DIRECTION_STRINGS we should probably use it?
 * <li>DisplayLevel: single value integer, unknown use, some indications that we ought to be using it
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
 * <li>DistributionNotes: single value string, unknown use
 * <li>DistributionSource: single value string, unknown use
 * <li>GlobalOptionFilter: multiple value flagset, from SwordConstants.GOF_STRINGS
 * <li>History: multiple values starting with History, some sort of change-log?
 * <li>InstallSize: single value integer, the installed size (in bytes?)
 * <li>Feature: multiple value flagset, from SwordConstants.FEATURE_STRINGS
 * <li>Font: single value string
 * <li>Lang: single value string, defaults to en, the language of the module
 * <li>LCSH: unknown
 * <li>LexiconFrom: unknown
 * <li>LexiconTo: unknown
 * <li>MinimumVersion: single value version number, lowest sword c++ version that can read this module
 * <li>SwordVersionDate: unknown
 * <li>TextSource: single value string, unknown use
 * <li>Version: single value string, unknown use
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
    public SwordConfig(File file) throws IOException
    {
        BufferedReader in = new BufferedReader(new FileReader(file));

        while (true)
        {
            String line = in.readLine();
            if (line == null)
            {
                break;
            }

            parseLine(line);
        }
        
        for (Iterator kit = getKeys(); kit.hasNext(); )
        {
            String key = (String) kit.next();
            List list = (List) table.get(key);
            
            StringBuffer combined = new StringBuffer();
            for (Iterator vit = list.iterator(); vit.hasNext(); )
            {
                String element = (String) vit.next();
                combined.append(element);
            }

            prop.setProperty(key, combined.toString());
        }
    }

    /**
     * The full read-only single key version of this config file
     */
    public Properties getProperties()
    {
        return prop;
    }
    
    /**
     * Returns an Enumeration of all the keys found in the config file.
     */
    public Iterator getKeys()
    {
        return table.keySet().iterator();
    }

    /**
     * Returns only one value for the key (for cases where only one value is expected).
     */
    public String getFirstValue(String key)
    {
        ArrayList list = (ArrayList) table.get(key);
        if (list == null)
        {
            return null;
        }

        return (String) list.get(0);
    }

    /**
     * Returns all values for the key (for cases where many values are expected).
     */
    public Iterator getAllValues(String key)
    {
        ArrayList list = (ArrayList) table.get(key);
        if (list == null)
        {
            return Collections.EMPTY_LIST.iterator();
        }

        return list.iterator();
    }

    /**
     * returns the index of the array element that matches the specified string
     */
    public int matchingIndex(String[] array, String title)
    {
        String value = getFirstValue(title);

        if (value == null)
        {
            log.error("Null string (title="+title+") in array: "+StringUtils.join(array, ", "));
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
        log.error("String "+value+" (title="+title+") not found in array: "+StringUtils.join(array, ", "));
        return -1;
    }

    /**
     * returns the index of the array element that matches the specified string
     */
    public int matchingIndex(String[] array, String title, int deft)
    {
        String value = getFirstValue(title);
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
        log.error("String "+value+" (title="+title+") not found in array: "+StringUtils.join(array, ", "));
        return deft;
    }

    /**
     * Is this one of the supported book types?
     */
    protected boolean isSupported()
    {
        switch (getModDrv())
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
     * Returns the description.
     * @return String
     */
    protected String getDescription()
    {
        return getFirstValue("Description");
    }

    /**
     * Returns the Charset of the module based on the encoding attribute
     * @return the charset of the module.
     */
    protected String getModuleCharset()
    {
        // PENDING(joe): why the messing with defaults?
        int encoding = matchingIndex(SwordConstants.ENCODING_STRINGS, "Encoding", 0);
        if (encoding < 0)
        {
            encoding = 1; // default is Latin-1, but why not encoding is a String object?
        }
        return SwordConstants.ENCODING_JAVA[encoding];
    }

    /**
     * Returns the modDrv.
     * @return int
     */
    protected int getModDrv()
    {
        return matchingIndex(SwordConstants.DRIVER_STRINGS, "ModDrv");
    }

    /**
     * Returns the sourceType.
     * @return int
     */
    protected Filter getFilter()
    {
        String sourcetype = getFirstValue("SourceType");
        return FilterFactory.getFilter(sourcetype);
    }

    /**
     * Parse a single line.
     */
    private void parseLine(String line)
    {
        // don't use a tokenizer - a value may have an =
        int eqpos = line.indexOf('=');
        if (eqpos >= 0 && eqpos < line.length() - 1)
        {
            String key = line.substring(0, eqpos).trim();
            if (key.length() > 0)
            {
                String value = line.substring(eqpos + 1);
                // check to see if there already values for this key...
                List list = (ArrayList) table.get(key);
                if (list != null)
                {
                    list.add(value);
                }
                else
                {
                    list = new ArrayList();
                    list.add(value);
                    table.put(key, list);
                }
            }
        }
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(SwordConfig.class);

    /**
     * A map of lists of known keys
     */
    private Map table = new HashMap();

    /**
     * The single key version of the input file
     */
    private Properties prop = new Properties();
}
