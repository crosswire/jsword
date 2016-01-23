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

/**
 * A Utility class containing various static methods.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public final class PassageUtil {
    /**
     * Prevent instantiation
     */
    private PassageUtil() {
    }

    /**
     * Do we remember the original string used to configure us?
     * 
     * @param persistentNaming
     *            True to keep the old string False (default) to generate a new
     *            better one
     */
    public static void setPersistentNaming(boolean persistentNaming) {
        PassageUtil.persistentNaming = persistentNaming;
    }

    /**
     * Do we remember the original string used to configure us?
     * 
     * @return True if we keep the old string False (default) if we generate a
     *         new better one
     */
    public static boolean isPersistentNaming() {
        return persistentNaming;
    }

    /**
     * By default do we remember the original string used to configure us?
     * 
     * @return false getDefaultPersistentNaming() is always false
     */
    public static boolean getDefaultPersistentNaming() {
        return false;
    }

    /**
     * Do we store the original string?
     */
    private static boolean persistentNaming = getDefaultPersistentNaming();
}
