
package org.crosswire.common.config;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.jdom.Element;

/**
 * A helper for when we need to be a choice created dynamically.
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public abstract class ReflectedChoice implements Choice, Serializable
{
    /**
     * Setup the available options
     */
    public void init(Element option) throws StartupException
    {
        type = option.getAttributeValue("type");

        // The important 3 things saying what we update and how we describe ourselves
        Element introspector = option.getChild("introspect");
        if (introspector == null)
            throw new StartupException("config_missingele", new Object[] { "introspect" });

        String clazzname = introspector.getAttributeValue("class");
        if (clazzname == null)
            throw new StartupException("config_missingele", new Object[] { "class" });

        propertyname = introspector.getAttributeValue("property");
        if (propertyname == null)
            throw new StartupException("config_missingele", new Object[] { "property" });

        log.debug("Looking up "+clazzname+".set"+propertyname+"("+getConvertionClass().getName()+" arg0)");

        try
        {
            clazz = Class.forName(clazzname);
        }
        catch (ClassNotFoundException ex)
        {
            throw new StartupException("config_no_class", ex, new Object[] { clazzname });
        }

        try
        {
            setter = clazz.getMethod("set"+propertyname, new Class[] { getConvertionClass() });
        }
        catch (NoSuchMethodException ex)
        {
            throw new StartupException("Specified method not found "+clazz.getName()+".set"+propertyname+"("+getConvertionClass().getName()+" arg0)", ex);
        }

        try
        {
            try
            {
                getter = clazz.getMethod("get"+propertyname, new Class[0]);
            }
            catch (Exception ex)
            {
                getter = clazz.getMethod("is"+propertyname, new Class[0]);
            }
        }
        catch (NoSuchMethodException ex)
        {
            throw new StartupException("Specified method not found "+clazz.getName()+".get"+propertyname+"()", ex);
        }

        if (getter.getReturnType() != getConvertionClass())
        {
            log.debug("Not using "+propertyname+" from "+clazz.getName()+" because the return type of the getter is not "+getConvertionClass().getName());
            throw new StartupException("Mismatch of return types, found: "+getter.getReturnType()+" required:"+getConvertionClass());
        }

        // Help text
        Element childele = option.getChild("help");
        if (childele == null)
            helptext = "";
        helptext = childele.getTextTrim();

        // 2 optional config attrubites
        String priorityname = option.getAttributeValue("priority");
        if (priorityname == null)
            priority = ReflectedChoice.PRIORITY_NORMAL;
        else
            priority = Integer.parseInt(priorityname);
    }

    /**
     * The type by which UIs can pick an appropriate editor
     * @return String The type string as supplied in config.xml
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
     * Convert from a stored string to an object to use with relfection
     */
    public abstract Object convertToObject(String orig);

    /**
     * Get some help on this Field. In this case we are just providing
     * a default help text, that isn't much use.
     * @return The default help text
     */
    public String getHelpText()
    {
        return helptext;
    }

    /**
     * Get some help on this Field. In this case we are just providing
     * a default help text, that isn't much use.
     * @return The default help text
     */
    public void setHelpText(String helptext)
    {
        this.helptext = helptext;
    }

    /**
     * Is this Choice OK to write out to a file, or should we use settings
     * in this run of the program, but forget them for next time. A
     * typical use of this is for password configuration.
     * @return True if it is safe to store the value in a config file.
     */
    public boolean isSaveable()
    {
        return true;
    }

    /**
     * Sometimes we need to ensure that we configure items in a certain
     * order, the config package moves the changes to the application
     * starting with the highest priority, moving to the lowest
     * @return A priority level
     */
    public int getPriority()
    {
        return priority;
    }

    /**
     * Sometimes we need to ensure that we configure items in a certain
     * order, the config package moves the changes to the application
     * starting with the highest priority, moving to the lowest
     * @param priority A priority level
     */
    public void setPriority(int priority)
    {
        this.priority = priority;
    }

    /**
     * Do we need to restart the program in order for this change to have
     * effect?
     * @return True if a restart is required
     */
    public boolean requiresRestart()
    {
        return false;
    }

    /**
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
            log.error("Illegal access getting value from "+clazz.getName()+"."+getter.getName(), ex);
            return "";
        }
        catch (InvocationTargetException ex)
        {
            log.error("Failed to get value from "+clazz.getName()+"."+getter.getName(), ex);
            return "";
        }
    }

    /**
     * @see org.crosswire.common.config.Choice#setString(String)
     */
    public void setString(String value) throws Exception
    {
        try
        {
            Object object = convertToObject(value);
            setter.invoke(null, new Object[] { object });
        }
        catch (InvocationTargetException ex)
        {
            Throwable orig = ex.getTargetException();
            if (orig instanceof Exception)
                throw (Exception) orig;

            // So we can't re-throw the original exception because it wasn't an
            // Exception so we will have to re-throw the InvocationTargetException
            throw ex;
        }
    }

    /** The highest level priority generally for system level stuff */
    public static final int PRIORITY_SYSTEM = 10;

    /** The priority level for important but non system level stuff */
    public static final int PRIORITY_EXTENDER = 9;

    /** The priority level for important but non system level stuff */
    public static final int PRIORITY_HIGHEST = 8;

    /** The priority level for normal use */
    public static final int PRIORITY_NORMAL = 6;

    /** The priority level for creating items for later configuring */
    public static final int PRIORITY_CTOR = 4;

    /** The priority level for configuring previously created items */
    public static final int PRIORITY_ACCESSOR = 2;

    /** The lowest level priority generally for system level stuff */
    public static final int PRIORITY_LOWEST = 0;

    /**
     * Th type that we reflect to
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
    private String helptext = "No help available";

    /**
     * The priority of this config level
     */
    private int priority = PRIORITY_NORMAL;

    /**
     * The log stream
     */
    protected static Logger log = Logger.getLogger(ReflectedChoice.class);
}
