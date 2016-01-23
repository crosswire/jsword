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
 * © CrossWire Bible Society, 2013 - 2016
 *
 */
package org.crosswire.jsword.book.sword.state;

import org.crosswire.jsword.book.BookMetaData;

/**
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public abstract class AbstractOpenFileState implements OpenFileState {
    /**
     * Create an AbstractOpenFileState tied to a BookMetaData.
     * 
     * @param bmd the BookMetaData for this OpenFileState
     */
    public AbstractOpenFileState(BookMetaData bmd) {
        bookMetaData = bmd;
        lastAccess = System.currentTimeMillis();
    }

    /**
     * Allows us to decide whether to release the resources or continue using them
     */
    public void close() {
        OpenFileStateManager.instance().release(this);
    }

    /**
     * Get the BookMetaData for this OpenFileState.
     * 
     * @return the BookMetaData
     */
    public BookMetaData getBookMetaData() {
        return bookMetaData;
    }
    /**
      * @return latest access before releasing back to the pool
     */
    public long getLastAccess() {
        return this.lastAccess;
    }

    /**
     * @param lastAccess last time the file state was accessed
     */
    public void setLastAccess(final long lastAccess) {
        this.lastAccess = lastAccess;
    }

    /**
     * The BookMetaData for this OpenFileState. Used to locate files.
     */
    private BookMetaData bookMetaData;

    /**
     * The time of last access, used for LRU expiration of state.
     */
    private long lastAccess;
}
