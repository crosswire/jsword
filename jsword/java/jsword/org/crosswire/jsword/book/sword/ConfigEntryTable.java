package org.crosswire.jsword.book.sword;

import java.awt.ComponentOrientation;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.BookType;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.basic.AbstractBookMetaData;
import org.jdom.Element;

/**
 * A utility class for loading the entries in a Sword module's conf file.
 * Since the conf files are manually maintained, there can be all sorts
 * of errors in them. This class does robust checking and reporting.
 *
 * <p>Config file format. See also:
 * <a href="http://sword.sourceforge.net/cgi-bin/twiki/view/Swordapi/ConfFileLayout">
 * http://sword.sourceforge.net/cgi-bin/twiki/view/Swordapi/ConfFileLayout</a>
 *
 * <p> The contents of the About field are in rtf.
 * <p> \ is used as a continuation line.
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
 * @author DM Smith [dmsmith555 at yahoo dot com]
 * @version $Id$
 */
public class ConfigEntryTable
{
    /**
     * Loads a sword config from a given Reader.
     * @throws IOException
     */
    public ConfigEntryTable(Reader in, String moduleName) throws IOException
    {
        internal = moduleName;
        supported = true;

        loadFile(in);
        adjustLanguage();
        adjustBookType();
        adjustName();
        validate();
    }

    /**
     * Determines whether the Sword Module's conf is supported by JSword.
     */
    public boolean isSupported()
    {
        return supported;
    }
    /**
     * Returns an Enumeration of all the keys found in the config file.
     */
    public Iterator getKeys()
    {
        return table.keySet().iterator();
    }

    /**
     * Returns an Enumeration of all the keys found in the config file.
     */
    public ModuleType getModuleType()
    {
        return moduleType;
    }

    /**
     * Gets a particular ConfigEntry's value by its type
     * @param type of the ConfigEntry
     * @return the requested value, the default (if there is no entry) or null (if there is no default)
     */
    public Object getValue(ConfigEntryType type)
    {
        ConfigEntry ce = (ConfigEntry) table.get(type);
        if (ce != null)
        {
            return ce.getValue();
        }
        return type.getDefault();
    }

    /**
     * Sort the keys for a more meaningful presentation order.
     * TODO(DM): Replace this with a conversion of the properties to XML and then by XSLT to HTML.
     */
    public Element toOSIS()
    {
        OSISUtil.ObjectFactory factory = OSISUtil.factory();
        Element ele = factory.createTable();
        toOSIS(factory, ele, "BasicInfo", BASIC_INFO); //$NON-NLS-1$
        toOSIS(factory, ele, "LangInfo", LANG_INFO); //$NON-NLS-1$
        toOSIS(factory, ele, "LicenseInfo", COPYRIGHT_INFO); //$NON-NLS-1$
        toOSIS(factory, ele, "FeatureInfo", FEATURE_INFO); //$NON-NLS-1$
        toOSIS(factory, ele, "SysInfo", SYSTEM_INFO); //$NON-NLS-1$
        return ele;
    }

    private void loadFile(Reader in) throws IOException
    {
        // read the config file
        BufferedReader bin = new BufferedReader(in);
        loadInitials(bin);
        loadContents(bin);
    }

    private void loadContents(BufferedReader in) throws IOException
    {
        StringBuffer buf = new StringBuffer();
        while (true)
        {
            // Empty out the buffer
            buf.setLength(0);

            String line = advance(in);
            if (line == null)
            {
                break;
            }

            // skip blank lines
            if (line.length() == 0)
            {
                continue;
            }

            int eqpos = line.indexOf("="); //$NON-NLS-1$
            if (eqpos == -1)
            {
                log.warn("Expected to see '=' in " + internal + ": " + line); //$NON-NLS-1$ //$NON-NLS-2$
                continue;
            }
            
            String key = line.substring(0, eqpos).trim();
            String value = line.substring(eqpos + 1).trim();
            // Only CIPHER_KEYS that are empty are not ignored
            if (value.length() == 0 && !ConfigEntryType.CIPHER_KEY.getName().equals(key))
            {
                log.warn("Ignoring empty entry in " + internal + ": " + line); //$NON-NLS-1$ //$NON-NLS-2$
                continue;
            }

            // Create a configEntry so that the name is normalized.
            ConfigEntry configEntry = new ConfigEntry(internal, key);
            
            ConfigEntry e = (ConfigEntry) table.get(configEntry.getType());

            if (e == null)
            {
                ConfigEntryType type = configEntry.getType();
                if (type == null)
                {
                    log.warn("Ignoring unexpected entry in " + internal  + " of " + configEntry.getName()); //$NON-NLS-1$ //$NON-NLS-2$
                }
                else if (type.isSynthetic())
                {
                    log.warn("Ignoring unexpected entry in " + internal  + " of " + configEntry.getName()); //$NON-NLS-1$ //$NON-NLS-2$
                }
                else
                {
                    table.put(configEntry.getType(), configEntry);
                }
            }
            else
            {
                configEntry = e;
            }

            buf.append(value);
            getContinuation(configEntry, in, buf);

            // History is a special case it is of the form History_x.x
            // The config entry is History without the x.x.
            // We want to put x.x at the beginning of the string
            value = buf.toString();
            if (ConfigEntryType.HISTORY.equals(configEntry.getType()))
            {
                int pos = key.indexOf('_');
                value = key.substring(pos + 1) + ' ' + value;
            }

            configEntry.addValue(value);
        }
    }

