
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
public class TestBibles extends TestCase
{
    public TestBibles(String s)
    {
        super(s);
    }

    String[] names = null;

    protected void setUp() throws Exception
    {
        names = Bibles.getBibleNames();
    }

    protected void tearDown()
    {
    }

    public void testGetBibleNames() throws Exception
    {
        assertTrue(names.length > 0);
    }

    public void testGetBible() throws Exception
    {
        for (int i=0; i<names.length; i++)
        {
            Bible b = Bibles.getBible(names[i]);
            assertTrue(b != null);
        }
        try { Bible b = Bibles.getBible("NONE"); fail(); }
        catch (BookException ex) { }
    }

    public void testCreateBible() throws Exception
    {
        // Bible b2 = Bibles.createBible("dest_name", dest_driver);
    }

    public void testDefaultBible() throws Exception
    {
        String deft = Bibles.getDefaultName();
        for (int i=0; i<names.length; i++)
        {
            Bibles.setDefaultName(names[0]);
            assertEquals(Bibles.getDefaultName(), names[0]);
            assertEquals(Bibles.getDefaultBible().getMetaData().getName(), names[0]);
        }
        try { Bibles.setDefaultName("NONE"); fail(); }
        catch (BookException ex) { }
        Bibles.setDefaultName(deft);
    }

    public void testCacheingBibles() throws Exception
    {
        boolean cb = Bibles.getCacheingBibles();
        Bibles.setCacheingBibles(true);
        assertTrue(Bibles.getCacheingBibles());
        Bibles.setCacheingBibles(false);
        assertTrue(!Bibles.getCacheingBibles());
        Bibles.setCacheingBibles(cb);
    }
}
