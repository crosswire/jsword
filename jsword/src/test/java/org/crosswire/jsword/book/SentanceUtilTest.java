/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
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
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class SentanceUtilTest extends TestCase {
    public SentanceUtilTest(String s) {
        super(s);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    /* @Override */
    protected void setUp() throws Exception {
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    /* @Override */
    protected void tearDown() throws Exception {
    }

    public void testTokenize() {
        String[] sa;

        sa = SentenceUtil.tokenize("one two three");
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "one ");
        assertEquals(sa[1], "two ");
        assertEquals(sa[2], "three");

        sa = SentenceUtil.tokenize("!one  two three ");
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "!one ");
        assertEquals(sa[1], "two ");
        assertEquals(sa[2], "three ");

        sa = SentenceUtil.tokenize("\"one-- two three ");
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "\"one-- ");
        assertEquals(sa[1], "two ");
        assertEquals(sa[2], "three ");

        sa = SentenceUtil.tokenize("-one--two three ");
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "-one--");
        assertEquals(sa[1], "two ");
        assertEquals(sa[2], "three ");

        sa = SentenceUtil.tokenize("one-two--three ");
        assertEquals(sa.length, 2);
        assertEquals(sa[0], "one-two--");
        assertEquals(sa[1], "three ");

        sa = SentenceUtil.tokenize("one! \"*(two-three");
        assertEquals(sa.length, 2);
        assertEquals(sa[0], "one! ");
        assertEquals(sa[1], "\"*(two-three");

        // moved from TestRawBible
        sa = SentenceUtil.tokenize("one two three");
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "one ");
        assertEquals(sa[1], "two ");
        assertEquals(sa[2], "three");

        sa = SentenceUtil.tokenize("one");
        assertEquals(sa.length, 1);
        assertEquals(sa[0], "one");

        sa = SentenceUtil.tokenize("One, !Two-er THREE-er?");
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "One, ");
        assertEquals(sa[1], "!Two-er ");
        assertEquals(sa[2], "THREE-er?");

        sa = SentenceUtil.tokenize("One, !Two-er THREE--four?");
        assertEquals(sa.length, 4);
        assertEquals(sa[0], "One, ");
        assertEquals(sa[1], "!Two-er ");
        assertEquals(sa[2], "THREE--");
        assertEquals(sa[3], "four?");
    }

    public void testGetCase() {
        assertEquals(CaseType.getCase("FRED"), CaseType.UPPER);
        assertEquals(CaseType.getCase("F-ED"), CaseType.UPPER);
        assertEquals(CaseType.getCase("F00D"), CaseType.UPPER);
        assertEquals(CaseType.getCase("fred"), CaseType.LOWER);
        assertEquals(CaseType.getCase("f-ed"), CaseType.LOWER);
        assertEquals(CaseType.getCase("f00d"), CaseType.LOWER);
        assertEquals(CaseType.getCase("Fred"), CaseType.SENTENCE);
        assertEquals(CaseType.getCase("F-ed"), CaseType.SENTENCE);
        assertEquals(CaseType.getCase("F00d"), CaseType.SENTENCE);
        assertEquals(CaseType.getCase(""), CaseType.LOWER);
        // The results of this are undefined so
        // assertEquals(PassageUtil.getCase("FreD"), CaseType.SENTENCE);
    }

    public void testSetCase() {
        assertEquals(CaseType.UPPER.setCase("FRED"), "FRED");
        assertEquals(CaseType.UPPER.setCase("Fred"), "FRED");
        assertEquals(CaseType.UPPER.setCase("fred"), "FRED");
        assertEquals(CaseType.UPPER.setCase("frED"), "FRED");
        assertEquals(CaseType.UPPER.setCase("fr00"), "FR00");
        assertEquals(CaseType.UPPER.setCase("fr=_"), "FR=_");
        assertEquals(CaseType.LOWER.setCase("FRED"), "fred");
        assertEquals(CaseType.LOWER.setCase("Fred"), "fred");
        assertEquals(CaseType.LOWER.setCase("fred"), "fred");
        assertEquals(CaseType.LOWER.setCase("frED"), "fred");
        assertEquals(CaseType.LOWER.setCase("fr00"), "fr00");
        assertEquals(CaseType.LOWER.setCase("fr=_"), "fr=_");
        assertEquals(CaseType.SENTENCE.setCase("FRED"), "Fred");
        assertEquals(CaseType.SENTENCE.setCase("Fred"), "Fred");
        assertEquals(CaseType.SENTENCE.setCase("fred"), "Fred");
        assertEquals(CaseType.SENTENCE.setCase("frED"), "Fred");
        assertEquals(CaseType.SENTENCE.setCase("fr00"), "Fr00");
        assertEquals(CaseType.SENTENCE.setCase("fr=_"), "Fr=_");
        assertEquals(CaseType.LOWER.setCase("no-one"), "no-one");
        assertEquals(CaseType.UPPER.setCase("no-one"), "NO-ONE");
        assertEquals(CaseType.SENTENCE.setCase("no-one"), "No-one");
        assertEquals(CaseType.LOWER.setCase("xx-one"), "xx-one");
        assertEquals(CaseType.UPPER.setCase("xx-one"), "XX-ONE");
        assertEquals(CaseType.SENTENCE.setCase("xx-one"), "Xx-One");
        assertEquals(CaseType.SENTENCE.setCase("god-inspired"), "God-inspired");
        assertEquals(CaseType.SENTENCE.setCase("god-breathed"), "God-breathed");
        assertEquals(CaseType.SENTENCE.setCase("maher-shalal-hash-baz"), "Maher-Shalal-Hash-Baz");
        assertEquals(CaseType.LOWER.setCase(""), "");
        assertEquals(CaseType.UPPER.setCase(""), "");
        assertEquals(CaseType.SENTENCE.setCase(""), "");
    }

    public void testToSentenceCase() {
        assertEquals(CaseType.toSentenceCase("one"), "One");
        assertEquals(CaseType.toSentenceCase("one two"), "One two");
        assertEquals(CaseType.toSentenceCase("ONE"), "One");
        assertEquals(CaseType.toSentenceCase("ONE TWO"), "One two");
        assertEquals(CaseType.toSentenceCase("onE"), "One");
        assertEquals(CaseType.toSentenceCase("onE twO"), "One two");
        assertEquals(CaseType.toSentenceCase("12345"), "12345");
        assertEquals(CaseType.toSentenceCase("1 two"), "1 two");
        assertEquals(CaseType.toSentenceCase("1 TWO"), "1 two");
    }

    public void testStripPunctuation() {
        String[] sa;

        sa = SentenceUtil.stripPunctuation(new String[] {
            "aaaa"});
        assertEquals(sa.length, 1);
        assertEquals(sa[0], "aaaa");
        sa = SentenceUtil.stripPunctuation(new String[] {
                "aaaa", "bbbb"});
        assertEquals(sa.length, 2);
        assertEquals(sa[0], "aaaa");
        assertEquals(sa[1], "bbbb");
        sa = SentenceUtil.stripPunctuation(new String[] {
                "One", "Two", "Three"});
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "One");
        assertEquals(sa[1], "Two");
        assertEquals(sa[2], "Three");
        sa = SentenceUtil.stripPunctuation(new String[] {
                " One ", " Two ", " Three "});
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "One");
        assertEquals(sa[1], "Two");
        assertEquals(sa[2], "Three");
        sa = SentenceUtil.stripPunctuation(new String[] {
                " 'One's' ", "Two?", "!Three-Four\" "}); 
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "One's");
        assertEquals(sa[1], "Two");
        assertEquals(sa[2], "Three-Four");
        sa = SentenceUtil.stripPunctuation(new String[] {
                " 'One's' ", " ,Two? ", " !Three-- ", " Four\" "});
        assertEquals(sa.length, 4);
        assertEquals(sa[0], "One's");
        assertEquals(sa[1], "Two");
        assertEquals(sa[2], "Three");
        assertEquals(sa[3], "Four");
    }

    public void testStripWordsStringArray() {
        String[] sa;

        sa = SentenceUtil.stripWords(StringUtil.split(" 'One's' ,Two? !Three-Four\" "));
        assertEquals(sa.length, 4);
        assertEquals(sa[0], "'");
        assertEquals(sa[1], "',");
        assertEquals(sa[2], "?!");
        assertEquals(sa[3], "\"");

        sa = SentenceUtil.stripWords(StringUtil.split(" 'One's' ,Two? !Three-- Four\" "));
        assertEquals(sa.length, 5);
        assertEquals(sa[0], "'");
        assertEquals(sa[1], "',");
        assertEquals(sa[2], "?!");
        assertEquals(sa[3], "--");
        assertEquals(sa[4], "\"");

        sa = SentenceUtil.stripWords(new String[] {
                "'One's'", " ,Two? ", "!Three--", "Four\""});
        assertEquals(sa.length, 5);
        assertEquals(sa[0], "'");
        assertEquals(sa[1], "' ,");
        assertEquals(sa[2], "? !");
        assertEquals(sa[3], "--");
        assertEquals(sa[4], "\"");
    }

    public void testUpdatePassageTally() {
        // SentenceUtil.updatePassageTally(version, tally, words);
    }

    public void testUpdatePassageTallyFlat() {
        // SentenceUtil.updatePassageTallyFlat(version, tally, words);
    }

    public void testGetPassage() {
        // Passage ref = SentenceUtil.getPassage(version, words);
    }

    public void testIsNewPara() {
        // boolean b = SentenceUtil.isNewPara(doc);
    }

    public void testGetWords() {
        String[] sa;

        sa = SentenceUtil.getWords("One Two three");
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "one");
        assertEquals(sa[1], "two");
        assertEquals(sa[2], "three");

        sa = SentenceUtil.getWords("!one  two three ");
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "one");
        assertEquals(sa[1], "two");
        assertEquals(sa[2], "three");

        sa = SentenceUtil.getWords("\"one-- two three ");
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "one");
        assertEquals(sa[1], "two");
        assertEquals(sa[2], "three");

        sa = SentenceUtil.getWords("-one--two three ");
        assertEquals(sa.length, 3);
        assertEquals(sa[0], "one");
        assertEquals(sa[1], "two");
        assertEquals(sa[2], "three");

        sa = SentenceUtil.getWords("one-two--three ");
        assertEquals(sa.length, 2);
        assertEquals(sa[0], "one-two");
        assertEquals(sa[1], "three");

        sa = SentenceUtil.getWords("one! \"(two-three");
        assertEquals(sa.length, 2);
        assertEquals(sa[0], "one");
        assertEquals(sa[1], "two-three");
    }

    public void testStripPunctuationWord() {
        assertEquals(SentenceUtil.stripPunctuationWord("abcde"), "abcde");
        assertEquals(SentenceUtil.stripPunctuationWord("a---e"), "a---e");
        assertEquals(SentenceUtil.stripPunctuationWord("a'''e"), "a'''e");
        assertEquals(SentenceUtil.stripPunctuationWord("a'e-e"), "a'e-e");
        assertEquals(SentenceUtil.stripPunctuationWord("12345"), "12345");
        assertEquals(SentenceUtil.stripPunctuationWord("'abcde"), "abcde");
        assertEquals(SentenceUtil.stripPunctuationWord("'a---e"), "a---e");
        assertEquals(SentenceUtil.stripPunctuationWord("'a'''e"), "a'''e");
        assertEquals(SentenceUtil.stripPunctuationWord("'a'e-e"), "a'e-e");
        assertEquals(SentenceUtil.stripPunctuationWord("'12345"), "12345");
        assertEquals(SentenceUtil.stripPunctuationWord("'abcde'"), "abcde");
        assertEquals(SentenceUtil.stripPunctuationWord("'a---e'"), "a---e");
        assertEquals(SentenceUtil.stripPunctuationWord("'a'''e'"), "a'''e");
        assertEquals(SentenceUtil.stripPunctuationWord("'a'e-e'"), "a'e-e");
        assertEquals(SentenceUtil.stripPunctuationWord("'12345'"), "12345");
        assertEquals(SentenceUtil.stripPunctuationWord("'-abcde--"), "abcde");
        assertEquals(SentenceUtil.stripPunctuationWord("'-a---e--"), "a---e");
        assertEquals(SentenceUtil.stripPunctuationWord("'-a'''e--"), "a'''e");
        assertEquals(SentenceUtil.stripPunctuationWord("'-a'e-e--"), "a'e-e");
        assertEquals(SentenceUtil.stripPunctuationWord("'-12345--"), "12345");
        assertEquals(SentenceUtil.stripPunctuationWord("$'-abcde-'*"), "abcde");
        assertEquals(SentenceUtil.stripPunctuationWord("$'-a---e-'*"), "a---e");
        assertEquals(SentenceUtil.stripPunctuationWord("$'-a'''e-'*"), "a'''e");
        assertEquals(SentenceUtil.stripPunctuationWord("$'-a'e-e-'*"), "a'e-e");
        assertEquals(SentenceUtil.stripPunctuationWord("$'-12345-'*"), "12345");
        assertEquals(SentenceUtil.stripPunctuationWord("`'-abcde-'["), "abcde");
        assertEquals(SentenceUtil.stripPunctuationWord("`'-a---e-'["), "a---e");
        assertEquals(SentenceUtil.stripPunctuationWord("`'-a'''e-'["), "a'''e");
        assertEquals(SentenceUtil.stripPunctuationWord("`'-a'e-e-'["), "a'e-e");
        assertEquals(SentenceUtil.stripPunctuationWord("`'-12345-'["), "12345");
        assertEquals(SentenceUtil.stripPunctuationWord("#'-abcde-'}"), "abcde");
        assertEquals(SentenceUtil.stripPunctuationWord("#'-a---e-'}"), "a---e");
        assertEquals(SentenceUtil.stripPunctuationWord("#'-a'''e-'}"), "a'''e");
        assertEquals(SentenceUtil.stripPunctuationWord("#'-a'e-e-'}"), "a'e-e");
        assertEquals(SentenceUtil.stripPunctuationWord("#'-12345-'}"), "12345");
        assertEquals(SentenceUtil.stripPunctuationWord("%'-abcde-'/"), "abcde");
        assertEquals(SentenceUtil.stripPunctuationWord("%'-a---e-'/"), "a---e");
        assertEquals(SentenceUtil.stripPunctuationWord("%'-a'''e-'/"), "a'''e");
        assertEquals(SentenceUtil.stripPunctuationWord("%'-a'e-e-'/"), "a'e-e");
        assertEquals(SentenceUtil.stripPunctuationWord("%'-12345-'/"), "12345");

        assertEquals(SentenceUtil.stripPunctuationWord("test"), "test");
        assertEquals(SentenceUtil.stripPunctuationWord(" test"), "test");
        assertEquals(SentenceUtil.stripPunctuationWord("test-- "), "test");
        assertEquals(SentenceUtil.stripPunctuationWord("test! "), "test");
        assertEquals(SentenceUtil.stripPunctuationWord("test\" "), "test");
        assertEquals(SentenceUtil.stripPunctuationWord("test... "), "test");
        assertEquals(SentenceUtil.stripPunctuationWord("test's"), "test's");
        assertEquals(SentenceUtil.stripPunctuationWord("test's "), "test's");
        assertEquals(SentenceUtil.stripPunctuationWord("test's!"), "test's");
        assertEquals(SentenceUtil.stripPunctuationWord("test's?"), "test's");
        assertEquals(SentenceUtil.stripPunctuationWord("test!?;;'#\""), "test");
        assertEquals(SentenceUtil.stripPunctuationWord("!\"%$test"), "test");
        assertEquals(SentenceUtil.stripPunctuationWord("   test "), "test");
        assertEquals(SentenceUtil.stripPunctuationWord("--test "), "test");
        assertEquals(SentenceUtil.stripPunctuationWord("'test "), "test");
        assertEquals(SentenceUtil.stripPunctuationWord("/?test "), "test");
        assertEquals(SentenceUtil.stripPunctuationWord(" $%^\" test %^&"), "test");
    }

    public void testStripWordsStringString() {
        // String s = SentenceUtil.stripWords(first, last);
    }

    public void testFirstLetter() {
        assertEquals(SentenceUtil.firstLetter("abcde"), 0);
        assertEquals(SentenceUtil.firstLetter(" abcde"), 1);
        assertEquals(SentenceUtil.firstLetter(" \"%abcde"), 3);
        assertEquals(SentenceUtil.firstLetter(" \"%abcde--!   "), 3);
    }

    public void testLastLetter() {
        assertEquals(SentenceUtil.lastLetter("abcde"), 4);
        assertEquals(SentenceUtil.lastLetter("abcde "), 4);
        assertEquals(SentenceUtil.lastLetter("abcde\" "), 4);
        assertEquals(SentenceUtil.lastLetter("abcde\"%$ "), 4);
        assertEquals(SentenceUtil.lastLetter(" abcde"), 5);
        assertEquals(SentenceUtil.lastLetter(" abcde "), 5);
        assertEquals(SentenceUtil.lastLetter(" abcde\" "), 5);
        assertEquals(SentenceUtil.lastLetter(" abcde\"%$ "), 5);
        assertEquals(SentenceUtil.lastLetter(" abcde--\"%$ "), 5);
        assertEquals(SentenceUtil.lastLetter(" abcde\"%$-- "), 5);
    }

    public void testStripWords() {
        assertEquals(SentenceUtil.stripWords("one", "two"), "");
        assertEquals(SentenceUtil.stripWords("one,", "two"), ",");
        assertEquals(SentenceUtil.stripWords("one'", "two"), "'");
        assertEquals(SentenceUtil.stripWords("one-", "two"), "-");
        assertEquals(SentenceUtil.stripWords("one#", "two"), "#");
        assertEquals(SentenceUtil.stripWords("one", ",two"), ",");
        assertEquals(SentenceUtil.stripWords("one", "'two"), "'");
        assertEquals(SentenceUtil.stripWords("one", "-two"), "-");
        assertEquals(SentenceUtil.stripWords("one", "#two"), "#");
        assertEquals(SentenceUtil.stripWords("one-", "-two"), "--");
        assertEquals(SentenceUtil.stripWords("-one-", "-two-"), "--");
        assertEquals(SentenceUtil.stripWords("one-world", "two"), "");
        assertEquals(SentenceUtil.stripWords("one-world'", "two"), "'");
        assertEquals(SentenceUtil.stripWords("one ", "two"), " ");
        assertEquals(SentenceUtil.stripWords("one, ", "two"), ", ");
        assertEquals(SentenceUtil.stripWords("one' ", "two"), "' ");
        assertEquals(SentenceUtil.stripWords("one- ", "two"), "- ");
        assertEquals(SentenceUtil.stripWords("one# ", "two"), "# ");
        assertEquals(SentenceUtil.stripWords("one", " ,two"), " ,");
        assertEquals(SentenceUtil.stripWords("one", " 'two"), " '");
        assertEquals(SentenceUtil.stripWords("one", " -two"), " -");
        assertEquals(SentenceUtil.stripWords("one", "#two"), "#");
        assertEquals(SentenceUtil.stripWords("one- ", "-two"), "- -");
        assertEquals(SentenceUtil.stripWords("-one- ", "-two-"), "- -");
        assertEquals(SentenceUtil.stripWords("one-world ", "two"), " ");
        assertEquals(SentenceUtil.stripWords("one-world'", " two"), "' ");
    }
}
