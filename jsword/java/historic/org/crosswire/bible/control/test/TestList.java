
package org.crosswire.bible.control.test;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Old style Test.
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
public class TestList
{
    /**
     * Ensure that we are not initialized
     */
    private TestList()
    {
    }

    /**
     * Get a hash of available test sets and names for them
     * @return A test set hash
     */
    public static Hashtable getTesters()
    {
        if (hash == null)
        {
            hash = new Hashtable();
        }

        return hash;
    }

    /**
     * Get an array containing just the names of the tests
     * @return A list of test names
     */
    public static String[] getNames()
    {
        if (names == null)
        {
            if (hash == null)
                getTesters();

            names = new String[hash.size()];
            Enumeration en = hash.keys();
            int i = 0;
            while (en.hasMoreElements())
            {
                names[i++] = (String) en.nextElement();
            }
        }

        return names;
    }

    /** The hash of test sets */
    private static Hashtable hash;

    /** The array of test names */
    private static String[] names;
}
