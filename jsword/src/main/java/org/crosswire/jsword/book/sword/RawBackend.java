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
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.DataPolice;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.Verse;

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

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Key#contains(org.crosswire.jsword.passage
     * .Key)
     */
    /* @Override */
    public boolean contains(Key key) {
        checkActive();

        try {
        DataPolice.setKey(key);
        Verse verse = KeyUtil.getVerse(key);

        try {
            int testament = SwordConstants.getTestament(verse);
            long index = SwordConstants.getIndex(verse);

            // If this is a single testament Bible, return nothing.
            if (idxRaf[testament] == null) {
                return false;
            }

            DataIndex dataIndex = getIndex(idxRaf[testament], index);

            return dataIndex.getSize() > 0;
        } catch (IOException ex) {
            return false;
        }
        } finally {
            DataPolice.setKey(null);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.sword.AbstractBackend#getRawText(org.crosswire
     * .jsword.passage.Key, java.lang.String)
     */
    /* @Override */
    public String getRawText(Key key) throws BookException {
        checkActive();

        try {
            DataPolice.setKey(key);
            Verse verse = KeyUtil.getVerse(key);
            try {
                int testament = SwordConstants.getTestament(verse);
                long index = SwordConstants.getIndex(verse);

                // If this is a single testament Bible, return nothing.
                if (idxRaf[testament] == null) {
                    return ""; //$NON-NLS-1$
                }

                return getEntry(key.getName(), testament, index);
            } catch (IOException ex) {
                throw new BookException(UserMsg.READ_FAIL, ex, new Object[] {
                    verse.getName()
                });
            }
        } finally {
            DataPolice.setKey(null);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.sword.AbstractBackend#setRawText(org.crosswire
     * .jsword.passage.Key, java.lang.String)
     */
    public void setRawText(Key key, String text) throws BookException, IOException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.sword.AbstractBackend#isWritable()
     */
    public boolean isWritable() {
        // For the module to be writable either the old testament or the new
        // testament needs to be present
        // (i.e. readable) and both the index and the data files need to be
        // writable
        if (idxFile[SwordConstants.TESTAMENT_OLD].canRead()
                && (idxFile[SwordConstants.TESTAMENT_OLD].canWrite() || !txtFile[SwordConstants.TESTAMENT_OLD].canWrite()))
        {
            return false;
        }
        if (idxFile[SwordConstants.TESTAMENT_NEW].canRead()
                && (idxFile[SwordConstants.TESTAMENT_NEW].canWrite() || !txtFile[SwordConstants.TESTAMENT_NEW].canWrite()))
        {
            return false;
        }
        return idxFile[SwordConstants.TESTAMENT_OLD].canRead() || idxFile[SwordConstants.TESTAMENT_NEW].canRead();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#setAliasKey(org.crosswire.jsword.passage.Key, org.crosswire.jsword.passage.Key)
     */
    public void setAliasKey(Key alias, Key source) throws IOException {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.common.activate.Activatable#activate(org.crosswire.common
     * .activate.Lock)
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
        txtFile[SwordConstants.TESTAMENT_OLD] = new File(otPath.getPath());
        idxFile[SwordConstants.TESTAMENT_OLD] = new File(otPath.getPath() + SwordConstants.EXTENSION_VSS);

        URI ntPath = NetUtil.lengthenURI(path, File.separator + SwordConstants.FILE_NT);
        txtFile[SwordConstants.TESTAMENT_NEW] = new File(ntPath.getPath());
        idxFile[SwordConstants.TESTAMENT_NEW] = new File(ntPath.getPath() + SwordConstants.EXTENSION_VSS);

        // It is an error to be neither OT nor NT
        if (!txtFile[SwordConstants.TESTAMENT_OLD].canRead() && !txtFile[SwordConstants.TESTAMENT_NEW].canRead()) {
            Reporter.informUser(this, new BookException(Msg.MISSING_FILE, new Object[] {
                path
            }));
            return;
        }

        String fileMode = isWritable() ? FileUtil.MODE_WRITE : FileUtil.MODE_READ;

        if (idxFile[SwordConstants.TESTAMENT_OLD].canRead()) {
            try {
                idxRaf[SwordConstants.TESTAMENT_OLD] = new RandomAccessFile(idxFile[SwordConstants.TESTAMENT_OLD], fileMode);
                txtRaf[SwordConstants.TESTAMENT_OLD] = new RandomAccessFile(txtFile[SwordConstants.TESTAMENT_OLD], fileMode);
            } catch (FileNotFoundException ex) {
                assert false : ex;
                log.error("Could not open OT", ex); //$NON-NLS-1$
                idxRaf[SwordConstants.TESTAMENT_OLD] = null;
                txtRaf[SwordConstants.TESTAMENT_OLD] = null;
            }
        }

        if (idxFile[SwordConstants.TESTAMENT_NEW].canRead()) {
            try {
                idxRaf[SwordConstants.TESTAMENT_NEW] = new RandomAccessFile(idxFile[SwordConstants.TESTAMENT_NEW], fileMode);
                txtRaf[SwordConstants.TESTAMENT_NEW] = new RandomAccessFile(txtFile[SwordConstants.TESTAMENT_NEW], fileMode);
            } catch (FileNotFoundException ex) {
                assert false : ex;
                log.error("Could not open NT", ex); //$NON-NLS-1$
                idxRaf[SwordConstants.TESTAMENT_NEW] = null;
                txtRaf[SwordConstants.TESTAMENT_NEW] = null;
            }
        }

        active = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.common.activate.Activatable#deactivate(org.crosswire.common
     * .activate.Lock)
     */
    public final void deactivate(Lock lock) {
        try {
            idxRaf[SwordConstants.TESTAMENT_OLD].close();
            txtRaf[SwordConstants.TESTAMENT_OLD].close();

            idxRaf[SwordConstants.TESTAMENT_NEW].close();
            txtRaf[SwordConstants.TESTAMENT_NEW].close();
        } catch (IOException ex) {
            log.error("Failed to close files", ex); //$NON-NLS-1$
        } finally {
            idxRaf[SwordConstants.TESTAMENT_OLD] = null;
            txtRaf[SwordConstants.TESTAMENT_OLD] = null;

            idxRaf[SwordConstants.TESTAMENT_NEW] = null;
            txtRaf[SwordConstants.TESTAMENT_NEW] = null;
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
     *            testament number 0, 1 or 2
     * @return the text for the entry.
     * @throws IOException
     *             on a IO problem
     */
    protected String getEntry(String name, int testament, long index) throws IOException {
        DataIndex dataIndex = getIndex(idxRaf[testament], index);

        int size = dataIndex.getSize();
        if (size == 0) {
            return ""; //$NON-NLS-1$
        }

        if (size < 0) {
            log.error("In " + getBookMetaData().getInitials() + ": Verse " + name + " has a bad index size of " + size); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            return ""; //$NON-NLS-1$
        }

        byte[] data = SwordUtil.readRAF(txtRaf[testament], dataIndex.getOffset(), size);

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

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(RawBackend.class);

    protected RandomAccessFile[] idxRaf = new RandomAccessFile[3];
    protected RandomAccessFile[] txtRaf = new RandomAccessFile[3];
    protected File[] idxFile = new File[3];
    protected File[] txtFile = new File[3];

    /**
     * How many bytes in the offset pointers in the index
     */
    protected static final int OFFSETSIZE = 4;
}
