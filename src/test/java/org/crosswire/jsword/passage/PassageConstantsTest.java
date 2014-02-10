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
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.passage;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class PassageConstantsTest {

    @Test
    public void testAllowedDelims() {
        // Check that we're not re-using delimiters
        for (int i = 0; i < AccuracyType.VERSE_ALLOWED_DELIMS.length(); i++) {
            assertEquals(-1, AbstractPassage.REF_ALLOWED_DELIMS.indexOf(AccuracyType.VERSE_ALLOWED_DELIMS.charAt(i)));
            assertEquals(-1, VerseRange.RANGE_ALLOWED_DELIMS.indexOf(AccuracyType.VERSE_ALLOWED_DELIMS.charAt(i)));
        }

        for (int i = 0; i < AbstractPassage.REF_ALLOWED_DELIMS.length(); i++) {
            assertEquals(-1, VerseRange.RANGE_ALLOWED_DELIMS.indexOf(AbstractPassage.REF_ALLOWED_DELIMS.charAt(i)));
        }
    }
}
