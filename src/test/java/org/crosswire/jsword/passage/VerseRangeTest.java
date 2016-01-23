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
 * @author DM Smith
 */
public class VerseRangeTest {
    /** Control the output of names */
    private CaseType storedCase;
    private boolean fullName;

    private Versification v11n;
    private VerseRange rangeGenC1V0to31;
    private VerseRange rangeGenC1V01;
    private VerseRange rangeGenC1V1;
    private VerseRange rangeGenC1V02;
    private VerseRange rangeGenC1V12;
    private VerseRange rangeGenC1V1to31;
    private VerseRange rangeGenC1V0to34;
    private VerseRange rangeGenC1V1to33;
    private VerseRange rangeGenC1V2;
    private VerseRange rangeGenC0V0toEnd;
    private VerseRange rangeGenC0V0toExoC1V1;
    private VerseRange rangeGenC0V0toExoEnd;
    private VerseRange rangeGenC1V1toRevEnd;
    private VerseRange rangeRevC22V0toEnd;
    private VerseRange rangeRevC0V0toEnd;
    private VerseRange rangeRevC22V21;

    private Verse genC0V0;
    private Verse genC1V1;
    private Verse genC1V2;
    private Verse genC1V31;
    private Verse genC2V1;
    private Verse genC50V26;
    private Verse exoC1V1;
    private Verse revC0V0;
    private Verse revC1V1;
    private Verse revC1V2;
    private Verse revC22V21;
    private Verse revC22V0;
    private Verse revC22V1;

