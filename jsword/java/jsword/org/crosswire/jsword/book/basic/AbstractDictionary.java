
package org.crosswire.jsword.book.basic;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Dictionary;
import org.crosswire.jsword.book.Key;
import org.crosswire.jsword.book.Search;

/**
 * An AbstractDictionary implements a few of the more generic methods of Dictionary.
 * This class does a lot of work in helping make search easier, and implementing
 * some basic write methods. 
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
public abstract class AbstractDictionary implements Dictionary
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#activate()
     */
    public void activate()
    {
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#deactivate()
     */
    public void deactivate()
    {
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getBookMetaData()
     */
    public BookMetaData getBookMetaData()
    {
        return getDictionaryMetaData();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#find(org.crosswire.jsword.book.Search)
     */
    public Key find(Search search) throws BookException
    {
        return getKey("");
    }
}
