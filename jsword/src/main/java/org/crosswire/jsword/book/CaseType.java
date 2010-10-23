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

import java.io.Serializable;
import java.util.Locale;

/**
 * Types of Sentence Case.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public abstract class CaseType implements Serializable {
    public static final CaseType LOWER = new CaseType("LOWER")
    {
        public String setCase(String word) {
            return word.toLowerCase(Locale.getDefault());
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3546637707360286256L;
    };

    public static final CaseType SENTENCE = new CaseType("SENTENCE")
    {
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

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3905520510312985138L;
    };

    public static final CaseType UPPER = new CaseType("UPPER")
    {
        public String setCase(String word) {
            return word.toUpperCase(Locale.getDefault());
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3257002163871035698L;
    };

    public abstract String setCase(String word);

    /**
     * Simple ctor
     */
    public CaseType(String name) {
        this.name = name;
    }

    /**
     * Change to sentence case - ie first character in caps, the rest in lower.
     * 
     * @param word
     *            The word to be manipulated
     * @return The altered word
     */
    protected static String toSentenceCase(String word) {
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
        for (int i = 0; i < VALUES.length; i++) {
            if (equals(VALUES[i])) {
                return i;
            }
        }
        // cannot get here
        assert false;
        return -1;
    }

    /**
     * Lookup method to convert from a String
     */
    public static CaseType fromString(String name) {
        for (int i = 0; i < VALUES.length; i++) {
            CaseType o = VALUES[i];
            if (o.name.equalsIgnoreCase(name)) {
                return o;
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
        return VALUES[i];
    }

    /**
     * Prevent subclasses from overriding canonical identity based Object
     * methods
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public final boolean equals(Object o) {
        return super.equals(o);
    }

    /**
     * Prevent subclasses from overriding canonical identity based Object
     * methods
     * 
     * @see java.lang.Object#hashCode()
     */
    public final int hashCode() {
        return super.hashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return name;
    }

    /**
     * The name of the type
     */
    private transient String name;

    // Support for serialization
    private static int nextObj;
    private final int obj = nextObj++;

    Object readResolve() {
        return VALUES[obj];
    }

    private static final CaseType[] VALUES = {
            LOWER, SENTENCE, UPPER,
    };

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = -63772726311422060L;
}
