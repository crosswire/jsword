
package org.crosswire.jsword.book.basic;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.crosswire.jsword.book.Bibles;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.util.Project;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.util.StringUtil;

/**
 * VersionFactory.
 *
 * <table border='1' cellPadding='3' cellSpacing='0' width="100%">
 * <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
 * Distribution Licence:<br />
 * Project B is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
 * <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
 * @see <{docs.Licence}>
 * @author Joe Walker
 * @deprecated Use a public ctor on BasicBookMetaData
 */
public class VersionFactory
{
    /**
     * Ensure that we can not be instansiated
     */
    private VersionFactory()
    {
    }

    /**
     * Create a new Version or fetch an existing one from a name
     * and edition
     * @param name The name of the desired version
     * @param edition The edition of the desired version
     * @return The desired version
     */
    public static BookMetaData getVersion(String name, String edition)
    {
        String key = getFullName(name, edition);

        BookMetaData version = (BookMetaData) versions.get(key);
        if (version == null)
        {
            log.fine("Created a new version called '" + key + "'");
            version = new BasicBookMetaData(name, edition, StringUtil.getInitials(name), null, BookMetaData.STATUS_UNKNOWN, null);
            versions.put(key, version);
        }

        return version;
    }

    /**
     * Create a new Version or fetch an existing one from a name
     * and edition combined into one string separated by a ,
     * @param fullname The name and edition of the desired version
     * @return The desired version
     */
    public static BookMetaData getVersion(String fullname)
    {
        String name = getName(fullname);
        String edition = getEdition(fullname);
        return getVersion(name, edition);
    }

    /**
     * Take a key=val line from a properties file and turn it into a
     * Version
     * @param key The key (before the equals sign)
     * @param val The key (after the equals sign)
     * @return The new Version
     */
    protected static BookMetaData decodeVersion(String key, String val) throws BookException
    {
        String name = getName(key);
        String edition = getEdition(key);

        String[] val_tok = StringUtil.tokenize(val, SEPARATOR);
        if (val_tok.length != 4)
            throw new BookException("book_versions_val", new Object[] { key, val, new Integer(val_tok.length) });

        Date pub = null;
        try
        {
            if (val_tok[1].trim().length() > 0)
                pub = df.parse(val_tok[1]);
        }
        catch (ParseException ex)
        {
            throw new BookException("book_versions_date", ex, new Object[] { key, val, val_tok[1] });
        }

        int open = decodeStatus(val_tok[2]);

        URL licence = null;
        try
        {
            if (val_tok[3].trim().length() > 0)
                licence = new URL(val_tok[3]);
        }
        catch (MalformedURLException ex)
        {
            throw new BookException("book_versions_licence", ex, new Object[] { key, val, val_tok[3] });
        }

        return new BasicBookMetaData(name, edition, val_tok[0], pub, open, licence);
    }

    /**
     * Turn a string into a distribution status
     * @param The string to decode
     * @return The distribution status constant
     */
    protected static int decodeStatus(String status) throws BookException
    {
        status = status.trim();

        if ("PD".equalsIgnoreCase(status))
            return BookMetaData.STATUS_PD;

        if ("FREE".equalsIgnoreCase(status))
            return BookMetaData.STATUS_FREE;

        if ("COPYABLE".equalsIgnoreCase(status))
            return BookMetaData.STATUS_COPYABLE;

        if ("COMMERCIAL".equalsIgnoreCase(status))
            return BookMetaData.STATUS_COMMERCIAL;

        return BookMetaData.STATUS_UNKNOWN;
    }

    /**
     * Calculate the name of this version from the fullname
     * @param fullname The full "name, edition" of the version
     * @return The name part
     */
    protected static String getName(String fullname)
    {
        int sep = fullname.indexOf(SEPARATOR);
        if (sep == -1)
        {
            return fullname.trim();
        }
        else
        {
            return fullname.substring(0, sep).trim();
        }
    }

    /**
     * Calculate the edition of this version from the fullname
     * @param fullname The full "name, edition" of the version
     * @return The edition part
     */
    protected static String getEdition(String fullname)
    {
        int sep = fullname.indexOf(SEPARATOR);
        if (sep == -1)
        {
            return "";
        }
        else
        {
            return fullname.substring(sep + SEPARATOR.length()).trim();
        }
    }

    /**
     * Get a full name from a name and edition
     * @param The name of the version
     * @param The edition of this version
     * @return The full name "name, edition"
     */
    protected static String getFullName(String name, String edition)
    {
        if (edition == null || edition.trim().length() == 0)
            return name;

        return name + SEPARATOR + edition;
    }

    /**
     * The list of known versions
     */
    private static Hashtable versions = new Hashtable();

    /**
     * The date formatter for the Version published date
     */
    private static DateFormat df = new SimpleDateFormat("d M yyyy");

    /**
     * The log stream
     */
    protected static Logger log = Logger.getLogger("bible.book");

    /**
     * The name-edition separator
     */
    protected static final String SEPARATOR = ",";

    /**
     * Attempt to load the cache of Versions that we know about
     */
    static
    {
        try
        {
            Properties prop = Project.resource().getProperties("Versions");

            // setup the date parser
            df.setLenient(true);

            Enumeration en = prop.keys();
            while (en.hasMoreElements())
            {
                try
                {
                    String key = (String) en.nextElement();
                    String val = (String) prop.get(key);

                    BookMetaData version = decodeVersion(key, val);
                    key = version.getFullName();
                    versions.put(key, version);

                    log.fine("Created a new version called '" + key + "'");
                }
                catch (Exception ex)
                {
                    Reporter.informUser(Bibles.class, ex);
                }
            }
        }
        catch (Exception ex)
        {
            Reporter.informUser(Bibles.class, ex);
        }
    }
}
