
package org.crosswire.common.util;

/**
* A UserLevel keeps a track of how advanced the user is.
* It may not be a graphical component, but many graphical components
* depend on it, and it doesn't seem to be a 'util'.
* <p>We should consider having a addUserLevelListener interface for
* people that want to know about UserLevel changes. Hmmmm.
*
* <table border='1' cellPadding='3' cellSpacing='0' width="100%">
* <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
* Distribution Licence:<br />
* Project B is free software; you can redistribute it
* and/or modify it under the terms of the GNU General Public License,
* version 2 as published by the Free Software Foundation.<br />
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* General Public License for more details.<br />
* The License is available on the internet
* <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
* <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
* MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
* The copyright to this program is held by it's authors.
* </font></td></tr></table>
* @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
* @see <{docs.Licence}>
* @author Joe Walker
*/
public class UserLevel
{
    /**
    * Ensure that we can't be instansiated
    */
    private UserLevel()
    {
    }

    /**
    * Accessor for the user level
    * @return the current users name
    */
    public static int getUserLevel()
    {
        return level;
    }

    /**
    * Accessor for the user level
    * @param level The new user level
    */
    public static void setUserLevel(int level)
    {
        UserLevel.level = level;
    }

    /**
    * Accessor for the user level
    * @param level The new user level
    */
    public static String[] getLevels()
    {
        return names;
    }

    /** User level - Beginner */
    public static final int LEVEL_BEGINNER = 0;

    /** User level - Intermediate */
    public static final int LEVEL_INTERMEDIATE = 1;

    /** User level - Advanced */
    public static final int LEVEL_ADVANCED = 2;

    /** The level names */
    private static final String[] names = { "Beginner", "Intermediate", "Advanced", };

    /** The User level */
    private static int level = 0;
}
