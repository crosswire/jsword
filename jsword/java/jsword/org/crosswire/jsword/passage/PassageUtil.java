
package org.crosswire.jsword.passage;

import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.MsgBase;

/**
 * A Utility class containing various static methods.
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
public class PassageUtil implements PassageConstants
{
    /**
     * The default Blur settings. This is not explicitly used by any of
     * the blur methods. It simply provides a convienient place to store
     * a default blur setting if desired.
     * @param value The new default blur setting
     */
    public static void setBlurRestriction(int value)
    {
        if (!PassageUtil.isValidBlurRestriction(value))
        {
            throw new IllegalArgumentException(getResource(Msg.ERROR_BLUR));
        }

        blur = value;
    }

    /**
     * The default Blur settings. This is not explicitly used by any of the
     * blur methods. It simply provides a convienient place to store a default
     * blur setting if desired.
     * @return The current default blur setting
     */
    public static int getBlurRestriction()
    {
        return blur;
    }

    /**
     * Is the given restriction a valid one?
     * @param test The restriction to be tested for validity
     * @return True if the number is OK, False otherwise
     */
    public static final boolean isValidBlurRestriction(int test)
    {
        switch (test)
        {
        case RESTRICT_BOOK:
        case RESTRICT_CHAPTER:
        case RESTRICT_NONE:
            return true;

        default:
            return false;
        }
    }

    /**
     * The allowed Blur settings
     * @return A string array containing the blur settings
     */
    public static String[] getBlurRestrictions()
    {
        return RESTRICTIONS;
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
        case CASE_LOWER:
        case CASE_MIXED:
        case CASE_SENTANCE:
        case CASE_UPPER:
            return true;

        default:
            return false;
        }
    }

    /**
     * The allowed Case settings
     * @return A string array containing the case settings
     */
    public static String[] getCases()
    {
        return CASES;
    }

    /**
     * Is the given accuracy a valid one?
     * @param test The accuracy to be tested for validity
     * @return True if the number is OK, False otherwise
     */
    public static final boolean isValidAccuracy(int test)
    {
        switch (test)
        {
        case ACCURACY_BOOK_VERSE:
        case ACCURACY_BOOK_CHAPTER:
        case ACCURACY_BOOK_ONLY:
        case ACCURACY_CHAPTER_VERSE:
        case ACCURACY_NUMBER_ONLY:
        case ACCURACY_NONE:
            return true;

        default:
            return false;
        }
    }

    /**
     * Do we remember the original string used to configure us?
     * @param persistent_naming True to keep the old string
     *        False (default) to generate a new better one
     */
    public static final void setPersistentNaming(boolean persistent_naming)
    {
        PassageUtil.persistent_naming = persistent_naming;
    }

    /**
     * Do we remember the original string used to configure us?
     * @return True if we keep the old string
     *         False (default) if we generate a new better one
     */
    public static final boolean isPersistentNaming()
    {
        return persistent_naming;
    }

    /**
     * By default do we remember the original string used to configure us?
     * @return false getDefaultPersistentNaming() is always false
     */
    public static final boolean getDefaultPersistentNaming()
    {
        return false;
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
        if (word == null)
        {
            throw new NullPointerException();
        }

        // Blank word
        if (word.equals(""))
        {
            return CASE_LOWER;
        }

        // Lower case?
        if (word.equals(word.toLowerCase()))
        {
            return CASE_LOWER;
        }

        // Upper case?
        // A string length of 1 is no good ('I' or 'A' is sentance case)
        if (word.equals(word.toUpperCase()) && word.length() != 1)
        {
            return CASE_UPPER;
        }

        // If initial is lower then it must be mixed
        if (Character.isLowerCase(word.charAt(0)))
        {
            return CASE_MIXED;
        }

        // Hack the only real caseMixed is LORD's
        // And we don't want to bother sorting out Tubal-Cain
        // as CASE_SENTANCE, so for now ...
        if (word.equals("LORD's"))
        {
            return CASE_MIXED;
        }

        // So ...
        return CASE_SENTANCE;
    }

    /**
     * Set the case of the specified word. This section needs to have more
     * thought from a localization point of view.
     * @param word The word to be manipulated
     * @param new_case LOWER, SENTANCE, UPPER or MIXED
     * @return The altered word
     * @exception IllegalArgumentException If the case is not between 0 and 3.
     * @exception IllegalArgumentException For MIXED if the word is not LORD's
     */
    public static String setCase(String word, int new_case)
    {
        int index = 0;

        switch (new_case)
        {
        case CASE_LOWER:
            return word.toLowerCase();

        case CASE_UPPER:
            return word.toUpperCase();

        case CASE_SENTANCE:
            index = word.indexOf('-');
            if (index == -1)
            {
                return toSentanceCase(word);
            }

            // So there is a "-", however first some exceptions
            if (word.toLowerCase().equals("maher-shalal-hash-baz"))
            {
                return "Maher-Shalal-Hash-Baz";
            }

            if (word.toLowerCase().equals("no-one"))
            {
                return "No-one";
            }

            if (word.substring(0, 4).toLowerCase().equals("god-"))
            {
                return toSentanceCase(word);
            }

            // So cut by the -
            return toSentanceCase(word.substring(0, index))
                   + "-" + toSentanceCase(word.substring(index+1));

        case CASE_MIXED:
            if (word.toLowerCase().equals("lord's"))
            {
                return "LORD's";
            }
            // This should not happen
            throw new IllegalArgumentException(getResource(Msg.ERROR_MIXED));

        default:
            throw new IllegalArgumentException(getResource(Msg.ERROR_BADCASE));
        }
    }

    /**
     * Change to sentance case - ie first character in caps, the rest in lower.
     * @param word The word to be manipulated
     * @return The altered word
     */
    public static String toSentanceCase(String word)
    {
        if (word == null)
        {
            throw new NullPointerException();
        }

        if (word.equals(""))
        {
            return "";
        }

        return "" + Character.toUpperCase(word.charAt(0))
                  + word.substring(1).toLowerCase();
    }

    /**
     * Convert us to a binary representation.
     * There are sme distinctly endianist happenings here, but that is OK
     * because we are reading the stuff we write here just below.
     * @param ref The Passage to convert
     * @return a byte array
     */
    public static byte[] toBinaryRepresentation(Passage ref)
    {
        // store these locally we use them so often
        int verses = ref.countVerses();
        int ranges = ref.countRanges();

        // the size in bytes of teach storage method
        int bitwise_size = BibleInfo.versesInBible() / 8;
        int ranged_size =  (ranges * 4) + 1;
        int distinct_size = (verses * 2) + 1;

        // if bitwise is equal smallest
        if (bitwise_size <= ranged_size && bitwise_size <= distinct_size)
        {
            int array_size = binarySize(AbstractPassage.METHOD_COUNT)
                           + (BibleInfo.versesInBible() / 8) + 1;
            byte[] buffer = new byte[array_size];
            int index = 0;

            index += toBinary(buffer, index, AbstractPassage.BITWISE, AbstractPassage.METHOD_COUNT);

            Iterator it = ref.verseIterator();
            while (it.hasNext())
            {
                Verse verse = (Verse) it.next();
                int ord = verse.getOrdinal();

                // Which byte should we be altering
                int idx0 = (ord / 8) + index;

                // Which bit within that byte (0-7)
                int bit = (ord % 8) - 1;

                buffer[idx0] |= (1 << bit);
            }

            return buffer;
        }

        // if distinct is not bigger than ranged
        else if (distinct_size <= ranged_size)
        {
            int array_size = binarySize(AbstractPassage.METHOD_COUNT)
                           + binarySize(BibleInfo.versesInBible())
                           + (verses * binarySize(BibleInfo.versesInBible()));
            byte[] buffer = new byte[array_size];
            int index = 0;

            // write the Passage type and the number of verses. There must be
            // less than 2**16 verses
            index += toBinary(buffer, index, AbstractPassage.DISTINCT, AbstractPassage.METHOD_COUNT);
            index += toBinary(buffer, index, verses, BibleInfo.versesInBible());

            // write the verse ordinals in a loop
            Iterator it = ref.verseIterator();
            while (it.hasNext())
            {
                Verse verse = (Verse) it.next();
                int ord = verse.getOrdinal();
                index += toBinary(buffer, index, ord, BibleInfo.versesInBible());
            }

            return buffer;
        }

        // otherwise use ranges
        else
        {
            int array_size = binarySize(AbstractPassage.METHOD_COUNT)
                           + binarySize(BibleInfo.versesInBible()/2)
                           + (2 * ranges * binarySize(BibleInfo.versesInBible()));
            byte[] buffer = new byte[array_size];
            int index = 0;

            // write the Passage type and the number of ranges
            index += toBinary(buffer, index, AbstractPassage.RANGED, AbstractPassage.METHOD_COUNT);
            index += toBinary(buffer, index, ranges, BibleInfo.versesInBible()/2);

            // write the verse ordinals in a loop
            Iterator it = ref.rangeIterator();
            while (it.hasNext())
            {
                VerseRange range = (VerseRange) it.next();
                index += toBinary(buffer, index, range.getStart().getOrdinal(), BibleInfo.versesInBible());
                index += toBinary(buffer, index, range.getVerseCount(), BibleInfo.versesInBible());
            }

            // chop to size
            return buffer;
        }
    }

    /**
     * Write out the object to the given ObjectOutputStream
     * @param buffer The stream to read our state from
     * @return The converted Passage
     * @throws NoSuchVerseException If the buffer is invalid
     */
    public static Passage fromBinaryRepresentation(byte[] buffer) throws NoSuchVerseException
    {
        Passage ref = PassageFactory.createPassage();

        // Some speedups
        AbstractPassage aref = null;
        if (ref instanceof AbstractPassage)
        {
            aref = (AbstractPassage) ref;
            aref.raiseEventSuppresion();
            aref.raiseNormalizeProtection();
        }

        int[] index = new int[] { 0 };
        int type = fromBinary(buffer, index, AbstractPassage.METHOD_COUNT);

        switch (type)
        {
        case AbstractPassage.BITWISE:
            for (int ord=1; ord<=BibleInfo.versesInBible(); ord++)
            {
                // Which byte should we be viewing
                int idx0 = (ord / 8) + index[0];

                // Which bit within that byte (0-7)
                int bit = (ord % 8) - 1;

                if ((buffer[idx0] & (1 << bit)) != 0)
                    ref.add(new Verse(ord));
            }
            // index gets left behind here, but we dont care
            break;

        case AbstractPassage.DISTINCT:
            int verses = fromBinary(buffer, index, BibleInfo.versesInBible());
            for (int i=0; i<verses; i++)
            {
                int ord = fromBinary(buffer, index, BibleInfo.versesInBible());
                ref.add(new Verse(ord));
            }
            break;

        case AbstractPassage.RANGED:
            int ranges = fromBinary(buffer, index, BibleInfo.versesInBible()/2);
            for (int i=0; i<ranges; i++)
            {
                int ord = fromBinary(buffer, index, BibleInfo.versesInBible());
                int len = fromBinary(buffer, index, BibleInfo.versesInBible());
                ref.add(new VerseRange(new Verse(ord), len));
            }
            break;

        default:
            throw new NoSuchVerseException(Msg.PASSAGE_UNKNOWN);
        }

        // Some speedups
        if (aref != null)
        {
            aref.lowerEventSuppresionAndTest();
            aref.lowerNormalizeProtection();
        }

        return ref;
    }

    /**
     * Write to buffer (starting at index) the given number using a set of bytes
     * as required by the max possible value for the number
     * @param max The number to write
     * @return The number of bytes needed
     */
    protected static int binarySize(int max)
    {
        // 1 byte (2^8)
        if (max < 256)
        {
            return 1;
        }

        // 2 bytes (2^16)
        if (max < 65536)
        {
            return 2;
        }

        // 3 bytes (2^24)
        if (max < 16777216)
        {
            return 3;
        }

        // 4 bytes (2^32)
        return 4;
    }

    /**
     * Write to buffer (starting at index) the given number using a set of bytes
     * as required by the max possible value for the number
     * @param buffer Where to write to
     * @param index The offset to start at
     * @param number The number to write
     * @param max The max size
     * @return The number of bytes written
     */
    protected static int toBinary(byte[] buffer, int index, int number, int max)
    {
        if (number < 0)
        {
            throw new IllegalArgumentException("No -ve output "+number);
        }

        if (number > max)
        {
            throw new IllegalArgumentException("number "+number+" > max "+max);
        }

        // 1 byte (2^8)
        if (max < 256)
        {
            buffer[index] = (byte) number;
            return 1;
        }

        // 2 bytes (2^16)
        if (max < 65536)
        {
            buffer[index + 0] = (byte) (number >>> 8);
            buffer[index + 1] = (byte) (number >>> 0);
            return 2;
        }

        // 3 bytes (2^24)
        if (max < 16777216)
        {
            buffer[index + 0] = (byte) (number >>> 16);
            buffer[index + 1] = (byte) (number >>> 8);
            buffer[index + 2] = (byte) (number >>> 0);
            return 3;
        }

        // 4 bytes (2^32)
        buffer[index + 0] = (byte) (number >>> 24);
        buffer[index + 1] = (byte) (number >>> 16);
        buffer[index + 2] = (byte) (number >>> 8);
        buffer[index + 3] = (byte) (number >>> 0);
        return 4;
    }

    /**
     * Read and return an int from the buffer (starting at index[0]) using a set
     * of bytes as required by the max possible value for the number, and
     * incrementing index[0] by that number of bytes.
     * @param buffer The buffer to read from
     * @param index The offset to start at
     * @param max The max nuber of bytes to read
     * @return The converted number
     */
    protected static int fromBinary(byte[] buffer, int[] index, int max)
    {
        // Am i nieve in thinking that & 0x000000ff turns int -1 into 255?.

        // 1 byte (2^8)
        int b0 = buffer[index[0]++] & 0x000000ff;
        if (max < 256)
        {
            return b0;
        }

        // 2 bytes (2^16)
        int b1 = buffer[index[0]++] & 0x000000ff;
        if (max < 65536)
        {
            return (b0 << 8) + (b1 << 0);
        }

        // 3 bytes (2^24)
        int b2 = buffer[index[0]++] & 0x000000ff;
        if (max < 16777216)
        {
            return (b0 << 16) + (b1 << 8) + (b2 << 0);
        }

        // 4 bytes (2^32)
        int b3 = buffer[index[0]++] & 0x000000ff;
        return (b0 << 24) + (b1 << 16) + (b2 << 8) + (b3 << 0);
    }

    /**
     * Take a string and parse it into an Array of Strings. I'm not sure that
     * this is the correct place to put this.
     * @param command The string to parse.
     * @param delim A string containing the spacing characters.
     * @return The string array
     */
    protected static final String[] tokenize(String command, String delim)
    {
        StringTokenizer tokenize = new StringTokenizer(command, delim);
        String[] args = new String[tokenize.countTokens()];
        int argc = 0;

        while (tokenize.hasMoreTokens())
        {
            args[argc++] = tokenize.nextToken();
        }

        return args;
    }

    /**
     * This is simply a convenience function to wrap Character.isLetter()
     * @param text The string to be parsed
     * @return true if the string contains letters
     */
    protected static boolean containsLetter(String text)
    {
        for (int i=0; i<text.length(); i++)
        {
            if (Character.isLetter(text.charAt(i))) return true;
        }

        return false;
    }

    /**
     * Utility that enables us to have a single resource file for all the
     * passage classes
     * @param id The resource id to fetch
     * @return The String from the resource file
     */
    protected static String getResource(String id)
    {
        try
        {
            return res.getString(id);
        }
        catch (MissingResourceException ex)
        {
            return "Missing resource for: "+id;
        }
    }

    /**
     * Utility that enables us to have a single resource file for all the
     * passage classes
     * @param id The resource id to fetch
     * @return The String from the resource file
     */
    protected static String getResource(MsgBase id)
    {
        try
        {
            if (res != null)
            {
                return res.getString(id.toString());
            }
            else
            {
                return "Missing passage resource bundle";
            }
        }
        catch (MissingResourceException ex)
        {
            return "Missing resource for: "+id;
        }
    }

    /**
     * The ResourceBundle containing the name customizations
     */
    private static ResourceBundle res = null;

    /**
     * Do we store the original string?
     */
    private static boolean persistent_naming = getDefaultPersistentNaming();

    /**
     * The blur restriction
     */
    private static int blur = PassageConstants.RESTRICT_CHAPTER;

    /**
     *  The log stream
     */
    private static final Logger log = Logger.getLogger(PassageUtil.class);

    /**
     * Setup the resources
     */
    static
    {
        try
        {
            res = ResourceBundle.getBundle("org.crosswire.jsword.passage.Passage");
        }
        catch (MissingResourceException ex)
        {
            log.debug("No custom resource found: "+ex.getMessage());
        }
    }
}
