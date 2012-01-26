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
 * Copyright: 2005 - 2012
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.versification;

import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.book.CaseType;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.versification.system.Versifications;

/**
 * BibleInfo is a static class that deals with Bible book names, and conversion to and from
 * ordinal number and Verse.
 * <p>This class is likely to be reworked in it's entirety. It is really only true
 * of the KJV Bible. It is not true of other versifications such as Luther's.
 * </p>
 *
 * @deprecated Use Versifications.instance().getDefaultVersification() instead.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
@Deprecated
public final class BibleInfo {
    /**
     * constant for the old testament
     * @deprecated use {@link Testament#OLD} instead.
     */
    @Deprecated
    public static final int TESTAMENT_OLD = 0;

    /**
     * constant for the new testament
     * @deprecated use {@link Testament#NEW} instead.
     */
    @Deprecated
    public static final int TESTAMENT_NEW = 1;

    /**
     * Ensure that we can not be instantiated
     */
    private BibleInfo() {
    }

    /**
     * Get the immediately following book in the current versification.
     * @param book
     * @return the next book or null if no following book
     * @deprecated use Versification.getBooks().getNextBook(BibleBook) instead
     *             see {@link Versification#getBooks()} and {@link BibleBookList#getNextBook(BibleBook)}.
     */
    @Deprecated
    public static BibleBook getNextBook(BibleBook book) {
        return v11n.getBooks().getNextBook(book);
    }

    /**
     * Get the immediately prior book in the current versification.
     * @param book
     * @return the previous book or null if no previous book
     * @deprecated use Versification.getBooks().getPreviousBook(BibleBook) instead
     *             see {@link Versification#getBooks()} and {@link BibleBookList#getPreviousBook(BibleBook)}.
     */
    @Deprecated
    public static BibleBook getPreviousBook(BibleBook book) {
        return v11n.getBooks().getPreviousBook(book);
    }

    /**
     * Get the ordered array of books belonging to this versification.
     * This includes the 3 introductions.
     *
     * @return the array of books
     * @deprecated use {@link Versification#getBooks()} instead
     */
    @Deprecated
    public static BibleBook[] getBooks() {
        return BibleBook.getBooks();
    }

    /**
     * Get the last valid chapter number for a book.
     *
     * @param book
     *            The book part of the reference.
     * @return The last valid chapter number for a book.
     * @deprecated use {@link Versification#getLastChapter(BibleBook)} instead
     */
    @Deprecated
    public static int chaptersInBook(BibleBook book) {
        return v11n.getLastChapter(book);
    }

    /**
     * Get the last valid verse number for a chapter.
     *
     * @param book
     *            The book part of the reference.
     * @param chapter
     *            The current chapter
     * @return The last valid verse number for a chapter
     *                If the book or chapter number is not valid
     * @deprecated use {@link Versification#getLastVerse(BibleBook, int)} instead
     */
    @Deprecated
    public static int versesInChapter(BibleBook book, int chapter) {
        return v11n.getLastVerse(book, chapter);
    }

    /**
     * The maximum number of verses in the Bible, including module, testament, book and chapter introductions.
     *
     * @return the number of addressable verses in this versification.
     * @deprecated use {@link Versification#maximumOrdinal()} instead
     */
    @Deprecated
    public static int maximumOrdinal() {
        return v11n.maximumOrdinal();
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
     * @deprecated use {@link Versification#getOrdinal()} instead
     */
    @Deprecated
    public static int getOrdinal(Verse verse) {
        return v11n.getOrdinal(verse);
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
     * @deprecated use {@link Versification#getTestamentOrdinal()} instead
     */
    @Deprecated
    public static int getTestamentOrdinal(int ordinal) {
        return v11n.getTestamentOrdinal(ordinal);
    }

    /**
     * Get the testament of a given verse
     * @deprecated use {@link Versification#getTestament(int)} instead
     */
    @Deprecated
    public static Testament getTestament(int ordinal) {
        return v11n.getTestament(ordinal);
    }

    /**
     * Give the count of verses in the testament or the whole Bible.
     *
     * @param testament The testament to count. If null, then all testaments.
     * @return the number of verses in the testament
     * @deprecated use {@link Versification#getCount(Testament)} instead
     */
    @Deprecated
    public static int getCount(Testament testament) {
        return v11n.getCount(testament);
    }

    /**
     * Where does this verse come in the Bible. This will unwind the value returned by getOrdinal(Verse).
     *
     * @param ordinal
     *            The ordinal number of the verse
     * @return A Verse
     * @exception NoSuchVerseException
     *                If the reference is illegal
     * @deprecated use {@link Versification#decodeOrdinal(int)} instead
     */
    @Deprecated
    public static Verse decodeOrdinal(int ordinal) {
        return v11n.decodeOrdinal(ordinal);
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
     * @deprecated use {@link Versification#validate(BibleBook, int, int)} instead
     */
    @Deprecated
    public static void validate(BibleBook book, int chapter, int verse) throws NoSuchVerseException {
        v11n.validate(book, chapter, verse);
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
     * @deprecated use {@link Versification#patch(BibleBook, int, int)} instead
     */
    @Deprecated
    public static Verse patch(BibleBook book, int chapter, int verse) {
        return v11n.patch(book, chapter, verse);
    }

    /**
     * Count the books in the Bible.
     *
     * @return The number of books in the Bible, including the three introductions
     * @deprecated use {@link Versification#getBooks()} and {@link BibleBookList#getBookCount()} instead
     */
    @Deprecated
    public static int booksInBible() {
        return v11n.getBooks().getBookCount();
    }

    private static Versification v11n = Versifications.instance().getDefaultVersification();

    /**
     * Get the BookName.
     * This is merely a convenience function that validates that book is not null,
     * throwing NoSuchVerseException if it is.
     *
     * @param book
     *            The book of the Bible
     * @return The requested BookName
     * @exception NoSuchVerseException
     *                If the book is not valid
     * @deprecated Use {@link BibleBook#getBookName()} instead.
     */
    @Deprecated
    public static BookName getBookName(BibleBook book) throws NoSuchVerseException {
        try {
            return book.getBookName();
        } catch (NullPointerException ex) {
            throw new NoSuchVerseException(JSOtherMsg.lookupText("Book must not be null"));
        }
    }

    /**
     * Get number of a book from its name.
     *
     * @param find
     *            The string to identify
     * @return The BibleBook, On error null
     * @deprecated use {@link BibleBook#getBook(String)}
     */
    @Deprecated
    public static BibleBook getBook(String find) {
        return BibleBook.getBook(find);
    }

    /**
     * Is the given string a valid book name. If this method returns true then
     * getBook() will return a BibleBook and not throw an exception.
     *
     * @param find
     *            The string to identify
     * @return true when the book name is recognized
     * @deprecated use {@link BibleBook#isBook(String)}
     */
    @Deprecated
    public static boolean isBookName(String find) {
        return BibleBook.isBook(find);
    }

    /**
     * Count the chapters in the Bible.
     *
     * @return 1189 always - the number of chapters in the Bible
     * @deprecated do not use
     */
    @Deprecated
    public static int chaptersInBible() {
        return 1189;
    }

    /**
     * The maximum number of verses in the Bible, including module, testament, book and chapter introductions.
     * Note: it used to exclude introductions.
     *
     * @return the number of addressable verses in this versification.
     * @deprecated use {@link BibleInfo#maximumOrdinal()}
     */
    @Deprecated
    public static int versesInBible() {
        return maximumOrdinal();
    }

    /**
     * Get the preferred name of a book. Altered by the case setting (see
     * setBookCase() and isFullBookName())
     * This is merely a convenience function that validates that book is not null,
     * throwing NoSuchVerseException if it is.
     *
     * @param book
     *            The book of the Bible
     * @return The full name of the book
     * @exception NoSuchVerseException
     *                If the book is not valid
     * @deprecated Use {@link BibleBook#getPreferredName()} instead.
     */
    @Deprecated
    public static String getPreferredBookName(BibleBook book) throws NoSuchVerseException {
        try {
            return book.getPreferredName();
        } catch (NullPointerException ex) {
            throw new NoSuchVerseException(JSOtherMsg.lookupText("Book must not be null"));
        }
    }

    /**
     * Get the full name of a book (e.g. "Genesis"). Altered by the case setting
     * (see setBookCase())
     * This is merely a convenience function that validates that book is not null,
     * throwing NoSuchVerseException if it is.
     *
     * @param book
     *            The book of the Bible
     * @return The full name of the book
     * @exception NoSuchVerseException
     *                If the book is not valid
     * @deprecated Use {@link BibleBook#getLongName()} instead.
     */
    @Deprecated
    public static String getLongBookName(BibleBook book) throws NoSuchVerseException {
        try {
            return book.getLongName();
        } catch (NullPointerException ex) {
            throw new NoSuchVerseException(JSOtherMsg.lookupText("Book must not be null"));
        }
    }

    /**
     * Get the short name of a book (e.g. "Gen"). Altered by the case setting
     * (see setBookCase())
     * This is merely a convenience function that validates that book is not null,
     * throwing NoSuchVerseException if it is.
     *
     * @param book
     *            The book of the Bible
     * @return The short name of the book
     * @exception NoSuchVerseException
     *                If the book is not valid
     * @deprecated Use {@link BibleBook#getShortName()} instead.
     */
    @Deprecated
    public static String getShortBookName(BibleBook book) throws NoSuchVerseException {
        try {
            return book.getShortName();
        } catch (NullPointerException ex) {
            throw new NoSuchVerseException(JSOtherMsg.lookupText("Book must not be null"));
        }
    }

    /**
     * Get the OSIS name for a book.
     * This is merely a convenience function that validates that book is not null,
     * throwing NoSuchVerseException if it is.
     *
     * @param book
     *            The book of the Bible
     * @return the OSIS defined short name for a book
     * @exception NoSuchVerseException
     *                If the book is not valid
     * @deprecated Use {@link BibleBook#getOSIS()} instead.
     */
    @Deprecated
    public static String getOSISName(BibleBook book) throws NoSuchVerseException {
        try {
            return book.getOSIS();
        } catch (NullPointerException ex) {
            throw new NoSuchVerseException(JSOtherMsg.lookupText("Book must not be null"));
        }
    }

    /**
     * How many verses between verse1 and verse2 (inclusive).
     *
     * @param verse1
     *            The earlier verse.
     * @param verse2
     *            The later verse.
     * @return the number of verses
     * @exception NoSuchVerseException
     *                If either reference is illegal
     * @deprecated Use {@link Versification#subtract(Verse, Verse)} instead.
     */
    @Deprecated
    public static int verseCount(Verse verse1, Verse verse2) throws NoSuchVerseException {
        return verse2.subtract(verse1) + 1;
    }

    /**
     * This is only used by config.
     *
     * @param bookCase
     *            The new case to use for reporting book names
     * @exception IllegalArgumentException
     *                If the case is not between 0 and 2
     * @see #getCase()
     * @deprecated use {@link BookName#setCase(int)}
     */
    @Deprecated
    public static void setCase(int bookCase) {
        BookName.setCase(bookCase);
    }

    /**
     * This is only used by config
     *
     * @return The current case setting
     * @see #setCase(CaseType)
     * @deprecated use {@link BookName#getCase()}
     */
    @Deprecated
    public static int getCase() {
        return BookName.getCase();
    }

    /**
     * How do we report the names of the books?. These are static. This is on
     * the assumption that we will not want to have different sections of the
     * app using a different format. I expect this to be a good assumption, and
     * it saves passing a Book class around everywhere. CaseType.MIXED is not
     * allowed
     *
     * @param newBookCase
     *            The new case to use for reporting book names
     * @exception IllegalArgumentException
     *                If the case is not between 0 and 2
     * @see #getCase()
     * @deprecated use {@link BookName#setCase(CaseType)}
     */
    @Deprecated
    public static void setCase(CaseType newBookCase) {
        BookName.setCase(newBookCase);
    }

    /**
     * This is only used by config
     *
     * @return Whether the name is long or short. Default is Full (true).
     * @see #setFullBookName(boolean)
     * @deprecated use {@link BookName#isFullBookName()}
     */
    @Deprecated
    public static boolean isFullBookName() {
        return BookName.isFullBookName();
    }

    /**
     * Set whether the name should be full or abbreviated, long or short.
     *
     * @param fullName
     *            The new case to use for reporting book names
     * @see #isFullBookName()
     * @deprecated use {@link BookName#setFullBookName(boolean)}
     */
    @Deprecated
    public static void setFullBookName(boolean fullName) {
        BookName.setFullBookName(fullName);
    }

    /**
     * How do we report the names of the books?.
     *
     * @return The current case setting
     * @see #setCase(int)
     * @deprecated use {@link BookName#getDefaultCase()}
     */
    @Deprecated
    public static CaseType getDefaultCase() {
        return BookName.getDefaultCase();
    }

}
