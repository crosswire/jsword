
package org.crosswire.jsword.book.basic;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Openness;

/**
 * BasicVersion is the default and probably only implementation of the
 * Version interface.
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
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public abstract class AbstractBookMetaData implements BookMetaData
{
    /**
     * Basic constructor
     */
    public AbstractBookMetaData(Properties prop) throws MalformedURLException, ParseException
    {
        setName(prop.getProperty("Version"));
        setEdition(prop.getProperty("Edition"));
        setOpenness(prop.getProperty("Openness"));
        setLicense(prop.getProperty("LicenceURL"));
        setFirstPublished(prop.getProperty("Date"));
        setInitials(prop.getProperty("Initials"));
    }

    /**
     * Basic constructor where the user is expected to create correct
     * Date, Openness and URL objects
     */
    public AbstractBookMetaData(String name, String edition, String initials, Date pub, Openness open, URL licence)
    {
        setName(name);
        setEdition(edition);
        setFirstPublished(pub);
        setOpenness(open);
        setLicense(licence);
    }
    
    /**
     * Basic constructor where we do all the string conversion for the user
     */
    public AbstractBookMetaData(String name, String edition, String initials, String pubstr, String openstr, String licencestr) throws ParseException, MalformedURLException
    {
        setName(name);
        setEdition(edition);
        setFirstPublished(pubstr);
        setOpenness(openstr);
        setLicense(licencestr);
    }

    /**
     * Ctor for when we only know the book name
     */
    public AbstractBookMetaData(String name)
    {
        setName(name);
        setEdition(null);
        setFirstPublished((Date) null);
        setOpenness((Openness) null);
        setLicense((URL) null);
        setInitials(null);
    }

    /**
     * Convert a published date in the standard (String) format into a Date object 
     */
    public static Date parsePublishedDate(String pubstr)
    {
        if (pubstr == null)
            return DEFAULT;

        try
        {
            return PUBLISHED_FORMAT.parse(pubstr);
        }
        catch (ParseException ex)
        {
            log.warn("Invalid date format: "+pubstr, ex);
            return DEFAULT;
        }
    }

    /**
     * Convert a published Date into the standard (String) format
     */
    public static String formatPublishedDate(Date pub)
    {
        return PUBLISHED_FORMAT.format(pub);
    }

    /**
     * The name of the version, for example "King James Version" or
     * "Bible in Basic English" or "Greek". In general it should be
     * possible to deduce the initials from the name by removing all the
     * non-capital letters.
     * @return The name of this version
     */
    public String getName()
    {
        return name;
    }

    /**
     * Internal setter for the name.
     * This also updates the initials (if they are null) so if you have initials
     * to alter then do that afterwards (although this is unlikley)
     */
    private void setName(String name)
    {
        this.name = name;
        this.initials = StringUtil.getInitials(name);
    }

    /**
     * The edition of this version, for example "Anglicised" (NIV),
     * "Stephanus" (Greek). For 2 versions to be equal both the name and
     * the edition must be equal. In general the text returned by this
     * method should not include the word "Edition". Empty string is
     * used over null.
     * @return The name of the edition
     */
    public String getEdition()
    {
        return edition;
    }

    /**
     * Internal setter for the edition
     */
    private void setEdition(String edition)
    {
        if (edition == null)
            this.edition = "";
        else
            this.edition = edition;
    }

    /**
     * The initials of the version - how most people will know it, for
     * example "NIV", "KJV"
     * @return The versions initials
     */
    public String getInitials()
    {
        return initials;
    }

    /**
     * Internal setter for the initials
     */
    private void setInitials(String initials)
    {
        if (initials == null)
            this.initials = StringUtil.getInitials(name);
        else
            this.initials = initials;
    }

    /**
     * The date of first publishing. This does not need to be accurate and
     * 2 versions can be considered equal even if they have different
     * first publishing dates for that reason. In general "1 Jan 1970"
     * means published in 1970, and so on.
     * @return The date of first publishing
     */
    public Date getFirstPublished()
    {
        return pub;
    }

    /**
     * Internal setter for the initials
     */
    private void setFirstPublished(Date pub)
    {
        if (pub == null)
            this.pub = DEFAULT;
        else
            this.pub = pub;
    }

    /**
     * Internal setter for the first published date
     */
    private void setFirstPublished(String pubstr) throws ParseException
    {
        if (pubstr == null)
            this.pub = DEFAULT;
        else
            this.pub = PUBLISHED_FORMAT.parse(pubstr);
    }

    /**
     * Is this version sold for commercial profit like the NIV, or kept
     * open like the NET version.
     * @return A STATUS_* constant
     */
    public Openness getOpenness()
    {
        return open;
    }

    /**
     * Internal setter for the openness setting
     */
    private void setOpenness(Openness open)
    {
        if (open == null)
            this.open = Openness.UNKNOWN;
        else
            this.open = open;
    }

    /**
     * Internal setter for the openness setting
     */
    private void setOpenness(String openstr)
    {
        if (openstr == null)
            this.open = Openness.UNKNOWN;
        else
            this.open = Openness.get(openstr);
    }

    /**
     * Not sure about this one - Do we need a way of getting at the dist.
     * licence? Are we going to be able to tie it down to a single Version
     * policy like this?
     * @return String detailing the users right to distribute this version
     */
    public URL getLicence()
    {
        return licence;
    }

    /**
     * Internal setter for the license
     */
    private void setLicense(URL licence)
    {
        this.licence = licence;
    }

    /**
     * Internal setter for the license
     */
    private void setLicense(String licencestr) throws MalformedURLException
    {
        if (licencestr == null)
            this.licence = null;
        else
            this.licence = new URL(licencestr);
    }

    /**
     * Do the 2 versions have matching names, editions and drivers.
     * @param obj The object to compare to
     * @return true if the names and editions match
     */
    public boolean equals(Object obj)
    {
        // Since this can not be null
        if (obj == null)
            return false;

        // Check that that is the same as this
        // Don't use instanceof since that breaks inheritance
        if (!obj.getClass().equals(this.getClass()))
            return false;

        // If super does equals ...
        if (super.equals(obj) == false)
            return false;

        // The real bit ...
        AbstractBookMetaData that = (AbstractBookMetaData) obj;

        if (!getName().equals(that.getName()))
            return false;

        return getEdition().equals(that.getEdition());
    }

    /**
     * Get a moderately unique id for this Object.
     * @return The hashing number
     */
    public int hashCode()
    {
        return (getName() + getEdition()).hashCode();
    }

    /**
     * The full name including edition of the version, for example
     * "New International Version, Anglicised". The format is "name, edition"
     * @return The full name of this version
     */
    public String getFullName()
    {
        return getName() + ", " + getEdition() + " (" + getDriverName() + ")";
    }

    /**
     * Get a human readable version of this Version -just bounce to
     * getFullName()
     * @return The full name of this version
     */
    public String toString()
    {
        return getFullName();
    }

    /**
     * Do the 2 versions have matching names.
     * @param version The version to compare to
     * @return true if the names match
     */
    public boolean isSameFamily(BookMetaData version)
    {
        return getName().equals(version.getName());
    }

    /**
     * Delete a Bible
     * @throws BookException If anything goes wrong with this method
     * @see org.crosswire.jsword.book.BookMetaData#delete()
     */
    public void delete() throws BookException
    {
        throw new BookException("book_nodel", new Object[] { getName() });
    }

    /**
     * 
     */
    public static final DateFormat PUBLISHED_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * The default creation date
     */
    private static Date DEFAULT;

    /**
     * The log stream
     */
    protected static Logger log = Logger.getLogger(AbstractBookMetaData.class);

    /**
     * Setup the default publish date
     */
    static
    {
        try
        {
            DEFAULT = PUBLISHED_FORMAT.parse("1970-01-01");
        }
        catch (ParseException ex)
        {
            log.warn("Failed to set default fallback date", ex);
            DEFAULT = new Date();
        }
    }

    /**
     * The name of the version
     */
    private String name;

    /**
     * The edition of this version
     */
    private String edition;

    /**
     * The common initials of the version name
     */
    private String initials;

    /**
     * The approximate date of first publishing
     */
    private Date pub;

    /**
     * The openness of the version
     */
    private Openness open;

    /**
     * The URL of the distribution licence
     */
    private URL licence;
}
