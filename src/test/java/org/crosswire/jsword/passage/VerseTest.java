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

import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.BookName;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
 */
public class VerseTest {

    private Versification v11n;
    private Verse gen00;
    private Verse gen10;
    private Verse gen11;
    private Verse gen11a;
    private Verse gen11s;
    private Verse gen12;
    private Verse gen20;
    private Verse gen21;
    private Verse gen22;
    private Verse rev11;
    private Verse rev12;
    private Verse rev21;
    private Verse rev22;
    private Verse rev99;
    private Verse jude0;
    private Verse jude1;
    private Verse jude2;
    private Verse jude9;
    private Verse ssa00;
    private Verse ssa10;
    private Verse ssa11;

    @Before
    public void setUp() throws Exception {
        BookName.setFullBookName(false);
        // AV11N(DMS): Update test to test all V11Ns
        v11n = Versifications.instance().getVersification("KJV");

        gen00 = new Verse(v11n, BibleBook.GEN, 0, 0);
        gen10 = new Verse(v11n, BibleBook.GEN, 1, 0);
        gen11 = new Verse(v11n, BibleBook.GEN, 1, 1);
        gen11a = new Verse(v11n, BibleBook.GEN, 1, 1);
        gen11s = new Verse(v11n, BibleBook.GEN, 1, 1, "sub");
        gen12 = new Verse(v11n, BibleBook.GEN, 1, 2);
        gen20 = new Verse(v11n, BibleBook.GEN, 2, 0);
        gen21 = new Verse(v11n, BibleBook.GEN, 2, 1);
        gen22 = new Verse(v11n, BibleBook.GEN, 2, 2);
        rev11 = new Verse(v11n, BibleBook.REV, 1, 1);
        rev12 = new Verse(v11n, BibleBook.REV, 1, 2);
        rev21 = new Verse(v11n, BibleBook.REV, 2, 1);
        rev22 = new Verse(v11n, BibleBook.REV, 2, 2);
        rev99 = new Verse(v11n, BibleBook.REV, 22, 21);
        jude0 = new Verse(v11n, BibleBook.JUDE, 0, 0);
        jude1 = new Verse(v11n, BibleBook.JUDE, 1, 1);
        jude2 = new Verse(v11n, BibleBook.JUDE, 1, 2);
        jude9 = new Verse(v11n, BibleBook.JUDE, 1, 25);
        ssa00 = new Verse(v11n, BibleBook.SAM2, 0, 0);
        ssa10 = new Verse(v11n, BibleBook.SAM2, 1, 0);
        ssa11 = new Verse(v11n, BibleBook.SAM2, 1, 1);
    }

