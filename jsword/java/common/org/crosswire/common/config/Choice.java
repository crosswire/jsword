
package org.crosswire.common.config;

import java.io.Serializable;

import org.jdom.Element;

/**
 * Choice is the fundamental building block of the config system.
 * 
 * Every Choice must be able to:<ul>
 * <li>get and set itself using a String</li>
 * <li>provide some simple help about itself</li>
 * <li>elect a user level for itself (Beginner, Intermediate, Advanced)</li>
 * <li>provide a GUI editor for itself</li>
 * </ul>
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
public interface Choice extends Serializable
{
    /**
     * String value to associate with the name (key)
     * @return value of this Choice
     */
    public void init(Element option) throws StartupException;

    /**
     * The class that this Choice works on. Used to decide how to display the
     * choice to the user.
     * @return The Class that this Choice works using.
     */
    public Class getConvertionClass();

    /**
     * String value to associate with the name (key)
     * @return value of this Choice
     */
    public String getString();

    /**
     * String value to associate with this Field. This method can
     * throw any Exception since almost anything could go wrong at this
     * point. The Config dialog ought to cope with any errors.
     * @param value The new value for this Choice
     */
    public void setString(String value) throws Exception;

    /**
     * Gets a brief description of what is going on
     * @return Some help text
     */
    public String getHelpText();

    /**
     * Is this Choice OK to write out to a file, or should we use settings
     * in this run of the program, but forget them for next time. A
     * typical use of this is for password configuration.
     * @return True if it is safe to store the value in a config file.
     */
    public boolean isSaveable();

    /**
     * Sometimes we need to ensure that we configure items in a certain
     * order, the config package moves the changes to the application
     * starting with the highest priority, moving to the lowest. The
     * normal priorities are 0-10 or the PRIORITY_* constants, the default
     * being PRIORITY_NORMAL
     * @return A priority level
     */
    public int getPriority();

    /**
     * Do we need to restart the program in order for this change to have
     * effect?
     * @return True if a restart is required
     */
    public boolean requiresRestart();

    /** The highest level priority generally for system level stuff */
    public static final int PRIORITY_SYSTEM = 10;

    /** The priority level for important but non system level stuff */
    public static final int PRIORITY_EXTENDER = 9;

    /** The priority level for important but non system level stuff */
    public static final int PRIORITY_HIGHEST = 8;

    /** The priority level for normal use */
    public static final int PRIORITY_NORMAL = 6;

    /** The priority level for creating items for later configuring */
    public static final int PRIORITY_CTOR = 4;

    /** The priority level for configuring previously created items */
    public static final int PRIORITY_ACCESSOR = 2;

    /** The lowest level priority generally for system level stuff */
    public static final int PRIORITY_LOWEST = 0;
}
