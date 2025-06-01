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
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.common.config;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.crosswire.common.util.ClassUtil;
import org.crosswire.common.util.PluginUtil;
import org.crosswire.common.util.ReflectionUtil;
import org.jdom2.Element;

/**
 * Factory for the well known Choices.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public final class ChoiceFactory {
    /**
     * Prevent instantiation
     */
    private ChoiceFactory() {
    }

    /**
     * Get a ChoiceFactory by element.
     * 
     * @param option
     *            The element to check
     * @param configResources the resource bundle holding the option
     * @return One of the ChoiceTypes.
     * @throws StartupException if startup is not possible
     * @throws ReflectiveOperationException 
     *               if this {@code data} represents an abstract class,
     *               an interface, an array class, a primitive type, or void;
     *               or if the class has no reachable nullary constructor;
     *               or if the instantiation fails for some other reason.
     */
    public static Choice getChoice(Element option, ResourceBundle configResources) throws StartupException, ReflectiveOperationException
    {
        Class<Choice> clazz = null;

        String type = option.getAttributeValue("type");
        if ("custom".equals(type)) {
            String clazzstr = option.getAttributeValue("class");
            clazz = (Class<Choice>) ClassUtil.forName(clazzstr);
        } else {
            clazz = map.get(type);
        }

        Choice choice = ReflectionUtil.construct(clazz);
        choice.init(option, configResources);
        return choice;
    }

    /**
     * Method getDataMap.
     * 
     * @return the map data
     */
    public static Map<String, Object> getDataMap() {
        return datamap;
    }

    /**
     * Storage of various registered objects
     */
    private static Map<String, Object> datamap = new HashMap<>();

    /**
     * Store of the known ChoiceTypes
     */
    private static Map<String, Class<Choice>> map;

    /**
     * Setup the map of Choices
     */
    static {
        map = PluginUtil.getImplementorsMap(Choice.class);
    }
}
