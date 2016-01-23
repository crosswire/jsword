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
 * © CrossWire Bible Society, 2008 - 2016
 *
 */
package org.crosswire.common.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Project class looks after the source of project files. These are per user
 * files and as such have a different location on different operating systems.
 * These are:<br>
 * 
 * <table>
 * <tr>
 * <td>Mac OS X</td>
 * <td>~/Library/Application Support/JSword</td>
 * </tr>
 * <tr>
 * <td>Win NT/2000/XP/ME/9x</td>
 * <td>~/Application Data/JSword (~ is all over the place, but Java figures it
 * out)</td>
 * </tr>
 * <tr>
 * <td>Unix and otherwise</td>
 * <td>~/.jsword</td>
 * </tr>
 * </table>
 * 
 * <p>
 * Previously the location was ~/.jsword, which is unfriendly in the Windows and
 * Mac world. If this location is found on Mac or Windows, it will be moved to
 * the new location, if different and possible.
 * </p>
 * 
 * <p>
 * Note: If the Java System property jsword.home is set and it exists and is
 * writable then it will be used instead of the above location. This is useful
 * for USB Drives and other portable implementations of JSword. It is
 * recommended that this name be JSword.
 * </p>
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public final class CWProject {
    /**
     * Accessor for the resource singleton.
     * 
     * @return the singleton
     */
    public static CWProject instance() {
        return instance;
    }

    /**
     * Required for reset of statics during testing
     */
    @SuppressWarnings("unused")
    private static void reset() {
        instance = new CWProject();
    }

    /**
     * Establish how this project finds it's resources.
     * 
     * @param homeProperty
     *            a property that is used as the home directory name. If this
     *            property has a value then homeDir and altHomeDir are ignored.
     *            Defaults to jsword.home.
     * @param homeDir
     *            the name of the directory to be used for Unix. Typically this
     *            is a hidden directory, that is, it begins with a '.'. Defaults
     *            to .jsword
     * @param altHomeDir
     *            the name of the directory to be used for other OSes. This
     *            should not be a hidden directory. Defaults to JSword.
     */
    public static void setHome(String homeProperty, String homeDir, String altHomeDir) {
        CWProject.homeProperty = homeProperty;
        CWProject.homeDirectory = homeDir;
        CWProject.homeAltDirectory = altHomeDir;
        instance().establishProjectHome();
    }

    /**
     * Sets the name of the front-end. This then informs the read/write directory of the front-end.
     * @param frontendName
     */
    public void setFrontendName(String frontendName) {
        this.frontendName = frontendName;
    }

    /**
     * @return the writable home for the front-end settings, that can be kept separate from JSword's
     * configuration, as well from other front-ends.
     */
    public URI getWritableFrontendProjectDir() {
        establishProjectHome();
        return this.writableFrontEndHome;
    }

    /**
     * @return the readable home for the front-end settings, that can be kept separate from JSword's
     * configuration, as well from other front-ends.
     */
    public URI getReadableFrontendProjectDir() {
        establishProjectHome();
        return this.frontendReadHome;
    }


    /**
     * Get the writable user project directory.
     * 
     * @return the writable user project directory.
     */
    public URI getWritableProjectDir() {
        establishProjectHome();
        return writeHome;
    }

    /**
     * Get the locations where project resources can be found.
     * 
     * @return an array of URIs which can be used to look up resources.
     */
    public URI[] getProjectResourceDirs() {
        establishProjectHome();
        return homes.clone();
    }

    /**
     * Get the location where the project directory used to be.
     * 
     * @return ~/.jsword
     */
    public URI getDeprecatedWritableProjectDir() {
        return OSType.DEFAULT.getUserAreaFolder(homeDirectory, homeAltDirectory);
    }

    /**
     * Create a the URI for a (potentially non-existent) file to which we can
     * write. Typically this is used to store user preferences and application
     * overrides. This method of acquiring files is preferred over
     * getResourceProperties() as this is writable and can take into account
     * user preferences. This method makes no promise that the URI returned is
     * valid. It is totally untested, so reading may well cause errors.
     * 
     * @param subject
     *            The name (minus the .xxx extension)
     * @param extension
     *            The extension, prefixed with a '.' See: {@link FileUtil} for a
     *            list of popular extensions.
     * @return The resource as a URI
     */
    public URI getWritableURI(String subject, String extension) {
        return NetUtil.lengthenURI(getWritableProjectDir(), subject + extension);
    }

    /**
     * A directory within the project directory.
     * 
     * @param subject
     *            A name for the subdirectory of the Project directory.
     * @param create whether to create the directory if it does not exist
     * @return A file: URI pointing at a local writable directory.
     * @throws IOException 
     */
    public URI getWritableProjectSubdir(String subject, boolean create) throws IOException {
        URI temp = NetUtil.lengthenURI(getWritableProjectDir(), subject);

        if (create && !NetUtil.isDirectory(temp)) {
            NetUtil.makeDirectory(temp);
        }

        return temp;
    }

    /**
     * Prevent instantiation.
     */
    private CWProject() {
    }

    /**
     * Establishes the user's project directory. In a CD installation, the home
     * directory on the CD will be read-only. This is not sufficient. We also
     * need a writable home directory. And in looking up resources, the ones in
     * the writable directory trump those in the readable directory, allowing
     * the read-only resources to be overridden.
     * <p>
     * Here is the lookup order:
     * <ol>
     * <li>Check first to see if the jsword.home property is set.</li>
     * <li>Check for the existence of a platform specific project area and for
     * the existence of a deprecated project area (~/.jsword on Windows and Mac)
     * and if it exists and it is possible "upgrade" to the platform specific
     * project area. Of these "two" only one is the folder to check.</li>
     * </ol>
     * In checking these areas, if the one is read-only, add it to the list and
     * keep going. However, if it is also writable, then use it alone.
     */
    private void establishProjectHome() {
        if (writeHome == null && readHome == null) {
            // if there is a property set for the jsword home directory
            String cwHome = System.getProperty(homeProperty);
            if (cwHome != null) {
                URI home = NetUtil.getURI(new File(cwHome));
                if (NetUtil.canWrite(home)) {
                    writeHome = home;
                } else if (NetUtil.canRead(home)) {
                    readHome = home;
                }
                // otherwise jsword.home is not usable.
            }
        }

        if (writeHome == null) {
            URI path = OSType.getOSType().getUserAreaFolder(homeDirectory, homeAltDirectory);
            URI oldPath = getDeprecatedWritableProjectDir();
            writeHome = migrateUserProjectDir(oldPath, path);
        }

        if (homes == null) {
            if (readHome == null) {
                homes = new URI[] {
                    writeHome
                };
            } else {
                homes = new URI[] {
                        writeHome, readHome
                };
            }

            // Now that we know the "home" we can set other global notions of home.
            // TODO(dms): refactor this to CWClassLoader and NetUtil.
            CWClassLoader.setHome(getProjectResourceDirs());

            try {
                URI uricache = getWritableProjectSubdir(DIR_NETCACHE, true);
                File filecache = new File(uricache.getPath());
                NetUtil.setURICacheDir(filecache);
            } catch (IOException ex) {
                // This isn't fatal, it just means that NetUtil will try to use $TMP
                // in place of a more permanent solution.
                LOGGER.warn("Failed to get directory for NetUtil.setURICacheDir()", ex);
            }

            //also attempt to create the front-end home
            try {
                if (this.frontendName != null) {
                    this.writableFrontEndHome = getWritableProjectSubdir(this.frontendName, true);
                }
            } catch (IOException ex) {
                LOGGER.warn("Failed to create writable front-end home.", ex);
            }

            //attempt to set front-end readable home, if different
            if (readHome != null && this.frontendName != null) {
                this.frontendReadHome = NetUtil.lengthenURI(this.readHome, this.frontendName);
            }
        }
    }

    /**
     * Migrates the user's project dir, if necessary and possible.
     * 
     * @param oldPath
     *            the path to the old, deprecated location
     * @param newPath
     *            the path to the new location
     * @return newPath if the migration was possible or not needed.
     */
    private URI migrateUserProjectDir(URI oldPath, URI newPath) {
        if (oldPath.toString().equals(newPath.toString())) {
            return newPath;
        }

        if (NetUtil.isDirectory(oldPath)) {
            File oldDir = new File(oldPath.getPath());
            File newDir = new File(newPath.getPath());

            // renameTo will return false if it could not rename.
            // This will happen if the directory already exists.
            // So ensure that the directory does not currently exist.
            if (!NetUtil.isDirectory(newPath)) {
                if (oldDir.renameTo(newDir)) {
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
    private static final String DIR_NETCACHE = "netcache";

    /**
     * The homes for this application: first is writable, second (if present) is
     * read-only and specified by the system property jsword.home.
     */
    private URI[] homes;

    /**
     * The writable home for this application.
     */
    private URI writeHome;

    /**
     * The name of the front-end application. This allows front-ends to store information
     * under the jsword directory, separate from other front-ends
     */
    private String frontendName;

    /**
     * The readable home for this application, specified by the system property
     * jsword.home. Null, if jsword.home is also writable.
     */
    private URI readHome;

    /**
     * Front-end home, where the app can write information to it. Could be null if failed to create
     */
    private URI writableFrontEndHome;

    /**
     * Front-end read home, could be null if not present
     */
    private URI frontendReadHome;

    /**
     * System property for home directory
     */
    private static String homeProperty = "jsword.home";

    /**
     * The JSword user settings directory
     */
    private static String homeDirectory = ".jsword";

    /**
     * The JSword user settings directory for Mac and Windows
     */
    private static String homeAltDirectory = "JSword";

    /**
     * The filesystem resources
     */
    private static CWProject instance = new CWProject();

    /**
     * The log stream
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CWProject.class);
}
