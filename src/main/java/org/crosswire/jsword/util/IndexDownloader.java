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
package org.crosswire.jsword.util;

import java.io.IOException;
import java.net.URI;

import org.crosswire.common.util.NetUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.install.InstallException;
import org.crosswire.jsword.book.install.Installer;
import org.crosswire.jsword.index.IndexManager;
import org.crosswire.jsword.index.IndexManagerFactory;
import org.crosswire.jsword.index.IndexStatus;

/**
 * .
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public final class IndexDownloader {
    /**
     * Prevent instantiation
     */
    private IndexDownloader() {
    }

    /**
     * Download and install a search index
     * 
     * @param book
     *            The book to get an index for
     * @param installer 
     * @throws IOException 
     * @throws InstallException 
     * @throws BookException 
     */
    public static void downloadIndex(Book book, Installer installer) throws IOException, InstallException, BookException {
        // Get a temp home
        URI tempDownload = NetUtil.getTemporaryURI(TEMP_PREFIX, TEMP_SUFFIX);

        IndexStatus finalStatus = IndexStatus.UNDONE;
        try {
            // Now we know what installer to use, download to the temp file
            installer.downloadSearchIndex(book, tempDownload);

            // And install from that file.
            IndexManager idxman = IndexManagerFactory.getIndexManager();
            book.setIndexStatus(IndexStatus.CREATING);
            idxman.installDownloadedIndex(book, tempDownload);
            finalStatus = IndexStatus.DONE;
        } finally {
            book.setIndexStatus(finalStatus);
            // tidy up after ourselves
            if (tempDownload != null) {
                NetUtil.delete(tempDownload);
            }
        }
    }

    /**
     * Temp file prefix
     */
    private static final String TEMP_PREFIX = "jsword-index";

    /**
     * Temp file suffix
     */
    private static final String TEMP_SUFFIX = "dat";
}
