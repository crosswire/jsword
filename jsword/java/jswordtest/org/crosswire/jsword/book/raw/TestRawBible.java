
package org.crosswire.jsword.book.raw;

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
public class TestRawBible extends TestCase
{
    public TestRawBible(String s)
    {
        super(s);
    }

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
    }

    public void testRawUtil() throws Exception
    {
        /*
        RawBible raw = new RawBible();
        WordResource words = raw.getWordResource();
        WordInstResource wordinsts = raw.getWordInstResource();
        String[] test;

        log("RawUtil.tokenize(String)");
        test = RawUtil.tokenize("one two three");
        test(test.length, 3);
        test(test[0], "one ");
        test(test[1], "two ");
        test(test[2], "three");
        test = RawUtil.tokenize("one");
        test(test.length, 1);
        test(test[0], "one");
        test = RawUtil.tokenize("One, !Two-er THREE-er?");
        test(test.length, 3);
        test(test[0], "One, ");
        test(test[1], "!Two-er ");
        test(test[2], "THREE-er?");
        test = RawUtil.tokenize("One, !Two-er THREE--four?");
        test(test.length, 4);
        test(test[0], "One, ");
        test(test[1], "!Two-er ");
        test(test[2], "THREE--");
        test(test[3], "four?");

        log("RawUtil.stripPunctuation(String)");
        test(RawUtil.stripPunctuation("abcde"), "abcde");
        test(RawUtil.stripPunctuation("a---e"), "a---e");
        test(RawUtil.stripPunctuation("a'''e"), "a'''e");
        test(RawUtil.stripPunctuation("a'e-e"), "a'e-e");
        test(RawUtil.stripPunctuation("12345"), "12345");
        test(RawUtil.stripPunctuation("'abcde"), "abcde");
        test(RawUtil.stripPunctuation("'a---e"), "a---e");
        test(RawUtil.stripPunctuation("'a'''e"), "a'''e");
        test(RawUtil.stripPunctuation("'a'e-e"), "a'e-e");
        test(RawUtil.stripPunctuation("'12345"), "12345");
        test(RawUtil.stripPunctuation("'abcde'"), "abcde");
        test(RawUtil.stripPunctuation("'a---e'"), "a---e");
        test(RawUtil.stripPunctuation("'a'''e'"), "a'''e");
        test(RawUtil.stripPunctuation("'a'e-e'"), "a'e-e");
        test(RawUtil.stripPunctuation("'12345'"), "12345");
        test(RawUtil.stripPunctuation("'-abcde--"), "abcde");
        test(RawUtil.stripPunctuation("'-a---e--"), "a---e");
        test(RawUtil.stripPunctuation("'-a'''e--"), "a'''e");
        test(RawUtil.stripPunctuation("'-a'e-e--"), "a'e-e");
        test(RawUtil.stripPunctuation("'-12345--"), "12345");
        test(RawUtil.stripPunctuation("$'-abcde-'*"), "abcde");
        test(RawUtil.stripPunctuation("$'-a---e-'*"), "a---e");
        test(RawUtil.stripPunctuation("$'-a'''e-'*"), "a'''e");
        test(RawUtil.stripPunctuation("$'-a'e-e-'*"), "a'e-e");
        test(RawUtil.stripPunctuation("$'-12345-'*"), "12345");
        test(RawUtil.stripPunctuation("`'-abcde-'["), "abcde");
        test(RawUtil.stripPunctuation("`'-a---e-'["), "a---e");
        test(RawUtil.stripPunctuation("`'-a'''e-'["), "a'''e");
        test(RawUtil.stripPunctuation("`'-a'e-e-'["), "a'e-e");
        test(RawUtil.stripPunctuation("`'-12345-'["), "12345");
        test(RawUtil.stripPunctuation("#'-abcde-'}"), "abcde");
        test(RawUtil.stripPunctuation("#'-a---e-'}"), "a---e");
        test(RawUtil.stripPunctuation("#'-a'''e-'}"), "a'''e");
        test(RawUtil.stripPunctuation("#'-a'e-e-'}"), "a'e-e");
        test(RawUtil.stripPunctuation("#'-12345-'}"), "12345");
        test(RawUtil.stripPunctuation("£'-abcde-'/"), "abcde");
        test(RawUtil.stripPunctuation("£'-a---e-'/"), "a---e");
        test(RawUtil.stripPunctuation("£'-a'''e-'/"), "a'''e");
        test(RawUtil.stripPunctuation("£'-a'e-e-'/"), "a'e-e");
        test(RawUtil.stripPunctuation("£'-12345-'/"), "12345");

        log("RawUtil.stripPunctuation(String[])");
        test = RawUtil.stripPunctuation(StringUtil.tokenize("aaaa", " "));
        test(test.length, 1);
        test(test[0], "aaaa");
        test = RawUtil.stripPunctuation(StringUtil.tokenize("aaaa bbbb", " "));
        test(test.length, 2);
        test(test[0], "aaaa");
        test(test[1], "bbbb");
        test = RawUtil.stripPunctuation(StringUtil.tokenize("One Two Three", " "));
        test(test.length, 3);
        test(test[0], "One");
        test(test[1], "Two");
        test(test[2], "Three");
        test = RawUtil.stripPunctuation(StringUtil.tokenize(" One  Two  Three ", " "));
        test(test.length, 3);
        test(test[0], "One");
        test(test[1], "Two");
        test(test[2], "Three");
        test = RawUtil.stripPunctuation(StringUtil.tokenize(" 'One's' ,Two? !Three-Four\" ", " "));
        test(test.length, 3);
        test(test[0], "One's");
        test(test[1], "Two");
        test(test[2], "Three-Four");
        test = RawUtil.stripPunctuation(StringUtil.tokenize(" 'One's' ,Two? !Three-- Four\" ", " "));
        test(test.length, 4);
        test(test[0], "One's");
        test(test[1], "Two");
        test(test[2], "Three");
        test(test[3], "Four");

        log("RawUtil.stripWords(String)");
        test(RawUtil.stripWords("one", "two"), "");
        test(RawUtil.stripWords("one,", "two"), ",");
        test(RawUtil.stripWords("one'", "two"), "'");
        test(RawUtil.stripWords("one-", "two"), "-");
        test(RawUtil.stripWords("one#", "two"), "#");
        test(RawUtil.stripWords("one", ",two"), ",");
        test(RawUtil.stripWords("one", "'two"), "'");
        test(RawUtil.stripWords("one", "-two"), "-");
        test(RawUtil.stripWords("one", "#two"), "#");
        test(RawUtil.stripWords("one-", "-two"), "--");
        test(RawUtil.stripWords("-one-", "-two-"), "--");
        test(RawUtil.stripWords("one-world", "two"), "");
        test(RawUtil.stripWords("one-world'", "two"), "'");
        test(RawUtil.stripWords("one ", "two"), " ");
        test(RawUtil.stripWords("one, ", "two"), ", ");
        test(RawUtil.stripWords("one' ", "two"), "' ");
        test(RawUtil.stripWords("one- ", "two"), "- ");
        test(RawUtil.stripWords("one# ", "two"), "# ");
        test(RawUtil.stripWords("one", " ,two"), " ,");
        test(RawUtil.stripWords("one", " 'two"), " '");
        test(RawUtil.stripWords("one", " -two"), " -");
        test(RawUtil.stripWords("one" , "#two"), "#");
        test(RawUtil.stripWords("one- ", "-two"), "- -");
        test(RawUtil.stripWords("-one- ", "-two-"), "- -");
        test(RawUtil.stripWords("one-world ", "two"), " ");
        test(RawUtil.stripWords("one-world'", " two"), "' ");

        log("RawUtil.stripWords(String[])");
        test = RawUtil.stripWords(StringUtil.tokenize(" 'One's' ,Two? !Three-Four\" ", " "));
        test(test.length, 4);
        test(test[0], "'");
        test(test[1], "',");
        test(test[2], "?!");
        test(test[3], "\"");
        test = RawUtil.stripWords(StringUtil.tokenize(" 'One's' ,Two? !Three-- Four\" ", " "));
        test(test.length, 5);
        test(test[0], "'");
        test(test[1], "',");
        test(test[2], "?!");
        test(test[3], "--");
        test(test[4], "\"");
        test = RawUtil.stripWords(RawUtil.tokenize("'One's' ,Two? !Three--Four\""));
        test(test.length, 5);
        test(test[0], "'");
        test(test[1], "' ,");
        test(test[2], "? !");
        test(test[3], "--");
        test(test[4], "\"");

        log("WordResource.getIndex(String)");
        int in = words.getIndex("in");
        int th = words.getIndex("the");
        int be = words.getIndex("beginning");
        int go = words.getIndex("god");
        int cr = words.getIndex("created");
        test(in, words.getIndex("in"));
        test(th, words.getIndex("the"));
        test(be, words.getIndex("beginning"));
        test(go, words.getIndex("god"));
        test(cr, words.getIndex("created"));
        test(in != th);
        test(in != be);
        test(in != go);
        test(in != cr);
        test(th != be);
        test(th != go);
        test(th != cr);
        test(be != go);
        test(be != cr);
        test(go != cr);
        int g2 = words.getIndex("gods");
        int g3 = words.getIndex("god");
        int g4 = words.getIndex("god's");
        int g5 = words.getIndex("godly");
        int g6 = words.getIndex("good");
        test(g2, words.getIndex("gods"));
        test(g3, words.getIndex("god"));
        test(g4, words.getIndex("god's"));
        test(g5, words.getIndex("godly"));
        test(g6, words.getIndex("good"));
        test(go != g2);
        test(go, g3);
        test(go != g4);
        test(go != g5);
        test(go != g6);
        test(g2 != g3);
        test(g2 != g4);
        test(g2 != g5);
        test(g2 != g6);
        test(g3 != g4);
        test(g3 != g5);
        test(g3 != g6);
        test(g4 != g5);
        test(g4 != g6);
        test(g5 != g6);

        log("WordResource.getIndex(String[])");
        int[] idx = words.getIndex(new String[] { "in", "the", "beginning", "did", "god" });
        test(idx[0], in);
        test(idx[1], th);
        test(idx[2], be);
        test(idx[3], words.getIndex("did"));
        test(idx[4], go);

        log("WordResource.getWords()");
        Enumeration en = words.getEnumeration();
        while (en.hasMoreElements())
        {
            String word = (String) en.nextElement();
            int index = words.getIndex(word);
            String word2 = words.getItem(index);
            test(word, word2);
        }

        log("WordResource.getWord(int)");
        test("in", words.getItem(in));
        test("the", words.getItem(th));
        test("beginning", words.getItem(be));
        test("god", words.getItem(go));
        test("created", words.getItem(cr));
        test("good", words.getItem(g6));

        log("WordInstResource.setWords(int[], Verse)");
        int[] widx = new int[] { 0, 1, 2, 3, 4 };
        wordinsts.setIndexes(widx, new Verse(1));

        log("WordInstResource.getWords(int[])");
        int[] widx2 = wordinsts.getIndexes(new Verse(1));
        test(widx, widx2);
        */

        // I'm making these tests do for the Punc[Inst]Resource
        // classes as they are to similar.
    }
}

