
package org.crosswire.jsword.book;

import java.util.List;

import junit.framework.TestCase;

/**
 * JUnit Test.
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
public class TestDriverManager extends TestCase
{
    public TestDriverManager(String s)
    {
        super(s);
    }


    protected void setUp() throws Exception
    {
    }

    protected void tearDown()
    {
    }

    public void testGetDriverForBible() throws Exception
    {
        /*
        BibleMetaData[] names = Bibles.getBibles();

        for (int i=0; i<names.length; i++)
        {
            BibleDriver driver = names[i].getBibleDriver();
            assertTrue(driver != null);
        }

        for (int i=0; i<names.length; i++)
        {
            try { BibleDriverManager.getDriverForBible(names[i]); fail(); }
            catch (BookException ex) { }
        }
        */
    }

    public void testRegUnreg() throws Exception
    {
        List drivers = BibleDriverManager.getDrivers();

        int before = drivers.size();

        for (int i=0; i<before; i++)
        {
            BibleDriverManager.unregisterDriver((BibleDriver) drivers.get(i));

            List temp = BibleDriverManager.getDrivers();
            assertEquals(before - i, temp.size() + 1);
        }

        for (int i=0; i<before; i++)
        {
            BibleDriverManager.registerDriver((BibleDriver)drivers.get(i));

            List temp = BibleDriverManager.getDrivers();
            assertEquals(i + 1, temp.size());
        }
    }
}
