package org.crosswire.jsword.book;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;

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

        sa = SentanceUtil.tokenize("one! \"*(two-three"); //$NON-NLS-1$
        assertEquals(sa.length, 2);
        assertEquals(sa[0], "one! "); //$NON-NLS-1$
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
        assertEquals(CaseType.getCase("FRED"), CaseType.UPPER); //$NON-NLS-1$
        assertEquals(CaseType.getCase("F-ED"), CaseType.UPPER); //$NON-NLS-1$
        assertEquals(CaseType.getCase("F00D"), CaseType.UPPER); //$NON-NLS-1$
        assertEquals(CaseType.getCase("fred"), CaseType.LOWER); //$NON-NLS-1$
        assertEquals(CaseType.getCase("f-ed"), CaseType.LOWER); //$NON-NLS-1$
        assertEquals(CaseType.getCase("f00d"), CaseType.LOWER); //$NON-NLS-1$
        assertEquals(CaseType.getCase("Fred"), CaseType.SENTENCE); //$NON-NLS-1$
        assertEquals(CaseType.getCase("F-ed"), CaseType.SENTENCE); //$NON-NLS-1$
        assertEquals(CaseType.getCase("F00d"), CaseType.SENTENCE); //$NON-NLS-1$
        assertEquals(CaseType.getCase("fRED"), CaseType.MIXED); //$NON-NLS-1$
        assertEquals(CaseType.getCase("frED"), CaseType.MIXED); //$NON-NLS-1$
        assertEquals(CaseType.getCase("freD"), CaseType.MIXED); //$NON-NLS-1$
        assertEquals(CaseType.getCase("LORD's"), CaseType.MIXED); //$NON-NLS-1$
        assertEquals(CaseType.getCase(""), CaseType.LOWER); //$NON-NLS-1$
        // The results of this are undefined so
        // assertEquals(PassageUtil.getCase("FreD"), CaseType.SENTENCE);
    }

    public void testSetCase() throws Exception
    {
        assertEquals(CaseType.UPPER.setCase("FRED"), "FRED"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.UPPER.setCase("Fred"), "FRED"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.UPPER.setCase("fred"), "FRED"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.UPPER.setCase("frED"), "FRED"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.UPPER.setCase("fr00"), "FR00"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.UPPER.setCase("fr=_"), "FR=_"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.LOWER.setCase("FRED"), "fred"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.LOWER.setCase("Fred"), "fred"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.LOWER.setCase("fred"), "fred"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.LOWER.setCase("frED"), "fred"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.LOWER.setCase("fr00"), "fr00"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.LOWER.setCase("fr=_"), "fr=_"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.SENTENCE.setCase("FRED"), "Fred"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.SENTENCE.setCase("Fred"), "Fred"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.SENTENCE.setCase("fred"), "Fred"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.SENTENCE.setCase("frED"), "Fred"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.SENTENCE.setCase("fr00"), "Fr00"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.SENTENCE.setCase("fr=_"), "Fr=_"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.MIXED.setCase("lord's"), "LORD's"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.MIXED.setCase("LORD's"), "LORD's"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.LOWER.setCase("no-one"), "no-one"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.UPPER.setCase("no-one"), "NO-ONE"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.SENTENCE.setCase("no-one"), "No-one"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.LOWER.setCase("xx-one"), "xx-one"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.UPPER.setCase("xx-one"), "XX-ONE"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.SENTENCE.setCase("xx-one"), "Xx-One"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.SENTENCE.setCase("god-inspired"), "God-inspired"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.SENTENCE.setCase("god-breathed"), "God-breathed"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.SENTENCE.setCase("maher-shalal-hash-baz"), "Maher-Shalal-Hash-Baz"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.LOWER.setCase(""), ""); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.UPPER.setCase(""), ""); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.SENTENCE.setCase(""), ""); //$NON-NLS-1$ //$NON-NLS-2$
        try { CaseType.MIXED.setCase("god-inspired"); fail(); } //$NON-NLS-1$
        catch (IllegalArgumentException ex) { }
    }

    public void testToSentenceCase() throws Exception
    {
        assertEquals(CaseType.toSentenceCase("one"), "One"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.toSentenceCase("one two"), "One two"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.toSentenceCase("ONE"), "One"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.toSentenceCase("ONE TWO"), "One two"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.toSentenceCase("onE"), "One"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.toSentenceCase("onE twO"), "One two"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.toSentenceCase("12345"), "12345"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.toSentenceCase("1 two"), "1 two"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(CaseType.toSentenceCase("1 TWO"), "1 two"); //$NON-NLS-1$ //$NON-NLS-2$
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
        sa = SentanceUtil.stripPunctuation(new String[] { " 'One's' " , "Two?",  "!Three-Four\" " }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
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
        assertEquals(SentanceUtil.stripPunctuationWord("%'-abcde-'/"), "abcde"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("%'-a---e-'/"), "a---e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("%'-a'''e-'/"), "a'''e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("%'-a'e-e-'/"), "a'e-e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentanceUtil.stripPunctuationWord("%'-12345-'/"), "12345"); //$NON-NLS-1$ //$NON-NLS-2$

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
        assertEquals(SentanceUtil.stripPunctuationWord("!\"%$test"), "test"); //$NON-NLS-1$ //$NON-NLS-2$
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
        assertEquals(SentanceUtil.firstLetter(" \"%abcde"), 3); //$NON-NLS-1$
        assertEquals(SentanceUtil.firstLetter(" \"%abcde--!   "), 3); //$NON-NLS-1$
    }

    public void testLastLetter() throws Exception
    {
        assertEquals(SentanceUtil.lastLetter("abcde"), 4); //$NON-NLS-1$
        assertEquals(SentanceUtil.lastLetter("abcde "), 4); //$NON-NLS-1$
        assertEquals(SentanceUtil.lastLetter("abcde\" "), 4); //$NON-NLS-1$
        assertEquals(SentanceUtil.lastLetter("abcde\"%$ "), 4); //$NON-NLS-1$
        assertEquals(SentanceUtil.lastLetter(" abcde"), 5); //$NON-NLS-1$
        assertEquals(SentanceUtil.lastLetter(" abcde "), 5); //$NON-NLS-1$
        assertEquals(SentanceUtil.lastLetter(" abcde\" "), 5); //$NON-NLS-1$
        assertEquals(SentanceUtil.lastLetter(" abcde\"%$ "), 5); //$NON-NLS-1$
        assertEquals(SentanceUtil.lastLetter(" abcde--\"%$ "), 5); //$NON-NLS-1$
        assertEquals(SentanceUtil.lastLetter(" abcde\"%$-- "), 5); //$NON-NLS-1$
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