    private void loadInitials(BufferedReader in) throws IOException
    {
        String initials = null;
        while (true)
        {
            String line = advance(in);
            if (line == null)
            {
                break;
            }

            if (line.charAt(0) == '[' && line.charAt(line.length() - 1) == ']')
            {
                // The conf file contains a leading line of the form [KJV]
                // This is the acronymn by which Sword refers to it.
                initials = line.substring(1, line.length() - 1);
                break;
            }
        }
        if (initials == null)
        {
            log.error("Malformed conf file for " + internal + " no initials found. Using internal of " + internal); //$NON-NLS-1$ //$NON-NLS-2$
            initials = internal;
        }
        add(ConfigEntryType.INITIALS, initials);
    }

    /**
     * Get continuation lines, if any.
     */
    private void getContinuation(ConfigEntry configEntry, BufferedReader bin, StringBuffer buf) throws IOException
    {
        for (String line = advance(bin); line != null; line = advance(bin))
        {
            int length = buf.length();

            // Look for bad data as this condition did exist
            boolean continuation_expected = length > 0 && buf.charAt(length - 1) == '\\';

            if (continuation_expected)
            {
                // delete the continuation character
                buf.deleteCharAt(length - 1);
            }

            if (isKeyLine(line))
            {
                if (continuation_expected)
                {
                    log.warn("Continuation followed by key for " + configEntry.getName() + " in " + internal + ": " + line); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                }

                backup(line);
                break;
            }
            else if (!continuation_expected)
            {
                log.warn("Line without previous continuation for " + configEntry.getName()  + " in " + internal + ": " + line); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }

            if (!configEntry.allowsContinuation())
            {
                log.warn("Ignoring unexpected additional line for " + configEntry.getName()  + " in " + internal + ": " + line); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$                
            }
            else
            {
                buf.append(line);
            }
        }
    }

    /**
     * Get the next line from the input
     * @param bin The reader to get data from
     * @return the next line
     * @throws IOException
     */
    private String advance(BufferedReader bin) throws IOException
    {
        // Was something put back? If so, return it.
        if (readahead != null)
        {
            String line = readahead;
            readahead = null;
            return line;
        }

        // Get the next non-blank, non-comment line
        for (String line = bin.readLine(); line != null; line = bin.readLine())
        {
            // Remove trailing whitespace
            line = line.trim();

            int length = line.length();

            // skip blank and comment lines
            if (length != 0 && line.charAt(0) != '#')
            {
                return line;
            }
        }
        return null;
    }

    /**
     * Read too far ahead and need to return a line.
     */
    private void backup(String oops)
    {
        if (oops.length() > 0)
        {
            readahead = oops;
        }
        else
        {
            // should never happen
            log.error("Backup an empty string for " + internal); //$NON-NLS-1$
        }
    }

    /**
     * Does this line of text represent a key/value pair?
     */
    private boolean isKeyLine(String line)
    {
        return line.indexOf('=') != -1;
    }

    /**
     * A helper to create/replace a value for a given type.
     * @param type
     * @param aValue
     */
    private void add(ConfigEntryType type, String aValue)
    {
        table.put(type, new ConfigEntry(internal, type, aValue));
    }

