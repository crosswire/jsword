
package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BibleDriverManager;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.basic.AbstractBibleDriver;

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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
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
    	if(dir==null)
    	{ 
    		return new String[0];
    	}
    	try
    	{
	    	// load each config withing mods.d, discard those which are not bibles, return names of remaining
	    	URL mods = NetUtil.lengthenURL(dir,"mods.d");
	    	if(!NetUtil.isDirectory(mods)) return new String[0];
	    	File modsFile = new File(mods.getFile());
	    	String[] filenames = modsFile.list(new CustomFilenameFilter());
	    	SwordConfig[] configs = new SwordConfig[filenames.length];
	    	ArrayList validNames = new ArrayList();
	    	for(int i=0;i<filenames.length;i++)
	    	{
	    		String biblename=filenames[i].substring(0,filenames[i].indexOf(".conf"));
	    		configs[i] = new SwordConfig(new File(modsFile,filenames[i]).toURL(),biblename);
	    		// check to se if it's a bible
	    		if(isBible(configs[i]))
	    		{
	    			validNames.add(biblename);
	    			configCache.put(biblename,configs[i]);
	    		}
	    	}
	    	String[] retVal = (String[]) validNames.toArray(new String[0]);
	    	return retVal;
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
        return new String[0];
    }

	/**
	 * Method isBible.
	 * @param swordConfig
	 * @return boolean
	 */
	private boolean isBible(SwordConfig swordConfig) 
	{
		if(swordConfig.getModDrv()==SwordConstants.DRIVER_RAW_TEXT) return true;
		if(swordConfig.getModDrv()==SwordConstants.DRIVER_Z_TEXT) return true;
		return false;
	}


    /**
     * Does the named Bible exist?
     * @param name The name of the version to test for
     * @return true if the Bible exists
     */
    public boolean exists(String name)
    {
    	if(configCache.get(name)!=null)return true;
    	else return false;
    }

    /**
     * Featch a currently existing Bible, read-only
     * @param name The name of the version to create
     * @exception BookException If the name is not valid
     */
    public Bible getBible(String name) throws BookException
    {
    	return new SwordBible(dir,(SwordConfig)configCache.get(name));
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
        // Just accept that we're not supposed to work ...
        if (sword_dir == null || sword_dir.trim().length() == 0)
        {
            driver.dir = null;
            log.info("No sword dir set.");
            return;
        }

        URL dir_temp = new URL("file:"+sword_dir);

        if (!NetUtil.isDirectory(dir_temp))
            throw new MalformedURLException("No sword source found under "+sword_dir);

        driver.dir = dir_temp;
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

	/** config cache */
	private Hashtable configCache = new Hashtable();

    /** The directory URL */
    private URL dir;

    /** The singleton driver */
    protected static SwordBibleDriver driver;

    /** The log stream */
    protected static Logger log = Logger.getLogger(SwordBibleDriver.class);

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
        	if(name.endsWith(".conf")&&!name.startsWith("globals."))return true;
        	else return false;
        }
    }
}
