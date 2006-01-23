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
 * ID: $Id: BaseQuery.java 763 2005-07-27 19:26:43 -0400 (Wed, 27 Jul 2005) dmsmith $
 */
package org.crosswire.jsword.index.query;


/**
 * A base query is the smallest unit of search that the index can perform.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public abstract class AbstractQuery implements Query
{

    /**
     * Construct a query from a string.
     * 
     * @param theQuery
     */
    public AbstractQuery(String theQuery)
    {
        query = theQuery;
    }

    /**
     * @return the query
     */
    public String getQuery()
    {
        return query;
    }

    private String query;
}
