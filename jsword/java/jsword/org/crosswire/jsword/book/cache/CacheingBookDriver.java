
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
 * @author Joe Walker [joe at getahead dot ltd dot uk]
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
