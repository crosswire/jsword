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
package org.crosswire.jsword.book.sword;

/**
 * A Constants to help the SwordBookDriver to read Sword format data.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Mark Goodwin
 * @author Joe Walker
 * @author The SWORD Project (don't know who - no credits in original files (canon.h))
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
     * The configuration directory with a trailing /
     */
    public static final String PATH_CONF = "mods.d/";
}
