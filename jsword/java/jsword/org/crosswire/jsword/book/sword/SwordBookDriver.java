
package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.jsword.book.BibleMetaData;
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
 * @version $Id$
 */
public class SwordBookDriver extends AbstractBookDriver
{
    /**
     * Some basic name initialization
     */
    public SwordBookDriver() throws MalformedURLException
    {
        log.debug("Starting Sword drivers");
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookDriver#getBooks()
     */
    public BookMetaData[] getBooks()
    {
        if (dir == null)
        {
            log.debug("getBooks() empty because dir == null");
            return new BibleMetaData[0];
        }

        try
        {
            // load each config withing mods.d, discard those which are not bibles, return names of remaining
            URL mods = NetUtil.lengthenURL(dir, "mods.d");
            if (!NetUtil.isDirectory(mods))
            {
                log.debug("getBooks() empty mods.d does not exist");
                return new BibleMetaData[0];
            }

            return createBookMetaDataArray(mods);
        }
        catch (MalformedURLException ex)
        {
            log.warn("Failed to get mods.d directory", ex);
            return new BibleMetaData[0];
        }
    }

    /**
     * Create a list of BookMetaDatas at the given URL
     * @param mods
     * @return BookMetaData[]
     * @throws IOException
     */
    public BookMetaData[] createBookMetaDataArray(URL mods)
    {
        File modsdir = new File(mods.getFile());
        String[] bookdirs = modsdir.list(new SwordBookDriver.CustomFilenameFilter());
        List valid = new ArrayList();
        
        for (int i=0; i<bookdirs.length; i++)
        {
            try
            {
                SwordConfig config = new SwordConfig(this, modsdir, bookdirs[i]);
                SwordBookMetaData bmd = config.getMetaData();
                valid.add(bmd);
            }
            catch (Exception ex)
            {
                log.warn("Couldn't create SwordBookMetaData", ex);
            }
        }
        
        return (BookMetaData[]) valid.toArray(new BookMetaData[valid.size()]);
    }

    /**
     * Accessor for the Sword directory
     * @param sword_dir The new Sword directory
     */
    public static void setSwordDir(String sword_dir) throws MalformedURLException, BookException
    {
        // Fist we need to unregister any registered books from ourselves
        BookDriver[] matches = Books.getDriversByClass(SwordBookDriver.class);
        for (int i=0; i<matches.length; i++)
        {
            Books.unregisterDriver(matches[i]);
        }

        // If the new dir is empty then just accept that we're not supposed to work ...
        if (sword_dir == null || sword_dir.trim().length() == 0)
        {
            dir = null;
            log.info("No sword dir set.");
            return;
        }

        URL dir_temp = new URL("file", null, sword_dir);

        if (!NetUtil.isDirectory(dir_temp))
        {
            throw new MalformedURLException("No sword source found under " + sword_dir);
        }

        dir = dir_temp;

        // Now we need to register ourselves
        Books.registerDriver(new SwordBookDriver());
    }

    /**
     * Accessor for the Sword directory
     * @return The new Sword directory
     */
    public static String getSwordDir()
    {
        if (dir == null)
        {
            return "";
        }

        return dir.getFile();
    }

    /**
     * Accessor for the SWORD project installation directory
     */
    public static URL getSwordURL()
    {
        return dir;
    }

    /**
     * The directory URL
     */
    private static URL dir;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(SwordBookDriver.class);

    /**
     * Check that the directories in the version directory really
     * represent versions.
     */
    static class CustomFilenameFilter implements FilenameFilter
    {
        public boolean accept(File parent, String name)
        {
            return name.endsWith(".conf") && !name.startsWith("globals.");
        }
    }
}
