
package org.crosswire.jsword.book.remote;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Implement a Remoter using HTTP.
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
public class HttpRemoter implements Remoter
{
    /**
     * A simple name
     */
    public String getRemoterName()
    {
        return "Remote (HTTP)";
    }

    /**
     * Create an HttpRemoter with a baseurl to call.
     */
    public HttpRemoter(String baseurl)
    {
        if (baseurl == null)
            throw new NullPointerException("baseurl");

        this.baseurl = baseurl;
    }

    /**
     * @see Remoter#execute(RemoteMethod)
     */
    public Document execute(RemoteMethod method) throws RemoterException
    {
        try
        {
            URL url = new URL(baseurl+methodToParam(method));
            InputStream in = url.openStream();
            SAXBuilder builder = new SAXBuilder();
            
            Document doc = builder.build(in);
            return doc;
        }
        catch (MalformedURLException ex)
        {
            throw new RemoterException(ex);
        }
        catch (JDOMException ex)
        {
            throw new RemoterException(ex);
        }
        catch (IOException ex)
        {
            throw new RemoterException(ex);
        }
    }

    /**
     * Convert a RemoteMethod to a String which we can append to a base url
     * to get a complete URL which will get us the required XML document.
     */
    public static String methodToParam(RemoteMethod method)
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("?"+RemoteConstants.METHOD_KEY+"=");
        buffer.append(method.getMethodName());
        
        Iterator it = method.getParameterKeys();
        while (it.hasNext())
        {
            String key = (String) it.next();
            String val = method.getParameter(key);

            buffer.append("&");
            buffer.append(key);
            buffer.append("=");
            buffer.append(val);
        }

        return buffer.toString();
    }

    private String baseurl;
}
