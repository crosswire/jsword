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

/**
 * The NRSV Versification is nearly the same as the KJV versification.
 * It differs in that 3 John has 15 verses not 14 and Revelation 12
 * has 18 verses not 17.
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class SystemNRSV extends Versification {
    /**
     * Build the "NRSV" Versification.
     */
    /* protected */ SystemNRSV() {
        super(V11N_NAME, BOOKS_OT, BOOKS_NT, LAST_VERSE_OT, LAST_VERSE_NT);
    }

    public static final String V11N_NAME = "NRSV";

    /* protected */ static final BibleBook[] BOOKS_OT = SystemDefault.BOOKS_OT;

    /* protected */ static final BibleBook[] BOOKS_NT = SystemDefault.BOOKS_NT;

    /* protected */ static final int[][] LAST_VERSE_OT = SystemKJV.LAST_VERSE_OT;

    /* protected */ static final int[][] LAST_VERSE_NT =
    {
        // Matthew
        {
           25,  23,  17,  25,  48,  34,  29,  34,  38,  42,
           30,  50,  58,  36,  39,  28,  27,  35,  30,  34,
           46,  46,  39,  51,  46,  75,  66,  20,
        },
        // Mark
        {
           45,  28,  35,  41,  43,  56,  37,  38,  50,  52,
           33,  44,  37,  72,  47,  20,
        },
        // Luke
        {
           80,  52,  38,  44,  39,  49,  50,  56,  62,  42,
           54,  59,  35,  35,  32,  31,  37,  43,  48,  47,
           38,  71,  56,  53,
        },
        // John
        {
           51,  25,  36,  54,  47,  71,  53,  59,  41,  42,
           57,  50,  38,  31,  27,  33,  26,  40,  42,  31,
           25,
        },
        // Acts
        {
           26,  47,  26,  37,  42,  15,  60,  40,  43,  48,
           30,  25,  52,  28,  41,  40,  34,  28,  41,  38,
           40,  30,  35,  27,  27,  32,  44,  31,
        },
        // Romans
        {
           32,  29,  31,  25,  21,  23,  25,  39,  33,  21,
           36,  21,  14,  23,  33,  27,
        },
        // I Corinthians
        {
           31,  16,  23,  21,  13,  20,  40,  13,  27,  33,
           34,  31,  13,  40,  58,  24,
        },
        // II Corinthians
        {
           24,  17,  18,  18,  21,  18,  16,  24,  15,  18,
           33,  21,  14,
        },
        // Galatians
        {
           24,  21,  29,  31,  26,  18,
        },
        // Ephesians
        {
           23,  22,  21,  32,  33,  24,
        },
        // Philippians
        {
           30,  30,  21,  23,
        },
        // Colossians
        {
           29,  23,  25,  18,
        },
        // I Thessalonians
        {
           10,  20,  13,  18,  28,
        },
        // II Thessalonians
        {
           12,  17,  18,
        },
        // I Timothy
        {
           20,  15,  16,  16,  25,  21,
        },
        // II Timothy
        {
           18,  26,  17,  22,
        },
        // Titus
        {
           16,  15,  15,
        },
        // Philemon
        {
           25,
        },
        // Hebrews
        {
           14,  18,  19,  16,  14,  20,  28,  13,  28,  39,
           40,  29,  25,
        },
        // James
        {
           27,  26,  18,  17,  20,
        },
        // I Peter
        {
           25,  25,  22,  19,  14,
        },
        // II Peter
        {
           21,  22,  18,
        },
        // I John
        {
           10,  29,  24,  21,  21,
        },
        // II John
        {
           13,
        },
        // III John
        {
           15,
        },
        // Jude
        {
           25,
        },
        // Revelation of John
        {
           20,  29,  22,  11,  14,  17,  17,  13,  21,  11,
           19,  18,  18,  20,   8,  21,  18,  24,  21,  15,
           27,  21,
        },
    };

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 6104112750913219370L;
}
