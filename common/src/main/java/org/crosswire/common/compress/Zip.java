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
 * Copyright: 2007
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */

package org.crosswire.common.compress;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * A class can be Activatable if it needs a significant amount of memory on an
 * irregular basis, and so would benefit from being told when to wake-up and
 * when to conserver memory.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class Zip extends AbstractCompressor
{
    /**
     * Create a Zip that is capable of transforming the input.
     * 
     * @param input to compress or uncompress.
     */
    public Zip(byte[] input)
    {
        super(input);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.compress.Compressor#compress()
     */
    public byte[] compress() throws IOException
    {
        ByteArrayInputStream bis = new ByteArrayInputStream(input);
        BufferedInputStream in = new BufferedInputStream(bis);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DeflaterOutputStream out = new DeflaterOutputStream(bos, new Deflater(), ZBUF_SIZE);
        byte[] buf = new byte[ZBUF_SIZE];

        for (int count = in.read(buf); count != -1; count = in.read(buf))
        {
            out.write(buf, 0, count);
        }
        in.close();
        out.flush();
        out.close();
        return bos.toByteArray();
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.compress.Compressor#uncompress()
     */
    public byte[] uncompress() throws IOException
    {
        return uncompress(ZBUF_SIZE);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.compress.Compressor#uncompress(int)
     */
    public byte[] uncompress(int expectedLength) throws IOException
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BufferedOutputStream out = new BufferedOutputStream(bos, expectedLength);
        ByteArrayInputStream bis = new ByteArrayInputStream(input);
        InflaterInputStream in = new InflaterInputStream(bis, new Inflater(), expectedLength);
        byte[] buf = new byte[expectedLength];

        for (int count = in.read(buf); count != -1; count = in.read(buf))
        {
            out.write(buf, 0, count);
        }
        in.close();
        out.flush();
        out.close();
        return bos.toByteArray();
    }

    /**
     * The size to read/write when unzipping a compressed byte array of unknown size.
     */
    private static final int ZBUF_SIZE = 2048;
}
