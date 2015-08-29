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
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005 - 2014
 *     The copyright to this program is held by its authors.
 *
 */
package org.crosswire.common.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
 */
public class LanguageTest {
    @Test
    public void testCanonical() {
        Language lang = new Language("en");
        assertEquals("Test for LL.getCode", "en", lang.getCode());
        assertEquals("Test for LL.getScript", null, lang.getScript());
        assertEquals("Test for LL.getCountry", null, lang.getCountry());
        lang = new Language("en-Latn");
        assertEquals("Test for LL-SSSS.getCode", "en", lang.getCode());
        assertEquals("Test for LL-SSSS.getScript", "Latn", lang.getScript());
        assertEquals("Test for LL-SSSS.getCountry", null, lang.getCountry());
        lang = new Language("en-US");
        assertEquals("Test for LL-CC.getCode", "en", lang.getCode());
        assertEquals("Test for LL-CC.getScript", null, lang.getScript());
        assertEquals("Test for LL-CC.getCountry", "US", lang.getCountry());
        lang = new Language("en-Latn-US");
        assertEquals("Test for LL-SSSS-CC.getCode", "en", lang.getCode());
        assertEquals("Test for LL-SSSS-CC.getScript", "Latn", lang.getScript());
        assertEquals("Test for LL-SSSS-CC.getCountry", "US", lang.getCountry());
        lang = new Language("en-US-Latn");
        assertEquals("Test for LL-CC-SSSS.getCode", "en", lang.getCode());
        assertEquals("Test for LL-CC-SSSS.getScript", null, lang.getScript());
        assertEquals("Test for LL-CC-SSSS.getCountry", "US", lang.getCountry());
    }

    @Test
    public void testNonCanonical() {
        Language lang = new Language("EN");
        assertEquals("Test for LL.getCode", "en", lang.getCode());
        assertEquals("Test for LL.getScript", null, lang.getScript());
        assertEquals("Test for LL.getCountry", null, lang.getCountry());
        lang = new Language("EN-latn");
        assertEquals("Test for LL-SSSS.getCode", "en", lang.getCode());
        assertEquals("Test for LL-SSSS.getScript", "Latn", lang.getScript());
        assertEquals("Test for LL-SSSS.getCountry", null, lang.getCountry());
        lang = new Language("EN-us");
        assertEquals("Test for LL-CC.getCode", "en", lang.getCode());
        assertEquals("Test for LL-CC.getScript", null, lang.getScript());
        assertEquals("Test for LL-CC.getCountry", "US", lang.getCountry());
        lang = new Language("EN-latn-us");
        assertEquals("Test for LL-SSSS-CC.getCode", "en", lang.getCode());
        assertEquals("Test for LL-SSSS-CC.getScript", "Latn", lang.getScript());
        assertEquals("Test for LL-SSSS-CC.getCountry", "US", lang.getCountry());
        lang = new Language("EN-us-latn");
        assertEquals("Test for LL-CC-SSSS.getCode", "en", lang.getCode());
        assertEquals("Test for LL-CC-SSSS.getScript", null, lang.getScript());
        assertEquals("Test for LL-CC-SSSS.getCountry", "US", lang.getCountry());
    }

    @Test
    public void testMixed() {
        Language lang = new Language("En");
        assertEquals("Test for LL.getCode", "en", lang.getCode());
        assertEquals("Test for LL.getScript", null, lang.getScript());
        assertEquals("Test for LL.getCountry", null, lang.getCountry());
        assertEquals("Test for LL.getGiven", "En", lang.getGivenSpecification());
        assertEquals("Test for LL.getFound", "en", lang.getFoundSpecification());
        assertTrue("Test for LL.isValid", lang.isValidLanguage());
        lang = new Language("En-lATn");
        assertEquals("Test for LL-SSSS.getCode", "en", lang.getCode());
        assertEquals("Test for LL-SSSS.getScript", "Latn", lang.getScript());
        assertEquals("Test for LL-SSSS.getCountry", null, lang.getCountry());
        assertEquals("Test for LL-SSSS.getGiven", "En-lATn", lang.getGivenSpecification());
        assertEquals("Test for LL-SSSS.getFound", "en", lang.getFoundSpecification());
        assertTrue("Test for LL-SSSS-CC.isValid", lang.isValidLanguage());
        lang = new Language("En-Us");
        assertEquals("Test for LL-CC.getCode", "en", lang.getCode());
        assertEquals("Test for LL-CC.getScript", null, lang.getScript());
        assertEquals("Test for LL-CC.getCountry", "US", lang.getCountry());
        assertEquals("Test for LL-CC.getGiven", "En-Us", lang.getGivenSpecification());
        assertEquals("Test for LL-CC.getFound", "en-US", lang.getFoundSpecification());
        assertTrue("Test for LL-CC.isValid", lang.isValidLanguage());
        lang = new Language("En-lATn-Us");
        assertEquals("Test for LL-SSSS-CC.getCode", "en", lang.getCode());
        assertEquals("Test for LL-SSSS-CC.getScript", "Latn", lang.getScript());
        assertEquals("Test for LL-SSSS-CC.getCountry", "US", lang.getCountry());
        assertEquals("Test for LL-SSSS-CC.getGiven", "En-lATn-Us", lang.getGivenSpecification());
        assertEquals("Test for LL-SSSS=CC.getFound", "en-US", lang.getFoundSpecification());
        assertTrue("Test for LL-SSSS-CC.isValid", lang.isValidLanguage());
        lang = new Language("En-Us-lATn");
        assertEquals("Test for LL-CC-SSSS.getCode", "en", lang.getCode());
        assertEquals("Test for LL-CC-SSSS.getScript", null, lang.getScript());
        assertEquals("Test for LL-CC-SSSS.getCountry", "US", lang.getCountry());
        assertEquals("Test for LL-CC-SSSS.getGiven", "En-Us-lATn", lang.getGivenSpecification());
        assertEquals("Test for LL-CC-SSSS.getFound", "en-US", lang.getFoundSpecification());
        assertFalse("Test for LL-CC-SSSS.isValid", lang.isValidLanguage());
    }
}
