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
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.versification.BibleInfo;

/**
 * A VerseRange is one step between a Verse and a Passage - it is a
 * Verse plus a verseCount. Every VerseRange has a start, a verseCount
 * and an end. A VerseRange is designed to be immutable. This is a
 * necessary from a collections point of view. A VerseRange should always
 * be valid, although some versions may not return any text for verses
 * that they consider to be mis-translated in some way.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public final class VerseRange implements Key, Serializable
{
    /**
     * The default VerseRange is a single verse - Genesis 1:1. I didn't
     * want to provide this constructor however, you are supposed to
     * provide a default ctor for all beans. For this reason I suggest you
     * don't use it.
     */
    public VerseRange()
    {
        this(Verse.DEFAULT);
    }

    /**
     * Construct a VerseRange from a Verse. The resultant VerseRange will be
     * 1 verse in verseCount.
     * @param start The verse to start from
     */
    public VerseRange(Verse start)
    {
        this(start, start);
    }

    public VerseRange(Verse start, Verse end)
    {
        this(null, start, end);
    }

    /**
     * Construct a VerseRange from 2 Verses
     * If start is later than end then swap the two around.
     * This constructor is deliberately package protected so that is
     * used only by VerseFactory.
     * @param start The verse to start from
     * @param end The verse to end with
     */
    /*package*/ VerseRange(String original, Verse start, Verse end)
    {
        assert start != null;
        assert end != null;

        this.originalName = original;

        switch (start.compareTo(end))
        {
        case -1:
            this.start = start;
            this.end = end;
            this.verseCount = calcVerseCount(start, end);
            break;

        case 0:
            this.start = start;
            this.end = start;
            this.verseCount = 1;
            break;

        case 1:
            this.start = end;
            this.end = start;
            this.verseCount = calcVerseCount(this.start, this.end);
            break;

        default:
            assert false;
        }

        verifyData();
    }

    /**
     * Merge 2 VerseRanges together. The resulting range will encompass
     * Everying in-between the extremities of the 2 ranges.
     * @param a The first verse range to be merged
     * @param b The second verse range to be merged
     */
    public VerseRange(VerseRange a, VerseRange b)
    {
        start = Verse.min(a.getStart(), b.getStart());
        end = Verse.max(a.getEnd(), b.getEnd());
        verseCount = calcVerseCount(start, end);
    }

    /**
     * Fetch a more sensible shortened version of the name
     * @return A string like 'Gen 1:1-2'
     */
    public String getName()
    {
        return getName(null);
    }

    /**
     * Fetch a more sensible shortened version of the name
     * @param base A reference to allow things like Gen 1:1,3,5 as an output
     * @return A string like 'Gen 1:1-2'
     */
    public String getName(Key base)
    {
        if (PassageUtil.isPersistentNaming() && originalName != null)
        {
            return originalName;
        }

        // Cache these we're going to be using them a lot.
        int startBook = start.getBook();
        int startChapter = start.getChapter();
        int startVerse = start.getVerse();
        int endBook = end.getBook();
        int endChapter = end.getChapter();
        int endVerse = end.getVerse();

        try
        {
            // If this is in 2 separate books
            if (startBook != endBook)
            {
                // This range is exactly a whole book
                if (isWholeBooks())
                {
                    // Just report the name of the book, we don't need to worry about the
                    // base since we start at the start of a book, and should have been
                    // recently normalized()
                    return BibleInfo.getBookName(startBook)
                         + VerseRange.RANGE_PREF_DELIM
                         + BibleInfo.getBookName(endBook);
                }

                // If this range is exactly a whole chapter
                if (isWholeChapters())
                {
                    // Just report book and chapter names
                    return BibleInfo.getBookName(startBook)
                         + Verse.VERSE_PREF_DELIM1 + startChapter
                         + VerseRange.RANGE_PREF_DELIM + BibleInfo.getBookName(endBook)
                         + Verse.VERSE_PREF_DELIM1 + endChapter;
                }

                return start.getName(base) + VerseRange.RANGE_PREF_DELIM + end.getName(base);
            }

            // This range is exactly a whole book
            if (isWholeBook())
            {
                // Just report the name of the book, we don't need to worry about the
                // base since we start at the start of a book, and should have been
                // recently normalized()
                return BibleInfo.getBookName(startBook);
            }

            // If this is 2 separate chapters
            if (startChapter != endChapter)
            {
                // If this range is a whole number of chapters
                if (isWholeChapters())
                {
                    // Just report the name of the book and the chapters
                    return BibleInfo.getBookName(startBook)
                         + Verse.VERSE_PREF_DELIM1 + startChapter
                         + VerseRange.RANGE_PREF_DELIM + endChapter;
                }

                return start.getName(base)
                     + VerseRange.RANGE_PREF_DELIM + endChapter
                     + Verse.VERSE_PREF_DELIM2 + endVerse;
            }

            // If this range is exactly a whole chapter
            if (isWholeChapter())
            {
                // Just report the name of the book and the chapter
                return BibleInfo.getBookName(startBook)
                     + Verse.VERSE_PREF_DELIM1 + startChapter;
            }

            // If this is 2 separate verses
            if (startVerse != endVerse)
            {
                return start.getName(base)
                     + VerseRange.RANGE_PREF_DELIM + endVerse;
            }

            // The range is a single verse
            return start.getName(base);
        }
        catch (NoSuchVerseException ex)
        {
            assert false : ex;
            return "!Error!"; //$NON-NLS-1$
        }
    }

    /**
     * The OSIS defined reference specification for this VerseRange.
     * Uses osis books names, with "." as a verse part separator.
     * @return a String containing the OSIS description of the verses
     */
    public String getOsisRef()
    {
        try
        {
            // If this is in 2 separate books
            int startBook = start.getBook();
            int endBook = end.getBook();
            if (startBook != endBook)
            {
                return start.getOsisRef() + VerseRange.RANGE_PREF_DELIM + end.getOsisRef();
            }

            // This range is exactly a whole book
            if (isWholeBook())
            {
                // Just report the name of the book, we don't need to worry about the
                // base since we start at the start of a book, and should have been
                // recently normalized()
                return BibleInfo.getOSISName(startBook);
            }

            // If this is 2 separate chapters
            int startChapter = start.getChapter();
            int endChapter = end.getChapter();
            if (startChapter != endChapter)
            {
                return start.getOsisRef() + VerseRange.RANGE_PREF_DELIM + end.getOsisRef();
            }

            // If this range is exactly a whole chapter
            if (isWholeChapter())
            {
                // Just report the name of the book and the chapter
                return BibleInfo.getOSISName(startBook) + Verse.VERSE_OSIS_DELIM + startChapter;
            }

            // If this is 2 separate verses
            if (start.getVerse() != end.getVerse())
            {
                return start.getOsisRef() + VerseRange.RANGE_PREF_DELIM + end.getOsisRef();
            }

            // The range is a single verse
            return start.getOsisRef();
        }
        catch (NoSuchVerseException ex)
        {
            assert false : ex;
            return "!Error!"; //$NON-NLS-1$
        }
    }

    /**
     * The OSIS defined id specification for this VerseRange.
     * Uses osis books names, with "." as a verse part separator.
     * Each verse is separated by a space.
     * @return a String containing the OSIS description of the verses
     */
    public String getOsisID()
    {
        try
        {
            // This range is exactly a whole book
            if (isWholeBook())
            {
                // Just report the name of the book, we don't need to worry about the
                // base since we start at the start of a book, and should have been
                // recently normalized()
                return BibleInfo.getOSISName(start.getBook());
            }

            // If this range is exactly a whole chapter
            if (isWholeChapter())
            {
                // Just report the name of the book and the chapter
                return BibleInfo.getOSISName(start.getBook()) + Verse.VERSE_OSIS_DELIM + start.getChapter();
            }
        }
        catch (NoSuchVerseException ex)
        {
            assert false : ex;
            return "!Error!"; //$NON-NLS-1$
        }

        int startOrdinal = start.getOrdinal();
        int endOrdinal = end.getOrdinal();

        // TODO(DM): could analyze each book and chapter in the range
        // to see if it is wholly contained in the range and output it if it is.

        // Estimate the size of the buffer: book.dd.dd (where book is 3-5, 3 typical)
        StringBuffer buf = new StringBuffer((endOrdinal - startOrdinal + 1) * 10);
        buf.append(start.getOsisID());
        for (int i = startOrdinal; i < endOrdinal; i++)
        {
            try
            {
                buf.append(AbstractPassage.REF_OSIS_DELIM);
                buf.append(new Verse(i).getOsisID());
            }
            catch (NoSuchVerseException e)
            {
                assert false : e;
            }
        }

        // It just might be a single verse range!
        if (startOrdinal != endOrdinal)
        {
            buf.append(AbstractPassage.REF_OSIS_DELIM);
            buf.append(end.getOsisID());
        }

        return buf.toString();
    }

    /**
     * This just clones getName which seems the most sensible
     * type of string to return.
     * @return A string like 'Gen 1:1-2'
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Fetch the first verse in this range.
     * @return The first verse in the range
     */
    public Verse getStart()
    {
        return start;
    }

    /**
     * Fetch the last verse in this range.
     * @return The last verse in the range
     */
    public Verse getEnd()
    {
        return end;
    }

    /**
     * How many verses in this range
     * @return The number of verses. Always >= 1.
     */
    public int getVerseCount()
    {
        return verseCount;
    }

    /**
     * How many chapters in this range
     * @return The number of chapters. Always >= 1.
     */
    public int getChapterCount()
    {
        int startBook = start.getBook();
        int startChap = start.getChapter();
        int endBook = end.getBook();
        int endChap = end.getChapter();

        if (startBook == endBook)
        {
            return endChap - startChap + 1;
        }

        try
        {
            // So we are going to have to count up chapters from start to end
            int total = BibleInfo.chaptersInBook(startBook) - startChap;
            for (int b = startBook + 1; b < endBook; b++)
            {
                total += BibleInfo.chaptersInBook(b);
            }
            total += endChap;

            return total;
        }
        catch (NoSuchVerseException ex)
        {
            assert false : ex;
            return 1;
        }
    }

    /**
     * How many books in this range
     * @return The number of books. Always >= 1.
     */
    public int getBookCount()
    {
        int startBook = start.getBook();
        int endBook = end.getBook();

        return endBook - startBook + 1;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    public Object clone()
    {
        // This gets us a shallow copy
        VerseRange copy = null;
        try
        {
            copy = (VerseRange) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            assert false : e;
        }

        copy.start = (Verse) start.clone();
        copy.end = (Verse) end.clone();
        copy.verseCount = verseCount;
        copy.originalName = originalName;

        return copy;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        // Since this can not be null
        if (obj == null)
        {
            return false;
        }

        // Check that that is the same as this
        // Don't use instanceOf since that breaks inheritance
        if (!obj.getClass().equals(this.getClass()))
        {
            return false;
        }

        VerseRange vr = (VerseRange) obj;

        // The real tests
        if (!vr.getStart().equals(getStart()))
        {
            return false;
        }

        if (vr.getVerseCount() != getVerseCount())
        {
            return false;
        }

        // We don't really need to check this one too.
        //if (!vr.getEnd().equals(getEnd())) return false;

        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return (start.getOrdinal() << 16) + verseCount;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object obj)
    {
        // This ensures a ClassCastException without further test
        Verse that = null;
        if (obj instanceof Verse)
        {
            that = (Verse) obj;
        }
        else
        {
            that = ((VerseRange) obj).getStart();
        }

        int start_compare = getStart().compareTo(that);
        if (start_compare != 0)
        {
            return start_compare;
        }

        // So the start verses are the same, but the Verse(Range)s may not
        // be equal() since they have lengths
        int that_length = 1;
        if (obj instanceof VerseRange)
        {
            that_length = ((VerseRange) obj).getVerseCount();
        }

        if (that_length == getVerseCount())
        {
            return 0;
        }

        if (that_length < getVerseCount())
        {
            return 1;
        }

        return -1;
    }

    /**
     * Are the 2 VerseRanges in question contigious.
     * ie - could they be represented by a single VerseRange. Note that one
     * range could be entirely contained within the other and they would be
     * considered adjacentTo()
     * For example Gen 1:1-2 is adjacent to Gen 1:1-5 and Gen 1:3-4 but
     * not to Gen 1:4-10. Also Gen 1:29-30 is adjacent to Gen 2:1-10
     * @param that The VerseRange to compare to
     * @return true if the ranges are adjacent
     */
    public boolean adjacentTo(VerseRange that)
    {
        int thatStart = that.getStart().getOrdinal();
        int thatEnd = that.getEnd().getOrdinal();
        int thisStart = getStart().getOrdinal();
        int thisEnd = getEnd().getOrdinal();

        // if that starts inside or is next to this we are adjacent.
        if (thatStart >= thisStart - 1 && thatStart <= thisEnd + 1)
        {
            return true;
        }

        // if this starts inside or is next to that we are adjacent.
        if (thisStart >= thatStart - 1 && thisStart <= thatEnd + 1)
        {
            return true;
        }

        // otherwise we're not adjacent
        return false;
    }

    /**
     * Do the 2 VerseRanges in question actually overlap. This is slightly
     * more restrictive than the adjacentTo() test which could be satisfied by
     * ranges like Gen 1:1-2 and Gen 1:3-4. overlaps() however would return
     * false given these ranges.
     * For example Gen 1:1-2 is adjacent to Gen 1:1-5 but not to Gen 1:3-4
     * not to Gen 1:4-10. Also Gen 1:29-30 does not overlap Gen 2:1-10
     * @param that The VerseRange to compare to
     * @return true if the ranges are adjacent
     */
    public boolean overlaps(VerseRange that)
    {
        int thatStart = that.getStart().getOrdinal();
        int thatEnd = that.getEnd().getOrdinal();
        int thisStart = getStart().getOrdinal();
        int thisEnd = getEnd().getOrdinal();

        // if that starts inside this we are adjacent.
        if (thatStart >= thisStart && thatStart <= thisEnd)
        {
            return true;
        }

        // if this starts inside that we are adjacent.
        if (thisStart >= thatStart && thisStart <= thatEnd)
        {
            return true;
        }

        // otherwise we're not adjacent
        return false;
    }

    /**
     * Is the given verse entirely within our range.
     * For example if this = "Gen 1:1-31" then:
     * <tt>contains(Verse("Gen 1:3")) == true</tt>
     * <tt>contains(Verse("Gen 2:1")) == false</tt>
     * @param that The Verse to compare to
     * @return true if we contain it.
     */
    public boolean contains(Verse that)
    {
        if (start.compareTo(that) == 1)
        {
            return false;
        }

        if (end.compareTo(that) == -1)
        {
            return false;
        }

        return true;
    }

    /**
     * Is the given range within our range.
     * For example if this = "Gen 1:1-31" then:
     * <tt>this.contains(Verse("Gen 1:3-10")) == true</tt>
     * <tt>this.contains(Verse("Gen 2:1-1")) == false</tt>
     * @param that The Verse to compare to
     * @return true if we contain it.
     */
    public boolean contains(VerseRange that)
    {
        if (start.compareTo(that.getStart()) == 1)
        {
            return false;
        }

        if (end.compareTo(that.getEnd()) == -1)
        {
            return false;
        }

        return true;
    }

    /**
     * Does this range represent exactly one chapter, no more or less.
     * @return true if we are exactly one chapter.
     */
    public boolean isWholeChapter()
    {
        if (!start.isStartOfChapter())
        {
            return false;
        }

        if (!end.isEndOfChapter())
        {
            return false;
        }

        if (!start.isSameChapter(end))
        {
            return false;
        }

        return true;
    }

    /**
     * Does this range represent a number of whole chapters
     * @return true if we are a whole number of chapters.
     */
    public boolean isWholeChapters()
    {
        if (!start.isStartOfChapter())
        {
            return false;
        }

        if (!end.isEndOfChapter())
        {
            return false;
        }

        return true;
    }

    /**
     * Does this range represent exactly one book, no more or less.
     * @return true if we are exactly one book.
     */
    public boolean isWholeBook()
    {
        if (!start.isStartOfBook())
        {
            return false;
        }

        if (!end.isEndOfBook())
        {
            return false;
        }

        if (!start.isSameBook(end))
        {
            return false;
        }

        return true;
    }

    /**
     * Does this range represent a whole number of books.
     * @return true if we are a whole number of books.
     */
    public boolean isWholeBooks()
    {
        if (!start.isStartOfBook())
        {
            return false;
        }

        if (!end.isEndOfBook())
        {
            return false;
        }

        return true;
    }

    /**
     * Does this range occupy more than one book;
     * @return true if we occupy 2 or more books
     */
    public boolean isMultipleBooks()
    {
        return start.getBook() != end.getBook();
    }

    /**
     * Create an array of Verses
     * @return The array of verses that this makes up
     */
    public Verse[] toVerseArray()
    {
        try
        {
            Verse[] retcode = new Verse[verseCount];

            for (int i = 0; i < verseCount; i++)
            {
                retcode[i] = new Verse(start.getOrdinal() + i);
            }

            return retcode;
        }
        catch (NoSuchVerseException ex)
        {
            assert false : ex;
            return new Verse[0];
        }
    }

    /**
     * Enumerate the subranges in this range
     * @return a range iterator
     */
    public Iterator rangeIterator(RestrictionType restrict)
    {
        return new AbstractPassage.VerseRangeIterator(iterator(), restrict);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getParent()
     */
    public Key getParent()
    {
        return parent;
    }

    /**
     * Set a parent Key. This allows us to follow the Key interface more
     * closely, although the concept of a parent for a verse is fairly
     * alien.
     * @param parent The parent Key for this verse
     */
    public void setParent(Key parent)
    {
        this.parent = parent;
    }

    /**
     * Create a DistinctPassage that is the stuff left of VerseRange a
     * when you remove the stuff in VerseRange b.
     * @param a The verses that you might want
     * @param b The verses that you definately don't
     * @return A list of the Verses outstanding
     */
    public static VerseRange[] remainder(VerseRange a, VerseRange b)
    {
        VerseRange rstart = null;
        VerseRange rend = null;

        // If a starts before b get the Range of the prequel
        if (a.getStart().compareTo(b.getStart()) == -1)
        {
            rstart = new VerseRange(a.getStart(), b.getEnd().subtract(1));
        }

        // If a ends after b get the Range of the sequel
        if (a.getEnd().compareTo(b.getEnd()) == 1)
        {
            rend = new VerseRange(b.getEnd().add(1), a.getEnd());
        }

        if (rstart == null)
        {
            if (rend == null)
            {
                return new VerseRange[] { };
            }
            return new VerseRange[] { rend };
        }

        if (rend == null)
        {
            return new VerseRange[] { rstart };
        }
        return new VerseRange[] { rstart, rend };
    }

    /**
     * Create a DistinctPassage that is the stuff in VerseRange a
     * that is also in VerseRange b.
     * @param a The verses that you might want
     * @param b The verses that you definately don't
     * @return A list of the Verses outstanding
     */
    public static VerseRange intersection(VerseRange a, VerseRange b)
    {
        Verse new_start = Verse.max(a.getStart(), b.getStart());
        Verse new_end = Verse.min(a.getEnd(), b.getEnd());

        if (new_start.compareTo(new_end) < 1)
        {
            return new VerseRange(new_start, new_end);
        }

        return null;
    }

    /**
     * Returns a VerseRange that wraps the whole Bible
     * @return The whole bible VerseRange
     */
    public static VerseRange getWholeBibleVerseRange()
    {
        try
        {
            if (whole == null)
            {
                whole = new VerseRange(new Verse(1, 1, 1), new Verse(66, 22, 21));
            }
        }
        catch (NoSuchVerseException ex)
        {
            assert false : ex;
            return new VerseRange();
        }

        return whole;
    }

    /**
     * Calculate the last verse in this range.
     * @param start The first verse in the range
     * @param verseCount The number of verses
     * @return The last verse in the range
     */
    private static Verse calcEnd(Verse start, int verseCount)
    {
        return start.add(verseCount - 1);
    }

    /**
     * Calcualte how many verses in this range
     * @param start The first verse in the range
     * @param end The last verse in the range
     * @return The number of verses. Always >= 1.
     */
    private static int calcVerseCount(Verse start, Verse end)
    {
        return end.subtract(start) + 1;
    }

    /**
     * Check to see that everything is ok with the Data
     */
    private void verifyData()
    {
        assert verseCount == end.subtract(start) + 1 : "start=" + start + ", end=" + end + ", verseCount=" + verseCount; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    /**
     * Write out the object to the given ObjectOutputStream
     * @param out The stream to write our state to
     * @throws IOException If the write fails
     * @serialData Write the ordinal number of this verse
     */
    private void writeObject(ObjectOutputStream out) throws IOException
    {
        // Call even if there is no default serializable fields.
        out.defaultWriteObject();

        out.writeInt(start.getOrdinal());
        out.writeInt(verseCount);

        // Ignore the original name. Is this wise?
        // I am expecting that people are not that fussed about it and
        // it could make everything far more verbose
    }

    /**
     * Write out the object to the given ObjectOutputStream
     * @param in The stream to read our state from
     * @throws IOException If the write fails
     * @throws ClassNotFoundException If the read data is incorrect
     * @serialData Write the ordinal number of this verse
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        // Call even if there is no default serializable fields.
        in.defaultReadObject();

        try
        {
            start = new Verse(in.readInt());
            verseCount = in.readInt();
            end = calcEnd(start, verseCount);

            verifyData();
        }
        catch (NoSuchVerseException ex)
        {
            throw new IOException(ex.getMessage());
        }

        // We are ignoring the originalName. It was set to null in the
        // default ctor so I will ignore it here.
    }

    /**
     * Iterate over the Verses in the VerseRange
     */
    private static final class VerseIterator implements Iterator
    {
        /**
         * Ctor
         */
        protected VerseIterator(VerseRange range)
        {
            next = range.getStart().getOrdinal();
            last = range.getEnd().getOrdinal();
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext()
        {
            return next <= last;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        public Object next() throws NoSuchElementException
        {
            if (next > last)
            {
                throw new NoSuchElementException();
            }

            try
            {
                return new Verse(next++);
            }
            catch (NoSuchVerseException ex)
            {
                assert false : ex;
                return Verse.DEFAULT;
            }
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        public void remove() throws UnsupportedOperationException
        {
            throw new UnsupportedOperationException();
        }

        private int next;
        private int last;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#canHaveChildren()
     */
    public boolean canHaveChildren()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#size()
     */
    public int getChildCount()
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#isEmpty()
     */
    public boolean isEmpty()
    {
        return true;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#contains(org.crosswire.jsword.passage.Key)
     */
    public boolean contains(Key key)
    {
        if (key instanceof VerseRange)
        {
            return contains((VerseRange) key);
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#iterator()
     */
    public Iterator iterator()
    {
        return new VerseIterator(this);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#add(org.crosswire.jsword.passage.Key)
     */
    public void addAll(Key key)
    {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#remove(org.crosswire.jsword.passage.Key)
     */
    public void removeAll(Key key)
    {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#retain(org.crosswire.jsword.passage.Key)
     */
    public void retainAll(Key key)
    {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#clear()
     */
    public void clear()
    {
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#get(int)
     */
    public Key get(int index)
    {
        return null;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#indexOf(org.crosswire.jsword.passage.Key)
     */
    public int indexOf(Key that)
    {
        return -1;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#blur(int)
     */
    public void blur(int by, RestrictionType restrict)
    {
        VerseRange newRange = restrict.blur(this, by, by);
        start = newRange.start;
        end = newRange.end;
        verseCount = newRange.verseCount;
    }

    /**
     * What characters can we use to separate the 2 parts to a VerseRanges
     */
    public static final String RANGE_ALLOWED_DELIMS = "-"; //$NON-NLS-1$

    /**
     * What characters should we use to separate VerseRange parts on output
     */
    public static final String RANGE_PREF_DELIM = RANGE_ALLOWED_DELIMS;

    /**
     * To make serialization work across new versions
     */
    static final long serialVersionUID = 8307795549869653580L;

    /**
     * The real data - how many verses long are we?.
     * All ctors init this so leave default
     */
    private transient int verseCount;

    /**
     * The real data - where do we start?.
     * All ctors init this so leave default
     */
    private transient Verse start;

    /**
     * The real data - where do we end?.
     * All ctors init this so leave default
     */
    private transient Verse end;

    /**
     * The parent key. See the key interface for more information.
     * NOTE(joe): These keys are not serialized, should we?
     * @see Key
     */
    private transient Key parent;

    /**
     * The original string for picky users
     */
    private transient String originalName;

    /**
     * The whole Bible VerseRange
     */
    private static transient VerseRange whole;

    /**
     * The log stream
     */
    /* pkg protected */ static final transient Logger log = Logger.getLogger(VerseRange.class);

}
