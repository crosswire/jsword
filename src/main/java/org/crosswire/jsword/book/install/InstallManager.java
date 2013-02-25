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
 * Copyright: 2005-2013
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.book.install;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.crosswire.common.util.CWProject;
import org.crosswire.common.util.EventListenerList;
import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.PluginUtil;
import org.crosswire.common.util.PropertyMap;
import org.crosswire.common.util.Reporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A manager to abstract out the non-view specific book installation tasks.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public final class InstallManager {
    /**
     * Simple ctor
     */
    public InstallManager() {
        installers = new LinkedHashMap<String, Installer>();

        try {
            PropertyMap sitemap = PluginUtil.getPlugin(getClass());
            factories = PluginUtil.getImplementorsMap(InstallerFactory.class);
            int i = 0;
            for (String def = sitemap.get(PREFIX + ++i); def != null; def = sitemap.get(PREFIX + ++i)) {
                try {
                    String[] parts = def.split(",", 3);
                    String type = parts[0];
                    String name = parts[1];
                    String rest = parts[2];

                    Class<InstallerFactory> clazz = factories.get(type);
                    if (clazz == null) {
                        log.warn("Unable to get class for {}", type);
                    } else {
                        InstallerFactory ifactory = clazz.newInstance();
                        Installer installer = ifactory.createInstaller(rest);

                        internalAdd(name, installer);
                    }
                } catch (InstantiationException e) {
                    Reporter.informUser(this, e);
                } catch (IllegalAccessException e) {
                    Reporter.informUser(this, e);
                }
            }
        } catch (IOException ex) {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Save all properties to the user's local area. Uses the same property name
     * so as to override it.
     */
    public void save() {
        PropertyMap props = new PropertyMap();
        StringBuilder buf = new StringBuilder();
        int i = 1;
        for (String name : installers.keySet()) {
            Installer installer = installers.get(name);
            // Clear the buffer
            buf.delete(0, buf.length());
            buf.append(installer.getType());
            buf.append(',');
            buf.append(name);
            buf.append(',');
            buf.append(installer.getInstallerDefinition());
            props.put(PREFIX + i++, buf.toString());
        }
        URI outputURI = CWProject.instance().getWritableURI(getClass().getName(), FileUtil.EXTENSION_PLUGIN);
        try {
            NetUtil.storeProperties(props, outputURI, "Saved Installer Sites");
        } catch (IOException e) {
            log.error("Failed to save installers", e);
        }
    }

    /**
     * The names of all the known InstallerFactories
     */
    public Set<String> getInstallerFactoryNames() {
        return Collections.unmodifiableSet(factories.keySet());
    }

    /**
     * Find the registered name of the InstallerFactory that created the given
     * installer. There isn't a nice way to do this right now so we just do a
     * trawl through all the known factories looking!
     */
    public String getFactoryNameForInstaller(Installer installer) {
        Class<? extends Installer> match = installer.getClass();
        for (String name : factories.keySet()) {
            Class<InstallerFactory> factclazz = factories.get(name);
            try {
                InstallerFactory ifactory = factclazz.newInstance();
                Class<? extends Installer> clazz = ifactory.createInstaller().getClass();
                if (clazz == match) {
                    return name;
                }
            } catch (InstantiationException e) {
                log.warn("Failed to instantiate installer factory: {}={}", name, factclazz.getName(), e);
            } catch (IllegalAccessException e) {
                log.warn("Failed to instantiate installer factory: {}={}", name, factclazz.getName(), e);
            }
        }

        log.warn("Failed to find factory name for {} among the {} factories.", installer, Integer.toString(factories.size()));
        return null;
    }

    /**
     * Find the registered name of the Installer. There isn't a nice way to do
     * this right now so we just do a trawl through all the known factories
     * looking!
     */
    public String getInstallerNameForInstaller(Installer installer) {
        for (String name : installers.keySet()) {
            Installer test = installers.get(name);
            if (installer.equals(test)) {
                return name;
            }
        }

        log.warn("Failed to find installer name for {} among the {} installers.", installer, Integer.toString(installers.size()));
        for (String name : installers.keySet()) {
            Installer test = installers.get(name);
            log.warn("  it isn't equal to {}", test.getInstallerDefinition());
        }
        return null;
    }

    /**
     * Find the InstallerFactory associated with the given name.
     * 
     * @param name
     *            The InstallerFactory name to look-up
     * @return The found InstallerFactory or null if the name was not found
     */
    public InstallerFactory getInstallerFactory(String name) {
        Class<InstallerFactory> clazz = factories.get(name);
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            assert false : e;
        } catch (IllegalAccessException e) {
            assert false : e;
        }
        return null;
    }

    /**
     * Accessor for the known installers
     */
    public Map<String, Installer> getInstallers() {
        return Collections.unmodifiableMap(installers);
    }

    /**
     * Find an installer by name
     * 
     * @param name
     *            The name of the installer to find
     * @return The installer or null if none was found by the given name
     */
    public Installer getInstaller(String name) {
        return installers.get(name);
    }

    /**
     * Add an installer to our list of installers
     * 
     * @param name
     *            The name by which we reference the installer
     * @param installer
     *            The installer to add
     */
    public void addInstaller(String name, Installer installer) {
        assert installer != null;
        assert name != null;

        removeInstaller(name);

        internalAdd(name, installer);
        fireInstallersChanged(this, installer, true);
    }

    /**
     * InstallManager is a Map, however we demand that both names and installers
     * are unique (so we can lookup a name from an installer)
     * 
     * @param name
     *            The name of the new installer
     * @param installer
     *            The installer to associate with the given name
     */
    private void internalAdd(String name, Installer installer) {
        Iterator<String> it = installers.keySet().iterator();
        while (it.hasNext()) {
            String tname = it.next();
            Installer tinstaller = installers.get(tname);

            if (tinstaller.equals(installer)) {
                // We have a dupe - remove the old name
                log.warn("duplicate installers: {}={}. removing {}", name, tname, tname);

                // Can't call removeInstaller while iterating.
                it.remove();
                fireInstallersChanged(this, tinstaller, false);
            }
        }

        installers.put(name, installer);
    }

    /**
     * Remove an installer from our list
     * 
     * @param name
     *            The name by which this installer is referenced
     */
    public void removeInstaller(String name) {
        if (installers.containsKey(name)) {
            Installer old = installers.remove(name);
            fireInstallersChanged(this, old, false);
        }
    }

    /**
     * Remove a BibleListener from our list of listeners
     * 
     * @param li
     *            The old listener
     */
    public synchronized void addInstallerListener(InstallerListener li) {
        listeners.add(InstallerListener.class, li);
    }

    /**
     * Add a BibleListener to our list of listeners
     * 
     * @param li
     *            The new listener
     */
    public synchronized void removeBooksListener(InstallerListener li) {
        listeners.remove(InstallerListener.class, li);
    }

    /**
     * Kick of an event sequence
     * 
     * @param source
     *            The event source
     * @param installer
     *            The meta-data of the changed Bible
     * @param added
     *            Is it added?
     */
    protected synchronized void fireInstallersChanged(Object source, Installer installer, boolean added) {
        // Guaranteed to return a non-null array
        Object[] contents = listeners.getListenerList();

        // Process the listeners last to first, notifying
        // those that are interested in this event
        InstallerEvent ev = null;
        for (int i = contents.length - 2; i >= 0; i -= 2) {
            if (contents[i] == InstallerListener.class) {
                if (ev == null) {
                    ev = new InstallerEvent(source, installer, added);
                }

                if (added) {
                    ((InstallerListener) contents[i + 1]).installerAdded(ev);
                } else {
                    ((InstallerListener) contents[i + 1]).installerRemoved(ev);
                }
            }
        }
    }

    /**
     * The prefix for the keys in the installer property file.
     */
    private static final String PREFIX = "Installer.";

    /**
     * The map of installer factories
     */
    private Map<String, Class<InstallerFactory>> factories;

    /**
     * The list of discovered installers
     */
    private Map<String, Installer> installers;

    /**
     * The list of listeners
     */
    private static EventListenerList listeners = new EventListenerList();

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(InstallManager.class);
}
