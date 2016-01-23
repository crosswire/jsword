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
package org.crosswire.jsword.passage;

import java.util.EventListener;

/**
 * A PassageListener gets told when the verses in a Passage have changed (added
 * or removed).
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public interface PassageListener extends EventListener {
    /**
     * Sent after stuff has been added to the Passage, more info about what and
     * where can be had from the Event
     * 
     * @param ev
     *            a PassageEvent encapsulating the event information
     */
    void versesAdded(PassageEvent ev);

    /**
     * Sent after stuff has been removed from the Passage, more info about what
     * and where can be had from the Event
     * 
     * @param ev
     *            a PassageEvent encapsulating the event information
     */
    void versesRemoved(PassageEvent ev);

    /**
     * Sent after verses have been simultaneously added and removed from the
     * Passage, more info about what and where can be had from the Event
     * 
     * @param ev
     *            a PassageEvent encapsulating the event information
     */
    void versesChanged(PassageEvent ev);
}
