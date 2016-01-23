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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.MissingResourceException;

import org.crosswire.jsword.JSOtherMsg;

/**
 * Better implementations of the getResource methods with less ambiguity and
 * that are less dependent on the specific class loader situation.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
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
     * @param <T> the type of the resource
     * @param clazz the basis to search for the resource first.
     * @param resourceName
     *            The resource to find
     * @return The requested resource
     * @throws MissingResourceException
     *             if the resource can not be found
     */
    public static <T> URL getResource(Class<T> clazz, String resourceName) throws MissingResourceException {
        URL resource = CWClassLoader.instance(clazz).findResource(resourceName);

        if (resource == null) {
            throw new MissingResourceException(JSOtherMsg.lookupText("Cannot find resource: {0}", resourceName), clazz.getName(), resourceName);
        }

        return resource;
    }

    /**
     * Generic resource URL fetcher
     * 
     * @param search
     *            The name of the resource (without a leading /) to find
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
     * @param <T> the type of the resource
     * @param clazz the basis to search for the resource first.
     * @param search
     *            The name of the resource (without a leading /) to find
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
     */
    public static PropertyMap getProperties(String subject) throws IOException {
        return getProperties(CallContext.getCallingClass(), subject);
    }

    /**
     * Get and load a properties file from the writable area or if that fails
     * from the classpath (where a default ought to be stored)
     * 
     * @param <T> the type of the resource
     * @param clazz
     *            The name of the desired resource
     * @return The found and loaded properties file
     * @throws IOException
     *             if the resource can not be loaded
     */
    public static <T> PropertyMap getProperties(Class<T> clazz) throws IOException {
        return getProperties(clazz, ClassUtil.getShortClassName(clazz));
    }

    /**
     * Get and load a properties file from the writable area or if that fails
     * from the classpath (where a default ought to be stored)
     * 
     * @param <T> the type of the resource
     * @param clazz
     *            The name of the desired resource
     * @param subject
     *            The name of the desired resource (without any extension)
     * @return The found and loaded properties file
     * @throws IOException
     *             if the resource can not be loaded
     */
    private static <T> PropertyMap getProperties(Class<T> clazz, String subject) throws IOException {
        try {
            String lookup = subject + FileUtil.EXTENSION_PROPERTIES;
            InputStream in = getResourceAsStream(clazz, lookup);

            PropertyMap prop = new PropertyMap();
            prop.load(in);
            return prop;
        } catch (MissingResourceException e) {
            return new PropertyMap();
        }
    }
}
