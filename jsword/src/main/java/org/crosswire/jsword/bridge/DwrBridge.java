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
 * Copyright: 2008
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: org.eclipse.jdt.ui.prefs 1178 2006-11-06 12:48:02Z dmsmith $
 */
package org.crosswire.jsword.bridge;

import java.util.Iterator;
import java.util.List;

import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.Passage;

/**
 * The DWR DwrBridge adapts JSword to DWR. This is based upon APIExamples.
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class DwrBridge
{
    /**
     * Get just the canonical text of one or more book entries without any markup.
     *
     * @param bookInitials the book to use
     * @param reference a reference, appropriate for the book, of one or more entries
     */
    public String getPlainText(String bookInitials, String reference) throws BookException, NoSuchKeyException
    {
        Book book = getInstalledBook(bookInitials);
        if (book == null)
        {
            return ""; //$NON-NLS-1$
        }

        Key      key  = book.getKey(reference);
        BookData data = new BookData(book, key);
        return OSISUtil.getCanonicalText(data.getOsisFragment());
    }

    /**
     * Obtain a SAX event provider for the OSIS document representation of one or more book entries.
     *
     * @param bookInitials the book to use
     * @param reference a reference, appropriate for the book, of one or more entries
     */
    private SAXEventProvider getOSIS(String bookInitials, String reference, int maxKeyCount) throws BookException, NoSuchKeyException
    {
        if (bookInitials == null || reference == null)
        {
            return null;
        }

        Book book = getInstalledBook(bookInitials);

        Key key = null;
        if (BookCategory.BIBLE.equals(book.getBookCategory()))
        {
            key = book.getKey(reference);
            key = ((Passage) key).trimVerses(maxKeyCount);
        }
        else
        {
            key = book.createEmptyKeyList();
            
            Iterator iter = book.getKey(reference).iterator();
            int count = 0;
            while (iter.hasNext())
            {
                if (++count >= maxKeyCount)
                {
                    break;
                }
                key.addAll((Key) iter.next());
            }
        }

        BookData data = new BookData(book, key);

        return data.getSAXEventProvider();
    }

    /**
     * Get a list of all installed books.
     * @return the list of installed books
     */
    private List getInstalledBooks()
    {
        return Books.installed().getBooks();
    }

    /**
     * Get a list of installed books by BookFilter.
     * @param filter The book filter
     * @see BookFilter
     * @see Books
     */
    private List getInstalledBooks(BookFilter filter)
    {
        return Books.installed().getBooks(filter);
    }

    /**
     * Get a list of books by CustomFilter specification
     * @param filter The filter string
     * @see BookFilters#getCustom(java.lang.String)
     * @see Books
     */
    private List getInstalledBooks(String filterSpec)
    {
        return getInstalledBooks(BookFilters.getCustom(filterSpec));
    }

    /**
     * Get a particular installed book by initials.
     * 
     * @param bookInitials The book name to search for
     * @return The found book. Null otherwise.
     */
    private Book getInstalledBook(String bookInitials)
    {
        return Books.installed().getBook(bookInitials);
    }


}
