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
 * Copyright: 2012
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.versification;

import org.crosswire.jsword.versification.system.Versifications;

import junit.framework.TestCase;

/**
 *
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith
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
        Versification v11n = Versifications.instance().getDefaultVersification();
        assertEquals(BibleBook.GEN, v11n.getBook("Genesis"));
        assertEquals(BibleBook.GEN, v11n.getBook("Gene"));
        assertEquals(BibleBook.GEN, v11n.getBook("Gen"));
        assertEquals(BibleBook.GEN, v11n.getBook("GE"));
        assertEquals(BibleBook.GEN, v11n.getBook("ge"));
        assertEquals(BibleBook.GEN, v11n.getBook("GEN"));
        assertEquals(BibleBook.GEN, v11n.getBook("genesis"));
        assertEquals(BibleBook.PS, v11n.getBook("psa"));
        assertEquals(BibleBook.PS, v11n.getBook("ps"));
        assertEquals(BibleBook.PS, v11n.getBook("pss"));
        assertEquals(BibleBook.PS, v11n.getBook("psalter"));
        assertEquals(BibleBook.ECCL, v11n.getBook("ecc"));
        assertEquals(BibleBook.ECCL, v11n.getBook("Qohelot"));
        assertEquals(BibleBook.SONG, v11n.getBook("son"));
        assertEquals(BibleBook.SONG, v11n.getBook("song"));
        assertEquals(BibleBook.SONG, v11n.getBook("song of solomon"));
        assertEquals(BibleBook.SONG, v11n.getBook("songofsolomon"));
        assertEquals(BibleBook.SONG, v11n.getBook("ss"));
        assertEquals(BibleBook.SONG, v11n.getBook("canticle"));
        assertEquals(BibleBook.SONG, v11n.getBook("can"));
        assertEquals(BibleBook.PHIL, v11n.getBook("phi"));
        assertEquals(BibleBook.PHIL, v11n.getBook("phil"));
        assertEquals(BibleBook.PHIL, v11n.getBook("phili"));
        assertEquals(BibleBook.PHLM, v11n.getBook("phile"));
        assertEquals(BibleBook.REV,  v11n.getBook("revelations"));
        assertEquals(BibleBook.REV,  v11n.getBook("rev"));

        assertEquals(null, v11n.getBook("1"));
    }

    public void testGetBookOSIS() throws Exception {
        assertEquals("Gen", BibleBook.GEN.getOSIS());
        assertEquals("Exod", BibleBook.EXOD.getOSIS());
        assertEquals("Rev", BibleBook.REV.getOSIS());
    }

}
