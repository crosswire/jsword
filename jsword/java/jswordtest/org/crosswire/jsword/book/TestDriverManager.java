
package org.crosswire.jsword.book;

import junit.framework.TestCase;

/**
 * JUnit Test.
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
        String[] names = Bibles.getBibleNames();

        for (int i=0; i<names.length; i++)
        {
            BibleDriver driver = BibleDriverManager.getDriverForBible(names[i]);
            assertTrue(driver != null);
        }
        try { BibleDriverManager.getDriverForBible("NONE"); fail(); }
        catch (BookException ex) { }

        /*
        for (int i=0; i<names.length; i++)
        {
            try { BibleDriverManager.getDriverForBible(names[i]); fail(); }
            catch (BookException ex) { }
        }
        */
    }

    public void testRegUnreg() throws Exception
    {
        BibleDriver[] drivers = BibleDriverManager.getDrivers();

        int before = drivers.length;

        for (int i=0; i<before; i++)
        {
            BibleDriverManager.unregisterDriver(drivers[i]);

            BibleDriver[] temp = BibleDriverManager.getDrivers();
            assertEquals(before - i, temp.length + 1);
        }

        for (int i=0; i<before; i++)
        {
            BibleDriverManager.registerDriver(drivers[i]);

            BibleDriver[] temp = BibleDriverManager.getDrivers();
            assertEquals(i + 1, temp.length);
        }
    }
}
