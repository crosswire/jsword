
package org.crosswire.jsword.passage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;

import junit.framework.TestCase;

/**
 * JUnit Test.
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class PassageTally2Test extends TestCase
{
    public PassageTally2Test(String s)
    {
        super(s);
    }

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

    PassageTally tally = new PassageTally();
    PassageTally empty = new PassageTally();
    PassageTally temp = null;

    protected void setUp() throws Exception
    {
        gen11_1 = new VerseRange(new Verse(1, 1, 1), 1);
        gen11_2 = new VerseRange(new Verse(1, 1, 1), 2);
        gen12_1 = new VerseRange(new Verse(1, 1, 2), 1);
        exo21_1 = new VerseRange(new Verse(2, 2, 1), 1);
        exo21_2 = new VerseRange(new Verse(2, 2, 1), 2);
        exo22_1 = new VerseRange(new Verse(2, 2, 2), 1);

        gen11 = new Verse(1, 1, 1);
        gen12 = new Verse(1, 1, 2);
        gen13 = new Verse(1, 1, 3);
        gen15 = new Verse(1, 1, 5);
        exo21 = new Verse(2, 2, 1);
        exo22 = new Verse(2, 2, 2);
        exo23 = new Verse(2, 2, 3);
        exo3b = new Verse(2, 3, 11);

        gen1_135 = PassageFactory.createPassage("Gen 1:1, Gen 1:3, Gen 1:5");
        gen123_1 = PassageFactory.createPassage("Gen 1:1, Gen 2:1, Gen 3:1");
        gen1_157 = PassageFactory.createPassage("Gen 1:1, Gen 1:5, Gen 1:7");

        tally.setOrdering(PassageTally.ORDER_TALLY);
        empty.setOrdering(PassageTally.ORDER_TALLY);

        tally.addAll(gen1_135);
        tally.addAll(gen123_1);
        tally.addAll(gen1_157);
    }

    protected void tearDown()
    {
    }

    public void testGetName() throws Exception
    {
        assertEquals(tally.getName(0), "Gen 1:1, 5, 3, 7, 2:1, 3:1");
        assertEquals(tally.getName(1), "Gen 1:1");
        assertEquals(tally.getName(2), "Gen 1:1, 5");
        assertEquals(tally.getName(3), "Gen 1:1, 5, 3");
        assertEquals(tally.getName(4), "Gen 1:1, 5, 3, 7");
        assertEquals(tally.getName(5), "Gen 1:1, 5, 3, 7, 2:1");
        assertEquals(tally.getName(6), "Gen 1:1, 5, 3, 7, 2:1, 3:1");
        assertEquals(tally.getName(7), "Gen 1:1, 5, 3, 7, 2:1, 3:1");
        assertEquals(tally.getName(8), "Gen 1:1, 5, 3, 7, 2:1, 3:1");
        assertEquals(tally.getName(9), "Gen 1:1, 5, 3, 7, 2:1, 3:1");
        assertEquals(tally.getName(10), "Gen 1:1, 5, 3, 7, 2:1, 3:1");
        assertEquals(empty.getName(0), "");
        assertEquals(empty.getName(1), "");
        assertEquals(empty.getName(2), "");
        assertEquals(empty.getName(3), "");
        assertEquals(empty.getName(4), "");
        assertEquals(empty.getName(5), "");
        assertEquals(empty.getName(6), "");
        assertEquals(empty.getName(7), "");
        assertEquals(empty.getName(8), "");
        assertEquals(empty.getName(9), "");
        assertEquals(empty.getName(10), "");
    }

    public void testToString() throws Exception
    {
        assertEquals(tally.toString(), "Gen 1:1, 5, 3, 7, 2:1, 3:1");
        assertEquals(empty.toString(), "");
    }

    public void testGetOrderedNameAndTally() throws Exception
    {
        assertEquals(tally.getNameAndTally(), "Gen 1:1 (100%), Gen 1:5 (66%), Gen 1:3 (33%), Gen 1:7 (33%), Gen 2:1 (33%), Gen 3:1 (33%)");
        assertEquals(empty.getNameAndTally(), "");
    }

    public void testAddPassageListener() throws Exception
    {
        FixturePassageListener li = new FixturePassageListener();
        temp = (PassageTally) tally.clone();
        temp.addPassageListener(li);
        assertTrue(li.check(0, 0, 0));
        temp.add(new Verse("Gen 1:7"));
        assertTrue(li.check(1, 0, 0));
        temp.add(new Verse("Gen 1:9"));
        assertTrue(li.check(2, 0, 0));
        temp.removePassageListener(li);
        temp.add(new Verse("Gen 1:11"));
        assertTrue(li.check(2, 0, 0));
    }

    public void testClone() throws Exception
    {
        assertTrue(tally != tally.clone());
        assertEquals(tally, tally.clone());
        assertTrue(empty != empty.clone());
        assertEquals(empty, empty.clone());
    }

    public void testVerseIterator() throws Exception
    {
        Iterator it = tally.verseIterator();
        assertTrue(it.hasNext());
        assertEquals(it.next(), new Verse("Gen 1:1"));
        assertTrue(it.hasNext());
        assertEquals(it.next(), new Verse("Gen 1:5"));
        assertTrue(it.hasNext());
        assertEquals(it.next(), new Verse("Gen 1:3"));
        assertTrue(it.hasNext());
        assertEquals(it.next(), new Verse("Gen 1:7"));
        assertTrue(it.hasNext());
        assertEquals(it.next(), new Verse("Gen 2:1"));
        assertTrue(it.hasNext());
        assertEquals(it.next(), new Verse("Gen 3:1"));
        assertTrue(!it.hasNext());
        it = empty.verseIterator();
        assertTrue(!it.hasNext());
    }

    public void testRangeIterator() throws Exception
    {
        Iterator it = tally.rangeIterator();
        assertTrue(it.hasNext());
        assertEquals(it.next(), new VerseRange("Gen 1:1"));
        assertTrue(it.hasNext());
        assertEquals(it.next(), new VerseRange("Gen 1:5"));
        assertTrue(it.hasNext());
        assertEquals(it.next(), new VerseRange("Gen 1:3"));
        assertTrue(it.hasNext());
        assertEquals(it.next(), new VerseRange("Gen 1:7"));
        assertTrue(it.hasNext());
        assertEquals(it.next(), new VerseRange("Gen 2:1"));
        assertTrue(it.hasNext());
        assertEquals(it.next(), new VerseRange("Gen 3:1"));
        assertTrue(!it.hasNext());
        it = empty.rangeIterator();
        assertTrue(!it.hasNext());
    }

    public void testIsEmpty() throws Exception
    {
        assertTrue(!tally.isEmpty());
        assertTrue(empty.isEmpty());
    }

    public void testCountVerses() throws Exception
    {
        assertEquals(tally.countVerses(), 6);
        assertEquals(empty.countVerses(), 0);
    }

    public void testCountRanges() throws Exception
    {
        assertEquals(tally.countRanges(), 6);
        assertEquals(empty.countRanges(), 0);
    }

    public void testContainsVerse() throws Exception
    {
        assertTrue(!empty.contains(gen11));
        assertTrue(tally.contains(gen11));
        assertTrue(!tally.contains(gen12));
        assertTrue(tally.contains(gen13));
        assertTrue(tally.contains(gen15));
    }

    public void testContainsVerseRange() throws Exception
    {
        assertTrue(!empty.contains(gen11_1));
        assertTrue(tally.contains(gen11_1));
        assertTrue(!tally.contains(gen11_2));
        assertTrue(!tally.contains(gen12_1));
        assertTrue(!tally.contains(exo21_1));
    }

    public void testAdd() throws Exception
    {
        temp = (PassageTally) tally.clone();
        temp.add(new Verse("Gen 1:2"));
        assertEquals(temp.getName(), "Gen 1:1, 5, 2, 3, 7, 2:1, 3:1");
        temp.add(new Verse("Gen 1:4"));
        assertEquals(temp.getName(), "Gen 1:1, 5, 2, 3, 4, 7, 2:1, 3:1");
        temp = (PassageTally) tally.clone();
        temp.add(new VerseRange("Gen 1:2-4"));
        assertEquals(temp.getName(), "Gen 1:1, 3, 5, 2, 4, 7, 2:1, 3:1");
        try { temp.add(null); fail(); }
        catch (NullPointerException ex) { }
    }

    public void testUnAdd() throws Exception
    {
        temp = (PassageTally) tally.clone();
        temp.unAdd(new Verse("Gen 1:5"));
        assertEquals(temp.getName(), "Gen 1:1, 3, 5, 7, 2:1, 3:1");
        temp.unAdd(new Verse("Gen 1:5"));
        assertEquals(temp.getName(), "Gen 1:1, 3, 7, 2:1, 3:1");
        temp.unAdd(new Verse("Gen 1:5"));
        assertEquals(temp.getName(), "Gen 1:1, 3, 7, 2:1, 3:1");
    }

    public void testAddAll() throws Exception
    {
        temp = (PassageTally) tally.clone();
        temp.addAll(PassageFactory.createPassage("Gen 1:2, Gen 1:4"));
        assertEquals(temp.getName(), "Gen 1:1, 5, 2, 3, 4, 7, 2:1, 3:1");
    }

    public void testClear() throws Exception
    {
        temp = (PassageTally) tally.clone();
        temp.clear();
        assertEquals(temp.getName(), "");
        temp.clear();
        assertEquals(temp.getName(), "");
        temp = (PassageTally) empty.clone();
        temp.clear();
        assertEquals(temp.getName(), "");
    }

    public void testBlur() throws Exception
    {
        temp = (PassageTally) tally.clone();
        temp.blur(1, PassageConstants.RESTRICT_NONE);
        assertEquals(temp.getNameAndTally(), "Gen 1:1 (100%), Gen 1:2 (100%), Gen 1:4 (75%), Gen 1:5 (75%), Gen 1:6 (75%), Gen 1:3 (50%), Gen 1:7 (50%), Gen 2:1 (50%), Gen 3:1 (50%), Gen 1:8 (25%), Gen 1:31 (25%), Gen 2:2 (25%), Gen 2:25 (25%), Gen 3:2 (25%)");
        //temp = (PassageTally) tally.clone();
        //temp.blur(1, Verse.RESTRICT_CHAPTER);
        //assertEquals(temp.getOrderedNameAndTally(), "Gen 1:1, 3, 5, 7, 2:1, 3:1");
    }

    public void testFlatten() throws Exception
    {
        temp = (PassageTally) tally.clone();
        temp.flatten();
        assertEquals(temp.getName(), "Gen 1:1, 3, 5, 7, 2:1, 3:1");
    }

    public void testObject() throws Exception
    {
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
        assertEquals(gen1_135_copy, tally);
        assertEquals(exo2a_3b_copy, empty);
    }
}
