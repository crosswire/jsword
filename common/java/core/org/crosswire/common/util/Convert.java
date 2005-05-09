package org.crosswire.common.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Conversions between various types and Strings.
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
        if (data.equalsIgnoreCase("true")) //$NON-NLS-1$
        {
            return true;
        }
        if (data.equalsIgnoreCase("yes")) //$NON-NLS-1$
        {
            return true;
        }
        if (data.equalsIgnoreCase("ok")) //$NON-NLS-1$
        {
            return true;
        }
        if (data.equalsIgnoreCase("okay")) //$NON-NLS-1$
        {
            return true;
        }
        if (data.equalsIgnoreCase("on")) //$NON-NLS-1$
        {
            return true;
        }
        if (data.equalsIgnoreCase("1")) //$NON-NLS-1$
        {
            return true;
        }

        return false;
    }

    /**
     * Convert a boolean to a String
     * @param data the thing to convert
     * @return the converted data
     */
    public static String boolean2String(boolean data)
    {
        return data ? "True" : "False"; //$NON-NLS-1$ //$NON-NLS-2$
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
    public static Map string2Hashtable(String data, Class superclass)
    {
        Map commands = new HashMap();

        String[] data_arr = StringUtil.split(data, " "); //$NON-NLS-1$

        for (int i = 0; i < data_arr.length; i++)
        {
            try
            {
                int equ_pos = data_arr[i].indexOf('=');
                String key = data_arr[i].substring(0, equ_pos);
                String value = data_arr[i].substring(equ_pos + 1);
                Class clazz = Class.forName(value);

                if (clazz.isAssignableFrom(superclass))
                {
                    assert false;
                }
                else
                {
                    commands.put(key, value);
                }
            }
            catch (Exception ex)
            {
                log.warn("Invalid config file entry: " + data_arr[i] + " System message: " + ex.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
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
    public static Map string2Map(String data)
    {
        return string2Hashtable(data, Object.class);
    }

    /**
     * Convert a Hashtable to a Sting
     * @param commands the thing to convert
     * @return the converted data
     */
    public static String map2String(Map commands)
    {
        Iterator it = commands.entrySet().iterator();
        StringBuffer retcode = new StringBuffer();

        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry) it.next();

            retcode.append(entry.getKey());
            retcode.append('=');
            retcode.append(entry.getValue());
            retcode.append(' ');
        }

        return retcode.toString().trim();
    }

    /**
     * Convert a String to a StringArray
     * @param value the thing to convert
     * @return the converted data
     */
    public static String[] string2StringArray(String value, String separator)
    {
        return StringUtil.split(value, separator);
    }

    /**
     * Convert a StringArray to a String
     * @param value the thing to convert
     * @return the converted data
     */
    public static String stringArray2String(String[] value, String separator)
    {
        return StringUtil.join(value, separator);
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(Convert.class);
}
