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

import java.util.Iterator;

import junit.framework.TestCase;

import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class VerseRangeTest extends TestCase {
    public VerseRangeTest(String s) {
        super(s);
    }

    private Versification v11n;
    private VerseRange gen11_1 = null;
    private VerseRange gen11_2 = null;
    private VerseRange gen11_9 = null;
    private VerseRange gen11_a = null;
    private VerseRange gen12_1 = null;
    private VerseRange gen_all = null;
    private VerseRange gen_ex1 = null;
    private VerseRange gen_exo = null;
    private VerseRange gen_rev = null;
    private VerseRange rev99_9 = null;
    private VerseRange rev11_9 = null;
    private VerseRange rev99_1 = null;

    private Verse gen11 = null;
    private Verse gen12 = null;
    private Verse gen19 = null;
    private Verse gen21 = null;
    private Verse gen99 = null;
    private Verse exo11 = null;
    private Verse rev11 = null;
    private Verse rev12 = null;
    private Verse rev99 = null;
    private Verse rev91 = null;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        // AV11N(DMS): Update test to test all V11Ns
        v11n = Versifications.instance().getDefaultVersification();

        gen11_1 = RestrictionType.NONE.toRange(v11n, new Verse(BibleBook.GEN, 1, 1), 1);
        gen11_2 = RestrictionType.NONE.toRange(v11n, new Verse(BibleBook.GEN, 1, 1), 2);
        gen11_9 = RestrictionType.NONE.toRange(v11n, new Verse(BibleBook.GEN, 1, 1), 31);
        gen11_a = RestrictionType.NONE.toRange(v11n, new Verse(BibleBook.GEN, 1, 1), 33);
        gen12_1 = RestrictionType.NONE.toRange(v11n, new Verse(BibleBook.GEN, 1, 2), 1);
        gen_all = RestrictionType.NONE.toRange(v11n, new Verse(BibleBook.GEN, 1, 1), 1582);
        gen_ex1 = RestrictionType.NONE.toRange(v11n, new Verse(BibleBook.GEN, 1, 1), 1585);
        gen_exo = RestrictionType.NONE.toRange(v11n, new Verse(BibleBook.GEN, 1, 1), 2746);
        gen_rev = RestrictionType.NONE.toRange(v11n, new Verse(BibleBook.GEN, 1, 1), 32356);
        rev99_9 = RestrictionType.NONE.toRange(v11n, new Verse(BibleBook.REV, 22, 1), 21);
        rev11_9 = RestrictionType.NONE.toRange(v11n, new Verse(BibleBook.REV, 1, 1), 425);
        rev99_1 = RestrictionType.NONE.toRange(v11n, new Verse(BibleBook.REV, 22, 21), 1);

        gen11 = new Verse(BibleBook.GEN, 1, 1);
        gen12 = new Verse(BibleBook.GEN, 1, 2);
        gen19 = new Verse(BibleBook.GEN, 1, 31);
        gen21 = new Verse(BibleBook.GEN, 2, 1);
        gen99 = new Verse(BibleBook.GEN, 50, 26);
        exo11 = new Verse(BibleBook.EXOD, 1, 1);
        rev11 = new Verse(BibleBook.REV, 1, 1);
        rev12 = new Verse(BibleBook.REV, 1, 2);
        rev99 = new Verse(BibleBook.REV, 22, 21);
        rev91 = new Verse(BibleBook.REV, 22, 1);
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
        assertEquals(gen11_1, VerseRangeFactory.fromString(v11n, "Gen 1:1-1"));
        assertEquals(gen11_2, VerseRangeFactory.fromString(v11n, "Gen 1:1-2"));
        assertEquals(gen11_2, VerseRangeFactory.fromString(v11n, "Gen 1:1-1:2"));
        assertEquals(gen_all, VerseRangeFactory.fromString(v11n, "Gen"));
        assertEquals(gen_all, VerseRangeFactory.fromString(v11n, "Gen 1:1-50:26"));
        assertEquals(gen_all, VerseRangeFactory.fromString(v11n, "Gen 1:1-50:$"));
        assertEquals(gen_all, VerseRangeFactory.fromString(v11n, "Gen 1:1-50:ff"));
        assertEquals(gen_all, VerseRangeFactory.fromString(v11n, "Gen 1:1-$:26"));
        assertEquals(gen_all, VerseRangeFactory.fromString(v11n, "Gen 1:1-ff:26"));
        assertEquals(gen_all, VerseRangeFactory.fromString(v11n, "Gen 1:1-$:$"));
        assertEquals(gen_all, VerseRangeFactory.fromString(v11n, "Gen 1:1-$:ff"));
        assertEquals(gen_all, VerseRangeFactory.fromString(v11n, "Gen 1:1-ff:$"));
        assertEquals(gen_all, VerseRangeFactory.fromString(v11n, "Gen 1:1-ff:ff"));
        assertEquals(gen_ex1, VerseRangeFactory.fromString(v11n, "Gen 1:1-Exo 1:1"));
        assertEquals(gen_exo, VerseRangeFactory.fromString(v11n, "Gen-Exo"));
        assertEquals(gen_rev, VerseRangeFactory.fromString(v11n, "Gen 1:1-Rev 22:21"));
        assertEquals(gen_rev, VerseRangeFactory.fromString(v11n, "Gen 1:1-Rev 22:$"));
        assertEquals(gen_rev, VerseRangeFactory.fromString(v11n, "Gen 1:1-Rev 22:ff"));
        assertEquals(gen11_1, VerseRangeFactory.fromString(v11n, "Gen 1:1"));
        assertEquals(rev99_1, VerseRangeFactory.fromString(v11n, "Rev 22:21"));
        assertEquals(rev99_1, VerseRangeFactory.fromString(v11n, "Rev 22:21-21"));
        assertEquals(gen11_1, VerseRangeFactory.fromString(v11n, "Gen 1:1-Gen 1:1"));
        assertEquals(gen11_2, VerseRangeFactory.fromString(v11n, "Gen 1:1-Gen 1:2"));
        assertEquals(gen_all, VerseRangeFactory.fromString(v11n, "Gen 1:1-50:26"));
        assertEquals(gen_all, VerseRangeFactory.fromString(v11n, "Gen 1:1-Gen 50:26"));
        assertEquals(rev99_1, VerseRangeFactory.fromString(v11n, "Rev 22:21-Rev 22:21"));
        assertEquals(rev99_1, VerseRangeFactory.fromString(v11n, "Rev 22:21-Rev 22:ff"));
        assertEquals(rev99_1, VerseRangeFactory.fromString(v11n, "Rev 22:21-Rev 22:$"));
        assertEquals(rev99_1, VerseRangeFactory.fromString(v11n, "Rev 22:21-$"));
        assertEquals(rev99_1, VerseRangeFactory.fromString(v11n, "Rev 22:21-21"));
        assertEquals(gen11_1, VerseRangeFactory.fromString(v11n, "Gen 1:1-1:1"));
        assertEquals(gen_ex1, VerseRangeFactory.fromString(v11n, "g 1 1-e 1 1"));
        assertEquals(gen_ex1, VerseRangeFactory.fromString(v11n, "g 1-e 1 1"));
        assertEquals(gen_ex1, VerseRangeFactory.fromString(v11n, "Genesis 1:1-e 1 1"));
        assertEquals(gen_ex1, VerseRangeFactory.fromString(v11n, "g-e:1:1"));
        try {
            VerseRangeFactory.fromString(v11n, "Gen 1:1-Gen 1:2-Gen 1:3");fail();}
        catch (NoSuchVerseException ex) {
        }
        try {
            VerseRangeFactory.fromString(v11n, "Gen 1:1-2-3");fail();}
        catch (NoSuchVerseException ex) {
        }
        try {
            VerseRangeFactory.fromString(v11n, "b 1:1-2");fail();}
        catch (NoSuchVerseException ex) {
        }
        try {
            VerseRangeFactory.fromString(v11n, "g-f 1 2");fail();}
        catch (NoSuchVerseException ex) {
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

    public void testToString() {
        assertEquals(gen11_1.toString(), "Gen 1:1");
        assertEquals(gen11_2.toString(), "Gen 1:1-2");
        assertEquals(gen11_9.toString(), "Gen 1");
        assertEquals(gen11_a.toString(), "Gen 1:1-2:1");
        assertEquals(gen12_1.toString(), "Gen 1:2");
        assertEquals(gen_all.toString(), "Gen");
        assertEquals(gen_ex1.toString(), "Gen 1:1-Exo 1:1");
        assertEquals(gen_rev.toString(), "Gen-Rev");
        assertEquals(rev99_9.toString(), "Rev 22");
        assertEquals(rev11_9.toString(), "Rev");
        assertEquals(rev99_1.toString(), "Rev 22:21");
    }

    public void testPersistentNaming() throws Exception {
        PassageUtil.setPersistentNaming(false);
        assertEquals(VerseRangeFactory.fromString(v11n, "1corinth 8-9").toString(), "1Cor 8-9");
        assertEquals(VerseRangeFactory.fromString(v11n, "Genesis 1 1").toString(), "Gen 1:1");
        assertEquals(VerseRangeFactory.fromString(v11n, "g 1 1-e 1 1").toString(), "Gen 1:1-Exo 1:1");
        assertEquals(VerseRangeFactory.fromString(v11n, "g-e:1:10").toString(), "Gen 1:1-Exo 1:10");
        assertEquals(VerseRangeFactory.fromString(v11n, "g 1-e 2").toString(), "Gen 1-Exo 2");
        PassageUtil.setPersistentNaming(true);
        assertEquals(VerseRangeFactory.fromString(v11n, "Genesis 1 1").toString(), "Genesis 1 1");
        assertEquals(VerseRangeFactory.fromString(v11n, "g 1 1-e 1 1").toString(), "g 1 1-e 1 1");
        assertEquals(VerseRangeFactory.fromString(v11n, "g-e:1:1").toString(), "g-e:1:1");
        assertEquals(VerseRangeFactory.fromString(v11n, "g 1-e 2").toString(), "g 1-e 2");
        PassageUtil.setPersistentNaming(false);
    }

    public void testNewViaVerseInt() throws Exception {
        assertEquals(gen11_1, RestrictionType.NONE.toRange(v11n, VerseFactory.fromString(v11n, "Gen 1:1"), 1));
    }

    public void testNewViaVerseIntBoolean() {
        assertEquals(gen_rev, RestrictionType.NONE.toRange(v11n, gen11, 999999));
        assertEquals(gen11_1, RestrictionType.NONE.toRange(v11n, gen11, 0));
        assertEquals(gen11_1, RestrictionType.NONE.toRange(v11n, gen11, -1));
    }

    public void testNewViaVerse() {
        assertEquals(gen11_1, new VerseRange(v11n, gen11));
        assertEquals(rev99_1, new VerseRange(v11n, rev99));
    }

    public void testNewViaVerseVerse() throws Exception {
        assertEquals(gen11_1, new VerseRange(v11n, gen11, gen11));
        assertEquals(gen11_2, new VerseRange(v11n, gen11, gen12));
        assertEquals(gen11_2, new VerseRange(v11n, gen12, gen11));
        assertEquals(gen_rev, new VerseRange(v11n, gen11, rev99));
        assertEquals(gen_rev, new VerseRange(v11n, rev99, gen11));
        assertEquals(gen_all, new VerseRange(v11n, gen11, gen99));
        assertEquals(gen_all, new VerseRange(v11n, gen99, gen11));
        assertEquals(gen_ex1, new VerseRange(v11n, gen11, exo11));
        assertEquals(gen_ex1, new VerseRange(v11n, exo11, gen11));
        assertEquals(gen11_1, new VerseRange(v11n, gen11, new Verse(BibleBook.GEN, 1, 1)));
    }

    public void testNewViaVerseIntIntBoolean() {
        assertEquals(gen11_1.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11, 0, 0).getOsisRef());
        assertEquals(gen11_1.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11, 0, 0).getOsisRef());
        assertEquals(gen11_2.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11, 0, 1).getOsisRef());
        assertEquals(gen11_2.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11, 0, 1).getOsisRef());
        assertEquals(gen11_1.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11, 1, 0).getOsisRef());
        assertEquals(gen11_1.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11, 0, 0).getOsisRef());
        assertEquals(gen11_1.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11, 9, 0).getOsisRef());
        assertEquals(gen11_1.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11, 0, 0).getOsisRef());
        assertEquals(rev99_1.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, rev99, 0, 1).getOsisRef());
        assertEquals(rev99_1.getOsisRef(), RestrictionType.NONE.blur(v11n, rev99, 0, 1).getOsisRef());
        assertEquals(rev99_1.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, rev99, 0, 9).getOsisRef());
        assertEquals(rev99_1.getOsisRef(), RestrictionType.NONE.blur(v11n, rev99, 0, 9).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11, 0, 30).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11, 0, 30).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11, 1, 30).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11, 1, 30).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11, 0, 30).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11, 9, 30).getOsisRef());
        assertEquals(gen11_a.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11, 0, 32).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11, 0, 32).getOsisRef());
        assertEquals(gen11_a.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11, 1, 32).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11, 1, 32).getOsisRef());
        assertEquals(gen11_a.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11, 0, 32).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11, 9, 32).getOsisRef());
        assertEquals(gen_all.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11, 0, 1581).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11, 0, 1581).getOsisRef());
        assertEquals(gen_all.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11, 1, 1581).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11, 1, 1581).getOsisRef());
        assertEquals(gen_all.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11, 0, 1581).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11, 9, 1581).getOsisRef());
        assertEquals(gen_ex1.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11, 0, 1584).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11, 0, 1533).getOsisRef());
        assertEquals(gen_ex1.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11, 1, 1584).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11, 1, 1584).getOsisRef());
        assertEquals(gen_ex1.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11, 0, 1584).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11, 9, 1584).getOsisRef());
        assertEquals(gen_rev.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11, 0, 32356).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11, 0, 32356).getOsisRef());
        assertEquals(gen_rev.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11, 1, 32356).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11, 1, 32356).getOsisRef());
        assertEquals(gen_rev.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11, 0, 32356).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11, 9, 32356).getOsisRef());
        assertEquals(gen_rev.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11, 0, 32357).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11, 0, 32357).getOsisRef());
        assertEquals(gen_rev.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11, 1, 32357).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11, 1, 32357).getOsisRef());
        assertEquals(gen_rev.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11, 0, 32357).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11, 9, 32357).getOsisRef());
        assertEquals(gen_rev.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11, 0, 99999).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11, 0, 99999).getOsisRef());
        assertEquals(gen_rev.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11, 1, 99999).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11, 1, 99999).getOsisRef());
        assertEquals(gen_rev.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11, 0, 99999).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11, 9, 99999).getOsisRef());
        assertEquals(gen11_2.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen12, 1, 0).getOsisRef());
        assertEquals(gen11_2.getOsisRef(), RestrictionType.NONE.blur(v11n, gen12, 1, 0).getOsisRef());
        assertEquals(gen11_2.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen12, 9, 0).getOsisRef());
        assertEquals(gen11_2.getOsisRef(), RestrictionType.NONE.blur(v11n, gen12, 1, 0).getOsisRef());
        assertEquals(rev99_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, rev99, 20, 0).getOsisRef());
        assertEquals(rev99_9.getOsisRef(), RestrictionType.NONE.blur(v11n, rev99, 20, 0).getOsisRef());
        assertEquals(rev99_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, rev99, 20, 1).getOsisRef());
        assertEquals(rev99_9.getOsisRef(), RestrictionType.NONE.blur(v11n, rev99, 20, 1).getOsisRef());
        assertEquals(rev99_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, rev99, 20, 9).getOsisRef());
        assertEquals(rev99_9.getOsisRef(), RestrictionType.NONE.blur(v11n, rev99, 20, 9).getOsisRef());
        assertEquals(rev99_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, rev99, 425, 0).getOsisRef());
        assertEquals(rev11_9.getOsisRef(), RestrictionType.NONE.blur(v11n, rev99, 425, 0).getOsisRef());
        assertEquals(rev99_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, rev99, 425, 1).getOsisRef());
        assertEquals(rev11_9.getOsisRef(), RestrictionType.NONE.blur(v11n, rev99, 425, 1).getOsisRef());
        assertEquals(rev99_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, rev99, 425, 9).getOsisRef());
        assertEquals(rev11_9.getOsisRef(), RestrictionType.NONE.blur(v11n, rev99, 425, 9).getOsisRef());
    }

    public void testNewViaVerseRangeIntIntBoolean() {
        assertEquals(gen11_1.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11_1, 0, 0).getOsisRef());
        assertEquals(gen11_1.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11_1, 0, 0).getOsisRef());
        assertEquals(gen11_2.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11_1, 0, 1).getOsisRef());
        assertEquals(gen11_2.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11_1, 0, 1).getOsisRef());
        assertEquals(gen11_1.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11_1, 1, 0).getOsisRef());
        assertEquals(gen11_1.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11_1, 1, 0).getOsisRef());
        assertEquals(gen11_1.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11_1, 9, 0).getOsisRef());
        assertEquals(gen11_1.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11_1, 9, 0).getOsisRef());
        assertEquals(rev99_1.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, rev99_1, 0, 1).getOsisRef());
        assertEquals(rev99_1.getOsisRef(), RestrictionType.NONE.blur(v11n, rev99_1, 0, 1).getOsisRef());
        assertEquals(rev99_1.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, rev99_1, 0, 9).getOsisRef());
        assertEquals(rev99_1.getOsisRef(), RestrictionType.NONE.blur(v11n, rev99_1, 0, 9).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11_1, 0, 30).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11_1, 0, 30).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11_1, 1, 30).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11_1, 1, 30).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11_1, 9, 30).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11_1, 9, 30).getOsisRef());
        assertEquals(gen11_a.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11_1, 0, 31).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11_1, 0, 31).getOsisRef());
        assertEquals(gen11_a.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11_1, 1, 31).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11_1, 1, 31).getOsisRef());
        assertEquals(gen11_a.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11_1, 9, 31).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11_1, 9, 31).getOsisRef());
        assertEquals(gen_all.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11_1, 0, 1581).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11_1, 0, 1581).getOsisRef());
        assertEquals(gen_all.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11_1, 1, 1581).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11_1, 1, 1581).getOsisRef());
        assertEquals(gen_all.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11_1, 9, 1581).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11_1, 9, 1581).getOsisRef());
        assertEquals(gen_ex1.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11_1, 0, 1584).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11_1, 0, 1584).getOsisRef());
        assertEquals(gen_ex1.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11_1, 1, 1584).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11_1, 1, 1584).getOsisRef());
        assertEquals(gen_ex1.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11_1, 9, 1584).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11_1, 9, 1584).getOsisRef());
        assertEquals(gen_rev.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11_1, 0, 32356).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11_1, 0, 32356).getOsisRef());
        assertEquals(gen_rev.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11_1, 1, 32356).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11_1, 1, 32356).getOsisRef());
        assertEquals(gen_rev.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11_1, 9, 32356).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11_1, 9, 32356).getOsisRef());
        assertEquals(gen_rev.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11_1, 0, 32357).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11_1, 0, 32357).getOsisRef());
        assertEquals(gen_rev.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11_1, 1, 32357).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11_1, 1, 32357).getOsisRef());
        assertEquals(gen_rev.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11_1, 9, 32357).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11_1, 9, 32357).getOsisRef());
        assertEquals(gen_rev.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11_1, 0, 99999).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11_1, 0, 99999).getOsisRef());
        assertEquals(gen_rev.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11_1, 1, 99999).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11_1, 1, 99999).getOsisRef());
        assertEquals(gen_rev.getOsisRef(), RestrictionType.NONE.blur(v11n, gen11_1, 9, 99999).getOsisRef());
        assertEquals(gen11_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen11_1, 9, 99999).getOsisRef());
        assertEquals(gen11_2.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen12_1, 1, 0).getOsisRef());
        assertEquals(gen11_2.getOsisRef(), RestrictionType.NONE.blur(v11n, gen12_1, 1, 0).getOsisRef());
        assertEquals(gen11_2.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, gen12_1, 9, 0).getOsisRef());
        assertEquals(gen11_2.getOsisRef(), RestrictionType.NONE.blur(v11n, gen12_1, 9, 0).getOsisRef());
        assertEquals(rev99_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, rev99_1, 20, 0).getOsisRef());
        assertEquals(rev99_9.getOsisRef(), RestrictionType.NONE.blur(v11n, rev99_1, 20, 0).getOsisRef());
        assertEquals(rev99_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, rev99_1, 20, 1).getOsisRef());
        assertEquals(rev99_9.getOsisRef(), RestrictionType.NONE.blur(v11n, rev99_1, 20, 1).getOsisRef());
        assertEquals(rev99_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, rev99_1, 20, 9).getOsisRef());
        assertEquals(rev99_9.getOsisRef(), RestrictionType.NONE.blur(v11n, rev99_1, 20, 9).getOsisRef());
        assertEquals(rev99_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, rev99_1, 425, 0).getOsisRef());
        assertEquals(rev11_9.getOsisRef(), RestrictionType.NONE.blur(v11n, rev99_1, 425, 0).getOsisRef());
        assertEquals(rev99_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, rev99_1, 425, 1).getOsisRef());
        assertEquals(rev11_9.getOsisRef(), RestrictionType.NONE.blur(v11n, rev99_1, 425, 1).getOsisRef());
        assertEquals(rev99_9.getOsisRef(), RestrictionType.CHAPTER.blur(v11n, rev99_1, 425, 9).getOsisRef());
        assertEquals(rev11_9.getOsisRef(), RestrictionType.NONE.blur(v11n, rev99_1, 425, 9).getOsisRef());
    }

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

    public void testGetName() {
        assertEquals(gen11_1.getName(), "Gen 1:1");
        assertEquals(gen11_2.getName(), "Gen 1:1-2");
        assertEquals(gen11_9.getName(), "Gen 1");
        assertEquals(gen11_a.getName(), "Gen 1:1-2:1");
        assertEquals(gen12_1.getName(), "Gen 1:2");
        assertEquals(gen_all.getName(), "Gen");
        assertEquals(gen_ex1.getName(), "Gen 1:1-Exo 1:1");
        assertEquals(gen_rev.getName(), "Gen-Rev");
        assertEquals(rev99_9.getName(), "Rev 22");
        assertEquals(rev11_9.getName(), "Rev");
        assertEquals(rev99_1.getName(), "Rev 22:21");
    }

    public void testGetNameVerse() {
        assertEquals(gen11_2.getName(gen11), "1-2");
        assertEquals(gen12_1.getName(gen11), "2");
        assertEquals(rev99_9.getName(gen11), "Rev 22");
        assertEquals(rev99_9.getName(null), "Rev 22");
    }

    public void testGetStart() {
        assertEquals(gen11_1.getStart(), gen11);
        assertEquals(gen11_2.getStart(), gen11);
        assertEquals(gen11_9.getStart(), gen11);
        assertEquals(gen11_a.getStart(), gen11);
        assertEquals(gen12_1.getStart(), gen12);
        assertEquals(gen_all.getStart(), gen11);
        assertEquals(gen_ex1.getStart(), gen11);
        assertEquals(gen_rev.getStart(), gen11);
        assertEquals(rev99_9.getStart(), rev91);
        assertEquals(rev11_9.getStart(), rev11);
        assertEquals(rev99_1.getStart(), rev99);
    }

    public void testGetEnd() {
        assertEquals(gen11_1.getEnd(), gen11);
        assertEquals(gen11_2.getEnd(), gen12);
        assertEquals(gen11_9.getEnd(), gen19);
        assertEquals(gen11_a.getEnd(), gen21);
        assertEquals(gen12_1.getEnd(), gen12);
        assertEquals(gen_all.getEnd(), gen99);
        assertEquals(gen_ex1.getEnd(), exo11);
        assertEquals(gen_rev.getEnd(), rev99);
        assertEquals(rev99_9.getEnd(), rev99);
        assertEquals(rev11_9.getEnd(), rev99);
        assertEquals(rev99_1.getEnd(), rev99);
    }

    public void testGetVerseCount() {
        assertEquals(gen11_1.getCardinality(), 1);
        assertEquals(gen11_2.getCardinality(), 2);
        assertEquals(gen11_9.getCardinality(), 31);
        assertEquals(gen11_a.getCardinality(), 33);
        assertEquals(gen12_1.getCardinality(), 1);
        assertEquals(gen_all.getCardinality(), 1582);
        assertEquals(gen_ex1.getCardinality(), 1585);
        assertEquals(gen_rev.getCardinality(), 32356);
        assertEquals(rev99_9.getCardinality(), 21);
        assertEquals(rev11_9.getCardinality(), 425);
        assertEquals(rev99_1.getCardinality(), 1);
    }

    public void testClone() {
        assertTrue(gen11_1 != gen11_1.clone());
        assertTrue(gen11_1.equals(gen11_1.clone()));
        assertTrue(rev99_1 != rev99_1.clone());
        assertTrue(rev99_1.equals(rev99_1.clone()));
    }

    public void testCompareTo() {
        assertEquals(rev99_1.compareTo(gen11_1), 1);
        assertEquals(gen11_1.compareTo(rev99_1), -1);
        assertEquals(gen11_1.compareTo(gen11_1), 0);
        assertEquals(gen11_1.compareTo(gen11_2), -1);
        assertEquals(gen11_1.compareTo(gen11_9), -1);
        assertEquals(gen11_1.compareTo(gen11_a), -1);
        assertEquals(gen11_1.compareTo(gen_all), -1);
        assertEquals(gen12_1.compareTo(gen11_1), 1);
        assertEquals(gen12_1.compareTo(gen11_2), 1);
        assertEquals(gen12_1.compareTo(gen_rev), 1);
        assertEquals(gen12_1.compareTo(gen12_1), 0);
        assertEquals(gen12_1.compareTo(rev99_1), -1);
        assertEquals(gen12_1.compareTo(rev99_9), -1);
        try {
            gen12_1.compareTo(null);
            fail();
        } catch (NullPointerException ex) {
        }
    }

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

    public void testIsChapter() throws Exception {
        assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1").isWholeChapter());
        assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:1-ff").isWholeChapter());
        assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:1-$").isWholeChapter());
        assertTrue(VerseRangeFactory.fromString(v11n, "Exo 2").isWholeChapter());
        assertTrue(VerseRangeFactory.fromString(v11n, "Exo 2:1-ff").isWholeChapter());
        assertTrue(VerseRangeFactory.fromString(v11n, "Exo 2:1-$").isWholeChapter());
        assertTrue(!VerseRangeFactory.fromString(v11n, "Num 3:1").isWholeChapter());
        assertTrue(!VerseRangeFactory.fromString(v11n, "Num 4:1-5:1").isWholeChapter());
        assertTrue(!VerseRangeFactory.fromString(v11n, "Num 5:1-6:ff").isWholeChapter());
        assertTrue(!VerseRangeFactory.fromString(v11n, "Lev").isWholeChapter());
    }

    public void testIsBook() throws Exception {
        assertTrue(VerseRangeFactory.fromString(v11n, "Gen").isWholeBook());
        assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:1-Gen 50:ff").isWholeBook());
        assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1:1-Gen 50:$").isWholeBook());
        assertTrue(VerseRangeFactory.fromString(v11n, "Gen 1-50:ff").isWholeBook());
        assertTrue(!VerseRangeFactory.fromString(v11n, "Num 1:2-Num $:$").isWholeBook());
        assertTrue(!VerseRangeFactory.fromString(v11n, "Num 4:1-5:1").isWholeBook());
        assertTrue(!VerseRangeFactory.fromString(v11n, "Num 5:1-6:ff").isWholeBook());
        assertTrue(!VerseRangeFactory.fromString(v11n, "Lev-Deu 1:1").isWholeBook());
    }

    public void testToVerseArray() {
        assertEquals(gen11_1.toVerseArray()[0], gen11);
        assertEquals(gen11_2.toVerseArray()[0], gen11);
        assertEquals(gen11_2.toVerseArray()[1], gen12);
        assertEquals(gen11_9.toVerseArray()[0], gen11);
        assertEquals(gen11_9.toVerseArray()[30], gen19);
        assertEquals(gen12_1.toVerseArray()[0], gen12);
        assertEquals(gen_all.toVerseArray()[0], gen11);
        assertEquals(gen_ex1.toVerseArray()[0], gen11);
        assertEquals(gen_ex1.toVerseArray()[1584], exo11);
        assertEquals(rev11_9.toVerseArray()[0], rev11);
        assertEquals(rev11_9.toVerseArray()[1], rev12);
        assertEquals(rev11_9.toVerseArray()[424], rev99);
        assertEquals(gen11_1.toVerseArray().length, 1);
        assertEquals(gen11_2.toVerseArray().length, 2);
        assertEquals(gen11_9.toVerseArray().length, 31);
        assertEquals(gen11_a.toVerseArray().length, 33);
        assertEquals(gen12_1.toVerseArray().length, 1);
        assertEquals(gen_all.toVerseArray().length, 1582);
        assertEquals(gen_ex1.toVerseArray().length, 1585);
        assertEquals(rev11_9.toVerseArray().length, 425);
    }

    public void testVerseElements() {
        Iterator<Key> it = gen11_1.iterator();
        while (it.hasNext()) {
            assertTrue(it.hasNext());
            Verse v = (Verse) it.next();
            assertEquals(v, gen11);
            assertTrue(!it.hasNext());
        }
        it = gen11_2.iterator();
        while (it.hasNext()) {
            assertTrue(it.hasNext());
            Verse v = (Verse) it.next();
            assertEquals(v, gen11);
            assertTrue(it.hasNext());
            v = (Verse) it.next();
            assertEquals(v, gen12);
            assertTrue(!it.hasNext());
        }
    }
}
