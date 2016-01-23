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
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;

/**
 * BookLookup allows one to lookup via keys. Note: it does not work with
 * GenBook.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class BookLookup {

    public BookLookup(Book book) {
        this.book = book;
    }

    public String locate(Key key) throws BookException {
        StringBuilder buf = new StringBuilder();

        //FIXME(iteration should be pushed down for performanace gains
        for (Key currentKey : key) {
            String osisID = currentKey.getOsisID();
            if (buf.length() > 0) {
                buf.append('\n');
            }
            buf.append(book.getInitials());
            buf.append(':');
            buf.append(osisID);
            buf.append(" - ");
            String rawText = book.getRawText(currentKey);
            if (rawText != null && rawText.trim().length() > 0) {
                buf.append(rawText);
            } else {
                buf.append("Not found");
            }
        }

        return buf.toString();
    }

    private Book book;

    /**
     * Call with book, key. And book is the initials of a book, e.g. KJV. And
     * key is a reference for the work, e.g. "Genesis 1:5-ff". Note: if the key
     * has spaces, it must be quoted.
     * 
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            usage();
            return;
        }

        System.err.print("BookLookup");
        for (int i = 0; i < args.length; i++) {
            System.err.print(' ');
            System.err.print(args[i]);
        }
        System.err.print('\n');

        Book b = Books.installed().getBook(args[0]);
        if (b == null) {
            System.err.println("Book not found");
            return;
        }

        BookLookup lookup = new BookLookup(b);
        try {
            System.out.println(lookup.locate(b.getKey(args[1])));
        } catch (BookException e) {
            System.err.println("Error while doing lookup");
            e.printStackTrace();
        } catch (NoSuchKeyException e) {
            System.err.println("Error while doing lookup");
            e.printStackTrace();
        }
    }

    public static void usage() {
        System.err.println("Usage: BookLookup book key");
    }
}
