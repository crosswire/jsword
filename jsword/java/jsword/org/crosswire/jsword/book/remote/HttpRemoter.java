
package org.crosswire.jsword.book.remote;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.crosswire.jsword.book.Books;
import org.jdom.Document;
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
     * Create an HttpRemoter with a baseurl to call.
     */
    public HttpRemoter(String baseurl)
    {
        if (baseurl == null)
            throw new NullPointerException("baseurl");

        this.baseurl = baseurl;
    }

    /**
     * A simple name
     */
    public String getRemoterName()
    {
        return "Remote (HTTP)";
    }

    /**
     * @see Remoter#execute(RemoteMethod)
     */
    public Document execute(RemoteMethod method) throws RemoterException
    {
        try
        {
            String query = baseurl+methodToParam(method);
            log.debug("Executing query: "+query);

            URL url = new URL(query);
            InputStream in = url.openStream();
            SAXBuilder builder = new SAXBuilder();

            Document doc = builder.build(in);
            log.debug("Counting children of root element: "+doc.getRootElement().getChildren().size());

            return doc;
        }
        catch (Exception ex)
        {
            throw new RemoterException(Msg.REMOTE_FAIL, ex);
        }
    }

    /**
     * How fast are we?
     */
    public int getSpeed()
    {
        return Books.SPEED_REMOTE_SLOW;
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
            // JDK1.4 - will need to say:
            // try { ... URLEncoder.encode(val, "UTF-8"); }
            // catch (UnsupportedEncodingException ex) { throw new LogicError(ex); }

            String key = (String) it.next();
            String val = method.getParameter(key);
            String b64 = URLEncoder.encode(val);
            
            buffer.append("&");
            buffer.append(key);
            buffer.append("=");
            buffer.append(b64);
        }

        return buffer.toString();
    }

    /**
     * The URL that we append to to get valid queries
     */
    private String baseurl;

    /**
     * The log stream
     */
    private static Logger log = Logger.getLogger(HttpRemoter.class);
}
