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
 * © CrossWire Bible Society, 2012 - 2016
 *
 */
package org.crosswire.jsword.versification;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Iterator;

import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.book.ReferenceSystem;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;

/**
 * A named Versification defines
 * the order of BibleBooks by Testament,
 * the number of chapters in each BibleBook,
 * the number of verses in each chapter.
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class Versification implements ReferenceSystem, Serializable {
    public Versification() {
    }

    /**
     * Construct a Versification.
     * 
     * @param name
     *            The name of this reference system
     * @param booksOT
     *            An ordered list of books in this reference system. The list
     *            should not include INTRO_BIBLE, or INTRO_OT.
     * @param booksNT
     *            An ordered list of books in this reference system. The list
     *            should not include INTRO_NT.
     * @param lastVerseOT
     *            For each book in booksOT, this has an array with one entry for
     *            each chapter whose value is the highest numbered verse in that
     *            chapter. Do not include chapter 0.
     * @param lastVerseNT
     *            For each book in booksNT, this has an array with one entry for
     *            each chapter whose value is the highest numbered verse in that
     *            chapter. Do not include chapter 0.
     */
    public Versification(String name, BibleBook[] booksOT, BibleBook[] booksNT, int[][] lastVerseOT, int[][] lastVerseNT) {
        this.name = name;

        // Copy the books into an aggregated BibleBook array
        // including INTRO_BIBLE and INTRO_OT/INTRO_NT for non-null book lists
        int bookCount = 1; // Always include the INTRO_BIBLE
        if (booksOT.length > 0) {
            bookCount += booksOT.length + 1; // All of the OT books and INTRO_OT
        }

        int ntStart = bookCount;
        if (booksNT.length > 0) {
            bookCount += booksNT.length + 1; // All of the NT books and INTRO_NT
        }
        BibleBook[] books = new BibleBook[bookCount];
        books[0] = BibleBook.INTRO_BIBLE;
        if (booksOT.length > 0) {
            books[1] = BibleBook.INTRO_OT;
            System.arraycopy(booksOT, 0, books, 2, booksOT.length);
        }

        if (booksNT.length > 0) {
            books[ntStart] = BibleBook.INTRO_NT;
            System.arraycopy(booksNT, 0, books, ntStart + 1, booksNT.length);
        }

        this.bookList = new BibleBookList(books);

        int ordinal = 0;

        // Create an independent copy of lastVerse.
        this.lastVerse = new int[bookCount][];
        int bookIndex = 0;

        // Add in the bible introduction
        int[] chapters = new int[1];
        chapters[0] = 0;
        this.lastVerse[bookIndex++] = chapters;

        // Now append the OT info
        if (lastVerseOT.length > 0) {
            // Add in the testament intro
            chapters = new int[1];
            chapters[0] = 0;
            this.lastVerse[bookIndex++] = chapters;
            // then all the testament info
            for (int i = 0; i < lastVerseOT.length; i++) {
                int[] src = lastVerseOT[i];
                // Add one as the location for chapter 0.
                int[] dest = new int[src.length + 1];
                this.lastVerse[bookIndex++] = dest;
                // The last verse of chapter 0 is 0
                dest[0] = 0;
                // copy the last verse array for the chapter
                System.arraycopy(src, 0, dest, 1, src.length);
            }
        }

        // Now append the NT info
        if (lastVerseNT.length > 0) {
            // Add in the testament intro
            chapters = new int[1];
            chapters[0] = 0;
            this.lastVerse[bookIndex++] = chapters;
            // then all the testament info
            for (int i = 0; i < lastVerseNT.length; i++) {
                int[] src = lastVerseNT[i];
                // Add one as the location for chapter 0.
                int[] dest = new int[src.length + 1];
                this.lastVerse[bookIndex++] = dest;
                // The last verse of chapter 0 is 0
                dest[0] = 0;
                // copy the last verse array for the chapter
                System.arraycopy(src, 0, dest, 1, src.length);
            }
        }

        // Initialize chapterStarts to be a parallel array to lastVerse,
        // but with chapter starts
        this.chapterStarts = new int[bookCount][];
        for (bookIndex = 0; bookIndex < bookCount; bookIndex++) {

            // Remember where the OT ends
            if (bookList.getBook(bookIndex) == BibleBook.INTRO_NT) {
                // This is not reached for a v11n without a NT.
                this.otMaxOrdinal = ordinal - 1;
            }

            // Save off the chapter starts
            int[] src = this.lastVerse[bookIndex];
            int numChapters = src.length;
            int[] dest = new int[numChapters];
            this.chapterStarts[bookIndex] = dest;
            for (int chapterIndex = 0; chapterIndex < numChapters; chapterIndex++) {
                // Save off the chapter start
                dest[chapterIndex] = ordinal;

                // Set ordinal to the start of the next chapter or book introduction.
                // The number of verses in each chapter, when including verse 0,
                // is one more that the largest numbered verse in the chapter.
                ordinal += src[chapterIndex] + 1;
            }
        }

        // Remember where the NT ends
        this.ntMaxOrdinal = ordinal - 1;

        // The MT v11n has no NT, so at this point otMaxOrdinal == 0
        if (booksNT.length == 0) {
            this.otMaxOrdinal = this.ntMaxOrdinal;
        }
//        Versification.dump(System.out, this.osisName, this.bookList, this.lastVerse);
//        Versification.dump(System.out, this.osisName, this.bookList, this.chapterStarts);
    }

    /**
     * Get the OSIS name for this Versification.
     * @return the OSIS name of the Versification
     */
    public String getName() {
        return name;
    }

    /**
     * Does this Versification contain the BibleBook.
     *
     * @param book
     * @return true if it is present.
     */
    public boolean containsBook(BibleBook book) {
        return bookList.contains(book);
    }

    /**
     * Get the BibleBook by its position in this Versification.
     * If the position is negative, return the first book.
     * If the position is greater than the last, return the last book.
     *
     * @param ordinal
     * @return the indicated book
     */
    public BibleBook getBook(int ordinal) {
        return bookList.getBook(ordinal);
    }

    /**
     * Get a book from its name.
     *
     * @param find
     *            The string to identify
     * @return The BibleBook, On error null
     */
    public BibleBook getBook(String find) {
        BibleBook book = BibleNames.instance().getBook(find);
        if (containsBook(book)) {
            return book;
        }
        return null;
    }

    /**
     * Get the number of books in this Versification.
     * @return the number of books
     */
    public int getBookCount() {
        return bookList.getBookCount();
    }

    /**
     * The number of books between two verses includes
     * the books of the two verses and everything in between.
     * 
     * @param start
     *            The first Verse in the range
     * @param end The last Verse in the range
     * @return The number of books. Always &gt;= 1.
     */
    public int getBookCount(Verse start, Verse end) {
        int startBook = bookList.getOrdinal(start.getBook());
        int endBook = bookList.getOrdinal(end.getBook());

        return endBook - startBook + 1;
    }

    /**
     * Return the first book in the list.
     *
     * @return the first book in the list
     */
    public BibleBook getFirstBook() {
        return bookList.getFirstBook();
    }

    /**
     * Return the first book in the list.
     *
     * @return the first book in the list
     */
    public BibleBook getLastBook() {
        return bookList.getLastBook();
    }

    /**
     * Given a BibleBook, get the next BibleBook in this Versification. If it is the last book, return null.
     * @param book A BibleBook in the Versification
     * @return the previous BibleBook or null.
     */
    public BibleBook getNextBook(BibleBook book) {
        return bookList.getNextBook(book);
    }

    /**
     * Given a BibleBook, get the previous BibleBook in this Versification. If it is the first book, return null.
     * @param book A BibleBook in the Versification
     * @return the previous BibleBook or null.
     */
    public BibleBook getPreviousBook(BibleBook book) {
        return bookList.getPreviousBook(book);
    }

    /**
     * Get the BibleBooks in this Versification.
     *
     * @return an Iterator over the books
     */
    public Iterator<BibleBook> getBookIterator() {
        return bookList.iterator();
    }

    /**
     * Get the BookName.
     *
     * @param book the desired book
     * @return The requested BookName or null if not in this versification
     */
    public BookName getBookName(BibleBook book) {
        if (containsBook(book)) {
            return BibleNames.instance().getBookName(book);
        }
        return null;
    }

    /**
     * Get the preferred name of a book. Altered by the case setting (see
     * setBookCase() and isFullBookName())
     *
     * @param book the desired book
     * @return The full name of the book or null if not in this versification
     */
    public String getPreferredName(BibleBook book) {
        if (containsBook(book)) {
            return BibleNames.instance().getPreferredName(book);
        }
        return null;
      }

    /**
     * Get the full name of a book (e.g. "Genesis"). Altered by the case setting
     * (see setBookCase())
     *
     * @param book the book of the Bible
     * @return The full name of the book or null if not in this versification
     */
    public String getLongName(BibleBook book) {
        if (containsBook(book)) {
            return BibleNames.instance().getLongName(book);
        }
        return null;
      }

    /**
     * Get the short name of a book (e.g. "Gen"). Altered by the case setting
     * (see setBookCase())
     *
     * @param book the book of the Bible
     * @return The short name of the book or null if not in this versification
     */
    public String getShortName(BibleBook book) {
        if (containsBook(book)) {
            return BibleNames.instance().getShortName(book);
        }
        return null;
      }

    /**
     * Is the given string a valid book name. If this method returns true then
     * getBook() will return a BibleBook and not null.
     *
     * @param find
     *            The string to identify
     * @return true when the book name is recognized
     */
    public boolean isBook(String find) {
        return getBook(find) != null;
    }

    /**
     * Get the last valid chapter number for a book.
     *
     * @param book
     *            The book part of the reference.
     * @return The last valid chapter number for a book.
     */
    public int getLastChapter(BibleBook book) {
        // This is faster than doing the check explicitly, unless
        // The exception is actually thrown, then it is a lot slower
        // I'd like to think that the norm is to get it right
        try {
            return lastVerse[bookList.getOrdinal(book)].length - 1;
        } catch (NullPointerException ex) {
            return 0;
        } catch (ArrayIndexOutOfBoundsException ex) {
            return 0;
        }
    }

    /**
     * Get the last valid verse number for a chapter.
     *
     * @param book
     *            The book part of the reference.
     * @param chapter
     *            The current chapter
     * @return The last valid verse number for a chapter
     */
    public int getLastVerse(BibleBook book, int chapter) {
        // This is faster than doing the check explicitly, unless
        // The exception is actually thrown, then it is a lot slower
        // I'd like to think that the norm is to get it right
        try {
            return lastVerse[bookList.getOrdinal(book)][chapter];
        } catch (NullPointerException ex) {
            return 0;
        } catch (ArrayIndexOutOfBoundsException ex) {
            return 0;
        }
    }

    /**
     * Get a VerseRange encompassing this Versification.
     * 
     * @return a VerseRange for the whole versification
     */
    public VerseRange getAllVerses() {
        Verse first = new Verse(this, bookList.getFirstBook(), 0, 0);
        BibleBook book = bookList.getLastBook();
        int chapter = getLastChapter(book);
        Verse last = new Verse(this, book, chapter, getLastVerse(book, chapter));
        return new VerseRange(this, first, last);
    }

    /**
     * An introduction is a Verse that has a verse number of 0.
     *
     * @param verse the verse to test
     * @return true or false ...
     */
    public boolean isIntro(Verse verse) {
        int v = verse.getVerse();
        return v == 0;
    }

    /**
     * A book introduction is an introduction
     * that has a chapter of 0.
     *
     * @param verse the verse to test
     * @return true or false ...
     */
    public boolean isBookIntro(Verse verse) {
        return 0 == verse.getChapter() && isIntro(verse);
    }

    /**
     * A chapter introduction is an introduction
     * that has a chapter other than 0
     *
     * @param verse the verse to test
     * @return true or false ...
     */
    public boolean isChapterIntro(Verse verse) {
        return 0 != verse.getChapter() && isIntro(verse);
    }

    /**
     * The start of a chapter is indicated by
     * a verse number of 0 or 1
     *
     * @param verse the verse to test
     * @return true or false ...
     */
    public boolean isStartOfChapter(Verse verse) {
        int v = verse.getVerse();
        return v <= 1;
    }

    /**
     * The end of the chapter is indicated by
     * the verse number matching the last in the chapter.
     *
     * @param verse the verse to test
     * @return true or false ...
     */
    public boolean isEndOfChapter(Verse verse) {
        BibleBook b = verse.getBook();
        int v = verse.getVerse();
        int c = verse.getChapter();
        return v == getLastVerse(b, c);
    }

    /**
     * The start of a book is indicated by
     * a chapter number of 0 or 1 and
     * a verse number of 0 or 1.
     *
     * @param verse the verse to test
     * @return true or false ...
     */
    public boolean isStartOfBook(Verse verse) {
        int v = verse.getVerse();
        int c = verse.getChapter();
        return v <= 1 && c <= 1;
    }

    /**
     * The end of the book is indicated by
     * the chapter number matching the last chapter
     * in the book and the verse number matching
     * the last verse in the chapter.
     *
     * @param verse the verse to test
     * @return true or false ...
     */
    public boolean isEndOfBook(Verse verse) {
        BibleBook b = verse.getBook();
        int v = verse.getVerse();
        int c = verse.getChapter();
        return v == getLastVerse(b, c) && c == getLastChapter(b);
    }

    /**
     * Two verses are in the same chapter if both
     * the book and chapter agree.
     *
     * @param first
     *            The verse to compare to
     * @param second
     *            The verse to compare to
     * @return true or false ...
     */
    public boolean isSameChapter(Verse first, Verse second) {
        return first.getBook() == second.getBook() && first.getChapter() == second.getChapter();
    }

    /**
     * Two verse are adjacent if one immediately follows the other,
     * even across book boundaries. Introductions are considered
     * as having "zero width" in this determination. That is the
     * last verse in a chapter or book is adjacent every verse that
     * follows up to and including verse 1 of the next chapter in
     * the versification.
     * <br>
     * For example:<br>
     * The last verse in the Old Testament is adjacent to:
     * <ul>
     * <li>Intro.NT - the New Testament introduction</li>
     * <li>Matt 0:0 - the book introduction</li>
     * <li>Matt 1:0 - the chapter introduction</li>
     * <li>Matt 1:1 - the first verse of Matt</li>
     * </ul>
     * Note: the verses can be in any order.
     *
     * @param first
     *            The verse to compare to
     * @param second
     *            The verse to compare to
     * @return true or false ...
     */
    public boolean isAdjacentChapter(Verse first, Verse second) {
        Verse before = min(first, second);
        Verse after = max(first, second);
        if (isSameBook(first, second)) {
            return after.getChapter() - before.getChapter() == 1;
        }
        // The earlier verse has to be the  last chapter
        return isAdjacentBook(before, after)
             && getLastChapter(before.getBook()) == before.getChapter()
             && after.getChapter() <= 1;
    }

    /**
     * Two verses are in the same book
     * when they have the same book.
     *
     * @param first
     *            The verse to compare to
     * @param second
     *            The verse to compare to
     * @return true or false ...
     */
    public boolean isSameBook(Verse first, Verse second) {
        return first.getBook() == second.getBook();
    }

    /**
     * Two verses are in adjacent books if one book
     * follows the other in this versification.
     * Note: the verses can be in any order.
     *
     * @param first
     *            The verse to compare to
     * @param second
     *            The verse to compare to
     * @return true or false ...
     */
    public boolean isAdjacentBook(Verse first, Verse second) {
        return Math.abs(bookList.getOrdinal(second.getBook()) - bookList.getOrdinal(first.getBook())) == 1;
    }

    /**
     * Is this verse adjacent to another verse
     *
     * @param first
     *            The first verse in the comparison
     * @param second
     *            The second verse in the comparison
     * @return true if the verses are adjacent.
     */
    public boolean isAdjacentVerse(Verse first, Verse second) {
        Verse before = min(first, second);
        Verse after = max(first, second);
        if (isSameChapter(first, second)) {
            return after.getVerse() - before.getVerse() == 1;
        }
        // The earlier verse has to be the last verse in the chapter
        return isAdjacentChapter(before, after)
             && getLastVerse(before.getBook(), before.getChapter()) == before.getVerse()
             && after.getVerse() <= 1;
    }

    /**
     * How many verses are there in between the 2 Verses. The answer is -ve if
     * start is bigger than end. The answer is inclusive of start and exclusive
     * of end, so that <code>distance(gen11, gen12) == 1</code>
     *
     * @param start
     *            The first Verse in the range
     * @param end The last Verse in the range
     * @return The count of verses between this and that.
     */
    public int distance(Verse start, Verse end) {
        return end.getOrdinal() - start.getOrdinal();
    }

    /**
     * Determine the earlier of the two verses.
     * If first == second then return first.
     * 
     * @param first the first verse to compare
     * @param second the second verse to compare
     * @return The earlier of the two verses
     */
    public Verse min(Verse first, Verse second) {
        return first.getOrdinal() <= second.getOrdinal() ? first : second;
    }

    /**
     * Determine the later of the two verses.
     * If first == second then return first.
     * 
     * @param first the first verse to compare
     * @param second the second verse to compare
     * @return The later of the two verses
     */
    public Verse max(Verse first, Verse second) {
        return first.getOrdinal() > second.getOrdinal() ? first : second;
    }

    /**
     * Get the verse n down from here this Verse.
     *
     * @param verse
     *            The verse to use as a start
     * @param n
     *            The number to count down by
     * @return The new Verse
     */
    public Verse subtract(Verse verse, int n) {
        int newVerse = verse.getVerse() - n;
        // Try the simple case of the verse being in the same chapter
        if (newVerse >= 0) {
            return new Verse(verse.getVersification(), verse.getBook(), verse.getChapter(), newVerse);
        }
        return decodeOrdinal(verse.getOrdinal() - n);
    }

    /**
     * Get the verse that is a verses on from the one we've got.
     *
     * @param verse
     *            The verse to use as a start
     * @return The new verse or null if there is no next verse
     */
    public Verse next(Verse verse) {
        // Cannot increment past the end.
        if (verse.getOrdinal() == ntMaxOrdinal) {
            return null;
        }

        BibleBook nextBook = verse.getBook();
        int nextChapter = verse.getChapter();
        int nextVerse = verse.getVerse() + 1;
        if (nextVerse > getLastVerse(nextBook, nextChapter)) {
            // Go to an introduction.
            nextVerse = 0;
            // of the next chapter
            nextChapter += 1;
            // check to see that the chapter is valid for the book
            if (nextChapter > getLastChapter(nextBook)) {
                // To to an introduction
                nextChapter = 0;
                // of the next book
                nextBook = bookList.getNextBook(verse.getBook());
            }
        }

        // nextBook is null when we try to increment past the last verse
        // The test at the beginning is designed to prevent that
        if (nextBook == null) {
            assert false;
            return null;
        }

        return new Verse(this, nextBook, nextChapter, nextVerse);
    }

    /**
     * Get the verse that is a few verses on from the one we've got.
     *
     * @param verse
     *            The verse to use as a start
     * @param n
     *            the number of verses later than the one we're one
     * @return The new verse
     */
    public Verse add(Verse verse, int n) {
        int newVerse = verse.getVerse() + n;
        // Try the simple case of the verse being in the same chapter
        if (newVerse <= getLastVerse(verse.getBook(), verse.getChapter())) {
            return new Verse(verse.getVersification(), verse.getBook(), verse.getChapter(), newVerse);
        }
        return decodeOrdinal(verse.getOrdinal() + n);
    }

    /**
     * The number of chapters between two verses includes
     * the chapters of the two verses and everything in between.
     * 
     * @param start
     *            The first Verse in the range
     * @param end The last Verse in the range
     * @return The number of chapters. Always &gt;= 1.
     */
    public int getChapterCount(Verse start, Verse end) {
        BibleBook startBook = start.getBook();
        int startChap = start.getChapter();
        BibleBook endBook = end.getBook();
        int endChap = end.getChapter();

        if (startBook == endBook) {
            return endChap - startChap + 1;
        }

        // So we are going to have to count up chapters from start to end
        int total = getLastChapter(startBook) - startChap;
        startBook = bookList.getNextBook(startBook);
        endBook = bookList.getPreviousBook(endBook);
        for (BibleBook b =  startBook; b != endBook; b = bookList.getNextBook(b)) {
            total += getLastChapter(b);
        }
        total += endChap;

        return total;
    }

    /**
     * The maximum number of verses in the Bible, including module, testament, book and chapter introductions.
     *
     * @return the number of addressable verses in this versification.
     */
    public int maximumOrdinal() {
        // This is the same as the last ordinal in the Reference System.
        return ntMaxOrdinal;
    }

    /**
     * Where does this verse come in the Bible. The value that this returns should be treated as opaque, useful for a bit set.
     * The introductions to the Book, OT/NT Testaments, Bible books and chapters are included here.
     * <ul>
     * <li>0 - INTRO_BIBLE 0:0 - The Book introduction</li>
     * <li>1 - INTRO_OT 0:0 - The OT Testament introduction</li>
     * <li>2 - Gen 0:0 - The introduction to the book of Genesis</li>
     * <li>3 - Gen 1:0 - The introduction to Genesis chapter 1</li>
     * <li>4 - Gen 1:1</li>
     * <li>...</li>
     * <li>35 - Gen 1:31</li>
     * <li>36 - Gen 2:0 - The introduction to Genesis chapter 2</li>
     * <li>37 - Gen 2:1</li>
     * <li>...</li>
     * <li>n - last verse in the OT</li>
     * <li>n + 1 - INTRO_NT, 0, 0 - The New Testament introduction</li>
     * <li>n + 2 - Matt 0:0 - The introduction to Matt</li>
     * <li>n + 3 - Matt 1:0 - The introduction to Matt 1</li>
     * <li>n + 4 - Matt 1:1</li>
     * <li>...</li>
     * </ul>
     * 
     * If the verse is not in this versification, return 0.
     *
     * @param verse
     *            The verse to convert
     * @return The ordinal number of verses
    */
    public int getOrdinal(Verse verse) {
        try {
            return chapterStarts[bookList.getOrdinal(verse.getBook())][verse.getChapter()] + verse.getVerse();
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        }
    }

    /**
     * Determine the ordinal value for this versification given the
     * ordinal value in a testament. If the ordinal is out of bounds it
     * is constrained to be within the boundaries of the testament.
     * This unwinds getTestamentOrdinal.
     * 
     * @param testament the testament in which the ordinal value pertains
     * @param testamentOrdinal the ordinal value within the testament
     * @return the ordinal value for the versification as a whole
     */
    public int getOrdinal(Testament testament, int testamentOrdinal) {
        int ordinal = testamentOrdinal >= 0 ? testamentOrdinal : 0;
        if (Testament.NEW == testament) {
            ordinal = otMaxOrdinal + testamentOrdinal;
            return ordinal <= ntMaxOrdinal ? ordinal : ntMaxOrdinal;
        }
        return ordinal <= otMaxOrdinal ? ordinal : otMaxOrdinal;
    }

    /**
     * Where does this verse come in the Bible. The value that this returns should be treated as opaque, useful for a bit set.
     * The introductions to the Book, OT/NT Testaments, Bible books and chapters are included here.
     * <ul>
     * <li>0 - INTRO_BIBLE 0:0 - The Book introduction</li>
     * <li>1 - INTRO_OT 0:0 - The OT Testament introduction</li>
     * <li>2 - Gen 0:0 - The introduction to the book of Genesis</li>
     * <li>3 - Gen 1:0 - The introduction to Genesis chapter 1</li>
     * <li>4 - Gen 1:1</li>
     * <li>...</li>
     * <li>35 - Gen 1:31</li>
     * <li>36 - Gen 2:0 - The introduction to Genesis chapter 2</li>
     * <li>37 - Genesis 2:1</li>
     * <li>...</li>
     * <li>n - last verse in the OT</li>
     * <li>0 - INTRO_NT, 0, 0 - The New Testament introduction</li>
     * <li>1 - Matt 0:0 - The introduction to Matt</li>
     * <li>2 - Matt 1:0 - The introduction to Matt 1</li>
     * <li>3 - Matt 1:1</li>
     * <li>...</li>
     * </ul>
     *
     * @param ordinal
     *            The ordinal number of the verse to convert
     * @return The ordinal number of the Verse within its Testament
     */
    public int getTestamentOrdinal(int ordinal) {
        int ntOrdinal = otMaxOrdinal + 1;
        if (ordinal >= ntOrdinal) {
            return ordinal - ntOrdinal + 1;
        }
        return ordinal;
    }

    /**
     * Get the testament of a given verse
     * @param ordinal the ordinal position of the verse in the whole Bible
     * @return the testament in which that verse is found
     */
    public Testament getTestament(int ordinal) {
        if (ordinal > otMaxOrdinal) {
            // This is an NT verse
            return Testament.NEW;
        }
        // This is an OT verse
        return Testament.OLD;
    }

    /**
     * Give the count of verses in the testament or the whole Bible.
     *
     * @param testament The testament to count. If null, then all testaments.
     * @return the number of verses in the testament
     */
    public int getCount(Testament testament) {
        int total = ntMaxOrdinal + 1;
        if (testament == null) {
            return total;
        }

        int otCount = otMaxOrdinal + 1;
        if (testament == Testament.OLD) {
            return otCount;
        }

        return total - otCount;
    }

    /**
     * Where does this verse come in the Bible. This will unwind the value returned by getOrdinal(Verse).
     * If the ordinal value is less than 0 or greater than the last verse in this Versification,
     * then constrain it to the first or last verse in this Versification.
     *
     * @param ordinal
     *            The ordinal number of the verse
     * @return A Verse
     */
    public Verse decodeOrdinal(int ordinal) {
        int ord = ordinal;

        if (ord < 0) {
            ord = 0;
        } else if (ord > ntMaxOrdinal) {
            ord = ntMaxOrdinal;
        }

        // Handle three special cases
        // Book/Module introduction
        if (ord == 0) {
            return new Verse(this, BibleBook.INTRO_BIBLE, 0, 0);
        }

        // OT introduction
        if (ord == 1) {
            return new Verse(this, BibleBook.INTRO_OT, 0, 0);
        }

        // NT introduction
        if (ord == otMaxOrdinal + 1) {
            return new Verse(this, BibleBook.INTRO_NT, 0, 0);
        }

        // To find the book, do a binary search in chapterStarts against chapter 0
        int low = 0;
        int high = chapterStarts.length;
        int match = -1;

        while (high - low > 1) {
            // use >>> to keep mid always in range
            int mid = (low + high) >>> 1;

            // Compare the for the item at "mid"
            int cmp = chapterStarts[mid][0] - ord;
            if (cmp < 0) {
                low = mid;
            } else if (cmp > 0) {
                high = mid;
            } else {
                match = mid;
                break;
            }
        }

        // If we didn't have an exact match then use the low value
        int bookIndex = match >= 0 ? match : low;
        BibleBook book = bookList.getBook(bookIndex);

        // To find the chapter, do a binary search in chapterStarts against bookIndex
        low = 0;
        high = chapterStarts[bookIndex].length;
        match = -1;

        while (high - low > 1) {
            // use >>> to keep mid always in range
            int mid = (low + high) >>> 1;

            // Compare the for the item at "mid"
            int cmp = chapterStarts[bookIndex][mid] - ord;
            if (cmp < 0) {
                low = mid;
            } else if (cmp > 0) {
                high = mid;
            } else {
                match = mid;
                break;
            }
        }

        // If we didn't have an exact match then use the low value
        int chapterIndex = match >= 0 ? match : low;
        int verse = chapterIndex == 0 ? 0 : ord - chapterStarts[bookIndex][chapterIndex];
        return new Verse(this, book, chapterIndex, verse);
    }

    /**
     * Does the following represent a real verse?. It is code like this that
     * makes me wonder if I18 is done well/worth doing. All this code does is
     * check if the numbers are valid, but the exception handling code is huge
     * :(
     *
     * @param book
     *            The book part of the reference.
     * @param chapter
     *            The chapter part of the reference.
     * @param verse
     *            The verse part of the reference.
     * @exception NoSuchVerseException
     *                If the reference is illegal
     */
    public void validate(BibleBook book, int chapter, int verse) throws NoSuchVerseException {
        validate(book, chapter, verse, false);
    }

    /**
     * Does the following represent a real verse?. It is code like this that
     * makes me wonder if I18 is done well/worth doing. All this code does is
     * check if the numbers are valid, but the exception handling code is huge
     * :(
     *
     * @param book
     *            The book part of the reference.
     * @param chapter
     *            The chapter part of the reference.
     * @param verse
     *            The verse part of the reference.
     * @param silent
     *            true to indicate we do not want to throw an exception
     * @return true if validation was succesful
     * @exception NoSuchVerseException
     *                If the reference is illegal and silent was false
     */
    public boolean validate(BibleBook book, int chapter, int verse, boolean silent) throws NoSuchVerseException {
        // Check the book
        if (book == null) {
            if (silent) {
                return false;
            }
            // TRANSLATOR: The user did not supply a book for a verse reference.
            throw new NoSuchVerseException(JSOtherMsg.lookupText("Book must not be null"));
        }

        // Check the chapter
        int maxChapter = getLastChapter(book);
        if (chapter < 0 || chapter > maxChapter) {
            if (silent) {
                return false;
            }
            // TRANSLATOR: The user supplied a chapter that was out of bounds. This tells them what is allowed.
            // {0} is the lowest value that is allowed. This is always 0.
            // {1,number,integer} is the place holder for the highest chapter number in the book. The format is special in that it will present it in the user's preferred format.
            // {2} is a placeholder for the Bible book name.
            // {3,number,integer} is a placeholder for the chapter number that the user gave.
            throw new NoSuchVerseException(JSMsg.gettext("Chapter should be between {0} and {1,number,integer} for {2} (given {3,number,integer}).",
                    Integer.valueOf(0), Integer.valueOf(maxChapter), getPreferredName(book), Integer.valueOf(chapter)
            ));
        }

        // Check the verse
        int maxVerse = getLastVerse(book, chapter);
        if (verse < 0 || verse > maxVerse) {
            if (silent) {
                return false;
            }
            // TRANSLATOR: The user supplied a verse number that was out of bounds. This tells them what is allowed.
            // {0} is the lowest value that is allowed. This is always 0.
            // {1,number,integer} is the place holder for the highest verse number in the chapter. The format is special in that it will present it in the user's preferred format.
            // {2} is a placeholder for the Bible book name.
            // {3,number,integer} is a placeholder for the chapter number that the user gave.
            // {4,number,integer} is a placeholder for the verse number that the user gave.
            throw new NoSuchVerseException(JSMsg.gettext("Verse should be between {0} and {1,number,integer} for {2} {3,number,integer} (given {4,number,integer}).",
                    Integer.valueOf(0), Integer.valueOf(maxVerse), getPreferredName(book), Integer.valueOf(chapter), Integer.valueOf(verse)
                    ));
        }
        return true;
    }

    /**
     * Fix up these verses so that they are as valid a possible. This is
     * currently done so that we can say "Gen 1:1" + 31 = "Gen 1:32" and
     * "Gen 1:32".patch() is "Gen 2:1".
     * <p>
     * There is another patch system that allows us to use large numbers to mean
     * "the end of" so "Gen 1:32".otherPatch() gives "Gen 1:31". This could be
     * useful to allow the user to enter things like "Gen 1:99" meaning the end
     * of the chapter. Or "Isa 99:1" to mean the last chapter in Isaiah verse 1
     * or even "Rev 99:99" to mean the last verse in the Bible.
     * <p>
     * However I have not implemented this because I've used a different
     * convention: "Gen 1:$" (OLB compatible) or "Gen 1:ff" (common commentary
     * usage) to mean the end of the chapter - So the functionality is there
     * anyway.
     * <p>
     * I think that getting into the habit of typing "Gen 1:99" is bad. It could
     * be the source of surprises "Psa 119:99" is not what you'd might expect,
     * and neither is "Psa 99:1" is you wanted the last chapter in Psalms -
     * expecting us to type "Psa 999:1" seems like we're getting silly.
     * <p>
     * However despite this maybe we should provide the functionality anyway.
     *
     * @param book the book to obtain
     * @param chapter the supposed chapter
     * @param verse the supposed verse
     * @return The resultant verse.
     */
    public Verse patch(BibleBook book, int chapter, int verse) {
        BibleBook patchedBook = book;
        int patchedChapter = chapter;
        int patchedVerse = verse;

        // If the book is null,
        // then patch to the first book in the reference system
        if (patchedBook == null) {
            patchedBook = bookList.getFirstBook();
        }
        // If they are too small
        if (patchedChapter < 0) {
            patchedChapter = 0;
        }
        if (patchedVerse < 0) {
            patchedVerse = 0;
        }

        // Goal is to start in the current book and go forward that number of chapters
        // which might cause one to land in a later book.
        // For each book, the chapters number from 0 to n, where n is the last chapter number.
        // So if we want Genesis 53, then that would be 3 chapters into Exodus,
        // which would be chapter 2.
        while (patchedBook != null && patchedChapter > getLastChapter(patchedBook)) {
            patchedChapter -= getLastChapter(patchedBook) + 1;
            patchedBook = bookList.getNextBook(patchedBook);
        }

        // At this point we have a valid chapter.
        // Now we do the same for the verses.
        // For each book, the chapters number from 0 to n, where n is the last chapter number.
        // So if we want Genesis 49:36, then that would be 3 verses into Genesis 50,
        // which would be verse 50:2.     
        while (patchedBook != null && patchedVerse > getLastVerse(patchedBook, patchedChapter)) {
            patchedVerse -= getLastVerse(patchedBook, patchedChapter) + 1;
            patchedChapter += 1;

            if (patchedChapter > getLastChapter(patchedBook)) {
                patchedChapter -= getLastChapter(patchedBook) + 1;
                patchedBook = bookList.getNextBook(patchedBook);
            }
        }

        // If we have gone beyond the last book
        // then return the last chapter and verse in the last book
        if (patchedBook == null) {
            patchedBook = bookList.getLastBook();
            patchedChapter = getLastChapter(patchedBook);
            patchedVerse = getLastVerse(patchedBook, patchedChapter);
        }

        return new Verse(this, patchedBook, patchedChapter, patchedVerse);
    }

    public static void dump(PrintStream out, String name, BibleBookList bookList, int[][] array) {
        String vstr1 = "";
        String vstr2 = "";
        int count = 0;
        out.println("    private final int[][] " + name + " =");
        out.println("    {");
        // Output an array just like lastVerse, indexed by book and chapter,
        // that accumulates verse counts for offsets,
        // having a sentinel at the end.
        int bookCount = array.length;
        for (int bookIndex = 0; bookIndex < bookCount; bookIndex++) {
            count = 0;
            out.print("        // ");
            if (bookIndex < bookList.getBookCount()) {
                BibleBook book = bookList.getBook(bookIndex);
                out.println(book.getOSIS());
            } else {
                out.println("Sentinel");
            }
            out.print("        { ");

            int numChapters = array[bookIndex].length;
            for (int chapterIndex = 0; chapterIndex < numChapters; chapterIndex++) {

                // Pretty print with 10 items per line
                if (count++ % 10 == 0) {
                    out.println();
                    out.print("            ");
                }

                // Output the offset for the chapter introduction
                // This is referenced with a verse number of 0
                vstr1 = "     " + array[bookIndex][chapterIndex];
                vstr2 = vstr1.substring(vstr1.length() - 5);
                out.print(vstr2 + ", ");
            }
            out.println();
            out.println("        },");
        }
        out.println("    };");
    }

    public static void optimize(PrintStream out, BibleBookList bookList, int[][] lastVerse) {
        String vstr1 = "";
        String vstr2 = "";
        int count = 0;
        int ordinal = 0;
        out.println("    private final int[][] chapterStarts =");
        out.println("    {");
        // Output an array just like lastVerse, indexed by book and chapter,
        // that accumulates verse counts for offsets,
        // having a sentinel at the end.
        int bookIndex = 0;
        int ntStartOrdinal = 0;
        for (BibleBook book = bookList.getBook(0); book != null; book = bookList.getNextBook(book)) {
            count = 0;
            out.print("        // ");
            out.println(book.getOSIS());
            out.print("        { ");

            // Remember where the NT Starts
            if (book == BibleBook.INTRO_NT) {
                ntStartOrdinal = ordinal;
            }

            int numChapters = lastVerse[bookIndex].length;
            for (int chapterIndex = 0; chapterIndex < numChapters; chapterIndex++) {

                // Pretty print with 10 items per line
                if (count++ % 10 == 0) {
                    out.println();
                    out.print("            ");
                }

                // Output the offset for the chapter introduction
                // This is referenced with a verse number of 0
                vstr1 = "     " + ordinal;
                vstr2 = vstr1.substring(vstr1.length() - 5);
                out.print(vstr2 + ", ");
                // Set ordinal to the start of the next chapter or book introduction
                int versesInChapter = lastVerse[bookIndex][chapterIndex] + 1;
                ordinal += versesInChapter;
            }
            out.println();
            out.println("        },");
            bookIndex++;
        }

        // Output a sentinel value:
        // It is a book of one chapter starting with what would be the ordinal of the next chapter's introduction.
        vstr1 = "     " + ordinal;
        vstr2 = vstr1.substring(vstr1.length() - 5);
        out.println("        // Sentinel");
        out.println("        { ");
        out.println("            " + vstr2 + ", ");
        out.println("        },");
        out.println("    };");
        out.println();
        out.println("    /** The last ordinal number of the Old Testament */");
        out.println("    private int otMaxOrdinal = " + (ntStartOrdinal - 1) + ";");
        out.println("    /** The last ordinal number of the New Testament and the maximum ordinal number of this Reference System */");
        out.println("    private int ntMaxOrdinal = " + (ordinal - 1) + ";");
    }


    /** The OSIS name of the reference system. */
    private String name;

    /** The ordered list of books in this versification. */
    private BibleBookList bookList;

    /** The last ordinal number of the Old Testament */
    private int otMaxOrdinal;

    /** The last ordinal number of the New Testament and the maximum ordinal number of this Reference System */
    private int ntMaxOrdinal;

    /** Constant for the max verse number in each chapter */
    private int[][] lastVerse;

    /**
     * Constant for the ordinal number of the first verse in each chapter.
     */
    private int[][] chapterStarts;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = -6226916242596368765L;
}
