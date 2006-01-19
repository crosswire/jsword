/**
 * Distribution License:
 * This is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published
 * by the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/llgpl.html
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
package org.crosswire.common.config;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import org.crosswire.common.util.EventListenerList;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.jdom.Document;
import org.jdom.Element;

/**
 * Config is the core part of the configuration system; it is simply a
 * Collection of <code>Choice</code>s.
 *
 * <p>Config does the following things:<ul>
 *   <li>Provides a GUI independant API with which to create GUIs</li>
 *   <li>Stores a local store of settings</li>
 *   <li>Allows updates to the local store</li>
 * </ul></p>
 *
 * <p>Config does not attempt to make permanent copies of the config data
 * because different apps may wish to store the data in different ways.
 * Possible storage mechanisms include:<ul>
 *   <li>Properties Files</li>
 *   <li>Resource Objects (J2SE 1.4)</li>
 *   <li>Network Sockets (see Remote)</li>
 * </ul></p>
 *
 * The Config class stored the current Choices, and moves the data
 * between the various places that it is stored. There are 4 storage
 * areas:<ul>
 * <li><b>Permanent:</b> This can be local file, a URL, or a remote server
 *     Data is stored here between invocations of the program.
 * <li><b>Application:</b> This is the actual working copy of the data.
 * <li><b>Screen:</b> This copy of the data is shown on screen whist a
 *     Config dialog box is showing.
 * <li><b>Local:</b> This is required so that we can tell which bits of
 *     data have been changed in the screen data, and so that we can
 *     load data from disk to screen without involving the app.
 * </ul>
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class Config
{
    /**
     * Config ctor
     * @param title The name for dialog boxes and properties files
     */
    public Config(String title)
    {
        this.title = title;
    }

    /**
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Add a key/model pairing
     * @param key The new name
     * @param model The Field model to map to the key
     */
    public void add(String key, Choice model)
    {
        //log.debug("Adding key=" + key);

        keys.add(key);
        models.add(model);

        String value = model.getString();
        if (value == null)
        {
            value = ""; //$NON-NLS-1$
            log.info("key=" + key + " had a null value");  //$NON-NLS-1$//$NON-NLS-2$
        }

        local.put(key, value);

        fireChoiceAdded(key, model);
    }

    /**
     * Add the set of configuration options specified in the xml file.
     * @param xmlconfig The JDOM document to read.
     * @param configResources contains the user level text for this config
     */
    public void add(Document xmlconfig, ResourceBundle configResources)
    {
        // We are going to assume a DTD has validated the config file and
        // just assume that everything is laid out properly.
        Element root = xmlconfig.getRootElement();
        Iterator it = root.getChildren().iterator();
        while (it.hasNext())
        {
            Element element = (Element) it.next();
            String key = element.getAttributeValue("key"); //$NON-NLS-1$

            try
            {
                Choice choice = ChoiceFactory.getChoice(element, configResources);
                add(key, choice);
            }
            catch (Exception ex)
            {
                log.warn("Error creating config element, key=" + key, ex); //$NON-NLS-1$
            }
        }
    }

    /**
     * Remove a key/model pairing
     * @param key The name to kill
     */
    public void remove(String key)
    {
        Choice model = getChoice(key);
        keys.remove(key);
        models.remove(model);

        // Leave the pair in local?
        //local.put(key, value);

        fireChoiceRemoved(key, model);
    }

    /**
     * The set of Choice Names that we are controlling
     * @return An enumeration over the keys
     */
    public Iterator getPaths()
    {
        List paths = new ArrayList();

        Iterator it = models.iterator();
        while (it.hasNext())
        {
            Choice choice = (Choice) it.next();
            String path = getPath(choice.getFullPath());

            if (!paths.contains(path))
            {
                paths.add(path);
            }
        }

        return paths.iterator();
    }

    /**
     * The set of Choice Names that we are controlling
     * @return An enumeration over the keys
     */
    public Iterator getNames()
    {
        return keys.iterator();
    }

    /**
     * Step through the keys
     * @return an enum of the keys
     */
    public Choice getChoice(String key)
    {
        int index = keys.indexOf(key);
        if (index == -1)
        {
            return null;
        }

        return (Choice) models.get(index);
    }

    /**
     * The number of Choices
     * @return The number of Choices
     */
    public int size()
    {
        return keys.size();
    }

    /**
     * Set a configuration Choice (by name) to a new value. This method
     * is only of use to classes displaying config information
     */
    public void setLocal(String name, String value)
    {
        assert name != null;
        assert value != null;

        local.put(name, value);
    }

    /**
     * Get a configuration Choice (by name). This method
     * is only of use to classes displaying config information
     */
    public String getLocal(String name)
    {
        return local.getProperty(name);
    }

    /**
     * Take the data in the application and copy it to the local
     * storage area.
     */
    public void applicationToLocal()
    {
        Iterator it = keys.iterator();
        while (it.hasNext())
        {
            String key = (String) it.next();

            try
            {
                Choice model = getChoice(key);
                String value = model.getString();
                local.put(key, value);
            }
            catch (Exception ex)
            {
                log.warn("Failure with setting " + key); //$NON-NLS-1$
                Reporter.informUser(this, ex);
            }
        }
    }

    /**
     * Take the data in the local storage area and copy it to the
     * application.
     */
    public void localToApplication()
    {
        Iterator it = keys.iterator();
        while (it.hasNext())
        {
            String key = (String) it.next();
            Choice choice = getChoice(key);

            String oldValue = choice.getString();
            String newValue = local.getProperty(key);

            // The new value shouldn't really be blank - obviously this
            // choice has just been added, substitute the default.
            if (newValue == null || newValue.length() == 0)
            {
                if (oldValue == null)
                {
                    continue;
                }
                local.setProperty(key, oldValue);
                newValue = oldValue;
            }

            try
            {
                // If a value has not changed, we only call setString()
                // if force==true or if a higher priority choice has
                // changed.
                if (!newValue.equals(oldValue))
                {
                    log.info("Setting " + key + "=" + newValue + " (was " + oldValue + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    choice.setString(newValue);
                }
            }
            catch (Exception ex)
            {
                log.warn("Failure setting " + key + "=" + newValue, ex);  //$NON-NLS-1$ //$NON-NLS-2$
                Reporter.informUser(this, new ConfigException(Msg.CONFIG_SETFAIL, ex, new Object[] { choice.getFullPath() }));
            }
        }
//        int highestChange = Choice.PRIORITY_LOWEST;
//
//        if (force)
//        {
//            log.info("Force=true, all changes will propagate regardless"); //$NON-NLS-1$
//        }
//
//        for (int priority = Choice.PRIORITY_SYSTEM; priority >= Choice.PRIORITY_LOWEST; priority--)
//        {
//            log.info("Settings for priority level=" + priority); //$NON-NLS-1$
//
//            Iterator it = keys.iterator();
//            while (it.hasNext())
//            {
//                String key = (String) it.next();
//                Choice choice = getChoice(key);
//
//                if (choice.getPriority() == priority)
//                {
//                    String oldValue = choice.getString();
//                    String newValue = local.getProperty(key);
//
//                    // The new value shouldn't really be blank - obviously this
//                    // choice has just been added, substitute the default.
//                    if (newValue == null)
//                    {
//                        local.put(key, oldValue);
//                        newValue = oldValue;
//                    }
//
//                    try
//                    {
//                        // If a value has not changed, we only call setString()
//                        // if force==true or if a higher priority choice has
//                        // changed.
//                        if (force
//                            || priority < highestChange
//                            || !newValue.equals(oldValue))
//                        {
//                            log.info("Setting " + key + "=" + newValue + " (was " + oldValue + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
//                            choice.setString(newValue);
//
//                            if (priority > highestChange)
//                            {
//                                highestChange = priority;
//
//                                if (!force)
//                                {
//                                    log.info("Change at level " + highestChange + ", all changes will propagate regardless"); //$NON-NLS-1$ //$NON-NLS-2$
//                                }
//                            }
//                        }
//                    }
//                    catch (Exception ex)
//                    {
//                        log.warn("Failure setting " + key + "=" + newValue, ex);  //$NON-NLS-1$ //$NON-NLS-2$
//                        Reporter.informUser(this, new ConfigException(Msg.CONFIG_SETFAIL, ex, new Object[] { choice.getFullPath() } ));
//                    }
//                }
//            }
//        }
    }

    /**
     * Take the data stored permanetly and copy it to the local
     * storage area, using the specified stream
     */
    public void setProperties(Properties prop)
    {
        Iterator it = prop.keySet().iterator();
        while (it.hasNext())
        {
            String key = (String) it.next();
            String value = prop.getProperty(key);

            if (value != null)
            {
                local.put(key, value);
            }
        }
    }

    /**
     * Take the data in the local storage area and store it permanently
     */
    public Properties getProperties()
    {
        Properties prop = new Properties();

        Iterator it = keys.iterator();
        while (it.hasNext())
        {
            String key = (String) it.next();
            String value = local.getProperty(key);

            Choice model = getChoice(key);
            if (model.isSaveable())
            {
                prop.put(key, value);
            }
            else
            {
                prop.remove(key);
            }
        }

        return prop;
    }

    /**
     * Take the data stored permanently and copy it to the local
     * storage area, using the configured storage area
     */
    public void permanentToLocal(URL url) throws IOException
    {
        Properties prop = new Properties();
        prop.load(url.openStream());

        setProperties(prop);
    }

    /**
     * Take the data in the local storage area and store it permanently,
     * using the configured storage area.
     */
    public void localToPermanent(URL url) throws IOException
    {
        OutputStream out = null;

        try
        {
            out = new FileOutputStream(url.getFile());
            getProperties().store(out, title);
        }
        finally
        {
            if (out != null)
            {
                out.close();
            }
        }
    }

    /**
     * What is the Path of this key
     */
    public static String getPath(String key)
    {
        int lastDot = key.lastIndexOf('.');
        if (lastDot == -1)
        {
            throw new IllegalArgumentException("key=" + key + " does not contain a dot."); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return key.substring(0, lastDot);
    }

    /**
     * What is the Path of this key
     */
    public static String getLeaf(String key)
    {
        int lastDot = key.lastIndexOf('.');
        if (lastDot == -1)
        {
            throw new IllegalArgumentException("key=" + key + " does not contain a dot."); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return key.substring(lastDot + 1);
    }

    /**
     * Add an Exception listener to the list of things wanting
     * to know whenever we capture an Exception
     */
    public void addConfigListener(ConfigListener li)
    {
        listenerList.add(ConfigListener.class, li);
    }

    /**
     * Remove an Exception listener from the list of things wanting
     * to know whenever we capture an Exception
     */
    public void removeConfigListener(ConfigListener li)
    {
        listenerList.remove(ConfigListener.class, li);
    }

    /**
     * A Choice got added
     */
    protected void fireChoiceAdded(String key, Choice model)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();

        // Process the listeners last to first, notifying
        // those that are interested in this event
        ConfigEvent ev = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == ConfigListener.class)
            {
                if (ev == null)
                {
                    ev = new ConfigEvent(this, key, model);
                }

                ((ConfigListener) listeners[i + 1]).choiceAdded(ev);
            }
        }
    }

    /**
     * A Choice got added
     */
    protected void fireChoiceRemoved(String key, Choice model)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();

        // Process the listeners last to first, notifying
        // those that are interested in this event
        ConfigEvent ev = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == ConfigListener.class)
            {
                if (ev == null)
                {
                    ev = new ConfigEvent(this, key, model);
                }

                ((ConfigListener) listeners[i + 1]).choiceRemoved(ev);
            }
        }
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(Config.class);

    /**
     * The name for dialog boxes and properties files
     */
    protected String title;

    /**
     * The array that stores the keys
     */
    protected List keys = new ArrayList();

    /**
     * The array that stores the models
     */
    protected List models = new ArrayList();

    /**
     * The set of local values
     */
    protected Properties local = new Properties();

    /**
     * The list of listeners
     */
    protected EventListenerList listenerList = new EventListenerList();
}
