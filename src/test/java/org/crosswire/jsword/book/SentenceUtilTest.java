/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005 - 2014
 *     The copyright to this program is held by its authors.
 *
 */
package org.crosswire.jsword.book;

import static org.junit.Assert.assertEquals;

import org.crosswire.common.util.StringUtil;
import org.junit.Test;

/**
 * JUnit Test
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
 */
public class SentenceUtilTest {

    @Test
    public void testTokenize() {
        String[] sa;

        sa = SentenceUtil.tokenize("one two three");
        assertEquals(3, sa.length);
        assertEquals("one ", sa[0]);
        assertEquals("two ", sa[1]);
        assertEquals("three", sa[2]);

        sa = SentenceUtil.tokenize("!one  two three ");
        assertEquals(3, sa.length);
        assertEquals("!one ", sa[0]);
        assertEquals("two ", sa[1]);
        assertEquals("three ", sa[2]);

        sa = SentenceUtil.tokenize("\"one-- two three ");
        assertEquals(3, sa.length);
        assertEquals("\"one-- ", sa[0]);
        assertEquals("two ", sa[1]);
        assertEquals("three ", sa[2]);

        sa = SentenceUtil.tokenize("-one--two three ");
        assertEquals(sa.length, 3);
        assertEquals("-one--", sa[0]);
        assertEquals("two ", sa[1]);
        assertEquals("three ", sa[2]);

        sa = SentenceUtil.tokenize("one-two--three ");
        assertEquals(2, sa.length);
        assertEquals("one-two--", sa[0]);
        assertEquals("three ", sa[1]);

        sa = SentenceUtil.tokenize("one! \"*(two-three");
        assertEquals(2, sa.length);
        assertEquals("one! ", sa[0]);
        assertEquals("\"*(two-three", sa[1]);

        // moved from TestRawBible
        sa = SentenceUtil.tokenize("one two three");
        assertEquals(3, sa.length);
        assertEquals("one ", sa[0]);
        assertEquals("two ", sa[1]);
        assertEquals("three", sa[2]);

        sa = SentenceUtil.tokenize("one");
        assertEquals(1, sa.length);
        assertEquals("one", sa[0]);

        sa = SentenceUtil.tokenize("One, !Two-er THREE-er?");
        assertEquals(3, sa.length);
        assertEquals("One, ", sa[0]);
        assertEquals("!Two-er ", sa[1]);
        assertEquals("THREE-er?", sa[2]);

        sa = SentenceUtil.tokenize("One, !Two-er THREE--four?");
        assertEquals(4, sa.length);
        assertEquals("One, ", sa[0]);
        assertEquals("!Two-er ", sa[1]);
        assertEquals("THREE--", sa[2]);
        assertEquals("four?", sa[3]);
    }

    @Test
    public void testGetCase() {
        assertEquals(CaseType.UPPER, CaseType.getCase("FRED"));
        assertEquals(CaseType.UPPER, CaseType.getCase("F-ED"));
        assertEquals(CaseType.UPPER, CaseType.getCase("F00D"));
        assertEquals(CaseType.LOWER, CaseType.getCase("fred"));
        assertEquals(CaseType.LOWER, CaseType.getCase("f-ed"));
        assertEquals(CaseType.LOWER, CaseType.getCase("f00d"));
        assertEquals(CaseType.SENTENCE, CaseType.getCase("Fred"));
        assertEquals(CaseType.SENTENCE, CaseType.getCase("F-ed"));
        assertEquals(CaseType.SENTENCE, CaseType.getCase("F00d"));
        assertEquals(CaseType.LOWER, CaseType.getCase(""));
        // The results of this are undefined so
        // assertEquals(CaseType.SENTENCE, PassageUtil.getCase("FreD"));
    }

