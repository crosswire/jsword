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
package org.crosswire.jsword.versification;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.crosswire.common.util.CWClassLoader;
import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.passage.NoSuchVerseException;

/**
 * BibleNames is a static class that deals with Book name lookup conversions. We
 * start counting at 1 for books (so Genesis=1, Revelation=66). However
 * internally books start counting at 0 and go up to 65.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public final class BibleNames {
    /**
     * Create BibleNames for the given locale
     */
    public BibleNames(Locale locale) {
        this.locale = locale;
        initialize();
    }

    public BookName getName(int book) throws NoSuchVerseException {
        try {
            return books[book - 1];
        } catch (ArrayIndexOutOfBoundsException ex) {
            // This is faster than doing the check explicitly, unless
            // The exception is actually thrown, then it is a lot slower
            // I'd like to think that the norm is to get it right
            throw new NoSuchVerseException(Msg.BOOKS_BOOK, new Object[] {
                Integer.valueOf(book)
            });
        }
    }

    /**
     * Get the preferred name of a book. Altered by the case setting (see
     * setBookCase() and isLongBookName())
     * 
     * @param book
     *            The book number (1-66)
     * @return The full name of the book
     * @exception NoSuchVerseException
     *                If the book number is not valid
     */
    public String getPreferredName(int book) throws NoSuchVerseException {
        return getName(book).getPreferredName();
    }

    /**
     * Get the full name of a book (e.g. "Genesis"). Altered by the case setting
     * (see setBookCase())
     * 
     * @param book
     *            The book number (1-66)
     * @return The full name of the book
     * @exception NoSuchVerseException
     *                If the book number is not valid
     */
    public String getLongName(int book) throws NoSuchVerseException {
        return getName(book).getLongName();
    }

    /**
     * Get the short name of a book (e.g. "Gen"). Altered by the case setting
     * (see setBookCase())
     * 
     * @param book
     *            The book number (1-66)
     * @return The short name of the book
     * @exception NoSuchVerseException
     *                If the book number is not valid
     */
    public String getShortName(int book) throws NoSuchVerseException {
        return getName(book).getShortName();
    }

    /**
     * Get number of a book from its name.
     * 
     * @param find
     *            The string to identify
     * @return The book number (1 to 66) On error -1
     */
    public int getNumber(String find) {
        String match = BookName.normalize(find, locale);

        BookName bookName = fullBooksMap.get(match);
        if (bookName != null) {
            return bookName.getNumber();
        }

        bookName = shortBooksMap.get(match);
        if (bookName != null) {
            return bookName.getNumber();
        }

        bookName = altBooksMap.get(match);
        if (bookName != null) {
            return bookName.getNumber();
        }

        for (int i = 0; i < books.length; i++) {
            bookName = books[i];
            if (bookName.match(match)) {
                return bookName.getNumber();
            }
        }

        return -1;
    }

    /**
     * Is the given string a valid book name. If this method returns true then
     * getBookNumber() will return a number and not throw an exception.
     * 
     * @param find
     *            The string to identify
     * @return The book number (1 to 66)
     */
    public boolean isBookName(String find) {
        return getNumber(find) != -1;
    }

    /**
     * Load up the resources for Bible book and section names, and cache the
     * upper and lower versions of them.
     */
    private void initialize() {
        int booksInBible = BibleInfo.booksInBible();

        books = new BookName[booksInBible];

        // Create the book name maps
        fullBooksMap = new HashMap<String,BookName>(booksInBible);
        shortBooksMap = new HashMap<String,BookName>(booksInBible);

        altBooksMap = new HashMap<String,BookName>(booksInBible);

        ResourceBundle resources = ResourceBundle.getBundle(BibleNames.class.getName(), locale, CWClassLoader.instance(BibleNames.class));

        for (int i = 0; i < booksInBible; i++) {
            String osisName = "";
            try {
                osisName = OSISNames.getName(i + 1);
            } catch (NoSuchVerseException e) {
                assert false;
            }

            String fullBook = getString(resources, osisName + FULL_KEY);

            String shortBook = getString(resources, osisName + SHORT_KEY);
            if (shortBook.length() == 0) {
                shortBook = fullBook;
            }

            String altBook = getString(resources, osisName + ALT_KEY);

            BookName bookName = new BookName(locale, i + 1, fullBook, shortBook, altBook);
            books[i] = bookName;

            fullBooksMap.put(bookName.getNormalizedLongName(), bookName);

            shortBooksMap.put(bookName.getNormalizedShortName(), bookName);

            String[] alternates = StringUtil.split(altBook, ',');

            for (int j = 0; j < alternates.length; j++) {
                altBooksMap.put(alternates[j], bookName);
            }

        }
    }

    /*
     * Helper to make the code more readable.
     */
    private String getString(ResourceBundle resources, String key) {
        try {
            return resources.getString(key);
        } catch (MissingResourceException e) {
            assert false;
        }
        return null;
    }

    private static final String FULL_KEY = ".Full";
    private static final String SHORT_KEY = ".Short";
    private static final String ALT_KEY = ".Alt";

    /**
     * Handy book finder
     */
    public static final byte GENESIS = 1;
    public static final byte EXODUS = 2;
    public static final byte LEVITICUS = 3;
    public static final byte NUMBERS = 4;
    public static final byte DEUTERONOMY = 5;
    public static final byte JOSHUA = 6;
    public static final byte JUDGES = 7;
    public static final byte RUTH = 8;
    public static final byte SAMUEL1 = 9;
    public static final byte SAMUEL2 = 10;
    public static final byte KINGS1 = 11;
    public static final byte KINGS2 = 12;
    public static final byte CHRONICLES1 = 13;
    public static final byte CHRONICLES2 = 14;
    public static final byte EZRA = 15;
    public static final byte NEHEMIAH = 16;
    public static final byte ESTHER = 17;
    public static final byte JOB = 18;
    public static final byte PSALMS = 19;
    public static final byte PROVERBS = 20;
    public static final byte ECCLESIASTES = 21;
    public static final byte SONGOFSOLOMON = 22;
    public static final byte ISAIAH = 23;
    public static final byte JEREMIAH = 24;
    public static final byte LAMENTATIONS = 25;
    public static final byte EZEKIEL = 26;
    public static final byte DANIEL = 27;
    public static final byte HOSEA = 28;
    public static final byte JOEL = 29;
    public static final byte AMOS = 30;
    public static final byte OBADIAH = 31;
    public static final byte JONAH = 32;
    public static final byte MICAH = 33;
    public static final byte NAHUM = 34;
    public static final byte HABAKKUK = 35;
    public static final byte ZEPHANIAH = 36;
    public static final byte HAGGAI = 37;
    public static final byte ZECHARIAH = 38;
    public static final byte MALACHI = 39;
    public static final byte MATTHEW = 40;
    public static final byte MARK = 41;
    public static final byte LUKE = 42;
    public static final byte JOHN = 43;
    public static final byte ACTS = 44;
    public static final byte ROMANS = 45;
    public static final byte CORINTHIANS1 = 46;
    public static final byte CORINTHIANS2 = 47;
    public static final byte GALATIANS = 48;
    public static final byte EPHESIANS = 49;
    public static final byte PHILIPPIANS = 50;
    public static final byte COLOSSIANS = 51;
    public static final byte THESSALONIANS1 = 52;
    public static final byte THESSALONIANS2 = 53;
    public static final byte TIMOTHY1 = 54;
    public static final byte TIMOTHY2 = 55;
    public static final byte TITUS = 56;
    public static final byte PHILEMON = 57;
    public static final byte HEBREWS = 58;
    public static final byte JAMES = 59;
    public static final byte PETER1 = 60;
    public static final byte PETER2 = 61;
    public static final byte JOHN1 = 62;
    public static final byte JOHN2 = 63;
    public static final byte JOHN3 = 64;
    public static final byte JUDE = 65;
    public static final byte REVELATION = 66;

    private BookName[] books;

    /** The locale for the Bible Names */
    private Locale locale;

    /**
     * The full names of the book of the Bible, normalized, generated at runtime
     */
    private Map<String,BookName> fullBooksMap;

    /**
     * Standard shortened names for the book of the Bible, normalized, generated
     * at runtime.
     */
    private Map<String,BookName> shortBooksMap;

    /**
     * Alternative shortened names for the book of the Bible, normalized,
     * generated at runtime
     */
    private Map<String,BookName> altBooksMap;
}
