
package org.crosswire.jsword.book.config;

import java.util.Enumeration;
import java.util.Hashtable;

import org.crosswire.jsword.book.BibleDriver;
import org.crosswire.jsword.book.BibleDriverManager;
import org.crosswire.jsword.book.Bibles;
import org.crosswire.common.config.choices.HashtableChoice;
import org.crosswire.common.util.Reporter;

/**
 * DriversChoice allows the user to configure which drivers are available
 * for reading and writing versions.
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
 * @version D6.I4.T0
 */
public class DriversChoice extends HashtableChoice
{
    /**
     * Basic constructor
     */
    public DriversChoice()
    {
        super(BibleDriver.class);
    }

    /**
     * Generalized read Object from the Properties file
     * @return Found int or the default value
     */
    public Hashtable getHashtable()
    {
        Hashtable hash = new Hashtable();

        // For all the drivers
        BibleDriver[] da = BibleDriverManager.getDrivers();
        for (int i=0; i<da.length; i++)
        {
            String name = da[i].getDriverName();
            hash.put(name, da[i].getClass().getName());
        }

        return hash;
    }

    /**
     * Generalized set Object to the Properties file
     * @param value The value to enter
     */
    public void setHashtable(Hashtable value)
    {
        Class[] drivers = new Class[value.size()];
        int i = 0;

        for (Enumeration en=value.elements(); en.hasMoreElements(); )
        {
            try
            {
                // This should automatically register the driver
                String class_name = (String) en.nextElement();
                Class.forName(class_name);
            }
            catch (Exception ex)
            {
                Reporter.informUser(this, ex);
            }
        }

        Bibles.fireBiblesChanged(this, null, true);
    }
}
