package org.crosswire.jsword.book.search.parse;

import org.apache.commons.lang.StringUtils;

/**
 * A class representing various grammatical constructs (in English). 
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
public class Grammar
{
    /**
     * Prevent Instansiation
     */
    private Grammar()
    {
    }

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
        for (int i=0; i<ENDINGS.length; i++)
        {
            if (word.endsWith(ENDINGS[i]))
            {
                // Make the assumption that we never have 2 ending on a word
                return word.substring(0, word.length() - ENDINGS[i].length());
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
        if (word.equals("")) //$NON-NLS-1$
        {
            return true;
        }

        for (int i=0; i<WORD_FREQ.length; i++)
        {
            if (word.equalsIgnoreCase(WORD_FREQ[i]))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Is this word one of those small words that can slaughter a DB query
     */
    public static String[] stripSmallWords(String[] words)
    {
        // How many long words are there?
        int long_words = 0;
        for (int i=0; i<words.length; i++)
        {
            if (!isSmallWord(words[i]))
            {
                long_words++;
            }
        }

        // Create the array with just the long words
        int count = 0;
        String[] retcode = new String[long_words];
        for (int i=0; i<words.length; i++)
        {
            if (!isSmallWord(words[i]))
            {
                retcode[count++] = words[i];
            }
        }

        return retcode;
    }

    /**
     * Like PassageUtil.tokenize that leaves out the small words
     * @param original The sentance to split up
     * @param delims The word separators
     * @return The long words in the string
     */
    public static String[] tokenizeWithoutSmallWords(String original, String delims)
    {
        String[] words = StringUtils.split(original, delims);
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
            if (words[i] != null)
            {
                retcode[count++] = words[i];
            }
        }

        return retcode;
    }

    /**
     * The Endings a word can have.
     * These are matched in order so there is no point in having "s"
     * before "es" because the second will not be tried.
     */
    private static final String[] ENDINGS =
    {
        "es", //$NON-NLS-1$
        "'s", //$NON-NLS-1$
        "s", //$NON-NLS-1$
        "ing", //$NON-NLS-1$
        "ed", //$NON-NLS-1$
        "er", //$NON-NLS-1$
        "ly", //$NON-NLS-1$
    };

    /**
     * The one hundred most used words, and the instance count
     */
    private static final String[] WORD_FREQ = 
    {
        // word     instance count (in AV & NIV)
        "the",      // 119135 //$NON-NLS-1$
        "and",      // 81244 //$NON-NLS-1$
        "of",       // 59417 //$NON-NLS-1$
        "to",       // 43624 //$NON-NLS-1$
        "in",       // 24233 //$NON-NLS-1$
        "he",       // 20088 //$NON-NLS-1$
        "that",     // 18672 //$NON-NLS-1$
        "i",        // 17605 //$NON-NLS-1$
        "a",        // 17439 //$NON-NLS-1$
        "for",      // 16780 //$NON-NLS-1$
        "you",      // 16324 //$NON-NLS-1$
        "his",      // 15438 //$NON-NLS-1$
//      "lord",     // 15319
        "is",       // 14304 //$NON-NLS-1$
        "will",     // 13981 //$NON-NLS-1$
        "they",     // 13942 //$NON-NLS-1$
        "not",      // 12507 //$NON-NLS-1$
        "with",     // 12125 //$NON-NLS-1$
        "him",      // 12058 //$NON-NLS-1$
        "it",       // 11834 //$NON-NLS-1$
        "be",       // 11638 //$NON-NLS-1$
        "them",     // 11608 //$NON-NLS-1$
        "shall",    // 10833 //$NON-NLS-1$
        "all",      // 10333 //$NON-NLS-1$
        "my",       // 9547 //$NON-NLS-1$
        "from",     // 9323 //$NON-NLS-1$
        "was",      // 8530 //$NON-NLS-1$
        "your",     // 8400 //$NON-NLS-1$
//      "god",      // 8381
        "have",     // 8322 //$NON-NLS-1$
        "me",       // 8102 //$NON-NLS-1$
        "but",      // 7991 //$NON-NLS-1$
        "their",    // 7638 //$NON-NLS-1$
        "as",       // 7521 //$NON-NLS-1$
        "who",      // 7425 //$NON-NLS-1$
        "said",     // 7198 //$NON-NLS-1$
        "are",      // 6981 //$NON-NLS-1$
        "on",       // 6914 //$NON-NLS-1$
        "this",     // 6558 //$NON-NLS-1$
        "when",     // 5667 //$NON-NLS-1$
        "thou",     // 5470 //$NON-NLS-1$
        "thy",      // 5469 //$NON-NLS-1$
        "by",       // 5434 //$NON-NLS-1$
        "were",     // 5192 //$NON-NLS-1$
        "had",      // 5109 //$NON-NLS-1$
        "then",     // 5105 //$NON-NLS-1$
        "out",      // 4778 //$NON-NLS-1$
//      "man",      // 4702
//      "son",      // 4701
        "so",       // 4689 //$NON-NLS-1$
//      "king",     // 4568
//      "israel",   // 4407
        "there",    // 4393 //$NON-NLS-1$
//      "people",   // 4355
        "which",    // 4253 //$NON-NLS-1$
        "do",       // 4032 //$NON-NLS-1$
        "one",      // 3998 //$NON-NLS-1$
        "ye",       // 3970 //$NON-NLS-1$
        "up",       // 3798 //$NON-NLS-1$
        "thee",     // 3780 //$NON-NLS-1$
        "at",       // 3767 //$NON-NLS-1$
        "we",       // 3725 //$NON-NLS-1$
        "her",      // 3583 //$NON-NLS-1$
        "what",     // 3545 //$NON-NLS-1$
        "men",      // 3482 //$NON-NLS-1$
        "come",     // 3404 //$NON-NLS-1$
        "if",       // 3380 //$NON-NLS-1$
        "into",     // 3284 //$NON-NLS-1$
        "came",     // 3283 //$NON-NLS-1$
//      "land",     // 3182
//      "day",      // 3168
        "upon",     // 3164 //$NON-NLS-1$
        "before",   // 3133 //$NON-NLS-1$
        "or",       // 3097 //$NON-NLS-1$
//      "house",    // 2997
        "us",       // 2886 //$NON-NLS-1$
        "because",  // 2879 //$NON-NLS-1$
        "go",       // 2869 //$NON-NLS-1$
//      "against",  // 2851
        "an",       // 2828 //$NON-NLS-1$
//      "no",       // 2711
        "went",     // 2597 //$NON-NLS-1$
        "also",     // 2586 //$NON-NLS-1$
        "now",      // 2571 //$NON-NLS-1$
        "let",      // 2548 //$NON-NLS-1$
//      "made",     // 2478
        "hath",     // 2450 //$NON-NLS-1$
        "may",      // 2418 //$NON-NLS-1$
        "has",      // 2406 //$NON-NLS-1$
        "our",      // 2361 //$NON-NLS-1$
        "these",    // 2356 //$NON-NLS-1$
//      "down",     // 2314
//      "hand",     // 2314
//      "jesus",    // 2255
//      "children", // 2231
//      "like",     // 2180
//      "over",     // 2091
        "o",        // 2090 //$NON-NLS-1$
//      "david",    // 2089
//      "father",   // 2065
        "am", //$NON-NLS-1$
    };
}
