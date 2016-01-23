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
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
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
 * A simple container for all the known SourceFilters.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public final class SourceFilterFactory {
    /**
     * Prevent instantiation
     */
    private SourceFilterFactory() {
    }

    /**
     * Find a filter given a lookup string. If lookup is null or the filter is
     * not found then the default filter will be used.
     * 
     * @param lookup the lookup string for the filter
     * @return the matching filter
     */
    public static SourceFilter getFilter(String lookup) {
        SourceFilter reply = filters.get(lookup.toLowerCase(Locale.ENGLISH));

        if (reply == null) {
            reply = deft;
        }

        return reply.clone();
    }

    /**
     * Find a filter given a lookup string
     * 
     * @return the default filter
     */
    public static SourceFilter getDefaultFilter() {
        return deft.clone();
    }

    /**
     * Add to our list of known filters
     * 
     * @param name 
     * @param instance 
     */
    public static void addFilter(String name, SourceFilter instance) {
        filters.put(name.toLowerCase(Locale.ENGLISH), instance);
    }

    /**
     * The lookup table of filters
     */
    private static Map<String, SourceFilter> filters = new HashMap<String, SourceFilter>();

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(SourceFilterFactory.class);

    /**
     * The lookup table of filters
     */
    private static volatile SourceFilter deft;

    /**
     * Populate the lookup table of filters and the default from the properties
     * file.
     */
    static {
        Map<String, Class<SourceFilter>> map = PluginUtil.getImplementorsMap(SourceFilter.class);

        // the default value
        try {
            Class<SourceFilter> cdeft = map.remove("default");
            deft = cdeft.newInstance();
        } catch (InstantiationException e) {
            log.error("Failed to get default filter, will attempt to use first", e);
        } catch (IllegalAccessException e) {
            log.error("Failed to get default filter, will attempt to use first", e);
        }

        // the lookup table
        SourceFilter instance = null;
        for (Map.Entry<String, Class<SourceFilter>> entry : map.entrySet()) {
            try {
                Class<SourceFilter> clazz = entry.getValue();
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
