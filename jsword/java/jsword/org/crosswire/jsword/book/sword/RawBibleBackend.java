
package org.crosswire.jsword.book.sword;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;

import org.crosswire.common.util.NetUtil;
import org.crosswire.jsword.book.BookException;

/**
 * An implementation of a Sword Bible backend for raw bible data.
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
 * @see docs.Licence
 * @author Mark Goodwin [mark at thorubio dot org]
 * @author The Sword project (don't know who - no credits in original files (canon.h))
 * @version $Id:$
 */
public class RawBibleBackend implements SwordBibleBackend
{
    /**
     * Constructs a RawBibleBackend from a SwordConfig.
     */
    public RawBibleBackend(URL swordBase, SwordConfig config) throws BookException
    {
        this.swordBase = swordBase;
        try
        {
            URL url = NetUtil.lengthenURL(swordBase, config.getDataPath());
            if (!url.getProtocol().equals("file"))
                throw new BookException("sword_file_only", new Object[] { url.getProtocol()});

            String path = url.getFile();

            try
            {
                idx_raf[SwordConstants.TESTAMENT_OLD] = new RandomAccessFile(path + "/ot.vss", "r");
                txt_raf[SwordConstants.TESTAMENT_OLD] = new RandomAccessFile(path + "/ot", "r");
            }
            catch (FileNotFoundException ex)
            {
            }

            try
            {
                idx_raf[SwordConstants.TESTAMENT_NEW] = new RandomAccessFile(path + "/nt.vss", "r");
                txt_raf[SwordConstants.TESTAMENT_NEW] = new RandomAccessFile(path + "/nt", "r");
            }
            catch (FileNotFoundException ex)
            {
            }

            if (txt_raf[SwordConstants.TESTAMENT_OLD] == null && txt_raf[SwordConstants.TESTAMENT_NEW] == null)
                throw new BookException("sword_missing_file", new Object[] { url.getFile() });
        }
        catch (MalformedURLException mue)
        {
            throw new BookException("sword_init", mue);
        }
    }

    /**
     * @see org.crosswire.jsword.book.sword.SwordBibleBackend#getText(int, int, int, int)
     */
    public String getText(int testament, int book, int chapter, int verse) throws IOException
    {
        long start;
        int size;

        // work out the offset
        int bookOffset = SwordConstants.bks[testament][book];
        long chapOffset = SwordConstants.cps[testament][bookOffset + chapter];

        long offset = 6 * (chapOffset + verse);

        // Read the next 6 byes.
        idx_raf[testament].seek(offset);
        byte[] read = new byte[6];
        idx_raf[testament].readFully(read);

        // Un-2s-complement them
        int[] temp = new int[6];
        for (int i = 0; i < temp.length; i++)
        {
            temp[i] = read[i] >= 0 ? read[i] : 256 + read[i];
        }

        // The data is little endian - extract the start and size
        start = (temp[3] << 24) | (temp[2] << 16) | (temp[1] << 8) | temp[0];
        size = (temp[5] << 8) | temp[4];

        // Read from the data file.
        // I wonder if it would be safe to do a readLine() from here.
        // Probably be safer not to risk it since we know how long it is.
        byte[] buffer = new byte[size];
        txt_raf[testament].seek(start);
        txt_raf[testament].read(buffer);

        // We should probably think about encodings here?
        return new String(buffer);
    }

    private SwordConfig config;

    /** The array of index files */
    private RandomAccessFile[] idx_raf = new RandomAccessFile[3];

    /** The array of data files */
    private RandomAccessFile[] txt_raf = new RandomAccessFile[3];

    private URL swordBase;
}
