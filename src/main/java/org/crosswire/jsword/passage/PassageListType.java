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

/**
 * Types of Passage Lists.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
 */
public enum PassageListType {
    /**
     * Passage to be interpreted as a list of verses.
     */
    VERSES {
        @Override
        public Object getElementAt(Passage ref, int index, RestrictionType restrict) {
            if (ref == null) {
                return null;
            }
            return ref.getVerseAt(index);
        }

        @Override
        public int count(Passage ref, RestrictionType restrict) {
            if (ref == null) {
                return 0;
            }
            return ref.countVerses();
        }
    },

    /**
     * Passage to be interpreted as a list of ranges.
     */
    RANGES {
        @Override
        public Object getElementAt(Passage ref, int index, RestrictionType restrict) {
            if (ref == null) {
                return null;
            }
            return ref.getRangeAt(index, restrict);
        }

        @Override
        public int count(Passage ref, RestrictionType restrict) {
            if (ref == null) {
                return 0;
            }
            return ref.countRanges(restrict);
        }
    };

    public abstract Object getElementAt(Passage ref, int index, RestrictionType restrict);

    public abstract int count(Passage ref, RestrictionType restrict);
}
