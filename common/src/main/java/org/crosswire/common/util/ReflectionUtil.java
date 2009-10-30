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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Various utilities for calling constructors and methods via introspection.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public final class ReflectionUtil {
    /**
     * Prevent instantiation
     */
    private ReflectionUtil() {
    }

    /**
     * Build an object using its default constructor. Note: a constructor that
     * takes a boolean needs a type of boolean.class, but a parameter of type
     * Boolean. Likewise for other primitives. If this is needed, do not call
     * this method.
     * 
     * @param className
     *            the full class name of the object
     * @return the constructed object
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static Object construct(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class clazz = ClassUtil.forName(className);
        return clazz.newInstance();
    }

    /**
     * Build an object using the supplied parameters. Note: a constructor that
     * takes a boolean needs a type of boolean.class, but a parameter of type
     * Boolean. Likewise for other primitives.
     * 
     * @param className
     *            the full class name of the object
     * @param params
     *            the constructor's arguments
     * @return the built object
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public static Object construct(String className, Object[] params) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        Class[] paramTypes = describeParameters(params);
        Class clazz = ClassUtil.forName(className);
        final Constructor c = clazz.getConstructor(paramTypes);
        return c.newInstance(params);
    }

    /**
     * Build an object using the supplied parameters.
     * 
     * @param className
     *            the full class name of the object
     * @param params
     *            the constructor's arguments
     * @param paramTypes
     *            the types of the parameters
     * @return the built object
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public static Object construct(String className, Object[] params, Class[] paramTypes) throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, InstantiationException {
        Class[] calledTypes = paramTypes;
        if (calledTypes == null) {
            calledTypes = describeParameters(params);
        }
        Class clazz = ClassUtil.forName(className);
        final Constructor c = clazz.getConstructor(calledTypes);
        return c.newInstance(params);
    }

    /**
     * Call a method on a class given a sting
     * 
     * @param base
     *            The object to invoke a method on
     * @param methodName
     *            The text of the invocation, for example "getName"
     * @param params
     *            For example new Object[] { ...}
     */
    public static Object invoke(Object base, String methodName, Object[] params) throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        Class clazz = base.getClass();
        return invoke(clazz, base, methodName, params);
    }

    /**
     * Call a static method on a class given a string
     * 
     * @param call
     *            The text of the invocation, for example
     *            "java.lang.String.getName"
     * @param params
     *            For example new Object[] { ...}
     */
    public static Object invoke(String call, Object[] params) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        // Split the call into class name and method name
        int lastDot = call.lastIndexOf('.');
        String className = call.substring(0, lastDot);
        String methodName = call.substring(lastDot + 1);
        Class clazz = ClassUtil.forName(className);
        return invoke(clazz, clazz, methodName, params);
    }

    /**
     * Call a method on an object, or statically, with the supplied parameters.
     * 
     * Note: a method that takes a boolean needs a type of boolean.class, but a
     * parameter of type Boolean. Likewise for other primitives. If this is
     * needed, do not call this method.
     * 
     * @param clazz
     *            the class of the object
     * @param obj
     *            the object having the method, or null to call a static method
     * @param methodName
     *            the method to be called
     * @param params
     *            the parameters
     * @return whatever the method returns
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static Object invoke(Class clazz, Object obj, String methodName, Object[] params) throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        return invoke(clazz, obj, methodName, params, null);
    }

    /**
     * Call a method on an object, or statically, with the supplied parameters.
     * 
     * Note: a method that takes a boolean needs a type of boolean.class, but a
     * parameter of type Boolean. Likewise for other primitives.
     * 
     * @param clazz
     *            the class of the object
     * @param obj
     *            the object having the method, or null to call a static method
     * @param methodName
     *            the method to be called
     * @param params
     *            the parameters
     * @param paramTypes
     *            the types of each of the parameters
     * @return whatever the method returns
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static Object invoke(Class clazz, Object obj, String methodName, Object[] params, Class[] paramTypes) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        Class[] calledTypes = paramTypes;
        if (calledTypes == null) {
            calledTypes = describeParameters(params);
        }
        return getMethod(clazz, methodName, calledTypes).invoke(obj, params);
    }

    /**
     * Construct a parallel array of class objects for each element in params.
     * 
     * @param params
     *            the types to describe
     * @return the parallel array of class objects
     */
    private static Class[] describeParameters(Object[] params) {
        Class[] calledTypes = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            Class clazz = params[i].getClass();
            if (clazz.equals(Boolean.class)) {
                clazz = boolean.class;
            }
            calledTypes[i] = clazz;
        }
        return calledTypes;
    }

    private static Method getMethod(Class clazz, String methodName, Class[] calledTypes) throws NoSuchMethodException {
        // The bad news is that we can't use something like:
        // clazz.getMethod(methodNames, called_types);
        // because it does not cope with inheritance (at least in the MVM)
        // so we have to search ourselves...
        Method[] testMethods = clazz.getMethods();
        outer: for (int i = 0; i < testMethods.length; i++) {
            // This this the right method name?
            if (!testMethods[i].getName().equals(methodName)) {
                continue outer;
            }

            // The right number of params
            Class[] testTypes = testMethods[i].getParameterTypes();
            if (testTypes.length != calledTypes.length) {
                continue;
            }

            // Of the right types?
            for (int j = 0; j < testTypes.length; j++) {
                if (!testTypes[j].isAssignableFrom(calledTypes[j])) {
                    continue outer;
                }
            }

            // So this is a match
            return testMethods[i];
        }

        throw new NoSuchMethodException(methodName);
    }
}