    private void adjustLanguage()
    {
        // Java thinks it is LtoR but it is stated to be something else
        String dir = (String) getValue(ConfigEntryType.DIRECTION);
        String newDir = dir == null ? (String) ConfigEntryType.DIRECTION.getDefault() : dir;

        String langEntry = (String) getValue(ConfigEntryType.LANG);
        String lang = AbstractBookMetaData.getLanguage(internal, langEntry);
        add(ConfigEntryType.LANGUAGE, lang);

        // This returns ComponentOrientation.LEFT_TO_RIGHT if
        // it does not know what it is.
        boolean leftToRight = true;
        if (langEntry != null)
        {
            leftToRight = ComponentOrientation.getOrientation(new Locale(langEntry)).isLeftToRight();
        }

        String langFromEntry = (String) getValue(ConfigEntryType.GLOSSARY_FROM);
        String langToEntry = (String) getValue(ConfigEntryType.GLOSSARY_TO);
        
        if (langFromEntry != null || langToEntry != null)
        {
            String langFrom = AbstractBookMetaData.getLanguage(internal, langFromEntry);
            add(ConfigEntryType.LANGUAGE_FROM, langFrom);
            String langTo = AbstractBookMetaData.getLanguage(internal, langToEntry);
            add(ConfigEntryType.LANGUAGE_TO, langTo);
            boolean fromLeftToRight = true;
            boolean toLeftToRight = true;
            
            if (langFromEntry == null)
            {
                log.warn("Missing data for " + internal + ". Assuming " + ConfigEntryType.GLOSSARY_FROM.getName() + "=" + AbstractBookMetaData.DEFAULT_LANG_CODE);  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
            else
            {
                fromLeftToRight = ComponentOrientation.getOrientation(new Locale(langFromEntry)).isLeftToRight();
            }

            if (langToEntry == null)
            {
                log.warn("Missing data for " + internal + ". Assuming " + ConfigEntryType.GLOSSARY_TO.getName() + "=" + AbstractBookMetaData.DEFAULT_LANG_CODE);  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
            else
            {
                toLeftToRight = ComponentOrientation.getOrientation(new Locale(langToEntry)).isLeftToRight();
            }
            
            // At least one of the two languages should match the lang entry
            if (!langFrom.equals(lang) && !langTo.equals(lang))
            {
                log.error("Data error in " + internal + ". Neither " + ConfigEntryType.GLOSSARY_FROM.getName() + " or " + ConfigEntryType.GLOSSARY_FROM.getName() + " match " + ConfigEntryType.LANG.getName());  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            }
            
            if (fromLeftToRight != toLeftToRight)
            {
                newDir = ConfigEntryType.DIRECTION_BIDI;
            }
            else if (fromLeftToRight)
            {
                newDir = ConfigEntryType.DIRECTION_LTOR;
            }
            else
            {
                newDir = ConfigEntryType.DIRECTION_RTOL;
            }
        }
        else
        {
            if (leftToRight)
            {
                newDir = ConfigEntryType.DIRECTION_LTOR;
            }
            else
            {
                newDir = ConfigEntryType.DIRECTION_RTOL;
            }
        }

        if (newDir.equals(ConfigEntryType.DIRECTION_LTOR))
        {
            if (dir != null)
            {
                if (!newDir.equals(dir))
                {
                    log.warn("Fixing data for " + internal + ". Changing " + ConfigEntryType.DIRECTION.getName() + "=" + dir + " to " + newDir); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                }
                table.remove(ConfigEntryType.DIRECTION);
            }
        }
        else if (!newDir.equals(dir))
        {
            if (dir == null)
            {
                log.warn("Fixing data for " + internal + ". Adding " + ConfigEntryType.DIRECTION.getName() + "=" + newDir); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$                
            }
            else
            {
                log.warn("Fixing data for " + internal + ". Changing " + ConfigEntryType.DIRECTION.getName() + "=" + dir + " to " + newDir); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$                   
            }
            add(ConfigEntryType.DIRECTION, newDir);
        }
    }

    private void adjustBookType()
    {
        // From the config map, extract the important bean properties
        String modTypeName = (String) getValue(ConfigEntryType.MOD_DRV);
        if (modTypeName == null)
        {
            log.error("Book not supported: malformed conf file for " + internal + " no " + ConfigEntryType.MOD_DRV.getName() + " found"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            supported = false;
            return;
        }

        moduleType = ModuleType.getModuleType(modTypeName);
        if (getModuleType() == null)
        {
            log.error("Book not supported: malformed conf file for " + internal + " no module type found"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            supported = false;
            return;
        }

        BookType type = getModuleType().getBookType();
        if (type == null)
        {
            // We plan to add RawGenBook at a later time. So we don't need to be reminded all the time.
            if (!modTypeName.equals("RawGenBook")) //$NON-NLS-1$
            {
                log.debug("Book not supported: " + internal + " because missing book type for ModuleType (" + modTypeName + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
            supported = false;
            return;
        }

        add(ConfigEntryType.KEY, type.toString());
    }

    private void adjustName()
    {
        // If there is no name then use the internal name
        if (table.get(ConfigEntryType.DESCRIPTION) == null)
        {
            log.error("Malformed conf file for " + internal + " no " + ConfigEntryType.DESCRIPTION.getName() + " found. Using internal of " + internal); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            add(ConfigEntryType.DESCRIPTION, internal);
        }
    }

    /**
     * Determine which books are not supported.
     * Also, report on problems.
     */
    private void validate()
    {
        // Only locked modules that have a key can be used.
        String cipher = (String) getValue(ConfigEntryType.CIPHER_KEY);
        if (cipher != null && cipher.length() == 0)
        {
            log.debug("Book not supported: " + internal + " because it is locked and there is no key."); //$NON-NLS-1$ //$NON-NLS-2$
            supported = false;
            return;
        }
    }

    /**
     * Build an ordered map so that it displays in a consistent order.
     */
    private void toOSIS(OSISUtil.ObjectFactory factory, Element ele, String aTitle, ConfigEntryType[] category)
    {
        Element title = null;
        for (int i = 0; i < category.length; i++)
        {
            ConfigEntry entry = (ConfigEntry) table.get(category[i]);
            Element configElement = null;
            
            if (entry != null)
            {
                configElement = entry.toOSIS();
            }

            if (title == null && configElement != null)
            {
                // I18N(DMS): use aTitle to lookup translation. 
                title = factory.createHeader();
                title.addContent(aTitle); 
                ele.addContent(title);
            }

            if (configElement != null)
            {
                ele.addContent(configElement);
            }
        }
    }

    /**
     * These are the elements that JSword requires.
     * They are a superset of those that Sword requires.
     */
    public static final ConfigEntryType[] REQUIRED =
    {
        ConfigEntryType.INITIALS,
        ConfigEntryType.DESCRIPTION,
        ConfigEntryType.KEY,
        ConfigEntryType.DATA_PATH,
        ConfigEntryType.MOD_DRV,
    };

    public static final ConfigEntryType[] BASIC_INFO =
    {
        ConfigEntryType.INITIALS,
        ConfigEntryType.DESCRIPTION,
        ConfigEntryType.KEY,
        ConfigEntryType.LCSH,
        ConfigEntryType.CATEGORY,
        ConfigEntryType.VERSION,
        ConfigEntryType.SWORD_VERSION_DATE,
        ConfigEntryType.HISTORY,
    };

    public static final ConfigEntryType[] LANG_INFO =
    {
        ConfigEntryType.LANGUAGE,
        ConfigEntryType.LANG,
        ConfigEntryType.LANGUAGE_FROM,
        ConfigEntryType.GLOSSARY_FROM,
        ConfigEntryType.LANGUAGE_TO,
        ConfigEntryType.GLOSSARY_TO,
    };

    public static final ConfigEntryType[] COPYRIGHT_INFO =
    {
        ConfigEntryType.ABOUT,
        ConfigEntryType.SHORT_PROMO,
        ConfigEntryType.DISTRIBUTION_LICENSE,
        ConfigEntryType.DISTRIBUTION_NOTES,
        ConfigEntryType.DISTRIBUTION_SOURCE,
        ConfigEntryType.SHORT_COPYRIGHT,
        ConfigEntryType.COPYRIGHT,
        ConfigEntryType.COPYRIGHT_DATE,
        ConfigEntryType.COPYRIGHT_HOLDER,
        ConfigEntryType.COPYRIGHT_CONTACT_NAME,
        ConfigEntryType.COPYRIGHT_CONTACT_ADDRESS,
        ConfigEntryType.COPYRIGHT_CONTACT_EMAIL,
        ConfigEntryType.COPYRIGHT_NOTES,
        ConfigEntryType.TEXT_SOURCE,
    };

    public static final ConfigEntryType[] FEATURE_INFO =
    {
        ConfigEntryType.FEATURE,
        ConfigEntryType.GLOBAL_OPTION_FILTER,
        ConfigEntryType.FONT,
    };

    public static final ConfigEntryType[] SYSTEM_INFO =
    {
        ConfigEntryType.DATA_PATH,
        ConfigEntryType.MOD_DRV,
        ConfigEntryType.SOURCE_TYPE,
        ConfigEntryType.BLOCK_TYPE,
        ConfigEntryType.COMPRESS_TYPE,
        ConfigEntryType.ENCODING,
        ConfigEntryType.MINIMUM_VERSION,
        ConfigEntryType.DIRECTION,
    };

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(ConfigEntryTable.class);

    /**
     * True if this module's config type can be used by JSword.
     */
    private boolean supported;

    /**
     * The ModuleType for this ConfigEntry
     */
    private ModuleType moduleType;

    /**
     * A map of lists of known config entries.
     */
    private Map table = new HashMap();

    /**
     * The original name of this config file from mods.d.
     * This is only used for managing warnings and errors
     */
    private String internal;

    /**
     * A helper for the reading of the conf file.
     */
    private String readahead;
}
