/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id:AbstractIndex.java 983 2006-01-23 14:10:49 -0500 (Mon, 23 Jan 2006) dmsmith $
 */
package org.crosswire.jsword.index;

import org.crosswire.jsword.index.search.SearchModifier;

/**
 * A simple implementation of an Index that provides the set/get for
 * SearchModifier.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */

public abstract class AbstractIndex implements Index {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.index.search.Index#setSearchModifier(org.crosswire
     * .jsword.index.search.SearchModifier)
     */
    public void setSearchModifier(SearchModifier theModifier) {
        modifier = theModifier;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.index.search.Index#getSearchModifier()
     */
    public SearchModifier getSearchModifier() {
        return modifier;
    }

    /**
     * How the search is to be modified.
     */
    private SearchModifier modifier;
}
