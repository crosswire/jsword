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
 */
package org.crosswire.jsword.index.query;

/**
 * A binary query has a left query and right query.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public abstract class AbstractBinaryQuery implements BinaryQuery {
    /**
     * Create a query consisting of two queries.
     * 
     * @param theLeftQuery
     * @param theRightQuery
     */
    public AbstractBinaryQuery(Query theLeftQuery, Query theRightQuery) {
        leftQuery = theLeftQuery;
        rightQuery = theRightQuery;
    }

    /**
     * @return Returns the leftQuery.
     */
    public Query getLeftQuery() {
        return leftQuery;
    }

    /**
     * @return Returns the rightQuery.
     */
    public Query getRightQuery() {
        return rightQuery;
    }

    private Query leftQuery;
    private Query rightQuery;
}
