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
import org.crosswire.common.util.ClassUtil;
import org.crosswire.common.util.StringUtil;

/**
 * BibleNames deals with locale sensitive BibleBook name lookup conversions.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
/* package */ final class BibleNames {
    /**
     * Create BibleNames for the given locale
     */
    /* package */ BibleNames(Locale locale) {
        this.locale = locale;
        initialize();
    }

    /* package */ BookName getBookName(BibleBook book) {
        // This is faster than doing the check explicitly, unless
        // The exception is actually thrown, then it is a lot slower
        // I'd like to think that the norm is to get it right
        return books[book.ordinal()];
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

        BookName bookName = fullBooksMap.get(match);
        if (bookName != null) {
            return bookName.getBook();
        }

        bookName = shortBooksMap.get(match);
        if (bookName != null) {
            return bookName.getBook();
        }

        bookName = altBooksMap.get(match);
        if (bookName != null) {
            return bookName.getBook();
        }

        for (int i = 0; i < books.length; i++) {
            bookName = books[i];
            if (bookName.match(match)) {
                return bookName.getBook();
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
    private void initialize() {
        int booksInBible = BibleBook.values().length;

        books = new BookName[booksInBible];

        // Create the book name maps
        fullBooksMap = new HashMap<String, BookName>(booksInBible);
        shortBooksMap = new HashMap<String, BookName>(booksInBible);

        altBooksMap = new HashMap<String, BookName>(booksInBible);

        String className = BibleNames.class.getName();
        String shortClassName = ClassUtil.getShortClassName(className);
        ResourceBundle resources = ResourceBundle.getBundle(shortClassName, locale, CWClassLoader.instance(BibleNames.class));

        for (BibleBook book: BibleBook.values()) {
            String osisName = book.getOSIS();

            String fullBook = getString(resources, osisName + FULL_KEY);

            String shortBook = getString(resources, osisName + SHORT_KEY);
            if (shortBook.length() == 0) {
                shortBook = fullBook;
            }

            String altBook = getString(resources, osisName + ALT_KEY);

            BookName bookName = new BookName(locale, BibleBook.fromOSIS(osisName), fullBook, shortBook, altBook);
            books[book.ordinal()] = bookName;

            fullBooksMap.put(bookName.getNormalizedLongName(), bookName);

            shortBooksMap.put(bookName.getNormalizedShortName(), bookName);
            
            String[] alternates = StringUtil.split(BookName.normalize(altBook, locale), ',');

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

    private BookName[] books;

    /** The locale for the Bible Names */
    private Locale locale;

    /**
     * The full names of the book of the Bible, normalized, generated at runtime
     */
    private Map<String, BookName> fullBooksMap;

    /**
     * Standard shortened names for the book of the Bible, normalized, generated
     * at runtime.
     */
    private Map<String, BookName> shortBooksMap;

    /**
     * Alternative shortened names for the book of the Bible, normalized,
     * generated at runtime
     */
    private Map<String, BookName> altBooksMap;
}
