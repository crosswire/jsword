/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
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
import java.net.URI;

import org.crosswire.common.activate.Activator;
import org.crosswire.common.activate.Lock;
import org.crosswire.common.compress.CompressorType;
import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.versification.Testament;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;

/**
 * A backend to read compressed data verse based files. While the text file
 * contains data compressed with ZIP or LZSS, it cannot be uncompressed using a
 * stand alone zip utility, such as WinZip or gzip. The reason for this is that
 * the data file is a concatenation of blocks of compressed data.
 * 
 * <p>
 * The blocks can either be "b", book (aka testament); "c", chapter or "v",
 * verse. The choice is a matter of trade offs. The program needs to uncompress
 * a block into memory. Having it at the book level is very memory expensive.
 * Having it at the verse level is very disk expensive, but takes the least
 * amount of memory. The most common is chapter.
 * </p>
 * 
 * <p>
 * In order to find the data in the text file, we need to find the block. The
 * first index (comp) is used for this. Each verse is indexed to a tuple (block
 * number, verse start, verse size). This data allows us to find the correct
 * block, and to extract the verse from the uncompressed block, but it does not
 * help us uncompress the block.
 * </p>
 * 
 * <p>
 * Once the block is known, then the next index (idx) gives the location of the
 * compressed block, its compressed size and its uncompressed size.
 * </p>
 * 
 * <p>
 * There are 3 files for each testament, 2 (comp and idx) are indexes into the
 * third (text) which contains the data. The key into each index is the verse
 * index within that testament, which is determined by book, chapter and verse
 * of that key.
 * </p>
 * 
 * <p>
 * All numbers are stored 2-complement, little endian.
 * </p>
 * <p>
 * Then proceed as follows, at all times working on the set of files for the
 * testament in question:
 * </p>
 * 
 * <pre>
 * in the comp file, seek to the index * 10
 * read 10 bytes.
 * the block-index is the first 4 bytes (32-bit number)
 * the next bytes are the verse offset and length of the uncompressed block.
 * in the idx file seek to block-index * 12
 * read 12 bytes
 * the text-block-index is the first 4 bytes
 * the data-size is the next 4 bytes
 * the uncompressed-size is the next 4 bytes
 * in the text file seek to the text-block-index
 * read data-size bytes
 * decipher them if they are encrypted
 * unGZIP them into a byte uncompressed-size
 * </pre>
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class ZVerseBackend extends AbstractBackend {
    private static final String SUFFIX_COMP = "v";
    private static final String SUFFIX_INDEX = "s";
    private static final String SUFFIX_PART1 = "z";
    private static final String SUFFIX_TEXT = "z";

    /**
     * Simple ctor
     */
    public ZVerseBackend(SwordBookMetaData sbmd, BlockType blockType) {
        super(sbmd);
        this.blockType = blockType;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#activate(org.crosswire.common.activate.Lock)
     */
    public final void activate(Lock lock) {
        try {
            if (otIdxFile == null) {
                URI path = getExpandedDataPath();
                String otAllButLast = NetUtil.lengthenURI(path, File.separator + SwordConstants.FILE_OT + '.' + blockType.getIndicator() + SUFFIX_PART1).getPath();
                otIdxFile = new File(otAllButLast + SUFFIX_INDEX);
                otTextFile = new File(otAllButLast + SUFFIX_TEXT);
                otCompFile = new File(otAllButLast + SUFFIX_COMP);

                String ntAllButLast = NetUtil.lengthenURI(path, File.separator + SwordConstants.FILE_NT + '.' + blockType.getIndicator() + SUFFIX_PART1).getPath();
                ntIdxFile = new File(ntAllButLast + SUFFIX_INDEX);
                ntTextFile = new File(ntAllButLast + SUFFIX_TEXT);
                ntCompFile = new File(ntAllButLast + SUFFIX_COMP);
            }
        } catch (BookException e) {
            otIdxFile = null;
            otTextFile = null;
            otCompFile = null;

            ntIdxFile = null;
            ntTextFile = null;
            ntCompFile = null;

            return;
        }

        if (otIdxFile.canRead()) {
            try {
                otIdxRaf = new RandomAccessFile(otIdxFile, FileUtil.MODE_READ);
                otTextRaf = new RandomAccessFile(otTextFile, FileUtil.MODE_READ);
                otCompRaf = new RandomAccessFile(otCompFile, FileUtil.MODE_READ);
            } catch (FileNotFoundException ex) {
                assert false : ex;
                log.error("Could not open OT", ex);
                otIdxRaf = null;
                otTextRaf = null;
                otCompRaf = null;
            }
        }

        if (ntIdxFile.canRead()) {
            try {
                ntIdxRaf = new RandomAccessFile(ntIdxFile, FileUtil.MODE_READ);
                ntTextRaf = new RandomAccessFile(ntTextFile, FileUtil.MODE_READ);
                ntCompRaf = new RandomAccessFile(ntCompFile, FileUtil.MODE_READ);
            } catch (FileNotFoundException ex) {
                assert false : ex;
                log.error("Could not open NT", ex);
                ntIdxRaf = null;
                ntTextRaf = null;
                ntCompRaf = null;
            }
        }

        active = true;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#deactivate(org.crosswire.common.activate.Lock)
     */
    public final void deactivate(Lock lock) {
        if (ntIdxRaf != null) {
            try {
                ntIdxRaf.close();
                ntTextRaf.close();
                ntCompRaf.close();
            } catch (IOException ex) {
                log.error("failed to close nt files", ex);
            } finally {
                ntIdxRaf = null;
                ntTextRaf = null;
                ntCompRaf = null;
            }
        }

        if (otIdxRaf != null) {
            try {
                otIdxRaf.close();
                otTextRaf.close();
                otCompRaf.close();
            } catch (IOException ex) {
                log.error("failed to close ot files", ex);
            } finally {
                otIdxRaf = null;
                otTextRaf = null;
                otCompRaf = null;
            }
        }

        active = false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#contains(org.crosswire.jsword.passage.Key)
     */
    @Override
    public boolean contains(Key key) {
        checkActive();
        Verse verse = KeyUtil.getVerse(key);

        try {
            String v11nName = getBookMetaData().getProperty(ConfigEntryType.VERSIFICATION).toString();
            Versification v11n = Versifications.instance().getVersification(v11nName);
            int index = v11n.getOrdinal(verse);
            Testament testament = v11n.getTestament(index);
            index = v11n.getTestamentOrdinal(index);
            RandomAccessFile compRaf = otCompRaf;
            if (testament == Testament.NEW) {
                compRaf = ntCompRaf;
            }

            // If Bible does not contain the desired testament, then false
            if (compRaf == null) {
                return false;
            }

            // 10 because the index is 10 bytes long for each verse
            byte[] temp = SwordUtil.readRAF(compRaf, index * COMP_ENTRY_SIZE, COMP_ENTRY_SIZE);

            // If the Bible does not contain the desired verse, return nothing.
            // Some Bibles have different versification, so the requested verse may not exist.
            if (temp == null || temp.length == 0) {
                return false;
            }

            // The data is little endian - extract the blockNum, verseStart and verseSize
            int verseSize = SwordUtil.decodeLittleEndian16(temp, 8);

            return verseSize > 0;

        } catch (IOException e) {
            return false;
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#getRawText(org.crosswire.jsword.passage.Key)
     */
    @Override
    public String getRawText(Key key) throws BookException {
        checkActive();

        SwordBookMetaData sbmd = getBookMetaData();
        String charset = sbmd.getBookCharset();
        String compressType = (String) sbmd.getProperty(ConfigEntryType.COMPRESS_TYPE);

        Verse verse = KeyUtil.getVerse(key);

        try {
            String v11nName = getBookMetaData().getProperty(ConfigEntryType.VERSIFICATION).toString();
            Versification v11n = Versifications.instance().getVersification(v11nName);
            int index = v11n.getOrdinal(verse);
            Testament testament = v11n.getTestament(index);
            index = v11n.getTestamentOrdinal(index);
            RandomAccessFile compRaf = otCompRaf;
            RandomAccessFile idxRaf = otIdxRaf;
            RandomAccessFile textRaf = otTextRaf;
            if (testament == Testament.NEW) {
                compRaf = ntCompRaf;
                idxRaf = ntIdxRaf;
                textRaf = ntTextRaf;
            }

            // If Bible does not contain the desired testament, return nothing.
            if (compRaf == null) {
                return "";
            }

            // 10 because the index is 10 bytes long for each verse
            byte[] temp = SwordUtil.readRAF(compRaf, index * COMP_ENTRY_SIZE, COMP_ENTRY_SIZE);

            // If the Bible does not contain the desired verse, return nothing.
            // Some Bibles have different versification, so the requested verse may not exist.
            if (temp == null || temp.length == 0) {
                return "";
            }

            // The data is little endian - extract the blockNum, verseStart
            // and
            // verseSize
            long blockNum = SwordUtil.decodeLittleEndian32(temp, 0);
            int verseStart = SwordUtil.decodeLittleEndian32(temp, 4);
            int verseSize = SwordUtil.decodeLittleEndian16(temp, 8);

            // Can we get the data from the cache
            byte[] uncompressed = null;
            if (blockNum == lastBlockNum && testament == lastTestament) {
                uncompressed = lastUncompressed;
            } else {
                // Then seek using this index into the idx file
                temp = SwordUtil.readRAF(idxRaf, blockNum * IDX_ENTRY_SIZE, IDX_ENTRY_SIZE);
                if (temp == null || temp.length == 0) {
                    return "";
                }

                int blockStart = SwordUtil.decodeLittleEndian32(temp, 0);
                int blockSize = SwordUtil.decodeLittleEndian32(temp, 4);
                int uncompressedSize = SwordUtil.decodeLittleEndian32(temp, 8);

                // Read from the data file.
                byte[] data = SwordUtil.readRAF(textRaf, blockStart, blockSize);

                decipher(data);

                uncompressed = CompressorType.fromString(compressType).getCompressor(data).uncompress(uncompressedSize).toByteArray();

                // cache the uncompressed data for next time
                lastBlockNum = blockNum;
                lastTestament = testament;
                lastUncompressed = uncompressed;
            }

            // and cut out the required section.
            byte[] chopped = new byte[verseSize];
            System.arraycopy(uncompressed, verseStart, chopped, 0, verseSize);

            return SwordUtil.decode(key.getName(), chopped, charset);
        } catch (IOException e) {
            // TRANSLATOR: Common error condition: The file could not be read. There can be many reasons.
            // {0} is a placeholder for the file.
            throw new BookException(JSMsg.gettext("Error reading {0}", verse.getName()), e);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#setAliasKey(org.crosswire.jsword.passage.Key, org.crosswire.jsword.passage.Key)
     */
    @Override
    public void setAliasKey(Key alias, Key source) throws IOException {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#setRawText(org.crosswire.jsword.passage.Key, java.lang.String)
     */
    @Override
    public void setRawText(Key key, String text) throws BookException, IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * Helper method so we can quickly activate ourselves on access
     */
    protected final void checkActive() {
        if (!active) {
            Activator.activate(this);
        }
    }

    /**
     * Whether the book is blocked by Book, Chapter or Verse.
     */
    private BlockType blockType;

    /**
     *
     */
    private Testament lastTestament;

    /**
     *
     */
    private long lastBlockNum = -1;

    /**
     *
     */
    private byte[] lastUncompressed;

    /**
     * Are we active
     */
    private boolean active;

    /**
     * The index random access files
     */
    private RandomAccessFile otIdxRaf;
    private RandomAccessFile ntIdxRaf;

    /**
     * The data random access files
     */
    private RandomAccessFile otTextRaf;
    private RandomAccessFile ntTextRaf;

    /**
     * The compressed random access files
     */
    private RandomAccessFile otCompRaf;
    private RandomAccessFile ntCompRaf;

    /**
     * The index random access files
     */
    private File otIdxFile;
    private File ntIdxFile;

    /**
     * The data random access files
     */
    private File otTextFile;
    private File ntTextFile;

    /**
     * The compressed random access files
     */
    private File otCompFile;
    private File ntCompFile;

    /**
     * How many bytes in the comp index?
     */
    private static final int COMP_ENTRY_SIZE = 10;

    /**
     * How many bytes in the idx index?
     */
    private static final int IDX_ENTRY_SIZE = 12;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(ZVerseBackend.class);
}
