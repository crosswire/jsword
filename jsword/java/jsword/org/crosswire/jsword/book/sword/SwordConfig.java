package org.crosswire.jsword.book.sword;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.jsword.book.filter.Filter;
import org.crosswire.jsword.book.filter.FilterFactory;

/**
 * A utility class for loading and representing Sword module configs.
 * 
 * <p>Config file format. See also:
 * <a href="http://sword.sourceforge.net/cgi-bin/twiki/view/Swordapi/ConfFileLayout">
 * http://sword.sourceforge.net/cgi-bin/twiki/view/Swordapi/ConfFileLayout</a>
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
     * Loads a sword config from a given File.
     */
    public SwordConfig(File file, String internal) throws IOException
    {
        this(new FileReader(file), internal);
    }

    /**
     * Loads a sword config from a given Reader.
     */
    public SwordConfig(Reader in, String internal) throws IOException
    {
        this.internal = internal;

        BufferedReader bin = new BufferedReader(in);

        while (true)
        {
            String line = bin.readLine();
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
     * Save this config file to a URL
     * @param dest The URL to save the data to
     */
    public void save(URL dest) throws IOException
    {
        PrintWriter out = new PrintWriter(NetUtil.getOutputStream(dest));

        for (Iterator kit = getKeys(); kit.hasNext(); )
        {
            String key = (String) kit.next();
            List list = (List) table.get(key);

            for (Iterator eit = list.iterator(); eit.hasNext(); )
            {
                String value = (String) eit.next();

                out.print(key);
                out.print("=");
                out.println(value);
            }
        }

        out.close();
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
    public String getFirstValue(ConfigEntry key)
    {
        ArrayList list = (ArrayList) table.get(key.getName());
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
    public int matchingIndex(String[] array, ConfigEntry title)
    {
        String value = getFirstValue(title);

        if (value == null)
        {
            log.error("Null string (title="+title.getName()+") in array: "+StringUtils.join(array, ", "));
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
        log.error("String "+value+" (title="+title.getName()+") not found in array: "+StringUtils.join(array, ", "));
        return -1;
    }

    /**
     * returns the index of the array element that matches the specified string
     */
    public int matchingIndex(String[] array, ConfigEntry title, int deft)
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
        log.error("String "+value+" (title="+title.getName()+") not found in array: "+StringUtils.join(array, ", "));
        return deft;
    }

    /**
     * Is this one of the supported book types?
     */
    public boolean isSupported()
    {
        return getModDrv().getBookType() != null;
    }

    /**
     * Returns the description.
     * @return String
     */
    public String getDescription()
    {
        return getFirstValue(ConfigEntry.DESCRIPTION);
    }

    /**
     * Returns the Charset of the module based on the encoding attribute
     * @return the charset of the module.
     */
    public String getModuleCharset()
    {
        int encoding = matchingIndex(SwordConstants.ENCODING_STRINGS, ConfigEntry.ENCODING, SwordConstants.ENCODING_LATIN1);
        
        // There was code here that said:
        // if (encoding < 0)
        //  encoding = SwordConstants.ENCODING_UTF8;
        // However this would never trigger since we give a default above

        return SwordConstants.ENCODING_JAVA[encoding];
    }

    /**
     * Returns the Module Type.
     */
    public ModuleType getModDrv()
    {
        String value = getFirstValue(ConfigEntry.MOD_DRV);
        
        if (value == null)
        {
            log.error("Null string not a valid ModuleType.");
            return null;
        }

        ModuleType type = ModuleType.getModuleType(value);        
        if (type == null)
        {
            log.error("String "+value+" not a valid ModuleType.");
        }

        return type;
    }

    /**
     * Returns the sourceType.
     * @return int
     */
    public Filter getFilter()
    {
        String sourcetype = getFirstValue(ConfigEntry.SOURCE_TYPE);
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
            
            /*
            if (ConfigEntry.getConfigEntry(key) == null)
            {
                log.warn("unknown config entry: "+key);
            }
            */

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
     * @return Returns the internal name of this module.
     */
    public String getInternalName()
    {
        return internal;
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

    /**
     * The original name of this config file from mods.d
     */
    private String internal = null;
}
