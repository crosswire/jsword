package org.crosswire.jsword.book.remote;

import java.util.ArrayList;
import java.util.List;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Books;

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
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class HttpRemoteBookDriver extends RemoteBookDriver
{
    /**
     * Pass on the exception because RemoteBibleDriver.ctor() could fail due to
     * its ping start-up operation.
     */
    public HttpRemoteBookDriver(String baseurl) throws RemoterException
    {
        remoter = new HttpRemoter(baseurl);
        ping();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.remote.RemoteBookDriver#getRemoter()
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
        String[] copy = new String[urls.length];
        for (int i = 0; i < copy.length; i++)
        {
            copy[i] = urls[i];
        }

        return copy;
    }

    /**
     * Accessor for the URLs that we talk to.
     */
    public static void setURLs(String[] urls)
    {
        // first unregister all the old drivers
        for (int i=0; i<drivers.length; i++)
        {
            try
            {
                Books.installed().unregisterDriver(drivers[i]);
            }
            catch (BookException ex)
            {
                Reporter.informUser(HttpRemoteBookDriver.class, ex);
            }
        }

        // Then create and register the new ones
        HttpRemoteBookDriver.urls = urls;
        List dlist = new ArrayList();
        for (int i=0; i<urls.length; i++)
        {
            try
            {
                BookDriver driver = new HttpRemoteBookDriver(urls[i]); 
                dlist.add(driver);
                Books.installed().registerDriver(driver);
            }
            catch (Exception ex)
            {
                log.warn("Failed to start driver using: "+urls[i]); //$NON-NLS-1$
            }
        }

        // We do this via a temporary list because any drivers that fail to
        // start get excluded, so we shouldn't remember them in case we later
        // unregister() them
        drivers = (HttpRemoteBookDriver[]) dlist.toArray(new HttpRemoteBookDriver[dlist.size()]);
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(HttpRemoteBookDriver.class);

    /**
     * An array of the urls that we are currently using.
     */
    private static String[] urls = new String[0];
    
    /**
     * An array of the drivers that we are currently using. 
     */
    private static HttpRemoteBookDriver[] drivers = new HttpRemoteBookDriver[0];
}
