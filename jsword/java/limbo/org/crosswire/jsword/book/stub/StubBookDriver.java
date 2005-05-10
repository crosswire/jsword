/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $ID$
 */
package org.crosswire.jsword.book.stub;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookType;
import org.crosswire.jsword.book.basic.AbstractBookDriver;

/**
 * StubBookDriver is a simple stub implementation of BibleDriver that is
 * pretty much always going to work because it has no dependancies on external
 * files.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class StubBookDriver extends AbstractBookDriver
{
    /**
     * Setup the array of BookMetaDatas
     */
    public StubBookDriver()
    {
        books = new Book[]
        {
            new StubBook(this, "Stub Version", BookType.BIBLE), //$NON-NLS-1$
            new StubBook(this, "New Stub Version", BookType.BIBLE), //$NON-NLS-1$
            new StubBook(this, "Stub Comments", BookType.COMMENTARY), //$NON-NLS-1$
            new StubDictionary(this, "Stub Dict", BookType.DICTIONARY), //$NON-NLS-1$
        };
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookDriver#getBooks()
     */
    public Book[] getBooks()
    {
        return books;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookDriver#getDriverName()
     */
    public String getDriverName()
    {
        return "Stub"; //$NON-NLS-1$
    }

    /**
     * The meta data array
     */
    private Book[] books;
}