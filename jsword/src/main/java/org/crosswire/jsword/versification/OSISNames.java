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
public final class OSISNames {
    /**
     * Ensure that we can not be instantiated
     */
    private OSISNames() {
        initialize();
    }

    /**
     * Get the OSIS name for a book.
     * 
     * @param book
     *            The book number (1-66)
     * @return the OSIS defined short name for a book
     * @exception NoSuchVerseException
     *                If the book number is not valid
     */
    public static String getName(int book) throws NoSuchVerseException {
        try {
            return osisBooks[book - 1];
        } catch (ArrayIndexOutOfBoundsException ex) {
            // This is faster than doing the check explicitly, unless
            // The exception is actually thrown, then it is a lot slower
            // I'd like to think that the norm is to get it right
            throw new NoSuchVerseException(Msg.BOOKS_BOOK, new Object[] {
                new Integer(book)
            });
        }
    }

    /**
     * Get number of a book from its name.
     * 
     * @param find
     *            The string to identify
     * @return The book number (1 to 66) On error -1
     */
    public static int getNumber(String find) {
        String match = BookName.normalize(find, OSIS_LOCALE);

        Integer bookNum = (Integer) osisMap.get(match);
        if (bookNum != null) {
            return bookNum.intValue();
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
    public static boolean isBookName(String find) {
        return getNumber(find) != -1;
    }

    /**
     * Load up the resources for Bible book and section names, and cache the
     * upper and lower versions of them.
     */
    private static void initialize() {
        int booksInBible = BibleInfo.booksInBible();
        osisBooks = new String[booksInBible];
        osisMap = new HashMap(booksInBible);

        // Get all the OSIS standard book names
        ResourceBundle resources = ResourceBundle.getBundle(OSISNames.class.getName(), OSIS_LOCALE, CWClassLoader.instance(OSISNames.class));

        for (int i = 0; i < osisBooks.length; i++) {
            osisBooks[i] = getString(resources, OSIS_KEY + (i + 1));
            osisMap.put(BookName.normalize(osisBooks[i], OSIS_LOCALE), new Integer(i + 1));
        }
    }

    /*
     * Helper to make the code more readable.
     */
    private static String getString(ResourceBundle resources, String key) {
        try {
            return resources.getString(key);
        } catch (MissingResourceException e) {
            assert false;
        }
        return null;
    }

    /** The Locale of OSIS Names */
    private static final Locale OSIS_LOCALE = new Locale("en"); //$NON-NLS-1$

    /**
     * A singleton used to do initialization. Could be used to change static
     * methods to non-static
     */
    static final OSISNames instance = new OSISNames();

    private static final String OSIS_KEY = "OSIS."; //$NON-NLS-1$

    /** Standard OSIS names for the book of the Bible, in mixed case */
    private static String[] osisBooks;

    /**
     * Standard OSIS names for the book of the Bible, in lowercase, generated at
     * runtime
     */
    private static Map osisMap;
}
