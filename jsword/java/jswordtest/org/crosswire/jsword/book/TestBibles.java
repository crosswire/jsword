
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
public class TestBibles extends TestCase
{
    public TestBibles(String s)
    {
        super(s);
    }

    BibleMetaData[] names = null;

    protected void setUp() throws Exception
    {
        names = Bibles.getBibles();
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
            Bible b = names[i].getBible();
            assertTrue(b != null);
        }
    }

    public void testCreateBible() throws Exception
    {
        // Bible b2 = Bibles.createBible("dest_name", dest_driver);
    }
}
