
package org.crosswire.jsword.passage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.LogicError;

/**
 * A VerseRange is one step between a Verse and a Passage - it is a
 * Verse plus a verse_count. Every VerseRange has a start, a verse_count
 * and an end. A VerseRange is designed to be immutable. This is a
 * necessary from a collections point of view. A VerseRange should always
 * be valid, although some versions may not return any text for verses
 * that they consider to be mis-translated in some way.
 * 
 * NOTE(joe): make chapter ranges prefixed by book to separate from v-ranges
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class VerseRange implements VerseBase
{
    /**
     * The default VerseRange is a single verse - Genesis 1:1. I didn't
     * want to provide this constructor however, you are supposed to
     * provide a default ctor for all beans. For this reason I suggest you
     * don't use it.
     */
    public VerseRange()
    {
        this.original_name = null;
        this.start = Verse.DEFAULT;
        this.end = Verse.DEFAULT;
        this.verse_count = 1;

        verifyData();
    }

    /**
     * Construct a VerseRange from a human readable string. For example
     * "Gen 1:1-3" in case the user does not want to have their typing
     * 'fixed' by a meddling patronizing computer.
     * @param desc The textual representation
     * @exception NoSuchVerseException If the text can not be understood
     */
    public VerseRange(String desc) throws NoSuchVerseException
    {
        this(desc, Verse.DEFAULT);
    }

    /**
     * Construct a VerseRange from a String and a Verse. For example given
     * "2:2" and a basis of Gen 1:1 the result would be range of 1 verse
     * starting at Gen 2:2. Also given "2:2-5" and a basis of Gen 1:1 the
     * result would be a range of 5 verses starting at Gen 1:1.
     * @param desc The string describing the verse e.g "2:2"
     * @param basis The basis by which to understand the desc.
     * @exception NoSuchVerseException If the reference is illegal
     */
    public VerseRange(String desc, Verse basis) throws NoSuchVerseException
    {
        original_name = desc;

        // Do we need this?
        String[] parts = PassageUtil.tokenize(desc, RANGE_ALLOWED_DELIMS);

        switch (parts.length)
        {
        case 0:
            start = basis;
            verse_count = 1;
            end = calcEnd(start, verse_count);
            break;

        case 1:
            switch (Verse.getAccuracy(parts[0]))
            {
            case ACCURACY_BOOK_VERSE:
            case ACCURACY_CHAPTER_VERSE:
            case ACCURACY_VERSE_ONLY:
            case ACCURACY_NONE:
                start = new Verse(parts[0], basis);
                end = start;
                verse_count = 1;
                break;

            case ACCURACY_BOOK_CHAPTER:
                start = new Verse(parts[0], basis);
                verse_count = BibleInfo.versesInChapter(start.getBook(), start.getChapter());
                end = calcEnd(start, verse_count);
                break;

            case ACCURACY_BOOK_ONLY:
                start = new Verse(parts[0], basis);
                verse_count = BibleInfo.versesInBook(start.getBook());
                end = calcEnd(start, verse_count);
                break;

            default:
                throw new LogicError();
            }
            break;

        case 2:
            start = new Verse(parts[0], basis);
            switch (Verse.getAccuracy(parts[1]))
            {
            case ACCURACY_BOOK_VERSE:
            case ACCURACY_CHAPTER_VERSE:
            case ACCURACY_VERSE_ONLY:
            case ACCURACY_NONE:
                end = new Verse(parts[1], start);
                break;

            case ACCURACY_BOOK_CHAPTER:
            case ACCURACY_BOOK_ONLY:
                end = new VerseRange(parts[1], start).getEnd();
                break;

            default:
                throw new LogicError();
            }
            verse_count = calcVerseCount(start, end);
            break;

        default:
            throw new NoSuchVerseException(Msg.RANGE_PARTS, new Object[] { RANGE_ALLOWED_DELIMS, desc });
        }

        verifyData();
    }

    /**
     * Construct a VerseRange from a String and a VerseRange. For example given "2:2"
     * and a basis of Gen 1:1-2 the result would be range of 1 verse starting at
     * Gen 2:2. Also given "2:2-5" and a basis of Gen 1:1-2 the result would be a
     * range of 5 verses starting at Gen 1:1.
     * <p>This constructor is different from the (String, Verse) constructor in that
     * if the basis is a range that exactly covers a chapter and the string is a
     * single number, then we assume that the number referrs to a chapter and not to
     * a verse. This allows us to have a Passage like "Gen 1,2" and have the 2
     * understood as chapter 2 and not verse 2 of Gen 1, which would have occured
     * otherwise.
     * @param desc The string describing the verse e.g "2:2"
     * @param basis The verse that forms the basis by which to understand the desc.
     * @exception NoSuchVerseException If the reference is illegal
     */
    public VerseRange(String desc, VerseRange basis) throws NoSuchVerseException
    {
        original_name = desc;

        String[] parts = PassageUtil.tokenize(desc, RANGE_ALLOWED_DELIMS);

        switch (parts.length)
        {
        case 0:
            // This happens when someone types "Gen 1:1-" not sure what
            // someone would expect to happen as a result, maybe 1 verse,
            // maybe the whole chapter, maybe the whole book. I think that
            // we'd be silly to try to be too clever. So I'm assuming the
            // former. If you want either latter option use "Gen 1:1-$:$"
            start = basis.getStart();
            verse_count = 1;
            end = calcEnd(start, verse_count);
            break;

        case 1:
            // We need to cope with 2 special cases here:
            // o The first is of "Gen 1, 2": If there is 1 part to a verse,
            //   and the basis is a whole chapter then we are ACCURACY_CHAPTER
            // o The second is of "Mat, Mar": If there is 1 part to the verse
            //   and the basis is a whole book, then we are ACCURACY_BOOK
            //   However the second case is dealt with automatically because
            //   "Mar" can only be a book and never a chapter or verse
            switch (Verse.getAccuracy(parts[0]))
            {
            case ACCURACY_BOOK_VERSE:
                start = new Verse(parts[0], basis.getStart());
                end = start;
                verse_count = 1;
                break;

            case ACCURACY_VERSE_ONLY:
                if (basis.isChapter())
                {
                    // This should be ACCURACY_CHAPTER_ONLY if it existed
                    int book = basis.getStart().getBook();
                    int chapter = 0;
                    if (Verse.isEndMarker(parts[0]))
                    {
                        chapter = BibleInfo.chaptersInBook(book);
                    }
                    else
                    {
                        chapter = Verse.parseInt(parts[0]);
                    }

                    start = new Verse(book, chapter, 1);
                    end = new Verse(book, chapter, BibleInfo.versesInChapter(book, chapter));
                    verse_count = calcVerseCount(start, end);
                }
                else
                {
                    start = new Verse(parts[0], basis.getStart());
                    end = start;
                    verse_count = 1;
                }
                break;

            case ACCURACY_CHAPTER_VERSE:
            case ACCURACY_NONE:
                start = new Verse(parts[0], basis.getStart());
                end = start;
                verse_count = 1;
                break;

            case ACCURACY_BOOK_CHAPTER:
                start = new Verse(parts[0], basis.getStart());
                verse_count = BibleInfo.versesInChapter(start.getBook(), start.getChapter());
                end = calcEnd(start, verse_count);
                break;

            case ACCURACY_BOOK_ONLY:
                start = new Verse(parts[0], basis.getStart());
                verse_count = BibleInfo.versesInBook(start.getBook());
                end = calcEnd(start, verse_count);
                break;

            default:
                throw new LogicError();
            }
            break;

        case 2:
            start = new Verse(parts[0], basis.getStart());
            end = new Verse(parts[1], start);
            verse_count = calcVerseCount(start, end);
            break;

        default:
            throw new NoSuchVerseException(Msg.RANGE_PARTS, new Object[] { RANGE_ALLOWED_DELIMS, desc });
        }

        verifyData();
    }

    /**
     * Construct a VerseRange from a Verse. The resultant VerseRange will be
     * 1 verse in verse_count.
     * @param start The verse to start from
     */
    public VerseRange(Verse start)
    {
        if (start == null)
        {
            throw new NullPointerException();
        }

        this.original_name = null;
        this.start = start;
        this.end = start;
        this.verse_count = 1;

        verifyData();
    }

    /**
     * Construct a VerseRange from a Verse and a range.
     * @param start The verse to start from
     * @param verse_count The number of verses
     * @exception NoSuchVerseException If there arn't that many verses
     */
    public VerseRange(Verse start, int verse_count) throws NoSuchVerseException
    {
        if (verse_count < 1)
        {
            throw new NoSuchVerseException(Msg.RANGE_LOCOUNT);
        }

        if (start.getOrdinal()+verse_count-1 > BibleInfo.versesInBible())
        {
            Object[] params =
            {
                start.getName(),
                new Integer(BibleInfo.versesInBible()-start.getOrdinal()),
                new Integer(verse_count)
            };
            throw new NoSuchVerseException(Msg.RANGE_HICOUNT, params);
        }

        this.original_name = null;
        this.start = start;
        this.verse_count = verse_count;
        this.end = calcEnd(start, verse_count);

        verifyData();
    }

    /**
     * Construct a VerseRange from a Verse and a range.
     * Now the actual value of the boolean is ignored. However for future proofing
     * you should only use 'true'. Do not use patch_up=false, use Verse(int, int, int)
     * This so that we can declare this constructor to not throw an exception.
     * Is there a better way of doing this?
     * @param start The verse to start from
     * @param verse_count The number of verse to count
     * @param patch_up True to trigger reference fixing
     */
    public VerseRange(Verse start, int verse_count, boolean patch_up)
    {
        if (!patch_up)
        {
            throw new IllegalArgumentException(PassageUtil.getResource(Msg.ERROR_PATCH));
        }

        // Not sure that any of the code below (except verifyData() which may not stay there)
        // Checks for null so we do it explictly here.
        if (start == null)
        {
            throw new NullPointerException();
        }

        this.original_name = null;
        this.start = start;
        this.end = start.add(Math.max(verse_count, 1) - 1);
        this.verse_count = calcVerseCount(start, end);

        verifyData();
    }

    /**
     * Construct a VerseRange from 2 Verses
     * If start is later than end then swap the two around.
     * @param start The verse to start from
     * @param end The verse to end with
     */
    public VerseRange(Verse start, Verse end)
    {
        if (start == null || end == null)
            throw new NullPointerException();

        this.original_name = null;

        switch (start.compareTo(end))
        {
        case -1:
            this.start = start;
            this.end = end;
            this.verse_count = calcVerseCount(start, end);
            break;

        case 0:
            this.start = start;
            this.end = start;
            this.verse_count = 1;
            break;

        case 1:
            this.start = end;
            this.end = start;
            this.verse_count = calcVerseCount(this.start, this.end);
            break;

        default:
            throw new LogicError();
        }

        verifyData();
    }

    /**
     * Widen the range of the verses in this list. This is primarily for
     * "find x within n verses of y" type applications.
     * @param base_start The verse to start from
     * @param blur_down The number of verses to extend down by
     * @param blur_up The number of verses to extend up by
     * @param restrict How should we restrict the blurring?
     * @exception java.lang.IllegalArgumentException If a blurring is negative or the restrict mode is illegal
     * @see Passage
     */
    public VerseRange(Verse base_start, int blur_down, int blur_up, int restrict)
    {
        if (base_start == null)
        {
            throw new NullPointerException();
        }

        if (blur_down < 0 || blur_up < 0)
        {
            throw new IllegalArgumentException(PassageUtil.getResource(Msg.RANGE_BLURS));
        }

        this.original_name = null;

        switch (restrict)
        {
        case RESTRICT_CHAPTER:
            try
            {
                int start_book = base_start.getBook();
                int start_chapter = base_start.getChapter();
                int start_verse = base_start.getVerse() - blur_down;
                int end_verse = base_start.getVerse() + blur_up;

                start_verse = Math.max(start_verse, 1);
                end_verse = Math.min(end_verse, BibleInfo.versesInChapter(start_book, start_chapter));

                start = new Verse(start_book, start_chapter, start_verse);
                verse_count = end_verse - start_verse + 1;
                end = calcEnd(start, verse_count);
            }
            catch (NoSuchVerseException ex)
            {
                throw new LogicError(ex);
            }
            break;

        case RESTRICT_NONE:
            start = base_start.subtract(blur_down);
            end = base_start.add(blur_up);
            verse_count = calcVerseCount(start, end);
            break;

        case RESTRICT_BOOK:
            throw new IllegalArgumentException(PassageUtil.getResource(Msg.RANGE_BLURBOOK));

        default:
            throw new IllegalArgumentException(PassageUtil.getResource(Msg.RANGE_BLURNONE));
        }

        verifyData();
    }

    /**
     * Widen the range of the verses in this list. This is primarily for
     * "find x within n verses of y" type applications.
     * @param base_start The verse range to start from
     * @param blur_down The number of verses to extend down by
     * @param blur_up The number of verses to extend up by
     * @param restrict How should we restrict the blurring?
     * @exception java.lang.IllegalArgumentException If a blurring is negative or the restrict mode is illegal
     * @see Passage
     */
    public VerseRange(VerseRange base_start, int blur_down, int blur_up, int restrict)
    {
        if (base_start == null)
        {
            throw new NullPointerException();
        }

        if (blur_down < 0 || blur_up < 0)
        {
            throw new IllegalArgumentException(PassageUtil.getResource(Msg.RANGE_BLURS));
        }

        this.original_name = null;

        switch (restrict)
        {
        case RESTRICT_CHAPTER:
            try
            {

                int start_book = base_start.getStart().getBook();
                int start_chapter = base_start.getStart().getChapter();
                int start_verse = base_start.getStart().getVerse() - blur_down;

                int end_book = base_start.getEnd().getBook();
                int end_chapter = base_start.getEnd().getChapter();
                int end_verse = base_start.getEnd().getVerse() + blur_up;

                start_verse = Math.max(start_verse, 1);
                end_verse = Math.min(end_verse, BibleInfo.versesInChapter(end_book, end_chapter));

                start = new Verse(start_book, start_chapter, start_verse);
                end = new Verse(end_book, end_chapter, end_verse);
                verse_count = calcVerseCount(start, end);
            }
            catch (NoSuchVerseException ex)
            {
                throw new LogicError(ex);
            }
            break;

        case RESTRICT_NONE:
            start = base_start.getStart().subtract(blur_down);
            end = base_start.getEnd().add(blur_up);
            verse_count = calcVerseCount(start, end);
            break;

        case RESTRICT_BOOK:
            throw new IllegalArgumentException(PassageUtil.getResource(Msg.RANGE_BLURBOOK));

        default:
            throw new IllegalArgumentException(PassageUtil.getResource(Msg.RANGE_BLURNONE));
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
        original_name = null;
        start = Verse.min(a.getStart(), b.getStart());
        end = Verse.max(a.getEnd(), b.getEnd());
        verse_count = calcVerseCount(start, end);
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
    public String getName(Verse base)
    {
        if (PassageUtil.isPersistentNaming() && original_name != null)
        {
            return original_name;
        }

        // Cache these we're going to be using them a lot.
        int start_book = start.getBook();
        int start_chapter = start.getChapter();
        int start_verse = start.getVerse();
        int end_book = end.getBook();
        int end_chapter = end.getChapter();
        int end_verse = end.getVerse();

        try
        {
            // If this is in 2 separate books
            if (start_book != end_book)
            {
                // This range is exactly a whole book
                if (isBooks())
                {
                    // Just report the name of the book, we don't need to worry about the
                    // base since we start at the start of a book, and should have been
                    // recently normalized()
                    return BibleInfo.getShortBookName(start_book)
                         + RANGE_PREF_DELIM
                         + BibleInfo.getShortBookName(end_book);
                }

                // If this range is exactly a whole chapter
                if (isChapters())
                {
                    // Just report book and chapter names
                    return BibleInfo.getShortBookName(start_book)
                         + VERSE_PREF_DELIM1 + start_chapter
                         + RANGE_PREF_DELIM + BibleInfo.getShortBookName(end_book)
                         + VERSE_PREF_DELIM1 + end_chapter;
                }

                return start.getName(base) + RANGE_PREF_DELIM + end.getName(base);
            }

            // This range is exactly a whole book
            if (isBook())
            {
                // Just report the name of the book, we don't need to worry about the
                // base since we start at the start of a book, and should have been
                // recently normalized()
                return BibleInfo.getShortBookName(start_book);
            }

            // If this is 2 separate chapters
            if (start_chapter != end_chapter)
            {
                // If this range is a whole number of chapters
                if (isChapters())
                {
                    // Just report the name of the book and the chapters
                    return BibleInfo.getShortBookName(start_book)
                         + VERSE_PREF_DELIM1 + start_chapter
                         + RANGE_PREF_DELIM + end_chapter;
                }

                return start.getName(base)
                     + RANGE_PREF_DELIM + end_chapter
                     + VERSE_PREF_DELIM2 + end_verse;
            }

            // If this range is exactly a whole chapter
            if (isChapter())
            {
                // Just report the name of the book and the chapter
                return BibleInfo.getShortBookName(start_book)
                     + VERSE_PREF_DELIM1 + start_chapter;
            }

            // If this is 2 separate verses
            if (start_verse != end_verse)
            {
                return start.getName(base)
                     + RANGE_PREF_DELIM + end_verse;
            }

            // The range is a single verse
            return start.getName(base);
        }
        catch (NoSuchVerseException ex)
        {
            throw new LogicError(ex);
        }
    }

    /**
     * The OSIS defined specification for this VerseRange.
     * Uses short books names, with "." as a verse part separator.
     * @return a String containing the OSIS description of the verses
     */
    public String getOSISName()
    {
        // PENDING(joe): implement getOSISName() properly
        return getName();
    }

    /**
     * The OSIS defined specification for this VerseRange.
     * This method makes with the assumption that the specified Verse has just
     * been output, so if we are in the same book, we do not need to display the
     * book name, and so on.
     * @return a String containing the OSIS description of the verses
     */
    public String getOSISName(Verse base)
    {
        // PENDING(joe): implement getOSISName() properly
        return getName(base);
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
        return verse_count;
    }

    /**
     * Get a copy of ourselves. Points to note:
     *   Call clone() not new() on member Objects, and on us.
     *   Do not use Copy Constructors! - they do not inherit well.
     *   Think about this needing to be synchronized
     *   If this is not cloneable then writing cloneable children is harder
     * @return A complete copy of ourselves
     * @exception java.lang.CloneNotSupportedException We don't do this but our kids might
     */
    public Object clone() throws CloneNotSupportedException
    {
        // This gets us a shallow copy
        VerseRange copy = (VerseRange) super.clone();

        copy.start = (Verse) start.clone();
        copy.end = (Verse) end.clone();
        copy.verse_count = verse_count;
        copy.original_name = original_name;

        return copy;
    }

    /**
     * Is this Object equal to us. Points to note:
     *   If you override equals(), you must override hashCode() too.
     *   If you are doing this it is a good idea to be immutable.
     * @param obj The thing to test against
     * @return True/False is we are or are not equal to obj
     */
    public boolean equals(Object obj)
    {
        // Since this can not be null
        if (obj == null) return false;

        // Check that that is the same as this
        // Don't use instanceOf since that breaks inheritance
        if (!obj.getClass().equals(this.getClass())) return false;

        VerseRange vr = (VerseRange) obj;

        // The real tests
        if (!vr.getStart().equals(getStart())) return false;
        if (vr.getVerseCount() != getVerseCount()) return false;

        // We don't really need to check this one too.
        //if (!vr.getEnd().equals(getEnd())) return false;

        return true;
    }

    /**
     * The hashing number is currently calculated using the start ordinal
     * in the upper 16 bits, and the verse_count in the lower.
     * <p><b>Note that this may change and should not be relied upon</b>
     * Use getStart().getOrdinal() and so on to get that kind of info.
     * <p>The news from this however is that sorting by hashCode() is
     * currently the same as sorting using compareTo().
     * @return The hashing number
     */
    public int hashCode()
    {
        return (start.getOrdinal() << 16) + verse_count;
    }

    /**
     * Compare initially using the first element in a VerseRange. If the
     * starting verses are the same then sort according to length, shortest
     * first so:
     * <tt>Gen 1:1 &lt; Gen 1:1-2 &lt; Gen 1:1-26 &lt; Gen 1:2</tt>
     * <p>Note that this compares Verse("Gen 1:1") = VerseRange("Gen 1:1")
     * I'm not sure if this is 100% pucka, but it doesn't seem to cause any
     * problems.
     * @param obj The thing to compare against
     * @return 1 means he is earlier than me, -1 means he is later ...
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
        if (start_compare != 0) return start_compare;

        // So the start verses are the same, but the Verse(Range)s may not
        // be equal() since they have lengths
        int that_length = 1;
        if (obj instanceof VerseRange) that_length = ((VerseRange) obj).getVerseCount();

        if (that_length == getVerseCount()) return 0;
        if (that_length < getVerseCount()) return 1;
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
        int that_start = that.getStart().getOrdinal();
        int that_end = that.getEnd().getOrdinal();
        int this_start = getStart().getOrdinal();
        int this_end = getEnd().getOrdinal();

        // if that starts inside or is next to this we are adjacent.
        if (that_start >= this_start-1 && that_start <= this_end+1) return true;

        // if this starts inside or is next to that we are adjacent.
        if (this_start >= that_start-1 && this_start <= that_end+1) return true;

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
        int that_start = that.getStart().getOrdinal();
        int that_end = that.getEnd().getOrdinal();
        int this_start = getStart().getOrdinal();
        int this_end = getEnd().getOrdinal();

        // if that starts inside this we are adjacent.
        if (that_start >= this_start && that_start <= this_end) return true;

        // if this starts inside that we are adjacent.
        if (this_start >= that_start && this_start <= that_end) return true;

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
        if (start.compareTo(that) == 1) return false;
        if (end.compareTo(that) == -1) return false;

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
        if (start.compareTo(that.getStart()) == 1) return false;
        if (end.compareTo(that.getEnd()) == -1) return false;

        return true;
    }

    /**
     * Does this range represent exactly one chapter, no more or less.
     * @return true if we are exactly one chapter.
     */
    public boolean isChapter()
    {
        if (!start.isStartOfChapter()) return false;
        if (!end.isEndOfChapter()) return false;
        if (!start.isSameChapter(end)) return false;

        return true;
    }

    /**
     * Does this range represent a number of whole chapters
     * @return true if we are a whole number of chapters.
     */
    public boolean isChapters()
    {
        if (!start.isStartOfChapter()) return false;
        if (!end.isEndOfChapter()) return false;

        return true;
    }

    /**
     * Does this range represent exactly one book, no more or less.
     * @return true if we are exactly one book.
     */
    public boolean isBook()
    {
        if (!start.isStartOfBook()) return false;
        if (!end.isEndOfBook()) return false;
        if (!start.isSameBook(end)) return false;

        return true;
    }

    /**
     * Does this range represent a whole number of books.
     * @return true if we are a whole number of books.
     */
    public boolean isBooks()
    {
        if (!start.isStartOfBook()) return false;
        if (!end.isEndOfBook()) return false;

        return true;
    }

    /**
     * Create an array of Verses
     * @return The array of verses that this makes up
     */
    public Verse[] toVerseArray()
    {
        try
        {
            Verse[] retcode = new Verse[verse_count];

            for (int i=0; i<verse_count; i++)
            {
                retcode[i] = new Verse(start.getOrdinal()+i);
            }

            return retcode;
        }
        catch (NoSuchVerseException ex)
        {
            throw new LogicError(ex);
        }
    }

    /**
     * Enumerate over the verse in this range
     * @return A verse iterator
     */
    public Iterator verseIterator()
    {
        return new Iterator()
        {
            private int next_ordinal = start.getOrdinal();

            public boolean hasNext()
            {
                return next_ordinal <= end.getOrdinal();
            }

            public Object next()
            {
                try
                {
                    return new Verse(next_ordinal++);
                }
                catch (NoSuchVerseException ex)
                {
                    throw new LogicError(ex);
                }
            }

            public void remove() throws UnsupportedOperationException
            {
                throw new UnsupportedOperationException();
            }
        };
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
        VerseRange start = null;
        VerseRange end = null;

        // If a starts before b get the Range of the prequel
        if (a.getStart().compareTo(b.getStart()) == -1)
        {
            start = new VerseRange(a.getStart(), b.getEnd().subtract(1));
        }

        // If a ends after b get the Range of the sequel
        if (a.getEnd().compareTo(b.getEnd()) == 1)
        {
            end = new VerseRange(b.getEnd().add(1), a.getEnd());
        }

        if (start == null)
        {
            if (end == null)
            {
                return new VerseRange[] { };
            }
            else
            {
                return new VerseRange[] { end };
            }
        }
        else
        {
            if (end == null)
            {
                return new VerseRange[] { start };
            }
            else
            {
                return new VerseRange[] { start, end };
            }
        }
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
     * Is the string likely to be a VerseRange and not a Verse?
     * @param desc The string to be tested for Rangeness
     * @return true/false if this is likely to be a range
     */
    public static boolean isVerseRange(String desc)
    {
        for (int i=0; i<RANGE_ALLOWED_DELIMS.length(); i++)
        {
            if (desc.indexOf(RANGE_ALLOWED_DELIMS.charAt(i)) != -1)
            {
                return true;
            }
        }

        return false;
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
            throw new LogicError(ex);
        }

        return whole;
    }

    /**
     * Calculate the last verse in this range.
     * @param start The first verse in the range
     * @param verse_count The number of verses
     * @return The last verse in the range
     */
    private static final Verse calcEnd(Verse start, int verse_count)
    {
        return start.add(verse_count - 1);
    }

    /**
     * Calcualte how many verses in this range
     * @param start The first verse in the range
     * @param end The last verse in the range
     * @return The number of verses. Always >= 1.
     */
    private static final int calcVerseCount(Verse start, Verse end)
    {
        return end.subtract(start) + 1;
    }

    /**
     * Check to see that everything is ok with the Data
     */
    private void verifyData()
    {
        if (verse_count != end.subtract(start) + 1)
        {
            log.warn("start="+start);
            log.warn("end="+end);
            log.warn("verse_count="+verse_count);

            throw new LogicError();
        }
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
        out.writeInt(verse_count);

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
            verse_count = in.readInt();
            end = calcEnd(start, verse_count);

            verifyData();
        }
        catch (NoSuchVerseException ex)
        {
            throw new IOException(ex.getMessage());
        }

        // We are ignoring the original_name. It was set to null in the
        // default ctor so I will ignore it here.
    }

    /**
     * To make serialization work across new versions
     */
    static final long serialVersionUID = 8307795549869653580L;

    /**
     * The real data - how many verses long are we?.
     * All ctors init this so leave default
     */
    private transient int verse_count;

    /**
     * The real data - where do we start?.
     * All ctors init this so leave default
     */
    protected transient Verse start;

    /**
     * The real data - where do we end?.
     * All ctors init this so leave default
     */
    protected transient Verse end;

    /**
     * The original string for picky users
     */
    private transient String original_name;

    /**
     * The whole Bible VerseRange
     */
    private transient static VerseRange whole;

    /**
     * The log stream
     */
    protected transient static Logger log = Logger.getLogger(VerseRange.class);
}
