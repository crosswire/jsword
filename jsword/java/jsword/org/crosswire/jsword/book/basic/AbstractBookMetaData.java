
package org.crosswire.jsword.book.basic;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.book.BookDriver;
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
    public AbstractBookMetaData(BookDriver driver, Properties prop) throws MalformedURLException, ParseException
    {
        this.driver = driver;

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
    public AbstractBookMetaData(BookDriver driver, String name, String edition, String initials, Date pub, Openness open, URL licence)
    {
        this.driver = driver;

        setName(name);
        setEdition(edition);
        setFirstPublished(pub);
        setOpenness(open);
        setLicense(licence);
    }
    
    /**
     * Basic constructor where we do all the string conversion for the user
     */
    public AbstractBookMetaData(BookDriver driver, String name, String edition, String initials, String pubstr, String openstr, String licencestr) throws ParseException, MalformedURLException
    {
        this.driver = driver;

        setName(name);
        setEdition(edition);
        setFirstPublished(pubstr);
        setOpenness(openstr);
        setLicense(licencestr);
    }

    /**
     * Ctor for when we only know the book name
     */
    public AbstractBookMetaData(BookDriver driver, String name)
    {
        this.driver = driver;

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
        {
            return DEFAULT;
        }

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

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getDriver()
     */
    public BookDriver getDriver()
    {
        return driver;
    }

    /**
     * Convert a published Date into the standard (String) format
     */
    public static String formatPublishedDate(Date pub)
    {
        return PUBLISHED_FORMAT.format(pub);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getName()
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

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getEdition()
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
        {
            this.edition = "";
        }
        else
        {
            this.edition = edition;
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getInitials()
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
        {
            this.initials = StringUtil.getInitials(name);
        }
        else
        {
            this.initials = initials;
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getFirstPublished()
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
        {
            this.pub = DEFAULT;
        }
        else
        {
            this.pub = pub;
        }
    }

    /**
     * Internal setter for the first published date
     */
    private void setFirstPublished(String pubstr) throws ParseException
    {
        if (pubstr == null)
        {
            this.pub = DEFAULT;
        }
        else
        {
            this.pub = PUBLISHED_FORMAT.parse(pubstr);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getOpenness()
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
        {
            this.open = Openness.UNKNOWN;
        }
        else
        {
            this.open = open;
        }
    }

    /**
     * Internal setter for the openness setting
     */
    private void setOpenness(String openstr)
    {
        if (openstr == null)
        {
            this.open = Openness.UNKNOWN;
        }
        else
        {
            this.open = Openness.get(openstr);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getLicence()
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
        {
            this.licence = null;
        }
        else
        {
            this.licence = new URL(licencestr);
        }
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

        // Check that that is the same as this
        // Don't use instanceof since that breaks inheritance
        if (!obj.getClass().equals(this.getClass()))
        {
            return false;
        }

        // If super does equals ...
        if (super.equals(obj) == false)
        {
            return false;
        }

        // The real bit ...
        AbstractBookMetaData that = (AbstractBookMetaData) obj;

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
     * @see org.crosswire.jsword.book.BookMetaData#getFullName()
     */
    public String getFullName()
    {
        return getName() + ", " + getEdition() + " (" + getDriverName() + ")";
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return getFullName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#isSameFamily(org.crosswire.jsword.book.BookMetaData)
     */
    public boolean isSameFamily(BookMetaData version)
    {
        return getName().equals(version.getName());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#delete()
     */
    private final void delete() throws BookException
    {
        throw new BookException(Msg.DELETE_NOTIMPL, new Object[] { getName() });
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
     * The driver behind this Book
     */
    private BookDriver driver;

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
