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
package org.crosswire.common.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
 */
public class LanguagesTest {
    @Test
    public void testBCP47() {
        // Some day there may be a zzz language code and then this test will need to change.
        Assert.assertEquals("Test for unknown code", "zzz", Languages.getName("zzz"));
        // When the code is not valid the language name will the code
        Assert.assertEquals("Test for bad code", "zzzz", Languages.getName("zzzz"));
        // ur-Deva is defined so it should not return the code. The actual answer
        // is Locale dependent, so we don't test for an exact value
        Assert.assertFalse("Test for known code", "ur-Deva".equals(Languages.getName("ur-Deva")));
    }

    @Test
    public void testISO639() {
        // Some day there may be a zzz language code and then this test will need to change.
        Assert.assertEquals("Test for unknown code", "zzz", Languages.AllLanguages.getName("zzz"));
        // When the code is not valid the language name will the code
        Assert.assertEquals("Test for bad code", "zzzz", Languages.AllLanguages.getName("zzzz"));
        // ur-Deva is defined above but not here, so it should return the code.
        Assert.assertEquals("Test for known code", "ur-Deva", Languages.AllLanguages.getName("ur-Deva"));
        // en-US is defined above but not here, so it should return the code.
        Assert.assertEquals("Test for known code", "en-US", Languages.AllLanguages.getName("en-US"));
    }

