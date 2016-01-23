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

import java.util.EventObject;

/**
 * A WorkEvent happens whenever a task makes some progress in doing measurable
 * work.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class WorkEvent extends EventObject {
    /**
     * Initialize a WorkEvent
     * 
     * @param source the job that has made progress
     */
    public WorkEvent(Progress source) {
        super(source);
    }

    /**
     * Accessor for the Job
     * 
     * @return the job that has made progress
     */
    public Progress getJob() {
        return (Progress) getSource();
    }

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3976736990807011378L;
}
