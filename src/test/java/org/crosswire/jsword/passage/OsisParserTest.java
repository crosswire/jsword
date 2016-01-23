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
 * © CrossWire Bible Society, 2014 - 2016
 *
 */
package org.crosswire.jsword.passage;

import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;
import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit Test
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Chris Burrell
 */
public class OsisParserTest {
    private Versification testV11n = Versifications.instance().getVersification(Versifications.DEFAULT_V11N);

    private OsisParser osisParser = new OsisParser();

    @Test
    public void testVerseParsing() {
        Assert.assertEquals("Gen.1.1", osisParser.parseOsisID(testV11n, "Gen.1.1").getOsisID());
        Assert.assertEquals("Gen.1.2", osisParser.parseOsisID(testV11n, "Gen.1.2").getOsisID());
        Assert.assertEquals("Gen.2.1", osisParser.parseOsisID(testV11n, "Gen.2.1").getOsisID());
        Assert.assertEquals("Exod.2.1", osisParser.parseOsisID(testV11n, "Exod.2.1").getOsisID());
        Assert.assertEquals("3John.1.1", osisParser.parseOsisID(testV11n, "3John.1.1").getOsisID());
    }

    @Test
    public void testVerseParsingErrors() {
        Assert.assertEquals(null, osisParser.parseOsisID(testV11n, "Gen.1.1.4"));
        Assert.assertEquals(null, osisParser.parseOsisID(testV11n, "Gen."));
        Assert.assertEquals(null, osisParser.parseOsisID(testV11n, "Gen21"));
        Assert.assertEquals(null, osisParser.parseOsisID(testV11n, ""));
        Assert.assertEquals(null, osisParser.parseOsisID(testV11n, null));
    }
    @Test
    public void testVerseRangeParsing() {
        Assert.assertEquals("Gen.1.1-Gen.1.3", osisParser.parseOsisRef(testV11n, "Gen.1.1-Gen.1.3").getOsisRef());
        Assert.assertEquals("Gen.1.2-Gen.1.4", osisParser.parseOsisRef(testV11n, "Gen.1.2-Gen.1.4").getOsisRef());
        Assert.assertEquals("Gen.2.2-Gen.3.4", osisParser.parseOsisRef(testV11n, "Gen.2.2-Gen.3.4").getOsisRef());
        Assert.assertEquals("Exod.2.2-Lev.1.1", osisParser.parseOsisRef(testV11n, "Exod.2.2-Lev.1.1").getOsisRef());
        Assert.assertEquals("3John.1.2-3John.1.10", osisParser.parseOsisRef(testV11n, "3John.1.2-3John.1.10").getOsisRef());
    }

    @Test
    public void testChapterParsing() {
        Assert.assertEquals("Gen.1", osisParser.parseOsisRef(testV11n, "Gen.1").getOsisRef());
        Assert.assertEquals("Mark.10", osisParser.parseOsisRef(testV11n, "Mark.10").getOsisRef());
        Assert.assertEquals("Gen.1-Gen.3", osisParser.parseOsisRef(testV11n, "Gen.1-Gen.3").getOsisRef());
        Assert.assertEquals("Obad", osisParser.parseOsisRef(testV11n, "Obad.1").getOsisRef());
        Assert.assertEquals("Obad", osisParser.parseOsisRef(testV11n, "Obad").getOsisRef());
        Assert.assertEquals("Gen", osisParser.parseOsisRef(testV11n, "Gen").getOsisRef());
    }

    @Test
    public void testMixedLengthParsing() {
        Assert.assertEquals("Gen-Exod.1", osisParser.parseOsisRef(testV11n, "Gen-Exod.1").getOsisRef());
        Assert.assertEquals("Gen.2-Gen.3", osisParser.parseOsisRef(testV11n, "Gen.2.1-Gen.3").getOsisRef());
        Assert.assertEquals("Gen.1-Gen.3", osisParser.parseOsisRef(testV11n, "Gen-Gen.3").getOsisRef());
        Assert.assertEquals("Gen.1-Gen.3.4", osisParser.parseOsisRef(testV11n, "Gen-Gen.3.4").getOsisRef());
        Assert.assertEquals("Gen.3.4-Exod", osisParser.parseOsisRef(testV11n, "Gen.3.4-Exod").getOsisRef());
    }

    @Test
    public void testVerseRangeParsingErrors() {
        Assert.assertEquals(null, osisParser.parseOsisRef(testV11n, "Gen.1.1.4"));
        Assert.assertEquals(null, osisParser.parseOsisRef(testV11n, "Gen."));
        Assert.assertEquals(null, osisParser.parseOsisRef(testV11n, "Gen.1."));
        Assert.assertEquals(null, osisParser.parseOsisRef(testV11n, "Gen21"));
        Assert.assertEquals(null, osisParser.parseOsisRef(testV11n, "Gen.2.1-"));
        Assert.assertEquals(null, osisParser.parseOsisRef(testV11n, "Gen1-Gen.3"));
        Assert.assertEquals(null, osisParser.parseOsisRef(testV11n, "-Gen.3"));
        Assert.assertEquals(null, osisParser.parseOsisRef(testV11n, ""));
        Assert.assertEquals(null, osisParser.parseOsisRef(testV11n, null));
    }

    @Test
    public void testVerseWithPart() {
        Assert.assertEquals("3John.1.14!a", osisParser.parseOsisID(testV11n, "3John.1.14!a").getOsisID());
    }
}
