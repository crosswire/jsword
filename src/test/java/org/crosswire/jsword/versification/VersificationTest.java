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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.crosswire.jsword.book.CaseType;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.PassageKeyFactory;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.versification.system.SystemCalvin;
import org.crosswire.jsword.versification.system.SystemCatholic;
import org.crosswire.jsword.versification.system.SystemCatholic2;
import org.crosswire.jsword.versification.system.SystemDarbyFR;
import org.crosswire.jsword.versification.system.SystemGerman;
import org.crosswire.jsword.versification.system.SystemKJV;
import org.crosswire.jsword.versification.system.SystemKJVA;
import org.crosswire.jsword.versification.system.SystemLeningrad;
import org.crosswire.jsword.versification.system.SystemLuther;
import org.crosswire.jsword.versification.system.SystemMT;
import org.crosswire.jsword.versification.system.SystemNRSV;
import org.crosswire.jsword.versification.system.SystemNRSVA;
import org.crosswire.jsword.versification.system.SystemSegond;
import org.crosswire.jsword.versification.system.SystemSynodal;
import org.crosswire.jsword.versification.system.SystemSynodalProt;
import org.crosswire.jsword.versification.system.SystemVulg;
import org.crosswire.jsword.versification.system.Versifications;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
@RunWith(Parameterized.class)
public class VersificationTest {
    private Versification v11n;
    private CaseType storedCase;

