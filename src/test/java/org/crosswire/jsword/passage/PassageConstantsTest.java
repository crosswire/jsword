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

import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class PassageConstantsTest {

    @Test
    public void testAllowedDelims() {
        // Check that we're not re-using delimiters
        for (int i = 0; i < AccuracyType.VERSE_ALLOWED_DELIMS.length(); i++) {
            Assert.assertEquals(-1, AbstractPassage.REF_ALLOWED_DELIMS.indexOf(AccuracyType.VERSE_ALLOWED_DELIMS.charAt(i)));
            Assert.assertNotEquals(VerseRange.RANGE_OSIS_DELIM, AccuracyType.VERSE_ALLOWED_DELIMS.charAt(i));
        }

        for (int i = 0; i < AbstractPassage.REF_ALLOWED_DELIMS.length(); i++) {
            Assert.assertNotEquals(VerseRange.RANGE_OSIS_DELIM, AbstractPassage.REF_ALLOWED_DELIMS.charAt(i));
        }
    }
}