   @Test
    public void testNewViaString() throws Exception {
       Assert.assertEquals(gen11s, VerseFactory.fromString(v11n, "Gen.1.1!sub"));
        Assert.assertEquals(gen11, Verse.DEFAULT);
        Assert.assertEquals(gen11, VerseFactory.fromString(v11n, "Genesis 1 1"));
        Assert.assertEquals(gen11, VerseFactory.fromString(v11n, "Gen 1 1"));
        Assert.assertEquals(gen11, VerseFactory.fromString(v11n, "Ge 1 1"));
        Assert.assertEquals(gen11, VerseFactory.fromString(v11n, "genesis 1 1"));
        Assert.assertEquals(gen11, VerseFactory.fromString(v11n, "genesi 1 1"));
        Assert.assertEquals(gen11, VerseFactory.fromString(v11n, "GENESIS 1 1"));
        Assert.assertEquals(gen11, VerseFactory.fromString(v11n, "GENESI 1 1"));
        Assert.assertEquals(gen11, VerseFactory.fromString(v11n, "ge 1 1"));
        Assert.assertEquals(gen10, VerseFactory.fromString(v11n, "ge 1"));
        Assert.assertEquals(gen00, VerseFactory.fromString(v11n, "ge"));
        Assert.assertEquals(gen00, VerseFactory.fromString(v11n, "gen"));
        Assert.assertEquals(rev99, VerseFactory.fromString(v11n, "rev 22 21"));
        Assert.assertEquals(rev99, VerseFactory.fromString(v11n, "REVE 22 21"));
        Assert.assertEquals(rev99, VerseFactory.fromString(v11n, "REVELATIONS 22 21"));
        Assert.assertEquals(gen20, VerseFactory.fromString(v11n, "ge 2"));
        Assert.assertEquals(gen11, VerseFactory.fromString(v11n, "ge.1.1"));
        Assert.assertEquals(gen11, VerseFactory.fromString(v11n, "ge 1.1"));
        Assert.assertEquals(gen11, VerseFactory.fromString(v11n, "ge.1 1"));
        Assert.assertEquals(gen11, VerseFactory.fromString(v11n, "ge.1:1"));
        Assert.assertEquals(gen11, VerseFactory.fromString(v11n, "ge:1:1"));
        Assert.assertEquals(gen11, VerseFactory.fromString(v11n, "ge:1 1"));
        Assert.assertEquals(gen11, VerseFactory.fromString(v11n, " ge 1 1 "));
        Assert.assertEquals(gen10, VerseFactory.fromString(v11n, "gen1"));
        Assert.assertEquals(gen10, VerseFactory.fromString(v11n, "GEN1"));
        Assert.assertEquals(gen11, VerseFactory.fromString(v11n, "GENESIS1:1"));
        Assert.assertEquals(gen11, VerseFactory.fromString(v11n, "GE1    1"));
        Assert.assertEquals(gen11, VerseFactory.fromString(v11n, " GEN  1  1  "));
        Assert.assertEquals(gen11, VerseFactory.fromString(v11n, " gen 1 1 "));
        Assert.assertEquals(ssa11, VerseFactory.fromString(v11n, "2Sa 1:1"));
        Assert.assertEquals(ssa11, VerseFactory.fromString(v11n, "2Sa 1 1"));
        Assert.assertEquals(ssa11, VerseFactory.fromString(v11n, "2Sa1 1"));
        Assert.assertEquals(ssa11, VerseFactory.fromString(v11n, "2 Sa 1 1"));
        Assert.assertEquals(ssa11, VerseFactory.fromString(v11n, "2 Sa1 1"));
        Assert.assertEquals(ssa11, VerseFactory.fromString(v11n, "2Sa1:1"));
        Assert.assertEquals(ssa11, VerseFactory.fromString(v11n, "2 Sa 1 1"));
        Assert.assertEquals(ssa10, VerseFactory.fromString(v11n, "  2  Sa  1  "));
        Assert.assertEquals(ssa00, VerseFactory.fromString(v11n, "  2  Sa  "));
        Assert.assertEquals(ssa10, VerseFactory.fromString(v11n, "  2  Sa1  "));
        Assert.assertEquals(ssa11, VerseFactory.fromString(v11n, "  2  Sa1  1  "));
        Assert.assertEquals(ssa11, VerseFactory.fromString(v11n, "  2 : Sa1  1  "));
        Assert.assertEquals(rev99, VerseFactory.fromString(v11n, "Rev 22:$"));
        Assert.assertEquals(rev99, VerseFactory.fromString(v11n, " Rev 22 ff "));
        Assert.assertEquals(rev99, VerseFactory.fromString(v11n, "  Rev  22  ff  "));
        Assert.assertEquals(rev99, VerseFactory.fromString(v11n, "  Rev  22  $  "));
        Assert.assertEquals(jude0, VerseFactory.fromString(v11n, "Jude"));
        Assert.assertEquals(jude9, VerseFactory.fromString(v11n, "Jude $"));
        Assert.assertEquals(jude9, VerseFactory.fromString(v11n, " Jude  $ "));
        Assert.assertEquals(jude9, VerseFactory.fromString(v11n, "Jude ff"));
        Assert.assertEquals(jude9, VerseFactory.fromString(v11n, "  Jude  ff  "));
        Assert.assertEquals(VerseFactory.fromString(v11n, "Deu 1:1"), VerseFactory.fromString(v11n, "Dt 1:1"));
        Assert.assertEquals(VerseFactory.fromString(v11n, "Mat 1:1"), VerseFactory.fromString(v11n, "Mt 1:1"));
        try {
            VerseFactory.fromString(v11n, "gen 1 1 1");
            Assert.fail();
        } catch (NoSuchVerseException ex) {
            // This is allowed
        }
        try {
            VerseFactory.fromString(v11n, "gen.1.1.1");
            Assert.fail();
        } catch (NoSuchVerseException ex) {
            // This is allowed
        }
        try {
            VerseFactory.fromString(v11n, "gen.1.1:1");
            Assert.fail();
        } catch (NoSuchVerseException ex) {
            // This is allowed
        }
        try {
            VerseFactory.fromString(v11n, "gen 1 1 1");
            Assert.fail();
        } catch (NoSuchVerseException ex) {
            // This is allowed
        }
        try {
            VerseFactory.fromString(v11n, "gen 1 1 1");
            Assert.fail();
        } catch (NoSuchVerseException ex) {
            // This is allowed
        }
        try {
            VerseFactory.fromString(v11n, "gen 1 1 1");
            Assert.fail();
        } catch (NoSuchVerseException ex) {
            // This is allowed
        }
        try {
            VerseFactory.fromString(v11n, null);
            Assert.fail();
        } catch (NullPointerException ex) {
            // This is allowed
        } catch (NoSuchKeyException ex) {
            // This is allowed
        }
        Assert.assertEquals(jude1, VerseFactory.fromString(v11n, "jude 1"));
        Assert.assertEquals(jude2, VerseFactory.fromString(v11n, "jude 2"));
        Assert.assertEquals(jude9, VerseFactory.fromString(v11n, "jude 25"));
    }

