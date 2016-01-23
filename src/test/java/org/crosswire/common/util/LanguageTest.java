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
public class LanguageTest {
    @Test
    public void testCanonical() {
        Language lang = new Language("en");
        Assert.assertEquals("Test for LL.getCode", "en", lang.getCode());
        Assert.assertEquals("Test for LL.getScript", null, lang.getScript());
        Assert.assertEquals("Test for LL.getCountry", null, lang.getCountry());
        lang = new Language("en-Latn");
        Assert.assertEquals("Test for LL-SSSS.getCode", "en", lang.getCode());
        Assert.assertEquals("Test for LL-SSSS.getScript", "Latn", lang.getScript());
        Assert.assertEquals("Test for LL-SSSS.getCountry", null, lang.getCountry());
        lang = new Language("en-US");
        Assert.assertEquals("Test for LL-CC.getCode", "en", lang.getCode());
        Assert.assertEquals("Test for LL-CC.getScript", null, lang.getScript());
        Assert.assertEquals("Test for LL-CC.getCountry", "US", lang.getCountry());
        lang = new Language("en-Latn-US");
        Assert.assertEquals("Test for LL-SSSS-CC.getCode", "en", lang.getCode());
        Assert.assertEquals("Test for LL-SSSS-CC.getScript", "Latn", lang.getScript());
        Assert.assertEquals("Test for LL-SSSS-CC.getCountry", "US", lang.getCountry());
        lang = new Language("en-US-Latn");
        Assert.assertEquals("Test for LL-CC-SSSS.getCode", "en", lang.getCode());
        Assert.assertEquals("Test for LL-CC-SSSS.getScript", null, lang.getScript());
        Assert.assertEquals("Test for LL-CC-SSSS.getCountry", "US", lang.getCountry());
    }

    @Test
    public void testNonCanonical() {
        Language lang = new Language("EN");
        Assert.assertEquals("Test for LL.getCode", "en", lang.getCode());
        Assert.assertEquals("Test for LL.getScript", null, lang.getScript());
        Assert.assertEquals("Test for LL.getCountry", null, lang.getCountry());
        lang = new Language("EN-latn");
        Assert.assertEquals("Test for LL-SSSS.getCode", "en", lang.getCode());
        Assert.assertEquals("Test for LL-SSSS.getScript", "Latn", lang.getScript());
        Assert.assertEquals("Test for LL-SSSS.getCountry", null, lang.getCountry());
        lang = new Language("EN-us");
        Assert.assertEquals("Test for LL-CC.getCode", "en", lang.getCode());
        Assert.assertEquals("Test for LL-CC.getScript", null, lang.getScript());
        Assert.assertEquals("Test for LL-CC.getCountry", "US", lang.getCountry());
        lang = new Language("EN-latn-us");
        Assert.assertEquals("Test for LL-SSSS-CC.getCode", "en", lang.getCode());
        Assert.assertEquals("Test for LL-SSSS-CC.getScript", "Latn", lang.getScript());
        Assert.assertEquals("Test for LL-SSSS-CC.getCountry", "US", lang.getCountry());
        lang = new Language("EN-us-latn");
        Assert.assertEquals("Test for LL-CC-SSSS.getCode", "en", lang.getCode());
        Assert.assertEquals("Test for LL-CC-SSSS.getScript", null, lang.getScript());
        Assert.assertEquals("Test for LL-CC-SSSS.getCountry", "US", lang.getCountry());
    }

    @Test
    public void testMixed() {
        Language lang = new Language("En");
        Assert.assertEquals("Test for LL.getCode", "en", lang.getCode());
        Assert.assertEquals("Test for LL.getScript", null, lang.getScript());
        Assert.assertEquals("Test for LL.getCountry", null, lang.getCountry());
        Assert.assertEquals("Test for LL.getGiven", "En", lang.getGivenSpecification());
        Assert.assertEquals("Test for LL.getFound", "en", lang.getFoundSpecification());
        Assert.assertTrue("Test for LL.isValid", lang.isValidLanguage());
        lang = new Language("En-lATn");
        Assert.assertEquals("Test for LL-SSSS.getCode", "en", lang.getCode());
        Assert.assertEquals("Test for LL-SSSS.getScript", "Latn", lang.getScript());
        Assert.assertEquals("Test for LL-SSSS.getCountry", null, lang.getCountry());
        Assert.assertEquals("Test for LL-SSSS.getGiven", "En-lATn", lang.getGivenSpecification());
        Assert.assertEquals("Test for LL-SSSS.getFound", "en", lang.getFoundSpecification());
        Assert.assertTrue("Test for LL-SSSS-CC.isValid", lang.isValidLanguage());
        lang = new Language("En-Us");
        Assert.assertEquals("Test for LL-CC.getCode", "en", lang.getCode());
        Assert.assertEquals("Test for LL-CC.getScript", null, lang.getScript());
        Assert.assertEquals("Test for LL-CC.getCountry", "US", lang.getCountry());
        Assert.assertEquals("Test for LL-CC.getGiven", "En-Us", lang.getGivenSpecification());
        Assert.assertEquals("Test for LL-CC.getFound", "en-US", lang.getFoundSpecification());
        Assert.assertTrue("Test for LL-CC.isValid", lang.isValidLanguage());
        lang = new Language("En-lATn-Us");
        Assert.assertEquals("Test for LL-SSSS-CC.getCode", "en", lang.getCode());
        Assert.assertEquals("Test for LL-SSSS-CC.getScript", "Latn", lang.getScript());
        Assert.assertEquals("Test for LL-SSSS-CC.getCountry", "US", lang.getCountry());
        Assert.assertEquals("Test for LL-SSSS-CC.getGiven", "En-lATn-Us", lang.getGivenSpecification());
        Assert.assertEquals("Test for LL-SSSS=CC.getFound", "en-US", lang.getFoundSpecification());
        Assert.assertTrue("Test for LL-SSSS-CC.isValid", lang.isValidLanguage());
        lang = new Language("En-Us-lATn");
        Assert.assertEquals("Test for LL-CC-SSSS.getCode", "en", lang.getCode());
        Assert.assertEquals("Test for LL-CC-SSSS.getScript", null, lang.getScript());
        Assert.assertEquals("Test for LL-CC-SSSS.getCountry", "US", lang.getCountry());
        Assert.assertEquals("Test for LL-CC-SSSS.getGiven", "En-Us-lATn", lang.getGivenSpecification());
        Assert.assertEquals("Test for LL-CC-SSSS.getFound", "en-US", lang.getFoundSpecification());
        Assert.assertFalse("Test for LL-CC-SSSS.isValid", lang.isValidLanguage());
    }
}
