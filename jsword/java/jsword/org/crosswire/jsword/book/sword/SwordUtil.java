
package org.crosswire.jsword.book.sword;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.BookException;

/**
 * Various utilities used by different Sword classes.
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
public class SwordUtil
{
    /**
     * Read a RandomAccessFile
     * @param raf The file to read
     * @param index The record to read
     * @param size The number of bytes to read
     * @return the read data
     */
    protected static byte[] readRAF(RandomAccessFile raf, long index, int size) throws IOException
    {
        raf.seek(index);
        byte[] read = new byte[size];
        raf.readFully(read);
        
        return read;
    }

    /**
     * Decode little endian data from a byte array
     * @param data the byte[] from which to read 4 bytes
     * @param offset the offset into the array
     * @return long
     */
    protected static long decodeLittleEndian32(byte[] data, int offset)
    {
        long byte1 = SwordUtil.un2complement(data[0+offset]);
        long byte2 = SwordUtil.un2complement(data[1+offset]) << 8;
        long byte3 = SwordUtil.un2complement(data[2+offset]) << 16;
        long byte4 = SwordUtil.un2complement(data[3+offset]) << 24;
    
        return byte4 | byte3 | byte2 | byte1;
    }

    /**
     * Decode little endian data from a byte array
     * @param data the byte[] from which to read 4 bytes
     * @param offset the offset into the array
     * @return long
     */
    protected static int decodeLittleEndian32AsInt(byte[] data, int offset)
    {
        long result = decodeLittleEndian32(data, offset);

        if (result > Integer.MAX_VALUE)
        {
            log.warn("loss of precision converting to integer from "+result+" to "+((int) result));
        }

        return (int) result;
    }

    /**
     * Decode little endian data from a byte array
     * @param data the byte[] from which to read 4 bytes
     * @param offset the offset into the array
     * @return long
     */
    protected static int decodeLittleEndian16(byte[] data, int offset)
    {        
        int byte1 = SwordUtil.un2complement(data[0+offset]);
        int byte2 = SwordUtil.un2complement(data[1+offset]) << 8;

        return byte2 | byte1;
    }

    /**
     * Un 2-s complement a byte
     */
    protected static int un2complement(byte data)
    {
        return data >= 0 ? data : 256 + data;
    }

    /**
     * Find a byte of data in an array
     * @param data The array to search
     * @param sought The data to search for
     * @return The index of the found position or -1 if not found
     * @throws BookException
     */
    protected static int findByte(byte[] data, byte sought)
    {
        for (int i=0; i<data.length; i++)
        {
            if (data[i] == sought)
            {
                return i;
            }
        }

        return -1;
    }

    /**
     * Uncompress a block of GZIP compressed data
     * @param compressed
     * @param endsize
     * @return byte[]
     */
    public static byte[] uncompress(byte[] compressed, int endsize) throws IOException, DataFormatException, BookException
    {
        // Create the decompressor and give it the data to compress
        Inflater decompressor = new Inflater();
        decompressor.setInput(compressed);
    
        // Decompress the data
        byte[] uncompressed = new byte[endsize];
        int realendsize = decompressor.inflate(uncompressed);
        
        if (!decompressor.finished() || realendsize != endsize)
        {
            throw new BookException(Msg.GZIP_FORMAT, new Object[] { "wrong uncompressed size", });
        }
    
        return uncompressed;
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(SwordUtil.class);
}
