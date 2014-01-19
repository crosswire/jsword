package org.crosswire.jsword.passage;

import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
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
        assertEquals("3John.1.1", SimpleOsisParser.parseOsisID(testV11n, "3John.1").getOsisID());
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
        assertEquals("3John.1.2-3John.1.10", SimpleOsisParser.parseOsisRef(testV11n, "3John.2-3John.10").getOsisRef());
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
