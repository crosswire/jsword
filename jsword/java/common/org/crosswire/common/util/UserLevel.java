
package org.crosswire.common.util;

import java.util.HashMap;
import java.util.Map;

/**
 * A UserLevel keeps a track of how advanced the user is.
 * It may not be a graphical component, but many graphical components
 * depend on it, and it doesn't seem to be a 'util'.
 * <p>We should consider having a addUserLevelListener interface for
 * people that want to know about UserLevel changes. Hmmmm.
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class UserLevel
{
    /**
     * Ensure that we can't be instansiated
     */
    private UserLevel(int ordinal, String name)
    {
        this.ordinal = ordinal;
        this.name = name;

        map.put(name, this);
    }

    /**
     * Accessor for the user level
     * @return the current users name
     */
    public static UserLevel getGlobalUserLevel()
    {
        return global;
    }

    /**
     * Accessor for the user level
     * @param level The new user level
     */
    public static void setGlobalUserLevel(UserLevel level)
    {
        UserLevel.global = level;
    }

    /**
     * Accessor for this UserLevels name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get a Username by string.
     * @param levelname
     * @return UserLevel
     */
    public static UserLevel forName(String levelname)
    {
        return (UserLevel) map.get(levelname);
    }

    /**
     * Is this level available given the global UserLevel
     */
    public boolean isAvailable()
    {
        return this.ordinal <= global.ordinal;
    }

    /** The known UserLevels */
    public static final Map map = new HashMap();

    /** User level - Beginner */
    public static final UserLevel LEVEL_BEGINNER = new UserLevel(0, "Beginner");

    /** User level - Intermediate */
    public static final UserLevel LEVEL_INTERMEDIATE = new UserLevel(1, "Intermediate");

    /** User level - Advanced */
    public static final UserLevel LEVEL_ADVANCED = new UserLevel(2, "Advanced");

    /** The level name */
    private String name;
    
    /** The ordinal of this UserLevel */
    private int ordinal;

    /** The current User level */
    private static UserLevel global = LEVEL_BEGINNER;
}
