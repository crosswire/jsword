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
package org.crosswire.jsword.passage;

import java.util.EventListener;

/**
 * A PassageListener gets told when the verses in a Passage have changed (added
 * or removed).
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public interface PassageListener extends EventListener
{
    /** 
     * Sent after stuff has been added to the Passage, more info about what and
     * where can be had from the Event
     * @param ev a PassageEvent encapuslating the event information
     */
    void versesAdded(PassageEvent ev);

    /**
     * Sent after stuff has been removed from the Passage, more info about what
     * and where can be had from the Event
     * @param ev a PassageEvent encapuslating the event information
     */
    void versesRemoved(PassageEvent ev);

    /** 
     * Sent after verses have been symultaneously added and removed from
     * the Passage, more info about what and where can be had from the Event
     * @param ev a PassageEvent encapuslating the event information
     */
    void versesChanged(PassageEvent ev);
}
