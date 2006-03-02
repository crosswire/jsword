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
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.basic.AbstractBookDriver;
import org.crosswire.jsword.index.IndexManager;
import org.crosswire.jsword.index.IndexManagerFactory;
import org.crosswire.jsword.index.IndexStatus;
import org.crosswire.jsword.util.Project;

/**
 * This represents all of the SwordBibles.
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

        // Loop through the dirs in the lookup path
        for (int j = 0; j < dirs.length; j++)
        {
            File mods = new File(dirs[j], SwordConstants.DIR_CONF);
            if (mods.isDirectory())
            {
                String[] bookdirs = mods.list(new CustomFilenameFilter());

                // Loop through the entries in this mods.d directory
                for (int i = 0; i < bookdirs.length; i++)
                {
                    String bookdir = bookdirs[i];
                    try
                    {
                        File configfile = new File(mods, bookdir);
                        String internal = bookdir;
                        if (internal.endsWith(SwordConstants.EXTENSION_CONF))
                        {
                            internal = internal.substring(0, internal.length() - 5);
                        }
                        SwordBookMetaData sbmd = new SwordBookMetaData(configfile, internal);
                        sbmd.setDriver(this);

                        Book book = createBook(sbmd, dirs[j]);
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
                    catch (Exception ex)
                    {
                        log.warn("Couldn't create SwordBookMetaData", ex); //$NON-NLS-1$
                    }
                }
            }
            else
            {
                log.debug("mods.d directory at " + mods + " does not exist"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

        ConfigEntry.dumpStatistics();

        return (Book[]) valid.toArray(new Book[valid.size()]);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookDriver#isDeletable(org.crosswire.jsword.book.BookMetaData)
     */
    @Override
    public boolean isDeletable(Book dead)
    {
        SwordBookMetaData sbmd = (SwordBookMetaData) dead.getBookMetaData();
        File downloadDir = SwordBookDriver.getDownloadDir();
        File confFile = new File(downloadDir, sbmd.getConfPath());

        // We can only uninstall what we download into our download dir.
        return confFile.exists();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookDriver#delete(org.crosswire.jsword.book.BookMetaData)
     */
    @Override
    public void delete(Book dead) throws BookException
    {
        SwordBookMetaData sbmd = (SwordBookMetaData) dead.getBookMetaData();
        File downloadDir = SwordBookDriver.getDownloadDir();
        File confFile = new File(downloadDir, sbmd.getConfPath());

        // We can only uninstall what we download into our download dir.
        if (!confFile.exists())
        {
            throw new BookException(Msg.DELETE_FAILED, new Object [] {confFile});
        }

        File bookDir = new File(downloadDir, sbmd.getBookPath());

        // Delete the conf
        List failures = FileUtil.delete(confFile);
        if (failures.size() == 0)
        {
            // If the conf is gone, then we cannot get to the book
            // and then we can download it again.
            // But if the conf is present and the book is gone,
            // then we get errors.
            // Delete the download book's dir
            failures = FileUtil.delete(bookDir);
            Books.installed().removeBook(dead);
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
     * A helper class for the FtpSwordInstaller to tell us that it has copied a
     * new Book into our install directory
     * @param sbmd The SwordBookMetaData object for the new Book
     * @param bookpath The path that we have installed to
     * @throws BookException
     */
    public static void registerNewBook(SwordBookMetaData sbmd, File bookpath) throws BookException
    {
        BookDriver[] drivers = Books.installed().getDriversByClass(SwordBookDriver.class);
        for (int i = 0; i < drivers.length; i++)
        {
            SwordBookDriver sdriver = (SwordBookDriver) drivers[i];
            Book book = sdriver.createBook(sbmd, bookpath);
            Books.installed().addBook(book);
        }
    }

    /**
     * Create a Book appropriate for the BookMetaData
     */
    private Book createBook(SwordBookMetaData sbmd, File progdir) throws BookException
    {
        BookType modtype = sbmd.getBookType();
        if (modtype.getBookCategory() == null)
        {
            // LATER(joe): how do we support books?
            log.warn("No support for book type: DRIVER_RAW_GEN_BOOK"); //$NON-NLS-1$
            throw new BookException(Msg.TYPE_UNSUPPORTED);
        }

        return modtype.createBook(sbmd, progdir);
    }

    /**
     * Accessor for the Sword directory
     * @param theNewDirs The new Sword directory
     * @throws BookException
     */
    public static void setSwordPath(File[] theNewDirs) throws BookException
    {
        File[] newDirs = theNewDirs;
        newDirs = validateSwordPath(newDirs);
        if (newDirs == null)
        {
            return;
        }

        SwordBookDriver.dirs = newDirs;

        // Now we need to (re)register ourselves
        Books.installed().registerDriver(INSTANCE);
    }

    /**
     * validateSwordPath maintains the invariant that the download
     * location is first in the list. If null or an empty array
     * is passed then the defaultList is used.
     * @param theFiles
     * @return null if the list is not to be used, otherwise it returns the list.
     */
    private static File[] validateSwordPath(File[] theFiles)
    {
        File[] files = theFiles;
        // Get the current download file location
        File downloadDir = SwordBookDriver.dirs[0];
        boolean useDefaultPaths = false;

        // If the new paths are empty then guess ...
        if (files == null || files.length == 0)
        {
            files = getDefaultPaths();
            useDefaultPaths = true;
        }

        // If there is no change then there is nothing to do
        if (Arrays.equals(files, SwordBookDriver.dirs))
        {
            return EMPTY_FILE_ARRAY;
        }

        if (useDefaultPaths)
        {
            log.warn("No paths set, using defaults"); //$NON-NLS-1$
        }

        // Maintain that downloadDir is in the array and that it is first.
        else if (!files[0].equals(downloadDir))
        {
            // Find it
            int pos = -1;
            for (int i = 0; i < files.length; i++)
            {
                if (downloadDir.equals(files[i]))
                {
                    pos = i;
                    break;
                }
            }

            // If it is not in the list then add it
            if (pos == -1)
            {
                File[] temp = new File[files.length + 1];
                temp[0] = downloadDir;
                for (int i = 0; i < files.length; i++)
                {
                    temp[i + 1] = files[i];
                }
                files = temp;
            }
            else
            {
                // move downloadDir to the front
                for (int i = pos; i > 0; i--)
                {
                    files[pos] = files[pos - 1];
                }
                files[0] = downloadDir;
            }
        }

        return files;
    }

    /**
     * Accessor for the Sword directory
     * @return The new Sword directory
     */
    public static File[] getSwordPath()
    {
        return dirs;
    }

    /**
     * Have an OS dependent guess at where Sword might be installed
     */
    private static File[] getDefaultPaths()
    {
        List reply = new ArrayList();

        // .jsword in the users home directory is the first location
        reply.add(new File(System.getProperty(PROPERTY_USER_HOME) + File.separator + Project.DIR_PROJECT));

        if (System.getProperty("os.name").startsWith("Windows")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            testDefaultPath(reply, DIR_WINDOWS_DEFAULT);
        }
        else
        {
            // If it isn't unix then assume some sort of unix
            File sysconfig = new File(DIR_UNIX_GLOBAL_CONF);
            if (sysconfig.canRead())
            {
                try
                {
                    Properties prop = new Properties();
                    prop.load(new FileInputStream(sysconfig));
                    String datapath = prop.getProperty(ConfigEntryType.DATA_PATH.toString());
                    testDefaultPath(reply, datapath);
                }
                catch (IOException ex)
                {
                    log.warn("Failed to read system config file", ex); //$NON-NLS-1$
                }
            }
        }

        // if there is a property set for the sword home directory
        String swordhome = System.getProperty(PROPERTY_SWORD_HOME);
        if (swordhome != null)
        {
            testDefaultPath(reply, swordhome);
        }

        // .sword in the users home directory?
        testDefaultPath(reply, System.getProperty(PROPERTY_USER_HOME) + File.separator + DIR_SWORD_CONF);

        // mods.d in the current directory?
        testDefaultPath(reply, new File(".").getAbsolutePath()); //$NON-NLS-1$

        return (File[]) reply.toArray(new File[reply.size()]);
    }

    /**
     * Check to see if the given directory is a Sword mods.d directory
     * and then add it to the list if it is.
     */
    private static void testDefaultPath(List reply, String path)
    {
        File where = new File(path);
        File mods = new File(path, SwordConstants.DIR_CONF);
        if (mods.isDirectory())
        {
            reply.add(where);
        }
    }

    /**
     * @return Returns the download directory.
     */
    public static File getDownloadDir()
    {
        return dirs[0];
    }

    /**
     * @param downloadDir The download directory to set.
     */
    public static void setDownloadDir(File downloadDir)
    {
        if (!downloadDir.getPath().equals("")) //$NON-NLS-1$
        {
            dirs[0] = downloadDir;
            log.debug("Setting sword download directory to: " + downloadDir); //$NON-NLS-1$
        }
    }

    /**
     * Check that the directories in the version directory really
     * represent versions.
     */
    private static class CustomFilenameFilter implements FilenameFilter
    {
        /* (non-Javadoc)
         * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
         */
        public boolean accept(File parent, String name)
        {
            return !name.startsWith(PREFIX_GLOBALS) && name.endsWith(SwordConstants.EXTENSION_CONF);
        }
    }

    /**
     * An empty immutable <code>Rile</code> array.
     */
    public static final File[] EMPTY_FILE_ARRAY = new File[0];

    /**
     * Default windows installation directory
     */
    private static final String DIR_WINDOWS_DEFAULT = "C:\\Program Files\\CrossWire\\The SWORD Project"; //$NON-NLS-1$

    /**
     * Users config directory for Sword in Unix
     */
    private static final String DIR_SWORD_CONF = ".sword"; //$NON-NLS-1$

    /**
     * Sword global config file
     */
    private static final String DIR_UNIX_GLOBAL_CONF = "/etc/sword.conf"; //$NON-NLS-1$

    /**
     * System property for sword home directory
     */
    private static final String PROPERTY_SWORD_HOME = "sword.home"; //$NON-NLS-1$

    /**
     * Java system property for users home directory
     */
    private static final String PROPERTY_USER_HOME = "user.home"; //$NON-NLS-1$

    /**
     * File prefix for config file
     */
    private static final String PREFIX_GLOBALS = "globals."; //$NON-NLS-1$

    /**
     * A shared instance of this driver.
     */
    private static final BookDriver INSTANCE = new SwordBookDriver();

    /**
     * The directory URL
     */
    private static File[] dirs = getDefaultPaths();

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(SwordBookDriver.class);

}
