
package org.crosswire.jsword.passage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
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
public class TestPassage extends TestCase
{
    public TestPassage(String s)
    {
        super(s);
    }

    public TestPassage(String s, int ptype, boolean optimize)
    {
        super(s);

        PassageFactory.setDefaultPassage(ptype);
        this.optimize = optimize;
    }

    boolean optimize = false;
    long start = System.currentTimeMillis();

    Passage gen1_135 = null;
    Passage exo2a_3b = null;
    Passage gen_rev = null;
    Passage grace = null;
    Passage empty = null;
    Passage temp = null;

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
    Verse rev99 = null;

    /**
     * Some of these tests fail with an out of memory exception. The optimize
     * bit of these tests is wrong so we never optimize for write, and
     * performing tests aimed at writing after optimizing for reads is allowed
     * to be a bad performer (which includes using lots of memory) So the
     * problem is with the tests and not with the Passages.
     */
    protected void setUp() throws Exception
    {
        start = System.currentTimeMillis();

        gen1_135 = PassageFactory.createPassage("Gen 1:1, Gen 1:3, Gen 1:5");
        exo2a_3b = PassageFactory.createPassage("Exo 2:1-10, Exo 3:1-11");
        gen_rev = PassageFactory.createPassage("Gen 1:1-Rev 22:21");
        grace = PassageFactory.createPassage();
        grace.addAll(PassageFactory.createPassage("Gen 6:8, 19:19, 32:5, 33:8, 10, 15, 39:4, 47:25, 29, 50:4, Exo 33:12-13, 16-17, 34:9, Num 32:5, Judg 6:17, Rut 2:2, 10, 1Sa 1:18, 20:3, 27:5"));
        grace.addAll(PassageFactory.createPassage("2Sa 14:22, 16:4, Ezr 9:8, Est 2:17, Psa 45:2, 84:11, Pro 1:9, 3:22, 34, 4:9, 22:11, Jer 31:2, Zec 4:7, 12:10, Luk 2:40, Joh 1:14, 16-17"));
        grace.addAll(PassageFactory.createPassage("Act 4:33, 11:23, 13:43, 14:3, 26, 15:11, 40, 18:27, 20:24, 32, Rom 1:5, 7, 3:24, 4:4, 16, 5:2, 15, 17, 20-6:1, 6:14-15, 11:5-6, 12:3, 6, 15:15, 16:20, 24"));
        grace.addAll(PassageFactory.createPassage("1Co 1:3-4, 3:10, 10:30, 15:10, 16:23, 2Co 1:2, 12, 4:15, 6:1, 8:1, 6-7, 9, 19, 9:8, 14, 12:9, 13:14, Gal 1:3, 6, 15, 2:9, 21, 5:4, 6:18"));
        grace.addAll(PassageFactory.createPassage("Eph 1:2, 6-7, 2:5, 7-8, 3:2, 7-8, 4:7, 29, 6:24, Phili 1:2, 7, 4:23, Col 1:2, 6, 3:16, 4:6, 18-1Th 1:1, 1Th 5:28, 2Th 1:2, 12, 2:16, 3:18"));
        grace.addAll(PassageFactory.createPassage("1Ti 1:2, 14, 6:21, 2Ti 1:2, 9, 2:1, 4:22, Tit 1:4, 2:11, 3:7, 15, Phile 3, 25, Heb 2:9, 4:16, 10:29, 12:15, 28, 13:9, 25, Jam 1:11, 4:6"));
        grace.addAll(PassageFactory.createPassage("1Pe 1:2, 10, 13, 3:7, 4:10, 5:5, 10, 12, 2Pe 1:2, 3:18, 2Jo 3, Jude 4, Rev 1:4, 22:21"));
        empty = PassageFactory.createPassage();

        // String full_type = empty.getClass().getName();
        // String type = full_type.substring(full_type.lastIndexOf(".")+1);
        // boolean skip = type.equals("PassageTally");

        if (optimize)
        {
            gen1_135.optimizeReads();
            exo2a_3b.optimizeReads();
            gen_rev.optimizeReads();
            grace.optimizeReads();
            empty.optimizeReads();
        }

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
        rev99 = new Verse("Rev 22:21");
    }

    protected void tearDown()
    {
        // float secs = (System.currentTimeMillis() - start) / 1000F;
        // log(type+" total = "+secs+"s =======================");

        PassageFactory.setDefaultPassage(PassageFactory.SPEED);
    }

    public void testReadAddPassageListener() throws Exception
    {
        for (int i=0; i<300; i++)
        {
            TestPassageListener li1 = new TestPassageListener();
            TestPassageListener li2 = new TestPassageListener();
            temp = (Passage) gen1_135.clone();
            temp.addPassageListener(li1);
            temp.addPassageListener(li2);
            assertTrue(li1.check(0, 0, 0));
            assertTrue(li2.check(0, 0, 0));
            temp.add(new Verse("Gen 1:7"));
            assertTrue(li1.check(1, 0, 0));
            assertTrue(li2.check(1, 0, 0));
            temp.add(new Verse("Gen 1:9"));
            assertTrue(li1.check(2, 0, 0));
            assertTrue(li2.check(2, 0, 0));
            temp.removePassageListener(li1);
            temp.add(new Verse("Gen 1:11"));
            assertTrue(li1.check(2, 0, 0));
            assertTrue(li2.check(3, 0, 0));
        }
    }

