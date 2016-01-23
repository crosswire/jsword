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

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.crosswire.common.icu.NumberShaper;
import org.crosswire.jsword.internationalisation.LocaleProviderManager;
import org.slf4j.LoggerFactory;

/**
 * A base class for implementing type safe internationalization (i18n) that is
 * easy for most cases.
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
 */
public class MsgBase {
    /**
     * Create a MsgBase object
     */
    protected MsgBase() {
        this.shaper = new NumberShaper();
    }

    /**
     * Get the internationalized text, but return key if key is unknown.
     *
     * @param key the format key to internationalize
     * @param params the parameters for the format
     * @return the internationalized text
     */
    public String lookup(String key, Object... params) {
        String rawMessage = obtainString(key);
        if (params.length == 0) {
            return shaper.shape(rawMessage);
        }

        //MessageFormat strips off all single apostrophes from the message so replace single quotes with two quotes
        rawMessage = rawMessage.replaceAll("'", "''");

        return shaper.shape(MessageFormat.format(rawMessage, params));
    }

    private String obtainString(String key) {
        try {
            if (getLocalisedResources() != null) {
                return getLocalisedResources().getString(key);
            }
        } catch (MissingResourceException ex) {
            log.error("Missing resource: Locale={} name={} package={}", LocaleProviderManager.getLocale(), key, getClass().getName());
        }

        return key;
    }

    private ResourceBundle getLocalisedResources() {
        Class<? extends MsgBase> implementingClass = getClass();
        String className = implementingClass.getName();
        String shortClassName = ClassUtil.getShortClassName(className);

        Locale currentUserLocale = LocaleProviderManager.getLocale();
        Map<String, ResourceBundle> localisedResourceMap = getLazyLocalisedResourceMap(currentUserLocale);

        ResourceBundle resourceBundle = localisedResourceMap.get(className);
        if (resourceBundle == null) {
            resourceBundle = getResourceBundleForClass(implementingClass, className, shortClassName, currentUserLocale, localisedResourceMap);
        }

        // if for some reason, we are still looking at a null, then we can only do our best, which is to return the English Locale.
        if (resourceBundle == null) {
            resourceBundle  = getResourceBundleForClass(implementingClass, className, shortClassName, Locale.ENGLISH, localisedResourceMap);
        }

        //if we're still looking at a null, there is definitely nothing else we can do, so throw an exception
        if (resourceBundle == null) {
            log.error("Missing resources: Locale={} class={}", currentUserLocale, className);
            throw new MissingResourceException("Unable to find the language resources.", className, shortClassName);
        }
        return resourceBundle;
    }

    /**
     * Gets the resource bundle for a particular class
     *
     * @param implementingClass the implementing class
     * @param className the class name
     * @param shortClassName the short class name
     * @param currentUserLocale the current user locale
     * @param localisedResourceMap the localised resource map
     * @return the resource bundle for class
     */
    private ResourceBundle getResourceBundleForClass(Class<? extends MsgBase> implementingClass, String className, String shortClassName, Locale currentUserLocale, Map<String, ResourceBundle> localisedResourceMap) {
        ResourceBundle resourceBundle;
        synchronized (MsgBase.class) {
            resourceBundle = localisedResourceMap.get(className);
            if (resourceBundle == null) {
                try {
                    resourceBundle = ResourceBundle.getBundle(shortClassName, currentUserLocale, CWClassLoader.instance(implementingClass));
                    localisedResourceMap.put(className, resourceBundle);
                } catch (MissingResourceException ex) {
                    log.warn("Assuming key is the default message {}", className);
                }
            }
        }
        return resourceBundle;
    }

    /**
     * Gets the localised resource map, initialising it if it doesn't already exist
     *
     * @param currentUserLocale the current user locale
     * @return the lazy localised resource map
     */
    private Map<String, ResourceBundle> getLazyLocalisedResourceMap(Locale currentUserLocale) {
        Map<String, ResourceBundle> localisedResourceMap = localeToResourceMap.get(currentUserLocale);
        if (localisedResourceMap == null) {
            synchronized (MsgBase.class) {
                localisedResourceMap = localeToResourceMap.get(currentUserLocale);
                if (localisedResourceMap == null) {
                    localisedResourceMap = new HashMap<String, ResourceBundle>(512);
                    localeToResourceMap.put(currentUserLocale, localisedResourceMap);
                }
            }
        }
        return localisedResourceMap;
    }

    private static Map<Locale, Map<String, ResourceBundle>> localeToResourceMap = new HashMap<Locale, Map<String, ResourceBundle>>();

    /** Internationalize numbers */
    private NumberShaper shaper;

    /**
     * The log stream
     */
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(MsgBase.class);
}