    @Test
    public void testGetName() throws Exception {
        Assert.assertEquals(VerseFactory.fromString(v11n, "Genesis 1 1").getName(), "Gen 1:1");
        Assert.assertEquals(VerseFactory.fromString(v11n, "Gen 1 1").getName(), "Gen 1:1");
        Assert.assertEquals(VerseFactory.fromString(v11n, "Genesis 1:1").getName(), "Gen 1:1");
        Assert.assertEquals(VerseFactory.fromString(v11n, "Gen 1 1").getName(), "Gen 1:1");
        Assert.assertEquals(VerseFactory.fromString(v11n, "ge 1 1").getName(), "Gen 1:1");
        Assert.assertEquals(VerseFactory.fromString(v11n, "ge").getName(), "Gen 0:0");
        Assert.assertEquals(VerseFactory.fromString(v11n, "Ge:1:1").getName(), "Gen 1:1");
        Assert.assertEquals(VerseFactory.fromString(v11n, "Jude 1").getName(), "Jude 1");
        Assert.assertEquals(VerseFactory.fromString(v11n, "Jude").getName(), "Jude 0");
        Assert.assertEquals(VerseFactory.fromString(v11n, "Jude 1:1").getName(), "Jude 1");
    }

    @Test
    public void testGetNameVerse() throws Exception {
        Assert.assertEquals(VerseFactory.fromString(v11n, "Gen 1:2").getName(gen11), "2");
        Assert.assertEquals(VerseFactory.fromString(v11n, "Gen 2:1").getName(gen11), "2:1");
        Assert.assertEquals(VerseFactory.fromString(v11n, "Gen 2:1").getName(jude9), "Gen 2:1");
        Assert.assertEquals(VerseFactory.fromString(v11n, "Gen 2:1").getName(null), "Gen 2:1");
    }

    @Test
    public void testNewViaIntIntIntBoolean() {
        Assert.assertEquals(gen00, new Verse(v11n, null, 1, 1, true));
        Assert.assertEquals(gen10, new Verse(v11n, BibleBook.GEN, 0, 1, true));
        Assert.assertEquals(gen10, new Verse(v11n, BibleBook.GEN, 1, 0, true));
        Assert.assertEquals(rev99, new Verse(v11n, BibleBook.REV, 22, 22, true));
        Assert.assertEquals(rev99, new Verse(v11n, BibleBook.REV, 23, 21, true));
        Assert.assertEquals(rev99, new Verse(v11n, BibleBook.REV, 23, 22, true));
        Assert.assertEquals(rev99, new Verse(v11n, BibleBook.GEN, 999999, 0, true));
        Assert.assertEquals(rev99, new Verse(v11n, BibleBook.GEN, 0, 999999, true));
    }

    @Test
    public void testClone() {
        Assert.assertEquals(gen11, gen11.clone());
        Assert.assertEquals(gen11, gen11.clone());
        Assert.assertEquals(rev99, rev99.clone());
        Assert.assertEquals(rev99, rev99.clone());
    }

    @Test
    public void testEquals() {
        Assert.assertTrue(!gen11.equals(null));
        Assert.assertTrue(!gen11.equals(Integer.valueOf(0)));
        Assert.assertTrue(!gen11.equals("org.crosswire.jsword.passage.Verse"));
        Assert.assertTrue(gen11.equals(gen11a));
        Assert.assertTrue(!gen11.equals(gen12));
        Assert.assertTrue(!gen11.equals(rev99));
        Assert.assertTrue(!gen11.equals(gen12));
    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(gen11.hashCode(), gen11a.hashCode());
        Assert.assertTrue(gen11.hashCode() != 0);
    }

