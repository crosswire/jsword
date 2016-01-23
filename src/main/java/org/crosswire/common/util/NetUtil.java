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

import java.io.BufferedReader;
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

import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.JSOtherMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The NetUtil class looks after general utility stuff around the java.net
 * package.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author Mark Goodwin
 * @author DM Smith
 */
public final class NetUtil {
    /**
     * Basic constructor - ensure that we can't be instantiated
     */
    private NetUtil() {
    }

    /**
     * Constant for the file: protocol or scheme
     */
    public static final String PROTOCOL_FILE = "file";

    /**
     * Constant for the http: protocol or scheme
     */
    public static final String PROTOCOL_HTTP = "http";

    /**
     * Constant for the ftp: protocol or scheme
     */
    public static final String PROTOCOL_FTP = "ftp";

    /**
     * Constant for the jar: protocol or scheme
     */
    public static final String PROTOCOL_JAR = "jar";

    /**
     * For directory listings
     */
    public static final String INDEX_FILE = "index.txt";

    /**
     * URL/URI separator
     */
    public static final String SEPARATOR = "/";

    /**
     * Separating the username from the rest of the URL/URI
     */
    public static final String AUTH_SEPERATOR_USERNAME = "@";

    /**
     * Separating the password from the username
     */
    public static final String AUTH_SEPERATOR_PASSWORD = ":";

    /**
     * The temporary suffix, used when a temporary file is needed in the
     * system's temporary directory.
     */
    private static final String TEMP_SUFFIX = "tmp";

    public static URI copy(URI uri) {
        try {
            return new URI(uri.toString());
        } catch (URISyntaxException e) {
            assert false : e;
            return null;
        }
    }

    /**
     * If the directory does not exist, create it. Note this currently only
     * works with file: type URIs
     * 
     * @param orig
     *            The directory URI to create
     * @throws MalformedURLException
     *                If the URI is not valid
     */
    public static void makeDirectory(URI orig) throws MalformedURLException {
        checkFileURI(orig);

        File file = new File(orig.getPath());

        // If it is a file, except
        if (file.isFile()) {
            // TRANSLATOR: Error condition: A directory was expected, but a file was found. {0} is a placeholder for the file.
            throw new MalformedURLException(JSMsg.gettext("The URL {0} is a file.", orig));
        }

        // Is it already a directory ?
        if (!file.isDirectory()) {
            // Create the directory and make sure it worked.
            if (!file.mkdirs()) {
                // TRANSLATOR: Error condition: A directory could not be created. {0} is a placeholder for the directory
                throw new MalformedURLException(JSMsg.gettext("The URL {0} could not be created as a directory.", orig));
            }
        }
    }

    /**
     * If the file does not exist, create it. Note this currently only works
     * with file: type URIs
     * 
     * @param orig
     *            The file URI to create
     * @throws MalformedURLException
     *                If the URI is not valid
     * @throws IOException a problem with I/O happened
     */
    public static void makeFile(URI orig) throws MalformedURLException, IOException {
        checkFileURI(orig);

        File file = new File(orig.getPath());

        // If it is a file, except
        if (file.isDirectory()) {
            // TRANSLATOR: Error condition: A file was expected, but a directory was found. {0} is a placeholder for the directory.
            throw new MalformedURLException(JSMsg.gettext("The URL {0} is a directory.", orig));
        }

        // Is it already a directory ?
        if (!file.isFile()) {
            FileOutputStream fout = new FileOutputStream(file);
            fout.close();

            // Did that work?
            if (!file.isFile()) {
                // TRANSLATOR: Error condition: A file could not be created. {0} is a placeholder for the file
                throw new MalformedURLException(JSMsg.gettext("The URL {0} could not be created as a file.", orig));
            }
        }
    }

    /**
     * If there is a file at the other end of this URI return true.
     * 
     * @param uri
     *            The URI to check
     * @return true if the URI points at a file
     */
    public static boolean isFile(URI uri) {
        if (uri.getScheme().equals(PROTOCOL_FILE)) {
            return new File(uri.getPath()).isFile();
        }

        try {
            // This will throw if the resource does not exist
            uri.toURL().openStream().close();
            return true;
        } catch (IOException ex) {
            // the resource does not exist!
            return false;
        }
    }

