/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;

import org.crosswire.common.activate.Activator;
import org.crosswire.common.activate.Lock;
import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.versification.Testament;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;

/**
 * Both Books and Commentaries seem to use the same format so this class
 * abstracts out the similarities.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class RawBackend extends AbstractBackend {
    /**
     * Simple ctor
     */
    public RawBackend(SwordBookMetaData sbmd, int datasize) {
        super(sbmd);
        this.datasize = datasize;
        this.entrysize = OFFSETSIZE + datasize;

        assert datasize == 2 || datasize == 4;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#contains(org.crosswire.jsword.passage.Key)
     */
    @Override
    public boolean contains(Key key) {
        checkActive();

        Verse verse = KeyUtil.getVerse(key);

        try {
            String v11nName = getBookMetaData().getProperty(ConfigEntryType.VERSIFICATION).toString();
            Versification v11n = Versifications.instance().getVersification(v11nName);
            int index = v11n.getOrdinal(verse);
            Testament testament = v11n.getTestament(index);
            index = v11n.getTestamentOrdinal(index);
            RandomAccessFile idxRaf = otIdxRaf;
            if (testament == Testament.NEW) {
                idxRaf = ntIdxRaf;
            }

            // If this is a single testament Bible, return nothing.
            if (idxRaf == null) {
                return false;
            }

            DataIndex dataIndex = getIndex(idxRaf, index);

            return dataIndex.getSize() > 0;
        } catch (IOException ex) {
            return false;
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#getRawText(org.crosswire.jsword.passage.Key)
     */
    @Override
    public String getRawText(Key key) throws BookException {
        checkActive();

        Verse verse = KeyUtil.getVerse(key);
        try {
            String v11nName = getBookMetaData().getProperty(ConfigEntryType.VERSIFICATION).toString();
            Versification v11n = Versifications.instance().getVersification(v11nName);
            int index = v11n.getOrdinal(verse);
            Testament testament = v11n.getTestament(index);
            index = v11n.getTestamentOrdinal(index);
            RandomAccessFile idxRaf = otIdxRaf;
            if (testament == Testament.NEW) {
                idxRaf = ntIdxRaf;
            }

            // If this is a single testament Bible, return nothing.
            if (idxRaf == null) {
                return "";
            }

            return getEntry(key.getName(), testament, index);
        } catch (IOException ex) {
            // TRANSLATOR: Common error condition: The file could not be read. There can be many reasons.
            // {0} is a placeholder for the file.
            throw new BookException(JSMsg.gettext("Error reading {0}", verse.getName()), ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#setRawText(org.crosswire.jsword.passage.Key, java.lang.String)
     */
    @Override
    public void setRawText(Key key, String text) throws BookException, IOException {
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#isWritable()
     */
    @Override
    public boolean isWritable() {
        // For the module to be writable either the old testament or the new
        // testament needs to be present
        // (i.e. readable) and both the index and the data files need to be
        // writable
        if (otIdxFile.canRead() && (otIdxFile.canWrite() || !otTxtFile.canWrite())) {
            return false;
        }
        if (ntIdxFile.canRead() && (ntIdxFile.canWrite() || !ntTxtFile.canWrite())) {
            return false;
        }
        return otIdxFile.canRead() || ntIdxFile.canRead();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#setAliasKey(org.crosswire.jsword.passage.Key, org.crosswire.jsword.passage.Key)
     */
    @Override
    public void setAliasKey(Key alias, Key source) throws IOException {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#activate(org.crosswire.common.activate.Lock)
     */
    public final void activate(Lock lock) {
        URI path = null;
        try {
            path = getExpandedDataPath();
        } catch (BookException e) {
            Reporter.informUser(this, e);
            return;
        }

        URI otPath = NetUtil.lengthenURI(path, File.separator + SwordConstants.FILE_OT);
        otTxtFile = new File(otPath.getPath());
        otIdxFile = new File(otPath.getPath() + SwordConstants.EXTENSION_VSS);

        URI ntPath = NetUtil.lengthenURI(path, File.separator + SwordConstants.FILE_NT);
        ntTxtFile = new File(ntPath.getPath());
        ntIdxFile = new File(ntPath.getPath() + SwordConstants.EXTENSION_VSS);

        // It is an error to be neither OT nor NT
        if (!otTxtFile.canRead() && !ntTxtFile.canRead()) {
            Reporter.informUser(this, new BookException(JSOtherMsg.lookupText("Missing data files for old and new testaments in {0}.", path)));
            return;
        }

        String fileMode = isWritable() ? FileUtil.MODE_WRITE : FileUtil.MODE_READ;

        if (otIdxFile.canRead()) {
            try {
                otIdxRaf = new RandomAccessFile(otIdxFile, fileMode);
                otTxtRaf = new RandomAccessFile(otTxtFile, fileMode);
            } catch (FileNotFoundException ex) {
                assert false : ex;
                log.error("Could not open OT", ex);
                ntIdxRaf = null;
                ntTxtRaf = null;
            }
        }

        if (ntIdxFile.canRead()) {
            try {
                ntIdxRaf = new RandomAccessFile(ntIdxFile, fileMode);
                ntTxtRaf = new RandomAccessFile(ntTxtFile, fileMode);
            } catch (FileNotFoundException ex) {
                assert false : ex;
                log.error("Could not open NT", ex);
                ntIdxRaf = null;
                ntTxtRaf = null;
            }
        }

        active = true;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#deactivate(org.crosswire.common.activate.Lock)
     */
    public final void deactivate(Lock lock) {
        try {
            otIdxRaf.close();
            otTxtRaf.close();

            ntIdxRaf.close();
            ntTxtRaf.close();
        } catch (IOException ex) {
            log.error("Failed to close files", ex);
        } finally {
            otIdxRaf = null;
            otTxtRaf = null;

            ntIdxRaf = null;
            ntTxtRaf = null;
        }

        active = false;
    }

    /**
     * Helper method so we can quickly activate ourselves on access
     */
    protected final void checkActive() {
        if (!active) {
            Activator.activate(this);
        }
    }

    /**
     * Get the Index (that is offset and size) for an entry.
     * 
     * @param entry
     * @return the index for the entry
     * @throws IOException
     */
    protected DataIndex getIndex(RandomAccessFile raf, long entry) throws IOException {
        // Read the offset and size for this key from the index
        byte[] buffer = SwordUtil.readRAF(raf, entry * entrysize, entrysize);
        if (buffer == null || buffer.length == 0) {
            return new DataIndex(0, 0);
        }

        int entryOffset = SwordUtil.decodeLittleEndian32(buffer, 0);
        int entrySize = -1;
        switch (datasize) {
        case 2:
            entrySize = SwordUtil.decodeLittleEndian16(buffer, 4);
            break;
        case 4:
            entrySize = SwordUtil.decodeLittleEndian32(buffer, 4);
            break;
        default:
            assert false : datasize;
        }
        return new DataIndex(entryOffset, entrySize);
    }

    /**
     * Get the text for an indexed entry in the book.
     * 
     * @param index
     *            the entry to get
     * @param name
     *            name of the entry
     * @param testament
     *            the testament for the entry
     * @return the text for the entry.
     * @throws IOException
     *             on a IO problem
     */
    protected String getEntry(String name, Testament testament, long index) throws IOException {
        RandomAccessFile idxRaf = otIdxRaf;
        RandomAccessFile txtRaf = otTxtRaf;
        if (testament == Testament.NEW) {
            idxRaf = ntIdxRaf;
            txtRaf = ntTxtRaf;
        }

        DataIndex dataIndex = getIndex(idxRaf, index);

        int size = dataIndex.getSize();
        if (size == 0) {
            return "";
        }

        if (size < 0) {
            log.error("In " + getBookMetaData().getInitials() + ": Verse " + name + " has a bad index size of " + size);
            return "";
        }

        byte[] data = SwordUtil.readRAF(txtRaf, dataIndex.getOffset(), size);

        decipher(data);

        return SwordUtil.decode(name, data, getBookMetaData().getBookCharset());
    }

    /**
     * Are we active
     */
    protected boolean active;

    /**
     * How many bytes in the size count in the index
     */
    protected int datasize;

    /**
     * The number of bytes for each entry in the index: either 6 or 8
     */
    protected int entrysize;

    protected RandomAccessFile otIdxRaf;
    protected RandomAccessFile otTxtRaf;
    protected File otIdxFile;
    protected File otTxtFile;

    protected RandomAccessFile ntIdxRaf;
    protected RandomAccessFile ntTxtRaf;
    protected File ntIdxFile;
    protected File ntTxtFile;

    /**
     * How many bytes in the offset pointers in the index
     */
    protected static final int OFFSETSIZE = 4;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(RawBackend.class);
}
