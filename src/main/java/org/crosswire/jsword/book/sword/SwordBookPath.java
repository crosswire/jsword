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
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.crosswire.common.util.CWProject;
import org.crosswire.common.util.OSType;
import org.crosswire.common.util.PropertyMap;
import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Books;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This represents all of the Sword Books (aka modules).
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
 */
public final class SwordBookPath {
    /**
     * Some basic name initialization
     */
    private SwordBookPath() {
    }

    /**
     * Establish additional locations that Sword may hold books.
     * 
     * @param theNewDirs
     *            The new Sword directories
     * @throws BookException
     */
    public static void setAugmentPath(File[] theNewDirs) throws BookException {
        File[] newDirs = theNewDirs;
        if (newDirs == null) {
            return;
        }

        SwordBookPath.augmentPath = newDirs.clone();

        // Now we need to (re)register ourselves
        Books.installed().registerDriver(SwordBookDriver.instance());
    }

    /**
     * Retrieve the additional locations that Sword may hold Books.
     * 
     * @return The new Sword directory
     */
    public static File[] getAugmentPath() {
        return augmentPath.clone();
    }

    /**
     * Obtain a prioritized path of Book locations. This contains the download
     * dir as the first location, the user's augment path and finally all the
     * discovered standard locations.
     * 
     * @return the array of Book locations.
     */
    public static File[] getSwordPath() {
        ArrayList<File> swordPath = new ArrayList<File>();

        // The first place to look for Books
        swordPath.add(getSwordDownloadDir());

        // Then all the user's augments
        if (augmentPath != null) {
            for (int i = 0; i < augmentPath.length; i++) {
                File path = augmentPath[i];
                if (!swordPath.contains(path)) {
                    swordPath.add(path);
                }
            }
        }

        File[] defaultPath = getDefaultPaths();
        // Then all the user's bookDirs
        if (defaultPath != null) {
            for (int i = 0; i < defaultPath.length; i++) {
                File path = defaultPath[i];
                if (!swordPath.contains(path)) {
                    swordPath.add(path);
                }
            }
        }

        return swordPath.toArray(new File[swordPath.size()]);
    }

    /**
     * Get a list of books in a given location.
     * 
     * @param bookDir
     *            the directory in which to look
     * @return the list of books in that location
     */
    public static String[] getBookList(File bookDir) {
        return bookDir.list(new CustomFilenameFilter());
    }

    /**
     * Search all of the "standard" Sword locations for Books. Remember all the
     * locations.
     */
    private static File[] getDefaultPaths() {
        // If possible migrate the old location to the new one
        migrateBookDir();

        List<File> bookDirs = new ArrayList<File>();

        String home = System.getProperty(PROPERTY_USER_HOME);

        // Is sword.conf in the current directory?
        readSwordConf(bookDirs, ".");

        // mods.d in the current directory?
        testDefaultPath(bookDirs, ".");

        // how about in the library, just next door?
        testDefaultPath(bookDirs, ".." + File.separator + DIR_SWORD_LIBRARY);

        // if there is a property set for the sword home directory
        // The Sword project defines SWORD_HOME, but JSword expects this to be
        // transformed into sword.home.
        String swordhome = System.getProperty(PROPERTY_SWORD_HOME);
        if (swordhome != null) {
            testDefaultPath(bookDirs, swordhome);

            // how about in the library, just next door?
            testDefaultPath(bookDirs, swordhome + File.separator + ".." + File.separator + DIR_SWORD_LIBRARY);
        }

        if (System.getProperty("os.name").startsWith("Windows")) {
            testDefaultPath(bookDirs, DIR_WINDOWS_DEFAULT);
            // how about in the library, just next door?
            testDefaultPath(bookDirs, DIR_WINDOWS_DEFAULT + File.separator + ".." + File.separator + DIR_SWORD_LIBRARY);
        }

        // .sword in the users home directory?
        readSwordConf(bookDirs, home + File.separator + DIR_SWORD_CONF);

        // Check for sword.conf in the usual places
        String[] sysconfigPaths = StringUtil.split(DIR_SWORD_GLOBAL_CONF, ':');
        for (int i = 0; i < sysconfigPaths.length; i++) {
            readSwordConf(bookDirs, sysconfigPaths[i]);
        }

        URI userDataArea = OSType.getOSType().getUserAreaFolder(DIR_SWORD_CONF, DIR_SWORD_CONF_ALT);

        // Check look for mods.d in the sword user data area
        testDefaultPath(bookDirs, new File(userDataArea.getPath()));

        // JSword used to hold books in ~/.jsword (or its equivalent) but has
        // code that will
        // migrate it to ~/.sword (or its equivalent)
        // If the migration did not work then use the old area
        testDefaultPath(bookDirs, new File(CWProject.instance().getWritableProjectDir().getPath()));

        return bookDirs.toArray(new File[bookDirs.size()]);
    }

