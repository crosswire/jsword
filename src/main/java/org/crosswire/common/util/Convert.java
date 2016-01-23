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
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.common.util;

import java.util.Map;

import org.slf4j.LoggerFactory;

/**
 * Conversions between various types and Strings.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
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
     * @param data the thing to convert
     * @return the converted data
     * @throws  InstantiationException
     *               if this {@code data} represents an abstract class,
     *               an interface, an array class, a primitive type, or void;
     *               or if the class has no nullary constructor;
     *               or if the instantiation fails for some other reason.
     * @throws ClassNotFoundException if the class is not found
     * @throws IllegalAccessException  if the class or its nullary
     *               constructor is not accessible.
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
     * Convert a String to a Map, without type checking
     * 
     * @param data
     *            the thing to convert
     * @return the converted data
     */
    public static PropertyMap string2Map(String data) {
        PropertyMap commands = new PropertyMap();

        String[] parts = StringUtil.split(data, " ");
        String entry = "";
        for (int i = 0; i < parts.length; i++) {
            try {
                entry = parts[i];
                int pos = entry.indexOf('=');
                String key = entry.substring(0, pos);
                String value = entry.substring(pos + 1);
                Class<?> clazz = ClassUtil.forName(value);

                if (clazz.isAssignableFrom(Object.class)) {
                    assert false;
                } else {
                    commands.put(key, value);
                }
            } catch (ClassNotFoundException ex) {
                log.warn("Invalid config file entry: {} System message: {}", entry, ex.getMessage());
                Reporter.informUser(Convert.class, ex);
            }
        }

        return commands;
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
     * The log stream
     */
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Convert.class);
}