    public VersificationTest(String v11nName) {
        this.v11n = Versifications.instance().getVersification(v11nName);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][]{
                {SystemCalvin.V11N_NAME},
                {SystemCatholic.V11N_NAME},
                {SystemCatholic2.V11N_NAME},
                {SystemDarbyFR.V11N_NAME},
                {SystemGerman.V11N_NAME},
                {SystemKJV.V11N_NAME},
                {SystemKJVA.V11N_NAME},
                {SystemLeningrad.V11N_NAME},
                {SystemLuther.V11N_NAME},
                {SystemMT.V11N_NAME},
                {SystemNRSV.V11N_NAME},
                {SystemNRSVA.V11N_NAME},
                {SystemSegond.V11N_NAME},
                {SystemSynodal.V11N_NAME},
                {SystemSynodalProt.V11N_NAME},
                {SystemVulg.V11N_NAME}
        };
        return Arrays.asList(data);
    }

    @Before
    public void setUp() {
        storedCase = BookName.getDefaultCase();
    }

    @After
    public void tearDown() {
        BookName.setCase(storedCase);
    }

    @Test
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

        Assert.assertEquals(verseCount, v11n.maximumOrdinal() + 1);
        Assert.assertEquals(verseCount, v11n.getCount(null));
    }

    @Test
    public void testOrdinal() throws Exception {
        int ordinal = 0;
        for (BibleBook b = v11n.getFirstBook(); b != null; b = v11n.getNextBook(b)) {
            int cib = v11n.getLastChapter(b);
            for (int c = 0; c <= cib; c++) {
                int vic = v11n.getLastVerse(b, c);
                for (int v = 0; v <= vic; v++) {
                    Verse verse = new Verse(v11n, b, c, v);
                    Assert.assertEquals(verse.getOsisID(), ordinal++, v11n.getOrdinal(verse));
                }
            }
        }
    }

    @Test
    public void testDecodeOrdinal() throws Exception {
        int ordinal = 0;
        for (BibleBook b = v11n.getFirstBook(); b != null; b = v11n.getNextBook(b)) {
            int cib = v11n.getLastChapter(b);
            for (int c = 0; c <= cib; c++) {
                int vic = v11n.getLastVerse(b, c);
                for (int v = 0; v <= vic; v++) {
                    Verse verse = new Verse(v11n, b, c, v);
                    Assert.assertEquals(verse.getOsisID(), verse, v11n.decodeOrdinal(ordinal++));
                }
            }
        }
    }

    @Test
    public void testValidate() throws Exception {
        for (BibleBook b = v11n.getFirstBook(); b != null; b = v11n.getNextBook(b)) {
            // Test that negative chapters are invalid
            try {
                v11n.validate(b, -1, 0);
                Assert.fail();
            } catch (NoSuchVerseException ex) {
                // This is allowed
            }

            for (int c = 0; c <= v11n.getLastChapter(b); c++) {
                // Test that negative verse are invalid
                try {
                    v11n.validate(b, c, -1);
                    Assert.fail();
                } catch (NoSuchVerseException ex) {
                    // This is allowed
                }

                // test that every verse, including introductions are valid
                for (int v = 0; v <= v11n.getLastVerse(b, c); v++) {
                    v11n.validate(b, c, v);
                }

                // test that every verses past the end are invalid
                try {
                    v11n.validate(b, c, v11n.getLastVerse(b, c) + 1);
                    Assert.fail();
                } catch (NoSuchVerseException ex) {
                    // This is allowed
                }
            }

            // test that every chapters past the end are invalid
            try {
                v11n.validate(b, v11n.getLastChapter(b) + 1, 0);
                Assert.fail();
            } catch (NoSuchVerseException ex) {
                // This is allowed
            }
        }
    }

    @Test
    public void testPatch() throws Exception {

        int all = 0;
        // For all the books
        for (BibleBook b = v11n.getFirstBook(); b != null; b = v11n.getNextBook(b)) {
            int cib = v11n.getLastChapter(b);
            for (int c = 0; c <= cib; c++) {
                int vic = v11n.getLastVerse(b, c);
                for (int v = 0; v <= vic; v++) {
                    Verse pv = v11n.patch(BibleBook.INTRO_BIBLE, 0, all);
                    Assert.assertEquals(pv.getName(), b, pv.getBook());
                    Assert.assertEquals(pv.getName(), c, pv.getChapter());
                    Assert.assertEquals(pv.getName(), v, pv.getVerse());
                    all++;
                }
            }
        }

        Verse gen11 = new Verse(v11n, BibleBook.GEN, 1, 1);
        Assert.assertEquals(gen11, v11n.patch(BibleBook.GEN, 1, 1));
        Assert.assertEquals(gen11, v11n.patch(BibleBook.GEN, 0, 2));
        Assert.assertEquals(gen11, v11n.patch(null, 3, 1));
        Assert.assertEquals(gen11, v11n.patch(null, 0, 4));
    }

    @Test
    public void testVerseCount() throws Exception {
        int countUp = 0;
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
                    Assert.assertEquals(++countUp, up);
//                    Assert.assertEquals(verseCountSlow(gen00, curVerse), up);
                }
            }

        }
        int countDown = v11n.maximumOrdinal();
        Assert.assertEquals(v11n.getOrdinal(lastVerse), countDown);
        for (BibleBook b = v11n.getFirstBook(); b != null; b = v11n.getNextBook(b)) {
            int cib = v11n.getLastChapter(b);
            for (int c = 0; c <= cib; c++) {
                int vic = v11n.getLastVerse(b, c);
                for (int v = 0; v <= vic; v++) {
                    Verse curVerse = new Verse(v11n, b, c, v);
                    int down = v11n.distance(curVerse, lastVerse);
                    Assert.assertEquals(countDown--, down);
                }
            }

        }
    }

    @Test
    public void testNames() {
        // AV11N(DMS): Is this right?
        Assert.assertEquals(2, BibleBook.GEN.ordinal());
        Assert.assertEquals(68, BibleBook.REV.ordinal());
    }

    @Test
    public void testMTSystem() {
        // The MT v11n is OT only and had a problem where this would have failed.
        // At this time all versifications have an OT and Gen 1:1
        Versification v11nMT = Versifications.instance().getVersification(SystemMT.V11N_NAME);
        Verse verse = new Verse(v11n, BibleBook.GEN, 1, 1);
        int index = verse.getOrdinal();
        Testament testament = v11nMT.getTestament(index);
        Assert.assertEquals("Gen 1:1 is old testament", Testament.OLD, testament);
        Assert.assertEquals("ordinals in OT do not change", index, v11n.getTestamentOrdinal(index));
    }

    @Test
    public void testGetBook() {
        if (v11n.containsBook(BibleBook.GEN)) {
            Assert.assertEquals(BibleBook.GEN, v11n.getBook("Genesis"));
            Assert.assertEquals(BibleBook.GEN, v11n.getBook("Gene"));
            Assert.assertEquals(BibleBook.GEN, v11n.getBook("Gen"));
            Assert.assertEquals(BibleBook.GEN, v11n.getBook("GE"));
            Assert.assertEquals(BibleBook.GEN, v11n.getBook("ge"));
            Assert.assertEquals(BibleBook.GEN, v11n.getBook("GEN"));
            Assert.assertEquals(BibleBook.GEN, v11n.getBook("genesis"));
        }

        if (v11n.containsBook(BibleBook.PS)) {
            Assert.assertEquals(BibleBook.PS, v11n.getBook("psa"));
            Assert.assertEquals(BibleBook.PS, v11n.getBook("ps"));
            Assert.assertEquals(BibleBook.PS, v11n.getBook("pss"));
            Assert.assertEquals(BibleBook.PS, v11n.getBook("psalter"));
        }

        if (v11n.containsBook(BibleBook.ECCL)) {
            Assert.assertEquals(BibleBook.ECCL, v11n.getBook("ecc"));
            Assert.assertEquals(BibleBook.ECCL, v11n.getBook("Qohelot"));
        }

        if (v11n.containsBook(BibleBook.SONG)) {
            Assert.assertEquals(BibleBook.SONG, v11n.getBook("son"));
            Assert.assertEquals(BibleBook.SONG, v11n.getBook("song"));
            Assert.assertEquals(BibleBook.SONG, v11n.getBook("song of solomon"));
            Assert.assertEquals(BibleBook.SONG, v11n.getBook("songofsolomon"));
            Assert.assertEquals(BibleBook.SONG, v11n.getBook("ss"));
            Assert.assertEquals(BibleBook.SONG, v11n.getBook("canticle"));
            Assert.assertEquals(BibleBook.SONG, v11n.getBook("can"));
        }

        if (v11n.containsBook(BibleBook.PHIL)) {
            Assert.assertEquals(BibleBook.PHIL, v11n.getBook("phi"));
            Assert.assertEquals(BibleBook.PHIL, v11n.getBook("phil"));
            Assert.assertEquals(BibleBook.PHIL, v11n.getBook("phili"));
        }

        if (v11n.containsBook(BibleBook.PHLM)) {
            Assert.assertEquals(BibleBook.PHLM, v11n.getBook("phile"));
        }

        if (v11n.containsBook(BibleBook.REV)) {
            Assert.assertEquals(BibleBook.REV,  v11n.getBook("revelations"));
            Assert.assertEquals(BibleBook.REV,  v11n.getBook("rev"));
        }

        Assert.assertEquals(null, v11n.getBook("1"));
    }

    @Test
    public void testGetLongBookName() throws Exception {
        BookName.setCase(CaseType.SENTENCE);
        if (v11n.containsBook(BibleBook.GEN)) {
            Assert.assertEquals("Genesis", v11n.getLongName(BibleBook.GEN));
        }
        if (v11n.containsBook(BibleBook.REV)) {
            Assert.assertEquals("Revelation of John", v11n.getLongName(BibleBook.REV));
        }

        BookName.setCase(CaseType.LOWER);
        if (v11n.containsBook(BibleBook.GEN)) {
            Assert.assertEquals("genesis", v11n.getLongName(BibleBook.GEN));
        }
        if (v11n.containsBook(BibleBook.REV)) {
            Assert.assertEquals("revelation of john", v11n.getLongName(BibleBook.REV));
        }

        BookName.setCase(CaseType.UPPER);
        if (v11n.containsBook(BibleBook.GEN)) {
            Assert.assertEquals("GENESIS", v11n.getLongName(BibleBook.GEN));
        }
        if (v11n.containsBook(BibleBook.REV)) {
            Assert.assertEquals("REVELATION OF JOHN", v11n.getLongName(BibleBook.REV));
        }
    }

    @Test
    public void testGetShortBookName() throws Exception {
        BookName.setCase(CaseType.SENTENCE);
        if (v11n.containsBook(BibleBook.GEN)) {
            Assert.assertEquals("Gen", v11n.getShortName(BibleBook.GEN));
        }
        if (v11n.containsBook(BibleBook.EXOD)) {
            Assert.assertEquals("Exo", v11n.getShortName(BibleBook.EXOD));
        }
        if (v11n.containsBook(BibleBook.JUDG)) {
            Assert.assertEquals("Judg", v11n.getShortName(BibleBook.JUDG));
        }
        if (v11n.containsBook(BibleBook.MAL)) {
            Assert.assertEquals("Mal", v11n.getShortName(BibleBook.MAL));
        }
        if (v11n.containsBook(BibleBook.MATT)) {
            Assert.assertEquals("Mat", v11n.getShortName(BibleBook.MATT));
        }
        if (v11n.containsBook(BibleBook.PHIL)) {
            Assert.assertEquals("Phili", v11n.getShortName(BibleBook.PHIL));
        }
        if (v11n.containsBook(BibleBook.PHLM)) {
            Assert.assertEquals("Phile", v11n.getShortName(BibleBook.PHLM));
        }
        if (v11n.containsBook(BibleBook.JUDE)) {
            Assert.assertEquals("Jude", v11n.getShortName(BibleBook.JUDE));
        }
        if (v11n.containsBook(BibleBook.REV)) {
            Assert.assertEquals("Rev", v11n.getShortName(BibleBook.REV));
        }

        BookName.setCase(CaseType.LOWER);
        if (v11n.containsBook(BibleBook.GEN)) {
            Assert.assertEquals("gen", v11n.getShortName(BibleBook.GEN));
        }
        if (v11n.containsBook(BibleBook.EXOD)) {
            Assert.assertEquals("exo", v11n.getShortName(BibleBook.EXOD));
        }
        if (v11n.containsBook(BibleBook.JUDG)) {
            Assert.assertEquals("judg", v11n.getShortName(BibleBook.JUDG));
        }
        if (v11n.containsBook(BibleBook.MAL)) {
            Assert.assertEquals("mal", v11n.getShortName(BibleBook.MAL));
        }
        if (v11n.containsBook(BibleBook.MATT)) {
            Assert.assertEquals("mat", v11n.getShortName(BibleBook.MATT));
        }
        if (v11n.containsBook(BibleBook.PHIL)) {
            Assert.assertEquals("phili", v11n.getShortName(BibleBook.PHIL));
        }
        if (v11n.containsBook(BibleBook.PHLM)) {
            Assert.assertEquals("phile", v11n.getShortName(BibleBook.PHLM));
        }
        if (v11n.containsBook(BibleBook.JUDE)) {
            Assert.assertEquals("jude", v11n.getShortName(BibleBook.JUDE));
        }
        if (v11n.containsBook(BibleBook.REV)) {
            Assert.assertEquals("rev", v11n.getShortName(BibleBook.REV));
        }

        BookName.setCase(CaseType.UPPER);
        if (v11n.containsBook(BibleBook.GEN)) {
            Assert.assertEquals("GEN", v11n.getShortName(BibleBook.GEN));
        }
        if (v11n.containsBook(BibleBook.EXOD)) {
            Assert.assertEquals("EXO", v11n.getShortName(BibleBook.EXOD));
        }
        if (v11n.containsBook(BibleBook.JUDG)) {
            Assert.assertEquals("JUDG", v11n.getShortName(BibleBook.JUDG));
        }
        if (v11n.containsBook(BibleBook.MAL)) {
            Assert.assertEquals("MAL", v11n.getShortName(BibleBook.MAL));
        }
        if (v11n.containsBook(BibleBook.MATT)) {
            Assert.assertEquals("MAT", v11n.getShortName(BibleBook.MATT));
        }
        if (v11n.containsBook(BibleBook.PHIL)) {
            Assert.assertEquals("PHILI", v11n.getShortName(BibleBook.PHIL));
        }
        if (v11n.containsBook(BibleBook.PHLM)) {
            Assert.assertEquals("PHILE", v11n.getShortName(BibleBook.PHLM));
        }
        if (v11n.containsBook(BibleBook.JUDE)) {
            Assert.assertEquals("JUDE", v11n.getShortName(BibleBook.JUDE));
        }
        if (v11n.containsBook(BibleBook.REV)) {
            Assert.assertEquals("REV", v11n.getShortName(BibleBook.REV));
        }
    }

    @Test
    public void testVerseListSortOrder() {
        if (v11n.containsBook(BibleBook.GEN) && v11n.containsBook(BibleBook.REV)) {
            List<Key> keyList = new ArrayList<Key>();
            Verse gen11 = new Verse(v11n, BibleBook.GEN, 1, 1);
            Verse rev11 = new Verse(v11n, BibleBook.REV, 1, 1);

            keyList.add(gen11);
            keyList.add(rev11);
            Collections.sort(keyList);

            Assert.assertEquals("Genesis should be at start when sorted", gen11, keyList.get(0));
            Assert.assertEquals("Revelation should be at end when sorted", rev11, keyList.get(1));
        }
    }

    @Test
    public void testPassageListSortOrder() {
        try {
            if (v11n.containsBook(BibleBook.GEN) && v11n.containsBook(BibleBook.REV)) {
                List<Key> keyList = new ArrayList<Key>();
                Key gen11 = PassageKeyFactory.instance().getKey(v11n, "Gen.1.1");
                Key rev11 = PassageKeyFactory.instance().getKey(v11n, "Rev.1.1");

                keyList.add(gen11);
                keyList.add(rev11);
                Collections.sort(keyList);

                Assert.assertEquals("Genesis should be at start when sorted", gen11, keyList.get(0));
                Assert.assertEquals("Revelation should be at end when sorted", rev11, keyList.get(1));
            }
        } catch (NoSuchKeyException e) {
            Assert.fail("Exception in testPassageListSort:" + e.getMessage());
        }
    }
}
