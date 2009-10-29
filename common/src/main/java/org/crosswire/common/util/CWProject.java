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
 * Copyright: 2008
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.common.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * The Project class looks after the source of project files.
 * These are per user files and as such have a different location
 * on different operating systems. These are:<br/>
 *
 * <table>
 * <tr><td>Mac OS X</td><td>~/Library/Application Support/JSword</td></tr>
 * <tr><td>Win NT/2000/XP/ME/9x</td><td>~/Application Data/JSword (~ is all over the place, but Java figures it out)</td></tr>
 * <tr><td>Unix and otherwise</td><td>~/.jsword</td></tr>
 * </table>
 *
 * <p>
 * Previously the location was ~/.jsword, which is unfriendly in the Windows and Mac world.
 * If this location is found on Mac or Windows, it will be moved to the new location,
 * if different and possible.
 * </p>
 *
 * <p>
 * Note: If the Java System property jsword.home is set and it exists and is writable
 * then it will be used instead of the above location. This is useful for USB Drives
 * and other portable implementations of JSword. It is recommended that this name be JSword.
 * </p>
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public final class CWProject
{
    /**
     * Accessor for the resource singleton.
     */
    public static CWProject instance()
    {
        return instance;
    }

    /**
     * Establish how this project finds it's resources.
     * @param homeProperty a property that is used as the home directory name.
     *                If this property has a value then homeDir and altHomeDir are ignored.
     *                Defaults to jsword.home.
     * @param homeDir the name of the directory to be used for Unix.
     *                Typically this is a hidden directory, that is, it begins with a '.'.
     *                Defaults to .jsword
     * @param altHomeDir the name of the directory to be used for other OSes. This should not be a hidden directory.
     *                Defaults to JSword.
     */
    public static void setHome(String homeProperty, String homeDir, String altHomeDir)
    {
        CWProject.homeProperty = homeProperty;
        CWProject.homeDirectory = homeDir;
        CWProject.homeAltDirectory = altHomeDir;
        instance().establishProjectHome();
    }

    /**
     * Get the writable user project directory.
     * 
     * @return the writable user project directory.
     */
    public URI getWritableProjectDir()
    {
        establishProjectHome();
        return writeHome;
    }

    /**
     * Get the locations where project resources can be found.
     * 
     * @return an array of URIs which can be used to look up resources.
     */
    public URI[] getProjectResourceDirs()
    {
        establishProjectHome();
        return (URI[]) homes.clone();
    }

    /**
     * Get the location where the project directory used to be.
     *
     * @return ~/.jsword
     */
    public URI getDeprecatedWritableProjectDir()
    {
        return OSType.DEFAULT.getUserAreaFolder(homeDirectory, homeAltDirectory);
    }

    /**
     * Create a the URI for a (potentially non-existent) file to which we can write.
     * Typically this is used to store user preferences and application overrides.
     * This method of acquiring files is preferred over getResourceProperties()
     * as this is writable and can take into account user preferences.
     * This method makes no promise that the URI returned is valid. It is
     * totally untested, so reading may well cause errors.
     * @param subject The name (minus the .xxx extension)
     * @param extension The extension, prefixed with a '.' See: {@link FileUtil} for a list of popular extensions.
     * @return The resource as a URI
     */
    public URI getWritableURI(String subject, String extension)
    {
        return NetUtil.lengthenURI(getWritableProjectDir(), subject + extension);
    }

    /**
     * A directory within the project directory.
     * 
     * @param subject A name for the subdirectory of the Project directory.
     * @return A file: URI pointing at a local writable directory.
     */
    public URI getWriteableProjectSubdir(String subject, boolean create) throws IOException
    {
        URI temp = NetUtil.lengthenURI(getWritableProjectDir(), subject);

        if (create && !NetUtil.isDirectory(temp))
        {
            NetUtil.makeDirectory(temp);
        }

        return temp;
    }

    /**
     * Prevent instantiation.
     */
    private CWProject()
    {
    }

    /**
     * Establishes the user's project directory.
     * In a CD installation, the home directory on the CD will be read-only.
     * This is not sufficient. We also need a writable home directory. And
     * in looking up resources, the ones in the writable directory trump
     * those in the readable directory, allowing the read-only resources
     * to be overridden.
     * <p>Here is the lookup order:
     * <ol>
     * <li>Check first to see if the jsword.home property is set.</li>
     * <li>Check for the existence of a platform specific project area and for the existence of a deprecated project area (~/.jsword on Windows and Mac)
     * and if it exists and it is possible "upgrade" to the platform specific project area. Of these "two" only one is the folder to check.</li>
     * </ol>
     * In checking these areas, if the one is read-only, add it to the list and keep going.
     * However, if it is also writable, then use it alone.
     */
    private void establishProjectHome()
    {
        if (writeHome == null && readHome == null)
        {
            // if there is a property set for the jsword home directory
            String cwHome = System.getProperty(homeProperty);
            if (cwHome != null)
            {
                URI home = NetUtil.getURI(new File(cwHome));
                if (NetUtil.canWrite(home))
                {
                    writeHome = home;
                }
                else if (NetUtil.canRead(home))
                {
                    readHome = home;
                }
                // otherwise jsword.home is not usable.
            }
        }

        if (writeHome == null)
        {
            URI path = OSType.getOSType().getUserAreaFolder(homeDirectory, homeAltDirectory);
            URI oldPath = getDeprecatedWritableProjectDir();
            writeHome = migrateUserProjectDir(oldPath, path);
        }

        if (homes == null)
        {
            if (readHome == null)
            {
                homes = new URI[] { writeHome };
            }
            else
            {
                homes = new URI[] { writeHome, readHome };
            }

            // Now that we know the "home" we can set other global notions of home.
            // TODO(dms): refactor this to CWClassLoader and NetUtil.
            CWClassLoader.setHome(getProjectResourceDirs());

            try
            {
                URI uricache = getWriteableProjectSubdir(DIR_NETCACHE, true);
                File filecache = new File(uricache.getPath());
                NetUtil.setURICacheDir(filecache);
            }
            catch (IOException ex)
            {
                // This isn't fatal, it just means that NetUtil will try to use $TMP
                // in place of a more permanent solution.
                log.warn("Failed to get directory for NetUtil.setURICacheDir()", ex); //$NON-NLS-1$
            }

        }
    }

    /**
     * Migrates the user's project dir, if necessary and possible.
     *
     * @param oldPath the path to the old, deprecated location
     * @param newPath the path to the new location
     * @return newPath if the migration was possible or not needed.
     */
    private URI migrateUserProjectDir(URI oldPath, URI newPath)
    {
        if (oldPath.toString().equals(newPath.toString()))
        {
            return newPath;
        }

        if (NetUtil.isDirectory(oldPath))
        {
            File oldDir = new File(oldPath.getPath());
            File newDir = new File(newPath.getPath());

            // renameTo will return false if it could not rename.
            // This will happen if the directory already exists.
            // So ensure that the directory does not currently exist.
            if (!NetUtil.isDirectory(newPath))
            {
                if (oldDir.renameTo(newDir))
                {
                    return newPath;
                }
                return oldPath;
            }
        }
        return newPath;
    }

    /**
     * The cache of downloaded files inside the project directory
     */
    private static final String DIR_NETCACHE = "netcache"; //$NON-NLS-1$

    /**
     * The homes for this application: first is writable, second (if present) is read-only and specified by the system property jsword.home.
     */
    private URI[] homes;

    /**
     * The writable home for this application.
     */
    private URI writeHome;

    /**
     * The readable home for this application, specified by the system property jsword.home. Null, if jsword.home is also writable.
     */
    private URI readHome;

    /**
     * System property for home directory
     */
    private static String homeProperty = "jsword.home"; //$NON-NLS-1$

    /**
     * The JSword user settings directory
     */
    private static String homeDirectory = ".jsword"; //$NON-NLS-1$

    /**
     * The JSword user settings directory for Mac and Windows
     */
    private static String homeAltDirectory = "JSword"; //$NON-NLS-1$

    /**
     * The filesystem resources
     */
    private static CWProject instance = new CWProject();

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(CWProject.class);
}
