package org.crosswire.jsword.book.stub;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.BookType;
import org.crosswire.jsword.book.basic.AbstractBookDriver;

/**
 * StubBookDriver is a simple stub implementation of BibleDriver that is
 * pretty much always going to work because it has no dependancies on external
 * files.
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class StubBookDriver extends AbstractBookDriver
{
    /**
     * Setup the array of BookMetaDatas
     */
    public StubBookDriver()
    {
        Book[] books = new Book[]
        {
            new StubBook(this, "Stub Version", BookType.BIBLE), //$NON-NLS-1$
            new StubBook(this, "New Stub Version", BookType.BIBLE), //$NON-NLS-1$
            new StubBook(this, "Stub Comments", BookType.COMMENTARY), //$NON-NLS-1$
            new StubDictionary(this, "Stub Dict", BookType.DICTIONARY), //$NON-NLS-1$
        };

        bmds = new BookMetaData[books.length];

        for (int i = 0; i < books.length; i++)
        {
            bmds[i] = books[i].getBookMetaData();
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookDriver#getBooks()
     */
    public BookMetaData[] getBookMetaDatas()
    {
        return bmds;
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
    private BookMetaData[] bmds = null;
}