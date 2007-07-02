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
 * ID: $Id:DefaultSearchRequest.java 984 2006-01-23 14:18:33 -0500 (Mon, 23 Jan 2006) dmsmith $
 */
package org.crosswire.jsword.index.search;


/**
 * A default implementation of a SearchRequest.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class DefaultSearchRequest implements SearchRequest
{

    /**
     * Create a DefaultSearchRequest for the provided request and
     * the provided modifiers.
     * @param theRequest what is being searched
     * @param theModifier how the search is to be modified
     */
    public DefaultSearchRequest(String theRequest, SearchModifier theModifier)
    {
        request = theRequest;
        modifier = theModifier;
    }

    /**
     * Create a DefaultSearchRequest for the provided request.
     * @param theRequest what is being searched
     */
    public DefaultSearchRequest(String theRequest)
    {
        this(theRequest, null);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.index.search.SearchRequest#isRanked()
     */
    public SearchModifier getSearchModifier()
    {
        return modifier;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.index.search.SearchRequest#getRequest()
     */
    public String getRequest()
    {
        return request;
    }

    /**
     * The actual search request
     */
    private String request;

    /**
     * How the search is to be modified
     */
    private SearchModifier modifier;
}
