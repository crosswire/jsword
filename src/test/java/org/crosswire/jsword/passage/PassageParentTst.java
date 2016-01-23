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
package org.crosswire.jsword.passage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;

import org.crosswire.jsword.book.CaseType;
import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.BookName;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * This would be called TestPassage however then people might think it was a
 * separate TestCase, which it is not, needing to be inherited from to customize
 * the type of test.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class PassageParentTst {
    public PassageParentTst() {
    }

    public PassageParentTst(PassageType ptype, boolean optimize) {
        PassageKeyFactory.setDefaultPassage(PassageType.toInteger(ptype));
        this.optimize = optimize;
    }

    /** Control the output of names */
    private CaseType storedCase;
    private boolean fullName;
    private Versification v11n;

    /**
     * How we create Passages
     */
    private static PassageKeyFactory keyf = PassageKeyFactory.instance();

    private boolean optimize;

    private Passage genC1V135r;
    private Passage exoC2V1To10C2V1To11r;
    private Passage genToRev;
    private Passage grace;
    private Passage empty;
    private Passage temp;

    private VerseRange genC1V1r;
    private VerseRange genC1V12r;
    private VerseRange genC1V2r;
    private VerseRange exoC2V1r;
    private VerseRange exoC2V12r;
    private VerseRange exoC2V2r;

    private Verse genC1V1;
    private Verse genC1V2;
    private Verse genC1V3;
    private Verse genC1V5;
    private Verse exoC2V1;
    private Verse exoC2V2;
    private Verse exoC2V3;
    private Verse exoC2V11;
    private Verse revC22V21;

    /**
     * Some of these tests fail with an out of memory exception. The optimize
     * bit of these tests is wrong so we never optimize for write, and
     * performing tests aimed at writing after optimizing for reads is allowed
     * to be a bad performer (which includes using lots of memory) So the
     * problem is with the tests and not with the Passages.
     * 
     * @throws Exception 
     */
    @Before
    public void setUp() throws Exception {
        storedCase = BookName.getDefaultCase();
        BookName.setCase(CaseType.SENTENCE);
        fullName = BookName.isFullBookName();
        BookName.setFullBookName(false);
        // AV11N(DMS): Update test to test all V11Ns
        v11n = Versifications.instance().getVersification("KJV");

        genC1V135r = keyf.getKey(v11n, "Gen 1:1, Gen 1:3, Gen 1:5");
        exoC2V1To10C2V1To11r = keyf.getKey(v11n, "Exo 2:1-10, Exo 3:1-11");
        genToRev = keyf.getKey(v11n, "Gen-Rev 22:21");
        grace = (Passage) keyf.createEmptyKeyList(v11n);
        grace.addAll(keyf.getKey(v11n, "Gen 6:8, 19:19, 32:5, 33:8, 10, 15, 39:4, 47:25, 29, 50:4, Exo 33:12-13, 16-17, 34:9, Num 32:5, Judg 6:17, Rut 2:2, 10, 1Sa 1:18, 20:3, 27:5"));
        grace.addAll(keyf.getKey(v11n, "2Sa 14:22, 16:4, Ezr 9:8, Est 2:17, Psa 45:2, 84:11, Pro 1:9, 3:22, 34, 4:9, 22:11, Jer 31:2, Zec 4:7, 12:10, Luk 2:40, Joh 1:14, 16-17"));
        grace.addAll(keyf.getKey(v11n, "Act 4:33, 11:23, 13:43, 14:3, 26, 15:11, 40, 18:27, 20:24, 32, Rom 1:5, 7, 3:24, 4:4, 16, 5:2, 15, 17, 20-6:1, 6:14-15, 11:5-6, 12:3, 6, 15:15, 16:20, 24"));
        grace.addAll(keyf.getKey(v11n, "1Co 1:3-4, 3:10, 10:30, 15:10, 16:23, 2Co 1:2, 12, 4:15, 6:1, 8:1, 6-7, 9, 19, 9:8, 14, 12:9, 13:14, Gal 1:3, 6, 15, 2:9, 21, 5:4, 6:18"));
        grace.addAll(keyf.getKey(v11n, "Eph 1:2, 6-7, 2:5, 7-8, 3:2, 7-8, 4:7, 29, 6:24, Phili 1:2, 7, 4:23, Col 1:2, 6, 3:16, 4:6, 18-1Th 1:1, 1Th 5:28, 2Th 1:2, 12, 2:16, 3:18"));
        grace.addAll(keyf.getKey(v11n, "1Ti 1:2, 14, 6:21, 2Ti 1:2, 9, 2:1, 4:22, Tit 1:4, 2:11, 3:7, 15, Phile 3, 25, Heb 2:9, 4:16, 10:29, 12:15, 28, 13:9, 25, Jam 1:11, 4:6"));
        grace.addAll(keyf.getKey(v11n, "1Pe 1:2, 10, 13, 3:7, 4:10, 5:5, 10, 12, 2Pe 1:2, 3:18, 2Jo 3, Jude 4, Rev 1:4, 22:21"));
        empty = (Passage) keyf.createEmptyKeyList(v11n);

        // String full_type = empty.getClass().getName();
        // String type = full_type.substring(full_type.lastIndexOf(".")+1);
        // boolean skip = type.equals("PassageTally");

        if (optimize) {
            genC1V135r.optimizeReads();
            exoC2V1To10C2V1To11r.optimizeReads();
            genToRev.optimizeReads();
            grace.optimizeReads();
            empty.optimizeReads();
        }

        genC1V1r = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 1, 1), 1);
        genC1V12r = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 1, 1), 2);
        genC1V2r = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 1, 2), 1);
        exoC2V1r = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.EXOD, 2, 1), 1);
        exoC2V12r = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.EXOD, 2, 1), 2);
        exoC2V2r = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.EXOD, 2, 2), 1);

        genC1V1 = new Verse(v11n, BibleBook.GEN, 1, 1);
        genC1V2 = new Verse(v11n, BibleBook.GEN, 1, 2);
        genC1V3 = new Verse(v11n, BibleBook.GEN, 1, 3);
        genC1V5 = new Verse(v11n, BibleBook.GEN, 1, 5);
        exoC2V1 = new Verse(v11n, BibleBook.EXOD, 2, 1);
        exoC2V2 = new Verse(v11n, BibleBook.EXOD, 2, 2);
        exoC2V3 = new Verse(v11n, BibleBook.EXOD, 2, 3);
        exoC2V11 = new Verse(v11n, BibleBook.EXOD, 3, 11);
        revC22V21 = VerseFactory.fromString(v11n, "Rev 22:21");
    }

    @After
    public void tearDown() {
        BookName.setCase(storedCase);
        BookName.setFullBookName(fullName);

        // float secs = (System.currentTimeMillis() - start) / 1000F;
        // log(type+" total = "+secs+"s =======================");

        PassageKeyFactory.setDefaultPassage(PassageType.toInteger(PassageType.SPEED));
    }

    @Test
    public void testWholeBible() throws Exception {
        Iterator<VerseRange> it = genToRev.rangeIterator(RestrictionType.NONE);
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(it.next(), VerseRangeFactory.fromString(v11n, "Gen-Rev"));
        Assert.assertTrue(!it.hasNext());

        // it = gen_rev.rangeIterator(RestrictionType.BOOK);
        // Assert.assertTrue(it.hasNext());
        //        Assert.assertEquals(it.next(), VerseRangeFactory.fromString(v11n, "Gen"));
        // Assert.assertTrue(it.hasNext());
        //        Assert.assertEquals(it.next(), VerseRangeFactory.fromString(v11n, "Exo"));
        // Assert.assertTrue(it.hasNext());
        //        Assert.assertEquals(it.next(), VerseRangeFactory.fromString(v11n, "Lev"));
        // Assert.assertTrue(it.hasNext());
        //        Assert.assertEquals(it.next(), VerseRangeFactory.fromString(v11n, "Num"));
        // Assert.assertTrue(!it.hasNext());

        it = genToRev.rangeIterator(RestrictionType.CHAPTER);
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(it.next(), VerseRangeFactory.fromString(v11n, "Gen 0"));
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(it.next(), VerseRangeFactory.fromString(v11n, "Gen 1"));
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(it.next(), VerseRangeFactory.fromString(v11n, "Gen 2"));
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(it.next(), VerseRangeFactory.fromString(v11n, "Gen 3"));
        // Assert.assertTrue(!it.hasNext());

        it = empty.rangeIterator(RestrictionType.NONE);
        Assert.assertTrue(!it.hasNext());
    }

    @Test
    public void testReadAddPassageListener() throws Exception {
        FixturePassageListener li1 = new FixturePassageListener();
        FixturePassageListener li2 = new FixturePassageListener();
        temp = (Passage) genC1V135r.clone();
        temp.addPassageListener(li1);
        temp.addPassageListener(li2);
        Assert.assertTrue(li1.check(0, 0, 0));
        Assert.assertTrue(li2.check(0, 0, 0));
        temp.add(VerseFactory.fromString(v11n, "Gen 1:7"));
        Assert.assertTrue(li1.check(1, 0, 0));
        Assert.assertTrue(li2.check(1, 0, 0));
        temp.add(VerseFactory.fromString(v11n, "Gen 1:9"));
        Assert.assertTrue(li1.check(2, 0, 0));
        Assert.assertTrue(li2.check(2, 0, 0));
        temp.removePassageListener(li1);
        temp.add(VerseFactory.fromString(v11n, "Gen 1:11"));
        Assert.assertTrue(li1.check(2, 0, 0));
        Assert.assertTrue(li2.check(3, 0, 0));
    }

    @Test
    public void testReadRangeIterator() throws Exception {
        // We used to check for a UnsupportedOperationException until
        // DistinctPassage started
        // throwing IllegalStateExceptions here. I'm not too bothered about the
        // exact type of the
        // exception right now. Maybe we ought to be more thorough at some
        // stage.
        Iterator<VerseRange> it = genC1V135r.rangeIterator(RestrictionType.NONE);
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(VerseRangeFactory.fromString(v11n, "Gen 1:1"), it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(VerseRangeFactory.fromString(v11n, "Gen 1:3"), it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(VerseRangeFactory.fromString(v11n, "Gen 1:5"), it.next());
        Assert.assertTrue(!it.hasNext());
        it = empty.rangeIterator(RestrictionType.NONE);
        Assert.assertTrue(!it.hasNext());
    }

    @Test
    public void testReadVerseIterator() throws Exception {
        Iterator<Key> it = genC1V135r.iterator();
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(VerseFactory.fromString(v11n, "Gen 1:1"), it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(VerseFactory.fromString(v11n, "Gen 1:3"), it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(VerseFactory.fromString(v11n, "Gen 1:5"), it.next());
        Assert.assertTrue(!it.hasNext());
        it = empty.iterator();
        Assert.assertTrue(!it.hasNext());
    }

    @Test
    public void testReadIsEmpty() {
        Assert.assertTrue(!genC1V135r.isEmpty());
        Assert.assertTrue(!exoC2V1To10C2V1To11r.isEmpty());
        Assert.assertTrue(empty.isEmpty());
    }

    @Test
    public void testReadCountVerses() {
        Assert.assertEquals(3, genC1V135r.countVerses());
        Assert.assertEquals(21, exoC2V1To10C2V1To11r.countVerses());
        Assert.assertEquals(0, empty.countVerses());
    }

    @Test
    public void testReadCountRanges() {
        Assert.assertEquals(3, genC1V135r.countRanges(RestrictionType.NONE));
        Assert.assertEquals(2, exoC2V1To10C2V1To11r.countRanges(RestrictionType.NONE));
        Assert.assertEquals(0, empty.countVerses());
    }

    @Test
    public void testReadGetVerseAt() throws Exception {
        Assert.assertEquals(genC1V1, genC1V135r.getVerseAt(0));
        Assert.assertEquals(genC1V3, genC1V135r.getVerseAt(1));
        Assert.assertEquals(genC1V5, genC1V135r.getVerseAt(2));
        Assert.assertEquals(exoC2V1, exoC2V1To10C2V1To11r.getVerseAt(0));
        Assert.assertEquals(exoC2V2, exoC2V1To10C2V1To11r.getVerseAt(1));
        Assert.assertEquals(exoC2V3, exoC2V1To10C2V1To11r.getVerseAt(2));
        Assert.assertEquals(exoC2V11, exoC2V1To10C2V1To11r.getVerseAt(20));
    }

    @Test
    public void testReadGetVerseRangeAt() throws Exception {
        Assert.assertEquals(genC1V1r, genC1V135r.getRangeAt(0, RestrictionType.NONE));
        Assert.assertEquals(VerseRangeFactory.fromString(v11n, "Gen 1:3"), genC1V135r.getRangeAt(1, RestrictionType.NONE));
        Assert.assertEquals(VerseRangeFactory.fromString(v11n, "Gen 1:5"), genC1V135r.getRangeAt(2, RestrictionType.NONE));
        Assert.assertEquals(VerseRangeFactory.fromString(v11n, "Exo 2:1-10"), exoC2V1To10C2V1To11r.getRangeAt(0, RestrictionType.NONE));
        Assert.assertEquals(VerseRangeFactory.fromString(v11n, "Exo 3:1-11"), exoC2V1To10C2V1To11r.getRangeAt(1, RestrictionType.NONE));
    }

    @Test
    public void testReadBooksInPassage() {
        Assert.assertEquals(1, genC1V135r.booksInPassage());
        Assert.assertEquals(1, exoC2V1To10C2V1To11r.booksInPassage());
    }

    @Test
    public void testReadContainsVerse() {
        Assert.assertTrue(!empty.contains(genC1V1));
        Assert.assertTrue(genC1V135r.contains(genC1V1));
        Assert.assertTrue(!genC1V135r.contains(genC1V2));
        Assert.assertTrue(genC1V135r.contains(genC1V3));
        Assert.assertTrue(genC1V135r.contains(genC1V5));
        Assert.assertTrue(genToRev.contains(genC1V1));
        Assert.assertTrue(genToRev.contains(genC1V2));
        Assert.assertTrue(genToRev.contains(revC22V21));
    }

    @Test
    public void testReadContainsVerseRange() {
        Assert.assertTrue(genC1V135r.contains(genC1V1r));
        Assert.assertTrue(!genC1V135r.contains(genC1V12r));
        Assert.assertTrue(!genC1V135r.contains(genC1V2r));
        Assert.assertTrue(!genC1V135r.contains(exoC2V1r));
        Assert.assertTrue(!exoC2V1To10C2V1To11r.contains(genC1V1r));
        Assert.assertTrue(exoC2V1To10C2V1To11r.contains(exoC2V1r));
        Assert.assertTrue(exoC2V1To10C2V1To11r.contains(exoC2V12r));
        Assert.assertTrue(exoC2V1To10C2V1To11r.contains(exoC2V2r));
        Assert.assertTrue(genToRev.contains(new VerseRange(v11n, revC22V21)));
    }

    @Test
    public void testReadContainsAll() {
        Assert.assertTrue(!genC1V135r.containsAll(exoC2V1To10C2V1To11r));
        Assert.assertTrue(genC1V135r.containsAll(empty));
        Assert.assertTrue(genC1V135r.containsAll((Passage) genC1V135r.clone()));
        Assert.assertTrue(!exoC2V1To10C2V1To11r.containsAll(genC1V135r));
        Assert.assertTrue(exoC2V1To10C2V1To11r.containsAll(empty));
        Assert.assertTrue(exoC2V1To10C2V1To11r.containsAll((Passage) exoC2V1To10C2V1To11r.clone()));
    }

    @Test
    public void testReadContainsVerseBase() {
        Assert.assertTrue(!empty.contains(genC1V1));
        Assert.assertTrue(genC1V135r.contains(genC1V1));
        Assert.assertTrue(!genC1V135r.contains(genC1V2));
        Assert.assertTrue(genC1V135r.contains(genC1V3));
        Assert.assertTrue(genC1V135r.contains(genC1V5));
        Assert.assertTrue(genC1V135r.contains(genC1V1r));
        Assert.assertTrue(!genC1V135r.contains(genC1V12r));
        Assert.assertTrue(!genC1V135r.contains(genC1V2r));
        Assert.assertTrue(!genC1V135r.contains(exoC2V1r));
        Assert.assertTrue(!exoC2V1To10C2V1To11r.contains(genC1V1r));
        Assert.assertTrue(exoC2V1To10C2V1To11r.contains(exoC2V1r));
        Assert.assertTrue(exoC2V1To10C2V1To11r.contains(exoC2V12r));
        Assert.assertTrue(exoC2V1To10C2V1To11r.contains(exoC2V2r));
    }

    // ==========================================================================

    @Test
    public void testWriteCreatePassage() {
        Assert.assertEquals(0, ((Passage) keyf.createEmptyKeyList(v11n)).countVerses());
    }

    @Test
    public void testWriteToString() throws Exception {
        Assert.assertEquals("Gen 1:1-3, Rev 22:21", keyf.getKey(v11n, "gen 1 1,gen 1 3,rev 22 21,gen 1 2").toString());
        Assert.assertEquals("Gen 1:1-3, 22:2-10, Rev 22",
                keyf.getKey(v11n, "Gen 1 3;gen 22 2;rev 22 21;gen 22 3-10; rev 22 19;gen 1 1;rev 22 10-18; gen 1 2; rev 22 1-21").toString());
        Assert.assertEquals("", keyf.getKey(v11n, "").toString());
        Assert.assertEquals("Gen-Exo", keyf.getKey(v11n, "gen 1 1-50:26,ex,ex 1 2,ex 1 3-10").toString());
        Assert.assertEquals("", keyf.getKey(v11n, null).toString());
    }

    @Test
    public void testWriteGetName() throws Exception {
        Assert.assertEquals("Gen 1:1-3, Rev 22:21", keyf.getKey(v11n, "gen 1 1,gen 1 3,rev 22 21,gen 1 2").getName());
        Assert.assertEquals("Gen 1:1-3, 22:2-10, Rev 22",
                keyf.getKey(v11n, "Gen 1 3;gen 22 2;rev 22 21;gen 22 3-10; rev 22 19;gen 1 1;rev 22 10-18; gen 1 2; rev 22 1-21").getName());
        Assert.assertEquals("", keyf.getKey(v11n, "").getName());
        Assert.assertEquals("Gen-Exo", keyf.getKey(v11n, "gen 1 1-50:26,ex,ex 1 2,ex 1 3-10").getName());
        Assert.assertEquals("Exo 1:1, 4", keyf.getKey(v11n, "exo 1:1, 4").getName());
        Assert.assertEquals("Exo 1:1-4, 6-22", keyf.getKey(v11n, "exo 1:1, 4, 2-3, 11-ff, 6-10").getName());
        Assert.assertEquals("Num 1-2", keyf.getKey(v11n, "Num 1, 2").getName());
        // Test for the separator being a space. This comes from "Clarke"
        Assert.assertEquals("Gen 1:26, 3:22, 11:7, 20:13, 31:7, 53, 35:7", keyf.getKey(v11n, "Ge 1:26  3:22  11:7  20:13  31:7, 53  35:7").getName());
    }

    @Ignore
    @Test
    public void testWriteBlur() throws Exception {
        temp = (Passage) genC1V135r.clone();
        temp.blur(0, RestrictionType.CHAPTER);
        Assert.assertEquals(temp, genC1V135r);
        temp = (Passage) genC1V135r.clone();
        temp.blur(0, RestrictionType.NONE);
        Assert.assertEquals(temp, genC1V135r);
        temp = (Passage) genC1V135r.clone();
        temp.blur(1, RestrictionType.CHAPTER);
        Assert.assertEquals(temp, keyf.getKey(v11n, "Gen 1:0-6"));
        temp = (Passage) genC1V135r.clone();
        temp.blur(1, RestrictionType.NONE);
        Assert.assertEquals(temp, keyf.getKey(v11n, "Gen 1:0-6"));
        temp = (Passage) genC1V135r.clone();
        temp.blur(2, RestrictionType.CHAPTER);
        Assert.assertEquals(temp, keyf.getKey(v11n, "Gen 1:0-7"));
        temp = (Passage) genC1V135r.clone();
        temp.blur(2, RestrictionType.NONE);
        Assert.assertEquals(temp, keyf.getKey(v11n, "Gen 0:0-1:7"));
        temp = (Passage) genC1V135r.clone();
        temp.blur(12, RestrictionType.CHAPTER);
        Assert.assertEquals(temp, keyf.getKey(v11n, "Gen 1:0-17"));
        temp = (Passage) genC1V135r.clone();
        temp.blur(12, RestrictionType.NONE);
        Assert.assertEquals(temp, keyf.getKey(v11n, "Intro.OT 0:0-Gen 1:17"));
        temp = (Passage) genC1V135r.clone();
        temp.blur(26, RestrictionType.CHAPTER);
        Assert.assertEquals(temp, keyf.getKey(v11n, "Gen 1:0-31"));
        temp = (Passage) genC1V135r.clone();
        temp.blur(26, RestrictionType.NONE);
        Assert.assertEquals(temp, keyf.getKey(v11n, "Intro.OT-Gen 1:31"));
        temp = (Passage) genC1V135r.clone();
        temp.blur(27, RestrictionType.CHAPTER);
        Assert.assertEquals(temp, keyf.getKey(v11n, "Gen 1:0-31"));
        temp = (Passage) genC1V135r.clone();
        temp.blur(27, RestrictionType.NONE);
        Assert.assertEquals(temp, keyf.getKey(v11n, "Intro.OT-Gen 2:0"));
        temp = (Passage) exoC2V1To10C2V1To11r.clone();
        temp.blur(0, RestrictionType.CHAPTER);
        Assert.assertEquals(temp, keyf.getKey(v11n, "Exo 2:1-10, Exo 3:1-11"));
        temp = (Passage) exoC2V1To10C2V1To11r.clone();
        temp.blur(0, RestrictionType.NONE);
        Assert.assertEquals(temp, keyf.getKey(v11n, "Exo 2:1-10, Exo 3:1-11"));
        temp = (Passage) exoC2V1To10C2V1To11r.clone();
        temp.blur(1, RestrictionType.CHAPTER);
        Assert.assertEquals(temp, keyf.getKey(v11n, "Exo 2:0-11, Exo 3:0-12"));
        temp = (Passage) exoC2V1To10C2V1To11r.clone();
        temp.blur(1, RestrictionType.NONE);
        Assert.assertEquals(temp, keyf.getKey(v11n, "Exo 2:0-2:11, Exo 3:0-3:12"));
        temp = (Passage) exoC2V1To10C2V1To11r.clone();
        temp.blur(2, RestrictionType.CHAPTER);
        Assert.assertEquals(temp, keyf.getKey(v11n, "Exo 2:0-12, Exo 3:0-13"));
        temp = (Passage) exoC2V1To10C2V1To11r.clone();
        temp.blur(2, RestrictionType.NONE);
        Assert.assertEquals(temp, keyf.getKey(v11n, "Exo 1:22-2:12, Exo 2:25-3:13"));
        temp = (Passage) exoC2V1To10C2V1To11r.clone();
        temp.blur(3, RestrictionType.CHAPTER);
        Assert.assertEquals(temp, keyf.getKey(v11n, "Exo 2:0-13, Exo 3:0-14"));
        temp = (Passage) exoC2V1To10C2V1To11r.clone();
        temp.blur(3, RestrictionType.NONE);
        Assert.assertEquals(temp, keyf.getKey(v11n, "Exo 1:21-2:13, Exo 2:24-3:14"));
        temp = (Passage) exoC2V1To10C2V1To11r.clone();
        temp.blur(14, RestrictionType.CHAPTER);
        Assert.assertEquals(temp, keyf.getKey(v11n, "Exo 2:0-24, Exo 3:0-22"));
        temp = (Passage) exoC2V1To10C2V1To11r.clone();
        temp.blur(14, RestrictionType.NONE);
        Assert.assertEquals(temp, keyf.getKey(v11n, "Exo 1:10-2:24, Exo 2:12-4:2"));
        temp = (Passage) exoC2V1To10C2V1To11r.clone();
        temp.blur(15, RestrictionType.CHAPTER);
        Assert.assertEquals(temp, keyf.getKey(v11n, "Exo 2:0-25, Exo 3:0-22"));
        temp = (Passage) exoC2V1To10C2V1To11r.clone();
        temp.blur(15, RestrictionType.NONE);
        Assert.assertEquals(temp, keyf.getKey(v11n, "Exo 1:9-2:25, Exo 2:11-4:3"));
        temp = (Passage) exoC2V1To10C2V1To11r.clone();
        temp.blur(16, RestrictionType.CHAPTER);
        Assert.assertEquals(temp, keyf.getKey(v11n, "Exo 2:1-3:22"));
        temp = (Passage) exoC2V1To10C2V1To11r.clone();
        temp.blur(16, RestrictionType.NONE);
        Assert.assertEquals(temp, keyf.getKey(v11n, "Exo 1:8-4:4"));
        temp = (Passage) exoC2V1To10C2V1To11r.clone();
        temp.blur(99999, RestrictionType.CHAPTER);
        Assert.assertEquals(temp, keyf.getKey(v11n, "Exo 2:1-3:22"));
        temp = (Passage) exoC2V1To10C2V1To11r.clone();
        temp.blur(99999, RestrictionType.NONE);
        Assert.assertEquals(temp, keyf.getKey(v11n, "Intro.OT-Rev 22:21"));
    }

    @Test
    public void testWriteAddPassageListener() throws Exception {
        FixturePassageListener li1 = new FixturePassageListener();
        FixturePassageListener li2 = new FixturePassageListener();
        temp = (Passage) genC1V135r.clone();
        temp.addPassageListener(li1);
        temp.addPassageListener(li2);
        Assert.assertTrue(li1.check(0, 0, 0));
        Assert.assertTrue(li2.check(0, 0, 0));
        temp.add(VerseFactory.fromString(v11n, "Gen 1:7"));
        Assert.assertTrue(li1.check(1, 0, 0));
        Assert.assertTrue(li2.check(1, 0, 0));
        temp.add(VerseFactory.fromString(v11n, "Gen 1:9"));
        Assert.assertTrue(li1.check(2, 0, 0));
        Assert.assertTrue(li2.check(2, 0, 0));
        temp.removePassageListener(li1);
        temp.add(VerseFactory.fromString(v11n, "Gen 1:11"));
        Assert.assertTrue(li1.check(2, 0, 0));
        Assert.assertTrue(li2.check(3, 0, 0));
    }

    @Test
    public void testWriteClone() {
        Assert.assertTrue(genC1V135r != genC1V135r.clone());
        Assert.assertEquals(genC1V135r, genC1V135r.clone());
        Assert.assertTrue(exoC2V1To10C2V1To11r != exoC2V1To10C2V1To11r.clone());
        Assert.assertEquals(exoC2V1To10C2V1To11r, exoC2V1To10C2V1To11r.clone());
    }

    @Test
    public void testWriteRangeIterator() throws Exception {
        Iterator<VerseRange> it = genC1V135r.rangeIterator(RestrictionType.NONE);
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(it.next(), VerseRangeFactory.fromString(v11n, "Gen 1:1"));
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(it.next(), VerseRangeFactory.fromString(v11n, "Gen 1:3"));
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(it.next(), VerseRangeFactory.fromString(v11n, "Gen 1:5"));
        Assert.assertTrue(!it.hasNext());

        it = genToRev.rangeIterator(RestrictionType.NONE);
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(it.next(), VerseRangeFactory.fromString(v11n, "Gen-Rev"));
        Assert.assertTrue(!it.hasNext());

        // No longer have RestrictionType.BOOK
        // it = gen_rev.rangeIterator(RestrictionType.BOOK);
        // Assert.assertTrue(it.hasNext());
        //        Assert.assertEquals(it.next(), VerseRangeFactory.fromString(v11n, "Gen"));
        // Assert.assertTrue(it.hasNext());
        //        Assert.assertEquals(it.next(), VerseRangeFactory.fromString(v11n, "Exo"));
        // Assert.assertTrue(it.hasNext());
        //        Assert.assertEquals(it.next(), VerseRangeFactory.fromString(v11n, "Lev"));
        // Assert.assertTrue(it.hasNext());
        //        Assert.assertEquals(it.next(), VerseRangeFactory.fromString(v11n, "Num"));
        // Assert.assertTrue(!it.hasNext());

        it = genToRev.rangeIterator(RestrictionType.CHAPTER);
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(it.next(), VerseRangeFactory.fromString(v11n, "Gen 0"));
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(it.next(), VerseRangeFactory.fromString(v11n, "Gen 1"));
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(it.next(), VerseRangeFactory.fromString(v11n, "Gen 2"));
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(it.next(), VerseRangeFactory.fromString(v11n, "Gen 3"));
        // Assert.assertTrue(!it.hasNext());

        it = empty.rangeIterator(RestrictionType.NONE);
        Assert.assertTrue(!it.hasNext());
    }

    @Test
    public void testWriteVerseIterator() throws Exception {
        Iterator<Key> it = genC1V135r.iterator();
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(it.next(), VerseFactory.fromString(v11n, "Gen 1:1"));
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(it.next(), VerseFactory.fromString(v11n, "Gen 1:3"));
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(it.next(), VerseFactory.fromString(v11n, "Gen 1:5"));
        Assert.assertTrue(!it.hasNext());
        it = empty.iterator();
        Assert.assertTrue(!it.hasNext());
    }

    @Test
    public void testWriteIsEmpty() {
        Assert.assertTrue(!genC1V135r.isEmpty());
        Assert.assertTrue(!exoC2V1To10C2V1To11r.isEmpty());
        Assert.assertTrue(empty.isEmpty());
    }

    @Test
    public void testWriteCountVerses() {
        Assert.assertEquals(genC1V135r.countVerses(), 3);
        Assert.assertEquals(exoC2V1To10C2V1To11r.countVerses(), 21);
        Assert.assertEquals(empty.countVerses(), 0);
    }

    @Test
    public void testWriteCountRanges() {
        Assert.assertEquals(genC1V135r.countRanges(RestrictionType.NONE), 3);
        Assert.assertEquals(exoC2V1To10C2V1To11r.countRanges(RestrictionType.NONE), 2);
        Assert.assertEquals(empty.countVerses(), 0);
    }

    @Test
    public void testWriteGetVerseAt() throws Exception {
        Assert.assertEquals(genC1V135r.getVerseAt(0), genC1V1);
        Assert.assertEquals(genC1V135r.getVerseAt(1), genC1V3);
        Assert.assertEquals(genC1V135r.getVerseAt(2), genC1V5);
        Assert.assertEquals(exoC2V1To10C2V1To11r.getVerseAt(0), exoC2V1);
        Assert.assertEquals(exoC2V1To10C2V1To11r.getVerseAt(1), exoC2V2);
        Assert.assertEquals(exoC2V1To10C2V1To11r.getVerseAt(2), exoC2V3);
        Assert.assertEquals(exoC2V1To10C2V1To11r.getVerseAt(20), exoC2V11);
    }

    @Test
    public void testWriteGetVerseRangeAt() throws Exception {
        Assert.assertEquals(genC1V135r.getRangeAt(0, RestrictionType.NONE), genC1V1r);
        Assert.assertEquals(genC1V135r.getRangeAt(1, RestrictionType.NONE), VerseRangeFactory.fromString(v11n, "Gen 1:3"));
        Assert.assertEquals(genC1V135r.getRangeAt(2, RestrictionType.NONE), VerseRangeFactory.fromString(v11n, "Gen 1:5"));
        Assert.assertEquals(exoC2V1To10C2V1To11r.getRangeAt(0, RestrictionType.NONE), VerseRangeFactory.fromString(v11n, "Exo 2:1-10"));
        Assert.assertEquals(exoC2V1To10C2V1To11r.getRangeAt(1, RestrictionType.NONE), VerseRangeFactory.fromString(v11n, "Exo 3:1-11"));
    }

    @Test
    public void testWriteBooksInPassage() {
        Assert.assertEquals(genC1V135r.booksInPassage(), 1);
        Assert.assertEquals(exoC2V1To10C2V1To11r.booksInPassage(), 1);
    }

    @Test
    public void testWriteContainsVerse() {
        Assert.assertTrue(!empty.contains(genC1V1));
        Assert.assertTrue(genC1V135r.contains(genC1V1));
        Assert.assertTrue(!genC1V135r.contains(genC1V2));
        Assert.assertTrue(genC1V135r.contains(genC1V3));
        Assert.assertTrue(genC1V135r.contains(genC1V5));
        Assert.assertTrue(genToRev.contains(genC1V1));
        Assert.assertTrue(genToRev.contains(genC1V2));
        Assert.assertTrue(genToRev.contains(revC22V21));
    }

    @Test
    public void testWriteContainsVerseRange() {
        Assert.assertTrue(genC1V135r.contains(genC1V1r));
        Assert.assertTrue(!genC1V135r.contains(genC1V12r));
        Assert.assertTrue(!genC1V135r.contains(genC1V2r));
        Assert.assertTrue(!genC1V135r.contains(exoC2V1r));
        Assert.assertTrue(!exoC2V1To10C2V1To11r.contains(genC1V1r));
        Assert.assertTrue(exoC2V1To10C2V1To11r.contains(exoC2V1r));
        Assert.assertTrue(exoC2V1To10C2V1To11r.contains(exoC2V12r));
        Assert.assertTrue(exoC2V1To10C2V1To11r.contains(exoC2V2r));
        Assert.assertTrue(genToRev.contains(new VerseRange(v11n, revC22V21)));
    }

    @Test
    public void testWriteContainsAll() {
        Assert.assertTrue(!genC1V135r.containsAll(exoC2V1To10C2V1To11r));
        Assert.assertTrue(genC1V135r.containsAll(empty));
        Assert.assertTrue(genC1V135r.containsAll((Passage) genC1V135r.clone()));
        Assert.assertTrue(!exoC2V1To10C2V1To11r.containsAll(genC1V135r));
        Assert.assertTrue(exoC2V1To10C2V1To11r.containsAll(empty));
        Assert.assertTrue(exoC2V1To10C2V1To11r.containsAll((Passage) exoC2V1To10C2V1To11r.clone()));
        Assert.assertTrue(!empty.containsAll(genC1V135r));
        Assert.assertTrue(empty.containsAll(empty));
        Assert.assertTrue(!empty.containsAll(exoC2V1To10C2V1To11r));
        Assert.assertTrue(genToRev.containsAll(genToRev));
    }

    @Test
    public void testWriteContainsVerseBase() {
        Assert.assertTrue(!empty.contains(genC1V1));
        Assert.assertTrue(genC1V135r.contains(genC1V1));
        Assert.assertTrue(!genC1V135r.contains(genC1V2));
        Assert.assertTrue(genC1V135r.contains(genC1V3));
        Assert.assertTrue(genC1V135r.contains(genC1V5));
        Assert.assertTrue(genC1V135r.contains(genC1V1r));
        Assert.assertTrue(!genC1V135r.contains(genC1V12r));
        Assert.assertTrue(!genC1V135r.contains(genC1V2r));
        Assert.assertTrue(!genC1V135r.contains(exoC2V1r));
        Assert.assertTrue(!exoC2V1To10C2V1To11r.contains(genC1V1r));
        Assert.assertTrue(exoC2V1To10C2V1To11r.contains(exoC2V1r));
        Assert.assertTrue(exoC2V1To10C2V1To11r.contains(exoC2V12r));
        Assert.assertTrue(exoC2V1To10C2V1To11r.contains(exoC2V2r));
    }

    @Test
    public void testWriteAdd() throws Exception {
        temp = (Passage) genC1V135r.clone();
        temp.add(VerseFactory.fromString(v11n, "Gen 1:2"));
        Assert.assertEquals(temp.getName(), "Gen 1:1-3, 5");
        temp.add(VerseFactory.fromString(v11n, "Gen 1:4"));
        Assert.assertEquals(temp.getName(), "Gen 1:1-5");
        temp = (Passage) genC1V135r.clone();
        temp.add(VerseRangeFactory.fromString(v11n, "Gen 1:2-4"));
        Assert.assertEquals(temp.getName(), "Gen 1:1-5");
        temp = (Passage) genC1V135r.clone();
        temp.add(VerseRangeFactory.fromString(v11n, "Gen 1:2"));
        Assert.assertEquals(temp.getName(), "Gen 1:1-3, 5");
        temp.add(VerseRangeFactory.fromString(v11n, "Gen 1:4"));
        Assert.assertEquals(temp.getName(), "Gen 1:1-5");
        temp = (Passage) genC1V135r.clone();
        temp.add(VerseRangeFactory.fromString(v11n, "Gen 1:1-5"));
        Assert.assertEquals(temp.getName(), "Gen 1:1-5");
    }

    @Test
    public void testWriteAddAll() throws Exception {
        temp = (Passage) genC1V135r.clone();
        temp.addAll(keyf.getKey(v11n, "Gen 1:2, Gen 1:4"));
        Assert.assertEquals(temp.getName(), "Gen 1:1-5");
    }

    @Test
    public void testWriteClear() {
        temp = (Passage) genC1V135r.clone();
        temp.clear();
        Assert.assertEquals(temp.getName(), "");
        temp.clear();
        Assert.assertEquals(temp.getName(), "");
    }

    @Test
    public void testWriteRemove() throws Exception {
        temp = (Passage) genC1V135r.clone();
        temp.remove(VerseFactory.fromString(v11n, "Gen 1:3"));
        Assert.assertEquals(temp.getName(), "Gen 1:1, 5");
        temp.remove(VerseFactory.fromString(v11n, "Gen 1:5"));
        Assert.assertEquals(temp.getName(), "Gen 1:1");
        temp.remove(VerseFactory.fromString(v11n, "Gen 1:1"));
        Assert.assertEquals(temp.getName(), "");
        temp = keyf.getKey(v11n, "Gen 1:1-5");
        temp.remove(VerseFactory.fromString(v11n, "Gen 1:3"));
        Assert.assertEquals(temp.getName(), "Gen 1:1-2, 4-5");
    }

    @Test
    public void testWriteRemoveAllCollection() throws Exception {
        temp = keyf.getKey(v11n, "Gen 1:1-5");
        temp.removeAll(keyf.getKey(v11n, "Gen 1:2, Gen 1:4"));
        Assert.assertEquals(temp.getName(), "Gen 1:1, 3, 5");
        temp.removeAll(keyf.getKey(v11n, "Exo 1:2, Gen 1:4"));
        Assert.assertEquals(temp.getName(), "Gen 1:1, 3, 5");
        temp.removeAll(keyf.getKey(v11n, "Gen 1:2-Rev 22:21"));
        Assert.assertEquals(temp.getName(), "Gen 1:1");
        temp.removeAll(keyf.getKey(v11n, "Gen 1:1"));
        Assert.assertEquals(temp.getName(), "");
    }

    @Ignore
    @Test
    public void testWriteRetainAllCollection() throws Exception {
        temp = keyf.getKey(v11n, "Gen 1:1-5");
        temp.retainAll(keyf.getKey(v11n, "Gen 1:2, Gen 1:4"));
        Assert.assertEquals(temp.getName(), "Gen 1:2, 4");
        temp.retainAll(keyf.getKey(v11n, "Exo 1:2, Gen 1:4"));
        Assert.assertEquals(temp.getName(), "Gen 1:4");
        temp.retainAll(keyf.getKey(v11n, "Gen 1:2-Rev 22:21"));
        Assert.assertEquals(temp.getName(), "Gen 1:4");
        temp.retainAll(keyf.getKey(v11n, "Gen 1:1"));
        Assert.assertEquals(temp.getName(), "");

        temp.addAll(grace);
        Assert.assertEquals(temp.countVerses(), grace.countVerses());
        temp.retainAll(genToRev);
        Assert.assertEquals(temp, grace);
        temp.retainAll(keyf.getKey(v11n, "gen"));
        Assert.assertEquals(temp.countVerses(), 10);
        temp.retainAll(keyf.getKey(v11n, "gen 35:1-rev"));
        Assert.assertEquals(temp.countVerses(), 4);
        temp.retainAll(keyf.getKey(v11n, "exo-rev"));
        Assert.assertEquals(temp.getName(), "");
    }

    @Test
    public void testWriteObject() throws Exception {
        Passage hard = (Passage) keyf.createEmptyKeyList(v11n);
        for (int i = 10; i < v11n.maximumOrdinal(); i += 200) {
            hard.add(v11n.decodeOrdinal(i));
        }

        File testDat = new File("test.dat");

        FileOutputStream fileOut = new FileOutputStream(testDat);
        ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
        objOut.writeObject(genC1V135r);
        objOut.writeObject(exoC2V1To10C2V1To11r);
        objOut.writeObject(genToRev);
        objOut.writeObject(hard);
        objOut.writeObject(grace);
        objOut.close();
        objOut = null;
        FileInputStream fileIn = new FileInputStream(testDat);
        ObjectInputStream objIn = new ObjectInputStream(fileIn);
        Passage genC1V135Copy = (Passage) objIn.readObject();
        Passage exoC2V1To10C2V1To11rCopy = (Passage) objIn.readObject();
        Passage genToRevCopy = (Passage) objIn.readObject();
        Passage hardCopy = (Passage) objIn.readObject();
        Passage graceCopy = (Passage) objIn.readObject();
        objIn.close();
        Assert.assertEquals(genC1V135Copy, genC1V135r);
        Assert.assertEquals(exoC2V1To10C2V1To11rCopy, exoC2V1To10C2V1To11r);
        Assert.assertEquals(genToRevCopy, genToRev);
        Assert.assertEquals(hardCopy, hard);
        Assert.assertEquals(graceCopy, grace);
        testDat.delete();
    }

    @Test
    public void testWriteDescription() throws Exception {
        File testDat = new File("test.dat");
        FileWriter wout = new FileWriter(testDat);
        genC1V135r.writeDescription(wout);
        wout.close();
        wout = null;
        FileReader win = new FileReader(testDat);
        Passage lst = (Passage) keyf.createEmptyKeyList(v11n);
        lst.readDescription(win);
        win.close();
        win = null;
        Assert.assertEquals(genC1V135r, lst);
        testDat.delete();
    }

    @Test
    public void testSpecial() throws Exception {
        // Some special tests for known breakages
        Passage ich5l = keyf.getKey(v11n, "1ch 5");
        Passage ich5u = keyf.getKey(v11n, "1Ch 5");
        Assert.assertEquals(ich5l, ich5u);
    }
}
