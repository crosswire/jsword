
package org.crosswire.jsword.control.dictionary;

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
public class TestDictionary extends TestCase
{
    public TestDictionary(String s)
    {
        super(s);
    }

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
    }

    public void testGetRoot() throws Exception
    {
        assertEquals(Grammar.getRoot("joseph"), "joseph");
        assertEquals(Grammar.getRoot("joseph's"), "joseph");
        assertEquals(Grammar.getRoot("walker"), "walk");
        assertEquals(Grammar.getRoot("walked"), "walk");
        assertEquals(Grammar.getRoot("walks"), "walk");
        assertEquals(Grammar.getRoot("boxes"), "box");
    }

    public void testIsSmallWord() throws Exception
    {
        assertTrue(Grammar.isSmallWord("the"));
        assertTrue(Grammar.isSmallWord("and"));
        assertTrue(!Grammar.isSmallWord("lord"));
        assertTrue(!Grammar.isSmallWord("god"));
        assertTrue(Grammar.isSmallWord("o"));
        assertTrue(!Grammar.isSmallWord("nothing"));
        assertTrue(Grammar.isSmallWord(" the "));
        assertTrue(Grammar.isSmallWord(" and "));
        assertTrue(!Grammar.isSmallWord(" lord "));
        assertTrue(!Grammar.isSmallWord(" god "));
        assertTrue(Grammar.isSmallWord(" o "));
        assertTrue(!Grammar.isSmallWord(" nothing "));
        assertTrue(Grammar.isSmallWord(""));
        assertTrue(Grammar.isSmallWord(" "));
        assertTrue(Grammar.isSmallWord("  "));
    }

    public void testStripSmallWords() throws Exception
    {
        String[] temp = Grammar.stripSmallWords(new String[] { "i", "am", "but", "nothing", "o", "the", "lord", "god", "and", "", });
        assertEquals(temp[0], "nothing");
        assertEquals(temp[1], "lord");
        assertEquals(temp[2], "god");
        assertEquals(temp.length, 3);
    }

    public void testTokenizeWithoutSmallWords() throws Exception
    {
        String[] temp = Grammar.tokenizeWithoutSmallWords("i am but nothing o the lord god and ", " ");
        assertEquals(temp[0], "nothing");
        assertEquals(temp[1], "lord");
        assertEquals(temp[2], "god");
        assertEquals(temp.length, 3);
    }
}
