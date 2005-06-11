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
package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.crosswire.common.activate.Activator;
import org.crosswire.common.activate.Lock;
import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.Verse;

/**
 * A backend to read GZIPped data files. While the text file contains
 * data compressed with GZIP, it cannot be uncompressed using a stand
 * alone zip utility, such as WinZip or gzip. The reason for this is
 * that the data file is a concatenation of blocks of compressed data.
 * 
 * <p>The blocks can either be "b", book (aka testament); "c", chapter
 * or "v", verse. The choice is a matter of trade offs. The program needs
 * to uncompress a block into memory. Having it at the book level is
 * very memory expensive. Having it at the verse level is very disk
 * expensive, but takes the least amount of memory. The most common is
 * chapter.
 * 
 * <p>In order to find the data in the text file, we need to find the 
 * block. The first index (comp) is used for this. Each verse is indexed
 * to a tuple (block number, verse start, verse size). This data allows
 * us to find the correct block, and to extract the verse from the
 * uncompressed block, but it does not help us uncompress the block.
 * 
 * <p>Once the block is known, then the next index (idx) gives the location
 * of the compressed block, its compressed size and its uncompressed size.
 * 
 * <p>There are 3 files for each testament, 2 (comp and idx) are indexes into
 * the third (text) which contains the data. The key into each index is the
 * verse index within that testament, which is determined by book, chapter
 * and verse of that key.
 * 
 * <p>All numbers are stored 2-complement, little endian.
 * <p>Then proceed as follows, at all times working on the set of files for the
 * testament in question:
 * 
 * <pre>
 * in the comp file, seek to the index * 10
 * read 10 bytes.
 * the block-index is the first 4 bytes (32-bit number)
 * the next bytes are the verse offset and length of the uncompressed block.
 * 
 * in the idx file seek to block-index * 12
 * read 12 bytes
 * the text-block-index is the first 4 bytes
 * the data-size is the next 4 bytes
 * the uncompressed-size is the next 4 bytes
 * 
 * in the text file seek to the text-block-index
 * read data-size bytes
 * //decipher them if they are encrypted
 * unGZIP them into a byte array of uncompressed-size
 * </pre>
 * 
 * TODO(DM): Testament 0 is used to index an README file for the bible.
 * At this time it is ignored.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class GZIPBackend extends AbstractBackend
{
    private static final String SUFFIX_COMP = "v"; //$NON-NLS-1$
    private static final String SUFFIX_INDEX = "s"; //$NON-NLS-1$
    private static final String SUFFIX_PART1 = "z"; //$NON-NLS-1$
    private static final String SUFFIX_TEXT = "z"; //$NON-NLS-1$

    /**
     * Simple ctor
     */
    public GZIPBackend(SwordBookMetaData sbmd, File rootPath, BlockType blockType) throws BookException
    {
        super(sbmd, rootPath);

        String dataPath = sbmd.getProperty(ConfigEntryType.DATA_PATH);
        File baseurl = new File(rootPath, dataPath);
        String path = baseurl.getAbsolutePath();
        String otAllButLast = path + File.separator + SwordConstants.FILE_OT + '.' + blockType.getIndicator() + SUFFIX_PART1;
        idxFile[SwordConstants.TESTAMENT_OLD] = new File(otAllButLast + SUFFIX_INDEX);
        textFile[SwordConstants.TESTAMENT_OLD] = new File(otAllButLast + SUFFIX_TEXT);
        compFile[SwordConstants.TESTAMENT_OLD] = new File(otAllButLast + SUFFIX_COMP);

        String ntAllButLast = path + File.separator + SwordConstants.FILE_NT + '.' + blockType.getIndicator() + SUFFIX_PART1;
        idxFile[SwordConstants.TESTAMENT_NEW] = new File(ntAllButLast + SUFFIX_INDEX);
        textFile[SwordConstants.TESTAMENT_NEW] = new File(ntAllButLast + SUFFIX_TEXT);
        compFile[SwordConstants.TESTAMENT_NEW] = new File(ntAllButLast + SUFFIX_COMP);

        // It is an error to be neither OT nor NT
        if (!textFile[SwordConstants.TESTAMENT_OLD].canRead()
            && !textFile[SwordConstants.TESTAMENT_NEW].canRead())
        {
            log.error("Failed to find OT or NT files: '" + otAllButLast + SUFFIX_TEXT + "' and '" + ntAllButLast + SUFFIX_TEXT + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
            idxRaf[SwordConstants.TESTAMENT_OLD] = new RandomAccessFile(idxFile[SwordConstants.TESTAMENT_OLD], FileUtil.MODE_READ);
            textRaf[SwordConstants.TESTAMENT_OLD] = new RandomAccessFile(textFile[SwordConstants.TESTAMENT_OLD], FileUtil.MODE_READ);
            compRaf[SwordConstants.TESTAMENT_OLD] = new RandomAccessFile(compFile[SwordConstants.TESTAMENT_OLD], FileUtil.MODE_READ);
        }
        catch (FileNotFoundException ex)
        {
            // Ignore this might be NT only
        }

        try
        {
            idxRaf[SwordConstants.TESTAMENT_NEW] = new RandomAccessFile(idxFile[SwordConstants.TESTAMENT_NEW], FileUtil.MODE_READ);
            textRaf[SwordConstants.TESTAMENT_NEW] = new RandomAccessFile(textFile[SwordConstants.TESTAMENT_NEW], FileUtil.MODE_READ);
            compRaf[SwordConstants.TESTAMENT_NEW] = new RandomAccessFile(compFile[SwordConstants.TESTAMENT_NEW], FileUtil.MODE_READ);
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
            idxRaf[SwordConstants.TESTAMENT_NEW].close();
            textRaf[SwordConstants.TESTAMENT_NEW].close();
            compRaf[SwordConstants.TESTAMENT_NEW].close();
        }
        catch (IOException ex)
        {
            log.error("failed to close nt files", ex); //$NON-NLS-1$
        }
        catch (NullPointerException ex)
        {
            // ignore this might be OT only
        }
        finally
        {
            idxRaf[SwordConstants.TESTAMENT_NEW] = null;
            textRaf[SwordConstants.TESTAMENT_NEW] = null;
            compRaf[SwordConstants.TESTAMENT_NEW] = null;
        }

        try
        {
            idxRaf[SwordConstants.TESTAMENT_OLD].close();
            textRaf[SwordConstants.TESTAMENT_OLD].close();
            compRaf[SwordConstants.TESTAMENT_OLD].close();

        }
        catch (IOException ex)
        {
            log.error("failed to close ot files", ex); //$NON-NLS-1$
        }
        catch (NullPointerException ex)
        {
            // ignore this might be NT only
        }
        finally
        {
            idxRaf[SwordConstants.TESTAMENT_OLD] = null;
            textRaf[SwordConstants.TESTAMENT_OLD] = null;
            compRaf[SwordConstants.TESTAMENT_OLD] = null;
        }

        active = false;
    }

    /*
     * (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#getRawText(org.crosswire.jsword.passage.Key, java.lang.String)
     */
    public String getRawText(Key key) throws BookException
    {
        checkActive();

        String charset = getBookMetaData().getBookCharset();

        Verse verse = KeyUtil.getVerse(key);

        try
        {
            int testament = SwordConstants.getTestament(verse);
            int index = SwordConstants.getIndex(verse);

            // If Bible does not contain the desired testament, return nothing.
            if (compRaf[testament] == null)
            {
                return ""; //$NON-NLS-1$
            }

            // 10 because we the index is 10 bytes long for each verse
            byte[] temp = SwordUtil.readRAF(compRaf[testament], index * COMP_ENTRY_SIZE, COMP_ENTRY_SIZE);

            // If the Bible does not contain the desired verse, return nothing.
            // Some Bibles have different versification, so the requested verse
            // may not exist.
            if (temp == null || temp.length == 0)
            {
                return ""; //$NON-NLS-1$
            }

            // The data is little endian - extract the blockNum, verseStart and verseSize
            int blockNum = SwordUtil.decodeLittleEndian32(temp, 0);
            int verseStart = SwordUtil.decodeLittleEndian32(temp, 4);
            int verseSize = SwordUtil.decodeLittleEndian16(temp, 8);

            // Can we get the data from the cache
            byte[] uncompressed = null;
            if (blockNum == lastBlockNum && testament == lastTestament)
            {
                uncompressed = lastUncompressed;
            }
            else
            {
                // Then seek using this index into the idx file
                temp = SwordUtil.readRAF(idxRaf[testament], blockNum * IDX_ENTRY_SIZE, IDX_ENTRY_SIZE);
                if (temp == null || temp.length == 0)
                {
                    return ""; //$NON-NLS-1$
                }

                int blockStart = SwordUtil.decodeLittleEndian32(temp, 0);
                int blockSize = SwordUtil.decodeLittleEndian32(temp, 4);
                int uncompressedSize = SwordUtil.decodeLittleEndian32(temp, 8);

                // Read from the data file.
                byte[] data = SwordUtil.readRAF(textRaf[testament], blockStart, blockSize);

                decipher(data);

                uncompressed = SwordUtil.uncompress(data, uncompressedSize);

                // cache the uncompressed data for next time
                lastBlockNum = blockNum;
                lastTestament = testament;
                lastUncompressed = uncompressed;
            }

            // and cut out the required section.
            byte[] chopped = new byte[verseSize];
            System.arraycopy(uncompressed, verseStart, chopped, 0, verseSize);

            return SwordUtil.decode(key, chopped, charset);

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
     * @see org.crosswire.jsword.book.sword.AbstractBackend#readIndex()
     */
    public Key readIndex()
    {
        // PENDING(joe): refactor to get rid of this
        return null;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#isSupported()
     */
    public boolean isSupported()
    {
        return true;
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
    private int lastTestament = -1;

    /**
     * 
     */
    private int lastBlockNum = -1;

    /**
     * 
     */
    private byte[] lastUncompressed;

    /**
     * Are we active
     */
    private boolean active;

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
     * How many bytes in the comp index?
     */
    private static final int COMP_ENTRY_SIZE = 10;

    /**
     * How many bytes in the idx index?
     */
    private static final int IDX_ENTRY_SIZE = 12;
}
