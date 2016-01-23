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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.TreeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gather all references.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public final class GatherAllReferences {
    /**
     * Prevent instantiation
     */
    private GatherAllReferences() {
    }

    /**
     * Read all the books that we can get our hands on.
     * 
     * @param args 
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        out = new PrintWriter(new BufferedWriter(new FileWriter("passages.log")));
        // Loop through all the Books
        log.warn("*** Reading all known Books");
        BookFilter filter = BookFilters.getCustom("GlobalOptionFilter=ThMLScripref;Category=Biblical Texts");
        List<Book> comments = Books.installed().getBooks(filter);
        for (Book book : comments) {

            if (!book.isLocked()) {
                BookMetaData bmd = book.getBookMetaData();
                // Skip PlainText as they do not have references marked up
                if (bmd.getProperty("SourceType") != null) {
                    Key set = book.getGlobalKeyList();

                    readBook(book, set);
                }
            }
        }
        out.flush();
        out.close();
    }

    /**
     * Perform a test read on an iterator over a set of keys
     */
    private static void readBook(Book book, Key set) {
        int[] stats = new int[] {
                0, 0
        };

        boolean first = true;
        for (Key key : set) {
            // skip the root of a TreeKey as it often is not addressable.
            if (first) {
                first = false;
                if (key instanceof TreeKey && key.getName().length() == 0) {
                    continue;
                }
            }
            readKey(book, key, stats);
        }
        log.warn(book.getInitials() + ':' + stats[0] + ':' + stats[1]);

    }

    /**
     * Perform a test read on a single key
     */
    private static void readKey(Book book, Key key, int[] stats) {
        String orig;
        try {
            orig = book.getRawText(key);
        } catch (BookException ex) {
            log.warn("Failed to read: {}({}):{}", book.getInitials(), key.getOsisID(), ex.getMessage(), ex);
            return;
        }

        Matcher matcher = null;
        if (orig.indexOf("passage=\"") != -1) {
            matcher = thmlPassagePattern.matcher(orig);
        } else if (orig.indexOf("osisRef=\"") != -1) {
            matcher = osisPassagePattern.matcher(orig);
        } else if (orig.indexOf("<RX>") != -1) {
            matcher = gbfPassagePattern.matcher(orig);
        }

        if (matcher != null) {
            while (matcher.find()) {
                String rawRef = matcher.group(2);
                stats[0]++;
                String message = book.getInitials() + ':' + key.getOsisRef() + '/' + rawRef;
                /*
                    try {
                        Key ref = book.getKey(rawRef);
                        message += '/' + ref.getOsisRef();
                    } catch (NoSuchKeyException e) {
                        message += '!' + e.getMessage();
                        stats[1]++;
                    }
                 */

                out.println(message);
            }
        }
    }

    private static Pattern thmlPassagePattern = Pattern.compile("(osisRef|passage)=\"([^\"]*)");
    private static Pattern gbfPassagePattern = Pattern.compile("(<RX>)([^<]*)");
    private static Pattern osisPassagePattern = Pattern.compile("(osisRef)=\"([^\"]*)");
    private static PrintWriter out;

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(GatherAllReferences.class);
}
