
package org.crosswire.jsword.book.stub;

import org.apache.log4j.Logger;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BibleDriverManager;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.basic.AbstractBibleDriver;

/**
 * StubBibleDriver is a simple stub implementation of BibleDriver that is
 * pretty much always going to work because it has no dependancies on external
 * files.
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
public class StubBibleDriver extends AbstractBibleDriver
{
    /**
     * Some basic driver initialization
     */
    public StubBibleDriver()
    {
        log.debug("Starting");
    }

    /**
     * Some basic info about who we are
     * @param A short identifing string
     */
    public String getDriverName()
    {
        return "Stub";
    }

    /**
     * Get a list of the Books available from the driver
     * @return an array of book names
     */
    public String[] getBibleNames()
    {
        return new String[] { "av-stub", "niv-stub" };
    }

    /**
     * Get a list of the Books available from the driver
     * @return an array of book names
     */
    public boolean exists(String name)
    {
        return name.equals("av-stub") || name.equals("niv-stub");
    }

    /**
     * Get a list of the Books available from the driver
     * @return an array of book names
     */
    public Bible getBible(String name) throws BookException
    {
        return new StubBible(name);
    }

    /**
     * Create a new blank Bible read for writing
     * @param name The name of the version to create
     * @exception BookException If the name is not valid
     */
    public Bible createBible(String name) throws BookException
    {
        throw new BookException("stub_driver_readonly");
    }

    /**
     * The singleton driver
     */
    protected static StubBibleDriver driver;

    /** The log stream */
    protected static Logger log = Logger.getLogger(StubBibleDriver.class);

    /**
     * Register ourselves with the Driver Manager
     */
    static
    {
        driver = new StubBibleDriver();
        BibleDriverManager.registerDriver(driver);
    }
}
