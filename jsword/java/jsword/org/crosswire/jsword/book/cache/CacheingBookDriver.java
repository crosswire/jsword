
package org.crosswire.jsword.book.cache;

import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.basic.AbstractBookDriver;

/**
 * CacheingBookDriver is designed to allow data to be cached as it is read.
 * 
 * The designed features of CacheingBookDriver are:
 * <li>Ability to cache multiple (possibly remote) sources</li>
 * <li>Use of JDBC style URL to help cached data to re-connect with source</li>
 * <li>Can be used with multiple cacheing schemes (sword, ser, ...)</li>
 * 
 * <p>So it is up to the actual cacheing scheme to distinguish cached data from
 * other normal data, and to be able to return a original source URL in case
 * not all of the data is present.
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
public class CacheingBookDriver extends AbstractBookDriver
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookDriver#getBooks()
     */
    public BookMetaData[] getBooks()
    {
        return null;
    }
}
