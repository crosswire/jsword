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

import org.crosswire.jsword.JSMsg;

/**
 * DivisionName deals with traditional sections of the Bible.
 * 
 * AV11N(DMS): Is this right?
 * <ul>
 * <li><strong>contains(BibleBook)</strong> - This has several problems:
 * <ol>
 * <li>The use of BibleBook.ordinal() is dependent upon the ordering of the
 * members of the BibleBook enum. Currently it is ordered OT and NT according to
 * the KJV and then has the deuterocanonical and apocryphal books in no
 * particular order.</li>
 * <li>Each versification defines the what books are present and the order of
 * books. So for one versification, GEN might not be the first and REV might not
 * be the last. E.g. Some V11Ns consist only of the OT.</li>
 * </ol>
 * </li>
 * <li><strong>getSize()</strong> - This can vary by versification for at least
 * BIBLE, OT and NT for those that include deuterocanonical or apocryphal books.
 * </li>
 * <li><strong>getRange()</strong> - This range is fixed text giving end points
 * that may include BibleBooks that are not intended and may exclude BibleBooks
 * that are intended. It works for the default Versification and may not work
 * for others.</li>
 * <li>A given V11N might be a single testament or just the gospels. In this case,
 * it'd be good to know whether a division isDefined()</li>
 * </ul>
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
 */