    @Before
    public void setUp() throws Exception {
        storedCase = BookName.getDefaultCase();
        BookName.setCase(CaseType.SENTENCE);
        fullName = BookName.isFullBookName();
        BookName.setFullBookName(false);
        // AV11N(DMS): Update test to test all V11Ns
        v11n = Versifications.instance().getVersification("KJV");

        rangeGenC1V0to31 = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 1, 0), 32);
        rangeGenC1V01 = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 1, 0), 2);
        rangeGenC1V1 = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 1, 1), 1);
        rangeGenC1V02 = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 1, 0), 3);
        rangeGenC1V12 = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 1, 1), 2);
        rangeGenC1V1to31 = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 1, 1), 31);
        rangeGenC1V0to34 = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 1, 0), 34);
        rangeGenC1V1to33 = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 1, 1), 33);
        rangeGenC1V2 = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 1, 2), 1);
        rangeGenC0V0toEnd = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 0, 0), 1584);
        rangeGenC0V0toExoC1V1 = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 0, 0), 1587);
        rangeGenC0V0toExoEnd = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 0, 0), 2838);
        rangeGenC1V1toRevEnd = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.GEN, 1, 1), 32356);
        rangeRevC22V0toEnd = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.REV, 22, 0), 22);
        rangeRevC0V0toEnd = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.REV, 0, 0), 428);
        rangeRevC22V21 = RestrictionType.NONE.toRange(v11n, new Verse(v11n, BibleBook.REV, 22, 21), 1);

        genC0V0 = new Verse(v11n, BibleBook.GEN, 0, 0);
        genC1V1 = new Verse(v11n, BibleBook.GEN, 1, 1);
        genC1V2 = new Verse(v11n, BibleBook.GEN, 1, 2);
        genC1V31 = new Verse(v11n, BibleBook.GEN, 1, 31);
        genC2V1 = new Verse(v11n, BibleBook.GEN, 2, 1);
        genC50V26 = new Verse(v11n, BibleBook.GEN, 50, 26);
        exoC1V1 = new Verse(v11n, BibleBook.EXOD, 1, 1);
        revC0V0 = new Verse(v11n, BibleBook.REV, 0, 0);
        revC1V1 = new Verse(v11n, BibleBook.REV, 1, 1);
        revC1V2 = new Verse(v11n, BibleBook.REV, 1, 2);
        revC22V21 = new Verse(v11n, BibleBook.REV, 22, 21);
        revC22V0 = new Verse(v11n, BibleBook.REV, 22, 0);
        revC22V1 = new Verse(v11n, BibleBook.REV, 22, 1);
    }

    @After
    public void tearDown() {
        BookName.setCase(storedCase);
        BookName.setFullBookName(fullName);
    }

    @Test
    public void testNewViaString() throws Exception {
        Assert.assertEquals(rangeGenC1V1, VerseRangeFactory.fromString(v11n, "Gen 1:1-1"));
        Assert.assertEquals(rangeGenC1V12, VerseRangeFactory.fromString(v11n, "Gen 1:1-2"));
        Assert.assertEquals(rangeGenC1V12, VerseRangeFactory.fromString(v11n, "Gen 1:1-1:2"));
        Assert.assertEquals(rangeGenC0V0toEnd, VerseRangeFactory.fromString(v11n, "Gen"));
        Assert.assertEquals(rangeGenC0V0toEnd, VerseRangeFactory.fromString(v11n, "Gen 0:0-50:26"));
        Assert.assertEquals(rangeGenC0V0toEnd, VerseRangeFactory.fromString(v11n, "Gen 0:0-50:$"));
        Assert.assertEquals(rangeGenC0V0toEnd, VerseRangeFactory.fromString(v11n, "Gen 0:0-50:ff"));
        Assert.assertEquals(rangeGenC0V0toEnd, VerseRangeFactory.fromString(v11n, "Gen 0:0-$:26"));
        Assert.assertEquals(rangeGenC0V0toEnd, VerseRangeFactory.fromString(v11n, "Gen 0:0-ff:26"));
        Assert.assertEquals(rangeGenC0V0toEnd, VerseRangeFactory.fromString(v11n, "Gen 0:0-$:$"));
        Assert.assertEquals(rangeGenC0V0toEnd, VerseRangeFactory.fromString(v11n, "Gen 0:0-$:ff"));
        Assert.assertEquals(rangeGenC0V0toEnd, VerseRangeFactory.fromString(v11n, "Gen 0:0-ff:$"));
        Assert.assertEquals(rangeGenC0V0toEnd, VerseRangeFactory.fromString(v11n, "Gen 0:0-ff:ff"));
        Assert.assertEquals(rangeGenC0V0toExoC1V1, VerseRangeFactory.fromString(v11n, "Gen 0:0-Exo 1:1"));
        Assert.assertEquals(rangeGenC0V0toExoEnd, VerseRangeFactory.fromString(v11n, "Gen-Exo"));
        Assert.assertEquals(rangeGenC1V1toRevEnd, VerseRangeFactory.fromString(v11n, "Gen 1:1-Rev 22:21"));
        Assert.assertEquals(rangeGenC1V1toRevEnd, VerseRangeFactory.fromString(v11n, "Gen 1:1-Rev 22:$"));
        Assert.assertEquals(rangeGenC1V1toRevEnd, VerseRangeFactory.fromString(v11n, "Gen 1:1-Rev 22:ff"));
        Assert.assertEquals(rangeGenC1V1, VerseRangeFactory.fromString(v11n, "Gen 1:1"));
        Assert.assertEquals(rangeRevC22V21, VerseRangeFactory.fromString(v11n, "Rev 22:21"));
        Assert.assertEquals(rangeRevC22V21, VerseRangeFactory.fromString(v11n, "Rev 22:21-21"));
        Assert.assertEquals(rangeGenC1V1, VerseRangeFactory.fromString(v11n, "Gen 1:1-Gen 1:1"));
        Assert.assertEquals(rangeGenC1V12, VerseRangeFactory.fromString(v11n, "Gen 1:1-Gen 1:2"));
        Assert.assertEquals(rangeGenC0V0toEnd, VerseRangeFactory.fromString(v11n, "Gen 0:0-50:26"));
        Assert.assertEquals(rangeGenC0V0toEnd, VerseRangeFactory.fromString(v11n, "Gen 0:0-Gen 50:26"));
        Assert.assertEquals(rangeRevC22V21, VerseRangeFactory.fromString(v11n, "Rev 22:21-Rev 22:21"));
        Assert.assertEquals(rangeRevC22V21, VerseRangeFactory.fromString(v11n, "Rev 22:21-Rev 22:ff"));
        Assert.assertEquals(rangeRevC22V21, VerseRangeFactory.fromString(v11n, "Rev 22:21-Rev 22:$"));
        Assert.assertEquals(rangeRevC22V21, VerseRangeFactory.fromString(v11n, "Rev 22:21-$"));
        Assert.assertEquals(rangeRevC22V21, VerseRangeFactory.fromString(v11n, "Rev 22:21-21"));
        Assert.assertEquals(rangeGenC1V1, VerseRangeFactory.fromString(v11n, "Gen 1:1-1:1"));
        Assert.assertEquals(rangeGenC0V0toExoC1V1, VerseRangeFactory.fromString(v11n, "ge 0 0-ex 1 1"));
        Assert.assertEquals(rangeGenC0V0toExoC1V1, VerseRangeFactory.fromString(v11n, "ge -ex 1 1"));
        Assert.assertEquals(rangeGenC0V0toExoC1V1, VerseRangeFactory.fromString(v11n, "Genesis 0:0-ex 1 1"));
        Assert.assertEquals(rangeGenC0V0toExoC1V1, VerseRangeFactory.fromString(v11n, "ge-ex:1:1"));
        try {
            VerseRangeFactory.fromString(v11n, "Gen 1:1-Gen 1:2-Gen 1:3");
            Assert.fail();
        } catch (NoSuchVerseException expected) {
            // This is allowed
        }
        try {
            VerseRangeFactory.fromString(v11n, "Gen 1:1-2-3");
            Assert.fail();
        } catch (NoSuchVerseException expected) {
            // This is allowed
        }
        try {
            VerseRangeFactory.fromString(v11n, "b 1:1-2");
            Assert.fail();
        } catch (NoSuchVerseException expected) {
            // This is allowed
        }
        try {
            VerseRangeFactory.fromString(v11n, "g-f 1 2");
            Assert.fail();
        } catch (NoSuchVerseException expected) {
            // This is allowed
        }
        try {
            VerseRangeFactory.fromString(v11n, (String) null);
            Assert.fail();
        } catch (NoSuchVerseException expected) {
            // This is allowed
        }
        Assert.assertEquals(rangeGenC0V0toEnd, VerseRangeFactory.fromString(v11n, "Gen"));
        Assert.assertEquals(rangeRevC22V0toEnd, VerseRangeFactory.fromString(v11n, "Rev 22"));
        Assert.assertEquals(rangeRevC0V0toEnd, VerseRangeFactory.fromString(v11n, "Rev"));
    }

    @Test
    public void testToString() {
        Assert.assertEquals("Gen 1:1", rangeGenC1V1.toString());
        Assert.assertEquals("Gen 1:1-2", rangeGenC1V12.toString());
        Assert.assertEquals("Gen 1", rangeGenC1V1to31.toString());
        Assert.assertEquals("Gen 1:1-2:1", rangeGenC1V1to33.toString());
        Assert.assertEquals("Gen 1:2", rangeGenC1V2.toString());
        Assert.assertEquals("Gen", rangeGenC0V0toEnd.toString());
        Assert.assertEquals("Gen-Exo 1:1", rangeGenC0V0toExoC1V1.toString());
        Assert.assertEquals("Gen-Rev", rangeGenC1V1toRevEnd.toString());
        Assert.assertEquals("Rev 22", rangeRevC22V0toEnd.toString());
        Assert.assertEquals("Rev", rangeRevC0V0toEnd.toString());
        Assert.assertEquals("Rev 22:21", rangeRevC22V21.toString());
    }

    @Test
    public void testNewViaVerseInt() throws Exception {
        Assert.assertEquals(rangeGenC1V1, RestrictionType.NONE.toRange(v11n, VerseFactory.fromString(v11n, "Gen 1:1"), 1));
    }

    @Test
    public void testNewViaVerseIntBoolean() {
        Assert.assertEquals(rangeGenC1V1toRevEnd, RestrictionType.NONE.toRange(v11n, genC1V1, 999999));
        Assert.assertEquals(rangeGenC1V1, RestrictionType.NONE.toRange(v11n, genC1V1, 0));
        Assert.assertEquals(rangeGenC1V1, RestrictionType.NONE.toRange(v11n, genC1V1, -1));
    }

    @Test
    public void testNewViaVerse() {
        Assert.assertEquals(rangeGenC1V1, new VerseRange(v11n, genC1V1));
        Assert.assertEquals(rangeRevC22V21, new VerseRange(v11n, revC22V21));
    }

    @Test
    public void testNewViaVerseVerse() throws Exception {
        Assert.assertEquals(rangeGenC1V1, new VerseRange(v11n, genC1V1, genC1V1));
        Assert.assertEquals(rangeGenC1V12, new VerseRange(v11n, genC1V1, genC1V2));
        Assert.assertEquals(rangeGenC1V12, new VerseRange(v11n, genC1V2, genC1V1));
        Assert.assertEquals(rangeGenC1V1toRevEnd, new VerseRange(v11n, genC1V1, revC22V21));
        Assert.assertEquals(rangeGenC1V1toRevEnd, new VerseRange(v11n, revC22V21, genC1V1));
        Assert.assertEquals(rangeGenC0V0toEnd, new VerseRange(v11n, genC0V0, genC50V26));
        Assert.assertEquals(rangeGenC0V0toEnd, new VerseRange(v11n, genC50V26, genC0V0));
        Assert.assertEquals(rangeGenC0V0toExoC1V1, new VerseRange(v11n, genC0V0, exoC1V1));
        Assert.assertEquals(rangeGenC0V0toExoC1V1, new VerseRange(v11n, exoC1V1, genC0V0));
        Assert.assertEquals(rangeGenC1V1, new VerseRange(v11n, genC1V1, new Verse(v11n, BibleBook.GEN, 1, 1)));
    }

    @Test
    public void testNewViaVerseIntIntBoolean() {
        Assert.assertEquals(rangeGenC1V1, RestrictionType.CHAPTER.blur(v11n, genC1V1, 0, 0));
        Assert.assertEquals(rangeGenC1V1, RestrictionType.NONE.blur(v11n, genC1V1, 0, 0));
        Assert.assertEquals(rangeGenC1V12, RestrictionType.CHAPTER.blur(v11n, genC1V1, 0, 1));
        Assert.assertEquals(rangeGenC1V12, RestrictionType.NONE.blur(v11n, genC1V1, 0, 1));
        Assert.assertEquals(rangeGenC1V01, RestrictionType.CHAPTER.blur(v11n, genC1V1, 1, 0));
        Assert.assertEquals(rangeGenC1V1, RestrictionType.NONE.blur(v11n, genC1V1, 0, 0));
        Assert.assertEquals(rangeGenC1V01, RestrictionType.CHAPTER.blur(v11n, genC1V1, 9, 0));
        Assert.assertEquals(rangeGenC1V1, RestrictionType.NONE.blur(v11n, genC1V1, 0, 0));
        Assert.assertEquals(rangeRevC22V21, RestrictionType.CHAPTER.blur(v11n, revC22V21, 0, 1));
        Assert.assertEquals(rangeRevC22V21, RestrictionType.NONE.blur(v11n, revC22V21, 0, 1));
        Assert.assertEquals(rangeRevC22V21, RestrictionType.CHAPTER.blur(v11n, revC22V21, 0, 9));
        Assert.assertEquals(rangeRevC22V21, RestrictionType.NONE.blur(v11n, revC22V21, 0, 9));
        Assert.assertEquals(rangeGenC1V1to31, RestrictionType.NONE.blur(v11n, genC1V1, 0, 30));
        Assert.assertEquals(rangeGenC1V1to31, RestrictionType.CHAPTER.blur(v11n, genC1V1, 0, 30));
        Assert.assertEquals(rangeGenC1V0to31, RestrictionType.NONE.blur(v11n, genC1V1, 1, 30));
        Assert.assertEquals(rangeGenC1V0to31, RestrictionType.CHAPTER.blur(v11n, genC1V1, 1, 30));
        Assert.assertEquals(rangeGenC1V1to31, RestrictionType.NONE.blur(v11n, genC1V1, 0, 30));
        Assert.assertEquals(rangeGenC1V0to31, RestrictionType.CHAPTER.blur(v11n, genC1V1, 9, 30));
        Assert.assertEquals(rangeGenC1V1to33, RestrictionType.NONE.blur(v11n, genC1V1, 0, 32));
        Assert.assertEquals(rangeGenC1V1to31, RestrictionType.CHAPTER.blur(v11n, genC1V1, 0, 32));
        Assert.assertEquals(rangeGenC1V0to34, RestrictionType.NONE.blur(v11n, genC1V1, 1, 32));
        Assert.assertEquals(rangeGenC1V0to31, RestrictionType.CHAPTER.blur(v11n, genC1V1, 1, 32));
        Assert.assertEquals(rangeGenC1V1to33, RestrictionType.NONE.blur(v11n, genC1V1, 0, 32));
        Assert.assertEquals(rangeGenC1V0to31, RestrictionType.CHAPTER.blur(v11n, genC1V1, 9, 32));
        Assert.assertEquals(rangeGenC1V1to31, RestrictionType.CHAPTER.blur(v11n, genC1V1, 0, 1581));
        Assert.assertEquals(rangeGenC1V0to31, RestrictionType.CHAPTER.blur(v11n, genC1V1, 3, 1581));
        Assert.assertEquals(rangeGenC1V0to31, RestrictionType.CHAPTER.blur(v11n, genC1V1, 9, 1581));
        Assert.assertEquals(rangeGenC1V1to31, RestrictionType.CHAPTER.blur(v11n, genC1V1, 0, 1533));
        Assert.assertEquals(rangeGenC1V0to31, RestrictionType.CHAPTER.blur(v11n, genC1V1, 1, 1584));
        Assert.assertEquals(rangeGenC1V0to31, RestrictionType.CHAPTER.blur(v11n, genC1V1, 2, 1584));
        Assert.assertEquals(rangeGenC1V1toRevEnd, RestrictionType.NONE.blur(v11n, genC1V1, 0, 32356));
        Assert.assertEquals(rangeGenC1V1to31, RestrictionType.CHAPTER.blur(v11n, genC1V1, 0, 32356));
        Assert.assertEquals(rangeGenC1V1toRevEnd, RestrictionType.NONE.blur(v11n, genC1V1, 0, 32356));
        Assert.assertEquals(rangeGenC1V0to31, RestrictionType.CHAPTER.blur(v11n, genC1V1, 1, 32356));
        Assert.assertEquals(rangeGenC1V1toRevEnd, RestrictionType.NONE.blur(v11n, genC1V1, 0, 32356));
        Assert.assertEquals(rangeGenC1V0to31, RestrictionType.CHAPTER.blur(v11n, genC1V1, 9, 32356));
        Assert.assertEquals(rangeGenC1V1toRevEnd, RestrictionType.NONE.blur(v11n, genC1V1, 0, 32357));
        Assert.assertEquals(rangeGenC1V1to31, RestrictionType.CHAPTER.blur(v11n, genC1V1, 0, 32357));
        Assert.assertEquals(rangeGenC1V1toRevEnd, RestrictionType.NONE.blur(v11n, genC1V1, 0, 32357));
        Assert.assertEquals(rangeGenC1V0to31, RestrictionType.CHAPTER.blur(v11n, genC1V1, 1, 32357));
        Assert.assertEquals(rangeGenC1V1toRevEnd, RestrictionType.NONE.blur(v11n, genC1V1, 0, 32357));
        Assert.assertEquals(rangeGenC1V0to31, RestrictionType.CHAPTER.blur(v11n, genC1V1, 9, 32357));
        Assert.assertEquals(rangeGenC1V1toRevEnd, RestrictionType.NONE.blur(v11n, genC1V1, 0, 99999));
        Assert.assertEquals(rangeGenC1V1to31, RestrictionType.CHAPTER.blur(v11n, genC1V1, 0, 99999));
        Assert.assertEquals(rangeGenC1V1toRevEnd, RestrictionType.NONE.blur(v11n, genC1V1, 0, 99999));
        Assert.assertEquals(rangeGenC1V0to31, RestrictionType.CHAPTER.blur(v11n, genC1V1, 1, 99999));
        Assert.assertEquals(rangeGenC1V1toRevEnd, RestrictionType.NONE.blur(v11n, genC1V1, 0, 99999));
        Assert.assertEquals(rangeGenC1V0to31, RestrictionType.CHAPTER.blur(v11n, genC1V1, 9, 99999));
        Assert.assertEquals(rangeGenC1V12, RestrictionType.CHAPTER.blur(v11n, genC1V2, 1, 0));
        Assert.assertEquals(rangeGenC1V12, RestrictionType.NONE.blur(v11n, genC1V2, 1, 0));
        Assert.assertEquals(rangeGenC1V02, RestrictionType.CHAPTER.blur(v11n, genC1V2, 9, 0));
        Assert.assertEquals(rangeGenC1V12, RestrictionType.NONE.blur(v11n, genC1V2, 1, 0));
        Assert.assertEquals(rangeRevC22V0toEnd, RestrictionType.CHAPTER.blur(v11n, revC22V21, 21, 0));
        Assert.assertEquals(rangeRevC22V0toEnd, RestrictionType.NONE.blur(v11n, revC22V21, 21, 0));
        Assert.assertEquals(rangeRevC22V0toEnd, RestrictionType.CHAPTER.blur(v11n, revC22V21, 21, 1));
        Assert.assertEquals(rangeRevC22V0toEnd, RestrictionType.NONE.blur(v11n, revC22V21, 21, 1));
        Assert.assertEquals(rangeRevC22V0toEnd, RestrictionType.CHAPTER.blur(v11n, revC22V21, 21, 9));
        Assert.assertEquals(rangeRevC22V0toEnd, RestrictionType.NONE.blur(v11n, revC22V21, 21, 9));
        Assert.assertEquals(rangeRevC22V0toEnd, RestrictionType.CHAPTER.blur(v11n, revC22V21, 425, 0));
        Assert.assertEquals(rangeRevC22V0toEnd, RestrictionType.CHAPTER.blur(v11n, revC22V21, 427, 1));
        Assert.assertEquals(rangeRevC22V0toEnd, RestrictionType.CHAPTER.blur(v11n, revC22V21, 427, 9));
        Assert.assertEquals(rangeGenC0V0toEnd, RestrictionType.NONE.blur(v11n, genC1V1, 2, 1581));
        Assert.assertEquals(rangeGenC0V0toEnd, RestrictionType.NONE.blur(v11n, genC1V1, 2, 1581));
        Assert.assertEquals(rangeGenC0V0toExoC1V1, RestrictionType.NONE.blur(v11n, genC1V1, 2, 1584));
        Assert.assertEquals(rangeGenC0V0toExoC1V1, RestrictionType.NONE.blur(v11n, genC1V1, 2, 1584));
        Assert.assertEquals(rangeRevC0V0toEnd, RestrictionType.NONE.blur(v11n, revC22V21, 426, 0));
        Assert.assertEquals(rangeRevC0V0toEnd, RestrictionType.NONE.blur(v11n, revC22V21, 426, 1));
        Assert.assertEquals(rangeRevC0V0toEnd, RestrictionType.NONE.blur(v11n, revC22V21, 426, 9));
    }

    @Test
    public void testNewViaVerseRangeIntIntBoolean() {
        Assert.assertEquals(rangeGenC1V1, RestrictionType.CHAPTER.blur(v11n, rangeGenC1V1, 0, 0));
        Assert.assertEquals(rangeGenC1V1, RestrictionType.NONE.blur(v11n, rangeGenC1V1, 0, 0));
        Assert.assertEquals(rangeGenC1V12, RestrictionType.CHAPTER.blur(v11n, rangeGenC1V1, 0, 1));
        Assert.assertEquals(rangeGenC1V12, RestrictionType.NONE.blur(v11n, rangeGenC1V1, 0, 1));
        Assert.assertEquals(rangeGenC1V01, RestrictionType.CHAPTER.blur(v11n, rangeGenC1V1, 1, 0));
        Assert.assertEquals(rangeGenC1V01, RestrictionType.NONE.blur(v11n, rangeGenC1V1, 1, 0));
        Assert.assertEquals(rangeGenC1V01, RestrictionType.CHAPTER.blur(v11n, rangeGenC1V1, 9, 0));
        Assert.assertEquals(rangeRevC22V21, RestrictionType.CHAPTER.blur(v11n, rangeRevC22V21, 0, 1));
        Assert.assertEquals(rangeRevC22V21, RestrictionType.NONE.blur(v11n, rangeRevC22V21, 0, 1));
        Assert.assertEquals(rangeRevC22V21, RestrictionType.CHAPTER.blur(v11n, rangeRevC22V21, 0, 9));
        Assert.assertEquals(rangeRevC22V21, RestrictionType.NONE.blur(v11n, rangeRevC22V21, 0, 9));
        Assert.assertEquals(rangeGenC1V1to31, RestrictionType.NONE.blur(v11n, rangeGenC1V1, 0, 30));
        Assert.assertEquals(rangeGenC1V1to31, RestrictionType.CHAPTER.blur(v11n, rangeGenC1V1, 0, 30));
        Assert.assertEquals(rangeGenC1V0to31, RestrictionType.NONE.blur(v11n, rangeGenC1V1, 1, 30));
        Assert.assertEquals(rangeGenC1V0to31, RestrictionType.CHAPTER.blur(v11n, rangeGenC1V1, 1, 30));
        Assert.assertEquals(rangeGenC1V0to31, RestrictionType.CHAPTER.blur(v11n, rangeGenC1V1, 9, 30));
        Assert.assertEquals(rangeGenC1V1to33, RestrictionType.NONE.blur(v11n, rangeGenC1V1, 0, 32));
        Assert.assertEquals(rangeGenC1V1to31, RestrictionType.CHAPTER.blur(v11n, rangeGenC1V1, 0, 33));
        Assert.assertEquals(rangeGenC1V0to34, RestrictionType.NONE.blur(v11n, rangeGenC1V1, 1, 32));
        Assert.assertEquals(rangeGenC1V0to31, RestrictionType.CHAPTER.blur(v11n, rangeGenC1V1, 1, 31));
        Assert.assertEquals(rangeGenC1V0to31, RestrictionType.CHAPTER.blur(v11n, rangeGenC1V1, 9, 31));
        Assert.assertEquals(rangeGenC0V0toEnd, RestrictionType.NONE.blur(v11n, rangeGenC1V1, 2, 1581));
        Assert.assertEquals(rangeGenC1V1to31, RestrictionType.CHAPTER.blur(v11n, rangeGenC1V1, 0, 1581));
        Assert.assertEquals(rangeGenC0V0toEnd, RestrictionType.NONE.blur(v11n, rangeGenC1V1, 2, 1581));
        Assert.assertEquals(rangeGenC1V0to31, RestrictionType.CHAPTER.blur(v11n, rangeGenC1V1, 1, 1581));
        Assert.assertEquals(rangeGenC1V0to31, RestrictionType.CHAPTER.blur(v11n, rangeGenC1V1, 9, 1581));
        Assert.assertEquals(rangeGenC0V0toExoC1V1, RestrictionType.NONE.blur(v11n, rangeGenC1V1, 2, 1584));
        Assert.assertEquals(rangeGenC1V1to31, RestrictionType.CHAPTER.blur(v11n, rangeGenC1V1, 0, 1584));
        Assert.assertEquals(rangeGenC0V0toExoC1V1, RestrictionType.NONE.blur(v11n, rangeGenC1V1, 2, 1584));
        Assert.assertEquals(rangeGenC1V0to31, RestrictionType.CHAPTER.blur(v11n, rangeGenC1V1, 1, 1584));
        Assert.assertEquals(rangeGenC1V0to31, RestrictionType.CHAPTER.blur(v11n, rangeGenC1V1, 9, 1584));
        Assert.assertEquals(rangeGenC1V1toRevEnd, RestrictionType.NONE.blur(v11n, rangeGenC1V1, 0, 32356));
        Assert.assertEquals(rangeGenC1V1to31, RestrictionType.CHAPTER.blur(v11n, rangeGenC1V1, 0, 32356));
        Assert.assertEquals(rangeGenC1V1toRevEnd, RestrictionType.NONE.blur(v11n, rangeGenC1V1, 0, 32356));
        Assert.assertEquals(rangeGenC1V0to31, RestrictionType.CHAPTER.blur(v11n, rangeGenC1V1, 1, 32356));
        Assert.assertEquals(rangeGenC1V1toRevEnd, RestrictionType.NONE.blur(v11n, rangeGenC1V1, 0, 32356));
        Assert.assertEquals(rangeGenC1V0to31, RestrictionType.CHAPTER.blur(v11n, rangeGenC1V1, 9, 32356));
        Assert.assertEquals(rangeGenC1V1toRevEnd, RestrictionType.NONE.blur(v11n, rangeGenC1V1, 0, 32357));
        Assert.assertEquals(rangeGenC1V1to31, RestrictionType.CHAPTER.blur(v11n, rangeGenC1V1, 0, 32357));
        Assert.assertEquals(rangeGenC1V1toRevEnd, RestrictionType.NONE.blur(v11n, rangeGenC1V1, 0, 32357));
        Assert.assertEquals(rangeGenC1V0to31, RestrictionType.CHAPTER.blur(v11n, rangeGenC1V1, 1, 32357));
        Assert.assertEquals(rangeGenC1V1toRevEnd, RestrictionType.NONE.blur(v11n, rangeGenC1V1, 0, 32357));
        Assert.assertEquals(rangeGenC1V0to31, RestrictionType.CHAPTER.blur(v11n, rangeGenC1V1, 9, 32357));
        Assert.assertEquals(rangeGenC1V1toRevEnd, RestrictionType.NONE.blur(v11n, rangeGenC1V1, 0, 99999));
        Assert.assertEquals(rangeGenC1V1to31, RestrictionType.CHAPTER.blur(v11n, rangeGenC1V1, 0, 99999));
        Assert.assertEquals(rangeGenC1V1toRevEnd, RestrictionType.NONE.blur(v11n, rangeGenC1V1, 0, 99999));
        Assert.assertEquals(rangeGenC1V0to31, RestrictionType.CHAPTER.blur(v11n, rangeGenC1V1, 1, 99999));
        Assert.assertEquals(rangeGenC1V1toRevEnd, RestrictionType.NONE.blur(v11n, rangeGenC1V1, 0, 99999));
        Assert.assertEquals(rangeGenC1V0to31, RestrictionType.CHAPTER.blur(v11n, rangeGenC1V1, 9, 99999));
        Assert.assertEquals(rangeGenC1V12, RestrictionType.CHAPTER.blur(v11n, rangeGenC1V2, 1, 0));
        Assert.assertEquals(rangeGenC1V12, RestrictionType.NONE.blur(v11n, rangeGenC1V2, 1, 0));
        Assert.assertEquals(rangeGenC1V02, RestrictionType.CHAPTER.blur(v11n, rangeGenC1V2, 9, 0));
        Assert.assertEquals(rangeGenC1V02, RestrictionType.NONE.blur(v11n, rangeGenC1V2, 2, 0));
        Assert.assertEquals(rangeRevC22V0toEnd, RestrictionType.CHAPTER.blur(v11n, rangeRevC22V21, 21, 0));
        Assert.assertEquals(rangeRevC22V0toEnd, RestrictionType.NONE.blur(v11n, rangeRevC22V21, 21, 0));
        Assert.assertEquals(rangeRevC22V0toEnd, RestrictionType.CHAPTER.blur(v11n, rangeRevC22V21, 21, 1));
        Assert.assertEquals(rangeRevC22V0toEnd, RestrictionType.NONE.blur(v11n, rangeRevC22V21, 21, 1));
        Assert.assertEquals(rangeRevC22V0toEnd, RestrictionType.CHAPTER.blur(v11n, rangeRevC22V21, 21, 9));
        Assert.assertEquals(rangeRevC22V0toEnd, RestrictionType.NONE.blur(v11n, rangeRevC22V21, 21, 9));
        Assert.assertEquals(rangeRevC22V0toEnd, RestrictionType.CHAPTER.blur(v11n, rangeRevC22V21, 426, 0));
        Assert.assertEquals(rangeRevC0V0toEnd, RestrictionType.NONE.blur(v11n, rangeRevC22V21, 426, 0));
        Assert.assertEquals(rangeRevC22V0toEnd, RestrictionType.CHAPTER.blur(v11n, rangeRevC22V21, 426, 1));
        Assert.assertEquals(rangeRevC0V0toEnd, RestrictionType.NONE.blur(v11n, rangeRevC22V21, 426, 1));
        Assert.assertEquals(rangeRevC22V0toEnd, RestrictionType.CHAPTER.blur(v11n, rangeRevC22V21, 426, 9));
        Assert.assertEquals(rangeRevC0V0toEnd, RestrictionType.NONE.blur(v11n, rangeRevC22V21, 426, 9));
    }

    @Test
    public void testNewViaVerseRangeVerseRange() {
        Assert.assertEquals(rangeGenC1V1toRevEnd, new VerseRange(rangeGenC1V1, rangeRevC22V0toEnd));
        Assert.assertEquals(rangeGenC1V1toRevEnd, new VerseRange(rangeGenC1V1toRevEnd, rangeRevC22V0toEnd));
        Assert.assertEquals(rangeGenC1V1toRevEnd, new VerseRange(rangeRevC0V0toEnd, rangeGenC1V1toRevEnd));
        Assert.assertEquals(rangeGenC1V1toRevEnd, new VerseRange(rangeGenC1V1toRevEnd, rangeGenC1V1toRevEnd));
        try {
            new VerseRange(rangeGenC1V1toRevEnd, null);
            Assert.fail();
        } catch (NullPointerException expected) {
            // This is allowed
        }
        try {
            new VerseRange((VerseRange) null, rangeGenC1V1toRevEnd);
            Assert.fail();
        } catch (NullPointerException expected) {
            // This is allowed
        }
        try {
            new VerseRange((VerseRange) null, (VerseRange) null);
            Assert.fail();
        } catch (NullPointerException expected) {
            // This is allowed
        }
    }

    @Test
    public void testGetName() {
        Assert.assertEquals("Gen 1:1", rangeGenC1V1.getName());
        Assert.assertEquals("Gen 1:1-2", rangeGenC1V12.getName());
        Assert.assertEquals("Gen 1", rangeGenC1V1to31.getName());
        Assert.assertEquals("Gen 1:1-2:1", rangeGenC1V1to33.getName());
        Assert.assertEquals("Gen 1:2", rangeGenC1V2.getName());
        Assert.assertEquals("Gen", rangeGenC0V0toEnd.getName());
        Assert.assertEquals("Gen-Exo 1:1", rangeGenC0V0toExoC1V1.getName());
        Assert.assertEquals("Gen-Rev", rangeGenC1V1toRevEnd.getName());
        Assert.assertEquals("Rev 22", rangeRevC22V0toEnd.getName());
        Assert.assertEquals("Rev", rangeRevC0V0toEnd.getName());
        Assert.assertEquals("Rev 22:21", rangeRevC22V21.getName());
    }

    @Test
    public void testGetNameVerse() {
        Assert.assertEquals("1-2", rangeGenC1V12.getName(genC1V1));
        Assert.assertEquals("2", rangeGenC1V2.getName(genC1V1));
        Assert.assertEquals("Rev 22", rangeRevC22V0toEnd.getName(genC1V1));
        Assert.assertEquals("Rev 22", rangeRevC22V0toEnd.getName(null));
    }

    @Test
    public void testGetStart() {
        Assert.assertEquals(genC1V1, rangeGenC1V1.getStart());
        Assert.assertEquals(genC1V1, rangeGenC1V12.getStart());
        Assert.assertEquals(genC1V1, rangeGenC1V1to31.getStart());
        Assert.assertEquals(genC1V1, rangeGenC1V1to33.getStart());
        Assert.assertEquals(genC1V2, rangeGenC1V2.getStart());
        Assert.assertEquals(genC0V0, rangeGenC0V0toEnd.getStart());
        Assert.assertEquals(genC0V0, rangeGenC0V0toExoC1V1.getStart());
        Assert.assertEquals(genC1V1, rangeGenC1V1toRevEnd.getStart());
        Assert.assertEquals(revC22V0, rangeRevC22V0toEnd.getStart());
        Assert.assertEquals(revC0V0, rangeRevC0V0toEnd.getStart());
        Assert.assertEquals(revC22V21, rangeRevC22V21.getStart());
    }

    @Test
    public void testGetEnd() {
        Assert.assertEquals(genC1V1, rangeGenC1V1.getEnd());
        Assert.assertEquals(genC1V2, rangeGenC1V12.getEnd());
        Assert.assertEquals(genC1V31, rangeGenC1V1to31.getEnd());
        Assert.assertEquals(genC2V1, rangeGenC1V1to33.getEnd());
        Assert.assertEquals(genC1V2, rangeGenC1V2.getEnd());
        Assert.assertEquals(genC50V26, rangeGenC0V0toEnd.getEnd());
        Assert.assertEquals(exoC1V1, rangeGenC0V0toExoC1V1.getEnd());
        Assert.assertEquals(revC22V21, rangeGenC1V1toRevEnd.getEnd());
        Assert.assertEquals(revC22V21, rangeRevC22V0toEnd.getEnd());
        Assert.assertEquals(revC22V21, rangeRevC0V0toEnd.getEnd());
        Assert.assertEquals(revC22V21, rangeRevC22V21.getEnd());
    }

    @Test
    public void testGetVerseCount() {
        Assert.assertEquals(1, rangeGenC1V1.getCardinality());
        Assert.assertEquals(2, rangeGenC1V12.getCardinality());
        Assert.assertEquals(31, rangeGenC1V1to31.getCardinality());
        Assert.assertEquals(33, rangeGenC1V1to33.getCardinality());
        Assert.assertEquals(1, rangeGenC1V2.getCardinality());
        Assert.assertEquals(1584, rangeGenC0V0toEnd.getCardinality());
        Assert.assertEquals(1587, rangeGenC0V0toExoC1V1.getCardinality());
        Assert.assertEquals(32356, rangeGenC1V1toRevEnd.getCardinality());
        Assert.assertEquals(22, rangeRevC22V0toEnd.getCardinality());
        Assert.assertEquals(427, rangeRevC0V0toEnd.getCardinality());
        Assert.assertEquals(1, rangeRevC22V21.getCardinality());
    }

    @Test
    public void testClone() {
        Assert.assertTrue(rangeGenC1V1 != rangeGenC1V1.clone());
        Assert.assertTrue(rangeGenC1V1.equals(rangeGenC1V1.clone()));
        Assert.assertTrue(rangeRevC22V21 != rangeRevC22V21.clone());
        Assert.assertTrue(rangeRevC22V21.equals(rangeRevC22V21.clone()));
    }

    @Test
    public void testCompareTo() {
        Assert.assertTrue(rangeRevC22V21.compareTo(rangeGenC1V1) > 0);
        Assert.assertTrue(rangeGenC1V1.compareTo(rangeRevC22V21) < 0);
        Assert.assertTrue(rangeGenC1V1.compareTo(rangeGenC1V1) == 0);
        Assert.assertTrue(rangeGenC1V1.compareTo(rangeGenC1V12) < 0);
        Assert.assertTrue(rangeGenC1V1.compareTo(rangeGenC1V1to31) < 0);
        Assert.assertTrue(rangeGenC1V1.compareTo(rangeGenC1V1to33) < 0);
        Assert.assertTrue(rangeGenC1V1.compareTo(rangeGenC0V0toEnd) > 0);
        Assert.assertTrue(rangeGenC1V2.compareTo(rangeGenC1V1) > 0);
        Assert.assertTrue(rangeGenC1V2.compareTo(rangeGenC1V12) > 0);
        Assert.assertTrue(rangeGenC1V2.compareTo(rangeGenC1V1toRevEnd) > 0);
        Assert.assertTrue(rangeGenC1V2.compareTo(rangeGenC1V2) == 0);
        Assert.assertTrue(rangeGenC1V2.compareTo(rangeRevC22V21) < 0);
        Assert.assertTrue(rangeGenC1V2.compareTo(rangeRevC22V0toEnd) < 0);
        try {
            rangeGenC1V2.compareTo(null);
            Assert.fail();
        } catch (NullPointerException ex) {
            // This is allowed
        }
    }

    @Test
    public void testAdjacentTo() throws Exception {
        Assert.assertTrue(!rangeGenC1V1.adjacentTo(rangeRevC22V0toEnd));
        Assert.assertTrue(!rangeGenC1V1.adjacentTo(rangeRevC0V0toEnd));
        Assert.assertTrue(!rangeGenC1V2.adjacentTo(rangeRevC22V0toEnd));
        Assert.assertTrue(!rangeRevC22V21.adjacentTo(rangeGenC1V1));
        Assert.assertTrue(rangeGenC1V1.adjacentTo(rangeGenC1V2));
        Assert.assertTrue(rangeGenC0V0toEnd.adjacentTo(rangeGenC0V0toExoC1V1));
        Assert.assertTrue(rangeGenC0V0toEnd.adjacentTo(rangeGenC1V1toRevEnd));
        Assert.assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:10-11").adjacentTo(VerseRangeFactory.fromString(v11n, "Gen 1:12-13")));
        Assert.assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:10-12").adjacentTo(VerseRangeFactory.fromString(v11n, "Gen 1:11-13")));
        Assert.assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:10-13").adjacentTo(VerseRangeFactory.fromString(v11n, "Gen 1:11-12")));
        Assert.assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:10-13").adjacentTo(VerseRangeFactory.fromString(v11n, "Gen 1:10-13")));
        Assert.assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:11-12").adjacentTo(VerseRangeFactory.fromString(v11n, "Gen 1:10-13")));
        Assert.assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:11-13").adjacentTo(VerseRangeFactory.fromString(v11n, "Gen 1:10-12")));
        Assert.assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:12-13").adjacentTo(VerseRangeFactory.fromString(v11n, "Gen 1:10-11")));
        Assert.assertTrue(!VerseRangeFactory.fromString(v11n, "Gen 1:10-11").adjacentTo(VerseRangeFactory.fromString(v11n, "Gen 1:13-14")));
        Assert.assertTrue(!VerseRangeFactory.fromString(v11n, "Gen 1:13-14").adjacentTo(VerseRangeFactory.fromString(v11n, "Gen 1:10-11")));
        try {
            rangeGenC0V0toEnd.adjacentTo(null);
            Assert.fail();
        } catch (NullPointerException ex) {
            // This is allowed
        }
    }

    @Test
    public void testOverlaps() throws Exception {
        Assert.assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:10-11").overlaps(VerseRangeFactory.fromString(v11n, "Gen 1:11-12")));
        Assert.assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:10-12").overlaps(VerseRangeFactory.fromString(v11n, "Gen 1:11-13")));
        Assert.assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:10-13").overlaps(VerseRangeFactory.fromString(v11n, "Gen 1:11-12")));
        Assert.assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:10-13").overlaps(VerseRangeFactory.fromString(v11n, "Gen 1:10-13")));
        Assert.assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:11-12").overlaps(VerseRangeFactory.fromString(v11n, "Gen 1:10-13")));
        Assert.assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:11-13").overlaps(VerseRangeFactory.fromString(v11n, "Gen 1:10-12")));
        Assert.assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:11-12").overlaps(VerseRangeFactory.fromString(v11n, "Gen 1:10-11")));
        Assert.assertTrue(!VerseRangeFactory.fromString(v11n, "Gen 1:10-11").overlaps(VerseRangeFactory.fromString(v11n, "Gen 1:12-13")));
        Assert.assertTrue(!VerseRangeFactory.fromString(v11n, "Gen 1:12-13").overlaps(VerseRangeFactory.fromString(v11n, "Gen 1:10-11")));
        try {
            rangeGenC0V0toEnd.overlaps(null);
            Assert.fail();
        } catch (NullPointerException ex) {
            // This is allowed
        }
    }

    @Test
    public void testContainsVerse() {
        Assert.assertTrue(rangeGenC0V0toEnd.contains(genC1V1));
        Assert.assertTrue(rangeGenC0V0toEnd.contains(genC1V2));
        Assert.assertTrue(rangeGenC0V0toEnd.contains(genC50V26));
        Assert.assertTrue(rangeGenC1V1.contains(genC1V1));
        Assert.assertTrue(rangeGenC1V12.contains(genC1V1));
        Assert.assertTrue(rangeGenC1V12.contains(genC1V2));
        Assert.assertTrue(rangeGenC1V2.contains(genC1V2));
        Assert.assertTrue(rangeRevC22V0toEnd.contains(revC22V1));
        Assert.assertTrue(rangeRevC22V0toEnd.contains(revC22V21));
        Assert.assertTrue(!rangeRevC22V0toEnd.contains(genC1V1));
        Assert.assertTrue(!rangeGenC1V1.contains(genC1V2));
        Assert.assertTrue(!rangeGenC0V0toEnd.contains(exoC1V1));
        Assert.assertTrue(!rangeRevC22V0toEnd.contains(revC1V1));
        try {
            rangeGenC0V0toEnd.contains((Verse) null);
            Assert.fail();
        } catch (NullPointerException ex) {
            // This is allowed
        }
    }

    @Test
    public void testContainsVerseRange() {
        Assert.assertTrue(rangeGenC0V0toEnd.contains(rangeGenC1V1));
        Assert.assertTrue(rangeGenC0V0toEnd.contains(rangeGenC1V12));
        Assert.assertTrue(rangeGenC0V0toEnd.contains(rangeGenC1V2));
        Assert.assertTrue(rangeGenC1V1.contains(rangeGenC1V1));
        Assert.assertTrue(rangeGenC1V12.contains(rangeGenC1V1));
        Assert.assertTrue(!rangeGenC1V12.contains(rangeGenC0V0toEnd));
        Assert.assertTrue(!rangeGenC1V2.contains(rangeGenC1V12));
        Assert.assertTrue(rangeRevC22V0toEnd.contains(rangeRevC22V0toEnd));
        Assert.assertTrue(!rangeRevC22V0toEnd.contains(rangeGenC0V0toEnd));
        Assert.assertTrue(rangeRevC22V0toEnd.contains(rangeRevC22V21));
        Assert.assertTrue(!rangeGenC1V1.contains(rangeRevC22V0toEnd));
        Assert.assertTrue(rangeGenC0V0toEnd.contains(rangeGenC1V2));
        Assert.assertTrue(!rangeRevC22V0toEnd.contains(rangeRevC0V0toEnd));
        try {
            rangeGenC0V0toEnd.contains((VerseRange) null);
            Assert.fail();
        } catch (NullPointerException ex) {
            // This is allowed
        }
    }

    @Test
    public void testIsChapter() throws Exception {
        Assert.assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1").isWholeChapter());
        Assert.assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:0-ff").isWholeChapter());
        Assert.assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:0-$").isWholeChapter());
        Assert.assertTrue(VerseRangeFactory.fromString(v11n, "Exo 2").isWholeChapter());
        Assert.assertTrue(VerseRangeFactory.fromString(v11n, "Exo 2:0-ff").isWholeChapter());
        Assert.assertTrue(VerseRangeFactory.fromString(v11n, "Exo 2:0-$").isWholeChapter());
        Assert.assertTrue(!VerseRangeFactory.fromString(v11n, "Num 3:1").isWholeChapter());
        Assert.assertTrue(!VerseRangeFactory.fromString(v11n, "Num 4:1-5:1").isWholeChapter());
        Assert.assertTrue(!VerseRangeFactory.fromString(v11n, "Num 5:1-6:ff").isWholeChapter());
        Assert.assertTrue(!VerseRangeFactory.fromString(v11n, "Lev").isWholeChapter());
    }

    @Test
    public void testIsBook() throws Exception {
        Assert.assertTrue(VerseRangeFactory.fromString(v11n, "Gen").isWholeBook());
        Assert.assertTrue(VerseRangeFactory.fromString(v11n, "Gen 0:0-Gen 50:ff").isWholeBook());
        Assert.assertTrue(VerseRangeFactory.fromString(v11n, "Gen 0:0-Gen 50:$").isWholeBook());
        Assert.assertTrue(VerseRangeFactory.fromString(v11n, "Gen 0-50:ff").isWholeBook());
        Assert.assertTrue(!VerseRangeFactory.fromString(v11n, "Num 1:2-Num $:$").isWholeBook());
        Assert.assertTrue(!VerseRangeFactory.fromString(v11n, "Num 4:1-5:1").isWholeBook());
        Assert.assertTrue(!VerseRangeFactory.fromString(v11n, "Num 5:1-6:ff").isWholeBook());
        Assert.assertTrue(!VerseRangeFactory.fromString(v11n, "Lev-Deu 1:1").isWholeBook());
    }

    @Test
    public void testToVerseArray() {
        Assert.assertEquals(1, rangeGenC1V1.toVerseArray().length);
        Assert.assertEquals(2, rangeGenC1V12.toVerseArray().length);
        Assert.assertEquals(31, rangeGenC1V1to31.toVerseArray().length);
        Assert.assertEquals(33, rangeGenC1V1to33.toVerseArray().length);
        Assert.assertEquals(1, rangeGenC1V2.toVerseArray().length);
        Assert.assertEquals(1584, rangeGenC0V0toEnd.toVerseArray().length);
        Assert.assertEquals(1587, rangeGenC0V0toExoC1V1.toVerseArray().length);
        Assert.assertEquals(427, rangeRevC0V0toEnd.toVerseArray().length);
        Assert.assertEquals(genC1V1, rangeGenC1V1.toVerseArray()[0]);
        Assert.assertEquals(genC1V1, rangeGenC1V12.toVerseArray()[0]);
        Assert.assertEquals(genC1V2, rangeGenC1V12.toVerseArray()[1]);
        Assert.assertEquals(genC1V1, rangeGenC1V1to31.toVerseArray()[0]);
        Assert.assertEquals(genC1V31, rangeGenC1V1to31.toVerseArray()[30]);
        Assert.assertEquals(genC1V2, rangeGenC1V2.toVerseArray()[0]);
        Assert.assertEquals(genC1V1, rangeGenC0V0toEnd.toVerseArray()[2]);
        Assert.assertEquals(genC1V1, rangeGenC0V0toExoC1V1.toVerseArray()[2]);
        Assert.assertEquals(exoC1V1, rangeGenC0V0toExoC1V1.toVerseArray()[1586]);
        Assert.assertEquals(revC1V1, rangeRevC0V0toEnd.toVerseArray()[2]);
        Assert.assertEquals(revC1V2, rangeRevC0V0toEnd.toVerseArray()[3]);
        Assert.assertEquals(revC22V21, rangeRevC0V0toEnd.toVerseArray()[426]);
    }

    @Test
    public void testVerseElements() {
        Iterator<Key> it = rangeGenC1V1.iterator();
        while (it.hasNext()) {
            Assert.assertTrue(it.hasNext());
            Verse v = (Verse) it.next();
            Assert.assertEquals(genC1V1, v);
            Assert.assertTrue(!it.hasNext());
        }
        it = rangeGenC1V12.iterator();
        while (it.hasNext()) {
            Assert.assertTrue(it.hasNext());
            Verse v = (Verse) it.next();
            Assert.assertEquals(genC1V1, v);
            Assert.assertTrue(it.hasNext());
            v = (Verse) it.next();
            Assert.assertEquals(genC1V2, v);
            Assert.assertTrue(!it.hasNext());
        }
    }

    /**
     * Test fix related to JS-274 to ensure key.contains(verse) works correctly
     */
    @Test
    public void testKeyContainsVerse() {
        // this passes
        Assert.assertTrue(rangeGenC0V0toEnd.contains(genC1V1));

        // this fails
        Key genAllKey = rangeGenC0V0toEnd;
        Assert.assertTrue(genAllKey.contains(genC1V1));
    }

    @Test
    public void testIntersection() {
        Assert.assertTrue(VerseRange.intersection(rangeGenC0V0toEnd, rangeGenC0V0toExoEnd).contains(rangeGenC0V0toEnd));
    }
}
