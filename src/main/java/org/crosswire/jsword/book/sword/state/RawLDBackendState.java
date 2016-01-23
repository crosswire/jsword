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
import org.crosswire.jsword.book.sword.RawLDBackend;
import org.crosswire.jsword.book.sword.SwordConstants;
import org.crosswire.jsword.book.sword.SwordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * State for {@link RawLDBackend}
 * 
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class RawLDBackendState extends AbstractOpenFileState  {
    /**
     * This is default package access for forcing the use of the
     * OpenFileStateManager to manage the creation. Not doing so may result in
     * new instances of OpenFileState being created for no reason, and as a
     * result, if they are released to the OpenFileStateManager by mistake this
     * would result in leakage
     * 
     * @param bookMetaData the appropriate metadata for the book
     */
    RawLDBackendState(BookMetaData bookMetaData) throws BookException {
        super(bookMetaData);
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
            //failed to open the files, so close them now
            IOUtil.close(idxRaf);
            IOUtil.close(datRaf);

            LOGGER.error("failed to open files", ex);
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

    /**
     * The log stream
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RawLDBackend.class);
}
