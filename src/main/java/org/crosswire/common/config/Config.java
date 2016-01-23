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
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.common.config;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;

import org.crosswire.common.util.LucidException;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.PropertyMap;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.JSOtherMsg;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Config is the core part of the configuration system; it is simply a
 * Collection of <code>Choice</code>s.
 * 
 * Config does the following things:
 * <ul>
 * <li>Provides a GUI independent API with which to create GUIs</li>
 * <li>Stores a local store of settings</li>
 * <li>Allows updates to the local store</li>
 * </ul>
 * 
 * 
 * Config does not attempt to make permanent copies of the config data because
 * different applications may wish to store the data in different ways. Possible storage
 * mechanisms include:
 * <ul>
 * <li>Properties Files</li>
 * <li>Resource Objects (J2SE 1.4)</li>
 * <li>Network Sockets (see Remote)</li>
 * </ul>
 * 
 * The Config class stored the current Choices, and moves the data between the
 * various places that it is stored. There are 4 storage areas:
 * <ul>
 * <li><b>Permanent:</b> This can be local file, a URI, or a remote server Data
 * is stored here between invocations of the program.
 * <li><b>Application:</b> This is the actual working copy of the data.
 * <li><b>Screen:</b> This copy of the data is shown on screen whist a Config
 * dialog box is showing.
 * <li><b>Local:</b> This is required so that we can tell which bits of data
 * have been changed in the screen data, and so that we can load data from disk
 * to screen without involving the app.
 * </ul>
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class Config implements Iterable<Choice> {
    /**
     * Config ctor
     * 
     * @param title
     *            The name for dialog boxes and properties files
     */
    public Config(String title) {
        this.title = title;
        keys = new ArrayList<String>();
        models = new ArrayList<Choice>();
        local = new PropertyMap();
        listeners = new CopyOnWriteArrayList<ConfigListener>();
    }

    /**
     * The name for the dialog boxes and properties files.
     * 
     * @return the title for this config
     */
    public String getTitle() {
        return title;
    }

    /**
     * Add a key/model pairing
     * 
     * @param model
     *            The Choice model to map to its key
     */
    public void add(Choice model) {
        String key = model.getKey();
        // log.debug("Adding key={}", key);

        keys.add(key);
        models.add(model);

        String value = model.getString();
        if (value == null) {
            value = "";
            LOGGER.info("key={} had a null value", key);
        }

        local.put(key, value);

        fireChoiceAdded(key, model);
    }

    /**
     * Add the set of configuration options specified in the xml file.
     * 
     * @param xmlconfig
     *            The JDOM document to read.
     * @param configResources
     *            contains the user level text for this config
     */
    public void add(Document xmlconfig, ResourceBundle configResources) {
        // We are going to assume a DTD has validated the config file and
        // just assume that everything is laid out properly.
        Element root = xmlconfig.getRootElement();
        Iterator<?> iter = root.getChildren().iterator();
        while (iter.hasNext()) {
            Element element = (Element) iter.next();
            String key = element.getAttributeValue("key");

            Exception ex = null;
            try {
                Choice choice = ChoiceFactory.getChoice(element, configResources);
                if (!choice.isIgnored()) {
                    add(choice);
                }
            } catch (StartupException e) {
                ex = e;
            } catch (ClassNotFoundException e) {
                ex = e;
            } catch (IllegalAccessException e) {
                ex = e;
            } catch (InstantiationException e) {
                ex = e;
            }

            if (ex != null) {
                LOGGER.warn("Error creating config element, key={}", key, ex);
            }
        }
    }

    /**
     * Remove a key/model pairing
     * 
     * @param key
     *            The name to kill
     */
    public void remove(String key) {
        Choice model = getChoice(key);
        keys.remove(key);
        models.remove(model);

        // Leave the pair in local?
        // local.put(key, value);

        fireChoiceRemoved(key, model);
    }

    /**
     * The set of Choice that we are controlling
     * 
     * @return An iterator over the choices
     */
    public Iterator<Choice> iterator() {
        return models.iterator();
    }

    /**
     * Get the Choice for a given key
     * 
     * @param key the key for the choice
     * @return the requested choice
     */
    public Choice getChoice(String key) {
        int index = keys.indexOf(key);
        if (index == -1) {
            return null;
        }

        return models.get(index);
    }

    /**
     * The number of Choices
     * 
     * @return The number of Choices
     */
    public int size() {
        return keys.size();
    }

    /**
     * Set a configuration Choice (by name) to a new value. This method is only
     * of use to classes displaying config information.
     * 
     * @param name the key for the choice
     * @param value the value for the choice
     */
    public void setLocal(String name, String value) {
        assert name != null;
        assert value != null;

        local.put(name, value);
    }

    /**
     * Get a configuration Choice (by name). This method is only of use to
     * classes displaying config information.
     * 
     * @param name the key for the choice
     * @return the value for the choice.
     */
    public String getLocal(String name) {
        return local.get(name);
    }

    /**
     * Take the data in the application and copy it to the local storage area.
     */
    public void applicationToLocal() {
        for (String key : keys) {
            Choice model = getChoice(key);
            String value = model.getString();
            local.put(key, value);
        }
    }

    /**
     * Take the data in the local storage area and copy it to the application.
     */
    public void localToApplication() {
        for (String key : keys) {
            Choice choice = getChoice(key);

            String oldValue = choice.getString(); // never returns null
            String newValue = local.get(key);

            // The new value shouldn't really be blank - obviously this
            // choice has just been added, substitute the default.
            if ((newValue == null) || (newValue.length() == 0)) {
                if ((oldValue == null) || (oldValue.length() == 0)) {
                    continue;
                }
                local.put(key, oldValue);
                newValue = oldValue;
            }

            // If a value has not changed, we only call setString()
            // if force==true or if a higher priority choice has
            // changed.
            if (!newValue.equals(oldValue)) {
                LOGGER.info("Setting {}={} (was {})", key, newValue, oldValue);
                try {
                    choice.setString(newValue);
                    if (changeListeners != null) {
                        changeListeners.firePropertyChange(new PropertyChangeEvent(choice, choice.getKey(), oldValue, newValue));
                    }
                } catch (LucidException ex) {
                    LOGGER.warn("Failure setting {}={}", key, newValue, ex);
                    Reporter.informUser(this, new ConfigException(JSOtherMsg.lookupText("Failed to set option: {0}", choice.getFullPath()), ex));
                }
            }
        }
    }

    /**
     * Take the data stored permanently and copy it to the local storage area,
     * using the specified stream.
     * 
     * @param prop the set of properties to save
     */
    public void setProperties(PropertyMap prop) {
        for (String key : prop.keySet()) {
            String value = prop.get(key);

            Choice model = getChoice(key);
            // Only if a value was stored and it should be stored then we use
            // it.
            if (value != null && model != null && model.isSaveable()) {
                local.put(key, value);
            }
        }
    }

    /**
     * Take the data in the local storage area and store it permanently.
     * 
     * @return the collection of properties
     */
    public PropertyMap getProperties() {
        PropertyMap prop  = new PropertyMap();

        for (String key : keys) {
            String value = local.get(key);

            Choice model = getChoice(key);
            if (model.isSaveable()) {
                prop.put(key, value);
            } else {
                prop.remove(key);
            }
        }

        return prop;
    }

    /**
     * Take the data stored permanently and copy it to the local storage area,
     * using the configured storage area
     * 
     * @param uri the location of the permanent storage
     * @throws IOException if there was a problem getting the permanent config info
     */
    public void permanentToLocal(URI uri) throws IOException {
        setProperties(NetUtil.loadProperties(uri));
    }

    /**
     * Take the data in the local storage area and store it permanently, using
     * the configured storage area.
     * 
     * @param uri the location of the permanent storage
     * @throws IOException if there was a problem storing the permanent config info
     */
    public void localToPermanent(URI uri) throws IOException {
        NetUtil.storeProperties(getProperties(), uri, title);
    }

    /**
     * What is the Path of this key
     * 
     * @param key the key of the property
     * @return the path of the key
     */
    public static String getPath(String key) {
        int lastDot = key.lastIndexOf('.');
        if (lastDot == -1) {
            throw new IllegalArgumentException("key=" + key + " does not contain a dot.");
        }

        return key.substring(0, lastDot);
    }

    /**
     * What is the last part of the Path of this key.
     * 
     * @param key the key of the property
     * @return the part of the path after the last dot, '.'
     */
    public static String getLeaf(String key) {
        int lastDot = key.lastIndexOf('.');
        if (lastDot == -1) {
            throw new IllegalArgumentException("key=" + key + " does not contain a dot.");
        }

        return key.substring(lastDot + 1);
    }

    /**
     * Add a PropertyChangeListener to the listener list. The listener is
     * registered for all properties.
     * 
     * @param listener
     *            The PropertyChangeListener to be added
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (changeListeners == null) {
            changeListeners = new PropertyChangeSupport(this);
        }
        changeListeners.addPropertyChangeListener(listener);
    }

    /**
     * Add a PropertyChangeListener for a specific property. The listener will
     * be invoked only when a call on firePropertyChange names that specific
     * property.
     * 
     * @param propertyName
     *            The name of the property to listen on.
     * @param listener
     *            The PropertyChangeListener to be added
     */

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (changeListeners == null) {
            changeListeners = new PropertyChangeSupport(this);
        }
        changeListeners.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Remove a PropertyChangeListener from the listener list. This removes a
     * PropertyChangeListener that was registered for all properties.
     * 
     * @param listener
     *            The PropertyChangeListener to be removed
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (changeListeners != null) {
            changeListeners.removePropertyChangeListener(listener);
        }
    }

    /**
     * Remove a PropertyChangeListener for a specific property.
     * 
     * @param propertyName
     *            The name of the property that was listened on.
     * @param listener
     *            The PropertyChangeListener to be removed
     */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (changeListeners != null) {
            changeListeners.removePropertyChangeListener(propertyName, listener);
        }
    }

    /**
     * Add an Exception listener to the list of things wanting to know whenever
     * we capture an Exception.
     * 
     * @param li
     *            The ConfigListener to be added
     */
    public void addConfigListener(ConfigListener li) {
        listeners.add(li);
    }

    /**
     * Remove an Exception listener from the list of things wanting to know
     * whenever we capture an Exception
     * 
     * @param li
     *            The ConfigListener to be removed
     */
    public void removeConfigListener(ConfigListener li) {
        listeners.remove(li);
    }

    /**
     * A Choice got added.
     * 
     * @param key the key of the choice that has been added
     * @param model the choice that was added
     */
   protected void fireChoiceAdded(String key, Choice model) {
        ConfigEvent ev = new ConfigEvent(this, key, model);
        for (ConfigListener listener : listeners) {
            listener.choiceAdded(ev);
        }
    }

    /**
     * A Choice got removed.
     * 
     * @param key the key of the choice that has been removed
     * @param model the choice that was removed
     */
    protected void fireChoiceRemoved(String key, Choice model) {
        ConfigEvent ev = new ConfigEvent(this, key, model);
        for (ConfigListener listener : listeners) {
            listener.choiceRemoved(ev);
        }
    }

    /**
     * The name for dialog boxes and properties files
     */
    protected String title;

    /**
     * The array that stores the keys
     */
    protected List<String> keys = new ArrayList<String>();

    /**
     * The array that stores the models
     */
    protected List<Choice> models = new ArrayList<Choice>();

    /**
     * The set of local values
     */
    protected PropertyMap local;

    /**
     * The set of property change listeners.
     */
    protected PropertyChangeSupport changeListeners;

    /**
     * The list of listeners
     */
    protected List<ConfigListener> listeners;

    /**
     * The log stream
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);
}
