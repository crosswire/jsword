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
 * @author DM Smith
 */
public class VerseRangeTest {
    /** Control the output of names */
    private CaseType storedCase;
    private boolean fullName;

    private Versification v11n;
    private VerseRange gen10_9 = null;
    private VerseRange gen10_1 = null;
    private VerseRange gen11_1 = null;
    private VerseRange gen10_2 = null;
    private VerseRange gen11_2 = null;
    private VerseRange gen11_9 = null;
    private VerseRange gen10_a = null;
    private VerseRange gen11_a = null;
    private VerseRange gen12_1 = null;
    private VerseRange gen_all = null;
    private VerseRange gen_ex1 = null;
    private VerseRange gen_exo = null;
    private VerseRange gen_rev = null;
    private VerseRange rev99_9 = null;
    private VerseRange rev11_9 = null;
    private VerseRange rev99_1 = null;

    private Verse gen00 = null;
    private Verse gen11 = null;
    private Verse gen12 = null;
    private Verse gen19 = null;
    private Verse gen21 = null;
    private Verse gen99 = null;
    private Verse exo11 = null;
    private Verse rev00 = null;
    private Verse rev11 = null;
    private Verse rev12 = null;
    private Verse rev99 = null;
    private Verse rev90 = null;
    private Verse rev91 = null;

