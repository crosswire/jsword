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
package org.crosswire.jsword.book.search.basic;

import org.crosswire.jsword.book.search.Index;
import org.crosswire.jsword.book.search.SearchModifier;

/**
 * A simple implementation of an Index that provides the
 * set/get for SearchModifier.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at gmail dot com]
 */

public abstract class AbstractIndex implements Index
{

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.Index#setSearchModifier(org.crosswire.jsword.book.search.SearchModifier)
     */
    public void setSearchModifier(SearchModifier theModifier)
    {
        modifier = theModifier;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.Index#getSearchModifier()
     */
    public SearchModifier getSearchModifier()
    {
        return modifier;
    }

    /**
     * How the search is to be modified.
     */
    private SearchModifier modifier;
}
