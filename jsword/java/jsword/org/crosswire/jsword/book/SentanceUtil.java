package org.crosswire.jsword.book;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.crosswire.jsword.passage.PassageConstants;

/**
 * The SentanceUtil class provide utility functions for the various Books.
 * 
 * It is not designed to be used outside of the book package, so using it
 * outside of these bounds is at your own risk.
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
public class SentanceUtil
{
    /**
     * Ensure we can not be instansiated
     */
    private SentanceUtil()
    {
    }

    /**
     * Is the given case a valid one?
     * @param test The case to be tested for validity
     * @return True if the number is OK, False otherwise
     */
    public static final boolean isValidCase(int test)
    {
        switch (test)
        {
        case PassageConstants.CASE_LOWER:
        case PassageConstants.CASE_MIXED:
        case PassageConstants.CASE_SENTANCE:
        case PassageConstants.CASE_UPPER:
            return true;

        default:
            return false;
        }
    }

    /**
     * Set the case of the specified word. This section needs to have more
     * thought from a localization point of view.
     * @param word The word to be manipulated
     * @param new_case LOWER, SENTANCE, UPPER or MIXED
     * @return The altered word
     * @exception IllegalArgumentException If the case is not between 0 and 3
     * Or for MIXED if the word is not LORD's
     */
    public static String setCase(String word, int new_case)
    {
        int index = 0;
    
        switch (new_case)
        {
        case PassageConstants.CASE_LOWER:
            return word.toLowerCase();
    
        case PassageConstants.CASE_UPPER:
            return word.toUpperCase();
    
        case PassageConstants.CASE_SENTANCE:
            index = word.indexOf('-');
            if (index == -1)
            {
                return toSentenceCase(word);
            }
    
            // So there is a "-", however first some exceptions
            if (word.equalsIgnoreCase("maher-shalal-hash-baz")) //$NON-NLS-1$
            {
                return "Maher-Shalal-Hash-Baz"; //$NON-NLS-1$
            }
    
            if (word.equalsIgnoreCase("no-one")) //$NON-NLS-1$
            {
                return "No-one"; //$NON-NLS-1$
            }
    
            if (word.substring(0, 4).equalsIgnoreCase("god-")) //$NON-NLS-1$
            {
                return toSentenceCase(word);
            }
    
            // So cut by the -
            return toSentenceCase(word.substring(0, index))
                   + "-" + toSentenceCase(word.substring(index + 1)); //$NON-NLS-1$
    
        case PassageConstants.CASE_MIXED:
            if (word.equalsIgnoreCase("lord's")) //$NON-NLS-1$
            {
                return "LORD's"; //$NON-NLS-1$
            }
            // This should not happen
            throw new IllegalArgumentException(Msg.ERROR_MIXED.toString());
    
        default:
            throw new IllegalArgumentException(Msg.ERROR_BADCASE.toString());
        }
    }

    /**
     * Change to sentance case - ie first character in caps, the rest in lower.
     * @param word The word to be manipulated
     * @return The altered word
     */
    protected static String toSentenceCase(String word)
    {
        assert word != null;
    
        if (word.equals("")) //$NON-NLS-1$
        {
            return ""; //$NON-NLS-1$
        }
    
        return Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
    }

    /**
     * What case is the specified word?. A blank word is CASE_LOWER, a
     * word with a single upper case letter is CASE_SENTANCE and not
     * CASE_UPPER - Simply because this is more likely, however TO BE
     * SURE I WOULD NEED TO THE CONTEXT. I could not tell otherwise.
     * <p>The issue here is that getCase("FreD") is undefined. Telling
     * if this is CASE_SENTANCE (Tubal-Cain) or MIXED (really the case)
     * is complex and would slow things down for a case that I don't
     * believe happens with Bible text.</p>
     * @param word The word to be tested
     * @return CASE_LOWER, CASE_SENTANCE, CASE_UPPER or CASE_MIXED
     * @exception IllegalArgumentException is the word is null
     */
    public static int getCase(String word)
    {
        assert word != null;
    
        // Blank word
        if (word.equals("")) //$NON-NLS-1$
        {
            return PassageConstants.CASE_LOWER;
        }
    
        // Lower case?
        if (word.equals(word.toLowerCase()))
        {
            return PassageConstants.CASE_LOWER;
        }
    
        // Upper case?
        // A string length of 1 is no good ('I' or 'A' is sentance case)
        if (word.equals(word.toUpperCase()) && word.length() != 1)
        {
            return PassageConstants.CASE_UPPER;
        }
    
        // If initial is lower then it must be mixed
        if (Character.isLowerCase(word.charAt(0)))
        {
            return PassageConstants.CASE_MIXED;
        }
    
        // Hack the only real caseMixed is LORD's
        // And we don't want to bother sorting out Tubal-Cain
        // as CASE_SENTANCE, so for now ...
        if (word.equals("LORD's")) //$NON-NLS-1$
        {
            return PassageConstants.CASE_MIXED;
        }
    
        // So ...
        return PassageConstants.CASE_SENTANCE;
    }

    /**
     * From a sentance get a list of words (in original order) without
     * any punctuation, and all in lower case.
     * @param words an array of words to find punctuation from
     * @return Array of case definitions
     */
    public static int[] getCases(String[] words)
    {
        int[] retcode = new int[words.length];
    
        // Remove the punctuation from the ends of the words.
        for (int i = 0; i < words.length; i++)
        {
            retcode[i] = getCase(words[i]);
        }
    
        return retcode;
    }

    /**
     * Take a string and tokenize it using " " and "--" as delimiters
     * into an Array of Strings. There is a question mark over what to do
     * with initial spaces. This algorithm disgards them, I'm not sure if
     * this is the right thing to do.
     * @param sentance The string to parse.
     * @return The string array
     */
    public static String[] tokenize(String sentance)
    {
        ArrayList tokens = new ArrayList();
    
        int pos = 0;
        String temp;
        boolean alive = true;
    
        while (alive)
        {
            // Find the next space and double dash
            int nextSpace = sentance.indexOf(" ", pos); //$NON-NLS-1$
            int nextDDash = sentance.indexOf("--", pos); //$NON-NLS-1$
    
            // If there is a space just after the ddash then ignore the ddash
            if (nextSpace == nextDDash + 2)
            {
                nextDDash = -1;
            }
    
            // If there is a ddash just after the space then ignore the space
            if (nextDDash == nextSpace + 1)
            {
                nextSpace = -1;
            }
    
            // if there are no more tokens then just add in what we've got.
            if (nextSpace == -1 && nextDDash == -1)
            {
                temp = sentance.substring(pos);
                alive = false;
            }
            // Space is next if it is not -1 and it is less than ddash
            else if ((nextSpace != -1 && nextSpace < nextDDash) || (nextDDash == -1))
            {
                // The next separator is a space
                temp = sentance.substring(pos, nextSpace) + " "; //$NON-NLS-1$
                pos = nextSpace + 1;
            }
            else
            {
                // The next separator is a ddash
                temp = sentance.substring(pos, nextDDash) + "--"; //$NON-NLS-1$
                pos = nextDDash + 2;
            }
    
            if (temp != null && !temp.trim().equals("")) //$NON-NLS-1$
            {
                tokens.add(temp);
            }
        }
    
        // Create a String[]
        String[] retcode = new String[tokens.size()];
        int i = 0;
        for (Iterator it = tokens.iterator(); it.hasNext(); )
        {
            retcode[i++] = (String) it.next();
        }
    
        return retcode;
    }

    /**
     * From a sentance get a list of words (in original order) without
     * any punctuation, and all in lower case.
     * @param words Words with punctuation
     * @return Words without punctuation
     */
    public static String[] stripPunctuation(String[] words)
    {
        String[] retcode = new String[words.length];
    
        // Remove the punctuation from the ends of the words.
        for (int i = 0; i < words.length; i++)
        {
            retcode[i] = stripPunctuationWord(words[i]);
        }
    
        return retcode;
    }

    /**
     * From a sentance get a list of words (in original order) without
     * any punctuation, and all in lower case.
     * @param words Words with punctuation
     * @return Punctuation without words
     */
    public static String[] stripWords(String[] words)
    {
        if (words.length == 0)
        {
            return new String[0];
        }
    
        String[] retcode = new String[words.length + 1];
    
        // The first bit of punctuation is what comes in front of the first word
        int first = firstLetter(words[0]);
        if (first == 0)
        {
            retcode[0] = ""; //$NON-NLS-1$
        }
        else
        {
            retcode[0] = words[0].substring(0, first);
        }
    
        // The rest of the words
        for (int i = 1; i < words.length; i++)
        {
            retcode[i] = stripWords(words[i - 1], words[i]);
        }
    
        // The last bit of punctuation is what comes at the end of the last word
        int last = lastLetter(words[words.length - 1]);
        if (last == words[words.length - 1].length())
        {
            retcode[words.length] = ""; //$NON-NLS-1$
        }
        else
        {
            retcode[words.length] = words[words.length - 1].substring(last + 1);
        }
    
        return retcode;
    }

    /**
     * From a sentance get a list of words (in original order) without
     * any punctuation, and all in lower case.
     * @param sentance The string to parse.
     * @return The words split up as an array
     */
    public static String[] getWords(String sentance)
    {
        // First there are some things we regard as word delimitters even if
        // they are not near space. Note that "-" should not be in this list
        // because words like abel-beth-maiacha comtain them.
        sentance = StringUtils.replace(sentance, "--", " "); //$NON-NLS-1$ //$NON-NLS-2$
        sentance = StringUtils.replace(sentance, ".", " "); //$NON-NLS-1$ //$NON-NLS-2$
        sentance = StringUtils.replace(sentance, "!", " "); //$NON-NLS-1$ //$NON-NLS-2$
        sentance = StringUtils.replace(sentance, "?", " "); //$NON-NLS-1$ //$NON-NLS-2$
        sentance = StringUtils.replace(sentance, ":", " "); //$NON-NLS-1$ //$NON-NLS-2$
        sentance = StringUtils.replace(sentance, ";", " "); //$NON-NLS-1$ //$NON-NLS-2$
        sentance = StringUtils.replace(sentance, "\"", " "); //$NON-NLS-1$ //$NON-NLS-2$
        sentance = StringUtils.replace(sentance, "\'", " "); //$NON-NLS-1$ //$NON-NLS-2$
        sentance = StringUtils.replace(sentance, "(", " "); //$NON-NLS-1$ //$NON-NLS-2$
        sentance = StringUtils.replace(sentance, ")", " "); //$NON-NLS-1$ //$NON-NLS-2$

        String[] words = StringUtils.split(sentance, " "); //$NON-NLS-1$
        String[] retcode = new String[words.length];

        // Remove the punctuation from the ends of the words.
        for (int i = 0; i < words.length; i++)
        {
            retcode[i] = stripPunctuationWord(words[i]).toLowerCase();
        }

        return retcode;
    }

    /**
     * Remove the punctuation from the ends of the word
     * @param word Word with punctuation
     * @return Word without punctuation
     */
    public static String stripPunctuationWord(String word)
    {
        int first = firstLetter(word);
        int last = lastLetter(word) + 1;

        if (first > last)
        {
            return word;
        }

        return word.substring(first, last);
    }

    /**
     * Remove the punctuation from the ends of the word. The special
     * case is that if the first word ends "--" and the last word has
     * no punctuation at the beginning, then the answer is "--" and not
     * "-- ". We miss out the space because "--" is a special separator.
     * @param first The word to grab the punctuation from the end of
     * @param last The word to grab the punctuation from the start of
     * @return The end of the first, a space, and the end of the first
     */
    public static String stripWords(String first, String last)
    {
        String init1 = first.substring(lastLetter(first) + 1);
        String init2 = last.substring(0, firstLetter(last));

        return init1 + init2;
    }

    /**
     * Where is the first letter in this word
     * @param word The word to search for letters
     * @return The offset of the first letter
     */
    public static final int firstLetter(String word)
    {
        int first;

        for (first = 0; first < word.length(); first++)
        {
            char c = word.charAt(first);
            if (Character.isLetterOrDigit(c))
            {
                break;
            }
        }

        return first;
    }

    /**
     * Where is the last letter in this word
     * @param word The word to search for letters
     * @return The offset of the last letter
     */
    public static final int lastLetter(String word)
    {
        int last;

        for (last = word.length() - 1; last >= 0; last--)
        {
            char c = word.charAt(last);
            if (Character.isLetterOrDigit(c))
            {
                break;
            }
        }

        return last;
    }
}
