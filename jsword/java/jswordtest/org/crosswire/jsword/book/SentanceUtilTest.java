/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.book;

import junit.framework.TestCase;

import org.crosswire.common.util.StringUtil;

/**
 * .
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
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

        sa = SentenceUtil.tokenize("one two three"); //$NON-NLS-1$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "one "); //$NON-NLS-1$
        assertEquals(sa[1], "two "); //$NON-NLS-1$
        assertEquals(sa[2], "three"); //$NON-NLS-1$

        sa = SentenceUtil.tokenize("!one  two three "); //$NON-NLS-1$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "!one "); //$NON-NLS-1$
        assertEquals(sa[1], "two "); //$NON-NLS-1$
        assertEquals(sa[2], "three "); //$NON-NLS-1$

        sa = SentenceUtil.tokenize("\"one-- two three "); //$NON-NLS-1$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "\"one-- "); //$NON-NLS-1$
        assertEquals(sa[1], "two "); //$NON-NLS-1$
        assertEquals(sa[2], "three "); //$NON-NLS-1$

        sa = SentenceUtil.tokenize("-one--two three "); //$NON-NLS-1$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "-one--"); //$NON-NLS-1$
        assertEquals(sa[1], "two "); //$NON-NLS-1$
        assertEquals(sa[2], "three "); //$NON-NLS-1$

        sa = SentenceUtil.tokenize("one-two--three "); //$NON-NLS-1$
        assertEquals(sa.length, 2);
        assertEquals(sa[0], "one-two--"); //$NON-NLS-1$
        assertEquals(sa[1], "three "); //$NON-NLS-1$

        sa = SentenceUtil.tokenize("one! \"*(two-three"); //$NON-NLS-1$
        assertEquals(sa.length, 2);
        assertEquals(sa[0], "one! "); //$NON-NLS-1$
        assertEquals(sa[1], "\"*(two-three"); //$NON-NLS-1$

        // moved from TestRawBible
        sa = SentenceUtil.tokenize("one two three"); //$NON-NLS-1$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "one "); //$NON-NLS-1$
        assertEquals(sa[1], "two "); //$NON-NLS-1$
        assertEquals(sa[2], "three"); //$NON-NLS-1$

        sa = SentenceUtil.tokenize("one"); //$NON-NLS-1$
        assertEquals(sa.length, 1);
        assertEquals(sa[0], "one"); //$NON-NLS-1$

        sa = SentenceUtil.tokenize("One, !Two-er THREE-er?"); //$NON-NLS-1$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "One, "); //$NON-NLS-1$
        assertEquals(sa[1], "!Two-er "); //$NON-NLS-1$
        assertEquals(sa[2], "THREE-er?"); //$NON-NLS-1$

        sa = SentenceUtil.tokenize("One, !Two-er THREE--four?"); //$NON-NLS-1$
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

        sa = SentenceUtil.stripPunctuation(new String[] { "aaaa" }); //$NON-NLS-1$
        assertEquals(sa.length, 1);
        assertEquals(sa[0], "aaaa"); //$NON-NLS-1$
        sa = SentenceUtil.stripPunctuation(new String[] { "aaaa", "bbbb" }); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(sa.length, 2);
        assertEquals(sa[0], "aaaa"); //$NON-NLS-1$
        assertEquals(sa[1], "bbbb"); //$NON-NLS-1$
        sa = SentenceUtil.stripPunctuation(new String[] { "One", "Two", "Three" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "One"); //$NON-NLS-1$
        assertEquals(sa[1], "Two"); //$NON-NLS-1$
        assertEquals(sa[2], "Three"); //$NON-NLS-1$
        sa = SentenceUtil.stripPunctuation(new String[] { " One ", " Two ", " Three " }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "One"); //$NON-NLS-1$
        assertEquals(sa[1], "Two"); //$NON-NLS-1$
        assertEquals(sa[2], "Three"); //$NON-NLS-1$
        sa = SentenceUtil.stripPunctuation(new String[] { " 'One's' " , "Two?",  "!Three-Four\" " }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "One's"); //$NON-NLS-1$
        assertEquals(sa[1], "Two"); //$NON-NLS-1$
        assertEquals(sa[2], "Three-Four"); //$NON-NLS-1$
        sa = SentenceUtil.stripPunctuation(new String[] { " 'One's' ", " ,Two? ", " !Three-- ", " Four\" " }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        assertEquals(sa.length, 4);
        assertEquals(sa[0], "One's"); //$NON-NLS-1$
        assertEquals(sa[1], "Two"); //$NON-NLS-1$
        assertEquals(sa[2], "Three"); //$NON-NLS-1$
        assertEquals(sa[3], "Four"); //$NON-NLS-1$
    }

    public void testStripWordsStringArray() throws Exception
    {
        String[] sa;

        sa = SentenceUtil.stripWords(StringUtil.split(" 'One's' ,Two? !Three-Four\" ")); //$NON-NLS-1$
        assertEquals(sa.length, 4);
        assertEquals(sa[0], "'"); //$NON-NLS-1$
        assertEquals(sa[1], "',"); //$NON-NLS-1$
        assertEquals(sa[2], "?!"); //$NON-NLS-1$
        assertEquals(sa[3], "\""); //$NON-NLS-1$

        sa = SentenceUtil.stripWords(StringUtil.split(" 'One's' ,Two? !Three-- Four\" ")); //$NON-NLS-1$
        assertEquals(sa.length, 5);
        assertEquals(sa[0], "'"); //$NON-NLS-1$
        assertEquals(sa[1], "',"); //$NON-NLS-1$
        assertEquals(sa[2], "?!"); //$NON-NLS-1$
        assertEquals(sa[3], "--"); //$NON-NLS-1$
        assertEquals(sa[4], "\""); //$NON-NLS-1$

        sa = SentenceUtil.stripWords(new String[] { "'One's'", " ,Two? ", "!Three--", "Four\"" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        assertEquals(sa.length, 5);
        assertEquals(sa[0], "'"); //$NON-NLS-1$
        assertEquals(sa[1], "' ,"); //$NON-NLS-1$
        assertEquals(sa[2], "? !"); //$NON-NLS-1$
        assertEquals(sa[3], "--"); //$NON-NLS-1$
        assertEquals(sa[4], "\""); //$NON-NLS-1$
    }

    public void testUpdatePassageTally()
    {
        // SentenceUtil.updatePassageTally(version, tally, words);
    }

    public void testUpdatePassageTallyFlat() throws Exception
    {
        // SentenceUtil.updatePassageTallyFlat(version, tally, words);
    }

    public void testGetPassage() throws Exception
    {
        // Passage ref = SentenceUtil.getPassage(version, words);
    }

    public void testIsNewPara() throws Exception
    {
        // boolean b = SentenceUtil.isNewPara(doc);
    }

    public void testGetWords() throws Exception
    {
        String[] sa;

        sa = SentenceUtil.getWords("One Two three"); //$NON-NLS-1$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "one"); //$NON-NLS-1$
        assertEquals(sa[1], "two"); //$NON-NLS-1$
        assertEquals(sa[2], "three"); //$NON-NLS-1$

        sa = SentenceUtil.getWords("!one  two three "); //$NON-NLS-1$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "one"); //$NON-NLS-1$
        assertEquals(sa[1], "two"); //$NON-NLS-1$
        assertEquals(sa[2], "three"); //$NON-NLS-1$

        sa = SentenceUtil.getWords("\"one-- two three "); //$NON-NLS-1$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "one"); //$NON-NLS-1$
        assertEquals(sa[1], "two"); //$NON-NLS-1$
        assertEquals(sa[2], "three"); //$NON-NLS-1$

        sa = SentenceUtil.getWords("-one--two three "); //$NON-NLS-1$
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "one"); //$NON-NLS-1$
        assertEquals(sa[1], "two"); //$NON-NLS-1$
        assertEquals(sa[2], "three"); //$NON-NLS-1$

        sa = SentenceUtil.getWords("one-two--three "); //$NON-NLS-1$
        assertEquals(sa.length, 2);
        assertEquals(sa[0], "one-two"); //$NON-NLS-1$
        assertEquals(sa[1], "three"); //$NON-NLS-1$

        sa = SentenceUtil.getWords("one! \"(two-three"); //$NON-NLS-1$
        assertEquals(sa.length, 2);
        assertEquals(sa[0], "one"); //$NON-NLS-1$
        assertEquals(sa[1], "two-three"); //$NON-NLS-1$
    }

    public void testStripPunctuationWord() throws Exception
    {
        assertEquals(SentenceUtil.stripPunctuationWord("abcde"), "abcde"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("a---e"), "a---e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("a'''e"), "a'''e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("a'e-e"), "a'e-e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("12345"), "12345"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("'abcde"), "abcde"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("'a---e"), "a---e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("'a'''e"), "a'''e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("'a'e-e"), "a'e-e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("'12345"), "12345"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("'abcde'"), "abcde"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("'a---e'"), "a---e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("'a'''e'"), "a'''e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("'a'e-e'"), "a'e-e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("'12345'"), "12345"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("'-abcde--"), "abcde"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("'-a---e--"), "a---e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("'-a'''e--"), "a'''e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("'-a'e-e--"), "a'e-e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("'-12345--"), "12345"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("$'-abcde-'*"), "abcde"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("$'-a---e-'*"), "a---e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("$'-a'''e-'*"), "a'''e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("$'-a'e-e-'*"), "a'e-e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("$'-12345-'*"), "12345"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("`'-abcde-'["), "abcde"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("`'-a---e-'["), "a---e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("`'-a'''e-'["), "a'''e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("`'-a'e-e-'["), "a'e-e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("`'-12345-'["), "12345"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("#'-abcde-'}"), "abcde"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("#'-a---e-'}"), "a---e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("#'-a'''e-'}"), "a'''e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("#'-a'e-e-'}"), "a'e-e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("#'-12345-'}"), "12345"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("%'-abcde-'/"), "abcde"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("%'-a---e-'/"), "a---e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("%'-a'''e-'/"), "a'''e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("%'-a'e-e-'/"), "a'e-e"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("%'-12345-'/"), "12345"); //$NON-NLS-1$ //$NON-NLS-2$

        assertEquals(SentenceUtil.stripPunctuationWord("test"), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord(" test"), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("test-- "), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("test! "), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("test\" "), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("test... "), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("test's"), "test's"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("test's "), "test's"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("test's!"), "test's"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("test's?"), "test's"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("test!?;;'#\""), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("!\"%$test"), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("   test "), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("--test "), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("'test "), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord("/?test "), "test"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SentenceUtil.stripPunctuationWord(" $%^\" test %^&"), "test"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testStripWordsStringString() throws Exception
    {
        // String s = SentenceUtil.stripWords(first, last);
    }

    public void testFirstLetter() throws Exception
    {
        assertEquals(SentenceUtil.firstLetter("abcde"), 0); //$NON-NLS-1$
        assertEquals(SentenceUtil.firstLetter(" abcde"), 1); //$NON-NLS-1$
        assertEquals(SentenceUtil.firstLetter(" \"%abcde"), 3); //$NON-NLS-1$
        assertEquals(SentenceUtil.firstLetter(" \"%abcde--!   "), 3); //$NON-NLS-1$
    }

    public void testLastLetter() throws Exception
    {
        assertEquals(SentenceUtil.lastLetter("abcde"), 4); //$NON-NLS-1$
        assertEquals(SentenceUtil.lastLetter("abcde "), 4); //$NON-NLS-1$
        assertEquals(SentenceUtil.lastLetter("abcde\" "), 4); //$NON-NLS-1$
        assertEquals(SentenceUtil.lastLetter("abcde\"%$ "), 4); //$NON-NLS-1$
        assertEquals(SentenceUtil.lastLetter(" abcde"), 5); //$NON-NLS-1$
        assertEquals(SentenceUtil.lastLetter(" abcde "), 5); //$NON-NLS-1$
        assertEquals(SentenceUtil.lastLetter(" abcde\" "), 5); //$NON-NLS-1$
        assertEquals(SentenceUtil.lastLetter(" abcde\"%$ "), 5); //$NON-NLS-1$
        assertEquals(SentenceUtil.lastLetter(" abcde--\"%$ "), 5); //$NON-NLS-1$
        assertEquals(SentenceUtil.lastLetter(" abcde\"%$-- "), 5); //$NON-NLS-1$
    }

    public void testStripWords()
    {
        assertEquals(SentenceUtil.stripWords("one", "two"), ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentenceUtil.stripWords("one,", "two"), ","); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentenceUtil.stripWords("one'", "two"), "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentenceUtil.stripWords("one-", "two"), "-"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentenceUtil.stripWords("one#", "two"), "#"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentenceUtil.stripWords("one", ",two"), ","); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentenceUtil.stripWords("one", "'two"), "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentenceUtil.stripWords("one", "-two"), "-"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentenceUtil.stripWords("one", "#two"), "#"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentenceUtil.stripWords("one-", "-two"), "--"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentenceUtil.stripWords("-one-", "-two-"), "--"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentenceUtil.stripWords("one-world", "two"), ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentenceUtil.stripWords("one-world'", "two"), "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentenceUtil.stripWords("one ", "two"), " "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentenceUtil.stripWords("one, ", "two"), ", "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentenceUtil.stripWords("one' ", "two"), "' "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentenceUtil.stripWords("one- ", "two"), "- "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentenceUtil.stripWords("one# ", "two"), "# "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentenceUtil.stripWords("one", " ,two"), " ,"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentenceUtil.stripWords("one", " 'two"), " '"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentenceUtil.stripWords("one", " -two"), " -"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentenceUtil.stripWords("one" , "#two"), "#"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentenceUtil.stripWords("one- ", "-two"), "- -"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentenceUtil.stripWords("-one- ", "-two-"), "- -"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentenceUtil.stripWords("one-world ", "two"), " "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(SentenceUtil.stripWords("one-world'", " two"), "' "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
}
