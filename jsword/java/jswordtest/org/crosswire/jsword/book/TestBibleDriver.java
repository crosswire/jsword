
package org.crosswire.jsword.book;

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
public class TestBibleDriver extends TestCase
{
    public TestBibleDriver(String s, BibleDriver driver)
    {
        super(s);

        this.driver = driver;
    }

    BibleDriver driver = null;
    String[] names = null;

    protected void setUp()
    {
        names = driver.getBibleNames();
    }

    protected void tearDown()
    {
    }

    public void testDriverName() throws Exception
    {
        assertTrue(driver.getDriverName() != null);
    }

    public void testBibleNames() throws Exception
    {
        assertTrue(names != null);
        assertTrue(names.length > 0);
    }

    public void testCountBibles() throws Exception
    {
        assertEquals(driver.countBibles(), names.length);
    }

    public void testExists() throws Exception
    {
        for (int i=0; i<names.length; i++)
        {
            assertTrue(driver.exists(names[i]));
        }
        assertTrue(!driver.exists("NONE"));
    }

    public void testGetBible() throws Exception
    {
        for (int i=0; i<names.length; i++)
        {
            Bible b = driver.getBible(names[i]);
            assertTrue(b != null);
        }
        try { driver.getBible("NONE"); fail(); }
        catch (BookException ex) { }
    }
}
