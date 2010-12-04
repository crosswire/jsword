/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2007
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: org.eclipse.jdt.ui.prefs 1178 2006-11-06 12:48:02Z dmsmith $
 */
package org.crosswire.common.progress;

/**
 * Progress can be  one of several modes, which correspond to the <code>Progress.beginJob()</code> calls.
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public enum ProgressMode {
    /**
     * Progress is working toward 100% and is supplying work from 0 to 100.
     */
    PERCENT,
    /**
     * Progress is working toward a number of units. It might be 100.
     */
    UNITS,
    /**
     * Progress is predicted on the basis of prior runs. The caller has supplied a useful map of the sections for last run.
     */
    PREDICTIVE,
    /**
     * Progress is entirely indeterminate. The user has not supplied a useful map of the sections for last run.
     */
    UNKNOWN
}
