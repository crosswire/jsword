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
package org.crosswire.common.config;

import java.io.File;

import org.crosswire.common.util.Convert;

/**
 * A class to convert between strings and objects of a type.
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class PathChoice extends AbstractReflectedChoice
{
    /* (non-Javadoc)
     * @see org.crosswire.common.config.Choice#getConvertionClass()
     */
    public Class getConversionClass()
    {
        return File[].class;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.AbstractReflectedChoice#convertToString(java.lang.Object)
     */
    /* @Override */
    public String convertToString(Object orig)
    {
        File[] paths = (File[]) orig;
        String[] names = new String[paths.length];
        for (int i = 0; i < paths.length; i++)
        {
            names[i] = paths[i].getAbsolutePath();
        }

        return Convert.stringArray2String(names, File.pathSeparator);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.AbstractReflectedChoice#convertToObject(java.lang.String)
     */
    /* @Override */
    public Object convertToObject(String orig)
    {
        String[] names = Convert.string2StringArray(orig, File.pathSeparator);
        File[] paths = new File[names.length];
        for (int i = 0; i < names.length; i++)
        {
            paths[i] = new File(names[i]);
        }

        return paths;
    }
}
