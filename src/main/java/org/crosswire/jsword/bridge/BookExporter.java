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
 * © CrossWire Bible Society, 2008 - 2016
 *
 */
package org.crosswire.jsword.bridge;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.versification.BookName;

/**
 * Exports the Book in SWORD's imp format. This is identical to SWORD's mod2imp.
 * Note: it does not work with GenBook.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class BookExporter {

    public BookExporter(Book book) {
        this.book = book;
    }

    public void mod2imp() throws BookException {
        // Use short key names for Bibles.
        if (BookCategory.BIBLE.equals(book.getBookCategory())) {
            BookName.setFullBookName(false);
        }

        Key keys = book.getGlobalKeyList();

        StringBuilder buf = new StringBuilder();
        for (Key key : keys) {
            //FIXME(CJB) iteration should be pushed down to benefit from performance improvement
            String rawText = book.getRawText(key);
            if (rawText != null && rawText.trim().length() > 0) {
                buf.delete(0, buf.length());
                buf.append("$$$").append(key).append('\n').append(rawText);
                System.out.println(buf.toString());
            }
        }
    }

    private Book book;

    /**
     * Call with &lt;operation&gt; book. Where operation can be one of:
     * <ul>
     * <li>check - returns "TRUE" or "FALSE" indicating whether the index exists
     * or not</li>
     * <li>create - (re)create the index</li>
     * <li>delete - delete the index if it exists</li>
     * </ul>
     * And book is the initials of a book, e.g. KJV.
     * 
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            usage();
            return;
        }

        System.err.println("BookExporter " + args[0]);

        Book b = Books.installed().getBook(args[0]);
        if (b == null) {
            System.err.println("Book not found");
            return;
        }

        BookExporter exporter = new BookExporter(b);
        try {
            exporter.mod2imp();
        } catch (BookException e) {
            System.err.println("Error while exporting");
            e.printStackTrace();
        }
    }

    public static void usage() {
        System.err.println("Usage: BookExporter book");
    }
}
