package org.crosswire.jsword.book;

import org.apache.commons.lang.StringUtils;
import org.crosswire.jsword.book.SentanceUtil;
import org.crosswire.jsword.passage.PassageConstants;

import junit.framework.TestCase;

/**
 * .
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
public class SentanceUtilTest extends TestCase
{
    public SentanceUtilTest(String s)
    {
        super(s);
    }

    protected void setUp()
    {
    }

    protected void tearDown()
    {
    }

    public void testTokenize() throws Exception
    {
        String[] sa;

        sa = SentanceUtil.tokenize("one two three"); //$NON-NLS-1$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "one "); //$NON-NLS-1$
        assertEquals(sa[1], "two "); //$NON-NLS-1$
        assertEquals(sa[2], "three"); //$NON-NLS-1$

        sa = SentanceUtil.tokenize("!one  two three "); //$NON-NLS-1$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "!one "); //$NON-NLS-1$
        assertEquals(sa[1], "two "); //$NON-NLS-1$
        assertEquals(sa[2], "three "); //$NON-NLS-1$

        sa = SentanceUtil.tokenize("\"one-- two three "); //$NON-NLS-1$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "\"one-- "); //$NON-NLS-1$
        assertEquals(sa[1], "two "); //$NON-NLS-1$
        assertEquals(sa[2], "three "); //$NON-NLS-1$

        sa = SentanceUtil.tokenize("-one--two three "); //$NON-NLS-1$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "-one--"); //$NON-NLS-1$
        assertEquals(sa[1], "two "); //$NON-NLS-1$
        assertEquals(sa[2], "three "); //$NON-NLS-1$

        sa = SentanceUtil.tokenize("one-two--three "); //$NON-NLS-1$
        assertEquals(sa.length, 2);
        assertEquals(sa[0], "one-two--"); //$NON-NLS-1$
        assertEquals(sa[1], "three "); //$NON-NLS-1$

        sa = SentanceUtil.tokenize("one!£ \"*(two-three"); //$NON-NLS-1$
        assertEquals(sa.length, 2);
        assertEquals(sa[0], "one!£ "); //$NON-NLS-1$
        assertEquals(sa[1], "\"*(two-three"); //$NON-NLS-1$

        // moved from TestRawBible
        sa = SentanceUtil.tokenize("one two three"); //$NON-NLS-1$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "one "); //$NON-NLS-1$
        assertEquals(sa[1], "two "); //$NON-NLS-1$
        assertEquals(sa[2], "three"); //$NON-NLS-1$

        sa = SentanceUtil.tokenize("one"); //$NON-NLS-1$
        assertEquals(sa.length, 1);
        assertEquals(sa[0], "one"); //$NON-NLS-1$

        sa = SentanceUtil.tokenize("One, !Two-er THREE-er?"); //$NON-NLS-1$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "One, "); //$NON-NLS-1$
        assertEquals(sa[1], "!Two-er "); //$NON-NLS-1$
        assertEquals(sa[2], "THREE-er?"); //$NON-NLS-1$

        sa = SentanceUtil.tokenize("One, !Two-er THREE--four?"); //$NON-NLS-1$
        assertEquals(sa.length, 4);
        assertEquals(sa[0], "One, "); //$NON-NLS-1$
        assertEquals(sa[1], "!Two-er "); //$NON-NLS-1$
        assertEquals(sa[2], "THREE--"); //$NON-NLS-1$
        assertEquals(sa[3], "four?"); //$NON-NLS-1$
    }

    public void testGetCase() throws Exception
    {
        assertEquals(SentanceUtil.getCase("FRED"), PassageConstants.CASE_UPPER); //$NON-NLS-1$
        assertEquals(SentanceUtil.getCase("F-ED"), PassageConstants.CASE_UPPER); //$NON-NLS-1$
        assertEquals(SentanceUtil.getCase("F00D"), PassageConstants.CASE_UPPER); //$NON-NLS-1$
        assertEquals(SentanceUtil.getCase("fred"), PassageConstants.CASE_LOWER); //$NON-NLS-1$
        assertEquals(SentanceUtil.getCase("f-ed"), PassageConstants.CASE_LOWER); //$NON-NLS-1$
        assertEquals(SentanceUtil.getCase("f00d"), PassageConstants.CASE_LOWER); //$NON-NLS-1$
        assertEquals(SentanceUtil.getCase("Fred"), PassageConstants.CASE_SENTANCE); //$NON-NLS-1$
        assertEquals(SentanceUtil.getCase("F-ed"), PassageConstants.CASE_SENTANCE); //$NON-NLS-1$
        assertEquals(SentanceUtil.getCase("F00d"), PassageConstants.CASE_SENTANCE); //$NON-NLS-1$
        assertEquals(SentanceUtil.getCase("fRED"), PassageConstants.CASE_MIXED); //$NON-NLS-1$
        assertEquals(SentanceUtil.getCase("frED"), PassageConstants.CASE_MIXED); //$NON-NLS-1$
        assertEquals(SentanceUtil.getCase("freD"), PassageConstants.CASE_MIXED); //$NON-NLS-1$
        assertEquals(SentanceUtil.getCase("LORD's"), PassageConstants.CASE_MIXED); //$NON-NLS-1$
        assertEquals(SentanceUtil.getCase(""), PassageConstants.CASE_LOWER); //$NON-NLS-1$
        try { SentanceUtil.getCase(null); fail(); }
        catch (NullPointerException ex) { }
        // The results of this are undefined so
        // assertEquals(PassageUtil.getCase("FreD"), PassageConstants.CASE_SENTANCE);
    }

    public void testSetCase() throws Exception
    {
        assertEquals(SentanceUtil.setCase("FRED", PassageConstants.CASE_UPPER), "FRED"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.setCase("Fred", PassageConstants.CASE_UPPER), "FRED"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.setCase("fred", PassageConstants.CASE_UPPER), "FRED"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.setCase("frED", PassageConstants.CASE_UPPER), "FRED"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.setCase("fr00", PassageConstants.CASE_UPPER), "FR00"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.setCase("fr=_", PassageConstants.CASE_UPPER), "FR=_"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.setCase("FRED", PassageConstants.CASE_LOWER), "fred"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.setCase("Fred", PassageConstants.CASE_LOWER), "fred"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.setCase("fred", PassageConstants.CASE_LOWER), "fred"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.setCase("frED", PassageConstants.CASE_LOWER), "fred"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.setCase("fr00", PassageConstants.CASE_LOWER), "fr00"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.setCase("fr=_", PassageConstants.CASE_LOWER), "fr=_"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.setCase("FRED", PassageConstants.CASE_SENTANCE), "Fred"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.setCase("Fred", PassageConstants.CASE_SENTANCE), "Fred"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.setCase("fred", PassageConstants.CASE_SENTANCE), "Fred"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.setCase("frED", PassageConstants.CASE_SENTANCE), "Fred"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.setCase("fr00", PassageConstants.CASE_SENTANCE), "Fr00"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.setCase("fr=_", PassageConstants.CASE_SENTANCE), "Fr=_"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.setCase("lord's", PassageConstants.CASE_MIXED), "LORD's"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.setCase("LORD's", PassageConstants.CASE_MIXED), "LORD's"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.setCase("no-one", PassageConstants.CASE_LOWER), "no-one"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.setCase("no-one", PassageConstants.CASE_UPPER), "NO-ONE"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.setCase("no-one", PassageConstants.CASE_SENTANCE), "No-one"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.setCase("xx-one", PassageConstants.CASE_LOWER), "xx-one"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.setCase("xx-one", PassageConstants.CASE_UPPER), "XX-ONE"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.setCase("xx-one", PassageConstants.CASE_SENTANCE), "Xx-One"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.setCase("god-inspired", PassageConstants.CASE_SENTANCE), "God-inspired"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.setCase("god-breathed", PassageConstants.CASE_SENTANCE), "God-breathed"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.setCase("maher-shalal-hash-baz", PassageConstants.CASE_SENTANCE), "Maher-Shalal-Hash-Baz"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.setCase("", PassageConstants.CASE_LOWER), ""); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.setCase("", PassageConstants.CASE_UPPER), ""); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.setCase("", PassageConstants.CASE_SENTANCE), ""); //$NON-NLS-1$ //$NON-NLS-2$
        try { SentanceUtil.setCase("god-inspired", PassageConstants.CASE_MIXED); fail(); } //$NON-NLS-1$
        catch (IllegalArgumentException ex) { }
        try { SentanceUtil.setCase("fred", -1); fail(); } //$NON-NLS-1$
        catch (IllegalArgumentException ex) { }
        try { SentanceUtil.setCase("fred", 4); fail(); } //$NON-NLS-1$
        catch (IllegalArgumentException ex) { }
    }

    public void testToSentenceCase() throws Exception
    {
        assertEquals(SentanceUtil.toSentenceCase("one"), "One"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.toSentenceCase("one two"), "One two"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.toSentenceCase("ONE"), "One"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.toSentenceCase("ONE TWO"), "One two"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.toSentenceCase("onE"), "One"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.toSentenceCase("onE twO"), "One two"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.toSentenceCase("12345"), "12345"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.toSentenceCase("1 two"), "1 two"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.toSentenceCase("1 TWO"), "1 two"); //$NON-NLS-1$ //$NON-NLS-2$
        try { SentanceUtil.toSentenceCase(null); fail(); }
        catch (NullPointerException ex) { }
    }

    public void testGetCases() throws Exception
    {
        int[] ia = SentanceUtil.getCases(new String[] { "abc" }); //$NON-NLS-1$
        assertEquals(ia[0], PassageConstants.CASE_LOWER);
    }

    public void testStripPunctuation() throws Exception
    {
        String[] sa;

        sa = SentanceUtil.stripPunctuation(new String[] { "aaaa" }); //$NON-NLS-1$
        assertEquals(sa.length, 1);
        assertEquals(sa[0], "aaaa"); //$NON-NLS-1$
        sa = SentanceUtil.stripPunctuation(new String[] { "aaaa", "bbbb" }); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(sa.length, 2);
        assertEquals(sa[0], "aaaa"); //$NON-NLS-1$
        assertEquals(sa[1], "bbbb"); //$NON-NLS-1$
        sa = SentanceUtil.stripPunctuation(new String[] { "One", "Two", "Three" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "One"); //$NON-NLS-1$
        assertEquals(sa[1], "Two"); //$NON-NLS-1$
        assertEquals(sa[2], "Three"); //$NON-NLS-1$
        sa = SentanceUtil.stripPunctuation(new String[] { " One ", " Two ", " Three " }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "One"); //$NON-NLS-1$
        assertEquals(sa[1], "Two"); //$NON-NLS-1$
        assertEquals(sa[2], "Three"); //$NON-NLS-1$
        sa = SentanceUtil.stripPunctuation(new String[] { " 'One's' ,Two? !Three-Four\" " }); //$NON-NLS-1$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "One's"); //$NON-NLS-1$
        assertEquals(sa[1], "Two"); //$NON-NLS-1$
        assertEquals(sa[2], "Three-Four"); //$NON-NLS-1$
        sa = SentanceUtil.stripPunctuation(new String[] { " 'One's' ", " ,Two? ", " !Three-- ", " Four\" " }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        assertEquals(sa.length, 4);
        assertEquals(sa[0], "One's"); //$NON-NLS-1$
        assertEquals(sa[1], "Two"); //$NON-NLS-1$
        assertEquals(sa[2], "Three"); //$NON-NLS-1$
        assertEquals(sa[3], "Four"); //$NON-NLS-1$
    }

    public void testStripWordsStringArray() throws Exception
    {
        String[] sa;

        sa = SentanceUtil.stripWords(StringUtils.split(" 'One's' ,Two? !Three-Four\" ")); //$NON-NLS-1$
        assertEquals(sa.length, 4);
        assertEquals(sa[0], "'"); //$NON-NLS-1$
        assertEquals(sa[1], "',"); //$NON-NLS-1$
        assertEquals(sa[2], "?!"); //$NON-NLS-1$
        assertEquals(sa[3], "\""); //$NON-NLS-1$

        sa = SentanceUtil.stripWords(StringUtils.split(" 'One's' ,Two? !Three-- Four\" ")); //$NON-NLS-1$
        assertEquals(sa.length, 5);
        assertEquals(sa[0], "'"); //$NON-NLS-1$
        assertEquals(sa[1], "',"); //$NON-NLS-1$
        assertEquals(sa[2], "?!"); //$NON-NLS-1$
        assertEquals(sa[3], "--"); //$NON-NLS-1$
        assertEquals(sa[4], "\""); //$NON-NLS-1$

        sa = SentanceUtil.stripWords(new String[] { "'One's'", " ,Two? ", "!Three--", "Four\"" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        assertEquals(sa.length, 5);
        assertEquals(sa[0], "'"); //$NON-NLS-1$
        assertEquals(sa[1], "' ,"); //$NON-NLS-1$
        assertEquals(sa[2], "? !"); //$NON-NLS-1$
        assertEquals(sa[3], "--"); //$NON-NLS-1$
        assertEquals(sa[4], "\""); //$NON-NLS-1$
    }

    public void testUpdatePassageTally()
    {
        // SentanceUtil.updatePassageTally(version, tally, words);
    }

    public void testUpdatePassageTallyFlat() throws Exception
    {
        // SentanceUtil.updatePassageTallyFlat(version, tally, words);
    }

    public void testGetPassage() throws Exception
    {
        // Passage ref = SentanceUtil.getPassage(version, words);
    }

    public void testIsNewPara() throws Exception
    {
        // boolean b = SentanceUtil.isNewPara(doc);
    }

    public void testGetWords() throws Exception
    {
        String[] sa;

        sa = SentanceUtil.getWords("One Two three"); //$NON-NLS-1$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "one"); //$NON-NLS-1$
        assertEquals(sa[1], "two"); //$NON-NLS-1$
        assertEquals(sa[2], "three"); //$NON-NLS-1$

        sa = SentanceUtil.getWords("!one  two three "); //$NON-NLS-1$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "one"); //$NON-NLS-1$
        assertEquals(sa[1], "two"); //$NON-NLS-1$
        assertEquals(sa[2], "three"); //$NON-NLS-1$

        sa = SentanceUtil.getWords("\"one-- two three "); //$NON-NLS-1$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "one"); //$NON-NLS-1$
        assertEquals(sa[1], "two"); //$NON-NLS-1$
        assertEquals(sa[2], "three"); //$NON-NLS-1$

        sa = SentanceUtil.getWords("-one--two three "); //$NON-NLS-1$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "one"); //$NON-NLS-1$
        assertEquals(sa[1], "two"); //$NON-NLS-1$
        assertEquals(sa[2], "three"); //$NON-NLS-1$

        sa = SentanceUtil.getWords("one-two--three "); //$NON-NLS-1$
        assertEquals(sa.length, 2);
        assertEquals(sa[0], "one-two"); //$NON-NLS-1$
        assertEquals(sa[1], "three"); //$NON-NLS-1$

        sa = SentanceUtil.getWords("one! \"(two-three"); //$NON-NLS-1$
        assertEquals(sa.length, 2);
        assertEquals(sa[0], "one"); //$NON-NLS-1$
        assertEquals(sa[1], "two-three"); //$NON-NLS-1$
    }

    public void testStripPunctuationWord() throws Exception
    {
        assertEquals(SentanceUtil.stripPunctuationWord("abcde"), "abcde"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("a---e"), "a---e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("a'''e"), "a'''e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("a'e-e"), "a'e-e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("12345"), "12345"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("'abcde"), "abcde"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("'a---e"), "a---e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("'a'''e"), "a'''e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("'a'e-e"), "a'e-e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("'12345"), "12345"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("'abcde'"), "abcde"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("'a---e'"), "a---e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("'a'''e'"), "a'''e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("'a'e-e'"), "a'e-e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("'12345'"), "12345"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("'-abcde--"), "abcde"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("'-a---e--"), "a---e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("'-a'''e--"), "a'''e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("'-a'e-e--"), "a'e-e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("'-12345--"), "12345"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("$'-abcde-'*"), "abcde"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("$'-a---e-'*"), "a---e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("$'-a'''e-'*"), "a'''e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("$'-a'e-e-'*"), "a'e-e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("$'-12345-'*"), "12345"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("`'-abcde-'["), "abcde"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("`'-a---e-'["), "a---e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("`'-a'''e-'["), "a'''e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("`'-a'e-e-'["), "a'e-e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("`'-12345-'["), "12345"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("#'-abcde-'}"), "abcde"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("#'-a---e-'}"), "a---e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("#'-a'''e-'}"), "a'''e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("#'-a'e-e-'}"), "a'e-e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("#'-12345-'}"), "12345"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("£'-abcde-'/"), "abcde"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("£'-a---e-'/"), "a---e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("£'-a'''e-'/"), "a'''e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("£'-a'e-e-'/"), "a'e-e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("£'-12345-'/"), "12345"); //$NON-NLS-1$ //$NON-NLS-2$

        assertEquals(SentanceUtil.stripPunctuationWord("test"), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord(" test"), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("test-- "), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("test! "), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("test\" "), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("test... "), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("test's"), "test's"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("test's "), "test's"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("test's!"), "test's"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("test's?"), "test's"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("test!?;;'#\""), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("!\"£$test"), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("   test "), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("--test "), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("'test "), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("/?test "), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord(" $%^\" test %^&"), "test"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testStripWordsStringString() throws Exception
    {
        // String s = SentanceUtil.stripWords(first, last);
    }

    public void testFirstLetter() throws Exception
    {
        assertEquals(SentanceUtil.firstLetter("abcde"), 0); //$NON-NLS-1$
        assertEquals(SentanceUtil.firstLetter(" abcde"), 1); //$NON-NLS-1$
        assertEquals(SentanceUtil.firstLetter(" \"£abcde"), 3); //$NON-NLS-1$
        assertEquals(SentanceUtil.firstLetter(" \"£abcde--!   "), 3); //$NON-NLS-1$
    }

    public void testLastLetter() throws Exception
    {
        assertEquals(SentanceUtil.lastLetter("abcde"), 4); //$NON-NLS-1$
        assertEquals(SentanceUtil.lastLetter("abcde "), 4); //$NON-NLS-1$
        assertEquals(SentanceUtil.lastLetter("abcde\" "), 4); //$NON-NLS-1$
        assertEquals(SentanceUtil.lastLetter("abcde\"£$ "), 4); //$NON-NLS-1$
        assertEquals(SentanceUtil.lastLetter(" abcde"), 5); //$NON-NLS-1$
        assertEquals(SentanceUtil.lastLetter(" abcde "), 5); //$NON-NLS-1$
        assertEquals(SentanceUtil.lastLetter(" abcde\" "), 5); //$NON-NLS-1$
        assertEquals(SentanceUtil.lastLetter(" abcde\"£$ "), 5); //$NON-NLS-1$
        assertEquals(SentanceUtil.lastLetter(" abcde--\"£$ "), 5); //$NON-NLS-1$
        assertEquals(SentanceUtil.lastLetter(" abcde\"£$-- "), 5); //$NON-NLS-1$
    }

    public void testStripWords()
    {
        assertEquals(SentanceUtil.stripWords("one", "two"), ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentanceUtil.stripWords("one,", "two"), ","); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentanceUtil.stripWords("one'", "two"), "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentanceUtil.stripWords("one-", "two"), "-"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentanceUtil.stripWords("one#", "two"), "#"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentanceUtil.stripWords("one", ",two"), ","); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentanceUtil.stripWords("one", "'two"), "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentanceUtil.stripWords("one", "-two"), "-"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentanceUtil.stripWords("one", "#two"), "#"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentanceUtil.stripWords("one-", "-two"), "--"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentanceUtil.stripWords("-one-", "-two-"), "--"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentanceUtil.stripWords("one-world", "two"), ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentanceUtil.stripWords("one-world'", "two"), "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentanceUtil.stripWords("one ", "two"), " "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentanceUtil.stripWords("one, ", "two"), ", "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentanceUtil.stripWords("one' ", "two"), "' "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentanceUtil.stripWords("one- ", "two"), "- "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentanceUtil.stripWords("one# ", "two"), "# "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentanceUtil.stripWords("one", " ,two"), " ,"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentanceUtil.stripWords("one", " 'two"), " '"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentanceUtil.stripWords("one", " -two"), " -"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentanceUtil.stripWords("one" , "#two"), "#"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentanceUtil.stripWords("one- ", "-two"), "- -"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentanceUtil.stripWords("-one- ", "-two-"), "- -"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentanceUtil.stripWords("one-world ", "two"), " "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentanceUtil.stripWords("one-world'", " two"), "' "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
}