    public void testReadRangeIterator() throws Exception
    {
        // We used to check for a UnsupportedOperationException until DistinctPassage started
        // throwing IllegalStateExceptions here. I'm not too bothered about the exact type of the
        // exception right now. Maybe we ought to be more thorough at some stage.
        for (int i=0; i<12; i++)
        {
            Iterator it = gen1_135.rangeIterator();
            assertTrue(it.hasNext());
            assertEquals(it.next(), new VerseRange("Gen 1:1"));
            assertTrue(it.hasNext());
            assertEquals(it.next(), new VerseRange("Gen 1:3"));
            assertTrue(it.hasNext());
            assertEquals(it.next(), new VerseRange("Gen 1:5"));
            assertTrue(!it.hasNext());
            it = empty.rangeIterator();
            assertTrue(!it.hasNext());
        }
    }

    public void testReadVerseIterator() throws Exception
    {
        for (int i=0; i<12; i++)
        {
            Iterator it = gen1_135.verseIterator();
            assertTrue(it.hasNext());
            assertEquals(it.next(), new Verse("Gen 1:1"));
            assertTrue(it.hasNext());
            assertEquals(it.next(), new Verse("Gen 1:3"));
            assertTrue(it.hasNext());
            assertEquals(it.next(), new Verse("Gen 1:5"));
            assertTrue(!it.hasNext());
            it = empty.verseIterator();
            assertTrue(!it.hasNext());
        }
    }

    public void testReadIsEmpty() throws Exception
    {
        for (int i=0; i<20; i++)
        {
            assertTrue(!gen1_135.isEmpty());
            assertTrue(!exo2a_3b.isEmpty());
            assertTrue(empty.isEmpty());
        }
    }

    public void testReadCountVerses() throws Exception
    {
        for (int i=0; i<12; i++)
        {
            assertEquals(gen1_135.countVerses(), 3);
            assertEquals(exo2a_3b.countVerses(), 21);
            assertEquals(empty.countVerses(), 0);
        }
    }

    public void testReadCountRanges() throws Exception
    {
        for (int i=0; i<10; i++)
        {
            assertEquals(gen1_135.countRanges(), 3);
            assertEquals(exo2a_3b.countRanges(), 2);
            assertEquals(empty.countVerses(), 0);
        }
    }

    public void testReadGetVerseAt() throws Exception
    {
        for (int i=0; i<10; i++)
        {
            assertEquals(gen1_135.getVerseAt(0), gen11);
            assertEquals(gen1_135.getVerseAt(1), gen13);
            assertEquals(gen1_135.getVerseAt(2), gen15);
            assertEquals(exo2a_3b.getVerseAt(0), exo21);
            assertEquals(exo2a_3b.getVerseAt(1), exo22);
            assertEquals(exo2a_3b.getVerseAt(2), exo23);
            assertEquals(exo2a_3b.getVerseAt(20), exo3b);
        }
    }

    public void testReadGetVerseRangeAt() throws Exception
    {
        for (int i=0; i<5; i++)
        {
            assertEquals(gen1_135.getVerseRangeAt(0), gen11_1);
            assertEquals(gen1_135.getVerseRangeAt(1), new VerseRange("Gen 1:3"));
            assertEquals(gen1_135.getVerseRangeAt(2), new VerseRange("Gen 1:5"));
            assertEquals(exo2a_3b.getVerseRangeAt(0), new VerseRange("Exo 2:1-10"));
            assertEquals(exo2a_3b.getVerseRangeAt(1), new VerseRange("Exo 3:1-11"));
        }
    }

    public void testReadBooksInPassage() throws Exception
    {
        for (int i=0; i<12; i++)
        {
            assertEquals(gen1_135.booksInPassage(), 1);
            assertEquals(exo2a_3b.booksInPassage(), 1);
        }
    }

    public void testReadChaptersInPassage() throws Exception
    {
        for (int i=0; i<3; i++)
        {
            assertEquals(gen1_135.chaptersInPassage(1), 1);
            assertEquals(exo2a_3b.chaptersInPassage(1), 0);
            assertEquals(gen1_135.chaptersInPassage(2), 0);
            assertEquals(exo2a_3b.chaptersInPassage(2), 2);
            assertEquals(gen1_135.chaptersInPassage(3), 0);
            assertEquals(exo2a_3b.chaptersInPassage(3), 0);
            assertEquals(gen1_135.chaptersInPassage(0), 1);
            assertEquals(exo2a_3b.chaptersInPassage(0), 2);
        }
    }

