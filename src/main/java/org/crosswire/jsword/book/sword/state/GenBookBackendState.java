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
 * Stores the random access files required for processing the passage request
 * 
 * The caller is required to close to correctly free resources and avoid File
 * pointer leaks
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class GenBookBackendState extends AbstractOpenFileState {
    /**
     * This is default package access for forcing the use of the
     * OpenFileStateManager to manage the creation. Not doing so may result in
     * new instances of OpenFileState being created for no reason, and as a
     * result, if they are released to the OpenFileStateManager by mistake this
     * would result in leakage
     * 
     * @param bookMetaData the appropriate metadata for the book
     */
    GenBookBackendState(BookMetaData bookMetaData) {
        super(bookMetaData);
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

            LOGGER.error("failed to open files", ex);
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

    /**
     * The log stream
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GenBookBackendState.class);
}
