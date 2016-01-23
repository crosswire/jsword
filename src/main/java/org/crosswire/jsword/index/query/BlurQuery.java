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
import org.crosswire.jsword.passage.RestrictionType;

/**
 * A blur query specifies how much to blur the results of the right query before
 * ANDing it to the left.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class BlurQuery extends AbstractBinaryQuery {
    /**
     * Create a query that specifies how much to blur the results of the right
     * query before ANDing it to the left.
     * 
     * @param theLeftQuery
     * @param theRightQuery
     * @param theFactor the amount of blurring
     */
    public BlurQuery(Query theLeftQuery, Query theRightQuery, int theFactor) {
        super(theLeftQuery, theRightQuery);
        factor = theFactor;
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
            return right;
        }

        right.blur(factor, RestrictionType.getDefaultBlurRestriction());

        left.retainAll(right);

        return left;
    }

    /**
     * @return the blur factor
     */
    public int getFactor() {
        return factor;
    }

    private int factor;
}