    @Test
    public void testCompareTo() {
        Assert.assertTrue(gen11.compareTo(rev99) < 0);
        Assert.assertTrue(rev99.compareTo(gen11) > 0);
        Assert.assertEquals(gen11.compareTo(gen11), 0);
    }

    @Test
    public void testAddSubtract() {
        Assert.assertEquals(v11n.distance(gen11, gen12), 1);
        Assert.assertEquals(v11n.distance(gen11, gen11), 0);
        Assert.assertEquals(v11n.distance(gen12, gen11), -1);
        Verse last = gen11.clone();
        for (int i = 0; i < v11n.maximumOrdinal(); i += 99) {
            Verse next = v11n.add(last, i);
            Assert.assertEquals(v11n.distance(last, next), i);

            Verse next2 = v11n.subtract(next, i);
            Assert.assertEquals(gen11.getOsisID(), gen11, next2);
        }
        Assert.assertEquals(gen11.getOsisID(), gen11, v11n.subtract(gen11, 0));
        Assert.assertEquals(gen11.getOsisID(), gen10, v11n.subtract(gen11, 1));
        Assert.assertEquals(gen11.getOsisID(), gen00, v11n.subtract(gen11, 2));
        Assert.assertEquals(gen11.getOsisID(), gen11, v11n.add(gen11, 0));
        Assert.assertEquals(rev99.getOsisID(), rev99, v11n.add(rev99, 0));
        Assert.assertEquals(rev99.getOsisID(), rev99, v11n.add(rev99, 1));
        Assert.assertEquals(rev99.getOsisID(), rev99, v11n.add(rev99, 2));
    }

    @Test
    public void testToString() {
        Assert.assertEquals("Gen 1:1", gen11.toString());
        Assert.assertEquals("Gen 1:2", gen12.toString());
        Assert.assertEquals("Gen 2:1", gen21.toString());
        Assert.assertEquals("Gen 2:2", gen22.toString());
        Assert.assertEquals("Rev 1:1", rev11.toString());
        Assert.assertEquals("Rev 1:2", rev12.toString());
        Assert.assertEquals("Rev 2:1", rev21.toString());
        Assert.assertEquals("Rev 2:2", rev22.toString());
        Assert.assertEquals("Rev 22:21", rev99.toString());
    }

    @Test
    public void testGetBook() {
        Assert.assertEquals(gen11.getBook(), BibleBook.GEN);
        Assert.assertEquals(gen12.getBook(), BibleBook.GEN);
        Assert.assertEquals(gen21.getBook(), BibleBook.GEN);
        Assert.assertEquals(gen22.getBook(), BibleBook.GEN);
        Assert.assertEquals(rev11.getBook(), BibleBook.REV);
        Assert.assertEquals(rev12.getBook(), BibleBook.REV);
        Assert.assertEquals(rev21.getBook(), BibleBook.REV);
        Assert.assertEquals(rev22.getBook(), BibleBook.REV);
        Assert.assertEquals(rev99.getBook(), BibleBook.REV);
    }

    @Test
    public void testGetChapter() {
        Assert.assertEquals(gen11.getChapter(), 1);
        Assert.assertEquals(gen12.getChapter(), 1);
        Assert.assertEquals(gen21.getChapter(), 2);
        Assert.assertEquals(gen22.getChapter(), 2);
        Assert.assertEquals(rev11.getChapter(), 1);
        Assert.assertEquals(rev12.getChapter(), 1);
        Assert.assertEquals(rev21.getChapter(), 2);
        Assert.assertEquals(rev22.getChapter(), 2);
        Assert.assertEquals(rev99.getChapter(), 22);
    }

    @Test
    public void testGetVerse() {
        Assert.assertEquals(gen11.getVerse(), 1);
        Assert.assertEquals(gen12.getVerse(), 2);
        Assert.assertEquals(gen21.getVerse(), 1);
        Assert.assertEquals(gen22.getVerse(), 2);
        Assert.assertEquals(rev11.getVerse(), 1);
        Assert.assertEquals(rev12.getVerse(), 2);
        Assert.assertEquals(rev21.getVerse(), 1);
        Assert.assertEquals(rev22.getVerse(), 2);
        Assert.assertEquals(rev99.getVerse(), 21);
    }

