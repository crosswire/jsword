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
 * Copyright: 2007
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.versification;

import java.util.Locale;
import java.util.regex.Pattern;

import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.book.CaseType;

/**
 * BookName represents the different ways a book of the bible is named.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public final class BookName {
    /**
     * Create a BookName for a Book of the Bible in a given language.
     * 
     * @param locale
     *            the language of this BookName
     * @param bookNumber
     *            the Book's canonical number
     * @param longName
     *            the Book's long name
     * @param shortName
     *            the Book's short name, if any
     * @param alternateNames
     *            optional comma separated list of alternates for the Book
     */
    public BookName(Locale locale, int bookNumber, String longName, String shortName, String alternateNames) {
        this.locale = locale;
        this.bookNumber = bookNumber;
        this.longName = longName;
        this.normalizedLongName = normalize(longName, locale);
        this.shortName = shortName;
        this.normalizedShortName = normalize(shortName, locale);

        if (alternateNames != null) {
            this.alternateNames = StringUtil.split(normalize(alternateNames, locale), ',');
        }
    }

    /**
     * Get canonical number of a book.
     * 
     * @return The book number (1 to 66)
     */
    public int getNumber() {
        return bookNumber;
    }

    /**
     * Get the preferred name of a book. Altered by the case setting (see
     * setBookCase() and isLongBookName())
     * 
     * @return The preferred name of the book
     */
    public String getPreferredName() {
        if (BibleInfo.isFullBookName()) {
            return getLongName();
        }
        return getShortName();
    }

    /**
     * Get the full name of a book (e.g. "Genesis"). Altered by the case setting
     * (see setBookCase())
     * 
     * @return The full name of the book
     */
    public String getLongName() {
        CaseType bookCase = BibleInfo.getDefaultCase();

        if (bookCase == CaseType.LOWER) {
            return longName.toLowerCase(locale);
        }

        if (bookCase == CaseType.UPPER) {
            return longName.toUpperCase(locale);
        }

        return longName;
    }

    /**
     * Get the short name of a book (e.g. "Gen"). Altered by the case setting
     * (see setBookCase())
     * 
     * @return The short name of the book
     */
    public String getShortName() {
        CaseType bookCase = BibleInfo.getDefaultCase();

        if (bookCase == CaseType.LOWER) {
            return shortName.toLowerCase(locale);
        }

        if (bookCase == CaseType.UPPER) {
            return shortName.toUpperCase(locale);
        }

        return shortName;
    }

    /**
     * @return the normalizedLongName
     */
    public String getNormalizedLongName() {
        return normalizedLongName;
    }

    /**
     * @return the normalizedShortName
     */
    public String getNormalizedShortName() {
        return normalizedShortName;
    }

    /**
     * Match the normalized name as closely as possible. It will match if:
     * <ol>
     * <li>it is a prefix of a normalized alternate name</li>
     * <li>a normalized alternate name is a prefix of it</li>
     * <li>it is a prefix of a normalized long name</li>
     * <li>it is a prefix of a normalized short name</li>
     * <li>a normalized short name is a prefix of it</li>
     * 
     * @param normalizedName
     *            the already normalized name to match against.
     * @return true of false
     */
    public boolean match(String normalizedName) {
        // Does it match one of the alternative versions
        for (int j = 0; j < alternateNames.length; j++) {
            String targetBookName = alternateNames[j];
            if (targetBookName.startsWith(normalizedName) || normalizedName.startsWith(targetBookName)) {
                return true;
            }
        }

        // Does it match a long version of the book
        if (normalizedLongName.startsWith(normalizedName)) {
            return true;
        }

        // or a short version
        if (normalizedShortName.startsWith(normalizedName) || normalizedName.startsWith(normalizedShortName)) {
            return true;
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return bookNumber;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final BookName other = (BookName) obj;
        return bookNumber == other.bookNumber;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getPreferredName();
    }

    /**
     * Normalize by stripping punctuation and whitespace and lowercasing.
     * 
     * @param str
     *            the string to normalize
     * @return the normalized string
     */
    public static String normalize(String str, Locale locale) {
        return normPattern.matcher(str).replaceAll("").toLowerCase(locale);
    }

    /** remove spaces and some punctuation in Book Name (make sure , is allowed) */
    private static Pattern normPattern = Pattern.compile("[. ]");

    private int bookNumber;
    private String longName;
    private String normalizedLongName;
    private String shortName;
    private String normalizedShortName;
    private String[] alternateNames;

    /** The locale for the Book Name */
    private Locale locale;
}
