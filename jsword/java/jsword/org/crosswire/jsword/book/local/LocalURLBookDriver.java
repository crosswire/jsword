
package org.crosswire.jsword.book.local;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.util.ResourceUtil;
import org.crosswire.common.util.StringUtil;
import org.crosswire.common.util.URLFilter;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BibleMetaData;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.basic.AbstractBookDriver;

/**
 * LocalURLBookDriver is a helper for drivers that want to store files locally.
 * 
 * It takes care of providing you with a directory to work from and managing the
 * files stored in that directory.
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
public abstract class LocalURLBookDriver extends AbstractBookDriver
{
    /**
     * The ctor checks on the filesystem
     */
    protected LocalURLBookDriver(String name, String subdir, Class bibleclass, int speed) throws MalformedURLException, IOException
    {
        log.debug("Starting "+name+" in "+subdir);

        this.speed = speed;
        this.name = name;
        this.bibleclass = bibleclass;

        findBibleRoot();
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
                log.debug("The directory '"+dir+"' does not exist. cwd="+new File(".").getCanonicalPath());
            }
        }
        else
        {
            URL search = NetUtil.lengthenURL(dir, "list.txt");
            InputStream in = search.openStream();
            if (in == null)
            {
                log.debug("The remote listing file at '"+search+"' does not exist.");
            }
        }       
    }

    /**
     * The expected speed at which this implementation gets correct answers.
     * @see org.crosswire.jsword.book.BookMetaData#getSpeed()
     */
    public int getSpeed()
    {
        return speed;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#delete()
     */
    public void delete(BookMetaData bmd) throws BookException
    {
        if (!(bmd instanceof LocalURLBibleMetaData))
        {
            throw new BookException(Msg.DELETE_FAIL, new Object[] { bmd.getName()});
        }
        
        LocalURLBibleMetaData lbmd = (LocalURLBibleMetaData) bmd;
        try
        {
            if (!NetUtil.delete(lbmd.getURL()))
            {
                throw new BookException(Msg.DELETE_FAIL, new Object[] { bmd.getName() });
            }
        }
        catch (IOException ex)
        {
            throw new BookException(Msg.DELETE_FAIL, ex, new Object[] { bmd.getName() });
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookDriver#create(org.crosswire.jsword.book.Book, org.crosswire.jsword.book.WorkListener)
     */
    public Book create(Book book) throws BookException
    {
        if (!(book instanceof Bible))
        {
            throw new BookException(Msg.CREATE_NOBIBLE);
        }

        Bible source = (Bible) book;
        BibleMetaData basis = source.getBibleMetaData();

        try
        {
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

            LocalURLBible dest = (LocalURLBible) bibleclass.newInstance();
            dest.setLocalURLBibleMetaData(bbmd);
            dest.generateText(source);

            return dest;
        }
        catch (Exception ex)
        {
            throw new BookException(Msg.CREATE_FAIL, ex);
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

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookDriver#getBooks()
     */
    public BookMetaData[] getBooks()
    {
        if (dir == null)
        {
            return new BibleMetaData[0];
        }

        try
        {
            String[] names = NetUtil.list(dir, new CustomURLFilter(dir));
            if (names == null)
            {
                return new BibleMetaData[0];
            }

            BibleMetaData[] versions = new BibleMetaData[names.length]; 

            for (int i=0; i<names.length; i++)
            {
                URL url = NetUtil.lengthenURL(dir, names[i]);
                URL prop_url = NetUtil.lengthenURL(url, "bible.properties");

                Properties prop = new Properties();
                prop.load(prop_url.openStream());

                versions[i] = new LocalURLBibleMetaData(this, url, prop);
            }

            // List all the versions
            return versions;
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);

            log.warn("failed to load "+name+" Bibles because source directory is not present: "+dir.toExternalForm());
            return new BibleMetaData[0];
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
            log.debug("Testing system property jsword.bible.dir="+sysprop);

            if (sysprop != null)
            {
                URL found = NetUtil.lengthenURL(new URL("file", null, sysprop), "versions");
                URL test = NetUtil.lengthenURL(found, "locator.properties");

                if (NetUtil.isFile(test))
                {
                    log.debug("Found BibleRoot using system property jsword.bible.dir at "+test);
                    root = found;
                }
                else
                {
                    log.warn("Missing jsword.bible.dir under: "+test.toExternalForm());
                }
            }
        }

        // If not then try a wild guess
        if (root == null)
        {
            URL found = ResourceUtil.getResource("versions/locator.properties");
            URL test = NetUtil.shortenURL(found, "locator.properties");
            if (NetUtil.isFile(test))
            {
                log.debug("Found BibleRoot from current directory: "+test.toExternalForm());
                root = test;
            }
            else
            {
                log.warn("Missing BibleRoot from current directory: "+test.toExternalForm());
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

    /**
     * The speed config option
     */
    private int speed;

    /**
     * The type of Bible we are to create
     */
    protected Class bibleclass;

    /**
     * The diriver name
     */
    private String name;

    /**
     * The directory URL
     */
    private URL dir;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(LocalURLBookDriver.class);

    /**
     * The Bibles root
     */
    private static URL root;

    /**
     * Check that the directories in the version directory really
     * represent versions.
     */
    static class CustomURLFilter implements URLFilter
    {
        public CustomURLFilter(URL parent)
        {
            this.parent = parent;
        }

        public boolean accept(String name)
        {
            try
            {
                return NetUtil.isDirectory(NetUtil.lengthenURL(parent, name));
            }
            catch (IOException ex)
            {
                Reporter.informUser(LocalURLBookDriver.class, ex);
                return false;
            }
        }

        private URL parent;
    }
}
