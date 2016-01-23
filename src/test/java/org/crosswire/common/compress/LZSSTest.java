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
package org.crosswire.common.compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.MissingResourceException;

import org.crosswire.common.util.PlatformTestUtils;
import org.crosswire.common.util.ResourceUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class LZSSTest {
    public static final int RING_SIZE = 4096;
    public static final int RING_WRAP = RING_SIZE - 1;
    public static final int THRESHOLD = 3;


    @Test
    public void testCore() {
        // Test the idiom s = (s + 1) & RING_WRAP
        for (int i = -1; i < RING_SIZE - 1; i++) {
            Assert.assertEquals(i + 1, (i + 1) & RING_WRAP);
        }
        for (int i = -1; i < RING_SIZE - 1; i++) {
            Assert.assertEquals(i + 1, (i + RING_SIZE + 1) & RING_WRAP);
        }

        // Test the counting of the flag set
        byte mask = 1;
        for (int i = 7; i > 0; i--) {
            mask <<= 1;
            Assert.assertTrue(mask != 0);
        }
        mask <<= 1;
        Assert.assertEquals(0, mask);

        // Test the storing of a position and length
        // Note: pos can be in the range of 0-4095 and len in the range of 3 -
        // 18
        int pos = 0x0FFF;
        int len = 0x000F + THRESHOLD;
        byte lowPart = (byte) pos;
        byte highPart = (byte) (((pos >> 4) & 0xF0) | (len - THRESHOLD));
        Assert.assertEquals((byte) 0x0FF, lowPart);
        Assert.assertEquals((byte) 0x0FF, highPart);

        // Test the extraction of a position and length
        // that it undoes the store operation
        Assert.assertEquals(pos, (lowPart & 0xFF) | ((highPart & 0xF0) << 4));
        Assert.assertEquals(len, (short) ((highPart & 0x0F) + THRESHOLD));
    }

    @Test
    public void testCompression() {
        InputStream kjvGenesis = null;
        try {
            kjvGenesis = ResourceUtil.getResourceAsStream("kjv_genesis.txt");
        } catch (MissingResourceException e) {
            Assert.fail();
        } catch (IOException e) {
            Assert.fail();
        }
        // new ByteArrayInputStream("ATATAAAFFFF".getBytes()));
        Compressor compressor = new LZSS(kjvGenesis);
        ByteArrayOutputStream bosCompressed = null;
        try {
            bosCompressed = compressor.compress();
        } catch (IOException e) {
            Assert.fail();
            return;
        }
        Compressor uncompressor = new LZSS(new ByteArrayInputStream(bosCompressed.toByteArray()));
        ByteArrayOutputStream bosUncompressed = null;
        try {
            bosUncompressed = uncompressor.uncompress();
        } catch (IOException e) {
            Assert.fail();
            return;
        }
        String result;
        try {
            byte[] back = bosUncompressed.toByteArray();
            result = new String(back, "UTF-8");
            Assert.assertTrue("round trip LZSS uncompression", PlatformTestUtils.startsWith(result, "^          \r?\nThe First Book of Moses, called Genesis"));
        } catch (UnsupportedEncodingException e) {
            Assert.fail();
            return;
        }

    }
}