    private static void readSwordConf(List<File> bookDirs, File swordConfDir) {
        File sysconfig = new File(swordConfDir, SWORD_GLOBAL_CONF);
        if (sysconfig.canRead()) {
            InputStream is = null;
            try {
                PropertyMap prop = new PropertyMap();
                is = new FileInputStream(sysconfig);
                prop.load(is);
                String datapath = prop.get(DATA_PATH);
                testDefaultPath(bookDirs, datapath);
                datapath = prop.get(AUGMENT_PATH);
                testDefaultPath(bookDirs, datapath);
            } catch (IOException ex) {
                log.warn("Failed to read system config file", ex);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        log.warn("Failed to close system config file", e);
                    }
                }
            }
        }
    }

    private static void readSwordConf(List<File> bookDirs, String swordConfDir) {
        readSwordConf(bookDirs, new File(swordConfDir));
    }

    /**
     * Check to see if the given directory is a Sword mods.d directory and then
     * add it to the list if it is.
     * 
     * @param bookDirs
     *            The list to add good paths
     * @param path
     *            the path to check
     */
    private static void testDefaultPath(List<File> bookDirs, File path) {
        if (path == null) {
            return;
        }

        File mods = new File(path, SwordConstants.DIR_CONF);
        if (mods.isDirectory() && mods.canRead()) {
            bookDirs.add(path);
        }
    }

    /**
     * Check to see if the given directory is a Sword mods.d directory and then
     * add it to the list if it is.
     * 
     * @param bookDirs
     *            The list to add good paths
     * @param path
     *            the path to check
     */
    private static void testDefaultPath(List<File> bookDirs, String path) {
        if (path == null) {
            return;
        }

        testDefaultPath(bookDirs, new File(path));
    }

    private static File getDefaultDownloadPath() {
        File path = null;
        File[] possiblePaths = getDefaultPaths();

        if (possiblePaths != null) {
            for (int i = 0; i < possiblePaths.length; i++) {
                File mods = new File(possiblePaths[i], SwordConstants.DIR_CONF);
                if (mods.canWrite()) {
                    path = possiblePaths[i];
                    break;
                }
            }
        }

        // If it is not found on the path then it doesn't exist yet and needs to
        // be established
        if (path == null) {
            URI userDataArea = OSType.getOSType().getUserAreaFolder(DIR_SWORD_CONF, DIR_SWORD_CONF_ALT);
            path = new File(userDataArea.getPath());
        }

        return path;
    }

    private static void migrateBookDir() {
        // Books should be on this path
        URI userDataArea = OSType.getOSType().getUserAreaFolder(DIR_SWORD_CONF, DIR_SWORD_CONF_ALT);

        File swordBookPath = new File(userDataArea.getPath());

        // The "old" Book location might be in one of two locations
        // It might be ~/.jsword or the new project dir
        File oldPath = new File(CWProject.instance().getDeprecatedWritableProjectDir().getPath());

        if (oldPath.isDirectory()) {
            migrateBookDir(oldPath, swordBookPath);
            return;
        }

        // now trying the new project dir
        oldPath = new File(CWProject.instance().getWritableProjectDir().getPath());

        if (oldPath.isDirectory()) {
            migrateBookDir(oldPath, swordBookPath);
            return;
        }

        // Finally, it might be ~/.sword
        oldPath = new File(OSType.DEFAULT.getUserAreaFolder(DIR_SWORD_CONF, DIR_SWORD_CONF_ALT).getPath());
        if (oldPath.isDirectory()) {
            migrateBookDir(oldPath, swordBookPath);
        }
    }

    private static void migrateBookDir(File oldPath, File newPath) {
        // move the modules and confs
        File oldDataDir = new File(oldPath, SwordConstants.DIR_DATA);
        File newDataDir = new File(newPath, SwordConstants.DIR_DATA);
        File oldConfDir = new File(oldPath, SwordConstants.DIR_CONF);
        File newConfDir = new File(newPath, SwordConstants.DIR_CONF);

        // move the modules
        if (!migrate(oldDataDir, newDataDir)) {
            return;
        }

        // move the confs
        if (!migrate(oldConfDir, newConfDir)) {
            // oops, restore the modules
            migrate(newDataDir, oldDataDir);
        }
    }

    private static boolean migrate(File oldPath, File newPath) {
        if (oldPath.equals(newPath) || !oldPath.exists()) {
            return true;
        }

        // make sure the parent exists
        File parent = newPath.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            return false;
        }

        return oldPath.renameTo(newPath);
    }

    /**
     * Get the download directory, which is either the one that the user chose
     * or that JSword picked for the user.
     * 
     * @return Returns the download directory.
     */
    public static File getSwordDownloadDir() {
        if (overrideDownloadDir != null) {
            return overrideDownloadDir;
        }
        return defaultDownloadDir;
    }

    /**
     * @return Returns the download directory that the user chose.
     */
    public static File getDownloadDir() {
        return overrideDownloadDir;
    }

    /**
     * @param dlDir
     *            The download directory that the user specifies.
     */
    public static void setDownloadDir(File dlDir) {
        if (!"".equals(dlDir.getPath())) {
            overrideDownloadDir = dlDir;
            log.debug("Setting sword download directory to: {}", dlDir);
        }
    }

    /**
     * Check that the directories in the version directory really represent
     * versions.
     */
    static class CustomFilenameFilter implements FilenameFilter {
        /*
         * (non-Javadoc)
         * 
         * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
         */
        public boolean accept(File parent, String name) {
            return !name.startsWith(PREFIX_GLOBALS) && name.endsWith(SwordConstants.EXTENSION_CONF);
        }
    }

    /**
     * Default windows installation directory
     */
    private static final String DIR_WINDOWS_DEFAULT = "C:\\Program Files\\CrossWire\\The SWORD Project";

    /**
     * Library may be a sibling of DIR_WINDOWS_DEFAULT or SWORD_HOME or CWD
     */
    private static final String DIR_SWORD_LIBRARY = "library";

    /**
     * Users config directory for Sword in Unix
     */
    private static final String DIR_SWORD_CONF = ".sword";

    /**
     * Users config directory for Sword in Unix
     */
    private static final String DIR_SWORD_CONF_ALT = "Sword";

    /**
     * Sword global config file
     */
    private static final String SWORD_GLOBAL_CONF = "sword.conf";

    /**
     * Sword global config file locations
     */
    private static final String DIR_SWORD_GLOBAL_CONF = "/etc:/usr/local/etc";

    /**
     * Sword global config file's path to where mods can be found
     */
    private static final String DATA_PATH = "DataPath";

    /**
     * Sword global config file's path to where mods can be found
     */
    private static final String AUGMENT_PATH = "AugmentPath";

    /**
     * System property for sword home directory
     */
    private static final String PROPERTY_SWORD_HOME = "sword.home";

    /**
     * Java system property for users home directory
     */
    private static final String PROPERTY_USER_HOME = "user.home";

    /**
     * File prefix for config file
     */
    private static final String PREFIX_GLOBALS = "globals.";

    /**
     * The directory URL
     */
    private static File[] augmentPath = new File[0];

    /**
     * The directory URL
     */
    private static File defaultDownloadDir = getDefaultDownloadPath();

    /**
     * The directory URL
     */
    private static File overrideDownloadDir;

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(SwordBookPath.class);

}
