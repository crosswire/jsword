package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;

import org.crosswire.common.util.NetUtil;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.BibleInfo;
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class RawBackend
{

    /**
     * @param backend
     * @param config
     * @throws BookException
     */
    public void init(SwordConfig config) throws BookException
    {
        URL swordBase = SwordBookDriver.dir;
        
        try
        {
            URL url = NetUtil.lengthenURL(swordBase, config.getDataPath());
            if (!url.getProtocol().equals("file"))
                throw new BookException("sword_file_only", new Object[] { url.getProtocol()});
        
            String path = url.getFile();
        
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
                throw new BookException("sword_missing_file", new Object[] { url.getFile() });
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
        catch (MalformedURLException ex)
        {
            throw new BookException("sword_init", ex);
        }
    }

    /**
     * @param backend
     * @param v
     * @return String
     * @throws IOException
     */
    public byte[] getRawText(Verse v) throws BookException
    {
        try
        {
            int ord = v.getOrdinal();
            int book = v.getBook();
            int chapter = v.getChapter();
            int verse = v.getVerse();
            int testament;
            
            if (ord >= SwordConstants.ORDINAL_MAT11)
            {
                // This is an NT verse
                testament = SwordConstants.TESTAMENT_NEW;
                book = book - BibleInfo.Names.Malachi;
            }
            else
            {
                // This is an OT verse
                testament = SwordConstants.TESTAMENT_OLD;
            };
            
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
            
            return buffer;
        }
        catch (IOException ex)
        {
            throw new BookException("sword_readfail", ex);
        }
    }

    /** The array of index files */
    private RandomAccessFile[] idx_raf = new RandomAccessFile[3];

    /** The array of data files */
    private RandomAccessFile[] txt_raf = new RandomAccessFile[3];
}
