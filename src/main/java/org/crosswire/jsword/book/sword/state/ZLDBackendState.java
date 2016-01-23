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
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;

import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.IOUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.sword.SwordUtil;
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
public class ZLDBackendState extends RawLDBackendState {
    /**
     * This is default package access for forcing the use of the
     * OpenFileStateManager to manage the creation. Not doing so may result in
     * new instances of OpenFileState being created for no reason, and as a
     * result, if they are released to the OpenFileStateManager by mistake this
     * would result in leakage
     * 
     * @param bookMetaData the appropriate metadata for the book
     */
     ZLDBackendState(BookMetaData bookMetaData) throws BookException {
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
            //failed to open the files, so close them now
            IOUtil.close(zdxRaf);
            IOUtil.close(zdtRaf);

            LOGGER.error("failed to open files", ex);
            zdxRaf = null;
            zdtRaf = null;
            return;
        }
    }

    @Override
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
     * The log stream
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ZLDBackendState.class);
}
