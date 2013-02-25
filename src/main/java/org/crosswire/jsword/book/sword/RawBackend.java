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
 * Copyright: 2005-2013
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.book.sword;

import java.io.IOException;
import java.io.RandomAccessFile;

import org.crosswire.common.util.IOUtil;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.sword.state.OpenFileStateManager;
import org.crosswire.jsword.book.sword.state.RawBackendState;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.versification.Testament;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Both Books and Commentaries seem to use the same format so this class
 * abstracts out the similarities.
 * 
 * @param <T> The type of the RawBackendState that this class extends.
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class RawBackend<T extends RawBackendState> extends AbstractBackend<RawBackendState> {

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
        String v11nName = getBookMetaData().getProperty(ConfigEntryType.VERSIFICATION).toString();
        Versification v11n = Versifications.instance().getVersification(v11nName);
        Verse verse = KeyUtil.getVerse(key);

        RawBackendState initState = null;
        try {
            int index = verse.getOrdinal();
            Testament testament = v11n.getTestament(index);
            index = v11n.getTestamentOrdinal(index);
            initState = initState();
            RandomAccessFile idxRaf = testament == Testament.NEW ? initState.getNtIdxRaf() : initState.getOtIdxRaf();

            // If this is a single testament Bible, return nothing.
            if (idxRaf == null) {
                return false;
            }

            DataIndex dataIndex = getIndex(idxRaf, index);

            return dataIndex.getSize() > 0;
        } catch (IOException ex) {
            return false;
        } catch (BookException e) {
            return false;
        } finally {
            OpenFileStateManager.release(initState);
        }
    }

    public T initState() throws BookException {
        return (T) OpenFileStateManager.getRawBackendState(getBookMetaData());
    }

    public String getRawText(RawBackendState state, Key key) throws IOException {
        return readRawContent(state, key);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#getRawText(org.crosswire.jsword.passage.Key)
     */
    public String readRawContent(RawBackendState state, Key key) throws IOException {
        String v11nName = getBookMetaData().getProperty(ConfigEntryType.VERSIFICATION).toString();
        Versification v11n = Versifications.instance().getVersification(v11nName);
        Verse verse = KeyUtil.getVerse(key);

        int index = verse.getOrdinal();

        Testament testament = v11n.getTestament(index);
        index = v11n.getTestamentOrdinal(index);
        RawBackendState initState = null;
        try {
            initState = initState();
            RandomAccessFile idxRaf = testament == Testament.NEW ? state.getNtIdxRaf() : state.getOtIdxRaf();

            // If this is a single testament Bible, return nothing.
            if (idxRaf == null) {
                return "";
            }

            return getEntry(state, verse.getName(), testament, index);
        } catch (BookException e) {
            return "";
        } finally {
            OpenFileStateManager.release(initState);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#setRawText(org.crosswire.jsword.passage.Key, java.lang.String)
     */
    public void setRawText(RawBackendState state, Key key, String text) throws BookException, IOException {
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#isWritable()
     */
    @Override
    public boolean isWritable() {
        RawBackendState rawBackendState = null;
        try {
        rawBackendState = initState();
        return rawBackendState.isWritable();
        } catch (BookException e) {
            return false;
        } finally {
            IOUtil.close(rawBackendState);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#setAliasKey(org.crosswire.jsword.passage.Key, org.crosswire.jsword.passage.Key)
     */
    public void setAliasKey(RawBackendState state, Key alias, Key source) throws IOException {
        throw new UnsupportedOperationException();
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
     * @param state 
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
    protected String getEntry(RawBackendState state, String name, Testament testament, long index) throws IOException {
        final RandomAccessFile idxRaf;
        final RandomAccessFile txtRaf;
        if (testament == Testament.NEW) {
            idxRaf = state.getNtIdxRaf();
            txtRaf = state.getNtTextRaf();
        } else {
             idxRaf = state.getOtIdxRaf();
             txtRaf = state.getOtTextRaf();
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
     * How many bytes in the size count in the index
     */
    protected final int datasize;

    /**
     * The number of bytes for each entry in the index: either 6 or 8
     */
    protected final int entrysize;

    /**
     * How many bytes in the offset pointers in the index
     */
    protected static final int OFFSETSIZE = 4;

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(RawBackend.class);


}
