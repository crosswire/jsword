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
 * © CrossWire Bible Society, 2013 - 2016
 *
 */
package org.crosswire.jsword.book.sword.state;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.net.URI;

import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.IOUtil;
import org.crosswire.common.util.NetUtil;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.sword.BlockType;
import org.crosswire.jsword.book.sword.SwordConstants;
import org.crosswire.jsword.book.sword.SwordUtil;
import org.crosswire.jsword.versification.Testament;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stores the random access files required for processing the passage request.
 * 
 * The caller is required to close to correctly free resources and avoid File
 * pointer leaks.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class ZVerseBackendState extends AbstractOpenFileState {
    /**
     * This is default package access for forcing the use of the
     * OpenFileStateManager to manage the creation. Not doing so may result in
     * new instances of OpenFileState being created for no reason, and as a
     * result, if they are released to the OpenFileStateManager by mistake this
     * would result in leakage
     * 
     * @param bookMetaData the appropriate metadata for the book
     */
    ZVerseBackendState(BookMetaData bookMetaData, BlockType blockType) throws BookException {
        super(bookMetaData);
        URI path = SwordUtil.getExpandedDataPath(bookMetaData);
        String otAllButLast = NetUtil.lengthenURI(path, File.separator + SwordConstants.FILE_OT + '.' + blockType.getIndicator() + SUFFIX_PART1).getPath();
        File otIdxFile = new File(otAllButLast + SUFFIX_INDEX);
        File otTextFile = new File(otAllButLast + SUFFIX_TEXT);
        File otCompFile = new File(otAllButLast + SUFFIX_COMP);

        String ntAllButLast = NetUtil.lengthenURI(path, File.separator + SwordConstants.FILE_NT + '.' + blockType.getIndicator() + SUFFIX_PART1).getPath();
        File ntIdxFile = new File(ntAllButLast + SUFFIX_INDEX);
        File ntTextFile = new File(ntAllButLast + SUFFIX_TEXT);
        File ntCompFile = new File(ntAllButLast + SUFFIX_COMP);

        // check whether exists to swallow any exception as before
        if (otIdxFile.canRead()) {
            try {
                otCompRaf = new RandomAccessFile(otIdxFile, FileUtil.MODE_READ);
                otTextRaf = new RandomAccessFile(otTextFile, FileUtil.MODE_READ);
                otIdxRaf = new RandomAccessFile(otCompFile, FileUtil.MODE_READ);
            } catch (FileNotFoundException ex) {
                //failed to open the files, so close them now
                IOUtil.close(otCompRaf);
                IOUtil.close(otTextRaf);
                IOUtil.close(otIdxRaf);

                assert false : ex;
                LOGGER.error("Could not open OT", ex);
            }
        }

        // why do swallow the exception and log. Can Books have one testament
        // without the other.
        if (ntIdxFile.canRead()) {
            try {
                ntCompRaf = new RandomAccessFile(ntIdxFile, FileUtil.MODE_READ);
                ntTextRaf = new RandomAccessFile(ntTextFile, FileUtil.MODE_READ);
                ntIdxRaf = new RandomAccessFile(ntCompFile, FileUtil.MODE_READ);
            } catch (FileNotFoundException ex) {
                //failed to open the files, so close them now
                IOUtil.close(ntCompRaf);
                IOUtil.close(ntTextRaf);
                IOUtil.close(ntIdxRaf);

                assert false : ex;
                LOGGER.error("Could not open OT", ex);
            }
        }
    }

    public void releaseResources() {
        IOUtil.close(ntCompRaf);
        IOUtil.close(ntTextRaf);
        IOUtil.close(ntIdxRaf);
        IOUtil.close(otCompRaf);
        IOUtil.close(otTextRaf);
        IOUtil.close(otIdxRaf);
        ntCompRaf = null;
        ntTextRaf = null;
        ntIdxRaf = null;
        otCompRaf = null;
        otTextRaf = null;
        otIdxRaf = null;
    }

    /**
     * Get the compression file for the given testament.
     * 
     * @param testament the testament for the index
     * @return the index for the testament
     */
    public RandomAccessFile getCompRaf(Testament testament) {
        return testament == Testament.NEW ? ntCompRaf : otCompRaf;
    }

    /**
     * Get the text file for the given testament.
     * 
     * @param testament the testament for the index
     * @return the index for the testament
     */
    public RandomAccessFile getTextRaf(Testament testament) {
        return testament == Testament.NEW ? ntTextRaf : otTextRaf;
    }

    /**
     * Get the index file for the given testament.
     * 
     * @param testament the testament for the index
     * @return the index for the testament
     */
    public RandomAccessFile getIdxRaf(Testament testament) {
        return testament == Testament.NEW ? ntIdxRaf : otIdxRaf;
    }

    /**
     * @return the lastTestament
     */
    public Testament getLastTestament() {
        return lastTestament;
    }

    /**
     * @return the lastBlockNum
     */
    public long getLastBlockNum() {
        return lastBlockNum;
    }

    /**
     * @return the lastUncompressed
     */
    public byte[] getLastUncompressed() {
        return lastUncompressed;
    }

    /**
     * @param lastTestament
     *            the lastTestament to set
     */
    public void setLastTestament(Testament lastTestament) {
        this.lastTestament = lastTestament;
    }

    /**
     * @param lastBlockNum
     *            the lastBlockNum to set
     */
    public void setLastBlockNum(long lastBlockNum) {
        this.lastBlockNum = lastBlockNum;
    }

    /**
     * @param lastUncompressed
     *            the lastUncompressed to set
     */
    public void setLastUncompressed(byte[] lastUncompressed) {
        this.lastUncompressed = lastUncompressed;
    }

    private static final String SUFFIX_COMP = "v";
    private static final String SUFFIX_INDEX = "s";
    private static final String SUFFIX_PART1 = "z";
    private static final String SUFFIX_TEXT = "z";

    /**
     * The compressed random access files
     */
    private RandomAccessFile otCompRaf;
    private RandomAccessFile ntCompRaf;

    /**
     * The data random access files
     */
    private RandomAccessFile otTextRaf;
    private RandomAccessFile ntTextRaf;

    /**
     * The index random access files
     */
    private RandomAccessFile otIdxRaf;
    private RandomAccessFile ntIdxRaf;
    private Testament lastTestament;
    private long lastBlockNum = -1;
    private byte[] lastUncompressed;

    /**
     * The log stream
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ZVerseBackendState.class);
}
