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
 * Copyright: 2012
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: org.eclipse.jdt.ui.prefs 1178 2006-11-06 12:48:02Z dmsmith $
 */
package org.crosswire.jsword.versification;

import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Verse;

/**
 * A named ReferenceSystem defines
 * the order of BibleBooks by Testament,
 * the number of chapters in each BibleBook,
 * the number of verses in each chapter.
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class ReferenceSystem {
    /**
     * Construct a ReferenceSystem.
     * @param osisName
     */
    public ReferenceSystem(String osisName) {
        this.osisName = osisName;
    }

    /**
     * Get the OSIS name for this ReferenceSystem.
     * @return the OSIS name of the ReferenceSystem
     */
    public String getOSISName() {
        return osisName;
    }

    /**
     * Given a BibleBook, get the previous BibleBook in this ReferenceSystem. If it is the first book, return null.
     * @param book A BibleBook in the ReferenceSystem
     * @return the previous BibleBook or null.
     */
    public BibleBook getPreviousBook(BibleBook book) {
        try {
            return books[book.ordinal() - 1];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Given a BibleBook, get the next BibleBook in this ReferenceSystem. If it is the last book, return null.
     * @param book A BibleBook in the ReferenceSystem
     * @return the previous BibleBook or null.
     */
    public BibleBook getNextBook(BibleBook book) {
        try {
            return books[book.ordinal() + 1];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Get the last valid chapter number for a book.
     * 
     * @param book
     *            The book part of the reference.
     * @return The last valid chapter number for a book.
     * @exception NoSuchVerseException
     *                If the book is not valid
     */
    public int getLastChapter(BibleBook book) throws NoSuchVerseException {
        // This is faster than doing the check explicitly, unless
        // The exception is actually thrown, then it is a lot slower
        // I'd like to think that the norm is to get it right
        try {
            return lastVerse[book.ordinal()].length - 1;
        } catch (NullPointerException ex) {
            return 0;
        } catch (ArrayIndexOutOfBoundsException ex) {
            return 0;
        }
    }

    /**
     * Get the last valid verse number for a chapter.
     * 
     * @param book
     *            The book part of the reference.
     * @param chapter
     *            The current chapter
     * @return The last valid verse number for a chapter
     * @exception NoSuchVerseException
     *                If the book or chapter number is not valid
     */
    public int getLastVerse(BibleBook book, int chapter) throws NoSuchVerseException {
        // This is faster than doing the check explicitly, unless
        // The exception is actually thrown, then it is a lot slower
        // I'd like to think that the norm is to get it right
        try {
            return lastVerse[book.ordinal()][chapter];
        } catch (NullPointerException ex) {
            return 0;
        } catch (ArrayIndexOutOfBoundsException ex) {
            return 0;
        }
    }

    /**
     * The maximum number of verses in the Bible, including module, testament, book and chapter introductions.
     * 
     * @return the number of addressable verses in this versification.
     */
    public int maximumOrdinal() {
        // The sentinel value in chapterStarts points to what would be the book introduction
        // of the book following the last book in the NT, typically REV.
        return chapterStarts[chapterStarts.length - 1][0] - 1;
    }

    /**
     * Where does this verse come in the Bible. The value that this returns should be treated as opaque, useful for a bit set.
     * The introductions to the Book, OT/NT Testaments, Bible books and chapters are included here.
     * <ul>
     * <li>0 - INTRO_BIBLE 0:0 - The Book introduction</li>
     * <li>1 - INTRO_OT 0:0 - The OT Testament introduction</li>
     * <li>2 - Gen 0:0 - The introduction to the book of Genesis</li>
     * <li>3 - Gen 1:0 - The introduction to Genesis chapter 1</li>
     * <li>4 - Gen 1:1</li>
     * <li>...</li>
     * <li>35 - Gen 1:31</li>
     * <li>36 - Gen 2:0 - The introduction to Genesis chapter 2</li>
     * <li>37 - Gen 2:1</li>
     * <li>...</li>
     * <li>n - last verse in the OT</li>
     * <li>n + 1 - INTRO_NT, 0, 0 - The New Testament introduction</li>
     * <li>n + 2 - Matt 0:0 - The introduction to Matt</li>
     * <li>n + 3 - Matt 1:0 - The introduction to Matt 1</li>
     * <li>n + 4 - Matt 1:1</li>
     * <li>...</li>
     * </ul>
     * 
     * @param verse
     *            The verse to convert
     * @return The ordinal number of verses
     * @exception NoSuchVerseException
     *                If the reference is illegal
     */
    public int getOrdinal(Verse verse) throws NoSuchVerseException {
        BibleBook b = verse.getBook();
        int c = verse.getChapter();
        int v = verse.getVerse();
        validate(b, c, v);
        return chapterStarts[b.ordinal()][c] + v;
    }

    /**
     * Where does this verse come in the Bible. The value that this returns should be treated as opaque, useful for a bit set.
     * The introductions to the Book, OT/NT Testaments, Bible books and chapters are included here.
     * <ul>
     * <li>0 - INTRO_BIBLE 0:0 - The Book introduction</li>
     * <li>1 - INTRO_OT 0:0 - The OT Testament introduction</li>
     * <li>2 - Gen 0:0 - The introduction to the book of Genesis</li>
     * <li>3 - Gen 1:0 - The introduction to Genesis chapter 1</li>
     * <li>4 - Gen 1:1</li>
     * <li>...</li>
     * <li>35 - Gen 1:31</li>
     * <li>36 - Gen 2:0 - The introduction to Genesis chapter 2</li>
     * <li>37 - Genesis 2:1</li>
     * <li>...</li>
     * <li>n - last verse in the OT</li>
     * <li>0 - INTRO_NT, 0, 0 - The New Testament introduction</li>
     * <li>1 - Matt 0:0 - The introduction to Matt</li>
     * <li>2 - Matt 1:0 - The introduction to Matt 1</li>
     * <li>3 - Matt 1:1</li>
     * <li>...</li>
     * </ul>
     * 
     * @param verse
     *            The verse to convert
     * @return The ordinal number of verses
     * @exception NoSuchVerseException
     *                If the reference is illegal
     */
    public int getTestamentOrdinal(Verse verse) throws NoSuchVerseException {
        BibleBook b = verse.getBook();
        int c = verse.getChapter();
        int v = verse.getVerse();
        validate(b, c, v);
        int ordinal = chapterStarts[b.ordinal()][c] + v;
        if (ordinal >= ntSart) {
            return ordinal - ntSart + 1;
        }
        return ordinal;
    }

    /**
     * Where does this verse come in the Bible. This will unwind the value returned by getOrdinal(Verse).
     * 
     * @param ordinal
     *            The ordinal number of the verse
     * @return A Verse
     * @exception NoSuchVerseException
     *                If the reference is illegal
     */
    public Verse decodeOrdinal(int ordinal) throws NoSuchVerseException {
        BibleBook book = null;
        int bookIndex = -1;
        int chapterIndex = 0;
        int verse = 0;

        if (ordinal < 0 || ordinal > maximumOrdinal()) {
            throw new NoSuchVerseException(JSOtherMsg.lookupText("Ordinal must be between 0 and {0,number,integer} (given {1,number,integer}).", Integer.valueOf(maximumOrdinal()), Integer.valueOf(ordinal)));
        }

        // Handle three special cases
        // Book/Module introduction
        if (ordinal == 0) {
            return new Verse(BibleBook.INTRO_BIBLE, 0, 0);
        }

        // OT introduction
        if (ordinal == 1) {
            return new Verse(BibleBook.INTRO_OT, 0, 0);
        }

        // NT introduction
        if (ordinal == ntSart) {
            return new Verse(BibleBook.INTRO_NT, 0, 0);
        }

        // The chapterStarts has a sentinel value at the end of the array
        // Therefore, subtract  1
        int lastBook = chapterStarts.length - 1;
        for (int b = lastBook; b >= 0; b--) {
            // A book has a slot for a heading followed by a slot for a chapter heading.
            // These precede the start of the chapter.
            if (ordinal >= chapterStarts[b][0]) {
                bookIndex = b;
                break;
            }
        }

        // There is a gap for the New Testament introduction.
        // This occurs when ordinal is one less than the book introduction of the next book.
        if (bookIndex == otLastBook && ordinal == chapterStarts[bookIndex + 1][0] - 1) {
            bookIndex++;
        }

        book = BibleBook.getBooks()[bookIndex];
        int cib = getLastChapter(book);
        for (int c = cib; c >= 0; c--) {
            if (ordinal >= chapterStarts[bookIndex][c]) {
                chapterIndex = c;
                break;
            }
        }

        if (chapterIndex > 0) {
            verse = ordinal - chapterStarts[bookIndex][chapterIndex];
        }
        
        return new Verse(book, chapterIndex, verse);
    }

    /**
     * Does the following represent a real verse?. It is code like this that
     * makes me wonder if I18 is done well/worth doing. All this code does is
     * check if the numbers are valid, but the exception handling code is huge
     * :(
     * 
     * @param book
     *            The book part of the reference.
     * @param chapter
     *            The chapter part of the reference.
     * @param verse
     *            The verse part of the reference.
     * @exception NoSuchVerseException
     *                If the reference is illegal
     */
    public void validate(BibleBook book, int chapter, int verse) throws NoSuchVerseException {

        // Check the chapter
        int maxChapter = getLastChapter(book);
        if (chapter < 0 || chapter > maxChapter) {
            // TRANSLATOR: The user supplied a chapter that was out of bounds. This tells them what is allowed.
            // {0} is the lowest value that is allowed. This is always 0.
            // {1,number,integer} is the place holder for the highest chapter number in the book. The format is special in that it will present it in the user's preferred format.
            // {2} is a placeholder for the Bible book name.
            // {3,number,integer} is a placeholder for the chapter number that the user gave.
            throw new NoSuchVerseException(JSMsg.gettext("Chapter should be between {0} and {1,number,integer} for {2} (given {3,number,integer}).",
                    Integer.valueOf(0), Integer.valueOf(maxChapter), book.getPreferredName(), Integer.valueOf(chapter)
                    ));
        }

        // Check the verse
        int maxVerse = getLastVerse(book, chapter);
        if (verse < 0 || verse > maxVerse) {
            // TRANSLATOR: The user supplied a verse number that was out of bounds. This tells them what is allowed.
            // {0} is the lowest value that is allowed. This is always 0.
            // {1,number,integer} is the place holder for the highest verse number in the chapter. The format is special in that it will present it in the user's preferred format.
            // {2} is a placeholder for the Bible book name.
            // {3,number,integer} is a placeholder for the chapter number that the user gave.
            // {4,number,integer} is a placeholder for the verse number that the user gave.
            throw new NoSuchVerseException(JSMsg.gettext("Verse should be between {0} and {1,number,integer} for {2} {3,number,integer} (given {4,number,integer}).",
                    Integer.valueOf(0), Integer.valueOf(maxVerse), book.getPreferredName(), Integer.valueOf(chapter), Integer.valueOf(verse)
                    ));
        }
    }

    /**
     * Fix up these verses so that they are as valid a possible. This is
     * currently done so that we can say "Gen 1:1" + 31 = "Gen 1:32" and
     * "Gen 1:32".patch() is "Gen 2:1".
     * <p>
     * There is another patch system that allows us to use large numbers to mean
     * "the end of" so "Gen 1:32".otherPatch() gives "Gen 1:31". This could be
     * useful to allow the user to enter things like "Gen 1:99" meaning the end
     * of the chapter. Or "Isa 99:1" to mean the last chapter in Isaiah verse 1
     * or even "Rev 99:99" to mean the last verse in the Bible.
     * <p>
     * However I have not implemented this because I've used a different
     * convention: "Gen 1:$" (OLB compatible) or "Gen 1:ff" (common commentary
     * usage) to mean the end of the chapter - So the functionality is there
     * anyway.
     * <p>
     * I think that getting into the habit of typing "Gen 1:99" is bad. It could
     * be the source of surprises "Psa 119:99" is not what you'd might expect,
     * and neither is "Psa 99:1" is you wanted the last chapter in Psalms -
     * expecting us to type "Psa 999:1" seems like we're getting silly.
     * <p>
     * However despite this maybe we should provide the functionality anyway.
     * 
     * @param book the book to obtain
     * @param chapter the supposed chapter
     * @param verse the supposed verse
     * @return The resultant verse.
     */
    public Verse patch(BibleBook book, int chapter, int verse) {
        BibleBook patchedBook = book;
        int patchedChapter = chapter;
        int patchedVerse = verse;
        
        try {
            // If the book is null, then patch to GENESIS
            if (patchedBook == null) {
                patchedBook = BibleBook.GEN;
            }
            // If they are too small
            if (patchedChapter < 0) {
                patchedChapter = 0;
            }
            if (patchedVerse < 0) {
                patchedVerse = 0;
            }

            while (patchedChapter > getLastChapter(patchedBook)) {
                patchedChapter -= getLastChapter(patchedBook);
                patchedBook = getNextBook(patchedBook);

                if (patchedBook == null) {
                    patchedBook = BibleBook.REV;
                    patchedChapter = getLastChapter(patchedBook);
                    patchedVerse = getLastVerse(patchedBook, patchedChapter);
                    return new Verse(patchedBook, patchedChapter, patchedVerse);
                }
            }

            while (patchedVerse > getLastVerse(patchedBook, patchedChapter)) {
                patchedVerse -= getLastVerse(patchedBook, patchedChapter);
                patchedChapter += 1;

                if (patchedChapter > getLastChapter(patchedBook)) {
                    patchedChapter -= getLastChapter(patchedBook);
                    patchedBook = getNextBook(patchedBook);

                    if (patchedBook == null) {
                        patchedBook = BibleBook.REV;
                        patchedChapter = getLastChapter(patchedBook);
                        patchedVerse = getLastVerse(patchedBook, patchedChapter);
                        return new Verse(patchedBook, patchedChapter, patchedVerse);
                    }
                }
            }

            return new Verse(patchedBook, patchedChapter, patchedVerse);
        } catch (NoSuchKeyException ex) {
            assert false : ex;
            return new Verse(BibleBook.GEN, 1, 1, true);
        }
    }

    /** The OSIS name of the reference system. */
    private String osisName;

    /** The ordered list of BibleBooks */
    private BibleBook[] books;

    private int otLastBook = 38;
    private int ntSart = 24115;

    /** Constant for the max verse number in each chapter */
    private int[][] lastVerse =
    {
        // Bible Introduction
        {
                0,
        },
        // Old Testament Introduction
        {
                0,
        },
        // Gen
        {
                0,    31,    25,    24,    26,    32,    22,    24,    22,    29,
               32,    32,    20,    18,    24,    21,    16,    27,    33,    38,
               18,    34,    24,    20,    67,    34,    35,    46,    22,    35,
               43,    55,    32,    20,    31,    29,    43,    36,    30,    23,
               23,    57,    38,    34,    34,    28,    34,    31,    22,    33,
               26,
        },
        // Exod
        {
                0,    22,    25,    22,    31,    23,    30,    25,    32,    35,
               29,    10,    51,    22,    31,    27,    36,    16,    27,    25,
               26,    36,    31,    33,    18,    40,    37,    21,    43,    46,
               38,    18,    35,    23,    35,    35,    38,    29,    31,    43,
               38,
        },
        // Lev
        {
                0,    17,    16,    17,    35,    19,    30,    38,    36,    24,
               20,    47,     8,    59,    57,    33,    34,    16,    30,    37,
               27,    24,    33,    44,    23,    55,    46,    34,
        },
        // Num
        {
                0,    54,    34,    51,    49,    31,    27,    89,    26,    23,
               36,    35,    16,    33,    45,    41,    50,    13,    32,    22,
               29,    35,    41,    30,    25,    18,    65,    23,    31,    40,
               16,    54,    42,    56,    29,    34,    13,
        },
        // Deut
        {
                0,    46,    37,    29,    49,    33,    25,    26,    20,    29,
               22,    32,    32,    18,    29,    23,    22,    20,    22,    21,
               20,    23,    30,    25,    22,    19,    19,    26,    68,    29,
               20,    30,    52,    29,    12,
        },
        // Josh
        {
                0,    18,    24,    17,    24,    15,    27,    26,    35,    27,
               43,    23,    24,    33,    15,    63,    10,    18,    28,    51,
                9,    45,    34,    16,    33,
        },
        // Judg
        {
                0,    36,    23,    31,    24,    31,    40,    25,    35,    57,
               18,    40,    15,    25,    20,    20,    31,    13,    31,    30,
               48,    25,
        },
        // Ruth
        {
                0,    22,    23,    18,    22,
        },
        // 1Sam
        {
                0,    28,    36,    21,    22,    12,    21,    17,    22,    27,
               27,    15,    25,    23,    52,    35,    23,    58,    30,    24,
               42,    15,    23,    29,    22,    44,    25,    12,    25,    11,
               31,    13,
        },
        // 2Sam
        {
                0,    27,    32,    39,    12,    25,    23,    29,    18,    13,
               19,    27,    31,    39,    33,    37,    23,    29,    33,    43,
               26,    22,    51,    39,    25,
        },
        // 1Kgs
        {
                0,    53,    46,    28,    34,    18,    38,    51,    66,    28,
               29,    43,    33,    34,    31,    34,    34,    24,    46,    21,
               43,    29,    53,
        },
        // 2Kgs
        {
                0,    18,    25,    27,    44,    27,    33,    20,    29,    37,
               36,    21,    21,    25,    29,    38,    20,    41,    37,    37,
               21,    26,    20,    37,    20,    30,
        },
        // 1Chr
        {
                0,    54,    55,    24,    43,    26,    81,    40,    40,    44,
               14,    47,    40,    14,    17,    29,    43,    27,    17,    19,
                8,    30,    19,    32,    31,    31,    32,    34,    21,    30,
        },
        // 2Chr
        {
                0,    17,    18,    17,    22,    14,    42,    22,    18,    31,
               19,    23,    16,    22,    15,    19,    14,    19,    34,    11,
               37,    20,    12,    21,    27,    28,    23,     9,    27,    36,
               27,    21,    33,    25,    33,    27,    23,
        },
        // Ezra
        {
                0,    11,    70,    13,    24,    17,    22,    28,    36,    15,
               44,
        },
        // Neh
        {
                0,    11,    20,    32,    23,    19,    19,    73,    18,    38,
               39,    36,    47,    31,
        },
        // Esth
        {
                0,    22,    23,    15,    17,    14,    14,    10,    17,    32,
                3,
        },
        // Job
        {
                0,    22,    13,    26,    21,    27,    30,    21,    22,    35,
               22,    20,    25,    28,    22,    35,    22,    16,    21,    29,
               29,    34,    30,    17,    25,     6,    14,    23,    28,    25,
               31,    40,    22,    33,    37,    16,    33,    24,    41,    30,
               24,    34,    17,
        },
        // Ps
        {
                0,     6,    12,     8,     8,    12,    10,    17,     9,    20,
               18,     7,     8,     6,     7,     5,    11,    15,    50,    14,
                9,    13,    31,     6,    10,    22,    12,    14,     9,    11,
               12,    24,    11,    22,    22,    28,    12,    40,    22,    13,
               17,    13,    11,     5,    26,    17,    11,     9,    14,    20,
               23,    19,     9,     6,     7,    23,    13,    11,    11,    17,
               12,     8,    12,    11,    10,    13,    20,     7,    35,    36,
                5,    24,    20,    28,    23,    10,    12,    20,    72,    13,
               19,    16,     8,    18,    12,    13,    17,     7,    18,    52,
               17,    16,    15,     5,    23,    11,    13,    12,     9,     9,
                5,     8,    28,    22,    35,    45,    48,    43,    13,    31,
                7,    10,    10,     9,     8,    18,    19,     2,    29,   176,
                7,     8,     9,     4,     8,     5,     6,     5,     6,     8,
                8,     3,    18,     3,     3,    21,    26,     9,     8,    24,
               13,    10,     7,    12,    15,    21,    10,    20,    14,     9,
                6,
        },
        // Prov
        {
                0,    33,    22,    35,    27,    23,    35,    27,    36,    18,
               32,    31,    28,    25,    35,    33,    33,    28,    24,    29,
               30,    31,    29,    35,    34,    28,    28,    27,    28,    27,
               33,    31,
        },
        // Eccl
        {
                0,    18,    26,    22,    16,    20,    12,    29,    17,    18,
               20,    10,    14,
        },
        // Song
        {
                0,    17,    17,    11,    16,    16,    13,    13,    14,
        },
        // Isa
        {
                0,    31,    22,    26,     6,    30,    13,    25,    22,    21,
               34,    16,     6,    22,    32,     9,    14,    14,     7,    25,
                6,    17,    25,    18,    23,    12,    21,    13,    29,    24,
               33,     9,    20,    24,    17,    10,    22,    38,    22,     8,
               31,    29,    25,    28,    28,    25,    13,    15,    22,    26,
               11,    23,    15,    12,    17,    13,    12,    21,    14,    21,
                      22,    11,    12,    19,    12,    25,    24,
        },
        // Jer
        {
                0,    19,    37,    25,    31,    31,    30,    34,    22,    26,
               25,    23,    17,    27,    22,    21,    21,    27,    23,    15,
               18,    14,    30,    40,    10,    38,    24,    22,    17,    32,
               24,    40,    44,    26,    22,    19,    32,    21,    28,    18,
               16,    18,    22,    13,    30,     5,    28,     7,    47,    39,
               46,    64,    34,
        },
        // Lam
        {
            0,    22,    22,    66,    22,    22,
        },
        // Ezek
        {
                0,    28,    10,    27,    17,    17,    14,    27,    18,    11,
               22,    25,    28,    23,    23,     8,    63,    24,    32,    14,
               49,    32,    31,    49,    27,    17,    21,    36,    26,    21,
               26,    18,    32,    33,    31,    15,    38,    28,    23,    29,
               49,    26,    20,    27,    31,    25,    24,    23,    35,
        },
        // Dan
        {
                0,    21,    49,    30,    37,    31,    28,    28,    27,    27,
               21,    45,    13,
        },
        // Hos
        {
                0,    11,    23,     5,    19,    15,    11,    16,    14,    17,
               15,    12,    14,    16,     9,
        },
        // Joel
        {
                0,    20,    32,    21,
        },
        // Amos
        {
                0,    15,    16,    15,    13,    27,    14,    17,    14,    15,
        },
        // Obad
        {
                0,    21,
        },
        // Jonah
        {
                0,    17,    10,    10,    11,
        },
        // Mic
        {
                0,    16,    13,    12,    13,    15,    16,    20,
        },
        // Nah
        {
                0,    15,    13,    19,
        },
        // Hab
        {
                0,    17,    20,    19,
        },
        // Zeph
        {
                0,    18,    15,    20,
        },
        // Hag
        {
                0,    15,    23,
        },
        // Zech
        {
                0,    21,    13,    10,    14,    11,    15,    14,    23,    17,
               12,    17,    14,     9,    21,
        },
        // Mal
        {
                0,    14,    17,    18,     6,
        },
        // New Testament Introduction
        {
                0,
        },
        // Matt
        {
                0,    25,    23,    17,    25,    48,    34,    29,    34,    38,
               42,    30,    50,    58,    36,    39,    28,    27,    35,    30,
               34,    46,    46,    39,    51,    46,    75,    66,    20,
        },
        // Mark
        {
                0,    45,    28,    35,    41,    43,    56,    37,    38,    50,
               52,    33,    44,    37,    72,    47,    20,
        },
        // Luke
        {
                0,    80,    52,    38,    44,    39,    49,    50,    56,    62,
               42,    54,    59,    35,    35,    32,    31,    37,    43,    48,
               47,    38,    71,    56,    53,
        },
        // John
        {
                0,    51,    25,    36,    54,    47,    71,    53,    59,    41,
               42,    57,    50,    38,    31,    27,    33,    26,    40,    42,
               31,    25,
        },
        // Acts
        {
                0,    26,    47,    26,    37,    42,    15,    60,    40,    43,
               48,    30,    25,    52,    28,    41,    40,    34,    28,    41,
               38,    40,    30,    35,    27,    27,    32,    44,    31,
        },
        // Rom
        {
                0,    32,    29,    31,    25,    21,    23,    25,    39,    33,
               21,    36,    21,    14,    23,    33,    27,
        },
        // 1Cor
        {
                0,    31,    16,    23,    21,    13,    20,    40,    13,    27,
               33,    34,    31,    13,    40,    58,    24,
        },
        // 2Cor
        {
                0,    24,    17,    18,    18,    21,    18,    16,    24,    15,
               18,    33,    21,    14,
        },
        // Gal
        {
                0,    24,    21,    29,    31,    26,    18,
        },
        // Eph
        {
                0,    23,    22,    21,    32,    33,    24,
        },
        // Phil
        {
                0,    30,    30,    21,    23,
        },
        // Col
        {
                0,    29,    23,    25,    18,
        },
        // 1Thess
        {
                0,    10,    20,    13,    18,    28,
        },
        // 2Thess
        {
                0,    12,    17,    18,
        },
        // 1Tim
        {
                0,    20,    15,    16,    16,    25,    21,
        },
        // 2Tim
        {
                0,    18,    26,    17,    22,
        },
        // Titus
        {
                0,    16,    15,    15,
        },
        // Phlm
        {
                0,    25,
        },
        // Heb
        {
                0,    14,    18,    19,    16,    14,    20,    28,    13,    28,
               39,    40,    29,    25,
        },
        // Jas
        {
                0,    27,    26,    18,    17,    20,
        },
        // 1Pet
        {
                0,    25,    25,    22,    19,    14,
        },
        // 2Pet
        {
                0,    21,    22,    18,
        },
        // 1John
        {
                0,    10,    29,    24,    21,    21,
        },
        // 2John
        {
                0,    13,
        },
        // 3John
        {
                0,    14,
        },
        // Jude
        {
                0,    25,
        },
        // Rev
        {
                0,    20,    29,    22,    11,    14,    17,    17,    13,    21,
               11,    19,    17,    18,    20,     8,    21,    18,    24,    21,
               15,    27,    21,
        },
    };

    /**
     * Constant for the ordinal number of the first verse in each chapter.
     */
    // Note the sentinel at the end of the array is one greater
    // than the last ordinal in the last book
    private int[][] chapterStarts =
    {
        // Bible Introduction
        {
                0,
        },
        // Old Testament Introduction
        {
                1,
        },
        // Gen
        { 
                2,     3,    35,    61,    86,   113,   146,   169,   194,   217, 
              247,   280,   313,   334,   353,   378,   400,   417,   445,   479, 
              518,   537,   572,   597,   618,   686,   721,   757,   804,   827, 
              863,   907,   963,   996,  1017,  1049,  1079,  1123,  1160,  1191, 
             1215,  1239,  1297,  1336,  1371,  1406,  1435,  1470,  1502,  1525, 
             1559, 
        },
        // Exod
        { 
             1586,  1587,  1610,  1636,  1659,  1691,  1715,  1746,  1772,  1805, 
             1841,  1871,  1882,  1934,  1957,  1989,  2017,  2054,  2071,  2099, 
             2125,  2152,  2189,  2221,  2255,  2274,  2315,  2353,  2375,  2419, 
             2466,  2505,  2524,  2560,  2584,  2620,  2656,  2695,  2725,  2757, 
             2801, 
        },
        // Lev
        { 
             2840,  2841,  2859,  2876,  2894,  2930,  2950,  2981,  3020,  3057, 
             3082,  3103,  3151,  3160,  3220,  3278,  3312,  3347,  3364,  3395, 
             3433,  3461,  3486,  3520,  3565,  3589,  3645,  3692, 
        },
        // Num
        { 
             3727,  3728,  3783,  3818,  3870,  3920,  3952,  3980,  4070,  4097, 
             4121,  4158,  4194,  4211,  4245,  4291,  4333,  4384,  4398,  4431, 
             4454,  4484,  4520,  4562,  4593,  4619,  4638,  4704,  4728,  4760, 
             4801,  4818,  4873,  4916,  4973,  5003,  5038, 
        },
        // Deut
        { 
             5052,  5053,  5100,  5138,  5168,  5218,  5252,  5278,  5305,  5326, 
             5356,  5379,  5412,  5445,  5464,  5494,  5518,  5541,  5562,  5585, 
             5607,  5628,  5652,  5683,  5709,  5732,  5752,  5772,  5799,  5868, 
             5898,  5919,  5950,  6003,  6033, 
        },
        // Josh
        { 
             6046,  6047,  6066,  6091,  6109,  6134,  6150,  6178,  6205,  6241, 
             6269,  6313,  6337,  6362,  6396,  6412,  6476,  6487,  6506,  6535, 
             6587,  6597,  6643,  6678,  6695, 
        },
        // Judg
        { 
             6729,  6730,  6767,  6791,  6823,  6848,  6880,  6921,  6947,  6983, 
             7041,  7060,  7101,  7117,  7143,  7164,  7185,  7217,  7231,  7263, 
             7294,  7343, 
        },
        // Ruth
        { 
             7369,  7370,  7393,  7417,  7436, 
        },
        // 1Sam
        { 
             7459,  7460,  7489,  7526,  7548,  7571,  7584,  7606,  7624,  7647, 
             7675,  7703,  7719,  7745,  7769,  7822,  7858,  7882,  7941,  7972, 
             7997,  8040,  8056,  8080,  8110,  8133,  8178,  8204,  8217,  8243, 
             8255,  8287, 
        },
        // 2Sam
        { 
             8301,  8302,  8330,  8363,  8403,  8416,  8442,  8466,  8496,  8515, 
             8529,  8549,  8577,  8609,  8649,  8683,  8721,  8745,  8775,  8809, 
             8853,  8880,  8903,  8955,  8995, 
        },
        // 1Kgs
        { 
             9021,  9022,  9076,  9123,  9152,  9187,  9206,  9245,  9297,  9364, 
             9393,  9423,  9467,  9501,  9536,  9568,  9603,  9638,  9663,  9710, 
             9732,  9776,  9806, 
        },
        // 2Kgs
        { 
             9860,  9861,  9880,  9906,  9934,  9979, 10007, 10041, 10062, 10092, 
            10130, 10167, 10189, 10211, 10237, 10267, 10306, 10327, 10369, 10407, 
            10445, 10467, 10494, 10515, 10553, 10574, 
        },
        // 1Chr
        { 
            10605, 10606, 10661, 10717, 10742, 10786, 10813, 10895, 10936, 10977, 
            11022, 11037, 11085, 11126, 11141, 11159, 11189, 11233, 11261, 11279, 
            11299, 11308, 11339, 11359, 11392, 11424, 11456, 11489, 11524, 11546, 
        },
        // 2Chr
        { 
            11577, 11578, 11596, 11615, 11633, 11656, 11671, 11714, 11737, 11756, 
            11788, 11808, 11832, 11849, 11872, 11888, 11908, 11923, 11943, 11978, 
            11990, 12028, 12049, 12062, 12084, 12112, 12141, 12165, 12175, 12203, 
            12240, 12268, 12290, 12324, 12350, 12384, 12412, 
        },
        // Ezra
        { 
            12436, 12437, 12449, 12520, 12534, 12559, 12577, 12600, 12629, 12666, 
            12682, 
        },
        // Neh
        { 
            12727, 12728, 12740, 12761, 12794, 12818, 12838, 12858, 12932, 12951, 
            12990, 13030, 13067, 13115, 
        },
        // Esth
        { 
            13147, 13148, 13171, 13195, 13211, 13229, 13244, 13259, 13270, 13288, 
            13321, 
        },
        // Job
        { 
            13325, 13326, 13349, 13363, 13390, 13412, 13440, 13471, 13493, 13516, 
            13552, 13575, 13596, 13622, 13651, 13674, 13710, 13733, 13750, 13772, 
            13802, 13832, 13867, 13898, 13916, 13942, 13949, 13964, 13988, 14017, 
            14043, 14075, 14116, 14139, 14173, 14211, 14228, 14262, 14287, 14329, 
            14360, 14385, 14420, 
        },
        // Ps
        { 
            14438, 14439, 14446, 14459, 14468, 14477, 14490, 14501, 14519, 14529, 
            14550, 14569, 14577, 14586, 14593, 14601, 14607, 14619, 14635, 14686, 
            14701, 14711, 14725, 14757, 14764, 14775, 14798, 14811, 14826, 14836, 
            14848, 14861, 14886, 14898, 14921, 14944, 14973, 14986, 15027, 15050, 
            15064, 15082, 15096, 15108, 15114, 15141, 15159, 15171, 15181, 15196, 
            15217, 15241, 15261, 15271, 15278, 15286, 15310, 15324, 15336, 15348, 
            15366, 15379, 15388, 15401, 15413, 15424, 15438, 15459, 15467, 15503, 
            15540, 15546, 15571, 15592, 15621, 15645, 15656, 15669, 15690, 15763, 
            15777, 15797, 15814, 15823, 15842, 15855, 15869, 15887, 15895, 15914, 
            15967, 15985, 16002, 16018, 16024, 16048, 16060, 16074, 16087, 16097, 
            16107, 16113, 16122, 16151, 16174, 16210, 16256, 16305, 16349, 16363, 
            16395, 16403, 16414, 16425, 16435, 16444, 16463, 16483, 16486, 16516, 
            16693, 16701, 16710, 16720, 16725, 16734, 16740, 16747, 16753, 16760, 
            16769, 16778, 16782, 16801, 16805, 16809, 16831, 16858, 16868, 16877, 
            16902, 16916, 16927, 16935, 16948, 16964, 16986, 16997, 17018, 17033, 
            17043, 
        },
        // Prov
        { 
            17050, 17051, 17085, 17108, 17144, 17172, 17196, 17232, 17260, 17297, 
            17316, 17349, 17381, 17410, 17436, 17472, 17506, 17540, 17569, 17594, 
            17624, 17655, 17687, 17717, 17753, 17788, 17817, 17846, 17874, 17903, 
            17931, 17965, 
        },
        // Eccl
        { 
            17997, 17998, 18017, 18044, 18067, 18084, 18105, 18118, 18148, 18166, 
            18185, 18206, 18217, 
        },
        // Song
        { 
            18232, 18233, 18251, 18269, 18281, 18298, 18315, 18329, 18343, 
        },
        // Isa
        { 
            18358, 18359, 18391, 18414, 18441, 18448, 18479, 18493, 18519, 18542, 
            18564, 18599, 18616, 18623, 18646, 18679, 18689, 18704, 18719, 18727, 
            18753, 18760, 18778, 18804, 18823, 18847, 18860, 18882, 18896, 18926, 
            18951, 18985, 18995, 19016, 19041, 19059, 19070, 19093, 19132, 19155, 
            19164, 19196, 19226, 19252, 19281, 19310, 19336, 19350, 19366, 19389, 
            19416, 19428, 19452, 19468, 19481, 19499, 19513, 19526, 19548, 19563, 
            19585, 19608, 19620, 19633, 19653, 19666, 19692, 
        },
        // Jer
        { 
            19717, 19718, 19738, 19776, 19802, 19834, 19866, 19897, 19932, 19955, 
            19982, 20008, 20032, 20050, 20078, 20101, 20123, 20145, 20173, 20197, 
            20213, 20232, 20247, 20278, 20319, 20330, 20369, 20394, 20417, 20435, 
            20468, 20493, 20534, 20579, 20606, 20629, 20649, 20682, 20704, 20733, 
            20752, 20769, 20788, 20811, 20825, 20856, 20862, 20891, 20899, 20947, 
            20987, 21034, 21099, 
        },
        // Lam
        { 
            21134, 21135, 21158, 21181, 21248, 21271, 
        },
        // Ezek
        { 
            21294, 21295, 21324, 21335, 21363, 21381, 21399, 21414, 21442, 21461, 
            21473, 21496, 21522, 21551, 21575, 21599, 21608, 21672, 21697, 21730, 
            21745, 21795, 21828, 21860, 21910, 21938, 21956, 21978, 22015, 22042, 
            22064, 22091, 22110, 22143, 22177, 22209, 22225, 22264, 22293, 22317, 
            22347, 22397, 22424, 22445, 22473, 22505, 22531, 22556, 22580, 
        },
        // Dan
        { 
            22616, 22617, 22639, 22689, 22720, 22758, 22790, 22819, 22848, 22876, 
            22904, 22926, 22972, 
        },
        // Hos
        { 
            22986, 22987, 22999, 23023, 23029, 23049, 23065, 23077, 23094, 23109, 
            23127, 23143, 23156, 23171, 23188, 
        },
        // Joel
        { 
            23198, 23199, 23220, 23253, 
        },
        // Amos
        { 
            23275, 23276, 23292, 23309, 23325, 23339, 23367, 23382, 23400, 23415, 
        },
        // Obad
        { 
            23431, 23432, 
        },
        // Jonah
        { 
            23454, 23455, 23473, 23484, 23495, 
        },
        // Mic
        { 
            23507, 23508, 23525, 23539, 23552, 23566, 23582, 23599, 
        },
        // Nah
        { 
            23620, 23621, 23637, 23651, 
        },
        // Hab
        { 
            23671, 23672, 23690, 23711, 
        },
        // Zeph
        { 
            23731, 23732, 23751, 23767, 
        },
        // Hag
        { 
            23788, 23789, 23805, 
        },
        // Zech
        { 
            23829, 23830, 23852, 23866, 23877, 23892, 23904, 23920, 23935, 23959, 
            23977, 23990, 24008, 24023, 24033, 
        },
        // Mal
        { 
            24055, 24056, 24071, 24089, 24108, 
        },
        // NT Testament Introduction
        {
            24115,
        },
        // Matt
        { 
            24116, 24117, 24143, 24167, 24185, 24211, 24260, 24295, 24325, 24360, 
            24399, 24442, 24473, 24524, 24583, 24620, 24660, 24689, 24717, 24753, 
            24784, 24819, 24866, 24913, 24953, 25005, 25052, 25128, 25195, 
        },
        // Mark
        { 
            25216, 25217, 25263, 25292, 25328, 25370, 25414, 25471, 25509, 25548, 
            25599, 25652, 25686, 25731, 25769, 25842, 25890, 
        },
        // Luke
        { 
            25911, 25912, 25993, 26046, 26085, 26130, 26170, 26220, 26271, 26328, 
            26391, 26434, 26489, 26549, 26585, 26621, 26654, 26686, 26724, 26768, 
            26817, 26865, 26904, 26976, 27033, 
        },
        // John
        { 
            27087, 27088, 27140, 27166, 27203, 27258, 27306, 27378, 27432, 27492, 
            27534, 27577, 27635, 27686, 27725, 27757, 27785, 27819, 27846, 27887, 
            27930, 27962, 
        },
        // Acts
        { 
            27988, 27989, 28016, 28064, 28091, 28129, 28172, 28188, 28249, 28290, 
            28334, 28383, 28414, 28440, 28493, 28522, 28564, 28605, 28640, 28669, 
            28711, 28750, 28791, 28822, 28858, 28886, 28914, 28947, 28992, 
        },
        // Rom
        { 
            29024, 29025, 29058, 29088, 29120, 29146, 29168, 29192, 29218, 29258, 
            29292, 29314, 29351, 29373, 29388, 29412, 29446, 
        },
        // 1Cor
        { 
            29474, 29475, 29507, 29524, 29548, 29570, 29584, 29605, 29646, 29660, 
            29688, 29722, 29757, 29789, 29803, 29844, 29903, 
        },
        // 2Cor
        { 
            29928, 29929, 29954, 29972, 29991, 30010, 30032, 30051, 30068, 30093, 
            30109, 30128, 30162, 30184, 
        },
        // Gal
        { 
            30199, 30200, 30225, 30247, 30277, 30309, 30336, 
        },
        // Eph
        { 
            30355, 30356, 30380, 30403, 30425, 30458, 30492, 
        },
        // Phil
        { 
            30517, 30518, 30549, 30580, 30602, 
        },
        // Col
        { 
            30626, 30627, 30657, 30681, 30707, 
        },
        // 1Thess
        { 
            30726, 30727, 30738, 30759, 30773, 30792, 
        },
        // 2Thess
        { 
            30821, 30822, 30835, 30853, 
        },
        // 1Tim
        { 
            30872, 30873, 30894, 30910, 30927, 30944, 30970, 
        },
        // 2Tim
        { 
            30992, 30993, 31012, 31039, 31057, 
        },
        // Titus
        { 
            31080, 31081, 31098, 31114, 
        },
        // Phlm
        { 
            31130, 31131, 
        },
        // Heb
        { 
            31157, 31158, 31173, 31192, 31212, 31229, 31244, 31265, 31294, 31308, 
            31337, 31377, 31418, 31448, 
        },
        // Jas
        { 
            31474, 31475, 31503, 31530, 31549, 31567, 
        },
        // 1Pet
        { 
            31588, 31589, 31615, 31641, 31664, 31684, 
        },
        // 2Pet
        { 
            31699, 31700, 31722, 31745, 
        },
        // 1John
        { 
            31764, 31765, 31776, 31806, 31831, 31853, 
        },
        // 2John
        { 
            31875, 31876, 
        },
        // 3John
        { 
            31890, 31891, 
        },
        // Jude
        { 
            31906, 31907, 
        },
        // Rev
        { 
            31933, 31934, 31955, 31985, 32008, 32020, 32035, 32053, 32071, 32085, 
            32107, 32119, 32139, 32157, 32176, 32197, 32206, 32228, 32247, 32272, 
            32294, 32310, 32338, 
        },
        // Sentinel
        { 
            32360, 
        },
    };
}
