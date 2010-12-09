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
 * Types of Passage Lists.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
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
