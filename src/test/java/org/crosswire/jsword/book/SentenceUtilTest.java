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
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.jsword.book;

import org.crosswire.common.util.StringUtil;
import org.junit.Assert;
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
        Assert.assertEquals(3, sa.length);
        Assert.assertEquals("one ", sa[0]);
        Assert.assertEquals("two ", sa[1]);
        Assert.assertEquals("three", sa[2]);

        sa = SentenceUtil.tokenize("!one  two three ");
        Assert.assertEquals(3, sa.length);
        Assert.assertEquals("!one ", sa[0]);
        Assert.assertEquals("two ", sa[1]);
        Assert.assertEquals("three ", sa[2]);

        sa = SentenceUtil.tokenize("\"one-- two three ");
        Assert.assertEquals(3, sa.length);
        Assert.assertEquals("\"one-- ", sa[0]);
        Assert.assertEquals("two ", sa[1]);
        Assert.assertEquals("three ", sa[2]);

        sa = SentenceUtil.tokenize("-one--two three ");
        Assert.assertEquals(sa.length, 3);
        Assert.assertEquals("-one--", sa[0]);
        Assert.assertEquals("two ", sa[1]);
        Assert.assertEquals("three ", sa[2]);

        sa = SentenceUtil.tokenize("one-two--three ");
        Assert.assertEquals(2, sa.length);
        Assert.assertEquals("one-two--", sa[0]);
        Assert.assertEquals("three ", sa[1]);

        sa = SentenceUtil.tokenize("one! \"*(two-three");
        Assert.assertEquals(2, sa.length);
        Assert.assertEquals("one! ", sa[0]);
        Assert.assertEquals("\"*(two-three", sa[1]);

        // moved from TestRawBible
        sa = SentenceUtil.tokenize("one two three");
        Assert.assertEquals(3, sa.length);
        Assert.assertEquals("one ", sa[0]);
        Assert.assertEquals("two ", sa[1]);
        Assert.assertEquals("three", sa[2]);

        sa = SentenceUtil.tokenize("one");
        Assert.assertEquals(1, sa.length);
        Assert.assertEquals("one", sa[0]);

        sa = SentenceUtil.tokenize("One, !Two-er THREE-er?");
        Assert.assertEquals(3, sa.length);
        Assert.assertEquals("One, ", sa[0]);
        Assert.assertEquals("!Two-er ", sa[1]);
        Assert.assertEquals("THREE-er?", sa[2]);

        sa = SentenceUtil.tokenize("One, !Two-er THREE--four?");
        Assert.assertEquals(4, sa.length);
        Assert.assertEquals("One, ", sa[0]);
        Assert.assertEquals("!Two-er ", sa[1]);
        Assert.assertEquals("THREE--", sa[2]);
        Assert.assertEquals("four?", sa[3]);
    }

    @Test
    public void testGetCase() {
        Assert.assertEquals(CaseType.UPPER, CaseType.getCase("FRED"));
        Assert.assertEquals(CaseType.UPPER, CaseType.getCase("F-ED"));
        Assert.assertEquals(CaseType.UPPER, CaseType.getCase("F00D"));
        Assert.assertEquals(CaseType.LOWER, CaseType.getCase("fred"));
        Assert.assertEquals(CaseType.LOWER, CaseType.getCase("f-ed"));
        Assert.assertEquals(CaseType.LOWER, CaseType.getCase("f00d"));
        Assert.assertEquals(CaseType.SENTENCE, CaseType.getCase("Fred"));
        Assert.assertEquals(CaseType.SENTENCE, CaseType.getCase("F-ed"));
        Assert.assertEquals(CaseType.SENTENCE, CaseType.getCase("F00d"));
        Assert.assertEquals(CaseType.LOWER, CaseType.getCase(""));
        // The results of this are undefined so
        // Assert.assertEquals(CaseType.SENTENCE, PassageUtil.getCase("FreD"));
    }

    @Test
    public void testSetCase() {
        Assert.assertEquals("FRED", CaseType.UPPER.setCase("FRED"));
        Assert.assertEquals("FRED", CaseType.UPPER.setCase("Fred"));
        Assert.assertEquals("FRED", CaseType.UPPER.setCase("fred"));
        Assert.assertEquals("FRED", CaseType.UPPER.setCase("frED"));
        Assert.assertEquals("FR00", CaseType.UPPER.setCase("fr00"));
        Assert.assertEquals("FR=_", CaseType.UPPER.setCase("fr=_"));
        Assert.assertEquals("fred", CaseType.LOWER.setCase("FRED"));
        Assert.assertEquals("fred", CaseType.LOWER.setCase("Fred"));
        Assert.assertEquals("fred", CaseType.LOWER.setCase("fred"));
        Assert.assertEquals("fred", CaseType.LOWER.setCase("frED"));
        Assert.assertEquals("fr00", CaseType.LOWER.setCase("fr00"));
        Assert.assertEquals("fr=_", CaseType.LOWER.setCase("fr=_"));
        Assert.assertEquals("Fred", CaseType.SENTENCE.setCase("FRED"));
        Assert.assertEquals("Fred", CaseType.SENTENCE.setCase("Fred"));
        Assert.assertEquals("Fred", CaseType.SENTENCE.setCase("fred"));
        Assert.assertEquals("Fred", CaseType.SENTENCE.setCase("frED"));
        Assert.assertEquals("Fr00", CaseType.SENTENCE.setCase("fr00"));
        Assert.assertEquals("Fr=_", CaseType.SENTENCE.setCase("fr=_"));
        Assert.assertEquals("no-one", CaseType.LOWER.setCase("no-one"));
        Assert.assertEquals("NO-ONE", CaseType.UPPER.setCase("no-one"));
        Assert.assertEquals("No-one", CaseType.SENTENCE.setCase("no-one"));
        Assert.assertEquals("xx-one", CaseType.LOWER.setCase("xx-one"));
        Assert.assertEquals("XX-ONE", CaseType.UPPER.setCase("xx-one"));
        Assert.assertEquals("Xx-One", CaseType.SENTENCE.setCase("xx-one"));
        Assert.assertEquals("God-inspired", CaseType.SENTENCE.setCase("god-inspired"));
        Assert.assertEquals("God-breathed", CaseType.SENTENCE.setCase("god-breathed"));
        Assert.assertEquals("Maher-Shalal-Hash-Baz", CaseType.SENTENCE.setCase("maher-shalal-hash-baz"));
        Assert.assertEquals("", CaseType.LOWER.setCase(""));
        Assert.assertEquals("", CaseType.UPPER.setCase(""));
        Assert.assertEquals("", CaseType.SENTENCE.setCase(""));
    }

    @Test
    public void testToSentenceCase() {
        Assert.assertEquals("One", CaseType.toSentenceCase("one"));
        Assert.assertEquals("One two", CaseType.toSentenceCase("one two"));
        Assert.assertEquals("One", CaseType.toSentenceCase("ONE"));
        Assert.assertEquals("One two", CaseType.toSentenceCase("ONE TWO"));
        Assert.assertEquals("One", CaseType.toSentenceCase("onE"));
        Assert.assertEquals("One two", CaseType.toSentenceCase("onE twO"));
        Assert.assertEquals("12345", CaseType.toSentenceCase("12345"));
        Assert.assertEquals("1 two", CaseType.toSentenceCase("1 two"));
        Assert.assertEquals("1 two", CaseType.toSentenceCase("1 TWO"));
    }

    @Test
    public void testStripPunctuation() {
        String[] sa;

        sa = SentenceUtil.stripPunctuation("aaaa");
        Assert.assertEquals(1, sa.length);
        Assert.assertEquals("aaaa", sa[0]);
        sa = SentenceUtil.stripPunctuation("aaaa", "bbbb");
        Assert.assertEquals(2, sa.length);
        Assert.assertEquals("aaaa", sa[0]);
        Assert.assertEquals("bbbb", sa[1]);
        sa = SentenceUtil.stripPunctuation("One", "Two", "Three");
        Assert.assertEquals(3, sa.length);
        Assert.assertEquals("One", sa[0]);
        Assert.assertEquals("Two", sa[1]);
        Assert.assertEquals("Three", sa[2]);
        sa = SentenceUtil.stripPunctuation(" One ", " Two ", " Three ");
        Assert.assertEquals(3, sa.length);
        Assert.assertEquals("One", sa[0]);
        Assert.assertEquals("Two", sa[1]);
        Assert.assertEquals("Three", sa[2]);
        sa = SentenceUtil.stripPunctuation(" 'One's' ", "Two?", "!Three-Four\" ");
        Assert.assertEquals(3, sa.length);
        Assert.assertEquals("One's", sa[0]);
        Assert.assertEquals("Two", sa[1]);
        Assert.assertEquals("Three-Four", sa[2]);
        sa = SentenceUtil.stripPunctuation(" 'One's' ", " ,Two? ", " !Three-- ", " Four\" ");
        Assert.assertEquals(4, sa.length);
        Assert.assertEquals("One's", sa[0]);
        Assert.assertEquals("Two", sa[1]);
        Assert.assertEquals("Three", sa[2]);
        Assert.assertEquals("Four", sa[3]);
    }

    @Test
    public void testStripWordsStringArray() {
        String[] sa;

        sa = SentenceUtil.stripWords(StringUtil.split(" 'One's' ,Two? !Three-Four\" "));
        Assert.assertEquals(4, sa.length, 4);
        Assert.assertEquals("'", sa[0]);
        Assert.assertEquals("',", sa[1]);
        Assert.assertEquals("?!", sa[2]);
        Assert.assertEquals("\"", sa[3]);

        sa = SentenceUtil.stripWords(StringUtil.split(" 'One's' ,Two? !Three-- Four\" "));
        Assert.assertEquals(5, sa.length, 5);
        Assert.assertEquals("'", sa[0]);
        Assert.assertEquals("',", sa[1]);
        Assert.assertEquals("?!", sa[2]);
        Assert.assertEquals("--", sa[3]);
        Assert.assertEquals("\"", sa[4]);

        sa = SentenceUtil.stripWords("'One's'", " ,Two? ", "!Three--", "Four\"");
        Assert.assertEquals(5, sa.length);
        Assert.assertEquals("'", sa[0]);
        Assert.assertEquals("' ,", sa[1]);
        Assert.assertEquals("? !", sa[2]);
        Assert.assertEquals("--", sa[3]);
        Assert.assertEquals("\"", sa[4]);
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
        Assert.assertEquals(3, sa.length);
        Assert.assertEquals("one", sa[0]);
        Assert.assertEquals("two", sa[1]);
        Assert.assertEquals("three", sa[2]);

        sa = SentenceUtil.getWords("!one  two three ");
        Assert.assertEquals(3, sa.length);
        Assert.assertEquals("one", sa[0]);
        Assert.assertEquals("two", sa[1]);
        Assert.assertEquals("three", sa[2]);

        sa = SentenceUtil.getWords("\"one-- two three ");
        Assert.assertEquals(3, sa.length);
        Assert.assertEquals("one", sa[0]);
        Assert.assertEquals("two", sa[1]);
        Assert.assertEquals("three", sa[2]);

        sa = SentenceUtil.getWords("-one--two three ");
        Assert.assertEquals(3, sa.length);
        Assert.assertEquals("one", sa[0]);
        Assert.assertEquals("two", sa[1]);
        Assert.assertEquals("three", sa[2]);

        sa = SentenceUtil.getWords("one-two--three ");
        Assert.assertEquals(2, sa.length);
        Assert.assertEquals("one-two", sa[0]);
        Assert.assertEquals("three", sa[1]);

        sa = SentenceUtil.getWords("one! \"(two-three");
        Assert.assertEquals(2, sa.length);
        Assert.assertEquals("one", sa[0]);
        Assert.assertEquals("two-three", sa[1]);
    }

    @Test
    public void testStripPunctuationWord() {
        Assert.assertEquals("abcde", SentenceUtil.stripPunctuationWord("abcde"));
        Assert.assertEquals("a---e", SentenceUtil.stripPunctuationWord("a---e"));
        Assert.assertEquals("a'''e", SentenceUtil.stripPunctuationWord("a'''e"));
        Assert.assertEquals("a'e-e", SentenceUtil.stripPunctuationWord("a'e-e"));
        Assert.assertEquals("12345", SentenceUtil.stripPunctuationWord("12345"));
        Assert.assertEquals("abcde", SentenceUtil.stripPunctuationWord("'abcde"));
        Assert.assertEquals("a---e", SentenceUtil.stripPunctuationWord("'a---e"));
        Assert.assertEquals("a'''e", SentenceUtil.stripPunctuationWord("'a'''e"));
        Assert.assertEquals("a'e-e", SentenceUtil.stripPunctuationWord("'a'e-e"));
        Assert.assertEquals("12345", SentenceUtil.stripPunctuationWord("'12345"));
        Assert.assertEquals("abcde", SentenceUtil.stripPunctuationWord("'abcde'"));
        Assert.assertEquals("a---e", SentenceUtil.stripPunctuationWord("'a---e'"));
        Assert.assertEquals("a'''e", SentenceUtil.stripPunctuationWord("'a'''e'"));
        Assert.assertEquals("a'e-e", SentenceUtil.stripPunctuationWord("'a'e-e'"));
        Assert.assertEquals("12345", SentenceUtil.stripPunctuationWord("'12345'"));
        Assert.assertEquals("abcde", SentenceUtil.stripPunctuationWord("'-abcde--"));
        Assert.assertEquals("a---e", SentenceUtil.stripPunctuationWord("'-a---e--"));
        Assert.assertEquals("a'''e", SentenceUtil.stripPunctuationWord("'-a'''e--"));
        Assert.assertEquals("a'e-e", SentenceUtil.stripPunctuationWord("'-a'e-e--"));
        Assert.assertEquals("12345", SentenceUtil.stripPunctuationWord("'-12345--"));
        Assert.assertEquals("abcde", SentenceUtil.stripPunctuationWord("$'-abcde-'*"));
        Assert.assertEquals("a---e", SentenceUtil.stripPunctuationWord("$'-a---e-'*"));
        Assert.assertEquals("a'''e", SentenceUtil.stripPunctuationWord("$'-a'''e-'*"));
        Assert.assertEquals("a'e-e", SentenceUtil.stripPunctuationWord("$'-a'e-e-'*"));
        Assert.assertEquals("12345", SentenceUtil.stripPunctuationWord("$'-12345-'*"));
        Assert.assertEquals("abcde", SentenceUtil.stripPunctuationWord("`'-abcde-'["));
        Assert.assertEquals("a---e", SentenceUtil.stripPunctuationWord("`'-a---e-'["));
        Assert.assertEquals("a'''e", SentenceUtil.stripPunctuationWord("`'-a'''e-'["));
        Assert.assertEquals("a'e-e", SentenceUtil.stripPunctuationWord("`'-a'e-e-'["));
        Assert.assertEquals("12345", SentenceUtil.stripPunctuationWord("`'-12345-'["));
        Assert.assertEquals("abcde", SentenceUtil.stripPunctuationWord("#'-abcde-'}"));
        Assert.assertEquals("a---e", SentenceUtil.stripPunctuationWord("#'-a---e-'}"));
        Assert.assertEquals("a'''e", SentenceUtil.stripPunctuationWord("#'-a'''e-'}"));
        Assert.assertEquals("a'e-e", SentenceUtil.stripPunctuationWord("#'-a'e-e-'}"));
        Assert.assertEquals("12345", SentenceUtil.stripPunctuationWord("#'-12345-'}"));
        Assert.assertEquals("abcde", SentenceUtil.stripPunctuationWord("%'-abcde-'/"));
        Assert.assertEquals("a---e", SentenceUtil.stripPunctuationWord("%'-a---e-'/"));
        Assert.assertEquals("a'''e", SentenceUtil.stripPunctuationWord("%'-a'''e-'/"));
        Assert.assertEquals("a'e-e", SentenceUtil.stripPunctuationWord("%'-a'e-e-'/"));
        Assert.assertEquals("12345", SentenceUtil.stripPunctuationWord("%'-12345-'/"));

        Assert.assertEquals("test", SentenceUtil.stripPunctuationWord("test"));
        Assert.assertEquals("test", SentenceUtil.stripPunctuationWord(" test"));
        Assert.assertEquals("test", SentenceUtil.stripPunctuationWord("test-- "));
        Assert.assertEquals("test", SentenceUtil.stripPunctuationWord("test! "));
        Assert.assertEquals("test", SentenceUtil.stripPunctuationWord("test\" "));
        Assert.assertEquals("test", SentenceUtil.stripPunctuationWord("test... "));
        Assert.assertEquals("test's", SentenceUtil.stripPunctuationWord("test's"));
        Assert.assertEquals("test's", SentenceUtil.stripPunctuationWord("test's "));
        Assert.assertEquals("test's", SentenceUtil.stripPunctuationWord("test's!"));
        Assert.assertEquals("test's", SentenceUtil.stripPunctuationWord("test's?"));
        Assert.assertEquals("test", SentenceUtil.stripPunctuationWord("test!?;;'#\""));
        Assert.assertEquals("test", SentenceUtil.stripPunctuationWord("!\"%$test"));
        Assert.assertEquals("test", SentenceUtil.stripPunctuationWord("   test "));
        Assert.assertEquals("test", SentenceUtil.stripPunctuationWord("--test "));
        Assert.assertEquals("test", SentenceUtil.stripPunctuationWord("'test "));
        Assert.assertEquals("test", SentenceUtil.stripPunctuationWord("/?test "));
        Assert.assertEquals("test", SentenceUtil.stripPunctuationWord(" $%^\" test %^&"));
    }

    @Test
    public void testStripWordsStringString() {
        // String s = SentenceUtil.stripWords(first, last);
    }

    @Test
    public void testFirstLetter() {
        Assert.assertEquals(0, SentenceUtil.firstLetter("abcde"));
        Assert.assertEquals(1, SentenceUtil.firstLetter(" abcde"));
        Assert.assertEquals(3, SentenceUtil.firstLetter(" \"%abcde"));
        Assert.assertEquals(3, SentenceUtil.firstLetter(" \"%abcde--!   "));
    }

    @Test
    public void testLastLetter() {
        Assert.assertEquals(4, SentenceUtil.lastLetter("abcde"));
        Assert.assertEquals(4, SentenceUtil.lastLetter("abcde "));
        Assert.assertEquals(4, SentenceUtil.lastLetter("abcde\" "));
        Assert.assertEquals(4, SentenceUtil.lastLetter("abcde\"%$ "));
        Assert.assertEquals(5, SentenceUtil.lastLetter(" abcde"));
        Assert.assertEquals(5, SentenceUtil.lastLetter(" abcde "));
        Assert.assertEquals(5, SentenceUtil.lastLetter(" abcde\" "));
        Assert.assertEquals(5, SentenceUtil.lastLetter(" abcde\"%$ "));
        Assert.assertEquals(5, SentenceUtil.lastLetter(" abcde--\"%$ "));
        Assert.assertEquals(5, SentenceUtil.lastLetter(" abcde\"%$-- "));
    }

    @Test
    public void testStripWords() {
        Assert.assertEquals("", SentenceUtil.stripWords("one", "two"));
        Assert.assertEquals(",", SentenceUtil.stripWords("one,", "two"));
        Assert.assertEquals("'", SentenceUtil.stripWords("one'", "two"));
        Assert.assertEquals("-", SentenceUtil.stripWords("one-", "two"));
        Assert.assertEquals("#", SentenceUtil.stripWords("one#", "two"));
        Assert.assertEquals(",", SentenceUtil.stripWords("one", ",two"));
        Assert.assertEquals("'", SentenceUtil.stripWords("one", "'two"));
        Assert.assertEquals("-", SentenceUtil.stripWords("one", "-two"));
        Assert.assertEquals("#", SentenceUtil.stripWords("one", "#two"));
        Assert.assertEquals("--", SentenceUtil.stripWords("one-", "-two"));
        Assert.assertEquals("--", SentenceUtil.stripWords("-one-", "-two-"));
        Assert.assertEquals("", SentenceUtil.stripWords("one-world", "two"));
        Assert.assertEquals("'", SentenceUtil.stripWords("one-world'", "two"));
        Assert.assertEquals(" ", SentenceUtil.stripWords("one ", "two"));
        Assert.assertEquals(", ", SentenceUtil.stripWords("one, ", "two"));
        Assert.assertEquals("' ", SentenceUtil.stripWords("one' ", "two"));
        Assert.assertEquals("- ", SentenceUtil.stripWords("one- ", "two"));
        Assert.assertEquals("# ", SentenceUtil.stripWords("one# ", "two"));
        Assert.assertEquals(" ,", SentenceUtil.stripWords("one", " ,two"));
        Assert.assertEquals(" '", SentenceUtil.stripWords("one", " 'two"));
        Assert.assertEquals(" -", SentenceUtil.stripWords("one", " -two"));
        Assert.assertEquals("#", SentenceUtil.stripWords("one", "#two"));
        Assert.assertEquals("- -", SentenceUtil.stripWords("one- ", "-two"));
        Assert.assertEquals("- -", SentenceUtil.stripWords("-one- ", "-two-"));
        Assert.assertEquals(" ", SentenceUtil.stripWords("one-world ", "two"));
        Assert.assertEquals("' ", SentenceUtil.stripWords("one-world'", " two"));
    }
}
