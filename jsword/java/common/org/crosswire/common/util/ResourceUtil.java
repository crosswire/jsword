package org.crosswire.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Better implemenetations of the getResource methods with less ambiguity and
 * that are less dependent on the specific classloader situation.
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
public class ResourceUtil
{
    /**
     * Prevent Instansiation
     */
    private ResourceUtil()
    {
    }

    /**
     * Extension for properties files
     */
    public static final String PROPERTIES_EXTENSION = ".properties";

    /**
     * Generic resource URL fetcher. One way or the other we'll find it!
     * I'm fairly sure some of these do the same thing, but which and how they
     * change on various VMs is complex, and it seems simpler to take the
     * shotgun approach.
     * @param search The name of the resource (without a leading /) to find
     * @return The requested resource
     * @throws MalformedURLException if the resource can not be found
     */
    public static URL getResource(String search) throws MalformedURLException
    {
        String ssearch = "/" + search;

        if (search.startsWith("/"))
        {
            ResourceUtil.log.warn("getResource(" + search + ") starts with a /. More chance of success if it doesn't");
        }
    
        URL reply = ResourceUtil.class.getResource(search);
        if (reply != null)
        {
            //log.debug("getResource("+search+") = "+reply+" using getClass().getResource(search);");
            return reply;
        }
    
        reply = ResourceUtil.class.getResource(ssearch);
        if (reply != null)
        {
            //log.debug("getResource("+search+") = "+reply+" using getClass().getResource(/search);");
            return reply;
        }
    
        reply = ResourceUtil.class.getClassLoader().getResource(search);
        if (reply != null)
        {
            //log.debug("getResource("+search+") = "+reply+" using getClass().getClassLoader().getResource(search);");
            return reply;
        }
    
        reply = ResourceUtil.class.getClassLoader().getResource(ssearch);
        if (reply != null)
        {
            //log.debug("getResource("+search+") = "+reply+" using getClass().getClassLoader().getResource(/search);");
            return reply;
        }

        reply = ClassLoader.getSystemResource(search);
        if (reply != null)
        {
            //log.debug("getResource("+search+") = "+reply+" using ClassLoader.getSystemResource(search);");
            return reply;
        }

        reply = ClassLoader.getSystemResource(ssearch);
        if (reply != null)
        {
            //log.debug("getResource("+search+") = "+reply+" using ClassLoader.getSystemResource(/search);");
            return reply;
        }
    
        throw new MalformedURLException("Can't find resource: "+search);
    }

    /**
     * Generic resource URL fetcher
     * @return The requested resource
     * @throws IOException if there is a problem reading the file
     * @throws MalformedURLException if the resource can not be found
     */
    public static InputStream getResourceAsStream(String search) throws IOException, MalformedURLException
    {
        URL url = ResourceUtil.getResource(search);
        return url.openStream();
    }

    /**
     * Get the known implementors of some interface or abstract class.
     * This is currently done by looking up a properties file by the name of
     * the given class, and assuming that values are implementors of said
     * class. Those that are not are warned, but ignored.
     * @param clazz The class or interface to find implementors of.
     * @return The list of implementing classes.
     */
    public static Class[] getImplementors(Class clazz)
    {
        try
        {
            List matches = new ArrayList();
            Properties props = getProperties(clazz.getName());
            Iterator it = props.values().iterator();
            while (it.hasNext())
            {
                try
                {
                    String name = (String) it.next();
                    Class impl = Class.forName(name);
                    if (clazz.isAssignableFrom(impl))
                    {
                        matches.add(impl);
                    }
                    else
                    {
                        log.warn("Class " + impl.getName() + " does not implement " + clazz.getName() + ". Ignoring.");
                    }
                }
                catch (Exception ex)
                {
                    log.warn("Failed to add class to list: " + clazz.getName(), ex);
                }
            }

            log.debug("Found " + matches.size() + " implementors of " + clazz.getName());
            return (Class[]) matches.toArray(new Class[0]);
        }
        catch (Exception ex)
        {
            log.error("Failed to get any classes.", ex);
            return new Class[0];
        }
    }

