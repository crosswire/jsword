
package org.crosswire.jsword.book;

import java.util.ArrayList;
import java.util.List;

import org.crosswire.jsword.util.Project;

/**
 * The BibleDriverManager is a set of helpers for things wanting to work
 * with BibleDrivers and Bibles.
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
public class BibleDriverManager
{
    /**
     * Ensure that we can not be instansiated
     */
    private BibleDriverManager()
    {
    }

    /**
     * Add to the list of drivers
     * @param driver The BookDriver to add
     */
    public static void registerDriver(BibleDriver driver)
    {
        drivers.add(driver);
    }

    /**
     * Remove from the list of drivers
     * @param driver The BookDriver to remove
     */
    public static void unregisterDriver(BibleDriver driver)
    {
        drivers.remove(driver);
    }

    /**
     * Get an array of all the known drivers
     * @return Found int or the default value
     */
    public static BibleDriver[] getDrivers()
    {
        BibleDriver[] da = new BibleDriver[drivers.size()];
        for (int i=0; i<da.length; i++)
        {
            da[i] = (BibleDriver) drivers.get(i);
        }

        return da;
    }

    /**
     * An array of BookDrivers
     */
    private static List drivers = new ArrayList();

    /**
     * Initialize the driver array
     */
    static
    {
        // This will classload them all and they will register themselves.
        Project.resource().getImplementors(BibleDriver.class);
    }
}
