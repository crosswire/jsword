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
public final class OsisParser {
    /**
     * String OSIS Ref parser, assumes a - separating two osis IDs
     * @param v11n the v11n
     * @param osisRef the ref
     * @return the equivalent verse range
     */
    public VerseRange parseOsisRef(final Versification v11n, final String osisRef) {
        final String[] osisIDs = StringUtil.splitAll(osisRef, VerseRange.RANGE_OSIS_DELIM);
        // Too many or few parts?
        if (osisIDs.length < 1 || osisIDs.length > 2) {
            return null;
        }

        // Parts must have content.
        if (osisIDs[0].length() == 0 || (osisIDs.length == 2 && osisIDs[1].length() == 0)) {
            return null;
        }
        
        // manipulate to 2 OSIS IDs
        String osisID1 = osisIDs[0];
        String osisID2;
        if (osisIDs.length==1) {
            osisID2 = osisID1;
        } else {
            osisID2 = osisIDs[1];
        }

        // ensure no empty parts in osis id1 and not too many parts
        String[] osisID1Parts = StringUtil.splitAll(osisID1, Verse.VERSE_OSIS_DELIM);
        if (isAnEmptyPart(osisID1Parts) || osisID1Parts.length>3) {
            return null;
        }
        
        // manipulate first osis id to 3 parts, padding with first chapter or verse
        if (osisID1Parts.length<3) {
            osisID1 += replicate(".1", 3-osisPartCount(osisID1));
            osisID1Parts = StringUtil.splitAll(osisID1, Verse.VERSE_OSIS_DELIM);
        }
        // now we have a full 3 part start verse in osis id1
        
        // Now let us manufacture a 3 part end verse

        // First check no empty parts were passed in for osis id 2
        String[] osisID2Parts = StringUtil.splitAll(osisID2, Verse.VERSE_OSIS_DELIM);
        if (isAnEmptyPart(osisID2Parts)) {
            return null;
        }

        // Add end chapter/verse if missing
        int osisID2PartCount = osisID2Parts.length;
        if (osisID2PartCount<3) {
            // need to calculate chapter and verse for osis id 2

            // there will always be a book
            String bookName = osisID2Parts[0];
            final BibleBook osisID2Book = BibleBook.fromExactOSIS(bookName);

            // can asssume last chapter if unspecified because this is the trailing osis Id
            int osisID2Chapter;
            if (osisID2PartCount==1) {
                osisID2Chapter = v11n.getLastChapter(osisID2Book);
                osisID2 += "."+osisID2Chapter;
            } else {
                osisID2Chapter = Integer.parseInt(osisID2Parts[1]);
            }

            // can asssume last verse if unspecified because this is the trailing osis Id
            int osisID2Verse;
            if (osisID2PartCount<3) {
                osisID2Verse = v11n.getLastVerse(osisID2Book, osisID2Chapter);
                osisID2 += "."+osisID2Verse;
            }
        
            // now there are more parts in osis id 2 
            osisID2Parts = StringUtil.splitAll(osisID2, Verse.VERSE_OSIS_DELIM);
        }
        
        // Specific 3-part verse(s) only beyond this point
        
        Verse start = parseOsisID(v11n, osisID1Parts);
        if (start == null) {
            return null;
        }

        Verse end = parseOsisID(v11n, osisID2Parts);
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
    public Verse parseOsisID(final Versification v11n, final String osisID) {
        if (osisID == null) {
            return null;
        }

        final String[] osisIDParts = StringUtil.splitAll(osisID, Verse.VERSE_OSIS_DELIM);

        if (osisIDParts.length != 3 || isAnEmptyPart(osisIDParts)) {
            return null;
        }
        
        return parseOsisID(v11n, osisIDParts);
    }

    private Verse parseOsisID(final Versification v11n, final String[] osisIDParts) {
        
        final BibleBook b = BibleBook.fromExactOSIS(osisIDParts[0]);
        if (b == null) {
            return null;
        }

        // Allow a Verse to have a sub identifier on the last part.
        // We should use it, but throwing it away for now.
        String[] endParts = StringUtil.splitAll(osisIDParts[2], Verse.VERSE_OSIS_SUB_PREFIX);
        String subIdentifier = null;
        if (endParts.length == 2 && endParts[1].length() > 0) {
            subIdentifier = endParts[1];
        }
        return new Verse(v11n, b, Integer.parseInt(osisIDParts[1]), Integer.parseInt(endParts[0]), subIdentifier);
    }

    /** 
     * Check no part of the Osis ref is empty
     */
    private static boolean isAnEmptyPart(String[] parts) {
        for (String part : parts) {
            if (part.length()==0) {
                return true;
            }
        }
        return false;
    }
    
    private static int osisPartCount(String osisRef) {
        return StringUtil.splitAll(osisRef, Verse.VERSE_OSIS_DELIM).length;
    }
    
    private static String replicate(String text, int times) {
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<times; i++) {
            builder.append(text);
        }
        return builder.toString();
    }

}
