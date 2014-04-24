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
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005-2013
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
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
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's authors.
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
        ConfigEntry.resetStatistics();


        File[] dirs = SwordBookPath.getSwordPath();
        //initial size based on Guava's  newHashMapWithExpectedSize method:
        //http://docs.guava-libraries.googlecode.com/git/javadoc/src-html/com/google/common/collect/Maps.html#line.201
        Set<Book> valid = new HashSet<Book>(dirs.length + dirs.length / 3);
        for (int j = 0; j < dirs.length; j++) {
            getBooks(valid, dirs[j]);
        }

        ConfigEntry.dumpStatistics();

        return valid.toArray(new Book[valid.size()]);
    }

    private void getBooks(Set<Book> valid, File bookDir) {
        //there are 5 directories that we might want to read from
        // Sword/mods.d, jsword/mods.d (readable and writeable), jsword/frontend/mods.d (readable and writeable)
        //the main directory is always mods.d from Sword, so we ensure that one exists.
        File mods = new File(bookDir, SwordConstants.DIR_CONF);

        List<MetaFile> modsHierarchy = getModsDirectories(mods);

        if (mods.isDirectory()) {
            String[] bookConfs = SwordBookPath.getBookList(mods);

            // Loop through the entries in this mods.d directory
            for (int i = 0; i < bookConfs.length; i++) {
                String bookConf = bookConfs[i];
                try {
                    String internal = bookConf;
                    if (internal.endsWith(SwordConstants.EXTENSION_CONF)) {
                        internal = internal.substring(0, internal.length() - 5);
                    }

                    SwordBookMetaData sbmd = null;

                    //we go through the loop from the end, such that the least important entry (sword home)
                    //has no parent, and the most important entry refers to its parent in the chain
                    for (int j = modsHierarchy.size() - 1; j >= 0; j--) {
                        File configfile = new File(modsHierarchy.get(j).getFile(), bookConf);
                        if (configfile.exists()) {
                            sbmd = new SwordBookMetaData(sbmd, modsHierarchy.get(j).getLevel(), configfile, internal, NetUtil.getURI(bookDir));
                        }
                    }
                    // skip any book that is not supported.
                    if (!sbmd.isSupported()) {
                        log.error("The book's configuration files is not supported.");
                        log.error(" -> Initials [{}], Driver=[{}], Versification=[{}], Book type=[{}], Book category=[{}]",
                                sbmd.getInitials(),
                                sbmd.getDriver(), sbmd.getProperty(ConfigEntryType.VERSIFICATION),
                                sbmd.getBookType(), sbmd.getBookCategory());
                        continue;
                    }

                    sbmd.setDriver(this);

                    // Only take the first "installation" of the Book
                    Book book = createBook(sbmd);
                    sbmd.setCurrentBook(book);
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
                    log.warn("Couldn't create SwordBookMetaData", e);
                } catch (MissingDataFilesException e) {
                    log.warn(e.getMessage());
                    log.trace(e.getMessage(), e);
                } catch (BookException e) {
                    log.warn("Couldn't create SwordBookMetaData", e);
                }
            }
        } else {
            log.debug("mods.d directory at {} does not exist", mods);
        }
    }

    /**
     * Gets the full list of secondary directories, in the order of overrides.
     * The position in the list determines the importance of the file in terms of override.
     * <p/>
     * A file later on in the list is less important and will override the earlier files.
     * <p/>
     * The order goes
     * <pre>
     *  frontend writeable home
     *  frontend readable home
     *  jsword writeable home
     *  jsword readable home
     *  sword home
     *  </pre>
     */
    private List<MetaFile> getModsDirectories(File swordMods) {
        final List<MetaFile> files = new ArrayList<MetaFile>(5);

        addNonNull(files, MetaFile.Level.FRONTEND_WRITE);
        addNonNull(files, MetaFile.Level.FRONTEND_READ);
        addNonNull(files, MetaFile.Level.JSWORD_WRITE);
        addNonNull(files, MetaFile.Level.JSWORD_READ);
        files.add(new MetaFile(swordMods, MetaFile.Level.SWORD));
        return files;
    }

    /**
     * Adds the URI to the list if not null
     *
     * @param files a list of files
     * @param level the type of conf file
     */
    private void addNonNull(List<MetaFile> files, MetaFile.Level level) {
        File mods = level.getConfigLocation();
        if (mods != null) {
            files.add(new MetaFile(mods, level));
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
     * @param sbmd The SwordBookMetaData object for the new Book
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
    private static final Logger log = LoggerFactory.getLogger(SwordBookDriver.class);
}
