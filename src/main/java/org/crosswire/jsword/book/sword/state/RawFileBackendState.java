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

        if (existsReadAndWrite(getOtTextFile()) && existsReadAndWrite(getOtIdxFile()) && existsReadAndWrite(getNtTextFile())
                && existsReadAndWrite(getNtIdxFile()) && existsReadAndWrite(incFile)) {
            return true;
        }
        return false;
    }

    /**
     * If file is null, then maybe we don't have an OT, so we still want to return true, because the NT might be writable
     * @param file the file to check. 
     * @return true if exists, readable and writable, OR if file is null
     */
    private boolean existsReadAndWrite(File file) {
        return (file.exists() && file.canRead() && file.canWrite()) || file == null;
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
