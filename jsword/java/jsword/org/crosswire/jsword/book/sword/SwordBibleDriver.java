
package org.crosswire.jsword.book.sword;

import java.io.FilenameFilter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.util.Level;
import org.crosswire.common.util.ArrayEnumeration;
import org.crosswire.jsword.util.Project;
import org.crosswire.jsword.book.basic.AbstractBibleDriver;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BibleDriverManager;

/**
 * This represents all of the SwordBibles.
 *
 * <table border='1' cellPadding='3' cellSpacing='0' width="100%">
 * <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
 * Distribution Licence:<br />
 * Project B is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
 * <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
 * @see <{docs.Licence}>
 * @author Joe Walker
 */
public class SwordBibleDriver extends AbstractBibleDriver
{
    /**
     * Some basic driver initialization
     */
    private SwordBibleDriver() throws MalformedURLException
    {
    }

    /**
     * Some basic info about who we are
     * @param A short identifing string
     */
    public String getDriverName()
    {
        return "Sword";
    }

    /**
     * Get a list of the Books available from the driver
     * @return an array of book names
     */
    public String[] getBibleNames()
    {
        try
        {
            if (!nested.getProtocol().equals("file"))
                throw new BookException("sword_file_only", new Object[] { dir.getProtocol() });

            File fdir = new File(nested.getFile());

            // Check that the dir exists
            if (!fdir.isDirectory())
            {
                log.fine("The directory '"+dir+"' does not exist.");
                return new String[0];
            }

            // List all the versions
            return fdir.list(new CustomFilenameFilter());
        }
        catch (Exception ex)
        {
            return new String[0];
        }
    }

    /**
     * Does the named Bible exist?
     * @param name The name of the version to test for
     * @return true if the Bible exists
     */
    public boolean exists(String name)
    {
        try
        {
            URL url = NetUtil.lengthenURL(nested, name);
            return NetUtil.isDirectory(url);
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    /**
     * Featch a currently existing Bible, read-only
     * @param name The name of the version to create
     * @exception BookException If the name is not valid
     */
    public Bible getBible(String name) throws BookException
    {
        try
        {
            URL url = NetUtil.lengthenURL(nested, name);

            if (!NetUtil.isDirectory(url))
                throw new BookException("sword_driver_find", new Object[] { name });

            return new SwordBible(name, url);
        }
        catch (MalformedURLException ex)
        {
            throw new BookException("sword_driver_dir", ex);
        }
    }

    /**
     * Create a new blank Bible read for writing
     * @param name The name of the version to create
     * @exception BookException If the name is not valid
     */
    public Bible createBible(String name) throws BookException
    {
        throw new BookException("sword_driver_readonly");
    }

    /**
     * Rename this version
     * @param old_name The current name for the version
     * @param new_name The name we would like the driver to have
     */
    public void renameBible(String old_name, String new_name) throws BookException
    {
        throw new BookException("sword_driver_readonly");
    }

    /**
     * Delete the set of files that make up this version.
     * @param name The name of the version to delete
     */
    public void deleteBible(String name) throws BookException
    {
        throw new BookException("sword_driver_readonly");
    }

    /**
     * Accessor for the Sword directory
     * @param sword_dir The new Sword directory
     */
    public static void setSwordDir(String sword_dir) throws MalformedURLException
    {
        URL dir_temp = new URL("file:"+sword_dir);
        URL nest_temp = NetUtil.lengthenURL(dir_temp, "modules", "texts", "rawtext");

        if (!NetUtil.isDirectory(nest_temp))
            throw new MalformedURLException("No sword source found under "+sword_dir);

        driver.dir = dir_temp;
        driver.nested = nest_temp;
    }

    /**
     * Accessor for the Sword directory
     * @return The new Sword directory
     */
    public static String getSwordDir()
    {
        if (driver.dir == null)
            return "";

        return driver.dir.toExternalForm().substring(5);
    }

    /** The directory URL */
    private URL dir;

    /** The directory URL */
    private URL nested;

    /** The singleton driver */
    protected static SwordBibleDriver driver;

    /** The log stream */
    protected static Logger log = Logger.getLogger("bible.book");

    /**
     * Register ourselves with the Driver Manager
     */
    static
    {
        try
        {
            driver = new SwordBibleDriver();
            BibleDriverManager.registerDriver(driver);
        }
        catch (MalformedURLException ex)
        {
            Reporter.informUser(SwordBibleDriver.class, ex);
        }
    }

    /**
     * Check that the directories in the version directory really
     * represent versions.
     */
    static class CustomFilenameFilter implements FilenameFilter
    {
        public boolean accept(File parent, String name)
        {
            try
            {
                return new File(parent.getCanonicalPath() + File.separator + name).isDirectory();
            }
            catch (IOException ex)
            {
                Reporter.informUser(SwordBibleDriver.class, ex);
                return false;
            }
        }
    }
}
