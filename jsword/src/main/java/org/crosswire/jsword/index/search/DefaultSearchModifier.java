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
 * ID: $Id:DefaultSearchModifier.java 984 2006-01-23 14:18:33 -0500 (Mon, 23 Jan 2006) dmsmith $
 */
package org.crosswire.jsword.index.search;


/**
 * The DefaultSearchModifier provides a simple implementation
 * of a SearchModifier.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */

public class DefaultSearchModifier implements SearchModifier
{

    /**
     * A default SearchModifier that returns all hits and does not rank the results.
     */
    public DefaultSearchModifier()
    {
        ranked     = false;
        maxResults = Integer.MAX_VALUE;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.index.search.SearchModifier#isRanked()
     */
    public boolean isRanked()
    {
        return ranked;
    }

    /**
     * Set whether or not the search should be ranked.
     * @param newRanked true if the search should be ranked
     */
    public void setRanked(boolean newRanked)
    {
        ranked = newRanked;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.index.search.SearchModifier#getMaxResults()
     */
    public int getMaxResults()
    {
        return maxResults;
    }

    /**
     * The maximum number of results to provide. A value of Integer.MAX_VALUE, the default, means get all results.
     * 
     * @param newMaxResults the maxResults to set
     */
    public void setMaxResults(int newMaxResults)
    {
        maxResults = newMaxResults;
    }

    /**
     * The indicator of whether the request should be ranked.
     */
    private boolean ranked;

    /**
     * The indicator of whether the request should be ranked.
     */
    private int maxResults;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 0L;
}
