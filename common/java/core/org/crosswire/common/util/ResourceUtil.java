package org.crosswire.common.util;

import java.io.File;
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

import org.apache.commons.lang.ClassUtils;

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
     * The string for default implementations
     */
    private static final String DEFAULT = "default"; //$NON-NLS-1$

    /**
     * If the application has set the home, it will return
     * the application's home directory, otherwise it returns null.
     * @return Returns the home.
     */
    public static synchronized URL getHome()
    {
        return home;
    }

    /**
     * Establish the applications home directory from where
     * additional resources can be found. URL is expected to
     * end with the directory name, not '/'.
     * @param newhome The home to set.
     */
    public static synchronized void setHome(URL newhome)
    {
        home = newhome;
    }

    /**
     * Generic resource URL fetcher. One way or the other we'll find it!
     * Either as a relative or an absolute reference.
     * @param search The name of the resource (without a leading /) to find
     * @return The requested resource
     * @throws MalformedURLException if the resource can not be found
     */
    public static URL getResource(String search) throws MalformedURLException
    {
        URL resource = null;

        if (search != null && search.length() > 0)
        {
            // First look for the resource using an absolute path
            if (!search.startsWith("/")) //$NON-NLS-1$
            {
                resource = findResource('/' + search);
            }
            else
            {
                log.warn("getResource(" + search + ") starts with a /. More chance of success if it doesn't"); //$NON-NLS-1$ //$NON-NLS-2$
            }

            // If that fails just look for it as is.
            if (resource != null)
            {
                resource = findResource(search);
            }
        }

        if (resource == null)
        {
            throw new MalformedURLException(Msg.NO_RESOURCE.toString(search));
        }

        return resource;
    }

    /**
     * Generic resource URL fetcher. One way or the other we'll find it!
     * Either as a relative or an absolute reference.
     * @param clazz The resource to find
     * @return The requested resource
     * @throws MalformedURLException if the resource can not be found
     */
    public static URL getResource(Class clazz, String extension) throws MalformedURLException
    {
        URL resource = findResource(clazz, extension);

        if (resource == null)
        {
            throw new MalformedURLException(Msg.NO_RESOURCE.toString(clazz.getName()));
        }

        return resource;
    }

    /**
     * Generic resource URL fetcher. This uses a variety of strategies
     * to find the resource.
     * I'm fairly sure some of these do the same thing, but which and how they
     * change on various VMs is complex, and it seems simpler to take the
     * shotgun approach.
     * @param clazz The resource to find
     * @return The requested resource, or null if it cannot be found
     */
    private static URL findResource(Class clazz, String extension)
    {
        URL reply = null;

        if (clazz == null)
        {
            assert false : "findResource called on a null string."; //$NON-NLS-1$
            return reply;
        }

        String shortName = clazz.getName() + extension;
        String longName = ClassUtils.getShortClassName(clazz) + extension;

        reply = findHomeResource(shortName);

        if (reply == null)
        {
            reply = findHomeResource(longName);
        }

        if (reply == null)
        {
            reply = clazz.getResource(shortName);
        }

        if (reply == null)
        {
            reply = clazz.getResource(longName);
        }

        if (reply == null)
        {
            reply = clazz.getClassLoader().getResource(shortName);
        }

        if (reply == null)
        {
            reply = clazz.getClassLoader().getResource(longName);
        }

        if (reply == null)
        {
            reply = ClassLoader.getSystemResource(shortName);
        }

        if (reply == null)
        {
            reply = ClassLoader.getSystemResource(longName);
        }

        return reply;
    }

    /**
     * Generic resource URL fetcher. This uses a variety of strategies
     * to find the resource.
     * I'm fairly sure some of these do the same thing, but which and how they
     * change on various VMs is complex, and it seems simpler to take the
     * shotgun approach.
     * @param search The name of the resource to find
     * @return The requested resource, or null if it cannot be found
     */
    public static URL findResource(String search)
    {
        URL reply = null;

        if (search == null)
        {
            assert false : "findResource called on a null string."; //$NON-NLS-1$
            return reply;
        }

        if (search.length() == 0)
        {
            assert false : "findResource called on an empty string."; //$NON-NLS-1$
            return reply;
        }

        reply = findHomeResource(search);

        if (reply == null)
        {
            reply = ResourceUtil.class.getResource(search);
        }

        if (reply == null)
        {
            reply = ResourceUtil.class.getClassLoader().getResource(search);
        }

        if (reply == null)
        {
            reply = ClassLoader.getSystemResource(search);
        }

        return reply;
    }

    /**
     * Look for the resource in the home directory
     * @param search must be non-null, non-empty
     * @return
     */
    public static URL findHomeResource(String search)
    {
        URL reply = null;

        // Look at the application's home first to allow overrides
        if (home != null)
        {
            // Since home does not end in a '/'
            // we need to add one to the front of search
            // if it does not have it.
            String ssearch = null;
            if (search.charAt(0) == '/')
            {
                ssearch = search;
            }
            else
            {
                ssearch = '/' + search;
            }

            URL override = null;

            // Make use of "home" thread safe
            synchronized (ResourceUtil.class)
            {
                override = NetUtil.lengthenURL(home, ssearch);
            }

            // Make sure the file exists and can be read
            File f = new File(override.getFile());
            if (f.canRead())
            {
                reply = override;
            }
        }

        return reply;
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
     * Generic resource URL fetcher
     * @return The requested resource
     * @throws IOException if there is a problem reading the file
     * @throws MalformedURLException if the resource can not be found
     */
    public static InputStream getResourceAsStream(Class clazz, String extension) throws IOException, MalformedURLException
    {
        URL url = ResourceUtil.getResource(clazz, extension);
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
            Properties props = getProperties(clazz);
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
                        log.warn("Class " + impl.getName() + " does not implement " + clazz.getName() + ". Ignoring."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    }
                }
                catch (Exception ex)
                {
                    log.warn("Failed to add class to list: " + clazz.getName(), ex); //$NON-NLS-1$
                }
            }

            log.debug("Found " + matches.size() + " implementors of " + clazz.getName()); //$NON-NLS-1$ //$NON-NLS-2$
            return (Class[]) matches.toArray(new Class[0]);
        }
        catch (Exception ex)
        {
            log.error("Failed to get any classes.", ex); //$NON-NLS-1$
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
            Properties props = getProperties(clazz);
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
                        log.warn("Class " + impl.getName() + " does not implement " + clazz.getName() + ". Ignoring."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    }
                }
                catch (Exception ex)
                {
                    log.warn("Failed to add class to list: " + clazz.getName(), ex); //$NON-NLS-1$
                }
            }

            log.debug("Found " + matches.size() + " implementors of " + clazz.getName()); //$NON-NLS-1$ //$NON-NLS-2$
        }
        catch (Exception ex)
        {
            log.error("Failed to get any classes.", ex); //$NON-NLS-1$
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
        Properties props = getProperties(clazz);
        String name = props.getProperty(DEFAULT);

        Class impl = Class.forName(name);
        if (!clazz.isAssignableFrom(impl))
        {
            throw new ClassCastException(Msg.NOT_ASSIGNABLE.toString(new Object[] { impl.getName(), clazz.getName() }));
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
        String lookup = subject+FileUtil.EXTENSION_PROPERTIES;
        InputStream in = getResourceAsStream(lookup);

        Properties prop = new Properties();
        prop.load(in);

        return prop;
    }

    /**
     * Get and load a properties file from the writable area or if that
     * fails from the classpath (where a default ought to be stored)
     * @param clazz The name of the desired resource
     * @return The found and loaded properties file
     * @throws IOException if the resource can not be loaded
     * @throws MalformedURLException if the resource can not be found
     */
    public static Properties getProperties(Class clazz) throws IOException, MalformedURLException
    {
        InputStream in = getResourceAsStream(clazz, FileUtil.EXTENSION_PROPERTIES);

        Properties prop = new Properties();
        prop.load(in);

        return prop;
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(ResourceUtil.class);

    /**
     * Notion of the project's home from where additional resources can be found.
     */
    private static URL home = null;
}
