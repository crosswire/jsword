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
 * Copyright: 2012
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: org.eclipse.jdt.ui.prefs 1178 2006-11-06 12:48:02Z dmsmith $
 */
package org.crosswire.jsword.versification;

import java.io.PrintStream;

import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Verse;

/**
 * A named ReferenceSystem defines
 * the order of BibleBooks by Testament,
 * the number of chapters in each BibleBook,
 * the number of verses in each chapter.
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class ReferenceSystem {
    /**
     * Construct a ReferenceSystem.
     * 
     * @param osisName
     *            The name of this reference system
     * @param books
     *            An ordered list of books in this reference system. The list
     *            should always start with INTRO_BIBLE and INTRO_OT. The first
     *            New Testament book should be preceded by INTRO_NT.
     * @param lastVerse
     *            For each book in books, this has an array with one entry for
     *            each chapter including chapter 0 whose value is the highest
     *            numbered verse in that chapter.
     */
    public ReferenceSystem(String osisName, BibleBook[] books, int[][] lastVerse) {
        this.osisName = osisName;
        this.bookList = new BibleBookList(books);

        int ordinal = 0;
        int bookCount = lastVerse.length;

        // Create an independent copy of lastVerse.
        this.lastVerse = lastVerse.clone();
        for (int bookIndex = 0; bookIndex < bookCount; bookIndex++) {
            this.lastVerse[bookIndex] = lastVerse[bookIndex].clone();
        }

        // Initialize chapterStarts to be a parallel array to lastVerse,
        // but with chapter starts
        this.chapterStarts = new int[bookCount][];
        for (int bookIndex = 0; bookIndex < bookCount; bookIndex++) {

            // Remember where the OT ends
            if (bookList.getBook(bookIndex) == BibleBook.INTRO_NT) {
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
    }

    /**
     * Get the OSIS name for this ReferenceSystem.
     * @return the OSIS name of the ReferenceSystem
     */
    public String getOSISName() {
        return osisName;
    }

    public BibleBookList getBooks() {
        return bookList;
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
     * Create a new Verse being the last verse in the current book
     *
     * @return The last verse in this book
     */
    public Verse getLastVerseInBook(Verse verse) {
        BibleBook book = verse.getBook();
        int lastchap = BibleInfo.chaptersInBook(book);
        int lastverse = BibleInfo.versesInChapter(book, lastchap);

        return new Verse(book, lastchap, lastverse);
    }

    /**
     * Create a new Verse being the last verse in the current book
     *
     * @return The last verse in this book
     */
    public Verse getLastVerseInChapter(Verse verse) {
        BibleBook book = verse.getBook();
        int chapter = verse.getChapter();
        int lastverse = BibleInfo.versesInChapter(book, chapter);

        return new Verse(book, chapter, lastverse);
    }

    /**
     * Create a new Verse being the first verse in the current book
     *
     * @return The first verse in this book
     */
    public Verse getFirstVerseInBook(Verse verse) {
        return new Verse(verse.getBook(), 1, 1);
    }

    /**
     * Create a new Verse being the first verse in the current book
     *
     * @return The first verse in this book
     */
    public Verse getFirstVerseInChapter(Verse verse) {
        return new Verse(verse.getBook(), verse.getChapter(), 1);
    }

    /**
     * Is this verse the first in a chapter
     *
     * @return true or false ...
     */
    public boolean isStartOfChapter(Verse verse) {
        int v = verse.getVerse();
        return v == 1 || v == 0;
    }

    /**
     * Is this verse the first in a chapter
     *
     * @return true or false ...
     */
    public boolean isEndOfChapter(Verse verse) {
        int v = verse.getVerse();
        return v == getLastVerse(verse.getBook(), verse.getChapter());
    }

    /**
     * Is this verse the first in a chapter
     *
     * @return true or false ...
     */
    public boolean isStartOfBook(Verse verse) {
        int v = verse.getVerse();
        int c = verse.getChapter();
        return (v == 1 || v == 0) && (c == 1 || c == 0);
    }

    /**
     * Is this verse the last in the book
     *
     * @return true or false ...
     */
    public boolean isEndOfBook(Verse verse) {
        BibleBook b = verse.getBook();
        int v = verse.getVerse();
        int c = verse.getChapter();
        return v == BibleInfo.versesInChapter(b, c) && c == BibleInfo.chaptersInBook(b);
    }

    /**
     * Is this verse in the same chapter as that one
     *
     * @param that
     *            The verse to compare to
     * @return true or false ...
     */
    public boolean isSameChapter(Verse a, Verse that) {
        return a.getBook() == that.getBook() && a.getChapter() == that.getChapter();
    }

    /**
     * Is this verse in the same book as that one
     *
     * @param that
     *            The verse to compare to
     * @return true or false ...
     */
    public boolean isSameBook(Verse a, Verse that) {
        return a.getBook() == that.getBook();
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
    public boolean adjacentTo(Verse first, Verse second) {
        return Math.abs(getOrdinal(second) - getOrdinal(first)) == 1;
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
        return getOrdinal(start) - getOrdinal(end);
    }

    /**
     * Get the verse n down from here this Verse.
     *
     * @param n
     *            The number to count down by
     * @return The new Verse
     */
    public Verse subtract(Verse verse, int n) {
        return decodeOrdinal(getOrdinal(verse) - n);
    }

    /**
     * Get the verse that is a few verses on from the one we've got.
     *
     * @param n
     *            the number of verses later than the one we're one
     * @return The new verse
     */
    public Verse add(Verse verse, int n) {
        return decodeOrdinal(getOrdinal(verse) + n);
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
     * @param verse
     *            The verse to convert
     * @return The ordinal number of verses
     */
    public int getOrdinal(Verse verse) {
        return chapterStarts[bookList.getOrdinal(verse.getBook())][verse.getChapter()] + verse.getVerse();
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
     * @param verse
     *            The verse to convert
     * @return The ordinal number of verses
     */
    public int getTestamentOrdinal(int ordinal) {
        int nt_ordinal = otMaxOrdinal + 1;
        if (ordinal >= nt_ordinal) {
            return ordinal - nt_ordinal + 1;
        }
        return ordinal;
    }

    /**
     * Get the testament of a given verse
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
     * If the ordinal value is less than 0 or greater than the last verse in this ReferenceSystem,
     * then constrain it to the first or last verse in this ReferenceSystem.
     *
     * @param ordinal
     *            The ordinal number of the verse
     * @return A Verse
     */
    public Verse decodeOrdinal(int ordinal) {
        int ord = ordinal;
        BibleBook book = null;
        int bookIndex = -1;
        int chapterIndex = 0;
        int verse = 0;

        if (ord < 0) {
            ord = 0;
        } else if (ord > ntMaxOrdinal) {
            ord = ntMaxOrdinal;
        }

        // Handle three special cases
        // Book/Module introduction
        if (ord == 0) {
            return new Verse(BibleBook.INTRO_BIBLE, 0, 0);
        }

        // OT introduction
        if (ord == 1) {
            return new Verse(BibleBook.INTRO_OT, 0, 0);
        }

        // NT introduction
        if (ord == otMaxOrdinal + 1) {
            return new Verse(BibleBook.INTRO_NT, 0, 0);
        }

        int lastBook = chapterStarts.length - 1;
        for (int b = lastBook; b >= 0; b--) {
            // A book has a slot for a heading followed by a slot for a chapter heading.
            // These precede the start of the chapter.
            if (ord >= chapterStarts[b][0]) {
                bookIndex = b;
                break;
            }
        }

        book = bookList.getBook(bookIndex);
        int cib = getLastChapter(book);
        for (int c = cib; c >= 0; c--) {
            if (ord >= chapterStarts[bookIndex][c]) {
                chapterIndex = c;
                break;
            }
        }

        if (chapterIndex > 0) {
            verse = ord - chapterStarts[bookIndex][chapterIndex];
        }

        return new Verse(book, chapterIndex, verse, true);
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

        // Check the book
        if (book == null) {
            // TRANSLATOR: The user did not supply a book for a verse reference.
            throw new NoSuchVerseException(JSOtherMsg.lookupText("Book must not be null"));
        }

        // Check the chapter
        int maxChapter = getLastChapter(book);
        if (chapter < 0 || chapter > maxChapter) {
            // TRANSLATOR: The user supplied a chapter that was out of bounds. This tells them what is allowed.
            // {0} is the lowest value that is allowed. This is always 0.
            // {1,number,integer} is the place holder for the highest chapter number in the book. The format is special in that it will present it in the user's preferred format.
            // {2} is a placeholder for the Bible book name.
            // {3,number,integer} is a placeholder for the chapter number that the user gave.
            throw new NoSuchVerseException(JSMsg.gettext("Chapter should be between {0} and {1,number,integer} for {2} (given {3,number,integer}).",
                    Integer.valueOf(0), Integer.valueOf(maxChapter), book.getPreferredName(), Integer.valueOf(chapter)
                    ));
        }

        // Check the verse
        int maxVerse = getLastVerse(book, chapter);
        if (verse < 0 || verse > maxVerse) {
            // TRANSLATOR: The user supplied a verse number that was out of bounds. This tells them what is allowed.
            // {0} is the lowest value that is allowed. This is always 0.
            // {1,number,integer} is the place holder for the highest verse number in the chapter. The format is special in that it will present it in the user's preferred format.
            // {2} is a placeholder for the Bible book name.
            // {3,number,integer} is a placeholder for the chapter number that the user gave.
            // {4,number,integer} is a placeholder for the verse number that the user gave.
            throw new NoSuchVerseException(JSMsg.gettext("Verse should be between {0} and {1,number,integer} for {2} {3,number,integer} (given {4,number,integer}).",
                    Integer.valueOf(0), Integer.valueOf(maxVerse), book.getPreferredName(), Integer.valueOf(chapter), Integer.valueOf(verse)
                    ));
        }
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

        // If the book is null, then patch to GENESIS
        if (patchedBook == null) {
            patchedBook = BibleBook.GEN;
        }
        // If they are too small
        if (patchedChapter < 0) {
            patchedChapter = 0;
        }
        if (patchedVerse < 0) {
            patchedVerse = 0;
        }

        while (patchedChapter > getLastChapter(patchedBook)) {
            patchedChapter -= getLastChapter(patchedBook);
            patchedBook = bookList.getNextBook(patchedBook);

            if (patchedBook == null) {
                patchedBook = BibleBook.REV;
                patchedChapter = getLastChapter(patchedBook);
                patchedVerse = getLastVerse(patchedBook, patchedChapter);
                return new Verse(patchedBook, patchedChapter, patchedVerse);
            }
        }

        while (patchedVerse > getLastVerse(patchedBook, patchedChapter)) {
            patchedVerse -= getLastVerse(patchedBook, patchedChapter);
            patchedChapter += 1;

            if (patchedChapter > getLastChapter(patchedBook)) {
                patchedChapter -= getLastChapter(patchedBook);
                patchedBook = bookList.getNextBook(patchedBook);

                if (patchedBook == null) {
                    patchedBook = BibleBook.REV;
                    patchedChapter = getLastChapter(patchedBook);
                    patchedVerse = getLastVerse(patchedBook, patchedChapter);
                    return new Verse(patchedBook, patchedChapter, patchedVerse);
                }
            }
        }

        return new Verse(patchedBook, patchedChapter, patchedVerse);
    }

    /** The OSIS name of the reference system. */
    private String osisName;


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

}
