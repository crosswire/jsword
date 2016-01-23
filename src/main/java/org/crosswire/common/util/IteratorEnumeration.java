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
 *
 */
package org.crosswire.common.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Convert an Iterator into a Enumeration.
 * <p>
 * The only real difference between the 2 is the naming and that Enumeration
 * does not have the delete method.
 * </p>
 * 
 * @param <E> The type of the elements returned by this iterator
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public final class IteratorEnumeration<E> implements Enumeration<E> {
    /**
     * Create an Enumeration that proxies to an Iterator.
     * 
     * @param it the iterator to wrap.
     */
    public IteratorEnumeration(Iterator<E> it) {
        this.it = it;
    }

    /* (non-Javadoc)
     * @see java.util.Enumeration#hasMoreElements()
     */
    public boolean hasMoreElements() {
        return it.hasNext();
    }

    /* (non-Javadoc)
     * @see java.util.Enumeration#nextElement()
     */
    public E nextElement() throws NoSuchElementException {
        return it.next();
    }

    /**
     * The Iterator that we are proxying to
     */
    private Iterator<E> it;
}
