/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.common.util;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Some utils to help work with Collections.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class CollectionUtil
{
    /**
     * Dont do this
     */
    private CollectionUtil()
    {
    }

    /**
     * Create a List from an Iterable.
     * @param it The source of data for the list
     * @return List
     */
    public static <T> List<T> createList(Iterable<T> it)
    {
        List<T> reply = new ArrayList<T>();
        for (T t : it)
        {
            reply.add(t);
        }

        return reply;
    }

    /**
     * Create a Set from an Iterable.
     * @param it The source of data for the list
     * @return the created set
     */
    public static <T> Set<T> createSet(Iterable<T> it)
    {
        Set<T> reply = new HashSet<T>();
        for (T t : it)
        {
            reply.add(t);
        }

        return reply;
    }

    public static Map<String, String> properties2Map(Properties prop)
    {
        Map<String, String> propMap = new HashMap<String, String>();
        for (Map.Entry entry : prop.entrySet())
        {
            propMap.put((String) entry.getKey(), (String) entry.getValue());
        }
        return propMap;
    }

    public static Map<String, String> properties2Map(URL propUrl) throws IOException
    {
        Properties prop = new Properties();
        prop.load(propUrl.openStream());
        return properties2Map(prop);
    }
}
