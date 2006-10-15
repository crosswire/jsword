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
 * ID: $Id: SwordBookDriver.java 1117 2006-08-15 16:41:29 -0400 (Tue, 15 Aug 2006) dmsmith $
 */
package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.util.Project;

/**
 * This represents all of the Sword Books (aka modules).
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class SwordBookPath
{
    /**
     * Some basic name initialization
     */
    private SwordBookPath()
    {
    }

    /**
     * Accessor for the Sword directory
     * @param theNewDirs The new Sword directory
     * @throws BookException
     */
    public static void setAugmentPath(File[] theNewDirs) throws BookException
    {
        File[] newDirs = theNewDirs;
        if (newDirs == null)
        {
            return;
        }

        SwordBookPath.augmentPath = (File[]) newDirs.clone();

        // Now we need to (re)register ourselves
        Books.installed().registerDriver(SwordBookDriver.instance());
    }

    /**
     * Accessor for the Sword directory
     * @return The new Sword directory
     */
    public static File[] getAugmentPath()
    {
        return (File[]) augmentPath.clone();
    }

    /**
     * Obtain a prioritized path of Book locations.
     * This contains the downloadDir as the first location,
     * the user's augment path and finally all the discovered
     * standard locations.
     * 
     * @return the array of Book locations.
     */
    public static File[] getSwordPath()
    {
        ArrayList swordPath = new ArrayList();

        // The first place to look for Books
        swordPath.add(downloadDir);

        // Then all the user's augments
        if (augmentPath != null)
        {
            for (int i = 0; i < augmentPath.length; i++)
            {
                File path = augmentPath[i];
                if (!swordPath.contains(path))
                {
                    swordPath.add(path);
                }
            }
        }

        File[] defaultPath = getDefaultPaths();
        // Then all the user's bookDirs
        if (defaultPath != null)
        {
            for (int i = 0; i < defaultPath.length; i++)
            {
                File path = defaultPath[i];
                if (!swordPath.contains(path))
                {
                    swordPath.add(path);
                }
            }
        }

        return (File[]) swordPath.toArray(new File[swordPath.size()]);
    }

    public static String[] getBookList(File bookDir)
    {
        return bookDir.list(new CustomFilenameFilter());
    }

    /**
     * Search all of the "standard" Sword locations for Books.
     * Remember all the locations.
     */
    private static File[] getDefaultPaths()
    {
        // If possible migrate the old location to the new one
        migrateBookDir();

        List bookDirs = new ArrayList();

        String home = System.getProperty(PROPERTY_USER_HOME);

        // Is sword.conf in the current diretory?
        readSwordConf(bookDirs, "."); //$NON-NLS-1$

        // mods.d in the current directory?
        testDefaultPath(bookDirs, "."); //$NON-NLS-1$

        // how about in the library, just next door?
        testDefaultPath(bookDirs, ".." + File.separator + DIR_SWORD_LIBRARY); //$NON-NLS-1$

        // if there is a property set for the sword home directory
        String swordhome = System.getProperty(PROPERTY_SWORD_HOME);
        if (swordhome != null)
        {
            testDefaultPath(bookDirs, swordhome);

            // how about in the library, just next door?
            testDefaultPath(bookDirs, swordhome + File.separator + ".." + File.separator + DIR_SWORD_LIBRARY); //$NON-NLS-1$
        }

        if (System.getProperty("os.name").startsWith("Windows")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            testDefaultPath(bookDirs, DIR_WINDOWS_DEFAULT);
            // how about in the library, just next door?
            testDefaultPath(bookDirs, DIR_WINDOWS_DEFAULT + File.separator + ".." + File.separator + DIR_SWORD_LIBRARY); //$NON-NLS-1$
        }

        // .sword in the users home directory?
        readSwordConf(bookDirs, home + File.separator + DIR_SWORD_CONF);

        // Check for sword.conf in the usual places
        String [] sysconfigPaths = StringUtil.split(DIR_SWORD_GLOBAL_CONF, ':');
        for (int i = 0; i < sysconfigPaths.length; i++)
        {
            readSwordConf(bookDirs, sysconfigPaths[i]);
        }

        URL userDataArea = Project.instance().getUserProjectDir(DIR_SWORD_CONF, DIR_SWORD_CONF_ALT);

        // Check look for mods.d in the sword user data area
        testDefaultPath(bookDirs, new File(userDataArea.getFile()));

        // If the migration did not work then use the old area
        testDefaultPath(bookDirs, new File(Project.instance().getUserProjectDir().getFile()));

        return (File[]) bookDirs.toArray(new File[bookDirs.size()]);
    }

    private static void readSwordConf(List bookDirs, File swordConfDir)
    {
        File sysconfig = new File(swordConfDir, SWORD_GLOBAL_CONF);
        if (sysconfig.canRead())
        {
            InputStream is = null;
            try
            {
                Properties prop = new Properties();
                is = new FileInputStream(sysconfig);
                prop.load(is);
                String datapath = prop.getProperty(DATA_PATH);
                testDefaultPath(bookDirs, datapath);
                datapath = prop.getProperty(AUGMENT_PATH);
                testDefaultPath(bookDirs, datapath);
            }
            catch (IOException ex)
            {
                log.warn("Failed to read system config file", ex); //$NON-NLS-1$
            }
            finally
            {
                if (is != null)
                {
                    try
                    {
                        is.close();
                    }
                    catch (IOException e)
                    {
                        log.warn("Failed to close system config file", e); //$NON-NLS-1$
                    }
                }
            }
        }
    }

    private static void readSwordConf(List bookDirs, String swordConfDir)
    {
        readSwordConf(bookDirs, new File(swordConfDir));
    }

    /**
     * Check to see if the given directory is a Sword mods.d directory
     * and then add it to the list if it is.
     * @param bookDirs The list to add good paths
     * @param path the path to check 
     */
    private static void testDefaultPath(List bookDirs, File path)
    {
        if (path == null)
        {
            return;
        }

        File mods = new File(path, SwordConstants.DIR_CONF);
        if (mods.isDirectory() && mods.canRead())
        {
            bookDirs.add(path);
        }
    }

    /**
     * Check to see if the given directory is a Sword mods.d directory
     * and then add it to the list if it is.
     * @param bookDirs The list to add good paths
     * @param path the path to check 
     */
    private static void testDefaultPath(List bookDirs, String path)
    {
        if (path == null)
        {
            return;
        }

        testDefaultPath(bookDirs, new File(path));
    }

    private static File getDefaultDownloadPath()
    {
        File path = null;
        File[] possiblePaths = getDefaultPaths();

        if (possiblePaths != null)
        {
            for (int i = 0; i < possiblePaths.length; i++)
            {
                File mods = new File(possiblePaths[i], SwordConstants.DIR_CONF);
                if (mods.canWrite())
                {
                    path = possiblePaths[i];
                    break;
                }
            }
        }

        // If it is not found on the path then it doesn't exist yet and needs to be established
        if (path == null)
        {
            URL userDataArea = Project.instance().getUserProjectDir(DIR_SWORD_CONF, DIR_SWORD_CONF_ALT);
            path = new File(userDataArea.getFile());
        }

        return path;
    }

    private static void migrateBookDir()
    {
        // Books should be on this path
        URL userDataArea = Project.instance().getUserProjectDir(DIR_SWORD_CONF, DIR_SWORD_CONF_ALT);

        File swordBookPath = new File(userDataArea.getFile());

        // The "old" Book location might be in one of two locations
        // It might be ~/.jsword or the new project dir
        File oldPath = new File(Project.instance().getDeprecatedUserProjectDir().getFile());

        if (oldPath.isDirectory())
        {
            migrateBookDir(oldPath, swordBookPath);
            return;
        }

        oldPath = new File(Project.instance().getUserProjectDir().getFile());

        if (oldPath.isDirectory())
        {
            migrateBookDir(oldPath, swordBookPath);
        }
    }

    private static void migrateBookDir(File oldPath, File newPath)
    {
        // move the modules and confs
        File oldDataDir = new File(oldPath, SwordConstants.DIR_DATA);
        File newDataDir = new File(newPath, SwordConstants.DIR_DATA);
        File oldConfDir = new File(oldPath, SwordConstants.DIR_CONF);
        File newConfDir = new File(newPath, SwordConstants.DIR_CONF);

        // move the modules
        if (!migrate(oldDataDir, newDataDir))
        {
            return;
        }

        // move the confs
        if (!migrate(oldConfDir, newConfDir))
        {
            // oops, restore the modules
            migrate(newDataDir, oldDataDir);
        }
    }

    private static boolean migrate(File oldPath, File newPath)
    {
        if (oldPath.equals(newPath) || !oldPath.exists())
        {
            return true;
        }

        File parent = newPath.getParentFile();
        if (!parent.exists())
        {
            parent.mkdirs();
        }

        return oldPath.renameTo(newPath);
    }

    /**
     * @return Returns the download directory.
     */
    public static File getDownloadDir()
    {
        return downloadDir;
    }

    /**
     * @param dlDir The download directory to set.
     */
    public static void setDownloadDir(File dlDir)
    {
        if (!dlDir.getPath().equals("")) //$NON-NLS-1$
        {
            downloadDir = dlDir;
            log.debug("Setting sword download directory to: " + dlDir); //$NON-NLS-1$
        }
    }

    /**
     * Check that the directories in the version directory really
     * represent versions.
     */
    static class CustomFilenameFilter implements FilenameFilter
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
     * Default windows installation directory
     */
    private static final String DIR_WINDOWS_DEFAULT = "C:\\Program Files\\CrossWire\\The SWORD Project"; //$NON-NLS-1$

    /**
     * Library may be a sibling of DIR_WINDOWS_DEFAULT or SWORD_HOME or CWD
     */
    private static final String DIR_SWORD_LIBRARY = "library"; //$NON-NLS-1$

    /**
     * Users config directory for Sword in Unix
     */
    private static final String DIR_SWORD_CONF = ".sword"; //$NON-NLS-1$

    /**
     * Users config directory for Sword in Unix
     */
    private static final String DIR_SWORD_CONF_ALT = "Sword"; //$NON-NLS-1$

    /**
     * Sword global config file
     */
    private static final String SWORD_GLOBAL_CONF = "sword.conf"; //$NON-NLS-1$

    /**
     * Sword global config file locations
     */
    private static final String DIR_SWORD_GLOBAL_CONF = "/etc:/usr/local/etc"; //$NON-NLS-1$

    /**
     * Sword global config file's path to where mods can be found
     */
    private static final String DATA_PATH = "DataPath"; //$NON-NLS-1$

    /**
     * Sword global config file's path to where mods can be found
     */
    private static final String AUGMENT_PATH = "AugmentPath"; //$NON-NLS-1$

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
     * The directory URL
     */
    private static File[] augmentPath = new File[0];

    /**
     * The directory URL
     */
    private static File downloadDir = getDefaultDownloadPath();

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(SwordBookPath.class);

}
