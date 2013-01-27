/*******************************************************************************
 * Copyright (c) 2012, Directors of the Tyndale STEP Project
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions 
 * are met:
 * 
 * Redistributions of source code must retain the above copyright 
 * notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright 
 * notice, this list of conditions and the following disclaimer in 
 * the documentation and/or other materials provided with the 
 * distribution.
 * Neither the name of the Tyndale House, Cambridge (www.TyndaleHouse.com)  
 * nor the names of its contributors may be used to endorse or promote 
 * products derived from this software without specific prior written 
 * permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS 
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING 
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
 * THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.crosswire.jsword.book.sword.state;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.crosswire.common.util.IOUtil;
import org.crosswire.common.util.Logger;
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
public class RawFileBackendState extends RawBackendState {
    public static final String INCFILE = "incfile";
    /** The log stream */
    private static final Logger log = Logger.getLogger(RawFileBackendState.class);
    private File incfile;
    private Integer incfileValue;

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
    RawFileBackendState(SwordBookMetaData bookMetaData) throws BookException {
        super(bookMetaData);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#isWritable()
     */
    public boolean isWritable() {
        File incFile = getIncfile();

            if(existsAndCanReadAndWrite(otTextFile) && 
                    existsAndCanReadAndWrite(ntTextFile) && 
                    existsAndCanReadAndWrite(otIdxFile) && 
                    existsAndCanReadAndWrite(otTextFile) && 
                    (incFile == null || existsAndCanReadAndWrite(incFile))) {
                return true;
            }
            return false;
    }
    
    /**
     * Returns true if the file exists, can be read and can be written to.
     * 
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
                    log.error("Read data is not of appropriate size of 4 bytes!");
                    throw new IOException("Incfile is not 4 bytes long");
                }
                ret = SwordUtil.decodeLittleEndian32(buffer, 0);

                // also store this
                this.incfileValue = ret;
            } catch (FileNotFoundException e) {
                log.error("Error on writing to incfile, file should exist already!");
                log.error(e.getMessage());
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
            log.error("Error on checking incfile: " + e.getMessage());
            this.incfile = null;
        }
    }

    /**
     * @return the incfileValue
     */
    public int getIncfileValue() {
        if (incfileValue == null) {
            try {
                readIncfile();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
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

}
