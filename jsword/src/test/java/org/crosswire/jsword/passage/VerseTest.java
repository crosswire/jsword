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
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.passage;

import junit.framework.TestCase;

import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.BookName;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class VerseTest extends TestCase {
    public VerseTest(String s) {
        super(s);
    }

    private Versification v11n;
    private Verse gen00 = null;
    private Verse gen10 = null;
    private Verse gen11 = null;
    private Verse gen11a = null;
    private Verse gen12 = null;
    private Verse gen20 = null;
    private Verse gen21 = null;
    private Verse gen22 = null;
    private Verse rev11 = null;
    private Verse rev12 = null;
    private Verse rev21 = null;
    private Verse rev22 = null;
    private Verse rev99 = null;
    private Verse jude1 = null;
    private Verse jude2 = null;
    private Verse jude9 = null;
    private Verse ssa00 = null;
    private Verse ssa10 = null;
    private Verse ssa11 = null;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        BookName.setFullBookName(false);
        // AV11N(DMS): Update test to test all V11Ns
        v11n = Versifications.instance().getDefaultVersification();

        gen00 = new Verse(BibleBook.GEN, 0, 0);
        gen10 = new Verse(BibleBook.GEN, 1, 0);
        gen11 = new Verse(BibleBook.GEN, 1, 1);
        gen11a = new Verse(BibleBook.GEN, 1, 1);
        gen12 = new Verse(BibleBook.GEN, 1, 2);
        gen20 = new Verse(BibleBook.GEN, 2, 0);
        gen21 = new Verse(BibleBook.GEN, 2, 1);
        gen22 = new Verse(BibleBook.GEN, 2, 2);
        rev11 = new Verse(BibleBook.REV, 1, 1);
        rev12 = new Verse(BibleBook.REV, 1, 2);
        rev21 = new Verse(BibleBook.REV, 2, 1);
        rev22 = new Verse(BibleBook.REV, 2, 2);
        rev99 = new Verse(BibleBook.REV, 22, 21);
        jude1 = new Verse(BibleBook.JUDE, 1, 1);
        jude2 = new Verse(BibleBook.JUDE, 1, 2);
        jude9 = new Verse(BibleBook.JUDE, 1, 25);
        ssa00 = new Verse(BibleBook.SAM2, 0, 0);
        ssa10 = new Verse(BibleBook.SAM2, 1, 0);
        ssa11 = new Verse(BibleBook.SAM2, 1, 1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() {
    }

    public void testNewViaString() throws Exception {
        assertEquals(gen11, Verse.DEFAULT);
        assertEquals(gen11, VerseFactory.fromString(v11n, "Genesis 1 1"));
        assertEquals(gen11, VerseFactory.fromString(v11n, "Gen 1 1"));
        assertEquals(gen11, VerseFactory.fromString(v11n, "G 1 1"));
        assertEquals(gen11, VerseFactory.fromString(v11n, "genesis 1 1"));
        assertEquals(gen11, VerseFactory.fromString(v11n, "genesi 1 1"));
        assertEquals(gen11, VerseFactory.fromString(v11n, "GENESIS 1 1"));
        assertEquals(gen11, VerseFactory.fromString(v11n, "GENESI 1 1"));
        assertEquals(gen11, VerseFactory.fromString(v11n, "g 1 1"));
        assertEquals(gen10, VerseFactory.fromString(v11n, "g 1"));
        assertEquals(gen00, VerseFactory.fromString(v11n, "g"));
        assertEquals(gen00, VerseFactory.fromString(v11n, "gen"));
        assertEquals(rev99, VerseFactory.fromString(v11n, "rev 22 21"));
        assertEquals(rev99, VerseFactory.fromString(v11n, "REVE 22 21"));
        assertEquals(rev99, VerseFactory.fromString(v11n, "REVELATIONS 22 21"));
        assertEquals(gen20, VerseFactory.fromString(v11n, "g 2"));
        assertEquals(gen11, VerseFactory.fromString(v11n, "g.1.1"));
        assertEquals(gen11, VerseFactory.fromString(v11n, "g 1.1"));
        assertEquals(gen11, VerseFactory.fromString(v11n, "g.1 1"));
        assertEquals(gen11, VerseFactory.fromString(v11n, "g.1:1"));
        assertEquals(gen11, VerseFactory.fromString(v11n, "g:1:1"));
        assertEquals(gen11, VerseFactory.fromString(v11n, "g:1 1"));
        assertEquals(gen11, VerseFactory.fromString(v11n, " g 1 1 "));
        assertEquals(gen10, VerseFactory.fromString(v11n, "gen1"));
        assertEquals(gen10, VerseFactory.fromString(v11n, "GEN1"));
        assertEquals(gen11, VerseFactory.fromString(v11n, "GENESIS1:1"));
        assertEquals(gen11, VerseFactory.fromString(v11n, "G1    1"));
        assertEquals(gen11, VerseFactory.fromString(v11n, " GEN  1  1  "));
        /*
         * See note in AccuracyType.tokenize() assertEquals(gen11,
         * VerseFactory.fromString(v11n, "gen1v1")); assertEquals(gen11,
         * VerseFactory.fromString(v11n, "gen 1 v 1"));
         * assertEquals(gen11, VerseFactory.fromString(v11n, "gen 1v1"));
         * assertEquals(gen11,
         * VerseFactory.fromString(v11n, "gen 1  v  1"));
         * assertEquals(gen11, VerseFactory.fromString(v11n, "gen 1  v  1 "));
         * assertEquals(gen11,
         * VerseFactory.fromString(v11n, " gen 1  v  1 "));
         * assertEquals(gen11, VerseFactory.fromString(v11n, "gen ch1 1"));
         * assertEquals(gen11, VerseFactory.fromString(v11n, "gen ch 1 1"));
         * assertEquals(gen11, VerseFactory.fromString(v11n, "gen ch  1 1"));
         * assertEquals(gen11, VerseFactory.fromString(v11n, "gen ch1v1"));
         * assertEquals(gen11, VerseFactory.fromString(v11n, " gen ch 1 v 1 "));
         */
        assertEquals(gen11, VerseFactory.fromString(v11n, " gen 1 1 "));
        //assertEquals(pro11, VerseFactory.fromString(v11n, "proverbs 1v1"));
        // assertEquals(ch111, VerseFactory.fromString(v11n, "1chronicles ch1 1"));
        assertEquals(ssa11, VerseFactory.fromString(v11n, "2Sa 1:1"));
        assertEquals(ssa11, VerseFactory.fromString(v11n, "2Sa 1 1"));
        assertEquals(ssa11, VerseFactory.fromString(v11n, "2Sa1 1"));
        assertEquals(ssa11, VerseFactory.fromString(v11n, "2 Sa 1 1"));
        assertEquals(ssa11, VerseFactory.fromString(v11n, "2 Sa1 1"));
        assertEquals(ssa11, VerseFactory.fromString(v11n, "2Sa1:1"));
        assertEquals(ssa11, VerseFactory.fromString(v11n, "2 Sa 1 1"));
        assertEquals(ssa10, VerseFactory.fromString(v11n, "  2  Sa  1  "));
        assertEquals(ssa00, VerseFactory.fromString(v11n, "  2  Sa  "));
        assertEquals(ssa10, VerseFactory.fromString(v11n, "  2  Sa1  "));
        assertEquals(ssa11, VerseFactory.fromString(v11n, "  2  Sa1  1  "));
        assertEquals(ssa11, VerseFactory.fromString(v11n, "  2 : Sa1  1  "));
        assertEquals(rev99, VerseFactory.fromString(v11n, "Rev 22:$"));
        assertEquals(rev99, VerseFactory.fromString(v11n, " Rev 22 ff "));
        assertEquals(rev99, VerseFactory.fromString(v11n, "  Rev  22  ff  "));
        assertEquals(rev99, VerseFactory.fromString(v11n, "  Rev  22  $  "));
        assertEquals(jude9, VerseFactory.fromString(v11n, "Jude $"));
        assertEquals(jude9, VerseFactory.fromString(v11n, " Jude  $ "));
        assertEquals(jude9, VerseFactory.fromString(v11n, "Jude ff"));
        assertEquals(jude9, VerseFactory.fromString(v11n, "  Jude  ff  "));
        assertEquals(VerseFactory.fromString(v11n, "Deu 1:1"), VerseFactory.fromString(v11n, "Dt 1:1"));
        assertEquals(VerseFactory.fromString(v11n, "Mat 1:1"), VerseFactory.fromString(v11n, "Mt 1:1"));
        try {
            VerseFactory.fromString(v11n, "gen 1 1 1");
            fail();
        } catch (NoSuchVerseException ex) {
        }
        try {
            VerseFactory.fromString(v11n, "gen.1.1.1");
            fail();
        } catch (NoSuchVerseException ex) {
        }
        try {
            VerseFactory.fromString(v11n, "gen.1.1:1");
            fail();
        } catch (NoSuchVerseException ex) {
        }
        try {
            VerseFactory.fromString(v11n, "gen 1 1 1");
            fail();
        } catch (NoSuchVerseException ex) {
        }
        try {
            VerseFactory.fromString(v11n, "gen 1 1 1");
            fail();
        } catch (NoSuchVerseException ex) {
        }
        try {
            VerseFactory.fromString(v11n, "gen 1 1 1");
            fail();
        } catch (NoSuchVerseException ex) {
        }
        try {
            VerseFactory.fromString(v11n, null);
            fail();
        } catch (NullPointerException ex) {
        } catch (NoSuchKeyException ex) {
        }
        assertEquals(jude1, VerseFactory.fromString(v11n, "jude 1"));
        assertEquals(jude2, VerseFactory.fromString(v11n, "jude 2"));
        assertEquals(jude9, VerseFactory.fromString(v11n, "jude 25"));
    }

    public void testGetName() throws Exception {
        assertEquals(VerseFactory.fromString(v11n, "Genesis 1 1").getName(), "Gen 1:1");
        assertEquals(VerseFactory.fromString(v11n, "Gen 1 1").getName(), "Gen 1:1");
        assertEquals(VerseFactory.fromString(v11n, "Genesis 1:1").getName(), "Gen 1:1");
        assertEquals(VerseFactory.fromString(v11n, "Gen 1 1").getName(), "Gen 1:1");
        assertEquals(VerseFactory.fromString(v11n, "g 1 1").getName(), "Gen 1:1");
        assertEquals(VerseFactory.fromString(v11n, "g").getName(), "Gen 0:0");
        assertEquals(VerseFactory.fromString(v11n, "G:1:1").getName(), "Gen 1:1");
        assertEquals(VerseFactory.fromString(v11n, "Jude 1").getName(), "Jude 1");
        assertEquals(VerseFactory.fromString(v11n, "Jude").getName(), "Jude 0");
        assertEquals(VerseFactory.fromString(v11n, "Jude 1:1").getName(), "Jude 1");
    }

    public void testGetNameVerse() throws Exception {
        assertEquals(VerseFactory.fromString(v11n, "Gen 1:2").getName(gen11), "2");
        assertEquals(VerseFactory.fromString(v11n, "Gen 2:1").getName(gen11), "2:1");
        assertEquals(VerseFactory.fromString(v11n, "Gen 2:1").getName(jude9), "Gen 2:1");
        assertEquals(VerseFactory.fromString(v11n, "Gen 2:1").getName(null), "Gen 2:1");
    }

    public void testNewViaIntIntIntBoolean() {
        assertEquals(gen11, new Verse(null, 1, 1, true));
        assertEquals(gen11, new Verse(BibleBook.GEN, 0, 1, true));
        assertEquals(gen10, new Verse(BibleBook.GEN, 1, 0, true));
        assertEquals(rev99, new Verse(BibleBook.REV, 22, 22, true));
        assertEquals(rev99, new Verse(BibleBook.REV, 23, 21, true));
        assertEquals(rev99, new Verse(BibleBook.REV, 23, 22, true));
        assertEquals(rev99, new Verse(BibleBook.GEN, 999999, 0, true));
        assertEquals(rev99, new Verse(BibleBook.GEN, 0, 999999, true));
    }

    public void testClone() {
        assertEquals(gen11, gen11.clone());
        assertEquals(gen11, gen11.clone());
        assertEquals(rev99, rev99.clone());
        assertEquals(rev99, rev99.clone());
    }

    public void testEquals() {
        assertTrue(!gen11.equals(null));
        assertTrue(!gen11.equals(Integer.valueOf(0)));
        assertTrue(!gen11.equals("org.crosswire.jsword.passage.Verse"));
        assertTrue(gen11.equals(gen11a));
        assertTrue(!gen11.equals(gen12));
        assertTrue(!gen11.equals(rev99));
        assertTrue(!gen11.equals(gen12));
    }

    public void testHashCode() {
        assertEquals(gen11.hashCode(), gen11a.hashCode());
        assertEquals(gen11.hashCode(), gen11.getOrdinal());
        assertTrue(gen11.hashCode() != gen12.getOrdinal());
        assertTrue(gen11.hashCode() != 0);
    }

    public void testCompareTo() {
        assertEquals(gen11.compareTo(rev99), -1);
        assertEquals(rev99.compareTo(gen11), 1);
        assertEquals(gen11.compareTo(gen11), 0);
    }

    public void testAddSubtract() {
        assertEquals(gen12.subtract(gen11), 1);
        assertEquals(gen11.subtract(gen11), 0);
        assertEquals(gen11.subtract(gen12), -1);
        Verse last = gen11.clone();
        for (int i = 0; i < v11n.maximumOrdinal(); i += 99) {
            Verse next = last.add(i);
            assertEquals(next.subtract(last), i);

            Verse next2 = next.subtract(i);
            assertEquals(gen11.getOsisID(), gen11, next2);
        }
        assertEquals(gen11.getOsisID(), gen11, gen11.subtract(0));
//        assertEquals(gen11.getOsisID(), gen11, gen11.subtract(1));
//        assertEquals(gen11.getOsisID(), gen11, gen11.subtract(2));
        assertEquals(gen11.getOsisID(), gen11, gen11.add(0));
        assertEquals(rev99.getOsisID(), rev99, rev99.add(0));
        assertEquals(rev99.getOsisID(), rev99, rev99.add(1));
        assertEquals(rev99.getOsisID(), rev99, rev99.add(2));
    }

    public void testToString() {
        assertEquals(gen11.toString(), "Gen 1:1");
        assertEquals(gen12.toString(), "Gen 1:2");
        assertEquals(gen21.toString(), "Gen 2:1");
        assertEquals(gen22.toString(), "Gen 2:2");
        assertEquals(rev11.toString(), "Rev 1:1");
        assertEquals(rev12.toString(), "Rev 1:2");
        assertEquals(rev21.toString(), "Rev 2:1");
        assertEquals(rev22.toString(), "Rev 2:2");
        assertEquals(rev99.toString(), "Rev 22:21");
    }

    public void testGetBook() {
        assertEquals(gen11.getBook(), BibleBook.GEN);
        assertEquals(gen12.getBook(), BibleBook.GEN);
        assertEquals(gen21.getBook(), BibleBook.GEN);
        assertEquals(gen22.getBook(), BibleBook.GEN);
        assertEquals(rev11.getBook(), BibleBook.REV);
        assertEquals(rev12.getBook(), BibleBook.REV);
        assertEquals(rev21.getBook(), BibleBook.REV);
        assertEquals(rev22.getBook(), BibleBook.REV);
        assertEquals(rev99.getBook(), BibleBook.REV);
    }

    public void testGetChapter() {
        assertEquals(gen11.getChapter(), 1);
        assertEquals(gen12.getChapter(), 1);
        assertEquals(gen21.getChapter(), 2);
        assertEquals(gen22.getChapter(), 2);
        assertEquals(rev11.getChapter(), 1);
        assertEquals(rev12.getChapter(), 1);
        assertEquals(rev21.getChapter(), 2);
        assertEquals(rev22.getChapter(), 2);
        assertEquals(rev99.getChapter(), 22);
    }

    public void testGetVerse() {
        assertEquals(gen11.getVerse(), 1);
        assertEquals(gen12.getVerse(), 2);
        assertEquals(gen21.getVerse(), 1);
        assertEquals(gen22.getVerse(), 2);
        assertEquals(rev11.getVerse(), 1);
        assertEquals(rev12.getVerse(), 2);
        assertEquals(rev21.getVerse(), 1);
        assertEquals(rev22.getVerse(), 2);
        assertEquals(rev99.getVerse(), 21);
    }

    public void testGetOrdinal() {
        assertEquals(gen11.getOrdinal(), 4);
        assertEquals(gen12.getOrdinal(), 5);
        assertEquals(gen21.getOrdinal(), 36);
        assertEquals(gen22.getOrdinal(), 37);
        assertEquals(rev11.getOrdinal(), 31935);
        assertEquals(rev12.getOrdinal(), 31936);
        assertEquals(rev21.getOrdinal(), 31956);
        assertEquals(rev22.getOrdinal(), 31957);
        assertEquals(rev99.getOrdinal(), 32359);
    }

    public void testGetAccuracy() throws Exception {
        VerseRange vr = new VerseRange(v11n, gen11, gen11);
        assertEquals(AccuracyType.fromText(v11n, "Gen 1:1", AccuracyType.tokenize("Gen 1:1"), vr), AccuracyType.BOOK_VERSE);
        assertEquals(AccuracyType.fromText(v11n, "Gen 1", AccuracyType.tokenize("Gen 1"), vr), AccuracyType.BOOK_CHAPTER);
        assertEquals(AccuracyType.fromText(v11n, "Jude 1", AccuracyType.tokenize("Jude 1"), vr), AccuracyType.BOOK_VERSE);
        assertEquals(AccuracyType.fromText(v11n, "Jude 1:1", AccuracyType.tokenize("Jude 1:1"), vr), AccuracyType.BOOK_VERSE);
        assertEquals(AccuracyType.fromText(v11n, "Gen", AccuracyType.tokenize("Gen"), vr), AccuracyType.BOOK_ONLY);
        assertEquals(AccuracyType.fromText(v11n, "1:1", AccuracyType.tokenize("1:1"), vr), AccuracyType.CHAPTER_VERSE);
        assertEquals(AccuracyType.fromText(v11n, "1", AccuracyType.tokenize("1"), vr), AccuracyType.VERSE_ONLY);
        try {
            AccuracyType.fromText(v11n, "Komplete and utter rubbish", AccuracyType.tokenize("Komplete and utter rubbish"), vr);
            fail();
        } catch (NoSuchVerseException ex) {
        }
        try {
            AccuracyType.fromText(v11n, "x 1 1", AccuracyType.tokenize("x 1 1"), vr);
            fail();
        } catch (NoSuchVerseException ex) {
        }
        try {
            AccuracyType.fromText(v11n, null, (String[]) null, vr);
            fail();
        } catch (NullPointerException ex) {
        }
    }

    public void testIsStartEndOfChapterBook() throws Exception {
        assertTrue(VerseFactory.fromString(v11n, "Gen 1:1").isStartOfChapter());
        assertTrue(!VerseFactory.fromString(v11n, "Gen 1:10").isStartOfChapter());
        assertTrue(!VerseFactory.fromString(v11n, "Gen 1:$").isStartOfChapter());
        assertTrue(VerseFactory.fromString(v11n, "Gen 10:1").isStartOfChapter());
        assertTrue(!VerseFactory.fromString(v11n, "Gen 10:10").isStartOfChapter());
        assertTrue(!VerseFactory.fromString(v11n, "Gen 10:$").isStartOfChapter());
        assertTrue(VerseFactory.fromString(v11n, "Gen $:1").isStartOfChapter());
        assertTrue(!VerseFactory.fromString(v11n, "Gen $:10").isStartOfChapter());
        assertTrue(!VerseFactory.fromString(v11n, "Gen $:$").isStartOfChapter());
        assertTrue(!VerseFactory.fromString(v11n, "Gen 1:1").isEndOfChapter());
        assertTrue(!VerseFactory.fromString(v11n, "Gen 1:10").isEndOfChapter());
        assertTrue(VerseFactory.fromString(v11n, "Gen 1:$").isEndOfChapter());
        assertTrue(!VerseFactory.fromString(v11n, "Gen 10:1").isEndOfChapter());
        assertTrue(!VerseFactory.fromString(v11n, "Gen 10:10").isEndOfChapter());
        assertTrue(VerseFactory.fromString(v11n, "Gen 10:$").isEndOfChapter());
        assertTrue(!VerseFactory.fromString(v11n, "Gen $:1").isEndOfChapter());
        assertTrue(!VerseFactory.fromString(v11n, "Gen $:10").isEndOfChapter());
        assertTrue(VerseFactory.fromString(v11n, "Gen $:$").isEndOfChapter());
        assertTrue(VerseFactory.fromString(v11n, "Gen 1:1").isStartOfBook());
        assertTrue(!VerseFactory.fromString(v11n, "Gen 1:10").isStartOfBook());
        assertTrue(!VerseFactory.fromString(v11n, "Gen 1:$").isStartOfBook());
        assertTrue(!VerseFactory.fromString(v11n, "Gen 10:1").isStartOfBook());
        assertTrue(!VerseFactory.fromString(v11n, "Gen 10:10").isStartOfBook());
        assertTrue(!VerseFactory.fromString(v11n, "Gen 10:$").isStartOfBook());
        assertTrue(!VerseFactory.fromString(v11n, "Gen $:1").isStartOfBook());
        assertTrue(!VerseFactory.fromString(v11n, "Gen $:10").isStartOfBook());
        assertTrue(!VerseFactory.fromString(v11n, "Gen $:$").isStartOfBook());
        assertTrue(!VerseFactory.fromString(v11n, "Gen 1:1").isEndOfBook());
        assertTrue(!VerseFactory.fromString(v11n, "Gen 1:10").isEndOfBook());
        assertTrue(!VerseFactory.fromString(v11n, "Gen 1:$").isEndOfBook());
        assertTrue(!VerseFactory.fromString(v11n, "Gen 10:1").isEndOfBook());
        assertTrue(!VerseFactory.fromString(v11n, "Gen 10:10").isEndOfBook());
        assertTrue(!VerseFactory.fromString(v11n, "Gen 10:$").isEndOfBook());
        assertTrue(!VerseFactory.fromString(v11n, "Gen $:1").isEndOfBook());
        assertTrue(!VerseFactory.fromString(v11n, "Gen $:10").isEndOfBook());
        assertTrue(VerseFactory.fromString(v11n, "Gen $:$").isEndOfBook());
    }

    public void testMax() {
        assertEquals(Verse.max(gen11, gen12), gen12);
        assertEquals(Verse.max(gen11, rev99), rev99);
        assertEquals(Verse.max(gen11, gen11a), gen11);
        assertEquals(Verse.max(gen12, gen11), gen12);
        assertEquals(Verse.max(rev99, gen11), rev99);
        assertEquals(Verse.max(gen11a, gen11), gen11a);
    }

    public void testMin() {
        assertEquals(Verse.min(gen11, gen12), gen11);
        assertEquals(Verse.min(gen11, rev99), gen11);
        assertEquals(Verse.min(gen11, gen11a), gen11);
        assertEquals(Verse.min(gen12, gen11), gen11);
        assertEquals(Verse.min(rev99, gen11), gen11);
        assertEquals(Verse.min(gen11a, gen11a), gen11a);
    }

    public void testToVerseArray() {
        assertEquals(gen11.toVerseArray().length, 1);
    }
}