    /**
     * If there is a directory at the other end of this URI return true. Note
     * non file: type URI will always return false
     * 
     * @param orig
     *            The URI to check
     * @return true if the URI points at a file: directory
     */
    public static boolean isDirectory(URI orig) {
        if (!orig.getScheme().equals(PROTOCOL_FILE)) {
            return false;
        }

        return new File(orig.getPath()).isDirectory();
    }

    /**
     * If there is a writable directory or file at the other end of this URI
     * return true. Note non file: type URIs will always return false
     * 
     * @param orig
     *            The URI to check
     * @return true if the URI points at a writable file or directory
     */
    public static boolean canWrite(URI orig) {
        if (!orig.getScheme().equals(PROTOCOL_FILE)) {
            return false;
        }

        return new File(orig.getPath()).canWrite();
    }

    /**
     * If there is a readable directory or file at the other end of this URI
     * return true. Note non file: type URIs will always return false
     * 
     * @param orig
     *            The URI to check
     * @return true if the URI points at a readable file or directory
     */
    public static boolean canRead(URI orig) {
        if (!orig.getScheme().equals(PROTOCOL_FILE)) {
            return false;
        }

        return new File(orig.getPath()).canRead();
    }

    /**
     * Move a URI from one place to another. Currently this only works for file:
     * URIs, however the interface should not need to change to handle more
     * complex URIs
     * 
     * @param oldUri
     *            The URI to move
     * @param newUri
     *            The destination URI
     * @return whether the move happened
     * @throws IOException a problem with I/O happened
     */
    public static boolean move(URI oldUri, URI newUri) throws IOException {
        checkFileURI(oldUri);
        checkFileURI(newUri);

        File oldFile = new File(oldUri.getPath());
        File newFile = new File(newUri.getPath());
        return oldFile.renameTo(newFile);
    }

    /**
     * Delete a URI. Currently this only works for file: URIs, however the
     * interface should not need to change to handle more complex URIs
     * 
     * @param orig
     *            The URI to delete
     * @return whether the deleted happened
     * @throws IOException a problem with I/O happened
     */
    public static boolean delete(URI orig) throws IOException {
        checkFileURI(orig);

        return new File(orig.getPath()).delete();
    }

    /**
     * Return a File from the URI either by extracting from a file: URI or by
     * downloading to a temp dir first
     * 
     * @param uri
     *            The original URI to the file.
     * @return The URI as a file
     * @throws IOException a problem with I/O happened
     */
    public static File getAsFile(URI uri) throws IOException {
        // if the URI is already a file URI, return it
        if (uri.getScheme().equals(PROTOCOL_FILE)) {
            return new File(uri.getPath());
        }
        String hashString = (uri.toString().hashCode() + "").replace('-', 'm');

        // get the location of the tempWorkingDir
        File workingDir = getURICacheDir();
        File workingFile = null;

        if (workingDir != null && workingDir.isDirectory()) {
            workingFile = new File(workingDir, hashString);
        } else {
            // If there's no working dir, we just use temp...
            workingFile = File.createTempFile(hashString, TEMP_SUFFIX);
        }
        workingFile.deleteOnExit();

        // copy the contents of the URI to the file
        OutputStream output = null;
        InputStream input = null;
        try {
            output = new FileOutputStream(workingFile);
            input = uri.toURL().openStream();
            byte[] data = new byte[512];
            for (int read = 0; read != -1; read = input.read(data)) {
                output.write(data, 0, read);
            }
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } finally {
                if (output != null) {
                    output.close();
                }
            }
        }

