/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.jsword.book.sword;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import org.crosswire.common.util.NetUtil;
import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Various utilities used by different Sword classes.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public final class SwordUtil {
    /**
     * Prevent instantiation
     */
    private SwordUtil() {
    }

    /**
     * Read a RandomAccessFile
     * 
     * @param raf
     *            The file to read
     * @param offset
     *            The start of the record to read
     * @param theSize
     *            The number of bytes to read
     * @return the read data
     * @throws IOException
     *             on error
     */
    protected static byte[] readRAF(RandomAccessFile raf, long offset, int theSize) throws IOException {
        raf.seek(offset);
        return readNextRAF(raf, theSize);
    }

    /**
     * Read a RandomAccessFile from the current location in the file.
     * 
     * @param raf
     *            The file to read
     * @param theSize
     *            The number of bytes to read
     * @return the read data
     * @throws IOException
     *             on error
     */
    protected static byte[] readNextRAF(RandomAccessFile raf, int theSize) throws IOException {
        long offset = raf.getFilePointer();
        int size = theSize;
        long rafSize = raf.length();

        // It is common to have an entry that points to nothing.
        // That is the equivalent of an empty string.
        if (size == 0) {
            return new byte[0];
        }

        if (size < 0) {
            log.error("Nothing to read at offset = {} returning empty because negative size={}", Long.toString(offset), Integer.toString(size));
            return new byte[0];
        }

        if (offset >= rafSize) {
            log.error("Attempt to read beyond end. offset={} size={} but raf.length={}", Long.toString(offset), Integer.toString(size), Long.toString(rafSize));
            return new byte[0];
        }

        if (offset + size > raf.length()) {
            log.error("Need to reduce size to avoid EOFException. offset={} size={} but raf.length={}", Long.toString(offset), Integer.toString(size), Long.toString(rafSize));
            size = (int) (raf.length() - offset);
        }

        byte[] read = new byte[size];
        raf.readFully(read);

        return read;
    }

    /**
     * Writes "data" to a RandomAccessFile at the "offset" position
     * 
     * @param raf
     *            RandomAccessFile
     * @param offset
     *            offset to write at
     * @param data
     *            data to write
     * @throws IOException
     *             on error
     */
    protected static void writeRAF(RandomAccessFile raf, long offset, byte[] data) throws IOException {
        raf.seek(offset);
        writeNextRAF(raf, data);
    }

    protected static void writeNextRAF(RandomAccessFile raf, byte[] data) throws IOException {
        if (data == null) {
            return;
        }
        raf.write(data);
    }

    /**
     * Read a RandomAccessFile until a particular byte is seen
     * 
     * @param raf
     *            The file to read
     * @param offset
     *            The start of the record to read
     * @param stopByte
     *            The point at which to stop reading
     * @return the read data
     * @throws IOException
     *             on error
     */
    protected static byte[] readUntilRAF(RandomAccessFile raf, int offset, byte stopByte) throws IOException {
        raf.seek(offset);
        return readUntilRAF(raf, stopByte);
    }

    /**
     * Read a RandomAccessFile until a particular byte is seen
     * 
     * @param raf
     *            The file to read
     * @param stopByte
     *            The point at which to stop reading
     * @return the read data
     * @throws IOException
     *             on error
     */
    protected static byte[] readUntilRAF(RandomAccessFile raf, byte stopByte) throws IOException {
        // The strategy used here is to read the file twice.
        // Once to determine how much to read and then getting the actual data.
        // It may be more efficient to incrementally build up a byte buffer.
        // Note: that growing a static array by 1 byte at a time is O(n**2)
        // This is negligible when the n is small, but prohibitive otherwise.
        long offset = raf.getFilePointer();
        int size = 0;

        int nextByte = -1;
        do {
            nextByte = raf.read();

            size++;
        } while (nextByte != -1 && nextByte != stopByte);

        // Note: we allow for nextByte == -1 to be included in size
        // so that readRAF will report EOF errors
        return readRAF(raf, offset, size);
    }

    /**
     * Decode little endian data from a byte array. This assumes that the high
     * order bit is not set as this is used solely for an offset in a file in
     * bytes. For a practical limit, 2**31 is way bigger than any document that
     * we can have.
     * 
     * @param data
     *            the byte[] from which to read 4 bytes
     * @param offset
     *            the offset into the array
     * @return The decoded data
     */
    public static int decodeLittleEndian32(byte[] data, int offset) {
        // Convert from a byte to an int, but prevent sign extension.
        // So -16 becomes 240
        int byte1 = data[0 + offset] & 0xFF;
        int byte2 = (data[1 + offset] & 0xFF) << 8;
        int byte3 = (data[2 + offset] & 0xFF) << 16;
        int byte4 = (data[3 + offset] & 0xFF) << 24;

        return byte4 | byte3 | byte2 | byte1;
    }

    /**
     * Encode little endian data from a byte array. This assumes that the number
     * fits in a Java integer. That is, the range of an unsigned C integer is
     * greater than a signed Java integer. For a practical limit, 2**31 is way
     * bigger than any document that we can have. If this ever doesn't work, use
     * a long for the number.
     * 
     * @param val
     *            the number to encode into little endian
     * @param data
     *            the byte[] from which to write 4 bytes
     * @param offset
     *            the offset into the array
     */
    protected static void encodeLittleEndian32(int val, byte[] data, int offset) {
        data[0 + offset] = (byte) (val & 0xFF);
        data[1 + offset] = (byte) ((val >> 8) & 0xFF);
        data[2 + offset] = (byte) ((val >> 16) & 0xFF);
        data[3 + offset] = (byte) ((val >> 24) & 0xFF);
    }

    /**
     * Decode little endian data from a byte array
     * 
     * @param data
     *            the byte[] from which to read 2 bytes
     * @param offset
     *            the offset into the array
     * @return The decoded data
     */
    protected static int decodeLittleEndian16(byte[] data, int offset) {
        // Convert from a byte to an int, but prevent sign extension.
        // So -16 becomes 240
        int byte1 = data[0 + offset] & 0xFF;
        int byte2 = (data[1 + offset] & 0xFF) << 8;

        return byte2 | byte1;
    }

    /**
     * Encode a 16-bit little endian from an integer. It is assumed that the
     * integer's lower 16 bits are the only that are set.
     * 
     * @param data
     *            the byte[] from which to write 2 bytes
     * @param offset
     *            the offset into the array
     */
    protected static void encodeLittleEndian16(int val, byte[] data, int offset) {
        data[0 + offset] = (byte) (val & 0xFF);
        data[1 + offset] = (byte) ((val >> 8) & 0xFF);
    }

    /**
     * Find a byte of data in an array
     * 
     * @param data
     *            The array to search
     * @param sought
     *            The data to search for
     * @return The index of the found position or -1 if not found
     */
    protected static int findByte(byte[] data, byte sought) {
        return findByte(data, 0, sought);
    }

    /**
     * Find a byte of data in an array
     * 
     * @param data
     *            The array to search
     * @param offset
     *            The position in the array to begin looking
     * @param sought
     *            The data to search for
     * @return The index of the found position or -1 if not found
     */
    protected static int findByte(byte[] data, int offset, byte sought) {
        for (int i = offset; i < data.length; i++) {
            if (data[i] == sought) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Transform a byte array into a string given the encoding. If the encoding
     * is bad then it just does it as a string.
     * Note: this may modify data. Don't use it to examine data.
     * 
     * @param key the key
     * @param data
     *            The byte array to be converted
     * @param charset
     *            The encoding of the byte array
     * @return a string that is UTF-8 internally
     */
    public static String decode(String key, byte[] data, String charset) {
        return decode(key, data, 0, data.length, charset);
    }

    /**
     * Transform a portion of a byte array into a string given the encoding. If
     * the encoding is bad then it just does it as a string.
     * Note: this may modify data. Don't use it to examine data.
     * 
     * @param key the key
     * @param data
     *            The byte array to be converted
     * @param length
     *            The number of bytes to use.
     * @param charset
     *            The encoding of the byte array
     * @return a string that is UTF-8 internally
     */
    public static String decode(String key, byte[] data, int length, String charset) {
        return decode(key, data, 0, length, charset);
    }

    /**
     * Transform a portion of a byte array starting at an offset into a string
     * given the encoding. If the encoding is bad then it just does it as a
     * string. Note: this may modify data. Don't use it to examine data.
     * 
     * @param key the key
     * @param data
     *            The byte array to be converted
     * @param offset
     *            The starting position in the byte array
     * @param length
     *            The number of bytes to use.
     * @param charset
     *            The encoding of the byte array
     * @return a string that is UTF-8 internally
     */
    public static String decode(String key, byte[] data, int offset, int length, String charset) {
         if ("WINDOWS-1252".equals(charset)) {
            clean1252(key, data, offset, length);
         }
        String txt = "";
        try {
            if (offset + length <= data.length) {
                txt = new String(data, offset, length, charset);
            }
        } catch (UnsupportedEncodingException ex) {
            // It is impossible! In case, use system default...
            log.error("{}: Encoding {} not supported.", key, charset, ex);
            txt = new String(data, offset, length);
        }

        return txt;
    }

    /**
     * Remove rogue characters in the source. These are characters that are not
     * valid in cp1252 aka WINDOWS-1252 and in UTF-8 or are non-printing control
     * characters in the range of 0-32.
     */
    private static void clean1252(String key, byte[] data, int offset, int length) {
        int end = offset + length;
        // make sure it doesn't go off the end
        if (end > data.length) {
            end = data.length;
        }
        for (int i = offset; i < end; i++) {
            // between 0-32 only allow whitespace: \t, \n, \r, ' '
            // characters 0x81, 0x8D, 0x8F, 0x90 and 0x9D are undefined in
            // cp1252
            int c = data[i] & 0xFF;
            if ((c >= 0x00 && c < 0x20 && c != 0x09 && c != 0x0A && c != 0x0D) || (c == 0x81 || c == 0x8D || c == 0x8F || c == 0x90 || c == 0x9D)) {
                data[i] = 0x20;
                log.error("{} has bad character 0x{} at position {} in input.", key, Integer.toString(c, 16), Integer.toString(i));
            }
        }
    }

    /**
     * Returns where the book should be located
     * @param bookMetaData meta information about the book
     * @return the URI locating the resource
     * @throws BookException thrown if an issue is encountered, e.g. missing data files.
     */
    public static URI getExpandedDataPath(BookMetaData bookMetaData) throws BookException {
        URI loc = NetUtil.lengthenURI(bookMetaData.getLibrary(), bookMetaData.getProperty(SwordBookMetaData.KEY_DATA_PATH));

        if (loc == null) {
            // FIXME(DMS): missing parameter
            throw new BookException(JSOtherMsg.lookupText("Missing data files for old and new testaments in {0}."));
        }

        return loc;
    }

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(SwordUtil.class);

}
