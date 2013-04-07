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
package org.crosswire.jsword.book.sword;

import junit.framework.TestCase;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;

/**
 * A Raw File format that allows for each verse to have it's own storage.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author mbergmann
 */
public class GenBookTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
    }

    @Override
    protected void tearDown() throws Exception {
    }

    public void testOsisID() {
        Book book = Books.installed().getBook("Pilgrim"); // Bunyan's Pilgrim's Progress
        if (book != null) {
            Key key = null;
            try {
                key = book.getKey("THE FIRST STAGE");
            } catch (NoSuchKeyException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (key != null) {
                try {
                    assertEquals("PART_II.THE_FIRST_STAGE", key.getOsisID());
                } catch(RuntimeException e) {
                   fail("Could not get the osisID for a GenBook key.");
                }
            }
        }
    }
    
    public void testCount() {
        Book book = Books.installed().getBook("Pilgrim");
        if (book != null) {
            Key globalKeyList = book.getGlobalKeyList();
            assertEquals("Incorrect number of keys in master list", 29, globalKeyList.getCardinality());
            assertEquals("Incorrect number of top level keys", 6, globalKeyList.getChildCount());
        }
    }
    public void testInvalidKey() {
        Book book = Books.installed().getBook("Pilgrim");
        if (book != null) {
            Key key = book.getGlobalKeyList();
            try {
                book.getRawText(key);
            } catch (NullPointerException e) {
                fail("test for bad key should not have thrown an NPE.");
            } catch (BookException e) {
                assertEquals("testing for a bad key", "No entry for '' in Pilgrim.", e.getMessage());
            }
        }
    }

}