    @Before
    public void setUp() throws Exception {
        storedCase = BookName.getDefaultCase();
        BookName.setCase(CaseType.SENTENCE);
        fullName = BookName.isFullBookName();
        BookName.setFullBookName(false);
        // AV11N(DMS): Update test to test all V11Ns
        v11n = Versifications.instance().getVersification("KJV");

        gen10_9 = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 1, 0), 32);
        gen10_1 = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 1, 0), 2);
        gen11_1 = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 1, 1), 1);
        gen10_2 = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 1, 0), 3);
        gen11_2 = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 1, 1), 2);
        gen11_9 = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 1, 1), 31);
        gen10_a = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 1, 0), 34);
        gen11_a = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 1, 1), 33);
        gen12_1 = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 1, 2), 1);
        gen_all = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 0, 0), 1584);
        gen_ex1 = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 0, 0), 1587);
        gen_exo = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 0, 0), 2838);
        gen_rev = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 1, 1), 32356);
        rev99_9 = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.REV, 22, 0), 22);
        rev11_9 = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.REV, 0, 0), 428);
        rev99_1 = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.REV, 22, 21), 1);

        gen00 = new Verse(v11n, BibleBook.GEN, 0, 0);
        gen11 = new Verse(v11n, BibleBook.GEN, 1, 1);
        gen12 = new Verse(v11n, BibleBook.GEN, 1, 2);
        gen19 = new Verse(v11n, BibleBook.GEN, 1, 31);
        gen21 = new Verse(v11n, BibleBook.GEN, 2, 1);
        gen99 = new Verse(v11n, BibleBook.GEN, 50, 26);
        exo11 = new Verse(v11n, BibleBook.EXOD, 1, 1);
        rev00 = new Verse(v11n, BibleBook.REV, 0, 0);
        rev11 = new Verse(v11n, BibleBook.REV, 1, 1);
        rev12 = new Verse(v11n, BibleBook.REV, 1, 2);
        rev99 = new Verse(v11n, BibleBook.REV, 22, 21);
        rev90 = new Verse(v11n, BibleBook.REV, 22, 0);
        rev91 = new Verse(v11n, BibleBook.REV, 22, 1);
    }

    @After
    public void tearDown() {
        BookName.setCase(storedCase);
        BookName.setFullBookName(fullName);
    }

    @Test
    public void testNewViaString() throws Exception {
        assertEquals(gen11_1, VerseRangeFactory.fromString(v11n, "Gen 1:1-1"));
        assertEquals(gen11_2, VerseRangeFactory.fromString(v11n, "Gen 1:1-2"));
        assertEquals(gen11_2, VerseRangeFactory.fromString(v11n, "Gen 1:1-1:2"));
        assertEquals(gen_all, VerseRangeFactory.fromString(v11n, "Gen"));
        assertEquals(gen_all, VerseRangeFactory.fromString(v11n, "Gen 0:0-50:26"));
        assertEquals(gen_all, VerseRangeFactory.fromString(v11n, "Gen 0:0-50:$"));
        assertEquals(gen_all, VerseRangeFactory.fromString(v11n, "Gen 0:0-50:ff"));
        assertEquals(gen_all, VerseRangeFactory.fromString(v11n, "Gen 0:0-$:26"));
        assertEquals(gen_all, VerseRangeFactory.fromString(v11n, "Gen 0:0-ff:26"));
        assertEquals(gen_all, VerseRangeFactory.fromString(v11n, "Gen 0:0-$:$"));
        assertEquals(gen_all, VerseRangeFactory.fromString(v11n, "Gen 0:0-$:ff"));
        assertEquals(gen_all, VerseRangeFactory.fromString(v11n, "Gen 0:0-ff:$"));
        assertEquals(gen_all, VerseRangeFactory.fromString(v11n, "Gen 0:0-ff:ff"));
        assertEquals(gen_ex1, VerseRangeFactory.fromString(v11n, "Gen 0:0-Exo 1:1"));
        assertEquals(gen_exo, VerseRangeFactory.fromString(v11n, "Gen-Exo"));
        assertEquals(gen_rev, VerseRangeFactory.fromString(v11n, "Gen 1:1-Rev 22:21"));
        assertEquals(gen_rev, VerseRangeFactory.fromString(v11n, "Gen 1:1-Rev 22:$"));
        assertEquals(gen_rev, VerseRangeFactory.fromString(v11n, "Gen 1:1-Rev 22:ff"));
        assertEquals(gen11_1, VerseRangeFactory.fromString(v11n, "Gen 1:1"));
        assertEquals(rev99_1, VerseRangeFactory.fromString(v11n, "Rev 22:21"));
        assertEquals(rev99_1, VerseRangeFactory.fromString(v11n, "Rev 22:21-21"));
        assertEquals(gen11_1, VerseRangeFactory.fromString(v11n, "Gen 1:1-Gen 1:1"));
        assertEquals(gen11_2, VerseRangeFactory.fromString(v11n, "Gen 1:1-Gen 1:2"));
        assertEquals(gen_all, VerseRangeFactory.fromString(v11n, "Gen 0:0-50:26"));
        assertEquals(gen_all, VerseRangeFactory.fromString(v11n, "Gen 0:0-Gen 50:26"));
        assertEquals(rev99_1, VerseRangeFactory.fromString(v11n, "Rev 22:21-Rev 22:21"));
        assertEquals(rev99_1, VerseRangeFactory.fromString(v11n, "Rev 22:21-Rev 22:ff"));
        assertEquals(rev99_1, VerseRangeFactory.fromString(v11n, "Rev 22:21-Rev 22:$"));
        assertEquals(rev99_1, VerseRangeFactory.fromString(v11n, "Rev 22:21-$"));
        assertEquals(rev99_1, VerseRangeFactory.fromString(v11n, "Rev 22:21-21"));
        assertEquals(gen11_1, VerseRangeFactory.fromString(v11n, "Gen 1:1-1:1"));
        assertEquals(gen_ex1, VerseRangeFactory.fromString(v11n, "ge 0 0-ex 1 1"));
        assertEquals(gen_ex1, VerseRangeFactory.fromString(v11n, "ge -ex 1 1"));
        assertEquals(gen_ex1, VerseRangeFactory.fromString(v11n, "Genesis 0:0-ex 1 1"));
        assertEquals(gen_ex1, VerseRangeFactory.fromString(v11n, "ge-ex:1:1"));
        try {
            VerseRangeFactory.fromString(v11n, "Gen 1:1-Gen 1:2-Gen 1:3");
            fail();
        } catch (NoSuchVerseException ex) {
        }
        try {
            VerseRangeFactory.fromString(v11n, "Gen 1:1-2-3");
            fail();
        } catch (NoSuchVerseException ex) {
        }
        try {
            VerseRangeFactory.fromString(v11n, "b 1:1-2");
            fail();
        } catch (NoSuchVerseException ex) {
        }
        try {
            VerseRangeFactory.fromString(v11n, "g-f 1 2");
            fail();
        } catch (NoSuchVerseException ex) {
        }
        try {
            VerseRangeFactory.fromString(v11n, (String) null);
            fail();
        } catch (NoSuchVerseException ex) {
        }
        assertEquals(gen_all, VerseRangeFactory.fromString(v11n, "Gen"));
        assertEquals(rev99_9, VerseRangeFactory.fromString(v11n, "Rev 22"));
        assertEquals(rev11_9, VerseRangeFactory.fromString(v11n, "Rev"));
    }

    @Test
    public void testToString() {
        assertEquals("Gen 1:1", gen11_1.toString());
        assertEquals("Gen 1:1-2", gen11_2.toString());
        assertEquals("Gen 1", gen11_9.toString());
        assertEquals("Gen 1:1-2:1", gen11_a.toString());
        assertEquals("Gen 1:2", gen12_1.toString());
        assertEquals("Gen", gen_all.toString());
        assertEquals("Gen-Exo 1:1", gen_ex1.toString());
        assertEquals("Gen-Rev", gen_rev.toString());
        assertEquals("Rev 22", rev99_9.toString());
        assertEquals("Rev", rev11_9.toString());
        assertEquals("Rev 22:21", rev99_1.toString());
    }

    @Test
    public void testNewViaVerseInt() throws Exception {
        assertEquals(gen11_1, RestrictionType.NONE.toRange(v11n, VerseFactory.fromString(v11n, "Gen 1:1"), 1));
    }

    @Test
    public void testNewViaVerseIntBoolean() {
        assertEquals(gen_rev, RestrictionType.NONE.toRange(v11n, gen11, 999999));
        assertEquals(gen11_1, RestrictionType.NONE.toRange(v11n, gen11, 0));
        assertEquals(gen11_1, RestrictionType.NONE.toRange(v11n, gen11, -1));
    }

    @Test
    public void testNewViaVerse() {
        assertEquals(gen11_1, new VerseRange(v11n, gen11));
        assertEquals(rev99_1, new VerseRange(v11n, rev99));
    }

    @Test
    public void testNewViaVerseVerse() throws Exception {
        assertEquals(gen11_1, new VerseRange(v11n, gen11, gen11));
        assertEquals(gen11_2, new VerseRange(v11n, gen11, gen12));
        assertEquals(gen11_2, new VerseRange(v11n, gen12, gen11));
        assertEquals(gen_rev, new VerseRange(v11n, gen11, rev99));
        assertEquals(gen_rev, new VerseRange(v11n, rev99, gen11));
        assertEquals(gen_all, new VerseRange(v11n, gen00, gen99));
        assertEquals(gen_all, new VerseRange(v11n, gen99, gen00));
        assertEquals(gen_ex1, new VerseRange(v11n, gen00, exo11));
        assertEquals(gen_ex1, new VerseRange(v11n, exo11, gen00));
        assertEquals(gen11_1, new VerseRange(v11n, gen11, new Verse(v11n, BibleBook.GEN, 1, 1)));
    }

    @Test
    public void testNewViaVerseIntIntBoolean() {
        assertEquals(gen11_1, RestrictionType.CHAPTER.blur(v11n, gen11, 0, 0));
        assertEquals(gen11_1, RestrictionType.NONE.blur(v11n, gen11, 0, 0));
        assertEquals(gen11_2, RestrictionType.CHAPTER.blur(v11n, gen11, 0, 1));
        assertEquals(gen11_2, RestrictionType.NONE.blur(v11n, gen11, 0, 1));
        assertEquals(gen10_1, RestrictionType.CHAPTER.blur(v11n, gen11, 1, 0));
        assertEquals(gen11_1, RestrictionType.NONE.blur(v11n, gen11, 0, 0));
        assertEquals(gen10_1, RestrictionType.CHAPTER.blur(v11n, gen11, 9, 0));
        assertEquals(gen11_1, RestrictionType.NONE.blur(v11n, gen11, 0, 0));
        assertEquals(rev99_1, RestrictionType.CHAPTER.blur(v11n, rev99, 0, 1));
        assertEquals(rev99_1, RestrictionType.NONE.blur(v11n, rev99, 0, 1));
        assertEquals(rev99_1, RestrictionType.CHAPTER.blur(v11n, rev99, 0, 9));
        assertEquals(rev99_1, RestrictionType.NONE.blur(v11n, rev99, 0, 9));
        assertEquals(gen11_9, RestrictionType.NONE.blur(v11n, gen11, 0, 30));
        assertEquals(gen11_9, RestrictionType.CHAPTER.blur(v11n, gen11, 0, 30));
        assertEquals(gen10_9, RestrictionType.NONE.blur(v11n, gen11, 1, 30));
        assertEquals(gen10_9, RestrictionType.CHAPTER.blur(v11n, gen11, 1, 30));
        assertEquals(gen11_9, RestrictionType.NONE.blur(v11n, gen11, 0, 30));
        assertEquals(gen10_9, RestrictionType.CHAPTER.blur(v11n, gen11, 9, 30));
        assertEquals(gen11_a, RestrictionType.NONE.blur(v11n, gen11, 0, 32));
        assertEquals(gen11_9, RestrictionType.CHAPTER.blur(v11n, gen11, 0, 32));
        assertEquals(gen10_a, RestrictionType.NONE.blur(v11n, gen11, 1, 32));
        assertEquals(gen10_9, RestrictionType.CHAPTER.blur(v11n, gen11, 1, 32));
        assertEquals(gen11_a, RestrictionType.NONE.blur(v11n, gen11, 0, 32));
        assertEquals(gen10_9, RestrictionType.CHAPTER.blur(v11n, gen11, 9, 32));
        assertEquals(gen11_9, RestrictionType.CHAPTER.blur(v11n, gen11, 0, 1581));
        assertEquals(gen10_9, RestrictionType.CHAPTER.blur(v11n, gen11, 3, 1581));
        assertEquals(gen10_9, RestrictionType.CHAPTER.blur(v11n, gen11, 9, 1581));
        assertEquals(gen11_9, RestrictionType.CHAPTER.blur(v11n, gen11, 0, 1533));
        assertEquals(gen10_9, RestrictionType.CHAPTER.blur(v11n, gen11, 1, 1584));
        assertEquals(gen10_9, RestrictionType.CHAPTER.blur(v11n, gen11, 2, 1584));
        assertEquals(gen_rev, RestrictionType.NONE.blur(v11n, gen11, 0, 32356));
        assertEquals(gen11_9, RestrictionType.CHAPTER.blur(v11n, gen11, 0, 32356));
        assertEquals(gen_rev, RestrictionType.NONE.blur(v11n, gen11, 0, 32356));
        assertEquals(gen10_9, RestrictionType.CHAPTER.blur(v11n, gen11, 1, 32356));
        assertEquals(gen_rev, RestrictionType.NONE.blur(v11n, gen11, 0, 32356));
        assertEquals(gen10_9, RestrictionType.CHAPTER.blur(v11n, gen11, 9, 32356));
        assertEquals(gen_rev, RestrictionType.NONE.blur(v11n, gen11, 0, 32357));
        assertEquals(gen11_9, RestrictionType.CHAPTER.blur(v11n, gen11, 0, 32357));
        assertEquals(gen_rev, RestrictionType.NONE.blur(v11n, gen11, 0, 32357));
        assertEquals(gen10_9, RestrictionType.CHAPTER.blur(v11n, gen11, 1, 32357));
        assertEquals(gen_rev, RestrictionType.NONE.blur(v11n, gen11, 0, 32357));
        assertEquals(gen10_9, RestrictionType.CHAPTER.blur(v11n, gen11, 9, 32357));
        assertEquals(gen_rev, RestrictionType.NONE.blur(v11n, gen11, 0, 99999));
        assertEquals(gen11_9, RestrictionType.CHAPTER.blur(v11n, gen11, 0, 99999));
        assertEquals(gen_rev, RestrictionType.NONE.blur(v11n, gen11, 0, 99999));
        assertEquals(gen10_9, RestrictionType.CHAPTER.blur(v11n, gen11, 1, 99999));
        assertEquals(gen_rev, RestrictionType.NONE.blur(v11n, gen11, 0, 99999));
        assertEquals(gen10_9, RestrictionType.CHAPTER.blur(v11n, gen11, 9, 99999));
        assertEquals(gen11_2, RestrictionType.CHAPTER.blur(v11n, gen12, 1, 0));
        assertEquals(gen11_2, RestrictionType.NONE.blur(v11n, gen12, 1, 0));
        assertEquals(gen10_2, RestrictionType.CHAPTER.blur(v11n, gen12, 9, 0));
        assertEquals(gen11_2, RestrictionType.NONE.blur(v11n, gen12, 1, 0));
        assertEquals(rev99_9, RestrictionType.CHAPTER.blur(v11n, rev99, 21, 0));
        assertEquals(rev99_9, RestrictionType.NONE.blur(v11n, rev99, 21, 0));
        assertEquals(rev99_9, RestrictionType.CHAPTER.blur(v11n, rev99, 21, 1));
        assertEquals(rev99_9, RestrictionType.NONE.blur(v11n, rev99, 21, 1));
        assertEquals(rev99_9, RestrictionType.CHAPTER.blur(v11n, rev99, 21, 9));
        assertEquals(rev99_9, RestrictionType.NONE.blur(v11n, rev99, 21, 9));
        assertEquals(rev99_9, RestrictionType.CHAPTER.blur(v11n, rev99, 425, 0));
        assertEquals(rev99_9, RestrictionType.CHAPTER.blur(v11n, rev99, 427, 1));
        assertEquals(rev99_9, RestrictionType.CHAPTER.blur(v11n, rev99, 427, 9));
        assertEquals(gen_all, RestrictionType.NONE.blur(v11n, gen11, 2, 1581));
        assertEquals(gen_all, RestrictionType.NONE.blur(v11n, gen11, 2, 1581));
        assertEquals(gen_ex1, RestrictionType.NONE.blur(v11n, gen11, 2, 1584));
        assertEquals(gen_ex1, RestrictionType.NONE.blur(v11n, gen11, 2, 1584));
        assertEquals(rev11_9, RestrictionType.NONE.blur(v11n, rev99, 426, 0));
        assertEquals(rev11_9, RestrictionType.NONE.blur(v11n, rev99, 426, 1));
        assertEquals(rev11_9, RestrictionType.NONE.blur(v11n, rev99, 426, 9));
    }

    @Test
    public void testNewViaVerseRangeIntIntBoolean() {
        assertEquals(gen11_1, RestrictionType.CHAPTER.blur(v11n, gen11_1, 0, 0));
        assertEquals(gen11_1, RestrictionType.NONE.blur(v11n, gen11_1, 0, 0));
        assertEquals(gen11_2, RestrictionType.CHAPTER.blur(v11n, gen11_1, 0, 1));
        assertEquals(gen11_2, RestrictionType.NONE.blur(v11n, gen11_1, 0, 1));
        assertEquals(gen10_1, RestrictionType.CHAPTER.blur(v11n, gen11_1, 1, 0));
        assertEquals(gen10_1, RestrictionType.NONE.blur(v11n, gen11_1, 1, 0));
        assertEquals(gen10_1, RestrictionType.CHAPTER.blur(v11n, gen11_1, 9, 0));
        assertEquals(rev99_1, RestrictionType.CHAPTER.blur(v11n, rev99_1, 0, 1));
        assertEquals(rev99_1, RestrictionType.NONE.blur(v11n, rev99_1, 0, 1));
        assertEquals(rev99_1, RestrictionType.CHAPTER.blur(v11n, rev99_1, 0, 9));
        assertEquals(rev99_1, RestrictionType.NONE.blur(v11n, rev99_1, 0, 9));
        assertEquals(gen11_9, RestrictionType.NONE.blur(v11n, gen11_1, 0, 30));
        assertEquals(gen11_9, RestrictionType.CHAPTER.blur(v11n, gen11_1, 0, 30));
        assertEquals(gen10_9, RestrictionType.NONE.blur(v11n, gen11_1, 1, 30));
        assertEquals(gen10_9, RestrictionType.CHAPTER.blur(v11n, gen11_1, 1, 30));
        assertEquals(gen10_9, RestrictionType.CHAPTER.blur(v11n, gen11_1, 9, 30));
        assertEquals(gen11_a, RestrictionType.NONE.blur(v11n, gen11_1, 0, 32));
        assertEquals(gen11_9, RestrictionType.CHAPTER.blur(v11n, gen11_1, 0, 33));
        assertEquals(gen10_a, RestrictionType.NONE.blur(v11n, gen11_1, 1, 32));
        assertEquals(gen10_9, RestrictionType.CHAPTER.blur(v11n, gen11_1, 1, 31));
        assertEquals(gen10_9, RestrictionType.CHAPTER.blur(v11n, gen11_1, 9, 31));
        assertEquals(gen_all, RestrictionType.NONE.blur(v11n, gen11_1, 2, 1581));
        assertEquals(gen11_9, RestrictionType.CHAPTER.blur(v11n, gen11_1, 0, 1581));
        assertEquals(gen_all, RestrictionType.NONE.blur(v11n, gen11_1, 2, 1581));
        assertEquals(gen10_9, RestrictionType.CHAPTER.blur(v11n, gen11_1, 1, 1581));
        assertEquals(gen10_9, RestrictionType.CHAPTER.blur(v11n, gen11_1, 9, 1581));
        assertEquals(gen_ex1, RestrictionType.NONE.blur(v11n, gen11_1, 2, 1584));
        assertEquals(gen11_9, RestrictionType.CHAPTER.blur(v11n, gen11_1, 0, 1584));
        assertEquals(gen_ex1, RestrictionType.NONE.blur(v11n, gen11_1, 2, 1584));
        assertEquals(gen10_9, RestrictionType.CHAPTER.blur(v11n, gen11_1, 1, 1584));
        assertEquals(gen10_9, RestrictionType.CHAPTER.blur(v11n, gen11_1, 9, 1584));
        assertEquals(gen_rev, RestrictionType.NONE.blur(v11n, gen11_1, 0, 32356));
        assertEquals(gen11_9, RestrictionType.CHAPTER.blur(v11n, gen11_1, 0, 32356));
        assertEquals(gen_rev, RestrictionType.NONE.blur(v11n, gen11_1, 0, 32356));
        assertEquals(gen10_9, RestrictionType.CHAPTER.blur(v11n, gen11_1, 1, 32356));
        assertEquals(gen_rev, RestrictionType.NONE.blur(v11n, gen11_1, 0, 32356));
        assertEquals(gen10_9, RestrictionType.CHAPTER.blur(v11n, gen11_1, 9, 32356));
        assertEquals(gen_rev, RestrictionType.NONE.blur(v11n, gen11_1, 0, 32357));
        assertEquals(gen11_9, RestrictionType.CHAPTER.blur(v11n, gen11_1, 0, 32357));
        assertEquals(gen_rev, RestrictionType.NONE.blur(v11n, gen11_1, 0, 32357));
        assertEquals(gen10_9, RestrictionType.CHAPTER.blur(v11n, gen11_1, 1, 32357));
        assertEquals(gen_rev, RestrictionType.NONE.blur(v11n, gen11_1, 0, 32357));
        assertEquals(gen10_9, RestrictionType.CHAPTER.blur(v11n, gen11_1, 9, 32357));
        assertEquals(gen_rev, RestrictionType.NONE.blur(v11n, gen11_1, 0, 99999));
        assertEquals(gen11_9, RestrictionType.CHAPTER.blur(v11n, gen11_1, 0, 99999));
        assertEquals(gen_rev, RestrictionType.NONE.blur(v11n, gen11_1, 0, 99999));
        assertEquals(gen10_9, RestrictionType.CHAPTER.blur(v11n, gen11_1, 1, 99999));
        assertEquals(gen_rev, RestrictionType.NONE.blur(v11n, gen11_1, 0, 99999));
        assertEquals(gen10_9, RestrictionType.CHAPTER.blur(v11n, gen11_1, 9, 99999));
        assertEquals(gen11_2, RestrictionType.CHAPTER.blur(v11n, gen12_1, 1, 0));
        assertEquals(gen11_2, RestrictionType.NONE.blur(v11n, gen12_1, 1, 0));
        assertEquals(gen10_2, RestrictionType.CHAPTER.blur(v11n, gen12_1, 9, 0));
        assertEquals(gen10_2, RestrictionType.NONE.blur(v11n, gen12_1, 2, 0));
        assertEquals(rev99_9, RestrictionType.CHAPTER.blur(v11n, rev99_1, 21, 0));
        assertEquals(rev99_9, RestrictionType.NONE.blur(v11n, rev99_1, 21, 0));
        assertEquals(rev99_9, RestrictionType.CHAPTER.blur(v11n, rev99_1, 21, 1));
        assertEquals(rev99_9, RestrictionType.NONE.blur(v11n, rev99_1, 21, 1));
        assertEquals(rev99_9, RestrictionType.CHAPTER.blur(v11n, rev99_1, 21, 9));
        assertEquals(rev99_9, RestrictionType.NONE.blur(v11n, rev99_1, 21, 9));
        assertEquals(rev99_9, RestrictionType.CHAPTER.blur(v11n, rev99_1, 426, 0));
        assertEquals(rev11_9, RestrictionType.NONE.blur(v11n, rev99_1, 426, 0));
        assertEquals(rev99_9, RestrictionType.CHAPTER.blur(v11n, rev99_1, 426, 1));
        assertEquals(rev11_9, RestrictionType.NONE.blur(v11n, rev99_1, 426, 1));
        assertEquals(rev99_9, RestrictionType.CHAPTER.blur(v11n, rev99_1, 426, 9));
        assertEquals(rev11_9, RestrictionType.NONE.blur(v11n, rev99_1, 426, 9));
    }

    @Test
    public void testNewViaVerseRangeVerseRange() {
        assertEquals(gen_rev, new VerseRange(gen11_1, rev99_9));
        assertEquals(gen_rev, new VerseRange(gen_rev, rev99_9));
        assertEquals(gen_rev, new VerseRange(rev11_9, gen_rev));
        assertEquals(gen_rev, new VerseRange(gen_rev, gen_rev));
        try {
            new VerseRange(gen_rev, null);
            fail();
        } catch (NullPointerException ex) {
        }
        try {
            new VerseRange((VerseRange) null, gen_rev);
            fail();
        } catch (NullPointerException ex) {
        }
        try {
            new VerseRange((VerseRange) null, (VerseRange) null);
            fail();
        } catch (NullPointerException ex) {
        }
    }

    @Test
    public void testGetName() {
        assertEquals("Gen 1:1", gen11_1.getName());
        assertEquals("Gen 1:1-2", gen11_2.getName());
        assertEquals("Gen 1", gen11_9.getName());
        assertEquals("Gen 1:1-2:1", gen11_a.getName());
        assertEquals("Gen 1:2", gen12_1.getName());
        assertEquals("Gen", gen_all.getName());
        assertEquals("Gen-Exo 1:1", gen_ex1.getName());
        assertEquals("Gen-Rev", gen_rev.getName());
        assertEquals("Rev 22", rev99_9.getName());
        assertEquals("Rev", rev11_9.getName());
        assertEquals("Rev 22:21", rev99_1.getName());
    }

    @Test
    public void testGetNameVerse() {
        assertEquals("1-2", gen11_2.getName(gen11));
        assertEquals("2", gen12_1.getName(gen11));
        assertEquals("Rev 22", rev99_9.getName(gen11));
        assertEquals("Rev 22",rev99_9.getName(null));
    }

    @Test
    public void testGetStart() {
        assertEquals(gen11, gen11_1.getStart());
        assertEquals(gen11, gen11_2.getStart());
        assertEquals(gen11, gen11_9.getStart());
        assertEquals(gen11, gen11_a.getStart());
        assertEquals(gen12, gen12_1.getStart());
        assertEquals(gen00, gen_all.getStart());
        assertEquals(gen00, gen_ex1.getStart());
        assertEquals(gen11, gen_rev.getStart());
        assertEquals(rev90, rev99_9.getStart());
        assertEquals(rev00, rev11_9.getStart());
        assertEquals(rev99, rev99_1.getStart());
    }

    @Test
    public void testGetEnd() {
        assertEquals(gen11, gen11_1.getEnd());
        assertEquals(gen12, gen11_2.getEnd());
        assertEquals(gen19, gen11_9.getEnd());
        assertEquals(gen21, gen11_a.getEnd());
        assertEquals(gen12, gen12_1.getEnd());
        assertEquals(gen99, gen_all.getEnd());
        assertEquals(exo11, gen_ex1.getEnd());
        assertEquals(rev99, gen_rev.getEnd());
        assertEquals(rev99, rev99_9.getEnd());
        assertEquals(rev99, rev11_9.getEnd());
        assertEquals(rev99, rev99_1.getEnd());
    }

    @Test
    public void testGetVerseCount() {
        assertEquals(1, gen11_1.getCardinality());
        assertEquals(2, gen11_2.getCardinality());
        assertEquals(31, gen11_9.getCardinality());
        assertEquals(33, gen11_a.getCardinality());
        assertEquals(1, gen12_1.getCardinality());
        assertEquals(1584, gen_all.getCardinality());
        assertEquals(1587, gen_ex1.getCardinality());
        assertEquals(32356, gen_rev.getCardinality());
        assertEquals(22, rev99_9.getCardinality());
        assertEquals(427, rev11_9.getCardinality());
        assertEquals(1, rev99_1.getCardinality());
    }

    @Test
    public void testClone() {
        assertTrue(gen11_1 != gen11_1.clone());
        assertTrue(gen11_1.equals(gen11_1.clone()));
        assertTrue(rev99_1 != rev99_1.clone());
        assertTrue(rev99_1.equals(rev99_1.clone()));
    }

    @Test
    public void testCompareTo() {
        assertTrue(rev99_1.compareTo(gen11_1) > 0);
        assertTrue(gen11_1.compareTo(rev99_1) < 0);
        assertTrue(gen11_1.compareTo(gen11_1) == 0);
        assertTrue(gen11_1.compareTo(gen11_2) < 0);
        assertTrue(gen11_1.compareTo(gen11_9) < 0);
        assertTrue(gen11_1.compareTo(gen11_a) < 0);
        assertTrue(gen11_1.compareTo(gen_all) > 0);
        assertTrue(gen12_1.compareTo(gen11_1) > 0);
        assertTrue(gen12_1.compareTo(gen11_2) > 0);
        assertTrue(gen12_1.compareTo(gen_rev) > 0);
        assertTrue(gen12_1.compareTo(gen12_1) == 0);
        assertTrue(gen12_1.compareTo(rev99_1) < 0);
        assertTrue(gen12_1.compareTo(rev99_9) < 0);
        try {
            gen12_1.compareTo(null);
            fail();
        } catch (NullPointerException ex) {
        }
    }

    @Test
    public void testAdjacentTo() throws Exception {
        assertTrue(!gen11_1.adjacentTo(rev99_9));
        assertTrue(!gen11_1.adjacentTo(rev11_9));
        assertTrue(!gen12_1.adjacentTo(rev99_9));
        assertTrue(!rev99_1.adjacentTo(gen11_1));
        assertTrue(gen11_1.adjacentTo(gen12_1));
        assertTrue(gen_all.adjacentTo(gen_ex1));
        assertTrue(gen_all.adjacentTo(gen_rev));
        assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:10-11").adjacentTo(VerseRangeFactory.fromString(v11n, "Gen 1:12-13")));
        assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:10-12").adjacentTo(VerseRangeFactory.fromString(v11n, "Gen 1:11-13")));
        assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:10-13").adjacentTo(VerseRangeFactory.fromString(v11n, "Gen 1:11-12")));
        assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:10-13").adjacentTo(VerseRangeFactory.fromString(v11n, "Gen 1:10-13")));
        assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:11-12").adjacentTo(VerseRangeFactory.fromString(v11n, "Gen 1:10-13")));
        assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:11-13").adjacentTo(VerseRangeFactory.fromString(v11n, "Gen 1:10-12")));
        assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:12-13").adjacentTo(VerseRangeFactory.fromString(v11n, "Gen 1:10-11")));
        assertTrue(!VerseRangeFactory.fromString(v11n, "Gen 1:10-11").adjacentTo(VerseRangeFactory.fromString(v11n, "Gen 1:13-14")));
        assertTrue(!VerseRangeFactory.fromString(v11n, "Gen 1:13-14").adjacentTo(VerseRangeFactory.fromString(v11n, "Gen 1:10-11")));
        try {
            gen_all.adjacentTo(null);
            fail();
        } catch (NullPointerException ex) {
        }
    }

    @Test
    public void testOverlaps() throws Exception {
        assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:10-11").overlaps(VerseRangeFactory.fromString(v11n, "Gen 1:11-12")));
        assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:10-12").overlaps(VerseRangeFactory.fromString(v11n, "Gen 1:11-13")));
        assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:10-13").overlaps(VerseRangeFactory.fromString(v11n, "Gen 1:11-12")));
        assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:10-13").overlaps(VerseRangeFactory.fromString(v11n, "Gen 1:10-13")));
        assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:11-12").overlaps(VerseRangeFactory.fromString(v11n, "Gen 1:10-13")));
        assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:11-13").overlaps(VerseRangeFactory.fromString(v11n, "Gen 1:10-12")));
        assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:11-12").overlaps(VerseRangeFactory.fromString(v11n, "Gen 1:10-11")));
        assertTrue(!VerseRangeFactory.fromString(v11n, "Gen 1:10-11").overlaps(VerseRangeFactory.fromString(v11n, "Gen 1:12-13")));
        assertTrue(!VerseRangeFactory.fromString(v11n, "Gen 1:12-13").overlaps(VerseRangeFactory.fromString(v11n, "Gen 1:10-11")));
        try {
            gen_all.overlaps(null);
            fail();
        } catch (NullPointerException ex) {
        }
    }

    @Test
    public void testContainsVerse() {
        assertTrue(gen_all.contains(gen11));
        assertTrue(gen_all.contains(gen12));
        assertTrue(gen_all.contains(gen99));
        assertTrue(gen11_1.contains(gen11));
        assertTrue(gen11_2.contains(gen11));
        assertTrue(gen11_2.contains(gen12));
        assertTrue(gen12_1.contains(gen12));
        assertTrue(rev99_9.contains(rev91));
        assertTrue(rev99_9.contains(rev99));
        assertTrue(!rev99_9.contains(gen11));
        assertTrue(!gen11_1.contains(gen12));
        assertTrue(!gen_all.contains(exo11));
        assertTrue(!rev99_9.contains(rev11));
        try {
            gen_all.contains((Verse) null);
            fail();
        } catch (NullPointerException ex) {
        }
    }

    @Test
    public void testContainsVerseRange() {
        assertTrue(gen_all.contains(gen11_1));
        assertTrue(gen_all.contains(gen11_2));
        assertTrue(gen_all.contains(gen12_1));
        assertTrue(gen11_1.contains(gen11_1));
        assertTrue(gen11_2.contains(gen11_1));
        assertTrue(!gen11_2.contains(gen_all));
        assertTrue(!gen12_1.contains(gen11_2));
        assertTrue(rev99_9.contains(rev99_9));
        assertTrue(!rev99_9.contains(gen_all));
        assertTrue(rev99_9.contains(rev99_1));
        assertTrue(!gen11_1.contains(rev99_9));
        assertTrue(gen_all.contains(gen12_1));
        assertTrue(!rev99_9.contains(rev11_9));
        try {
            gen_all.contains((VerseRange) null);
            fail();
        } catch (NullPointerException ex) {
        }
    }

    @Test
    public void testIsChapter() throws Exception {
        assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1").isWholeChapter());
        assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:0-ff").isWholeChapter());
        assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:0-$").isWholeChapter());
        assertTrue(VerseRangeFactory.fromString(v11n, "Exo 2").isWholeChapter());
        assertTrue(VerseRangeFactory.fromString(v11n, "Exo 2:0-ff").isWholeChapter());
        assertTrue(VerseRangeFactory.fromString(v11n, "Exo 2:0-$").isWholeChapter());
        assertTrue(!VerseRangeFactory.fromString(v11n, "Num 3:1").isWholeChapter());
        assertTrue(!VerseRangeFactory.fromString(v11n, "Num 4:1-5:1").isWholeChapter());
        assertTrue(!VerseRangeFactory.fromString(v11n, "Num 5:1-6:ff").isWholeChapter());
        assertTrue(!VerseRangeFactory.fromString(v11n, "Lev").isWholeChapter());
    }

    @Test
    public void testIsBook() throws Exception {
        assertTrue(VerseRangeFactory.fromString(v11n, "Gen").isWholeBook());
        assertTrue(VerseRangeFactory.fromString(v11n, "Gen 0:0-Gen 50:ff").isWholeBook());
        assertTrue(VerseRangeFactory.fromString(v11n, "Gen 0:0-Gen 50:$").isWholeBook());
        assertTrue(VerseRangeFactory.fromString(v11n, "Gen 0-50:ff").isWholeBook());
        assertTrue(!VerseRangeFactory.fromString(v11n, "Num 1:2-Num $:$").isWholeBook());
        assertTrue(!VerseRangeFactory.fromString(v11n, "Num 4:1-5:1").isWholeBook());
        assertTrue(!VerseRangeFactory.fromString(v11n, "Num 5:1-6:ff").isWholeBook());
        assertTrue(!VerseRangeFactory.fromString(v11n, "Lev-Deu 1:1").isWholeBook());
    }

    @Test
    public void testToVerseArray() {
        assertEquals(1, gen11_1.toVerseArray().length);
        assertEquals(2, gen11_2.toVerseArray().length);
        assertEquals(31, gen11_9.toVerseArray().length);
        assertEquals(33, gen11_a.toVerseArray().length);
        assertEquals(1, gen12_1.toVerseArray().length);
        assertEquals(1584, gen_all.toVerseArray().length);
        assertEquals(1587, gen_ex1.toVerseArray().length);
        assertEquals(427, rev11_9.toVerseArray().length);
        assertEquals(gen11, gen11_1.toVerseArray()[0]);
        assertEquals(gen11, gen11_2.toVerseArray()[0]);
        assertEquals(gen12, gen11_2.toVerseArray()[1]);
        assertEquals(gen11, gen11_9.toVerseArray()[0]);
        assertEquals(gen19, gen11_9.toVerseArray()[30]);
        assertEquals(gen12, gen12_1.toVerseArray()[0]);
        assertEquals(gen11, gen_all.toVerseArray()[2]);
        assertEquals(gen11, gen_ex1.toVerseArray()[2]);
        assertEquals(exo11, gen_ex1.toVerseArray()[1586]);
        assertEquals(rev11, rev11_9.toVerseArray()[2]);
        assertEquals(rev12, rev11_9.toVerseArray()[3]);
        assertEquals(rev99, rev11_9.toVerseArray()[426]);
    }

    @Test
    public void testVerseElements() {
        Iterator<Key> it = gen11_1.iterator();
        while (it.hasNext()) {
            assertTrue(it.hasNext());
            Verse v = (Verse) it.next();
            assertEquals(gen11, v);
            assertTrue(!it.hasNext());
        }
        it = gen11_2.iterator();
        while (it.hasNext()) {
            assertTrue(it.hasNext());
            Verse v = (Verse) it.next();
            assertEquals(gen11, v);
            assertTrue(it.hasNext());
            v = (Verse) it.next();
            assertEquals(gen12, v);
            assertTrue(!it.hasNext());
        }
    }
    

    /**
     * Test fix related to JS-274 to ensure key.contains(verse) works correctly
     */
    @Test
    public void testKeyContainsVerse() {
        // this passes
        assertTrue(gen_all.contains(gen11));
        
        // this fails
        Key gen_allKey = gen_all;
        assertTrue(gen_allKey.contains(gen11));
    }

    @Test
    public void testIntersection() {
        assertTrue(VerseRange.intersection(gen_all, gen_exo).contains(gen_all));
    }
}
