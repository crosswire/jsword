
package org.crosswire.jsword.book;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;
import org.crosswire.jsword.passage.PassageConstants;

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
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class BookUtilTest extends TestCase
{
    public BookUtilTest(String s)
    {
        super(s);
    }

    String[] sa = BookUtil.tokenize("one two three"); //$NON-NLS-1$

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
        assertEquals(sa[0], "one "); //$NON-NLS-1$
        assertEquals(sa[1], "two "); //$NON-NLS-1$
        assertEquals(sa[2], "three"); //$NON-NLS-1$

        sa = BookUtil.tokenize("!one  two three "); //$NON-NLS-1$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "!one "); //$NON-NLS-1$
        assertEquals(sa[1], "two "); //$NON-NLS-1$
        assertEquals(sa[2], "three "); //$NON-NLS-1$

        sa = BookUtil.tokenize("\"one-- two three "); //$NON-NLS-1$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "\"one-- "); //$NON-NLS-1$
        assertEquals(sa[1], "two "); //$NON-NLS-1$
        assertEquals(sa[2], "three "); //$NON-NLS-1$

        sa = BookUtil.tokenize("-one--two three "); //$NON-NLS-1$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "-one--"); //$NON-NLS-1$
        assertEquals(sa[1], "two "); //$NON-NLS-1$
        assertEquals(sa[2], "three "); //$NON-NLS-1$

        sa = BookUtil.tokenize("one-two--three "); //$NON-NLS-1$
        assertEquals(sa.length, 2);
        assertEquals(sa[0], "one-two--"); //$NON-NLS-1$
        assertEquals(sa[1], "three "); //$NON-NLS-1$

        sa = BookUtil.tokenize("one!£ \"*(two-three"); //$NON-NLS-1$
        assertEquals(sa.length, 2);
        assertEquals(sa[0], "one!£ "); //$NON-NLS-1$
        assertEquals(sa[1], "\"*(two-three"); //$NON-NLS-1$

        // moved from TestRawBible
        sa = BookUtil.tokenize("one two three"); //$NON-NLS-1$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "one "); //$NON-NLS-1$
        assertEquals(sa[1], "two "); //$NON-NLS-1$
        assertEquals(sa[2], "three"); //$NON-NLS-1$

        sa = BookUtil.tokenize("one"); //$NON-NLS-1$
        assertEquals(sa.length, 1);
        assertEquals(sa[0], "one"); //$NON-NLS-1$

        sa = BookUtil.tokenize("One, !Two-er THREE-er?"); //$NON-NLS-1$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "One, "); //$NON-NLS-1$
        assertEquals(sa[1], "!Two-er "); //$NON-NLS-1$
        assertEquals(sa[2], "THREE-er?"); //$NON-NLS-1$

        sa = BookUtil.tokenize("One, !Two-er THREE--four?"); //$NON-NLS-1$
        assertEquals(sa.length, 4);
        assertEquals(sa[0], "One, "); //$NON-NLS-1$
        assertEquals(sa[1], "!Two-er "); //$NON-NLS-1$
        assertEquals(sa[2], "THREE--"); //$NON-NLS-1$
        assertEquals(sa[3], "four?"); //$NON-NLS-1$
    }

    public void testGetWords() throws Exception
    {
        sa = BookUtil.getWords("One Two three"); //$NON-NLS-1$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "one"); //$NON-NLS-1$
        assertEquals(sa[1], "two"); //$NON-NLS-1$
        assertEquals(sa[2], "three"); //$NON-NLS-1$

        sa = BookUtil.getWords("!one  two three "); //$NON-NLS-1$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "one"); //$NON-NLS-1$
        assertEquals(sa[1], "two"); //$NON-NLS-1$
        assertEquals(sa[2], "three"); //$NON-NLS-1$

        sa = BookUtil.getWords("\"one-- two three "); //$NON-NLS-1$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "one"); //$NON-NLS-1$
        assertEquals(sa[1], "two"); //$NON-NLS-1$
        assertEquals(sa[2], "three"); //$NON-NLS-1$

        sa = BookUtil.getWords("-one--two three "); //$NON-NLS-1$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "one"); //$NON-NLS-1$
        assertEquals(sa[1], "two"); //$NON-NLS-1$
        assertEquals(sa[2], "three"); //$NON-NLS-1$

        sa = BookUtil.getWords("one-two--three "); //$NON-NLS-1$
        assertEquals(sa.length, 2);
        assertEquals(sa[0], "one-two"); //$NON-NLS-1$
        assertEquals(sa[1], "three"); //$NON-NLS-1$

        sa = BookUtil.getWords("one! \"(two-three"); //$NON-NLS-1$
        assertEquals(sa.length, 2);
        assertEquals(sa[0], "one"); //$NON-NLS-1$
        assertEquals(sa[1], "two-three"); //$NON-NLS-1$
    }

    public void testStripPunctuation() throws Exception
    {
        assertEquals(BookUtil.stripPunctuationWord("abcde"), "abcde"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("a---e"), "a---e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("a'''e"), "a'''e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("a'e-e"), "a'e-e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("12345"), "12345"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("'abcde"), "abcde"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("'a---e"), "a---e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("'a'''e"), "a'''e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("'a'e-e"), "a'e-e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("'12345"), "12345"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("'abcde'"), "abcde"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("'a---e'"), "a---e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("'a'''e'"), "a'''e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("'a'e-e'"), "a'e-e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("'12345'"), "12345"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("'-abcde--"), "abcde"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("'-a---e--"), "a---e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("'-a'''e--"), "a'''e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("'-a'e-e--"), "a'e-e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("'-12345--"), "12345"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("$'-abcde-'*"), "abcde"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("$'-a---e-'*"), "a---e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("$'-a'''e-'*"), "a'''e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("$'-a'e-e-'*"), "a'e-e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("$'-12345-'*"), "12345"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("`'-abcde-'["), "abcde"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("`'-a---e-'["), "a---e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("`'-a'''e-'["), "a'''e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("`'-a'e-e-'["), "a'e-e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("`'-12345-'["), "12345"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("#'-abcde-'}"), "abcde"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("#'-a---e-'}"), "a---e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("#'-a'''e-'}"), "a'''e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("#'-a'e-e-'}"), "a'e-e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("#'-12345-'}"), "12345"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("£'-abcde-'/"), "abcde"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("£'-a---e-'/"), "a---e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("£'-a'''e-'/"), "a'''e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("£'-a'e-e-'/"), "a'e-e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("£'-12345-'/"), "12345"); //$NON-NLS-1$ //$NON-NLS-2$

        sa = BookUtil.stripPunctuation(BookUtil.tokenize("aaaa")); //$NON-NLS-1$
        assertEquals(sa.length, 1);
        assertEquals(sa[0], "aaaa"); //$NON-NLS-1$
        sa = BookUtil.stripPunctuation(BookUtil.tokenize("aaaa bbbb")); //$NON-NLS-1$
        assertEquals(sa.length, 2);
        assertEquals(sa[0], "aaaa"); //$NON-NLS-1$
        assertEquals(sa[1], "bbbb"); //$NON-NLS-1$
        sa = BookUtil.stripPunctuation(BookUtil.tokenize("One Two Three")); //$NON-NLS-1$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "One"); //$NON-NLS-1$
        assertEquals(sa[1], "Two"); //$NON-NLS-1$
        assertEquals(sa[2], "Three"); //$NON-NLS-1$
        sa = BookUtil.stripPunctuation(BookUtil.tokenize(" One  Two  Three ")); //$NON-NLS-1$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "One"); //$NON-NLS-1$
        assertEquals(sa[1], "Two"); //$NON-NLS-1$
        assertEquals(sa[2], "Three"); //$NON-NLS-1$
        sa = BookUtil.stripPunctuation(BookUtil.tokenize(" 'One's' ,Two? !Three-Four\" ")); //$NON-NLS-1$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "One's"); //$NON-NLS-1$
        assertEquals(sa[1], "Two"); //$NON-NLS-1$
        assertEquals(sa[2], "Three-Four"); //$NON-NLS-1$
        sa = BookUtil.stripPunctuation(BookUtil.tokenize(" 'One's' ,Two? !Three-- Four\" ")); //$NON-NLS-1$
        assertEquals(sa.length, 4);
        assertEquals(sa[0], "One's"); //$NON-NLS-1$
        assertEquals(sa[1], "Two"); //$NON-NLS-1$
        assertEquals(sa[2], "Three"); //$NON-NLS-1$
        assertEquals(sa[3], "Four"); //$NON-NLS-1$
    }

    public void testStripPunctuationWord() throws Exception
    {
        assertEquals(BookUtil.stripPunctuationWord("test"), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord(" test"), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("test-- "), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("test! "), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("test\" "), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("test... "), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("test's"), "test's"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("test's "), "test's"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("test's!"), "test's"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("test's?"), "test's"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("test!?;;'#\""), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("!\"£$test"), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("   test "), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("--test "), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("'test "), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord("/?test "), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BookUtil.stripPunctuationWord(" $%^\" test %^&"), "test"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testStripWordsStringArray() throws Exception
    {
        sa = BookUtil.stripWords(StringUtils.split(" 'One's' ,Two? !Three-Four\" ")); //$NON-NLS-1$
        assertEquals(sa.length, 4);
        assertEquals(sa[0], "'"); //$NON-NLS-1$
        assertEquals(sa[1], "',"); //$NON-NLS-1$
        assertEquals(sa[2], "?!"); //$NON-NLS-1$
        assertEquals(sa[3], "\""); //$NON-NLS-1$

        sa = BookUtil.stripWords(StringUtils.split(" 'One's' ,Two? !Three-- Four\" ")); //$NON-NLS-1$
        assertEquals(sa.length, 5);
        assertEquals(sa[0], "'"); //$NON-NLS-1$
        assertEquals(sa[1], "',"); //$NON-NLS-1$
        assertEquals(sa[2], "?!"); //$NON-NLS-1$
        assertEquals(sa[3], "--"); //$NON-NLS-1$
        assertEquals(sa[4], "\""); //$NON-NLS-1$

        sa = BookUtil.stripWords(BookUtil.tokenize("'One's' ,Two? !Three--Four\"")); //$NON-NLS-1$
        assertEquals(sa.length, 5);
        assertEquals(sa[0], "'"); //$NON-NLS-1$
        assertEquals(sa[1], "' ,"); //$NON-NLS-1$
        assertEquals(sa[2], "? !"); //$NON-NLS-1$
        assertEquals(sa[3], "--"); //$NON-NLS-1$
        assertEquals(sa[4], "\""); //$NON-NLS-1$
    }

    public void testStripWordsStringString() throws Exception
    {
        // String s = BookUtil.stripWords(first, last);
    }

    public void testGetCases() throws Exception
    {
        int[] ia = BookUtil.getCases(new String[] { "abc" }); //$NON-NLS-1$
        assertEquals(ia[0], PassageConstants.CASE_LOWER);
    }

    public void testFirstLetter() throws Exception
    {
        assertEquals(BookUtil.firstLetter("abcde"), 0); //$NON-NLS-1$
        assertEquals(BookUtil.firstLetter(" abcde"), 1); //$NON-NLS-1$
        assertEquals(BookUtil.firstLetter(" \"£abcde"), 3); //$NON-NLS-1$
        assertEquals(BookUtil.firstLetter(" \"£abcde--!   "), 3); //$NON-NLS-1$
    }

    public void testLastLetter() throws Exception
    {
        assertEquals(BookUtil.lastLetter("abcde"), 4); //$NON-NLS-1$
        assertEquals(BookUtil.lastLetter("abcde "), 4); //$NON-NLS-1$
        assertEquals(BookUtil.lastLetter("abcde\" "), 4); //$NON-NLS-1$
        assertEquals(BookUtil.lastLetter("abcde\"£$ "), 4); //$NON-NLS-1$
        assertEquals(BookUtil.lastLetter(" abcde"), 5); //$NON-NLS-1$
        assertEquals(BookUtil.lastLetter(" abcde "), 5); //$NON-NLS-1$
        assertEquals(BookUtil.lastLetter(" abcde\" "), 5); //$NON-NLS-1$
        assertEquals(BookUtil.lastLetter(" abcde\"£$ "), 5); //$NON-NLS-1$
        assertEquals(BookUtil.lastLetter(" abcde--\"£$ "), 5); //$NON-NLS-1$
        assertEquals(BookUtil.lastLetter(" abcde\"£$-- "), 5); //$NON-NLS-1$
    }

    public void testStripWords()
    {
        assertEquals(BookUtil.stripWords("one", "two"), ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(BookUtil.stripWords("one,", "two"), ","); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(BookUtil.stripWords("one'", "two"), "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(BookUtil.stripWords("one-", "two"), "-"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(BookUtil.stripWords("one#", "two"), "#"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(BookUtil.stripWords("one", ",two"), ","); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(BookUtil.stripWords("one", "'two"), "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(BookUtil.stripWords("one", "-two"), "-"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(BookUtil.stripWords("one", "#two"), "#"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(BookUtil.stripWords("one-", "-two"), "--"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(BookUtil.stripWords("-one-", "-two-"), "--"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(BookUtil.stripWords("one-world", "two"), ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(BookUtil.stripWords("one-world'", "two"), "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(BookUtil.stripWords("one ", "two"), " "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(BookUtil.stripWords("one, ", "two"), ", "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(BookUtil.stripWords("one' ", "two"), "' "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(BookUtil.stripWords("one- ", "two"), "- "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(BookUtil.stripWords("one# ", "two"), "# "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(BookUtil.stripWords("one", " ,two"), " ,"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(BookUtil.stripWords("one", " 'two"), " '"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(BookUtil.stripWords("one", " -two"), " -"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(BookUtil.stripWords("one" , "#two"), "#"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(BookUtil.stripWords("one- ", "-two"), "- -"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(BookUtil.stripWords("-one- ", "-two-"), "- -"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(BookUtil.stripWords("one-world ", "two"), " "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(BookUtil.stripWords("one-world'", " two"), "' "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
}
