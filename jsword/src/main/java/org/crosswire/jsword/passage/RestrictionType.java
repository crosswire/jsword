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

import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.BibleInfo;

/**
 * Types of Passage Blurring Restrictions.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public enum RestrictionType {
    /**
     * There is no restriction on blurring.
     */
    NONE {
        @Override
        public boolean isSameScope(Verse start, Verse end) {
            return true;
        }

        @Override
        public VerseRange blur(VerseRange range, int blurDown, int blurUp) {
            Verse start = range.getStart().subtract(blurDown);
            Verse end = range.getEnd().add(blurUp);
            return new VerseRange(start, end);
        }

        @Override
        public VerseRange blur(Verse verse, int blurDown, int blurUp) {
            Verse start = verse.subtract(blurDown);
            Verse end = verse.add(blurUp);
            return new VerseRange(start, end);
        }

        @Override
        public VerseRange toRange(Verse verse, int count) {
            Verse end = verse;
            if (count > 1) {
                end = verse.add(count - 1);
            }
            return new VerseRange(verse, end);
        }
    },

    /**
     * Blurring is restricted to the chapter
     */
    CHAPTER {
        @Override
        public boolean isSameScope(Verse start, Verse end) {
            return start.isSameChapter(end);
        }

        @Override
        public VerseRange blur(VerseRange range, int blurDown, int blurUp) {
            try {
                Verse start = range.getStart();
                BibleBook startBook = start.getBook();
                int startChapter = start.getChapter();
                int startVerse = start.getVerse() - blurDown;

                Verse end = range.getEnd();
                BibleBook endBook = end.getBook();
                int endChapter = end.getChapter();
                int endVerse = end.getVerse() + blurUp;

                startVerse = Math.max(startVerse, 1);
                endVerse = Math.min(endVerse, BibleInfo.versesInChapter(endBook, endChapter));

                Verse newStart = new Verse(startBook, startChapter, startVerse);
                Verse newEnd = new Verse(endBook, endChapter, endVerse);
                return new VerseRange(newStart, newEnd);
            } catch (NoSuchVerseException ex) {
                assert false : ex;
                return null;
            }
        }

        @Override
        public VerseRange blur(Verse verse, int blurDown, int blurUp) {
            int verseNumber = verse.getVerse();

            int down = verseNumber - Math.max(verseNumber - blurDown, 1);

            Verse start = verse;
            if (down > 0) {
                start = verse.subtract(down);
            }

            BibleBook book = verse.getBook();
            int chapterNumber = verse.getChapter();
            int up = Math.min(verseNumber + blurUp, BibleInfo.versesInChapter(book, chapterNumber)) - verseNumber;
            Verse end = verse;
            if (up > 0) {
                end = verse.add(up);
            }

            return new VerseRange(start, end);
        }

        @Override
        public VerseRange toRange(Verse verse, int count) {
            Verse end = verse.add(count - 1);
            return new VerseRange(verse, end);
        }
    };

    /**
     * Are the two verses in the same scope.
     * 
     * @param start
     *            the first verse
     * @param end
     *            the second verse
     * @return true if the two are in the same scope.
     */
    public abstract boolean isSameScope(Verse start, Verse end);

    /**
     * Blur a verse range the specified amount. Since verse ranges are
     * immutable, it creates a new one.
     * 
     * @param range
     * @param blurDown
     * @param blurUp
     * @return a verse range after blurring.
     */
    public abstract VerseRange blur(VerseRange range, int blurDown, int blurUp);

    /**
     * Blur a verse the specified amount. Since verse are immutable and refer to
     * a single verse, it creates a verse range.
     * 
     * @param verse
     * @param blurDown
     * @param blurUp
     * @return a verse range after blurring
     */
    public abstract VerseRange blur(Verse verse, int blurDown, int blurUp);

    /**
     * Create a range from the verse having the specified number of verses.
     * 
     * @param verse
     * @param count
     * @return a verse range created by extending a verse forward.
     */
    public abstract VerseRange toRange(Verse verse, int count);

    /**
     * Get an integer representation for this RestrictionType
     */
    public int toInteger() {
        return ordinal();
    }

    /**
     * Lookup method to convert from a String
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
