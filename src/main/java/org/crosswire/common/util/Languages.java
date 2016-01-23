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
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.internationalisation.LocaleProviderManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class that converts bcp-47 codes as supported by {@link Language} to their
 * localized language name.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public final class Languages {

    /**
     * Make the class a true utility class by having a private constructor.
     */
    private Languages() {
    }

    /**
     * Get the language name for the BCP-47 specification of the language.
     * 
     * @param code the BCP-47 specification for the language
     * @return the name of the language
     */
    public static String getName(String code) {
        // Returning the code is the fallback for lookup
        String name = code;
        try {
            ResourceBundle langs = getLocalisedCommonLanguages();
            if (langs != null) {
                name = langs.getString(code);
            }
        } catch (MissingResourceException e) {
            // This is allowed
        }
        return name;
    }

    /**
     * Gets the localised common languages. Caching here, is done to prevent extra logging 
     * happening every time we miss the iso639 ResourceBundle
     * and end up having to lookup the iso639full
     * 
     * @return the localised common languages
     */
    private static ResourceBundle getLocalisedCommonLanguages() {
        Locale locale = LocaleProviderManager.getLocale();
        ResourceBundle langs = localisedCommonLanguages.get(locale);
        if (langs == null) {
            synchronized (Languages.class) {
                langs = localisedCommonLanguages.get(locale);
                if (langs == null) {
                    langs = initLanguages(locale);
                    if (langs != null) {
                        localisedCommonLanguages.put(locale, langs);
                    }
                }
            }
        }
        return langs;
    }

    private static ResourceBundle initLanguages(Locale locale) {
        try {
            return ResourceBundle.getBundle("iso639", locale, CWClassLoader.instance());
        } catch (MissingResourceException e) {
            log.info("Unable to find language in iso639 bundle", e);
        }
        return null;
    }

    /**
     * Provide a fallback lookup against a huge list of all languages.
     * The basic list has a few hundred languages. The full list has
     * over 7000. As a fallback, this file is not internationalized.
     */
    public static final class AllLanguages {
        /**
         * This is a singleton class. Do not allow construction.
         */
        private AllLanguages() { }

        /**
         * Get the language name for the code. If the language name is not known
         * then return the code.
         * 
         * @param languageCode the language code
         * @return the name for the language.
         */
        public static String getName(String languageCode) {
            if (instance != null) {
                String name = instance.get(languageCode);
                if (name != null) {
                    return name;
                }
            }
            return languageCode;
        }

        /**
         * Do lazy loading of the huge file of languages.
         * Note: It is OK for it not to be present.
         */
        private static PropertyMap instance;
        static {
            try {
                instance = ResourceUtil.getProperties("iso639full");
                log.debug("Loading iso639full.properties file");
            } catch (IOException e) {
                log.info("Unable to load iso639full.properties", e);
            }
        }
    }

    /**
     * Provide a fallback lookup against a huge list of all languages.
     * The basic list has a few hundred languages. The full list has
     * over 7000. As a fallback, this file is not internationalized.
     */
    public static final class RtoL {
        /**
         * This is a singleton class. Do not allow construction.
         */
        private RtoL() { }

        /**
         * Determine whether this language is a Left-to-Right or a Right-to-Left
         * language. If the language has a script, it is used for the determination.
         * Otherwise, check the language.
         * <p>
         * Note: This is problematic. Languages do not have direction.
         * Scripts do. Further, there are over 7000 living languages, many of which
         * are written in Right-to-Left scripts and are not listed here.
         * </p>
         * 
         * @param script the iso15924 script code, must be in Title case
         * @param lang the iso639 language code, must be lower case
         * @return true if the language is Right-to-Left
         */
        public static boolean isRtoL(String script, String lang) {
            if (script != null) {
                return rtol.contains(script);
            }
            if (lang != null) {
                return rtol.contains(lang);
            }
            return false;
        }

        /**
         * Do lazy loading of the huge file of languages.
         * Note: It is OK for it not to be present.
         */
        private static Set rtol = new HashSet();
        /**
         * load RtoL data
         */
        static {
            try {
                URL index = ResourceUtil.getResource(Translations.class, "rtol.txt");
                String[] list = NetUtil.listByIndexFile(NetUtil.toURI(index));
                log.debug("Loading iso639full.properties file");
                for (int i = 0; i < list.length; i++) {
                    rtol.add(list[i]);
                }
            } catch (IOException ex) {
                log.info("Unable to load rtol.txt", ex);
            }
        }
    }

    private static Map<Locale, ResourceBundle> localisedCommonLanguages = new HashMap<Locale, ResourceBundle>();

    /**
     * The log stream
     */
    protected static final Logger log = LoggerFactory.getLogger(Books.class);
}
