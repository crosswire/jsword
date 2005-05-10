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
 * ID: $Id$
 */
package org.crosswire.jsword.book.search.lucene;

/**
 * A binary token has a left token and right token.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [ dmsmith555 at yahoo dot com]
 */
public abstract class AbstractBinaryQuery implements Query
{

    /**
     * 
     */
    public AbstractBinaryQuery(Query theLeftToken, Query theRightToken)
    {
        leftToken = theLeftToken;
        rightToken = theRightToken;
    }

    /**
     * @return Returns the leftToken.
     */
    public Query getLeftToken()
    {
        return leftToken;
    }

    /**
     * @return Returns the rightToken.
     */
    public Query getRightToken()
    {
        return rightToken;
    }

    private Query leftToken;
    private Query rightToken;
}
