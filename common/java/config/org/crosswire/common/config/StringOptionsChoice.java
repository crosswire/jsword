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
package org.crosswire.common.config;

import java.util.ResourceBundle;

import org.jdom.Element;

/**
 * A class to convert between strings and objects of a type.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class StringOptionsChoice extends AbstractReflectedChoice implements MultipleChoice
{
    /* (non-Javadoc)
     * @see org.crosswire.common.config.Choice#init(org.jdom.Element)
     */
    public void init(Element option, ResourceBundle configResources) throws StartupException
    {
        super.init(option, configResources);
        Element map = option.getChild("map"); //$NON-NLS-1$
        if (map == null)
        {
            throw new StartupException(Msg.CONFIG_NOMAP);
        }

        String name = map.getAttributeValue("name"); //$NON-NLS-1$
        array = (String[]) ChoiceFactory.getDataMap().get(name);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.MultipleChoice#getOptions()
     */
    public String[] getOptions()
    {
        String [] copy = new String[array.length];
        System.arraycopy(array, 0, copy, 0, array.length);
        return copy;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.Choice#getConvertionClass()
     */
    public Class getConvertionClass()
    {
        return String.class;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.AbstractReflectedChoice#convertToString(java.lang.Object)
     */
    public String convertToString(Object orig)
    {
        return (String) orig;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.AbstractReflectedChoice#convertToObject(java.lang.String)
     */
    public Object convertToObject(String orig)
    {
        return orig;
    }

    /**
     * The options that we are presenting the user with
     */
    private String[] array;
}
