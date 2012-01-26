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
package org.crosswire.common.util;

import java.util.Map;

/**
 * Conversions between various types and Strings.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class Convert {
    /**
     * We don't want anyone doing this ...
     */
    private Convert() {
    }

    /**
     * Convert a String to a boolean
     * 
     * @param data
     *            the thing to convert
     * @return the converted data
     */
    public static boolean string2Boolean(String data) {
        return Boolean.valueOf(data).booleanValue()
                || "yes".equalsIgnoreCase(data)
                || "ok".equalsIgnoreCase(data)
                || "okay".equalsIgnoreCase(data)
                || "on".equalsIgnoreCase(data)
                || "1".equals(data);
    }

    /**
     * Convert a boolean to a String
     * 
     * @param data
     *            the thing to convert
     * @return the converted data
     */
    public static String boolean2String(boolean data) {
        return Boolean.toString(data);
    }

    /**
     * Convert a String to an int
     * 
     * @param data
     *            the thing to convert
     * @return the converted data
     */
    public static int string2Int(String data) {
        try {
            return Integer.parseInt(data);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    /**
     * Convert an int to a String
     * 
     * @param data
     *            the thing to convert
     * @return the converted data
     */
    public static String int2String(int data) {
        return Integer.toString(data);
    }

    /**
     * Convert a String to an Object
     * 
     * @param data
     *            the thing to convert
     * @return the converted data
     */
    public static Object string2Object(String data) throws InstantiationException, ClassNotFoundException, IllegalAccessException {
        return ClassUtil.forName(data).newInstance();
    }

    /**
     * Convert an Object to a String
     * 
     * @param data
     *            the thing to convert
     * @return the converted data
     */
    public static String object2String(Object data) {
        return data.getClass().getName();
    }

    /**
     * Convert a String to a Class
     * 
     * @param data
     *            the thing to convert
     * @return the converted data
     */
    public static Class<?> string2Class(String data) throws ClassNotFoundException {
        return ClassUtil.forName(data);
    }

    /**
     * Convert a Class to a String
     * 
     * @param data
     *            the thing to convert
     * @return the converted data
     */
    public static String class2String(Class<?> data) {
        return data.getName();
    }

    /**
     * Convert a String to a Map, with type checking
     * 
     * @param data
     *            the thing to convert
     * @return the converted data
     */
    public static PropertyMap string2Hashtable(String data, Class<?> superclass) {
        PropertyMap commands = new PropertyMap();

        String[] data_arr = StringUtil.split(data, " ");
        String entry = "";
        for (int i = 0; i < data_arr.length; i++) {
            try {
                entry = data_arr[i];
                int equ_pos = entry.indexOf('=');
                String key = entry.substring(0, equ_pos);
                String value = entry.substring(equ_pos + 1);
                Class<?> clazz = ClassUtil.forName(value);

                if (clazz.isAssignableFrom(superclass)) {
                    assert false;
                } else {
                    commands.put(key, value);
                }
            } catch (ClassNotFoundException ex) {
                log.warn("Invalid config file entry: " + entry + " System message: " + ex.getMessage());
                Reporter.informUser(Convert.class, ex);
            }
        }

        return commands;
    }

    /**
     * Convert a String to a Map, without type checking
     * 
     * @param data
     *            the thing to convert
     * @return the converted data
     */
    public static PropertyMap string2Map(String data) {
        return string2Hashtable(data, Object.class);
    }

    /**
     * Convert a Map to a Sting
     * 
     * @param commands
     *            the thing to convert
     * @return the converted data
     */
    public static String map2String(Map<? extends Object, ? extends Object> commands) {
        StringBuilder retcode = new StringBuilder();
        for (Map.Entry<? extends Object, ? extends Object> entry : commands.entrySet()) {
            retcode.append(entry.getKey());
            retcode.append('=');
            retcode.append(entry.getValue());
            retcode.append(' ');
        }

        return retcode.toString().trim();
    }

    /**
     * Convert a String to a StringArray
     * 
     * @param value
     *            the thing to convert
     * @return the converted data
     */
    public static String[] string2StringArray(String value, String separator) {
        return StringUtil.split(value, separator);
    }

    /**
     * Convert a StringArray to a String
     * 
     * @param value
     *            the thing to convert
     * @return the converted data
     */
    public static String stringArray2String(String[] value, String separator) {
        return StringUtil.join(value, separator);
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(Convert.class);
}
