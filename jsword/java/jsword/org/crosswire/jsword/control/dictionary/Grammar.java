
package org.crosswire.jsword.control.dictionary;

import org.crosswire.common.util.StringUtil;

/**
* Grammar. 
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
public class Grammar
{
    /**
    * Strip of any parts of speech to leave a root word.
    * This class may not be the best place for this code, however I'm not
    * sure if we have a better place for it at the mo. Maybe it should be
    * in passage.PassageUtil?
    * @param word The word to strip
    * @return The root word
    */
    public static String getRoot(String word)
    {
        for (int i=0; i<endings.length; i++)
        {
            if (word.endsWith(endings[i]))
            {
                // Make the assumption that we never have 2 ending on a word
                return word.substring(0, word.length() - endings[i].length());
            }
        }

        return word;
    }

    /**
    * Is this word one of those small words that can slaughter a DB
    * query. An empty string IS a small word.
    * @param word The word to test
    */
    public static boolean isSmallWord(String word)
    {
        word = word.trim();
        if (word.equals(""))
            return true;

        for (int i=0; i<word_freq.length; i++)
        {
            if (word.equalsIgnoreCase(word_freq[i]))
            {
                return true;
            }
        }

        return false;
    }

    /**
    * Is this word one of those small words that can slaughter a DB query
    * @param word The word to test
    */
    public static String[] stripSmallWords(String[] words)
    {
        // How many long words are there?
        int long_words = 0;
        for (int i=0; i<words.length; i++)
        {
            if (!isSmallWord(words[i]))
                long_words++;
        }

        // Create the array with just the long words
        int count = 0;
        String[] retcode = new String[long_words];
        for (int i=0; i<words.length; i++)
        {
            if (!isSmallWord(words[i]))
                retcode[count++] = words[i];
        }

        return retcode;
    }

    /**
    * Like PassageUtil.tokenize that leaves out the small words
    * @param word The word to split up
    * @param delims The word separators
    * @return The long words in the string
    */
    public static String[] tokenizeWithoutSmallWords(String original, String delims)
    {
        String[] words = StringUtil.tokenize(original, delims);
        int small_words = 0;

        for (int i=0; i<words.length; i++)
        {
            if (Grammar.isSmallWord(words[i]))
            {
                small_words++;
                words[i] = null;
            }
        }

        String retcode[] = new String[words.length-small_words];
        int count = 0;
        for (int i=0; i<words.length; i++)
        {
            if (words[i] != null) retcode[count++] = words[i];
        }

        return retcode;
    }

    /**
    * The Endings a word can have.
    * These are matched in order so there is no point in having "s"
    * before "es" because the second will not be tried.
    */
    private static final String[] endings =
    {
        "es",
        "'s",
        "s",
        "ing",
        "ed",
        "er",
        "ly",
    };

    /**
    * The one hundred most used words, and the instance count
    */
    private static final String[] word_freq = 
    {
        // word     instance count (in AV & NIV)
        "the",      // 119135
        "and",      // 81244
        "of",       // 59417
        "to",       // 43624
        "in",       // 24233
        "he",       // 20088
        "that",     // 18672
        "i",        // 17605
        "a",        // 17439
        "for",      // 16780
        "you",      // 16324
        "his",      // 15438
//      "lord",     // 15319
        "is",       // 14304
        "will",     // 13981
        "they",     // 13942
        "not",      // 12507
        "with",     // 12125
        "him",      // 12058
        "it",       // 11834
        "be",       // 11638
        "them",     // 11608
        "shall",    // 10833
        "all",      // 10333
        "my",       // 9547
        "from",     // 9323
        "was",      // 8530
        "your",     // 8400
//      "god",      // 8381
        "have",     // 8322
        "me",       // 8102
        "but",      // 7991
        "their",    // 7638
        "as",       // 7521
        "who",      // 7425
        "said",     // 7198
        "are",      // 6981
        "on",       // 6914
        "this",     // 6558
        "when",     // 5667
        "thou",     // 5470
        "thy",      // 5469
        "by",       // 5434
        "were",     // 5192
        "had",      // 5109
        "then",     // 5105
        "out",      // 4778
//      "man",      // 4702
//      "son",      // 4701
        "so",       // 4689
//      "king",     // 4568
//      "israel",   // 4407
        "there",    // 4393
//      "people",   // 4355
        "which",    // 4253
        "do",       // 4032
        "one",      // 3998
        "ye",       // 3970
        "up",       // 3798
        "thee",     // 3780
        "at",       // 3767
        "we",       // 3725
        "her",      // 3583
        "what",     // 3545
        "men",      // 3482
        "come",     // 3404
        "if",       // 3380
        "into",     // 3284
        "came",     // 3283
//      "land",     // 3182
//      "day",      // 3168
        "upon",     // 3164
        "before",   // 3133
        "or",       // 3097
//      "house",    // 2997
        "us",       // 2886
        "because",  // 2879
        "go",       // 2869
//      "against",  // 2851
        "an",       // 2828
//      "no",       // 2711
        "went",     // 2597
        "also",     // 2586
        "now",      // 2571
        "let",      // 2548
//      "made",     // 2478
        "hath",     // 2450
        "may",      // 2418
        "has",      // 2406
        "our",      // 2361
        "these",    // 2356
//      "down",     // 2314
//      "hand",     // 2314
//      "jesus",    // 2255
//      "children", // 2231
//      "like",     // 2180
//      "over",     // 2091
        "o",        // 2090
//      "david",    // 2089
//      "father",   // 2065
        "am",
    };
}
