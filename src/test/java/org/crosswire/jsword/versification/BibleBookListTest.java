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
 * Copyright: 2012 - 2014
 *     The copyright to this program is held by its authors.
 *
 */
package org.crosswire.jsword.versification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

/**
 *
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class BibleBookListTest {

    public static BibleBook[] booksNT =
    {
        BibleBook.MATT,
        BibleBook.MARK,
        BibleBook.LUKE,
        BibleBook.JOHN,
        BibleBook.ACTS,
        BibleBook.JAS,
        BibleBook.PET1,
        BibleBook.PET2,
        BibleBook.JOHN1,
        BibleBook.JOHN2,
        BibleBook.JOHN3,
        BibleBook.JUDE,
        BibleBook.ROM,
        BibleBook.COR1,
        BibleBook.COR2,
        BibleBook.GAL,
        BibleBook.EPH,
        BibleBook.PHIL,
        BibleBook.COL,
        BibleBook.THESS1,
        BibleBook.THESS2,
        BibleBook.TIM1,
        BibleBook.TIM2,
        BibleBook.TITUS,
        BibleBook.PHLM,
        BibleBook.HEB,
        BibleBook.REV,
    };

    BibleBookList list;

    @Before
    public void setUp() throws Exception {
        list = new BibleBookList(booksNT);
    }

    @Test
    public void testCount() {
        assertEquals(booksNT.length, list.getBookCount());
    }

    @Test
    public void testContains() {
        // Are all the books from booksNT present
        for (BibleBook b : booksNT) {
            assertTrue(b.getOSIS(), list.contains(b));
        }
    }

    @Test
    public void testOrdinal() {
        for (int i = 0; i < booksNT.length; i++) {
            BibleBook b = booksNT[i];
            assertEquals(b.getOSIS(), i, list.getOrdinal(b));
        }
    }

    @Test
    public void testGet() {
        for (int i = 0; i < booksNT.length; i++) {
            BibleBook b = booksNT[i];
            assertEquals(b.getOSIS(), b, list.getBook(i));
        }
        assertEquals(booksNT[0].getOSIS(), booksNT[0], list.getBook(-1));
        assertEquals(booksNT[booksNT.length - 1].getOSIS(), booksNT[booksNT.length - 1], list.getBook(booksNT.length));
    }

    @Test
    public void testNextBook() {
        for (int i = 0; i < booksNT.length - 1; i++) {
            BibleBook b = booksNT[i];
            BibleBook n = booksNT[i + 1];
            assertEquals(b.getOSIS(), n, list.getNextBook(b));
        }
        // Test the last book in the list
        assertEquals("Next after last book", null, list.getNextBook(booksNT[booksNT.length - 1]));
        // Test a book not in the list
        assertEquals(BibleBook.GEN.getOSIS(), null, list.getNextBook(BibleBook.GEN));
    }

    @Test
    public void testPreviousBook() {
        for (int i = 1; i < booksNT.length; i++) {
            BibleBook b = booksNT[i];
            BibleBook p = booksNT[i - 1];
            assertEquals(b.getOSIS(), p, list.getPreviousBook(b));
        }
        // Test the first book in the list
        assertEquals("Prior before first book", null, list.getPreviousBook(booksNT[0]));
        // Test a book not in the list
        assertEquals(BibleBook.GEN.getOSIS(), null, list.getPreviousBook(BibleBook.GEN));
    }

    @Test
    public void testIterator() {
        int i = 0;
        for (BibleBook book: list) {
            BibleBook b = booksNT[i++];
            assertEquals(b.getOSIS(), b, book);
        }
        // Same loop written differently.
        i = 0;
        Iterator<BibleBook> iter = list.iterator();
        while (iter.hasNext()) {
            BibleBook book = iter.next();
            BibleBook b = booksNT[i++];
            assertEquals(b.getOSIS(), b, book);
            try {
                iter.remove();
                fail("Remove is not a supported operation on a BibleBookList");
            } catch (UnsupportedOperationException e) {
                ;
            }
        }
        try {
            iter.next();
            fail("Cannot call next after exhausting a BibleBookList iterator");
        } catch (NoSuchElementException e) {
            ;
        }
        
    }
}
