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
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2012
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.versification.system;

import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.Versification;

/**
 * The SystemDefault versification (v11n) is that of the Protestant KJV.
 * This is the first v11n defined within JSword and SWORD.
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith
 */
public class SystemDefault extends Versification {
    public static final String V11N_NAME = "";

    // Default Books for other Testament in single Testament Bibles
    /* protected */ static final BibleBook[] BOOKS_NONE =
    {
    };

    // Default offsets for other Testament in single Testament Bibles
    /* protected */ static final int[][] LAST_VERSE_NONE =
    {
    };

    // Default NT list is the most common
    /* protected */ static final BibleBook[] BOOKS_NT =
    {
        BibleBook.MATT,
        BibleBook.MARK,
        BibleBook.LUKE,
        BibleBook.JOHN,
        BibleBook.ACTS,
        BibleBook.ROM,
        BibleBook.COR1,
        BibleBook.COR2,
        BibleBook.GAL,
        BibleBook.EPH,
        BibleBook.PHIL,
        BibleBook.COL,
        BibleBook.THESS1,
        BibleBook.THESS2,
        BibleBook.TIM1,
        BibleBook.TIM2,
        BibleBook.TITUS,
        BibleBook.PHLM,
        BibleBook.HEB,
        BibleBook.JAS,
        BibleBook.PET1,
        BibleBook.PET2,
        BibleBook.JOHN1,
        BibleBook.JOHN2,
        BibleBook.JOHN3,
        BibleBook.JUDE,
        BibleBook.REV,
    };

    /* protected */ static final BibleBook[] BOOKS_OTA =
            {
                    BibleBook.GEN,
                    BibleBook.EXOD,
                    BibleBook.LEV,
                    BibleBook.NUM,
                    BibleBook.DEUT,
                    BibleBook.JOSH,
                    BibleBook.JUDG,
                    BibleBook.RUTH,
                    BibleBook.SAM1,
                    BibleBook.SAM2,
                    BibleBook.KGS1,
                    BibleBook.KGS2,
                    BibleBook.CHR1,
                    BibleBook.CHR2,
                    BibleBook.EZRA,
                    BibleBook.NEH,
                    BibleBook.ESTH,
                    BibleBook.JOB,
                    BibleBook.PS,
                    BibleBook.PROV,
                    BibleBook.ECCL,
                    BibleBook.SONG,
                    BibleBook.ISA,
                    BibleBook.JER,
                    BibleBook.LAM,
                    BibleBook.EZEK,
                    BibleBook.DAN,
                    BibleBook.HOS,
                    BibleBook.JOEL,
                    BibleBook.AMOS,
                    BibleBook.OBAD,
                    BibleBook.JONAH,
                    BibleBook.MIC,
                    BibleBook.NAH,
                    BibleBook.HAB,
                    BibleBook.ZEPH,
                    BibleBook.HAG,
                    BibleBook.ZECH,
                    BibleBook.MAL,
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
                    BibleBook.ESTH_GR,
                    BibleBook.PSALM_SOL,
                    BibleBook.MACC3,
                    BibleBook.MACC4,
                    BibleBook.ADD_PS,
                    BibleBook.EN1,
                    BibleBook.ODES,
                    BibleBook.MEQ1,
                    BibleBook.MEQ2,
                    BibleBook.MEQ3,
                    BibleBook.T12PATR,
                    BibleBook.JUBS,
                    BibleBook.BAR2,
                    BibleBook.BAR3,
                    BibleBook.BAR4,
                    BibleBook.EP_LAO,
            };

    /* protected */ static final BibleBook[] BOOKS_OT =
    {
        BibleBook.GEN,
        BibleBook.EXOD,
        BibleBook.LEV,
        BibleBook.NUM,
        BibleBook.DEUT,
        BibleBook.JOSH,
        BibleBook.JUDG,
        BibleBook.RUTH,
        BibleBook.SAM1,
        BibleBook.SAM2,
        BibleBook.KGS1,
        BibleBook.KGS2,
        BibleBook.CHR1,
        BibleBook.CHR2,
        BibleBook.EZRA,
        BibleBook.NEH,
        BibleBook.ESTH,
        BibleBook.JOB,
        BibleBook.PS,
        BibleBook.PROV,
        BibleBook.ECCL,
        BibleBook.SONG,
        BibleBook.ISA,
        BibleBook.JER,
        BibleBook.LAM,
        BibleBook.EZEK,
        BibleBook.DAN,
        BibleBook.HOS,
        BibleBook.JOEL,
        BibleBook.AMOS,
        BibleBook.OBAD,
        BibleBook.JONAH,
        BibleBook.MIC,
        BibleBook.NAH,
        BibleBook.HAB,
        BibleBook.ZEPH,
        BibleBook.HAG,
        BibleBook.ZECH,
        BibleBook.MAL,
    };


    /**
     * Serialization ID
     */
    private static final long serialVersionUID = -921273257871599555L;
}
