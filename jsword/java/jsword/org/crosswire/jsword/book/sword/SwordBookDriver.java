package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.SystemUtils;
import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.basic.AbstractBookDriver;

/**
 * This represents all of the SwordBibles.
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
 * @author DM Smith [dmsmith555 at hotmail dot com]
 * @version $Id$
 */
public class SwordBookDriver extends AbstractBookDriver
{
    /**
     * Some basic name initialization
     */
    public SwordBookDriver()
    {
        log.debug("Starting Sword drivers");
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookDriver#getBooks()
     */
    public BookMetaData[] getBookMetaDatas()
    {
        List valid = new ArrayList();

        // Loop through the dirs in the lookup path
        for (int j=0; j<dirs.length; j++)
        {
            File mods = new File(dirs[j], "mods.d");
            if (mods.isDirectory())
            {
                String[] bookdirs = mods.list(new CustomFilenameFilter());
                //String[] bookdirs = NetUtil.listByFile(mods, new CustomURLFilter());

                // Loop through the entries in this mods.d directory
                for (int i=0; i<bookdirs.length; i++)
                {
                    String bookdir = bookdirs[i];
                    try
                    {
                        File configfile = new File(mods, bookdir);
                        SwordBookMetaData sbmd = new SwordBookMetaData(configfile, bookdir);
                        sbmd.setDriver(this);

                        if (sbmd.isSupported())
                        {
                            Book book = createBook(sbmd, dirs[j]);
                            valid.add(book.getBookMetaData());
                        }
                        else
                        {
                            String name = bookdir.substring(0, bookdir.indexOf(".conf"));
                            log.warn("Unsupported Book: "+name);
                        }
                    }
                    catch (Exception ex)
                    {
                        log.warn("Couldn't create SwordBookMetaData", ex);
                    }
                }            
            }
            else
            {
                log.debug("mods.d directory at "+mods+" does not exist");
            }
        }

        return (BookMetaData[]) valid.toArray(new BookMetaData[valid.size()]);
    }

    /**
     * A helper class for the SwordInstaller to tell us that it has copied a
     * new Book into our install dorectory
     * @param sbmd The SwordBookMetaData object for the new Book
     * @param bookpath The path that we have installed to
     */
    public static void registerNewBook(SwordBookMetaData sbmd, File bookpath) throws BookException
    {
        if (sbmd.isSupported())
        {
            BookDriver[] drivers = Books.getDriversByClass(SwordBookDriver.class);
            for (int i = 0; i < drivers.length; i++)
            {
                SwordBookDriver sdriver = (SwordBookDriver) drivers[i];
                Book book = sdriver.createBook(sbmd, bookpath);
                Books.addBook(book.getBookMetaData());
            }
        }
    }

    /**
     * Create a Book appropriate for the BookMetaData
     */
    private Book createBook(SwordBookMetaData sbmd, File progdir) throws BookException
    {
        ModuleType modtype = sbmd.getModuleType();
        if (modtype.getBookType() == null)
        {
            // LATER(joe): how do we support books?
            log.warn("No support for book type: DRIVER_RAW_GEN_BOOK");
            throw new BookException(Msg.TYPE_UNSUPPORTED);
        }

        return modtype.createBook(sbmd, progdir);
    }

    /**
     * Accessor for the Sword directory
     * @param dirs The new Sword directory
     */
    public static void setSwordPath(File[] dirs) throws BookException
    {
        dirs = validateSwordPath(dirs);
        if (dirs == null)
        {
            return;
        }
        
        // First we need to unregister any registered books from ourselves
        BookDriver[] matches = Books.getDriversByClass(SwordBookDriver.class);
        for (int i=0; i<matches.length; i++)
        {
            Books.unregisterDriver(matches[i]);
        }
        
        SwordBookDriver.dirs = dirs;

        // Now we need to register ourselves
        Books.registerDriver(new SwordBookDriver());
    }

    /**
     * validateSwordPath maintains the invariant that the download
     * location is first in the list. If null or an empty array
     * is passed then the defaultList is used.
     * @param files
     * @return null if the list is not to be used, otherwise it returns the list.
     */
    private static File[] validateSwordPath(File[] files)
    {
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
            return null;
        }

        if (useDefaultPaths)
        {
            log.warn("No paths set, using defaults");
        }

        // Maintain that downloadDir is in the array and that it is first.
        else if (!files[0].equals(downloadDir))
        {
            // Find it
            int pos = ArrayUtils.indexOf(files, downloadDir);

            // If it is not in the list then add it
            if (pos == -1)
            {
                File[] temp = new File[dirs.length + 1];
                temp[0] = downloadDir;
                for (int i = 0; i < files.length; i++)
                {
                    temp[i+1] = files[i];
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
        reply.add(new File(System.getProperty("user.home")+"/.jsword"));

        if (SystemUtils.IS_OS_WINDOWS)
        {
            testDefaultPath(reply, "C:\\Program Files\\CrossWire\\The SWORD Project");
        }
        else
        {
            // If it isn't unix then assume some sort of unix
            File sysconfig = new File("/etc/sword.conf");
            if (sysconfig.canRead())
            {
                try
                {
                    Properties prop = new Properties();
                    prop.load(new FileInputStream(sysconfig));
                    String datapath = prop.getProperty(ConfigEntry.DATA_PATH.getName());
                    testDefaultPath(reply, datapath);
                }
                catch (IOException ex)
                {
                    log.warn("Failed to read system config file", ex);
                }
            }
        }

        // if there is a property set for the sword home directory
        String swordhome = System.getProperty("sword.home");
        if (swordhome != null)
        {
            testDefaultPath(reply, swordhome);
        }

        // .sword in the users home directory?
        testDefaultPath(reply, System.getProperty("user.home")+"/.sword");

        // mods.d in the current directory?
        testDefaultPath(reply, new File(".").getAbsolutePath());

        return (File[]) reply.toArray(new File[reply.size()]);
    }

    /**
     * Check to see if the given directory is a Sword mods.d directory
     * and then add it to the list if it is.
     */
    private static void testDefaultPath(List reply, String path)
    {
        File where = new File(path);
        File mods = new File(path, "mods.d");
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
        if (!downloadDir.getPath().equals(""))
        {
            dirs[0] = downloadDir;
            log.debug("Setting sword download directory to: " + downloadDir);
        }
    }

    /**
     * The directory URL
     */
    private static File[] dirs = getDefaultPaths();

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(SwordBookDriver.class);

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
            return !name.startsWith("globals.") && name.endsWith(".conf");
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookDriver#getName()
     */
    public String getDriverName()
    {
        return "Sword";
    }
}
