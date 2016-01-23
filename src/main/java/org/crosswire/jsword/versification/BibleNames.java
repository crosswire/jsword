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
 * © CrossWire Bible Society, 2005 - 2016
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
import org.crosswire.jsword.internationalisation.LocaleProviderManager;

/**
 * BibleNames deals with locale sensitive BibleBook name lookup conversions.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
 */
public final class BibleNames {
    /**
     * Get the singleton instance of BibleNames.
     *
     * @return the singleton
     */
    public static BibleNames instance() {
        return instance;
    }

    /**
     * Get the BookName.
     *
     * @param book the desired book
     * @return The requested BookName or null if not in this versification
     */
    public BookName getBookName(BibleBook book) {
        return getLocalizedBibleNames().getBookName(book);
    }

    /**
     * Get the preferred name of a book. Altered by the case setting (see
     * setBookCase() and isFullBookName())
     *
     * @param book the desired book
     * @return The full name of the book or blank if not in this versification
     */
    public String getPreferredName(BibleBook book) {
        return getLocalizedBibleNames().getPreferredName(book);
    }

    /**
     * Get the full name of a book (e.g. "Genesis"). Altered by the case setting
     * (see setBookCase())
     *
     * @param book the book
     * @return The full name of the book or blank if not in this versification
     */
    public String getLongName(BibleBook book) {
        return getLocalizedBibleNames().getLongName(book);
    }

    /**
     * Get the short name of a book (e.g. "Gen"). Altered by the case setting
     * (see setBookCase())
     *
     * @param book the book
     * @return The short name of the book or blank if not in this versification
     */
    public String getShortName(BibleBook book) {
        return getLocalizedBibleNames().getShortName(book);
    }

    /**
     * Get a book from its name.
     *
     * @param find
     *            The string to identify
     * @return The BibleBook, On error null
     */
    public BibleBook getBook(String find) {
        BibleBook book = null;
        if (containsLetter(find)) {
            book = BibleBook.fromOSIS(find);

            if (book == null) {
                book = getLocalizedBibleNames().getBook(find, false);
            }

            if (book == null) {
                book = englishBibleNames.getBook(find, false);
            }

            if (book == null) {
                book = getLocalizedBibleNames().getBook(find, true);
            }

            if (book == null) {
                book = englishBibleNames.getBook(find, true);
            }
        }

        return book;
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
     * Load name information for BibleNames for a given locale.
     * This routine is for testing the underlying NameList.
     * 
     * @param locale
     */
    void load(Locale locale) {
        NameList bibleNames = new NameList(locale);
        if (localizedBibleNames.get(locale) == null) {
            localizedBibleNames.put(locale, bibleNames);
        }
    }

    /**
     * This class is a singleton, enforced by a private constructor.
     */
    private BibleNames() {
        localizedBibleNames = new HashMap<Locale, NameList>();
        englishBibleNames = getBibleNamesForLocale(Locale.ENGLISH);
        localizedBibleNames.put(Locale.ENGLISH, englishBibleNames);
    }

    /**
     * Gets the localized bible names, based on the {@link LocaleProviderManager}
     *
     * @return the localized bible names
     */
    private NameList getLocalizedBibleNames() {
        // Get the current Locale
        return getBibleNamesForLocale(LocaleProviderManager.getLocale());
    }

    /**
     * Gets the bible names for a specific locale.
     *
     * @param locale the locale
     * @return the bible names for locale
     */
    private NameList getBibleNamesForLocale(Locale locale) {
        NameList bibleNames = localizedBibleNames.get(locale);
        if (bibleNames == null) {
            bibleNames = new NameList(locale);
            localizedBibleNames.put(locale, bibleNames);
        }

        return bibleNames;
    }

    /**
     * This is simply a convenience function to wrap Character.isLetter()
     *
     * @param text
     *            The string to be parsed
     * @return true if the string contains letters
     */
    private static boolean containsLetter(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (Character.isLetter(text.charAt(i))) {
                return true;
            }
        }

        return false;
    }

