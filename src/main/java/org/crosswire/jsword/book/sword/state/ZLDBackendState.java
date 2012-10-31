package org.crosswire.jsword.book.sword.state;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;

import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.IOUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.sword.SwordBookMetaData;
import org.crosswire.jsword.book.sword.SwordUtil;

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
public class ZLDBackendState extends RawLDBackendState {
    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(ZLDBackendState.class);
    private static final byte[] EMPTY_BYTES = new byte[0];
    private static final String EXTENSION_Z_INDEX = ".zdx";
    private static final String EXTENSION_Z_DATA = ".zdt";
    
    /**
     * The compressed index.
     */
    private File zdxFile;

    /**
     * The compressed index random access file.
     */
    private RandomAccessFile zdxRaf;

    /**
     * The compressed text.
     */
    private  File zdtFile;

    /**
     * The compressed text random access file.
     */
    private  RandomAccessFile zdtRaf;

    /**
     * The index of the block that is cached.
     */
    private  long lastBlockNum;

    /**
     * The cache for a read of a compressed block.
     */
    private  byte[] lastUncompressed;


    /**
     * This is default package access for forcing the use of the
     * OpenFileStateManager to manage the creation. Not doing so may result in
     * new instances of OpenFileState being created for no reason, and as a
     * result, if they are released to the OpenFileStateManager by mistake this
     * would result in leakage
     * 
     * @param bookMetaData the appropriate metadata for the book
     */
     ZLDBackendState(SwordBookMetaData bookMetaData) throws BookException {
        super(bookMetaData);
        zdxFile = null;
        zdtFile = null;
        zdxRaf = null;
        zdtRaf = null;
        lastBlockNum = -1;
        lastUncompressed = EMPTY_BYTES;

        URI path = null;
        try {
            path = SwordUtil.getExpandedDataPath(bookMetaData);
        } catch (BookException e) {
            Reporter.informUser(this, e);
            return;
        }

        try {
            zdxFile = new File(path.getPath() + EXTENSION_Z_INDEX);
            zdtFile = new File(path.getPath() + EXTENSION_Z_DATA);

            if (!zdxFile.canRead()) {
                // TRANSLATOR: Common error condition: The file could not be read. There can be many reasons.
                // {0} is a placeholder for the file.
                Reporter.informUser(this, new BookException(JSMsg.gettext("Error reading {0}", zdtFile.getAbsolutePath())));
                return;
            }

            if (!zdtFile.canRead()) {
                // TRANSLATOR: Common error condition: The file could not be read. There can be many reasons.
                // {0} is a placeholder for the file.
                Reporter.informUser(this, new BookException(JSMsg.gettext("Error reading {0}", zdtFile.getAbsolutePath())));
                return;
            }

            // Open the files
            zdxRaf = new RandomAccessFile(zdxFile, FileUtil.MODE_READ);
            zdtRaf = new RandomAccessFile(zdtFile, FileUtil.MODE_READ);
        } catch (IOException ex) {
            log.error("failed to open files", ex);
            zdxRaf = null;
            zdtRaf = null;
            return;
        }
    }
    
    

    public void releaseResources() {
        super.releaseResources();
        lastBlockNum = -1;
        lastUncompressed = EMPTY_BYTES;

        IOUtil.close(zdxRaf);
        IOUtil.close(zdtRaf);
            zdxRaf = null;
            zdtRaf = null;
    }



    /**
     * @return the zdxRaf
     */
    public RandomAccessFile getZdxRaf() {
        return zdxRaf;
    }



    /**
     * @return the zdtRaf
     */
    public RandomAccessFile getZdtRaf() {
        return zdtRaf;
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
     * @param lastBlockNum the lastBlockNum to set
     */
    public void setLastBlockNum(long lastBlockNum) {
        this.lastBlockNum = lastBlockNum;
    }

    /**
     * @param lastUncompressed the lastUncompressed to set
     */
    public void setLastUncompressed(byte[] lastUncompressed) {
        this.lastUncompressed = lastUncompressed;
    }

    
}
