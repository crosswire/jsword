/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Date;
import java.util.List;

import org.crosswire.common.progress.Progress;
import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.book.install.DownloadException;

import javax.net.ssl.HttpsURLConnection;


/**
 * A WebResource is backed by an URL and potentially the proxy through which it
 * need go. It can get basic information about the resource and it can get the
 * resource. The requests are subject to a timeout, which can be set via the
 * constructor or previously by a call to set the default timeout. The initial
 * default timeout is 750 milliseconds.
 * 
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class WebResource {
    /**
     * Construct a WebResource for the given URL, while timing out if too much
     * time has passed.
     * 
     * @param theURI
     *            the Resource to get via HTTP
     */
    public WebResource(URI theURI) {
        this(theURI, null, null, timeout);
    }

    /**
     * Construct a WebResource for the given URL, while timing out if too much
     * time has passed.
     * 
     * @param theURI
     *            the Resource to get via HTTP
     * @param theTimeout
     *            the length of time in milliseconds to allow a connection to
     *            respond before timing out
     */
    public WebResource(URI theURI, int theTimeout) {
        this(theURI, null, null, theTimeout);
    }

    /**
     * Construct a WebResource for the given URL, going through the optional
     * proxy and default port, while timing out if too much time has passed.
     * 
     * @param theURI
     *            the Resource to get via HTTP
     * @param theProxyHost
     *            the proxy host or null
     */
    public WebResource(URI theURI, String theProxyHost) {
        this(theURI, theProxyHost, null, timeout);
    }

    /**
     * Construct a WebResource for the given URL, going through the optional
     * proxy and default port, while timing out if too much time has passed.
     * 
     * @param theURI
     *            the Resource to get via HTTP
     * @param theProxyHost
     *            the proxy host or null
     * @param theTimeout
     *            the length of time in milliseconds to allow a connection to
     *            respond before timing out
     */
    public WebResource(URI theURI, String theProxyHost, int theTimeout) {
        this(theURI, theProxyHost, null, theTimeout);
    }

    /**
     * Construct a WebResource for the given URL, going through the optional
     * proxy and port, while timing out if too much time has passed.
     * 
     * @param theURI
     *            the Resource to get via HTTP
     * @param theProxyHost
     *            the proxy host or null
     * @param theProxyPort
     *            the proxy port or null, where null means use the standard port
     */
    public WebResource(URI theURI, String theProxyHost, Integer theProxyPort) {
        this(theURI, theProxyHost, theProxyPort, timeout);
    }

    /**
     * Construct a WebResource for the given URL, going through the optional
     * proxy and port, while timing out if too much time has passed.
     * 
     * @param theURI
     *            the Resource to get via HTTP
     * @param theProxyHost
     *            the proxy host or null
     * @param theProxyPort
     *            the proxy port or null, where null means use the standard port
     * @param theTimeout
     *            the length of time in milliseconds to allow a connection to
     *            respond before timing out
     */
    public WebResource(URI theURI, String theProxyHost, Integer theProxyPort, long theTimeout) {
        uri = theURI;
    }

    public static void setHostnameWhitelist(List<String> hostnameWhitelist) {
        WebResource.hostnameWhitelist = hostnameWhitelist;
    }

    /**
     * When this WebResource is no longer needed it should be shutdown to return
     * underlying resources back to the OS.
     */
    public void shutdown() {}

    /**
     * @return the timeout in milliseconds
     */
    public static int getTimeout() {
        return timeout;
    }

    /**
     * @param timeout
     *            the timeout to set in milliseconds
     */
    public static void setTimeout(int timeout) {
        WebResource.timeout = timeout;
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
        try {
            // Execute the method.
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection)uri.toURL().openConnection();
            httpsURLConnection.setRequestMethod("HEAD");
            httpsURLConnection.setConnectTimeout(timeout);
            httpsURLConnection.setReadTimeout(timeout);

            if (httpsURLConnection.getResponseCode() == 200) {
                return httpsURLConnection.getContentLength();
            }
            String reason = httpsURLConnection.getResponseMessage();
            // TRANSLATOR: Common error condition: {0} is a placeholder for the
            // URL of what could not be found.
            Reporter.informUser(this, JSMsg.gettext("Unable to find: {0}", reason + ':' + uri.getPath()));
        } catch (IOException e) {
            return 0;
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
        try {
            // Execute the method.
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection)uri.toURL().openConnection();
            httpsURLConnection.setRequestMethod("HEAD");
            httpsURLConnection.setConnectTimeout(timeout);
            httpsURLConnection.setReadTimeout(timeout);
            if (httpsURLConnection.getResponseCode() == 200) {
                return httpsURLConnection.getLastModified();
            }
            String reason = httpsURLConnection.getResponseMessage();
            // TRANSLATOR: Common error condition: {0} is a placeholder for the
            // URL of what could not be found.
            Reporter.informUser(this, JSMsg.gettext("Unable to find: {0}", reason + ':' + uri.getPath()));
        } catch (IOException e) {
            return new Date().getTime();
        }
        return new Date().getTime();
    }

    /**
     * Copy this WebResource to the destination and report progress.
     * 
     * @param dest
     *            the URI of the destination, typically a file:///.
     * @param meter
     *            the job on which to report progress
     * @throws LucidException when an error is encountered
     */
    public void copy(URI dest, Progress meter) throws LucidException {
        InputStream in = null;
        OutputStream out = null;
        int statusCode = 0;

        try {
            // Execute the method.
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection)uri.toURL().openConnection();
            httpsURLConnection.setConnectTimeout(timeout);
            httpsURLConnection.setReadTimeout(timeout);
            // Initialize the meter, if present
            if (meter != null) {
                // Find out how big it is
                int size = httpsURLConnection.getContentLength();
                // Sometimes the Content-Length is not given and we have to grab it via HEAD method
                if (size == 0) {
                    size = getSize();
                }
                meter.setTotalWork(size);
            }
            statusCode = httpsURLConnection.getResponseCode();
            in = httpsURLConnection.getInputStream();

            // Download the index file
            out = NetUtil.getOutputStream(dest);

            byte[] buf = new byte[4096];
            int count = in.read(buf);
            while (-1 != count) {
                if (meter != null) {
                    meter.incrementWorkDone(count);
                }
                out.write(buf, 0, count);
                count = in.read(buf);
            }
        } catch (IOException e) {
            // TRANSLATOR: Common error condition: {0} is a placeholder for the
            // URL of what could not be found.
            if(statusCode != 0) {
                throw new DownloadException(statusCode);
            }
            throw new LucidException(JSMsg.gettext("Unable to find: {0}", uri.toString()), e);
        } finally {
            // Close the streams
            IOUtil.close(in);
            IOUtil.close(out);
        }
    }

    /**
     * Copy this WebResource to the destination.
     * 
     * @param dest the destination URI
     * @throws LucidException when an error is encountered
     */
    public void copy(URI dest) throws LucidException {
        copy(dest, null);
    }

    /**
     * Define a 750 ms timeout to get a connection
     */
    private static int timeout = 750;

    private URI uri;
    //private CloseableHttpClient client;
    private static List<String> hostnameWhitelist;
}
