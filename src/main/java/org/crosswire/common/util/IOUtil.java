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

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Enumeration;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.crosswire.jsword.JSMsg;
import org.slf4j.LoggerFactory;

/**
 * .
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public final class IOUtil {
    /**
     * Prevent instantiation
     */
    private IOUtil() {
    }

    /**
     * Unpack a zip file to a given directory. Honor the paths as given in the
     * zip file.
     *
     * @param file
     *            The zip file to download
     * @param destdir
     *            The directory to unpack up
     * @throws IOException
     *             If there is an file error
     */
    public static void unpackZip(File file, File destdir) throws IOException {
        unpackZip(file, destdir, true);
    }

    /**
     * Unpack a zip file to a given directory. Honor the paths as given in the
     * zip file.
     *
     * @param file
     *            The zip file to download
     * @param destdir
     *            The directory to unpack up
     * @param include
     *            true to indicate the next arguments will be a filter that only includes what is specified.
     * @param includeExcludes
     *            a list of case insensitive patterns that will act as an inclusion or exclusion prefix
     * @throws IOException
     *            If there is an file error
     */
    public static void unpackZip(File file, File destdir, boolean include, String... includeExcludes) throws IOException {
        // unpack the zip.
        byte[] dbuf = new byte[4096];
        ZipFile zf = null;
        try {
            zf = new ZipFile(file);
            Enumeration<ZipArchiveEntry> entries = zf.getEntries();
            while (entries.hasMoreElements()) {
                ZipArchiveEntry entry = entries.nextElement();
                String entrypath = entry.getName();

                //check filters
                if (includeExcludes != null && includeExcludes.length > 0) {
                        //if include, then we attempt to match ANY path
                        //if exclude, then we ensure that we match NO path
                    boolean skip = include;
                    for (String filter : includeExcludes) {
                        final boolean matchesPath = entrypath.toLowerCase().startsWith(filter);
                        //for includes, ANY match counts, so we override the default of true to false to say 'not skip'
                        if (include && matchesPath) {
                            skip = false;
                        }

                        //for excludes, the default of skip is false
                        if (!include && matchesPath) {
                            skip = true;
                        }
                    }

                    if (skip) {
                        continue;
                    }
                }

                File entryFile = new File(destdir, entrypath);
                File parentDir = entryFile.getParentFile();
                // Is it already a directory ?
                if (!parentDir.isDirectory()) {
                    // Create the directory and make sure it worked.
                    if (!parentDir.mkdirs()) {
                        // TRANSLATOR: Error condition: A directory could not be created. {0} is a placeholder for the directory
                        throw new MalformedURLException(JSMsg.gettext("The URL {0} could not be created as a directory.", parentDir.toString()));
                    }
                }

                // write entryFile from zip to filesystem but avoid writing dir entries out as files
                if (!entry.isDirectory()) {

                    URI child = NetUtil.getURI(entryFile);

                    OutputStream dataOut = NetUtil.getOutputStream(child);
                    InputStream dataIn = zf.getInputStream(entry);

                    while (true) {
                        int count = dataIn.read(dbuf);
                        if (count == -1) {
                            break;
                        }
                        dataOut.write(dbuf, 0, count);
                    }

                    dataOut.close();
                }
            }
        } finally {
            IOUtil.close(zf);
        }
    }

    /**
     * Get a zip entry by specification, returning a buffer of the contents.
     * If there is an error, return a zero length buffer.
     * 
     * @param entrySpec This is of the form /path/to/zip!entryName
     * @return the contents as a buffer
     * @throws IOException 
     */
    public static byte[] getZipEntry(String entrySpec) throws IOException {
        // Get the buffer
        byte[] buffer = new byte[0];
        String[] parts = StringUtil.split(entrySpec, '!');
        ZipFile zipFile = null;
        InputStream zin = null;
        try {
            zipFile = new ZipFile(parts[0]);
            ZipArchiveEntry entry = zipFile.getEntry(parts[1]);
            zin = zipFile.getInputStream(entry);
            int size = (int) entry.getSize();
            buffer = new byte[size];
            // Repeatedly read until all has been read
            int offset = 0;
            while (offset < size) {
                offset += zin.read(buffer, offset, size - offset);
            }

            if (offset != size) {
                log.error("Error: Could not read {} bytes, instead {}, for {} from {}", Integer.toString(size), Integer.toString(offset), parts[1], parts[0]);
            }
        } finally {
            IOUtil.close(zin);
            IOUtil.close(zipFile);
        }
        return buffer;
    }

    /**
     * Closes any {@link Closeable} object
     *
     * @param closeable
     *            The zip file to close
     */
    public static void close(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (IOException ex) {
                log.error("close", ex);
            }
        }
    }

    /**
     * The log stream
     */
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(IOUtil.class);
}
