
package org.crosswire.jsword.book.raw;

import java.io.IOException;
import java.net.MalformedURLException;

import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BibleDriverManager;
import org.crosswire.jsword.book.BibleMetaData;
import org.crosswire.jsword.book.Bibles;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.basic.LocalURLBibleDriver;
import org.crosswire.jsword.book.basic.LocalURLBibleMetaData;
import org.crosswire.jsword.book.events.ProgressListener;

/**
 * This represents all of the RawBibles.
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
public class RawBibleDriver extends LocalURLBibleDriver
{
    /**
     * Do the Bibles we create cache everything in memory or leave it on
     * disk and then read it at query time.
     * @return True if we are cacheing data in memory
     */
    public static boolean getDefaultCacheData()
    {
        return memory;
    }

    /**
     * Do the Bibles we create cache everything in memory or leave it on
     * disk and then read it at query time.
     * @param memory True if we are cacheing data in memory
     */
    public static void setDefaultCacheData(boolean memory)
    {
        RawBibleDriver.memory = memory;
    }

    /**
     * Some basic driver initialization
     */
    private RawBibleDriver() throws MalformedURLException, IOException
    {
        super("raw");
    }

    /**
     * Some basic info about who we are
     * @param A short identifing string
     */
    public String getDriverName()
    {
        return "Raw";
    }

    /**
     * Do the real creation using the right meta data
     */
    public Bible getBible(LocalURLBibleMetaData bbmd) throws BookException
    {
        return new RawBible(bbmd);
    }

    /**
     * A new Bible with new source data
     */
    public Bible createBible(LocalURLBibleMetaData lbmd, Bible source, ProgressListener li) throws BookException
    {
        return new RawBible(lbmd, source, li);
    }

    /** The singleton driver */
    protected static RawBibleDriver driver;

    /** Do we instruct new RawBibles to cache data in memory? */
    private static boolean memory = true;

    /**
     * Register ourselves with the Driver Manager
     */
    static
    {
        try
        {
            driver = new RawBibleDriver();
            BibleMetaData[] bmds = driver.getBibles();
            for (int i=0; i<bmds.length; i++)
            {
                Bibles.addBible(bmds[i]);
            }

            BibleDriverManager.registerDriver(driver);
        }
        catch (Exception ex)
        {
            log.info("RawBibleDriver init failure", ex);
        }
    }
}
