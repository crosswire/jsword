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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.crosswire.common.util.IOUtil;
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
public class RawFileBackendState extends RawBackendState {
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
    RawFileBackendState(BookMetaData bookMetaData) throws BookException {
        super(bookMetaData);
        incfileValue = -1;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#isWritable()
     */
    @Override
    public boolean isWritable() {
        File incFile = getIncfile();

        if (existsAndCanReadAndWrite(otTextFile)
                && existsAndCanReadAndWrite(ntTextFile)
                && existsAndCanReadAndWrite(otIdxFile)
                && existsAndCanReadAndWrite(otTextFile)
                && (incFile == null || existsAndCanReadAndWrite(incFile)))
        {
            return true;
        }
        return false;
    }

    /**
     * Returns true if the file exists, can be read and can be written to.
     *
     * @param file the file
     * @return true, if successful
     */
    private boolean existsAndCanReadAndWrite(File file) {
        return file.exists() && file.canRead() && file.canWrite();
    }

    private int readIncfile() throws IOException {
        int ret = -1;

        if (incfile == null) {
            // then attempt to initialise it
            initIncFile();
        }

        if (this.incfile != null) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(this.incfile);
                byte[] buffer = new byte[4];
                if (fis.read(buffer) != 4) {
                    LOGGER.error("Read data is not of appropriate size of 4 bytes!");
                    throw new IOException("Incfile is not 4 bytes long");
                }
                ret = SwordUtil.decodeLittleEndian32(buffer, 0);

                // also store this
                this.incfileValue = ret;
            } catch (FileNotFoundException e) {
                LOGGER.error("Error on writing to incfile, file should exist already!: {}", e.getMessage(), e);
            } finally {
                IOUtil.close(fis);
            }
        }

        return ret;
    }

    private void initIncFile() {
        try {
            File tempIncfile = new File(SwordUtil.getExpandedDataPath(getBookMetaData()).getPath() + File.separator + INCFILE);
            if (tempIncfile.exists()) {
                this.incfile = tempIncfile;
            }
        } catch (BookException e) {
            LOGGER.error("Error on checking incfile: {}", e.getMessage(), e);
            this.incfile = null;
        }
    }

    /**
     * @return the incfileValue
     */
    public int getIncfileValue() {
        if (incfileValue == -1) {
            try {
                readIncfile();
            } catch (IOException e) {
                LOGGER.error("IO Error: {}", e.getMessage(), e);
            }
        }

        return incfileValue;
    }

    public void setIncfileValue(int incValue) {
        this.incfileValue = incValue;
    }

    /**
     * @return the incfile
     */
    public File getIncfile() {
        if (incfile == null) {
            initIncFile();
        }
        return incfile;
    }

    /**
     * @param incfile
     *            the incfile to set
     */
    public void setIncfile(File incfile) {
        this.incfile = incfile;
    }

    public static final String INCFILE = "incfile";
    private File incfile;
    private int incfileValue;

    /**
     * The log stream
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RawFileBackendState.class);
}
