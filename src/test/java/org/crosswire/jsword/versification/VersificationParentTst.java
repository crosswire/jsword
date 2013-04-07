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
 * Copyright: 2005 - 2013
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.versification;

import java.util.Locale;

import junit.framework.TestCase;

import org.crosswire.jsword.book.CaseType;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.versification.system.SystemMT;
import org.crosswire.jsword.versification.system.Versifications;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class VersificationParentTst extends TestCase {
    private Versification v11n;
    private CaseType storedCase;

    public VersificationParentTst(String s, String v11nName) {
        super(s);
        this.v11n = Versifications.instance().getVersification(v11nName);
    }

    @Override
    protected void setUp() {
        storedCase = BookName.getDefaultCase();
    }

    @Override
    protected void tearDown() {
        BookName.setCase(storedCase);
    }

    public void testIn() throws Exception {
        // Counts using loops
        int verseCount = 0;

        // For all the books
        for (BibleBook b = v11n.getFirstBook(); b != null; b = v11n.getNextBook(b)) {
            // Continue the verse counts for the whole Bible
            for (int c = 0; c <= v11n.getLastChapter(b); c++) {
                verseCount += v11n.getLastVerse(b, c) + 1;
            }
        }

        assertEquals(verseCount, v11n.maximumOrdinal() + 1);
        assertEquals(verseCount, v11n.getCount(null));
    }

    public void testOrdinal() throws Exception {
        int ordinal = 0;
        for (BibleBook b = v11n.getFirstBook(); b != null; b = v11n.getNextBook(b)) {
            int cib = v11n.getLastChapter(b);
            for (int c = 0; c <= cib; c++) {
                int vic = v11n.getLastVerse(b, c);
                for (int v = 0; v <= vic; v++) {
                    Verse verse = new Verse(v11n, b, c, v);
                    assertEquals(verse.getOsisID(), ordinal++, v11n.getOrdinal(verse));
                }
            }
        }
    }

    public void testDecodeOrdinal() throws Exception {
        int ordinal = 0;
        for (BibleBook b = v11n.getFirstBook(); b != null; b = v11n.getNextBook(b)) {
            int cib = v11n.getLastChapter(b);
            for (int c = 0; c <= cib; c++) {
                int vic = v11n.getLastVerse(b, c);
                for (int v = 0; v <= vic; v++) {
                    Verse verse = new Verse(v11n, b, c, v);
                    assertEquals(verse.getOsisID(), verse, v11n.decodeOrdinal(ordinal++));
                }
            }
        }
    }

    public void testValidate() throws Exception {
        for (BibleBook b = v11n.getFirstBook(); b != null; b = v11n.getNextBook(b)) {
            // Test that negative chapters are invalid
            try {
                v11n.validate(b, -1, 0);
                fail();
            } catch (NoSuchVerseException ex) {
            }

            for (int c = 0; c <= v11n.getLastChapter(b); c++) {
                // Test that negative verse are invalid
                try {
                    v11n.validate(b, c, -1);
                    fail();
                } catch (NoSuchVerseException ex) {
                }

                // test that every verse, including introductions are valid
                for (int v = 0; v <= v11n.getLastVerse(b, c); v++) {
                    v11n.validate(b, c, v);
                }

                // test that every verses past the end are invalid
                try {
                    v11n.validate(b, c, v11n.getLastVerse(b, c) + 1);
                    fail();
                } catch (NoSuchVerseException ex) {
                }
            }

            // test that every chapters past the end are invalid
            try {
                v11n.validate(b, v11n.getLastChapter(b) + 1, 0);
                fail();
            } catch (NoSuchVerseException ex) {
            }
        }
    }

    public void testPatch() throws Exception {

        int all = 0;
        // For all the books
        for (BibleBook b = v11n.getFirstBook(); b != null; b = v11n.getNextBook(b)) {
            int cib = v11n.getLastChapter(b);
            for (int c = 0; c <= cib; c++) {
                int vic = v11n.getLastVerse(b, c);
                for (int v = 0; v <= vic; v++) {
                    Verse pv = v11n.patch(BibleBook.INTRO_BIBLE, 0, all);
                    assertEquals(pv.getName(), b, pv.getBook());
                    assertEquals(pv.getName(), c, pv.getChapter());
                    assertEquals(pv.getName(), v, pv.getVerse());
                    all++;
                }
            }
        }

        Verse gen11 = new Verse(v11n, BibleBook.GEN, 1, 1);
        assertEquals(gen11, v11n.patch(BibleBook.GEN, 1, 1));
        assertEquals(gen11, v11n.patch(BibleBook.GEN, 0, 2));
        assertEquals(gen11, v11n.patch(null, 3, 1));
        assertEquals(gen11, v11n.patch(null, 0, 4));
    }

    public void testVerseCount() throws Exception {
        int count_up = 0;
        Verse firstVerse = new Verse(v11n, v11n.getFirstBook(), 0, 0);
        BibleBook lastBook = v11n.getLastBook();
        int lastChapter = v11n.getLastChapter(lastBook);
        Verse lastVerse = new Verse(v11n, lastBook, lastChapter, v11n.getLastVerse(lastBook, lastChapter));
        for (BibleBook b = v11n.getFirstBook(); b != null; b = v11n.getNextBook(b)) {
            int cib = v11n.getLastChapter(b);
            for (int c = 0; c <= cib; c++) {
                int vic = v11n.getLastVerse(b, c);
                for (int v = 0; v <= vic; v++) {
                    Verse curVerse = new Verse(v11n, b, c, v);
                    int up = v11n.distance(firstVerse, curVerse) + 1;
                    assertEquals(++count_up, up);
//                    assertEquals(verseCountSlow(gen00, curVerse), up);
                }
            }

        }
        int count_down = v11n.maximumOrdinal();
        assertEquals(v11n.getOrdinal(lastVerse), count_down);
        for (BibleBook b = v11n.getFirstBook(); b != null; b = v11n.getNextBook(b)) {
            int cib = v11n.getLastChapter(b);
            for (int c = 0; c <= cib; c++) {
                int vic = v11n.getLastVerse(b, c);
                for (int v = 0; v <= vic; v++) {
                    Verse curVerse = new Verse(v11n, b, c, v);
                    int down = v11n.distance(curVerse, lastVerse);
                    assertEquals(count_down--, down);
                }
            }

        }
    }

    public void testNames() {
        // AV11N(DMS): Is this right?
        assertEquals(2, BibleBook.GEN.ordinal());
        assertEquals(68, BibleBook.REV.ordinal());
    }

    public void testMTSystem() {
        // The MT v11n is OT only and had a problem where this would have failed.
        // At this time all versifications have an OT and Gen 1:1
        Versification v11nMT = Versifications.instance().getVersification(SystemMT.V11N_NAME);
        Verse verse = new Verse(v11n, BibleBook.GEN, 1, 1);
        int index = verse.getOrdinal();
        Testament testament = v11nMT.getTestament(index);
        assertEquals("Gen 1:1 is old testament", Testament.OLD, testament);
        assertEquals("ordinals in OT do not change", index, v11n.getTestamentOrdinal(index));
    }

    public void testGetBook() {
        if (v11n.containsBook(BibleBook.GEN)) {
            assertEquals(BibleBook.GEN, v11n.getBook("Genesis"));
            assertEquals(BibleBook.GEN, v11n.getBook("Gene"));
            assertEquals(BibleBook.GEN, v11n.getBook("Gen"));
            assertEquals(BibleBook.GEN, v11n.getBook("GE"));
            assertEquals(BibleBook.GEN, v11n.getBook("ge"));
            assertEquals(BibleBook.GEN, v11n.getBook("GEN"));
            assertEquals(BibleBook.GEN, v11n.getBook("genesis"));
        }

        if (v11n.containsBook(BibleBook.PS)) {
            assertEquals(BibleBook.PS, v11n.getBook("psa"));
            assertEquals(BibleBook.PS, v11n.getBook("ps"));
            assertEquals(BibleBook.PS, v11n.getBook("pss"));
            assertEquals(BibleBook.PS, v11n.getBook("psalter"));
        }

        if (v11n.containsBook(BibleBook.ECCL)) {
            assertEquals(BibleBook.ECCL, v11n.getBook("ecc"));
            assertEquals(BibleBook.ECCL, v11n.getBook("Qohelot"));
        }

        if (v11n.containsBook(BibleBook.SONG)) {
            assertEquals(BibleBook.SONG, v11n.getBook("son"));
            assertEquals(BibleBook.SONG, v11n.getBook("song"));
            assertEquals(BibleBook.SONG, v11n.getBook("song of solomon"));
            assertEquals(BibleBook.SONG, v11n.getBook("songofsolomon"));
            assertEquals(BibleBook.SONG, v11n.getBook("ss"));
            assertEquals(BibleBook.SONG, v11n.getBook("canticle"));
            assertEquals(BibleBook.SONG, v11n.getBook("can"));
        }

        if (v11n.containsBook(BibleBook.PHIL)) {
            assertEquals(BibleBook.PHIL, v11n.getBook("phi"));
            assertEquals(BibleBook.PHIL, v11n.getBook("phil"));
            assertEquals(BibleBook.PHIL, v11n.getBook("phili"));
        }

        if (v11n.containsBook(BibleBook.PHLM)) {
            assertEquals(BibleBook.PHLM, v11n.getBook("phile"));
        }

        if (v11n.containsBook(BibleBook.REV)) {
            assertEquals(BibleBook.REV,  v11n.getBook("revelations"));
            assertEquals(BibleBook.REV,  v11n.getBook("rev"));
        }

        assertEquals(null, v11n.getBook("1"));
    }

    public void testLoadEnglish() {
        new BibleNames(v11n, Locale.ENGLISH);
    }

    public void testLoadAF() {
        new BibleNames(v11n, new Locale("af"));
    }

    public void testLoadEgyptianArabic() {
        new BibleNames(v11n, new Locale("ar", "EG"));
    }

    public void testLoadBG() {
        new BibleNames(v11n, new Locale("bg"));
    }

    public void testLoadCS() {
        new BibleNames(v11n, new Locale("cs"));
    }

    public void testLoadCY() {
        new BibleNames(v11n, new Locale("cy"));
    }

    public void testLoadDanish() {
        new BibleNames(v11n, new Locale("da"));
    }

    public void testLoadGerman() {
        new BibleNames(v11n, new Locale("de"));
    }

    public void testLoadSpanish() {
        new BibleNames(v11n, new Locale("es"));
    }

    public void testLoadET() {
        new BibleNames(v11n, new Locale("et"));
    }

    public void testLoadFarsi() {
        new BibleNames(v11n, new Locale("fa"));
    }

    public void testLoadFinnish() {
        new BibleNames(v11n, new Locale("fi"));
    }

    public void testLoadFO() {
        new BibleNames(v11n, new Locale("fo"));
    }

    public void testLoadFrench() {
        new BibleNames(v11n, new Locale("fr"));
    }

    public void testLoadHebrew() {
        new BibleNames(v11n, new Locale("he"));
    }

    public void testLoadHU() {
        new BibleNames(v11n, new Locale("hu"));
    }

    public void testLoadID() {
        new BibleNames(v11n, new Locale("id"));
    }

    public void testLoadIN() {
        new BibleNames(v11n, new Locale("in"));
    }

    public void testLoadItalian() {
        new BibleNames(v11n, new Locale("it"));
    }

    public void testLoadIW() {
        new BibleNames(v11n, new Locale("iw"));
    }

    public void testLoadKO() {
        new BibleNames(v11n, new Locale("ko"));
    }

    public void testLoadLA() {
        new BibleNames(v11n, new Locale("la"));
    }

    public void testLoadLT() {
        new BibleNames(v11n, new Locale("lt"));
    }

    public void testLoadNB() {
        new BibleNames(v11n, new Locale("nb"));
    }

    public void testLoadDutch() {
        new BibleNames(v11n, new Locale("nl"));
    }

    public void testLoadNN() {
        new BibleNames(v11n, new Locale("nn"));
    }

    public void testLoadPL() {
        new BibleNames(v11n, new Locale("pl"));
    }

    public void testLoadBrazillianPortuguese() {
        new BibleNames(v11n, new Locale("pt", "BR"));
    }

    public void testLoadPortuguese() {
        new BibleNames(v11n, new Locale("pt"));
    }

    public void testLoadRo() {
        new BibleNames(v11n, new Locale("ro"));
    }

    public void testLoadRU() {
        new BibleNames(v11n, new Locale("ru"));
    }

    public void testLoadSK() {
        new BibleNames(v11n, new Locale("sk"));
    }

    public void testLoadSL() {
        new BibleNames(v11n, new Locale("sl"));
    }

    public void testLoadSwedish() {
        new BibleNames(v11n, new Locale("sv"));
    }

    public void testLoadThai() {
        new BibleNames(v11n, new Locale("th"));
    }

    public void testLoadTR() {
        new BibleNames(v11n, new Locale("tr"));
    }

    public void testLoadUkranian() {
        new BibleNames(v11n, new Locale("uk"));
    }

    public void testLoadVietnamese() {
        new BibleNames(v11n, new Locale("vi"));
    }

    public void testLoadChineseTraditional() {
        new BibleNames(v11n, new Locale("zh", "CN"));
    }

    public void testLoadChineseSimplified() {
        new BibleNames(v11n, new Locale("zh"));
    }

    public void testLoadSwahili() {
        new BibleNames(v11n, new Locale("sw"));
    }

    public void testGetLongBookName() throws Exception {
        BookName.setCase(CaseType.SENTENCE);
        if (v11n.containsBook(BibleBook.GEN)) {
            assertEquals("Genesis", v11n.getLongName(BibleBook.GEN));
        }
        if (v11n.containsBook(BibleBook.REV)) {
            assertEquals("Revelation of John", v11n.getLongName(BibleBook.REV));
        }

        BookName.setCase(CaseType.LOWER);
        if (v11n.containsBook(BibleBook.GEN)) {
            assertEquals("genesis", v11n.getLongName(BibleBook.GEN));
        }
        if (v11n.containsBook(BibleBook.REV)) {
            assertEquals("revelation of john", v11n.getLongName(BibleBook.REV));
        }

        BookName.setCase(CaseType.UPPER);
        if (v11n.containsBook(BibleBook.GEN)) {
            assertEquals("GENESIS", v11n.getLongName(BibleBook.GEN));
        }
        if (v11n.containsBook(BibleBook.REV)) {
            assertEquals("REVELATION OF JOHN", v11n.getLongName(BibleBook.REV));
        }
    }

    public void testGetShortBookName() throws Exception {
        BookName.setCase(CaseType.SENTENCE);
        if (v11n.containsBook(BibleBook.GEN)) {
            assertEquals("Gen", v11n.getShortName(BibleBook.GEN));
        }
        if (v11n.containsBook(BibleBook.EXOD)) {
            assertEquals("Exo", v11n.getShortName(BibleBook.EXOD));
        }
        if (v11n.containsBook(BibleBook.JUDG)) {
            assertEquals("Judg", v11n.getShortName(BibleBook.JUDG));
        }
        if (v11n.containsBook(BibleBook.MAL)) {
            assertEquals("Mal", v11n.getShortName(BibleBook.MAL));
        }
        if (v11n.containsBook(BibleBook.MATT)) {
            assertEquals("Mat", v11n.getShortName(BibleBook.MATT));
        }
        if (v11n.containsBook(BibleBook.PHIL)) {
            assertEquals("Phili", v11n.getShortName(BibleBook.PHIL));
        }
        if (v11n.containsBook(BibleBook.PHLM)) {
            assertEquals("Phile", v11n.getShortName(BibleBook.PHLM));
        }
        if (v11n.containsBook(BibleBook.JUDE)) {
            assertEquals("Jude", v11n.getShortName(BibleBook.JUDE));
        }
        if (v11n.containsBook(BibleBook.REV)) {
            assertEquals("Rev", v11n.getShortName(BibleBook.REV));
        }

        BookName.setCase(CaseType.LOWER);
        if (v11n.containsBook(BibleBook.GEN)) {
            assertEquals("gen", v11n.getShortName(BibleBook.GEN));
        }
        if (v11n.containsBook(BibleBook.EXOD)) {
            assertEquals("exo", v11n.getShortName(BibleBook.EXOD));
        }
        if (v11n.containsBook(BibleBook.JUDG)) {
            assertEquals("judg", v11n.getShortName(BibleBook.JUDG));
        }
        if (v11n.containsBook(BibleBook.MAL)) {
            assertEquals("mal", v11n.getShortName(BibleBook.MAL));
        }
        if (v11n.containsBook(BibleBook.MATT)) {
            assertEquals("mat", v11n.getShortName(BibleBook.MATT));
        }
        if (v11n.containsBook(BibleBook.PHIL)) {
            assertEquals("phili", v11n.getShortName(BibleBook.PHIL));
        }
        if (v11n.containsBook(BibleBook.PHLM)) {
            assertEquals("phile", v11n.getShortName(BibleBook.PHLM));
        }
        if (v11n.containsBook(BibleBook.JUDE)) {
            assertEquals("jude", v11n.getShortName(BibleBook.JUDE));
        }
        if (v11n.containsBook(BibleBook.REV)) {
            assertEquals("rev", v11n.getShortName(BibleBook.REV));
        }

        BookName.setCase(CaseType.UPPER);
        if (v11n.containsBook(BibleBook.GEN)) {
            assertEquals("GEN", v11n.getShortName(BibleBook.GEN));
        }
        if (v11n.containsBook(BibleBook.EXOD)) {
            assertEquals("EXO", v11n.getShortName(BibleBook.EXOD));
        }
        if (v11n.containsBook(BibleBook.JUDG)) {
            assertEquals("JUDG", v11n.getShortName(BibleBook.JUDG));
        }
        if (v11n.containsBook(BibleBook.MAL)) {
            assertEquals("MAL", v11n.getShortName(BibleBook.MAL));
        }
        if (v11n.containsBook(BibleBook.MATT)) {
            assertEquals("MAT", v11n.getShortName(BibleBook.MATT));
        }
        if (v11n.containsBook(BibleBook.PHIL)) {
            assertEquals("PHILI", v11n.getShortName(BibleBook.PHIL));
        }
        if (v11n.containsBook(BibleBook.PHLM)) {
            assertEquals("PHILE", v11n.getShortName(BibleBook.PHLM));
        }
        if (v11n.containsBook(BibleBook.JUDE)) {
            assertEquals("JUDE", v11n.getShortName(BibleBook.JUDE));
        }
        if (v11n.containsBook(BibleBook.REV)) {
            assertEquals("REV", v11n.getShortName(BibleBook.REV));
        }
    }

}
