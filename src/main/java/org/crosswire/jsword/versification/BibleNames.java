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
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.versification;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.crosswire.common.util.CWClassLoader;
import org.crosswire.common.util.ClassUtil;
import org.crosswire.common.util.StringUtil;

/**
 * BibleNames deals with locale sensitive BibleBook name lookup conversions.
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith
 */
/* package */ final class BibleNames {
    /**
     * Create BibleNames for the given locale
     */
    /* package */ BibleNames(Versification v11n, Locale locale) {
        this.locale = locale;
        initialize(v11n);
    }

    /* package */ BookName getBookName(BibleBook book) {
        return books.get(book);
    }

    /**
     * Get the preferred name of a book. Altered by the case setting (see
     * setBookCase() and isFullBookName())
     *
     * @param book
     *            The book of the Bible
     * @return The full name of the book
     */
    /* package */ String getPreferredName(BibleBook book) {
        return getBookName(book).getPreferredName();
    }

    /**
     * Get the full name of a book (e.g. "Genesis"). Altered by the case setting
     * (see setBookCase())
     *
     * @param book
     *            The book of the Bible
     * @return The full name of the book
     */
    /* package */ String getLongName(BibleBook book) {
        return getBookName(book).getLongName();
    }

    /**
     * Get the short name of a book (e.g. "Gen"). Altered by the case setting
     * (see setBookCase())
     *
     * @param book
     *            The book of the Bible
     * @return The short name of the book
     */
    /* package */ String getShortName(BibleBook book) {
        return getBookName(book).getShortName();
    }

    /**
     * Get number of a book from its name.
     *
     * @param find
     *            The string to identify
     * @return The BibleBook, On error null
     */
    /* package */ BibleBook getBook(String find) {
        String match = BookName.normalize(find, locale);

        BookName bookName = fullNT.get(match);
        if (bookName != null) {
            return bookName.getBook();
        }

        bookName = shortNT.get(match);
        if (bookName != null) {
            return bookName.getBook();
        }

        bookName = altNT.get(match);
        if (bookName != null) {
            return bookName.getBook();
        }

        bookName = fullOT.get(match);
        if (bookName != null) {
            return bookName.getBook();
        }

        bookName = shortOT.get(match);
        if (bookName != null) {
            return bookName.getBook();
        }

        bookName = altOT.get(match);
        if (bookName != null) {
            return bookName.getBook();
        }

        bookName = fullNC.get(match);
        if (bookName != null) {
            return bookName.getBook();
        }

        bookName = shortNC.get(match);
        if (bookName != null) {
            return bookName.getBook();
        }

        bookName = altNC.get(match);
        if (bookName != null) {
            return bookName.getBook();
        }

        for (BookName aBook: books.values()) {
            if (aBook.match(match)) {
                return aBook.getBook();
            }
        }

        return null;
    }

    /**
     * Is the given string a valid book name. If this method returns true then
     * getBook() will return a BibleBook and not null.
     *
     * @param find
     *            The string to identify
     * @return true if the book name is known
     */
    /* package */ boolean isBookName(String find) {
        return getBook(find) != null;
    }

    /**
     * Load up the resources for Bible book and section names, and cache the
     * upper and lower versions of them.
     */
    private void initialize(Versification v11n) {
        int ntCount = 0;
        int otCount = 0;
        int ncCount = 0;
        BibleBook[] bibleBooks = BibleBook.values();
        for (BibleBook book : bibleBooks) {
            // Only need those books that are in this versification
            if (!v11n.containsBook(book)) {
                continue;
            }
            int ordinal = book.ordinal();
            if (ordinal > BibleBook.INTRO_OT.ordinal() && ordinal < BibleBook.INTRO_NT.ordinal()) {
                ++ntCount;
            } else if (ordinal > BibleBook.INTRO_NT.ordinal() && ordinal <= BibleBook.REV.ordinal()) {
                ++otCount;
            } else {
                ++ncCount;
            }
        }

        // Create the book name maps
        books = new LinkedHashMap<BibleBook, BookName>(ntCount + otCount + ncCount);

        String className = BibleNames.class.getName();
        String shortClassName = ClassUtil.getShortClassName(className);
        ResourceBundle resources = ResourceBundle.getBundle(shortClassName, locale, CWClassLoader.instance(BibleNames.class));

        fullNT = new HashMap<String, BookName>(ntCount);
        shortNT = new HashMap<String, BookName>(ntCount);
        altNT = new HashMap<String, BookName>(ntCount);
        for (int i = BibleBook.MATT.ordinal(); i <= BibleBook.REV.ordinal(); ++i) {
            BibleBook book = bibleBooks[i];
            if (v11n.containsBook(book)) {
                store(resources, book, fullNT, shortNT, altNT);
            }
        }

        fullOT = new HashMap<String, BookName>(otCount);
        shortOT = new HashMap<String, BookName>(otCount);
        altOT = new HashMap<String, BookName>(otCount);
        for (int i = BibleBook.GEN.ordinal(); i <= BibleBook.MAL.ordinal(); ++i) {
            BibleBook book = bibleBooks[i];
            if (v11n.containsBook(book)) {
                store(resources, book, fullOT, shortOT, altOT);
            }
        }

        fullNC = new HashMap<String, BookName>(ncCount);
        shortNC = new HashMap<String, BookName>(ncCount);
        altNC = new HashMap<String, BookName>(ncCount);
        if (v11n.containsBook(BibleBook.INTRO_BIBLE)) {
            store(resources, BibleBook.INTRO_BIBLE, fullNC, shortNC, altNC);
        }
        if (v11n.containsBook(BibleBook.INTRO_OT)) {
            store(resources, BibleBook.INTRO_OT, fullNC, shortNC, altNC);
        }
        if (v11n.containsBook(BibleBook.INTRO_NT)) {
            store(resources, BibleBook.INTRO_NT, fullNC, shortNC, altNC);
        }
        for (int i = BibleBook.REV.ordinal() + 1; i < bibleBooks.length; ++i) {
            BibleBook book = bibleBooks[i];
            if (v11n.containsBook(book)) {
                store(resources, book, fullNC, shortNC, altNC);
            }
        }
    }

    private void store(ResourceBundle resources, BibleBook book, Map fullMap, Map shortMap, Map altMap) {
        String osisName = book.getOSIS();

        String fullBook = getString(resources, osisName + FULL_KEY);

        String shortBook = getString(resources, osisName + SHORT_KEY);
        if (shortBook.length() == 0) {
            shortBook = fullBook;
        }

        String altBook = getString(resources, osisName + ALT_KEY);

        BookName bookName = new BookName(locale, BibleBook.fromOSIS(osisName), fullBook, shortBook, altBook);
        books.put(book, bookName);

        fullMap.put(bookName.getNormalizedLongName(), bookName);

        shortMap.put(bookName.getNormalizedShortName(), bookName);

        String[] alternates = StringUtil.split(BookName.normalize(altBook, locale), ',');

        for (int j = 0; j < alternates.length; j++) {
            altMap.put(alternates[j], bookName);
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

    /** The locale for the Bible Names */
    private Locale locale;

    /**
     * The collection of BookNames by BibleBooks.
     */
    private LinkedHashMap<BibleBook, BookName> books;

    /**
     * The full names of the New Testament books of the Bible
     * normalized, generated at runtime
     */
    private Map<String, BookName> fullNT;

    /**
     * The full names of the Old Testament books of the Bible
     * normalized, generated at runtime
     */
    private Map<String, BookName> fullOT;

    /**
     * The full names of the Deuterocanonical books of the Bible
     * normalized, generated at runtime
     */
    private Map<String, BookName> fullNC;

    /**
     * Standard shortened names for the New Testament books of the Bible,
     * normalized, generated at runtime.
     */
    private Map<String, BookName> shortNT;

    /**
     * Standard shortened names for the Old Testament books of the Bible
     * normalized, generated at runtime.
     */
    private Map<String, BookName> shortOT;

    /**
     * Standard shortened names for the Deuterocanonical books of the Bible
     * normalized, generated at runtime.
     */
    private Map<String, BookName> shortNC;

    /**
     * Alternative shortened names for the New Testament books of the Bible
     * normalized, generated at runtime.
     */
    private Map<String, BookName> altNT;

    /**
     * Alternative shortened names for the Old Testament books of the Bible
     * normalized, generated at runtime.
     */
    private Map<String, BookName> altOT;

    /**
     * Alternative shortened names for the Deuterocanonical books of the Bible
     * normalized, generated at runtime.
     */
    private Map<String, BookName> altNC;
}
