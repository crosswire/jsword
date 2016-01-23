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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Various utilities for calling constructors and methods via introspection.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
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
     * @param <T> the type of the object to construct
     * @param className
     *            the full class name of the object
     * @return the constructed object
     * @throws ClassNotFoundException if the class is not found
     * @throws InstantiationException
     *               if this {@code data} represents an abstract class,
     *               an interface, an array class, a primitive type, or void;
     *               or if the class has no nullary constructor;
     *               or if the instantiation fails for some other reason.
     * @throws IllegalAccessException  if the class or its nullary
     *               constructor is not accessible.
     */
    public static <T> T construct(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class<T> clazz = (Class<T>) ClassUtil.forName(className);
        return clazz.newInstance();
    }

    /**
     * Build an object using the supplied parameters. Note: a constructor that
     * takes a boolean needs a type of boolean.class, but a parameter of type
     * Boolean. Likewise for other primitives.
     * 
     * @param <T> the type of the object to construct
     * @param className
     *            the full class name of the object
     * @param params
     *            the constructor's arguments
     * @return the built object
     * @throws ClassNotFoundException if the class is not found
     * @throws NoSuchMethodException
     *              the method does not exist
     * @throws  InstantiationException
     *               if this {@code data} represents an abstract class,
     *               an interface, an array class, a primitive type, or void;
     *               or if the class has no nullary constructor;
     *               or if the instantiation fails for some other reason.
     * @throws IllegalAccessException  if the class or its nullary
     *               constructor is not accessible.
     * @throws InvocationTargetException if the underlying constructor
     *              throws an exception.
     * @throws InstantiationException
     *              if the class that declares the
     *              underlying constructor represents an abstract class.
     */
    public static <T> T construct(String className, Object... params) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException
    {
        Class<T> clazz = (Class<T>) ClassUtil.forName(className);
        return construct(clazz, params);
    }

    /**
     * Build an object using the supplied parameters. Note: a constructor that
     * takes a boolean needs a type of boolean.class, but a parameter of type
     * Boolean. Likewise for other primitives.
     *
     * @param <T> the type of the object to construct
     * @param clazz
     *            the class of the object
     * @param params
     *            the constructor's arguments
     * @return the built object
     * @throws NoSuchMethodException
     *              the method does not exist
     * @throws InstantiationException
     *              if the class that declares the
     *              underlying constructor represents an abstract class.
     * @throws IllegalAccessException
     *              if this {@code Constructor} object
     *              is enforcing Java language access control and the underlying
     *              constructor is inaccessible.
     * @throws InvocationTargetException if the underlying constructor
     *              throws an exception.
     */
    public static <T> T construct(final Class<T> clazz, final Object... params) throws NoSuchMethodException, InstantiationException, IllegalAccessException,
            InvocationTargetException
    {
        Class<?>[] paramTypes = describeParameters(params);
        final Constructor<T> c = clazz.getConstructor(paramTypes);
        return c.newInstance(params);
    }

    /**
     * Build an object using the supplied parameters.
     * 
     * @param <T> the type of the object to construct
     * @param className
     *            the full class name of the object
     * @param params
     *            the constructor's arguments
     * @param paramTypes
     *            the types of the parameters
     * @return the built object
     * @throws ClassNotFoundException if the class is not found
     * @throws NoSuchMethodException
     *              the method does not exist
     * @throws InstantiationException
     *              if the class that declares the
     *              underlying constructor represents an abstract class.
     * @throws IllegalAccessException
     *              if this {@code Constructor} object
     *              is enforcing Java language access control and the underlying
     *              constructor is inaccessible.
     * @throws InvocationTargetException if the underlying constructor
     *              throws an exception.
     */
    public static <T> T construct(String className, Object[] params, Class<?>[] paramTypes) throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, InstantiationException
    {
        Class<?>[] calledTypes = paramTypes;
        if (calledTypes == null) {
            calledTypes = describeParameters(params);
        }
        Class<T> clazz = (Class<T>) ClassUtil.forName(className);
        final Constructor<T> c = clazz.getConstructor(calledTypes);
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
     * @return whatever the method returs
     * @throws NoSuchMethodException
     *              the method does not exist
     * @throws IllegalAccessException
     *              if this {@code Constructor} object
     *              is enforcing Java language access control and the underlying
     *              constructor is inaccessible.
     * @throws InvocationTargetException if the underlying constructor
     *              throws an exception.
     */
    public static Object invoke(Object base, String methodName, Object... params) throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException
    {
        Class<?> clazz = base.getClass();
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
     * @return whatever the method returs
     * @throws ClassNotFoundException if the class is not found
     * @throws NoSuchMethodException
     *              the method does not exist
     * @throws IllegalAccessException
     *              if this {@code Constructor} object
     *              is enforcing Java language access control and the underlying
     *              constructor is inaccessible.
     * @throws InvocationTargetException if the underlying constructor
     *              throws an exception.
     */
    public static Object invoke(String call, Object... params) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException
    {
        // Split the call into class name and method name
        int lastDot = call.lastIndexOf('.');
        String className = call.substring(0, lastDot);
        String methodName = call.substring(lastDot + 1);
        Class<?> clazz = ClassUtil.forName(className);
        return invoke(clazz, clazz, methodName, params);
    }

    /**
     * Call a method on an object, or statically, with the supplied parameters.
     * 
     * Note: a method that takes a boolean needs a type of boolean.class, but a
     * parameter of type Boolean. Likewise for other primitives. If this is
     * needed, do not call this method.
     * 
     * @param <T> the type of the object to construct
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
     *              the method does not exist
     * @throws IllegalAccessException
     *              if this {@code Constructor} object
     *              is enforcing Java language access control and the underlying
     *              constructor is inaccessible.
     * @throws InvocationTargetException if the underlying constructor
     *              throws an exception.
     */
    public static <T> Object invoke(Class<T> clazz, Object obj, String methodName, Object... params) throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException
    {
        return invoke(clazz, obj, methodName, params, null);
    }

    /**
     * Call a method on an object, or statically, with the supplied parameters.
     * 
     * Note: a method that takes a boolean needs a type of boolean.class, but a
     * parameter of type Boolean. Likewise for other primitives.
     * 
     * @param <T> the type of the object to construct
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
     *              the method does not exist
     * @throws IllegalAccessException
     *              if this {@code Constructor} object
     *              is enforcing Java language access control and the underlying
     *              constructor is inaccessible.
     * @throws InvocationTargetException if the underlying constructor
     *              throws an exception.
     */
    public static <T> Object invoke(Class<T> clazz, Object obj, String methodName, Object[] params, Class<?>[] paramTypes) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException
    {
        Class<?>[] calledTypes = paramTypes;
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
    private static Class<?>[] describeParameters(Object... params) {
        Class<?>[] calledTypes = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            Class<?> clazz = params[i].getClass();
            if (clazz.equals(Boolean.class)) {
                clazz = boolean.class;
            }
            calledTypes[i] = clazz;
        }
        return calledTypes;
    }

    private static <T> Method getMethod(Class<T> clazz, String methodName, Class<?>[] calledTypes) throws NoSuchMethodException {
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
            Class<?>[] testTypes = testMethods[i].getParameterTypes();
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
