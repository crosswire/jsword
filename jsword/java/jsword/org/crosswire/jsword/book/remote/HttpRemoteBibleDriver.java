
package org.crosswire.jsword.book.remote;


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
     * Simple ctor
     */
    public HttpRemoteBibleDriver(String baseurl)
    {
        this.baseurl = baseurl;
    }

    /**
     * Sets the baseurl.
     * @param baseurl The baseurl to set
     *
    public void setLocalBaseURL(String baseurl) throws BookException
    {
        // This is a bit nasty - we need to get the singleton of us that was
        // created by the Bibles class so we can unregister and then re-register
        // the Bibles

        if (driver != null)
        {
            BibleMetaData[] bmds = getBibles();
            for (int i=0; i<bmds.length; i++)
            {
                Bibles.removeBible(bmds[i]);
            }
        }

        if (baseurl == null)
        {
            remoter = null;
            driver = null;
            HttpRemoteBibleDriver.baseurl = null;
        }
        else
        {
            try
            {
                remoter = new HttpRemoter(baseurl);
                driver = new HttpRemoteBibleDriver();
                HttpRemoteBibleDriver.baseurl = baseurl;

                BibleMetaData[] bmds = driver.getBibles();
                for (int i=0; i<bmds.length; i++)
                {
                    Bibles.addBible(bmds[i]);
                }
            }
            catch (Exception ex)
            {
                Reporter.informUser(RemoteBibleDriver.class, ex);
            }
        }
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

    private String baseurl = null;

    private static Remoter remoter = null;
}
