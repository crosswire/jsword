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
 * Copyright: 2014
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.passage;

import static org.junit.Assert.assertEquals;

import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;
import org.junit.Test;

/**
 * JUnit Test
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author chrisburrell
 */
public class SimpleOsisIDParserTest {
    private Versification testV11n = Versifications.instance().getVersification(Versifications.DEFAULT_V11N); 
                
    @Test
    public void testVerseParsing() {
        assertEquals("Gen.1.1", SimpleOsisParser.parseOsisID(testV11n, "Gen.1.1").getOsisID());
        assertEquals("Gen.1.2", SimpleOsisParser.parseOsisID(testV11n, "Gen.1.2").getOsisID());
        assertEquals("Gen.2.1", SimpleOsisParser.parseOsisID(testV11n, "Gen.2.1").getOsisID());
        assertEquals("Exod.2.1", SimpleOsisParser.parseOsisID(testV11n, "Exod.2.1").getOsisID());
        assertEquals("3John.1.1", SimpleOsisParser.parseOsisID(testV11n, "3John.1.1").getOsisID());
    }

    @Test
    public void testVerseParsingErrors() {
        assertEquals(null, SimpleOsisParser.parseOsisID(testV11n, "Gen.1.1.4"));
        assertEquals(null, SimpleOsisParser.parseOsisID(testV11n, "Gen."));
        assertEquals(null, SimpleOsisParser.parseOsisID(testV11n, "Gen21"));
        assertEquals(null, SimpleOsisParser.parseOsisID(testV11n, ""));
        assertEquals(null, SimpleOsisParser.parseOsisID(testV11n, null));
    }
    @Test
    public void testVerseRangeParsing() {
        assertEquals("Gen.1.1-Gen.1.3", SimpleOsisParser.parseOsisRef(testV11n, "Gen.1.1-Gen.1.3").getOsisRef());
        assertEquals("Gen.1.2-Gen.1.4", SimpleOsisParser.parseOsisRef(testV11n, "Gen.1.2-Gen.1.4").getOsisRef());
        assertEquals("Gen.2.2-Gen.3.4", SimpleOsisParser.parseOsisRef(testV11n, "Gen.2.2-Gen.3.4").getOsisRef());
        assertEquals("Exod.2.2-Lev.1.1", SimpleOsisParser.parseOsisRef(testV11n, "Exod.2.2-Lev.1.1").getOsisRef());
        assertEquals("3John.1.2-3John.1.10", SimpleOsisParser.parseOsisRef(testV11n, "3John.1.2-3John.1.10").getOsisRef());
    }

    @Test
    public void testVerseRangeParsingErrors() {
        assertEquals(null, SimpleOsisParser.parseOsisRef(testV11n, "Gen.1.1.4"));
        assertEquals(null, SimpleOsisParser.parseOsisRef(testV11n, "Gen."));
        assertEquals(null, SimpleOsisParser.parseOsisRef(testV11n, "Gen21"));
        assertEquals(null, SimpleOsisParser.parseOsisRef(testV11n, "Gen.2.1-"));
        assertEquals(null, SimpleOsisParser.parseOsisRef(testV11n, "Gen.2.1-Gen.3"));
        assertEquals(null, SimpleOsisParser.parseOsisRef(testV11n, "Gen1-Gen.3"));
        assertEquals(null, SimpleOsisParser.parseOsisRef(testV11n, "Gen.1-Gen.3"));
        assertEquals(null, SimpleOsisParser.parseOsisRef(testV11n, "Gen-Gen.3"));
        assertEquals(null, SimpleOsisParser.parseOsisRef(testV11n, "-Gen.3"));
        assertEquals(null, SimpleOsisParser.parseOsisRef(testV11n, ""));
        assertEquals(null, SimpleOsisParser.parseOsisRef(testV11n, null));
    }
}
