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
 * ID: $Id: AbstractBookMetaData.java 1311 2007-05-03 19:36:51Z dmsmith $
 */
package org.crosswire.common.util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


/**
 * An implementaion of the Propery Change methods from BookMetaData.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class Languages
{
    /**
     * Get the language name from the language code.
     * If a locale is used for the iso639Code, it will use the part before the '_'.
     * Thus, this code does not support dialects, except as found in the iso639.
     * 
     * @param iso639Code
     * @return the name of the language
     */
    public static String getLanguage(String ident, String iso639Code)
    {
        String lookup = iso639Code;
        if (lookup == null || lookup.length() == 0)
        {
            return getLanguage(ident, DEFAULT_LANG_CODE);
        }

        if (lookup.indexOf('_') != -1)
        {
            String[] locale = StringUtil.split(lookup, '_');
            return getLanguage(ident, locale[0]);
        }

        // If the language begins w/ an x- then it is "Undetermined"
        // Also if it is not a 2 or 3 character code then it is not a valid
        // iso639 code.
        if (lookup.startsWith("x-") || lookup.startsWith("X-") || lookup.length() > 3) //$NON-NLS-1$ //$NON-NLS-2$
        {
            return getLanguage(ident, UNKNOWN_LANG_CODE);
        }

        try
        {
            return languages.getString(lookup);
        }
        catch (MissingResourceException e)
        {
            log.error("Not a valid language code:" + iso639Code + " in book " + ident); //$NON-NLS-1$ //$NON-NLS-2$
            return getLanguage(ident, UNKNOWN_LANG_CODE);
        }
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(Languages.class);

    public static final String DEFAULT_LANG_CODE = "en"; //$NON-NLS-1$
    private static final String UNKNOWN_LANG_CODE = "und"; //$NON-NLS-1$

    private static/*final*/ResourceBundle languages;
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
