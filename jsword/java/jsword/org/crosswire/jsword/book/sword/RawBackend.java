package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

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
        try
        {
            idx_raf[SwordConstants.TESTAMENT_OLD] = new RandomAccessFile(path + File.separator + "ot.vss", "r");
            txt_raf[SwordConstants.TESTAMENT_OLD] = new RandomAccessFile(path + File.separator + "ot", "r");
        }
        catch (FileNotFoundException ex)
        {
            // Ignore this might be NT only
        }

        try
        {
            idx_raf[SwordConstants.TESTAMENT_NEW] = new RandomAccessFile(path + File.separator + "nt.vss", "r");
            txt_raf[SwordConstants.TESTAMENT_NEW] = new RandomAccessFile(path + File.separator + "nt", "r");
        }
        catch (FileNotFoundException ex)
        {
            // Ignore this might be OT only
        }

        // It is an error to be neither OT nor NT
        if (txt_raf[SwordConstants.TESTAMENT_OLD] == null && txt_raf[SwordConstants.TESTAMENT_NEW] == null)
        {
            throw new BookException(Msg.MISSING_FILE, new Object[] { path });
        }

        // The original had a dtor that did the equiv of .close()ing the above
        // I'm not sure that there is a delete type ability in Book.java and
        // the finalizer for RandomAccessFile will do it anyway so for the
        // moment I'm going to ignore this.

        // The original also stored the path, but I don't think it ever used it

        // The original also kept an instance count, which went unused (and I
        // noticed in a few other places so it is either c&p or a pattern?
        // Either way the assumption that there is only one of a static is not
        // safe in many java environments (servlets, ejbs at least) so I've
        // deleted it
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.Backend#getRawText(org.crosswire.jsword.passage.Verse)
     */
    public byte[] getRawText(Key key) throws BookException
    {
        Verse verse = PassageUtil.getVerse(key);

        try
        {
            int testament = SwordConstants.getTestament(verse);
            long index = SwordConstants.getIndex(verse);

            // If this is a single testament Bible, return nothing.
            if (idx_raf[testament] == null)
            {
                return new byte[0];
            }

            // Read the next ENTRY_SIZE byes.
            byte[] read = SwordUtil.readRAF(idx_raf[testament], index * ENTRY_SIZE, ENTRY_SIZE);
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
            return SwordUtil.readRAF(txt_raf[testament], start, size);
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
        // TODO: refactor to get rid of this
        return null;
    }

    /**
     * The array of index files
     */
    private RandomAccessFile[] idx_raf = new RandomAccessFile[3];

    /**
     * The array of data files
     */
    private RandomAccessFile[] txt_raf = new RandomAccessFile[3];

    /**
     * How many bytes in an index?
     */
    private static final int ENTRY_SIZE = 6;
}
