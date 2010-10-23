/**
 * Distribution License:
 * BibleDesktop is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Date;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpHost;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.ProxyHost;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.util.HttpURLConnection;

/**
 * A WebResource is backed by an URL and potentially the proxy through which it
 * need go. It can get basic information about the resource and it can get the
 * resource.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class WebResource {
    public WebResource(URI theURI) {
        this(theURI, null);
    }

    public WebResource(URI theURI, String theProxyHost) {
        this(theURI, theProxyHost, null);
    }

    public WebResource(URI theURI, String theProxyHost, Integer theProxyPort) {
        uri = theURI;
        client = new HttpClient();

        // Set a 2 second timeout on getting a connection.
        HttpConnectionManager connectMgr = client.getHttpConnectionManager();
        connectMgr.getParams().setConnectionTimeout(TIMEOUT);

        // Configure the host and port
        HostConfiguration config = client.getHostConfiguration();
        config.setHost(new HttpHost(theURI.getHost(), theURI.getPort()));

        // Configure proxy info if necessary and defined
        if (theProxyHost != null && theProxyHost.length() > 0) {
            config.setProxyHost(new ProxyHost(theProxyHost, theProxyPort == null ? -1 : theProxyPort.intValue()));
        }
    }

    /**
     * Determine the size of this WebResource.
     * <p>
     * Note that the http client may read the entire file to determine this.
     * </p>
     * 
     * @return the size of the file
     */
    public int getSize() {
        HttpMethod method = new HeadMethod(uri.getPath());

        try {
            // Execute the method.
            int status = client.executeMethod(method);
            if (status == HttpStatus.SC_OK) {
                return new HttpURLConnection(method, NetUtil.toURL(uri)).getContentLength();
            }
            String reason = HttpStatus.getStatusText(status);
            // TRANSLATOR: Common error condition: {0} is a placeholder for the URL of what could not be found.
            Reporter.informUser(this, UserMsg.gettext("Unable to find: {0}", new Object[] {
                reason + ':' + uri.getPath()
            }));
        } catch (IOException e) {
            return 0;
        } finally {
            // Release the connection.
            method.releaseConnection();
        }
        return 0;
    }

    /**
     * Determine the last modified date of this WebResource.
     * <p>
     * Note that the http client may read the entire file.
     * </p>
     * 
     * @return the last mod date of the file
     */
    public long getLastModified() {
        HttpMethod method = new HeadMethod(uri.getPath());

        try {
            // Execute the method.
            if (client.executeMethod(method) == HttpStatus.SC_OK) {
                return new HttpURLConnection(method, NetUtil.toURL(uri)).getLastModified();
            }
        } catch (IOException e) {
            return new Date().getTime();
        } finally {
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
    public void copy(URI dest) throws LucidException {
        InputStream in = null;
        OutputStream out = null;

        HttpMethod method = new GetMethod(uri.getPath());

        try {
            // Execute the method.
            int status = client.executeMethod(method);
            if (status == HttpStatus.SC_OK) {
                in = method.getResponseBodyAsStream();

                // Download the index file
                out = NetUtil.getOutputStream(dest);

                byte[] buf = new byte[4096];
                int count = in.read(buf);
                while (-1 != count) {
                    out.write(buf, 0, count);
                    count = in.read(buf);
                }
            } else {
                String reason = HttpStatus.getStatusText(status);
                // TRANSLATOR: Common error condition: {0} is a placeholder for the URL of what could not be found.
                Reporter.informUser(this, UserMsg.gettext("Unable to find: {0}", new Object[] {
                    reason + ':' + uri.getPath()
                }));
            }
        } catch (IOException e) {
            // TRANSLATOR: Common error condition: {0} is a placeholder for the URL of what could not be found.
            throw new LucidException(UserMsg.gettext("Unable to find: {0}", new Object[] {
                uri.toString()
            }), e);
        } finally {
            // Release the connection.
            method.releaseConnection();
            // Close the streams
            IOUtil.close(in);
            IOUtil.close(out);
        }
    }

    /**
     * Define a 750 ms timeout to get a connection
     */
    private static final int TIMEOUT = 750;

    private URI uri;
    private HttpClient client;
}
