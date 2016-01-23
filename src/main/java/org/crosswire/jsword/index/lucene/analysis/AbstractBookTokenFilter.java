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
package org.crosswire.jsword.index.lucene.analysis;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.crosswire.jsword.book.Book;

/**
 * An AbstractBookTokenFilter ties a Lucene TokenFilter to a Book.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public abstract class AbstractBookTokenFilter extends TokenFilter {

    /**
     * Create a TokenFilter not tied to a Book.
     * 
     * @param input
     *            the token stream to filter
     */
    public AbstractBookTokenFilter(TokenStream input) {
        this(null, input);
    }

    /**
     * Create a TokenFilter tied to a Book.
     * 
     * @param book the book
     * @param input
     *            the token stream to filter
     */
    public AbstractBookTokenFilter(Book book, TokenStream input) {
        super(input);
        this.book = book;
    }

    /**
     * @return the book
     */
    public Book getBook() {
        return book;
    }

    /**
     * @param book
     *            the book to set
     */
    public void setBook(Book book) {
        this.book = book;
    }

    /* Define to quite FindBugs */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    /* Define to quite FindBugs */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    private Book book;
}
