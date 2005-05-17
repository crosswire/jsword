/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
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

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * A base class for implementing type safe internationalization (i18n) that is
 * easy for most cases. See {@link org.crosswire.common.util.Msg} for an
 * example of how to inherit from here.
 *
 * <p>Some Regex/Vi macros to convert from a half way house i18n scheme where
 * the strings are in Msg classes but not properties files:
 * The following makes the lookup string simple
 *   :%s/Msg \([^ ]*\) = new Msg(".*")/Msg \1 = new Msg("\1")/
 * These turn a lookup string into a properties file
 *   :%s/    static final Msg //
 *   :%s/ = new Msg("/: /
 *   :%s/");\/\/\$NON-NLS-1\$$/
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 * @see org.crosswire.common.util.Msg
 */
public class MsgBase
{
    /**
     * Create a MsgBase object
     */
    protected MsgBase(String name)
    {
        this.name = name;
        loadResources();
    }

    /* (non-Javadoc)
     * @see org.apache.commons.lang.enum.Enum#toString()
     */
    public String toString()
    {
        try
        {
            if (resources != null)
            {
                return resources.getString(name);
            }
        }
        catch (MissingResourceException ex)
        {
            log.error("Missing resource: Locale=" + Locale.getDefault().toString() + " name=" + name + " package=" + getClass().getName()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        return name;
    }

    /**
     * Formats the message with the given parameter.
     */
    public String toString(Object param)
    {
        return MessageFormat.format(toString(), new Object[] { param });
    }

    /**
     * Formats the message with the given parameters.
     */
    public String toString(Object[] params)
    {
        return MessageFormat.format(toString(), params);
    }

    /**
     * Initialise any resource bundles
     */
    protected final void loadResources()
    {
        Class implementingClass = getClass();
        String className = implementingClass.getName();

        // Class lock is needed around static resourceMap
        synchronized (MsgBase.class)
        {
            // see if it is in the cache
            resources = (ResourceBundle) resourceMap.get(className);

            // if not then create it and put it into the cache
            if (resources == null)
            {
                Locale defaultLocale = Locale.getDefault();
                try
                {
                    resources = ResourceBundle.getBundle(className, defaultLocale, new CWClassLoader(implementingClass));
                    resourceMap.put(className, resources);
                }
                catch (MissingResourceException ex)
                {
                    log.warn("Assuming key is the default message " + className + ": " + name); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        }
    }

    private String name;

    /**
     * resource map maintains a mapping of class names to resources found by that name.
     */
    private static Map resourceMap = new HashMap();

    /**
     * If there is any internationalization to be done, it is thru this
     */
    private ResourceBundle resources;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(MsgBase.class);
}
