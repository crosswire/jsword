package org.crosswire.jsword.book.sword;

import java.io.IOException;

import junit.framework.Assert;
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

    public void testOsisID() throws IOException, BookException {
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
                   Assert.fail("Could not get the osisID for a GenBook key.");
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
                book.getRawText(key, bob);
            } catch (NullPointerException e) {
                Assert.fail("test for bad key should not have thrown an NPE.");
            } catch (BookException e) {
                assertEquals("testing for a bad key", "No entry for '' in Pilgrim.", e.getMessage());
            }
        }
    }

}
