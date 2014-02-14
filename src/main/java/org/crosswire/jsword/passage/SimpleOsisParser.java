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
 * Copyright: 2005-2014
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.passage;

import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.Versification;

/**
 * The Osis ID parser simply assumes 2-3 parts divided by '.' and is very strict.
 * Any deviation from the dot-delimited formatted yields nulls.
 * 
 * OSIS Refs should be separated by a '-'.  
 * 
 * The current implementation doesn't support an OSIS ID or OSIS ref with a missing chapter, 
 * as are currently returned by the getOsisRef() calls occasionally.
 * 
 * @author chrisburrell
 */
public final class SimpleOsisParser {
    /**
     * Hiding constructor
     */
    private SimpleOsisParser() {
        //no-op
    }

    /**
     * String OSIS Ref parser, assumes a - separating two osis IDs
     * @param v11n the v11n
     * @param osisRef the ref
     * @return the equivalent verse range
     */
    public static VerseRange parseOsisRef(final Versification v11n, final String osisRef) {
        final String[] osisIDs = StringUtil.splitAll(osisRef, VerseRange.RANGE_OSIS_DELIM);
        // Too many or few parts?
        if (osisIDs.length < 1 || osisIDs.length > 2) {
            return null;
        }

        // Parts must have content.
        if (osisIDs[0].length() == 0 || (osisIDs.length == 2 && osisIDs[1].length() == 0)) {
            return null;
        }

        Verse start = parseOsisID(v11n, osisIDs[0]);
        if (start == null) {
            return null;
        }

        if (osisIDs.length == 1) {
            return new VerseRange(v11n, start);
        }

        Verse end = parseOsisID(v11n, osisIDs[1]);
        if (end == null) {
            return null;
        }

        return new VerseRange(v11n, start, end);
    }

    /**
     * Strict OSIS ID parsers, case-sensitive
     * @param v11n the versification to use when constructing the verse
     * @param osisID the ID we want to parse
     * @return the verse that matches the OSIS ID
     */
    public static Verse parseOsisID(final Versification v11n, final String osisID) {
        if (osisID == null) {
            return null;
        }

        final String[] verseParts = StringUtil.splitAll(osisID, Verse.VERSE_OSIS_DELIM);

        if (verseParts.length != 2 && verseParts.length != 3) {
            return null;
        }

        final BibleBook b = BibleBook.fromExactOSIS(verseParts[0]);
        if (b == null) {
            return null;
        }

// All OSIS ids have chapter 1 for all books that even have a single chapter.
// So 3 John 14 would be 3John.1.14
//        if (b.isShortBook()) {
//            if (verseParts.length != 2) {
//                return null;
//            }
//
//            return new Verse(v11n, b, 1, Integer.parseInt(verseParts[1]));
//        }

        if (verseParts.length != 3) {
            return null;
        }

        return new Verse(v11n, b, Integer.parseInt(verseParts[1]), Integer.parseInt(verseParts[2]));
    }
}
