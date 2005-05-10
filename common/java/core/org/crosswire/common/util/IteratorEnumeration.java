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
package org.crosswire.common.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Convert an Iterator into a Enumeration.
 * <p>The only real difference between the 2 is the naming and
 * that Enumeration does not have the delete method.
 *
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class IteratorEnumeration implements Enumeration
{
    /**
     * Create an Enumeration that proxies to an Iterator
     */
    public IteratorEnumeration(Iterator it)
    {
        this.it = it;
    }

    /**
     * Returns true if the iteration has more elements
     */
    public boolean hasMoreElements()
    {
        return it.hasNext();
    }

    /**
     *  Returns the next element in the interation
     */
    public Object nextElement() throws NoSuchElementException
    {
        return it.next();
    }

    /**
     * The Iterator that we are proxying to
     */
    private Iterator it;
}
