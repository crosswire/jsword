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
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.common.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.internationalisation.LocaleProviderManager;

/**
 * A utility class that converts ISO-639 codes or locales to their "friendly"
 * language name.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class Languages {
    /**
     * Make the class a true utility class by having a private constructor.
     */
    private Languages() {
    }

    /**
     * Determine whether the language code is valid. The code is valid if it is
     * null or empty. The code is valid if it is in iso639.properties. If a
     * locale is used for the iso639Code, it will use the part before the '_'.
     * Thus, this code does not support dialects, except as found in the iso639.
     * 
     * @param iso639Code
     * @return true if the language is valid.
     */
    public static boolean isValidLanguage(String iso639Code) {
        try {
            String code = getLanguageCode(iso639Code);
            if (DEFAULT_LANG_CODE.equals(code) || UNKNOWN_LANG_CODE.equals(code)) {
                return true;
            }
            getLocalisedCommonLanguages().getString(code);
            return true;
        } catch (MissingResourceException e) {
            return false;
        }
    }

    /**
     * Get the language name from the language code. If the code is null or
     * empty then it is considered to be DEFAULT_LANG_CODE (that is, English).
     * If it starts with x- or is too long then it will return unknown. If the
     * code's name cannot be found, it will return the code. If a locale is used
     * for the iso639Code, it will use the part before the '_'. Thus, this code
     * does not support dialects, except as found in the iso639.
     * 
     * @param iso639Code
     * @return the name of the language
     */
    public static String getLanguageName(String iso639Code) {
        String code = getLanguageCode(iso639Code);
        try {
            return getLocalisedCommonLanguages().getString(code);
        } catch (MissingResourceException e) {
            try {
                return allLangs.getString(code);
            } catch (MissingResourceException e1) {
                return code;
            }
        }
    }

    /**
     * Get the language code from the input. If the code is null or empty then
     * it is considered to be DEFAULT_LANG_CODE (that is, English). If a locale
     * is used for the iso639Code, it will use the part before the '_'. Thus,
     * this code does not support dialects, except as found in the iso639. If it
     * is known to be unknown then return unknown. Otherwise, return the 2 or 3
     * letter code. Note: it might not be valid.
     * 
     * @param input
     * @return the code for the language
     */
    public static String getLanguageCode(String input) {
        String lookup = input;
        if (lookup == null || lookup.length() == 0) {
            return DEFAULT_LANG_CODE;
        }

        if (lookup.indexOf('_') != -1) {
            String[] locale = StringUtil.split(lookup, '_');
            // We need to check what stands before the _, it might be empty or
            // unknown.
            return getLanguageCode(locale[0]);
        }

        // These are not uncommon. Looking for them prevents exceptions
        // and provides the same result.
        if (lookup.startsWith("x-") || lookup.startsWith("X-") || lookup.length() > 3)
        {
            return UNKNOWN_LANG_CODE;
        }

        return lookup;
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
        if(langs == null) {
            synchronized(Languages.class) {
                langs = localisedCommonLanguages.get(locale);
                if(langs == null) {
                    langs = initLanguages(locale);
                    localisedCommonLanguages.put(locale, langs);
                }
            }
        }
        return langs;
    }
    
    private static ResourceBundle initLanguages(Locale locale) {
        try {
            return ResourceBundle.getBundle("iso639", locale, CWClassLoader.instance());
        } catch (MissingResourceException e) {
            // try the iso 639 full
            log.info("Unable to find language in iso639 bundle", e);
        }

        // this is incorrect but see JS-195
        return ResourceBundle.getBundle("iso639full", locale, CWClassLoader.instance());
    }

    
    public static final String DEFAULT_LANG_CODE = "en";
    private static final String UNKNOWN_LANG_CODE = "und";
    private static final Logger log = Logger.getLogger(Books.class);
    
    private static/* final */ResourceBundle allLangs;
    private static Map<Locale, ResourceBundle> localisedCommonLanguages = new HashMap<Locale, ResourceBundle>();
}
