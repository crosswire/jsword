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
 * Copyright: 2005-2013
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.book;

import java.util.List;

import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.TreeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test to check that all books can be read.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class ReadEverything {
    /**
     * Prevent instantiation
     */
    private ReadEverything() {
    }

    /**
     * Read all the books that we can get our hands on.
     */
    public static void main(String[] args) {
        // Loop through all the Books
        log.warn("*** Reading all installed Bibles");
        BookFilter filter = BookFilters.getCustom("SourceType=GBF");
        List<Book> comments = Books.installed().getBooks(filter);
        for (Book book : comments) {

            if (!book.isLocked()) {
                log.warn("****** Reading: [{}] {} ({})", book.getInitials(), book.getName(), book.getBookCategory());
                Key set = book.getGlobalKeyList();

                testReadMultiple(book, set);
            } else {
                log.warn("****** Skipping: [{}] {} ({})", book.getInitials(), book.getName(), book.getBookCategory());
            }
        }
    }

    /**
     * Perform a test read on an iterator over a set of keys
     */
    private static void testReadMultiple(Book book, Key set) {
        // log.info("Testing: {}={}", bmd.getInitials(), bmd.getName());
        long start = System.currentTimeMillis();
        int entries = 0;

        boolean first = true;
        for (Key key : set) {
            // skip the root of a TreeKey as it often is not addressable.
            if (first) {
                first = false;
                if (key instanceof TreeKey && key.getName().length() == 0) {
                    continue;
                }
            }
            testReadSingle(book, key);

            entries++;
        }

        long end = System.currentTimeMillis();
        float time = (end - start) / 1000F;

        log.info("Tested: book={} entries={} time={}s ({}ms per entry)", book.getInitials(), Integer.toString(entries), Float.toString(time), Float.toString(1000 * time / entries));
    }

    /**
     * Perform a test read on a single key
     */
    private static void testReadSingle(Book book, Key key) {
        try {
            log.debug("reading: {}/{}", book.getInitials(), key.getName());

            BookData data = new BookData(book, key);
            if (data.getOsisFragment() == null) {
                log.warn("No output from: {},{}", book.getInitials(), key.getOsisID());
            }

            // This might be a useful extra test, except that a failure gives
            // you no help at all.
            // data.validate();
        } catch (Throwable ex) {
            log.error("Unexpected error reading: {}, {}, {}", book.getInitials(), key.getOsisID(), key.getClass().getName(), ex);
        }
    }

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(ReadEverything.class);
}
