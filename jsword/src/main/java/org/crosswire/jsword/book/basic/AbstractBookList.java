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
package org.crosswire.jsword.book.basic;

import java.util.Collections;
import java.util.List;

import org.crosswire.common.util.CollectionUtil;
import org.crosswire.common.util.EventListenerList;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilterIterator;
import org.crosswire.jsword.book.BookList;
import org.crosswire.jsword.book.BooksEvent;
import org.crosswire.jsword.book.BooksListener;

/**
 * A basic implementation of BookList.
 * The methods in this abstract class are duplicates of those in Books, so
 * bugs fixed in one should be fixed in the other too.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public abstract class AbstractBookList implements BookList
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookList#getBooks(org.crosswire.jsword.book.BookFilter)
     */
    public List getBooks(BookFilter filter)
    {
        List temp = CollectionUtil.createList(new BookFilterIterator(getBooks(), filter));
        return Collections.unmodifiableList(temp);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookList#addBooksListener(org.crosswire.jsword.book.BooksListener)
     */
    public synchronized void addBooksListener(BooksListener li)
    {
        listeners.add(BooksListener.class, li);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookList#removeBooksListener(org.crosswire.jsword.book.BooksListener)
     */
    public synchronized void removeBooksListener(BooksListener li)
    {
        listeners.remove(BooksListener.class, li);
    }

    /**
     * Kick of an event sequence
     * @param source The event source
     * @param book The changed Book
     * @param added Is it added?
     */
    protected static synchronized void fireBooksChanged(Object source, Book book, boolean added)
    {
        // Guaranteed to return a non-null array
        Object[] contents = listeners.getListenerList();

        // Process the listeners last to first, notifying
        // those that are interested in this event
        BooksEvent ev = null;
        for (int i = contents.length - 2; i >= 0; i -= 2)
        {
            if (contents[i] == BooksListener.class)
            {
                if (ev == null)
                {
                    ev = new BooksEvent(source, book, added);
                }

                if (added)
                {
                    ((BooksListener) contents[i + 1]).bookAdded(ev);
                }
                else
                {
                    ((BooksListener) contents[i + 1]).bookRemoved(ev);
                }
            }
        }
    }

    /**
     * The list of listeners
     */
    private static EventListenerList listeners = new EventListenerList();
}
