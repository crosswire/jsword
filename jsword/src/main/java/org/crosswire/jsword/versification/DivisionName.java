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

import org.crosswire.jsword.JSMsg;

/**
 * DivisionName deals with traditional sections of the Bible.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public enum DivisionName {

    /** BIBLE consists of the entire/whole Bible (Gen - Rev) **/
    BIBLE {
        @Override
        public boolean contains(int book) {
            return book >= BibleNames.GENESIS && book <= BibleNames.REVELATION;
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
    /** OLD_TESTAMENT consists of the old testament (Gen - Rev) **/
    OLD_TESTAMENT {
        @Override
        public boolean contains(int book) {
            return book >= BibleNames.GENESIS && book <= BibleNames.MALACHI;
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
        public boolean contains(int book) {
            return book >= BibleNames.GENESIS && book <= BibleNames.DEUTERONOMY;
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
        public boolean contains(int book) {
            return book >= BibleNames.JOSHUA && book <= BibleNames.ESTHER;
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
        public boolean contains(int book) {
            return book >= BibleNames.JOB && book <= BibleNames.SONGOFSOLOMON;
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
        public boolean contains(int book) {
            return book == BibleNames.REVELATION || book >= BibleNames.ISAIAH && book <= BibleNames.MALACHI;
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
        public boolean contains(int book) {
            return book >= BibleNames.ISAIAH && book <= BibleNames.DANIEL;
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
        public boolean contains(int book) {
            return book >= BibleNames.HOSEA && book <= BibleNames.MALACHI;
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
        public boolean contains(int book) {
            return book >= BibleNames.GENESIS && book <= BibleNames.REVELATION;
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
        public boolean contains(int book) {
            return book >= BibleNames.MATTHEW && book <= BibleNames.ACTS;
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
        public boolean contains(int book) {
            return book >= BibleNames.ROMANS && book <= BibleNames.JUDE;
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
        public boolean contains(int book) {
            return book >= BibleNames.ROMANS && book <= BibleNames.JUDE;
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
        public boolean contains(int book) {
            return book >= BibleNames.ROMANS && book <= BibleNames.JUDE;
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
        public boolean contains(int book) {
            return book == BibleNames.REVELATION;
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
     * This method is present for the sake of org.crosswire.biblemapper.sw*ng.GroupVerseColor.
     * @param book
     * @return true if the book is contained within the division
     */
    public abstract boolean contains(int book);

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

}
