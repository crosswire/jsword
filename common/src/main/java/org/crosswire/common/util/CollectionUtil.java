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
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Some utils to help work with Collections.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class CollectionUtil {
    /**
     * Dont do this
     */
    private CollectionUtil() {
    }

    /**
     * Create a List from an Iterable.
     * 
     * @param it
     *            The source of data for the list
     * @return List
     */
    public static List createList(Iterable it) {
        List reply = new ArrayList();
        Iterator iter = it.iterator();
        while (iter.hasNext()) {
            reply.add(iter.next());
        }

        return reply;
    }

    /**
     * Create a Set from an Iterable.
     * 
     * @param it
     *            The source of data for the list
     * @return the created set
     */
    public static Set createSet(Iterable it) {
        Set reply = new HashSet();
        Iterator iter = it.iterator();
        while (iter.hasNext()) {
            reply.add(iter.next());
        }

        return reply;
    }

    /**
     * Convert a <code>Properties</code> into a <code>Map</code>.
     * 
     * @param prop
     *            The Properties to convert
     * @return The map
     */
    public static Map properties2Map(Properties prop) {
        Map propMap = new HashMap();
        Iterator iter = prop.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            propMap.put(entry.getKey(), entry.getValue());
        }
        return propMap;
    }

    /**
     * Convert a <code>Properties</code> located at <code>propURI</code> into a
     * <code>Map</code>.
     * 
     * @param propUri
     *            The URI of the Properties to convert
     * @return The map
     */
    public static Map properties2Map(URI propUri) throws IOException {
        return properties2Map(NetUtil.loadProperties(propUri));
    }

}
