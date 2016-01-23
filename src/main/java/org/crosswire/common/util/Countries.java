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

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.crosswire.jsword.internationalisation.LocaleProviderManager;

/**
 * A utility class that converts ISO-3166 codes or locales to their "friendly"
 * country name.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public final class Countries {
    /**
     * Make the class a true utility class by having a private constructor.
     */
    private Countries() {
    }

    /**
     * Determine whether the country code is valid. The code is valid if it is
     * null or empty. The code is valid if it is in iso3166.properties. If a
     * locale is used for the iso3166Code, it will use the part after the '_'.
     * Thus, this code does not support dialects.
     * 
     * @param iso3166Code the country code
     * @return true if the country is valid.
     */
    public static boolean isValidCountry(String iso3166Code) {
        String lookup = iso3166Code;
        if (lookup == null || lookup.length() == 0) {
            return true;
        }

        if (lookup.indexOf('_') != -1) {
            String[] locale = StringUtil.split(lookup, '_');
            return isValidCountry(locale[1]);
        }

        if (lookup.length() > 2) {
            return false;
        }

        try {
            getLocalisedCountries().getString(lookup);
            return true;
        } catch (MissingResourceException e) {
            return false;
        }
    }

    /**
     * Get the country name from the country code. If the code is null or empty
     * then it is considered to be DEFAULT_COUNTRY_CODE (that is, US).
     * Otherwise, it will generate a log message and return unknown. If a locale
     * is used for the iso3166Code, it will use the part before the '_'. Thus,
     * this code does not support dialects, except as found in the iso3166.
     * 
     * @param iso3166Code the country code
     * @return the name of the country
     */
    public static String getCountry(String iso3166Code) {
        String lookup = iso3166Code;
        if (lookup == null || lookup.length() == 0) {
            return getCountry(DEFAULT_COUNTRY_CODE);
        }

        if (lookup.indexOf('_') != -1) {
            String[] locale = StringUtil.split(lookup, '_');
            return getCountry(locale[1]);
        }

        try {
            return getLocalisedCountries().getString(lookup);
        } catch (MissingResourceException e) {
            return getCountry(UNKNOWN_COUNTRY_CODE);
        }
    }

    /**
     * Gets the localised countries.
     *
     * @return the localised countries
     */
    private static ResourceBundle getLocalisedCountries() {
        return ResourceBundle.getBundle("iso3166", LocaleProviderManager.getLocale(), CWClassLoader.instance());
    }

    public static final String DEFAULT_COUNTRY_CODE = "US";
    private static final String UNKNOWN_COUNTRY_CODE = "XX";
}
