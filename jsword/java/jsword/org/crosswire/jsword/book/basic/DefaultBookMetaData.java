package org.crosswire.jsword.book.basic;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
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
    public Map getProperties()
    {
        return map;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getFullName()
     */
    public String getFullName()
    {
        StringBuffer buf = new StringBuffer(getName());
        String ed = getEdition();

        if (!ed.equals("")) //$NON-NLS-1$
        {
            buf.append(", ").append(ed); //$NON-NLS-1$
        }

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
        return getType().getName() + "." + getInitials(); //$NON-NLS-1$
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

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#isLeftToRight()
     */
    public boolean isLeftToRight()
    {
        // TODO(joe): Do this correctly
        return true;
    }

    /**
     * @param book The book to set.
     */
    public void setBook(Book book)
    {
        this.book = book;
    }

    /**
     * @param driver The driver to set.
     */
    public void setDriver(BookDriver driver)
    {
        this.driver = driver;
    }

    /**
     * @param edition The edition to set.
     */
    public void setEdition(String edition)
    {
        if (edition == null)
        {
            this.edition = ""; //$NON-NLS-1$
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
            firstPublished = FIRSTPUB_DEFAULT;
        }
        this.firstPublished = firstPublished;

        map.put(KEY_FIRSTPUB, this.firstPublished.toString());
    }

    /**
     * Internal setter for the first published date
     */
    public void setFirstPublished(String pubstr) throws ParseException
    {
        Date newPublished = null;
        if (pubstr != null && pubstr.trim().length() > 0)
        {
            newPublished = FIRSTPUB_FORMAT.parse(pubstr);
        }
        setFirstPublished(newPublished);
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
                this.initials = ""; //$NON-NLS-1$
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

        map.put(KEY_LICENCE, licence == null ? "" : this.licence.toString()); //$NON-NLS-1$
    }

    /**
     * Internal setter for the license
     */
    public void setLicence(String licencestr) throws MalformedURLException
    {
        URL newLicence = null;
        if (licencestr != null)
        {
            newLicence = new URL(licencestr);
        }

        setLicence(newLicence);
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
            openness = Openness.UNKNOWN;
        }
        this.openness = openness;

        map.put(KEY_OPENNESS, this.openness.getName());
    }

    /**
     * @param openstr The string version of the openness to set.
     */
    public void setOpenness(String openstr)
    {
        Openness newOpenness = null;
        if (openstr != null)
        {
            newOpenness = Openness.get(openstr);
        }

        setOpenness(newOpenness);
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
        setSpeed(Integer.parseInt(speedstr));
    }

    /**
     * @param type The type to set.
     */
    public void setType(BookType type)
    {
        if (type == null)
        {
            type = BookType.BIBLE;
        }
        this.type = type;

        map.put(KEY_TYPE, type == null ? "" : type.getName()); //$NON-NLS-1$
    }

    /**
     * @param typestr The string version of the type to set.
     */
    public void setType(String typestr)
    {
        BookType newType = null;
        if (typestr != null)
        {
            newType = BookType.get(typestr);
        }

        setType(newType);
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
     * Convert a published Date into the standard (String) format
     */
    public static String formatPublishedDate(Date pub)
    {
        return FIRSTPUB_FORMAT.format(pub);
    }

    /**
     * 
     */
    private Map map = new LinkedHashMap();

    private BookType type;
    private Book book;
    private BookDriver driver;
    private String name = ""; //$NON-NLS-1$
    private String edition = ""; //$NON-NLS-1$
    private String initials = ""; //$NON-NLS-1$
    private int speed = BookMetaData.SPEED_SLOWEST;
    private Date firstPublished = FIRSTPUB_DEFAULT;
    private Openness openness = Openness.UNKNOWN;
    private URL licence;
}
