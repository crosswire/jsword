
package org.crosswire.common.config;

import java.util.HashMap;
import java.util.Map;

import org.jdom.Element;

/**
 * Factory for the well known Choices.
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
public abstract class ChoiceFactory
{
    /**
     * Get a ChoiceFactory by element.
     * @param option The element to check
     * @return One of the ChoiceTypes.
     */
    public static Choice getChoice(Element option) throws ClassNotFoundException, IllegalAccessException, InstantiationException, StartupException
    {
        Class clazz = null;

        String type = option.getAttributeValue("type");
        if (type.equals("custom"))
        {
            String clazzstr = option.getAttributeValue("class");
            clazz = Class.forName(clazzstr);
        }
        else
        {
            clazz = (Class) map.get(type);
        }

        Choice choice = (Choice) clazz.newInstance();
        choice.init(option);
        return choice;
    }

    /**
     * Method getDataMap.
     */
    public static Map getDataMap()
    {
        return datamap;
    }

    /**
     * Storage of various registered objects
     */    
    private static Map datamap = new HashMap();

    /**
     * Store of the known ChoiceTypes
     */
    private static Map map = new HashMap();

    /**
     * Setup the map of Choices
     * @see org.crosswire.common.config.swing.FieldMap#hash
     */
    static
    {
        map.put("string", StringChoice.class);
        map.put("boolean", BooleanChoice.class);
        map.put("int-options", IntOptionsChoice.class);
        map.put("string-options", StringOptionsChoice.class);
        map.put("string-array", StringArrayChoice.class);
        map.put("file", FileChoice.class);
        map.put("path", PathChoice.class);
        map.put("directory", DirectoryChoice.class);
        map.put("number", NumberChoice.class);
        map.put("font", FontChoice.class);
        map.put("class", ClassChoice.class);
    }
}
