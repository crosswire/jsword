package org.crosswire.jsword.book.sword.state;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.net.URI;

import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.IOUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.sword.SwordBookMetaData;
import org.crosswire.jsword.book.sword.SwordConstants;
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
public class RawBackendState extends AbstractOpenFileState {
    /** The log stream */
    private static final Logger log = Logger.getLogger(RawBackendState.class);

    protected RandomAccessFile otIdxRaf;
    protected RandomAccessFile ntIdxRaf;
    protected RandomAccessFile otTextRaf;
    protected RandomAccessFile ntTextRaf;
    protected File ntIdxFile;
    protected File ntTextFile;
    protected File otIdxFile;
    protected File otTextFile;

    private SwordBookMetaData bookMetaData;

    /**
     * This is default package access for forcing the use of the
     * OpenFileStateManager to manage the creation. Not doing so may result in
     * new instances of OpenFileState being created for no reason, and as a
     * result, if they are released to the OpenFileStateManager by mistake this
     * would result in leakage
     * 
     * @param bookMetaData
     *            the appropriate metadata for the book
     */
    RawBackendState(SwordBookMetaData bookMetaData) throws BookException {
        this.bookMetaData = bookMetaData;
        URI path = SwordUtil.getExpandedDataPath(bookMetaData);

        URI otPath = NetUtil.lengthenURI(path, File.separator + SwordConstants.FILE_OT);
        otTextFile = new File(otPath.getPath());
        otIdxFile = new File(otPath.getPath() + SwordConstants.EXTENSION_VSS);

        URI ntPath = NetUtil.lengthenURI(path, File.separator + SwordConstants.FILE_NT);
        ntTextFile = new File(ntPath.getPath());
        ntIdxFile = new File(ntPath.getPath() + SwordConstants.EXTENSION_VSS);

        // It is an error to be neither OT nor NT
        // Throwing exception, as if we can't read either the ot or nt file,
        // then we might as well give up
        if (!otTextFile.canRead() && !ntTextFile.canRead()) {
            BookException prob = new BookException(JSOtherMsg.lookupText("Missing data files for old and new testaments in {0}.", path));
            Reporter.informUser(this, prob);
            throw prob;
        }

        String fileMode = isWritable() ? FileUtil.MODE_WRITE : FileUtil.MODE_READ;

        if (otIdxFile.canRead()) {
            try {
                otIdxRaf = new RandomAccessFile(otIdxFile, fileMode);
                otTextRaf = new RandomAccessFile(otTextFile, fileMode);
            } catch (FileNotFoundException ex) {
                assert false : ex;
                log.error("Could not open OT", ex);
                ntIdxRaf = null;
                ntTextRaf = null;
            }
        }

        if (ntIdxFile.canRead()) {
            try {
                ntIdxRaf = new RandomAccessFile(ntIdxFile, fileMode);
                ntTextRaf = new RandomAccessFile(ntTextFile, fileMode);
            } catch (FileNotFoundException ex) {
                assert false : ex;
                log.error("Could not open NT", ex);
                ntIdxRaf = null;
                ntTextRaf = null;
            }
        }
    }

    public void releaseIndex() {
        IOUtil.close(ntIdxRaf);
        IOUtil.close(otIdxRaf);

        ntIdxRaf = null;
        otIdxRaf = null;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#isWritable()
     */
    public boolean isWritable() {
        // For the module to be writable either the old testament or the new
        // testament needs to be present
        // (i.e. readable) and both the index and the data files need to be
        // writable
        if (otIdxFile.canRead() && (otIdxFile.canWrite() || !otTextFile.canWrite())) {
            return false;
        }
        if (ntIdxFile.canRead() && (ntIdxFile.canWrite() || !ntTextFile.canWrite())) {
            return false;
        }
        return otIdxFile.canRead() || ntIdxFile.canRead();
    }

    public void releaseResources() {
        IOUtil.close(ntIdxRaf);
        IOUtil.close(ntTextRaf);
        IOUtil.close(otIdxRaf);
        IOUtil.close(otTextRaf);
        ntIdxRaf = null;
        ntTextRaf = null;
        otIdxRaf = null;
        otTextRaf = null;
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
     * @return the ntTextFile
     */
    public File getNtTextFile() {
        return ntTextFile;
    }

    /**
     * @return the otTextFile
     */
    public File getOtTextFile() {
        return otTextFile;
    }

    /**
     * @return the ntIdxFile
     */
    public File getNtIdxFile() {
        return ntIdxFile;
    }

    /**
     * @return the otIdxFile
     */
    public File getOtIdxFile() {
        return otIdxFile;
    }

    /**
     * @return the bookMetaData
     */
    public SwordBookMetaData getBookMetaData() {
        return bookMetaData;
    }
}
