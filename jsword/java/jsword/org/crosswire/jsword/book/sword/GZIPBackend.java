package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.crosswire.common.activate.Activator;
import org.crosswire.common.activate.Lock;
import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyList;
import org.crosswire.jsword.passage.PassageUtil;
import org.crosswire.jsword.passage.Verse;

/**
 * A backend to read GZIPped data files.
 * 
 * <p>There are 3 files, 2 (comp and idx) are indexes into the third (text)
 * which contains the data. I'm not sure why we need 2 indexes, but that's the
 * way it is done any it is too late to change it now.
 * <p>In addition there is a separate set of files for each testament. So for
 * each read you will need to know the testament from which to read and an index
 * (derived from the book, chapter and verse) within that testament.
 * <p>All numbers are stored 2-complement, little endian.
 * <p>Then proceed as follows, at all times working on the set of files for the
 * testament in question:
 * 
 * <pre>
 * in the comp file, seek to the index * 10
 * read 10 bytes.
 * the compressed-buffer-index is the first 4 bytes (32-bit number)
 * the remaining bytes are ignored
 * 
 * in the idx file seek to compressed-buffer-index * 12
 * read 12 bytes
 * the text-buffer-index is the first 4 bytes
 * the compressed-size is the next 4 bytes
 * the uncompressed-size is the next 4 bytes
 * 
 * in the text file seek to the text-buffer-index
 * read compressed-size bytes
 * //decipher them. wont this change their size? 
 * unGZIP them and check for uncompressed-size
 * </pre>
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
public class GZIPBackend implements Backend
{
    /**
     * Simple ctor
     */
    public GZIPBackend(String path, int blockType) throws BookException
    {
        String allbutlast = path + File.separator + "ot." + UNIQUE_INDEX_ID[blockType] + "z";
        idxFile[SwordConstants.TESTAMENT_OLD] = new File(allbutlast + "s");
        textFile[SwordConstants.TESTAMENT_OLD] = new File(allbutlast + "z");
        compFile[SwordConstants.TESTAMENT_OLD] = new File(allbutlast + "v");

        allbutlast = path + File.separator + "nt." + UNIQUE_INDEX_ID[blockType] + "z";
        idxFile[SwordConstants.TESTAMENT_NEW] = new File(allbutlast + "s");
        textFile[SwordConstants.TESTAMENT_NEW] = new File(allbutlast + "z");
        compFile[SwordConstants.TESTAMENT_NEW] = new File(allbutlast + "v");

        // It is an error to be neither OT nor NT
        if (!textFile[SwordConstants.TESTAMENT_OLD].canRead()
            && !textFile[SwordConstants.TESTAMENT_NEW].canRead())
        {
            throw new BookException(Msg.MISSING_FILE, new Object[] { path });
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#activate(org.crosswire.common.activate.Lock)
     */
    public final void activate(Lock lock)
    {
        try
        {
            idxRaf[SwordConstants.TESTAMENT_OLD] = new RandomAccessFile(idxFile[SwordConstants.TESTAMENT_OLD], "r");
            textRaf[SwordConstants.TESTAMENT_OLD] = new RandomAccessFile(textFile[SwordConstants.TESTAMENT_OLD], "r");
            compRaf[SwordConstants.TESTAMENT_OLD] = new RandomAccessFile(compFile[SwordConstants.TESTAMENT_OLD], "r");
        }
        catch (FileNotFoundException ex)
        {
            // Ignore this might be NT only
        }

        try
        {
            idxRaf[SwordConstants.TESTAMENT_NEW] = new RandomAccessFile(idxFile[SwordConstants.TESTAMENT_NEW], "r");
            textRaf[SwordConstants.TESTAMENT_NEW] = new RandomAccessFile(textFile[SwordConstants.TESTAMENT_NEW], "r");
            compRaf[SwordConstants.TESTAMENT_NEW] = new RandomAccessFile(compFile[SwordConstants.TESTAMENT_NEW], "r");
        }
        catch (FileNotFoundException ex)
        {
            // Ignore this might be OT only
        }

        active = true;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#deactivate(org.crosswire.common.activate.Lock)
     */
    public final void deactivate(Lock lock)
    {
        try
        {
            idxRaf[SwordConstants.TESTAMENT_OLD].close();
            textRaf[SwordConstants.TESTAMENT_OLD].close();
            compRaf[SwordConstants.TESTAMENT_OLD].close();

            idxRaf[SwordConstants.TESTAMENT_NEW].close();
            textRaf[SwordConstants.TESTAMENT_NEW].close();
            compRaf[SwordConstants.TESTAMENT_NEW].close();
        }
        catch (IOException ex)
        {
            log.error("failed to close files", ex);
        }

        idxRaf[SwordConstants.TESTAMENT_OLD] = null;
        textRaf[SwordConstants.TESTAMENT_OLD] = null;
        compRaf[SwordConstants.TESTAMENT_OLD] = null;

        idxRaf[SwordConstants.TESTAMENT_NEW] = null;
        textRaf[SwordConstants.TESTAMENT_NEW] = null;
        compRaf[SwordConstants.TESTAMENT_NEW] = null;

        active = false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.Backend#getRawText(org.crosswire.jsword.passage.Verse)
     */
    public byte[] getRawText(Key key) throws BookException
    {
        checkActive();

        Verse verse = PassageUtil.getVerse(key);

        try
        {
            int testament = SwordConstants.getTestament(verse);
            long index = SwordConstants.getIndex(verse);

            // If this is a single testament Bible, return nothing.
            if (compRaf[testament] == null)
            {
                return new byte[0];
            }

            // 10 because we the index is 10 bytes long for each verse
            byte[] temp = SwordUtil.readRAF(compRaf[testament], index * COMP_ENTRY_SIZE, COMP_ENTRY_SIZE);

            // On occasion the index is short so just do nothing.
            if (temp == null || temp.length == 0)
            {
                return temp;
            }

            // The data is little endian - extract the start, size and endsize
            int buffernum = SwordUtil.decodeLittleEndian32AsInt(temp, 0);

            // These 2 bits of data are never read.
            int bstart = SwordUtil.decodeLittleEndian32AsInt(temp, 4);
            int bsize = SwordUtil.decodeLittleEndian16(temp, 8);

            // Can we get the data from the cache
            byte[] uncompr = null;
            if (buffernum == lastbuffernum)
            {
                uncompr = lastuncompr;
            }
            else
            {
                // Then seek using this index into the idx file
                temp = SwordUtil.readRAF(idxRaf[testament], buffernum * IDX_ENTRY_SIZE, IDX_ENTRY_SIZE);
                if (temp == null || temp.length == 0)
                {
                    return new byte[0];
                }

                long start = SwordUtil.decodeLittleEndian32(temp, 0);
                int size = SwordUtil.decodeLittleEndian32AsInt(temp, 4);
                int endsize = SwordUtil.decodeLittleEndian32AsInt(temp, 8);

                // Read from the data file.
                byte[] compressed = SwordUtil.readRAF(textRaf[testament], start, size);

                // LATER(joe): implement encryption?
                // byte[] decrypted = decrypt(compressed);

                uncompr = SwordUtil.uncompress(compressed, endsize);

                // cache the uncompressed data for next time
                lastbuffernum = buffernum;
                lastuncompr = uncompr;
            }

            // and cut out the required section.
            byte[] chopped = new byte[bsize];
            System.arraycopy(uncompr, bstart, chopped, 0, bsize);

            return chopped;

            /* The code converted from Sword looked like this, but we can do better
            // buffer number
            comp_raf[testament].seek(offset);
            long buffernum = comp_raf[testament - 1].readInt();
            buffernum = swordtoarch32(buffernum);

            // verse offset within buffer
            // long versestart =
                comp_raf[testament - 1].readInt();
            // versestart = swordtoarch32(versestart);
            // short versesize =
                comp_raf[testament - 1].readShort();
            //versesize = swordtoarch16(versesize);

            idx_raf[testament].seek(buffernum * 12);

            // compressed buffer start
            long start = idx_raf[testament - 1].readInt();
            start = swordtoarch32(start);
            
            // buffer size compressed (was long but can't use long as array index)
            int size = idx_raf[testament - 1].readInt();
            size = swordtoarch32(size);
            
            // buffer size uncompressed (was long but can't use long as array index)
            int endsize = idx_raf[testament - 1].readInt();
            endsize = swordtoarch32(endsize);
            /**/
        }
        catch (Exception ex)
        {
            throw new BookException(Msg.READ_FAIL, ex, new Object[] { verse.getName() });
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.Backend#readIndex()
     */
    public KeyList readIndex()
    {
        // PENDING(joe): refactor to get rid of this
        return null;
    }

    /**
     * Helper method so we can quickly activate ourselves on access
     */
    protected final void checkActive()
    {
        if (!active)
        {
            Activator.activate(this);
        }
    }

    /**
     * 
     */
    private int lastbuffernum = -1;

    /**
     * 
     */
    private byte[] lastuncompr = null;

    /**
     * Are we active
     */
    private boolean active = false;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(GZIPBackend.class);

    /**
     * The array of index random access files
     */
    private RandomAccessFile[] idxRaf = new RandomAccessFile[3];

    /**
     * The array of data random access files
     */
    private RandomAccessFile[] textRaf = new RandomAccessFile[3];

    /**
     * The array of compressed random access files?
     */
    private RandomAccessFile[] compRaf = new RandomAccessFile[3];

    /**
     * The array of index random access files
     */
    private File[] idxFile = new File[3];

    /**
     * The array of data random access files
     */
    private File[] textFile = new File[3];

    /**
     * The array of compressed random access files?
     */
    private File[] compFile = new File[3];

    /**
     * Yuck - they blocking method defines the initial file character.
     * The Sword original used: { 'X', 'r', 'v', 'c', 'b' }, however the values
     * we read from SwordConfig never matched up. Perhaps we need to work out
     * why.
     */
    private static final char[] UNIQUE_INDEX_ID = { 'b', 'c', 'v', };

    /**
     * How many bytes in the comp index?
     */
    private static final int COMP_ENTRY_SIZE = 10;

    /**
     * How many bytes in the idx index?
     */
    private static final int IDX_ENTRY_SIZE = 12;
}
