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
package org.crosswire.jsword.versification;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A BibleBookList is an ordered list of one or more BibleBooks.
 * Typically, a BibleBookList is a member of a Versification.
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
/* pkg protected */ class BibleBookList implements Iterable<BibleBook>, Serializable {
    /**
     * Create an ordered BibleBookList from the input.
     * @param books
     */
    BibleBookList(BibleBook[] books) {
        this.books = books.clone();
        initialize();
    }

    /**
     * Does this Versification contain the BibleBook.
     *
     * @param book
     * @return true if it is present.
     */
    public boolean contains(BibleBook book) {
        return book != null && bookMap[book.ordinal()] != -1;
    }

    /**
     * Where does the BibleBook come in the order of books of the Bible.
     * The first book is 0, the next is 1 and so forth.
     * If the BibleBook is not in this Reference System,
     * then the return value of this routine is -1.
     *
     * @param book
     * @return the ordinal value of the book or -1 if not present
     */
    public int getOrdinal(BibleBook book) {
        return bookMap[book.ordinal()];
    }

    /**
     * Get the number of books in this Versification.
     * @return the number of books
     */
    public int getBookCount() {
        return books.length;
    }

    /**
     * Get the BibleBook by its position in this Versification.
     * If the position is negative, return the first book.
     * If the position is greater than the last, return the last book.
     *
     * @param ordinal
     * @return the indicated book
     */
    public BibleBook getBook(int ordinal) {
        int ord = ordinal;
        if (ord < 0) {
            ord = 0;
        }
        if (ord >= books.length) {
            ord = books.length - 1;
        }
        return books[ord];
    }

    /**
     * Get the BibleBooks in this Versification.
     *
     * @return an Iterator over the books
     */
    public Iterator<BibleBook> iterator() {
        return new Iterator<BibleBook>() {

            private BibleBook nextBook = books[0];

            public boolean hasNext() {
                return nextBook != null;
            }

            public BibleBook next() {

                if (nextBook == null) {
                    throw new NoSuchElementException();
                }

                BibleBook current = nextBook;
                nextBook = getNextBook(nextBook);
                return current;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }

        };
    }

    /**
     * Return the first book in the list.
     *
     * @return the first book in the list
     */
    public BibleBook getFirstBook() {
        return books[0];
    }

    /**
     * Return the first book in the list.
     *
     * @return the first book in the list
     */
    public BibleBook getLastBook() {
        return books[books.length - 1];
    }

    /**
     * Given a BibleBook, get the previous BibleBook in this Versification. If it is the first book, return null.
     * @param book A BibleBook in the Versification
     * @return the previous BibleBook or null.
     */
    public BibleBook getPreviousBook(BibleBook book) {
        int ordinal = book.ordinal();
        int position = bookMap[ordinal];
        if (position > 0) {
            return books[position - 1];
        }

        return null;
    }

    /**
     * Given a BibleBook, get the next BibleBook in this Versification. If it is the last book, return null.
     * @param book A BibleBook in the Versification
     * @return the previous BibleBook or null.
     */
    public BibleBook getNextBook(BibleBook book) {
        int ordinal = book.ordinal();
        int position = bookMap[ordinal];
        if (position != -1 && position + 1 < books.length) {
            return books[position + 1];
        }
        return null;
    }

    /**
     * The bookMap contains one slot for every BibleBook, indexed by it's ordinal value.
     * The value of that entry is the position of the book in the BookList.
     * If the BibleBook is not present in books, it's value is -1.
     */
    private void initialize() {
        bookMap = new int[BibleBook.values().length + 1];
        // Initialize all slots to -1
        for (BibleBook b : BibleBook.values()) {
            bookMap[b.ordinal()] = -1;
        }

        // Fill in the position of the books into that list
        for (int i = 0; i < books.length; i++) {
            bookMap[books[i].ordinal()] = i;
        }
    }

    /** The ordered books in this list */
    protected BibleBook[] books;

    /** The bookMap maps from a BibleBook to the position that it has in <code>books</code>. */
    private int[] bookMap;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = -2681289798451902815L;
}
