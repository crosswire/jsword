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
 * Copyright: 2012
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.versification;

import junit.framework.TestCase;

/**
 *
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class BibleBookTest extends TestCase {

    public BibleBookTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetBook() {
        assertEquals(BibleBook.GEN, BibleBook.getBook("Genesis"));
        assertEquals(BibleBook.GEN, BibleBook.getBook("Gene"));
        assertEquals(BibleBook.GEN, BibleBook.getBook("Gen"));
        assertEquals(BibleBook.GEN, BibleBook.getBook("G"));
        assertEquals(BibleBook.GEN, BibleBook.getBook("g"));
        assertEquals(BibleBook.GEN, BibleBook.getBook("GEN"));
        assertEquals(BibleBook.GEN, BibleBook.getBook("genesis"));
        assertEquals(BibleBook.PS, BibleBook.getBook("psa"));
        assertEquals(BibleBook.PS, BibleBook.getBook("ps"));
        assertEquals(BibleBook.PS, BibleBook.getBook("pss"));
        assertEquals(BibleBook.PS, BibleBook.getBook("psalter"));
        assertEquals(BibleBook.ECCL, BibleBook.getBook("ecc"));
        assertEquals(BibleBook.ECCL, BibleBook.getBook("Qohelot"));
        assertEquals(BibleBook.SONG, BibleBook.getBook("son"));
        assertEquals(BibleBook.SONG, BibleBook.getBook("song"));
        assertEquals(BibleBook.SONG, BibleBook.getBook("song of solomon"));
        assertEquals(BibleBook.SONG, BibleBook.getBook("songofsolomon"));
        assertEquals(BibleBook.SONG, BibleBook.getBook("ss"));
        assertEquals(BibleBook.SONG, BibleBook.getBook("canticle"));
        assertEquals(BibleBook.SONG, BibleBook.getBook("can"));
        assertEquals(BibleBook.PHIL, BibleBook.getBook("phi"));
        assertEquals(BibleBook.PHIL, BibleBook.getBook("phil"));
        assertEquals(BibleBook.PHIL, BibleBook.getBook("phili"));
        assertEquals(BibleBook.PHLM, BibleBook.getBook("phile"));
        assertEquals(BibleBook.REV,  BibleBook.getBook("revelations"));
        assertEquals(BibleBook.REV,  BibleBook.getBook("rev"));

        assertEquals(null, BibleBook.getBook("1"));
    }

    public void testGetBookOSIS() throws Exception {
        assertEquals("Gen", BibleBook.GEN.getOSIS());
        assertEquals("Exod", BibleBook.EXOD.getOSIS());
        assertEquals("Rev", BibleBook.REV.getOSIS());
    }

}