    @Test
    public void testSetCase() {
        assertEquals("FRED", CaseType.UPPER.setCase("FRED"));
        assertEquals("FRED", CaseType.UPPER.setCase("Fred"));
        assertEquals("FRED", CaseType.UPPER.setCase("fred"));
        assertEquals("FRED", CaseType.UPPER.setCase("frED"));
        assertEquals("FR00", CaseType.UPPER.setCase("fr00"));
        assertEquals("FR=_", CaseType.UPPER.setCase("fr=_"));
        assertEquals("fred", CaseType.LOWER.setCase("FRED"));
        assertEquals("fred", CaseType.LOWER.setCase("Fred"));
        assertEquals("fred", CaseType.LOWER.setCase("fred"));
        assertEquals("fred", CaseType.LOWER.setCase("frED"));
        assertEquals("fr00", CaseType.LOWER.setCase("fr00"));
        assertEquals("fr=_", CaseType.LOWER.setCase("fr=_"));
        assertEquals("Fred", CaseType.SENTENCE.setCase("FRED"));
        assertEquals("Fred", CaseType.SENTENCE.setCase("Fred"));
        assertEquals("Fred", CaseType.SENTENCE.setCase("fred"));
        assertEquals("Fred", CaseType.SENTENCE.setCase("frED"));
        assertEquals("Fr00", CaseType.SENTENCE.setCase("fr00"));
        assertEquals("Fr=_", CaseType.SENTENCE.setCase("fr=_"));
        assertEquals("no-one", CaseType.LOWER.setCase("no-one"));
        assertEquals("NO-ONE", CaseType.UPPER.setCase("no-one"));
        assertEquals("No-one", CaseType.SENTENCE.setCase("no-one"));
        assertEquals("xx-one", CaseType.LOWER.setCase("xx-one"));
        assertEquals("XX-ONE", CaseType.UPPER.setCase("xx-one"));
        assertEquals("Xx-One", CaseType.SENTENCE.setCase("xx-one"));
        assertEquals("God-inspired", CaseType.SENTENCE.setCase("god-inspired"));
        assertEquals("God-breathed", CaseType.SENTENCE.setCase("god-breathed"));
        assertEquals("Maher-Shalal-Hash-Baz", CaseType.SENTENCE.setCase("maher-shalal-hash-baz"));
        assertEquals("", CaseType.LOWER.setCase(""));
        assertEquals("", CaseType.UPPER.setCase(""));
        assertEquals("", CaseType.SENTENCE.setCase(""));
    }

    @Test
    public void testToSentenceCase() {
        assertEquals("One", CaseType.toSentenceCase("one"));
        assertEquals("One two", CaseType.toSentenceCase("one two"));
        assertEquals("One", CaseType.toSentenceCase("ONE"));
        assertEquals("One two", CaseType.toSentenceCase("ONE TWO"));
        assertEquals("One", CaseType.toSentenceCase("onE"));
        assertEquals("One two", CaseType.toSentenceCase("onE twO"));
        assertEquals("12345", CaseType.toSentenceCase("12345"));
        assertEquals("1 two", CaseType.toSentenceCase("1 two"));
        assertEquals("1 two", CaseType.toSentenceCase("1 TWO"));
    }

    @Test
    public void testStripPunctuation() {
        String[] sa;

        sa = SentenceUtil.stripPunctuation("aaaa");
        assertEquals(1, sa.length);
        assertEquals("aaaa", sa[0]);
        sa = SentenceUtil.stripPunctuation("aaaa", "bbbb");
        assertEquals(2, sa.length);
        assertEquals("aaaa", sa[0]);
        assertEquals("bbbb", sa[1]);
        sa = SentenceUtil.stripPunctuation("One", "Two", "Three");
        assertEquals(3, sa.length);
        assertEquals("One", sa[0]);
        assertEquals("Two", sa[1]);
        assertEquals("Three", sa[2]);
        sa = SentenceUtil.stripPunctuation(" One ", " Two ", " Three ");
        assertEquals(3, sa.length);
        assertEquals("One", sa[0]);
        assertEquals("Two", sa[1]);
        assertEquals("Three", sa[2]);
        sa = SentenceUtil.stripPunctuation(" 'One's' ", "Two?", "!Three-Four\" "); 
        assertEquals(3, sa.length);
        assertEquals("One's", sa[0]);
        assertEquals("Two", sa[1]);
        assertEquals("Three-Four", sa[2]);
        sa = SentenceUtil.stripPunctuation(" 'One's' ", " ,Two? ", " !Three-- ", " Four\" ");
        assertEquals(4, sa.length);
        assertEquals("One's", sa[0]);
        assertEquals("Two", sa[1]);
        assertEquals("Three", sa[2]);
        assertEquals("Four", sa[3]);
    }

