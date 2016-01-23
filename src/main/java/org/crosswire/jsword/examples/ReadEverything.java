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
package org.crosswire.jsword.examples;

import java.util.List;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.passage.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test to check that all books can be read.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public final class ReadEverything {
    /**
     * Prevent instantiation
     */
    private ReadEverything() {
    }

    /**
     * Read all the books that we can get our hands on.
     * @param args 
     */
    public static void main(String[] args) {
        // TODO(DMS): add the ability to pass a filter specification
        // Loop through all the Books
        log.warn("*** Reading all installed Bibles");
        BookFilter filter = null;
        //filter = BookFilters.getCustom("SourceType=ThML");
        //filter = BookFilters.getCustom("Description=Ergane Turkish to English Glossary");
        //filter = BookFilters.getCustom("ModDrv=zLD");
        //filter = BookFilters.getCustom("Initials=BosworthToller");
        //filter = BookFilters.getDictionaries();
        List<Book> books = getBooks(filter);
        for (Book book : books) {
            if (book.isLocked()) {
                log.warn("****** Skipping: [{}] {} ({})", book.getInitials(), book.getName(), book.getBookCategory());
                continue;
            }
            log.warn("****** Reading: [{}] {} ({})", book.getInitials(), book.getName(), book.getBookCategory());
            Key set = book.getGlobalKeyList();
            testReadMultiple(book, set);
        }
    }

    private static List<Book> getBooks(BookFilter filter) {
        if (filter == null) {
            return Books.installed().getBooks();
        }
        return Books.installed().getBooks(filter);
    }

    /**
     * Perform a test read on an iterator over a set of keys
     */
    private static void testReadMultiple(Book book, Key set) {
        // log.info("Testing: {}={}", bmd.getInitials(), bmd.getName());
        long start = System.currentTimeMillis();
        int entries = 0;
        count = 0;
        boolean first = true;
        for (Key key : set) {
            // skip the first entry if the length is 0.
            if (first) {
                first = false;
                if (key.getName().length() == 0) {
                    continue;
                }
            }
            testReadSingle(book, key, entries);

            entries++;
        }

        long end = System.currentTimeMillis();
        float time = (end - start) / 1000F;

        if (count > 0) {
            log.info("Tested: book={} entries={} time={}s errors={} ({}ms per entry)", book.getInitials(), Integer.toString(entries), Float.toString(time), Integer.toString(count), Float.toString(1000 * time / entries));
        } else {
            log.info("Tested: book={} entries={} time={}s ({}ms per entry)", book.getInitials(), Integer.toString(entries), Float.toString(time), Float.toString(1000 * time / entries));
        }
    }

    /**
     * Perform a test read on a single key
     */
    private static void testReadSingle(Book book, Key key, int entry) {
        Throwable oops = null;
        try {
            log.debug("reading: {}/{}:{}", book.getInitials(), Integer.toString(entry), key.getName());

            BookData data = new BookData(book, key);
            if (data.getOsisFragment() == null) {
                log.warn("No output from: {},{}", book.getInitials(), key.getOsisID());
            }

            // This might be a useful extra test, except that a failure gives
            // you no help at all.
            // data.validate();
        } catch (BookException ex) {
            oops = ex;
        }
        if (oops != null) {
            ++count;
            if (count < 5) {
                log.error("Unexpected error reading: {}, {}, {}, {}", book.getInitials(), Integer.toString(entry), key.getOsisID(), key.getClass().getName(), oops);
            }
        }
    }

    private static int count;

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(ReadEverything.class);
}
