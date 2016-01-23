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
package org.crosswire.jsword.passage;

import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit Test
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class AccuracyTypeTest {

    private Versification rs;

    @Before
    public void setUp() throws Exception {
        // AV11N(DMS): Update test to test all V11Ns
        rs = Versifications.instance().getVersification("KJV");
    }

    @Test
    public void testFromTextOnePartInvalidBook() {
        try {
            AccuracyType.fromText(rs, "10", new String[] { "10"}, null, null);
        } catch (NoSuchVerseException expected) {
            // This is allowed
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            Assert.fail("ArrayIndexOutOfBoundsException caught, expecting NoSuchVerseException");
        }

    }

    @Test
    public void testFromTextTooManyParts() {
        boolean caught = false;
        try {
            AccuracyType.fromText(rs, "1:2:3:4", new String[] { "1", "2", "3", "4"}, null, null);
        } catch (NoSuchVerseException nsve) {
            // TRANSLATOR: The user specified a verse with too many separators. {0} is a placeholder for the allowable separators.
            NoSuchVerseException correctException = new NoSuchVerseException(JSMsg.gettext("Too many parts to the Verse. (Parts are separated by any of {0})",
                "1:2:3:4, 1, 2, 3, 4"));
            Assert.assertEquals("Unexpected exception message", correctException.getMessage(), nsve.getMessage());
            caught = true;
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            Assert.fail("ArrayIndexOutOfBoundsException caught, expecting NoSuchVerseException");
        }

        if (!caught) {
            Assert.fail("Expected fromText to throw an exception when passed too many parts");
        }
    }

    @Test
    public void testFromTextThreePartsInvalidBook() {
        boolean caught = false;
        try {
            AccuracyType.fromText(rs, "-1:2:3", new String[] { "-1", "2", "3"}, null, null);
        } catch (NoSuchVerseException nsve) {
            // TRANSLATOR: The user specified a verse with too many separators. {0} is a placeholder for the allowable separators.
            NoSuchVerseException correctException = new NoSuchVerseException(JSMsg.gettext("Too many parts to the Verse. (Parts are separated by any of {0})",
                    "-1:2:3, -1, 2, 3"));
            Assert.assertEquals("Unexpected exception message", correctException.getMessage(), nsve.getMessage());
            caught = true;
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            Assert.fail("ArrayIndexOutOfBoundsException caught, expecting NoSuchVerseException");
        }

        if (!caught) {
            Assert.fail("Expected fromText to throw an exception when passed three parts with an invalid book");
        }
    }

}
