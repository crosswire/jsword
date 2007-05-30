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
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.crosswire.common.activate.Activator;
import org.crosswire.common.activate.Lock;
import org.crosswire.common.compress.CompressorType;
import org.crosswire.common.util.ClassUtil;
import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.DataPolice;
import org.crosswire.jsword.passage.DefaultKeyList;
import org.crosswire.jsword.passage.Key;

/**
 * An implementation KeyBackend to read Z format files.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class ZLDBackend extends AbstractBackend
{
    private static final String EXTENSION_INDEX = ".idx"; //$NON-NLS-1$
    private static final String EXTENSION_DATA = ".dat"; //$NON-NLS-1$
    private static final String EXTENSION_Z_INDEX = ".zdx"; //$NON-NLS-1$
    private static final String EXTENSION_Z_DATA = ".zdt"; //$NON-NLS-1$

    private static final int IDX_ENTRY_SIZE = 8;
    private static final int ZDX_ENTRY_SIZE = 8;
    private static final int BLOCK_ENTRY_COUNT = 4;
    private static final int BLOCK_ENTRY_SIZE = 8;

    /**
     * Used to separate the key name from the key value
     */
    private static final byte SEPARATOR = 10; // ^M=CR=13=0x0d=\r ^J=LF=10=0x0a=\n

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(ZLDBackend.class);

    private File idxFile;

    private File datFile;

    /**
     * The compressed index.
     */
    private File zdxFile;

    /**
     * The compressed text.
     */
    private File zdtFile;

    private RandomAccessFile idxRaf;
    private RandomAccessFile datRaf;
    private RandomAccessFile zdxRaf;
    private RandomAccessFile zdtRaf;
    private boolean active;
    private Key keys;
    private long lastBlockNum = -1;
    private static final byte[] EMPTY_BYTES = new byte[0];
    private byte[] lastUncompressed = EMPTY_BYTES;

    /**
     * Simple ctor
     * @throws BookException 
     */
    public ZLDBackend(SwordBookMetaData sbmd) throws BookException
    {
        super(sbmd);

        String path = getExpandedDataPath();

        idxFile = new File(path + EXTENSION_INDEX);
        datFile = new File(path + EXTENSION_DATA);
        zdxFile = new File(path + EXTENSION_Z_INDEX);
        zdtFile = new File(path + EXTENSION_Z_DATA);

        if (!idxFile.canRead())
        {
            throw new BookException(Msg.READ_FAIL, new Object[] { idxFile.getAbsolutePath() });
        }

        if (!datFile.canRead())
        {
            throw new BookException(Msg.READ_FAIL, new Object[] { datFile.getAbsolutePath() });
        }

        if (!zdxFile.canRead())
        {
            throw new BookException(Msg.READ_FAIL, new Object[] { zdxFile.getAbsolutePath() });
        }

        if (!zdtFile.canRead())
        {
            throw new BookException(Msg.READ_FAIL, new Object[] { zdtFile.getAbsolutePath() });
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#activate(org.crosswire.common.activate.Lock)
     */
    public final void activate(Lock lock)
    {
        try
        {
            idxRaf = new RandomAccessFile(idxFile, FileUtil.MODE_READ);
            datRaf = new RandomAccessFile(datFile, FileUtil.MODE_READ);
            zdxRaf = new RandomAccessFile(zdxFile, FileUtil.MODE_READ);
            zdtRaf = new RandomAccessFile(zdtFile, FileUtil.MODE_READ);
        }
        catch (IOException ex)
        {
            log.error("failed to open files", ex); //$NON-NLS-1$
            idxRaf = null;
            datRaf = null;
            zdxRaf = null;
            zdtRaf = null;
        }
        active = true;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#deactivate(org.crosswire.common.activate.Lock)
     */
    public final void deactivate(Lock lock)
    {
        try
        {
            if (idxRaf != null)
            {
                idxRaf.close();
            }
            if (datRaf != null)
            {
                datRaf.close();
            }
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
            log.error("failed to close nt files", ex); //$NON-NLS-1$
        }
        finally
        {
            idxRaf = null;
            datRaf = null;
            zdxRaf = null;
            zdtRaf = null;
        }
        active = false;
   }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.KeyBackend#readIndex()
     */
    /* @Override */
    public Key readIndex()
    {
        checkActive();
        SwordBookMetaData bmd = getBookMetaData();
        String charset = bmd.getBookCharset();

        keys = new DefaultKeyList(null, bmd.getName());

        boolean isDailyDevotional = bmd.getBookCategory().equals(BookCategory.DAILY_DEVOTIONS);

        Calendar greg = new GregorianCalendar();
        DateFormat nameDF = DateFormat.getDateInstance(DateFormat.MEDIUM);

        long entries;
        try
        {
            entries = idxRaf.length() / IDX_ENTRY_SIZE;
        }
        catch (IOException ex)
        {
            Reporter.informUser(this, ex);
            return keys;
        }

        for (long entry = 0; entry < entries; entry++)
        {
            try
            {
                // Read the offset and size for this key from the index
                byte[] buffer = SwordUtil.readRAF(idxRaf, entry * IDX_ENTRY_SIZE, IDX_ENTRY_SIZE);
                int offset = SwordUtil.decodeLittleEndian32(buffer, 0);
                int size = SwordUtil.decodeLittleEndian32(buffer, 4);

                // Now read the data file for this key using the offset and size
                byte[] data = SwordUtil.readRAF(datRaf, offset, size);

                int keyend = SwordUtil.findByte(data, SEPARATOR);
                if (keyend == -1)
                {
                    DataPolice.report("Failed to find keyname. offset=" + offset + " data='" + new String(data) + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    continue;
                }

                byte[] keydata = new byte[keyend];
                System.arraycopy(data, 0, keydata, 0, keyend);

                String keytitle = SwordUtil.decode(keys, keydata, charset).trim();

                // for some wierd reason plain text (i.e. SourceType=0) dicts
                // all get \ added to the ends of the index entries.
                if (keytitle.endsWith("\\")) //$NON-NLS-1$
                {
                    keytitle = keytitle.substring(0, keytitle.length() - 1);
                }

                if (isDailyDevotional)
                {
                    String[] parts = StringUtil.splitAll(keytitle, '.');
                    greg.set(Calendar.MONTH, Integer.parseInt(parts[0]) - 1);
                    greg.set(Calendar.DATE, Integer.parseInt(parts[1]));
                    keytitle = nameDF.format(greg.getTime());
                }

                Key key = new IndexKey(keytitle, offset, size, keys);

                // remove duplicates, keeping later one.
                // This occurs under some conditions:
                // For daily devotionals where 02.29 becomes calendarized to Mar 1 for non-leap years
                // For modules that have been updated by appending new data.
                if (keys.contains(key))
                {
                    keys.removeAll(key);
                }

                keys.addAll(key);
            }
            catch (IOException ex)
            {
                log.error("Ignoring entry", ex); //$NON-NLS-1$
            }
            catch (NumberFormatException e)
            {
                log.error("Ignoring entry", e); //$NON-NLS-1$
            }
        }

        return keys;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#getRawText(org.crosswire.jsword.passage.Key, java.lang.String)
     */
    /* @Override */
    public String getRawText(Key key) throws BookException
    {
        checkActive();

        SwordBookMetaData sbmd = getBookMetaData();
        String charset = sbmd.getBookCharset();
        String compressType = sbmd.getProperty(ConfigEntryType.COMPRESS_TYPE);

        if (!(key instanceof IndexKey))
        {
            throw new BookException(Msg.BAD_KEY, new Object[] { ClassUtil.getShortClassName(key.getClass()), key.getName() });
        }

        IndexKey ikey = (IndexKey) key;

        try
        {
            byte[] data = SwordUtil.readRAF(datRaf, ikey.getOffset(), ikey.getSize());

            int keyend = SwordUtil.findByte(data, SEPARATOR);
            if (keyend == -1)
            {
                throw new BookException(Msg.READ_FAIL);
            }

            int remainder = data.length - (keyend + 1);
            byte[] temp = new byte[remainder];
            System.arraycopy(data, keyend + 1, temp, 0, remainder);

            String linkCheck = new String(temp, 0, 5, charset);
            if ("@LINK".equals(linkCheck)) //$NON-NLS-1$
            {
                keyend = SwordUtil.findByte(temp, SEPARATOR);
                String linkKey = new String(temp, 6, temp.length - (keyend + 1), charset).trim();
                ikey = (IndexKey) keys.get(keys.indexOf(new IndexKey(linkKey)));
                return getRawText(ikey);
            }

            long blockNum = SwordUtil.decodeLittleEndian32(temp, 0);
            int entry = SwordUtil.decodeLittleEndian32(temp, 4);

            // Can we get the data from the cache
            byte[] uncompressed = null;
            if (blockNum == lastBlockNum)
            {
                uncompressed = lastUncompressed;
            }
            else
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

                uncompressed = CompressorType.fromString(compressType).getCompressor(temp).uncompress();

                // cache the uncompressed data for next time
                lastBlockNum = blockNum;
                lastUncompressed = uncompressed;
            }

            // get the "entry" from this block.
            int entryCount = SwordUtil.decodeLittleEndian32(uncompressed, 0);
            if (entry >= entryCount)
            {
                return ""; //$NON-NLS-1$
            }
            int entryOffset = BLOCK_ENTRY_COUNT + (BLOCK_ENTRY_SIZE * entry);
            int entryStart = SwordUtil.decodeLittleEndian32(uncompressed, entryOffset);
            // Note: the actual entry is '\0' terminated
            int entrySize = SwordUtil.decodeLittleEndian32(uncompressed, entryOffset + 4);
            byte[] entryBytes = new byte[entrySize];
            System.arraycopy(uncompressed, entryStart, entryBytes, 0, entrySize);

            return SwordUtil.decode(key, entryBytes, charset).trim();
        }
        catch (IOException e)
        {
            throw new BookException(Msg.READ_FAIL, e);
        }
    }

    /**
     * Helper method so we can quickly activate ourselves on access
     */
    protected final void checkActive()
    {
        if (!active)
        {
            Activator.activate(this);
        }
    }
}
