
package org.crosswire.jsword.book.ser;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.log4j.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BibleDriverManager;
import org.crosswire.jsword.book.BibleMetaData;
import org.crosswire.jsword.book.Bibles;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.basic.LocalURLBibleDriver;
import org.crosswire.jsword.book.basic.LocalURLBibleMetaData;
import org.crosswire.jsword.book.events.ProgressListener;

/**
 * This represents all of the SerBibles.
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
 * @author Mark Goodwin [mark at thorubio dot org]
 * @version $Id$
 */
public class SerBibleDriver extends LocalURLBibleDriver
{
    /**
     * Some basic driver initialization
     */
    private SerBibleDriver() throws MalformedURLException, IOException
    {
        super("ser");
    }

    /**
     * Some basic info about who we are
     * @param A short identifing string
     */
    public String getDriverName()
    {
        return "Serialized";
    }

    /**
     * Do the real creation using the right meta data
     */
    public Bible getBible(LocalURLBibleMetaData bbmd) throws BookException
    {
        return new SerBible(bbmd);
    }

    /**
     * A new Bible with new source data
     */
    public Bible createBible(LocalURLBibleMetaData lbmd, Bible source, ProgressListener li) throws BookException
    {
        return new SerBible(lbmd, source, li);
    }

    /** The singleton driver */
    protected static SerBibleDriver driver;

    /** The log stream */
    protected static Logger log = Logger.getLogger(SerBibleDriver.class);

    /**
     * Register ourselves with the Driver Manager
     */
    static
    {
        try
        {
            driver = new SerBibleDriver();
            BibleMetaData[] bmds = driver.getBibles();
            for (int i=0; i<bmds.length; i++)
            {
                Bibles.addBible(bmds[i]);
            }

            BibleDriverManager.registerDriver(driver);
        }
        catch (Exception ex)
        {
            Reporter.informUser(SerBibleDriver.class, ex);
        }
    }
}
