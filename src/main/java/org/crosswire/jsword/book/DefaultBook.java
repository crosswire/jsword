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

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines a single default book.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class DefaultBook {
    public DefaultBook(BookList bookList, BookFilter bookFilter) {
        books = bookList;
        filter = bookFilter;
    }

    /**
     * Set the default Book. It must satisfy the filter.
     * 
     * @param newBook
     *            The version to use as default.
     */
    public void setDefault(Book newBook) {
        if (filter.test(newBook)) {
            book = newBook;
        }
    }

    /**
     * Set the default Book conditionally. It has to satisfy the filter and the
     * book must not currently be set.
     * 
     * @param newBook
     *            The version to use as default.
     */
    public void setDefaultConditionally(Book newBook) {
        if (book == null) {
            setDefault(newBook);
        }
    }

    /**
     * Unset the current default book and attempt to appoint another.
     */
    protected void unsetDefault() {
        book = null;

        checkReplacement();
    }

    /**
     * Unset the current default book, if it matches the argument and attempt to
     * appoint another.
     * 
     * @param oldBook the book to unset if it is the default
     */
    protected void unsetDefaultConditionally(Book oldBook) {
        if (book == oldBook) {
            unsetDefault();
        }
    }

    /**
     * Get the current default book or null if there is none.
     * 
     * @return the current default version
     */
    public Book getDefault() {
        return book;
    }

    /**
     * This method is identical to <code>getDefault().getName()</code> and is
     * only used by Config which works best with strings under reflection.
     * 
     * @return the default book name
     */
    public String getDefaultName() {
        if (book == null) {
            return null;
        }

        return book.getName();
    }

    /**
     * Trawl through all the known Books satisfying the filter looking for the
     * one matching the given name.
     * <p>
     * This method is for use with config scripts and other things that
     * <b>need</b> to work with Strings. The preferred method is to use Book
     * objects.
     * <p>
     * This method is picky in that it only matches when the driver and the
     * version are the same. The user (probably) only cares about the version
     * though, and so might be disappointed when we fail to match AV (FooDriver)
     * against AV (BarDriver).
     * 
     * @param name
     *            The version to use as default.
     */
    public void setDefaultByName(String name) {
        if (name == null || name.length() == 0) {
            LOGGER.warn("Attempt to set empty book as default. Ignoring");
            return;
        }

        for (Book aBook : books.getBooks(filter)) {
            if (aBook.match(name)) {
                setDefault(aBook);
                return;
            }
        }

        LOGGER.warn("Book not found. Ignoring: {}", name);
    }

    /**
     * Go through all of the current books checking to see if we need to replace
     * the current defaults with one of these.
     */
    protected void checkReplacement() {
        List<Book> bookList = books.getBooks(filter);

        Iterator<Book> it = bookList.iterator();
        if (it.hasNext()) {
            book = it.next();
        }
    }

    /**
     * The default book
     */
    private Book book;

    /**
     * The list of candidate books.
     */
    private final BookList books;

    /**
     * The filter against books that returns candidates.
     */
    private final BookFilter filter;

    /**
     * The log stream
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultBook.class);
}
