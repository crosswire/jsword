
package org.crosswire.jsword.book.basic;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.PropertiesUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.util.StringUtil;
import org.crosswire.common.util.URLFilter;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BibleMetaData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.events.ProgressListener;
import org.crosswire.jsword.util.Project;

/**
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public abstract class LocalURLBibleDriver extends SearchableBibleDriver
{
    /**
     * The expected speed at which this implementation gets correct answers.
     * @see org.crosswire.jsword.book.BookMetaData#getSpeed()
     */
    public abstract int getSpeed();

    /**
     * A Bible to read data from
     */
    public abstract Bible getBible(LocalURLBibleMetaData version) throws BookException;

    /**
     * A new place to write Bible data
     */
    public abstract Bible createBible(LocalURLBibleMetaData bbmd, Bible source, ProgressListener li) throws BookException;

    /**
     * The ctor checks on the filesystem
     * @param subdir
     * @throws MalformedURLException
     * @throws IOException
     */
    protected LocalURLBibleDriver(String name, String subdir) throws MalformedURLException, IOException
    {
        log.debug("Starting "+name+" in "+subdir);

        this.name = name;
        URL root = findBibleRoot();
        if (root == null)
        {
            log.warn("Cant find Bibles root, restart needed before service can resume.");
            dir = null;
            return;
        }

        dir = NetUtil.lengthenURL(root, subdir);

        // To save giving off hundreds of warnings later we'll do a check on the
        // setup and available Bibles now ...
        if (dir.getProtocol().equals("file"))
        {
            File fdir = new File(dir.getFile());
            if (!fdir.isDirectory())
            {
                log.debug("The directory '"+dir+"' does not exist.");
            }
        }
        else
        {
            URL search = NetUtil.lengthenURL(dir, "list.txt");
            InputStream in = search.openStream();
            if (in == null)
                log.debug("The remote listing file at '"+search+"' does not exist.");
        }       
    }

    /**
     * A simple name description name.
     * @return A short identifing string
     */
    public String getDriverName()
    {
        return name;
    }

    /**
     * @see org.crosswire.jsword.book.basic.AbstractBibleDriver#getBibles()
     */
    public BibleMetaData[] getBibles()
    {
        if (dir == null)
            return new BibleMetaData[0];

        try
        {
            String[] names = NetUtil.list(dir, new CustomURLFilter());
            if (names == null)
                return new BibleMetaData[0];

            BibleMetaData[] versions = new BibleMetaData[names.length]; 

            for (int i=0; i<names.length; i++)
            {
                URL url = NetUtil.lengthenURL(dir, names[i]);
                URL prop_url = NetUtil.lengthenURL(url, "bible.properties");
                InputStream prop_in = prop_url.openStream();
                Properties prop = new Properties();
                PropertiesUtil.load(prop, prop_in);
                versions[i] = new LocalURLBibleMetaData(this, url, prop);
            }

            // List all the versions
            return versions;
        }
        catch (Exception ex)
        {
            log.warn("failed to load ser Bibles: ", ex);
            return new BibleMetaData[0];
        }
    }

    /**
     * Create a new blank Bible read for writing
     * @param name The name of the version to create
     * @exception BookException If the name is not valid
     */
    public Bible create(Bible source, ProgressListener li) throws BookException
    {
        try
        {
            BibleMetaData basis = source.getBibleMetaData();

            String base = source.getBibleMetaData().getFullName();
            base = StringUtil.createJavaName(base);
            base = StringUtil.shorten(base, 10);

            URL url = NetUtil.lengthenURL(dir, base);
            if (NetUtil.isDirectory(url) || NetUtil.isFile(url))
            {
                int count = 1;
                while (true)
                {
                    url = NetUtil.lengthenURL(dir, base + count);
                    if (!NetUtil.isDirectory(url))
                        break;

                    count++;
                }
            }

            NetUtil.makeDirectory(url);

            LocalURLBibleMetaData bbmd = new LocalURLBibleMetaData(this, url, basis);
            return createBible(bbmd, source, li);
        }
        catch (MalformedURLException ex)
        {
            throw new BookException("raw_driver_dir", ex);
        }
    }

    /**
     * Search for versions directories
     */
    protected static synchronized URL findBibleRoot() throws MalformedURLException
    {
        // First see if there is a System property that can help us out
        if (root == null)
        {
            String sysprop = System.getProperty("jsword.bible.dir");
            if (sysprop != null)
            {
                URL found = NetUtil.lengthenURL(new URL("file", null, sysprop), "versions");
                URL test = NetUtil.lengthenURL(found, "locator.properties");
                if (NetUtil.isFile(test))
                {
                    log.debug("Found BibleRoot from system property jsword.bible.dir at "+sysprop+"");
                    root = found;
                }
            }

            // If not then try a wild guess
            if (root == null)
            {
                URL found = Project.resource().getResource("/versions/locator.properties");
                if (found == null)
                    throw new MalformedURLException("Missing locator.properties.");

                log.debug("Found BibleRoot by guessing");

                root = NetUtil.shortenURL(found, "/locator.properties");
            }
        }

        return root;
    }

    /**
     * Accessor for the local directory
     */
    protected URL getBasedir()
    {
        return dir;
    }

    /** The diriver name */
    private String name;

    /** The directory URL */
    private URL dir;

    /** The log stream */
    protected static Logger log = Logger.getLogger(LocalURLBibleDriver.class);

    /** The Bibles root */
    private static URL root;

    /**
     * Check that the directories in the version directory really
     * represent versions.
     */
    static class CustomURLFilter implements URLFilter
    {
        public boolean accept(URL parent, String name)
        {
            try
            {
                return NetUtil.isDirectory(NetUtil.lengthenURL(parent, name));
            }
            catch (IOException ex)
            {
                Reporter.informUser(LocalURLBibleDriver.class, ex);
                return false;
            }
        }
    }
}
