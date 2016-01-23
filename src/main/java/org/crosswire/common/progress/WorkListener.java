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
package org.crosswire.common.progress;

import java.util.EventListener;

/**
 * Implement WorkListener and call myClassObj.addProgressListener() to receive
 * WorkEvents when ever we make progress.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public interface WorkListener extends EventListener {
    /**
     * This method is called to indicate that some progress has been made. The
     * amount of progress is indicated by ev.getPercent()
     * 
     * @param ev
     *            Describes the progress
     */
    void workProgressed(WorkEvent ev);

    /**
     * This method is called to indicate that the work state has changed, perhaps finished.
     * 
     * @param ev
     *            Describes the progress
     */
    void workStateChanged(WorkEvent ev);
}
