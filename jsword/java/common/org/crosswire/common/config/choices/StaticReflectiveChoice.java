
package org.crosswire.common.config.choices;

import java.beans.IntrospectionException;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.crosswire.common.config.Choice;
import org.crosswire.common.util.Convert;
import org.crosswire.common.util.UserLevel;
import org.jdom.Element;

/**
 * A choice that will work using reflection on a wide variety of
 * configurable items.
 * The action paramter with which this class is constructed must be of the form:
 * <code>full.package.name.ClassName.StaticProperty</code>
 * Where StaticProperty is a name to which we can prepend get OR set and then
 * use introspection to find a methods where the setter takes a single parameter
 * and the getter takes no paramters, but returns something of the same type.
 * If there are several parameter types that match the above definitions then
 * we take String in preference to all others. Other parameter types match in
 * an undefined manner. The types that are currently supported include:
 * <li>String
 * <li>String[]
 * <li>Boolean
 * <li>Integer
 * <li>Class
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
public class StaticReflectiveChoice implements Choice
{
    /**
     * Construct how this choice will act when called to configure itself
     */
    public StaticReflectiveChoice(Element config) throws ClassNotFoundException, IntrospectionException
    {
        helptext = getSubElementText(config, "help");
        data = getSubElementText(config, "data");
        String clazzname = getSubElementText(config, "class");
        String propertyname = getSubElementText(config, "property");
        String levelname = getSubElementText(config, "level");
        if (levelname != null)
            level = UserLevel.forName(levelname);
        else
            level = UserLevel.LEVEL_BEGINNER;

        String priorityname = getSubElementText(config, "priority");
        if (priorityname == null)
            priority = Choice.PRIORITY_NORMAL;
        else
            priority = Integer.parseInt(priorityname);

        // Find an appropriate class        
        clazz = Class.forName(clazzname);

        // Find an appropriate method
        boolean found = false;
        for (int i=0; i<types.length && !found; i++)
        {
            found = setMethods(propertyname, types[i]);
        }
        
        if (!found)
            throw new IntrospectionException("No methods found for "+clazzname+".[g|s]et"+propertyname);
    }
    
    /**
     * Get the text in a sub element
     */
    private String getSubElementText(Element config, String child)
    {
        Element childele = config.getChild(child);
        if (childele == null)
            return null;

        return childele.getTextTrim();
    }

    /**
     * Only call this method from within the ctor or you will get all sorts of
     * synchronization errors.
     */
    private boolean setMethods(String propertyname, TypeConverter test)
    {
        try
        {
            Method temp_setter = clazz.getMethod("set"+propertyname, new Class[] { test.getTypeToConvert() });
            Method temp_getter = null;
            try
            {
                temp_getter = clazz.getMethod("get"+propertyname, new Class[0]);
            }
            catch (Exception ex)
            {
                temp_getter = clazz.getMethod("is"+propertyname, new Class[0]);
            }

            if (temp_getter.getReturnType() == test.getTypeToConvert())
            {
                setter = temp_setter;
                getter = temp_getter;
                type = test;
                return true;
            }

            log.debug("Not using "+propertyname+" from "+clazz.getName()+" because the return type of the getter is not "+test.getTypeToConvert().getName());
            return false;
        }
        catch (NoSuchMethodException ex)
        {
            return false;
        }
    }

    /**
     * @see org.crosswire.common.config.Choice#getString()
     */
    public String getString()
    {
        try
        {
            Object retval = getter.invoke(null, new Object[0]);
            return type.toString(retval);
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
        setter.invoke(null, new Object[] { type.fromString(value) });
    }

    /**
     * Help text
     */
    public String getHelpText()
    {
        return helptext;
    }

    /**
     * Override this to check and note any change
     */
    public String getType()
    {
        return type.getTypeName();
    }

    /**
     * This method is used to configure a the type selected above.
     * The object returned will depend on the type of editor selected.
     * For example an editor of type "options" may need a String array.
     * @return a configuration parameter for the type
     */
    public Object getTypeOptions()
    {
        return type.getTypeOptions();
    }

    /**
     * The UserLevel
     */
    public UserLevel getUserLevel()
    {
        return level;
    }

    /**
     * The priority which which we configure this item
     * @return A priority level
     */
    public int priority()
    {
        return priority;
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
     * Do we need to restart the program in order for this change to have
     * effect?
     * @return True if a restart is required
     */
    public boolean requiresRestart()
    {
        return false;
    }

    /**
     * A class to convert between strings and objects of a type
     */
    public abstract class TypeConverter
    {
        public abstract Class getTypeToConvert();
        public abstract String getTypeName();
        public abstract Object getTypeOptions();
        public abstract String toString(Object orig);
        public abstract Object fromString(String orig);
    }

    /**
     * A class to convert between strings and objects of a type
     */
    public class StringTypeConverter extends TypeConverter
    {
        public Class getTypeToConvert()
        {
            return String.class;
        }

        public String getTypeName()
        {
            return "text";
        }

        public Object getTypeOptions()
        {
            return data;
        }

        public String toString(Object orig)
        {
            return (String) orig;
        }

        public Object fromString(String orig)
        {
            return orig;
        }
    }

    /**
     * A class to convert between strings and objects of a type
     */
    public class IntegerTypeConverter extends TypeConverter
    {
        public Class getTypeToConvert()
        {
            return Integer.TYPE;
        }

        public String getTypeName()
        {
            return "number";
        }

        public Object getTypeOptions()
        {
            return data;
        }

        public String toString(Object orig)
        {
            return Convert.int2String(((Integer) orig).intValue());
        }

        public Object fromString(String orig)
        {
            return new Integer(Convert.string2Int(orig));
        }
    }

    /**
     * A class to convert between strings and objects of a type
     */
    public class StringArrayTypeConverter extends TypeConverter
    {
        public Class getTypeToConvert()
        {
            return String[].class;
        }

        public String getTypeName()
        {
            return "array";
        }

        public Object getTypeOptions()
        {
            return data;
        }

        public String toString(Object orig)
        {
            if (data.equals("File.pathSeparator"))
            {
                return Convert.stringArray2String(((String[]) orig), File.pathSeparator);
            }
            else
            {
                log.error("No valid entry for data object. Got data="+data);
                return "";
            }
        }

        public Object fromString(String orig)
        {
            if (data.equals("File.pathSeparator"))
            {
                return Convert.string2StringArray(orig, File.pathSeparator);
            }
            else
            {
                log.error("No valid entry for data object. Got data="+data);
                return null;
            }
        }
    }

    /**
     * A class to convert between strings and objects of a type
     */
    public class BooleanTypeConverter extends TypeConverter
    {
        public Class getTypeToConvert()
        {
            return Boolean.TYPE;
        }

        public String getTypeName()
        {
            return "boolean";
        }

        public Object getTypeOptions()
        {
            return data;
        }

        public String toString(Object orig)
        {
            return Convert.boolean2String(((Boolean) orig).booleanValue());
        }

        public Object fromString(String orig)
        {
            return new Boolean(Convert.string2Boolean(orig));
        }
    }

    /**
     * A class to convert between strings and objects of a type
     */
    public class UserLevelTypeConverter extends TypeConverter
    {
        public Class getTypeToConvert()
        {
            return UserLevel.class;
        }

        public String getTypeName()
        {
            return "options";
        }

        public Object getTypeOptions()
        {
            return Convert.string2StringArray(data, ",");
        }
        
        public String toString(Object orig)
        {
            return ((UserLevel) orig).getName();
        }

        public Object fromString(String orig)
        {
            return UserLevel.forName(orig);
        }
    }

    /**
     * A class to convert between strings and objects of a type
     */
    public class ClassTypeConverter extends TypeConverter
    {
        public Class getTypeToConvert()
        {
            return Class.class;
        }

        public String getTypeName()
        {
            return "text";
        }

        public Object getTypeOptions()
        {
            return data;
        }

        public String toString(Object orig)
        {
            return ((Class) orig).getName();
        }

        public Object fromString(String orig)
        {
            try
            {
                return Class.forName(orig);
            }
            catch (ClassNotFoundException ex)
            {
                log.warn("Class not found: "+orig, ex);
                return null;
            }
        }
    }

    /**
     * The converters that we know about.
     */
    private TypeConverter[] types = new TypeConverter[]
    {
        new StringTypeConverter(),
        new StringArrayTypeConverter(),
        new BooleanTypeConverter(),
        new UserLevelTypeConverter(),
        new IntegerTypeConverter(),
        new ClassTypeConverter(),
    };

    /**
     * The method to call to get the value
     */
    private Method getter;

    /**
     * The method to call to set the value
     */
    private Method setter;

    /**
     * The class we are going to call a static method on
     */
    private Class clazz;

    /**
     * The parameter type that we convert to and from
     */
    private TypeConverter type;

    /**
     * The help text (tooltip) for this item
     */
    private String helptext;

    /**
     * The userlevel
     */
    private UserLevel level;

    /**
     * Some options require some extra data in the config file
     */
    private String data;

    /**
     * The priority of this config level
     */
    private int priority;

    /**
     * The log stream
     */
    protected static Logger log = Logger.getLogger(StaticReflectiveChoice.class);
}
