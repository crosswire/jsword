package org.crosswire.jsword.book.sword.state;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.net.URI;

import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.IOUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.sword.BlockType;
import org.crosswire.jsword.book.sword.SwordBookMetaData;
import org.crosswire.jsword.book.sword.SwordConstants;
import org.crosswire.jsword.book.sword.SwordUtil;
import org.crosswire.jsword.versification.Testament;

/**
 * Stores the random access files required for processing the passage request
 * 
 * The caller is required to close to correctly free resources and avoid File
 * pointer leaks
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class ZVerseBackendState extends AbstractOpenFileState {
    private static final String SUFFIX_COMP = "v";
    private static final String SUFFIX_INDEX = "s";
    private static final String SUFFIX_PART1 = "z";
    private static final String SUFFIX_TEXT = "z";

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(ZVerseBackendState.class);

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
    private Testament lastTestament;
    private long lastBlockNum = -1;
    private byte[] lastUncompressed;
    private SwordBookMetaData bookMetaData;

    /**
     * This is default package access for forcing the use of the
     * OpenFileStateManager to manage the creation. Not doing so may result in
     * new instances of OpenFileState being created for no reason, and as a
     * result, if they are released to the OpenFileStateManager by mistake this
     * would result in leakage
     * 
     * @param bookMetaData the appropriate metadata for the book
     */
     ZVerseBackendState(SwordBookMetaData bookMetaData, BlockType blockType) throws BookException {
        this.bookMetaData = bookMetaData;
        URI path = SwordUtil.getExpandedDataPath(bookMetaData);
        String otAllButLast = NetUtil.lengthenURI(path, File.separator + SwordConstants.FILE_OT + '.' + blockType.getIndicator() + SUFFIX_PART1).getPath();
        File otIdxFile = new File(otAllButLast + SUFFIX_INDEX);
        File otTextFile = new File(otAllButLast + SUFFIX_TEXT);
        File otCompFile = new File(otAllButLast + SUFFIX_COMP);

        String ntAllButLast = NetUtil.lengthenURI(path, File.separator + SwordConstants.FILE_NT + '.' + blockType.getIndicator() + SUFFIX_PART1).getPath();
        File ntIdxFile = new File(ntAllButLast + SUFFIX_INDEX);
        File ntTextFile = new File(ntAllButLast + SUFFIX_TEXT);
        File ntCompFile = new File(ntAllButLast + SUFFIX_COMP);

        // check whether exists to swallow any exception as befor
        if (otIdxFile.canRead()) {
            try {
                otIdxRaf = new RandomAccessFile(otIdxFile, FileUtil.MODE_READ);
                otTextRaf = new RandomAccessFile(otTextFile, FileUtil.MODE_READ);
                otCompRaf = new RandomAccessFile(otCompFile, FileUtil.MODE_READ);
            } catch (FileNotFoundException ex) {
                assert false : ex;
                log.error("Could not open OT", ex);
            }
        }

        // why do swallow the exception and log. Can Books have one testament
        // without the other.
        if (ntIdxFile.canRead()) {
            try {
                ntIdxRaf = new RandomAccessFile(ntIdxFile, FileUtil.MODE_READ);
                ntTextRaf = new RandomAccessFile(ntTextFile, FileUtil.MODE_READ);
                ntCompRaf = new RandomAccessFile(ntCompFile, FileUtil.MODE_READ);
            } catch (FileNotFoundException ex) {
                assert false : ex;
                log.error("Could not open OT", ex);
            }
        }
    }

    public void releaseResources() {
        IOUtil.close(ntIdxRaf);
        IOUtil.close(ntTextRaf);
        IOUtil.close(ntCompRaf);
        IOUtil.close(otIdxRaf);
        IOUtil.close(otTextRaf);
        IOUtil.close(otCompRaf);
        ntIdxRaf = null;
        ntTextRaf = null;
        ntCompRaf = null;
        otIdxRaf = null;
        otTextRaf = null;
        otCompRaf = null;
    }

    /**
     * @return the otIdxRaf
     */
    public RandomAccessFile getOtIdxRaf() {
        return otIdxRaf;
    }

    /**
     * @return the ntIdxRaf
     */
    public RandomAccessFile getNtIdxRaf() {
        return ntIdxRaf;
    }

    /**
     * @return the otTextRaf
     */
    public RandomAccessFile getOtTextRaf() {
        return otTextRaf;
    }

    /**
     * @return the ntTextRaf
     */
    public RandomAccessFile getNtTextRaf() {
        return ntTextRaf;
    }

    /**
     * @return the otCompRaf
     */
    public RandomAccessFile getOtCompRaf() {
        return otCompRaf;
    }

    /**
     * @return the ntCompRaf
     */
    public RandomAccessFile getNtCompRaf() {
        return ntCompRaf;
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

    /**
     * @return the bookMetaData
     */
    public SwordBookMetaData getBookMetaData() {
        return bookMetaData;
    }
}
