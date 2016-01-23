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

import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.Versification;

/**
 * Types of Passage Blurring Restrictions.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
 */
public enum RestrictionType {
    /**
     * There is no restriction on blurring.
     */
    NONE {
        @Override
        public boolean isSameScope(Versification v11n, Verse start, Verse end) {
            return true;
        }

        @Override
        public VerseRange blur(Versification v11n, VerseRange range, int blurDown, int blurUp) {
            Verse start = v11n.subtract(range.getStart(), blurDown);
            Verse end = v11n.add(range.getEnd(), blurUp);
            return new VerseRange(v11n, start, end);
        }

        @Override
        public VerseRange blur(Versification v11n, Verse verse, int blurDown, int blurUp) {
            Verse start = v11n.subtract(verse, blurDown);
            Verse end = v11n.add(verse, blurUp);
            return new VerseRange(v11n, start, end);
        }

        @Override
        public VerseRange toRange(Versification v11n, Verse verse, int count) {
            Verse end = verse;
            if (count > 1) {
                end = v11n.add(verse, count - 1);
            }
            return new VerseRange(v11n, verse, end);
        }
    },

    /**
     * Blurring is restricted to the chapter
     */
    CHAPTER {
        @Override
        public boolean isSameScope(Versification v11n, Verse start, Verse end) {
            return v11n.isSameChapter(start, end);
        }

        @Override
        public VerseRange blur(Versification v11n, VerseRange range, int blurDown, int blurUp) {
            Verse start = range.getStart();
            BibleBook startBook = start.getBook();
            int startChapter = start.getChapter();
            int startVerse = start.getVerse() - blurDown;

            Verse end = range.getEnd();
            BibleBook endBook = end.getBook();
            int endChapter = end.getChapter();
            int endVerse = end.getVerse() + blurUp;

            startVerse = Math.max(startVerse, 0);
            endVerse = Math.min(endVerse, v11n.getLastVerse(endBook, endChapter));

            Verse newStart = new Verse(v11n, startBook, startChapter, startVerse);
            Verse newEnd = new Verse(v11n, endBook, endChapter, endVerse);
            return new VerseRange(v11n, newStart, newEnd);
        }

        @Override
        public VerseRange blur(Versification v11n, Verse verse, int blurDown, int blurUp) {
            BibleBook book = verse.getBook();
            int chapter = verse.getChapter();
            int startVerse = verse.getVerse() - blurDown;
            int endVerse = verse.getVerse() + blurUp;

            startVerse = Math.max(startVerse, 0);
            endVerse = Math.min(endVerse, v11n.getLastVerse(book, chapter));

            Verse start = new Verse(v11n, book, chapter, startVerse);
            Verse end = new Verse(v11n, book, chapter, endVerse);
            return new VerseRange(v11n, start, end);
        }

        @Override
        public VerseRange toRange(Versification v11n, Verse verse, int count) {
            Verse end = v11n.add(verse, count - 1);
            return new VerseRange(v11n, verse, end);
        }
    };

    /**
     * Are the two verses in the same scope.
     * 
     * @param v11n
     *            the versification to which this reference pertains
     * @param start
     *            the first verse
     * @param end
     *            the second verse
     * @return true if the two are in the same scope.
     */
    public abstract boolean isSameScope(Versification v11n, Verse start, Verse end);

    /**
     * Blur a verse range the specified amount. Since verse ranges are
     * immutable, it creates a new one.
     * 
     * @param v11n
     *            the versification to which this reference pertains
     * @param range
     * @param blurDown
     * @param blurUp
     * @return a verse range after blurring.
     */
    public abstract VerseRange blur(Versification v11n, VerseRange range, int blurDown, int blurUp);

    /**
     * Blur a verse the specified amount. Since verse are immutable and refer to
     * a single verse, it creates a verse range.
     * 
     * @param v11n
     *            the versification to which this reference pertains
     * @param verse
     * @param blurDown
     * @param blurUp
     * @return a verse range after blurring
     */
    public abstract VerseRange blur(Versification v11n, Verse verse, int blurDown, int blurUp);

    /**
     * Create a range from the verse having the specified number of verses.
     * 
     * @param v11n
     *            the versification to which this reference pertains
     * @param verse
     * @param count
     * @return a verse range created by extending a verse forward.
     */
    public abstract VerseRange toRange(Versification v11n, Verse verse, int count);

    /**
     * Get an integer representation for this RestrictionType
     * 
     * @return the integer representation
     */
    public int toInteger() {
        return ordinal();
    }

    /**
     * Lookup method to convert from a String
     * @param name 
     * @return the matching restriction type
     */
    public static RestrictionType fromString(String name) {
        for (RestrictionType v : values()) {
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
     * @return the restriction type from its ordinal value
     */
    public static RestrictionType fromInteger(int i) {
        for (RestrictionType v : values()) {
            if (v.ordinal() == i) {
                return v;
            }
        }

        // cannot get here
        assert false;
        return null;
    }

    /**
     * The default Blur settings. This is used by config to set a default.
     * 
     * @param value
     *            The new default blur setting
     */
    public static void setBlurRestriction(int value) {
        defaultBlurRestriction = RestrictionType.fromInteger(value);
    }

    /**
     * The default Blur settings. This is used by config to manage a default
     * setting.
     * 
     * @return The current default blurRestriction setting
     */
    public static int getBlurRestriction() {
        return getDefaultBlurRestriction().toInteger();
    }

    /**
     * The default Blur settings. This is used by BlurCommandWord
     * 
     * @return The current default blurRestriction setting
     */
    public static RestrictionType getDefaultBlurRestriction() {
        return defaultBlurRestriction;
    }

    /**
     * A default restriction type for blurring.
     */
    private static RestrictionType defaultBlurRestriction = RestrictionType.NONE;
}
