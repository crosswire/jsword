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
 * © CrossWire Bible Society, 2012 - 2016
 *
 */
package org.crosswire.jsword.book.sword;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.junit.Assert;
import org.junit.Test;

/**
 * A Raw File format that allows for each verse to have it's own storage.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author mbergmann
 * @author DM Smith
 */
public class GenBookTest {

    @Test
    public void testOsisID() {
        Book book = Books.installed().getBook("Pilgrim"); // Bunyan's Pilgrim's Progress
        if (book != null) {
            Key key = null;
            try {
                key = book.getKey("THE FIRST STAGE");
            } catch (NoSuchKeyException ex) {
                Assert.fail();
            }
            if (key != null) {
                Assert.assertEquals("PART II/THE FIRST STAGE", key.getOsisID());
            }
        }
    }

    @Test
    public void testCount() {
        Book book = Books.installed().getBook("Pilgrim");
        if (book != null) {
            Key globalKeyList = book.getGlobalKeyList();
            Assert.assertEquals("Incorrect number of keys in master list", 29, globalKeyList.getCardinality());
            Assert.assertEquals("Incorrect number of top level keys", 6, globalKeyList.getChildCount());
        }
    }

    @Test
    public void testInvalidKey() {
        Book book = Books.installed().getBook("Pilgrim");
        if (book != null) {
            Key key = book.getGlobalKeyList();
            try {
                book.getRawText(key);
            } catch (NullPointerException e) {
                Assert.fail("test for bad key should not have thrown an NPE.");
            } catch (BookException e) {
                Assert.assertEquals("testing for a bad key", "No entry for '' in Pilgrim.", e.getMessage());
            }
        }
    }

}
