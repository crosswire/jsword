
package org.crosswire.jsword.book;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;
import org.crosswire.jsword.passage.BibleInfo;

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

        // moved from TestRawBible
        sa = BookUtil.tokenize("one two three");
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "one ");
        assertEquals(sa[1], "two ");
        assertEquals(sa[2], "three");

        sa = BookUtil.tokenize("one");
        assertEquals(sa.length, 1);
        assertEquals(sa[0], "one");

        sa = BookUtil.tokenize("One, !Two-er THREE-er?");
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "One, ");
        assertEquals(sa[1], "!Two-er ");
        assertEquals(sa[2], "THREE-er?");

        sa = BookUtil.tokenize("One, !Two-er THREE--four?");
        assertEquals(sa.length, 4);
        assertEquals(sa[0], "One, ");
        assertEquals(sa[1], "!Two-er ");
        assertEquals(sa[2], "THREE--");
        assertEquals(sa[3], "four?");
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
        assertEquals(BookUtil.stripPunctuationWord("abcde"), "abcde");
        assertEquals(BookUtil.stripPunctuationWord("a---e"), "a---e");
        assertEquals(BookUtil.stripPunctuationWord("a'''e"), "a'''e");
        assertEquals(BookUtil.stripPunctuationWord("a'e-e"), "a'e-e");
        assertEquals(BookUtil.stripPunctuationWord("12345"), "12345");
        assertEquals(BookUtil.stripPunctuationWord("'abcde"), "abcde");
        assertEquals(BookUtil.stripPunctuationWord("'a---e"), "a---e");
        assertEquals(BookUtil.stripPunctuationWord("'a'''e"), "a'''e");
        assertEquals(BookUtil.stripPunctuationWord("'a'e-e"), "a'e-e");
        assertEquals(BookUtil.stripPunctuationWord("'12345"), "12345");
        assertEquals(BookUtil.stripPunctuationWord("'abcde'"), "abcde");
        assertEquals(BookUtil.stripPunctuationWord("'a---e'"), "a---e");
        assertEquals(BookUtil.stripPunctuationWord("'a'''e'"), "a'''e");
        assertEquals(BookUtil.stripPunctuationWord("'a'e-e'"), "a'e-e");
        assertEquals(BookUtil.stripPunctuationWord("'12345'"), "12345");
        assertEquals(BookUtil.stripPunctuationWord("'-abcde--"), "abcde");
        assertEquals(BookUtil.stripPunctuationWord("'-a---e--"), "a---e");
        assertEquals(BookUtil.stripPunctuationWord("'-a'''e--"), "a'''e");
        assertEquals(BookUtil.stripPunctuationWord("'-a'e-e--"), "a'e-e");
        assertEquals(BookUtil.stripPunctuationWord("'-12345--"), "12345");
        assertEquals(BookUtil.stripPunctuationWord("$'-abcde-'*"), "abcde");
        assertEquals(BookUtil.stripPunctuationWord("$'-a---e-'*"), "a---e");
        assertEquals(BookUtil.stripPunctuationWord("$'-a'''e-'*"), "a'''e");
        assertEquals(BookUtil.stripPunctuationWord("$'-a'e-e-'*"), "a'e-e");
        assertEquals(BookUtil.stripPunctuationWord("$'-12345-'*"), "12345");
        assertEquals(BookUtil.stripPunctuationWord("`'-abcde-'["), "abcde");
        assertEquals(BookUtil.stripPunctuationWord("`'-a---e-'["), "a---e");
        assertEquals(BookUtil.stripPunctuationWord("`'-a'''e-'["), "a'''e");
        assertEquals(BookUtil.stripPunctuationWord("`'-a'e-e-'["), "a'e-e");
        assertEquals(BookUtil.stripPunctuationWord("`'-12345-'["), "12345");
        assertEquals(BookUtil.stripPunctuationWord("#'-abcde-'}"), "abcde");
        assertEquals(BookUtil.stripPunctuationWord("#'-a---e-'}"), "a---e");
        assertEquals(BookUtil.stripPunctuationWord("#'-a'''e-'}"), "a'''e");
        assertEquals(BookUtil.stripPunctuationWord("#'-a'e-e-'}"), "a'e-e");
        assertEquals(BookUtil.stripPunctuationWord("#'-12345-'}"), "12345");
        assertEquals(BookUtil.stripPunctuationWord("£'-abcde-'/"), "abcde");
        assertEquals(BookUtil.stripPunctuationWord("£'-a---e-'/"), "a---e");
        assertEquals(BookUtil.stripPunctuationWord("£'-a'''e-'/"), "a'''e");
        assertEquals(BookUtil.stripPunctuationWord("£'-a'e-e-'/"), "a'e-e");
        assertEquals(BookUtil.stripPunctuationWord("£'-12345-'/"), "12345");

        sa = BookUtil.stripPunctuation(BookUtil.tokenize("aaaa"));
        assertEquals(sa.length, 1);
        assertEquals(sa[0], "aaaa");
        sa = BookUtil.stripPunctuation(BookUtil.tokenize("aaaa bbbb"));
        assertEquals(sa.length, 2);
        assertEquals(sa[0], "aaaa");
        assertEquals(sa[1], "bbbb");
        sa = BookUtil.stripPunctuation(BookUtil.tokenize("One Two Three"));
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "One");
        assertEquals(sa[1], "Two");
        assertEquals(sa[2], "Three");
        sa = BookUtil.stripPunctuation(BookUtil.tokenize(" One  Two  Three "));
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "One");
        assertEquals(sa[1], "Two");
        assertEquals(sa[2], "Three");
        sa = BookUtil.stripPunctuation(BookUtil.tokenize(" 'One's' ,Two? !Three-Four\" "));
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "One's");
        assertEquals(sa[1], "Two");
        assertEquals(sa[2], "Three-Four");
        sa = BookUtil.stripPunctuation(BookUtil.tokenize(" 'One's' ,Two? !Three-- Four\" "));
        assertEquals(sa.length, 4);
        assertEquals(sa[0], "One's");
        assertEquals(sa[1], "Two");
        assertEquals(sa[2], "Three");
        assertEquals(sa[3], "Four");
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
        sa = BookUtil.stripWords(StringUtils.split(" 'One's' ,Two? !Three-Four\" "));
        assertEquals(sa.length, 4);
        assertEquals(sa[0], "'");
        assertEquals(sa[1], "',");
        assertEquals(sa[2], "?!");
        assertEquals(sa[3], "\"");

        sa = BookUtil.stripWords(StringUtils.split(" 'One's' ,Two? !Three-- Four\" "));
        assertEquals(sa.length, 5);
        assertEquals(sa[0], "'");
        assertEquals(sa[1], "',");
        assertEquals(sa[2], "?!");
        assertEquals(sa[3], "--");
        assertEquals(sa[4], "\"");

        sa = BookUtil.stripWords(BookUtil.tokenize("'One's' ,Two? !Three--Four\""));
        assertEquals(sa.length, 5);
        assertEquals(sa[0], "'");
        assertEquals(sa[1], "' ,");
        assertEquals(sa[2], "? !");
        assertEquals(sa[3], "--");
        assertEquals(sa[4], "\"");
    }

    public void testStripWordsStringString() throws Exception
    {
        // String s = BookUtil.stripWords(first, last);
    }

    public void testGetCases() throws Exception
    {
        int[] ia = BookUtil.getCases(new String[] { "abc" });
        assertEquals(ia[0], BibleInfo.CASE_LOWER);
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

    public void testStripWords()
    {
        assertEquals(BookUtil.stripWords("one", "two"), "");
        assertEquals(BookUtil.stripWords("one,", "two"), ",");
        assertEquals(BookUtil.stripWords("one'", "two"), "'");
        assertEquals(BookUtil.stripWords("one-", "two"), "-");
        assertEquals(BookUtil.stripWords("one#", "two"), "#");
        assertEquals(BookUtil.stripWords("one", ",two"), ",");
        assertEquals(BookUtil.stripWords("one", "'two"), "'");
        assertEquals(BookUtil.stripWords("one", "-two"), "-");
        assertEquals(BookUtil.stripWords("one", "#two"), "#");
        assertEquals(BookUtil.stripWords("one-", "-two"), "--");
        assertEquals(BookUtil.stripWords("-one-", "-two-"), "--");
        assertEquals(BookUtil.stripWords("one-world", "two"), "");
        assertEquals(BookUtil.stripWords("one-world'", "two"), "'");
        assertEquals(BookUtil.stripWords("one ", "two"), " ");
        assertEquals(BookUtil.stripWords("one, ", "two"), ", ");
        assertEquals(BookUtil.stripWords("one' ", "two"), "' ");
        assertEquals(BookUtil.stripWords("one- ", "two"), "- ");
        assertEquals(BookUtil.stripWords("one# ", "two"), "# ");
        assertEquals(BookUtil.stripWords("one", " ,two"), " ,");
        assertEquals(BookUtil.stripWords("one", " 'two"), " '");
        assertEquals(BookUtil.stripWords("one", " -two"), " -");
        assertEquals(BookUtil.stripWords("one" , "#two"), "#");
        assertEquals(BookUtil.stripWords("one- ", "-two"), "- -");
        assertEquals(BookUtil.stripWords("-one- ", "-two-"), "- -");
        assertEquals(BookUtil.stripWords("one-world ", "two"), " ");
        assertEquals(BookUtil.stripWords("one-world'", " two"), "' ");
    }
}
