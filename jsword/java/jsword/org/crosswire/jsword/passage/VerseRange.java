package org.crosswire.jsword.passage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.lang.StringUtils;
import org.crosswire.common.util.Logger;

/**
 * A VerseRange is one step between a Verse and a Passage - it is a
 * Verse plus a verseCount. Every VerseRange has a start, a verseCount
 * and an end. A VerseRange is designed to be immutable. This is a
 * necessary from a collections point of view. A VerseRange should always
 * be valid, although some versions may not return any text for verses
 * that they consider to be mis-translated in some way.
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
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public final class VerseRange implements VerseBase
{
    /**
     * The default VerseRange is a single verse - Genesis 1:1. I didn't
     * want to provide this constructor however, you are supposed to
     * provide a default ctor for all beans. For this reason I suggest you
     * don't use it.
     */
    public VerseRange()
    {
        this.originalName = null;
        this.start = Verse.DEFAULT;
        this.end = Verse.DEFAULT;
        this.verseCount = 1;

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
        this(desc, DEFAULT);
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
        originalName = desc;

        Verse vbasis = basis.getEnd();

        // Do we need this?
        String[] parts = StringUtils.split(desc, PassageConstants.RANGE_ALLOWED_DELIMS);

        switch (parts.length)
        {
        case 0:
            // So no parts to this at all, so all we have is the basis
            start = vbasis;
            verseCount = 1;
            end = calcEnd(start, verseCount);
            break;

        case 1:
            // Only one part so for most things this will a single verse
            // with the exception of whole chapters and books
            switch (Verse.getAccuracy(parts[0]))
            {
            case PassageConstants.ACCURACY_BOOK_ONLY:
                start = new Verse(parts[0], vbasis);
                verseCount = BibleInfo.versesInBook(start.getBook());
                end = calcEnd(start, verseCount);
                break;

            case PassageConstants.ACCURACY_BOOK_CHAPTER:
                start = new Verse(parts[0], vbasis);
                verseCount = BibleInfo.versesInChapter(start.getBook(), start.getChapter());
                end = calcEnd(start, verseCount);
                break;

            case PassageConstants.ACCURACY_BOOK_VERSE:
            case PassageConstants.ACCURACY_CHAPTER_VERSE:
                start = new Verse(parts[0], vbasis);
                end = start;
                verseCount = 1;
                break;

            case PassageConstants.ACCURACY_NUMBER_ONLY:
                if (basis.isWholeChapter())
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
                        chapter = Verse.parseInt(parts[0].trim());
                    }
    
                    start = new Verse(book, chapter, 1);
                    end = new Verse(book, chapter, BibleInfo.versesInChapter(book, chapter));
                    verseCount = calcVerseCount(start, end);
                }
                else
                {
                    start = new Verse(parts[0], basis.getStart());
                    end = start;
                    verseCount = 1;
                }
                break;
    
            case PassageConstants.ACCURACY_NONE:
                start = vbasis;
                end = vbasis;
                break;

            default:
                assert false : parts.length;
            }
            break;

        case 2:
            switch (Verse.getAccuracy(parts[0]))
            {
            case PassageConstants.ACCURACY_BOOK_ONLY:
                // We start with only a Book like "Gen". For all of these the
                // basis is irrelevant since we start with a book.
                switch (Verse.getAccuracy(parts[1]))
                {
                case PassageConstants.ACCURACY_BOOK_ONLY:
                    // And we end with a book, so we need to encompass the lot
                    start = new Verse(parts[0]);
                    end = new Verse(parts[1]);
                    // except that this gives us end at 1:1, and not the book end
                    end = end.getLastVerseInBook();
                    break;

                case PassageConstants.ACCURACY_BOOK_CHAPTER:
                    // So we have something like "Gen-Exo 4"
                    start = new Verse(parts[0]);
                    end = new Verse(parts[1]);
                    // except that this gives us end at verse 1, and not the book end
                    end = end.getLastVerseInChapter();
                    break;

                case PassageConstants.ACCURACY_BOOK_VERSE:
                    // So we have something like "Gen-Exo 4:2"
                    start = new Verse(parts[0]);
                    end = new Verse(parts[1]);
                    break;

                case PassageConstants.ACCURACY_CHAPTER_VERSE:
                    // This is a bit wierd: "Gen-3:4". Assume "Gen 1:1-3:4"
                    start = new Verse(parts[0]);
                    end = new Verse(parts[1], start);
                    break;

                case PassageConstants.ACCURACY_NUMBER_ONLY:
                    // This is like "Gen-3", which could mean anything, but since
                    // we interpret "1" to mean Genesis we will use "Gen-Num"
                    start = new Verse(parts[0]);
                    end = new Verse(parts[1]);
                    // except that this gives us end at 1:1, and not the book end
                    end = end.getLastVerseInBook();
                    break;

                case PassageConstants.ACCURACY_NONE:
                    // Also wierd "Gen-". Perhaps we need to assume end of book?
                    start = new Verse(parts[0]);
                    end = start.getLastVerseInBook();
                    break;

                default:
                    assert false : Verse.getAccuracy(parts[0]);
                    break;
                }
                break;

            case PassageConstants.ACCURACY_BOOK_CHAPTER:
                // So we start something like "Gen 3", as above the basis is
                // not relevant for any of these
                switch (Verse.getAccuracy(parts[1]))
                {
                case PassageConstants.ACCURACY_BOOK_ONLY:
                    // For example "Gen 3-Exo"
                    start = new Verse(parts[0]);
                    end = new Verse(parts[1]);
                    // except that this gives us end at 1:1, and not the book end
                    end = end.getLastVerseInBook();
                    break;

                case PassageConstants.ACCURACY_BOOK_CHAPTER:
                    // For example "Gen 3-Exo 4"
                    start = new Verse(parts[0]);
                    end = new Verse(parts[1]);
                    // except that this gives us end at verse 1, and not the book end
                    end = end.getLastVerseInChapter();
                    break;

                case PassageConstants.ACCURACY_BOOK_VERSE:
                    // For example "Gen 4-Exo 3:4"
                    start = new Verse(parts[0]);
                    end = new Verse(parts[1]);
                    break;

                case PassageConstants.ACCURACY_CHAPTER_VERSE:
                    // For example "Gen 4-5:6"
                    start = new Verse(parts[0]);
                    end = new Verse(parts[1], start);
                    break;

                case PassageConstants.ACCURACY_NUMBER_ONLY:
                    // For example "Gen 4-6"
                    start = new Verse(parts[0]);
                    int chap = Verse.parseInt(parts[1].trim());
                    end = new Verse(start.getBook(), chap, BibleInfo.versesInChapter(start.getBook(), chap));
                    break;

                case PassageConstants.ACCURACY_NONE:
                    // For example "Gen 4-", lets assume the end of the chapter?
                    start = new Verse(parts[0]);
                    end = start.getLastVerseInChapter();
                    break;

                default:
                    assert false : Verse.getAccuracy(parts[1]);
                }
                break;

            case PassageConstants.ACCURACY_BOOK_VERSE:
                // So we start something like "Gen 2:3"
                switch (Verse.getAccuracy(parts[1]))
                {
                case PassageConstants.ACCURACY_BOOK_ONLY:
                    // For example "Gen 3:2-Exo"
                    start = new Verse(parts[0]);
                    end = new Verse(parts[1]);
                    // except that this gives us end at 1:1, and not the book end
                    end = end.getLastVerseInBook();
                    break;

                case PassageConstants.ACCURACY_BOOK_CHAPTER:
                    // For example "Gen 3:2-Exo 4"
                    start = new Verse(parts[0]);
                    end = new Verse(parts[1]);
                    // except that this gives us end at verse 1, and not the book end
                    end = end.getLastVerseInChapter();
                    break;

                case PassageConstants.ACCURACY_BOOK_VERSE:
                    // For example "Gen 3:2-Exo 3:4"
                    start = new Verse(parts[0]);
                    end = new Verse(parts[1]);
                    break;

                case PassageConstants.ACCURACY_CHAPTER_VERSE:
                    // For example "Gen 3:2-4:4"
                    start = new Verse(parts[0]);
                    end = new Verse(parts[1], start);
                    break;

                case PassageConstants.ACCURACY_NUMBER_ONLY:
                    // For example "Gen 3:2-5"
                    start = new Verse(parts[0]);
                    end = new Verse(parts[1], start);
                    break;

                case PassageConstants.ACCURACY_NONE:
                    // Very wierd: "Gen 4:3-", can really assume end of anything
                    // so make end = start.
                    start = new Verse(parts[0]);
                    end = start;
                    break;
    
                default:
                    assert false : Verse.getAccuracy(parts[1]);
                }
                break;

            case PassageConstants.ACCURACY_CHAPTER_VERSE:
                // So we start something like "3:4".
                // Now the basis starts to become important
                switch (Verse.getAccuracy(parts[1]))
                {
                case PassageConstants.ACCURACY_BOOK_ONLY:
                    // For example "3:2-Exo"
                    start = new Verse(parts[0], vbasis);
                    end = new Verse(parts[1]);
                    // except that this gives us end at 1:1, and not the book end
                    end = end.getLastVerseInBook();
                    break;

                case PassageConstants.ACCURACY_BOOK_CHAPTER:
                    // For example "3:2-Exo 4"
                    start = new Verse(parts[0], vbasis);
                    end = new Verse(parts[1]);
                    // except that this gives us end at verse 1, and not the book end
                    end = end.getLastVerseInChapter();
                    break;

                case PassageConstants.ACCURACY_BOOK_VERSE:
                    // For example "3:2-Exo 3:4"
                    start = new Verse(parts[0], vbasis);
                    end = new Verse(parts[1]);
                    break;

                case PassageConstants.ACCURACY_CHAPTER_VERSE:
                    // For example "3:2-4:4"
                    start = new Verse(parts[0], vbasis);
                    end = new Verse(parts[1], start);
                    break;

                case PassageConstants.ACCURACY_NUMBER_ONLY:
                    // For example "3:2-5"
                    start = new Verse(parts[0], vbasis);
                    end = new Verse(parts[1], start);
                    break;

                case PassageConstants.ACCURACY_NONE:
                    // Very wierd: "4:3-", can really assume end of anything
                    // so make end = start.
                    start = new Verse(parts[0], vbasis);
                    end = start;
                    break;
    
                default:
                    assert false : Verse.getAccuracy(parts[1]);
                }
                break;

            case PassageConstants.ACCURACY_NUMBER_ONLY:
                // So we start something like "5"
                switch (Verse.getAccuracy(parts[1]))
                {
                case PassageConstants.ACCURACY_BOOK_ONLY:
                    // Wierd - For example "1-Exo". Since there is a basis to
                    // work from we won't assume that 1 means Gen.
                    start = new Verse(parts[0], vbasis);
                    end = new Verse(parts[1]);
                    // except that this gives us end at 1:1, and not the book end
                    end = end.getLastVerseInBook();
                    break;

                case PassageConstants.ACCURACY_BOOK_CHAPTER:
                    // For example "2-Exo 4". See note for ACCURACY_BOOK_ONLY 
                    start = new Verse(parts[0], vbasis);
                    end = new Verse(parts[1]);
                    // except that this gives us end at verse 1, and not the book end
                    end = end.getLastVerseInChapter();
                    break;

                case PassageConstants.ACCURACY_BOOK_VERSE:
                    // For example "2-Exo 3:4"
                    start = new Verse(parts[0], vbasis);
                    end = new Verse(parts[1]);
                    break;

                case PassageConstants.ACCURACY_CHAPTER_VERSE:
                    // For example "2-4:4"
                    start = new Verse(parts[0], vbasis);
                    end = new Verse(parts[1], start);
                    break;

                case PassageConstants.ACCURACY_NUMBER_ONLY:
                    // For example "3-5". Tricky because the scope of the basis
                    // tells us how to interpret this.
                    // "Gen 1, 3-5" probably means chapters, but
                    // "Gen 1:1, 3-5" probably means verses.
                    // also "Gen, 3-5" might mean "Gen, Num-Deu" but I think we can ignore that for now
                    if (basis.isWholeChapter())
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
                            chapter = Verse.parseInt(parts[0].trim());
                        }
    
                        start = new Verse(book, chapter, 1);
                        end = new Verse(book, chapter, BibleInfo.versesInChapter(book, chapter));
                    }
                    else
                    {
                        start = new Verse(parts[0], basis.getStart());
                        end = new Verse(parts[1], start);
                    }
                    break;

                case PassageConstants.ACCURACY_NONE:
                    // For example "4-".
                    start = new Verse(parts[0], vbasis);
                    end = vbasis;
                    break;
    
                default:
                    assert false : Verse.getAccuracy(parts[1]);
                }
                break;

            case PassageConstants.ACCURACY_NONE:
                // So we start with nothing. For most of these we will use
                // the basis as the start point.
                switch (Verse.getAccuracy(parts[1]))
                {
                case PassageConstants.ACCURACY_BOOK_ONLY:
                    // For example "-Exo".
                    start = vbasis;
                    end = new Verse(parts[1]);
                    // except that this gives us end at 1:1, and not the book end
                    end = end.getLastVerseInBook();
                    break;

                case PassageConstants.ACCURACY_BOOK_CHAPTER:
                    // For example "-Exo 2"
                    start = vbasis;
                    end = new Verse(parts[1]);
                    // except that this gives us end at verse 1, and not the book end
                    end = end.getLastVerseInChapter();
                    break;

                case PassageConstants.ACCURACY_BOOK_VERSE:
                    // For example "-Exo 3:4"
                    start = vbasis;
                    end = new Verse(parts[1]);
                    break;

                case PassageConstants.ACCURACY_CHAPTER_VERSE:
                    // For example "-4:4"
                    start = vbasis;
                    end = new Verse(parts[1], start);
                    break;

                case PassageConstants.ACCURACY_NUMBER_ONLY:
                    // For example "-5". This is wierd enough that is is hard
                    // to say we got this wrong.
                    start = vbasis;
                    end = new Verse(parts[1], start);
                    break;

                case PassageConstants.ACCURACY_NONE:
                    // For example "-". We just make everything the basis,
                    // although I'm not sure we shouldn't except.
                    start = vbasis;
                    end = vbasis;
                    break;
    
                default:
                    assert false : Verse.getAccuracy(parts[1]);
                }
                break;

            default:
                assert false : Verse.getAccuracy(parts[0]);
            }

            verseCount = calcVerseCount(start, end);
            break;

        default:
            throw new NoSuchVerseException(Msg.RANGE_PARTS, new Object[] { PassageConstants.RANGE_ALLOWED_DELIMS, desc });
        }

        verifyData();
    }

    /**
     * Construct a VerseRange from a Verse. The resultant VerseRange will be
     * 1 verse in verseCount.
     * @param start The verse to start from
     */
    public VerseRange(Verse start)
    {
        assert start != null;

        this.originalName = null;
        this.start = start;
        this.end = start;
        this.verseCount = 1;

        verifyData();
    }

    /**
     * Construct a VerseRange from a Verse and a range.
     * @param start The verse to start from
     * @param verseCount The number of verses
     * @exception NoSuchVerseException If there arn't that many verses
     */
    public VerseRange(Verse start, int verseCount) throws NoSuchVerseException
    {
        if (verseCount < 1)
        {
            throw new NoSuchVerseException(Msg.RANGE_LOCOUNT);
        }

        if (start.getOrdinal() + verseCount - 1 > BibleInfo.versesInBible())
        {
            Object[] params =
            {
                start.getName(),
                new Integer(BibleInfo.versesInBible() - start.getOrdinal()),
                new Integer(verseCount)
            };
            throw new NoSuchVerseException(Msg.RANGE_HICOUNT, params);
        }

        this.originalName = null;
        this.start = start;
        this.verseCount = verseCount;
        this.end = calcEnd(start, verseCount);

        verifyData();
    }

    /**
     * Construct a VerseRange from a Verse and a range.
     * Now the actual value of the boolean is ignored. However for future proofing
     * you should only use 'true'. Do not use patch_up=false, use Verse(int, int, int)
     * This so that we can declare this constructor to not throw an exception.
     * Is there a better way of doing this?
     * @param start The verse to start from
     * @param verseCount The number of verse to count
     * @param patchUp True to trigger reference fixing
     */
    public VerseRange(Verse start, int verseCount, boolean patchUp)
    {
        if (!patchUp)
        {
            throw new IllegalArgumentException(Msg.ERROR_PATCH.toString());
        }

        // Not sure that any of the code below (except verifyData() which may not stay there)
        // Checks for null so we do it explictly here.
        assert start != null;

        this.originalName = null;
        this.start = start;
        this.end = start.add(Math.max(verseCount, 1) - 1);
        this.verseCount = calcVerseCount(start, end);

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
        assert start != null;
        assert end != null;

        this.originalName = null;

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
            assert false : start.compareTo(end);
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
        assert base_start != null;
        assert blur_down >= 0;
        assert blur_up >= 0;

        this.originalName = null;

        switch (restrict)
        {
        case PassageConstants.RESTRICT_CHAPTER:
            try
            {
                int start_book = base_start.getBook();
                int start_chapter = base_start.getChapter();
                int start_verse = base_start.getVerse() - blur_down;
                int end_verse = base_start.getVerse() + blur_up;

                start_verse = Math.max(start_verse, 1);
                end_verse = Math.min(end_verse, BibleInfo.versesInChapter(start_book, start_chapter));

                start = new Verse(start_book, start_chapter, start_verse);
                verseCount = end_verse - start_verse + 1;
                end = calcEnd(start, verseCount);
            }
            catch (NoSuchVerseException ex)
            {
                assert false : ex;
            }
            break;

        case PassageConstants.RESTRICT_NONE:
            start = base_start.subtract(blur_down);
            end = base_start.add(blur_up);
            verseCount = calcVerseCount(start, end);
            break;

        case PassageConstants.RESTRICT_BOOK:
            throw new IllegalArgumentException(Msg.RANGE_BLURBOOK.toString());

        default:
            throw new IllegalArgumentException(Msg.RANGE_BLURNONE.toString());
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
        assert base_start != null;
        assert blur_down >= 0;
        assert blur_up >= 0;

        this.originalName = null;

        switch (restrict)
        {
        case PassageConstants.RESTRICT_CHAPTER:
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
                verseCount = calcVerseCount(start, end);
            }
            catch (NoSuchVerseException ex)
            {
                assert false : ex;
            }
            break;

        case PassageConstants.RESTRICT_NONE:
            start = base_start.getStart().subtract(blur_down);
            end = base_start.getEnd().add(blur_up);
            verseCount = calcVerseCount(start, end);
            break;

        case PassageConstants.RESTRICT_BOOK:
            throw new IllegalArgumentException(Msg.RANGE_BLURBOOK.toString());

        default:
            throw new IllegalArgumentException(Msg.RANGE_BLURNONE.toString());
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
        originalName = null;
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
    public String getName(Verse base)
    {
        if (PassageUtil.isPersistentNaming() && originalName != null)
        {
            return originalName;
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
                if (isWholeBooks())
                {
                    // Just report the name of the book, we don't need to worry about the
                    // base since we start at the start of a book, and should have been
                    // recently normalized()
                    return BibleInfo.getShortBookName(start_book)
                         + PassageConstants.RANGE_PREF_DELIM
                         + BibleInfo.getShortBookName(end_book);
                }

                // If this range is exactly a whole chapter
                if (isWholeChapters())
                {
                    // Just report book and chapter names
                    return BibleInfo.getShortBookName(start_book)
                         + PassageConstants.VERSE_PREF_DELIM1 + start_chapter
                         + PassageConstants.RANGE_PREF_DELIM + BibleInfo.getShortBookName(end_book)
                         + PassageConstants.VERSE_PREF_DELIM1 + end_chapter;
                }

                return start.getName(base) + PassageConstants.RANGE_PREF_DELIM + end.getName(base);
            }

            // This range is exactly a whole book
            if (isWholeBook())
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
                if (isWholeChapters())
                {
                    // Just report the name of the book and the chapters
                    return BibleInfo.getShortBookName(start_book)
                         + PassageConstants.VERSE_PREF_DELIM1 + start_chapter
                         + PassageConstants.RANGE_PREF_DELIM + end_chapter;
                }

                return start.getName(base)
                     + PassageConstants.RANGE_PREF_DELIM + end_chapter
                     + PassageConstants.VERSE_PREF_DELIM2 + end_verse;
            }

            // If this range is exactly a whole chapter
            if (isWholeChapter())
            {
                // Just report the name of the book and the chapter
                return BibleInfo.getShortBookName(start_book)
                     + PassageConstants.VERSE_PREF_DELIM1 + start_chapter;
            }

            // If this is 2 separate verses
            if (start_verse != end_verse)
            {
                return start.getName(base)
                     + PassageConstants.RANGE_PREF_DELIM + end_verse;
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
     * The OSIS defined specification for this VerseRange.
     * Uses short books names, with "." as a verse part separator.
     * NOTE(joe): Technically wrong - we should list verses separated by spaces
     * But that could get very messy, so I'm keeping things simple.
     * @return a String containing the OSIS description of the verses
     */
    public String getOSISName()
    {
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
                    return BibleInfo.getOSISName(startBook)
                         + PassageConstants.RANGE_PREF_DELIM
                         + BibleInfo.getOSISName(endBook);
                }

                // If this range is exactly a whole chapter
                if (isWholeChapters())
                {
                    // Just report book and chapter names
                    return BibleInfo.getOSISName(startBook)
                         + PassageConstants.VERSE_OSIS_DELIM + startChapter
                         + PassageConstants.RANGE_PREF_DELIM + BibleInfo.getOSISName(endBook)
                         + PassageConstants.VERSE_OSIS_DELIM + endChapter;
                }

                return start.getOSISName() + PassageConstants.RANGE_PREF_DELIM + end.getOSISName();
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
            if (startChapter != endChapter)
            {
                // If this range is a whole number of chapters
                if (isWholeChapters())
                {
                    // Just report the name of the book and the chapters
                    return BibleInfo.getOSISName(startBook)
                         + PassageConstants.VERSE_OSIS_DELIM + startChapter
                         + PassageConstants.RANGE_PREF_DELIM + endChapter;
                }

                return start.getOSISName()
                     + PassageConstants.RANGE_PREF_DELIM + endChapter
                     + PassageConstants.VERSE_OSIS_DELIM + endVerse;
            }

            // If this range is exactly a whole chapter
            if (isWholeChapter())
            {
                // Just report the name of the book and the chapter
                return BibleInfo.getOSISName(startBook)
                     + PassageConstants.VERSE_OSIS_DELIM + startChapter;
            }

            // If this is 2 separate verses
            if (startVerse != endVerse)
            {
                return start.getOSISName()
                     + PassageConstants.RANGE_PREF_DELIM + endVerse;
            }

            // The range is a single verse
            return start.getOSISName();
        }
        catch (NoSuchVerseException ex)
        {
            assert false : ex;
            return "!Error!"; //$NON-NLS-1$
        }
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
    public Object clone() throws CloneNotSupportedException
    {
        // This gets us a shallow copy
        VerseRange copy = (VerseRange) super.clone();

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
     * Does this range occupy more than one chapter;
     * @return true if we occupy 2 or more chapters
     */
    public boolean isMultipleChapters()
    {
        if (start.getBook() != end.getBook())
        {
            return true;
        }

        if (start.getChapter() != end.getChapter())
        {
            return true;
        }

        return false;
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
     * Enumerate over the verse in this range
     * @return A verse iterator
     */
    public Iterator verseIterator()
    {
        return new VerseIterator(this);
    }

    /**
     * Enumerate the subranges in this range
     * @return a range iterator
     */
    public Iterator rangeIterator(int restrict)
    {
        return new AbstractPassage.VerseRangeIterator(verseIterator(), restrict);
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
            else
            {
                return new VerseRange[] { rend };
            }
        }
        else
        {
            if (rend == null)
            {
                return new VerseRange[] { rstart };
            }
            else
            {
                return new VerseRange[] { rstart, rend };
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
    private static final Verse calcEnd(Verse start, int verseCount)
    {
        return start.add(verseCount - 1);
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
            next = range.start.getOrdinal();
            last = range.end.getOrdinal();
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
                return new Verse();
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
        return false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#iterator()
     */
    public Iterator iterator()
    {
        return new Iterator()
        {
            public void remove()
            {
                throw new UnsupportedOperationException();
            }

            public boolean hasNext()
            {
                return false;
            }

            public Object next()
            {
                return null;
            }
        };
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
    public void blur(int by, int bounds)
    {
        throw new UnsupportedOperationException();
    }

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
    protected transient Verse start;

    /**
     * The real data - where do we end?.
     * All ctors init this so leave default
     */
    protected transient Verse end;

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
     * The default verse range is a singel verse starting at Gen 1.
     * It is mostly only used by the ctors as a default.
     */
    private static final VerseRange DEFAULT = new VerseRange(Verse.DEFAULT);

    /**
     * The whole Bible VerseRange
     */
    private static transient VerseRange whole;

    /**
     * The log stream
     */
    protected static final transient Logger log = Logger.getLogger(VerseRange.class);
}
