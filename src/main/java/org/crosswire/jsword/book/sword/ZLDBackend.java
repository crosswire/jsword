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

import java.io.IOException;
import java.io.ObjectInputStream;

import org.crosswire.common.compress.CompressorType;
import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.sword.state.ZLDBackendState;

/**
 * An extension of RawLDBackend to read Z format files.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class ZLDBackend extends RawLDBackend<ZLDBackendState> {
    /**
     * Simple ctor
     */
    public ZLDBackend(SwordBookMetaData sbmd) {
        super(sbmd, 4);
    }

    @Override
    public ZLDBackendState initState() throws BookException{
        return new ZLDBackendState(getBookMetaData());
     }
    
    protected String getRawText(ZLDBackendState state, DataEntry entry) {
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
                    return "";
                }

                int blockStart = SwordUtil.decodeLittleEndian32(temp, 0);
                int blockSize = SwordUtil.decodeLittleEndian32(temp, 4);

                temp = SwordUtil.readRAF(state.getZdtRaf(), blockStart, blockSize);

                decipher(temp);

                String compressType = (String) getBookMetaData().getProperty(ConfigEntryType.COMPRESS_TYPE);
                uncompressed = CompressorType.fromString(compressType).getCompressor(temp).uncompress().toByteArray();

                // cache the uncompressed data for next time
                state.setLastBlockNum(blockNum);
                state.setLastUncompressed(uncompressed);
            } catch (IOException e) {
                return "";
            }
        }

        // get the "entry" from this block.
        int entryCount = SwordUtil.decodeLittleEndian32(uncompressed, 0);
        if (blockEntry >= entryCount) {
            return "";
        }

        int entryOffset = BLOCK_ENTRY_COUNT + (BLOCK_ENTRY_SIZE * blockEntry);
        int entryStart = SwordUtil.decodeLittleEndian32(uncompressed, entryOffset);
        // Note: the actual entry is '\0' terminated
        int entrySize = SwordUtil.decodeLittleEndian32(uncompressed, entryOffset + 4);
        byte[] entryBytes = new byte[entrySize];
        System.arraycopy(uncompressed, entryStart, entryBytes, 0, entrySize);

        return SwordUtil.decode(entry.getName(), entryBytes, getBookMetaData().getBookCharset()).trim();
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


    private static final int ZDX_ENTRY_SIZE = 8;
    private static final int BLOCK_ENTRY_COUNT = 4;
    private static final int BLOCK_ENTRY_SIZE = 8;


    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(ZLDBackend.class);
    private static final long serialVersionUID = 3536098410391064446L;
}
