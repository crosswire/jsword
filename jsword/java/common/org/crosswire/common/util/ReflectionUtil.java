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
     * @param called_method_name The text of the invocation eg "getName"
     * @param called_params For example new Object[] { ...}
     */
    public static Object invoke(Object base, String called_method_name, Object[] called_params) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        // Create a Class array describing the params
        Class[] called_types = new Class[called_params.length];
        for (int i=0; i<called_params.length; i++)
        {
            called_types[i] = called_params[i].getClass();
        }
    
        // Reflection
        Class called_class = base.getClass();
    
        // The bad news is that we can't use something like:
        // called_class.getMethod(called_method_name, called_types);
        // because it does not cope with inheritance (at least in the MVM)
        // so we have to search ourselves...
        Method[] test_methods = called_class.getMethods();
        outer:
        for (int i=0; i<test_methods.length; i++)
        {
            // This this the right method name?
            if (!test_methods[i].getName().equals(called_method_name))
            {
                continue outer;
            }
    
            // The right number of params
            Class[] test_types = test_methods[i].getParameterTypes();
            if (test_types.length != called_types.length) continue;
    
            // Of the right types?
            for (int j=0; j<test_types.length; j++)
            {
                if (!test_types[j].isAssignableFrom(called_types[j]))
                {
                    continue outer;
                }
            }
    
            // So this is a match
            return test_methods[i].invoke(base, called_params);
        }
    
        throw new NoSuchMethodException(called_method_name);
    }

    /**
     * Call a static method on a class given a sting
     * @param call The text of the invocation eg "java.lang.String.getName"
     * @param called_params For example new Object[] { ...}
     */
    public static Object invoke(String call, Object[] called_params) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        // Spilt the call into class name and method name
        int last_dot = call.lastIndexOf('.');
        String called_class_name = call.substring(0, last_dot);
        String called_method_name = call.substring(last_dot+1);
    
        // Create a Class array describing the params
        Class[] called_types = new Class[called_params.length];
        for (int i=0; i<called_params.length; i++)
        {
            called_types[i] = called_params[i].getClass();
        }
    
        // Reflection
        Class called_class = Class.forName(called_class_name);
    
        // The bad news is that we can't use something like:
        // called_class.getMethod(called_method_name, called_types);
        // because it does not cope with inheritance (at least in the MVM)
        // so we have to search ourselves...
        Method[] test_methods = called_class.getMethods();
        outer:
        for (int i=0; i<test_methods.length; i++)
        {
            // This this the right method name?
            if (!test_methods[i].getName().equals(called_method_name))
            {
                continue outer;
            }
    
            // The right number of params
            Class[] test_types = test_methods[i].getParameterTypes();
            if (test_types.length != called_types.length) continue;
    
            // Of the right types?
            for (int j=0; j<test_types.length; j++)
            {
                if (!test_types[j].isAssignableFrom(called_types[j]))
                {
                    continue outer;
                }
            }
    
            // So this is a match
            return test_methods[i].invoke(null, called_params);
        }
    
        throw new NoSuchMethodException(called_method_name);
    }
}
