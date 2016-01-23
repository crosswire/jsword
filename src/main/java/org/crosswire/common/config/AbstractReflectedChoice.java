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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.crosswire.common.util.ClassUtil;
import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.JSOtherMsg;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A helper for when we need to be a choice created dynamically.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
 */
public abstract class AbstractReflectedChoice implements Choice {
    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.config.Choice#init(org.jdom2.Element)
     */
    public void init(Element option, ResourceBundle configResources) throws StartupException {
        assert configResources != null;

        key = option.getAttributeValue("key");

        // Hidden is an optional field so it is ok for the resource to be
        // missing.
        try {
            String hiddenState = configResources.getString(key + ".hidden");
            hidden = Boolean.valueOf(hiddenState).booleanValue();
        } catch (MissingResourceException e) {
            hidden = false;
        }

        // Ignore is an optional field so it is ok for the resource to be
        // missing.
        try {
            String ignoreState = configResources.getString(key + ".ignore");
            ignored = Boolean.valueOf(ignoreState).booleanValue();
            if (ignored) {
                hidden = true;
                return;
            }
        } catch (MissingResourceException e) {
            ignored = false;
        }

        String helpText = configResources.getString(key + ".help");
        assert helpText != null;
        setHelpText(helpText);

        // OPTIMIZE(dms): This is poorly done (by me!)
        String[] pathParts = StringUtil.split(key, '.');
        StringBuilder parentKey = new StringBuilder();
        StringBuilder path = new StringBuilder();
        for (int i = 0; i < pathParts.length; i++) {
            if (i > 0) {
                parentKey.append('.');
                path.append('.');
            }
            parentKey.append(pathParts[i]);
            String parent = configResources.getString(parentKey + ".name");
            assert parent != null;
            path.append(parent);
        }
        setFullPath(path.toString());

        external = Boolean.valueOf(option.getAttributeValue("external")).booleanValue();

        restart = Boolean.valueOf(option.getAttributeValue("restart")).booleanValue();

        type = option.getAttributeValue("type");

        // The important 3 things saying what we update and how we describe
        // ourselves
        Element introspector = option.getChild("introspect");
        if (introspector == null) {
            throw new StartupException(JSOtherMsg.lookupText("Missing {0} element in config.xml", "introspect"));
        }

        String clazzname = introspector.getAttributeValue("class");
        if (clazzname == null) {
            throw new StartupException(JSOtherMsg.lookupText("Missing {0} element in config.xml", "class"));
        }

        propertyname = introspector.getAttributeValue("property");
        if (propertyname == null) {
            throw new StartupException(JSOtherMsg.lookupText("Missing {0} element in config.xml", "property"));
        }

        // log.debug("Looking up {}.set{}({} arg0)", clazzname, propertyname, getConvertionClass().getName());

        try {
            clazz = ClassUtil.forName(clazzname);
        } catch (ClassNotFoundException ex) {
            throw new StartupException(JSOtherMsg.lookupText("Specified class not found: {0}", clazzname), ex);
        }

        try {
            setter = clazz.getMethod("set" + propertyname, getConversionClass());
        } catch (NoSuchMethodException ex) {
            throw new StartupException(JSOtherMsg.lookupText("Specified method not found {0}.set{1}({2} arg0)",
                    clazz.getName(), propertyname, getConversionClass().getName()), ex
            );
        }

        try {
            try {
                getter = clazz.getMethod("is" + propertyname, new Class[0]);
            } catch (NoSuchMethodException e) {
                getter = clazz.getMethod("get" + propertyname, new Class[0]);
            }
        } catch (NoSuchMethodException ex) {
            throw new StartupException(JSOtherMsg.lookupText("Specified method not found {0}.get{1}()", clazz.getName(), propertyname), ex);
        }

        if (getter.getReturnType() != getConversionClass()) {
            log.debug("Not using {} from {} because the return type of the getter is not {}", propertyname, clazz.getName(), getConversionClass().getName());
            throw new StartupException(JSOtherMsg.lookupText("Mismatch of return types, found: {0} required: {1}", getter.getReturnType(), getConversionClass()));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.config.Choice#getKey()
     */
    public String getKey() {
        return key;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.config.Choice#getType()
     */
    public String getType() {
        return type;
    }

    /**
     * Convert from a reflection return value to a String for storage
     * 
     * @param orig the object to be converted to a string
     * @return the marshaled representation of the object
     */
    public abstract String convertToString(Object orig);

    /**
     * Convert from a stored string to an object to use with reflection
     * @param orig the marshaled representation of the object
     * @return the reconstituted object
     */
    public abstract Object convertToObject(String orig);

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.config.Choice#getFullPath()
     */
    public String getFullPath() {
        return fullPath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.config.Choice#setFullPath(java.lang.String)
     */
    public void setFullPath(String newFullPath) {
        fullPath = newFullPath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.config.Choice#getHelpText()
     */
    public String getHelpText() {
        return helptext;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.config.Choice#setHelpText(java.lang.String)
     */
    public void setHelpText(String helptext) {
        this.helptext = helptext;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.config.Choice#isSaveable()
     */
    public boolean isSaveable() {
        return !external;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.config.Choice#isHidden()
     */
    public boolean isHidden() {
        return hidden;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.config.Choice#isIgnored()
     */
    public boolean isIgnored() {
        return ignored;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.config.Choice#requiresRestart()
     */
    public boolean requiresRestart() {
        return restart;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.config.Choice#getString()
     */
    public String getString() {
        try {
            Object retval = getter.invoke(null, new Object[0]);
            return convertToString(retval);
        } catch (IllegalAccessException ex) {
            log.error("Illegal access getting value from {}.{}", clazz.getName(), getter.getName(), ex);
            return "";
        } catch (InvocationTargetException ex) {
            log.error("Failed to get value from {}.{}", clazz.getName(), getter.getName(), ex);
            return "";
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.config.Choice#setString(java.lang.String)
     */
    public void setString(String value) throws ConfigException {
        Exception ex = null;
        try {
            Object object = convertToObject(value);
            if (object != null) {
                setter.invoke(null, object);
            }
        } catch (InvocationTargetException e) {
            ex = e;
        } catch (IllegalArgumentException e) {
            ex = e;
        } catch (IllegalAccessException e) {
            ex = e;
        } catch (NullPointerException e) {
            ex = e;
        }

        if (ex != null) {
            log.info("Exception while attempting to execute: {}", setter.toString());

            // So we can't re-throw the original exception because it wasn't an
            // Exception so we will have to re-throw the
            // InvocationTargetException
            throw new ConfigException(JSOtherMsg.lookupText("Failed to set option: {0}", setter), ex);
        }
    }

    /**
     * The key of the option.
     */
    private String key;

    /**
     * The type that we reflect to
     */
    private Class<? extends Object> clazz;

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
     * Whether this choice is managed externally, via setXXX and getXXX.
     */
    private boolean external;

    /**
     * Whether this choice is requires a restart to be seen.
     */
    private boolean restart;

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(AbstractReflectedChoice.class);
}
