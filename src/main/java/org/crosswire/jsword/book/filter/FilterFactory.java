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
 * Copyright: 2005-2013
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.book.filter;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.crosswire.common.util.PluginUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple container for all the known filters.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class FilterFactory {
    /**
     * Prevent instantiation
     */
    private FilterFactory() {
    }

    /**
     * Find a filter given a lookup string. If lookup is null or the filter is
     * not found then the default filter will be used.
     */
    public static Filter getFilter(String lookup) {
        Filter reply = filters.get(lookup.toLowerCase(Locale.ENGLISH));

        if (reply == null) {
            reply = deft;
        }

        return reply.clone();
    }

    /**
     * Find a filter given a lookup string
     */
    public static Filter getDefaultFilter() {
        return deft.clone();
    }

    /**
     * Add to our list of known filters
     */
    public static void addFilter(String name, Filter instance) {
        filters.put(name.toLowerCase(Locale.ENGLISH), instance);
    }

    /**
     * The lookup table of filters
     */
    private static Map<String, Filter> filters = new HashMap<String, Filter>();

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(FilterFactory.class);

    /**
     * The lookup table of filters
     */
    private static volatile Filter deft;

    /**
     * Populate the lookup table of filters and the default from the properties
     * file.
     */
    static {
        Map<String, Class<Filter>> map = PluginUtil.getImplementorsMap(Filter.class);

        // the default value
        try {
            Class<Filter> cdeft = map.remove("default");
            deft = cdeft.newInstance();
        } catch (InstantiationException e) {
            log.error("Failed to get default filter, will attempt to use first", e);
        } catch (IllegalAccessException e) {
            log.error("Failed to get default filter, will attempt to use first", e);
        }

        // the lookup table
        Filter instance = null;
        for (Map.Entry<String, Class<Filter>> entry : map.entrySet()) {
            try {
                Class<Filter> clazz = entry.getValue();
                instance = clazz.newInstance();
                addFilter(entry.getKey(), instance);
            } catch (InstantiationException ex) {
                log.error("Failed to add filter", ex);
            } catch (IllegalAccessException ex) {
                log.error("Failed to add filter", ex);
            }
        }

        // if the default didn't work then make a stab at an answer
        if (deft == null) {
            deft = instance;
        }
    }
}
