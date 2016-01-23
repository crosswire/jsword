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
public class GzipTest {

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
        Compressor compressor = new Gzip(kjvGenesis);
        ByteArrayOutputStream bosCompressed = null;
        try {
            bosCompressed = compressor.compress();
        } catch (IOException e) {
            Assert.fail();
            return;
        }
        Compressor uncompressor = new Gzip(new ByteArrayInputStream(bosCompressed.toByteArray()));
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
            Assert.assertTrue("round trip ZIP uncompression", PlatformTestUtils.startsWith(result, "^          \r?\nThe First Book of Moses, called Genesis"));
        } catch (UnsupportedEncodingException e) {
            Assert.fail();
            return;
        }

    }
}
