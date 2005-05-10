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
 * ID: $ID$
 */
package org.crosswire.jsword.book.search;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Key;

/**
 * The central interface to all searching.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public interface Searcher
{
    /**
     * Setup the index that this parser can use to do word level searches
     * @param index The Index to query for words
     */
    public void init(Index index);

    /**
     * Take a search request and decipher it into a Passage.
     * @param request The request
     * @return The matching verses
     */
    public Key search(SearchRequest request) throws BookException;

    /**
     * Take a search request and decipher it into a Passage.
     * @param request The request
     * @return The matching verses
     */
    public Key search(String request) throws BookException;
}
