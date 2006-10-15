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
package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.basic.AbstractBookDriver;
import org.crosswire.jsword.index.IndexManager;
import org.crosswire.jsword.index.IndexManagerFactory;
import org.crosswire.jsword.index.IndexStatus;

/**
 * This represents all of the Sword Books (aka modules).
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class SwordBookDriver extends AbstractBookDriver
{
    /**
     * Some basic name initialization
     */
    public SwordBookDriver()
    {
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookDriver#getName()
     */
    public String getDriverName()
    {
        return "Sword"; //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookDriver#getBooks()
     */
    public Book[] getBooks()
    {
        ConfigEntry.resetStatistics();

        List valid = new ArrayList();

        File[] dirs = SwordBookPath.getSwordPath();
        for (int j = 0; j < dirs.length; j++)
        {
            getBooks(valid, dirs[j]);
        }

        ConfigEntry.dumpStatistics();

        return (Book[]) valid.toArray(new Book[valid.size()]);
    }

    private void getBooks(List valid, File bookDir)
    {
        File mods = new File(bookDir, SwordConstants.DIR_CONF);
        if (mods.isDirectory())
        {
            String[] bookConfs = SwordBookPath.getBookList(mods);

            // Loop through the entries in this mods.d directory
            for (int i = 0; i < bookConfs.length; i++)
            {
                String bookConf = bookConfs[i];
                try
                {
                    File configfile = new File(mods, bookConf);
                    String internal = bookConf;
                    if (internal.endsWith(SwordConstants.EXTENSION_CONF))
                    {
                        internal = internal.substring(0, internal.length() - 5);
                    }
                    SwordBookMetaData sbmd = new SwordBookMetaData(configfile, internal, NetUtil.getURL(bookDir));
                    sbmd.setDriver(this);

                    // Only take the first "installation" of the Book
                    Book book = createBook(sbmd);
                    if (!valid.contains(book))
                    {
                        valid.add(book);

                        IndexManager imanager = IndexManagerFactory.getIndexManager();
                        if (imanager.isIndexed(book))
                        {
                            sbmd.setIndexStatus(IndexStatus.DONE);
                        }
                        else
                        {
                            sbmd.setIndexStatus(IndexStatus.UNDONE);
                        }
                    }
                }
                catch (IOException e)
                {
                    log.warn("Couldn't create SwordBookMetaData", e); //$NON-NLS-1$
                }
                catch (BookException e)
                {
                    log.warn("Couldn't create SwordBookMetaData", e); //$NON-NLS-1$
                }
            }
        }
        else
        {
            log.debug("mods.d directory at " + mods + " does not exist"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookDriver#isDeletable(org.crosswire.jsword.book.BookMetaData)
     */
    /* @Override */
    public boolean isDeletable(Book dead)
    {
        SwordBookMetaData sbmd = (SwordBookMetaData) dead.getBookMetaData();
        File dlDir = SwordBookPath.getDownloadDir();
        File confFile = new File(dlDir, sbmd.getConfPath());

        // We can only uninstall what we download into our download dir.
        return confFile.exists();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookDriver#delete(org.crosswire.jsword.book.BookMetaData)
     */
    /* @Override */
    public void delete(Book dead) throws BookException
    {
        SwordBookMetaData sbmd = (SwordBookMetaData) dead.getBookMetaData();
        File dlDir = SwordBookPath.getDownloadDir();
        File confFile = new File(dlDir, sbmd.getConfPath());

        // We can only uninstall what we download into our download dir.
        if (!confFile.exists())
        {
            throw new BookException(Msg.DELETE_FAILED, new Object [] {confFile});
        }

        // Delete the conf
        List failures = FileUtil.delete(confFile);
        if (failures.size() == 0)
        {
            URL loc = sbmd.getLocation();
            if (loc != null)
            {
                File bookDir = new File(loc.getFile());
                failures = FileUtil.delete(bookDir);
                Books.installed().removeBook(dead);
            }

        }

        // TODO(DM): list all that failed
        if (failures.size() > 0)
        {
            throw new BookException(Msg.DELETE_FAILED, new Object [] {failures.get(0)});
        }
    }

    /**
     * Get the singleton instance of this driver.
     * @return this driver instance
     */
    public static BookDriver instance()
    {
        return INSTANCE;
    }

    /**
     * A helper class for the SwordInstaller to tell us that it has copied a
     * new Book into our install directory
     * @param sbmd The SwordBookMetaData object for the new Book
     * @throws BookException
     */
    public static void registerNewBook(SwordBookMetaData sbmd) throws BookException
    {
        BookDriver[] drivers = Books.installed().getDriversByClass(SwordBookDriver.class);
        for (int i = 0; i < drivers.length; i++)
        {
            SwordBookDriver sdriver = (SwordBookDriver) drivers[i];
            Book book = sdriver.createBook(sbmd);
            Books.installed().addBook(book);
        }
    }

    /**
     * Create a Book appropriate for the BookMetaData
     */
    private Book createBook(SwordBookMetaData sbmd) throws BookException
    {
        BookType modtype = sbmd.getBookType();
        if (modtype.getBookCategory() == null)
        {
            // LATER(joe): how do we support books?
            log.warn("No support for book type: DRIVER_RAW_GEN_BOOK"); //$NON-NLS-1$
            throw new BookException(Msg.TYPE_UNSUPPORTED);
        }

        return modtype.createBook(sbmd);
    }

    /**
     * A shared instance of this driver.
     */
    private static final BookDriver INSTANCE = new SwordBookDriver();

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(SwordBookDriver.class);

}
