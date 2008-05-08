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
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.common.config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.crosswire.common.util.ClassUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.StringUtil;
import org.jdom.Element;

/**
 * A helper for when we need to be a choice created dynamically.
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public abstract class AbstractReflectedChoice implements Choice
{
    /* (non-Javadoc)
     * @see org.crosswire.common.config.Choice#init(org.jdom.Element)
     */
    public void init(Element option, ResourceBundle configResources) throws StartupException
    {
        assert configResources != null;

        key = option.getAttributeValue("key"); //$NON-NLS-1$

        // Hidden is an optional field so it is ok for the resource to be missing.
        try
        {
            String hiddenState = configResources.getString(key + ".hidden"); //$NON-NLS-1$
            hidden = Boolean.valueOf(hiddenState).booleanValue();
        }
        catch (MissingResourceException e)
        {
            hidden = false;
        }

        // Ignore is an optional field so it is ok for the resource to be missing.
        try
        {
            String ignoreState = configResources.getString(key + ".ignore"); //$NON-NLS-1$
            ignored = Boolean.valueOf(ignoreState).booleanValue();
            if (ignored)
            {
                hidden = true;
                return;
            }
        }
        catch (MissingResourceException e)
        {
            ignored = false;
        }

        String helpText = configResources.getString(key + ".help"); //$NON-NLS-1$
        assert helpText != null;
        setHelpText(helpText);

        // OPTIMIZE(dms): This is poorly done (by me!)
        String[] pathParts = StringUtil.split(key, '.');
        StringBuffer parentKey = new StringBuffer();
        StringBuffer path = new StringBuffer();
        for (int i = 0; i < pathParts.length; i++)
        {
            if (i > 0)
            {
                parentKey.append('.');
                path.append('.');
            }
            parentKey.append(pathParts[i]);
            String parent = configResources.getString(parentKey + ".name"); //$NON-NLS-1$
            assert parent != null;
            path.append(parent);
        }
        setFullPath(path.toString());

        type = option.getAttributeValue("type"); //$NON-NLS-1$

        // The important 3 things saying what we update and how we describe ourselves
        Element introspector = option.getChild("introspect"); //$NON-NLS-1$
        if (introspector == null)
        {
            throw new StartupException(Msg.CONFIG_MISSINGELE, new Object[] { "introspect" }); //$NON-NLS-1$
        }

        String clazzname = introspector.getAttributeValue("class"); //$NON-NLS-1$
        if (clazzname == null)
        {
            throw new StartupException(Msg.CONFIG_MISSINGELE, new Object[] { "class" }); //$NON-NLS-1$
        }

        propertyname = introspector.getAttributeValue("property"); //$NON-NLS-1$
        if (propertyname == null)
        {
            throw new StartupException(Msg.CONFIG_MISSINGELE, new Object[] { "property" }); //$NON-NLS-1$
        }

        //log.debug("Looking up " + clazzname + ".set" + propertyname + "(" + getConvertionClass().getName() + " arg0)");

        try
        {
            clazz = ClassUtil.forName(clazzname);
        }
        catch (ClassNotFoundException ex)
        {
            throw new StartupException(Msg.CONFIG_NOCLASS, ex, new Object[] { clazzname });
        }

        try
        {
            setter = clazz.getMethod("set" + propertyname, new Class[] { getConversionClass() }); //$NON-NLS-1$
        }
        catch (NoSuchMethodException ex)
        {
            throw new StartupException(Msg.CONFIG_NOSETTER, ex, new Object[] { clazz.getName(), propertyname, getConversionClass().getName() });
        }

        try
        {
            try
            {
                getter = clazz.getMethod("is" + propertyname, new Class[0]); //$NON-NLS-1$
            }
            catch (NoSuchMethodException e)
            {
                getter = clazz.getMethod("get" + propertyname, new Class[0]); //$NON-NLS-1$
            }
        }
        catch (NoSuchMethodException ex)
        {
            throw new StartupException(Msg.CONFIG_NOGETTER, ex, new Object[] { clazz.getName(), propertyname });
        }

        if (getter.getReturnType() != getConversionClass())
        {
            log.debug("Not using " + propertyname + " from " + clazz.getName() + " because the return type of the getter is not " + getConversionClass().getName()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            throw new StartupException(Msg.CONFIG_NORETURN, new Object[] { getter.getReturnType(), getConversionClass() });
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.Choice#getKey()
     */
    public String getKey()
    {
        return key;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.Choice#getType()
     */
    public String getType()
    {
        return type;
    }

    /**
     * Convert from a reflection return value to a String for storage
     */
    public abstract String convertToString(Object orig);

    /**
     * Convert from a stored string to an object to use with reflection
     */
    public abstract Object convertToObject(String orig);

    /* (non-Javadoc)
     * @see org.crosswire.common.config.Choice#getFullPath()
     */
    public String getFullPath()
    {
        return fullPath;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.Choice#setFullPath(java.lang.String)
     */
    public void setFullPath(String newFullPath)
    {
        fullPath = newFullPath;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.Choice#getHelpText()
     */
    public String getHelpText()
    {
        return helptext;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.Choice#setHelpText(java.lang.String)
     */
    public void setHelpText(String helptext)
    {
        this.helptext = helptext;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.Choice#isSaveable()
     */
    public boolean isSaveable()
    {
        return true;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.Choice#isHidden()
     */
    public boolean isHidden()
    {
        return hidden;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.Choice#isIgnored()
     */
    public boolean isIgnored()
    {
        return ignored;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.Choice#requiresRestart()
     */
    public boolean requiresRestart()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.Choice#getString()
     */
    public String getString()
    {
        try
        {
            Object retval = getter.invoke(null, new Object[0]);
            return convertToString(retval);
        }
        catch (IllegalAccessException ex)
        {
            log.error("Illegal access getting value from " + clazz.getName() + "." + getter.getName(), ex); //$NON-NLS-1$ //$NON-NLS-2$
            return ""; //$NON-NLS-1$
        }
        catch (InvocationTargetException ex)
        {
            log.error("Failed to get value from " + clazz.getName() + "." + getter.getName(), ex); //$NON-NLS-1$ //$NON-NLS-2$
            return ""; //$NON-NLS-1$
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.Choice#setString(java.lang.String)
     */
    public void setString(String value) throws ConfigException
    {
        Exception ex = null;
        try
        {
            Object object = convertToObject(value);
            if (object != null)
            {
                setter.invoke(null, new Object[] { object });
            }
        }
        catch (InvocationTargetException e)
        {
            ex = e;
        }
        catch (IllegalArgumentException e)
        {
            ex = e;
        }
        catch (IllegalAccessException e)
        {
            ex = e;
        }
        catch (NullPointerException e)
        {
            ex = e;
        }

        if (ex != null)
        {
            log.info("Exception while attempting to execute: " + setter.toString()); //$NON-NLS-1$


            // So we can't re-throw the original exception because it wasn't an
            // Exception so we will have to re-throw the InvocationTargetException
            throw new ConfigException(Msg.CONFIG_SETFAIL, ex, new Object[] { setter });
        }
    }

    /**
     * The key of the option.
     */
    private String key;

    /**
     * The type that we reflect to
     */
    private Class clazz;

    /**
     * The property that we call on the reflecting class
     */
    private String propertyname;

    /**
     * The type (as specified in config.xml)
     */
    private String type;

    /**
     * The method to call to get the value
     */
    private Method getter;

    /**
     * The method to call to set the value
     */
    private Method setter;

    /**
     * The help text (tooltip) for this item
     */
    private String helptext;

    /**
     * The full path of this item
     */
    private String fullPath;

    /**
     * Whether this choice should be visible or hidden
     */
    private boolean hidden;

    /**
     * Whether this choice should be ignored altogether.
     */
    private boolean ignored;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(AbstractReflectedChoice.class);
}
