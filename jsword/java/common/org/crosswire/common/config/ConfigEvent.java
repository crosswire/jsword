
package org.crosswire.common.config;

import java.util.EventObject;

/**
 * An event indicating that an exception has happened.
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
public class ConfigEvent extends EventObject
{
    /**
     * Constructs an ConfigEvent object.
     * @param source The event originator, or log stream
     * @param message The string to be logged
     */
    public ConfigEvent(Object source, String key, Choice model)
    {
        super(source);

        this.key = key;
        this.model = model;
    }

    /**
     * Returns the key.
     * @return the key
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Returns the choice.
     * @return the choice
     */
    public Choice getChoice()
    {
        return model;
    }

    /**
     * Returns the choice.
     * @return the choice
     */
    public Choice getPath()
    {
        return model;
    }

    /** The name of the choice */
    private String key;

    /** The Choice */
    private Choice model;
}