    @Test
    public void testStripWordsStringArray() {
        String[] sa;

        sa = SentenceUtil.stripWords(StringUtil.split(" 'One's' ,Two? !Three-Four\" "));
        assertEquals(4, sa.length, 4);
        assertEquals("'", sa[0]);
        assertEquals("',", sa[1]);
        assertEquals("?!", sa[2]);
        assertEquals("\"", sa[3]);

        sa = SentenceUtil.stripWords(StringUtil.split(" 'One's' ,Two? !Three-- Four\" "));
        assertEquals(5, sa.length, 5);
        assertEquals("'", sa[0]);
        assertEquals("',", sa[1]);
        assertEquals("?!", sa[2]);
        assertEquals("--", sa[3]);
        assertEquals("\"", sa[4]);

        sa = SentenceUtil.stripWords("'One's'", " ,Two? ", "!Three--", "Four\"");
        assertEquals(5, sa.length);
        assertEquals("'", sa[0]);
        assertEquals("' ,", sa[1]);
        assertEquals("? !", sa[2]);
        assertEquals("--", sa[3]);
        assertEquals("\"", sa[4]);
    }

    @Test
    public void testUpdatePassageTally() {
        // SentenceUtil.updatePassageTally(version, tally, words);
    }

    @Test
    public void testUpdatePassageTallyFlat() {
        // SentenceUtil.updatePassageTallyFlat(version, tally, words);
    }

    @Test
    public void testGetPassage() {
        // Passage ref = SentenceUtil.getPassage(version, words);
    }

    @Test
    public void testIsNewPara() {
        // boolean b = SentenceUtil.isNewPara(doc);
    }

    @Test
    public void testGetWords() {
        String[] sa;

        sa = SentenceUtil.getWords("One Two three");
        assertEquals(3, sa.length);
        assertEquals("one", sa[0]);
        assertEquals("two", sa[1]);
        assertEquals("three", sa[2]);

        sa = SentenceUtil.getWords("!one  two three ");
        assertEquals(3, sa.length);
        assertEquals("one", sa[0]);
        assertEquals("two", sa[1]);
        assertEquals("three", sa[2]);

        sa = SentenceUtil.getWords("\"one-- two three ");
        assertEquals(3, sa.length);
        assertEquals("one", sa[0]);
        assertEquals("two", sa[1]);
        assertEquals("three", sa[2]);

        sa = SentenceUtil.getWords("-one--two three ");
        assertEquals(3, sa.length);
        assertEquals("one", sa[0]);
        assertEquals("two", sa[1]);
        assertEquals("three", sa[2]);

        sa = SentenceUtil.getWords("one-two--three ");
        assertEquals(2, sa.length);
        assertEquals("one-two", sa[0]);
        assertEquals("three", sa[1]);

        sa = SentenceUtil.getWords("one! \"(two-three");
        assertEquals(2, sa.length);
        assertEquals("one", sa[0]);
        assertEquals("two-three", sa[1]);
    }

