/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.book.search;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;

/**
 * An index into a body of text that knows what words exist and where they are.
 *
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public interface Index
{
    /**
     * Find the set of references that satisfy the query. Query is anything that
     * the underlying index can handle.
     * If the <code>query</code> being searched for is null then an empty Key
     * <b>MUST</b> be returned. Users of this index may use this functionality
     * to get empty KeyLists which they then use to aggregate other searches
     * done on this index.
     * @param query The text to search for
     * @return The references to the word
     */
    public Key find(String query) throws BookException;

    /**
     * An index must be able to create KeyLists for users in a similar way to
     * the Book that it is indexing.
     * @param name The string to convert to a Key
     * @return A new Key representing the given string, if possible
     * @throws NoSuchKeyException If the string can not be turned into a Key
     * @see org.crosswire.jsword.passage.KeyFactory#getKey(String)
     */
    public Key getKey(String name) throws NoSuchKeyException;

    /**
     * Set any modifier for the current and subsequent search.
     * Using null will clear the search modifier.
     *
     * @param modifier how to modify the search and its results.
     */
    public void setSearchModifier(SearchModifier modifier);

    /**
     * Get the current SearchModifier. If there is none then return null.
     * @return the current search modifier, or null if there is not one.
     */
    public SearchModifier getSearchModifier();
}
