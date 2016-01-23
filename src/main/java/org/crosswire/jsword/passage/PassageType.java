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

import org.crosswire.jsword.versification.Versification;

/**
 * Types of Passage optimizations.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public enum PassageType {
    /**
     * Optimize the Passage for speed
     */
    SPEED {
        @Override
        public Passage createPassage(Versification v11n, String passage, Key basis) throws NoSuchVerseException {
            if (passage == null || passage.length() == 0) {
                return createEmptyPassage(v11n);
            }
            return new RocketPassage(v11n, passage, basis);
        }

        @Override
        public Passage createEmptyPassage(Versification v11n) {
            return new RocketPassage(v11n);
        }
    },

    /**
     * Optimize the Passage for write speed
     */
    WRITE_SPEED {
        @Override
        public Passage createPassage(Versification v11n, String passage, Key basis) throws NoSuchVerseException {
            if (passage == null || passage.length() == 0) {
                return createEmptyPassage(v11n);
            }
            return new BitwisePassage(v11n, passage, basis);
        }

        @Override
        public Passage createEmptyPassage(Versification v11n) {
            return new BitwisePassage(v11n);
        }
    },

    /**
     * Optimize the Passage for size
     */
    SIZE {
        @Override
        public Passage createPassage(Versification v11n, String passage, Key basis) throws NoSuchVerseException {
            if (passage == null || passage.length() == 0) {
                return createEmptyPassage(v11n);
            }
            return new DistinctPassage(v11n, passage, basis);
        }

        @Override
        public Passage createEmptyPassage(Versification v11n) {
            return new DistinctPassage(v11n);
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = -1959355535575121168L;
    },

    /**
     * Optimize the Passage for a mix
     */
    MIX {
        @Override
        public Passage createPassage(Versification v11n, String passage, Key basis) throws NoSuchVerseException {
            if (passage == null || passage.length() == 0) {
                return createEmptyPassage(v11n);
            }
            return new RangedPassage(v11n, passage, basis);
        }

        @Override
        public Passage createEmptyPassage(Versification v11n) {
            return new RangedPassage(v11n);
        }
    },

    /**
     * Optimize the Passage for tally operations
     */
    TALLY {
        @Override
        public Passage createPassage(Versification v11n, String passage, Key basis) throws NoSuchVerseException {
            if (passage == null || passage.length() == 0) {
                return createEmptyPassage(v11n);
            }
            return new PassageTally(v11n, passage, basis);
        }

        @Override
        public Passage createEmptyPassage(Versification v11n) {
            return new PassageTally(v11n);
        }
    };

    /**
     * Create an optimized passage
     * 
     * @param v11n
     *            the versification to which this reference pertains
     * @param passage
     * @param basis 
     * @return the optimized passage
     * @throws NoSuchVerseException
     */
    public abstract Passage createPassage(Versification v11n, String passage, Key basis) throws NoSuchVerseException;
    public Passage createPassage(Versification v11n, String passage) throws NoSuchVerseException {
        return createPassage(v11n, passage, null);
    }

    /**
     * Create an empty, optimized passage
     * 
     * @param v11n
     *            the versification to which this reference pertains
     * @return the optimized, empty passage
     */
    public abstract Passage createEmptyPassage(Versification v11n);

    /**
     * Lookup method to convert from a String
     * @param name 
     * @return the matching Passage type
     */
    public static PassageType fromString(String name) {
        for (PassageType v : values()) {
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
     * 
     * @param i 
     * @return the Passage type from its ordinal value
     */
    public static PassageType fromInteger(int i) {
        for (PassageType v : values()) {
            if (v.ordinal() == i) {
                return v;
            }
        }

        // on error return SPEED
        return SPEED;
    }

    /**
     * Lookup method to convert from an integer
     * @param type 
     * @return the ordinal value for the passage type
     */
    public static int toInteger(PassageType type) {
        return type.ordinal();
    }
}
