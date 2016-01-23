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
 * © CrossWire Bible Society, 2013 - 2016
 *
 */
package org.crosswire.jsword.internationalisation;

import java.util.Locale;

/**
 * A factory for creating LocaleProvider objects, to support all the static instances of where JSword needs access to the the Locale.
 * <p>
 * It is expected that the LocaleProvider will only be set once, as a result, no effort is made to make this thread-safe as this should happen on
 * start up of the application. A default locale provider is given which simply returns the default locale. See {@link DefaultLocaleProvider} for more details.
 * </p>
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Chris Burrell
 */
public final class LocaleProviderManager {
    /**
     * Prevent public access. Instantiates a new locale provider factory.
     */
    private LocaleProviderManager() {
        //No OP
    }

    /**
     * Gets the locale provider.
     *
     * @return the locale provider
     */
    public static LocaleProvider getLocaleProvider() {
        return localeProvider;
    }

    /**
     * Gets the locale to be used by the JSword library
     *
     * @return the locale
     */
    public static Locale getLocale() {
        return localeProvider.getUserLocale();
    }

    /**
     * Allow third-party applications to.
     *
     * @param provider the new locale provider
     */
    public static void setLocaleProvider(LocaleProvider provider) {
        localeProvider = provider;
    }

    private static LocaleProvider localeProvider = new DefaultLocaleProvider();
}
