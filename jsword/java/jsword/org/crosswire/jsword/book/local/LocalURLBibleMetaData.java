
package org.crosswire.jsword.book.local;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Properties;

import org.crosswire.common.util.NetUtil;
import org.crosswire.jsword.book.BibleMetaData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.basic.AbstractBibleMetaData;

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
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class LocalURLBibleMetaData extends AbstractBibleMetaData
{
    /**
     * Constructor LocalURLBibleMetaData.
     */
    public LocalURLBibleMetaData(LocalURLBookDriver driver, URL dir, BibleMetaData basis) throws BookException, InstantiationException, IllegalAccessException
    {
        super(driver, basis.getName(), basis.getEdition(), basis.getInitials(), basis.getFirstPublished(), basis.getOpenness(), basis.getLicence());

        this.dir = dir;
        this.prop = new Properties();

        LocalURLBible bible = (LocalURLBible) driver.bibleclass.newInstance();
        bible.setLocalURLBibleMetaData(this);
        bible.init();

        setBible(bible);
    }

    /**
     * Basic constructor
     */
    public LocalURLBibleMetaData(LocalURLBookDriver driver, URL dir, Properties prop) throws InstantiationException, IllegalAccessException, BookException, MalformedURLException, ParseException
    {
        super(driver, prop);

        this.dir = dir;
        this.prop = prop;

        LocalURLBible bible = (LocalURLBible) driver.bibleclass.newInstance();
        bible.setLocalURLBibleMetaData(this);
        bible.init();

        setBible(bible);
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

    /**
     * The properties by which we got our data
     */
    private Properties prop;
    
    /**
     * The location of our home directory
     */
    private URL dir;
}
