package org.crosswire.jsword.book.sword;

import java.awt.ComponentOrientation;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;

import org.crosswire.common.util.CWClassLoader;
import org.crosswire.common.util.Histogram;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.BookType;
import org.crosswire.jsword.book.IndexStatus;
import org.crosswire.jsword.book.basic.AbstractBookMetaData;
import org.crosswire.jsword.book.filter.Filter;
import org.crosswire.jsword.book.filter.FilterFactory;

/**
 * A utility class for loading and representing Sword module configs.
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
public class SwordBookMetaData extends AbstractBookMetaData
{
    /**
     * Loads a sword config from a given File.
     * The returned BookMetaData object will not be associated with a Book so
     * setBook() should be called before getBook() is expected to return
     * anything other than null.
     */
    public SwordBookMetaData(File file, String internal) throws IOException
    {
        this(new FileReader(file), internal);
    }

    /**
     * Loads a sword config from a given Reader.
     * The returned BookMetaData object will not be associated with a Book so
     * setBook() should be called before getBook() is expected to return
     * anything other than null.
     */
    public SwordBookMetaData(Reader in, String internal) throws IOException
    {
        this.book = null;
        this.internal = internal;
        this.supported = true;

        // read the config file
        BufferedReader bin = new BufferedReader(in);
        StringBuffer buf = new StringBuffer();
        while (true)
        {
            // Empty out the buffer
            buf.setLength(0);

            String line = advance(bin);
            if (line == null)
            {
                break;
            }

            int length = line.length();
            if (length > 0)
            {
                buf.append(line);
            }
            else
            {
                buf.append(' ');
            }

            getContinuation(bin, buf);

            parseEntry(buf.toString());
        }

        // merge entries into properties file
        for (Iterator kit = getKeys(); kit.hasNext();)
        {
            String key = (String) kit.next();

            // Only copy the valid ones
            if (ConfigEntry.fromString(key) == null)
            {
                continue;
            }

            List list = (List) table.get(key);

            StringBuffer combined = new StringBuffer();
            boolean appendSeparator = false;
            for (Iterator vit = list.iterator(); vit.hasNext();)
            {
                String element = (String) vit.next();
                if (appendSeparator)
                {
                    combined.append('\n');
                }
                combined.append(element);
                appendSeparator = true;
            }

            prop.put(key, combined.toString());
        }

        // set the key property file entries
        prop.put(KEY_INITIALS, initials);

        validate();

        if (!isSupported())
        {
            return;
        }

        // From the config map, extract the important bean properties
        String modTypeName = getProperty(ConfigEntry.MOD_DRV);
        if (modTypeName != null)
        {
            mtype = ModuleType.getModuleType(modTypeName);
        }

        // The default compression type is BOOK.
        // This probably a data problem as it only occurs with webstersdict
        String cStr = getProperty(ConfigEntry.COMPRESS_TYPE);
        String blocking = getProperty(ConfigEntry.BLOCK_TYPE);
        if (cStr != null && blocking == null)
        {
            log.warn("Fixing data for " + internal + ". Adding BlockType of BOOK"); //$NON-NLS-1$ //$NON-NLS-2$
            fixEntry(ConfigEntry.BLOCK_TYPE, BlockType.BLOCK_BOOK.toString());
        }

        String lang = getProperty(ConfigEntry.LANG);
        prop.put(KEY_LANGUAGE, getLanguage(lang));
        if (lang != null) //$NON-NLS-1$
        {
            // This returns ComponentOrientation.LEFT_TO_RIGHT if
            // it does not know what it is.
            boolean leftToRight = ComponentOrientation.getOrientation(new Locale(lang)).isLeftToRight();
            String dir = getProperty(ConfigEntry.DIRECTION);
            // Java thinks it is RtoL but it is not stated to be such in the conf.
            if (!leftToRight && dir == null)
            {
                log.warn("Fixing data for " + internal + ". Adding DIRECTION=RtoL"); //$NON-NLS-1$ //$NON-NLS-2$
                fixEntry(ConfigEntry.DIRECTION, SwordConstants.DIRECTION_STRINGS[SwordConstants.DIRECTION_RTOL]);
            }
            // Java thinks it is LtoR but it is stated to be something else
            String ltor = SwordConstants.DIRECTION_STRINGS[SwordConstants.DIRECTION_LTOR];
            if (leftToRight && dir != null && !dir.equals(ltor))
            {
                log.warn("Fixing data for " + internal + ". Changing DIRECTION=RtoL to LtoR"); //$NON-NLS-1$ //$NON-NLS-2$
                fixEntry(ConfigEntry.DIRECTION, ltor);
            }
        }

        if (mtype != null)
        {
            BookType type = mtype.getBookType();
            prop.put(KEY_TYPE, type != null ? type.toString() : ""); //$NON-NLS-1$
        }

        // now that prop is fully populated we can organize it.
        organize();
    }

    /**
     * Get the language name from the language code. Note, this code does not support dialects.
     * @param iso639Code
     * @return the name of the language
     */
    private String getLanguage(String iso639Code)
    {
        String lookup = iso639Code;
        if (lookup == null || lookup.length() == 0)
        {
            log.warn("Book " + internal + " named " + getName() + " has no language specified. Assuming English."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            return getLanguage(DEFAULT_LANG_CODE);
        }

        if (lookup.indexOf('_') != -1)
        {
            String[] locale = StringUtil.split(lookup, '_');
            return getLanguage(locale[0]);
        }

        char firstLangChar = lookup.charAt(0);
        // If the language begins w/ an x then it is "Undetermined"
        // Also if it is not a 2 or 3 character code then it is not a valid
        // iso639 code.
        if (firstLangChar == 'x' || firstLangChar == 'X' || lookup.length() > 3)
        {
            return getLanguage(UNKNOWN_LANG_CODE);
        }

        try
        {
            return languages.getString(lookup);
        }
        catch (MissingResourceException e)
        {
            log.error("Not a valid language code:" + iso639Code + " in book " + internal); //$NON-NLS-1$ //$NON-NLS-2$
            return getLanguage(UNKNOWN_LANG_CODE);
        }
    }

    /**
     * Save this config file to a URL
     * @param dest The URL to save the data to
     */
    public void save(URL dest)
    {
        PrintWriter out = null;
        try
        {
            out = new PrintWriter(NetUtil.getOutputStream(dest));
            out.print(data);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (out != null)
            {
                out.close();
            }
        }
    }

    /**
     * Returns an Enumeration of all the keys found in the config file.
     */
    public Iterator getKeys()
    {
        return table.keySet().iterator();
    }

    /**
     * returns the index of the array element that matches the specified string
     */
    private int matchingIndex(String[] array, ConfigEntry title, int deft)
    {
        String value = getProperty(title);
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
        log.error("String " + value + " (title=" + title + ") not found in array: " + StringUtil.join(array, ", ")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        return deft;
    }

    /**
     * Sort the keys for a more meaningful presentation order.
     * TODO(DM): Replace this with a conversion of the properties to XML and then by XSLT to HTML.
     */
    private void organize()
    {
        Map orderedMap = new LinkedHashMap();
        organize(orderedMap, BASIC_INFO);
        organize(orderedMap, LANG_INFO);
        organize(orderedMap, HISTORY_INFO);
        organize(orderedMap, SYSTEM_INFO);
        organize(orderedMap, COPYRIGHT_INFO);
        // add everything else in sorted order
        orderedMap.putAll(new TreeMap(prop));
        prop = orderedMap;
    }

    /**
     * 
     */
    private void organize(Map result, Object[] category)
    {
        for (int i = 0; i < category.length; i++)
        {
            String key = category[i].toString();
            Object value = prop.remove(key);
            if (value != null)
            {
                result.put(key, value);
            }
        }
    }

    /**
     * 
     */
    public void validate()
    {
        // See if the ModuleType can handle this BookMetaData

        // Locked modules are described but don't exist.
        if (getProperty(ConfigEntry.CIPHER_KEY) != null)
        {
            // Don't need to say we don't support locked modules. We already know that!
            //log.debug("Book not supported: " + internal + " because it is locked."); //$NON-NLS-1$ //$NON-NLS-2$
            supported = false;
            return;
        }

        // It has to have a usable name
        String name = getName();

        if (name == null || name.length() == 0)
        {
            log.debug("Book not supported: " + internal + " because it has no name."); //$NON-NLS-1$ //$NON-NLS-2$
            supported = false;
            return;
        }

        String modTypeName = getProperty(ConfigEntry.MOD_DRV);
        if (modTypeName == null || modTypeName.length() == 0)
        {
            log.error("Book not supported: " + internal + " because it has no " + ConfigEntry.MOD_DRV); //$NON-NLS-1$ //$NON-NLS-2$
            supported = false;
            return;
        }

        ModuleType type = ModuleType.getModuleType(modTypeName);
        if (type == null)
        {
            log.debug("Book not supported: " + internal + " because no ModuleType for " + modTypeName); //$NON-NLS-1$ //$NON-NLS-2$
            supported = false;
            return;
        }

        if (type.getBookType() == null)
        {
            // We plan to add RawGenBook at a later time. So we don't need to be reminded all the time.
            if (!modTypeName.equals("RawGenBook")) //$NON-NLS-1$
            {
                log.debug("Book not supported: " + internal + " because missing book type for ModuleType (" + modTypeName + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
            supported = false;
            return;
        }

        if (!type.isSupported(this))
        {
            log.debug("Book not supported: " + internal + " because ModuleType (" + modTypeName + ") is not supported."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            supported = false;
            return;
        }

        // report on unknown config entries
        for (Iterator kit = getKeys(); kit.hasNext();)
        {
            String key = (String) kit.next();
            if (ConfigEntry.fromString(key) == null)
            {
                log.warn("Unknown config entry for " + internal + ": " + key); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

        // output collected warnings
        for (Iterator kit = warnings.iterator(); kit.hasNext();)
        {
            log.warn((String) kit.next());
        }
    }

    /**
     * Is this one of the supported book types?
     */
    public boolean isSupported()
    {
        return supported;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getName()
     */
    public String getName()
    {
        return getProperty(ConfigEntry.DESCRIPTION);
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
    public ModuleType getModuleType()
    {
        return mtype;
    }

    /**
     * Returns the sourceType.
     */
    public Filter getFilter()
    {
        String sourcetype = getProperty(ConfigEntry.SOURCE_TYPE);
        return FilterFactory.getFilter(sourcetype);
    }

    /**
     * Parse a single line.
     * The About field may use RTF, where \par is a line break.
     */
    private void parseEntry(String line)
    {
        String key = null;
        String value = ""; //$NON-NLS-1$
        int eqpos = line.indexOf('=');
        if (eqpos == -1)
        {
            key = line;
        }
        else
        {
            key = line.substring(0, eqpos).trim();
            value = line.substring(eqpos + 1).trim();
        }

        if (key.charAt(0) == '[' && key.charAt(key.length() - 1) == ']')
        {
            // The conf file contains a leading line of the form [KJV]
            // This is the acronymn by which Sword refers to it.
            initials = key.substring(1, key.length() - 1);
        }
        else
        {
            value = handleRTF(key, value);
            addEntry(key, value);
        }

    }

    private String handleRTF(String key, String value)
    {
        String copy = value;
        // This method is a hack! It could be made much nicer.

        // strip \pard
        copy = copy.replaceAll("\\\\pard ?", ""); //$NON-NLS-1$ //$NON-NLS-2$

        // replace rtf newlines
        copy = copy.replaceAll("\\\\pa[er] ?", "\n"); //$NON-NLS-1$ //$NON-NLS-2$

        // strip whatever \qc is.
        copy = copy.replaceAll("\\\\qc ?", ""); //$NON-NLS-1$ //$NON-NLS-2$

        // strip bold and italic
        copy = copy.replaceAll("\\\\[bi]0? ?", ""); //$NON-NLS-1$ //$NON-NLS-2$

        // strip unicode characters
        copy = copy.replaceAll("\\\\u-?[0-9]{4,6}+\\?", ""); //$NON-NLS-1$ //$NON-NLS-2$

        // strip { and } which are found in {\i text }
        copy = copy.replaceAll("[{}]", ""); //$NON-NLS-1$ //$NON-NLS-2$

        if (!allowsRTF.contains(key))
        {
            if (!copy.equals(value))
            {
                warnings.add("Ignoring unexpected RTF for " + key + " in " + internal + ": " + value); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
            return value;
        }

        return copy;
    }

    /**
     * Get continuation lines, if any.
     */
    private void getContinuation(BufferedReader bin, StringBuffer buf) throws IOException
    {
        String key = null;
        int eqpos = buf.indexOf("="); //$NON-NLS-1$
        if (eqpos != -1)
        {
            key = buf.substring(0, eqpos).trim();
        }

        for (String line = advance(bin); line != null; line = advance(bin))
        {
            int length = buf.length();

            // Look for bad data as this condition did exist
            boolean continuation_expected = buf.charAt(length - 1) == '\\';

            if (continuation_expected)
            {
                // delete the continuation character
                buf.deleteCharAt(length - 1);
            }

            if (isKeyLine(line))
            {
                if (continuation_expected)
                {
                    warnings.add("Continuation followed by key for " + key + " in " + internal + ": " + line); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                }

                backup(line);
                break;
            }
            else if (!continuation_expected)
            {
                warnings.add("Line without previous continuation for " + key + " in " + internal + ": " + line); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }

            if (singleLine.contains(key))
            {
                warnings.add("Ignoring unexpected additional line for " + key + " in " + internal + ": " + line); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$                
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
            // Save the original for diagnostics and for save
            data.append(line).append('\n');

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
     * Add a value to the list of values for a key
     */
    private void addEntry(String key, String value)
    {
        // check to see if there already values for this key...
        List list = (ArrayList) table.get(key);
        if (list != null)
        {
            if (multipleEntry.contains(key))
            {
                list.add(value);
            }
            else
            {
                warnings.add("Ignoring unexpected additional entry for " + key + " in " + internal + ": " + value); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$                
            }
        }
        else
        {
            list = new ArrayList();
            list.add(value);
            table.put(key, list);
        }
    }

    /**
     * Fix the configuration
     */
    private void fixEntry(ConfigEntry entry, String value)
    {
        String key = entry.toString();

        table.remove(key);
        List list = new ArrayList();
        list.add(value);
        table.put(key, list);

        prop.put(key, value);
    }

    /**
     * @return Returns the name of this module as it is used for directory and filenames.
     */
    public String getDiskName()
    {
        return initials.toLowerCase();
    }

    //==========================================================================

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getType()
     */
    public BookType getType()
    {
        return mtype.getBookType();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getBook()
     */
    public Book getBook()
    {
        return book;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getDriver()
     */
    public BookDriver getDriver()
    {
        return driver;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getLanguage()
     */
    public String getLanguage()
    {
        return getProperty(KEY_LANGUAGE);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getInitials()
     */
    public String getInitials()
    {
        return initials;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getProperties()
     */
    public Map getProperties()
    {
        return prop;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getIndexStatus()
     */
    public IndexStatus getIndexStatus()
    {
        return indexStatus;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#setIndexStatus(java.lang.String)
     */
    public void setIndexStatus(IndexStatus newValue)
    {
        IndexStatus oldValue = this.indexStatus;
        this.indexStatus = newValue;
        prop.put(KEY_INDEXSTATUS, newValue);
        firePropertyChange(KEY_INDEXSTATUS, oldValue, newValue);
    }

    /**
     * Get the string value for the property or null if it is not defined.
     * It is assumed that all properties gotten with this method are single line.
     * @param entry typically a string or a ConfigEntry
     * @return the property or null
     */
    public String getProperty(Object entry)
    {
        String result = (String) prop.get(entry.toString());

        if (result == null)
        {
            return null;
        }

        int index = result.indexOf('\n');
        if (index != -1)
        {
            log.warn("Fixing data for " + internal + ". Stripping all but first line of " + entry); //$NON-NLS-1$ //$NON-NLS-2$
            result = result.substring(0, index);
            if (entry instanceof ConfigEntry)
            {
                fixEntry((ConfigEntry) entry, result);
            }
            else
            {
                prop.put(entry.toString(), result);
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getFullName()
     */
    public String getFullName()
    {
        if (fullName == null)
        {
            fullName = computeFullName();
        }
        return fullName;
    }

    /**
     * 
     */
    private String computeFullName()
    {
        StringBuffer buf = new StringBuffer(getName());

        if (driver != null)
        {
            buf.append(" (").append(getDriverName()).append(")"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return buf.toString();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getFullName()
     */
    public String getOsisID()
    {
        return getType().toString() + '.' + getInitials();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getDriverName()
     */
    public String getDriverName()
    {
        if (driver == null)
        {
            return null;
        }

        return driver.getDriverName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#lsLeftToRight()
     */
    public boolean isLeftToRight()
    {
        String ltor = getProperty(ConfigEntry.DIRECTION);
        return ltor == null || ltor.equals(SwordConstants.DIRECTION_STRINGS[SwordConstants.DIRECTION_LTOR]);
    }

    /**
     * @param book The book to set.
     */
    protected void setBook(Book book)
    {
        this.book = book;
    }

    /**
     * @param driver The driver to set.
     */
    protected void setDriver(BookDriver driver)
    {
        this.driver = driver;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        // Since this can not be null
        if (obj == null)
        {
            return false;
        }

        // We might consider checking for equality against all BookMetaDatas?
        // However currently we dont.

        // Check that that is the same as this
        // Don't use instanceof since that breaks inheritance
        if (!obj.getClass().equals(this.getClass()))
        {
            return false;
        }

        // The real bit ...
        BookMetaData that = (BookMetaData) obj;

        return getName().equals(that.getName());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return getName().hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        if (displayName == null)
        {
            StringBuffer buf = new StringBuffer("["); //$NON-NLS-1$
            buf.append(getInitials());
            buf.append("] - "); //$NON-NLS-1$
            buf.append(getFullName());
            displayName = buf.toString();
        }
        return displayName;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object obj)
    {
        BookMetaData that = (BookMetaData) obj;
        return this.getName().compareTo(that.getName());
    }

    public static void dumpStatistics()
    {
        System.out.println(histogram.toString());
    }

    private static final String DEFAULT_LANG_CODE = "en"; //$NON-NLS-1$
    private static final String UNKNOWN_LANG_CODE = "und"; //$NON-NLS-1$

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(SwordBookMetaData.class);

    private static/*final*/ResourceBundle languages;
    static
    {
        try
        {
            languages = ResourceBundle.getBundle("iso639", Locale.getDefault(), new CWClassLoader()); //$NON-NLS-1$;
        }
        catch (MissingResourceException e)
        {
            assert false;
        }
    }

    private static final Object[] BASIC_INFO =
    {
        KEY_INITIALS,
        ConfigEntry.DESCRIPTION,
        KEY_TYPE,
        ConfigEntry.LCSH,
        ConfigEntry.CATEGORY,
        ConfigEntry.VERSION,
        ConfigEntry.SWORD_VERSION_DATE,
    };

    private static final Object[] LANG_INFO =
    {
        KEY_LANGUAGE,
        ConfigEntry.LANG,
        ConfigEntry.GLOSSARY_FROM,
        ConfigEntry.GLOSSARY_TO,
        ConfigEntry.LEXICON_FROM,
        ConfigEntry.LEXICON_TO,
    };

    private static final Object[] HISTORY_INFO =
    {
        ConfigEntry.HISTORY_2_5,
        ConfigEntry.HISTORY_2_2,
        ConfigEntry.HISTORY_2_1,
        ConfigEntry.HISTORY_2_0,
        ConfigEntry.HISTORY_1_9,
        ConfigEntry.HISTORY_1_8,
        ConfigEntry.HISTORY_1_7,
        ConfigEntry.HISTORY_1_6,
        ConfigEntry.HISTORY_1_5,
        ConfigEntry.HISTORY_1_4,
        ConfigEntry.HISTORY_1_3,
        ConfigEntry.HISTORY_1_2,
        ConfigEntry.HISTORY_1_1,
        ConfigEntry.HISTORY_1_0,
        ConfigEntry.HISTORY_0_92,
        ConfigEntry.HISTORY_0_91,
        ConfigEntry.HISTORY_0_9,
        ConfigEntry.HISTORY_0_3,
        ConfigEntry.HISTORY_0_2,
        ConfigEntry.HISTORY_0_1
    };

    private static final Object[] SYSTEM_INFO =
    {
        ConfigEntry.DATA_PATH,
        ConfigEntry.MOD_DRV,
        ConfigEntry.SOURCE_TYPE,
        ConfigEntry.BLOCK_TYPE,
        ConfigEntry.COMPRESS_TYPE,
        ConfigEntry.ENCODING,
        ConfigEntry.MINIMUM_VERSION,
        ConfigEntry.MINIMUM_SWORD_VERSION,
        ConfigEntry.DIRECTION
    };

    private static final Object[] COPYRIGHT_INFO =
    {
        ConfigEntry.ABOUT,
        ConfigEntry.DISTRIBUTION,
        ConfigEntry.DISTRIBUTION_LICENSE,
        ConfigEntry.DISTRIBUTION_NOTES,
        ConfigEntry.DISTRIBUTION_SOURCE,
        ConfigEntry.COPYRIGHT,
        ConfigEntry.COPYRIGHT_DATE,
        ConfigEntry.COPYRIGHT_HOLDER,
        ConfigEntry.COPYRIGHT_CONTACT_NAME,
        ConfigEntry.COPYRIGHT_CONTACT_ADDRESS,
        ConfigEntry.COPYRIGHT_CONTACT_EMAIL,
        ConfigEntry.COPYRIGHT_NOTES,
        ConfigEntry.TEXT_SOURCE,
    };

    // Some config entries are expected to be single line entries
    private static final Set singleLine = new HashSet();
    static
    {
        singleLine.add(ConfigEntry.BLOCK_TYPE.toString());
        singleLine.add(ConfigEntry.DATA_PATH.toString());
        singleLine.add(ConfigEntry.COMPRESS_TYPE.toString());
        singleLine.add(ConfigEntry.SOURCE_TYPE.toString());
        singleLine.add(ConfigEntry.DESCRIPTION.toString());
        singleLine.add(ConfigEntry.LANG.toString());
        singleLine.add(ConfigEntry.DIRECTION.toString());
        singleLine.add(ConfigEntry.CIPHER_KEY.toString());
        singleLine.add(ConfigEntry.MOD_DRV.toString());
        singleLine.add(ConfigEntry.ENCODING.toString());
    }

    // Some config entries may exist more than once
    private static final Set multipleEntry = new HashSet();
    static
    {
        multipleEntry.add(ConfigEntry.FEATURE.toString());
        multipleEntry.add(ConfigEntry.GLOBAL_OPTION_FILTER.toString());
        multipleEntry.add(ConfigEntry.OBSOLETES.toString());
    }

    // Some config entries allow RTF
    private static final Set allowsRTF = new HashSet();
    static
    {
        allowsRTF.add(ConfigEntry.ABOUT.toString());
        allowsRTF.add(ConfigEntry.COPYRIGHT_CONTACT_ADDRESS.toString());
        allowsRTF.add(ConfigEntry.COPYRIGHT_NOTES.toString());
        allowsRTF.add(ConfigEntry.COPYRIGHT_CONTACT_NAME.toString());
    }

    /**
     * A map of lists of known keys. Keys are presented in insertion order
     */
    private Map table = new LinkedHashMap();

    /**
     * The single key version of the input file
     */
    private Map prop = new LinkedHashMap();

    /**
     * The original name of this config file from mods.d.
     * This is only used for reporting
     */
    private String internal;

    private ModuleType mtype;
    private Book book;
    private BookDriver driver;
    private String fullName;
    private String displayName;
    private String initials = ""; //$NON-NLS-1$
    private boolean supported;
    private StringBuffer data = new StringBuffer();
    private String readahead;
    private static Histogram histogram = new Histogram();
    private List warnings = new ArrayList();
    private IndexStatus indexStatus = IndexStatus.UNDONE;
}
