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
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.common.util;

import junit.framework.TestCase;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class LanguagesTest extends TestCase {
    public LanguagesTest(String s) {
        super(s);
    }

    @Override
    protected void setUp() throws Exception {
    }

    @Override
    protected void tearDown() throws Exception {
    }

    public void testBCP47() {
        // Some day there may be a zzz language code and then this test will need to change.
        assertEquals("Test for unknown code", "zzz", Languages.getName("zzz"));
        // When the code is not valid the language name will the code
        assertEquals("Test for bad code", "zzzz", Languages.getName("zzzz"));
        // ur-Deva is defined so it should not return the code. The actual answer
        // is Locale dependent, so we don't test for an exact value
        assertFalse("Test for known code", "ur-Deva".equals(Languages.getName("ur-Deva")));
    }
    public void testISO639() {
        // Some day there may be a zzz language code and then this test will need to change.
        assertEquals("Test for unknown code", "zzz", Languages.AllLanguages.getName("zzz"));
        // When the code is not valid the language name will the code
        assertEquals("Test for bad code", "zzzz", Languages.AllLanguages.getName("zzzz"));
        // ur-Deva is defined above but not here, so it should return the code.
        assertEquals("Test for known code", "ur-Deva", Languages.AllLanguages.getName("ur-Deva"));
        // en-US is defined above but not here, so it should return the code.
        assertEquals("Test for known code", "en-US", Languages.AllLanguages.getName("en-US"));
    }
    public void testRtoL() {
        // Known means in rtol.txt
        assertTrue("Test for known RtoL script: Arab  # Arabic", Languages.RtoL.isRtoL("Arab", null));
        assertTrue("Test for known RtoL script: Armi  # Imperial Aramaic", Languages.RtoL.isRtoL("Armi", null));
        assertTrue("Test for known RtoL script: Avst  # Avestan", Languages.RtoL.isRtoL("Avst", null));
        assertTrue("Test for known RtoL script: Hebr  # Hebrew", Languages.RtoL.isRtoL("Hebr", null));
        assertTrue("Test for known RtoL script: Hung  # Old Hungarian (Hungarian Runic)", Languages.RtoL.isRtoL("Hung", null));
        assertTrue("Test for known RtoL script: Lydi  # Lydian", Languages.RtoL.isRtoL("Lydi", null));
        assertTrue("Test for known RtoL script: Mand  # Mandaic, Mandaean", Languages.RtoL.isRtoL("Mand", null));
        assertTrue("Test for known RtoL script: Mani  # Manichaean", Languages.RtoL.isRtoL("Mani", null));
        assertTrue("Test for known RtoL script: Merc  # Meroitic Cursive", Languages.RtoL.isRtoL("Merc", null));
        assertTrue("Test for known RtoL script: Mero  # Meroitic Hieroglyphs", Languages.RtoL.isRtoL("Mero", null));
        assertTrue("Test for known RtoL script: Mong  # Mongolian", Languages.RtoL.isRtoL("Mong", null));
        assertTrue("Test for known RtoL script: Mroo  # Mro, Mru", Languages.RtoL.isRtoL("Mroo", null));
        assertTrue("Test for known RtoL script: Narb  # Old North Arabian (Ancient North Arabian)", Languages.RtoL.isRtoL("Narb", null));
        assertTrue("Test for known RtoL script: Nbat  # Nabataean", Languages.RtoL.isRtoL("Nbat", null));
        assertTrue("Test for known RtoL script: Nkoo  # N’Ko", Languages.RtoL.isRtoL("Nkoo", null));
        assertTrue("Test for known RtoL script: Orkh  # Old Turkic, Orkhon Runic", Languages.RtoL.isRtoL("Orkh", null));
        assertTrue("Test for known RtoL script: Palm  # Palmyrene", Languages.RtoL.isRtoL("Palm", null));
        assertTrue("Test for known RtoL script: Phli  # Inscriptional Pahlavi", Languages.RtoL.isRtoL("Phli", null));
        assertTrue("Test for known RtoL script: Phlp  # Psalter Pahlavi", Languages.RtoL.isRtoL("Phlp", null));
        assertTrue("Test for known RtoL script: Phlv  # Book Pahlavi", Languages.RtoL.isRtoL("Phlv", null));
        assertTrue("Test for known RtoL script: Phnx  # Phoenician", Languages.RtoL.isRtoL("Phnx", null));
        assertTrue("Test for known RtoL script: Prti  # Inscriptional Parthian", Languages.RtoL.isRtoL("Prti", null));
        assertTrue("Test for known RtoL script: Samr  # Samaritan", Languages.RtoL.isRtoL("Samr", null));
        assertTrue("Test for known RtoL script: Sarb  # Old South Arabian", Languages.RtoL.isRtoL("Sarb", null));
        assertTrue("Test for known RtoL script: Syrc  # Syriac", Languages.RtoL.isRtoL("Syrc", null));
        assertTrue("Test for known RtoL script: Syre  # Syriac (Estrangelo variant)", Languages.RtoL.isRtoL("Syre", null));
        assertTrue("Test for known RtoL script: Syrj  # Syriac (Western variant)", Languages.RtoL.isRtoL("Syrj", null));
        assertTrue("Test for known RtoL script: Syrn  # Syriac (Eastern variant)", Languages.RtoL.isRtoL("Syrn", null));
        assertTrue("Test for known RtoL script: Tfng  # Tifinagh (Berber)", Languages.RtoL.isRtoL("Tfng", null));
        assertTrue("Test for known RtoL script: Thaa  # Thaana", Languages.RtoL.isRtoL("Thaa", null));

        assertTrue("Test for known RtoL lang: ar    # Arabic", Languages.RtoL.isRtoL(null, "ar"));
        assertTrue("Test for known RtoL lang: fa    # Farsi/Persian", Languages.RtoL.isRtoL(null, "fa"));
        assertTrue("Test for known RtoL lang: he    # Hebrew", Languages.RtoL.isRtoL(null, "he"));
        assertTrue("Test for known RtoL lang: syr   # Syriac", Languages.RtoL.isRtoL(null, "syr"));
        assertTrue("Test for known RtoL lang: ur    # Uighur", Languages.RtoL.isRtoL(null, "ur"));
        assertTrue("Test for known RtoL lang: uig   # Uighur", Languages.RtoL.isRtoL(null, "uig"));
        
        assertFalse("Test for unknown RtoL script value: diddly-squat", Languages.RtoL.isRtoL("diddly squat", null));
        assertFalse("Test for unknown RtoL lang value: diddly-squat", Languages.RtoL.isRtoL(null, "diddly squat"));
        assertFalse("Test for unknown RtoL script and lang value: diddly-squat", Languages.RtoL.isRtoL("diddly squat", "diddly squat"));

        assertTrue("Test for known RtoL script and language", Languages.RtoL.isRtoL("Arab", "fa"));
        assertTrue("Test for known RtoL script and language, but bad combo", Languages.RtoL.isRtoL("Arab", "he"));
        assertTrue("Test for known RtoL script and unknown language", Languages.RtoL.isRtoL("Arab", "zzzzzz"));
        assertFalse("Test for known RtoL unknown script and known language", Languages.RtoL.isRtoL("Xxxx", "fa"));

        assertFalse("Test case insensitivity of RtoL script", Languages.RtoL.isRtoL("arab", null));
        assertFalse("Test case insensitivity of RtoL lang", Languages.RtoL.isRtoL(null, "FA"));
    }
}
