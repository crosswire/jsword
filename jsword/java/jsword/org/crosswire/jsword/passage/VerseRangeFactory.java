package org.crosswire.jsword.passage;

import org.apache.commons.lang.StringUtils;

/**
 * A factory that creates VerseRanges from user input.
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
 * @author DM Smith [dmsmith555 at hotmail dot com]
 * @version $Id$
 */
public final class VerseRangeFactory
{

    /**
     * prevent instantiation
     */
    private VerseRangeFactory()
    {
    }

      /**
      * Construct a VerseRange from a human readable string. For example
      * "Gen 1:1-3" in case the user does not want to have their typing
      * 'fixed' by a meddling patronizing computer.
      * @param orginal The textual representation
      * @exception NoSuchVerseException If the text can not be understood
      */
     public static VerseRange fromString(String orginal) throws NoSuchVerseException
     {
         return fromString(orginal, null);
     }

    /**
     * Construct a VerseRange from a String and a VerseRange. For example given "2:2"
     * and a basis of Gen 1:1-2 the result would be range of 1 verse starting at
     * Gen 2:2. Also given "2:2-5" and a basis of Gen 1:1-2 the result would be a
     * range of 5 verses starting at Gen 1:1.
     * <p>This constructor is different from the (String, Verse) constructor in that
     * if the basis is a range that exactly covers a chapter and the string is a
     * single number, then we assume that the number referrs to a chapter and not to
     * a verse. This allows us to have a Passage like "Gen 1,2" and have the 2
     * understood as chapter 2 and not verse 2 of Gen 1, which would have occured
     * otherwise.
     * @param original The string describing the verse e.g "2:2"
     * @param basis The verse that forms the basis by which to understand the orginal.
     * @exception NoSuchVerseException If the reference is illegal
     */
    public static VerseRange fromString(String original, VerseRange basis) throws NoSuchVerseException
    {
        String[] parts = StringUtils.split(original, VerseRange.RANGE_ALLOWED_DELIMS);

        switch (parts.length)
        {
        case 1:
            return fromText(original, parts[0], parts[0], basis);

        case 2:
            return fromText(original, parts[0], parts[1], basis);

        default:
            throw new NoSuchVerseException(Msg.RANGE_PARTS, new Object[] { VerseRange.RANGE_ALLOWED_DELIMS, original });
        }
    }

    private static VerseRange fromText(String original, String startVerseDesc, String endVerseDesc, VerseRange basis) throws NoSuchVerseException
    {
        String[] startParts = AccuracyType.tokenize(startVerseDesc);
        AccuracyType accuracyStart = AccuracyType.fromText(startParts, basis);
        Verse start = accuracyStart.createStartVerse(startVerseDesc, basis, startParts);

        String[] endParts = AccuracyType.tokenize(endVerseDesc);
        AccuracyType accuracyEnd = AccuracyType.fromText(endParts, accuracyStart, basis);
        Verse end = accuracyEnd.createEndVerse(endVerseDesc, start, endParts);

        return new VerseRange(original, start, end);
    }
}
