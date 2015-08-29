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

import java.io.Closeable;

import org.crosswire.jsword.book.sword.SwordBookMetaData;

/**
 * Marker interface for objects holding open files that should be freed up upon finishing
 *
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public interface OpenFileState extends Closeable {

    SwordBookMetaData getBookMetaData();

    void releaseResources();

    /**
     * @return latest access before releasing back to the pool
     */
    long getLastAccess();

    /**
     * Sets the last access time
     * @param lastAccess the time at which this instance was last accessed
     */
    void setLastAccess(long lastAccess);
}
