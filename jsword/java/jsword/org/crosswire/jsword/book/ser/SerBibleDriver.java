
package org.crosswire.jsword.book.ser;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BibleDriverManager;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.basic.AbstractBibleDriver;

/**
 * This represents all of the SerBibles.
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
 * @author Mark Goodwin [mark at thorubio dot org]
 * @version $Id$
 */
public class SerBibleDriver extends AbstractBibleDriver
{
    /**
     * Some basic driver initialization
     */
    private SerBibleDriver() throws MalformedURLException, IOException
    {
        log.debug("Starting");

        URL root = findBibleRoot();
        if (root == null)
        {
            log.warn("Cant find Bibles root, restart needed before service can resume.");
            dir = null;
            return;
        }

        dir = NetUtil.lengthenURL(root, "ser");

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
     * Some basic info about who we are
     * @param A short identifing string
     */
    public String getDriverName()
    {
        return "Serialized";
    }

    /**
     * Get a list of the Books available from the driver
     * @return an array of book names
     */
    public String[] getBibleNames()
    {
        if (dir == null)
            return new String[0];

        try
        {
            if (dir.getProtocol().equals("file"))
            {
                File fdir = new File(dir.getFile());

                // Check that the dir exists
                if (!fdir.isDirectory())
                {
                    return new String[0];
                }

                // List all the versions
                return fdir.list(new CustomFilenameFilter());
            }
            else
            {
                // We should probably cache this in some way? This is going
                // to get very slow calling this often across a network
                URL search = NetUtil.lengthenURL(dir, "list.txt");
                InputStream in = search.openStream();
                String contents = StringUtil.read(new InputStreamReader(in));
                
                String[] names = StringUtil.tokenize(contents, "\n");
                for(int i=0;i<names.length;i++)
                {
                	// we need to trim, as we may have \r\n not \n
                	names[i]=names[i].trim();
                }
                return names;
            }
        }
        catch (IOException ex)
        {
            log.warn("failed to load ser Bibles: "+ex);
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
            URL url = NetUtil.lengthenURL(dir, name);
            try{
            	if(NetUtil.isDirectory(url)) return true;
            }catch(MalformedURLException mue){
            	// For some reason isDirectory throws one...
            }
            url = NetUtil.lengthenURL(url,"bible.properties");
            try {
            	// This will throw if the resource does not exist
				InputStream is = url.openStream();
				is.close();
				return true;
			} catch (IOException e) {
			    // the resource does not exist!
				return false;
			}
        }
        catch (MalformedURLException ex)
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
            URL url = NetUtil.lengthenURL(dir, name);
            
            // now we load from JARs and other resources we can no longer just discard non dir URLs...
            /*if (!NetUtil.isDirectory(url))
                throw new BookException("ser_driver_find", new Object[] { name });*/
            if(!exists(name))
            	throw new BookException("ser_driver_find", new Object[] { name });

            return new SerBible(name, url, false);
        }
        catch (MalformedURLException ex)
        {
            throw new BookException("ser_driver_dir", ex);
        }
    }

    /**
     * Create a new blank Bible read for writing
     * @param name The name of the version to create
     * @exception BookException If the name is not valid
     */
    public Bible createBible(String name) throws BookException
    {
        try
        {
            URL url = NetUtil.lengthenURL(dir, name);

            if (NetUtil.isDirectory(url))
                throw new BookException("ser_driver_exists", new Object[] { name });

            NetUtil.makeDirectory(url);
            return new SerBible(name, url, true);
        }
        catch (MalformedURLException ex)
        {
            throw new BookException("ser_driver_dir", ex);
        }
    }

    /**
     * Rename this version
     * @param old_name The current name for the version
     * @param new_name The name we would like the driver to have
     */
    public void renameBible(String old_name, String new_name) throws BookException
    {
        try
        {
            URL old_url = NetUtil.lengthenURL(dir, old_name);
            URL new_url = NetUtil.lengthenURL(dir, new_name);

            if (!NetUtil.isDirectory(old_url))
                throw new BookException("ser_driver_find", new Object[] { old_name });

            if (NetUtil.isDirectory(new_url))
                throw new BookException("ser_driver_exists", new Object[] { new_name });

            NetUtil.move(old_url, new_url);
        }
        catch (Exception ex)
        {
            throw new BookException("ser_driver_dir", ex);
        }
    }

    /**
     * Delete the set of files that make up this version.
     * @param name The name of the version to delete
     */
    public void deleteBible(String name) throws BookException
    {
        try
        {
            StringBuffer failures = new StringBuffer();
            URL home = NetUtil.lengthenURL(dir, name);

            if (!NetUtil.isDirectory(home))
                throw new BookException("ser_driver_find", new Object[] { name });

            deleteFileURL(home, "ref.idx", failures);
            deleteFileURL(home, "xml.idx", failures);
            deleteFileURL(home, "ref.dat", failures);
            deleteFileURL(home, "xml.dat", failures);
            deleteFileURL(home, "bible.properties", failures);
            deleteFileURL(home, "generate.log", failures);

            if (failures.length() != 0)
            {
                throw new BookException("ser_driver_delfile", new Object[] { failures, home });
            }
            else
            {
                if (!NetUtil.delete(home))
                    throw new BookException("ser_driver_deldir", new Object[] { home });
            }
        }
        catch (Exception ex)
        {
            throw new BookException("ser_driver_dir", ex);
        }
    }

    /**
     * Convenience file delete and check routine.
     * @param name The name of the file to delete
     * @param errors The place to store an accumulated error message
     */
    private void deleteFileURL(URL home, String name, StringBuffer errors) throws IOException
    {
        URL url = NetUtil.lengthenURL(home, name);

        if (!NetUtil.isFile(url))
            return;

        if (!NetUtil.delete(url))
        {
            if (errors.length() != 0)
                errors.append(", ");

            errors.append(name);
        }
    }

    /** The directory URL */
    private URL dir;

    /** The singleton driver */
    protected static SerBibleDriver driver;

    /** The log stream */
    protected static Logger log = Logger.getLogger(SerBibleDriver.class);

    /**
     * Register ourselves with the Driver Manager
     */
    static
    {
        try
        {
            driver = new SerBibleDriver();
            BibleDriverManager.registerDriver(driver);
        }
        catch (Exception ex)
        {
            Reporter.informUser(SerBibleDriver.class, ex);
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
                Reporter.informUser(SerBibleDriver.class, ex);
                return false;
            }
        }
    }
}
