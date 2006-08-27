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
package org.crosswire.jsword.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.crosswire.common.util.CWClassLoader;
import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;

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
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public final class Project
{
    /**
     * The cache of downloaded files inside the project directory
     */
    public static final String DIR_NETCACHE = "netcache"; //$NON-NLS-1$

    /**
     * The JSword user settings directory
     */
    public static final String DIR_PROJECT = ".jsword"; //$NON-NLS-1$

    /**
     * The JSword user settings directory for Mac and Windows
     */
    public static final String DIR_PROJECT_ALT = "JSword"; //$NON-NLS-1$

    /**
     * The Windows user settings parent directory
     */
    public static final String WIN32_USER_DATA_AREA = "Application Data"; //$NON-NLS-1$

    /**
     * The Mac user settings parent directory
     */
    public static final String MAC_USER_DATA_AREA = "Library/Application Support"; //$NON-NLS-1$

    /**
     * Accessor for the resource singleton.
     */
    public static Project instance()
    {
        return instance;
    }

    /**
     * Prevent Instansiation.
     * <p>The biggest job is trying to work out which resource bundle to
     * load to work out where the config and data files are stored.
     * We construct a name from the projectname, hostname and any other
     * info and then try to use that.
     */
    private Project()
    {
        CWClassLoader.setHome(getUserProjectDir());

        try
        {
            URL urlcache = getTempScratchSpace(DIR_NETCACHE, true);
            File filecache = new File(urlcache.getFile());
            NetUtil.setURLCacheDir(filecache);
        }
        catch (IOException ex)
        {
            // This isn't fatal, it just means that NetUtil will try to use $TMP
            // in place of a more permanent solution.
            log.warn("Failed to get directory for NetUtil.setURLCacheDir()", ex); //$NON-NLS-1$
        }
    }

    /**
     * Establishes the user's project directory.
     * @throws MalformedURLException
     */
    public URL getUserProjectDir(String unixDefault, String winMacDefault)
    {
        String projectDir = winMacDefault;
        URL path = null;
        try
        {
            if (userArea == null)
            {
                String user = System.getProperty("user.home"); //$NON-NLS-1$
                String osName = System.getProperty("os.name"); //$NON-NLS-1$

                path = new URL(NetUtil.PROTOCOL_FILE, null, user);

                if (osName.startsWith("Mac OS X")) //$NON-NLS-1$
                {
                    path = NetUtil.lengthenURL(path, MAC_USER_DATA_AREA);
                }
                else if (osName.startsWith("Windows")) //$NON-NLS-1$
                {
                    path = NetUtil.lengthenURL(path, WIN32_USER_DATA_AREA);
                }
                else
                {
                    projectDir = unixDefault;
                }
                userArea = path;
            }
            path = NetUtil.lengthenURL(userArea, projectDir);
        }
        catch (MalformedURLException ex)
        {
            log.fatal("Failed to find user's private data area", ex); //$NON-NLS-1$
            assert false : ex;
        }

        return path;
    }

    /**
     * Establishes the user's project directory.
     * @throws MalformedURLException
     */
    public URL getUserProjectDir()
    {
        if (home == null)
        {
            URL path = getUserProjectDir(DIR_PROJECT, DIR_PROJECT_ALT);
            URL oldPath = getDeprecatedUserProjectDir();
            home = migrateUserProjectDir(oldPath, path);
        }

        return home;
    }

    /**
     * Get the location where the project dir used to be.
     * 
     * @return ~/.jsword
     * @throws MalformedURLException
     */
    public URL getDeprecatedUserProjectDir()
    {
        try
        {
            String user = System.getProperty("user.home"); //$NON-NLS-1$

            URL path = new URL(NetUtil.PROTOCOL_FILE, null, user);
            path = NetUtil.lengthenURL(path, DIR_PROJECT);

            return path;
        }
        catch (MalformedURLException ex)
        {
            log.fatal("Failed to create home directory URL", ex); //$NON-NLS-1$
            assert false : ex;
        }
        return null;
    }

    /**
     * Migrates the user's project dir, if necessary and possible.
     * 
     * @param oldPath the path to the old, deprecated location
     * @param newPath the path to the new location
     * @return newPath if the migration was possible or not needed.
     */
    private URL migrateUserProjectDir(URL oldPath, URL newPath)
    {
        if (oldPath.equals(newPath))
        {
            return newPath;
        }

        if (NetUtil.isDirectory(oldPath))
        {
            File oldDir = new File(oldPath.getFile());
            File newDir = new File(newPath.getFile());
            if (oldDir.renameTo(newDir))
            {
                return newPath;
            }
            return oldPath;
        }
        return newPath;
    }

    /**
     * Get a the URL of a (potentially non-existant) properties file that we can
     * write to. This method of aquiring properties files is preferred over
     * getResourceProperties() as this is writable and can take into account
     * user preferences.
     * This method makes no promise that the URL returned is valid. It is
     * totally untested, so reading may well cause errors.
     * @param subject The name (minus the .properties extension)
     * @return The resource as a URL
     */
    public URL getWritablePropertiesURL(String subject)
    {
        return NetUtil.lengthenURL(getUserProjectDir(), subject + FileUtil.EXTENSION_PROPERTIES);
    }

    /**
     * When we need a directory to write stuff to.
     * <p>This directory should be used as a cache for something that could also
     * be got at runtime by some other means.
     * <p>So it is not for config data, and not for program files. If someone
     * were to delete all the files in this directory while you weren't looking
     * then life should not stop, but should carry on albeit with a slower
     * service.
     * <p>This method may well return null if we are running in a restricted
     * environment, so callers of this method should be prepared for that
     * eventuallity too.
     * <p>As a result of these limitations it could be OK to use {@link File} in
     * place of {@link URL} (which is the norm for this project), however there
     * doesn't seem to be a good reason to relax this rule here.
     * @param subject A moniker for the area to write to. This will be converted into a directory name.
     * @return A file: URL pointing at a local writable directory.
     */
    public URL getTempScratchSpace(String subject, boolean create) throws IOException
    {
        URL temp = NetUtil.lengthenURL(getUserProjectDir(), subject);

        if (create && !NetUtil.isDirectory(temp))
        {
            NetUtil.makeDirectory(temp);
        }

        return temp;
    }

    /**
     * The parent directory for the home of this application
     */
    private URL userArea;

    /**
     * The home for this application
     */
    private URL home;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(Project.class);

    /**
     * The filesystem resources
     */
    private static Project instance = new Project();
}
