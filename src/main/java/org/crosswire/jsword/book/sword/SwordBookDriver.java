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
package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.NetUtil;
import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.basic.AbstractBookDriver;
import org.crosswire.jsword.index.IndexManager;
import org.crosswire.jsword.index.IndexManagerFactory;
import org.crosswire.jsword.index.IndexStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This represents all of the Sword Books (aka modules).
 *
 * @author Joe Walker
 * @author DM Smith
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.<br>
 * The copyright to this program is held by its authors.
 */
public class SwordBookDriver extends AbstractBookDriver {
    /**
     * Some basic name initialization
     */
    public SwordBookDriver() {
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookDriver#getDriverName()
     */
    public String getDriverName() {
        return "Sword";
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookProvider#getBooks()
     */
    public Book[] getBooks() {
        File[] dirs = SwordBookPath.getSwordPath();
        //initial size based on Guava's  newHashMapWithExpectedSize method:
        //http://docs.guava-libraries.googlecode.com/git/javadoc/src-html/com/google/common/collect/Maps.html#line.201
        Set<Book> valid = new HashSet<Book>(dirs.length + dirs.length / 3);
        for (int j = 0; j < dirs.length; j++) {
            getBooks(valid, dirs[j]);
        }
        return valid.toArray(new Book[valid.size()]);
    }

    private void getBooks(Set<Book> valid, File bookDir) {
        File mods = new File(bookDir, SwordConstants.DIR_CONF);
        if (!mods.isDirectory()) {
            LOGGER.debug("mods.d directory at {} does not exist", mods);
            return;
        }

        String[] bookConfs = SwordBookPath.getBookList(mods);

        // Loop through the entries in this mods.d directory
        URI bookDirURI = NetUtil.getURI(bookDir);
        for (int i = 0; i < bookConfs.length; i++) {
            String bookConf = bookConfs[i];
            try {
                SwordBookMetaData sbmd = null;

                File configfile = new File(mods, bookConf);
                if (configfile.exists()) {
                    // First time here chain is null, indicating that we are at the master BookMetaData
                    sbmd = new SwordBookMetaData(configfile, bookDirURI);
                }

                if (sbmd == null) {
                    LOGGER.error("The book's configuration files is not supported.");
                    continue;
                }

                // skip any book that is not supported.
                if (!sbmd.isSupported()) {
                    LOGGER.error("The book's configuration files is not supported. -> Initials [{}], Driver=[{}], Versification=[{}], Book type=[{}], Book category=[{}]",
                            sbmd.getInitials(), sbmd.getDriver(), sbmd.getProperty(BookMetaData.KEY_VERSIFICATION), sbmd.getBookType(), sbmd.getBookCategory());
                    continue;
                }

                sbmd.setDriver(this);

                // Only take the first "installation" of the Book
                Book book = createBook(sbmd);
                if (!valid.contains(book)) {
                    valid.add(book);

                    IndexManager imanager = IndexManagerFactory.getIndexManager();
                    if (imanager.isIndexed(book)) {
                        sbmd.setIndexStatus(IndexStatus.DONE);
                    } else {
                        sbmd.setIndexStatus(IndexStatus.UNDONE);
                    }
                }
            } catch (IOException e) {
                LOGGER.warn("Couldn't create SwordBookMetaData", e);
            } catch (BookException e) {
                LOGGER.warn("Couldn't create SwordBookMetaData", e);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractBookDriver#isDeletable(org.crosswire.jsword.book.Book)
     */
    @Override
    public boolean isDeletable(Book dead) {
        SwordBookMetaData sbmd = (SwordBookMetaData) dead.getBookMetaData();
        File confFile = sbmd.getConfigFile();
        // We can only uninstall what we download into our download dir.
        return confFile != null && confFile.exists();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractBookDriver#delete(org.crosswire.jsword.book.Book)
     */
    @Override
    public void delete(Book dead) throws BookException {
        SwordBookMetaData sbmd = (SwordBookMetaData) dead.getBookMetaData();
        File confFile = sbmd.getConfigFile();

        // We can only uninstall what we download into our download dir.
        if (confFile == null || !confFile.exists()) {
            // TRANSLATOR: Common error condition: The file could not be deleted. There can be many reasons.
            // {0} is a placeholder for the file.
            throw new BookException(JSMsg.gettext("Unable to delete: {0}", confFile));
        }

        // Delete the conf
        List<File> failures = FileUtil.delete(confFile);
        if (failures.isEmpty()) {
            URI loc = sbmd.getLocation();
            if (loc != null) {
                File bookDir = new File(loc.getPath());
                failures = FileUtil.delete(bookDir);
                Books.installed().removeBook(dead);
            }

        }

        // TODO(DM): list all that failed
        if (!failures.isEmpty()) {
            // TRANSLATOR: Common error condition: The file could not be deleted. There can be many reasons.
            // {0} is a placeholder for the file.
            throw new BookException(JSMsg.gettext("Unable to delete: {0}", failures.get(0)));
        }
    }

    /**
     * Get the singleton instance of this driver.
     * 
     * @return this driver instance
     */
    public static BookDriver instance() {
        return INSTANCE;
    }

    /**
     * A helper class for the SwordInstaller to tell us that it has copied a new
     * Book into our install directory
     * 
     * @param sbmd
     *            The SwordBookMetaData object for the new Book
     * @throws BookException
     */
    public static void registerNewBook(SwordBookMetaData sbmd) throws BookException {
        BookDriver[] drivers = Books.installed().getDriversByClass(SwordBookDriver.class);
        for (int i = 0; i < drivers.length; i++) {
            SwordBookDriver sdriver = (SwordBookDriver) drivers[i];
            Book book = sdriver.createBook(sbmd);
            Books.installed().addBook(book);
        }
    }

    /**
     * Create a Book appropriate for the BookMetaData
     */
    private Book createBook(SwordBookMetaData sbmd) throws BookException {
        BookType modtype = sbmd.getBookType();
        if (modtype == null || modtype.getBookCategory() == null) {
            // FIXME(DMS): missing parameter
            throw new BookException(JSOtherMsg.lookupText("Unsupported type: {0} when reading {1}"));
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
    private static final Logger LOGGER = LoggerFactory.getLogger(SwordBookDriver.class);
}
