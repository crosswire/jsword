
package org.crosswire.jsword.book.local;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Properties;

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
    public LocalURLBibleMetaData(LocalURLBookDriver driver, URL dir, BibleMetaData basis)
    {
        super(basis.getName(), basis.getEdition(), basis.getInitials(), basis.getFirstPublished(), basis.getOpenness(), basis.getLicence());
        this.dir = dir;
        this.prop = new Properties();

        setDriver(driver);
    }

    /**
     * Basic constructor
     */
    public LocalURLBibleMetaData(LocalURLBookDriver driver, URL dir, Properties prop) throws MalformedURLException, ParseException
    {
        super(prop);
        this.dir = dir;
        this.prop = prop;

        setDriver(driver);
    }

    /**
     * Delete the set of files that make up this version.
     * @throws BookException If anything goes wrong with this method
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
     * Fetch a currently existing Bible, read-only
     * @param name The name of the version to create
     * @exception BookException If the name is not valid
     */
    public Bible getBible() throws BookException
    {
        // DCL
        // I know double checked locking is theoretically broken however it isn't
        // practically broken 99% of the time, and even if the 1% comes up here
        // the only effect is some temporary wasted memory
        if (bible == null)
        {
            synchronized(this)
            {
                if (bible == null)
                {
                    bible = driver.getBible(this, null);
                }
            }
        }

        return bible;
    }

    /**
     * Method getURL.
     * @return URL
     */
    public URL getURL()
    {
        return dir;
    }

    /**
     * @see org.crosswire.jsword.book.BookMetaData#getDriverName()
     */
    public String getDriverName()
    {
        return driver.getDriverName();
    }

    /**
     * Internal setter for the BookDriver
     */
    private void setDriver(LocalURLBookDriver driver)
    {
        if (driver == null)
        {
            throw new NullPointerException();
        }

        this.driver = driver;
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
        LocalURLBibleMetaData that = (LocalURLBibleMetaData) obj;

        return driver.equals(that.driver);
    }


    /**
     * The expected speed at which this implementation gets correct answers.
     * @see org.crosswire.jsword.book.BookMetaData#getSpeed()
     */
    public int getSpeed()
    {
        return driver.getSpeed();
    }

    /**
     * The name
     */
    private LocalURLBookDriver driver;

    /**
     * The properties by which we got our data
     */
    private Properties prop;
    
    /**
     * The location of our home directory
     */
    private URL dir;

    /**
     * The cached bible so we don't have to create too many
     */
    private Bible bible = null;
}
