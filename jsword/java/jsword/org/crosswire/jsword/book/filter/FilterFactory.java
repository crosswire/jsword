
package org.crosswire.jsword.book.filter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.util.Project;

/**
 * A simple container for all the known filters.
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
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class FilterFactory
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
    private static Map filters = new HashMap();

    /**
     * The lookup table of filters
     */
    private static Filter deft = null;

    /**
     * Populate the lookup table of filters and the default from the properties
     * file.
     */
    static
    {
        Map map = Project.instance().getImplementorsMap(Filter.class);

        // the default value
        try
        {
            Class cdeft = (Class) map.remove("default");
            deft = (Filter) cdeft.newInstance();            
        }
        catch (Exception ex)
        {
            log.fatal("Failed to get default filter, will attempt to use first", ex);
        }

        // the lookup table
        for (Iterator it = map.keySet().iterator(); it.hasNext();)
        {
            try
            {
                String key = (String) it.next();
                Class clazz = (Class) map.get(key);
                Filter instance = (Filter) clazz.newInstance();
                addFilter(key, instance);
            }
            catch (Exception ex)
            {
                log.error("Failed to add filter", ex);
            }
        }
        
        // if the default didn't work then make a stab at an answer
        if (deft == null)
        {
            deft = (Filter) filters.values().iterator().next();
        }
    }

    /**
     * Find a filter given a lookup string. If lookup is null or the filter is
     * not found then the default filter will be used.
     */
    public static Filter getFilter(String lookup)
    {
        Filter reply = null;
        for (Iterator it = filters.keySet().iterator(); it.hasNext();)
        {
            String key = (String) it.next();
            if (key.equalsIgnoreCase(lookup))
            {
                reply = (Filter) filters.get(key);
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
