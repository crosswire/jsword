
package org.crosswire.jsword.book;

import junit.framework.TestCase;

import org.crosswire.jsword.book.data.BibleData;
import org.crosswire.jsword.book.data.BookData;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;

/**
 * JUnit Test.
 * For when we don't actually want to do testing of responses
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
 * @version $Id: ParentTstBible.java,v 1.4 2002/11/25 18:07:52 joe Exp $
 */
public class ParentTstBible extends TestCase
{
    public ParentTstBible(String s, BibleDriver driver)
    {
        super(s);
        this.driver = driver;
    }

    protected BibleDriver driver = null;
    protected BibleMetaData[] bmds = null;
    protected Bible[] bibles = null;
    protected Passage gen11 = null;

    protected void setUp() throws Exception
    {
        gen11 = PassageFactory.createPassage("Gen 1:1");
        bmds = driver.getBibles();
        
        bibles = new Bible[bmds.length];
        
        for (int i=0; i<bibles.length; i++)
        {
            bibles[i] = bmds[i].getBible();
        }
    }

    protected void tearDown()throws Exception
    {
    }

    public void testGetBible()
    {
        for (int i=0; i<bibles.length; i++)
        {
            Bible bible = bibles[i];
            assertTrue(bible != null);
        }
    }

    public void testGetBibleMetaData() throws Exception
    {
        for (int i=0; i<bibles.length; i++)
        {
            Bible bible = bibles[i];
            BibleMetaData bmd = bible.getBibleMetaData();
            assertEquals(bmd, bmds[i]);
        }
    }

    public void testGetBookMetaData() throws Exception
    {
        for (int i=0; i<bibles.length; i++)
        {
            Bible bible = bibles[i];
            BookMetaData bmd = bible.getBookMetaData();
            assertEquals(bmd, bmds[i]);
        }
    }

    public void testGetDataKey() throws Exception
    {
        for (int i=0; i<bibles.length; i++)
        {
            Bible bible = bibles[i];
            BookData data = bible.getData(new PassageKey("Gen 1:1"));
            assertNotNull(data);
        }
    }

    public void testGetDataPassage() throws Exception
    {
        for (int i=0; i<bibles.length; i++)
        {
            Bible bible = bibles[i];
            BibleData data = bible.getData(gen11);
            assertNotNull(data);
        }
    }

    public void testGetFind() throws Exception
    {
        // This only checks that find() does something vaguely sensible
        // I assume that find() just calls findPassage(), where the real tests are
        for (int i=0; i<bibles.length; i++)
        {
            Bible bible = bibles[i];
            Key key = bible.find("aaron");
            assertNotNull(key);
        }
    }

    public void testFindPassage() throws Exception
    {
        for (int i=0; i<bibles.length; i++)
        {
            Bible ver = bibles[i];

            Passage ref = ver.findPassage("aaron");
            assertTrue(ref != null);
        }
    }

    public void testGetStartsWith() throws Exception
    {
        for (int i=0; i<bibles.length; i++)
        {
            Bible ver = bibles[i];

            String[] sa = BookUtil.toStringArray(ver.getStartsWith("a"));
            assertTrue(sa != null);
        }
    }
}
