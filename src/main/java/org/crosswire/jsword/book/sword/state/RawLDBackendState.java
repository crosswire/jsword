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
import org.crosswire.jsword.book.sword.RawLDBackend;
import org.crosswire.jsword.book.sword.SwordBookMetaData;
import org.crosswire.jsword.book.sword.SwordConstants;
import org.crosswire.jsword.book.sword.SwordUtil;

/**
 * State for {@link RawLDBackend}
 * 
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class RawLDBackendState extends AbstractOpenFileState  {
    private static final Logger log = Logger.getLogger(RawLDBackend.class);
    /**
     * The number of entries in the book.
     */
    private int size = -1;

    /**
     * The index file
     */
    private File idxFile;

    /**
     * The index random access file
     */
    private RandomAccessFile idxRaf;

    /**
     * The data file
     */
    private File datFile;

    /**
     * The data random access file
     */
    private RandomAccessFile datRaf;
    private SwordBookMetaData bookMetaData;

    public RawLDBackendState(SwordBookMetaData bookMetaData) throws BookException {
        this.bookMetaData = bookMetaData;
        URI path = null;
        try {
            path = SwordUtil.getExpandedDataPath(bookMetaData);
        } catch (BookException e) {
            Reporter.informUser(this, e);
            throw e;
        }

        try {
            idxFile = new File(path.getPath() + SwordConstants.EXTENSION_INDEX);
            datFile = new File(path.getPath() + SwordConstants.EXTENSION_DATA);

            if (!idxFile.canRead()) {
                // TRANSLATOR: Common error condition: The file could not be
                // read. There can be many reasons.
                // {0} is a placeholder for the file.
                Reporter.informUser(this, new BookException(JSMsg.gettext("Error reading {0}", idxFile.getAbsolutePath())));

                // As per previous behaviour, we continue with state half made, having informed the user of the error
                return;
            }

            if (!datFile.canRead()) {
                // Throwing exception, as if we can't read our data file, then we might as well give up

                // TRANSLATOR: Common error condition: The file could not be
                // read. There can be many reasons.
                // {0} is a placeholder for the file.
                BookException prob = new BookException(JSMsg.gettext("Error reading {0}", datFile.getAbsolutePath()));
                Reporter.informUser(this, prob);
                throw prob;
            }

            // Open the files
            idxRaf = new RandomAccessFile(idxFile, FileUtil.MODE_READ);
            datRaf = new RandomAccessFile(datFile, FileUtil.MODE_READ);
        } catch (IOException ex) {
            log.error("failed to open files", ex);
            idxRaf = null;
            datRaf = null;
            // TRANSLATOR: Common error condition: The file could not be read.
            // There can be many reasons.
            // {0} is a placeholder for the file.
            throw new BookException(JSMsg.gettext("Error reading {0}", datFile.getAbsolutePath()), ex);
        }
    }

    public void releaseResources() {
        size = -1;
        IOUtil.close(idxRaf);
        IOUtil.close(datRaf);
        idxRaf = null;
        datRaf = null;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @return the idxFile
     */
    public File getIdxFile() {
        return idxFile;
    }

    /**
     * @return the idxRaf
     */
    public RandomAccessFile getIdxRaf() {
        return idxRaf;
    }

    /**
     * @return the datRaf
     */
    public RandomAccessFile getDatRaf() {
        return datRaf;
    }

    /**
     * @param size
     *            the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }

    public SwordBookMetaData getBookMetaData() {
        return this.bookMetaData;
    }

}