    /**
     * NameList is the internal, internationalize list of names
     * for a locale.
     *
     * @see gnu.lgpl.License The GNU Lesser General Public License for details.<br>
     *      The copyright to this program is held by its authors.
     * @author DM Smith
     */
    private class NameList {
        /**
         * Create NameList for the given locale
         */
        NameList(Locale locale) {
            this.locale = locale;
            initialize();
        }

        BookName getBookName(BibleBook book) {
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
        String getPreferredName(BibleBook book) {
            return getBookName(book).getPreferredName();
        }

        /**
         * Get the full name of a book (e.g. "Genesis"). Altered by the case
         * setting (see setBookCase())
         * 
         * @param book
         *            The book of the Bible
         * @return The full name of the book
         */
        String getLongName(BibleBook book) {
            return getBookName(book).getLongName();
        }

        /**
         * Get the short name of a book (e.g. "Gen"). Altered by the case
         * setting (see setBookCase())
         * 
         * @param book
         *            The book of the Bible
         * @return The short name of the book
         */
        String getShortName(BibleBook book) {
            return getBookName(book).getShortName();
        }

        /**
         * Get a book from its name.
         * 
         * @param find
         *            The string to identify
         * @param fuzzy
         *            Whether to also find bible books where only a substring matches
         * @return The BibleBook, On error null
         */
        BibleBook getBook(String find, boolean fuzzy) {
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

            if (!fuzzy) {
                return null;
            }

            for (BookName aBook : books.values()) {
                if (aBook.match(match)) {
                    return aBook.getBook();
                }
            }

            return null;
        }

        /**
         * Load up the resources for Bible book and section names, and cache the
         * upper and lower versions of them.
         */
        private void initialize() {
            int ntCount = 0;
            int otCount = 0;
            int ncCount = 0;
            BibleBook[] bibleBooks = BibleBook.values();
            for (BibleBook book : bibleBooks) {
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
                store(resources, book, fullNT, shortNT, altNT);
            }

            fullOT = new HashMap<String, BookName>(otCount);
            shortOT = new HashMap<String, BookName>(otCount);
            altOT = new HashMap<String, BookName>(otCount);
            for (int i = BibleBook.GEN.ordinal(); i <= BibleBook.MAL.ordinal(); ++i) {
                BibleBook book = bibleBooks[i];
                store(resources, book, fullOT, shortOT, altOT);
            }

            fullNC = new HashMap<String, BookName>(ncCount);
            shortNC = new HashMap<String, BookName>(ncCount);
            altNC = new HashMap<String, BookName>(ncCount);
            store(resources, BibleBook.INTRO_BIBLE, fullNC, shortNC, altNC);
            store(resources, BibleBook.INTRO_OT, fullNC, shortNC, altNC);
            store(resources, BibleBook.INTRO_NT, fullNC, shortNC, altNC);
            for (int i = BibleBook.REV.ordinal() + 1; i < bibleBooks.length; ++i) {
                BibleBook book = bibleBooks[i];
                store(resources, book, fullNC, shortNC, altNC);
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
         * The full names of the New Testament books of the Bible normalized,
         * generated at runtime
         */
        private Map<String, BookName> fullNT;

        /**
         * The full names of the Old Testament books of the Bible normalized,
         * generated at runtime
         */
        private Map<String, BookName> fullOT;

        /**
         * The full names of the Deuterocanonical books of the Bible normalized,
         * generated at runtime
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
         * Alternative shortened names for the Deuterocanonical books of the
         * Bible normalized, generated at runtime.
         */
        private Map<String, BookName> altNC;
    }

    /** we cache the Localized Bible Names because there is quite a bit of processing going on for each individual Locale */
    private transient Map<Locale, NameList> localizedBibleNames;

    /** English BibleNames, or null when using the program's default locale */
    private static NameList englishBibleNames;

    private static final BibleNames instance = new BibleNames();
}