    public void testReadVersesInPassage() throws Exception
    {
        for (int i=0; i<1; i++)
        {
            assertEquals(gen1_135.versesInPassage(1, 1), 3);
            assertEquals(exo2a_3b.versesInPassage(1, 1), 0);
            assertEquals(gen1_135.versesInPassage(1, 2), 0);
            assertEquals(exo2a_3b.versesInPassage(1, 2), 0);
            assertEquals(gen1_135.versesInPassage(2, 1), 0);
            assertEquals(exo2a_3b.versesInPassage(2, 1), 0);
            assertEquals(gen1_135.versesInPassage(2, 2), 0);
            assertEquals(exo2a_3b.versesInPassage(2, 2), 10);
            assertEquals(gen1_135.versesInPassage(2, 3), 0);
            assertEquals(exo2a_3b.versesInPassage(2, 3), 11);
            assertEquals(gen1_135.versesInPassage(2, 4), 0);
            assertEquals(exo2a_3b.versesInPassage(2, 4), 0);
            assertEquals(gen1_135.versesInPassage(3, 1), 0);
            assertEquals(exo2a_3b.versesInPassage(3, 1), 0);
            assertEquals(gen1_135.versesInPassage(1, 0), 3);
            assertEquals(exo2a_3b.versesInPassage(1, 0), 0);
            assertEquals(gen1_135.versesInPassage(2, 0), 0);
            assertEquals(exo2a_3b.versesInPassage(2, 0), 21);
            assertEquals(gen1_135.versesInPassage(3, 0), 0);
            assertEquals(exo2a_3b.versesInPassage(3, 0), 0);
            assertEquals(gen1_135.versesInPassage(0, 1), 3);
            assertEquals(exo2a_3b.versesInPassage(0, 1), 0);
            assertEquals(gen1_135.versesInPassage(0, 2), 0);
            assertEquals(exo2a_3b.versesInPassage(0, 2), 10);
            assertEquals(gen1_135.versesInPassage(0, 3), 0);
            assertEquals(exo2a_3b.versesInPassage(0, 3), 11);
            assertEquals(gen1_135.versesInPassage(0, 0), gen1_135.countVerses());
            assertEquals(exo2a_3b.versesInPassage(0, 0), exo2a_3b.countVerses());
        }
    }

    public void testReadContainsVerse() throws Exception
    {
        for (int i=0; i<1200; i++)
        {
            assertTrue(!empty.contains(gen11));
            assertTrue(gen1_135.contains(gen11));
            assertTrue(!gen1_135.contains(gen12));
            assertTrue(gen1_135.contains(gen13));
            assertTrue(gen1_135.contains(gen15));
            assertTrue(gen_rev.contains(gen11));
            assertTrue(gen_rev.contains(gen12));
            assertTrue(gen_rev.contains(rev99));
        }
    }

    public void testReadContainsVerseRange() throws Exception
    {
        for (int i=0; i<600; i++)
        {
            assertTrue(gen1_135.contains(gen11_1));
            assertTrue(!gen1_135.contains(gen11_2));
            assertTrue(!gen1_135.contains(gen12_1));
            assertTrue(!gen1_135.contains(exo21_1));
            assertTrue(!exo2a_3b.contains(gen11_1));
            assertTrue(exo2a_3b.contains(exo21_1));
            assertTrue(exo2a_3b.contains(exo21_2));
            assertTrue(exo2a_3b.contains(exo22_1));
            assertTrue(gen_rev.contains(new VerseRange(rev99)));
        }
    }

    public void testReadContainsAll() throws Exception
    {
        assertTrue(!gen1_135.containsAll(exo2a_3b));
        assertTrue(gen1_135.containsAll(empty));
        assertTrue(gen1_135.containsAll((Passage) gen1_135.clone()));
        assertTrue(!exo2a_3b.containsAll(gen1_135));
        assertTrue(exo2a_3b.containsAll(empty));
        assertTrue(exo2a_3b.containsAll((Passage) exo2a_3b.clone()));
    }

    public void testReadContainsVerseBase() throws Exception
    {
        for (int i=0; i<1000; i++)
        {
            assertTrue(!empty.contains((VerseBase) gen11));
            assertTrue(gen1_135.contains((VerseBase) gen11));
            assertTrue(!gen1_135.contains((VerseBase) gen12));
            assertTrue(gen1_135.contains((VerseBase) gen13));
            assertTrue(gen1_135.contains((VerseBase) gen15));
            assertTrue(gen1_135.contains((VerseBase) gen11_1));
            assertTrue(!gen1_135.contains((VerseBase) gen11_2));
            assertTrue(!gen1_135.contains((VerseBase) gen12_1));
            assertTrue(!gen1_135.contains((VerseBase) exo21_1));
            assertTrue(!exo2a_3b.contains((VerseBase) gen11_1));
            assertTrue(exo2a_3b.contains((VerseBase) exo21_1));
            assertTrue(exo2a_3b.contains((VerseBase) exo21_2));
            assertTrue(exo2a_3b.contains((VerseBase) exo22_1));
        }
    }

