
package org.crosswire.common.config;

import org.jdom.Element;

/**
 * URGENT(joe): get rid of this class
 * A UserLevel keeps a track of how advanced the user is.
 * It may not be a graphical component, but many graphical components depend on
 * it, and it doesn't seem to be a 'util'.
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
 * @version $Id: UserLevel.java,v 1.4 2002/10/08 21:36:09 joe Exp $
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
     * Get a Username by string. This method comes down to: if the string
     * starts with [i|I] return UserLevel.INTERMEDIATE, if it starts with
     * [a|A] return UserLevel.ADVANCED, and otherwise UserLevel.BEGINNER
     * @param levelname The string to check
     * @return One of the 3 UserLevels.
     */
    public static UserLevel forName(String levelname)
    {
        if (levelname == null)
            return BEGINNER;

        levelname = levelname.toLowerCase();

        if (levelname.startsWith("i"))
            return INTERMEDIATE;

        if (levelname.startsWith("a"))
            return ADVANCED;

        return BEGINNER;
    }

    /**
     * Method an array containing all the known UserLevels.
     * @return Object
     */
    public static UserLevel[] getUserLevels()
    {
        return null;
    }

    /**
     * Is this level available given the global UserLevel
     */
    public boolean isAvailable()
    {
        return this.ordinal <= global.ordinal;
    }
    
    /** User level - Beginner */
    public static final UserLevel BEGINNER = new UserLevel(0, "Beginner");

    /** User level - Intermediate */
    public static final UserLevel INTERMEDIATE = new UserLevel(1, "Intermediate");

    /** User level - Advanced */
    public static final UserLevel ADVANCED = new UserLevel(2, "Advanced");

    /** The level name */
    private String name;
    
    /** The ordinal of this UserLevel */
    private int ordinal;

    /** The current User level */
    private static UserLevel global = BEGINNER;

    /**
     * A Custom Choice
     */
    public static class UserLevelChoice extends AbstractChoice implements MultipleChoice
    {
        /**
         * Simple ctor so there is a default
         */
        public void init(Element option) throws StartupException
        {
            // Help text
            Element childele = option.getChild("help");
            if (childele == null)
                helptext = "";
            helptext = childele.getTextTrim();
        }

        /**
         * The available alternative values to be presented as options to the user
         * where the user interface allows presentation of alternatives.
         * @return A string array of alternatives.
         */
        public String[] getOptions()
        {
            return levels;
        }

        /**
         * Accessor for this UserLevels type.
         */
        public Class getConvertionClass()
        {
            return UserLevel.class;
        }

        /**
         * Read the save setting from UserLevel
         */
        public void setString(String value)
        {
            UserLevel.setGlobalUserLevel(UserLevel.forName(value));
        }
    
        /**
         * Save the save setting to UserLevel
         */
        public String getString()
        {
            return UserLevel.getGlobalUserLevel().getName();
        }

        /**
         * Get some help on this Choice.
         */
        public String getHelpText()
        {
            return helptext;
        }

        private String helptext;

        /**
         * An array containing all the UserLevels
         */
        private static final String[] levels = new String[]
        {
            BEGINNER.getName(),
            INTERMEDIATE.getName(),
            ADVANCED.getName(),
        };
    }
}
