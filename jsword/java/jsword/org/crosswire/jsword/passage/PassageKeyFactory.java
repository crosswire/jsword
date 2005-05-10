/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
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
package org.crosswire.jsword.passage;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

/**
 * An implementation of KeyFactory that works for most Bibles that contain all
 * the verses in the Bible.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class PassageKeyFactory implements KeyFactory
{
    /**
     * Optimize the Passage for speed
     */
    public static final int SPEED = 0;

    /**
     * Optimize the Passage for speed
     */
    public static final int WRITE_SPEED = 1;

    /**
     * Optimize the Passage for size
     */
    public static final int SIZE = 2;

    /**
     * Optimize the Passage for a mix
     */
    public static final int MIX = 3;

    /**
     * Optimize the Passage for tally operations
     */
    public static final int TALLY = 4;

    /**
     * This class implements a Singleton pattern. So the ctor is private
     */
    private PassageKeyFactory()
    {
    }

    public static KeyFactory instance()
    {
        return keyf;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyFactory#createEmptyKeyList()
     */
    public Key createEmptyKeyList()
    {
        return createPassage();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyFactory#createKey(java.lang.String)
     */
    public Key getKey(String name) throws NoSuchKeyException
    {
        return createPassage(name);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyFactory#getGlobalKeyList()
     */
    public Key getGlobalKeyList()
    {
        try
        {
            if (whole == null)
            {
                whole = new ReadOnlyPassage(createPassage("Gen 1:1-Rev 22:21"), true); //$NON-NLS-1$
            }

            return whole;
        }
        catch (Exception ex)
        {
            assert false : ex;
            return createPassage();
        }
    }

    /**
     * Create an empty Passage using the default type.
     * @return The new Passage
     */
    protected Passage createPassage()
    {
        return createPassage(defaultType);
    }

    /**
     * Create an empty Passage using the default type. And set the
     * contents of the Passage using a string.
     * @param name The Passage description.
     * @return The new Passage
     * @throws NoSuchVerseException if the name is invalid
     */
    protected Passage createPassage(String name) throws NoSuchVerseException
    {
        if (name == null)
        {
            createPassage(defaultType);
        }

        return createPassage(defaultType, name);
    }

    /**
     * Create an empty Passage using a specified type.
     * @param type The type of Passage to create.
     * @return The new Passage
     * @see PassageKeyFactory#setDefaultPassage(int)
     */
    protected Passage createPassage(int type)
    {
        switch (type)
        {
        case PassageKeyFactory.MIX:
            return new RangedPassage();

        case PassageKeyFactory.WRITE_SPEED:
            return new BitwisePassage();

        case PassageKeyFactory.SPEED:
            return new RocketPassage();

        case PassageKeyFactory.SIZE:
            return new DistinctPassage();

        case PassageKeyFactory.TALLY:
            return new PassageTally();

        default :
            throw new IllegalArgumentException(Integer.toString(type));
        }
    }

    /**
     * Create an empty Passage using a specified type. And set the
     * contents of the Passage using a string.
     * @param type The type of Passage to create.
     * @param name The Passage description.
     * @return The new Passage
     * @throws NoSuchVerseException if the name is invalid
     * @see PassageKeyFactory#setDefaultPassage(int)
     */
    protected Passage createPassage(int type, String name) throws NoSuchVerseException
    {
        if (name == null)
        {
            createPassage(type);
        }

        switch (type)
        {
        case PassageKeyFactory.MIX:
            return new RangedPassage(name);

        case PassageKeyFactory.WRITE_SPEED:
            return new BitwisePassage(name);

        case PassageKeyFactory.SPEED:
            return new RocketPassage(name);

        case PassageKeyFactory.SIZE:
            return new DistinctPassage(name);

        case PassageKeyFactory.TALLY:
            return new PassageTally(name);

        default:
            throw new IllegalArgumentException(Integer.toString(type));
        }
    }

    /**
     * Set the default reference type. Must be one of:<ul>
     * <li>PassageFactory.SPEED
     * <li>PassageFactory.WRITE_SPEED
     * <li>PassageFactory.SIZE
     * <li>PassageFactory.MIX
     * <li>PassageFactory.TALLY
     * </ul>
     * @param defaultType The new default type.
     */
    public static void setDefaultPassage(int defaultType)
    {
        PassageKeyFactory.defaultType = defaultType;
    }

    /**
     * Get the default reference type.
     * @return default_type The new default type.
     * @see PassageKeyFactory#setDefaultPassage
     */
    public static int getDefaultPassage()
    {
        return defaultType;
    }

    /**
     * Get a new Passage based on another Passage that synchronizes all access
     * to its members.
     * @param ref The passage to synchronize
     * @return A new synchronized passage that proxies requests to the original
     */
    public static Passage getSynchronizedPassage(Passage ref)
    {
        return new SynchronizedPassage(ref);
    }

    /**
     * Get a new Passage based on another Passage that synchronizes all access
     * to its members.
     * @param ref The passage to synchronize
     * @param ignore Do we throw up if someone tries to change us
     * @return A new synchronized passage that proxies requests to the original
     */
    public static Passage getReadOnlyPassage(Passage ref, boolean ignore)
    {
        return new ReadOnlyPassage(ref, ignore);
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
        int ranges = ref.countRanges(RestrictionType.NONE);

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

            Iterator it = ref.iterator();
            while (it.hasNext())
            {
                Verse verse = (Verse) it.next();
                int ord = verse.getOrdinal();

                // Which byte should we be altering
                int idx0 = (ord / 8) + index;

                // Which bit within that byte (0-7)
                int bit = (ord % 8) - 1;

                buffer[idx0] |= 1 << bit;
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
            Iterator it = ref.iterator();
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
                           + binarySize(BibleInfo.versesInBible() / 2)
                           + (2 * ranges * binarySize(BibleInfo.versesInBible()));
            byte[] buffer = new byte[array_size];
            int index = 0;

            // write the Passage type and the number of ranges
            index += toBinary(buffer, index, AbstractPassage.RANGED, AbstractPassage.METHOD_COUNT);
            index += toBinary(buffer, index, ranges, BibleInfo.versesInBible() / 2);

            // write the verse ordinals in a loop
            Iterator it = ref.rangeIterator(RestrictionType.NONE);
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
        Passage ref = (Passage) keyf.createEmptyKeyList();

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
            for (int ord = 1; ord <= BibleInfo.versesInBible(); ord++)
            {
                // Which byte should we be viewing
                int idx0 = (ord / 8) + index[0];

                // Which bit within that byte (0-7)
                int bit = (ord % 8) - 1;

                if ((buffer[idx0] & (1 << bit)) != 0)
                {
                    ref.add(new Verse(ord));
                }
            }
            // index gets left behind here, but we dont care
            break;

        case AbstractPassage.DISTINCT:
            int verses = fromBinary(buffer, index, BibleInfo.versesInBible());
            for (int i = 0; i < verses; i++)
            {
                int ord = fromBinary(buffer, index, BibleInfo.versesInBible());
                ref.add(new Verse(ord));
            }
            break;

        case AbstractPassage.RANGED:
            int ranges = fromBinary(buffer, index, BibleInfo.versesInBible() / 2);
            for (int i = 0; i < ranges; i++)
            {
                int ord = fromBinary(buffer, index, BibleInfo.versesInBible());
                int len = fromBinary(buffer, index, BibleInfo.versesInBible());
                ref.add(RestrictionType.NONE.toRange(new Verse(ord), len));
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
     * Read a passage from a given stream
     * @param in The stream to read from
     * @return a newly built Passage
     * @throws IOException If there was troule reading the stream
     * @throws NoSuchVerseException if the data was not a valid passage
     */
    public static Passage readPassage(Reader in) throws IOException, NoSuchVerseException
    {
        Passage ref = (Passage) keyf.createEmptyKeyList();
        ref.readDescription(in);
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
        assert number >= 0 : "No -ve output " + number; //$NON-NLS-1$
        assert number <= max : "number " + number + " > max " + max; //$NON-NLS-1$ //$NON-NLS-2$

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
     * How we create Passages
     */
    private static KeyFactory keyf = new PassageKeyFactory();

    /**
     * The cached whole Bible passage
     */
    private static Passage whole;

    /**
     * The default type
     */
    private static int defaultType = PassageKeyFactory.SPEED;
}
