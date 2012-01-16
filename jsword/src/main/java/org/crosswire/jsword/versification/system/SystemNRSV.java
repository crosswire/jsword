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
 * ID: $Id$
 */
package org.crosswire.jsword.versification.system;

import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.ReferenceSystem;

/**
 * The NRSV Versification is nearly the same as the KJV versification.
 * It differs in that 3 John has 15 verses not 14 and Revelation 12
 * has 18 verses not 17.
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class SystemNRSV extends ReferenceSystem {
    public static String name = "NRSV";
    public static BibleBook[] booksNT = SystemDefault.booksNT;

    public static int[][] lastVerseNT =
    {
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
                0,    15,
        },
        // Jude
        {
                0,    25,
        },
        // Rev
        {
                0,    20,    29,    22,    11,    14,    17,    17,    13,    21,
               11,    19,    18,    18,    20,     8,    21,    18,    24,    21,
               15,    27,    21,
        },
    };


    public static BibleBook[] booksOT = SystemDefault.booksOT;
    public static int[][] lastVerseOT = SystemKJV.lastVerseOT;
}
