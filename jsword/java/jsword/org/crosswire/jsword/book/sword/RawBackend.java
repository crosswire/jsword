package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.crosswire.common.activate.Activator;
import org.crosswire.common.activate.Lock;
import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyList;
import org.crosswire.jsword.passage.PassageUtil;
import org.crosswire.jsword.passage.Verse;

/**
 * Both Books and Commentaries seem to use the same format so this class
 * abstracts out the similarities.
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
public class RawBackend implements Backend
{
    /**
     * Simple ctor
     */
    public RawBackend(String path) throws BookException
    {
        idxFile[SwordConstants.TESTAMENT_OLD] = new File(path + File.separator + "ot.vss");
        txtFile[SwordConstants.TESTAMENT_OLD] = new File(path + File.separator + "ot");

        idxFile[SwordConstants.TESTAMENT_NEW] = new File(path + File.separator + "nt.vss");
        txtFile[SwordConstants.TESTAMENT_NEW] = new File(path + File.separator + "nt");

        // It is an error to be neither OT nor NT
        if (!txtFile[SwordConstants.TESTAMENT_OLD].canRead() && !txtFile[SwordConstants.TESTAMENT_NEW].canRead())
        {
            throw new BookException(Msg.MISSING_FILE, new Object[] { path });
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#activate(org.crosswire.common.activate.Lock)
     */
    public final void activate(Lock lock)
    {
        try
        {
            idxRaf[SwordConstants.TESTAMENT_OLD] = new RandomAccessFile(idxFile[SwordConstants.TESTAMENT_OLD], "r");
            txtRaf[SwordConstants.TESTAMENT_OLD] = new RandomAccessFile(txtFile[SwordConstants.TESTAMENT_OLD], "r");
        }
        catch (FileNotFoundException ex)
        {
            // Ignore this might be NT only
        }

        try
        {
            idxRaf[SwordConstants.TESTAMENT_NEW] = new RandomAccessFile(idxFile[SwordConstants.TESTAMENT_NEW], "r");
            txtRaf[SwordConstants.TESTAMENT_NEW] = new RandomAccessFile(txtFile[SwordConstants.TESTAMENT_NEW], "r");
        }
        catch (FileNotFoundException ex)
        {
            // Ignore this might be OT only
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
            idxRaf[SwordConstants.TESTAMENT_OLD].close();
            txtRaf[SwordConstants.TESTAMENT_OLD].close();

            idxRaf[SwordConstants.TESTAMENT_NEW].close();
            txtRaf[SwordConstants.TESTAMENT_NEW].close();
        }
        catch (IOException ex)
        {
            log.error("failed to close files", ex);
        }

        idxRaf[SwordConstants.TESTAMENT_OLD] = null;
        txtRaf[SwordConstants.TESTAMENT_OLD] = null;

        idxRaf[SwordConstants.TESTAMENT_NEW] = null;
        txtRaf[SwordConstants.TESTAMENT_NEW] = null;

        active = false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.Backend#getRawText(org.crosswire.jsword.passage.Verse)
     */
    public byte[] getRawText(Key key) throws BookException
    {
        checkActive();

        Verse verse = PassageUtil.getVerse(key);

        try
        {
            int testament = SwordConstants.getTestament(verse);
            long index = SwordConstants.getIndex(verse);

            // If this is a single testament Bible, return nothing.
            if (idxRaf[testament] == null)
            {
                return new byte[0];
            }

            // Read the next ENTRY_SIZE byes.
            byte[] read = SwordUtil.readRAF(idxRaf[testament], index * ENTRY_SIZE, ENTRY_SIZE);
            if (read == null || read.length == 0)
            {
                return new byte[0];
            }

            // The data is little endian - extract the start and size
            long start = SwordUtil.decodeLittleEndian32(read, 0);
            int size = SwordUtil.decodeLittleEndian16(read, 4);

            // Read from the data file.
            // I wonder if it would be safe to do a readLine() from here.
            // Probably be safer not to risk it since we know how long it is.
            return SwordUtil.readRAF(txtRaf[testament], start, size);
        }
        catch (IOException ex)
        {
            throw new BookException(Msg.READ_FAIL, ex, new Object[] { verse.getName() });
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.Backend#readIndex()
     */
    public KeyList readIndex()
    {
        // PENDING(joe): refactor to get rid of this
        return null;
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
     * The log stream
     */
    private static final Logger log = Logger.getLogger(RawBackend.class);

    /**
     * The array of index files
     */
    private RandomAccessFile[] idxRaf = new RandomAccessFile[3];

    /**
     * The array of data files
     */
    private RandomAccessFile[] txtRaf = new RandomAccessFile[3];

    /**
     * The array of index random access files
     */
    private File[] idxFile = new File[3];

    /**
     * The array of data random access files
     */
    private File[] txtFile = new File[3];

    /**
     * How many bytes in an index?
     */
    private static final int ENTRY_SIZE = 6;
}
