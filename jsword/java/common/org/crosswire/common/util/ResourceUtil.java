
package org.crosswire.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Better implemenetations of the getResource methods with less ambiguity and
 * that are less dependent on the specific classloader situation.
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
public class ResourceUtil
{
    /**
     * Prevent Instansiation
     */
    private ResourceUtil()
    {
    }

    /**
     * Generic resource URL fetcher. One way or the other we'll find it!
     * I'm fairly sure some of these do the same thing, but which and how they
     * change on various JDK's is complex, and it seems simpler to take the
     * shotgun approach.
     * @param search The name of the resource (without a leading /) to find
     * @return The requested resource
     * @throws MalformedURLException if the resource can not be found
     */
    public static URL getResource(String search) throws MalformedURLException
    {
        if (search.startsWith("/"))
        {
            ResourceUtil.log.warn("getResource(" + search + ") starts with a /. More chance of success if it doesn't");
        }
    
        URL reply = ResourceUtil.class.getResource(search);
        if (reply != null)
        {
            //log.debug("getResource("+search+") = "+reply+" using getClass().getResource(search);");
            return reply;
        }
    
        reply = ResourceUtil.class.getResource("/"+search);
        if (reply != null)
        {
            //log.debug("getResource("+search+") = "+reply+" using getClass().getResource(/search);");
            return reply;
        }
    
        reply = ResourceUtil.class.getClassLoader().getResource(search);
        if (reply != null)
        {
            //log.debug("getResource("+search+") = "+reply+" using getClass().getClassLoader().getResource(search);");
            return reply;
        }
    
        reply = ResourceUtil.class.getClassLoader().getResource("/"+search);
        if (reply != null)
        {
            //log.debug("getResource("+search+") = "+reply+" using getClass().getClassLoader().getResource(/search);");
            return reply;
        }

        reply = ClassLoader.getSystemResource(search);
        if (reply != null)
        {
            //log.debug("getResource("+search+") = "+reply+" using ClassLoader.getSystemResource(search);");
            return reply;
        }

        reply = ClassLoader.getSystemResource("/"+search);
        if (reply != null)
        {
            //log.debug("getResource("+search+") = "+reply+" using ClassLoader.getSystemResource(/search);");
            return reply;
        }
    
        throw new MalformedURLException("Can't find resource: "+search);
    }

    /**
     * Generic resource URL fetcher
     * @return The requested resource
     * @throws IOException if there is a problem reading the file
     * @throws MalformedURLException if the resource can not be found
     */
    public static InputStream getResourceAsStream(String search) throws IOException, MalformedURLException
    {
        URL url = ResourceUtil.getResource(search);
        return url.openStream();
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(ResourceUtil.class);
}
