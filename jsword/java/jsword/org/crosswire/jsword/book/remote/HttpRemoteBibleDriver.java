
package org.crosswire.jsword.book.remote;

import java.util.ArrayList;
import java.util.List;

import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.BibleDriver;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.BookException;


/**
 * A fullfilment of RemoteBibleDriver that uses an HTTP commection to
 * communicate.
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
public class HttpRemoteBibleDriver extends RemoteBibleDriver
{
    /**
     * Pass on the exception because RemoteBibleDriver.ctor() could fail due to
     * its ping start-up operation.
     */
    public HttpRemoteBibleDriver(String baseurl) throws RemoterException
    {
        remoter = new HttpRemoter(baseurl);
        ping();
    }

    /**
     * Accessor for the current remoter.
     * @see org.crosswire.jsword.book.remote.RemoteBibleDriver#getRemoter()
     * @return The remoter or null if none is available.
     */
    protected Remoter getRemoter()
    {
        return remoter;
    }

    /**
     * The method by which we talk
     */
    private Remoter remoter = null;

    /**
     * Accessor for the URLs that we talk to.
     * @return String
     */
    public static String[] getURLs()
    {
        return urls;
    }

    /**
     * Accessor for the URLs that we talk to.
     * @param stemp
     */
    public static void setURLs(String[] urls)
    {
        // first unregister all the old drivers
        for (int i=0; i<drivers.length; i++)
        {
            try
            {
                Books.unregisterDriver(drivers[i]);
            }
            catch (BookException ex)
            {
                Reporter.informUser(HttpRemoteBibleDriver.class, ex);
            }
        }

        // Then create and register the new ones
        HttpRemoteBibleDriver.urls = urls;
        List dlist = new ArrayList();
        for (int i=0; i<urls.length; i++)
        {
            try
            {
                BibleDriver driver = new HttpRemoteBibleDriver(urls[i]); 
                dlist.add(driver);
                Books.registerDriver(driver);
            }
            catch (Exception ex)
            {
                Reporter.informUser(HttpRemoteBibleDriver.class, ex);
            }
        }

        // We do this via a temporary list because any drivers that fail to
        // start get excluded, so we shouldn't remember them in case we later
        // unregister() them
        drivers = (HttpRemoteBibleDriver[]) dlist.toArray(new HttpRemoteBibleDriver[dlist.size()]);
    }

    /**
     * An array of the urls that we are currently using.
     */
    private static String[] urls = new String[0];
    
    /**
     * An array of the drivers that we are currently using. 
     */
    private static HttpRemoteBibleDriver[] drivers = new HttpRemoteBibleDriver[0];
}
