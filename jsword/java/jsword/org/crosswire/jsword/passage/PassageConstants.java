
package org.crosswire.jsword.passage;

/**
 * Various constants used by the concrete Verse classes.
 * 
 * This class is intended to be implemented to get easy access to the constants.
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
public interface PassageConstants
{
    /** A word is lower case if all the letters lower case, or if the word is blank - that is it is a default */
    public static final int CASE_LOWER = 0;

    /** A word is sentance case if the first letter is upper case, and all subsequent letters are lower case */
    public static final int CASE_SENTANCE = 1;

    /** A word is upper case if all the letters upper case */
    public static final int CASE_UPPER = 2;

    /** A word is mixed case if it does not conform to any of the above */
    public static final int CASE_MIXED = 3;

    /** A word is mixed case if it does not conform to any of the above */
    public static final String[] CASES = { "lower", "Sentence", "UPPER", "MIXed", };


    /** Don't stop at all */
    public static final int RESTRICT_NONE = 0;

    /** Stop at the edge of a book */
    public static final int RESTRICT_BOOK = 1;

    /** Stop at the edge of a chapter */
    public static final int RESTRICT_CHAPTER = 2;

    /** Stop at the edge of a chapter */
    public static final String[] RESTRICTIONS = { "None", "Book", "Chapter", };


    /** The passage was specified to a exactly, eg Gen 1:1 */
    public static final int ACCURACY_BOOK_VERSE = 0;

    /** The passage was specified to a book and chapter (no verse), eg Gen 1 */
    public static final int ACCURACY_BOOK_CHAPTER = 1;

    /** The passage was specified to a book only (no chapter or verse), eg Gen */
    public static final int ACCURACY_BOOK_ONLY = 2;

    /** The passage was specified to a chapter and verse (no book), eg 1:1 */
    public static final int ACCURACY_CHAPTER_VERSE = 3;

    /** There was only a number so we can tell if chapter or verse (or even book) was meant */
    public static final int ACCURACY_NUMBER_ONLY = 4;

    /** The text was empty */
    public static final int ACCURACY_NONE = 5;


    /** What characters can we use to separate parts to a verse */
    public static final String VERSE_ALLOWED_DELIMS = " :.";

    /** What characters should we use to separate parts of an OSIS verse reference */
    public static final String VERSE_OSIS_DELIM = ".";

    /** What characters should we use to separate the book from the chapter */
    public static final String VERSE_PREF_DELIM1 = " ";

    /** What characters should we use to separate the chapter from the verse */
    public static final String VERSE_PREF_DELIM2 = ":";

    /** Characters that are used to indicate end of verse/chapter, part 1 */
    public static final String VERSE_END_MARK1 = "$";

    /** Characters that are used to indicate end of verse/chapter, part 2 */
    public static final String VERSE_END_MARK2 = "ff";

    /** The delimitters that can be used to prefix a book number */
    public static final String[] VERSE_NUMERIC_BOOK = { "#" };

    /** What characters can we use to separate the 2 parts to a VerseRanges */
    public static final String RANGE_ALLOWED_DELIMS = "-";

    /** What characters should we use to separate VerseRange parts */
    public static final String RANGE_PREF_DELIM = "-";

    /** What characters can we use to separate VerseRanges in a Passage */
    public static final String REF_ALLOWED_DELIMS = ",;\n\r\t";

    /** What characters should we use to separate VerseRanges in a Passage */
    public static final String REF_PREF_DELIM = ", ";

    /** What characters should we use to separate VerseRanges in a Passage */
    public static final String REF_OSIS_DELIM = " ";
}
