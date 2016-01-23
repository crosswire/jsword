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
 * © CrossWire Bible Society, 2008 - 2016
 *
 */
package org.crosswire.jsword.bridge;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.versification.BookName;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test of functionality for use with DWR. This test assumes, at a minimum, that
 * KJV, Strong's Greek and Hebrew Dictionaries, Robinson's morphological codes,
 * ... are installed and that the KJV is indexed.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
// TODO(DMS): make this test use mocks or setup its own environment
public class DwrBridgeMissingAssetsTest {
    private DwrBridge dwrBridge = new DwrBridge();

    @Before
    public void setUp() {
        BookName.setFullBookName(true);
    }

    @Test
    public void testGetBooks() {
        String[][] bibles = dwrBridge.getInstalledBooks("Category=Biblical Texts");
        Assert.assertTrue(bibles.length > 1);

        String[][] dicts = dwrBridge.getInstalledBooks("Category=Lexicons / Dictionaries");
        Assert.assertTrue(dicts.length > 1);
    }

    @Test
    public void testGetOsisString() {
        try {
            String verse = dwrBridge.getOSISString("KJV", "Gen 1:1", 0, 100);
            Assert.assertEquals(
                    "<div><title type=\"x-gen\">Genesis 1:1</title><verse osisID=\"Gen.1.1\"><w lemma=\"strong:H07225\">In the beginning</w> <w lemma=\"strong:H0430\">God</w> <w lemma=\"strong:H0853 strong:H01254\" morph=\"strongMorph:TH8804\">created</w> <w lemma=\"strong:H08064\">the heaven</w> <w lemma=\"strong:H0853\">and</w> <w lemma=\"strong:H0776\">the earth</w>.</verse></div>",
                    verse);
            String hdef = dwrBridge.getOSISString("StrongsHebrew", "H07225", 0, 100);
            Assert.assertEquals(
                    "<div><title type=\"x-gen\">07225</title>7225  re'shiyth  ray-sheeth'\r<lb></lb>\r<lb></lb> from the same as 7218; the first, in place, time, order or\r<lb></lb> rank (specifically, a firstfruit):--beginning, chief(-est),\r<lb></lb> first(-fruits, part, time), principal thing.\r<lb></lb> see HEBREW for 07218</div>",
                    hdef);
        } catch (BookException e) {
            Assert.fail(e.getDetailedMessage());
        } catch (NoSuchKeyException e) {
            Assert.fail();
        }
    }

    @Test
    public void testIndexed() {
        Assert.assertTrue(dwrBridge.isIndexed("KJV"));
        Assert.assertFalse(dwrBridge.isIndexed("not a bible"));
    }

    @Test
    public void testSearch() {
        try {
            String result = dwrBridge.search("KJV", "aaron AND moses AND egypt");
            Assert.assertEquals("Exo 5:4, 6:13, 26-27, 7:19, 8:5, 16, 12:1, 16:6, 32:1, Num 14:2, 26:59, 33:1, Jos 24:5, 1Sa 12:6, 8, Mic 6:4, Act 7:40", result);
        } catch (BookException e) {
            Assert.fail();
        }
    }

    @Test
    public void testMatch() {
        String[] result = dwrBridge.match("StrongsGreek", "0001", 10);
        Assert.assertTrue(result.length == 10);
    }
}
