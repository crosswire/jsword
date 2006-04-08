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

import java.io.Serializable;


/**
 * Types of Passage Blurring Restrictions.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public abstract class RestrictionType implements Serializable
{
    /**
     * There is no restriction on blurring.
     */
    public static final RestrictionType NONE = new RestrictionType("NONE") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.RestrictionType#isSameScope(org.crosswire.jsword.passage.Verse, org.crosswire.jsword.passage.Verse)
         */
        public boolean isSameScope(Verse start, Verse end)
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.RestrictionType#blur(org.crosswire.jsword.passage.VerseRange, int, int)
         */
        public VerseRange blur(VerseRange range, int blurDown, int blurUp)
        {
            Verse start = range.getStart().subtract(blurDown);
            Verse end = range.getEnd().add(blurUp);
            return new VerseRange(start, end);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.RestrictionType#blur(org.crosswire.jsword.passage.Verse, int, int)
         */
        public VerseRange blur(Verse verse, int blurDown, int blurUp)
        {
            Verse start = verse.subtract(blurDown);
            Verse end = verse.add(blurUp);
            return new VerseRange(start, end);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.RestrictionType#blur(org.crosswire.jsword.passage.Verse, int, int)
         */
        public VerseRange toRange(Verse verse, int count)
        {
            Verse end = verse;
            if (count > 1)
            {
                end = verse.add(count - 1);
            }
            return new VerseRange(verse, end);
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3905246714754643248L;
    };

//    /**
//     * Blurring is restricted to the book.
//     */
//    public static final RestrictionType BOOK = new RestrictionType("BOOK") //$NON-NLS-1$
//    {
//        /* (non-Javadoc)
//         * @see org.crosswire.jsword.passage.RestrictionType#isSameScope(org.crosswire.jsword.passage.Verse, org.crosswire.jsword.passage.Verse)
//         */
//        public boolean isSameScope(Verse start, Verse end)
//        {
//            return start.isSameBook(end);
//        }
//
//        /* (non-Javadoc)
//         * @see org.crosswire.jsword.passage.RestrictionType#blur(org.crosswire.jsword.passage.VerseRange, int, int)
//         */
//        public VerseRange blur(VerseRange range, int blurDown, int blurUp)
//        {
//            throw new IllegalArgumentException(Msg.RANGE_BLURBOOK.toString());
//        }
//
//        /* (non-Javadoc)
//         * @see org.crosswire.jsword.passage.RestrictionType#blur(org.crosswire.jsword.passage.Verse, int, int)
//         */
//        public VerseRange blur(Verse verse, int blurDown, int blurUp)
//        {
//            throw new IllegalArgumentException(Msg.RANGE_BLURBOOK.toString());
//        }
//
//        /* (non-Javadoc)
//         * @see org.crosswire.jsword.passage.RestrictionType#blur(org.crosswire.jsword.passage.Verse, int, int)
//         */
//        public VerseRange toRange(Verse verse, int count)
//        {
//            throw new IllegalArgumentException(Msg.RANGE_BLURBOOK.toString());
//        }
//
//        /**
//         * Serialization ID
//         */
//        private static final long serialVersionUID = 3978142166633820472L;
//    };

    /**
     * Blurring is restricted to the chapter
     */
    public static final RestrictionType CHAPTER = new RestrictionType("CHAPTER") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.RestrictionType#isSameScope(org.crosswire.jsword.passage.Verse, org.crosswire.jsword.passage.Verse)
         */
        public boolean isSameScope(Verse start, Verse end)
        {
            return start.isSameChapter(end);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.RestrictionType#blur(org.crosswire.jsword.passage.VerseRange, int, int)
         */
        public VerseRange blur(VerseRange range, int blurDown, int blurUp)
        {
            try
            {
                Verse start = range.getStart();
                int startBook = start.getBook();
                int startChapter = start.getChapter();
                int startVerse = start.getVerse() - blurDown;

                Verse end = range.getEnd();
                int endBook = end.getBook();
                int endChapter = end.getChapter();
                int endVerse = end.getVerse() + blurUp;

                startVerse = Math.max(startVerse, 1);
                endVerse = Math.min(endVerse, BibleInfo.versesInChapter(endBook, endChapter));

                Verse newStart = new Verse(startBook, startChapter, startVerse);
                Verse newEnd = new Verse(endBook, endChapter, endVerse);
                return new VerseRange(newStart, newEnd);
            }
            catch (NoSuchVerseException ex)
            {
                assert false : ex;
                return null;
            }
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.RestrictionType#blur(org.crosswire.jsword.passage.Verse, int, int)
         */
        public VerseRange blur(Verse verse, int blurDown, int blurUp)
        {
            try
            {
                int verseNumber = verse.getVerse();

                int down = verseNumber - Math.max(verseNumber - blurDown, 1);

                Verse start = verse;
                if (down > 0)
                {
                    start = verse.subtract(down);
                }

                int bookNumber = verse.getBook();
                int chapterNumber = verse.getChapter();
                int up = Math.min(verseNumber + blurUp, BibleInfo.versesInChapter(bookNumber, chapterNumber)) - verseNumber;
                Verse end = verse;
                if (up > 0)
                {
                    end = verse.add(up);
                }

                return new VerseRange(start, end);
            }
            catch (NoSuchVerseException ex)
            {
                assert false : ex;
                return null;
            }
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.RestrictionType#toRange(org.crosswire.jsword.passage.Verse, int)
         */
        public VerseRange toRange(Verse verse, int count)
        {
            Verse end = verse.add(count - 1);
            return new VerseRange(verse, end);
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3257284751327768626L;
    };

    /**
     * Are the two verses in the same scope.
     * @param start the first verse
     * @param end the second verse
     * @return true if the two are in the same scope.
     */
    public abstract boolean isSameScope(Verse start, Verse end);

    /**
     * Blur a verse range the specified amount. Since verse ranges are immutable,
     * it creates a new one.
     * @param range
     * @param blurDown
     * @param blurUp
     * @return a verse range after blurring.
     */
    public abstract VerseRange blur(VerseRange range, int blurDown, int blurUp);

    /**
     * Blur a verse the specified amount. Since verse are immutable and refer to
     * a single verse, it creates a verse range.
     * @param verse
     * @param blurDown
     * @param blurUp
     * @return a verse range after blurring
     */
    public abstract VerseRange blur(Verse verse, int blurDown, int blurUp);

    /**
     * Create a range from the verse having the specified number of verses.
     * @param verse
     * @param count
     * @return a verse range created by extending a verse forward.
     */
    public abstract VerseRange toRange(Verse verse, int count);

    /**
     * Simple ctor
     */
    public RestrictionType(String name)
    {
        this.name = name;
    }

    /**
     * Get an integer representation for this RestrictionType
     */
    public int toInteger()
    {
        for (int i = 0; i < VALUES.length; i++)
        {
            if (equals(VALUES[i]))
            {
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
    public static RestrictionType fromString(String name)
    {
        for (int i = 0; i < VALUES.length; i++)
        {
            RestrictionType o = VALUES[i];
            if (o.name.equalsIgnoreCase(name))
            {
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
    public static RestrictionType fromInteger(int i)
    {
        return VALUES[i];
    }

    /**
     * Prevent subclasses from overriding canonical identity based Object methods
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public final boolean equals(Object o)
    {
        return super.equals(o);
    }

    /**
     * Prevent subclasses from overriding canonical identity based Object methods
     * @see java.lang.Object#hashCode()
     */
    public final int hashCode()
    {
        return super.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return name;
    }

    /**
     * The default Blur settings. This is used by config to set a default.
     * @param value The new default blur setting
     */
    public static void setBlurRestriction(int value)
    {
        defaultBlurRestriction = RestrictionType.fromInteger(value);
    }

    /**
     * The default Blur settings. This is used by config to manage
     * a default setting.
     * @return The current default blurRestriction setting
     */
    public static int getBlurRestriction()
    {
        return getDefaultBlurRestriction().toInteger();
    }

    /**
     * The default Blur settings. This is used by BlurCommandWord
     * @return The current default blurRestriction setting
     */
    public static RestrictionType getDefaultBlurRestriction()
    {
        if (defaultBlurRestriction == null)
        {
            defaultBlurRestriction = RestrictionType.NONE;
        }
        return defaultBlurRestriction;
    }

    /**
     * A default restriction type for blurring.
     */
    private static RestrictionType defaultBlurRestriction;

    /**
     * The name of the PassageListType
     */
    private String name;

    // Support for serialization
    private static int nextObj;
    private final int obj = nextObj++;

    Object readResolve()
    {
        return VALUES[obj];
    }

    private static final RestrictionType[] VALUES =
    {
        NONE,
        CHAPTER,
//      BOOK,
    };
}