    //==========================================================================

    public void testWriteCreatePassage() throws Exception
    {
        assertEquals(PassageFactory.createPassage().countVerses(), 0);
    }

    public void testWriteToString() throws Exception
    {
        assertEquals(PassageFactory.createPassage("gen 1 1,gen 1 3,rev 22 21,gen 1 2").toString(), "Gen 1:1-3, Rev 22:21");
        assertEquals(PassageFactory.createPassage("Gen 1 3;gen 22 2;rev 22 21;gen 22 3-10; rev 22 19;gen 1 1;rev 22 10-18; gen 1 2; rev 22 1-21").toString(), "Gen 1:1-3, 22:2-10, Rev 22");
        assertEquals(PassageFactory.createPassage("").toString(), "");
        assertEquals(PassageFactory.createPassage("gen 1 1-50:26,e,e 1 2,e 1 3-10").toString(), "Gen-Exo");
        try { PassageFactory.createPassage((String) null); fail(); }
        catch (NullPointerException ex) { }
    }

    public void testWriteGetName() throws Exception
    {
        assertEquals(PassageFactory.createPassage("gen 1 1,gen 1 3,rev 22 21,gen 1 2").getName(), "Gen 1:1-3, Rev 22:21");
        assertEquals(PassageFactory.createPassage("Gen 1 3;gen 22 2;rev 22 21;gen 22 3-10; rev 22 19;gen 1 1;rev 22 10-18; gen 1 2; rev 22 1-21").getName(), "Gen 1:1-3, 22:2-10, Rev 22");
        assertEquals(PassageFactory.createPassage("").getName(), "");
        assertEquals(PassageFactory.createPassage("gen 1 1-50:26,e,e 1 2,e 1 3-10").getName(), "Gen-Exo");
        assertEquals(PassageFactory.createPassage("exo 1:1, 4").getName(), "Exo 1:1, 4");
        assertEquals(PassageFactory.createPassage("exo 1:1, 4, 2-3, 11-ff, 6-10").getName(), "Exo 1:1-4, 6-22");
        assertEquals(PassageFactory.createPassage("Num 1, 2").getName(), "Num 1-2");
    }

