package org.crosswire.common.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.crosswire.common.util.Reporter;
import org.jdom.Element;

/**
 * A class to convert between strings and objects of a type.
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class IntOptionsChoice extends AbstractReflectedChoice implements MultipleChoice
{
    /* (non-Javadoc)
     * @see org.crosswire.common.config.Choice#init(org.jdom.Element)
     */
    public void init(Element option) throws StartupException
    {
        super.init(option);

        List list = new ArrayList();
        List alts = option.getChildren("alternative"); //$NON-NLS-1$
        Iterator it = alts.iterator();
        while (it.hasNext())
        {
            Element alternative = (Element) it.next();
            int number = Integer.parseInt(alternative.getAttributeValue("number")); //$NON-NLS-1$
            String name = alternative.getAttributeValue("name"); //$NON-NLS-1$
            list.add(number, name);
        }

        options = (String[]) list.toArray(new String[0]);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.MultipleChoice#getOptions()
     */
    public String[] getOptions()
    {
        return options;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.Choice#getConvertionClass()
     */
    public Class getConvertionClass()
    {
        return Integer.TYPE;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.AbstractReflectedChoice#convertToString(java.lang.Object)
     */
    public String convertToString(Object orig)
    {
        return options[((Integer) orig).intValue()];
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.AbstractReflectedChoice#convertToObject(java.lang.String)
     */
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

    private String[] options = null;
}
