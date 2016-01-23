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
 * © CrossWire Bible Society, 2007 - 2016
 *
 */
package org.crosswire.common.util;

/**
 * A TimeGate when entered will cause the gate to be closed for a specified
 * period of time.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class TimeGate {
    /**
     * Build a TimeGate that will allow entry no more often than count
     * milliseconds
     * 
     * @param count
     *            the length of time to keep the gate shut after opening it.
     */
    public TimeGate(int count) {
        closeTime = count;
    }

    /**
     * Determine whether entry through the gate is allowed. Opening the gate
     * will close it until the TimeGate's interval has passed.
     * 
     * @return true if one may enter.
     */
    public synchronized boolean open() {
        // check to see if the gate has been closed long enough.
        // If so, then open it and note the time that it was opened.
        long now = System.currentTimeMillis();
        if (now - then > closeTime) {
            then = now;
            return true;
        }

        // Otherwise the gate was opened not that long ago and
        // is still closed.
        return false;
    }

    /**
     * The interval during which the gate is closed.
     */
    private int closeTime;

    /**
     * The time in milliseconds that the gate last closed.
     */
    private long then;
}
