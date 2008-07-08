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
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;

import org.crosswire.common.activate.Lock;
import org.crosswire.common.compress.CompressorType;
import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.BookException;

/**
 * An extension of RawLDBackend to read Z format files.
 * 
 * @see gnu.lgpl.License for license details. The copyright to this program is
 *      held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class ZLDBackend extends RawLDBackend
{
    /**
     * Simple ctor
     */
    public ZLDBackend(SwordBookMetaData sbmd)
    {
        super(sbmd, 4);
        this.lastBlockNum = -1;
        this.lastUncompressed = EMPTY_BYTES;
    }

    protected String getRawText(DataEntry entry)
    {
        DataIndex blockIndex = entry.getBlockIndex();
        long blockNum = blockIndex.getOffset();
        int blockEntry = blockIndex.getSize();

        // Can we get the data from the cache
        byte[] uncompressed = null;
        if (blockNum == lastBlockNum)
        {
            uncompressed = lastUncompressed;
        }
        else
        {
            byte[] temp;
            try
            {
                temp = SwordUtil.readRAF(zdxRaf, blockNum * ZDX_ENTRY_SIZE, ZDX_ENTRY_SIZE);
                if (temp == null || temp.length == 0)
                {
                    return ""; //$NON-NLS-1$
                }

                int blockStart = SwordUtil.decodeLittleEndian32(temp, 0);
                int blockSize = SwordUtil.decodeLittleEndian32(temp, 4);

                temp = SwordUtil.readRAF(zdtRaf, blockStart, blockSize);

                decipher(temp);

                String compressType = (String) getBookMetaData().getProperty(ConfigEntryType.COMPRESS_TYPE);
                uncompressed = CompressorType.fromString(compressType).getCompressor(temp).uncompress().toByteArray();

                // cache the uncompressed data for next time
                lastBlockNum = blockNum;
                lastUncompressed = uncompressed;
            }
            catch (IOException e)
            {
                return ""; //$NON-NLS-1$
            }
        }

        // get the "entry" from this block.
        int entryCount = SwordUtil.decodeLittleEndian32(uncompressed, 0);
        if (blockEntry >= entryCount)
        {
            return ""; //$NON-NLS-1$
        }

        int entryOffset = BLOCK_ENTRY_COUNT + (BLOCK_ENTRY_SIZE * blockEntry);
        int entryStart = SwordUtil.decodeLittleEndian32(uncompressed, entryOffset);
        // Note: the actual entry is '\0' terminated
        int entrySize = SwordUtil.decodeLittleEndian32(uncompressed, entryOffset + 4);
        byte[] entryBytes = new byte[entrySize];
        System.arraycopy(uncompressed, entryStart, entryBytes, 0, entrySize);

        return SwordUtil.decode(entry.getName(), entryBytes, getBookMetaData().getBookCharset()).trim();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.activate.Activatable#activate(org.crosswire.common.activate.Lock)
     */
    public void activate(Lock lock)
    {
        super.activate(lock);

        active = false;
        zdxFile = null;
        zdtFile = null;
        zdxRaf = null;
        zdtRaf = null;
        lastBlockNum = -1;
        lastUncompressed = EMPTY_BYTES;

        URI path = null;
        try
        {
            path = getExpandedDataPath();
        }
        catch (BookException e)
        {
            Reporter.informUser(this, e);
            return;
        }

        try
        {
            zdxFile = new File(path.getPath() + EXTENSION_Z_INDEX);
            zdtFile = new File(path.getPath() + EXTENSION_Z_DATA);

            if (!zdxFile.canRead())
            {
                Reporter.informUser(this, new BookException(UserMsg.READ_FAIL, new Object[] { zdtFile.getAbsolutePath() }));
                return;
            }

            if (!zdtFile.canRead())
            {
                Reporter.informUser(this, new BookException(UserMsg.READ_FAIL, new Object[] { zdtFile.getAbsolutePath() }));
                return;
            }

            // Open the files
            zdxRaf = new RandomAccessFile(zdxFile, FileUtil.MODE_READ);
            zdtRaf = new RandomAccessFile(zdtFile, FileUtil.MODE_READ);
        }
        catch (IOException ex)
        {
            log.error("failed to open files", ex); //$NON-NLS-1$
            zdxRaf = null;
            zdtRaf = null;
            return;
        }

        active = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.activate.Activatable#deactivate(org.crosswire.common.activate.Lock)
     */
    public void deactivate(Lock lock)
    {
        super.deactivate(lock);
        lastBlockNum = -1;
        lastUncompressed = EMPTY_BYTES;

        try
        {
            if (zdxRaf != null)
            {
                zdxRaf.close();
            }
            if (zdtRaf != null)
            {
                zdtRaf.close();
            }
        }
        catch (IOException ex)
        {
            log.error("failed to close files", ex); //$NON-NLS-1$
        }
        finally
        {
            zdxRaf = null;
            zdtRaf = null;
        }

        active = false;
    }

    /**
     * Determine whether we are active.
     */
    protected boolean isActive()
    {
        return active && super.isActive();
    }

    private static final String EXTENSION_Z_INDEX = ".zdx"; //$NON-NLS-1$
    private static final String EXTENSION_Z_DATA  = ".zdt"; //$NON-NLS-1$

    private static final int    ZDX_ENTRY_SIZE    = 8;
    private static final int    BLOCK_ENTRY_COUNT = 4;
    private static final int    BLOCK_ENTRY_SIZE  = 8;
    private static final byte[] EMPTY_BYTES       = new byte[0];

    /**
     * Flags whether there are open files or not
     */
    private boolean             active;

    /**
     * The compressed index.
     */
    private File                zdxFile;

    /**
     * The compressed index random access file.
     */
    private RandomAccessFile    zdxRaf;

    /**
     * The compressed text.
     */
    private File                zdtFile;

    /**
     * The compressed text random access file.
     */
    private RandomAccessFile    zdtRaf;

    /**
     * The index of the block that is cached.
     */
    private long                lastBlockNum;

    /**
     * The cache for a read of a compressed block.
     */
    private byte[]              lastUncompressed;

    /**
     * Serialization ID
     */
    private static final long   serialVersionUID  = 3536098410391064446L;

    /**
     * The log stream
     */
    private static final Logger log               = Logger.getLogger(ZLDBackend.class);
}
