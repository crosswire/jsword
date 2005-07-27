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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
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
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author Mark Goodwin
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
     * Constant for the file: protocol
     */
    public static final String PROTOCOL_FILE = "file"; //$NON-NLS-1$

    /**
     * Constant for the file: protocol
     */
    public static final String PROTOCOL_HTTP = "http"; //$NON-NLS-1$

    /**
     * Constant for the file: protocol
     */
    public static final String PROTOCOL_FTP = "ftp"; //$NON-NLS-1$

    /**
     * Constant for the jar: protocol
     */
    public static final String PROTOCOL_JAR = "jar"; //$NON-NLS-1$

    /**
     * For directory listings
     */
    public static final String INDEX_FILE = "index.txt"; //$NON-NLS-1$

    /**
     * URL separator
     */
    public static final String SEPARATOR = "/"; //$NON-NLS-1$

    /**
     * Separating the username from the rest of the URL
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

    /**
     * If the directory does not exist, create it.
     * Note this currently only works with file: type URLs
     * @param orig The URL to check
     */
    public static void makeDirectory(URL orig) throws MalformedURLException
    {
        checkFileURL(orig);

        File file = new File(orig.getFile());

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
     * Note this currently only works with file: type URLs
     * @param orig The URL to check
     */
    public static void makeFile(URL orig) throws MalformedURLException, IOException
    {
        checkFileURL(orig);

        File file = new File(orig.getFile());

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
     * If there is a file at the other end of this URL return true.
     * Note this currently only works with file: type URLs
     * @param url The URL to check
     * @return true if the URL points at a file
     */
    public static boolean isFile(URL url)
    {
        if (url.getProtocol().equals(PROTOCOL_FILE))
        {
            File file = new File(url.getFile());
            return file.isFile();
        }

        try
        {
            // This will throw if the resource does not exist
            InputStream is = url.openStream();
            is.close();
            return true;
        }
        catch (IOException ex)
        {
            // the resource does not exist!
            return false;
        }
    }

    /**
     * If there is a directory at the other end of this URL return true.
     * Note non file: type URLs will always return false
     * @param orig The URL to check
     * @return true if the URL points at a file: directory
     */
    public static boolean isDirectory(URL orig)
    {
        if (!orig.getProtocol().equals(PROTOCOL_FILE))
        {
            return false;
        }

        File file = new File(orig.getFile());
        return file.isDirectory();
    }

    /**
     * Move a URL from one place to another. Currently this only works for
     * file: URLs, however the interface should not need to change to
     * handle more complex URLs
     * @param oldUrl The URL to move
     * @param newUrl The desitination URL
     */
    public static boolean move(URL oldUrl, URL newUrl) throws IOException
    {
        checkFileURL(oldUrl);
        checkFileURL(newUrl);

        File oldFile = new File(oldUrl.getFile());
        File newFile = new File(newUrl.getFile());
        return oldFile.renameTo(newFile);
    }

    /**
     * Delete a URL. Currently this only works for file: URLs, however
     * the interface should not need to change to handle more complex URLs
     * @param orig The URL to delete
     */
    public static boolean delete(URL orig) throws IOException
    {
        checkFileURL(orig);

        File file = new File(orig.getFile());
        return file.delete();
    }

    /**
     * Return a File from the URL either by extracting from a file: URL or
     * by downloading to a temp dir first
     * @param url The original URL to the file.
     * @return The URL as a file
     * @throws IOException 
     */
    public static File getAsFile(URL url) throws IOException
    {
        // if the URL is already a file URL, return it
        if (url.getProtocol().equals(PROTOCOL_FILE))
        {
            return new File(url.getFile());
        }
        String hashString = (url.toExternalForm().hashCode() + "").replace('-', 'm'); //$NON-NLS-1$

        // get the location of the tempWorkingDir
        File workingDir = getURLCacheDir();
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

        // copy the contents of the URL to the file
        OutputStream output = null;
        InputStream input = null;
        try
        {
            output = new FileOutputStream(workingFile);
            input = url.openStream();
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

        // return the new file in URL form
        return workingFile;
    }

    /**
     * Utility to strip a string from the end of a URL.
     * @param orig The URL to strip
     * @param strip The text to strip from the end of the URL
     * @return The stripped URL
     * @exception MalformedURLException If the URL does not end in the given text
     */
    public static URL shortenURL(URL orig, String strip) throws MalformedURLException
    {
        String file = orig.getFile();
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

        return new URL(orig.getProtocol(),
                       orig.getHost(),
                       orig.getPort(),
                       newFile);
    }

    /**
     * Utility to add a string to the end of a URL.
     * @param orig The URL to strip
     * @param anExtra The text to add to the end of the URL
     * @return The stripped URL
     */
    public static URL lengthenURL(URL orig, String anExtra)
    {
        String extra = anExtra;
        try
        {
            char firstChar = extra.charAt(extra.length() - 1);
            if (isSeparator(firstChar))
            {
                extra = extra.substring(1);
            }

            if (orig.getProtocol().equals(PROTOCOL_FILE))
            {
                String file = orig.toExternalForm();
                char lastChar = file.charAt(file.length() - 1);
                if (isSeparator(lastChar))
                {
                    return new URL(orig.getProtocol(),
                                   orig.getHost(),
                                   orig.getPort(),
                                   orig.getFile() + extra);
                }
                return new URL(orig.getProtocol(),
                               orig.getHost(),
                               orig.getPort(),
                               orig.getFile() + File.separator + extra);
            }
            return new URL(orig.getProtocol(),
                           orig.getHost(),
                           orig.getPort(),
                           orig.getFile() + SEPARATOR + extra);
        }
        catch (MalformedURLException ex)
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
     * Attempt to obtain an OutputStream from a URL. The simple case will
     * just call url.openConnection().getOutputStream(), however in some
     * JVMs (MS at least this fails where new FileOutputStream(url) works.
     * So if openConnection().getOutputStream() fails and the protocol is
     * file, then the alternate version is used.
     * @param url The URL to attempt to write to
     * @return An OutputStream connection
     */
    public static OutputStream getOutputStream(URL url) throws IOException
    {
        return getOutputStream(url, false);
    }

    /**
     * Attempt to obtain an OutputStream from a URL. The simple case will
     * just call url.openConnection().getOutputStream(), however in some
     * JVMs (MS at least this fails where new FileOutputStream(url) works.
     * So if openConnection().getOutputStream() fails and the protocol is
     * file, then the alternate version is used.
     * @param url The URL to attempt to write to
     * @param append Do we write to the end of the file instead of the beginning
     * @return An OutputStream connection
     */
    public static OutputStream getOutputStream(URL url, boolean append) throws IOException
    {
        // We favour the FileOutputStream method here because append
        // is not well defined for the openConnection method
        if (url.getProtocol().equals(PROTOCOL_FILE))
        {
            return new FileOutputStream(url.getFile(), append);
        }
        URLConnection cnx = url.openConnection();
        cnx.setDoOutput(true);
        return cnx.getOutputStream();
    }

    /**
     * List the items available assuming that this URL points to a directory.
     * <p>There are 2 methods of calculating the answer - if the URL is a file:
     * URL then we can just use File.list(), otherwise we ask for a file inside
     * the directory called index.txt and assume the directories contents to be
     * listed one per line.
     * <p>If the URL is a file: URL then we execute both methods and warn if
     * there is a difference, but returning the values from the index.txt
     * method.
     */
    public static String[] list(URL url, URLFilter filter) throws MalformedURLException, IOException
    {
        // We should probably cache this in some way? This is going
        // to get very slow calling this often across a network
        String[] reply = {};
        try
        {
            URL search = NetUtil.lengthenURL(url, INDEX_FILE);
            reply = listByIndexFile(search, filter);
        }
        catch (FileNotFoundException ex)
        {
            // So the index file was not found - this isn't going to work over
            // JNLP or other systems that can't use file: URLs. But it is silly
            // to get to picky so if there is a solution using file: then just
            // print a warning and carry on.
            log.warn("index file for " + url.toExternalForm() + " was not found."); //$NON-NLS-1$ //$NON-NLS-2$
            if (url.getProtocol().equals(PROTOCOL_FILE))
            {
                return listByFile(url, filter);
            }
        }

        // if we can - check that the index file is up to date.
        if (url.getProtocol().equals(PROTOCOL_FILE))
        {
            String[] files = listByFile(url, filter);

            // Check that the answers are the same
            if (files.length != reply.length)
            {
                log.warn("index file for " + url.toExternalForm() + " has incorrect number of entries."); //$NON-NLS-1$ //$NON-NLS-2$
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
    public static String[] listByFile(URL url, URLFilter filter) throws MalformedURLException
    {
        File fdir = new File(url.getFile());
        if (!fdir.isDirectory())
        {
            throw new MalformedURLException(Msg.NOT_DIR.toString(url.toExternalForm()));
        }

        return fdir.list(new URLFilterFilenameFilter(filter));
    }

    /**
     * List all the files specified by the index file passed in.
     * @return String[] Matching results.
     */
    public static String[] listByIndexFile(URL index, URLFilter filter) throws IOException
    {
        InputStream in = index.openStream();
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

    /**
     * @param url the resource whose size is wanted
     * @return the size of that resource
     */
    public static int getSize(URL url)
    {
        try
        {
            URLConnection urlConnection = url.openConnection();
            int size = urlConnection.getContentLength();
            return size;
        }
        catch (IOException e)
        {
            return 0;
        }
    }

    /**
     * When was the given URL last modified. If no modification time is
     * available then this method return the current time.
     */
    public static long getLastModified(URL url)
    {
        try
        {
            URLConnection urlConnection = url.openConnection();
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
    public static boolean isNewer(URL left, URL right)
    {
        return NetUtil.getLastModified(left) > NetUtil.getLastModified(right);
    }

    /**
     * Quick implementation of FilenameFilter that uses a URLFilter
     */
    public static class URLFilterFilenameFilter implements FilenameFilter
    {
        /**
         * Simple ctor
         */
        public URLFilterFilenameFilter(URLFilter filter)
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

        private URLFilter filter;
    }

    /**
     * Throw if the given URL does not use the 'file:' protocol
     * @param url The URL to check
     * @throws MalformedURLException If the protocol is not file:
     */
    private static void checkFileURL(URL url) throws MalformedURLException
    {
        if (!url.getProtocol().equals(PROTOCOL_FILE))
        {
            throw new MalformedURLException(Msg.NOT_FILE_URL.toString(url));
        }
    }

    /**
     * Returns the URLCacheDir.
     * @return File
     */
    public static File getURLCacheDir()
    {
        return cachedir;
    }

    /**
     * Sets the cache directory.
     * @param cachedir The URLCacheDir to set
     */
    public static void setURLCacheDir(File cachedir)
    {
        NetUtil.cachedir = cachedir;
    }

    /**
     * Get a URL version of the given file.
     * @param file The File to turn into a URL
     * @return a URL for the given file
     */
    public static URL getURL(File file)
    {
        try
        {
            return new URL(PROTOCOL_FILE, null, -1, file.getCanonicalPath());
        }
        catch (IOException ex)
        {
            log.error("Failed to create URL", ex); //$NON-NLS-1$
            assert false;
            throw new IllegalArgumentException(ex.toString());
        }
    }

    /**
     * A URL version of <code>File.createTempFile()</code>
     * @return A new temporary URL
     * @throws IOException If something goes wrong creating the temp URL
     */
    public static URL getTemporaryURL(String prefix, String suffix) throws IOException
    {
        File tempFile = File.createTempFile(prefix, suffix);
        return getURL(tempFile);
    }

    /**
     * Check that the directories in the version directory really
     * represent versions.
     */
    public static class IsDirectoryURLFilter implements URLFilter
    {
        /**
         * Simple ctor
         */
        public IsDirectoryURLFilter(URL parent)
        {
            this.parent = parent;
        }

        /* (non-Javadoc)
         * @see org.crosswire.common.util.URLFilter#accept(java.lang.String)
         */
        public boolean accept(String name)
        {
            return NetUtil.isDirectory(NetUtil.lengthenURL(parent, name));
        }

        private URL parent;
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
