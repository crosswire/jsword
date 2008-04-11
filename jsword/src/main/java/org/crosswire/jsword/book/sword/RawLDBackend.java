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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.crosswire.common.activate.Activator;
import org.crosswire.common.activate.Lock;
import org.crosswire.common.icu.DateFormatter;
import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.DefaultLeafKeyList;
import org.crosswire.jsword.passage.Key;

/**
 * An implementation AbstractKeyBackend to read RAW format files.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class RawLDBackend extends AbstractKeyBackend
{
    /**
     * Simple ctor
     * @param datasize We need to know how many bytes in the size portion of the index
     */
    public RawLDBackend(SwordBookMetaData sbmd, int datasize)
    {
        super(sbmd);
        this.size = -1;
        this.datasize = datasize;
        this.entrysize = OFFSETSIZE + datasize;
    }

    /*
     * (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#getRawText(org.crosswire.jsword.passage.Key, java.lang.String)
     */
    /* @Override */
    public String getRawText(Key key) throws BookException
    {
        return getRawText(key.getName());
    }

    public String getRawText(String key) throws BookException
    {
        if (!checkActive())
        {
            return ""; //$NON-NLS-1$
        }

        try
        {
            int pos = search(key);
            if (pos >= 0)
            {
                DataEntry entry = getEntry(key, pos);
                if (entry.isLinkEntry())
                {
                    return getRawText(entry.getLinkTarget());
                }
                return getRawText(entry);
            }
            throw new BookException(UserMsg.READ_FAIL);
        }
        catch (IOException ex)
        {
            throw new BookException(UserMsg.READ_FAIL, ex);
        }
    }

    protected String getRawText(DataEntry entry)
    {
        String cipherKeyString = (String) getBookMetaData().getProperty(ConfigEntryType.CIPHER_KEY);
        return entry.getRawText((cipherKeyString != null) ? cipherKeyString.getBytes() : null);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getCardinality()
     */
    public int getCardinality()
    {
        if (!checkActive())
        {
            return 0;
        }

        if (size == -1)
        {
            try
            {
                size = (int) (idxRaf.length() / entrysize);
            }
            catch (IOException e)
            {
                size = 0;
            }
        }
        return size;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#get(int)
     */
    public Key get(int index)
    {
        if (checkActive())
        {
            try
            {
                if (index < getCardinality())
                {
                    DataEntry entry = getEntry(getBookMetaData().getInitials(), index);
                    return new DefaultLeafKeyList(entry.getKey());
                }
            }
            catch (IOException e)
            {
                // fall through
            }
        }
        throw new ArrayIndexOutOfBoundsException(index);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#indexOf(org.crosswire.jsword.passage.Key)
     */
    public int indexOf(Key that)
    {
        try
        {
            return search(that.getName());
        }
        catch (IOException e)
        {
            return -getCardinality() - 1;
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#activate(org.crosswire.common.activate.Lock)
     */
    public void activate(Lock lock)
    {
        active           = false;
        size             = -1;
        idxFile          = null;
        datFile          = null;
        idxRaf           = null;
        datRaf           = null;

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
            idxFile = new File(path.getPath() + SwordConstants.EXTENSION_INDEX);
            datFile = new File(path.getPath() + SwordConstants.EXTENSION_DATA);

            if (!idxFile.canRead())
            {
                Reporter.informUser(this, new BookException(UserMsg.READ_FAIL, new Object[] { idxFile.getAbsolutePath() }));
                return;
            }

            if (!datFile.canRead())
            {
                Reporter.informUser(this, new BookException(UserMsg.READ_FAIL, new Object[] { datFile.getAbsolutePath() }));
                return;
            }

            // Open the files
            idxRaf = new RandomAccessFile(idxFile, FileUtil.MODE_READ);
            datRaf = new RandomAccessFile(datFile, FileUtil.MODE_READ);
        }
        catch (IOException ex)
        {
            log.error("failed to open files", ex); //$NON-NLS-1$
            idxRaf = null;
            datRaf = null;
            return;
        }

        active = true;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#deactivate(org.crosswire.common.activate.Lock)
     */
    public void deactivate(Lock lock)
    {
        size             = -1;
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
        }
        catch (IOException ex)
        {
            log.error("failed to close files", ex); //$NON-NLS-1$
        }
        finally
        {
            idxRaf = null;
            datRaf = null;
        }

        active = false;
    }

    /**
     * Helper method so we can quickly activate ourselves on access
     */
    protected boolean checkActive()
    {
        if (!isActive())
        {
            Activator.activate(this);
        }
        return isActive();
    }

    /**
     * Determine whether we are active.
     */
    protected boolean isActive()
    {
        return active;
    }
    
    /**
     * Get the Index (that is offset and size) for an entry.
     * @param entry
     * @return
     * @throws IOException 
     */
    private DataIndex getIndex(long entry) throws IOException
    {
        // Read the offset and size for this key from the index
        byte[] buffer = SwordUtil.readRAF(idxRaf, entry * entrysize, entrysize);
        int entryOffset = SwordUtil.decodeLittleEndian32(buffer, 0);
        int entrySize = -1;
        switch (datasize)
        {
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
     * @param index the entry to get
     * @return the text for the entry.
     * @throws IOException 
     */
    private DataEntry getEntry(String reply, int index) throws IOException
    {
        DataIndex dataIndex = getIndex(index);
        // Now read the data file for this key using the offset and size
        byte[] data = SwordUtil.readRAF(datRaf, dataIndex.getOffset(), dataIndex.getSize());

        return new DataEntry(reply, data, getBookMetaData().getBookCharset());
    }

    /**
     * Find a matching entry, returning it's index. Otherwise return < 0, such that
     * (-pos - 1) gives the insertion index.
     * @param key
     * @return
     * @throws IOException
     */
    private int search(String key) throws IOException
    {
        if (!checkActive())
        {
            return -1;
        }

        SwordBookMetaData bmd = getBookMetaData();

        boolean isDailyDevotional = bmd.getBookCategory().equals(BookCategory.DAILY_DEVOTIONS);

        Calendar greg = new GregorianCalendar();
        DateFormatter nameDF = DateFormatter.getDateInstance();

        String target = key.toUpperCase(Locale.US);

        int low = 1;
        int high = getCardinality() - 1;

        while (low <= high)
        {
            int mid = (low + high) >> 1;

            // Get the key for the item at "mid"
            DataEntry entry = getEntry(key, mid);
            String midVal = entry.getKey();

            // Massage midVal if can be.
            if (isDailyDevotional && midVal.length() >= 3)
            {
                String[] spec = StringUtil.splitAll(midVal, '.');
                greg.set(Calendar.MONTH, Integer.parseInt(spec[0]) - 1);
                greg.set(Calendar.DATE, Integer.parseInt(spec[1]));
                midVal = nameDF.format(greg.getTime());
            }

            int cmp = midVal.toUpperCase(Locale.US).compareTo(target);

            if (cmp < 0)
            {
                low = mid + 1;
            }
            else if (cmp > 0)
            {
                high = mid - 1;
            }
            else
            {
                return mid; // key found
            }
        }

        // Strong's Greek And Hebrew dictionaries have an introductory entry, so check it for a match.
        // Get the key for the item at "mid"
        DataEntry entry = getEntry(key, 0);
        String midVal = entry.getKey();

        // Massage midVal if can be.
        if (isDailyDevotional && midVal.length() >= 3)
        {
            String[] spec = StringUtil.splitAll(midVal, '.');
            greg.set(Calendar.MONTH, Integer.parseInt(spec[0]) - 1);
            greg.set(Calendar.DATE, Integer.parseInt(spec[1]));
            midVal = nameDF.format(greg.getTime());
        }

        int cmp = midVal.toUpperCase(Locale.US).compareTo(target);
        if (cmp == 0)
        {
            return 0;
        }

        return -(low + 1); // key not found
    }

    /**
     * How many bytes in the offset pointers in the index
     */
    private static final int OFFSETSIZE = 4;

    /**
     * Flags whether there are open files or not
     */
    private boolean active;

    /**
     * The number of bytes in the size count in the index
     */
    private int datasize;

    /**
     * The number of bytes for each entry in the index: either 6 or 8
     */
    private int entrysize;

    /**
     * The number of entries in the book.
     */
    private int size;

    /**
     * The index file
     */
    private File idxFile;

    /**
     * The index random access file
     */
    private RandomAccessFile idxRaf;

    /**
     * The data file
     */
    private File datFile;

    /**
     * The data random access file
     */
    private RandomAccessFile datRaf;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 818089833394450383L;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(RawLDBackend.class);
}
