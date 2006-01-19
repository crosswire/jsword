/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.book;

import java.util.List;

/**
 * There are several lists of Books, the most important being the installed
 * Books, however there may be others like the available books or books from
 * a specific driver.
 * This interface provides a common method of accessing all of them.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @see gnu.lgpl.License
 * @author Joe Walker [joe at eireneh dot com]
 */
public interface BookList
{
    /**
     * Get an iterator over all the Books of all types.
     */
    List getBooks();

    /**
     * Get a filtered iterator over all the Books.
     * @see BookFilters
     */
    List getBooks(BookFilter filter);

    /**
     * Remove a BibleListener from our list of listeners
     * @param li The old listener
     */
    void addBooksListener(BooksListener li);

    /**
     * Add a BibleListener to our list of listeners
     * @param li The new listener
     */
    void removeBooksListener(BooksListener li);
}
