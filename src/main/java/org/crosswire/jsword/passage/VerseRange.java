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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.crosswire.common.icu.NumberShaper;
import org.crosswire.common.util.Logger;
import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.Versification;

/**
 * A VerseRange is one step between a Verse and a Passage - it is a Verse plus a
 * verseCount. Every VerseRange has a start, a verseCount and an end. A
 * VerseRange is designed to be immutable. This is a necessary from a
 * collections point of view. A VerseRange should always be valid, although some
 * versions may not return any text for verses that they consider to be
 * miss-translated in some way.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public final class VerseRange implements Key {
    /**
     * The default VerseRange is a single verse - Genesis 1:1. I didn't want to
     * provide this constructor however, you are supposed to provide a default
     * ctor for all beans. For this reason I suggest you don't use it.
     * @deprecated  use {@link #VerseRange(Versification)} instead
     */
    @Deprecated
    public VerseRange() {
        this(null, null, Verse.DEFAULT, Verse.DEFAULT);
    }

    /**
     * Construct a VerseRange from a Verse. The resultant VerseRange will be 1
     * verse in verseCount.
     * 
     * @param start
     *            The verse to start from
     * @deprecated  use {@link #VerseRange(Versification, Verse)} instead
     */
    @Deprecated
    public VerseRange(Verse start) {
        this(null, null, start, start);
    }

    /**
     * @param start
     * @param end
     * @deprecated  use {@link #VerseRange(Versification, Verse, Verse)} instead
     */
    @Deprecated
    public VerseRange(Verse start, Verse end) {
        this(null, null, start, end);
    }

    /**
     * The default VerseRange is a single verse - Genesis 1:1. I didn't want to
     * provide this constructor however, you are supposed to provide a default
     * ctor for all beans. For this reason I suggest you don't use it.
     */
    public VerseRange(Versification v11n) {
        this(v11n, null, Verse.DEFAULT, Verse.DEFAULT);
    }

    /**
     * Construct a VerseRange from a Verse. The resultant VerseRange will be 1
     * verse in verseCount.
     * 
     * @param start
     *            The verse to start from
     */
    public VerseRange(Versification v11n, Verse start) {
        this(v11n, null, start, start);
    }

    public VerseRange(Versification v11n, Verse start, Verse end) {
        this(v11n, null, start, end);
    }

    /**
     * Construct a VerseRange from 2 Verses If start is later than end then swap
     * the two around. This constructor is deliberately package protected so
     * that is used only by VerseFactory.
     * 
     * @param start
     *            The verse to start from
     * @param end
     *            The verse to end with
     * @deprecated  use {@link #VerseRange(Versification, String, Verse, Verse)} instead
     */
    @Deprecated
    /* package */VerseRange(String original, Verse start, Verse end) {
        this(null, original, start, end);
    }

    /* package */VerseRange(Versification v11n, String original, Verse start, Verse end) {
        assert v11n != null;
        assert start != null;
        assert end != null;

        this.v11n = v11n;
        this.originalName = original;
        shaper = new NumberShaper();

        int distance = v11n.distance(start, end);

        if (distance < 0) {
            this.start = end;
            this.end = start;
            this.verseCount = calcVerseCount();
        } else if (distance == 0) {
            this.start = start;
            this.end = start;
            this.verseCount = 1;
        } else {
            this.start = start;
            this.end = end;
            this.verseCount = calcVerseCount();
        }

        verifyData();
    }

    /**
     * Get the Versification to which this VerseRange participates.
     * 
     * @return the reference system.
     */
    public Versification getVersification() {
        return v11n;
    }

    /**
     * Merge 2 VerseRanges together. The resulting range will encompass
     * Everything in-between the extremities of the 2 ranges.
     * 
     * @param a
     *            The first verse range to be merged
     * @param b
     *            The second verse range to be merged
     */
    public VerseRange(VerseRange a, VerseRange b) {
        v11n = a.v11n;
        shaper = new NumberShaper();
        start = v11n.min(a.getStart(), b.getStart());
        end = v11n.max(a.getEnd(), b.getEnd());
        verseCount = calcVerseCount();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getName()
     */
    public String getName() {
        return getName(null);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getName(org.crosswire.jsword.passage.Key)
     */
    public String getName(Key base) {
        if (PassageUtil.isPersistentNaming() && originalName != null) {
            return originalName;
        }

        try {
            String rangeName = doGetName(base);
            // Only shape it if it can be unshaped.
            if (shaper.canUnshape()) {
                return shaper.shape(rangeName);
            }

            return rangeName;
        } catch (NoSuchVerseException ex) {
            assert false : ex;
            return "!Error!";
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getRootName()
     */
    public String getRootName() {
        return start.getRootName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getOsisRef()
     */
    public String getOsisRef() {
        BibleBook startBook = start.getBook();
        BibleBook endBook = end.getBook();
        int startChapter = start.getChapter();
        int endChapter = end.getChapter();

        // If this is in 2 separate books
        if (startBook != endBook) {
            StringBuilder buf = new StringBuilder();
            if (v11n.isStartOfBook(start)) {
                buf.append(startBook.getOSIS());
            } else if (v11n.isStartOfChapter(start)) {
                buf.append(startBook.getOSIS());
                buf.append(Verse.VERSE_OSIS_DELIM);
                buf.append(startChapter);
            } else {
                buf.append(start.getOsisRef());
            }

            buf.append(VerseRange.RANGE_PREF_DELIM);

            if (v11n.isEndOfBook(end)) {
                buf.append(endBook.getOSIS());
            } else if (v11n.isEndOfChapter(end)) {
                buf.append(endBook.getOSIS());
                buf.append(Verse.VERSE_OSIS_DELIM);
                buf.append(endChapter);
            } else {
                buf.append(end.getOsisRef());
            }

            return buf.toString();
        }

        // This range is exactly a whole book
        if (isWholeBook()) {
            // Just report the name of the book, we don't need to worry
            // about the
            // base since we start at the start of a book, and should have
            // been
            // recently normalized()
            return startBook.getOSIS();
        }

        // If this is 2 separate chapters in the same book
        if (startChapter != endChapter) {
            StringBuilder buf = new StringBuilder();
            if (v11n.isStartOfChapter(start)) {
                buf.append(startBook.getOSIS());
                buf.append(Verse.VERSE_OSIS_DELIM);
                buf.append(startChapter);
            } else {
                buf.append(start.getOsisRef());
            }

            buf.append(VerseRange.RANGE_PREF_DELIM);

            if (v11n.isEndOfChapter(end)) {
                buf.append(endBook.getOSIS());
                buf.append(Verse.VERSE_OSIS_DELIM);
                buf.append(endChapter);
            } else {
                buf.append(end.getOsisRef());
            }

            return buf.toString();
        }

        // If this range is exactly a whole chapter
        if (isWholeChapter()) {
            // Just report the name of the book and the chapter
            StringBuilder buf = new StringBuilder();
            buf.append(startBook.getOSIS());
            buf.append(Verse.VERSE_OSIS_DELIM);
            buf.append(startChapter);
            return buf.toString();
        }

        // If this is 2 separate verses
        if (start.getVerse() != end.getVerse()) {
            StringBuilder buf = new StringBuilder();
            buf.append(start.getOsisRef());
            buf.append(VerseRange.RANGE_PREF_DELIM);
            buf.append(end.getOsisRef());
            return buf.toString();
        }

        // The range is a single verse
        return start.getOsisRef();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getOsisID()
     */
    public String getOsisID() {

        // This range is exactly a whole book
        if (isWholeBook()) {
            // Just report the name of the book, we don't need to worry
            // about the base since we start at the start of a book, and
            // should have been recently normalized()
            return start.getBook().getOSIS();
        }

        // If this range is exactly a whole chapter
        if (isWholeChapter()) {
            // Just report the name of the book and the chapter
            return start.getBook().getOSIS() + Verse.VERSE_OSIS_DELIM + start.getChapter();
        }

        int startOrdinal = start.getOrdinal();
        int endOrdinal = end.getOrdinal();

        // to see if it is wholly contained in the range and output it if it is.

        // Estimate the size of the buffer: book.dd.dd (where book is 3-5, 3 typical)
        StringBuilder buf = new StringBuilder((endOrdinal - startOrdinal + 1) * 10);
        buf.append(start.getOsisID());
        for (int i = startOrdinal + 1; i < endOrdinal; i++) {
            buf.append(AbstractPassage.REF_OSIS_DELIM);
            buf.append(v11n.decodeOrdinal(i).getOsisID());
        }

        // It just might be a single verse range!
        if (startOrdinal != endOrdinal) {
            buf.append(AbstractPassage.REF_OSIS_DELIM);
            buf.append(end.getOsisID());
        }

        return buf.toString();
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Fetch the first verse in this range.
     * 
     * @return The first verse in the range
     */
    public Verse getStart() {
        return start;
    }

    /**
     * Fetch the last verse in this range.
     * 
     * @return The last verse in the range
     */
    public Verse getEnd() {
        return end;
    }

    /**
     * How many chapters in this range
     * 
     * @return The number of chapters. Always >= 1.
     * @deprecated  use {@link org.crosswire.jsword.versification.Versification#getChapterCount(Verse, Verse)} instead
     */
    @Deprecated
    public int getChapterCount() {
        return v11n.getChapterCount(start, end);
    }

    /**
     * How many books in this range
     * 
     * @return The number of books. Always >= 1.
     * @deprecated  use {@link org.crosswire.jsword.versification.Versification#getBookCount(Verse, Verse)} instead
     */
    @Deprecated
    public int getBookCount() {
        return v11n.getBookCount(start, end);
    }

    @Override
    public VerseRange clone() {
        // This gets us a shallow copy
        VerseRange copy = null;
        try {
            copy = (VerseRange) super.clone();
            copy.start = start;
            copy.end = end;
            copy.verseCount = verseCount;
            copy.originalName = originalName;
            copy.shaper = new NumberShaper();
            copy.v11n = v11n;
        } catch (CloneNotSupportedException e) {
            assert false : e;
        }

        return copy;
    }

    @Override
    public boolean equals(Object obj) {
        // Since this can not be null
        if (obj == null) {
            return false;
        }

        // Check that that is the same as this
        // Don't use instanceOf since that breaks inheritance
        if (!obj.getClass().equals(this.getClass())) {
            return false;
        }

        VerseRange vr = (VerseRange) obj;

        // The real tests
        if (!vr.getStart().equals(getStart())) {
            return false;
        }

        if (vr.getCardinality() != getCardinality()) {
            return false;
        }

        // We don't really need to check this one too.
        // if (!vr.getEnd().equals(getEnd())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (start.getOrdinal() << 16) + verseCount;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Key obj) {
        // This ensures a ClassCastException without further test
        Verse that = null;
        if (obj instanceof Verse) {
            that = (Verse) obj;
        } else {
            that = ((VerseRange) obj).getStart();
        }

        int start_compare = getStart().compareTo(that);
        if (start_compare != 0) {
            return start_compare;
        }

        // So the start verses are the same, but the Verse(Range)s may not
        // be equal() since they have lengths
        int that_length = 1;
        if (obj instanceof VerseRange) {
            that_length = ((VerseRange) obj).getCardinality();
        }

        if (that_length == getCardinality()) {
            return 0;
        }

        if (that_length < getCardinality()) {
            return 1;
        }

        return -1;
    }

    /**
     * Are the 2 VerseRanges in question contiguous. that is - could they be
     * represented by a single VerseRange. Note that one range could be entirely
     * contained within the other and they would be considered adjacentTo() For
     * example Gen 1:1-2 is adjacent to Gen 1:1-5 and Gen 1:3-4 but not to Gen
     * 1:4-10. Also Gen 1:29-30 is adjacent to Gen 2:1-10
     * 
     * @param that
     *            The VerseRange to compare to
     * @return true if the ranges are adjacent
     */
    public boolean adjacentTo(VerseRange that) {
        int thatStart = that.getStart().getOrdinal();
        int thatEnd = that.getEnd().getOrdinal();
        int thisStart = getStart().getOrdinal();
        int thisEnd = getEnd().getOrdinal();

        // if that starts inside or is next to this we are adjacent.
        if (thatStart >= thisStart - 1 && thatStart <= thisEnd + 1) {
            return true;
        }

        // if this starts inside or is next to that we are adjacent.
        if (thisStart >= thatStart - 1 && thisStart <= thatEnd + 1) {
            return true;
        }

        // otherwise we're not adjacent
        return false;
    }

    /**
     * Do the 2 VerseRanges in question actually overlap. This is slightly more
     * restrictive than the adjacentTo() test which could be satisfied by ranges
     * like Gen 1:1-2 and Gen 1:3-4. overlaps() however would return false given
     * these ranges. For example Gen 1:1-2 is adjacent to Gen 1:1-5 but not to
     * Gen 1:3-4 not to Gen 1:4-10. Also Gen 1:29-30 does not overlap Gen 2:1-10
     * 
     * @param that
     *            The VerseRange to compare to
     * @return true if the ranges are adjacent
     */
    public boolean overlaps(VerseRange that) {
        int thatStart = that.getStart().getOrdinal();
        int thatEnd = that.getEnd().getOrdinal();
        int thisStart = getStart().getOrdinal();
        int thisEnd = getEnd().getOrdinal();

        // if that starts inside this we are adjacent.
        if (thatStart >= thisStart && thatStart <= thisEnd) {
            return true;
        }

        // if this starts inside that we are adjacent.
        if (thisStart >= thatStart && thisStart <= thatEnd) {
            return true;
        }

        // otherwise we're not adjacent
        return false;
    }

    /**
     * Is the given verse entirely within our range. For example if this =
     * "Gen 1:1-31" then: <tt>contains(Verse("Gen 1:3")) == true</tt>
     * <tt>contains(Verse("Gen 2:1")) == false</tt>
     * 
     * @param that
     *            The Verse to compare to
     * @return true if we contain it.
     */
    public boolean contains(Verse that) {
        return v11n.distance(start, that) >= 0 && v11n.distance(that, end) >= 0;
    }

    /**
     * Is the given range within our range. For example if this = "Gen 1:1-31"
     * then: <tt>this.contains(Verse("Gen 1:3-10")) == true</tt>
     * <tt>this.contains(Verse("Gen 2:1-1")) == false</tt>
     * 
     * @param that
     *            The Verse to compare to
     * @return true if we contain it.
     */
    public boolean contains(VerseRange that) {
        return v11n.distance(start, that.getStart()) >= 0 && v11n.distance(that.getEnd(), end) >= 0;
    }

    /**
     * Does this range represent exactly one chapter, no more or less.
     * 
     * @return true if we are exactly one chapter.
     */
    public boolean isWholeChapter() {
        return v11n.isSameChapter(start, end) && isWholeChapters();
    }

    /**
     * Does this range represent a number of whole chapters
     * 
     * @return true if we are a whole number of chapters.
     */
    public boolean isWholeChapters() {
        return v11n.isStartOfChapter(start) && v11n.isEndOfChapter(end);
    }

    /**
     * Does this range represent exactly one book, no more or less.
     * 
     * @return true if we are exactly one book.
     */
    public boolean isWholeBook() {
        return v11n.isSameBook(start, end) && isWholeBooks();
    }

    /**
     * Does this range represent a whole number of books.
     * 
     * @return true if we are a whole number of books.
     */
    public boolean isWholeBooks() {
        return v11n.isStartOfBook(start) && v11n.isEndOfBook(end);
    }

    /**
     * Does this range occupy more than one book;
     * 
     * @return true if we occupy 2 or more books
     */
    public boolean isMultipleBooks() {
        return start.getBook() != end.getBook();
    }

    /**
     * Create an array of Verses
     * 
     * @return The array of verses that this makes up
     */
    public Verse[] toVerseArray() {
        Verse[] retcode = new Verse[verseCount];
        int ord = start.getOrdinal();
        for (int i = 0; i < verseCount; i++) {
            retcode[i] = v11n.decodeOrdinal(ord + i);
        }

        return retcode;
    }

    /**
     * Enumerate the subranges in this range
     * 
     * @return a range iterator
     */
    public Iterator<Key> rangeIterator(RestrictionType restrict) {
        return new AbstractPassage.VerseRangeIterator(v11n, iterator(), restrict);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getParent()
     */
    public Key getParent() {
        return parent;
    }

    /**
     * Set a parent Key. This allows us to follow the Key interface more
     * closely, although the concept of a parent for a verse is fairly alien.
     * 
     * @param parent
     *            The parent Key for this verse
     */
    public void setParent(Key parent) {
        this.parent = parent;
    }

    /**
     * Create a VerseRange that is the stuff left of VerseRange a when you
     * remove the stuff in VerseRange b.
     * 
     * @param a
     *            Verses at the start or end of b
     * @param b
     *            All the verses
     * @return A list of the Verses outstanding
     */
    public static VerseRange[] remainder(VerseRange a, VerseRange b) {
        VerseRange rstart = null;
        VerseRange rend = null;

        Versification v11n = a.getVersification();

        // If a starts before b get the Range of the prequel
        if (v11n.distance(a.getStart(), b.getStart()) > 0) {
            rstart = new VerseRange(v11n, a.getStart(), v11n.subtract(b.getEnd(), 1));
        }

        // If a ends after b get the Range of the sequel
        if (v11n.distance(a.getEnd(), b.getEnd()) < 0) {
            rend = new VerseRange(v11n, v11n.add(b.getEnd(), 1), a.getEnd());
        }

        if (rstart == null) {
            if (rend == null) {
                return new VerseRange[] {};
            }
            return new VerseRange[] {
                rend
            };
        }

        if (rend == null) {
            return new VerseRange[] {
                rstart
            };
        }
        return new VerseRange[] {
                rstart, rend
        };
    }

    /**
     * Create a VerseRange that is the stuff in VerseRange a that is also
     * in VerseRange b.
     * 
     * @param a
     *            The verses that you might want
     * @param b
     *            The verses that you definitely don't
     * @return A list of the Verses outstanding
     */
    public static VerseRange intersection(VerseRange a, VerseRange b) {
        Versification v11n = a.getVersification();
        Verse new_start = v11n.max(a.getStart(), b.getStart());
        Verse new_end = v11n.min(a.getEnd(), b.getEnd());

        if (v11n.distance(new_start, new_end) <= 0) {
            return new VerseRange(a.getVersification(), new_start, new_end);
        }

        return null;
    }

    private String doGetName(Key base) throws NoSuchVerseException {
        // Cache these we're going to be using them a lot.
        BibleBook startBook = start.getBook();
        int startChapter = start.getChapter();
        int startVerse = start.getVerse();
        BibleBook endBook = end.getBook();
        int endChapter = end.getChapter();
        int endVerse = end.getVerse();

        // If this is in 2 separate books
        if (startBook != endBook) {
            // This range is exactly a whole book
            if (isWholeBooks()) {
                // Just report the name of the book, we don't need to worry
                // about the base since we start at the start of a book,
                // and should have been recently normalized()
                return v11n.getPreferredName(startBook) + VerseRange.RANGE_PREF_DELIM + v11n.getPreferredName(endBook);
            }

            // If this range is exactly a whole chapter
            if (isWholeChapters()) {
                // Just report book and chapter names
                return v11n.getPreferredName(startBook) + Verse.VERSE_PREF_DELIM1 + startChapter + VerseRange.RANGE_PREF_DELIM
                        + v11n.getPreferredName(endBook) + Verse.VERSE_PREF_DELIM1 + endChapter;
            }

            if (v11n.isChapterIntro(start)) {
                return v11n.getPreferredName(startBook) + Verse.VERSE_PREF_DELIM1 + startChapter + VerseRange.RANGE_PREF_DELIM  + end.getName(base);
            }
            if (v11n.isBookIntro(start)) {
                return v11n.getPreferredName(startBook) + VerseRange.RANGE_PREF_DELIM + end.getName(base);
            }
            return start.getName(base) + VerseRange.RANGE_PREF_DELIM + end.getName(base);
        }

        // This range is exactly a whole book
        if (isWholeBook()) {
            // Just report the name of the book, we don't need to worry about
            // the
            // base since we start at the start of a book, and should have been
            // recently normalized()
            return v11n.getPreferredName(startBook);
        }

        // If this is 2 separate chapters
        if (startChapter != endChapter) {
            // If this range is a whole number of chapters
            if (isWholeChapters()) {
                // Just report the name of the book and the chapters
                return v11n.getPreferredName(startBook) + Verse.VERSE_PREF_DELIM1 + startChapter + VerseRange.RANGE_PREF_DELIM + endChapter;
            }

            return start.getName(base) + VerseRange.RANGE_PREF_DELIM + endChapter + Verse.VERSE_PREF_DELIM2 + endVerse;
        }

        // If this range is exactly a whole chapter
        if (isWholeChapter()) {
            // Just report the name of the book and the chapter
            return v11n.getPreferredName(startBook) + Verse.VERSE_PREF_DELIM1 + startChapter;
        }

        // If this is 2 separate verses
        if (startVerse != endVerse) {
            return start.getName(base) + VerseRange.RANGE_PREF_DELIM + endVerse;
        }

        // The range is a single verse
        return start.getName(base);
    }

    /**
     * Calculate the last verse in this range.
     * 
     * @return The last verse in the range
     */
    private Verse calcEnd() {
        if (verseCount == 1) {
            return start;
        }
        return v11n.add(start, verseCount - 1);
    }

    /**
     * Calculate how many verses in this range
     * 
     * @return The number of verses. Always >= 1.
     */
    private int calcVerseCount() {
        return v11n.distance(start, end) + 1;
    }

    /**
     * Check to see that everything is ok with the Data
     */
    private void verifyData() {
        assert verseCount == calcVerseCount() : "start=" + start + ", end=" + end + ", verseCount=" + verseCount;
    }

    /**
     * Write out the object to the given ObjectOutputStream
     * 
     * @param out
     *            The stream to write our state to
     * @throws IOException
     *             If the write fails
     * @serialData Write the ordinal number of this verse
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        // Ignore the original name. Is this wise?
        // I am expecting that people are not that fussed about it and
        // it could make everything far more verbose
    }

    /**
     * Write out the object to the given ObjectOutputStream
     * 
     * @param in
     *            The stream to read our state from
     * @throws IOException
     *             If the write fails
     * @throws ClassNotFoundException
     *             If the read data is incorrect
     * @serialData Write the ordinal number of this verse
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        end = calcEnd();
        shaper = new NumberShaper();

        verifyData();

        // We are ignoring the originalName and parent.
    }

    /**
     * Iterate over the Verses in the VerseRange
     */
    private static final class VerseIterator implements Iterator<Key> {
        /**
         * Ctor
         */
        protected VerseIterator(VerseRange range) {
            v11n = range.getVersification();
            nextVerse = range.getStart();
            total = range.getCardinality();
            count = 0;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return nextVerse != null;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        public Key next() throws NoSuchElementException {
            if (nextVerse == null) {
                throw new NoSuchElementException();
            }
            Verse currentVerse = nextVerse;
            nextVerse = ++count < total ? v11n.next(nextVerse) : null;
            return currentVerse;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        private Versification v11n;
        private Verse nextVerse;
        private int count;
        private int total;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#canHaveChildren()
     */
    public boolean canHaveChildren() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getChildCount()
     */
    public int getChildCount() {
        return 0;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getCardinality()
     */
    public int getCardinality() {
        return verseCount;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#isEmpty()
     */
    public boolean isEmpty() {
        return verseCount == 0;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#contains(org.crosswire.jsword.passage.Key)
     */
    public boolean contains(Key key) {
        if (key instanceof VerseRange) {
            return contains((VerseRange) key);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#iterator()
     */
    public Iterator<Key> iterator() {
        return new VerseIterator(this);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#addAll(org.crosswire.jsword.passage.Key)
     */
    public void addAll(Key key) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#removeAll(org.crosswire.jsword.passage.Key)
     */
    public void removeAll(Key key) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#retainAll(org.crosswire.jsword.passage.Key)
     */
    public void retainAll(Key key) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#clear()
     */
    public void clear() {
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#get(int)
     */
    public Key get(int index) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#indexOf(org.crosswire.jsword.passage.Key)
     */
    public int indexOf(Key that) {
        return -1;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#blur(int, org.crosswire.jsword.passage.RestrictionType)
     */
    public void blur(int by, RestrictionType restrict) {
        VerseRange newRange = restrict.blur(v11n, this, by, by);
        start = newRange.start;
        end = newRange.end;
        verseCount = newRange.verseCount;
    }

    /**
     * What characters can we use to separate the 2 parts to a VerseRanges
     */
    public static final String RANGE_ALLOWED_DELIMS = "-";

    /**
     * What characters should we use to separate VerseRange parts on output
     */
    public static final String RANGE_PREF_DELIM = RANGE_ALLOWED_DELIMS;

    /**
     * To make serialization work across new versions
     */
    static final long serialVersionUID = 8307795549869653580L;

    /**
     * The Versification with which this range is defined.
     */
    private transient Versification v11n;

    /**
     * The start of the range
     */
    private Verse start;

    /**
     * The number of verses in the range
     */
    private int verseCount;

    /**
     * The last verse. Not actually needed, since it can be computed.
     */
    private transient Verse end;

    /**
     * Allow the conversion to and from other number representations.
     */
    private transient NumberShaper shaper;

    /**
     * The parent key.
     */
    private transient Key parent;

    /**
     * The original string for picky users
     */
    private transient String originalName;

    /**
     * The log stream
     */
    /* pkg protected */static final transient Logger log = Logger.getLogger(VerseRange.class);

}
