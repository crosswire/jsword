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

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.crosswire.common.util.Iterable;

/**
 * An iterator that filters as it goes.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class BookFilterIterator implements Iterable, Iterator {
    /**
     * Simple ctor
     * 
     * @param filter
     *            The filter to use, if null, will iterate over all values
     */
    public BookFilterIterator(List books, BookFilter filter) {
        this.it = books.iterator();
        this.filter = filter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Iterable#iterator()
     */
    public Iterator iterator() {
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        next = findNext();
        return next != null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Iterator#next()
     */
    public Object next() {
        if (next == null) {
            throw new NoSuchElementException();
        }
        return next;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Find the next (if there is one)
     */
    private Book findNext() {
        while (it.hasNext()) {
            Book book = (Book) it.next();
            if (filter == null || filter.test(book)) {
                return book;
            }
        }

        return null;
    }

    /**
     * The stored next value
     */
    private Book next;

    private Iterator it;

    /**
     * The value filter
     */
    private BookFilter filter;

}
