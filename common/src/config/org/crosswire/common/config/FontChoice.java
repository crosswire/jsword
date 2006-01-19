/**
 * Distribution License:
 * This is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published
 * by the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/llgpl.html
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

/**
 * A class to convert between strings and objects of a type.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class FontChoice extends AbstractReflectedChoice
{
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
        // PENDING: object=font? is this right? isn't there a converter somewhere
        return (String) orig;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.AbstractReflectedChoice#convertToObject(java.lang.String)
     */
    public Object convertToObject(String orig)
    {
        return orig;
    }
}
