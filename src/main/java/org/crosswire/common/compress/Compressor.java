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
 * © CrossWire Bible Society, 2007 - 2016
 *
 */
package org.crosswire.common.compress;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * A compressor provides the ability to compress and uncompress block text.
 * Implementing classes are expected to provide a way to supply the input.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public interface Compressor {
    /**
     * The size to read/write when unzipping a compressed byte array of unknown
     * size.
     */
    int BUF_SIZE = 2048;

    /**
     * Compresses the input and provides the result.
     * 
     * @return the compressed result
     * @throws IOException if an exception is encountered
     */
    ByteArrayOutputStream compress() throws IOException;

    /**
     * Uncompresses the input and provides the result.
     * 
     * @return the uncompressed result
     * @throws IOException if an exception is encountered
     */
    ByteArrayOutputStream uncompress() throws IOException;

    /**
     * Uncompresses the input and provides the result.
     * 
     * @param expectedLength
     *            the size of the result buffer
     * @return the uncompressed result
     * @throws IOException if an exception is encountered
     */
    ByteArrayOutputStream uncompress(int expectedLength) throws IOException;
}
