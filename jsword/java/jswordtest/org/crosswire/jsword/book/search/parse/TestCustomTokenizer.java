
package org.crosswire.jsword.book.search.parse;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
public class TestCustomTokenizer extends TestCase
{
    public TestCustomTokenizer(String s)
    {
        super(s);
    }

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
    }

    public void testGetStringArray() throws Exception
    {
        Map commands = SearchDefault.getMap();
        List output = null;

        output = CustomTokenizer.tokenize("/ aaron , moses", commands);
        assertEquals(output.get(0).getClass().getName(), "org.crosswire.jsword.book.search.parse.AddCommandWord");
        assertEquals(output.get(1).getClass().getName(), "org.crosswire.jsword.book.search.parse.DefaultParamWord");
        assertEquals(output.get(2).getClass().getName(), "org.crosswire.jsword.book.search.parse.RetainCommandWord");
        assertEquals(output.get(3).getClass().getName(), "org.crosswire.jsword.book.search.parse.DefaultParamWord");
        assertEquals(output.size(), 4);

        output = CustomTokenizer.tokenize("/aaron+moses", commands);
        assertEquals(output.get(0).getClass().getName(), "org.crosswire.jsword.book.search.parse.AddCommandWord");
        assertEquals(output.get(1).getClass().getName(), "org.crosswire.jsword.book.search.parse.DefaultParamWord");
        assertEquals(output.get(2).getClass().getName(), "org.crosswire.jsword.book.search.parse.RetainCommandWord");
        assertEquals(output.get(3).getClass().getName(), "org.crosswire.jsword.book.search.parse.DefaultParamWord");
        assertEquals(output.size(), 4);

        output = CustomTokenizer.tokenize("&aaron-moses", commands);
        assertEquals(output.get(0).getClass().getName(), "org.crosswire.jsword.book.search.parse.RetainCommandWord");
        assertEquals(output.get(1).getClass().getName(), "org.crosswire.jsword.book.search.parse.DefaultParamWord");
        assertEquals(output.get(2).getClass().getName(), "org.crosswire.jsword.book.search.parse.RemoveCommandWord");
        assertEquals(output.get(3).getClass().getName(), "org.crosswire.jsword.book.search.parse.DefaultParamWord");
        assertEquals(output.size(), 4);

        output = CustomTokenizer.tokenize("/aaron~5+moses", commands);
        assertEquals(output.get(0).getClass().getName(), "org.crosswire.jsword.book.search.parse.AddCommandWord");
        assertEquals(output.get(1).getClass().getName(), "org.crosswire.jsword.book.search.parse.DefaultParamWord");
        assertEquals(output.get(2).getClass().getName(), "org.crosswire.jsword.book.search.parse.BlurCommandWord");
        assertEquals(output.get(3).getClass().getName(), "org.crosswire.jsword.book.search.parse.DefaultParamWord");
        assertEquals(output.get(4).getClass().getName(), "org.crosswire.jsword.book.search.parse.RetainCommandWord");
        assertEquals(output.get(5).getClass().getName(), "org.crosswire.jsword.book.search.parse.DefaultParamWord");
        assertEquals(output.size(), 6);

        output = CustomTokenizer.tokenize("  /  aaron  ~   5    +     moses   ", commands);
        assertEquals(output.get(0).getClass().getName(), "org.crosswire.jsword.book.search.parse.AddCommandWord");
        assertEquals(output.get(1).getClass().getName(), "org.crosswire.jsword.book.search.parse.DefaultParamWord");
        assertEquals(output.get(2).getClass().getName(), "org.crosswire.jsword.book.search.parse.BlurCommandWord");
        assertEquals(output.get(3).getClass().getName(), "org.crosswire.jsword.book.search.parse.DefaultParamWord");
        assertEquals(output.get(4).getClass().getName(), "org.crosswire.jsword.book.search.parse.RetainCommandWord");
        assertEquals(output.get(5).getClass().getName(), "org.crosswire.jsword.book.search.parse.DefaultParamWord");
        assertEquals(output.size(), 6);

        output = CustomTokenizer.tokenize("  /  aaron  ~   5    +     moses   ", commands);
        Iterator it = output.iterator();
        assertTrue(it.hasNext());
        assertEquals(it.next().getClass().getName(), "org.crosswire.jsword.book.search.parse.AddCommandWord");
        assertTrue(it.hasNext());
        assertEquals(it.next().getClass().getName(), "org.crosswire.jsword.book.search.parse.DefaultParamWord");
        assertTrue(it.hasNext());
        assertEquals(it.next().getClass().getName(), "org.crosswire.jsword.book.search.parse.BlurCommandWord");
        assertTrue(it.hasNext());
        assertEquals(it.next().getClass().getName(), "org.crosswire.jsword.book.search.parse.DefaultParamWord");
        assertTrue(it.hasNext());
        assertEquals(it.next().getClass().getName(), "org.crosswire.jsword.book.search.parse.RetainCommandWord");
        assertTrue(it.hasNext());
        assertEquals(it.next().getClass().getName(), "org.crosswire.jsword.book.search.parse.DefaultParamWord");
        assertTrue(!it.hasNext());

        // This is not actually a legal search string ... however the parser should get it right
        output = CustomTokenizer.tokenize("&~5-/", commands);
        assertEquals(output.get(0).getClass().getName(), "org.crosswire.jsword.book.search.parse.RetainCommandWord");
        assertEquals(output.get(1).getClass().getName(), "org.crosswire.jsword.book.search.parse.BlurCommandWord");
        assertEquals(output.get(2).getClass().getName(), "org.crosswire.jsword.book.search.parse.DefaultParamWord");
        assertEquals(output.get(3).getClass().getName(), "org.crosswire.jsword.book.search.parse.RemoveCommandWord");
        assertEquals(output.get(4).getClass().getName(), "org.crosswire.jsword.book.search.parse.AddCommandWord");
        assertEquals(output.size(), 5);
    }
}
