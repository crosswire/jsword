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
package org.crosswire.jsword.index.lucene;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.crosswire.common.util.CWProject;
import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.IOUtil;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.index.Index;
import org.crosswire.jsword.index.IndexManager;
import org.crosswire.jsword.index.IndexPolicy;
import org.crosswire.jsword.index.IndexPolicyAdapter;
import org.crosswire.jsword.index.IndexStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
//todo
  use org.apache.lucene.util.Version when upgrading Lucene;

  OPEN questions
*/
/**
 * An implementation of IndexManager for Lucene indexes.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class LuceneIndexManager implements IndexManager {
    /**
     * Create a LuceneIndexManager with a default IndexPolicy.
     */
    public LuceneIndexManager() {
        policy = new IndexPolicyAdapter();
        try {
            baseFolderURI = CWProject.instance().getWritableProjectSubdir(DIR_LUCENE, false);
        } catch (IOException ex) {
            log.error("Failed to find lucene index storage area.", ex);

        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.index.IndexManager#isIndexed(org.crosswire.jsword.book.Book)
     */
    public boolean isIndexed(Book book) {
        try {
            if (book == null) {
                return false;
            }
            URI storage = getStorageArea(book);
            return NetUtil.isDirectory(storage);
        } catch (IOException ex) {
            log.error("Failed to find lucene index storage area.", ex);
            return false;
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.index.IndexManager#getIndex(org.crosswire.jsword.book.Book)
     */
    public Index getIndex(Book book) throws BookException {
        try {
            Index reply = INDEXES.get(book);
            if (reply == null) {
                URI storage = getStorageArea(book);
                reply = new LuceneIndex(book, storage);
                INDEXES.put(book, reply);
            }

            return reply;
        } catch (IOException ex) {
            // TRANSLATOR: Common error condition: Some error happened while opening a search index.
            throw new BookException(JSMsg.gettext("Failed to initialize Lucene search engine."), ex);
        }
    }

    /**
     * Clients can use this to determine if book's index is stale and needs to re-indexed or downloaded.
     * Assumes index exists: Client must use isIndexed() prior to using this method
     * 
     * @return true, if Latest.Index.Version.xxx &gt; Installed.Index.Version.xxx
     * @see org.crosswire.jsword.index.IndexManager#needsReindexing(org.crosswire.jsword.book.Book)
     */
    public boolean needsReindexing(Book book) {
        //check for index version
        //should Clients use IndexStatus.INVALID
        float installedV = InstalledIndex.instance().getInstalledIndexVersion(book);
        if (installedV < IndexMetadata.instance().getLatestIndexVersion(book)) {
            log.info("{}: needs reindexing, Installed index version @{}", book.getBookMetaData().getInitials(), Float.toString(installedV));
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.index.IndexManager#closeAllIndexes()
     */
    public void closeAllIndexes() {
        for (Index index : INDEXES.values()) {
            index.close();
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.index.IndexManager#scheduleIndexCreation(org.crosswire.jsword.book.Book)
     */
    public void scheduleIndexCreation(final Book book) {
        book.setIndexStatus(IndexStatus.SCHEDULED);

        IndexStatus finalStatus = IndexStatus.UNDONE;

        try {
            URI storage = getStorageArea(book);
            Index index = new LuceneIndex(book, storage, this.policy);

            //todo update Installed IndexVersion for newly created index
            // todo implement: Installed.Index.Version.Book.XXX value add/update in metadata file after creation, use value getLatestIndexVersion(book)

            // We were successful if the directory exists.
            if (NetUtil.getAsFile(storage).exists()) {
                finalStatus = IndexStatus.DONE;
                INDEXES.put(book, index);

                //update IndexVersion
                InstalledIndex.instance().storeLatestVersionAsInstalledIndexMetadata(book);
            }
        } catch (IOException e) {
            Reporter.informUser(LuceneIndexManager.this, e);
        } catch (BookException e) {
            Reporter.informUser(LuceneIndexManager.this, e);
        } finally {
            book.setIndexStatus(finalStatus);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.index.IndexManager#installDownloadedIndex(org.crosswire.jsword.book.Book, java.net.URI)
     */
    public void installDownloadedIndex(Book book, URI tempDest) throws BookException {
        try {
            URI storage = getStorageArea(book);
            File zip = NetUtil.getAsFile(tempDest);
            IOUtil.unpackZip(zip, NetUtil.getAsFile(storage));
            //todo Index.Version management??
        } catch (IOException ex) {
            // TRANSLATOR: The search index could not be moved to it's final location.
            throw new BookException(JSMsg.gettext("Installation failed."), ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.index.IndexManager#deleteIndex(org.crosswire.jsword.book.Book)
     */
    public void deleteIndex(Book book) throws BookException {
        // Lucene can build in the directory that currently exists,
        // overwriting what is there. So we rename the directory,
        // mark the operation as success and then try to delete the
        // directory.
        File tempPath = null;
        try {
            // TODO(joe): This needs some checks that it isn't being used
            //temporary fix, which closes the index - non-thread safe since someone could theoretically come in and activate this again!
            Index index = INDEXES.get(book);
            if (index != null) {
                index.close();
            }

            File storage = NetUtil.getAsFile(getStorageArea(book));
            String finalCanonicalPath = storage.getCanonicalPath();
            tempPath = new File(finalCanonicalPath + '.' + IndexStatus.CREATING.toString());

            if (tempPath.exists()) {
                FileUtil.delete(tempPath);
            }

            // Issues in at least Windows seem to create issues with reusing a file that's been deleted... 
            tempPath = new File(finalCanonicalPath + '.' + IndexStatus.CREATING.toString());
            if (!storage.renameTo(tempPath)) {
                // TRANSLATOR: Error condition: The index could not be deleted.
                throw new BookException(JSMsg.gettext("Failed to delete search index."));
            }
            book.setIndexStatus(IndexStatus.UNDONE);

            //Delete index Version metadata (InstalledIndex)
            InstalledIndex.instance().removeFromInstalledIndexMetadata(book);

        } catch (IOException ex) {
            // TRANSLATOR: Error condition: The index could not be deleted.
            throw new BookException(JSMsg.gettext("Failed to delete search index."), ex);
        }

        FileUtil.delete(tempPath);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.index.IndexManager#getIndexPolicy()
     */
    public IndexPolicy getIndexPolicy() {
        return policy;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.index.IndexManager#setIndexPolicy(org.crosswire.jsword.index.IndexPolicy)
     */
    public void setIndexPolicy(IndexPolicy policy) {
        if (policy != null) {
            this.policy = policy;
        } else {
            this.policy = new IndexPolicyAdapter();
        }

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


        //URI driver = NetUtil.lengthenURI(baseFolderURI, driverName);
        return NetUtil.lengthenURI(baseFolderURI, driverName + NetUtil.SEPARATOR + bookName);
    }

    private IndexPolicy policy;
    private URI baseFolderURI;

    /**
     * The created indexes
     */
    protected static final Map<Book, Index> INDEXES = new HashMap<Book, Index>();

    /**
     * The lucene search index directory
     */
    public static final String DIR_LUCENE = "lucene";

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(LuceneIndexManager.class);
}