    public void testWriteBlur() throws Exception
    {
        temp = (Passage) gen1_135.clone();
        temp.blur(0, Verse.RESTRICT_CHAPTER);
        assertEquals(temp, gen1_135);
        temp = (Passage) gen1_135.clone();
        temp.blur(0, Verse.RESTRICT_NONE);
        assertEquals(temp, gen1_135);
        temp = (Passage) gen1_135.clone();
        temp.blur(1, Verse.RESTRICT_CHAPTER);
        assertEquals(temp, PassageFactory.createPassage("Gen 1:1-6"));
        temp = (Passage) gen1_135.clone();
        temp.blur(1, Verse.RESTRICT_NONE);
        assertEquals(temp, PassageFactory.createPassage("Gen 1:1-6"));
        temp = (Passage) gen1_135.clone();
        temp.blur(2, Verse.RESTRICT_CHAPTER);
        assertEquals(temp, PassageFactory.createPassage("Gen 1:1-7"));
        temp = (Passage) gen1_135.clone();
        temp.blur(2, Verse.RESTRICT_NONE);
        assertEquals(temp, PassageFactory.createPassage("Gen 1:1-7"));
        temp = (Passage) gen1_135.clone();
        temp.blur(12, Verse.RESTRICT_CHAPTER);
        assertEquals(temp, PassageFactory.createPassage("Gen 1:1-17"));
        temp = (Passage) gen1_135.clone();
        temp.blur(12, Verse.RESTRICT_NONE);
        assertEquals(temp, PassageFactory.createPassage("Gen 1:1-17"));
        temp = (Passage) gen1_135.clone();
        temp.blur(26, Verse.RESTRICT_CHAPTER);
        assertEquals(temp, PassageFactory.createPassage("Gen 1:1-31"));
        temp = (Passage) gen1_135.clone();
        temp.blur(26, Verse.RESTRICT_NONE);
        assertEquals(temp, PassageFactory.createPassage("Gen 1:1-31"));
        temp = (Passage) gen1_135.clone();
        temp.blur(27, Verse.RESTRICT_CHAPTER);
        assertEquals(temp, PassageFactory.createPassage("Gen 1:1-31"));
        temp = (Passage) gen1_135.clone();
        temp.blur(27, Verse.RESTRICT_NONE);
        assertEquals(temp, PassageFactory.createPassage("Gen 1:1-2:1"));
        temp = (Passage) exo2a_3b.clone();
        temp.blur(0, Verse.RESTRICT_CHAPTER);
        assertEquals(temp, PassageFactory.createPassage("Exo 2:1-10, Exo 3:1-11"));
        temp = (Passage) exo2a_3b.clone();
        temp.blur(0, Verse.RESTRICT_NONE);
        assertEquals(temp, PassageFactory.createPassage("Exo 2:1-10, Exo 3:1-11"));
        temp = (Passage) exo2a_3b.clone();
        temp.blur(1, Verse.RESTRICT_CHAPTER);
        assertEquals(temp, PassageFactory.createPassage("Exo 2:1-11, Exo 3:1-12"));
        temp = (Passage) exo2a_3b.clone();
        temp.blur(1, Verse.RESTRICT_NONE);
        assertEquals(temp, PassageFactory.createPassage("Exo 1:22-2:11, Exo 2:25-3:12"));
        temp = (Passage) exo2a_3b.clone();
        temp.blur(2, Verse.RESTRICT_CHAPTER);
        assertEquals(temp, PassageFactory.createPassage("Exo 2:1-12, Exo 3:1-13"));
        temp = (Passage) exo2a_3b.clone();
        temp.blur(2, Verse.RESTRICT_NONE);
        assertEquals(temp, PassageFactory.createPassage("Exo 1:21-2:12, Exo 2:24-3:13"));
        temp = (Passage) exo2a_3b.clone();
        temp.blur(3, Verse.RESTRICT_CHAPTER);
        assertEquals(temp, PassageFactory.createPassage("Exo 2:1-13, Exo 3:1-14"));
        temp = (Passage) exo2a_3b.clone();
        temp.blur(3, Verse.RESTRICT_NONE);
        assertEquals(temp, PassageFactory.createPassage("Exo 1:20-2:13, Exo 2:23-3:14"));
        temp = (Passage) exo2a_3b.clone();
        temp.blur(14, Verse.RESTRICT_CHAPTER);
        assertEquals(temp, PassageFactory.createPassage("Exo 2:1-24, Exo 3:1-22"));
        temp = (Passage) exo2a_3b.clone();
        temp.blur(14, Verse.RESTRICT_NONE);
        assertEquals(temp, PassageFactory.createPassage("Exo 1:9-2:24, Exo 2:12-4:3"));
        temp = (Passage) exo2a_3b.clone();
        temp.blur(15, Verse.RESTRICT_CHAPTER);
        assertEquals(temp, PassageFactory.createPassage("Exo 2:1-25, Exo 3:1-22"));
        temp = (Passage) exo2a_3b.clone();
        temp.blur(15, Verse.RESTRICT_NONE);
        assertEquals(temp, PassageFactory.createPassage("Exo 1:8-2:25, Exo 2:11-4:4"));
        temp = (Passage) exo2a_3b.clone();
        temp.blur(16, Verse.RESTRICT_CHAPTER);
        assertEquals(temp, PassageFactory.createPassage("Exo 2:1-3:22"));
        temp = (Passage) exo2a_3b.clone();
        temp.blur(16, Verse.RESTRICT_NONE);
        assertEquals(temp, PassageFactory.createPassage("Exo 1:7-4:5"));
        temp = (Passage) exo2a_3b.clone();
        temp.blur(99999, Verse.RESTRICT_CHAPTER);
        assertEquals(temp, PassageFactory.createPassage("Exo 2:1-3:22"));
        temp = (Passage) exo2a_3b.clone();
        temp.blur(99999, Verse.RESTRICT_NONE);
        assertEquals(temp, PassageFactory.createPassage("Gen 1:1-Rev 22:21"));
        try { temp.blur(-1, Verse.RESTRICT_NONE); fail(temp.toString()); }
        catch (IllegalArgumentException ex) { }
        try { temp.blur(-1, -1); fail(temp.toString()); }
        catch (IllegalArgumentException ex) { }
        try { temp.blur(-1, 5); fail(temp.toString()); }
        catch (IllegalArgumentException ex) { }
        try { temp.blur(-1, Verse.RESTRICT_BOOK); fail(temp.toString()); }
        catch (IllegalArgumentException ex) { }
    }

    public void testWriteAddPassageListener() throws Exception
    {
        TestPassageListener li1 = new TestPassageListener();
        TestPassageListener li2 = new TestPassageListener();
        temp = (Passage) gen1_135.clone();
        temp.addPassageListener(li1);
        temp.addPassageListener(li2);
        assertTrue(li1.check(0, 0, 0));
        assertTrue(li2.check(0, 0, 0));
        temp.add(new Verse("Gen 1:7"));
        assertTrue(li1.check(1, 0, 0));
        assertTrue(li2.check(1, 0, 0));
        temp.add(new Verse("Gen 1:9"));
        assertTrue(li1.check(2, 0, 0));
        assertTrue(li2.check(2, 0, 0));
        temp.removePassageListener(li1);
        temp.add(new Verse("Gen 1:11"));
        assertTrue(li1.check(2, 0, 0));
        assertTrue(li2.check(3, 0, 0));
    }

    public void testWriteClone() throws Exception
    {
        assertTrue(gen1_135 != gen1_135.clone());
        assertEquals(gen1_135, gen1_135.clone());
        assertTrue(exo2a_3b != exo2a_3b.clone());
        assertEquals(exo2a_3b, exo2a_3b.clone());
    }