    @Test
    public void testStripPunctuationWord() {
        assertEquals("abcde", SentenceUtil.stripPunctuationWord("abcde"));
        assertEquals("a---e", SentenceUtil.stripPunctuationWord("a---e"));
        assertEquals("a'''e", SentenceUtil.stripPunctuationWord("a'''e"));
        assertEquals("a'e-e", SentenceUtil.stripPunctuationWord("a'e-e"));
        assertEquals("12345", SentenceUtil.stripPunctuationWord("12345"));
        assertEquals("abcde", SentenceUtil.stripPunctuationWord("'abcde"));
        assertEquals("a---e", SentenceUtil.stripPunctuationWord("'a---e"));
        assertEquals("a'''e", SentenceUtil.stripPunctuationWord("'a'''e"));
        assertEquals("a'e-e", SentenceUtil.stripPunctuationWord("'a'e-e"));
        assertEquals("12345", SentenceUtil.stripPunctuationWord("'12345"));
        assertEquals("abcde", SentenceUtil.stripPunctuationWord("'abcde'"));
        assertEquals("a---e", SentenceUtil.stripPunctuationWord("'a---e'"));
        assertEquals("a'''e", SentenceUtil.stripPunctuationWord("'a'''e'"));
        assertEquals("a'e-e", SentenceUtil.stripPunctuationWord("'a'e-e'"));
        assertEquals("12345", SentenceUtil.stripPunctuationWord("'12345'"));
        assertEquals("abcde", SentenceUtil.stripPunctuationWord("'-abcde--"));
        assertEquals("a---e", SentenceUtil.stripPunctuationWord("'-a---e--"));
        assertEquals("a'''e", SentenceUtil.stripPunctuationWord("'-a'''e--"));
        assertEquals("a'e-e", SentenceUtil.stripPunctuationWord("'-a'e-e--"));
        assertEquals("12345", SentenceUtil.stripPunctuationWord("'-12345--"));
        assertEquals("abcde", SentenceUtil.stripPunctuationWord("$'-abcde-'*"));
        assertEquals("a---e", SentenceUtil.stripPunctuationWord("$'-a---e-'*"));
        assertEquals("a'''e", SentenceUtil.stripPunctuationWord("$'-a'''e-'*"));
        assertEquals("a'e-e", SentenceUtil.stripPunctuationWord("$'-a'e-e-'*"));
        assertEquals("12345", SentenceUtil.stripPunctuationWord("$'-12345-'*"));
        assertEquals("abcde", SentenceUtil.stripPunctuationWord("`'-abcde-'["));
        assertEquals("a---e", SentenceUtil.stripPunctuationWord("`'-a---e-'["));
        assertEquals("a'''e", SentenceUtil.stripPunctuationWord("`'-a'''e-'["));
        assertEquals("a'e-e", SentenceUtil.stripPunctuationWord("`'-a'e-e-'["));
        assertEquals("12345", SentenceUtil.stripPunctuationWord("`'-12345-'["));
        assertEquals("abcde", SentenceUtil.stripPunctuationWord("#'-abcde-'}"));
        assertEquals("a---e", SentenceUtil.stripPunctuationWord("#'-a---e-'}"));
        assertEquals("a'''e", SentenceUtil.stripPunctuationWord("#'-a'''e-'}"));
        assertEquals("a'e-e", SentenceUtil.stripPunctuationWord("#'-a'e-e-'}"));
        assertEquals("12345", SentenceUtil.stripPunctuationWord("#'-12345-'}"));
        assertEquals("abcde", SentenceUtil.stripPunctuationWord("%'-abcde-'/"));
        assertEquals("a---e", SentenceUtil.stripPunctuationWord("%'-a---e-'/"));
        assertEquals("a'''e", SentenceUtil.stripPunctuationWord("%'-a'''e-'/"));
        assertEquals("a'e-e", SentenceUtil.stripPunctuationWord("%'-a'e-e-'/"));
        assertEquals("12345", SentenceUtil.stripPunctuationWord("%'-12345-'/"));

        assertEquals("test", SentenceUtil.stripPunctuationWord("test"));
        assertEquals("test", SentenceUtil.stripPunctuationWord(" test"));
        assertEquals("test", SentenceUtil.stripPunctuationWord("test-- "));
        assertEquals("test", SentenceUtil.stripPunctuationWord("test! "));
        assertEquals("test", SentenceUtil.stripPunctuationWord("test\" "));
        assertEquals("test", SentenceUtil.stripPunctuationWord("test... "));
        assertEquals("test's", SentenceUtil.stripPunctuationWord("test's"));
        assertEquals("test's", SentenceUtil.stripPunctuationWord("test's "));
        assertEquals("test's", SentenceUtil.stripPunctuationWord("test's!"));
        assertEquals("test's", SentenceUtil.stripPunctuationWord("test's?"));
        assertEquals("test", SentenceUtil.stripPunctuationWord("test!?;;'#\""));
        assertEquals("test", SentenceUtil.stripPunctuationWord("!\"%$test"));
        assertEquals("test", SentenceUtil.stripPunctuationWord("   test "));
        assertEquals("test", SentenceUtil.stripPunctuationWord("--test "));
        assertEquals("test", SentenceUtil.stripPunctuationWord("'test "));
        assertEquals("test", SentenceUtil.stripPunctuationWord("/?test "));
        assertEquals("test", SentenceUtil.stripPunctuationWord(" $%^\" test %^&"));
    }

