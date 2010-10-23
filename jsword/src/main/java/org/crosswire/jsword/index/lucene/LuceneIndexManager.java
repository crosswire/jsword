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
package org.crosswire.jsword.index.lucene;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.crosswire.common.util.CWProject;
import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.IOUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.index.Index;
import org.crosswire.jsword.index.IndexManager;
import org.crosswire.jsword.index.IndexStatus;

/**
 * An implementation of IndexManager for Lucene indexes.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class LuceneIndexManager implements IndexManager {
    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.index.search.AbstractIndex#isIndexed()
     */
    public boolean isIndexed(Book book) {
        try {
            URI storage = getStorageArea(book);
            return NetUtil.isDirectory(storage);
        } catch (IOException ex) {
            log.error("Failed to find lucene index storage area.", ex);
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.index.search.IndexManager#getIndex(org.crosswire
     * .jsword.book.Book)
     */
    public Index getIndex(Book book) throws BookException {
        try {
            Index reply = (Index) INDEXES.get(book);
            if (reply == null) {
                URI storage = getStorageArea(book);
                reply = new LuceneIndex(book, storage);
                INDEXES.put(book, reply);
            }

            return reply;
        } catch (IOException ex) {
            // TRANSLATOR: Common error condition: Some error happened while opening a search index.
            throw new BookException(UserMsg.gettext("Failed to initialize Lucene search engine."), ex);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.index.search.AbstractIndex#generateSearchIndex(org
     * .crosswire.common.progress.Job)
     */
    public void scheduleIndexCreation(final Book book) {
        book.setIndexStatus(IndexStatus.SCHEDULED);

        Thread work = new Thread(new Runnable() {
            public void run() {
                IndexStatus finalStatus = IndexStatus.UNDONE;

                try {
                    URI storage = getStorageArea(book);
                    Index index = new LuceneIndex(book, storage, true);
                    // We were successful if the directory exists.
                    if (NetUtil.getAsFile(storage).exists()) {
                        finalStatus = IndexStatus.DONE;
                        INDEXES.put(book, index);
                    }
                } catch (IOException e) {
                    Reporter.informUser(LuceneIndexManager.this, e);
                } catch (BookException e) {
                    Reporter.informUser(LuceneIndexManager.this, e);
                } finally {
                    book.setIndexStatus(finalStatus);
                }
            }
        });
        work.start();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.index.search.IndexManager#installDownloadedIndex
     * (org.crosswire.jsword.book.Book, java.net.URI)
     */
    public void installDownloadedIndex(Book book, URI tempDest) throws BookException {
        try {
            URI storage = getStorageArea(book);
            File zip = NetUtil.getAsFile(tempDest);
            IOUtil.unpackZip(zip, NetUtil.getAsFile(storage));
        } catch (IOException ex) {
            // TRANSLATOR: The search index could not be moved to it's final location.
            throw new BookException(UserMsg.gettext("Installation failed."), ex);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.index.search.IndexManager#deleteIndex(org.crosswire
     * .jsword.book.Book)
     */
    public void deleteIndex(Book book) throws BookException {
        // Lucene can build in the directory that currently exists,
        // overwriting what is there. So we rename the directory,
        // mark the operation as success and then try to delete the
        // directory.
        File tempPath = null;
        try {
            // TODO(joe): This needs some checks that it isn't being used
            File storage = NetUtil.getAsFile(getStorageArea(book));
            String finalCanonicalPath = storage.getCanonicalPath();
            tempPath = new File(finalCanonicalPath + '.' + IndexStatus.CREATING.toString());
            FileUtil.delete(tempPath);
            if (!storage.renameTo(tempPath)) {
                // TRANSLATOR: Error condition: The index could not be deleted.
                throw new BookException(UserMsg.gettext("Failed to delete search index."));
            }
            book.setIndexStatus(IndexStatus.UNDONE);
        } catch (IOException ex) {
            // TRANSLATOR: Error condition: The index could not be deleted.
            throw new BookException(UserMsg.gettext("Failed to delete search index."), ex);
        }

        FileUtil.delete(tempPath);
    }

    /**
     * Determine where an index should be stored
     * 
     * @param book
     *            The book to be indexed
     * @return A URI to store stuff in
     * @throws IOException
     *             If there is a problem in finding where to store stuff
     */
    protected URI getStorageArea(Book book) throws IOException {
        BookMetaData bmd = book.getBookMetaData();
        String driverName = bmd.getDriverName();
        String bookName = bmd.getInitials();

        assert driverName != null;
        assert bookName != null;

        URI base = CWProject.instance().getWriteableProjectSubdir(DIR_LUCENE, false);
        URI driver = NetUtil.lengthenURI(base, driverName);

        return NetUtil.lengthenURI(driver, bookName);
    }

    /**
     * The created indexes
     */
    protected static final Map INDEXES = new HashMap();

    /**
     * The lucene search index directory
     */
    private static final String DIR_LUCENE = "lucene";

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(LuceneIndexManager.class);
}
