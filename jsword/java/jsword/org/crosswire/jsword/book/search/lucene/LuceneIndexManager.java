package org.crosswire.jsword.book.search.lucene;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.crosswire.common.util.IOUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.IndexStatus;
import org.crosswire.jsword.book.search.Index;
import org.crosswire.jsword.book.search.IndexManager;
import org.crosswire.jsword.util.Project;

/**
 * An implementation of IndexManager for Lucene indexes.
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class LuceneIndexManager implements IndexManager
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.AbstractIndex#isIndexed()
     */
    public boolean isIndexed(Book book)
    {
        try
        {
            URL storage = getStorageArea(book.getBookMetaData());
            URL longer = NetUtil.lengthenURL(storage, DIR_SEGMENTS);
            return NetUtil.isFile(longer);
        }
        catch (IOException ex)
        {
            log.error("Failed to find lucene index storage area.", ex); //$NON-NLS-1$
            return false;
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.IndexManager#getIndex(org.crosswire.jsword.book.Book)
     */
    public Index getIndex(Book book) throws BookException
    {
        try
        {
            Index reply = (Index) indexes.get(book);
            if (reply == null)
            {
                URL storage = getStorageArea(book.getBookMetaData());
                reply = new LuceneIndex(book, storage);
                indexes.put(book, reply);
            }

            return reply;
        }
        catch (IOException ex)
        {
            throw new BookException(Msg.LUCENE_INIT, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.AbstractIndex#generateSearchIndex(org.crosswire.common.progress.Job)
     */
    public void scheduleIndexCreation(final Book book)
    {
        book.getBookMetaData().setIndexStatus(IndexStatus.SCHEDULED);

        Thread work = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    URL storage = getStorageArea(book.getBookMetaData());
                    Index index = new LuceneIndex(book, storage, true);
                    indexes.put(book, index);
                }
                catch (Exception ex)
                {
                    Reporter.informUser(LuceneIndexManager.this, ex);
                }
            }
        });
        work.start();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.IndexManager#installDownloadedIndex(org.crosswire.jsword.book.Book, java.net.URL)
     */
    public void installDownloadedIndex(BookMetaData bmd, URL tempDest) throws BookException
    {
        try
        {
            URL storage = getStorageArea(bmd);
            File zip = NetUtil.getAsFile(tempDest);
            IOUtil.unpackZip(zip, NetUtil.getAsFile(storage));
        }
        catch (IOException ex)
        {
            throw new BookException(Msg.INSTALL_FAIL, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.IndexManager#deleteIndex(org.crosswire.jsword.book.Book)
     */
    public void deleteIndex(Book book) throws BookException
    {
        try
        {
            // TODO(joe): This needs some checks that it isn't being used
            URL storage = getStorageArea(book.getBookMetaData());
            NetUtil.delete(storage);
        }
        catch (IOException ex)
        {
            throw new BookException(Msg.DELETE_FAILED, ex);
        }
    }

    /**
     * Determine where an index should be stored
     * @param bmd The book to be indexed
     * @return A URL to store stuff in
     * @throws IOException If there is a problem in finding where to store stuff
     */
    protected URL getStorageArea(BookMetaData bmd) throws IOException
    {
        String driverName = bmd.getDriverName();
        String bookName = bmd.getInitials();

        assert driverName != null;
        assert bookName != null;

        URL base = Project.instance().getTempScratchSpace(DIR_LUCENE, false);
        URL driver = NetUtil.lengthenURL(base, driverName);

        return NetUtil.lengthenURL(driver, bookName);
    }

    /**
     * The created indexes
     */
    protected static final Map indexes = new HashMap();

    /**
     * The segments directory
     */
    private static final String DIR_SEGMENTS = "segments"; //$NON-NLS-1$

    /**
     * The lucene search index directory
     */
    private static final String DIR_LUCENE = "lucene"; //$NON-NLS-1$

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(LuceneIndexManager.class);
}
