
package org.crosswire.jsword.control.search;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

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
        Hashtable commands = SearchDefault.getHashtable();
        Vector output = null;

        output = CustomTokenizer.tokenize("/ aaron , moses", commands);
        assertEquals(output.elementAt(0).getClass().getName(), "org.crosswire.jsword.control.search.words.AddCommandWord");
        assertEquals(output.elementAt(1).getClass().getName(), "org.crosswire.jsword.control.search.words.DefaultParamWord");
        assertEquals(output.elementAt(2).getClass().getName(), "org.crosswire.jsword.control.search.words.RetainCommandWord");
        assertEquals(output.elementAt(3).getClass().getName(), "org.crosswire.jsword.control.search.words.DefaultParamWord");
        assertEquals(output.size(), 4);

        output = CustomTokenizer.tokenize("/aaron+moses", commands);
        assertEquals(output.elementAt(0).getClass().getName(), "org.crosswire.jsword.control.search.words.AddCommandWord");
        assertEquals(output.elementAt(1).getClass().getName(), "org.crosswire.jsword.control.search.words.DefaultParamWord");
        assertEquals(output.elementAt(2).getClass().getName(), "org.crosswire.jsword.control.search.words.RetainCommandWord");
        assertEquals(output.elementAt(3).getClass().getName(), "org.crosswire.jsword.control.search.words.DefaultParamWord");
        assertEquals(output.size(), 4);

        output = CustomTokenizer.tokenize("&aaron-moses", commands);
        assertEquals(output.elementAt(0).getClass().getName(), "org.crosswire.jsword.control.search.words.RetainCommandWord");
        assertEquals(output.elementAt(1).getClass().getName(), "org.crosswire.jsword.control.search.words.DefaultParamWord");
        assertEquals(output.elementAt(2).getClass().getName(), "org.crosswire.jsword.control.search.words.RemoveCommandWord");
        assertEquals(output.elementAt(3).getClass().getName(), "org.crosswire.jsword.control.search.words.DefaultParamWord");
        assertEquals(output.size(), 4);

        output = CustomTokenizer.tokenize("/aaron~5+moses", commands);
        assertEquals(output.elementAt(0).getClass().getName(), "org.crosswire.jsword.control.search.words.AddCommandWord");
        assertEquals(output.elementAt(1).getClass().getName(), "org.crosswire.jsword.control.search.words.DefaultParamWord");
        assertEquals(output.elementAt(2).getClass().getName(), "org.crosswire.jsword.control.search.words.BlurCommandWord");
        assertEquals(output.elementAt(3).getClass().getName(), "org.crosswire.jsword.control.search.words.DefaultParamWord");
        assertEquals(output.elementAt(4).getClass().getName(), "org.crosswire.jsword.control.search.words.RetainCommandWord");
        assertEquals(output.elementAt(5).getClass().getName(), "org.crosswire.jsword.control.search.words.DefaultParamWord");
        assertEquals(output.size(), 6);

        output = CustomTokenizer.tokenize("  /  aaron  ~   5    +     moses   ", commands);
        assertEquals(output.elementAt(0).getClass().getName(), "org.crosswire.jsword.control.search.words.AddCommandWord");
        assertEquals(output.elementAt(1).getClass().getName(), "org.crosswire.jsword.control.search.words.DefaultParamWord");
        assertEquals(output.elementAt(2).getClass().getName(), "org.crosswire.jsword.control.search.words.BlurCommandWord");
        assertEquals(output.elementAt(3).getClass().getName(), "org.crosswire.jsword.control.search.words.DefaultParamWord");
        assertEquals(output.elementAt(4).getClass().getName(), "org.crosswire.jsword.control.search.words.RetainCommandWord");
        assertEquals(output.elementAt(5).getClass().getName(), "org.crosswire.jsword.control.search.words.DefaultParamWord");
        assertEquals(output.size(), 6);

        output = CustomTokenizer.tokenize("  /  aaron  ~   5    +     moses   ", commands);
        Enumeration en = output.elements();
        assertTrue(en.hasMoreElements());
        assertEquals(en.nextElement().getClass().getName(), "org.crosswire.jsword.control.search.words.AddCommandWord");
        assertTrue(en.hasMoreElements());
        assertEquals(en.nextElement().getClass().getName(), "org.crosswire.jsword.control.search.words.DefaultParamWord");
        assertTrue(en.hasMoreElements());
        assertEquals(en.nextElement().getClass().getName(), "org.crosswire.jsword.control.search.words.BlurCommandWord");
        assertTrue(en.hasMoreElements());
        assertEquals(en.nextElement().getClass().getName(), "org.crosswire.jsword.control.search.words.DefaultParamWord");
        assertTrue(en.hasMoreElements());
        assertEquals(en.nextElement().getClass().getName(), "org.crosswire.jsword.control.search.words.RetainCommandWord");
        assertTrue(en.hasMoreElements());
        assertEquals(en.nextElement().getClass().getName(), "org.crosswire.jsword.control.search.words.DefaultParamWord");
        assertTrue(!en.hasMoreElements());

        // This is not actually a legal search string ... however the parser should get it right
        output = CustomTokenizer.tokenize("&~5-/", commands);
        assertEquals(output.elementAt(0).getClass().getName(), "org.crosswire.jsword.control.search.words.RetainCommandWord");
        assertEquals(output.elementAt(1).getClass().getName(), "org.crosswire.jsword.control.search.words.BlurCommandWord");
        assertEquals(output.elementAt(2).getClass().getName(), "org.crosswire.jsword.control.search.words.DefaultParamWord");
        assertEquals(output.elementAt(3).getClass().getName(), "org.crosswire.jsword.control.search.words.RemoveCommandWord");
        assertEquals(output.elementAt(4).getClass().getName(), "org.crosswire.jsword.control.search.words.AddCommandWord");
        assertEquals(output.size(), 5);
    }
}