        // return the new file in URI form
        return workingFile;
    }

    /**
     * Utility to strip a string from the end of a URI.
     * 
     * @param orig
     *            The URI to strip
     * @param strip
     *            The text to strip from the end of the URI
     * @return The stripped URI
     * @exception MalformedURLException
     *                If the URI does not end in the given text
     */
    public static URI shortenURI(URI orig, String strip) throws MalformedURLException {
        String file = orig.getPath();
        char lastChar = file.charAt(file.length() - 1);
        if (isSeparator(lastChar)) {
            file = file.substring(0, file.length() - 1);
        }

        String test = file.substring(file.length() - strip.length());
        if (!test.equals(strip)) {
            throw new MalformedURLException(JSOtherMsg.lookupText("The URL {0} does not end in {1}.", orig, strip));
        }

        String newFile = file.substring(0, file.length() - strip.length());

        try {
            return new URI(orig.getScheme(), orig.getUserInfo(), orig.getHost(), orig.getPort(), newFile, "",
                    "");
        } catch (URISyntaxException e) {
            throw new MalformedURLException(JSOtherMsg.lookupText("The URL {0} does not end in {1}.", orig, strip));
        }
    }

    /**
     * Utility to add a string to the end of a URI.
     * 
     * @param orig
     *            The URI to lengthen
     * @param anExtra
     *            The text to add to the end of the URI
     * @return The lengthened URI
     */
    public static URI lengthenURI(URI orig, String anExtra) {
        String extra = anExtra;
        try {
            StringBuilder path = new StringBuilder(orig.getPath());
            char lastChar = path.charAt(path.length() - 1);
            char firstChar = extra.charAt(0);
            if (isSeparator(firstChar)) {
                if (isSeparator(lastChar)) {
                    path.append(extra.substring(1));
                } else {
                    path.append(extra);
                }
            } else {
                if (!isSeparator(lastChar)) {
                    path.append(SEPARATOR);
                }
                path.append(extra);
            }

            return new URI(orig.getScheme(), orig.getUserInfo(), orig.getHost(), orig.getPort(), path.toString(), orig.getQuery(), orig.getFragment());
        } catch (URISyntaxException ex) {
            return null;
        }
    }

    private static boolean isSeparator(char c) {
        return c == '/' || c == '\\';
    }

    /**
     * Attempt to obtain an InputStream from a URI. If the URI is a file scheme
     * then just open it directly. Otherwise, call uri.toURL().openStream().
     * 
     * @param uri
     *            The URI to attempt to read from
     * @return An InputStream connection
     * @throws IOException a problem with I/O happened
     */
    public static InputStream getInputStream(URI uri) throws IOException {
        // We favor the FileOutputStream
        if (uri.getScheme().equals(PROTOCOL_FILE)) {
            return new FileInputStream(uri.getPath());
        }
        return uri.toURL().openStream();
    }

    /**
     * Attempt to obtain an OutputStream from a URI. The simple case will open
     * it if it is local. Otherwise, it will call
     * uri.toURL().openConnection().getOutputStream(), however in some JVMs (MS
     * at least this fails where new FileOutputStream(url) works.
     * 
     * @param uri
     *            The URI to attempt to write to
     * @return An OutputStream connection
     * @throws IOException a problem with I/O happened
     */
    public static OutputStream getOutputStream(URI uri) throws IOException {
        return getOutputStream(uri, false);
    }

    /**
     * Attempt to obtain an OutputStream from a URI. The simple case will open
     * it if it is local. Otherwise, it will call
     * uri.toURL().openConnection().getOutputStream(), however in some JVMs (MS
     * at least this fails where new FileOutputStream(url) works.
     * 
     * @param uri
     *            The URI to attempt to write to
     * @param append
     *            Do we write to the end of the file instead of the beginning
     * @return An OutputStream connection
     * @throws IOException a problem with I/O happened
     */
    public static OutputStream getOutputStream(URI uri, boolean append) throws IOException {
        // We favor the FileOutputStream method here because append
        // is not well defined for the openConnection method
        if (uri.getScheme().equals(PROTOCOL_FILE)) {
            return new FileOutputStream(uri.getPath(), append);
        }
        URLConnection cnx = uri.toURL().openConnection();
        cnx.setDoOutput(true);
        return cnx.getOutputStream();
    }

    /**
     * List the items available assuming that this URI points to a directory.
     * <p>
     * There are 2 methods of calculating the answer - if the URI is a file: URI
     * then we can just use File.list(), otherwise we ask for a file inside the
     * directory called index.txt and assume the directories contents to be
     * listed one per line.
     * <p>
     * If the URI is a file: URI then we execute both methods and warn if there
     * is a difference, but returning the values from the index.txt method.
     * 
     * @param uri the URI to list
     * @param filter the filter for the listing
     * @return the filtered list
     * @throws MalformedURLException
     *                If the URI is not valid
     * @throws IOException a problem with I/O happened
     */
    public static String[] list(URI uri, URIFilter filter) throws MalformedURLException, IOException {
        // We should probably cache this in some way? This is going
        // to get very slow calling this often across a network
        String[] reply = {};
        try {
            URI search = NetUtil.lengthenURI(uri, INDEX_FILE);
            reply = listByIndexFile(search, filter);
        } catch (FileNotFoundException ex) {
            // So the index file was not found - this isn't going to work over
            // JNLP or other systems that can't use file: URIs. But it is silly
            // to get to picky so if there is a solution using file: then just
            // print a warning and carry on.
            LOGGER.warn("index file for " + uri.toString() + " was not found.");
            if (uri.getScheme().equals(PROTOCOL_FILE)) {
                return listByFile(uri, filter);
            }
        }

        // if we can - check that the index file is up to date.
        if (uri.getScheme().equals(PROTOCOL_FILE)) {
            String[] files = listByFile(uri, filter);

            // Check that the answers are the same
            if (files.length != reply.length) {
                LOGGER.warn("index file for {} has incorrect number of entries.", uri.toString());
            } else {
                List<String> list = Arrays.asList(files);
                for (int i = 0; i < files.length; i++) {
                    if (!list.contains(files[i])) {
                        LOGGER.warn("file: based index found {} but this was not found using index file.", files[i]);
                    }
                }
            }
        }

        return reply;
    }

    /**
     * List all the files specified by the index file passed in.
     * 
     * @param uri the URI to list
     * @param filter the filter for the listing
     * @return the filtered list
     * @throws MalformedURLException
     *                If the URI is not valid
     */
    public static String[] listByFile(URI uri, URIFilter filter) throws MalformedURLException {
        File fdir = new File(uri.getPath());
        if (!fdir.isDirectory()) {
            // TRANSLATOR: Error condition: A directory was expected, but a file was found. {0} is a placeholder for the file.
            throw new MalformedURLException(JSMsg.gettext("The URL {0} is not a directory", uri.toString()));
        }

        return fdir.list(new URIFilterFilenameFilter(filter));
    }

    /**
     * List all the strings specified by the index file passed in. To be
     * acceptable it must be a non-0 length string, not commented with #, and
     * not the index file itself.
     * 
     * @param index the URI to list
     * @return the list.
     * @throws IOException a problem with I/O happened
     */
    public static String[] listByIndexFile(URI index) throws IOException {
        return listByIndexFile(index, new DefaultURIFilter());
    }

    /**
     * List all the files specified by the index file passed in.
     * <p>Each line is pre-processed:</p>
     * <ul>
     * <li>Ignore comments (# to end of line)</li>
     * <li>Trim spaces from line.</li>
     * <li>Ignore blank lines.</li>
     * </ul>
     * 
     * To be acceptable it:
     * <ul>
     * <li> cannot be the index file itself</li>
     * <li> and must acceptable by the filter.</li>
     * </ul>
     * 
     * @param index the URI to list
     * @param filter the filter for the listing
     * @return the list.
     * @throws IOException a problem with I/O happened
     */
    public static String[] listByIndexFile(URI index, URIFilter filter) throws IOException {
        InputStream in = null;
        BufferedReader din = null;
        try {
            in = NetUtil.getInputStream(index);
            // Quiet Android from complaining about using the default BufferReader buffer size.
            // The actual buffer size is undocumented. So this is a good idea any way.
            din = new BufferedReader(new InputStreamReader(in), 8192);

            // We still need to do the filtering
            List<String> list = new ArrayList<String>();

            while (true) {
                String line = din.readLine();

                if (line == null) {
                    break;
                }

                String name = line;

                // Strip comments from the line
                int len = name.length();
                int commentPos;
                for (commentPos = 0; commentPos < len && name.charAt(commentPos) != '#'; ++commentPos) {
                    continue; // test does the work
                }

                if (commentPos < len) {
                    name = name.substring(0, commentPos);
                }

                // we need to trim extraneous whitespace on the line
                name = name.trim();

                // Is it acceptable?
                if (name.length() > 0 && !name.equals(INDEX_FILE) && filter.accept(name)) {
                    list.add(name);
                }
            }

            return list.toArray(new String[list.size()]);
        } finally {
            IOUtil.close(din);
            IOUtil.close(in);
        }
    }

    /**
     * Load up properties given by a URI.
     * 
     * @param uri
     *            the location of the properties
     * @return the properties given by the URI
     * @throws IOException a problem with I/O happened
     */
    public static PropertyMap loadProperties(URI uri) throws IOException {
        InputStream is = null;
        try {
            is = NetUtil.getInputStream(uri);
            PropertyMap prop = new PropertyMap();
            prop.load(is);
            is.close();
            return prop;
        } finally {
            IOUtil.close(is);
        }
    }

    /**
     * Store the properties at the location given by the uri using the supplied
     * title.
     * 
     * @param properties
     *            the properties to store
     * @param uri
     *            the location of the store
     * @param title
     *            the label held in the properties file
     * @throws IOException a problem with I/O happened
     */
    public static void storeProperties(PropertyMap properties, URI uri, String title) throws IOException {
        OutputStream out = null;

        try {
            out = NetUtil.getOutputStream(uri);
            PropertyMap temp = new PropertyMap();
            temp.putAll(properties);
            temp.store(out, title);
        } finally {
            IOUtil.close(out);
        }
    }

    /**
     * @param uri
     *            the resource whose size is wanted
     * @return the size of that resource
     */
    public static int getSize(URI uri) {
        return getSize(uri, null, null);
    }

    public static int getSize(URI uri, String proxyHost) {
        return getSize(uri, proxyHost, null);
    }

    public static int getSize(URI uri, String proxyHost, Integer proxyPort) {
        try {
            if (uri.getScheme().equals(PROTOCOL_HTTP)) {
                WebResource resource = new WebResource(uri, proxyHost, proxyPort);
                int size = resource.getSize();
                resource.shutdown();
                return size;
            }

            return uri.toURL().openConnection().getContentLength();
        } catch (IOException e) {
            return 0;
        }
    }

    /**
     * When was the given URI last modified. If no modification time is
     * available then this method return the current time.
     * 
     * @param uri the URI to examine
     * @return the last modified date
     */
    public static long getLastModified(URI uri) {
        return getLastModified(uri, null, null);
    }

    /**
     * When was the given URI last modified. If no modification time is
     * available then this method return the current time.
     * 
     * @param uri the URI to examine
     * @param proxyHost the proxy host
     * @return the last modified date
     */
    public static long getLastModified(URI uri, String proxyHost) {
        return getLastModified(uri, proxyHost, null);
    }

    /**
     * When was the given URI last modified. If no modification time is
     * available then this method return the current time.
     * 
     * @param uri the URI to examine
     * @param proxyHost the proxy host
     * @param proxyPort the proxy port
     * @return the last modified date
     */
    public static long getLastModified(URI uri, String proxyHost, Integer proxyPort) {
        try {
            if (uri.getScheme().equals(PROTOCOL_HTTP)) {
                WebResource resource = new WebResource(uri, proxyHost, proxyPort);
                long time = resource.getLastModified();
                resource.shutdown();
                return time;
            }

            URLConnection urlConnection = uri.toURL().openConnection();
            long time = urlConnection.getLastModified();

            // If it were a jar then time contains the last modified date of the jar.
            if (urlConnection instanceof JarURLConnection) {
                // form is jar:file:.../xxx.jar!.../filename.ext
                JarURLConnection jarConnection = (JarURLConnection) urlConnection;
                JarEntry jarEntry = jarConnection.getJarEntry();
                time = jarEntry.getTime();
            }

            return time;
        } catch (IOException ex) {
            LOGGER.warn("Failed to get modified time", ex);
            return new Date().getTime();
        }
    }

    /**
     * Returns whether the left is newer than the right by comparing their last
     * modified dates.
     * 
     * @param left one URI to compare
     * @param right the other URI to compare
     * @return true if the left is newer
     */
    public static boolean isNewer(URI left, URI right) {
        return isNewer(left, right, null, null);
    }

    /**
     * Returns whether the left is newer than the right by comparing their last
     * modified dates.
     * 
     * @param left one URI to compare
     * @param right the other URI to compare
     * @param proxyHost the proxy host
     * @return true if the left is newer
     */
    public static boolean isNewer(URI left, URI right, String proxyHost) {
        return isNewer(left, right, proxyHost, null);
    }

    /**
     * Returns whether the left is newer than the right by comparing their last
     * modified dates.
     * 
     * @param left one URI to compare
     * @param right the other URI to compare
     * @param proxyHost the proxy host
     * @param proxyPort the proxy port
     * @return true if the left is newer
     */
    public static boolean isNewer(URI left, URI right, String proxyHost, Integer proxyPort) {
        return NetUtil.getLastModified(left, proxyHost, proxyPort) > NetUtil.getLastModified(right, proxyHost, proxyPort);
    }

    /**
     * Quick implementation of FilenameFilter that uses a URIFilter
     */
    public static class URIFilterFilenameFilter implements FilenameFilter {
        /**
         * Simple ctor
         * 
         * @param filter the filter
         */
        public URIFilterFilenameFilter(URIFilter filter) {
            this.filter = filter;
        }

        /* (non-Javadoc)
         * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
         */
        public boolean accept(File arg0, String name) {
            return filter.accept(name);
        }

        private URIFilter filter;
    }

    /**
     * Throw if the given URI does not use the 'file:' protocol
     * 
     * @param uri
     *            The URI to check
     * @throws MalformedURLException
     *             If the protocol is not file:
     */
    private static void checkFileURI(URI uri) throws MalformedURLException {
        if (!uri.getScheme().equals(PROTOCOL_FILE)) {
            // TRANSLATOR: Error condition: The URL protocol "file:" was expected, but something else was found. {0} is a placeholder for the URL.
            throw new MalformedURLException(JSMsg.gettext("The URL {0} is not a file.", uri));
        }
    }

    /**
     * Returns the cache directory.
     * 
     * @return File
     */
    public static File getURICacheDir() {
        return cachedir;
    }

    /**
     * Sets the cache directory.
     * 
     * @param cachedir
     *            The cache directory to set
     */
    public static void setURICacheDir(File cachedir) {
        NetUtil.cachedir = cachedir;
    }

    /**
     * Get a URI version of the given file.
     * 
     * @param file
     *            The File to turn into a URI
     * @return a URI for the given file
     */
    public static URI getURI(File file) {
        return file.toURI();
    }

    /**
     * A URI version of <code>File.createTempFile()</code>
     * 
     * @param prefix the prefix of the temporary file
     * @param suffix the suffix of the temporary file
     * @return A new temporary URI
     * @throws IOException
     *             If something goes wrong creating the temp URI
     */
    public static URI getTemporaryURI(String prefix, String suffix) throws IOException {
        File tempFile = File.createTempFile(prefix, suffix);
        return getURI(tempFile);
    }

    /**
     * Convert an URL to an URI.
     * 
     * @param url
     *            to convert
     * @return the URI representation of the URL
     */
    public static URI toURI(URL url) {
        try {
            return new URI(url.toExternalForm());
        } catch (URISyntaxException e) {
            return null;
        }
    }

    /**
     * Convert an URI to an URL.
     * 
     * @param uri
     *            to convert
     * @return the URL representation of the URI
     */
    public static URL toURL(URI uri) {
        try {
            return uri.toURL();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /**
     * Check that the directories in the version directory really represent
     * versions.
     */
    public static class IsDirectoryURIFilter implements URIFilter {
        /**
         * Simple ctor
         * 
         * @param parent the parent directory
         */
        public IsDirectoryURIFilter(URI parent) {
            this.parent = parent;
        }

        /* (non-Javadoc)
         * @see org.crosswire.common.util.URIFilter#accept(java.lang.String)
         */
        public boolean accept(String name) {
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
    private static final Logger LOGGER = LoggerFactory.getLogger(NetUtil.class);
}
