package org.crosswire.jsword.book;

import java.util.List;

/**
 * There are several lists of Books, the most important being the installed
 * Books, however there may be others like the available books or books from
 * a specific driver.
 * This interface provides a common method of accessing all of them.
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
public interface BookList
{
    /**
     * Get an iterator over all the Books of all types.
     */
    public List getBooks();

    /**
     * Get a filtered iterator over all the Books.
     * @see BookFilters
     */
    public List getBooks(BookFilter filter);

    /**
     * Remove a BibleListener from our list of listeners
     * @param li The old listener
     */
    public void addBooksListener(BooksListener li);

    /**
     * Add a BibleListener to our list of listeners
     * @param li The new listener
     */
    public void removeBooksListener(BooksListener li);
}
