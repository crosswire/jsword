package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.commons.lang.ClassUtils;
import org.crosswire.common.activate.Activator;
import org.crosswire.common.activate.Lock;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.LogicError;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.DataPolice;
import org.crosswire.jsword.passage.DefaultKey;
import org.crosswire.jsword.passage.DefaultKeyList;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyList;

/**
 * An implementation KeyBackend to read RAW format files.
 *
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class RawLDBackend implements Backend
{
    /**
     * Simple ctor
     * @param datasize We need to know how many bytes in the size portion of the index
     */
    public RawLDBackend(SwordConfig config, String path, int datasize) throws BookException
    {
        this.config = config;
        this.datasize = datasize;

        if (datasize != 2 && datasize != 4)
        {
            throw new BookException(Msg.TYPE_UNKNOWN);
        }

        idxFile = new File(path + ".idx");
        datFile = new File(path + ".dat");

        if (!idxFile.canRead())
        {
            throw new BookException(Msg.READ_FAIL, new Object[] { idxFile.getAbsolutePath() });
        }

        if (!datFile.canRead())
        {
            throw new BookException(Msg.READ_FAIL, new Object[] { datFile.getAbsolutePath() });
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#activate(org.crosswire.common.activate.Lock)
     */
    public final void activate(Lock lock)
    {
        try
        {
            // Open the files
            idxRaf = new RandomAccessFile(idxFile, "r");
            datRaf = new RandomAccessFile(datFile, "r");
        }
        catch (IOException ex)
        {
            log.error("failed to open files", ex);

            idxRaf = null;
            datRaf = null;
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
            idxRaf.close();
            datRaf.close();
        }
        catch (IOException ex)
        {
            log.error("failed to close files", ex);
        }

        idxRaf = null;
        datRaf = null;

        active = false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.KeyBackend#readIndex()
     */
    public KeyList readIndex()
    {
        checkActive();

        KeyList reply = new DefaultKeyList(config.getDescription());

        int entrysize = OFFSETSIZE + datasize;
        long entries;
        try
        {
            entries = idxRaf.length() / entrysize;
        }
        catch (IOException ex)
        {
            Reporter.informUser(this, ex);
            return reply;
        }

        for (int entry=0; entry<entries; entry++)
        {
            try
            {
                // Read the offset and size for this key from the index
                byte[] buffer = SwordUtil.readRAF(idxRaf, entry*entrysize, entrysize);
                long offset = SwordUtil.decodeLittleEndian32(buffer, 0);
                int size = -1;
                switch (datasize)
                {
                case 2:
                    size = SwordUtil.decodeLittleEndian16(buffer, 4);
                    break;
                case 4:
                    size = SwordUtil.decodeLittleEndian32AsInt(buffer, 4);
                    break;
                default:
                    throw new LogicError();
                }
                
                // Now read the data file for this key using the offset and size
                byte[] data = SwordUtil.readRAF(datRaf, offset, size);

                int keyend = SwordUtil.findByte(data, SEPARATOR);
                if (keyend == -1)
                {
                    DataPolice.report("Failed to find keyname. offset="+offset+" data='"+new String(data)+"'");
                    continue;
                }

                byte[] keydata = new byte[keyend];
                System.arraycopy(data, 0, keydata, 0, keyend);
                
                String keytitle = new String(keydata).trim();
                // for some wierd reason plain text (i.e. SourceType=0) dicts
                // all get \ added to the ends of the index entries.
                if (keytitle.endsWith("\\"))
                {
                    keytitle = keytitle.substring(0, keytitle.length()-1);
                }
                Key key = new IndexKey(keytitle, offset, size, reply);
                
                reply.add(key);
            }
            catch (IOException ex)
            {
                log.error("Ignoring entry", ex);
            }
        }

        return reply;
    }


    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.KeyBackend#getRawText(org.crosswire.jsword.book.Key)
     */
    public byte[] getRawText(Key key) throws BookException
    {
        checkActive();

        if (!(key instanceof IndexKey))
        {
            throw new BookException(Msg.BAD_KEY, new Object[] { ClassUtils.getShortClassName(key.getClass()), key.getName() });
        }

        IndexKey ikey = (IndexKey) key;

        try
        {
            byte[] data = SwordUtil.readRAF(datRaf, ikey.offset, ikey.size);

            int keyend = SwordUtil.findByte(data, SEPARATOR);
            if (keyend == -1)
            {
                throw new BookException(Msg.READ_FAIL);
            }

            int remainder = data.length - (keyend + 1);
            byte[] reply = new byte[remainder];
            System.arraycopy(data, keyend + 1, reply, 0, remainder);

            return reply;
        }
        catch (IOException ex)
        {
            throw new BookException(Msg.READ_FAIL, ex);
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

    /**
     * Are we active
     */
    private boolean active = false;

    /**
     * Used to separate the key name from the key value
     */
    private static final byte SEPARATOR = 10; // ^M=CR=13=0x0d=\r ^J=LF=10=0x0a=\n

    /**
     * How many bytes in the offset pointers in the index
     */
    private static final int OFFSETSIZE = 4;

    /**
     * How many bytes in the size count in the index
     */
    private int datasize = -1;

    /**
     * The data random access file
     */
    private RandomAccessFile datRaf;

    /**
     * The index random access file
     */
    private RandomAccessFile idxRaf;

    /**
     * The data file
     */
    private File datFile;

    /**
     * The index file
     */
    private File idxFile;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(RawLDBackend.class);

    /**
     * The book driver that we are providing data for
     */
    private SwordConfig config;

    /**
     * A Key that knows where the data is in the real file.
     */
    static class IndexKey extends DefaultKey
    {
        /**
         * Setup with the key name and positions of data in the file
         */
        protected IndexKey(String text, long offset, int size, Key parent)
        {
            super(text, parent);

            this.offset = offset;
            this.size = size;
        }

        protected long offset;
        protected int size;
    }
}
