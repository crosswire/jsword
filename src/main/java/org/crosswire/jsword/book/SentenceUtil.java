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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.crosswire.common.util.StringUtil;

/**
 * The SentenceUtil class provide utility functions for the various Books.
 * 
 * It is not designed to be used outside of the book package, so using it
 * outside of these bounds is at your own risk.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public final class SentenceUtil {
    /**
     * Ensure we can not be instantiated
     */
    private SentenceUtil() {
    }

    /**
     * Take a string and tokenize it using " " and "--" as delimiters into an
     * Array of Strings. There is a question mark over what to do with initial
     * spaces. This algorithm discards them, I'm not sure if this is the right
     * thing to do.
     * 
     * @param sentence
     *            The string to parse.
     * @return The string array
     */
    public static String[] tokenize(String sentence) {
        List<String> tokens = new ArrayList<String>();

        int pos = 0;
        String temp;
        boolean alive = true;

        while (alive) {
            // Find the next space and double dash
            int nextSpace = sentence.indexOf(' ', pos);
            int nextDDash = sentence.indexOf("--", pos);

            // If there is a space just after the ddash then ignore the ddash
            if (nextSpace == nextDDash + 2) {
                nextDDash = -1;
            }

            // If there is a ddash just after the space then ignore the space
            if (nextDDash == nextSpace + 1) {
                nextSpace = -1;
            }

            // if there are no more tokens then just add in what we've got.
            if (nextSpace == -1 && nextDDash == -1) {
                temp = sentence.substring(pos);
                alive = false;
            } else if ((nextSpace != -1 && nextSpace < nextDDash) || (nextDDash == -1)) {
                // Space is next if it is not -1 and it is less than ddash
                // The next separator is a space
                temp = sentence.substring(pos, nextSpace) + ' ';
                pos = nextSpace + 1;
            } else {
                // The next separator is a ddash
                temp = sentence.substring(pos, nextDDash) + "--";
                pos = nextDDash + 2;
            }

            if (temp != null && !"".equals(temp.trim())) {
                tokens.add(temp);
            }
        }

        // Create a String[]
        String[] retcode = new String[tokens.size()];
        int i = 0;
        for (String token : tokens) {
            retcode[i++] = token;
        }

        return retcode;
    }

    /**
     * From a sentence get a list of words (in original order) without any
     * punctuation, and all in lower case.
     * 
     * @param words
     *            Words with punctuation
     * @return Words without punctuation
     */
    public static String[] stripPunctuation(String... words) {
        String[] retcode = new String[words.length];

        // Remove the punctuation from the ends of the words.
        for (int i = 0; i < words.length; i++) {
            retcode[i] = stripPunctuationWord(words[i]);
        }

        return retcode;
    }

    /**
     * From a sentence get a list of words (in original order) without any
     * punctuation, and all in lower case.
     * 
     * @param words
     *            Words with punctuation
     * @return Punctuation without words
     */
    public static String[] stripWords(String... words) {
        if (words.length == 0) {
            return new String[0];
        }

        String[] retcode = new String[words.length + 1];

        // The first bit of punctuation is what comes in front of the first word
        int first = firstLetter(words[0]);
        if (first == 0) {
            retcode[0] = "";
        } else {
            retcode[0] = words[0].substring(0, first);
        }

        // The rest of the words
        for (int i = 1; i < words.length; i++) {
            retcode[i] = stripWords(words[i - 1], words[i]);
        }

        // The last bit of punctuation is what comes at the end of the last word
        int last = lastLetter(words[words.length - 1]);
        if (last == words[words.length - 1].length()) {
            retcode[words.length] = "";
        } else {
            retcode[words.length] = words[words.length - 1].substring(last + 1);
        }

        return retcode;
    }

    /**
     * Remove the punctuation from the ends of the word. The special case is
     * that if the first word ends "--" and the last word has no punctuation at
     * the beginning, then the answer is "--" and not "-- ". We miss out the
     * space because "--" is a special separator.
     * 
     * @param first
     *            The word to grab the punctuation from the end of
     * @param last
     *            The word to grab the punctuation from the start of
     * @return The end of the first, a space, and the end of the first
     */
    public static String stripWords(String first, String last) {
        String init1 = first.substring(lastLetter(first) + 1);
        String init2 = last.substring(0, firstLetter(last));

        return init1 + init2;
    }

    /**
     * From a sentence get a list of words (in original order) without any
     * punctuation, and all in lower case.
     * 
     * @param aSentence
     *            The string to parse.
     * @return The words split up as an array
     */
    public static String[] getWords(String aSentence) {
        String sentence = aSentence;
        // First there are some things we regard as word delimiters even if
        // they are not near space. Note that "-" should not be in this list
        // because words like abel-beth-maiacha contain them.
        sentence = sentence.replaceAll("--", " ");
        sentence = sentence.replace('.', ' ');
        sentence = sentence.replace('!', ' ');
        sentence = sentence.replace('?', ' ');
        sentence = sentence.replace(':', ' ');
        sentence = sentence.replace(';', ' ');
        sentence = sentence.replace('"', ' ');
        sentence = sentence.replace('\'', ' ');
        sentence = sentence.replace('(', ' ');
        sentence = sentence.replace(')', ' ');

        String[] words = StringUtil.split(sentence, " ");
        String[] retcode = new String[words.length];

        // Remove the punctuation from the ends of the words.
        for (int i = 0; i < words.length; i++) {
            retcode[i] = stripPunctuationWord(words[i]).toLowerCase(Locale.ENGLISH);
        }

        return retcode;
    }

    /**
     * Remove the punctuation from the ends of the word
     * 
     * @param word
     *            Word with punctuation
     * @return Word without punctuation
     */
    public static String stripPunctuationWord(String word) {
        int first = firstLetter(word);
        int last = lastLetter(word) + 1;

        if (first > last) {
            return word;
        }

        return word.substring(first, last);
    }

    /**
     * Where is the first letter in this word
     * 
     * @param word
     *            The word to search for letters
     * @return The offset of the first letter
     */
    public static int firstLetter(String word) {
        int first;

        for (first = 0; first < word.length(); first++) {
            char c = word.charAt(first);
            if (Character.isLetterOrDigit(c)) {
                break;
            }
        }

        return first;
    }

    /**
     * Where is the last letter in this word
     * 
     * @param word
     *            The word to search for letters
     * @return The offset of the last letter
     */
    public static int lastLetter(String word) {
        int last;

        for (last = word.length() - 1; last >= 0; last--) {
            char c = word.charAt(last);
            if (Character.isLetterOrDigit(c)) {
                break;
            }
        }

        return last;
    }
}
