
package org.crosswire.jsword.book.basic;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.events.ProgressListener;

/**
 * The AbstractBookDriver class implements some BibleDriver methods, making a
 * simple read-only BibleDriver.
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public abstract class AbstractBookDriver implements BookDriver
{
    /**
     * Is this name capable of creating writing data in the correct format
     * as well as reading it?
     * @return true/false to indicate ability to write data
     */
    public boolean isWritable()
    {
        return false;
    }

    /**
     * Create a new Book, copied from the source
     * @param source The book to copy
     * @param li The place to report progress
     * @exception BookException If creation fails
     * @see org.crosswire.jsword.book.BookDriver#create(org.crosswire.jsword.book.Book, org.crosswire.jsword.book.events.ProgressListener)
     */
    public Book create(Book source, ProgressListener li) throws BookException
    {
        // if (!(source instanceof Bible))
        //     throw new BookException("bible_invalid_source");

        throw new BookException("bible_driver_readonly");
    }
}
