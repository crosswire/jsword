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
 * Copyright: 2007
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: Bookmark.java 1605 2007-08-03 21:34:46Z dmsmith $
 */
package org.crosswire.jsword.book.basic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.Bookmark;
import org.crosswire.jsword.index.search.SearchRequest;

/**
 * A Bookmark remembers a particular view of one or more Books. What is viewed
 * regarding a book set is either a SearchRequest or a key lookup request.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class DefaultBookmark implements Bookmark {
    /**
     * Create an empty default bookmark
     */
    public DefaultBookmark() {
        books = new ArrayList();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.Bookmark#addBook(org.crosswire.jsword.book.
     * Book)
     */
    public void addBook(Book book) {
        books.add(book);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.Bookmark#getBooks()
     */
    public List getBooks() {
        return Collections.unmodifiableList(books);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.Bookmark#setSearchRequest(org.crosswire.jsword
     * .index.search.SearchRequest)
     */
    public void setSearchRequest(SearchRequest request) {
        searchRequest = request;
        lookupRequest = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.Bookmark#getSearchRequest()
     */
    public SearchRequest getSearchRequest() {
        return searchRequest;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.Bookmark#setLookupRequest(java.lang.String)
     */
    public void setLookupRequest(String request) {
        lookupRequest = request;
        searchRequest = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.Bookmark#getLookupRequest()
     */
    public String getLookupRequest() {
        return lookupRequest;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.Bookmark#getBookData()
     */
    public BookData getBookData() {
        return null;
    }

    /**
     * This needs to be declared here so that it is visible as a method on a
     * derived Bookmark.
     * 
     * @return A complete copy of ourselves
     */
    public Object clone() {
        Object clone = null;
        try {
            clone = super.clone();
        } catch (CloneNotSupportedException e) {
            assert false : e;
        }
        return clone;
    }

    /**
     * The list of books.
     */
    private List books;

    /**
     * The lookup request.
     */
    private String lookupRequest;

    /**
     * The search request.
     */
    private SearchRequest searchRequest;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 6959196267292499574L;
}
