/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
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

/**
 * JUnit Test.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class VerseTest extends TestCase
{
    public VerseTest(String s)
    {
        super(s);
    }

    Verse gen11 = null;
    Verse gen11a = null;
    Verse gen12 = null;
    Verse gen21 = null;
    Verse gen22 = null;
    Verse rev11 = null;
    Verse rev12 = null;
    Verse rev21 = null;
    Verse rev22 = null;
    Verse rev99 = null;
    Verse jude1 = null;
    Verse jude2 = null;
    Verse jude9 = null;
    Verse ssa11 = null;
    Verse pro11 = null;
    Verse ch111 = null;

    protected void setUp() throws Exception
    {
        gen11 = new Verse(1, 1, 1);
        gen11a = new Verse(1, 1, 1);
        gen12 = new Verse(1, 1, 2);
        gen21 = new Verse(1, 2, 1);
        gen22 = new Verse(1, 2, 2);
        rev11 = new Verse(66, 1, 1);
        rev12 = new Verse(66, 1, 2);
        rev21 = new Verse(66, 2, 1);
        rev22 = new Verse(66, 2, 2);
        rev99 = new Verse(66, 22, 21);
        jude1 = new Verse(65, 1, 1);
        jude2 = new Verse(65, 1, 2);
        jude9 = new Verse(65, 1, 25);
        ssa11 = new Verse(10, 1, 1);
        pro11 = new Verse(20, 1, 1);
        ch111 = new Verse(13, 1, 1);
    }

    protected void tearDown()
    {
    }

    public void testNewViaString() throws Exception
    {
        assertEquals(gen11, Verse.DEFAULT);
        assertEquals(gen11, VerseFactory.fromString("Genesis 1 1")); //$NON-NLS-1$
        assertEquals(gen11, VerseFactory.fromString("Gen 1 1")); //$NON-NLS-1$
        assertEquals(gen11, VerseFactory.fromString("G 1 1")); //$NON-NLS-1$
        assertEquals(gen11, VerseFactory.fromString("genesis 1 1")); //$NON-NLS-1$
        assertEquals(gen11, VerseFactory.fromString("genesi 1 1")); //$NON-NLS-1$
        assertEquals(gen11, VerseFactory.fromString("GENESIS 1 1")); //$NON-NLS-1$
        assertEquals(gen11, VerseFactory.fromString("GENESI 1 1")); //$NON-NLS-1$
        assertEquals(gen11, VerseFactory.fromString("g 1 1")); //$NON-NLS-1$
        assertEquals(gen11, VerseFactory.fromString("g 1")); //$NON-NLS-1$
        assertEquals(gen11, VerseFactory.fromString("g")); //$NON-NLS-1$
        assertEquals(gen11, VerseFactory.fromString("gen")); //$NON-NLS-1$
        assertEquals(rev99, VerseFactory.fromString("rev 22 21")); //$NON-NLS-1$
        assertEquals(rev99, VerseFactory.fromString("REVE 22 21")); //$NON-NLS-1$
        assertEquals(rev99, VerseFactory.fromString("REVELATIONS 22 21")); //$NON-NLS-1$
        assertEquals(gen21, VerseFactory.fromString("g 2")); //$NON-NLS-1$
        assertEquals(gen11, VerseFactory.fromString("g.1.1")); //$NON-NLS-1$
        assertEquals(gen11, VerseFactory.fromString("g 1.1")); //$NON-NLS-1$
        assertEquals(gen11, VerseFactory.fromString("g.1 1")); //$NON-NLS-1$
        assertEquals(gen11, VerseFactory.fromString("g.1:1")); //$NON-NLS-1$
        assertEquals(gen11, VerseFactory.fromString("g:1:1")); //$NON-NLS-1$
        assertEquals(gen11, VerseFactory.fromString("g:1 1")); //$NON-NLS-1$
        assertEquals(gen11, VerseFactory.fromString(" g 1 1 ")); //$NON-NLS-1$
        assertEquals(gen11, VerseFactory.fromString("gen1")); //$NON-NLS-1$
        assertEquals(gen11, VerseFactory.fromString("GEN1")); //$NON-NLS-1$
        assertEquals(gen11, VerseFactory.fromString("GENESIS1:1")); //$NON-NLS-1$
        assertEquals(gen11, VerseFactory.fromString("G1    1")); //$NON-NLS-1$
        assertEquals(gen11, VerseFactory.fromString(" GEN  1  1  ")); //$NON-NLS-1$
        /* See note in AccuracyType.tokenize()
        assertEquals(gen11, VerseFactory.fromString("gen1v1")); //$NON-NLS-1$
        assertEquals(gen11, VerseFactory.fromString("gen 1 v 1")); //$NON-NLS-1$
        assertEquals(gen11, VerseFactory.fromString("gen 1v1")); //$NON-NLS-1$
        assertEquals(gen11, VerseFactory.fromString("gen 1  v  1")); //$NON-NLS-1$
        assertEquals(gen11, VerseFactory.fromString("gen 1  v  1 ")); //$NON-NLS-1$
        assertEquals(gen11, VerseFactory.fromString(" gen 1  v  1 ")); //$NON-NLS-1$
        assertEquals(gen11, VerseFactory.fromString("gen ch1 1"));
        assertEquals(gen11, VerseFactory.fromString("gen ch 1 1"));
        assertEquals(gen11, VerseFactory.fromString("gen ch  1 1"));
        assertEquals(gen11, VerseFactory.fromString("gen ch1v1"));
        assertEquals(gen11, VerseFactory.fromString(" gen ch 1 v 1 "));
        */
        assertEquals(gen11, VerseFactory.fromString(" gen 1 1 ")); //$NON-NLS-1$
        //assertEquals(pro11, VerseFactory.fromString("proverbs 1v1")); //$NON-NLS-1$
        //assertEquals(ch111, VerseFactory.fromString("1chronicles ch1 1"));
        assertEquals(ssa11, VerseFactory.fromString("2Sa 1:1")); //$NON-NLS-1$
        assertEquals(ssa11, VerseFactory.fromString("2Sa 1 1")); //$NON-NLS-1$
        assertEquals(ssa11, VerseFactory.fromString("2Sa1 1")); //$NON-NLS-1$
        assertEquals(ssa11, VerseFactory.fromString("2 Sa 1 1")); //$NON-NLS-1$
        assertEquals(ssa11, VerseFactory.fromString("2 Sa1 1")); //$NON-NLS-1$
        assertEquals(ssa11, VerseFactory.fromString("2Sa1:1")); //$NON-NLS-1$
        assertEquals(ssa11, VerseFactory.fromString("2 Sa 1 1")); //$NON-NLS-1$
        assertEquals(ssa11, VerseFactory.fromString("  2  Sa  1  ")); //$NON-NLS-1$
        assertEquals(ssa11, VerseFactory.fromString("  2  Sa  ")); //$NON-NLS-1$
        assertEquals(ssa11, VerseFactory.fromString("  2  Sa1  ")); //$NON-NLS-1$
        assertEquals(ssa11, VerseFactory.fromString("  2  Sa1  1  ")); //$NON-NLS-1$
        assertEquals(ssa11, VerseFactory.fromString("  2 : Sa1  1  ")); //$NON-NLS-1$
        assertEquals(rev99, VerseFactory.fromString("Rev 22:$")); //$NON-NLS-1$
        assertEquals(rev99, VerseFactory.fromString(" Rev 22 ff ")); //$NON-NLS-1$
        assertEquals(rev99, VerseFactory.fromString("  Rev  22  ff  ")); //$NON-NLS-1$
        assertEquals(rev99, VerseFactory.fromString("  Rev  22  $  ")); //$NON-NLS-1$
        assertEquals(jude9, VerseFactory.fromString("Jude $")); //$NON-NLS-1$
        assertEquals(jude9, VerseFactory.fromString(" Jude  $ ")); //$NON-NLS-1$
        assertEquals(jude9, VerseFactory.fromString("Jude ff")); //$NON-NLS-1$
        assertEquals(jude9, VerseFactory.fromString("  Jude  ff  ")); //$NON-NLS-1$
        assertEquals(VerseFactory.fromString("Deu 1:1"), VerseFactory.fromString("Dt 1:1")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(VerseFactory.fromString("Mat 1:1"), VerseFactory.fromString("Mt 1:1")); //$NON-NLS-1$ //$NON-NLS-2$
        try
        {
            VerseFactory.fromString("gen 1 1 1"); //$NON-NLS-1$
            fail();
        }
        catch (NoSuchVerseException ex)
        {
        }
        try
        {
            VerseFactory.fromString("gen.1.1.1"); //$NON-NLS-1$
            fail();
        }
        catch (NoSuchVerseException ex)
        {
        }
        try
        {
            VerseFactory.fromString("gen.1.1:1"); //$NON-NLS-1$
            fail();
        }
        catch (NoSuchVerseException ex)
        {
        }
        try
        {
            VerseFactory.fromString("gen 1 1 1"); //$NON-NLS-1$
            fail();
        }
        catch (NoSuchVerseException ex)
        {
        }
        try
        {
            VerseFactory.fromString("gen 1 1 1"); //$NON-NLS-1$
            fail();
        }
        catch (NoSuchVerseException ex)
        {
        }
        try
        {
            VerseFactory.fromString("gen 1 1 1"); //$NON-NLS-1$
            fail();
        }
        catch (NoSuchVerseException ex)
        {
        }
        try
        {
            VerseFactory.fromString(null);
            fail();
        }
        catch (Exception ex)
        {
        }
        assertEquals(jude1, VerseFactory.fromString("jude 1")); //$NON-NLS-1$
        assertEquals(jude2, VerseFactory.fromString("jude 2")); //$NON-NLS-1$
        assertEquals(jude9, VerseFactory.fromString("jude 25")); //$NON-NLS-1$
    }

    public void testGetName() throws Exception
    {
        assertEquals(VerseFactory.fromString("Genesis 1 1").getName(), "Gen 1:1"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(VerseFactory.fromString("Gen 1 1").getName(), "Gen 1:1"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(VerseFactory.fromString("Genesis 1:1").getName(), "Gen 1:1"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(VerseFactory.fromString("Gen 1 1").getName(), "Gen 1:1"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(VerseFactory.fromString("g 1 1").getName(), "Gen 1:1"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(VerseFactory.fromString("g").getName(), "Gen 1:1"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(VerseFactory.fromString("G:1:1").getName(), "Gen 1:1"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(VerseFactory.fromString("Jude 1").getName(), "Jude 1"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(VerseFactory.fromString("Jude").getName(), "Jude 1"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(VerseFactory.fromString("Jude 1:1").getName(), "Jude 1"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testGetNameVerse() throws Exception
    {
        assertEquals(VerseFactory.fromString("Gen 1:2").getName(gen11), "2"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(VerseFactory.fromString("Gen 2:1").getName(gen11), "2:1"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(VerseFactory.fromString("Gen 2:1").getName(jude9), "Gen 2:1"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(VerseFactory.fromString("Gen 2:1").getName(null), "Gen 2:1"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testNewViaIntIntIntBoolean() throws Exception
    {
        assertEquals(gen11, new Verse(0, 1, 1, true));
        assertEquals(gen11, new Verse(1, 0, 1, true));
        assertEquals(gen11, new Verse(1, 1, 0, true));
        assertEquals(rev99, new Verse(66, 22, 22, true));
        assertEquals(rev99, new Verse(66, 23, 21, true));
        assertEquals(rev99, new Verse(66, 23, 22, true));
        assertEquals(rev99, new Verse(67, 22, 21, true));
        assertEquals(rev99, new Verse(67, 22, 22, true));
        assertEquals(rev99, new Verse(999999, 0, 0, true));
        assertEquals(rev99, new Verse(0, 999999, 0, true));
        assertEquals(rev99, new Verse(0, 0, 999999, true));
        try
        {
            new Verse(0, 1, 1);
            fail();
        }
        catch (NoSuchVerseException ex)
        {
        }
        try
        {
            new Verse(1, 0, 1);
            fail();
        }
        catch (NoSuchVerseException ex)
        {
        }
        try
        {
            new Verse(1, 1, 0);
            fail();
        }
        catch (NoSuchVerseException ex)
        {
        }
        try
        {
            new Verse(1, 1, 32);
            fail();
        }
        catch (NoSuchVerseException ex)
        {
        }
        try
        {
            new Verse(1, 51, 1);
            fail();
        }
        catch (NoSuchVerseException ex)
        {
        }
        try
        {
            new Verse(67, 1, 1);
            fail();
        }
        catch (NoSuchVerseException ex)
        {
        }
        try
        {
            new Verse(0, 1, 1, false);
            fail();
        }
        catch (IllegalArgumentException ex)
        {
        }
        try
        {
            new Verse(1, 0, 1, false);
            fail();
        }
        catch (IllegalArgumentException ex)
        {
        }
        try
        {
            new Verse(1, 1, 0, false);
            fail();
        }
        catch (IllegalArgumentException ex)
        {
        }
        try
        {
            new Verse(1, 1, 32, false);
            fail();
        }
        catch (IllegalArgumentException ex)
        {
        }
        try
        {
            new Verse(1, 51, 1, false);
            fail();
        }
        catch (IllegalArgumentException ex)
        {
        }
        try
        {
            new Verse(67, 1, 1, false);
            fail();
        }
        catch (IllegalArgumentException ex)
        {
        }
    }

    public void testClone() throws Exception
    {
        assertEquals(gen11, gen11.clone());
        assertEquals(gen11, gen11.clone());
        assertEquals(rev99, rev99.clone());
        assertEquals(rev99, rev99.clone());
    }

    public void testEquals() throws Exception
    {
        assertTrue(!gen11.equals(null));
        assertTrue(!gen11.equals(new Integer(0)));
        assertTrue(!gen11.equals("org.crosswire.jsword.passage.Verse")); //$NON-NLS-1$
        assertTrue(gen11.equals(gen11a));
        assertTrue(!gen11.equals(gen12));
        assertTrue(!gen11.equals(rev99));
        assertTrue(!gen11.equals(gen12));
    }

    public void testHashCode() throws Exception
    {
        assertEquals(gen11.hashCode(), gen11a.hashCode());
        assertEquals(gen11.hashCode(), gen11.getOrdinal());
        assertTrue(gen11.hashCode() != gen12.getOrdinal());
        assertTrue(gen11.hashCode() != 0);
    }

    public void testCompareTo() throws Exception
    {
        assertEquals(gen11.compareTo(rev99), -1);
        assertEquals(rev99.compareTo(gen11), 1);
        assertEquals(gen11.compareTo(gen11), 0);
    }

    public void testAddSubtract() throws Exception
    {
        assertEquals(gen12.subtract(gen11), 1);
        assertEquals(gen11.subtract(gen11), 0);
        assertEquals(gen11.subtract(gen12), -1);
        Verse last = (Verse) gen11.clone();
        for (int i = 0; i < BibleInfo.versesInBible(); i += 99)
        {
            Verse next = last.add(i);
            assertEquals(next.subtract(last), i);

            Verse next2 = next.subtract(i);
            assertEquals(next2, gen11);
        }
        assertEquals(gen11.subtract(0), gen11);
        assertEquals(gen11.subtract(1), gen11);
        assertEquals(gen11.subtract(2), gen11);
        assertEquals(gen11.add(0), gen11);
        assertEquals(rev99.add(0), rev99);
        assertEquals(rev99.add(1), rev99);
        assertEquals(rev99.add(2), rev99);
    }

    public void testToString() throws Exception
    {
        assertEquals(gen11.toString(), "Gen 1:1"); //$NON-NLS-1$
        assertEquals(gen12.toString(), "Gen 1:2"); //$NON-NLS-1$
        assertEquals(gen21.toString(), "Gen 2:1"); //$NON-NLS-1$
        assertEquals(gen22.toString(), "Gen 2:2"); //$NON-NLS-1$
        assertEquals(rev11.toString(), "Rev 1:1"); //$NON-NLS-1$
        assertEquals(rev12.toString(), "Rev 1:2"); //$NON-NLS-1$
        assertEquals(rev21.toString(), "Rev 2:1"); //$NON-NLS-1$
        assertEquals(rev22.toString(), "Rev 2:2"); //$NON-NLS-1$
        assertEquals(rev99.toString(), "Rev 22:21"); //$NON-NLS-1$
    }

    public void testGetBook() throws Exception
    {
        assertEquals(gen11.getBook(), 1);
        assertEquals(gen12.getBook(), 1);
        assertEquals(gen21.getBook(), 1);
        assertEquals(gen22.getBook(), 1);
        assertEquals(rev11.getBook(), 66);
        assertEquals(rev12.getBook(), 66);
        assertEquals(rev21.getBook(), 66);
        assertEquals(rev22.getBook(), 66);
        assertEquals(rev99.getBook(), 66);
    }

    public void testGetChapter() throws Exception
    {
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

    public void testGetVerse() throws Exception
    {
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

    public void testGetRefArray() throws Exception
    {
        assertEquals(BibleInfo.verseCount(gen11.getRefArray(), new int[]{1, 1, 1}), 1);
        assertEquals(BibleInfo.verseCount(gen12.getRefArray(), new int[]{1, 1, 2}), 1);
        assertEquals(BibleInfo.verseCount(gen21.getRefArray(), new int[]{1, 2, 1}), 1);
        assertEquals(BibleInfo.verseCount(gen22.getRefArray(), new int[]{1, 2, 2}), 1);
        assertEquals(BibleInfo.verseCount(rev11.getRefArray(), new int[]{66, 1, 1}), 1);
        assertEquals(BibleInfo.verseCount(rev12.getRefArray(), new int[]{66, 1, 2}), 1);
        assertEquals(BibleInfo.verseCount(rev21.getRefArray(), new int[]{66, 2, 1}), 1);
        assertEquals(BibleInfo.verseCount(rev22.getRefArray(), new int[]{66, 2, 2}), 1);
        assertEquals(BibleInfo.verseCount(rev99.getRefArray(), new int[]{66, 22, 21}), 1);
    }

    public void testGetOrdinal() throws Exception
    {
        assertEquals(gen11.getOrdinal(), 1);
        assertEquals(gen12.getOrdinal(), 2);
        assertEquals(gen21.getOrdinal(), 32);
        assertEquals(gen22.getOrdinal(), 33);
        assertEquals(rev11.getOrdinal(), 30699);
        assertEquals(rev12.getOrdinal(), 30700);
        assertEquals(rev21.getOrdinal(), 30719);
        assertEquals(rev22.getOrdinal(), 30720);
        assertEquals(rev99.getOrdinal(), 31102);
    }

    public void testGetAccuracy() throws Exception
    {
        VerseRange vr = new VerseRange(gen11, gen11);
        assertEquals(AccuracyType.fromText(AccuracyType.tokenize("Gen 1:1"), vr), AccuracyType.BOOK_VERSE); //$NON-NLS-1$
        assertEquals(AccuracyType.fromText(AccuracyType.tokenize("Gen 1"), vr), AccuracyType.BOOK_CHAPTER); //$NON-NLS-1$
        assertEquals(AccuracyType.fromText(AccuracyType.tokenize("Jude 1"), vr), AccuracyType.BOOK_VERSE); //$NON-NLS-1$
        assertEquals(AccuracyType.fromText(AccuracyType.tokenize("Jude 1:1"), vr), AccuracyType.BOOK_VERSE); //$NON-NLS-1$
        assertEquals(AccuracyType.fromText(AccuracyType.tokenize("Gen"), vr), AccuracyType.BOOK_ONLY); //$NON-NLS-1$
        assertEquals(AccuracyType.fromText(AccuracyType.tokenize("1:1"), vr), AccuracyType.CHAPTER_VERSE); //$NON-NLS-1$
        assertEquals(AccuracyType.fromText(AccuracyType.tokenize("1"), vr), AccuracyType.VERSE_ONLY); //$NON-NLS-1$
        try
        {
            AccuracyType.fromText(AccuracyType.tokenize("complete and utter rubbish"), vr); //$NON-NLS-1$
            fail();
        }
        catch (NoSuchVerseException ex)
        {
        }
        try
        {
            AccuracyType.fromText(AccuracyType.tokenize("b 1 1"), vr); //$NON-NLS-1$
            fail();
        }
        catch (NoSuchVerseException ex)
        {
        }
        try
        {
            AccuracyType.fromText((String[])null, vr);
            fail();
        }
        catch (NullPointerException ex)
        {
        }
    }

    public void testIsStartEndOfChapterBook() throws Exception
    {
        assertTrue(VerseFactory.fromString("Gen 1:1").isStartOfChapter()); //$NON-NLS-1$
        assertTrue(!VerseFactory.fromString("Gen 1:10").isStartOfChapter()); //$NON-NLS-1$
        assertTrue(!VerseFactory.fromString("Gen 1:$").isStartOfChapter()); //$NON-NLS-1$
        assertTrue(VerseFactory.fromString("Gen 10:1").isStartOfChapter()); //$NON-NLS-1$
        assertTrue(!VerseFactory.fromString("Gen 10:10").isStartOfChapter()); //$NON-NLS-1$
        assertTrue(!VerseFactory.fromString("Gen 10:$").isStartOfChapter()); //$NON-NLS-1$
        assertTrue(VerseFactory.fromString("Gen $:1").isStartOfChapter()); //$NON-NLS-1$
        assertTrue(!VerseFactory.fromString("Gen $:10").isStartOfChapter()); //$NON-NLS-1$
        assertTrue(!VerseFactory.fromString("Gen $:$").isStartOfChapter()); //$NON-NLS-1$
        assertTrue(!VerseFactory.fromString("Gen 1:1").isEndOfChapter()); //$NON-NLS-1$
        assertTrue(!VerseFactory.fromString("Gen 1:10").isEndOfChapter()); //$NON-NLS-1$
        assertTrue(VerseFactory.fromString("Gen 1:$").isEndOfChapter()); //$NON-NLS-1$
        assertTrue(!VerseFactory.fromString("Gen 10:1").isEndOfChapter()); //$NON-NLS-1$
        assertTrue(!VerseFactory.fromString("Gen 10:10").isEndOfChapter()); //$NON-NLS-1$
        assertTrue(VerseFactory.fromString("Gen 10:$").isEndOfChapter()); //$NON-NLS-1$
        assertTrue(!VerseFactory.fromString("Gen $:1").isEndOfChapter()); //$NON-NLS-1$
        assertTrue(!VerseFactory.fromString("Gen $:10").isEndOfChapter()); //$NON-NLS-1$
        assertTrue(VerseFactory.fromString("Gen $:$").isEndOfChapter()); //$NON-NLS-1$
        assertTrue(VerseFactory.fromString("Gen 1:1").isStartOfBook()); //$NON-NLS-1$
        assertTrue(!VerseFactory.fromString("Gen 1:10").isStartOfBook()); //$NON-NLS-1$
        assertTrue(!VerseFactory.fromString("Gen 1:$").isStartOfBook()); //$NON-NLS-1$
        assertTrue(!VerseFactory.fromString("Gen 10:1").isStartOfBook()); //$NON-NLS-1$
        assertTrue(!VerseFactory.fromString("Gen 10:10").isStartOfBook()); //$NON-NLS-1$
        assertTrue(!VerseFactory.fromString("Gen 10:$").isStartOfBook()); //$NON-NLS-1$
        assertTrue(!VerseFactory.fromString("Gen $:1").isStartOfBook()); //$NON-NLS-1$
        assertTrue(!VerseFactory.fromString("Gen $:10").isStartOfBook()); //$NON-NLS-1$
        assertTrue(!VerseFactory.fromString("Gen $:$").isStartOfBook()); //$NON-NLS-1$
        assertTrue(!VerseFactory.fromString("Gen 1:1").isEndOfBook()); //$NON-NLS-1$
        assertTrue(!VerseFactory.fromString("Gen 1:10").isEndOfBook()); //$NON-NLS-1$
        assertTrue(!VerseFactory.fromString("Gen 1:$").isEndOfBook()); //$NON-NLS-1$
        assertTrue(!VerseFactory.fromString("Gen 10:1").isEndOfBook()); //$NON-NLS-1$
        assertTrue(!VerseFactory.fromString("Gen 10:10").isEndOfBook()); //$NON-NLS-1$
        assertTrue(!VerseFactory.fromString("Gen 10:$").isEndOfBook()); //$NON-NLS-1$
        assertTrue(!VerseFactory.fromString("Gen $:1").isEndOfBook()); //$NON-NLS-1$
        assertTrue(!VerseFactory.fromString("Gen $:10").isEndOfBook()); //$NON-NLS-1$
        assertTrue(VerseFactory.fromString("Gen $:$").isEndOfBook()); //$NON-NLS-1$
    }

    public void testMax() throws Exception
    {
        assertEquals(Verse.max(gen11, gen12), gen12);
        assertEquals(Verse.max(gen11, rev99), rev99);
        assertEquals(Verse.max(gen11, gen11a), gen11);
        assertEquals(Verse.max(gen12, gen11), gen12);
        assertEquals(Verse.max(rev99, gen11), rev99);
        assertEquals(Verse.max(gen11a, gen11), gen11a);
    }

    public void testMin() throws Exception
    {
        assertEquals(Verse.min(gen11, gen12), gen11);
        assertEquals(Verse.min(gen11, rev99), gen11);
        assertEquals(Verse.min(gen11, gen11a), gen11);
        assertEquals(Verse.min(gen12, gen11), gen11);
        assertEquals(Verse.min(rev99, gen11), gen11);
        assertEquals(Verse.min(gen11a, gen11a), gen11a);
    }

    public void testToVerseArray() throws Exception
    {
        assertEquals(gen11.toVerseArray().length, 1);
    }
}
