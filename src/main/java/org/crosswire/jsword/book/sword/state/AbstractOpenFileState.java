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
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2013
 *     The copyright to this program is held by its authors.
 *
 */
package org.crosswire.jsword.book.sword.state;

/**
  *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public abstract class AbstractOpenFileState implements OpenFileState {
    private long lastAccess = System.currentTimeMillis();

    /**
     * Allows us to decide whether to release the resources or continue using them
     */
    public void close() {
        OpenFileStateManager.instance().release(this);
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
}
