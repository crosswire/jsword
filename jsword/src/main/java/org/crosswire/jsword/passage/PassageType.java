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


/**
 * Types of Passage optimizations.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public enum PassageType {
    /**
     * Optimize the Passage for speed
     */
    SPEED {
        public Passage createPassage(String passage) throws NoSuchVerseException {
            if (passage == null || passage.length() == 0) {
                return createEmptyPassage();
            }
            return new RocketPassage(passage);
        }

        public Passage createEmptyPassage() {
            return new RocketPassage();
        }
    },

    /**
     * Optimize the Passage for write speed
     */
    WRITE_SPEED {
        public Passage createPassage(String passage) throws NoSuchVerseException {
            if (passage == null || passage.length() == 0) {
                return createEmptyPassage();
            }
            return new BitwisePassage(passage);
        }

        public Passage createEmptyPassage() {
            return new BitwisePassage();
        }
    },

    /**
     * Optimize the Passage for size
     */
    SIZE {
        public Passage createPassage(String passage) throws NoSuchVerseException {
            if (passage == null || passage.length() == 0) {
                return createEmptyPassage();
            }
            return new DistinctPassage(passage);
        }

        public Passage createEmptyPassage() {
            return new DistinctPassage();
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
        public Passage createPassage(String passage) throws NoSuchVerseException {
            if (passage == null || passage.length() == 0) {
                return createEmptyPassage();
            }
            return new PassageTally(passage);
        }

        public Passage createEmptyPassage() {
            return new PassageTally();
        }
    },

    /**
     * Optimize the Passage for tally operations
     */
    TALLY {
        public Passage createPassage(String passage) throws NoSuchVerseException {
            if (passage == null || passage.length() == 0) {
                return createEmptyPassage();
            }
            return new PassageTally(passage);
        }

        public Passage createEmptyPassage() {
            return new PassageTally();
        }
    };

    /**
     * Create an optimized passage
     * 
     * @param passage
     * @return the optimized passage
     * @throws NoSuchVerseException
     */
    public abstract Passage createPassage(String passage) throws NoSuchVerseException;

    /**
     * Create an empty, optimized passage
     * 
     * @return the optimized, empty passage
     * @throws NoSuchVerseException
     */
    public abstract Passage createEmptyPassage();

    /**
     * Lookup method to convert from a String
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
     */
    public static int toInteger(PassageType type) {
        return type.ordinal();
    }
}
