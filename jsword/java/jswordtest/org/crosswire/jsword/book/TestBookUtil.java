
package org.crosswire.jsword.book;

import junit.framework.TestCase;

import org.crosswire.jsword.passage.Books;

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
public class TestBookUtil extends TestCase
{
    public TestBookUtil(String s)
    {
        super(s);
    }

    String[] sa = BookUtil.tokenize("one two three");

    protected void setUp()
    {
    }

    protected void tearDown()
    {
    }

    public void testUpdatePassageTally()
    {
        // BookUtil.updatePassageTally(version, tally, words);
    }

    public void testUpdatePassageTallyFlat() throws Exception
    {
        // BookUtil.updatePassageTallyFlat(version, tally, words);
    }

    public void testGetPassage() throws Exception
    {
        // Passage ref = BookUtil.getPassage(version, words);
    }

    public void testIsNewPara() throws Exception
    {
        // boolean b = BookUtil.isNewPara(doc);
    }

    public void testTokenize() throws Exception
    {
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "one ");
        assertEquals(sa[1], "two ");
        assertEquals(sa[2], "three");
        sa = BookUtil.tokenize("!one  two three ");
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "!one ");
        assertEquals(sa[1], "two ");
        assertEquals(sa[2], "three ");
        sa = BookUtil.tokenize("\"one-- two three ");
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "\"one-- ");
        assertEquals(sa[1], "two ");
        assertEquals(sa[2], "three ");
        sa = BookUtil.tokenize("-one--two three ");
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "-one--");
        assertEquals(sa[1], "two ");
        assertEquals(sa[2], "three ");
        sa = BookUtil.tokenize("one-two--three ");
        assertEquals(sa.length, 2);
        assertEquals(sa[0], "one-two--");
        assertEquals(sa[1], "three ");
        sa = BookUtil.tokenize("one!£ \"*(two-three");
        assertEquals(sa.length, 2);
        assertEquals(sa[0], "one!£ ");
        assertEquals(sa[1], "\"*(two-three");
    }

    public void testGetWords() throws Exception
    {
        sa = BookUtil.getWords("One Two three");
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "one");
        assertEquals(sa[1], "two");
        assertEquals(sa[2], "three");
        sa = BookUtil.getWords("!one  two three ");
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "one");
        assertEquals(sa[1], "two");
        assertEquals(sa[2], "three");
        sa = BookUtil.getWords("\"one-- two three ");
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "one");
        assertEquals(sa[1], "two");
        assertEquals(sa[2], "three");
        sa = BookUtil.getWords("-one--two three ");
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "one");
        assertEquals(sa[1], "two");
        assertEquals(sa[2], "three");
        sa = BookUtil.getWords("one-two--three ");
        assertEquals(sa.length, 2);
        assertEquals(sa[0], "one-two");
        assertEquals(sa[1], "three");
        sa = BookUtil.getWords("one!£ \"*(two-three");
        assertEquals(sa.length, 2);
        assertEquals(sa[0], "one");
        assertEquals(sa[1], "two-three");
    }

    public void testStripPunctuation() throws Exception
    {
        // String[] sa = BookUtil.stripPunctuation(words);
    }

    public void testStripPunctuationWord() throws Exception
    {
        assertEquals(BookUtil.stripPunctuationWord("test"), "test");
        assertEquals(BookUtil.stripPunctuationWord(" test"), "test");
        assertEquals(BookUtil.stripPunctuationWord("test-- "), "test");
        assertEquals(BookUtil.stripPunctuationWord("test! "), "test");
        assertEquals(BookUtil.stripPunctuationWord("test\" "), "test");
        assertEquals(BookUtil.stripPunctuationWord("test... "), "test");
        assertEquals(BookUtil.stripPunctuationWord("test's"), "test's");
        assertEquals(BookUtil.stripPunctuationWord("test's "), "test's");
        assertEquals(BookUtil.stripPunctuationWord("test's!"), "test's");
        assertEquals(BookUtil.stripPunctuationWord("test's?"), "test's");
        assertEquals(BookUtil.stripPunctuationWord("test!?;;'#\""), "test");
        assertEquals(BookUtil.stripPunctuationWord("!\"£$test"), "test");
        assertEquals(BookUtil.stripPunctuationWord("   test "), "test");
        assertEquals(BookUtil.stripPunctuationWord("--test "), "test");
        assertEquals(BookUtil.stripPunctuationWord("'test "), "test");
        assertEquals(BookUtil.stripPunctuationWord("/?test "), "test");
        assertEquals(BookUtil.stripPunctuationWord(" $%^\" test %^&"), "test");
    }

    public void testStripWordsStringArray() throws Exception
    {
        // String[] sa = BookUtil.stripWords(words);
    }

    public void testStripWordsStringString() throws Exception
    {
        // String s = BookUtil.stripWords(first, last);
    }

    public void testGetCases() throws Exception
    {
        int[] ia = BookUtil.getCases(new String[] { "abc" });
        assertEquals(ia[0], Books.CASE_LOWER);
    }

    public void testFirstLetter() throws Exception
    {
        assertEquals(BookUtil.firstLetter("abcde"), 0);
        assertEquals(BookUtil.firstLetter(" abcde"), 1);
        assertEquals(BookUtil.firstLetter(" \"£abcde"), 3);
        assertEquals(BookUtil.firstLetter(" \"£abcde--!   "), 3);
    }

    public void testLastLetter() throws Exception
    {
        assertEquals(BookUtil.lastLetter("abcde"), 4);
        assertEquals(BookUtil.lastLetter("abcde "), 4);
        assertEquals(BookUtil.lastLetter("abcde\" "), 4);
        assertEquals(BookUtil.lastLetter("abcde\"£$ "), 4);
        assertEquals(BookUtil.lastLetter(" abcde"), 5);
        assertEquals(BookUtil.lastLetter(" abcde "), 5);
        assertEquals(BookUtil.lastLetter(" abcde\" "), 5);
        assertEquals(BookUtil.lastLetter(" abcde\"£$ "), 5);
        assertEquals(BookUtil.lastLetter(" abcde--\"£$ "), 5);
        assertEquals(BookUtil.lastLetter(" abcde\"£$-- "), 5);
    }
}