    /**
     * Get a map of known implementors of some interface or abstract class.
     * This is currently done by looking up a properties file by the name of
     * the given class, and assuming that values are implementors of said
     * class. Those that are not are warned, but ignored. The reply is in the
     * form of a map of keys=strings, and values=classes in case you need to get
     * at the names given to the classes in the properties file.
     * @see ResourceUtil#getImplementors(Class)
     * @param clazz The class or interface to find implementors of.
     * @return The map of implementing classes.
     */
    public static Map getImplementorsMap(Class clazz)
    {
        Map matches = new HashMap();

        try
        {
            Properties props = getProperties(clazz.getName());
            Iterator it = props.keySet().iterator();
            while (it.hasNext())
            {
                try
                {
                    String key = (String) it.next();
                    String value = props.getProperty(key);
                    Class impl = Class.forName(value);
                    if (clazz.isAssignableFrom(impl))
                    {
                        matches.put(key, impl);
                    }
                    else
                    {
                        log.warn("Class " + impl.getName() + " does not implement " + clazz.getName() + ". Ignoring.");
                    }
                }
                catch (Exception ex)
                {
                    log.warn("Failed to add class to list: " + clazz.getName(), ex);
                }
            }

            log.debug("Found " + matches.size() + " implementors of " + clazz.getName());
        }
        catch (Exception ex)
        {
            log.error("Failed to get any classes.", ex);
        }

        return matches;
    }

    /**
     * Get the preferred implementor of some interface or abstract class.
     * This is currently done by looking up a properties file by the name of
     * the given class, and assuming that the "default" key is an implemention
     * of said class. Warnings are given otherwise.
     * @param clazz The class or interface to find an implementation of.
     * @return The configured implementing class.
     * @throws MalformedURLException if the properties file can not be found
     * @throws IOException if there is a problem reading the found file
     * @throws ClassNotFoundException if the read contents are not found
     * @throws ClassCastException if the read contents are not valid
     * @see ResourceUtil#getImplementors(Class)
     */
    public static Class getImplementor(Class clazz) throws MalformedURLException, IOException, ClassNotFoundException, ClassCastException
    {
        Properties props = getProperties(clazz.getName());
        String name = props.getProperty("default");

        Class impl = Class.forName(name);
        if (!clazz.isAssignableFrom(impl))
        {
            throw new ClassCastException("Class " + impl.getName() + " does not implement " + clazz.getName());
        }

        return impl;
    }

    /**
     * Get and instansiate the preferred implementor of some interface or abstract class.
     * @param clazz The class or interface to find an implementation of.
     * @return The configured implementing class.
     * @throws MalformedURLException if the properties file can not be found
     * @throws IOException if there is a problem reading the found file
     * @throws ClassNotFoundException if the read contents are not found
     * @throws ClassCastException if the read contents are not valid
     * @throws InstantiationException if the new object can not be instansiated
     * @throws IllegalAccessException if the new object can not be instansiated
     * @see ResourceUtil#getImplementors(Class)
     */
    public static Object getImplementation(Class clazz) throws MalformedURLException, ClassCastException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        Class impl = getImplementor(clazz);
        return impl.newInstance();
    }

    /**
     * Get and load a properties file from the writable area or if that
     * fails from the classpath (where a default ought to be stored)
     * @param subject The name of the desired resource (without any extension)
     * @return The found and loaded properties file
     * @throws IOException if the resource can not be loaded
     * @throws MalformedURLException if the resource can not be found
     */
    public static Properties getProperties(String subject) throws IOException, MalformedURLException
    {
        String lookup = subject+PROPERTIES_EXTENSION;
        InputStream in = getResourceAsStream(lookup);

        Properties prop = new Properties();
        prop.load(in);

        return prop;
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(ResourceUtil.class);
}
