
package org.crosswire.jsword.book.remote;

import java.io.IOException;
import java.net.MalformedURLException;

import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.BibleDriverManager;

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
     * Returns the baseurl.
     * @return String
     */
    public static String getBaseURL()
    {
        return baseurl;
    }

    /**
     * Sets the baseurl.
     * @param baseurl The baseurl to set
     */
    public static void setBaseURL(String baseurl)
    {
        HttpRemoteBibleDriver.baseurl = baseurl;
        
        if (baseurl == null)
            remoter = null;
        else
            remoter = new HttpRemoter(baseurl);
    }

    /**
     * 
     */
    private HttpRemoteBibleDriver() throws MalformedURLException, IOException
    {
    }

    /**
     * Accessor for the current remoter.
     * @see org.crosswire.jsword.book.remote.RemoteBibleDriver#getXML(java.lang.String)
     * @return The remoter or null if none is available.
     */
    protected Remoter getRemoter()
    {
        return remoter;
    }

    /**
     * @see org.crosswire.jsword.book.BibleDriver#getDriverName()
     */
    public String getDriverName()
    {
        return "Remote (HTTP)";
    }

    private static String baseurl = null;

    private static Remoter remoter = new HttpRemoter(baseurl);

    private static HttpRemoteBibleDriver driver;

    /**
     * Register ourselves with the Driver Manager
     */
    static
    {
        try
        {
            driver = new HttpRemoteBibleDriver();
            BibleDriverManager.registerDriver(driver);
        }
        catch (Exception ex)
        {
            Reporter.informUser(RemoteBibleDriver.class, ex);
        }
    }
}
