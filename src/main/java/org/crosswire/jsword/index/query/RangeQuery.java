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

import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.index.Index;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;

/**
 * A range query specifies how a range should be included in the search. It
 * provides a range, a modifier (AND [+] or AND NOT [-]).
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class RangeQuery extends AbstractQuery {

    /**
     * Construct a query from the range specification.
     * 
     * @param theRange
     */
    public RangeQuery(String theRange) {
        super(theRange);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.index.search.parse.Query#find(org.crosswire.jsword
     * .index.search.Index)
     */
    public Key find(Index index) throws BookException {
        String range = getQuery();
        try {
            return index.getKey(range);
        } catch (NoSuchKeyException e) {
            // TRANSLATOR: User error condition: The passage range could not be understood. {0} is a placeholder for the passage.
            throw new BookException(JSMsg.gettext("Syntax Error: Invalid passage \"{0}\"", range), e);
        }
    }
}
