package org.crosswire.common.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.lang.StringUtils;

/**
 * Various Java Class Utilities.
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
public class ClassUtil
{
    /**
     * Prevent Instansiation
     */
    private ClassUtil()
    {
    }

    /**
     * This function finds the first matching filename for a Java class
     * file from the classpath, if none is found it returns null.
     */
    public static String findClasspathEntry(String classname, String classpath)
    {
        String full = null;

        String[] paths = StringUtils.split(classpath, File.pathSeparator);
        for (int i = 0; i < paths.length; i++)
        {
            // Search the jar
            if (paths[i].endsWith(EXTENSION_ZIP) || paths[i].endsWith(EXTENSION_JAR))
            {
                try
                {
                    String file_name = StringUtils.replace(classname, ".", "/") + EXTENSION_CLASS; //$NON-NLS-1$ //$NON-NLS-2$
                    ZipFile zip = new ZipFile(paths[i]);
                    ZipEntry entry = zip.getEntry(file_name);

                    if (entry != null && !entry.isDirectory())
                    {
                        if (full != null && !full.equals(file_name))
                        {
                            log.warn("Warning duplicate " + classname + " found: " + full + " and " + paths[i]); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        }
                        else
                        {
                            full = paths[i];
                        }
                    }
                }
                catch (IOException ex)
                {
                    // If that zip file failed, then ignore it and more on.
                }
            }
            else
            {
                // Search for the file
                String extra = StringUtils.replace(classname, ".", File.separator); //$NON-NLS-1$

                if (!paths[i].endsWith(File.separator))
                {
                    paths[i] = paths[i] + File.separator;
                }

                String file_name = paths[i] + extra + EXTENSION_CLASS;

                if (new File(file_name).isFile())
                {
                    if (full != null && !full.equals(file_name))
                    {
                        log.warn("Warning duplicate " + classname + " found: " + full + " and " + paths[i]); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    }
                    else
                    {
                        full = paths[i];
                    }
                }
            }
        }

        return full;
    }

    /**
     * This function find the first matching filename for a Java class
     * file from the classpath, if none is found it returns null.
     */
    public static String findClasspathEntry(String classname)
    {
        String classpath = System.getProperty("java.class.path", ""); //$NON-NLS-1$ //$NON-NLS-2$
        return findClasspathEntry(classname, classpath);
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
            Properties props = ResourceUtil.getProperties(clazz);
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
     * @see ClassUtil#getImplementors(Class)
     * @param clazz The class or interface to find implementors of.
     * @return The map of implementing classes.
     */
    public static Map getImplementorsMap(Class clazz)
    {
        Map matches = new HashMap();

        try
        {
            Properties props = ResourceUtil.getProperties(clazz);
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
     * @see ClassUtil#getImplementors(Class)
     */
    public static Class getImplementor(Class clazz) throws MalformedURLException, IOException, ClassNotFoundException, ClassCastException
    {
        Properties props = ResourceUtil.getProperties(clazz);
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
     * @see ClassUtil#getImplementors(Class)
     */
    public static Object getImplementation(Class clazz) throws MalformedURLException, ClassCastException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        Class impl = getImplementor(clazz);
        return impl.newInstance();
    }

    private static final String EXTENSION_CLASS = ".class"; //$NON-NLS-1$
    private static final String EXTENSION_JAR = ".jar"; //$NON-NLS-1$
    private static final String EXTENSION_ZIP = ".zip"; //$NON-NLS-1$

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(ClassUtil.class);
    /**
     * The string for default implementations
     */
    private static final String DEFAULT = "default"; //$NON-NLS-1$
}
