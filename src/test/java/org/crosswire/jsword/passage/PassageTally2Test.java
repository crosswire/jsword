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
 * Copyright: 2005 - 2014
 *     The copyright to this program is held by its authors.
 *
 */
package org.crosswire.jsword.passage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

    VerseRange gen11_1 = null;
    VerseRange gen11_2 = null;
    VerseRange gen12_1 = null;
    VerseRange exo21_1 = null;
    VerseRange exo21_2 = null;
    VerseRange exo22_1 = null;

    Verse gen11 = null;
    Verse gen12 = null;
    Verse gen13 = null;
    Verse gen15 = null;
    Verse exo21 = null;
    Verse exo22 = null;
    Verse exo23 = null;
    Verse exo3b = null;

    Passage gen1_135 = null;
    Passage gen123_1 = null;
    Passage gen1_157 = null;

    // AV11N(DMS): Update test to test all V11Ns
    PassageTally tally = new PassageTally(Versifications.instance().getVersification("KJV"));
    PassageTally empty = new PassageTally(Versifications.instance().getVersification("KJV"));
    PassageTally temp = null;

    @Before
    public void setUp() throws Exception {
        storedCase = BookName.getDefaultCase();
        BookName.setCase(CaseType.SENTENCE);
        fullName = BookName.isFullBookName();
        BookName.setFullBookName(false);
        // AV11N(DMS): Update test to test all V11Ns
        v11n = Versifications.instance().getVersification("KJV");

        gen11_1 = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 1, 1), 1);
        gen11_2 = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 1, 1), 2);
        gen12_1 = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 1, 2), 1);
        exo21_1 = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.EXOD, 2, 1), 1);
        exo21_2 = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.EXOD, 2, 1), 2);
        exo22_1 = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.EXOD, 2, 2), 1);

        gen11 = new Verse(v11n, BibleBook.GEN, 1, 1);
        gen12 = new Verse(v11n, BibleBook.GEN, 1, 2);
        gen13 = new Verse(v11n, BibleBook.GEN, 1, 3);
        gen15 = new Verse(v11n, BibleBook.GEN, 1, 5);
        exo21 = new Verse(v11n, BibleBook.EXOD, 2, 1);
        exo22 = new Verse(v11n, BibleBook.EXOD, 2, 2);
        exo23 = new Verse(v11n, BibleBook.EXOD, 2, 3);
        exo3b = new Verse(v11n, BibleBook.EXOD, 3, 11);

        gen1_135 = keyf.getKey(v11n, "Gen 1:1, Gen 1:3, Gen 1:5");
        gen123_1 = keyf.getKey(v11n, "Gen 1:1, Gen 2:1, Gen 3:1");
        gen1_157 = keyf.getKey(v11n, "Gen 1:1, Gen 1:5, Gen 1:7");

        tally.setOrdering(PassageTally.Order.TALLY);
        empty.setOrdering(PassageTally.Order.TALLY);

        tally.addAll(gen1_135);
        tally.addAll(gen123_1);
        tally.addAll(gen1_157);
    }

    @After
    public void tearDown() {
        BookName.setCase(storedCase);
        BookName.setFullBookName(fullName);
    }

    @Test
    public void testGetName() {
        assertEquals("Gen 1:1, 5, 3, 7, 2:1, 3:1", tally.getName(0));
        assertEquals("Gen 1:1", tally.getName(1));
        assertEquals("Gen 1:1, 5", tally.getName(2));
        assertEquals("Gen 1:1, 5, 3", tally.getName(3));
        assertEquals("Gen 1:1, 5, 3, 7", tally.getName(4));
        assertEquals("Gen 1:1, 5, 3, 7, 2:1", tally.getName(5));
        assertEquals("Gen 1:1, 5, 3, 7, 2:1, 3:1", tally.getName(6));
        assertEquals("Gen 1:1, 5, 3, 7, 2:1, 3:1", tally.getName(7));
        assertEquals("Gen 1:1, 5, 3, 7, 2:1, 3:1", tally.getName(8));
        assertEquals("Gen 1:1, 5, 3, 7, 2:1, 3:1", tally.getName(9));
        assertEquals("Gen 1:1, 5, 3, 7, 2:1, 3:1", tally.getName(10));
        assertEquals("", empty.getName(0));
        assertEquals("", empty.getName(1));
        assertEquals("", empty.getName(2));
        assertEquals("", empty.getName(3));
        assertEquals("", empty.getName(4));
        assertEquals("", empty.getName(5));
        assertEquals("", empty.getName(6));
        assertEquals("", empty.getName(7));
        assertEquals("", empty.getName(8));
        assertEquals("", empty.getName(9));
        assertEquals("", empty.getName(10));
    }

    @Test
    public void testToString() {
        assertEquals("Gen 1:1, 5, 3, 7, 2:1, 3:1", tally.toString());
        assertEquals("", empty.toString());
    }

    @Test
    public void testGetOrderedNameAndTally() {
        assertEquals("Gen 1:1 (100%), Gen 1:5 (66%), Gen 1:3 (33%), Gen 1:7 (33%), Gen 2:1 (33%), Gen 3:1 (33%)", tally.getNameAndTally());
        assertEquals("", empty.getNameAndTally());
    }

    @Test
    public void testAddPassageListener() throws Exception {
        FixturePassageListener li = new FixturePassageListener();
        temp = tally.clone();
        temp.addPassageListener(li);
        assertTrue(li.check(0, 0, 0));
        temp.add(VerseFactory.fromString(v11n, "Gen 1:7"));
        assertTrue(li.check(1, 0, 0));
        temp.add(VerseFactory.fromString(v11n, "Gen 1:9"));
        assertTrue(li.check(2, 0, 0));
        temp.removePassageListener(li);
        temp.add(VerseFactory.fromString(v11n, "Gen 1:11"));
        assertTrue(li.check(2, 0, 0));
    }

    @Test
    public void testClone() {
        assertTrue(tally != tally.clone());
        assertEquals(tally, tally.clone());
        assertTrue(empty != empty.clone());
        assertEquals(empty, empty.clone());
    }

    @Test
    public void testVerseIterator() throws Exception {
        Iterator<Key> it = tally.iterator();
        assertTrue(it.hasNext());
        assertEquals(VerseFactory.fromString(v11n, "Gen 1:1"), it.next());
        assertTrue(it.hasNext());
        assertEquals(VerseFactory.fromString(v11n, "Gen 1:5"), it.next());
        assertTrue(it.hasNext());
        assertEquals(VerseFactory.fromString(v11n, "Gen 1:3"), it.next());
        assertTrue(it.hasNext());
        assertEquals(VerseFactory.fromString(v11n, "Gen 1:7"), it.next());
        assertTrue(it.hasNext());
        assertEquals(VerseFactory.fromString(v11n, "Gen 2:1"), it.next());
        assertTrue(it.hasNext());
        assertEquals(VerseFactory.fromString(v11n, "Gen 3:1"), it.next());
        assertTrue(!it.hasNext());
        it = empty.iterator();
        assertTrue(!it.hasNext());
    }

    @Test
    public void testRangeIterator() throws Exception {
        Iterator<VerseRange> it = tally.rangeIterator(RestrictionType.NONE);
        assertTrue(it.hasNext());
        assertEquals(VerseRangeFactory.fromString(v11n, "Gen 1:1"), it.next());
        assertTrue(it.hasNext());
        assertEquals(VerseRangeFactory.fromString(v11n, "Gen 1:5"), it.next());
        assertTrue(it.hasNext());
        assertEquals(VerseRangeFactory.fromString(v11n, "Gen 1:3"), it.next());
        assertTrue(it.hasNext());
        assertEquals(VerseRangeFactory.fromString(v11n, "Gen 1:7"), it.next());
        assertTrue(it.hasNext());
        assertEquals(VerseRangeFactory.fromString(v11n, "Gen 2:1"), it.next());
        assertTrue(it.hasNext());
        assertEquals(VerseRangeFactory.fromString(v11n, "Gen 3:1"), it.next());
        assertTrue(!it.hasNext());
        it = empty.rangeIterator(RestrictionType.NONE);
        assertTrue(!it.hasNext());
    }

    @Test
    public void testIsEmpty() {
        assertTrue(!tally.isEmpty());
        assertTrue(empty.isEmpty());
    }

    @Test
    public void testCountVerses() {
        assertEquals(tally.countVerses(), 6);
        assertEquals(empty.countVerses(), 0);
    }

    @Test
    public void testCountRanges() {
        assertEquals(6, tally.countRanges(RestrictionType.NONE));
        assertEquals(0, empty.countRanges(RestrictionType.NONE));
    }

    @Test
    public void testContainsVerse() {
        assertTrue(!empty.contains(gen11));
        assertTrue(tally.contains(gen11));
        assertTrue(!tally.contains(gen12));
        assertTrue(tally.contains(gen13));
        assertTrue(tally.contains(gen15));
    }

    @Test
    public void testContainsVerseRange() {
        assertTrue(!empty.contains(gen11_1));
        assertTrue(tally.contains(gen11_1));
        assertTrue(!tally.contains(gen11_2));
        assertTrue(!tally.contains(gen12_1));
        assertTrue(!tally.contains(exo21_1));
    }

    @Test
    public void testAdd() throws Exception {
        temp = tally.clone();
        temp.add(VerseFactory.fromString(v11n, "Gen 1:2"));
        assertEquals("Gen 1:1, 5, 2, 3, 7, 2:1, 3:1", temp.getName());
        temp.add(VerseFactory.fromString(v11n, "Gen 1:4"));
        assertEquals("Gen 1:1, 5, 2, 3, 4, 7, 2:1, 3:1", temp.getName());
        temp = tally.clone();
        temp.add(VerseRangeFactory.fromString(v11n, "Gen 1:2-4"));
        assertEquals("Gen 1:1, 3, 5, 2, 4, 7, 2:1, 3:1", temp.getName());
        try {
            temp.addAll((Key) null);
            fail();
        } catch (NullPointerException ex) {
        }
    }

    @Test
    public void testUnAdd() throws Exception {
        temp = tally.clone();
        temp.unAdd(VerseFactory.fromString(v11n, "Gen 1:5"));
        assertEquals("Gen 1:1, 3, 5, 7, 2:1, 3:1", temp.getName());
        temp.unAdd(VerseFactory.fromString(v11n, "Gen 1:5"));
        assertEquals("Gen 1:1, 3, 7, 2:1, 3:1", temp.getName());
        temp.unAdd(VerseFactory.fromString(v11n, "Gen 1:5"));
        assertEquals("Gen 1:1, 3, 7, 2:1, 3:1", temp.getName());
    }

    @Test
    public void testAddAll() throws Exception {
        temp = tally.clone();
        temp.addAll(keyf.getKey(v11n, "Gen 1:2, Gen 1:4"));
        assertEquals("Gen 1:1, 5, 2, 3, 4, 7, 2:1, 3:1", temp.getName());
    }

    @Test
    public void testClear() {
        temp = tally.clone();
        temp.clear();
        assertEquals("", temp.getName());
        temp.clear();
        assertEquals("", temp.getName());
        temp = empty.clone();
        temp.clear();
        assertEquals("", temp.getName());
    }

    @Test
    public void testBlur() {
        temp = tally.clone();
        temp.blur(1, RestrictionType.NONE);
        assertEquals("Gen 1:1 (100%), Gen 1:2 (100%), Gen 1:0 (75%), Gen 1:4 (75%), Gen 1:5 (75%), Gen 1:6 (75%), Gen 1:3 (50%), Gen 1:7 (50%), Gen 2:1 (50%), Gen 3:1 (50%), Gen 1:8 (25%), Gen 2:0 (25%), Gen 2:2 (25%), Gen 3:0 (25%), Gen 3:2 (25%)",
                temp.getNameAndTally());
    }

    @Test
    public void testFlatten() {
        temp = tally.clone();
        temp.flatten();
        assertEquals(temp.getName(), "Gen 1:1, 3, 5, 7, 2:1, 3:1");
    }

    @Test
    public void testObject() throws Exception {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream obj_out = new ObjectOutputStream(bout);
        obj_out.writeObject(tally);
        obj_out.writeObject(empty);
        obj_out.close();
        obj_out = null;
        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
        ObjectInputStream obj_in = new ObjectInputStream(bin);
        Passage gen1_135_copy = (Passage) obj_in.readObject();
        Passage exo2a_3b_copy = (Passage) obj_in.readObject();
        obj_in.close();
        assertEquals(tally, gen1_135_copy);
        assertEquals(empty, exo2a_3b_copy);
    }
}
