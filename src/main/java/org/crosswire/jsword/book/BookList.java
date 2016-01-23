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
package org.crosswire.jsword.book;

import java.util.List;

/**
 * There are several lists of Books, the most important being the installed
 * Books, however there may be others like the available books or books from a
 * specific driver. This interface provides a common method of accessing all of
 * them.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @see gnu.lgpl.License
 * @author Joe Walker
 */
public interface BookList {
    /**
     * Get a list of all the Books of all types.
     * 
     * @return the desired list of books
     */
    List<Book> getBooks();

    /**
     * Get a filtered list of all the Books.
     * 
     * @param filter the filter to apply to the list of books
     * @return the desired list of books
     * @see BookFilters
     */
    List<Book> getBooks(BookFilter filter);

    /**
     * Add a BibleListener from our list of listeners
     * 
     * @param li interested listener
     */
    void addBooksListener(BooksListener li);

    /**
     * Remove a BibleListener to our list of listeners
     * 
     * @param li disinterested listener
     */
    void removeBooksListener(BooksListener li);
}