    @Test
    public void testGetOrdinal() {
        Assert.assertEquals(4, gen11.getOrdinal());
        Assert.assertEquals(5, gen12.getOrdinal());
        Assert.assertEquals(36, gen21.getOrdinal());
        Assert.assertEquals(37, gen22.getOrdinal());
        Assert.assertEquals(31935, rev11.getOrdinal());
        Assert.assertEquals(31936, rev12.getOrdinal());
        Assert.assertEquals(31956, rev21.getOrdinal());
        Assert.assertEquals(31957, rev22.getOrdinal());
        Assert.assertEquals(32359, rev99.getOrdinal());
    }

    @Test
    public void testGetAccuracy() throws Exception {
        VerseRange vr = new VerseRange(v11n, gen11, gen11);
        Assert.assertEquals(AccuracyType.fromText(v11n, "Gen 1:1", AccuracyType.tokenize("Gen 1:1"), vr), AccuracyType.BOOK_VERSE);
        Assert.assertEquals(AccuracyType.fromText(v11n, "Gen 1", AccuracyType.tokenize("Gen 1"), vr), AccuracyType.BOOK_CHAPTER);
        Assert.assertEquals(AccuracyType.fromText(v11n, "Jude 1", AccuracyType.tokenize("Jude 1"), vr), AccuracyType.BOOK_VERSE);
        Assert.assertEquals(AccuracyType.fromText(v11n, "Jude 1:1", AccuracyType.tokenize("Jude 1:1"), vr), AccuracyType.BOOK_VERSE);
        Assert.assertEquals(AccuracyType.fromText(v11n, "Gen", AccuracyType.tokenize("Gen"), vr), AccuracyType.BOOK_ONLY);
        Assert.assertEquals(AccuracyType.fromText(v11n, "1:1", AccuracyType.tokenize("1:1"), vr), AccuracyType.CHAPTER_VERSE);
        Assert.assertEquals(AccuracyType.fromText(v11n, "1", AccuracyType.tokenize("1"), vr), AccuracyType.VERSE_ONLY);
        try {
            AccuracyType.fromText(v11n, "Komplete and utter rubbish", AccuracyType.tokenize("Komplete and utter rubbish"), vr);
            Assert.fail();
        } catch (NoSuchVerseException ex) {
            // This is allowed
        }
        try {
            AccuracyType.fromText(v11n, "x 1 1", AccuracyType.tokenize("x 1 1"), vr);
            Assert.fail();
        } catch (NoSuchVerseException ex) {
            // This is allowed
        }
        try {
            AccuracyType.fromText(v11n, null, (String[]) null, vr);
            Assert.fail();
        } catch (NullPointerException ex) {
            // This is allowed
        }
    }

