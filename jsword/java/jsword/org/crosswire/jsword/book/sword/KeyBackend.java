
package org.crosswire.jsword.book.sword;

import java.util.List;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Key;

/**
 * This interface is very similar to Backend except that is uses a Key rather
 * than a verse as an index into what is needs to read.
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
 * @see org.crosswire.jsword.book.sword.Backend
 */
public interface KeyBackend
{
    /**
     * Initialise a Backend before use. This method needs to call addKey() a
     * number of times on SwordDictionary
     * @throws BookException If we should not be used for some reason
     */
    public List readIndex();

    /**
     * Return the bytes that make up the data for the given key
     * @param key The index of the data to lookup
     * @return byte[] The found data
     * @throws BookException If there is a problem reading the data
     */
    public byte[] getRawText(Key key) throws BookException;
}
