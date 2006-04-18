/**
 * Distribution License: JSword is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License, version
 * 2.1 as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details. The License is
 * available on the internet at: http://www.gnu.org/copyleft/lgpl.html or by
 * writing to: Free Software Foundation, Inc. 59 Temple Place - Suite 330
 * Boston, MA 02111-1307, USA Copyright: 2005 The copyright to this program is
 * held by it's authors. ID: $Id: URLFilter.java,v 1.5 2005/07/27 23:26:42
 * dmsmith Exp $
 */
package org.crosswire.common.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpHost;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.ProxyHost;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.HttpURLConnection;

/**
 * A WebResource is backed by an URL and potentially the proxy through which it
 * need go. It can get basic information about the resource and it can get the
 * resource.
 * 
 * @see gnu.lgpl.License for license details.<br> The copyright to this program is
 *      held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class WebResource
{
    public WebResource(URL theURL)
    {
        this(theURL, null);
    }

    public WebResource(URL theURL, String theProxyHost)
    {
        this(theURL, theProxyHost, null);
    }

    public WebResource(URL theURL, String theProxyHost, Integer theProxyPort)
    {
        url = theURL;
        client = new HttpClient();
        HostConfiguration config = client.getHostConfiguration();
        config.setHost(new HttpHost(theURL.getHost(), theURL.getPort()));
        if (theProxyHost != null && theProxyHost.length() > 0)
        {
            config.setProxyHost(new ProxyHost(theProxyHost, theProxyPort == null ? -1 : theProxyPort.intValue()));
        }
    }

    public int getSize()
    {
        HttpMethod method = new GetMethod(url.getQuery());

        try
        {
            // Execute the method.
            if (client.executeMethod(method) == HttpStatus.SC_OK)
            {
                HttpURLConnection connection = new HttpURLConnection(method, url);
                return connection.getContentLength();
            }
        }
        catch (Exception e)
        {
            return 0;
        }
        finally
        {
            // Release the connection.
            method.releaseConnection();
        }
        return 0;
    }

    public long getLastModified()
    {
        HttpMethod method = new GetMethod(url.getQuery());

        try
        {
            // Execute the method.
            if (client.executeMethod(method) == HttpStatus.SC_OK)
            {
                HttpURLConnection connection = new HttpURLConnection(method, url);
                return connection.getLastModified();
            }
        }
        catch (Exception e)
        {
            return new Date().getTime();
        }
        finally
        {
            // Release the connection.
            method.releaseConnection();
        }
        return new Date().getTime();
    }

    /**
     * Copy this WebResource to the destination.
     * 
     * @param dest
     * @throws LucidException
     */
    public void copy(URL dest) throws LucidException
    {
        InputStream in = null;
        OutputStream out = null;

        HttpMethod method = new GetMethod(url.getPath());

        try
        {
            // Execute the method.
            if (client.executeMethod(method) == HttpStatus.SC_OK)
            {
                in = method.getResponseBodyAsStream();

                // Download the index file
                out = NetUtil.getOutputStream(dest);

                byte[] buf = new byte[4096];
                int count = in.read(buf);
                while (-1 != count)
                {
                    out.write(buf, 0, count);
                    count = in.read(buf);
                }
            }
        }
        catch (Exception e)
        {
            throw new LucidException(Msg.MISSING_FILE, e);
        }
        finally
        {
            // Release the connection.
            method.releaseConnection();
            // Close the streams
            IOUtil.close(in);
            IOUtil.close(out);
        }
    }

    private URL url;
    private HttpClient client;
}