    @Test
    public void testIsStartEndOfChapterBook() throws Exception {
        Assert.assertTrue(v11n.isStartOfChapter(VerseFactory.fromString(v11n, "Gen 1:1")));
        Assert.assertTrue(!v11n.isStartOfChapter(VerseFactory.fromString(v11n, "Gen 1:10")));
        Assert.assertTrue(!v11n.isStartOfChapter(VerseFactory.fromString(v11n, "Gen 1:$")));
        Assert.assertTrue(v11n.isStartOfChapter(VerseFactory.fromString(v11n, "Gen 10:0")));
        Assert.assertTrue(!v11n.isStartOfChapter(VerseFactory.fromString(v11n, "Gen 10:10")));
        Assert.assertTrue(!v11n.isStartOfChapter(VerseFactory.fromString(v11n, "Gen 10:$")));
        Assert.assertTrue(v11n.isStartOfChapter(VerseFactory.fromString(v11n, "Gen $:0")));
        Assert.assertTrue(!v11n.isStartOfChapter(VerseFactory.fromString(v11n, "Gen $:10")));
        Assert.assertTrue(!v11n.isStartOfChapter(VerseFactory.fromString(v11n, "Gen $:$")));
        Assert.assertTrue(!v11n.isEndOfChapter(VerseFactory.fromString(v11n, "Gen 1:1")));
        Assert.assertTrue(!v11n.isEndOfChapter(VerseFactory.fromString(v11n, "Gen 1:10")));
        Assert.assertTrue(v11n.isEndOfChapter(VerseFactory.fromString(v11n, "Gen 1:$")));
        Assert.assertTrue(!v11n.isEndOfChapter(VerseFactory.fromString(v11n, "Gen 10:1")));
        Assert.assertTrue(!v11n.isEndOfChapter(VerseFactory.fromString(v11n, "Gen 10:10")));
        Assert.assertTrue(v11n.isEndOfChapter(VerseFactory.fromString(v11n, "Gen 10:$")));
        Assert.assertTrue(!v11n.isEndOfChapter(VerseFactory.fromString(v11n, "Gen $:1")));
        Assert.assertTrue(!v11n.isEndOfChapter(VerseFactory.fromString(v11n, "Gen $:10")));
        Assert.assertTrue(v11n.isEndOfChapter(VerseFactory.fromString(v11n, "Gen $:$")));
        Assert.assertTrue(v11n.isStartOfBook(VerseFactory.fromString(v11n, "Gen 0:0")));
        Assert.assertTrue(!v11n.isStartOfBook(VerseFactory.fromString(v11n, "Gen 1:10")));
        Assert.assertTrue(!v11n.isStartOfBook(VerseFactory.fromString(v11n, "Gen 1:$")));
        Assert.assertTrue(!v11n.isStartOfBook(VerseFactory.fromString(v11n, "Gen 10:1")));
        Assert.assertTrue(!v11n.isStartOfBook(VerseFactory.fromString(v11n, "Gen 10:10")));
        Assert.assertTrue(!v11n.isStartOfBook(VerseFactory.fromString(v11n, "Gen 10:$")));
        Assert.assertTrue(!v11n.isStartOfBook(VerseFactory.fromString(v11n, "Gen $:1")));
        Assert.assertTrue(!v11n.isStartOfBook(VerseFactory.fromString(v11n, "Gen $:10")));
        Assert.assertTrue(!v11n.isStartOfBook(VerseFactory.fromString(v11n, "Gen $:$")));

        Assert.assertTrue(!v11n.isEndOfBook(VerseFactory.fromString(v11n, "Gen 1:1")));
        Assert.assertTrue(!v11n.isEndOfBook(VerseFactory.fromString(v11n, "Gen 1:10")));
        Assert.assertTrue(!v11n.isEndOfBook(VerseFactory.fromString(v11n, "Gen 1:$")));
        Assert.assertTrue(!v11n.isEndOfBook(VerseFactory.fromString(v11n, "Gen 10:1")));
        Assert.assertTrue(!v11n.isEndOfBook(VerseFactory.fromString(v11n, "Gen 10:10")));
        Assert.assertTrue(!v11n.isEndOfBook(VerseFactory.fromString(v11n, "Gen 10:$")));
        Assert.assertTrue(!v11n.isEndOfBook(VerseFactory.fromString(v11n, "Gen $:1")));
        Assert.assertTrue(!v11n.isEndOfBook(VerseFactory.fromString(v11n, "Gen $:10")));
        Assert.assertTrue(v11n.isEndOfBook(VerseFactory.fromString(v11n, "Gen $:$")));
    }

    @Test
    public void testMax() {
        Assert.assertEquals(v11n.max(gen11, gen12), gen12);
        Assert.assertEquals(v11n.max(gen11, rev99), rev99);
        Assert.assertEquals(v11n.max(gen11, gen11a), gen11);
        Assert.assertEquals(v11n.max(gen12, gen11), gen12);
        Assert.assertEquals(v11n.max(rev99, gen11), rev99);
        Assert.assertEquals(v11n.max(gen11a, gen11), gen11a);
    }

    @Test
    public void testMin() {
        Assert.assertEquals(v11n.min(gen11, gen12), gen11);
        Assert.assertEquals(v11n.min(gen11, rev99), gen11);
        Assert.assertEquals(v11n.min(gen11, gen11a), gen11);
        Assert.assertEquals(v11n.min(gen12, gen11), gen11);
        Assert.assertEquals(v11n.min(rev99, gen11), gen11);
        Assert.assertEquals(v11n.min(gen11a, gen11a), gen11a);
    }

    @Test
    public void testToVerseArray() {
        Assert.assertEquals(gen11.toVerseArray().length, 1);
    }

    @Test
    public void testVerseHasName() {
        Verse v = new Verse(Versifications.instance().getVersification(Versifications.DEFAULT_V11N), 5);
        Assert.assertEquals("Gen 1:2", v.getName());
    }
}
