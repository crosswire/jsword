
package org.crosswire.common.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.crosswire.common.util.EventListenerList;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.util.RobustList;
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
 *   <li>Resource Objects (Merlin, JDK 1.4)</li>
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class Config
{
    /**
     * Ensure that we can not be instansiated
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
        //log.debug("Adding key="+key);

        keys.add(key);
        models.add(model);

        String value = model.getString();
        if (value == null)
        {
            value = "";
            log.info("key="+key+" had a null value");
        }

        local.put(key, value);

        fireChoiceAdded(key, model);
    }

    /**
     * Add the set of configuration options specified in the xml file.
     * @param xmlconfig The JDOM document to read.
     */
    public void add(Document xmlconfig)
    {
        // We are going to assume a DTD has validated the config file and
        // just assume that everything is laid out properly.
        Element root = xmlconfig.getRootElement();
        Iterator it = root.getChildren().iterator();
        while (it.hasNext())
        {
            Element element = (Element) it.next();
            String key = element.getAttributeValue("key");
            try
            {
                Choice choice = ChoiceFactory.getChoice(element);
                add(key, choice);
            }
            catch (Exception ex)
            {
                log.warn("Error creating config element, key="+key, ex);
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

        Iterator it = keys.iterator();
        while (it.hasNext())
        {
            String key = (String) it.next();
            String path = getPath(key);

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
            catch (Throwable ex)
            {
                log.warn("Failure with setting "+key);
                Reporter.informUser(this, ex);
            }
        }
    }

    /**
     * Take the data in the local storage area and copy it to the
     * application.
     * @param force If the new value is the same as the current do we set anyway
     */
    public void localToApplication(boolean force)
    {
        int highest_change = Choice.PRIORITY_LOWEST;

        if (force)
        {
            log.info("Force=true, all changes will propogate regardless");
        }

        for (int priority=Choice.PRIORITY_SYSTEM; priority>=Choice.PRIORITY_LOWEST; priority--)
        {
            log.info("Settings for priority level="+priority);

            Iterator it = keys.iterator();
            while (it.hasNext())
            {
                String key = (String) it.next();
                Choice model = getChoice(key);

                if (model.getPriority() == priority)
                {
                    String old_value = model.getString();
                    String new_value = local.getProperty(key);

                    // The new value shouldn't really be blank - obviously this
                    // choice has just been added, substitute the default.
                    if (new_value == null)
                    {
                        local.put(key, old_value);
                        new_value = old_value;
                    }

                    try
                    {
                        // If a value has not changed, we only call setString()
                        // if force==true or if a higher priority choice has
                        // changed.
                        if (force
                            || priority < highest_change
                            || !new_value.equals(old_value))
                        {
                            log.info("Setting "+key+"="+new_value+" (was "+old_value+")");
                            model.setString(new_value);

                            if (priority > highest_change)
                            {
                                highest_change = priority;

                                if (!force)
                                {
                                    log.info("Change at level " + highest_change + ", all changes will propogate regardless");
                                }
                            }
                        }
                    }
                    catch (Throwable ex)
                    {
                        log.warn("Failure with "+key+"="+new_value, ex);
                        Reporter.informUser(this, ex);
                    }
                }
            }
        }
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
        File file = new File(url.getFile());
        OutputStream out = new FileOutputStream(file);

        getProperties().store(out, title);
        out.close();
    }

    /**
     * What is the Path of this key
     */
    public static String getPath(String key)
    {
        int last_dot = key.lastIndexOf('.');
        if (last_dot == -1)
        {
            throw new IllegalArgumentException("key=" + key + " does not contain a dot.");
        }

        return key.substring(0, last_dot);
    }

    /**
     * What is the Path of this key
     */
    public static String getLeaf(String key)
    {
        int last_dot = key.lastIndexOf('.');
        if (last_dot == -1)
        {
            throw new IllegalArgumentException("key=" + key + " does not contain a dot.");
        }

        return key.substring(last_dot+1);
    }

    /**
     * Add an Exception listener to the list of things wanting
     * to know whenever we capture an Exception
     */
    public void addConfigListener(ConfigListener li)
    {
        listener_list.add(ConfigListener.class, li);
    }

    /**
     * Remove an Exception listener from the list of things wanting
     * to know whenever we capture an Exception
     */
    public void removeConfigListener(ConfigListener li)
    {
        listener_list.remove(ConfigListener.class, li);
    }

    /**
     * A Choice got added
     */
    protected void fireChoiceAdded(String key, Choice model)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listener_list.getListenerList();

        // Process the listeners last to first, notifying
        // those that are interested in this event
        ConfigEvent ev = null;
        for (int i=listeners.length-2; i>=0; i-=2)
        {
            if (listeners[i] == ConfigListener.class)
            {
                if (ev == null)
                {
                    ev = new ConfigEvent(this, key, model);
                }

                ((ConfigListener) listeners[i+1]).choiceAdded(ev);
            }
        }
    }

    /**
     * A Choice got added
     */
    protected void fireChoiceRemoved(String key, Choice model)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listener_list.getListenerList();

        // Process the listeners last to first, notifying
        // those that are interested in this event
        ConfigEvent ev = null;
        for (int i=listeners.length-2; i>=0; i-=2)
        {
            if (listeners[i] == ConfigListener.class)
            {
                if (ev == null)
                {
                    ev = new ConfigEvent(this, key, model);
                }

                ((ConfigListener) listeners[i+1]).choiceRemoved(ev);
            }
        }
    }

    /** The log stream */
    private static final Logger log = Logger.getLogger(Config.class);

    /** The name for dialog boxes and properties files */
    protected String title;

    /** The array that stores the keys */
    protected RobustList keys = new RobustList();

    /** The array that stores the models */
    protected RobustList models = new RobustList();

    /** The set of local values */
    protected Properties local = new Properties();

    /** The list of listeners */
    protected EventListenerList listener_list = new EventListenerList();
}
