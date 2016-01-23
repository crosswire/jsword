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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import org.junit.Test;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class PassageTally2Test {
    /** Control the output of names */
    private CaseType storedCase;
    private boolean fullName;
    private Versification v11n;

    /**
     * How we create Passages
     */
    private static PassageKeyFactory keyf = PassageKeyFactory.instance();

    private VerseRange genC1V1r;
    private VerseRange genC1V12r;
    private VerseRange genC1V2r;
    private VerseRange exoC2V1r;

    private Verse genC1V1;
    private Verse genC1V2;
    private Verse genC1V3;
    private Verse genC1V5;

    private Passage genC1V135;
    private Passage genC123V1;
    private Passage genC1V157;

    // AV11N(DMS): Update test to test all V11Ns
    private PassageTally tally = new PassageTally(Versifications.instance().getVersification("KJV"));
    private PassageTally empty = new PassageTally(Versifications.instance().getVersification("KJV"));
    private PassageTally temp;

    @Before
    public void setUp() throws Exception {
        storedCase = BookName.getDefaultCase();
        BookName.setCase(CaseType.SENTENCE);
        fullName = BookName.isFullBookName();
        BookName.setFullBookName(false);
        // AV11N(DMS): Update test to test all V11Ns
        v11n = Versifications.instance().getVersification("KJV");

        genC1V1r = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 1, 1), 1);
        genC1V12r = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 1, 1), 2);
        genC1V2r = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 1, 2), 1);
        exoC2V1r = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.EXOD, 2, 1), 1);

        genC1V1 = new Verse(v11n, BibleBook.GEN, 1, 1);
        genC1V2 = new Verse(v11n, BibleBook.GEN, 1, 2);
        genC1V3 = new Verse(v11n, BibleBook.GEN, 1, 3);
        genC1V5 = new Verse(v11n, BibleBook.GEN, 1, 5);

        genC1V135 = keyf.getKey(v11n, "Gen 1:1, Gen 1:3, Gen 1:5");
        genC123V1 = keyf.getKey(v11n, "Gen 1:1, Gen 2:1, Gen 3:1");
        genC1V157 = keyf.getKey(v11n, "Gen 1:1, Gen 1:5, Gen 1:7");

        tally.setOrdering(PassageTally.Order.TALLY);
        empty.setOrdering(PassageTally.Order.TALLY);

        tally.addAll(genC1V135);
        tally.addAll(genC123V1);
        tally.addAll(genC1V157);
    }

    @After
    public void tearDown() {
        BookName.setCase(storedCase);
        BookName.setFullBookName(fullName);
    }

    @Test
    public void testGetName() {
        Assert.assertEquals("Gen 1:1, 5, 3, 7, 2:1, 3:1", tally.getName(0));
        Assert.assertEquals("Gen 1:1", tally.getName(1));
        Assert.assertEquals("Gen 1:1, 5", tally.getName(2));
        Assert.assertEquals("Gen 1:1, 5, 3", tally.getName(3));
        Assert.assertEquals("Gen 1:1, 5, 3, 7", tally.getName(4));
        Assert.assertEquals("Gen 1:1, 5, 3, 7, 2:1", tally.getName(5));
        Assert.assertEquals("Gen 1:1, 5, 3, 7, 2:1, 3:1", tally.getName(6));
        Assert.assertEquals("Gen 1:1, 5, 3, 7, 2:1, 3:1", tally.getName(7));
        Assert.assertEquals("Gen 1:1, 5, 3, 7, 2:1, 3:1", tally.getName(8));
        Assert.assertEquals("Gen 1:1, 5, 3, 7, 2:1, 3:1", tally.getName(9));
        Assert.assertEquals("Gen 1:1, 5, 3, 7, 2:1, 3:1", tally.getName(10));
        Assert.assertEquals("", empty.getName(0));
        Assert.assertEquals("", empty.getName(1));
        Assert.assertEquals("", empty.getName(2));
        Assert.assertEquals("", empty.getName(3));
        Assert.assertEquals("", empty.getName(4));
        Assert.assertEquals("", empty.getName(5));
        Assert.assertEquals("", empty.getName(6));
        Assert.assertEquals("", empty.getName(7));
        Assert.assertEquals("", empty.getName(8));
        Assert.assertEquals("", empty.getName(9));
        Assert.assertEquals("", empty.getName(10));
    }

    @Test
    public void testToString() {
        Assert.assertEquals("Gen 1:1, 5, 3, 7, 2:1, 3:1", tally.toString());
        Assert.assertEquals("", empty.toString());
    }

    @Test
    public void testGetOrderedNameAndTally() {
        Assert.assertEquals("Gen 1:1 (100%), Gen 1:5 (66%), Gen 1:3 (33%), Gen 1:7 (33%), Gen 2:1 (33%), Gen 3:1 (33%)", tally.getNameAndTally());
        Assert.assertEquals("", empty.getNameAndTally());
    }

    @Test
    public void testAddPassageListener() throws Exception {
        FixturePassageListener li = new FixturePassageListener();
        temp = tally.clone();
        temp.addPassageListener(li);
        Assert.assertTrue(li.check(0, 0, 0));
        temp.add(VerseFactory.fromString(v11n, "Gen 1:7"));
        Assert.assertTrue(li.check(1, 0, 0));
        temp.add(VerseFactory.fromString(v11n, "Gen 1:9"));
        Assert.assertTrue(li.check(2, 0, 0));
        temp.removePassageListener(li);
        temp.add(VerseFactory.fromString(v11n, "Gen 1:11"));
        Assert.assertTrue(li.check(2, 0, 0));
    }

    @Test
    public void testClone() {
        Assert.assertTrue(tally != tally.clone());
        Assert.assertEquals(tally, tally.clone());
        Assert.assertTrue(empty != empty.clone());
        Assert.assertEquals(empty, empty.clone());
    }

    @Test
    public void testVerseIterator() throws Exception {
        Iterator<Key> it = tally.iterator();
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(VerseFactory.fromString(v11n, "Gen 1:1"), it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(VerseFactory.fromString(v11n, "Gen 1:5"), it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(VerseFactory.fromString(v11n, "Gen 1:3"), it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(VerseFactory.fromString(v11n, "Gen 1:7"), it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(VerseFactory.fromString(v11n, "Gen 2:1"), it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(VerseFactory.fromString(v11n, "Gen 3:1"), it.next());
        Assert.assertTrue(!it.hasNext());
        it = empty.iterator();
        Assert.assertTrue(!it.hasNext());
    }

    @Test
    public void testRangeIterator() throws Exception {
        Iterator<VerseRange> it = tally.rangeIterator(RestrictionType.NONE);
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(VerseRangeFactory.fromString(v11n, "Gen 1:1"), it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(VerseRangeFactory.fromString(v11n, "Gen 1:5"), it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(VerseRangeFactory.fromString(v11n, "Gen 1:3"), it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(VerseRangeFactory.fromString(v11n, "Gen 1:7"), it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(VerseRangeFactory.fromString(v11n, "Gen 2:1"), it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(VerseRangeFactory.fromString(v11n, "Gen 3:1"), it.next());
        Assert.assertTrue(!it.hasNext());
        it = empty.rangeIterator(RestrictionType.NONE);
        Assert.assertTrue(!it.hasNext());
    }

    @Test
    public void testIsEmpty() {
        Assert.assertTrue(!tally.isEmpty());
        Assert.assertTrue(empty.isEmpty());
    }

    @Test
    public void testCountVerses() {
        Assert.assertEquals(tally.countVerses(), 6);
        Assert.assertEquals(empty.countVerses(), 0);
    }

    @Test
    public void testCountRanges() {
        Assert.assertEquals(6, tally.countRanges(RestrictionType.NONE));
        Assert.assertEquals(0, empty.countRanges(RestrictionType.NONE));
    }

    @Test
    public void testContainsVerse() {
        Assert.assertTrue(!empty.contains(genC1V1));
        Assert.assertTrue(tally.contains(genC1V1));
        Assert.assertTrue(!tally.contains(genC1V2));
        Assert.assertTrue(tally.contains(genC1V3));
        Assert.assertTrue(tally.contains(genC1V5));
    }

    @Test
    public void testContainsVerseRange() {
        Assert.assertTrue(!empty.contains(genC1V1r));
        Assert.assertTrue(tally.contains(genC1V1r));
        Assert.assertTrue(!tally.contains(genC1V12r));
        Assert.assertTrue(!tally.contains(genC1V2r));
        Assert.assertTrue(!tally.contains(exoC2V1r));
    }

    @Test
    public void testAdd() throws Exception {
        temp = tally.clone();
        temp.add(VerseFactory.fromString(v11n, "Gen 1:2"));
        Assert.assertEquals("Gen 1:1, 5, 2, 3, 7, 2:1, 3:1", temp.getName());
        temp.add(VerseFactory.fromString(v11n, "Gen 1:4"));
        Assert.assertEquals("Gen 1:1, 5, 2, 3, 4, 7, 2:1, 3:1", temp.getName());
        temp = tally.clone();
        temp.add(VerseRangeFactory.fromString(v11n, "Gen 1:2-4"));
        Assert.assertEquals("Gen 1:1, 3, 5, 2, 4, 7, 2:1, 3:1", temp.getName());
        try {
            temp.addAll((Key) null);
            Assert.fail();
        } catch (NullPointerException ex) {
            // This is allowed
        }
    }

    @Test
    public void testUnAdd() throws Exception {
        temp = tally.clone();
        temp.unAdd(VerseFactory.fromString(v11n, "Gen 1:5"));
        Assert.assertEquals("Gen 1:1, 3, 5, 7, 2:1, 3:1", temp.getName());
        temp.unAdd(VerseFactory.fromString(v11n, "Gen 1:5"));
        Assert.assertEquals("Gen 1:1, 3, 7, 2:1, 3:1", temp.getName());
        temp.unAdd(VerseFactory.fromString(v11n, "Gen 1:5"));
        Assert.assertEquals("Gen 1:1, 3, 7, 2:1, 3:1", temp.getName());
    }

    @Test
    public void testAddAll() throws Exception {
        temp = tally.clone();
        temp.addAll(keyf.getKey(v11n, "Gen 1:2, Gen 1:4"));
        Assert.assertEquals("Gen 1:1, 5, 2, 3, 4, 7, 2:1, 3:1", temp.getName());
    }

    @Test
    public void testClear() {
        temp = tally.clone();
        temp.clear();
        Assert.assertEquals("", temp.getName());
        temp.clear();
        Assert.assertEquals("", temp.getName());
        temp = empty.clone();
        temp.clear();
        Assert.assertEquals("", temp.getName());
    }

    @Test
    public void testBlur() {
        temp = tally.clone();
        temp.blur(1, RestrictionType.NONE);
        Assert.assertEquals("Gen 1:1 (100%), Gen 1:2 (100%), Gen 1:0 (75%), Gen 1:4 (75%), Gen 1:5 (75%), Gen 1:6 (75%), Gen 1:3 (50%), Gen 1:7 (50%), Gen 2:1 (50%), Gen 3:1 (50%), Gen 1:8 (25%), Gen 2:0 (25%), Gen 2:2 (25%), Gen 3:0 (25%), Gen 3:2 (25%)",
                temp.getNameAndTally());
    }

    @Test
    public void testFlatten() {
        temp = tally.clone();
        temp.flatten();
        Assert.assertEquals(temp.getName(), "Gen 1:1, 3, 5, 7, 2:1, 3:1");
    }

    @Test
    public void testObject() throws Exception {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(bout);
        objOut.writeObject(tally);
        objOut.writeObject(empty);
        objOut.close();
        objOut = null;
        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
        ObjectInputStream objIn = new ObjectInputStream(bin);
        Passage genC1V135Copy = (Passage) objIn.readObject();
        Passage exo2a3bCopy = (Passage) objIn.readObject();
        objIn.close();
        Assert.assertEquals(tally, genC1V135Copy);
        Assert.assertEquals(empty, exo2a3bCopy);
    }
}
