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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.crosswire.common.util.Reporter;
import org.jdom.Element;

/**
 * A class to convert between strings and objects of a type.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class IntOptionsChoice extends AbstractReflectedChoice implements MultipleChoice
{
    /* (non-Javadoc)
     * @see org.crosswire.common.config.Choice#init(org.jdom.Element)
     */
    /* @Override */
    public void init(Element option, ResourceBundle configResources) throws StartupException
    {
        assert configResources != null;

        super.init(option, configResources);

        String prefix = option.getAttributeValue("key") + ".alternative."; //$NON-NLS-1$  //$NON-NLS-2$

        List list = new ArrayList();
        Iterator iter = option.getChildren("alternative").iterator(); //$NON-NLS-1$
        while (iter.hasNext())
        {
            Element alternative = (Element) iter.next();
            int number = Integer.parseInt(alternative.getAttributeValue("number")); //$NON-NLS-1$
            String name = configResources.getString(prefix + number);
            list.add(number, name);
        }

        options = (String[]) list.toArray(new String[list.size()]);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.MultipleChoice#getOptions()
     */
    public String[] getOptions()
    {
        String [] copy = new String[options.length];
        System.arraycopy(options, 0, copy, 0, options.length);
        return copy;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.Choice#getConvertionClass()
     */
    public Class getConversionClass()
    {
        return Integer.TYPE;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.AbstractReflectedChoice#convertToString(java.lang.Object)
     */
    /* @Override */
    public String convertToString(Object orig)
    {
        return options[((Integer) orig).intValue()];
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.AbstractReflectedChoice#convertToObject(java.lang.String)
     */
    /* @Override */
    public Object convertToObject(String orig)
    {
        // First check to see if this is a number
        try
        {
            return new Integer(orig);
        }
        catch (NumberFormatException ex)
        {
            // Then work on the name list
            for (int i = 0; i < options.length; i++)
            {
                if (options[i].equalsIgnoreCase(orig))
                {
                    return new Integer(i);
                }
            }

            Reporter.informUser(this, Msg.IGNORE, new Object[] { orig });
            return options[0];
        }
    }

    private String[] options;
}