    public void testWriteRangeIterator() throws Exception
    {
        // We used to check for a UnsupportedOperationException until DistinctPassage started
        // throwing IllegalStateExceptions here. I'm not too bothered about the exact type of the
        // exception right now. Maybe we ought to be more thorough at some stage.
        Iterator it = gen1_135.rangeIterator();
        assertTrue(it.hasNext());
        assertEquals(it.next(), new VerseRange("Gen 1:1"));
        assertTrue(it.hasNext());
        assertEquals(it.next(), new VerseRange("Gen 1:3"));
        assertTrue(it.hasNext());
        assertEquals(it.next(), new VerseRange("Gen 1:5"));
        assertTrue(!it.hasNext());
        it = empty.rangeIterator();
        assertTrue(!it.hasNext());
    }

    public void testWriteVerseIterator() throws Exception
    {
        Iterator it = gen1_135.verseIterator();
        assertTrue(it.hasNext());
        assertEquals(it.next(), new Verse("Gen 1:1"));
        assertTrue(it.hasNext());
        assertEquals(it.next(), new Verse("Gen 1:3"));
        assertTrue(it.hasNext());
        assertEquals(it.next(), new Verse("Gen 1:5"));
        assertTrue(!it.hasNext());
        it = empty.verseIterator();
        assertTrue(!it.hasNext());
    }

    public void testWriteIsEmpty() throws Exception
    {
        assertTrue(!gen1_135.isEmpty());
        assertTrue(!exo2a_3b.isEmpty());
        assertTrue(empty.isEmpty());
    }

    public void testWriteCountVerses() throws Exception
    {
        assertEquals(gen1_135.countVerses(), 3);
        assertEquals(exo2a_3b.countVerses(), 21);
        assertEquals(empty.countVerses(), 0);
    }

    public void testWriteCountRanges() throws Exception
    {
        assertEquals(gen1_135.countRanges(), 3);
        assertEquals(exo2a_3b.countRanges(), 2);
        assertEquals(empty.countVerses(), 0);
    }

    public void testWriteGetVerseAt() throws Exception
    {
        assertEquals(gen1_135.getVerseAt(0), gen11);
        assertEquals(gen1_135.getVerseAt(1), gen13);
        assertEquals(gen1_135.getVerseAt(2), gen15);
        assertEquals(exo2a_3b.getVerseAt(0), exo21);
        assertEquals(exo2a_3b.getVerseAt(1), exo22);
        assertEquals(exo2a_3b.getVerseAt(2), exo23);
        assertEquals(exo2a_3b.getVerseAt(20), exo3b);
    }

    public void testWriteGetVerseRangeAt() throws Exception
    {
        assertEquals(gen1_135.getVerseRangeAt(0), gen11_1);
        assertEquals(gen1_135.getVerseRangeAt(1), new VerseRange("Gen 1:3"));
        assertEquals(gen1_135.getVerseRangeAt(2), new VerseRange("Gen 1:5"));
        assertEquals(exo2a_3b.getVerseRangeAt(0), new VerseRange("Exo 2:1-10"));
        assertEquals(exo2a_3b.getVerseRangeAt(1), new VerseRange("Exo 3:1-11"));
    }

    public void testWriteBooksInPassage() throws Exception
    {
        assertEquals(gen1_135.booksInPassage(), 1);
        assertEquals(exo2a_3b.booksInPassage(), 1);
    }

    public void testWriteChaptersInPassage() throws Exception
    {
        assertEquals(gen1_135.chaptersInPassage(1), 1);
        assertEquals(exo2a_3b.chaptersInPassage(1), 0);
        assertEquals(gen1_135.chaptersInPassage(2), 0);
        assertEquals(exo2a_3b.chaptersInPassage(2), 2);
        assertEquals(gen1_135.chaptersInPassage(3), 0);
        assertEquals(exo2a_3b.chaptersInPassage(3), 0);
        assertEquals(gen1_135.chaptersInPassage(0), 1);
        assertEquals(exo2a_3b.chaptersInPassage(0), 2);
    }