public enum DivisionName {
    /** BIBLE consists of the entire/whole Bible (Gen - Rev) **/
    BIBLE {
        @Override
        public boolean contains(BibleBook book) {
            int bookNum = book.ordinal();
            return bookNum >= BibleBook.GEN.ordinal() && bookNum <= BibleBook.REV.ordinal();
        }

        @Override
        public int getSize() {
            return 66;
        }

        @Override
        public String getName() {
            // TRANSLATOR: The entire/whole Bible (Gen - Rev)
            return JSMsg.gettext("The Whole Bible");
        }

        @Override
        public String getRange() {
            return "Gen-Rev";
        }

    },
    /** OLD_TESTAMENT consists of the old testament (Gen - Mal) **/
    OLD_TESTAMENT {
        @Override
        public boolean contains(BibleBook book) {
            int bookNum = book.ordinal();
            return bookNum >= BibleBook.GEN.ordinal() && bookNum <= BibleBook.MAL.ordinal();
        }

        @Override
        public int getSize() {
            return 39;
        }

        @Override
        public String getName() {
            // TRANSLATOR: The old testament (Gen - Mal)
            return JSMsg.gettext("Old Testament");
        }

        @Override
        public String getRange() {
            return "Gen-Mal";
        }
    },
    /** PENTATEUCH consists of the 5 books of Moses (Gen - Deu) **/
    PENTATEUCH {
        @Override
        public boolean contains(BibleBook book) {
            int bookNum = book.ordinal();
            return bookNum >= BibleBook.GEN.ordinal() && bookNum <= BibleBook.DEUT.ordinal();
        }

        @Override
        public int getSize() {
            return 5;
        }

        @Override
        public String getName() {
            // TRANSLATOR: Pentateuch is the first 5 books of the Bible.
            return JSMsg.gettext("Pentateuch");
        }

        @Override
        public String getRange() {
            return "Gen-Deu";
        }
    },
    /** HISTORY consists of the history in the Old Testament of Israel */
    HISTORY {
        @Override
        public boolean contains(BibleBook book) {
            int bookNum = book.ordinal();
            return bookNum >= BibleBook.JOSH.ordinal() && bookNum <= BibleBook.ESTH.ordinal();
        }

        @Override
        public int getSize() {
            return 12;
        }

        @Override
        public String getName() {
            // TRANSLATOR: History are the books of the Old Testament that give the history of Israel
            return JSMsg.gettext("History");
        }

        @Override
        public String getRange() {
            return "Jos-Est";
        }
    },
    /** POETRY consists of the poetic works (Job-Song) */
    POETRY {
        @Override
        public boolean contains(BibleBook book) {
            int bookNum = book.ordinal();
            return bookNum >= BibleBook.JOB.ordinal() && bookNum <= BibleBook.SONG.ordinal();
        }

        @Override
        public int getSize() {
            return 5;
        }

        @Override
        public String getName() {
            // TRANSLATOR: The poetic works of the Bible consisting of:
            // Job, Psalms, Proverbs, Ecclesiastes, and Song of Solomon
            return JSMsg.gettext("Poetry");
        }

        @Override
        public String getRange() {
            return "Job-Song";
        }
    },
    /** PROPHECY consists of the Deu 28, major prophets, minor prophets, Revelation (Isa-Mal, Rev) */
    PROPHECY {
        @Override
        public boolean contains(BibleBook book) {
            int bookNum = book.ordinal();
            return bookNum == BibleBook.REV.ordinal() || bookNum >= BibleBook.ISA.ordinal() && bookNum <= BibleBook.MAL.ordinal();
        }

        @Override
        public int getSize() {
            return 18;
        }

        @Override
        public String getName() {
            // TRANSLATOR: A division of the Bible containing prophecy:
            // Deuteronomy 28
            // Major Prophets: Isaiah, Jeremiah, Lamentations, Ezekiel, Daniel
            // Minor Prophets: Hosea, Joel, Amos, Obadiah, Jonah, Micah, Nahum,
            //                 Habakkuk, Zephaniah, Haggai, Zechariah, Malachi 
            // Revelation
            return JSMsg.gettext("All Prophecy");
        }

        @Override
        public String getRange() {
            return "Deu 28,Isa-Mal,Rev";
        }
    },
    /** MAJOR_PROPHETS consists of the major prophets (Isa-Dan) */
    MAJOR_PROPHETS {
        @Override
        public boolean contains(BibleBook book) {
            int bookNum = book.ordinal();
            return bookNum >= BibleBook.ISA.ordinal() && bookNum <= BibleBook.DAN.ordinal();
        }

        @Override
        public int getSize() {
            return 5;
        }

        @Override
        public String getName() {
            // TRANSLATOR: A division of the Bible containing the major prophets (Isa-Dan)
            // Isaiah, Jeremiah, Lamentations, Ezekiel, Daniel 
            return JSMsg.gettext("Major Prophets");
        }

        @Override
        public String getRange() {
            return "Isa-Dan";
        }
    },
    /** MINOR_PROPHETS consists of the minor prophets (Hos-Mal) */
    MINOR_PROPHETS {
        @Override
        public boolean contains(BibleBook book) {
            int bookNum = book.ordinal();
            return bookNum >= BibleBook.HOS.ordinal() && bookNum <= BibleBook.MAL.ordinal();
        }

        @Override
        public int getSize() {
            return 12;
        }

        @Override
        public String getName() {
            // TRANSLATOR: A division of the Bible containing the minor prophets (Hos-Mal)
            // Hosea, Joel, Amos, Obadiah, Jonah, Micah, Nahum, 
            // Habakkuk, Zephaniah, Haggai, Zechariah, Malachi 
            return JSMsg.gettext("Minor Prophets");
        }

        @Override
        public String getRange() {
            return "Hos-Mal";
        }
    },
    /** NEW_TESTAMENT consists of the new testament (Mat - Rev) **/
    NEW_TESTAMENT {
        @Override
        public boolean contains(BibleBook book) {
            int bookNum = book.ordinal();
            return bookNum >= BibleBook.MATT.ordinal() && bookNum <= BibleBook.REV.ordinal();
        }

        @Override
        public int getSize() {
            return 27;
        }

        @Override
        public String getName() {
            // TRANSLATOR: The New Testament (Mat - Rev)
            return JSMsg.gettext("New Testament");
        }

        @Override
        public String getRange() {
            return "Mat-Rev";
        }
    },
    /** GOSPELS_AND_ACTS consists of the 4 Gospels and Acts (Mat-Acts) */
    GOSPELS_AND_ACTS {
        @Override
        public boolean contains(BibleBook book) {
            int bookNum = book.ordinal();
            return bookNum >= BibleBook.MATT.ordinal() && bookNum <= BibleBook.ACTS.ordinal();
        }

        @Override
        public int getSize() {
            return 5;
        }

        @Override
        public String getName() {
            // TRANSLATOR: A division of the Bible containing the 4 Gospels and Acts (Mat-Acts)
            // Matthew, Mark, Luke, John, Acts
            return JSMsg.gettext("Gospels and Acts");
        }

        @Override
        public String getRange() {
            return "Mat-Acts";
        }
    },
    /** LETTERS consists of the letters/epistles (Rom-Jud) */
    LETTERS {
        @Override
        public boolean contains(BibleBook book) {
            int bookNum = book.ordinal();
            return bookNum >= BibleBook.ROM.ordinal() && bookNum <= BibleBook.JUDE.ordinal();
        }

        @Override
        public int getSize() {
            return 21;
        }

        @Override
        public String getName() {
            // TRANSLATOR: A division of the Bible containing the letters/epistles (Rom-Jud)
            // Pauline: Romans, 1&2 Corinthians, Galatians, Ephesians, Philippians, Colossians,
            //          1&2 Thessalonians, 1&2 Timothy, Titus, Philemon, Hebrews
            // General: James, 1-2 Peter, 1-3 John, Jude
            return JSMsg.gettext("Letters");
        }

        @Override
        public String getRange() {
            return "Rom-Jud";
        }
    },
    /** LETTERS consists of the Pauline letters/epistles (Rom-Heb) */
    PAULINE_LETTERS {
        @Override
        public boolean contains(BibleBook book) {
            int bookNum = book.ordinal();
            return bookNum >= BibleBook.ROM.ordinal() && bookNum <= BibleBook.JUDE.ordinal();
        }

        @Override
        public int getSize() {
            return 14;
        }

        @Override
        public String getName() {
            // TRANSLATOR: A division of the Bible containing the Pauline letters/epistles (Rom-Heb)
            // Romans, 1-2 Corinthians, Galatians, Ephesians, Philippians, Colossians,
            // 1-2 Thessalonians, 1-2 Timothy, Titus, Philemon, Hebrews
            return JSMsg.gettext("Letters to People");
        }

        @Override
        public String getRange() {
            return "Rom-Heb";
        }
    },
    /** LETTERS consists of the general letters/epistles (Jas-Jud) */
    GENERAL_LETTERS {
        @Override
        public boolean contains(BibleBook book) {
            int bookNum = book.ordinal();
            return bookNum >= BibleBook.JAS.ordinal() && bookNum <= BibleBook.JUDE.ordinal();
        }

        @Override
        public int getSize() {
            return 7;
        }

        @Override
        public String getName() {
            // TRANSLATOR: A division of the Bible containing the general letters/epistles (Jas-Jud)
            // James, 1-2 Peter, 1-3 John, Jude
            return JSMsg.gettext("Letters from People");
        }

        @Override
        public String getRange() {
            return "Jas-Jud";
        }
    },
    /** REVELATION consists of the book of Revelation (Rev) */
    REVELATION {
        @Override
        public boolean contains(BibleBook book) {
            return book == BibleBook.REV;
        }

        @Override
        public int getSize() {
            return 1;
        }

        @Override
        public String getName() {
            // TRANSLATOR: A division of the Bible containing the book of Revelation (Rev)
            return JSMsg.gettext("Revelation");
        }

        @Override
        public String getRange() {
            return "Rev";
        }
    };

