/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.jsword.index.search;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.index.Index;
import org.crosswire.jsword.index.query.Query;
import org.crosswire.jsword.passage.Key;

/**
 * The central interface to all searching.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public interface Searcher {
    /**
     * Setup the index that this parser can use to do word level searches
     * 
     * @param index
     *            The Index to query for words
     */
    void init(Index index);

    /**
     * Take a search request and decipher it into a Passage.
     * 
     * @param request
     *            The request
     * @return The matching verses
     * @throws BookException 
     */
    Key search(SearchRequest request) throws BookException;

    /**
     * Take a search request and decipher it into a Passage.
     * 
     * @param request
     *            The request
     * @return The matching verses
     * @throws BookException 
     */
    Key search(Query request) throws BookException;
}
