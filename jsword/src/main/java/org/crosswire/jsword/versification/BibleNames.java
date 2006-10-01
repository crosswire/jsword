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
 * ID: $Id: BibleNames.java 1068 2006-04-07 22:20:41 -0400 (Fri, 07 Apr 2006) dmsmith $
 */
package org.crosswire.jsword.versification;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import org.crosswire.common.util.CWClassLoader;
import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.book.CaseType;
import org.crosswire.jsword.passage.NoSuchVerseException;

/**
 * BibleNames is a static class that deals with Book name lookup conversions.
 * We start counting at 1 for books (so Genesis=1, Revelation=66).
 * However internally books start counting at 0 and go up to 65.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public final class BibleNames
{
    /**
     * Ensure that we can not be instantiated
     */
    public BibleNames(Locale locale)
    {
        this.locale = locale;
        initialize();
    }

    /**
     * Get the preferred name of a book.
     * Altered by the case setting (see setBookCase() and isLongBookName())
     * @param book The book number (1-66)
     * @return The full name of the book
     * @exception NoSuchVerseException If the book number is not valid
     */
    public String getBookName(int book) throws NoSuchVerseException
    {
        if (BibleInfo.isFullBookName())
        {
            return getLongBookName(book);
        }
        return getShortBookName(book);
    }

    /**
     * Get the full name of a book (e.g. "Genesis").
     * Altered by the case setting (see setBookCase())
     * @param book The book number (1-66)
     * @return The full name of the book
     * @exception NoSuchVerseException If the book number is not valid
     */
    public String getLongBookName(int book) throws NoSuchVerseException
    {
        try
        {
            CaseType bookCase = BibleInfo.getDefaultCase();

            if (bookCase == CaseType.LOWER)
            {
                return fullBooks[book - 1].toLowerCase();
            }

            if (bookCase == CaseType.UPPER)
            {
                return fullBooks[book - 1].toUpperCase();
            }

            return fullBooks[book - 1];
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            // This is faster than doing the check explicitly, unless
            // The exception is actually thrown, then it is a lot slower
            // I'd like to think that the norm is to get it right
            throw new NoSuchVerseException(Msg.BOOKS_BOOK, new Object[] { new Integer(book) });
        }
    }

    /**
     * Get the short name of a book (e.g. "Gen").
     * Altered by the case setting (see setBookCase())
     * @param book The book number (1-66)
     * @return The short name of the book
     * @exception NoSuchVerseException If the book number is not valid
     */
    public String getShortBookName(int book) throws NoSuchVerseException
    {
        try
        {
            CaseType bookCase = BibleInfo.getDefaultCase();

            if (bookCase.equals(CaseType.LOWER))
            {
                return shortBooks[book - 1].toLowerCase();
            }

            if (bookCase.equals(CaseType.UPPER))
            {
                return shortBooks[book - 1].toUpperCase();
            }

            return shortBooks[book - 1];
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            // This is faster than doing the check explicitly, unless
            // The exception is actually thrown, then it is a lot slower
            // I'd like to think that the norm is to get it right
            throw new NoSuchVerseException(Msg.BOOKS_BOOK, new Object[] { new Integer(book) });
        }
    }

    /**
     * Get the OSIS name for a book.
     * @param book The book number (1-66)
     * @return the OSIS defined short name for a book
     * @exception NoSuchVerseException If the book number is not valid
     */
    public String getOSISName(int book) throws NoSuchVerseException
    {
        try
        {
            return osisBooks[book - 1];
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            // This is faster than doing the check explicitly, unless
            // The exception is actually thrown, then it is a lot slower
            // I'd like to think that the norm is to get it right
            throw new NoSuchVerseException(Msg.BOOKS_BOOK, new Object[] { new Integer(book) });
        }
    }

    /**
     * Get number of a book from its name.
     * @param find The string to identify
     * @return The book number (1 to 66) On error -1
     */
    public int getBookNumber(String find)
    {
        String match = normalize(find);

        // Favor OSIS names.
        Integer bookNum = (Integer) osisMap.get(match);
        if (bookNum != null)
        {
            return bookNum.intValue();
        }

        bookNum = (Integer) fullBooksMap.get(match);
        if (bookNum != null)
        {
            return bookNum.intValue();
        }

        bookNum = (Integer) shortBooksMap.get(match);
        if (bookNum != null)
        {
            return bookNum.intValue();
        }

        bookNum = (Integer) altBooksMap.get(match);
        if (bookNum != null)
        {
            return bookNum.intValue();
        }

        // Or does it match one of the alternative versions
        for (int i = 0; i < altBooks.length; i++)
        {
            for (int j = 0; j < altBooks[i].length; j++)
            {
                String targetBookName = altBooks[i][j];
                if (targetBookName.startsWith(match) || match.startsWith(targetBookName))
                {
                    return i + 1;
                }
            }
        }

        // Does it match a long version of the book or a short version
        for (int i = 0; i < fullBooksSearch.length; i++)
        {
            String targetBookName = fullBooksSearch[i];
            if (targetBookName.startsWith(match))
            {
                return i + 1;
            }

            targetBookName = shortBooksSearch[i];
            if (targetBookName.startsWith(match) || match.startsWith(targetBookName))
            {
                return i + 1;
            }
        }

        return -1;
    }

    /**
     * Is the given string a valid book name. If this method returns true then
     * getBookNumber() will return a number and not throw an exception.
     * @param find The string to identify
     * @return The book number (1 to 66)
     */
    public boolean isBookName(String find)
    {
        return getBookNumber(find) != -1;
    }

    /**
     * Is this book part of the Pentateuch?
     * @param book The book to test
     * @return True if this book is a part of this section
     */
    public static boolean isPentateuch(int book)
    {
        return book >= Names.GENESIS && book <= Names.DEUTERONOMY;
    }

    /**
     * Is this book part of the OT History?
     * @param book The book to test
     * @return True if this book is a part of this section
     */
    public static boolean isHistory(int book)
    {
        return book >= Names.JOSHUA && book <= Names.ESTHER;
    }

    /**
     * Is this book part of the OT History?
     * @param book The book to test
     * @return True if this book is a part of this section
     */
    public static boolean isPoetry(int book)
    {
        return book >= Names.JOB && book <= Names.SONGOFSOLOMON;
    }

    /**
     * Is this book part of the major prophets?
     * @param book The book to test
     * @return True if this book is a part of this section
     */
    public static boolean isMajorProphet(int book)
    {
        return book >= Names.ISAIAH && book <= Names.DANIEL;
    }

    /**
     * Is this book part of the minor prophets?
     * @param book The book to test
     * @return True if this book is a part of this section
     */
    public static boolean isMinorProphet(int book)
    {
        return book >= Names.HOSEA && book <= Names.MALACHI;
    }

    /**
     * Is this book part of the Gospels?
     * @param book The book to test
     * @return True if this book is a part of this section
     */
    public static boolean isGospel(int book)
    {
        return book >= Names.MATTHEW && book <= Names.JOHN;
    }

    /**
     * Is this book part of the Gospels or Acts?
     * @param book The book to test
     * @return True if this book is a part of this section
     */
    public static boolean isGospelOrActs(int book)
    {
        return book >= Names.MATTHEW && book <= Names.ACTS;
    }

    /**
     * Is this book part of the letters?
     * @param book The book to test
     * @return True if this book is a part of this section
     */
    public static boolean isLetter(int book)
    {
        return book >= Names.ROMANS && book <= Names.JUDE;
    }

    /**
     * What section is this book a part of?
     * @param book The book to test
     * @return True The section
     * @see BibleNames.Section
     */
    public static int getSection(int book)
    {
        // Ordered by section size for speed
        if (isLetter(book))
        {
            return Section.LETTERS;
        }

        if (isHistory(book))
        {
            return Section.HISTORY;
        }

        if (isMinorProphet(book))
        {
            return Section.MINOR_PROPHETS;
        }

        if (isGospelOrActs(book))
        {
            return Section.GOSPELS_AND_ACTS;
        }

        if (isPentateuch(book))
        {
            return Section.PENTATEUCH;
        }

        if (isPoetry(book))
        {
            return Section.POETRY;
        }

        if (isMajorProphet(book))
        {
            return Section.MAJOR_PROPHETS;
        }

        return Section.REVELATION;
    }

    /**
     * Get the full name of a book (e.g. "Genesis").
     * Altered by the case setting (see setBookCase())
     * @param section The book number (1-66)
     * @return The full name of the book
     * @exception NoSuchVerseException If the book number is not valid
     */
    public String getSectionName(int section) throws NoSuchVerseException
    {
        if (section == 0)
        {
            throw new NoSuchVerseException(Msg.BOOKS_SECTION, new Object[] { new Integer(section)});
        }

        try
        {
            CaseType bookCase = BibleInfo.getDefaultCase();

            if (bookCase.equals(CaseType.LOWER))
            {
                return sections[section - 1].toLowerCase();
            }

            if (bookCase.equals(CaseType.UPPER))
            {
                return sections[section - 1].toUpperCase();
            }

            return sections[section - 1];
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            // This is faster than doing the check explicitly, unless
            // The exception is actually thrown, then it is a lot slower
            // I'd like to think that the norm is to get it right
            throw new NoSuchVerseException(Msg.BOOKS_SECTION, new Object[] { new Integer(section) });
        }
    }

    /**
     * Normalize by stripping punctuation and whitespace.
     * @param str the string to normalize
     * @return the normalized string
     */
    private String normalize(String str)
    {
        return normPattern.matcher(str).replaceAll("").toLowerCase(); //$NON-NLS-1$
    }

    /**
     * Load up the resources for Bible book and section names,
     * and cache the upper and lower versions of them.
     */
    private void initialize()
    {
        int booksInBible = BibleInfo.booksInBible();

        // Create the book name arrays
        fullBooks = new String[booksInBible];
        fullBooksSearch = new String[booksInBible];
        fullBooksMap = new HashMap(booksInBible);

        shortBooks = new String[booksInBible];
        shortBooksSearch = new String[booksInBible];
        shortBooksMap = new HashMap(booksInBible);

        altBooks = new String[booksInBible][];
        altBooksMap = new HashMap(booksInBible);

        ResourceBundle resources = ResourceBundle.getBundle(BibleInfo.class.getName(), locale, new CWClassLoader(BibleInfo.class));

        for (int i = 0; i < booksInBible; i++)
        {
            Integer bookNum = new Integer(i + 1);
            String fullBook = getString(resources, FULL_KEY + (i + 1));
            String normalized = normalize(fullBook);
            fullBooks[i] = fullBook;
            fullBooksSearch[i] = normalized;
            fullBooksMap.put(normalized, bookNum);

            String shortBook = getString(resources, SHORT_KEY + (i + 1));
            normalized = normalize(shortBook);
            shortBooks[i] = shortBook;
            shortBooksSearch[i] = normalized;
            shortBooksMap.put(normalized, bookNum);

            String altBook = getString(resources, ALT_KEY + (i + 1));
            String [] alternates = StringUtil.split(altBook, ',');
            altBooks[i] = alternates;

            for (int j = 0; j < alternates.length; j++)
            {
                altBooksMap.put(alternates[j], bookNum);
            }
        }

        sections = new String[SECTIONS_IN_BIBLE];

        for (int i = 0; i < SECTIONS_IN_BIBLE; i++)
        {
            String section = getString(resources, SECTION_KEY + (i + 1));
            sections[i] = section;
        }

        if (osisBooks == null)
        {
            osisBooks = new String[booksInBible];
            osisMap   = new HashMap(booksInBible);

            // Get all the OSIS standard book names
            resources = ResourceBundle.getBundle(OSIS_PROPERTIES, Locale.getDefault(), new CWClassLoader(BibleInfo.class));

            for (int i = 0; i < osisBooks.length; i++)
            {
                osisBooks[i] = getString(resources, OSIS_KEY + (i + 1));
                osisMap.put(normalize(osisBooks[i]), new Integer(i + 1));
            }
        }
    }

    /*
     * Helper to make the code more readable.
     */
    private String getString(ResourceBundle resources, String key)
    {
        try
        {
            return resources.getString(key);
        }
        catch (Exception e)
        {
            assert false;
        }
        return null;
    }

    private static final String FULL_KEY = "BibleNames.Full."; //$NON-NLS-1$
    private static final String SHORT_KEY = "BibleNames.Short."; //$NON-NLS-1$
    private static final String ALT_KEY = "BibleNames.Alt."; //$NON-NLS-1$
    private static final String SECTION_KEY = "BibleNames.Sections."; //$NON-NLS-1$
    private static final String OSIS_KEY = "BibleNames.OSIS."; //$NON-NLS-1$
    private static final String OSIS_PROPERTIES = "OSISNames"; //$NON-NLS-1$

    /**
     * Handy section finder. There is a bit of moderately bad programming
     * here because org.crosswire.jsword.control.map.sw*ng.GroupVerseColor
     * uses these numbers as an index into an array, so we shouldn't
     * change these numbers without fixing that, however I don't imagine
     * that this section could ever change without breaking
     * GroupVerseColor anyway so I don't see it as a big problem.
     */
    public static class Section
    {
        public static final byte PENTATEUCH = 1;
        public static final byte HISTORY = 2;
        public static final byte POETRY = 3;
        public static final byte MAJOR_PROPHETS = 4;
        public static final byte MINOR_PROPHETS = 5;
        public static final byte GOSPELS_AND_ACTS = 6;
        public static final byte LETTERS = 7;
        public static final byte REVELATION = 8;
    }

    /**
     * Handy book finder
     */
    public static class Names
    {
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
    }

    /** The locale for the Bible Names */
    private Locale locale;

    /** The full names of the book of the Bible, in mixed case */
    private String[] fullBooks;

    /** The full names of the book of the Bible, normalized, generated at runtime */
    private String[] fullBooksSearch;

    /** The full names of the book of the Bible, normalized, generated at runtime */
    private Map fullBooksMap;

    /** Standard shortened names for the book of the Bible, in mixed case */
    private String[] shortBooks;

    /** Standard shortened names for the book of the Bible, normalized, generated at runtime. */
    private String[] shortBooksSearch;

    /** Standard shortened names for the book of the Bible, normalized, generated at runtime. */
    private Map shortBooksMap;

     /** Alternative shortened names for the book of the Bible, expected to be normalized */
    private String[][] altBooks;

    /** Alternative shortened names for the book of the Bible, normalized, generated at runtime */
    private Map altBooksMap;

    /** Standard OSIS names for the book of the Bible, in mixed case */
    private static String[] osisBooks;

    /** Standard OSIS names for the book of the Bible, in lowercase, generated at runtime  */
    private static Map osisMap;

    /** Standard names for the sections */
    private String[] sections;

    private static Pattern normPattern = Pattern.compile("[. ]"); //$NON-NLS-1$

    /** Constant for the number of sections in the Bible */
    private static final int SECTIONS_IN_BIBLE = 8;

}
