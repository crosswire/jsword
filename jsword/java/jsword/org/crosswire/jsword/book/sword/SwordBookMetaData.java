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
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.BookType;
import org.crosswire.jsword.book.Openness;
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
 * @author DM Smith [dmsmith555 at hotmail dot com]
 * @version $Id$
 */
public class SwordBookMetaData implements BookMetaData
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

        // read the config file
        BufferedReader bin = new BufferedReader(in);
        while (true)
        {
            String line = bin.readLine();
            if (line == null)
            {
                break;
            }

            int length = line.length();
            if (length > 0)
            {
                if (line.charAt(length - 1) == '\\')
                {
                    StringBuffer buf = new StringBuffer(line.substring(0, length - 1).trim());
                    buf.append('\n');
                    getContinuation(bin, buf);
                    line = buf.toString();
                }

                parseLine(line);
            }
        }

        // From the config map, extract the important bean properties
        name = getFirstValue(ConfigEntry.DESCRIPTION);
        if (name != null)
        {
            initials = StringUtil.getInitials(name);
        }

        String modTypeName = getFirstValue(ConfigEntry.MOD_DRV);
        if (modTypeName != null)
        {
            mtype = ModuleType.getModuleType(modTypeName);
        }

        speed = BookMetaData.SPEED_FAST;
        edition = "";
        openness = Openness.UNKNOWN;
        licence = null;
        firstPublished = FIRSTPUB_DEFAULT;

        if (name == null)
        {
            log.warn("Missing description for: "+internal);
            name = internal;
        }
        if (mtype == null)
        {
            log.warn("Missing module type for: "+internal+" checked: "+modTypeName);
        }
        else
        {
            if (mtype.getBookType() == null)
            {
                log.warn("Missing book type for: "+internal+" checked: "+modTypeName);
            }
        }

        // merge entries into properties file
        for (Iterator kit = getKeys(); kit.hasNext(); )
        {
            String key = (String) kit.next();
            List list = (List) table.get(key);
            
            StringBuffer combined = new StringBuffer();
            boolean appendSeparator = false;
            for (Iterator vit = list.iterator(); vit.hasNext(); )
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
        prop.put(KEY_NAME, name);
        prop.put(KEY_INITIALS, initials);

        if (mtype != null)
        {
            BookType type = mtype.getBookType();
            prop.put(KEY_TYPE, type != null ? type.getName() : "");
        }

        prop.put(KEY_SPEED, Integer.toString(speed));
        prop.put(KEY_EDITION, edition);
        prop.put(KEY_OPENNESS, openness.getName());
        prop.put(KEY_LICENCE, licence == null ? "" : licence.toString());
        prop.put(KEY_FIRSTPUB, firstPublished.toString());
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
        ModuleType type = getModuleType();
        return type != null && type.getBookType() != null;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getName()
     */
    public String getName()
    {
        return name;
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
     * @return int
     */
    public Filter getFilter()
    {
        String sourcetype = getFirstValue(ConfigEntry.SOURCE_TYPE);
        return FilterFactory.getFilter(sourcetype);
    }

    /**
     * Parse a single line.
     * @param line
     */
    private void parseLine(String line)
    {
        int eqpos = line.indexOf('=');
        if (eqpos == -1)
        {
            addEntry(line, "");
        }
        else
        {
            String key = line.substring(0, eqpos).trim();
            String value = line.substring(eqpos + 1).trim();

            addEntry(key, value);
        }
    }

    /**
     * Since the current line ended with \, append the next line.
     * Use \n to separate lines.
     * @param bin
     * @param buf
     */
    private void getContinuation(BufferedReader bin, StringBuffer buf) throws IOException
    {
        String line = bin.readLine();
        if (line == null)
        {
            return;
        }
        int length = line.length();
        if (length > 0)
        {
            if (line.charAt(length - 1) == '\\')
            {
                buf.append(line.substring(0, length - 1).trim());
                buf.append('\n');
                getContinuation(bin, buf);
            }
        }        
    }

    /**
     * Add a value to the list of values for a key
     */
    private void addEntry(String key, String value)
    {
        /*
        if (ConfigEntry.getConfigEntry(key) == null)
        {
            log.warn("unknown config entry: "+key);
        }
        */

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

    /**
     * @return Returns the internal name of this module.
     */
    public String getInternalName()
    {
        return internal;
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
     * @see org.crosswire.jsword.book.BookMetaData#getEdition()
     */
    public String getEdition()
    {
        return edition;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getInitials()
     */
    public String getInitials()
    {
        return initials;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getSpeed()
     */
    public int getSpeed()
    {
        return speed;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getFirstPublished()
     */
    public Date getFirstPublished()
    {
        return firstPublished;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getOpenness()
     */
    public Openness getOpenness()
    {
        return openness;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getLicence()
     */
    public URL getLicence()
    {
        return licence;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getProperties()
     */
    public Map getProperties()
    {
        return prop;
    }
    
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getFullName()
     */
    public String getFullName()
    {
        StringBuffer buf = new StringBuffer(getName());
        String ed = getEdition();
        if (!ed.equals(""))
        {
            buf.append(", ").append(ed);
        }
        if (driver != null)
        {
            buf.append(" (").append(getDriverName()).append(")");
        }

        return buf.toString();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getFullName()
     */
    public String getOsisID()
    {
        return getType().getName() + "." + getInitials();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#isSameFamily(org.crosswire.jsword.book.BookMetaData)
     */
    public boolean isSameFamily(BookMetaData version)
    {
        return getName().equals(version.getName());
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

        // If super does equals ...
        /* Commented out because super.equals() always equals false
         if (!super.equals(obj))
         {
         return false;
         }
         */

        // The real bit ...
        BookMetaData that = (BookMetaData) obj;

        if (!getName().equals(that.getName()))
        {
            return false;
        }

        return getEdition().equals(that.getEdition());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return (getName() + getEdition()).hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return getFullName();
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object obj)
    {
        BookMetaData that = (BookMetaData) obj;
        return this.getName().compareTo(that.getName());
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(SwordBookMetaData.class);

    /**
     * A map of lists of known keys. Keys are presented in insertion order
     */
    private Map table = new LinkedHashMap();

    /**
     * The single key version of the input file
     */
    private Map prop = new LinkedHashMap();

    /**
     * The original name of this config file from mods.d
     */
    private String internal = null;

    private ModuleType mtype;
    private Book book;
    private BookDriver driver = null;
    private String name = "";
    private String edition = "";
    private String initials = "";
    private int speed = BookMetaData.SPEED_SLOWEST;
    private Date firstPublished = FIRSTPUB_DEFAULT;
    private Openness openness = Openness.UNKNOWN;
    private URL licence = null;
}