    @Test
    public void testStripWordsStringString() {
        // String s = SentenceUtil.stripWords(first, last);
    }

    @Test
    public void testFirstLetter() {
        assertEquals(0, SentenceUtil.firstLetter("abcde"));
        assertEquals(1, SentenceUtil.firstLetter(" abcde"));
        assertEquals(3, SentenceUtil.firstLetter(" \"%abcde"));
        assertEquals(3, SentenceUtil.firstLetter(" \"%abcde--!   "));
    }

    @Test
    public void testLastLetter() {
        assertEquals(4, SentenceUtil.lastLetter("abcde"));
        assertEquals(4, SentenceUtil.lastLetter("abcde "));
        assertEquals(4, SentenceUtil.lastLetter("abcde\" "));
        assertEquals(4, SentenceUtil.lastLetter("abcde\"%$ "));
        assertEquals(5, SentenceUtil.lastLetter(" abcde"));
        assertEquals(5, SentenceUtil.lastLetter(" abcde "));
        assertEquals(5, SentenceUtil.lastLetter(" abcde\" "));
        assertEquals(5, SentenceUtil.lastLetter(" abcde\"%$ "));
        assertEquals(5, SentenceUtil.lastLetter(" abcde--\"%$ "));
        assertEquals(5, SentenceUtil.lastLetter(" abcde\"%$-- "));
    }

    @Test
    public void testStripWords() {
        assertEquals("", SentenceUtil.stripWords("one", "two"));
        assertEquals(",", SentenceUtil.stripWords("one,", "two"));
        assertEquals("'", SentenceUtil.stripWords("one'", "two"));
        assertEquals("-", SentenceUtil.stripWords("one-", "two"));
        assertEquals("#", SentenceUtil.stripWords("one#", "two"));
        assertEquals(",", SentenceUtil.stripWords("one", ",two"));
        assertEquals("'", SentenceUtil.stripWords("one", "'two"));
        assertEquals("-", SentenceUtil.stripWords("one", "-two"));
        assertEquals("#", SentenceUtil.stripWords("one", "#two"));
        assertEquals("--", SentenceUtil.stripWords("one-", "-two"));
        assertEquals("--", SentenceUtil.stripWords("-one-", "-two-"));
        assertEquals("", SentenceUtil.stripWords("one-world", "two"));
        assertEquals("'", SentenceUtil.stripWords("one-world'", "two"));
        assertEquals(" ", SentenceUtil.stripWords("one ", "two"));
        assertEquals(", ", SentenceUtil.stripWords("one, ", "two"));
        assertEquals("' ", SentenceUtil.stripWords("one' ", "two"));
        assertEquals("- ", SentenceUtil.stripWords("one- ", "two"));
        assertEquals("# ", SentenceUtil.stripWords("one# ", "two"));
        assertEquals(" ,", SentenceUtil.stripWords("one", " ,two"));
        assertEquals(" '", SentenceUtil.stripWords("one", " 'two"));
        assertEquals(" -", SentenceUtil.stripWords("one", " -two"));
        assertEquals("#", SentenceUtil.stripWords("one", "#two"));
        assertEquals("- -", SentenceUtil.stripWords("one- ", "-two"));
        assertEquals("- -", SentenceUtil.stripWords("-one- ", "-two-"));
        assertEquals(" ", SentenceUtil.stripWords("one-world ", "two"));
        assertEquals("' ", SentenceUtil.stripWords("one-world'", " two"));
    }
}
