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
package org.crosswire.jsword.book.filter;

import java.util.HashMap;
import java.util.Map;

import org.crosswire.common.util.ClassUtil;
import org.crosswire.common.util.Logger;

/**
 * A simple container for all the known filters.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class FilterFactory
{
    /**
     * Prevent instansiation
     */
    private FilterFactory()
    {
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(FilterFactory.class);

    /**
     * The lookup table of filters
     */
    private static Map<String, Filter> filters = new HashMap<String, Filter>();

    /**
     * The lookup table of filters
     */
    private static Filter deft;

    /**
     * Populate the lookup table of filters and the default from the properties
     * file.
     */
    static
    {
        Map<String, Class> map = ClassUtil.getImplementorsMap(Filter.class);

        // the default value
        try
        {
            Class cdeft = map.remove("default"); //$NON-NLS-1$
            deft = (Filter) cdeft.newInstance();
        }
        catch (Exception ex)
        {
            log.fatal("Failed to get default filter, will attempt to use first", ex); //$NON-NLS-1$
        }

        // the lookup table
        for (Map.Entry<String, Class> entry : map.entrySet())
        {
            try
            {
                Class clazz = entry.getValue();
                Filter instance = (Filter) clazz.newInstance();
                addFilter(entry.getKey(), instance);
            }
            catch (Exception ex)
            {
                log.error("Failed to add filter", ex); //$NON-NLS-1$
            }
        }

        // if the default didn't work then make a stab at an answer
        if (deft == null)
        {
            deft = filters.values().iterator().next();
        }
    }

    /**
     * Find a filter given a lookup string. If lookup is null or the filter is
     * not found then the default filter will be used.
     */
    public static Filter getFilter(String lookup)
    {
        Filter reply = null;
        for (String key : filters.keySet())
        {
            if (key.equalsIgnoreCase(lookup))
            {
                reply = filters.get(key);
                break;
            }
        }

        if (reply == null)
        {
            reply = deft;
        }

        return reply;
    }

    /**
     * Find a filter given a lookup string
     */
    public static Filter getDefaultFilter()
    {
        return deft;
    }

    /**
     * Add to our list of known filters
     */
    public static void addFilter(String name, Filter instance)
    {
        filters.put(name, instance);
    }
}
