
package org.crosswire.jsword.book;

import java.util.Date;

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
 * @version $Id: ParentTstBibleDriver.java,v 1.4 2002/11/25 18:07:52 joe Exp $
 */
public class ParentTstBibleDriver extends TestCase
{
    public ParentTstBibleDriver(String s, BibleDriver driver)
    {
        super(s);

        this.driver = driver;
    }

    private BibleDriver driver = null;
    private BibleMetaData[] bmds = null;

    protected void setUp()
    {
        bmds = driver.getBibles();
    }

    protected void tearDown()
    {
    }

    public void testGeneral() throws Exception
    {
        assertTrue(!driver.equals(null));
        assertTrue(!driver.equals(""));

        assertTrue(driver.getBooks().length == bmds.length);
    }

    public void testDriverName() throws Exception
    {
        assertTrue(driver.getDriverName() != null);
    }

    public void testMetaData() throws Exception
    {
        assertTrue(bmds != null);
        
        if (bmds.length == 0)
            System.out.println("warning: zero Bibles from "+driver.getDriverName()+" ("+driver.getClass().getName()+")");

        for (int i=0; i<bmds.length; i++)
        {
            BibleMetaData bmd = bmds[i];
            
            assertTrue(bmd.getDriverName().equals(driver.getDriverName()));
            assertTrue(bmd.getEdition() != null);
            assertTrue(!bmd.getEdition().endsWith("Edition"));

            Date pub = bmd.getFirstPublished();
            if (pub != null)
            {
                // the date must be in the past
                assertTrue(pub.before(new Date()));
            }

            assertTrue(bmd.getFullName() != null);
            assertTrue(bmd.getFullName().length() > 0);
            assertTrue(bmd.getInitials() != null);
            assertTrue(bmd.getInitials().length() > 0);
            assertTrue(bmd.getName() != null);
            assertTrue(bmd.getName().length() > 0);
            assertTrue(bmd.getFullName().length() > bmd.getName().length());
            assertTrue(bmd.getName().length() > bmd.getInitials().length());
            assertTrue(bmd.getOpenness() != null);
        }
    }

    public void testGetBible() throws Exception
    {
        for (int i=0; i<bmds.length; i++)
        {
            Bible b = bmds[i].getBible();
            assertTrue(b != null);
        }
    }
}
