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
package org.crosswire.jsword.book;

import java.util.Locale;

/**
 * Types of Sentence Case.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public enum CaseType {
    LOWER  {
        @Override
        public String setCase(String word) {
            return word.toLowerCase(Locale.getDefault());
        }
    },

    SENTENCE {
        @Override
        public String setCase(String word) {
            int index = word.indexOf('-');
            if (index == -1) {
                return toSentenceCase(word);
            }

            // So there is a "-", however first some exceptions
            if ("maher-shalal-hash-baz".equalsIgnoreCase(word)) {
                return "Maher-Shalal-Hash-Baz";
            }

            if ("no-one".equalsIgnoreCase(word)) {
                return "No-one";
            }

            if (word.substring(0, 4).equalsIgnoreCase("god-")) {
                return toSentenceCase(word);
            }

            // So cut by the -
            return toSentenceCase(word.substring(0, index)) + "-" + toSentenceCase(word.substring(index + 1));
        }
    },

    UPPER {
        @Override
        public String setCase(String word) {
            return word.toUpperCase(Locale.getDefault());
        }
    };

    public abstract String setCase(String word);

    /**
     * Change to sentence case - that is first character in caps, the rest in lower.
     * 
     * @param word
     *            The word to be manipulated
     * @return The altered word
     */
    public static String toSentenceCase(String word) {
        assert word != null;

        if (word.length() == 0) {
            return "";
        }

        return Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase(Locale.getDefault());
    }

    /**
     * What case is the specified word?. A blank word is LOWER, a word with a
     * single upper case letter is SENTENCE and not UPPER - Simply because this
     * is more likely, however TO BE SURE I WOULD NEED TO THE CONTEXT. I could
     * not tell otherwise.
     * <p>
     * The issue here is that getCase("FreD") is undefined. Telling if this is
     * SENTENCE (Tubal-Cain) or MIXED (really the case) is complex and would
     * slow things down for a case that I don't believe happens with Bible text.
     * </p>
     * 
     * @param word
     *            The word to be tested
     * @return LOWER, SENTENCE, UPPER or MIXED
     * @exception IllegalArgumentException
     *                is the word is null
     */
    public static CaseType getCase(String word) {
        assert word != null;

        // Blank word
        if (word.length() == 0) {
            return LOWER;
        }

        // Lower case?
        if (word.equals(word.toLowerCase(Locale.getDefault()))) {
            return LOWER;
        }

        // Upper case?
        // A string length of 1 is no good ('I' or 'A' is sentence case)
        if (word.equals(word.toUpperCase(Locale.getDefault())) && word.length() != 1) {
            return UPPER;
        }

        // So ...
        return SENTENCE;
    }

    /**
     * Get an integer representation for this CaseType
     */
    public int toInteger() {
        return ordinal();
    }

    /**
     * Lookup method to convert from a String
     */
    public static CaseType fromString(String name) {
        for (CaseType v : values()) {
            if (v.name().equalsIgnoreCase(name)) {
                return v;
            }
        }

        // cannot get here
        assert false;
        return null;
    }

    /**
     * Lookup method to convert from an integer
     */
    public static CaseType fromInteger(int i) {
        for (CaseType v : values()) {
            if (v.ordinal() == i) {
                return v;
            }
        }

        // cannot get here
        assert false;
        return null;
    }

}
