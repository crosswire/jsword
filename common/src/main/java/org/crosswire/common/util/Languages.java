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

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


/**
 * A utility class that converts ISO-639 codes or locales to their "friendly" language name.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class Languages
{
    /**
     * Make the class a true utility class by having a private constructor.
     */
    private Languages()
    {
    }

    /**
     * Determine whether the language code is valid.
     * The code is valid if it is null or empty.
     * The code is valid if it is in iso639.properties.
     * If a locale is used for the iso639Code, it will use the part before the '_'.
     * Thus, this code does not support dialects, except as found in the iso639.
     * 
     * @param iso639Code
     * @return true if the language is valid.
     */
    public static boolean isValidLanguage(String iso639Code)
    {
        String lookup = iso639Code;
        if (lookup == null || lookup.length() == 0)
        {
            return true;
        }

        if (lookup.indexOf('_') != -1)
        {
            String[] locale = StringUtil.split(lookup, '_');
            return isValidLanguage(locale[0]);
        }

        // These are not uncommon. Looking for them prevents exceptions
        // and provides the same result.
        if (lookup.startsWith("x-") || lookup.startsWith("X-") || lookup.length() > 3) //$NON-NLS-1$ //$NON-NLS-2$
        {
            return false;
        }

        try
        {
            languages.getString(lookup);
            return true;
        }
        catch (MissingResourceException e)
        {
            return false;
        }
    }

    /**
     * Get the language name from the language code.
     * If the code is null or empty then it is considered to be DEFAULT_LANG_CODE (that is, English).
     * Otherwise, it will generate a log message and return unknown.
     * If a locale is used for the iso639Code, it will use the part before the '_'.
     * Thus, this code does not support dialects, except as found in the iso639.
     * 
     * @param iso639Code
     * @return the name of the language
     */
    public static String getLanguage(String iso639Code)
    {
        String lookup = iso639Code;
        if (lookup == null || lookup.length() == 0)
        {
            return getLanguage(DEFAULT_LANG_CODE);
        }

        if (lookup.indexOf('_') != -1)
        {
            String[] locale = StringUtil.split(lookup, '_');
            return getLanguage(locale[0]);
        }

        // These are not uncommon. Looking for them prevents exceptions
        // and provides the same result.
        if (lookup.startsWith("x-") || lookup.startsWith("X-") || lookup.length() > 3) //$NON-NLS-1$ //$NON-NLS-2$
        {
            return getLanguage(UNKNOWN_LANG_CODE);
        }

        try
        {
            return languages.getString(lookup);
        }
        catch (MissingResourceException e)
        {
            return getLanguage(UNKNOWN_LANG_CODE);
        }
    }

    public static final String DEFAULT_LANG_CODE = "en"; //$NON-NLS-1$
    private static final String UNKNOWN_LANG_CODE = "und"; //$NON-NLS-1$

    private static /*final*/ ResourceBundle languages;
    static
    {
        try
        {
            languages = ResourceBundle.getBundle("iso639", Locale.getDefault(), CWClassLoader.instance()); //$NON-NLS-1$;
        }
        catch (MissingResourceException e)
        {
            assert false;
        }
    }
}