    /**
     * Determine whether the book is contained within the section.
     * @param book
     * @return true if the book is contained within the division
     */
    public abstract boolean contains(BibleBook book);

    /**
     * Get the number of whole books in the section.
     * @return the number of whole books in the section
     */
    public abstract int getSize();

    /**
     * Obtain a localized string description of the section.
     * @return the localized name.
     */
    public abstract String getName();

    /**
     * Obtain a string representation of the scope of the section.
     * @return the localized name.
     */
    public abstract String getRange();

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Determine the section to which this book belongs.
     * 
     * @param book The book to test
     * @return the section
     */
    public static DivisionName getSection(BibleBook book) {
        // Ordered by section size for speed
        if (LETTERS.contains(book)) {
            return LETTERS;
        }

        if (HISTORY.contains(book)) {
            return HISTORY;
        }

        if (MINOR_PROPHETS.contains(book)) {
            return MINOR_PROPHETS;
        }

        if (GOSPELS_AND_ACTS.contains(book)) {
            return GOSPELS_AND_ACTS;
        }

        if (PENTATEUCH.contains(book)) {
            return PENTATEUCH;
        }

        if (POETRY.contains(book)) {
            return POETRY;
        }

        if (MAJOR_PROPHETS.contains(book)) {
            return MAJOR_PROPHETS;
        }

        // AAV11N(DMS): might not be true
        return REVELATION;
    }

    /**
     * Handy section finder. There is a bit of moderately bad programming here
     * because org.crosswire.biblemapper.sw*ng.GroupVerseColor uses these
     * numbers as an index into an array, so we shouldn't change these numbers
     * without fixing that, however I don't imagine that this section could ever
     * change without breaking GroupVerseColor anyway so I don't see it as a big
     * problem.
    public static final byte PENTATEUCH = 1;
    public static final byte HISTORY = 2;
    public static final byte POETRY = 3;
    public static final byte MAJOR_PROPHETS = 4;
    public static final byte MINOR_PROPHETS = 5;
    public static final byte GOSPELS_AND_ACTS = 6;
    public static final byte LETTERS = 7;
    public static final byte REVELATION = 8;
     */

    /** Constant for the number of sections in the Bible
    private static final int SECTIONS_IN_BIBLE = 8;
     */
}
