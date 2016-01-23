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
package org.crosswire.common.activate;

/**
 * A class can be Activatable if it needs a significant amount of memory on an
 * irregular basis, and so would benefit from being told when to wake-up and
 * when to conserver memory.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public interface Activatable {
    /**
     * Called to indicate that the Book should initialize itself, and consume
     * whatever system resources it needs to be able to respond to other
     * queries.
     * 
     * @param lock
     *            An attempt to ensure that only the Activator calls this method
     */
    void activate(Lock lock);

    /**
     * Called to indicate that the Book should release whatever system resources
     * it can to make way for other uses.
     * 
     * @param lock
     *            An attempt to ensure that only the Activator calls this method
     */
    void deactivate(Lock lock);
}
