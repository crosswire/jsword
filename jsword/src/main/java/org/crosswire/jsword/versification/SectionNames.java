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

import java.util.Locale;

import org.crosswire.common.util.MsgBase;
import org.crosswire.jsword.book.CaseType;
import org.crosswire.jsword.passage.NoSuchVerseException;

/**
 * SectionNames deals with traditional sections of the Bible.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public final class SectionNames extends MsgBase {
    /**
     * Create a SectionNames object
     */
    public SectionNames() {
        initialize();
    }

    /**
     * Is this book part of the Pentateuch?
     * 
     * @param book
     *            The book to test
     * @return True if this book is a part of this section
     */
    public static boolean isPentateuch(int book) {
        return book >= BibleNames.GENESIS && book <= BibleNames.DEUTERONOMY;
    }

    /**
     * Is this book part of the OT History?
     * 
     * @param book
     *            The book to test
     * @return True if this book is a part of this section
     */
    public static boolean isHistory(int book) {
        return book >= BibleNames.JOSHUA && book <= BibleNames.ESTHER;
    }

    /**
     * Is this book part of the OT History?
     * 
     * @param book
     *            The book to test
     * @return True if this book is a part of this section
     */
    public static boolean isPoetry(int book) {
        return book >= BibleNames.JOB && book <= BibleNames.SONGOFSOLOMON;
    }

    /**
     * Is this book part of the major prophets?
     * 
     * @param book
     *            The book to test
     * @return True if this book is a part of this section
     */
    public static boolean isMajorProphet(int book) {
        return book >= BibleNames.ISAIAH && book <= BibleNames.DANIEL;
    }

    /**
     * Is this book part of the minor prophets?
     * 
     * @param book
     *            The book to test
     * @return True if this book is a part of this section
     */
    public static boolean isMinorProphet(int book) {
        return book >= BibleNames.HOSEA && book <= BibleNames.MALACHI;
    }

    /**
     * Is this book part of the Gospels?
     * 
     * @param book
     *            The book to test
     * @return True if this book is a part of this section
     */
    public static boolean isGospel(int book) {
        return book >= BibleNames.MATTHEW && book <= BibleNames.JOHN;
    }

    /**
     * Is this book part of the Gospels or Acts?
     * 
     * @param book
     *            The book to test
     * @return True if this book is a part of this section
     */
    public static boolean isGospelOrActs(int book) {
        return book >= BibleNames.MATTHEW && book <= BibleNames.ACTS;
    }

    /**
     * Is this book part of the letters?
     * 
     * @param book
     *            The book to test
     * @return True if this book is a part of this section
     */
    public static boolean isLetter(int book) {
        return book >= BibleNames.ROMANS && book <= BibleNames.JUDE;
    }

    /**
     * What section is this book a part of?
     * 
     * @param book
     *            The book to test
     * @return True The section
     */
    public static int getSection(int book) {
        // Ordered by section size for speed
        if (isLetter(book)) {
            return LETTERS;
        }

        if (isHistory(book)) {
            return HISTORY;
        }

        if (isMinorProphet(book)) {
            return MINOR_PROPHETS;
        }

        if (isGospelOrActs(book)) {
            return GOSPELS_AND_ACTS;
        }

        if (isPentateuch(book)) {
            return PENTATEUCH;
        }

        if (isPoetry(book)) {
            return POETRY;
        }

        if (isMajorProphet(book)) {
            return MAJOR_PROPHETS;
        }

        return REVELATION;
    }

    /**
     * Get the full name of a book (e.g. "Genesis"). Altered by the case setting
     * (see setBookCase())
     * 
     * @param section
     *            The book number (1-66)
     * @return The full name of the book
     * @exception NoSuchVerseException
     *                If the book number is not valid
     */
    public String getSectionName(int section) throws NoSuchVerseException {
        if (section == 0) {
            throw new NoSuchVerseException(Msg.BOOKS_SECTION, new Object[] {
                new Integer(section)
            });
        }

        try {
            CaseType bookCase = BibleInfo.getDefaultCase();

            if (bookCase.equals(CaseType.LOWER)) {
                return sections[section - 1].toLowerCase(Locale.getDefault());
            }

            if (bookCase.equals(CaseType.UPPER)) {
                return sections[section - 1].toUpperCase(Locale.getDefault());
            }

            return sections[section - 1];
        } catch (ArrayIndexOutOfBoundsException ex) {
            // This is faster than doing the check explicitly, unless
            // The exception is actually thrown, then it is a lot slower
            // I'd like to think that the norm is to get it right
            throw new NoSuchVerseException(Msg.BOOKS_SECTION, new Object[] {
                new Integer(section)
            });
        }
    }

    /**
     * Load up the resources for Bible book and section names, and cache the
     * upper and lower versions of them.
     */
    private void initialize() {
        sections = new String[SECTIONS_IN_BIBLE];

        // TRANSLATOR: Pentateuch is the first 5 books of the Bible.
        sections[0] = UserMsg.gettext("Pentateuch");
        // TRANSLATOR: History are the books of the Old Testament that give the history of Israel
        sections[1] = UserMsg.gettext("History");
        // TRANSLATOR: The Bible poetry books
        sections[2] = UserMsg.gettext("Poetry");
        // TRANSLATOR: The Bible's major prophets
        sections[3] = UserMsg.gettext("Major Prophets");
        // TRANSLATOR: The Bible's minor prophets
        sections[4] = UserMsg.gettext("Minor Prophets");
        // TRANSLATOR: The 4 Gospels and Acts in the New Testament
        sections[5] = UserMsg.gettext("Gospels And Acts");
        // TRANSLATOR: The letters of the New Testament
        sections[6] = UserMsg.gettext("Letters");
        // TRANSLATOR: The book of Revelation
        sections[7] = UserMsg.gettext("Revelation");
    }

    /**
     * Handy section finder. There is a bit of moderately bad programming here
     * because org.crosswire.biblemapper.sw*ng.GroupVerseColor uses these
     * numbers as an index into an array, so we shouldn't change these numbers
     * without fixing that, however I don't imagine that this section could ever
     * change without breaking GroupVerseColor anyway so I don't see it as a big
     * problem.
     */
    public static final byte PENTATEUCH = 1;
    public static final byte HISTORY = 2;
    public static final byte POETRY = 3;
    public static final byte MAJOR_PROPHETS = 4;
    public static final byte MINOR_PROPHETS = 5;
    public static final byte GOSPELS_AND_ACTS = 6;
    public static final byte LETTERS = 7;
    public static final byte REVELATION = 8;

    /** Standard names for the sections */
    private String[] sections;

    /** Constant for the number of sections in the Bible */
    private static final int SECTIONS_IN_BIBLE = 8;
}
