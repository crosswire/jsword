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
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.jsword.book.sword;

import java.io.IOException;
import java.io.RandomAccessFile;

import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.sword.state.OpenFileStateManager;
import org.crosswire.jsword.book.sword.state.RawBackendState;
import org.crosswire.jsword.passage.BitwisePassage;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.RocketPassage;
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
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class RawBackend<T extends RawBackendState> extends AbstractBackend<RawBackendState> {

    /**
     * Simple ctor
     * 
     * @param sbmd 
     * @param datasize 
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
        return getRawTextLength(key) > 0;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#size(org.crosswire.jsword.passage.Key)
     */
    @Override
    public int getRawTextLength(Key key) {
        String v11nName = getBookMetaData().getProperty(BookMetaData.KEY_VERSIFICATION);
        Versification v11n = Versifications.instance().getVersification(v11nName);
        Verse verse = KeyUtil.getVerse(key);

        RawBackendState initState = null;
        try {
            int index = verse.getOrdinal();
            Testament testament = v11n.getTestament(index);
            index = v11n.getTestamentOrdinal(index);
            initState = initState();
            RandomAccessFile idxRaf = initState.getIdxRaf(testament);

            // If this is a single testament Bible, return nothing.
            if (idxRaf == null) {
                return 0;
            }

            DataIndex dataIndex = getIndex(idxRaf, index);

            return dataIndex.getSize();
        } catch (IOException ex) {
            return 0;
        } catch (BookException e) {
            return 0;
        } finally {
            OpenFileStateManager.instance().release(initState);
        }
    }

    @Override
    public Key getGlobalKeyList() throws BookException {
        RawBackendState rafBook = null;
        try {
            rafBook = initState();

            String v11nName = getBookMetaData().getProperty(BookMetaData.KEY_VERSIFICATION);
            Versification v11n = Versifications.instance().getVersification(v11nName);

            Testament[] testaments = new Testament[] {
                    Testament.OLD, Testament.NEW
            };

            BitwisePassage passage = new RocketPassage(v11n);
            passage.raiseEventSuppresion();
            passage.raiseNormalizeProtection();

            for (Testament currentTestament : testaments) {
                RandomAccessFile idxRaf = rafBook.getIdxRaf(currentTestament);

                // If Bible does not contain the desired testament, then false
                if (idxRaf == null) {
                    // no keys in this testament
                    continue;
                }

                int maxIndex = v11n.getCount(currentTestament) - 1;

                // Read in the whole index, a few hundred Kb at most.
                byte[] temp = SwordUtil.readRAF(idxRaf, 0, entrysize * maxIndex);

                // For each entry of entrysize bytes, the length of the verse in bytes
                // is in the last datasize bytes. If all bytes are 0, then there is no content.
                if (datasize == 2) {
                    for (int ii = 0; ii < temp.length; ii += entrysize) {
                        // This can be simplified to temp[ii + 4] == 0 && temp[ii + 5] == 0.
                        // int verseSize = SwordUtil.decodeLittleEndian16(temp, ii + 4);
                        // if (verseSize > 0) {
                        if (temp[ii + 4] != 0 || temp[ii + 5] != 0) {
                            int ordinal = ii / entrysize;
                            passage.addVersifiedOrdinal(v11n.getOrdinal(currentTestament, ordinal));
                        }
                    }
                } else { // datasize == 4
                    for (int ii = 0; ii < temp.length; ii += entrysize) {
                        // This can be simplified to temp[ii + 4] == 0 && temp[ii + 5] == 0 && temp[ii + 6] == 0 && temp[ii + 7] == 0.
                        // int verseSize = SwordUtil.decodeLittleEndian32(temp, ii + 4);
                        // if (verseSize > 0) {
                        if (temp[ii + 4] != 0 || temp[ii + 5] != 0 || temp[ii + 6] != 0 || temp[ii + 7] != 0) {
                            int ordinal = ii / entrysize;
                            passage.addVersifiedOrdinal(v11n.getOrdinal(currentTestament, ordinal));
                        }
                    }
                }
            }

            passage.lowerNormalizeProtection();
            passage.lowerEventSuppressionAndTest();

            return passage;
        } catch (IOException e) {
            throw new BookException(JSMsg.gettext("Unable to read key list from book."));
        } finally {
            OpenFileStateManager.instance().release(rafBook);
        }
    }

    public T initState() throws BookException {
        return (T) OpenFileStateManager.instance().getRawBackendState(getBookMetaData());
    }

    public String getRawText(RawBackendState state, Key key) throws IOException {
        return readRawContent(state, key);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#getRawText(org.crosswire.jsword.passage.Key)
     */
    public String readRawContent(RawBackendState state, Key key) throws IOException {
        String v11nName = getBookMetaData().getProperty(BookMetaData.KEY_VERSIFICATION);
        Versification v11n = Versifications.instance().getVersification(v11nName);
        Verse verse = KeyUtil.getVerse(key);

        int index = verse.getOrdinal();

        Testament testament = v11n.getTestament(index);
        index = v11n.getTestamentOrdinal(index);
        RawBackendState initState = null;
        try {
            initState = initState();
            return getEntry(state, verse.getName(), testament, index);
        } catch (BookException e) {
            return "";
        } finally {
            OpenFileStateManager.instance().release(initState);
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
            OpenFileStateManager.instance().release(rawBackendState);
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
        idxRaf = state.getIdxRaf(testament);
        txtRaf = state.getTextRaf(testament);

        // It may be that this is a single testament Bible
        if (idxRaf == null) {
            return "";
        }

        DataIndex dataIndex = getIndex(idxRaf, index);

        int size = dataIndex.getSize();
        if (size == 0) {
            return "";
        }

        if (size < 0) {
            log.error("In {}: Verse {} has a bad index size of {}", getBookMetaData().getInitials(), name, Integer.toString(size));
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