    public void testWriteVersesInPassage() throws Exception
    {
        assertEquals(gen1_135.versesInPassage(1, 1), 3);
        assertEquals(exo2a_3b.versesInPassage(1, 1), 0);
        assertEquals(gen1_135.versesInPassage(1, 2), 0);
        assertEquals(exo2a_3b.versesInPassage(1, 2), 0);
        assertEquals(gen1_135.versesInPassage(2, 1), 0);
        assertEquals(exo2a_3b.versesInPassage(2, 1), 0);
        assertEquals(gen1_135.versesInPassage(2, 2), 0);
        assertEquals(exo2a_3b.versesInPassage(2, 2), 10);
        assertEquals(gen1_135.versesInPassage(2, 3), 0);
        assertEquals(exo2a_3b.versesInPassage(2, 3), 11);
        assertEquals(gen1_135.versesInPassage(2, 4), 0);
        assertEquals(exo2a_3b.versesInPassage(2, 4), 0);
        assertEquals(gen1_135.versesInPassage(3, 1), 0);
        assertEquals(exo2a_3b.versesInPassage(3, 1), 0);
        assertEquals(gen1_135.versesInPassage(1, 0), 3);
        assertEquals(exo2a_3b.versesInPassage(1, 0), 0);
        assertEquals(gen1_135.versesInPassage(2, 0), 0);
        assertEquals(exo2a_3b.versesInPassage(2, 0), 21);
        assertEquals(gen1_135.versesInPassage(3, 0), 0);
        assertEquals(exo2a_3b.versesInPassage(3, 0), 0);
        assertEquals(gen1_135.versesInPassage(0, 1), 3);
        assertEquals(exo2a_3b.versesInPassage(0, 1), 0);
        assertEquals(gen1_135.versesInPassage(0, 2), 0);
        assertEquals(exo2a_3b.versesInPassage(0, 2), 10);
        assertEquals(gen1_135.versesInPassage(0, 3), 0);
        assertEquals(exo2a_3b.versesInPassage(0, 3), 11);
        assertEquals(gen1_135.versesInPassage(0, 0), gen1_135.countVerses());
        assertEquals(exo2a_3b.versesInPassage(0, 0), exo2a_3b.countVerses());
    }

    public void testWriteContainsVerse() throws Exception
    {
        assertTrue(!empty.contains(gen11));
        assertTrue(gen1_135.contains(gen11));
        assertTrue(!gen1_135.contains(gen12));
        assertTrue(gen1_135.contains(gen13));
        assertTrue(gen1_135.contains(gen15));
        assertTrue(gen_rev.contains(gen11));
        assertTrue(gen_rev.contains(gen12));
        assertTrue(gen_rev.contains(rev99));
    }

    public void testWriteContainsVerseRange() throws Exception
    {
        assertTrue(gen1_135.contains(gen11_1));
        assertTrue(!gen1_135.contains(gen11_2));
        assertTrue(!gen1_135.contains(gen12_1));
        assertTrue(!gen1_135.contains(exo21_1));
        assertTrue(!exo2a_3b.contains(gen11_1));
        assertTrue(exo2a_3b.contains(exo21_1));
        assertTrue(exo2a_3b.contains(exo21_2));
        assertTrue(exo2a_3b.contains(exo22_1));
        assertTrue(gen_rev.contains(new VerseRange(rev99)));
    }

    public void testWriteContainsAll() throws Exception
    {
        assertTrue(!gen1_135.containsAll(exo2a_3b));
        assertTrue(gen1_135.containsAll(empty));
        assertTrue(gen1_135.containsAll((Passage) gen1_135.clone()));
        assertTrue(!exo2a_3b.containsAll(gen1_135));
        assertTrue(exo2a_3b.containsAll(empty));
        assertTrue(exo2a_3b.containsAll((Passage) exo2a_3b.clone()));
        assertTrue(!empty.containsAll(gen1_135));
        assertTrue(empty.containsAll(empty));
        assertTrue(!empty.containsAll(exo2a_3b));
        assertTrue(gen_rev.containsAll(gen_rev));
    }

    public void testWriteContainsVerseBase() throws Exception
    {
        assertTrue(!empty.contains((VerseBase) gen11));
        assertTrue(gen1_135.contains((VerseBase) gen11));
        assertTrue(!gen1_135.contains((VerseBase) gen12));
        assertTrue(gen1_135.contains((VerseBase) gen13));
        assertTrue(gen1_135.contains((VerseBase) gen15));
        assertTrue(gen1_135.contains((VerseBase) gen11_1));
        assertTrue(!gen1_135.contains((VerseBase) gen11_2));
        assertTrue(!gen1_135.contains((VerseBase) gen12_1));
        assertTrue(!gen1_135.contains((VerseBase) exo21_1));
        assertTrue(!exo2a_3b.contains((VerseBase) gen11_1));
        assertTrue(exo2a_3b.contains((VerseBase) exo21_1));
        assertTrue(exo2a_3b.contains((VerseBase) exo21_2));
        assertTrue(exo2a_3b.contains((VerseBase) exo22_1));
    }

    public void testWriteAdd() throws Exception
    {
        temp = (Passage) gen1_135.clone();
        temp.add(new Verse("Gen 1:2"));
        assertEquals(temp.getName(), "Gen 1:1-3, 5");
        temp.add(new Verse("Gen 1:4"));
        assertEquals(temp.getName(), "Gen 1:1-5");
        temp = (Passage) gen1_135.clone();
        temp.add(new VerseRange("Gen 1:2-4"));
        assertEquals(temp.getName(), "Gen 1:1-5");
        temp = (Passage) gen1_135.clone();
        temp.add(new VerseRange("Gen 1:2"));
        assertEquals(temp.getName(), "Gen 1:1-3, 5");
        temp.add(new VerseRange("Gen 1:4"));
        assertEquals(temp.getName(), "Gen 1:1-5");
        temp = (Passage) gen1_135.clone();
        temp.add(new VerseRange("Gen 1:1-5"));
        assertEquals(temp.getName(), "Gen 1:1-5");
        try { temp.add(null); fail(); }
        catch (NullPointerException ex) { }
    }

