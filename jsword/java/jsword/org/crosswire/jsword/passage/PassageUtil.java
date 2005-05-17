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

/**
 * A Utility class containing various static methods.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class PassageUtil
{
    /**
     * Prevent Instansiation
     */
    private PassageUtil()
    {
    }

    /**
     * Do we remember the original string used to configure us?
     * @param persistentNaming True to keep the old string
     *        False (default) to generate a new better one
     */
    public static void setPersistentNaming(boolean persistentNaming)
    {
        PassageUtil.persistentNaming = persistentNaming;
    }

    /**
     * Do we remember the original string used to configure us?
     * @return True if we keep the old string
     *         False (default) if we generate a new better one
     */
    public static boolean isPersistentNaming()
    {
        return persistentNaming;
    }

    /**
     * By default do we remember the original string used to configure us?
     * @return false getDefaultPersistentNaming() is always false
     */
    public static boolean getDefaultPersistentNaming()
    {
        return false;
    }
    /**
     * Do we store the original string?
     */
    private static boolean persistentNaming = getDefaultPersistentNaming();
}
