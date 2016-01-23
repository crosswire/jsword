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
package org.crosswire.jsword.index;

import java.util.EventObject;

/**
 * An IndexStatusEvent is fired whenever the IndexStatus of a book has changed.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class IndexStatusEvent extends EventObject {
    /**
     * Basic constructor
     * 
     * @param source the source for the event
     * @param status
     *            The new status of the book.
     */
    public IndexStatusEvent(Object source, IndexStatus status) {
        super(source);

        indexStatus = status;
    }

    /**
     * @return Returns the indexStatus.
     */
    public IndexStatus getIndexStatus() {
        return indexStatus;
    }

    /**
     * The indexStatus of the book.
     */
    private IndexStatus indexStatus;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3834876879554819894L;

}
