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
public class GenBookBackendState extends AbstractOpenFileState {
    /** The log stream */
    private static final Logger log = Logger.getLogger(GenBookBackendState.class);
    /**
     * Raw GenBook file extensions
     */
    private static final String EXTENSION_BDT = ".bdt";

    /**
     * The raw data file
     */
    private File bdtFile;
    /**
     * The random access file for the raw data
     */
    private RandomAccessFile bdtRaf;
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
    GenBookBackendState(SwordBookMetaData bookMetaData) {
        URI path = null;
        try {
            path = SwordUtil.getExpandedDataPath(bookMetaData);
        } catch (BookException e) {
            Reporter.informUser(this, e);
            return;
        }

        bdtFile = new File(path.getPath() + EXTENSION_BDT);

        if (!bdtFile.canRead()) {
            // TRANSLATOR: Common error condition: The file could not be read.
            // There can be many reasons.
            // {0} is a placeholder for the file.
            Reporter.informUser(this, new BookException(JSMsg.gettext("Error reading {0}", bdtFile.getAbsolutePath())));
            return;
        }

        try {
            bdtRaf  = new RandomAccessFile(bdtFile, FileUtil.MODE_READ);
        } catch (IOException ex) {
            //failed to open the files, so close them now
            IOUtil.close(bdtRaf);

            log.error("failed to open files", ex);
            bdtRaf = null;
        }
    }

    public void releaseResources() {
        IOUtil.close(bdtRaf);
        bdtRaf = null;
    }

    /**
     * @return the bdtRaf
     */
    public RandomAccessFile getBdtRaf() {
        return bdtRaf;
    }

    /**
     * @return the bookMetaData
     */
    public SwordBookMetaData getBookMetaData() {
        return bookMetaData;
    }
}
