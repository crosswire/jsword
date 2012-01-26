/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.passage;

import org.crosswire.jsword.versification.Versification;

/**
 * A factory to create a Verse from user input.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class VerseFactory {
    /**
     * Prevent a VerseFactory from being created.
     */
    private VerseFactory() {
    }

    /**
     * Construct a Verse from a String - something like "Gen 1:1". in case the
     * user does not want to have their typing 'fixed' by a meddling patronizing
     * computer. The following initial letters can not be matched at all -
     * 'bfquvwx'.
     * 
     * @param original
     *            The text string to be converted
     * @return the Verse representation of the string
     * @exception NoSuchVerseException
     *                If the text can not be understood
     * @deprecated use {@link #fromString(Versification, String)} instead
     */
    @Deprecated
    public static Verse fromString(String original) throws NoSuchVerseException {
        return fromString(null, original);
    }

    public static Verse fromString(Versification v11n, String original) throws NoSuchVerseException {
        if ("".equals(original)) {
            return null;
        }
        String[] parts = AccuracyType.tokenize(original);
        AccuracyType accuracy = AccuracyType.fromText(v11n, original, parts);
        assert accuracy != null;
        return accuracy.createStartVerse(v11n, original, null, parts);
    }

    /**
     * Construct a Verse from a String and a VerseRange. For example given "2:2"
     * and a basis of Gen 1:1 - 12 the result would be Gen 2:2
     * 
     * @param original
     *            The string describing the verse e.g "2:2"
     * @param verseRangeBasis
     *            The basis by which to understand the desc.
     * @return the verse representation of the string
     * @exception NoSuchVerseException
     *                If the reference is illegal
     * @deprecated use {@link #fromString(Versification, String, VerseRange)} instead
     */
    @Deprecated
    public static Verse fromString(String original, VerseRange verseRangeBasis) throws NoSuchVerseException {
        return fromString(null, original, verseRangeBasis);
    }

    public static Verse fromString(Versification v11n, String original, VerseRange verseRangeBasis) throws NoSuchVerseException {
        if ("".equals(original)) {
            return null;
        }
        String[] parts = AccuracyType.tokenize(original);
        AccuracyType accuracy = AccuracyType.fromText(v11n, original, parts, null, verseRangeBasis);
        assert accuracy != null;
        return accuracy.createStartVerse(v11n, original, verseRangeBasis, parts);
    }

}
