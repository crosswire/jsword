package org.crosswire.jsword.passage;

import junit.framework.TestCase;

public class AccuracyTypeTest extends TestCase {

    public AccuracyTypeTest(String s) {
        super(s);
    }

    public void testFromText_onePartInvalidBook() {
        try {
            AccuracyType.fromText("10", new String[] { "10"}, null, null);
        } catch (NoSuchVerseException nsve) {
            // expected
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            fail("ArrayIndexOutOfBoundsException caught, expecting NoSuchVerseException");
        }

    }

    public void testFromText_TooManyParts() {
        boolean caught = false;
        try {
            AccuracyType.fromText("1:2:3:4", new String[] { "1", "2", "3", "4"}, null, null);
        } catch (NoSuchVerseException nsve) {
            // I18N(DMS)
            NoSuchVerseException correctException = new NoSuchVerseException(UserMsg.gettext("Too many parts to the Verse. (Parts are separated by any of {0})", 
                "1:2:3:4, 1, 2, 3, 4"));
            assertEquals("Unexpected exception message", correctException.getMessage(), nsve.getMessage());
            caught = true;
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            fail("ArrayIndexOutOfBoundsException caught, expecting NoSuchVerseException");
        }

        if (!caught) {
            fail("Expected fromText to throw an exception when passed too many parts");
        }
    }

    public void testFromText_ThreePartsInvalidBook() {
        boolean caught = false;
        try {
            AccuracyType.fromText("-1:2:3", new String[] { "-1", "2", "3"}, null, null);
        } catch (NoSuchVerseException nsve) {
            // I18N(DMS)
            NoSuchVerseException correctException = new NoSuchVerseException(UserMsg.gettext("Too many parts to the Verse. (Parts are separated by any of {0})", 
                    "-1:2:3, -1, 2, 3"));
            assertEquals("Unexpected exception message", correctException.getMessage(), nsve.getMessage());
            caught = true;
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            fail("ArrayIndexOutOfBoundsException caught, expecting NoSuchVerseException");
        }

        if (!caught) {
            fail("Expected fromText to throw an exception when passed three parts with an invalid book");
        }
    }

}
