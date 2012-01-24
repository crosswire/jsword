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
 * Copyright: 2005 - 2012
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.versification;

import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.book.CaseType;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.versification.system.ReferenceSystems;

/**
 * BibleInfo is a static class that deals with Bible book names, and conversion to and from
 * ordinal number and Verse.
 * <p>This class is likely to be reworked in it's entirety. It is really only true
 * of the KJV Bible. It is not true of other versifications such as Luther's.
 * </p>
 *
 * @deprecated Use ReferenceSystems.getReferenceSystem("KJV") instead.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public final class BibleInfo {
    /**
     * constant for the old testament
     */
    public static final int TESTAMENT_OLD = 0;

    /**
     * constant for the new testament
     */
    public static final int TESTAMENT_NEW = 1;

    /**
     * Ensure that we can not be instantiated
     */
    private BibleInfo() {
    }

    /**
     * Get the immediately following book in the current versification.
     * @param book
     * @return the next book or null if no following book
     */
    public static BibleBook getNextBook(BibleBook book) {
        return referenceSystem.getBooks().getNextBook(book);
    }

    /**
     * Get the immediately prior book in the current versification.
     * @param book
     * @return the previous book or null if no previous book
     */
    public static BibleBook getPreviousBook(BibleBook book) {
        return referenceSystem.getBooks().getPreviousBook(book);
    }

    /**
     * Get the ordered array of books belonging to this versification.
     * This includes the 3 introductions.
     *
     * @return the array of books
     */
    public static BibleBook[] getBooks() {
        return BibleBook.getBooks();
    }

    /**
     * Get the last valid chapter number for a book.
     *
     * @param book
     *            The book part of the reference.
     * @return The last valid chapter number for a book.
     */
    public static int chaptersInBook(BibleBook book) {
        return referenceSystem.getLastChapter(book);
    }

    /**
     * Get the last valid verse number for a chapter.
     *
     * @param book
     *            The book part of the reference.
     * @param chapter
     *            The current chapter
     * @return The last valid verse number for a chapter
     *                If the book or chapter number is not valid
     */
    public static int versesInChapter(BibleBook book, int chapter) {
        return referenceSystem.getLastVerse(book, chapter);
    }

    /**
     * The maximum number of verses in the Bible, including module, testament, book and chapter introductions.
     *
     * @return the number of addressable verses in this versification.
     */
    public static int maximumOrdinal() {
        return referenceSystem.maximumOrdinal();
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
     */
    public static int getOrdinal(Verse verse) {
        return referenceSystem.getOrdinal(verse);
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
     */
    public static int getTestamentOrdinal(int ordinal) {
        return referenceSystem.getTestamentOrdinal(ordinal);
    }

    /**
     * Get the testament of a given verse
     */
    public static Testament getTestament(int ordinal) {
        return referenceSystem.getTestament(ordinal);
    }

    /**
     * Give the count of verses in the testament or the whole Bible.
     *
     * @param testament The testament to count. If null, then all testaments.
     * @return the number of verses in the testament
     */
    public static int getCount(Testament testament) {
        return referenceSystem.getCount(testament);
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
    public static Verse decodeOrdinal(int ordinal) {
        return referenceSystem.decodeOrdinal(ordinal);
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
    public static void validate(BibleBook book, int chapter, int verse) throws NoSuchVerseException {
        referenceSystem.validate(book, chapter, verse);
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
    public static Verse patch(BibleBook book, int chapter, int verse) {
        return referenceSystem.patch(book, chapter, verse);
    }

    /**
     * Count the books in the Bible.
     *
     * @return The number of books in the Bible, including the three introductions
     */
    public static int booksInBible() {
        return referenceSystem.getBooks().getBookCount();
    }

    /** The OSIS name of the reference system. */
    public static String osisName = "KJV";
    public static ReferenceSystem referenceSystem = ReferenceSystems.instance().getReferenceSystem(osisName);

    /**
     * Constant for the ordinal number of the first verse in each chapter.
     */
    public static final int[][] kjvChapterStarts =
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
    };

    /**
     * Get the BookName.
     * This is merely a convenience function that validates that book is not null,
     * throwing NoSuchVerseException if it is.
     *
     * @param book
     *            The book of the Bible
     * @return The requested BookName
     * @exception NoSuchVerseException
     *                If the book is not valid
     * @deprecated Use <code>book.getBookName()</code> instead.
     */
    @Deprecated
    public static BookName getBookName(BibleBook book) throws NoSuchVerseException {
        try {
            return book.getBookName();
        } catch (NullPointerException ex) {
            throw new NoSuchVerseException(JSOtherMsg.lookupText("Book must not be null"));
        }
    }

    /**
     * Get number of a book from its name.
     *
     * @param find
     *            The string to identify
     * @return The BibleBook, On error null
     * @deprecated use {@link #BibleBook.getBook(String)}
     */
    @Deprecated
    public static BibleBook getBook(String find) {
        return BibleBook.getBook(find);
    }

    /**
     * Is the given string a valid book name. If this method returns true then
     * getBook() will return a BibleBook and not throw an exception.
     *
     * @param find
     *            The string to identify
     * @return true when the book name is recognized
     * @deprecated use {@link #BibleBook.isBook(String)}
     */
    @Deprecated
    public static boolean isBookName(String find) {
        return BibleBook.isBook(find);
    }

    /**
     * Count the chapters in the Bible.
     *
     * @return 1189 always - the number of chapters in the Bible
     * @deprecated do not use
     */
    @Deprecated
    public static int chaptersInBible() {
        return 1189;
    }

    /**
     * The maximum number of verses in the Bible, including module, testament, book and chapter introductions.
     * Note: it used to exclude introductions.
     *
     * @return the number of addressable verses in this versification.
     * @deprecated use {@link #BibleInfo.maximumOrdinal()}
     */
    @Deprecated
    public static int versesInBible() {
        return maximumOrdinal();
    }

    /**
     * Get the preferred name of a book. Altered by the case setting (see
     * setBookCase() and isFullBookName())
     * This is merely a convenience function that validates that book is not null,
     * throwing NoSuchVerseException if it is.
     *
     * @param book
     *            The book of the Bible
     * @return The full name of the book
     * @exception NoSuchVerseException
     *                If the book is not valid
     * @deprecated Use <code>book.getPreferredName()</code> instead.
     */
    @Deprecated
    public static String getPreferredBookName(BibleBook book) throws NoSuchVerseException {
        try {
            return book.getPreferredName();
        } catch (NullPointerException ex) {
            throw new NoSuchVerseException(JSOtherMsg.lookupText("Book must not be null"));
        }
    }

    /**
     * Get the full name of a book (e.g. "Genesis"). Altered by the case setting
     * (see setBookCase())
     * This is merely a convenience function that validates that book is not null,
     * throwing NoSuchVerseException if it is.
     *
     * @param book
     *            The book of the Bible
     * @return The full name of the book
     * @exception NoSuchVerseException
     *                If the book is not valid
     * @deprecated Use <code>book.getLongName()</code> instead.
     */
    @Deprecated
    public static String getLongBookName(BibleBook book) throws NoSuchVerseException {
        try {
            return book.getLongName();
        } catch (NullPointerException ex) {
            throw new NoSuchVerseException(JSOtherMsg.lookupText("Book must not be null"));
        }
    }

    /**
     * Get the short name of a book (e.g. "Gen"). Altered by the case setting
     * (see setBookCase())
     * This is merely a convenience function that validates that book is not null,
     * throwing NoSuchVerseException if it is.
     *
     * @param book
     *            The book of the Bible
     * @return The short name of the book
     * @exception NoSuchVerseException
     *                If the book is not valid
     * @deprecated Use <code>book.getShortName()</code> instead.
     */
    @Deprecated
    public static String getShortBookName(BibleBook book) throws NoSuchVerseException {
        try {
            return book.getShortName();
        } catch (NullPointerException ex) {
            throw new NoSuchVerseException(JSOtherMsg.lookupText("Book must not be null"));
        }
    }

    /**
     * Get the OSIS name for a book.
     * This is merely a convenience function that validates that book is not null,
     * throwing NoSuchVerseException if it is.
     *
     * @param book
     *            The book of the Bible
     * @return the OSIS defined short name for a book
     * @exception NoSuchVerseException
     *                If the book is not valid
     * @deprecated Use <code>book.getOSIS()</code> instead.
     */
    @Deprecated
    public static String getOSISName(BibleBook book) throws NoSuchVerseException {
        try {
            return book.getOSIS();
        } catch (NullPointerException ex) {
            throw new NoSuchVerseException(JSOtherMsg.lookupText("Book must not be null"));
        }
    }

    /**
     * How many verses between verse1 and verse2 (inclusive).
     *
     * @param verse1
     *            The earlier verse.
     * @param verse2
     *            The later verse.
     * @return the number of verses
     * @exception NoSuchVerseException
     *                If either reference is illegal
     * @deprecated use <code>verse2.subtract(verse1) + 1</code> instead
     */
    @Deprecated
    public static int verseCount(Verse verse1, Verse verse2) throws NoSuchVerseException {
        return verse2.subtract(verse1) + 1;
    }

    /**
     * This is only used by config.
     *
     * @param bookCase
     *            The new case to use for reporting book names
     * @exception IllegalArgumentException
     *                If the case is not between 0 and 2
     * @see #getCase()
     * @deprecated use {@link #BookName.setCase(int)}
     */
    @Deprecated
    public static void setCase(int bookCase) {
        BookName.setCase(bookCase);
    }

    /**
     * This is only used by config
     *
     * @return The current case setting
     * @see #setCase(CaseType)
     * @deprecated use {@link #BookName.getCase()}
     */
    @Deprecated
    public static int getCase() {
        return BookName.getCase();
    }

    /**
     * How do we report the names of the books?. These are static. This is on
     * the assumption that we will not want to have different sections of the
     * app using a different format. I expect this to be a good assumption, and
     * it saves passing a Book class around everywhere. CaseType.MIXED is not
     * allowed
     *
     * @param newBookCase
     *            The new case to use for reporting book names
     * @exception IllegalArgumentException
     *                If the case is not between 0 and 2
     * @see #getCase()
     * @deprecated use {@link #BookName.setCase(CaseType)}
     */
    @Deprecated
    public static void setCase(CaseType newBookCase) {
        BookName.setCase(newBookCase);
    }

    /**
     * This is only used by config
     *
     * @return Whether the name is long or short. Default is Full (true).
     * @see #setFullBookName(boolean)
     * @deprecated use {@link #BookName.isFullBookName()}
     */
    @Deprecated
    public static boolean isFullBookName() {
        return BookName.isFullBookName();
    }

    /**
     * Set whether the name should be full or abbreviated, long or short.
     *
     * @param fullName
     *            The new case to use for reporting book names
     * @see #isFullBookName()
     * @deprecated use {@link #BookName.setFullBookName(boolean)}
     */
    @Deprecated
    public static void setFullBookName(boolean fullName) {
        BookName.setFullBookName(fullName);
    }

    /**
     * How do we report the names of the books?.
     *
     * @return The current case setting
     * @see #setCase(int)
     * @deprecated use {@link #BookName.getDefaultCase()}
     */
    @Deprecated
    public static CaseType getDefaultCase() {
        return BookName.getDefaultCase();
    }

}
