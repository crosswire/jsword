
package org.crosswire.common.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.crosswire.common.util.Logger;
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class IntOptionsChoice extends ReflectedChoice implements MultipleChoice
{
    public void init(Element option) throws StartupException
    {
        super.init(option);

        List list = new ArrayList();
        List alts = option.getChildren("alternative");
        Iterator it = alts.iterator();
        while (it.hasNext())
        {
            Element alternative = (Element) it.next();
            int number = Integer.parseInt(alternative.getAttributeValue("number"));
            String name = alternative.getAttributeValue("name");
            list.add(number, name);
        }

        options = (String[]) list.toArray(new String[0]);
    }

    public String[] getOptions()
    {
        return options;
    }

    public Class getConvertionClass()
    {
        return Integer.TYPE;
    }

    public String convertToString(Object orig)
    {
        return options[((Integer) orig).intValue()];
    }

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
                String option = (String) options[i];
                if (option.equalsIgnoreCase(orig))
                    return new Integer(i);
            }
        
            Reporter.informUser(this, "Ignoring invalid option: "+orig);
            return options[0];
        }
    }

    /**
     * The log stream
     */
    protected static Logger log = Logger.getLogger(IntOptionsChoice.class);

    private String[] options = null;
}
