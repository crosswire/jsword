
package org.crosswire.jsword.book.local;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Properties;

import org.crosswire.common.util.LogicError;
import org.crosswire.common.util.NetUtil;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BibleMetaData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.search.SearchableBibleMetaData;

/**
 * A default implmentation of BibleMetaData.
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
public class LocalURLBibleMetaData extends SearchableBibleMetaData
{
    /**
     * Constructor LocalURLBibleMetaData.
     */
    public LocalURLBibleMetaData(LocalURLBookDriver driver, URL dir, BibleMetaData basis) throws InstantiationException, IllegalAccessException
    {
        super(driver, basis.getName(), basis.getEdition(), basis.getInitials(), basis.getFirstPublished(), basis.getOpenness(), basis.getLicence());

        // Check that we can do this (but ignore the results) to ensure that
        // it will work in getBible()
        driver.bibleclass.newInstance();

        this.dir = dir;
        this.prop = new Properties();
    }

    /**
     * Basic constructor
     */
    public LocalURLBibleMetaData(LocalURLBookDriver driver, URL dir, Properties prop) throws MalformedURLException, ParseException, InstantiationException, IllegalAccessException
    {
        super(driver, prop);

        // Check that we can do this (but ignore the results) to ensure that
        // it will work in getBible()
        driver.bibleclass.newInstance();

        this.dir = dir;
        this.prop = prop;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#delete()
     */
    public void delete() throws BookException
    {
        try
        {
            if (!NetUtil.delete(getURL()))
            {
                throw new BookException(Msg.DELETE_FAIL, new Object[] { getName() });
            }
        }
        catch (IOException ex)
        {
            throw new BookException(Msg.DELETE_FAIL, ex, new Object[] { getName() });
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractBibleMetaData#createBible()
     */
    protected Bible createBible()
    {
        try
        {
            LocalURLBookDriver driver = (LocalURLBookDriver) getDriver(); 
            LocalURLBible bible = (LocalURLBible) driver.bibleclass.newInstance();
            bible.setLocalURLBibleMetaData(this);
            bible.init(null);
            return bible;
        }
        catch (Exception ex)
        {
            // This may sound harsh but we tried this in the ctor
            // so it really should not break here.
            throw new LogicError(ex);
        }
    }

    /**
     * Accessor for keys from our properties file.
     * @param property
     * @return String
     */
    public String getProperty(String property)
    {
        return prop.getProperty(property);
    }

    /**
     * Method getURL.
     * @return URL
     */
    public URL getURL()
    {
        return dir;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getDriverName()
     */
    public String getDriverName()
    {
        return ((LocalURLBookDriver) getDriver()).getDriverName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getSpeed()
     */
    public int getSpeed()
    {
        return ((LocalURLBookDriver) getDriver()).getSpeed();
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

        // Check that that is the same type as this
        // Don't use instanceof since that breaks inheritance
        if (!obj.getClass().equals(this.getClass()))
        {
            return false;
        }

        // If super does equals ...
        if (!super.equals(obj))
        {
            return false;
        }

        // The real bit ...
        LocalURLBibleMetaData that = (LocalURLBibleMetaData) obj;

        return getDriver().equals(that.getDriver());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractBookMetaData#hashCode()
     */
    public int hashCode()
    {
        return getDriver().hashCode();
    }

    /**
     * The properties by which we got our data
     */
    private Properties prop;
    
    /**
     * The location of our home directory
     */
    private URL dir;
}
