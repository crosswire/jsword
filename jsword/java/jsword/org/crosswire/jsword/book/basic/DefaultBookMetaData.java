package org.crosswire.jsword.book.basic;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.Properties;

import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.BookType;
import org.crosswire.jsword.book.Openness;

/**
 * DefaultBookMetaData is an implementation of the of the BookMetaData
 * interface. A less complete implementation design for imheritance is
 * available in AbstractBookMetaData where the complexity is in the setup rather
 * than the inheritance. DefaultBookMetaData is probably the preferred
 * implementation.
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
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class DefaultBookMetaData implements BookMetaData
{
    /**
     * Ctor with a properties from which to get values.
     * A call to setBook() is still required after this ctor is called
     */
    public DefaultBookMetaData(BookDriver driver, Book book, Properties prop) throws MalformedURLException, ParseException
    {
        this.driver = driver;
        this.book = book;

        map.putAll(prop);

        setName(prop.getProperty(BookMetaData.KEY_NAME));
        setType(prop.getProperty(BookMetaData.KEY_TYPE));
        setSpeed(Integer.parseInt(prop.getProperty(BookMetaData.KEY_SPEED)));

        setEdition(prop.getProperty(BookMetaData.KEY_EDITION));
        setOpenness(prop.getProperty(BookMetaData.KEY_OPENNESS));
        setLicence(prop.getProperty(BookMetaData.KEY_LICENCE));
        setFirstPublished(prop.getProperty(BookMetaData.KEY_FIRSTPUB));
    }

    /**
     * Ctor with some default values.
     * A call to setBook() is still required after this ctor is called
     */
    public DefaultBookMetaData(BookDriver driver, Book book, String name, BookType type, int speed)
    {
        this.driver = driver;
        this.book = book;

        setName(name);
        setType(type);
        setSpeed(speed);
    }

    /**
     * Ctor with all important values.
     * A call to setBook() is still required after this ctor is called
     */
    public DefaultBookMetaData(BookDriver driver, Book book, String name, BookType type, int speed, String edition, Openness openness, URL licence, Date firstPublished)
    {
        this.driver = driver;
        this.book = book;

        setName(name);
        setType(type);
        setSpeed(speed);

        setEdition(edition);
        setOpenness(openness);
        setLicence(licence);
        setFirstPublished(firstPublished);
    }

    /**
     * Ctor with all important values.
     * A call to setBook() is still required after this ctor is called
     */
    public DefaultBookMetaData(BookDriver driver, Book book, String name, BookType type, int speed, String edition, String openstr, String licencestr, String pubstr) throws MalformedURLException, ParseException, NumberFormatException
    {
        this.driver = driver;
        this.book = book;

        setName(name);
        setType(type);
        setSpeed(speed);

        setEdition(edition);
        setOpenness(openstr);
        setLicence(licencestr);
        setFirstPublished(pubstr);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getType()
     */
    public BookType getType()
    {
        return type;
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
     * @see org.crosswire.jsword.book.BookMetaData#getName()
     */
    public String getName()
    {
        return name;
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
    public Properties getProperties()
    {
        return map;
    }
    
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getFullName()
     */
    public String getFullName()
    {
        return getName() + ", " + getEdition() + " (" + getDriverName() + ")";
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
        return driver.getDriverName();
    }

    /**
     * @param book The book to set.
     */
    public void setBook(Book book)
    {
        this.book = book;

        map.put(KEY_BOOK, this.book);
    }

    /**
     * @param driver The driver to set.
     */
    public void setDriver(BookDriver driver)
    {
        this.driver = driver;

        map.put(KEY_DRIVER, this.driver);
    }

    /**
     * @param edition The edition to set.
     */
    public void setEdition(String edition)
    {
        if (edition == null)
        {
            this.edition = "";
        }
        else
        {
            this.edition = edition;
        }

        map.put(KEY_EDITION, this.edition);
    }

    /**
     * @param firstPublished The firstPublished to set.
     */
    public void setFirstPublished(Date firstPublished)
    {
        if (firstPublished == null)
        {
            this.firstPublished = FIRSTPUB_DEFAULT;
        }
        else
        {
            this.firstPublished = firstPublished;
        }

        map.put(KEY_FIRSTPUB, this.firstPublished);
    }

    /**
     * Internal setter for the first published date
     */
    public void setFirstPublished(String pubstr) throws ParseException
    {
        if (pubstr == null || pubstr.trim().length() == 0)
        {
            firstPublished = FIRSTPUB_DEFAULT;
        }
        else
        {
            firstPublished = FIRSTPUB_FORMAT.parse(pubstr);
        }

        map.put(KEY_FIRSTPUB, this.firstPublished);
    }

    /**
     * See note on setName() for side effect on setInitials(). If a value of
     * null is used then the initials are defaulted using the name
     * @see DefaultBookMetaData#setName(String)
     * @param initials The initials to set.
     */
    public void setInitials(String initials)
    {
        if (initials == null)
        {
            if (name == null)
            {
                this.initials = "";
            }
            else
            {
                this.initials = StringUtil.getInitials(name);
            }
        }
        else
        {
            this.initials = initials;
        }

        map.put(KEY_INITIALS, this.initials);
    }

    /**
     * @param licence The licence to set.
     */
    public void setLicence(URL licence)
    {
        this.licence = licence;

        map.put(KEY_LICENCE, this.licence);
    }

    /**
     * Internal setter for the license
     */
    public void setLicence(String licencestr) throws MalformedURLException
    {
        if (licencestr == null)
        {
            this.licence = null;
        }
        else
        {
            this.licence = new URL(licencestr);
        }

        map.put(KEY_LICENCE, this.licence);
    }

    /**
     * Setting the name also sets some default initials, so if you wish to set
     * some specific initials then it should be done after setting the name.
     * @see DefaultBookMetaData#setInitials(String)
     * @param name The name to set.
     */
    public void setName(String name)
    {
        this.name = name;

        map.put(KEY_NAME, this.name);

        setInitials(StringUtil.getInitials(name));
    }

    /**
     * @param openness The openness to set.
     */
    public void setOpenness(Openness openness)
    {
        if (openness == null)
        {
            this.openness = Openness.UNKNOWN;
        }
        else
        {
            this.openness = openness;
        }

        map.put(KEY_OPENNESS, this.openness);
    }

    /**
     * @param openstr The string version of the openness to set.
     */
    public void setOpenness(String openstr)
    {
        if (openstr == null)
        {
            openness = Openness.UNKNOWN;
        }
        else
        {
            openness = Openness.get(openstr);
            
            if (openness == null)
            {
                openness = Openness.UNKNOWN;
            }
        }

        map.put(KEY_OPENNESS, this.openness);
    }

    /**
     * @param speed The speed to set.
     */
    public void setSpeed(int speed)
    {
        this.speed = speed;

        map.put(KEY_SPEED, Integer.toString(this.speed));
    }

    /**
     * @param speedstr The speed to set.
     */
    public void setSpeed(String speedstr) throws NumberFormatException
    {
        this.speed = Integer.parseInt(speedstr);

        map.put(KEY_SPEED, Integer.toString(this.speed));
    }

    /**
     * @param type The type to set.
     */
    public void setType(BookType type)
    {
        this.type = type;

        map.put(KEY_TYPE, this.type);
    }

    /**
     * @param typestr The string version of the type to set.
     */
    public void setType(String typestr)
    {
        if (typestr == null)
        {
            type = BookType.BIBLE;
        }
        else
        {
            type = BookType.get(typestr);
            
            if (type == null)
            {
                type = BookType.BIBLE;
            }
        }

        map.put(KEY_TYPE, this.type);
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

    /**
     * Convert a published Date into the standard (String) format
     */
    public static String formatPublishedDate(Date pub)
    {
        return FIRSTPUB_FORMAT.format(pub);
    }

    /**
     * 
     */
    private Properties map = new Properties();

    private BookType type;
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
