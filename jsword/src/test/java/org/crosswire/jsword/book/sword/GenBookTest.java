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

    public void testCreate() throws IOException, BookException {
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

}