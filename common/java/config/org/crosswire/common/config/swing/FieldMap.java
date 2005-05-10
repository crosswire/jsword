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
 * ID: $Id$
 */
package org.crosswire.common.config.swing;

import java.util.Map;

import org.crosswire.common.config.Choice;
import org.crosswire.common.config.MultipleChoice;
import org.crosswire.common.util.ClassUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;

/**
 * This class provides mapping between Choice types and Fields.
 * There is an argument that this class should be a properties file
 * however the practical advantages of compile time type-checking and
 * make simplicity, overweigh the possible re-use gains of a
 * properties file.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class FieldMap
{
    /**
     * Prevent Instansiation
     */
    private FieldMap()
    {
    }

    /**
     * Get a field from a string
     * @param type the configuration type
     * @return The best Field that matches
     */
    public static Field getField(Choice type)
    {
        Field field = null;

        try
        {
            // We need to treat instances of MultipleChoice differently
            // because they are always OptionsFields whatever their underlying
            // type is.
            if (type instanceof MultipleChoice)
            {
                field = new OptionsField();
            }
            else
            {
                Class clazz = (Class) map.get(type.getType());
                if (clazz != null)
                {
                    field = (Field) clazz.newInstance();
                }
                else
                {
                    log.warn("field type (" + type + ") unregistered."); //$NON-NLS-1$ //$NON-NLS-2$
                    field = new TextField();
                }
            }
        }
        catch (Exception ex)
        {
            log.warn("field type (" + type + ") initialization failed:", ex); //$NON-NLS-1$ //$NON-NLS-2$
            Reporter.informUser(type, ex);

            log.warn("field type (" + type + ") unregistered."); //$NON-NLS-1$ //$NON-NLS-2$
            field = new TextField();
        }

        field.setChoice(type);
        return field;
    }

    /**
     * The configuration table
     */
    private static Map map;

    /**
     * Default hashtable configuration
     */
    static
    {
        map = ClassUtil.getImplementorsMap(Field.class);
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(FieldMap.class);
}
