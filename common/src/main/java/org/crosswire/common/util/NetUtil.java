/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.jar.JarEntry;

/**
 * The NetUtil class looks after general utility stuff around the
 * java.net package.
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author Mark Goodwin [goodwinster at gmail dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public final class NetUtil
{
    /**
     * Basic constructor - ensure that we can't be instansiated
     */
    private NetUtil()
    {
    }

    /**
     * Constant for the file: protocol or scheme
     */
    public static final String PROTOCOL_FILE = "file"; //$NON-NLS-1$

    /**
     * Constant for the http: protocol or scheme
     */
    public static final String PROTOCOL_HTTP = "http"; //$NON-NLS-1$

    /**
     * Constant for the ftp: protocol or scheme
     */
    public static final String PROTOCOL_FTP = "ftp"; //$NON-NLS-1$

    /**
     * Constant for the jar: protocol or scheme
     */
    public static final String PROTOCOL_JAR = "jar"; //$NON-NLS-1$

    /**
     * For directory listings
     */
    public static final String INDEX_FILE = "index.txt"; //$NON-NLS-1$

    /**
     * URL/URI separator
     */
    public static final String SEPARATOR = "/"; //$NON-NLS-1$

    /**
     * Separating the username from the rest of the URL/URI
     */
    public static final String AUTH_SEPERATOR_USERNAME = "@"; //$NON-NLS-1$

    /**
     * Separating the password from the username
     */
    public static final String AUTH_SEPERATOR_PASSWORD = ":"; //$NON-NLS-1$

    /**
     * The temporary suffix, used when a temporary file is needed in the
     * system's temporary directory.
     */
    private static final String TEMP_SUFFIX = "tmp"; //$NON-NLS-1$

    public static URI copy(URI uri)
    {
        try
        {
            return new URI(uri.toString());
        }
        catch (URISyntaxException e)
        {
            assert false : e;
            return null;
        }
    }

    /**
     * If the directory does not exist, create it.
     * Note this currently only works with file: type URIs
     * @param orig The directory URI to create
     */
    public static void makeDirectory(URI orig) throws MalformedURLException
    {
        checkFileURI(orig);

        File file = new File(orig.getPath());

        // If it is a file, except
        if (file.isFile())
        {
            throw new MalformedURLException(Msg.IS_FILE.toString(orig));
        }

        // Is it already a directory ?
        if (!file.isDirectory())
        {
            file.mkdirs();

            // Did that work?
            if (!file.isDirectory())
            {
                throw new MalformedURLException(Msg.CREATE_DIR_FAIL.toString(orig));
            }
        }
    }

    /**
     * If the file does not exist, create it.
     * Note this currently only works with file: type URIs
     * @param orig The file URI to create
     */
    public static void makeFile(URI orig) throws MalformedURLException, IOException
    {
        checkFileURI(orig);

        File file = new File(orig.getPath());

        // If it is a file, except
        if (file.isDirectory())
        {
            throw new MalformedURLException(Msg.IS_DIR.toString(orig));
        }

        // Is it already a directory ?
        if (!file.isFile())
        {
            FileOutputStream fout = new FileOutputStream(file);
            fout.close();

            // Did that work?
            if (!file.isFile())
            {
                throw new MalformedURLException(Msg.CREATE_FILE_FAIL.toString(orig));
            }
        }
    }

    /**
     * If there is a file at the other end of this URI return true.
     * @param uri The URI to check
     * @return true if the URI points at a file
     */
    public static boolean isFile(URI uri)
    {
        if (uri.getScheme().equals(PROTOCOL_FILE))
        {
            return new File(uri.getPath()).isFile();
        }

        try
        {
            // This will throw if the resource does not exist
            uri.toURL().openStream().close();
            return true;
        }
        catch (IOException ex)
        {
            // the resource does not exist!
            return false;
        }
    }

    /**
     * If there is a directory at the other end of this URI return true.
     * Note non file: type URI will always return false
     * @param orig The URI to check
     * @return true if the URI points at a file: directory
     */
    public static boolean isDirectory(URI orig)
    {
        if (!orig.getScheme().equals(PROTOCOL_FILE))
        {
            return false;
        }

        return new File(orig.getPath()).isDirectory();
    }

    /**
     * If there is a writable directory or file at the other end of this URI return true.
     * Note non file: type URIs will always return false
     * @param orig The URI to check
     * @return true if the URI points at a file: directory
     */
    public static boolean canWrite(URI orig)
    {
        if (!orig.getScheme().equals(PROTOCOL_FILE))
        {
            return false;
        }

        return new File(orig.getPath()).canWrite();
    }

    /**
     * Move a URI from one place to another. Currently this only works for
     * file: URIs, however the interface should not need to change to
     * handle more complex URIs
     * @param oldUri The URI to move
     * @param newUri The desitination URI
     */
    public static boolean move(URI oldUri, URI newUri) throws IOException
    {
        checkFileURI(oldUri);
        checkFileURI(newUri);

        File oldFile = new File(oldUri.getPath());
        File newFile = new File(newUri.getPath());
        return oldFile.renameTo(newFile);
    }

    /**
     * Delete a URI. Currently this only works for file: URIs, however
     * the interface should not need to change to handle more complex URIs
     * @param orig The URI to delete
     */
    public static boolean delete(URI orig) throws IOException
    {
        checkFileURI(orig);

        return new File(orig.getPath()).delete();
    }

    /**
     * Return a File from the URI either by extracting from a file: URI or
     * by downloading to a temp dir first
     * @param uri The original URI to the file.
     * @return The URI as a file
     * @throws IOException
     */
    public static File getAsFile(URI uri) throws IOException
    {
        // if the URI is already a file URI, return it
        if (uri.getScheme().equals(PROTOCOL_FILE))
        {
            return new File(uri.getPath());
        }
        String hashString = (uri.toString().hashCode() + "").replace('-', 'm'); //$NON-NLS-1$

        // get the location of the tempWorkingDir
        File workingDir = getURICacheDir();
        File workingFile = null;

        if (workingDir != null && workingDir.isDirectory())
        {
            workingFile = new File(workingDir, hashString);
        }
        else
        {
            // If there's no working dir, we just use temp...
            workingFile = File.createTempFile(hashString, TEMP_SUFFIX);
        }
        workingFile.deleteOnExit();

        // copy the contents of the URI to the file
        OutputStream output = null;
        InputStream input = null;
        try
        {
            output = new FileOutputStream(workingFile);
            input = uri.toURL().openStream();
            byte[] data = new byte[512];
            for (int read = 0; read != -1; read = input.read(data))
            {
                output.write(data, 0, read);
            }
        }
        finally
        {
            try
            {
                if (input != null)
                {
                    input.close();
                }
            }
            finally
            {
                if (output != null)
                {
                    output.close();
                }
            }
        }

        // return the new file in URI form
        return workingFile;
    }

    /**
     * Utility to strip a string from the end of a URI.
     * @param orig The URI to strip
     * @param strip The text to strip from the end of the URI
     * @return The stripped URI
     * @exception MalformedURLException If the URI does not end in the given text
     */
    public static URI shortenURI(URI orig, String strip) throws MalformedURLException
    {
        String file = orig.getPath();
        char lastChar = file.charAt(file.length() - 1);
        if (isSeparator(lastChar))
        {
            file = file.substring(0, file.length() - 1);
        }

        String test = file.substring(file.length() - strip.length());
        if (!test.equals(strip))
        {
            throw new MalformedURLException(Msg.CANT_STRIP.toString(new Object[] { orig, strip }));
        }

        String newFile = file.substring(0, file.length() - strip.length());

        try
        {
            return new URI(orig.getScheme(),
                           orig.getUserInfo(),
                           orig.getHost(),
                           orig.getPort(),
                           newFile,
                           orig.getQuery(),
                           orig.getFragment());
        }
        catch (URISyntaxException e)
        {
            throw new MalformedURLException(Msg.CANT_STRIP.toString(new Object[] { orig, strip }));
        }
    }

    /**
     * Utility to add a string to the end of a URI.
     * @param orig The URI to lengthen
     * @param anExtra The text to add to the end of the URI
     * @return The lengthened URI
     */
    public static URI lengthenURI(URI orig, String anExtra)
    {
        String extra = anExtra;
        try
        {
            char firstChar = extra.charAt(0);
            if (isSeparator(firstChar))
            {
                extra = extra.substring(1);
            }

            if (orig.getScheme().equals(PROTOCOL_FILE))
            {
                String file = orig.toString();
                char lastChar = file.charAt(file.length() - 1);
                if (isSeparator(lastChar))
                {
                    return new URI(orig.getScheme(),
                                   orig.getUserInfo(),
                                   orig.getHost(),
                                   orig.getPort(),
                                   orig.getPath() + extra,
                                   orig.getQuery(),
                                   orig.getFragment());
                }
                return new URI(orig.getScheme(),
                               orig.getUserInfo(),
                               orig.getHost(),
                               orig.getPort(),
                               orig.getPath() + File.separator + extra,
                               orig.getQuery(),
                               orig.getFragment());
            }
            return new URI(orig.getScheme(),
                           orig.getUserInfo(),
                           orig.getHost(),
                           orig.getPort(),
                           orig.getPath() + SEPARATOR + extra,
                           orig.getQuery(),
                           orig.getFragment());
        }
        catch (URISyntaxException ex)
        {
            assert false : ex;
            return null;
        }
    }

    private static boolean isSeparator(char c)
    {
        return c == '/' || c == '\\';
    }

    /**
     * Attempt to obtain an InputStream from a URI. If the URI is a file
     * scheme then just open it directly. Otherwise, call uri.toURL().openStream().
     * @param uri The URI to attempt to read from
     * @return An InputStream connection
     */
    public static InputStream getInputStream(URI uri) throws IOException
    {
        // We favor the FileOutputStream
        if (uri.getScheme().equals(PROTOCOL_FILE))
        {
            return new FileInputStream(uri.getPath());
        }
        return uri.toURL().openStream();
    }

    /**
     * Attempt to obtain an OutputStream from a URI. The simple case will
     * open it if it is local. Otherwise, it will call uri.toURL().openConnection().getOutputStream(), however in some
     * JVMs (MS at least this fails where new FileOutputStream(url) works.
     * @param uri The URI to attempt to write to
     * @return An OutputStream connection
     */
    public static OutputStream getOutputStream(URI uri) throws IOException
    {
        return getOutputStream(uri, false);
    }

    /**
     * Attempt to obtain an OutputStream from a URI. The simple case will
     * open it if it is local. Otherwise, it will call uri.toURL().openConnection().getOutputStream(), however in some
     * JVMs (MS at least this fails where new FileOutputStream(url) works.
     * @param uri The URI to attempt to write to
     * @param append Do we write to the end of the file instead of the beginning
     * @return An OutputStream connection
     */
    public static OutputStream getOutputStream(URI uri, boolean append) throws IOException
    {
        // We favor the FileOutputStream method here because append
        // is not well defined for the openConnection method
        if (uri.getScheme().equals(PROTOCOL_FILE))
        {
            return new FileOutputStream(uri.getPath(), append);
        }
        URLConnection cnx = uri.toURL().openConnection();
        cnx.setDoOutput(true);
        return cnx.getOutputStream();
    }

    /**
     * List the items available assuming that this URI points to a directory.
     * <p>There are 2 methods of calculating the answer - if the URI is a file:
     * URI then we can just use File.list(), otherwise we ask for a file inside
     * the directory called index.txt and assume the directories contents to be
     * listed one per line.
     * <p>If the URI is a file: URI then we execute both methods and warn if
     * there is a difference, but returning the values from the index.txt
     * method.
     */
    public static String[] list(URI uri, URIFilter filter) throws MalformedURLException, IOException
    {
        // We should probably cache this in some way? This is going
        // to get very slow calling this often across a network
        String[] reply = {};
        try
        {
            URI search = NetUtil.lengthenURI(uri, INDEX_FILE);
            reply = listByIndexFile(search, filter);
        }
        catch (FileNotFoundException ex)
        {
            // So the index file was not found - this isn't going to work over
            // JNLP or other systems that can't use file: URIs. But it is silly
            // to get to picky so if there is a solution using file: then just
            // print a warning and carry on.
            log.warn("index file for " + uri.toString() + " was not found."); //$NON-NLS-1$ //$NON-NLS-2$
            if (uri.getScheme().equals(PROTOCOL_FILE))
            {
                return listByFile(uri, filter);
            }
        }

        // if we can - check that the index file is up to date.
        if (uri.getScheme().equals(PROTOCOL_FILE))
        {
            String[] files = listByFile(uri, filter);

            // Check that the answers are the same
            if (files.length != reply.length)
            {
                log.warn("index file for " + uri.toString() + " has incorrect number of entries."); //$NON-NLS-1$ //$NON-NLS-2$
            }
            else
            {
                List list = Arrays.asList(files);
                for (int i = 0; i < files.length; i++)
                {
                    if (!list.contains(files[i]))
                    {
                        log.warn("file: based index found " + files[i] + " but this was not found using index file."); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                }
            }
        }

        return reply;
    }

    /**
     * List all the files specified by the index file passed in.
     * @return String[] Matching results.
     */
    public static String[] listByFile(URI uri, URIFilter filter) throws MalformedURLException
    {
        File fdir = new File(uri.getPath());
        if (!fdir.isDirectory())
        {
            throw new MalformedURLException(Msg.NOT_DIR.toString(uri.toString()));
        }

        return fdir.list(new URIFilterFilenameFilter(filter));
    }

    /**
     * List all the files specified by the index file passed in.
     * @return String[] Matching results.
     * @throws FileNotFoundException
     */
    public static String[] listByIndexFile(URI index, URIFilter filter) throws IOException
    {
        InputStream in = null;
        try
        {
            in = NetUtil.getInputStream(index);
            String contents = StringUtil.read(new InputStreamReader(in));

            // We still need to do the filtering
            List list = new ArrayList();
            String[] names = StringUtil.split(contents, "\n"); //$NON-NLS-1$
            for (int i = 0; i < names.length; i++)
            {
                // we need to trim, as we may have \r\n not \n
                String name = names[i].trim();

                // to be acceptable it must be a non-0 length string, not commented
                // with #, not the index file itself and acceptable by the filter.
                if (name.length() > 0
                    && name.charAt(0) != '#'
                    && !name.equals(INDEX_FILE)
                    && filter.accept(name))
                {
                    list.add(name);
                }
            }

            return (String[]) list.toArray(new String[list.size()]);
        }
        finally
        {
            IOUtil.close(in);
        }
    }

    /**
     * @param uri the resource whose size is wanted
     * @return the size of that resource
     */
    public static int getSize(URI uri)
    {
        return getSize(uri, null, null);
    }
    public static int getSize(URI uri, String proxyHost)
    {
        return getSize(uri, proxyHost, null);
    }
    public static int getSize(URI uri, String proxyHost, Integer proxyPort)
    {
        try
        {
            if (uri.getScheme().equals(PROTOCOL_HTTP))
            {
                return new WebResource(uri, proxyHost, proxyPort).getSize();
            }

            return uri.toURL().openConnection().getContentLength();
        }
        catch (IOException e)
        {
            return 0;
        }
    }

    /**
     * When was the given URI last modified. If no modification time is
     * available then this method return the current time.
     */
    public static long getLastModified(URI uri)
    {
        return getLastModified(uri, null, null);
    }

    public static long getLastModified(URI uri, String proxyHost)
    {
        return getLastModified(uri, proxyHost, null);
    }

    public static long getLastModified(URI uri, String proxyHost, Integer proxyPort)
    {
        try
        {
            if (uri.getScheme().equals(PROTOCOL_HTTP))
            {
                return new WebResource(uri, proxyHost, proxyPort).getLastModified();
            }

            URLConnection urlConnection = uri.toURL().openConnection();
            long time = urlConnection.getLastModified();

            // If it were a jar then time contains the last modified date of the jar.
            if (urlConnection instanceof JarURLConnection)
            {
                // form is jar:file:.../xxx.jar!.../filename.ext
                JarURLConnection jarConnection = (JarURLConnection) urlConnection;
                JarEntry jarEntry = jarConnection.getJarEntry();
                time = jarEntry.getTime();
            }

            return time;
        }
        catch (IOException ex)
        {
            log.warn("Failed to get modified time", ex); //$NON-NLS-1$
            return new Date().getTime();
        }
    }

    /**
     * Returns whether the left is newer than the right by comparing their last
     * modified dates.
     * @param left
     * @param right
     * @return true if the left is newer
     */
    public static boolean isNewer(URI left, URI right)
    {
        return isNewer(left, right, null, null);
    }
    public static boolean isNewer(URI left, URI right, String proxyHost)
    {
        return isNewer(left, right, proxyHost, null);
    }
    public static boolean isNewer(URI left, URI right, String proxyHost, Integer proxyPort)
    {
        return NetUtil.getLastModified(left, proxyHost, proxyPort) > NetUtil.getLastModified(right, proxyHost, proxyPort);
    }

    /**
     * Quick implementation of FilenameFilter that uses a URIFilter
     */
    public static class URIFilterFilenameFilter implements FilenameFilter
    {
        /**
         * Simple ctor
         */
        public URIFilterFilenameFilter(URIFilter filter)
        {
            this.filter = filter;
        }

        /* (non-Javadoc)
         * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
         */
        public boolean accept(File arg0, String name)
        {
            return filter.accept(name);
        }

        private URIFilter filter;
    }

    /**
     * Throw if the given URI does not use the 'file:' protocol
     * @param uri The URI to check
     * @throws MalformedURLException If the protocol is not file:
     */
    private static void checkFileURI(URI uri) throws MalformedURLException
    {
        if (!uri.getScheme().equals(PROTOCOL_FILE))
        {
            throw new MalformedURLException(Msg.NOT_FILE_URI.toString(uri));
        }
    }

    /**
     * Returns the cache directory.
     * @return File
     */
    public static File getURICacheDir()
    {
        return cachedir;
    }

    /**
     * Sets the cache directory.
     * @param cachedir The cache directory to set
     */
    public static void setURICacheDir(File cachedir)
    {
        NetUtil.cachedir = cachedir;
    }

    /**
     * Get a URI version of the given file.
     * @param file The File to turn into a URI
     * @return a URI for the given file
     */
    public static URI getURI(File file)
    {
        return file.toURI();
    }

    /**
     * A URI version of <code>File.createTempFile()</code>
     * @return A new temporary URI
     * @throws IOException If something goes wrong creating the temp URI
     */
    public static URI getTemporaryURI(String prefix, String suffix) throws IOException
    {
        File tempFile = File.createTempFile(prefix, suffix);
        return getURI(tempFile);
    }

    /**
     * Convert an URL to an URI.
     * @param url to convert
     * @return the URI representation of the URL
     */
    public static URI toURI(URL url)
    {
        try
        {
            return new URI(url.toExternalForm());
        }
        catch (URISyntaxException e)
        {
            return null;
        }
    }

    /**
     * Convert an URI to an URL.
     * @param uri to convert
     * @return the URL representation of the URI
     */
    public static URL toURL(URI uri)
    {
        try
        {
            return uri.toURL();
        }
        catch (MalformedURLException e)
        {
            return null;
        }
    }

    /**
     * Check that the directories in the version directory really
     * represent versions.
     */
    public static class IsDirectoryURIFilter implements URIFilter
    {
        /**
         * Simple ctor
         */
        public IsDirectoryURIFilter(URI parent)
        {
            this.parent = parent;
        }

        /* (non-Javadoc)
         * @see org.crosswire.common.util.URLFilter#accept(java.lang.String)
         */
        public boolean accept(String name)
        {
            return NetUtil.isDirectory(NetUtil.lengthenURI(parent, name));
        }

        private URI parent;
    }

    /**
     * Where are temporary files cached.
     */
    private static File cachedir;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(NetUtil.class);
}
