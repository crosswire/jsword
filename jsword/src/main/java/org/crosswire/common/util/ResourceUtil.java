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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.Properties;

/**
 * Better implementations of the getResource methods with less ambiguity and
 * that are less dependent on the specific classloader situation.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [ dmsmith555 at yahoo dot com ]
 */
public final class ResourceUtil {
    /**
     * Prevent Instantiation
     */
    private ResourceUtil() {
    }

    /**
     * Generic resource URL fetcher. One way or the other we'll find it! Either
     * as a relative or an absolute reference.
     * 
     * @param search
     *            The name of the resource (without a leading /) to find
     * @return The requested resource
     * @throws MissingResourceException
     *             if the resource can not be found
     */
    public static URL getResource(String search) throws MissingResourceException {
        return getResource(CallContext.getCallingClass(), search);
    }

    /**
     * Generic resource URL fetcher. One way or the other we'll find it! Either
     * as a relative or an absolute reference.
     * 
     * @param clazz
     *            The resource to find
     * @return The requested resource
     * @throws MissingResourceException
     *             if the resource can not be found
     */
    public static <T> URL getResource(Class<T> clazz, String resourceName) throws MissingResourceException {
        URL resource = CWClassLoader.instance(clazz).findResource(resourceName);

        if (resource == null) {
            throw new MissingResourceException(Msg.NO_RESOURCE.toString(resourceName), clazz.getName(), resourceName);
        }

        return resource;
    }

    /**
     * Generic resource URL fetcher
     * 
     * @return The requested resource
     * @throws IOException
     *             if there is a problem reading the file
     * @throws MissingResourceException
     *             if the resource can not be found
     */
    public static InputStream getResourceAsStream(String search) throws IOException, MissingResourceException {
        return getResourceAsStream(CallContext.getCallingClass(), search);
    }

    /**
     * Generic resource URL fetcher
     * 
     * @return The requested resource
     * @throws IOException
     *             if there is a problem reading the file
     * @throws MissingResourceException
     *             if the resource can not be found
     */
    public static <T> InputStream getResourceAsStream(Class<T> clazz, String search) throws IOException, MissingResourceException {
        return ResourceUtil.getResource(clazz, search).openStream();
    }

    /**
     * Get and load a properties file from the writable area or if that fails
     * from the classpath (where a default ought to be stored)
     * 
     * @param subject
     *            The name of the desired resource (without any extension)
     * @return The found and loaded properties file
     * @throws IOException
     *             if the resource can not be loaded
     * @throws MissingResourceException
     *             if the resource can not be found
     */
    public static Properties getProperties(String subject) throws IOException {
        return getProperties(CallContext.getCallingClass(), subject);
    }

    /**
     * Get and load a properties file from the writable area or if that fails
     * from the classpath (where a default ought to be stored)
     * 
     * @param clazz
     *            The name of the desired resource
     * @return The found and loaded properties file
     * @throws IOException
     *             if the resource can not be loaded
     * @throws MissingResourceException
     *             if the resource can not be found
     */
    public static <T> Properties getProperties(Class<T> clazz) throws IOException {
        return getProperties(clazz, ClassUtil.getShortClassName(clazz));
    }

    /**
     * Get and load a properties file from the writable area or if that fails
     * from the classpath (where a default ought to be stored)
     * 
     * @param clazz
     *            The name of the desired resource
     * @return The found and loaded properties file
     * @throws IOException
     *             if the resource can not be loaded
     * @throws MissingResourceException
     *             if the resource can not be found
     */
    private static <T> Properties getProperties(Class<T> clazz, String subject) throws IOException {
        try {
            String lookup = subject + FileUtil.EXTENSION_PROPERTIES;
            InputStream in = getResourceAsStream(clazz, lookup);

            Properties prop = new Properties();
            prop.load(in);
            return prop;
        } catch (MissingResourceException e) {
            return new Properties();
        }
    }
}
