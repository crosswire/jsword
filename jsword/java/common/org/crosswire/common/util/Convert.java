
package org.crosswire.common.util;

import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.log4j.Logger;

/**
 * Conversions between various types and Strings.
 *
 * <table border='1' cellPadding='3' cellSpacing='0' width="100%">
 * <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
 * Distribution Licence:<br />
 * Project B is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
 * <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
 * @see <{docs.Licence}>
 * @author Joe Walker
 */
public class Convert
{
    /**
     * We don't want anyone doing this ...
     */
    private Convert()
    {
    }

    /**
     * Convert a String to a boolean
     * @param data the thing to convert
     * @return the converted data
     */
    public static boolean string2Boolean(String data)
    {
        if (data.equalsIgnoreCase("true")) return true;
        if (data.equalsIgnoreCase("yes")) return true;
        if (data.equalsIgnoreCase("ok")) return true;
        if (data.equalsIgnoreCase("okay")) return true;
        if (data.equalsIgnoreCase("on")) return true;
        if (data.equalsIgnoreCase("1")) return true;

        return false;
    }

    /**
     * Convert a boolean to a String
     * @param data the thing to convert
     * @return the converted data
     */
    public static String boolean2String(boolean data)
    {
        return data ? "True" : "False";
    }

    /**
     * Convert a String to an int
     * @param data the thing to convert
     * @return the converted data
     */
    public static int string2Int(String data)
    {
        try
        {
            return Integer.parseInt(data);
        }
        catch (NumberFormatException ex)
        {
            return 0;
        }
    }

    /**
     * Convert an int to a String
     * @param data the thing to convert
     * @return the converted data
     */
    public static String int2String(int data)
    {
        return Integer.toString(data);
    }

    /**
     * Convert a String to an Object
     * @param data the thing to convert
     * @return the converted data
     */
    public static Object string2Object(String data) throws InstantiationException, ClassNotFoundException, IllegalAccessException
    {
        return Class.forName(data).newInstance();
    }

    /**
     * Convert an Object to a String
     * @param data the thing to convert
     * @return the converted data
     */
    public static String object2String(Object data)
    {
        return data.getClass().getName();
    }

    /**
     * Convert a String to a Class
     * @param data the thing to convert
     * @return the converted data
     */
    public static Class string2Class(String data) throws ClassNotFoundException
    {
        return Class.forName(data);
    }

    /**
     * Convert a Class to a String
     * @param data the thing to convert
     * @return the converted data
     */
    public static String class2String(Class data)
    {
        return data.getName();
    }

    /**
     * Convert a String to a Hashtable, with type checking
     * @param data the thing to convert
     * @return the converted data
     */
    public static Hashtable string2Hashtable(String data, Class superclass)
    {
        Hashtable commands = new Hashtable();

        String[] data_arr = StringUtil.tokenize(data, " ");

        for (int i=0; i<data_arr.length; i++)
        {
            try
            {
                int equ_pos = data_arr[i].indexOf('=');
                String key = data_arr[i].substring(0, equ_pos);
                String value = data_arr[i].substring(equ_pos + 1);
                Class clazz = Class.forName(value);

                if (clazz.isAssignableFrom(superclass))
                    throw new ClassCastException("Type Error");

                commands.put(key, value);
            }
            catch (Exception ex)
            {
                log.warn("Invalid config file entry: "+data_arr[i]+" System message: "+ex.getMessage());
                Reporter.informUser(Convert.class, ex);
            }
        }

        return commands;
    }

    /**
     * Convert a String to a Hashtable, without type checking
     * @param data the thing to convert
     * @return the converted data
     */
    public static Hashtable string2Hashtable(String data)
    {
        return string2Hashtable(data, Object.class);
    }

    /**
     * Convert a Hashtable to a Sting
     * @param data the thing to convert
     * @return the converted data
     */
    public static String hashtable2String(Hashtable commands)
    {
        Enumeration en = commands.keys();
        StringBuffer retcode = new StringBuffer();

        while (en.hasMoreElements())
        {
            String key = "";
            String value = "";

            try
            {
                key = (String) en.nextElement();
                value = (String) commands.get(key);

                retcode.append(key);
                retcode.append("=");
                retcode.append(value);
                retcode.append(" ");
            }
            catch (ClassCastException ex)
            {
                log.warn("non-String member found: key="+key+" value="+value);
                Reporter.informUser(Convert.class, ex);
            }
        }

        return retcode.toString().trim();
    }

    /**
     * Convert a String to a StringArray
     * @param data the thing to convert
     * @return the converted data
     */
    public static String[] string2StringArray(String value, String separator)
    {
        return StringUtil.tokenize(value, separator);
    }

    /**
     * Convert a StringArray to a String
     * @param data the thing to convert
     * @return the converted data
     */
    public static String stringArray2String(String[] value, String separator)
    {
        return StringUtil.cat(value, separator);
    }

    /** The log stream */
    protected static Logger log = Logger.getLogger("util.util");
}

