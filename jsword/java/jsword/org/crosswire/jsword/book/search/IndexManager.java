package org.crosswire.jsword.book.search;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;

/**
 * .
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
public interface IndexManager
{
    /**
     * Detects if index data has been stored for this Bible already
     */
    public boolean isIndexed(Book book);

    /**
     * Create a new Searcher.
     */
    public Index getIndex(Book book) throws BookException;

    /**
     * Read from the given source version to generate ourselves. On completion
     * of this method the index should be usable.
     */
    public void scheduleIndexCreation(Book book);

    /**
     * Tidy up after yourself and remove all the files that make up any indexes
     * you created.
     */
    public void deleteIndex(Book book) throws BookException;
}
