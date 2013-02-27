/**
 * Distribution License:
 * This is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published
 * by the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/llgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005-2013
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.common.config;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.crosswire.common.util.ClassUtil;
import org.crosswire.common.util.PluginUtil;
import org.jdom2.Element;

/**
 * Factory for the well known Choices.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
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
     * @return One of the ChoiceTypes.
     */
    public static Choice getChoice(Element option, ResourceBundle configResources) throws ClassNotFoundException, IllegalAccessException,
            InstantiationException, StartupException
    {
        Class<Choice> clazz = null;

        String type = option.getAttributeValue("type");
        if ("custom".equals(type)) {
            String clazzstr = option.getAttributeValue("class");
            clazz = (Class<Choice>) ClassUtil.forName(clazzstr);
        } else {
            clazz = map.get(type);
        }

        Choice choice = clazz.newInstance();
        choice.init(option, configResources);
        return choice;
    }

    /**
     * Method getDataMap.
     */
    public static Map<String, Object> getDataMap() {
        return datamap;
    }

    /**
     * Storage of various registered objects
     */
    private static Map<String, Object> datamap = new HashMap<String, Object>();

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
