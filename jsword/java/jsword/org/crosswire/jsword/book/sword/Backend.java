package org.crosswire.jsword.book.sword;

import org.crosswire.common.activate.Activatable;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyList;

/**
 * A generic way to read data from disk for later formatting.
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
public interface Backend extends Activatable
{
    /**
     * Initialise a Backend before use. This method needs to call addKey() a
     * number of times on SwordDictionary
     * @throws BookException If we should not be used for some reason
     */
    public KeyList readIndex();

    /**
     * Get the bytes alotted for the given verse
     * @param verse The verse to fetch
     * @return byte[] The data for the verse in question
     * @throws BookException If the data can not be read.
     */
    public byte[] getRawText(Key key) throws BookException;
}
