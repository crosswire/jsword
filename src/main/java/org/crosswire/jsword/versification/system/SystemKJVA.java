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
 * © CrossWire Bible Society, 2012 - 2016
 *
 */
package org.crosswire.jsword.versification.system;

import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.Versification;

import java.util.Arrays;

/**
 *
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class SystemKJVA extends Versification {
    /**
     * Build the "KJVA" Versification.
     */
    /* protected */ SystemKJVA() {
        super(V11N_NAME, BOOKS_OT, BOOKS_NT, LAST_VERSE_OT, LAST_VERSE_NT);
    }

    public static final String V11N_NAME = "KJVA";

    private static <T> T[] concat(T[] array1, T[] array2) {
        T[] result = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }

    // All KJV old testament books followed by all deuterocanonical books.
    /* protected */ static final BibleBook[] BOOKS_OT = concat(SystemKJV.BOOKS_OT, new BibleBook[] {
        BibleBook.ESD1,
        BibleBook.ESD2,
        BibleBook.TOB,
        BibleBook.JDT,
        BibleBook.ADD_ESTH,
        BibleBook.WIS,
        BibleBook.SIR,
        BibleBook.BAR,
        BibleBook.PR_AZAR,
        BibleBook.SUS,
        BibleBook.BEL,
        BibleBook.PR_MAN,
        BibleBook.MACC1,
        BibleBook.MACC2,
    });

    /* protected */ static final BibleBook[] BOOKS_NT = SystemDefault.BOOKS_NT;

    // Same verse numbering as KJV for regular OT, followed by additional verse numberings for deuterocanonical books.
    /* protected */ static final int[][] LAST_VERSE_OT = concat(SystemKJV.LAST_VERSE_OT, new int[][]{
        // I Esdras
        {
            58,  30,  24,  63,  73,  34,  15,  96,  55,
        },
        // II Esdras
        {
            40,  48,  36,  52,  56,  59,  70,  63,  47,  59,
            46,  51,  58,  48,  63,  78,
        },
        // Tobit
        {
            22,  14,  17,  21,  22,  17,  18,  21,   6,  12,
            19,  22,  18,  15,
        },
        // Judith
        {
            16,  28,  10,  15,  24,  21,  32,  36,  14,  23,
            23,  20,  20,  19,  13,  25,
        },
        // Additions to Esther
        {
            1,   1,   1,   1,   1,   1,   1,   1,   1,  13,
            12,   6,  18,  19,  16,  24,
        },
        // Wisdom
        {
            16,  24,  19,  20,  23,  25,  30,  21,  18,  21,
            26,  27,  19,  31,  19,  29,  21,  25,  22,
        },
        // Sirach
        {
            30,  18,  31,  31,  15,  37,  36,  19,  18,  31,
            34,  18,  26,  27,  20,  30,  32,  33,  30,  32,
            28,  27,  28,  34,  26,  29,  30,  26,  28,  25,
            31,  24,  31,  26,  20,  26,  31,  34,  35,  30,
            24,  25,  33,  22,  26,  20,  25,  25,  16,  29,
            30,
        },
        // Baruch
        {
            22,  35,  37,  37,   9,  73,
        },
        // Prayer of Azariah
        {
            68,
        },
        // Susanna
        {
            64,
        },
        // Bel and the Dragon
        {
            42,
        },
        // Prayer of Manasses
        {
            1,
        },
        // I Maccabees
        {
            64,  70,  60,  61,  68,  63,  50,  32,  73,  89,
            74,  53,  53,  49,  41,  24,
        },
        // II Maccabees
        {
            36,  32,  40,  50,  27,  31,  42,  36,  29,  38,
            38,  45,  26,  46,  39,
        },
    });

    /* protected */ static final int[][] LAST_VERSE_NT = SystemKJV.LAST_VERSE_NT;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 1054681694714921358L;
}