    @Test
    public void testRtoL() {
        // Known means in rtol.txt
        Assert.assertTrue("Test for known RtoL script: Arab  # Arabic", Languages.RtoL.isRtoL("Arab", null));
        Assert.assertTrue("Test for known RtoL script: Armi  # Imperial Aramaic", Languages.RtoL.isRtoL("Armi", null));
        Assert.assertTrue("Test for known RtoL script: Avst  # Avestan", Languages.RtoL.isRtoL("Avst", null));
        Assert.assertTrue("Test for known RtoL script: Hebr  # Hebrew", Languages.RtoL.isRtoL("Hebr", null));
        Assert.assertTrue("Test for known RtoL script: Hung  # Old Hungarian (Hungarian Runic)", Languages.RtoL.isRtoL("Hung", null));
        Assert.assertTrue("Test for known RtoL script: Lydi  # Lydian", Languages.RtoL.isRtoL("Lydi", null));
        Assert.assertTrue("Test for known RtoL script: Mand  # Mandaic, Mandaean", Languages.RtoL.isRtoL("Mand", null));
        Assert.assertTrue("Test for known RtoL script: Mani  # Manichaean", Languages.RtoL.isRtoL("Mani", null));
        Assert.assertTrue("Test for known RtoL script: Merc  # Meroitic Cursive", Languages.RtoL.isRtoL("Merc", null));
        Assert.assertTrue("Test for known RtoL script: Mero  # Meroitic Hieroglyphs", Languages.RtoL.isRtoL("Mero", null));
        Assert.assertTrue("Test for known RtoL script: Mong  # Mongolian", Languages.RtoL.isRtoL("Mong", null));
        Assert.assertTrue("Test for known RtoL script: Mroo  # Mro, Mru", Languages.RtoL.isRtoL("Mroo", null));
        Assert.assertTrue("Test for known RtoL script: Narb  # Old North Arabian (Ancient North Arabian)", Languages.RtoL.isRtoL("Narb", null));
        Assert.assertTrue("Test for known RtoL script: Nbat  # Nabataean", Languages.RtoL.isRtoL("Nbat", null));
        Assert.assertTrue("Test for known RtoL script: Nkoo  # N’Ko", Languages.RtoL.isRtoL("Nkoo", null));
        Assert.assertTrue("Test for known RtoL script: Orkh  # Old Turkic, Orkhon Runic", Languages.RtoL.isRtoL("Orkh", null));
        Assert.assertTrue("Test for known RtoL script: Palm  # Palmyrene", Languages.RtoL.isRtoL("Palm", null));
        Assert.assertTrue("Test for known RtoL script: Phli  # Inscriptional Pahlavi", Languages.RtoL.isRtoL("Phli", null));
        Assert.assertTrue("Test for known RtoL script: Phlp  # Psalter Pahlavi", Languages.RtoL.isRtoL("Phlp", null));
        Assert.assertTrue("Test for known RtoL script: Phlv  # Book Pahlavi", Languages.RtoL.isRtoL("Phlv", null));
        Assert.assertTrue("Test for known RtoL script: Phnx  # Phoenician", Languages.RtoL.isRtoL("Phnx", null));
        Assert.assertTrue("Test for known RtoL script: Prti  # Inscriptional Parthian", Languages.RtoL.isRtoL("Prti", null));
        Assert.assertTrue("Test for known RtoL script: Samr  # Samaritan", Languages.RtoL.isRtoL("Samr", null));
        Assert.assertTrue("Test for known RtoL script: Sarb  # Old South Arabian", Languages.RtoL.isRtoL("Sarb", null));
        Assert.assertTrue("Test for known RtoL script: Syrc  # Syriac", Languages.RtoL.isRtoL("Syrc", null));
        Assert.assertTrue("Test for known RtoL script: Syre  # Syriac (Estrangelo variant)", Languages.RtoL.isRtoL("Syre", null));
        Assert.assertTrue("Test for known RtoL script: Syrj  # Syriac (Western variant)", Languages.RtoL.isRtoL("Syrj", null));
        Assert.assertTrue("Test for known RtoL script: Syrn  # Syriac (Eastern variant)", Languages.RtoL.isRtoL("Syrn", null));
        Assert.assertTrue("Test for known RtoL script: Tfng  # Tifinagh (Berber)", Languages.RtoL.isRtoL("Tfng", null));
        Assert.assertTrue("Test for known RtoL script: Thaa  # Thaana", Languages.RtoL.isRtoL("Thaa", null));

        Assert.assertTrue("Test for known RtoL lang: ar    # Arabic", Languages.RtoL.isRtoL(null, "ar"));
        Assert.assertTrue("Test for known RtoL lang: fa    # Farsi/Persian", Languages.RtoL.isRtoL(null, "fa"));
        Assert.assertTrue("Test for known RtoL lang: he    # Hebrew", Languages.RtoL.isRtoL(null, "he"));
        Assert.assertTrue("Test for known RtoL lang: syr   # Syriac", Languages.RtoL.isRtoL(null, "syr"));
        Assert.assertTrue("Test for known RtoL lang: ur    # Uighur", Languages.RtoL.isRtoL(null, "ur"));
        Assert.assertTrue("Test for known RtoL lang: uig   # Uighur", Languages.RtoL.isRtoL(null, "uig"));

        Assert.assertFalse("Test for unknown RtoL script value: diddly-squat", Languages.RtoL.isRtoL("diddly squat", null));
        Assert.assertFalse("Test for unknown RtoL lang value: diddly-squat", Languages.RtoL.isRtoL(null, "diddly squat"));
        Assert.assertFalse("Test for unknown RtoL script and lang value: diddly-squat", Languages.RtoL.isRtoL("diddly squat", "diddly squat"));

        Assert.assertTrue("Test for known RtoL script and language", Languages.RtoL.isRtoL("Arab", "fa"));
        Assert.assertTrue("Test for known RtoL script and language, but bad combo", Languages.RtoL.isRtoL("Arab", "he"));
        Assert.assertTrue("Test for known RtoL script and unknown language", Languages.RtoL.isRtoL("Arab", "zzzzzz"));
        Assert.assertFalse("Test for known RtoL unknown script and known language", Languages.RtoL.isRtoL("Xxxx", "fa"));

        Assert.assertFalse("Test case insensitivity of RtoL script", Languages.RtoL.isRtoL("arab", null));
        Assert.assertFalse("Test case insensitivity of RtoL lang", Languages.RtoL.isRtoL(null, "FA"));
    }
}
