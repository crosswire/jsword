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
 * © CrossWire Bible Society, 2008 - 2016
 *
 */
package org.crosswire.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;

import org.crosswire.jsword.JSOtherMsg;
import org.slf4j.LoggerFactory;

/**
 * A plugin maps one or more implementations to an interface or abstract class
 * via a properties file whose suffix is "plugin". When there is more than one
 * implementation, one is marked as a default.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public final class PluginUtil {
    /**
     * Prevent instantiation
     */
    private PluginUtil() {
    }

    /**
     * Get the known implementors of some interface or abstract class. This is
     * currently done by looking up a plugin file by the name of the given
     * class, and assuming that values are implementors of said class. Those
     * that are not are warned, but ignored.
     * 
     * @param <T> the implementor's type
     * @param clazz
     *            The class or interface to find implementors of.
     * @return The list of implementing classes.
     */
    public static <T> Class<T>[] getImplementors(Class<T> clazz) {
        try {
            List<Class<T>> matches = new ArrayList<Class<T>>();
            PropertyMap props = getPlugin(clazz);
            for (String key : props.keySet()) {
                String name = props.get(key);
                try {
                    Class<T> impl = (Class<T>) ClassUtil.forName(name);
                    if (clazz.isAssignableFrom(impl)) {
                        matches.add(impl);
                    } else {
                        log.warn("Class {} does not implement {}. Ignoring.", impl.getName(), clazz.getName());
                    }
                } catch (ClassNotFoundException ex) {
                    log.warn("Failed to add class to list: {}", clazz.getName(), ex);
                }
            }

            log.debug("Found {} implementors of {}", Integer.toString(matches.size()), clazz.getName());
            return matches.toArray(new Class[matches.size()]);
        } catch (IOException ex) {
            log.error("Failed to get any classes.", ex);
            return new Class[0];
        }
    }

    /**
     * Get a map of known implementors of some interface or abstract class. This
     * is currently done by looking up a plugins file by the name of the given
     * class, and assuming that values are implementors of said class. Those
     * that are not are warned, but ignored. The reply is in the form of a map
     * of keys=strings, and values=classes in case you need to get at the names
     * given to the classes in the plugin file.
     * 
     * @param <T> the implementor's type
     * @param clazz
     *            The class or interface to find implementors of.
     * @return The map of implementing classes.
     * @see PluginUtil#getImplementors(Class)
     */
    public static <T> Map<String, Class<T>> getImplementorsMap(Class<T> clazz) {
        Map<String, Class<T>> matches = new HashMap<String, Class<T>>();

        try {
            PropertyMap props = getPlugin(clazz);
            for (String key : props.keySet()) {
                try {
                    String value = props.get(key);
                    Class<T> impl = (Class<T>) ClassUtil.forName(value);
                    if (clazz.isAssignableFrom(impl)) {
                        matches.put(key, impl);
                    } else {
                        log.warn("Class {} does not implement {}. Ignoring.", impl.getName(), clazz.getName());
                    }
                } catch (ClassNotFoundException ex) {
                    log.warn("Failed to add class to list: {}", clazz.getName(), ex);
                }
            }

            log.debug("Found {} implementors of {}", Integer.toString(matches.size()), clazz.getName());
        } catch (IOException ex) {
            log.error("Failed to get any classes.", ex);
        }

        return matches;
    }

    /**
     * Get the preferred implementor of some interface or abstract class. This
     * is currently done by looking up a plugins file by the name of the given
     * class, and assuming that the "default" key is an implementation of said
     * class. Warnings are given otherwise.
     * 
     * @param <T> the implementor's type
     * @param clazz
     *            The class or interface to find an implementation of.
     * @return The configured implementing class.
     * @throws MalformedURLException
     *             if the plugin file can not be found
     * @throws IOException
     *             if there is a problem reading the found file
     * @throws ClassNotFoundException
     *             if the read contents are not found
     * @throws ClassCastException
     *             if the read contents are not valid
     * @see PluginUtil#getImplementors(Class)
     */
    public static <T> Class<T> getImplementor(Class<T> clazz) throws IOException, ClassNotFoundException, ClassCastException {
        PropertyMap props = getPlugin(clazz);
        String name = props.get(DEFAULT);

        Class<T> impl = (Class<T>) ClassUtil.forName(name);
        if (!clazz.isAssignableFrom(impl)) {
            throw new ClassCastException(JSOtherMsg.lookupText("Class {0} does not implement {1}.", impl.getName(), clazz.getName()));
        }

        return impl;
    }

    /**
     * Get and instantiate the preferred implementor of some interface or
     * abstract class.
     * 
     * @param <T> the implementor's type
     * @param clazz
     *            The class or interface to find an implementation of.
     * @return The configured implementing class.
     * @throws MalformedURLException
     *             if the plugin file can not be found
     * @throws IOException
     *             if there is a problem reading the found file
     * @throws ClassNotFoundException
     *             if the read contents are not found
     * @throws ClassCastException
     *             if the read contents are not valid
     * @throws InstantiationException
     *             if the new object can not be instantiated
     * @throws IllegalAccessException
     *             if the new object can not be instantiated
     * @see PluginUtil#getImplementors(Class)
     */
    public static <T> T getImplementation(Class<T> clazz) throws MalformedURLException, ClassCastException, IOException, ClassNotFoundException,
            InstantiationException, IllegalAccessException
    {
        return getImplementor(clazz).newInstance();
    }

    /**
     * Get and load a plugin file by looking it up as a resource.
     * 
     * @param <T> the implementor's type
     * @param clazz
     *            The name of the desired resource
     * @return The found and loaded plugin file
     * @throws IOException
     *             if the resource can not be loaded
     * @throws MissingResourceException
     *             if the resource can not be found
     */
    public static <T> PropertyMap getPlugin(Class<T> clazz) throws IOException {
        String subject = ClassUtil.getShortClassName(clazz);

        try {
            String lookup = subject + PluginUtil.EXTENSION_PLUGIN;
            InputStream in = ResourceUtil.getResourceAsStream(clazz, lookup);

            PropertyMap prop = new PropertyMap();
            prop.load(in);
            return prop;
        } catch (MissingResourceException e) {
            return new PropertyMap();
        }
    }

    /**
     * Extension for properties files
     */
    public static final String EXTENSION_PLUGIN = ".plugin";

    /**
     * The string for default implementations
     */
    private static final String DEFAULT = "default";

    /**
     * The log stream
     */
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(PluginUtil.class);

}
