
package org.crosswire.jsword.book;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator that filters as it goes.
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
public class BookFilterIterator implements Iterator
{
    /**
     * Simple ctor
     * @param filter The filter to use, if null, will iterate over all values
     */
    public BookFilterIterator(Iterator it, BookFilter filter)
    {
        this.it = it;
        this.filter = filter;

        findNext();
    }

    /**
     * Are there any more?
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext()
    {
        return next != null;
    }

    /**
     * Get the next. Hmmm using finally to avoid creating a temporary local
     * variable. Just how evil is this?
     * @see java.util.Iterator#next()
     */
    public Object next()
    {
        if (next == null)
        {
            throw new NoSuchElementException();
        }
        
        try
        {
            return next;
        }
        finally
        {
            findNext();
        }
    }

    /**
     * Can't do this.
     * @see java.util.Iterator#remove()
     */
    public void remove()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Store the next (if there is one)
     */
    private void findNext()
    {
        if (filter == null)
        {
            next = null;
            return;
        }
        
        do
        {
            if (!it.hasNext())
            {
                next = null;
                return;
            }

            next = (BookMetaData) it.next();
        }
        while (!filter.test(next));
    }

    /**
     * The stored next value
     */
    private BookMetaData next = null;

    /**
     * The source of filtered values
     */
    private Iterator it;

    /**
     * The value filter
     */
    private BookFilter filter;
}
