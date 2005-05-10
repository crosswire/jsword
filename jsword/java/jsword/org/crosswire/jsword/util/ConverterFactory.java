/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $ID$
 */
package org.crosswire.jsword.util;

import java.util.Map;

import org.crosswire.common.util.ClassUtil;
import org.crosswire.common.xml.Converter;

/**
 * A factory for Converters.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @see org.crosswire.common.xml.Converter
 */
public class ConverterFactory
{
    /**
     * Prevent instantiation
     */
    private ConverterFactory()
    {
    }

    /**
     * Generate a converter for the current converter name
     */
    public static Converter getConverter()
    {
        try
        {
            Class clazz = (Class) ClassUtil.getImplementorsMap(Converter.class).get(name);
            assert clazz != null : Msg.NO_CONVERTER.toString(name);
            Converter converter = (Converter) clazz.newInstance();
            return converter;
        }
        catch (Exception ex)
        {
            assert false : ex;
            return null;
        }
    }

    /**
     * Get a map of the known converters, by looking up the answers in Project
     */
    public static Map getKnownConverters()
    {
        return ClassUtil.getImplementorsMap(Converter.class);
    }

    /**
     * For config to set the currently preferred converter implementation
     */
    public static void setCurrentConverterName(String name)
    {
        ConverterFactory.name = name;
    }

    /**
     * For config to read the currently preferred converter implementation
     */
    public static String getCurrentConverterName()
    {
        return name;
    }

    /**
     * Current default converter implentation
     */
    private static String name = "Configurable"; //$NON-NLS-1$
}
