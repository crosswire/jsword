
package org.crosswire.common.swing;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import org.crosswire.common.util.Level;
import org.crosswire.common.util.Logger;

/**
* BeanInfo for the TextViewer. This was mostly generate using
* BeansExpress.
*
* <table border='1' cellPadding='3' cellSpacing='0' width="100%">
* <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
* Distribution Licence:<br />
* Project B is free software; you can redistribute it
* and/or modify it under the terms of the GNU General Public License,
* version 2 as published by the Free Software Foundation.<br />
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* General Public License for more details.<br />
* The License is available on the internet
* <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
* <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
* MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
* The copyright to this program is held by it's authors.
* </font></td></tr></table>
* @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
* @see <{docs.Licence}>
* @author Joe Walker
*/
public class TextViewPanelBeanInfo extends SimpleBeanInfo
{
    /**
    * Info about the extra properties we provide
    * @return an array of property descriptors
    */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        try
        {
            // The header property
            PropertyDescriptor header = new PropertyDescriptor("header",
                                                               TextViewPanel.class,
                                                               "getHeader",
                                                               "setHeader");
            header.setDisplayName("Header");
            header.setShortDescription("Header");
            header.setBound(true);

            // The main text property
            PropertyDescriptor text = new PropertyDescriptor("text",
                                                             TextViewPanel.class,
                                                             "getText",
                                                             "setText");
            text.setDisplayName("Text");
            text.setShortDescription("Text");
            text.setBound(true);

            return new PropertyDescriptor[] { header, text, };
        }
        catch (IntrospectionException ex)
        {
            log.log(Level.INFO, "Failure", ex);
            return null;
        }
    }

    /**
    * Get additional information from the superclass, in this case JPanel
    */
    public BeanInfo[] getAdditionalBeanInfo()
    {
        Class superclass = TextViewPanel.class.getSuperclass();
        try
        {
            BeanInfo superBeanInfo = Introspector.getBeanInfo(superclass);
            return new BeanInfo[] { superBeanInfo };
        }
        catch (IntrospectionException ex)
        {
            log.log(Level.INFO, "Failure", ex);
            return null;
        }
    }

    /** The log stream */
    protected static Logger log = Logger.getLogger("util.swing");
}
