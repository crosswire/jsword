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
import java.io.ObjectInputStream;

import org.crosswire.common.compress.CompressorType;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.sword.state.OpenFileStateManager;
import org.crosswire.jsword.book.sword.state.RawLDBackendState;
import org.crosswire.jsword.book.sword.state.ZLDBackendState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An extension of RawLDBackend to read Z format files.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
 */
public class ZLDBackend extends RawLDBackend<ZLDBackendState> {
    /**
     * Simple ctor
     * @param sbmd 
     */
    public ZLDBackend(SwordBookMetaData sbmd) {
        super(sbmd, 4);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.RawLDBackend#initState()
     */
    @Override
    public ZLDBackendState initState() throws BookException {
        return OpenFileStateManager.instance().getZLDBackendState(getBookMetaData());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.RawLDBackend#getEntry(org.crosswire.jsword.book.sword.state.RawLDBackendState, org.crosswire.jsword.book.sword.DataEntry)
     */
    @Override
    protected DataEntry getEntry(RawLDBackendState fileState, DataEntry entry) {
        ZLDBackendState state = null;
        if (fileState instanceof ZLDBackendState) {
            state = (ZLDBackendState) fileState;
        } else {
            //something went terribly wrong
            log.error("Backend State was not of type ZLDBackendState. Ignoring this entry and exiting.");
            return new DataEntry(entry.getName(), new byte[0], entry.getCharset());
        }

        DataIndex blockIndex = entry.getBlockIndex();
        long blockNum = blockIndex.getOffset();
        int blockEntry = blockIndex.getSize();

        // Can we get the data from the cache
        byte[] uncompressed = null;
        if (blockNum == state.getLastBlockNum()) {
            uncompressed = state.getLastUncompressed();
        } else {
            byte[] temp;
            try {
                temp = SwordUtil.readRAF(state.getZdxRaf(), blockNum * ZDX_ENTRY_SIZE, ZDX_ENTRY_SIZE);
                if (temp == null || temp.length == 0) {
                    return new DataEntry(entry.getName(), new byte[0], entry.getCharset());
                }

                int blockStart = SwordUtil.decodeLittleEndian32(temp, 0);
                int blockSize = SwordUtil.decodeLittleEndian32(temp, 4);

                temp = SwordUtil.readRAF(state.getZdtRaf(), blockStart, blockSize);

                decipher(temp);

                String compressType = getBookMetaData().getProperty(SwordBookMetaData.KEY_COMPRESS_TYPE);
                uncompressed = CompressorType.fromString(compressType).getCompressor(temp).uncompress().toByteArray();

                // cache the uncompressed data for next time
                state.setLastBlockNum(blockNum);
                state.setLastUncompressed(uncompressed);
            } catch (IOException e) {
                return new DataEntry(entry.getName(), new byte[0], entry.getCharset());
            }
        }

        // get the "entry" from this block.
        int entryCount = SwordUtil.decodeLittleEndian32(uncompressed, 0);
        if (blockEntry >= entryCount) {
            return new DataEntry(entry.getName(), new byte[0], entry.getCharset());
        }

        int entryOffset = BLOCK_ENTRY_COUNT + (BLOCK_ENTRY_SIZE * blockEntry);
        int entryStart = SwordUtil.decodeLittleEndian32(uncompressed, entryOffset);
        int entrySize = SwordUtil.decodeLittleEndian32(uncompressed, entryOffset + 4);
        // Note: the actual entry is '\0' terminated
        int nullTerminator = SwordUtil.findByte(uncompressed, entryStart, (byte) 0x00);
        if (nullTerminator - entryStart + 1 == entrySize) {
            entrySize -= 1;
        }
        byte[] entryBytes = new byte[entrySize];
        System.arraycopy(uncompressed, entryStart, entryBytes, 0, entrySize);
        DataEntry finalEntry = new DataEntry(entry.getName(), entryBytes, getBookMetaData().getBookCharset());

        return finalEntry;
    }

    /**
     * Serialization support.
     * 
     * @param is
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
        is.defaultReadObject();
    }
    /** 
     * Experimental code.
     */
    @Override
    public void dumpIdxRaf() {
        RawLDBackendState state = null;
        long end = -1;
        try {
            state = initState();
            end = getCardinality();
            StringBuilder buf = new StringBuilder();
            System.out.println("index\toffset\tsize\tkey\tvalue");
            for (long i = 0; i < end; ++i) {
                DataIndex index = getIndex(state, i);
                int offset = index.getOffset();
                int size   = index.getSize();
                buf.setLength(0);
                buf.append(i);
                buf.append('\t');
                buf.append(offset);
                buf.append('\t');
                buf.append(size);
                if (size > 0) {
                    // Now read the data file for this key using the offset and size
                    byte[] data = SwordUtil.readRAF(state.getDatRaf(), offset, size);
                    DataEntry blockEntry = new DataEntry(Long.toString(i), data, getBookMetaData().getBookCharset());
                    DataIndex block = blockEntry.getBlockIndex();
                    DataEntry dataEntry = getEntry(state, blockEntry);
                    String key = blockEntry.getKey();
                    buf.append('\t');
                    buf.append(key);
                    buf.append('\t');
                    buf.append(block.getOffset());
                    buf.append('\t');
                    buf.append(block.getSize());
                    String raw;
                    buf.append('\t');
                    if (dataEntry.isLinkEntry()) {
                        raw = dataEntry.getLinkTarget();
                        buf.append("Linked to: ").append(raw.replace('\n', ' '));
                    } else {
                        raw = getRawText(dataEntry);
                        if (raw.length() > 43) {
                            buf.append(raw.substring(0, 40).replace('\n', ' '));
                            buf.append("...");
                        } else {
                            buf.append(raw);
                        }
                    }
                }
                System.out.println(buf.toString());
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BookException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            OpenFileStateManager.instance().release(state);
        }
    }

    private static final int ZDX_ENTRY_SIZE = 8;
    private static final int BLOCK_ENTRY_COUNT = 4;
    private static final int BLOCK_ENTRY_SIZE = 8;

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(ZLDBackend.class);
    private static final long serialVersionUID = 3536098410391064446L;
}
