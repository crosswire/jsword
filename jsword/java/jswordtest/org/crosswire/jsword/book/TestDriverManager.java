
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

    String[] names = null;
    BibleDriver[] drivers = null;

    protected void setUp() throws Exception
    {
        names = Bibles.getBibleNames();
        drivers = BibleDriverManager.getDrivers();
    }

    protected void tearDown()
    {
    }

    public void testGetDrivers()
    {
        int before = drivers.length;
        assertEquals(before, 3);
    }

    public void testGetDriverForBible() throws Exception
    {
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

    public void testUnregisterDriver() throws Exception
    {
        BibleDriverManager.unregisterDriver(drivers[0]);
        BibleDriver[] d2 = BibleDriverManager.getDrivers();
        int n2 = d2.length;
        assertEquals(n2, 2);
        BibleDriverManager.unregisterDriver(drivers[1]);
        BibleDriver[] d3 = BibleDriverManager.getDrivers();
        int n3 = d3.length;
        assertEquals(n3, 1);
        BibleDriverManager.unregisterDriver(drivers[2]);
        BibleDriver[] d4 = BibleDriverManager.getDrivers();
        int n4 = d4.length;
        assertEquals(n4, 0);
    }

    public void testRegisterDriver() throws Exception
    {
        BibleDriverManager.registerDriver(drivers[0]);
        BibleDriver[] d5 = BibleDriverManager.getDrivers();
        int n5 = d5.length;
        assertEquals(n5, 1);
        BibleDriverManager.registerDriver(drivers[1]);
        BibleDriver[] d6 = BibleDriverManager.getDrivers();
        int n6 = d6.length;
        assertEquals(n6, 2);
        BibleDriverManager.registerDriver(drivers[2]);
        BibleDriver[] d7 = BibleDriverManager.getDrivers();
        int n7 = d7.length;
        assertEquals(n7, 3);
    }
}