    public void testWriteAddAll() throws Exception
    {
        temp = (Passage) gen1_135.clone();
        temp.addAll(PassageFactory.createPassage("Gen 1:2, Gen 1:4"));
        assertEquals(temp.getName(), "Gen 1:1-5");
    }

    public void testWriteClear() throws Exception
    {
        temp = (Passage) gen1_135.clone();
        temp.clear();
        assertEquals(temp.getName(), "");
        temp.clear();
        assertEquals(temp.getName(), "");
    }

    public void testWriteRemove() throws Exception
    {
        temp = (Passage) gen1_135.clone();
        temp.remove(new Verse("Gen 1:3"));
        assertEquals(temp.getName(), "Gen 1:1, 5");
        temp.remove(new Verse("Gen 1:5"));
        assertEquals(temp.getName(), "Gen 1:1");
        temp.remove(new Verse("Gen 1:1"));
        assertEquals(temp.getName(), "");
        temp = PassageFactory.createPassage("Gen 1:1-5");
        temp.remove(new Verse("Gen 1:3"));
        assertEquals(temp.getName(), "Gen 1:1-2, 4-5");
    }

    public void testWriteRemoveAllCollection() throws Exception
    {
        temp = PassageFactory.createPassage("Gen 1:1-5");
        temp.removeAll(PassageFactory.createPassage("Gen 1:2, Gen 1:4"));
        assertEquals(temp.getName(), "Gen 1:1, 3, 5");
        temp.removeAll(PassageFactory.createPassage("Exo 1:2, Gen 1:4"));
        assertEquals(temp.getName(), "Gen 1:1, 3, 5");
        temp.removeAll(PassageFactory.createPassage("Gen 1:2-Rev 22:21"));
        assertEquals(temp.getName(), "Gen 1:1");
        temp.removeAll(PassageFactory.createPassage("Gen 1:1"));
        assertEquals(temp.getName(), "");
    }

    public void testWriteRetainAllCollection() throws Exception
    {
        temp = PassageFactory.createPassage("Gen 1:1-5");
        temp.retainAll(PassageFactory.createPassage("Gen 1:2, Gen 1:4"));
        assertEquals(temp.getName(), "Gen 1:2, 4");
        temp.retainAll(PassageFactory.createPassage("Exo 1:2, Gen 1:4"));
        assertEquals(temp.getName(), "Gen 1:4");
        temp.retainAll(PassageFactory.createPassage("Gen 1:2-Rev 22:21"));
        assertEquals(temp.getName(), "Gen 1:4");
        temp.retainAll(PassageFactory.createPassage("Gen 1:1"));
        assertEquals(temp.getName(), "");
    }

    public void testWriteObject() throws Exception
    {
        Passage hard = PassageFactory.createPassage();
        for (int i=1; i<Books.versesInBible(); i+=200)
        {
            hard.add(new Verse(i));
        }

        File test_dat = new File("test.dat");

        FileOutputStream file_out = new FileOutputStream(test_dat);
        ObjectOutputStream obj_out = new ObjectOutputStream(file_out);
        obj_out.writeObject(gen1_135);
        obj_out.writeObject(exo2a_3b);
        obj_out.writeObject(gen_rev);
        obj_out.writeObject(hard);
        obj_out.writeObject(grace);
        obj_out.close();
        obj_out = null;
        FileInputStream file_in = new FileInputStream(test_dat);
        ObjectInputStream obj_in = new ObjectInputStream(file_in);
        Passage gen1_135_copy = (Passage) obj_in.readObject();
        Passage exo2a_3b_copy = (Passage) obj_in.readObject();
        Passage gen_rev_copy = (Passage) obj_in.readObject();
        Passage hard_copy = (Passage) obj_in.readObject();
        Passage grace_copy = (Passage) obj_in.readObject();
        obj_in.close();
        assertEquals(gen1_135_copy, gen1_135);
        assertEquals(exo2a_3b_copy, exo2a_3b);
        assertEquals(gen_rev_copy, gen_rev);
        assertEquals(hard_copy, hard);
        assertEquals(grace_copy, grace);
        test_dat.delete();
    }

    public void testWriteDescription() throws Exception
    {
        File test_dat = new File("test.dat");
        FileWriter wout = new FileWriter(test_dat);
        gen1_135.writeDescription(wout);
        wout.close();
        wout = null;
        FileReader win = new FileReader(test_dat);
        Passage lst = PassageFactory.createPassage();
        lst.readDescription(win);
        win.close();
        win = null;
        assertEquals(gen1_135, lst);
        test_dat.delete();
    }
    
    public void testSpecial() throws Exception
    {
        // Some special tests for known breakages
        Passage ich5l = PassageFactory.createPassage("1ch 5");
        Passage ich5u = PassageFactory.createPassage("1Ch 5");
        assertEquals(ich5l, ich5u);
    }
}
