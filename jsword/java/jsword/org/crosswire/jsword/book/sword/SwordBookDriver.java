package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.SystemUtils;
import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.BookType;
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
    public BookMetaData[] getBooks()
    {
        if (dirs == null)
        {
            return new BookMetaData[0];
        }

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
                        SwordConfig config = new SwordConfig(configfile, bookdir);

                        if (config.isSupported())
                        {
                            Book book = createBook(config, dirs[j]);
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
     * Create a Book to wrap the given backend
     */
    private Book createBook(SwordConfig config, File progdir) throws BookException, MalformedURLException, ParseException
    {
        String dataPath = config.getFirstValue(ConfigEntry.DATA_PATH);
        File baseurl = new File(progdir, dataPath);
        String path = baseurl.getAbsolutePath();

        Book book = null;

        ModuleType moddrv = config.getModDrv();
        if (moddrv.getBookType() == null)
        {
            // LATER(joe): how do we support books?
            log.warn("No support for book type: DRIVER_RAW_GEN_BOOK");
            throw new BookException(Msg.TYPE_UNSUPPORTED);
        }
        else if (moddrv.getBookType().equals(BookType.DICTIONARY))
        {
            Backend backend = null;

            if (moddrv.equals(ModuleType.RAW_LD))
            {
                backend = new RawLDBackend(config, path, 2);
            }
            else if (moddrv.equals(ModuleType.RAW_LD4))
            {
                backend = new RawLDBackend(config, path, 4);
            }
            else if (moddrv.equals(ModuleType.Z_LD))
            {
                backend = new ZLDBackend(config);
            }
            else
            {
                throw new BookException(Msg.TYPE_UNKNOWN, new Object[] { moddrv.getName(), path });
            }

            book = new SwordDictionary(this, config, backend, moddrv.getBookType());
        }
        else
        {
            Backend backend = null;
            if (moddrv.isCompressed())
            {
                backend = getCompressedBackend(config, path);
            }
            else
            {
                backend = new RawBackend(path);
            }

            book = new SwordBook(this, config, backend, moddrv.getBookType());
        }

        return book;
    }

    /**
     * 
     */
    private Backend getCompressedBackend(SwordConfig config, String path) throws BookException
    {
        switch (config.matchingIndex(SwordConstants.COMPRESSION_STRINGS, ConfigEntry.COMPRESS_TYPE))
        {
        case SwordConstants.COMPRESSION_ZIP:
            // The default blocktype (when we used fields) was SwordConstants.BLOCK_CHAPTER (2);
            // but the specified default here is BLOCK_BOOK (0)
            int blocktype = config.matchingIndex(SwordConstants.BLOCK_STRINGS, ConfigEntry.BLOCK_TYPE, SwordConstants.BLOCK_BOOK);
            return new GZIPBackend(path, blocktype);
      
        case SwordConstants.COMPRESSION_LZSS:
            return new LZSSBackend(config);
      
        default:
            throw new BookException(Msg.COMPRESSION_UNSUPPORTED, new Object[] { config.getFirstValue(ConfigEntry.COMPRESS_TYPE) });
        }
    }

    /**
     * Accessor for the Sword directory
     * @param dirs The new Sword directory
     */
    public static void setSwordPath(File[] dirs) throws BookException
    {
        // Fist we need to unregister any registered books from ourselves
        BookDriver[] matches = Books.getDriversByClass(SwordBookDriver.class);
        for (int i=0; i<matches.length; i++)
        {
            Books.unregisterDriver(matches[i]);
        }

        // If the new paths are empty then guess ...
        if (dirs == null || dirs.length == 0)
        {
            log.warn("No paths set, using defaults");
            dirs = getDefaultPaths();
        }

        SwordBookDriver.dirs = dirs;

        // Warn if any are not directories
        for (int i = 0; i < dirs.length; i++)
        {
            if (!dirs[i].isDirectory())
            {
                log.warn("No sword source found under: "+dirs[i]);
            }
        }

        // Now we need to register ourselves
        Books.registerDriver(new SwordBookDriver());
    }

    /**
     * Accessor for the Sword directory
     * @return The new Sword directory
     */
    public static File[] getSwordPath()
    {
        if (dirs == null || dirs.length == 0)
        {
            return new File[0];
        }

        return dirs;
    }

    /**
     * Have an OS dependent guess at where Sword might be installed
     */
    private static File[] getDefaultPaths()
    {
        List reply = new ArrayList();

        if (SystemUtils.IS_OS_WINDOWS)
        {
            reply.add(new File("C:\\Program Files\\CrossWire\\The SWORD Project"));
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
                    testDefaultPath(reply, datapath+"/mods.d");
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
            testDefaultPath(reply, swordhome+"/mods.d");
        }

        // .sword in the users home directory?
        testDefaultPath(reply, System.getProperty("user.home")+"/.sword/mods.d");

        // .jsword in the users home directory?
        testDefaultPath(reply, System.getProperty("user.home")+"/.jsword/mods.d");

        // mods.d in the current directory?
        testDefaultPath(reply, new File(".").getAbsolutePath()+"/mods.d");

        return (File[]) reply.toArray(new File[reply.size()]);
    }

    /**
     * Check to see if the given directory is a Sword mods.d directory
     * and then add it to the list if it is.
     */
    private static void testDefaultPath(List reply, String path)
    {
        File test = new File(path);
        if (test.isDirectory())
        {
            reply.add(test);
        }
    }

    /**
     * @return Returns the download directory.
     */
    public static File getDownloadDir()
    {
        return downloadDir;
    }

    /**
     * @param downloadDir The download directory to set.
     */
    public static void setDownloadDir(File downloadDir)
    {
        SwordBookDriver.downloadDir = downloadDir;
    }

    /**
     * The download directory
     */
    private static File downloadDir;

    /**
     * The directory URL
     */
    private static File[] dirs;

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
