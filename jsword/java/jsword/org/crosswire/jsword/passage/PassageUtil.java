package org.crosswire.jsword.passage;

/**
 * A Utility class containing various static methods.
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class PassageUtil
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
    public static final void setPersistentNaming(boolean persistentNaming)
    {
        PassageUtil.persistentNaming = persistentNaming;
    }

    /**
     * Do we remember the original string used to configure us?
     * @return True if we keep the old string
     *         False (default) if we generate a new better one
     */
    public static final boolean isPersistentNaming()
    {
        return persistentNaming;
    }

    /**
     * By default do we remember the original string used to configure us?
     * @return false getDefaultPersistentNaming() is always false
     */
    public static final boolean getDefaultPersistentNaming()
    {
        return false;
    }
    /**
     * Do we store the original string?
     */
    private static boolean persistentNaming = getDefaultPersistentNaming();

}
