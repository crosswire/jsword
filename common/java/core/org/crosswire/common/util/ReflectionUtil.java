package org.crosswire.common.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Various utilities for running introspected methods.
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
public class ReflectionUtil
{
    /**
     * Prevent Instansiation
     */
    private ReflectionUtil()
    {
    }

    /**
     * Call a method on a class given a sting
     * @param base The object to invoke a method on
     * @param methodName The text of the invocation eg "getName"
     * @param params For example new Object[] { ...}
     */
    public static Object invoke(Object base, String methodName, Object[] params) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        // Create a Class array describing the params
        Class[] calledTypes = new Class[params.length];
        for (int i = 0; i < params.length; i++)
        {
            calledTypes[i] = params[i].getClass();
        }

        // Reflection
        Class clazz = base.getClass();

        // The bad news is that we can't use something like:
        // called_class.getMethod(called_method_name, called_types);
        // because it does not cope with inheritance (at least in the MVM)
        // so we have to search ourselves...
        Method[] testMethods = clazz.getMethods();
        outer:
        for (int i = 0; i < testMethods.length; i++)
        {
            // This this the right method name?
            if (!testMethods[i].getName().equals(methodName))
            {
                continue outer;
            }

            // The right number of params
            Class[] testTypes = testMethods[i].getParameterTypes();
            if (testTypes.length != calledTypes.length)
            {
                continue;
            }

            // Of the right types?
            for (int j = 0; j < testTypes.length; j++)
            {
                if (!testTypes[j].isAssignableFrom(calledTypes[j]))
                {
                    continue outer;
                }
            }

            // So this is a match
            return testMethods[i].invoke(base, params);
        }

        throw new NoSuchMethodException(methodName);
    }

    /**
     * Call a static method on a class given a sting
     * @param call The text of the invocation eg "java.lang.String.getName"
     * @param params For example new Object[] { ...}
     */
    public static Object invoke(String call, Object[] params) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        // Spilt the call into class name and method name
        int lastDot = call.lastIndexOf('.');
        String className = call.substring(0, lastDot);
        String methodName = call.substring(lastDot + 1);

        // Create a Class array describing the params
        Class[] calledTypes = new Class[params.length];
        for (int i = 0; i < params.length; i++)
        {
            calledTypes[i] = params[i].getClass();
        }

        // Reflection
        Class clazz = Class.forName(className);

        // The bad news is that we can't use something like:
        // clazz.getMethod(called_method_name, called_types);
        // because it does not cope with inheritance (at least in the MVM)
        // so we have to search ourselves...
        Method[] testMethods = clazz.getMethods();
        outer:
        for (int i = 0; i < testMethods.length; i++)
        {
            // This this the right method name?
            if (!testMethods[i].getName().equals(methodName))
            {
                continue outer;
            }

            // The right number of params
            Class[] testTypes = testMethods[i].getParameterTypes();
            if (testTypes.length != calledTypes.length)
            {
                continue;
            }

            // Of the right types?
            for (int j = 0; j < testTypes.length; j++)
            {
                if (!testTypes[j].isAssignableFrom(calledTypes[j]))
                {
                    continue outer;
                }
            }

            // So this is a match
            return testMethods[i].invoke(null, params);
        }

        throw new NoSuchMethodException(methodName);
    }
}
