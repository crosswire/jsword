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
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.book.sword;

/**
 * A Constants to help the SwordBookDriver to read Sword format data.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Mark Goodwin [mark at thorubio dot org]
 * @author Joe Walker [joe at eireneh dot com]
 * @author The Sword project (don't know who - no credits in original files
 *         (canon.h))
 */
public final class SwordConstants {
    /**
     * Prevent instantiation
     */
    private SwordConstants() {
    }

    /**
     * New testament data files
     */
    public static final String FILE_NT = "nt";

    /**
     * Old testament data files
     */
    public static final String FILE_OT = "ot";

    /**
     * Index file extensions
     */
    public static final String EXTENSION_VSS = ".vss";

    /**
     * Extension for index files
     */
    public static final String EXTENSION_INDEX = ".idx";

    /**
     * Extension for data files
     */
    public static final String EXTENSION_DATA = ".dat";

    /**
     * Extension for config files
     */
    public static final String EXTENSION_CONF = ".conf";

    /**
     * The data directory
     */
    public static final String DIR_DATA = "modules";

    /**
     * The configuration directory
     */
    public static final String DIR_CONF = "mods.d";

    /**
     * The configuration directory
     */
    public static final String DIR_CONF_OVERRIDE = "jsword-mods.d";

}
