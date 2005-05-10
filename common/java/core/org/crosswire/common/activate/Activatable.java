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
 * ID: $ID$
 */
package org.crosswire.common.activate;

/**
 * A class can be Activatable if it needs a significant amount of memory on an
 * irregular basis, and so would benefit from being told when to wake-up and
 * when to conserver memory.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public interface Activatable
{
    /**
     * Called to indicate that the Book should initialize itself, and consume
     * whatever system resources it needs to be able to respond to other
     * queries.
     * @param lock An attempt to ensure that only the Activator calls this method
     */
    public void activate(Lock lock);

    /**
     * Called to indicate that the Book should release whatever system
     * resources it can to make way for other uses.
     * @param lock An attempt to ensure that only the Activator calls this method
     */
    public void deactivate(Lock lock);
}
