
package org.crosswire.common.config.choices;

import org.crosswire.common.config.Choice;
import org.crosswire.common.util.UserLevel;

/**
 * An AbstractChoice is one that registers itself with
 * AbstractChoice when it starts up, so that we don't need to pass
 * parameters around the place the whole time.
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
public abstract class AbstractChoice implements Choice
{
    /**
     * Gets a default user level (beginner to advanced)
     * @return The user level
     */
    public UserLevel getUserLevel()
    {
        return UserLevel.BEGINNER;
    }

    /**
     * Get some help on this Field. In this case we are just providing
     * a default help text, that isn't much use.
     * @return The default help text
     */
    public String getHelpText()
    {
        return "";
    }

    /**
     * This method is used to configure a good way of editing this
     * component. It returns a MIME style string, which a config
     * ui can use to select a suitable ui tool.
     * @return The editor style to use to edit this Choice
     */
    public String getType()
    {
        return "text";
    }

    /**
     * This method is used to configure a the type selected above.
     * The object returned will depend on the type of editor selected.
     * For example an editor of type "options" may need a String array.
     * @return a configuration parameter for the type
     */
    public Object getTypeOptions()
    {
        return null;
    }

    /**
     * Is this Choice OK to write out to a file, or should we use settings
     * in this run of the program, but forget them for next time. A
     * typical use of this is for password configuration.
     * @return True if it is safe to store the value in a config file.
     */
    public boolean isSaveable()
    {
        return true;
    }

    /**
     * Sometimes we need to ensure that we configure items in a certain
     * order, the config package moves the changes to the application
     * starting with the highest priority, moving to the lowest
     * @return A priority level
     */
    public int priority()
    {
        return PRIORITY_NORMAL;
    }

    /**
     * Do we need to restart the program in order for this change to have
     * effect?
     * @return True if a restart is required
     */
    public boolean requiresRestart()
    {
        return false;
    }
}

