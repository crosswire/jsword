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

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.index.Index;
import org.crosswire.jsword.passage.Key;

/**
 * An "And Not" query specifies that a result needs to be in the left but not in
 * the right query result.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class AndNotQuery extends AbstractBinaryQuery {

    /**
     * Create a query where the right query result is subtracted from the left
     * query result.
     * 
     * @param theLeftQuery
     * @param theRightQuery
     */
    public AndNotQuery(Query theLeftQuery, Query theRightQuery) {
        super(theLeftQuery, theRightQuery);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.index.search.parse.Query#find(org.crosswire.jsword
     * .index.search.Index)
     */
    public Key find(Index index) throws BookException {
        Key left = getLeftQuery().find(index);

        if (left.isEmpty()) {
            return left;
        }

        Key right = getRightQuery().find(index);

        if (right.isEmpty()) {
            return left;
        }

        left.removeAll(right);

        return left;
    }

}
