package org.crosswire.jsword.view.swing.book;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.StringUtil;

/**
 * A (supposedly) generic panel to display and allow editing of bean properties.
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
public class BeanPanel extends JPanel
{
    /**
     * Simple ctor
     */
    public BeanPanel()
    {
        initialize();
    }

    /**
     * GUI init
     */
    private void initialize()
    {
        this.setLayout(new GridBagLayout());
    }

    /**
     * @param object
     */
    public void setBean(Object object) throws IntrospectionException
    {
        this.removeAll();

        int y = 0;
        if (object != null)
        {
            BeanInfo info = Introspector.getBeanInfo(object.getClass());
            PropertyDescriptor[] properties = info.getPropertyDescriptors();

            for (int i = 0; i < properties.length; i++)
            {
                PropertyDescriptor property = properties[i];

                if (!property.isHidden() && property.getWriteMethod() != null)
                {
                    JLabel label = new JLabel();
                    JTextField text = new JTextField();

                    String title = property.getDisplayName();
                    title = StringUtil.createTitle(title);
                    label.setText(title+":");
                    label.setLabelFor(text);

                    text.setColumns(10);

                    try
                    {
                        Method reader = property.getReadMethod();
                        Object reply = reader.invoke(object, null);
                        text.setText(reply.toString());
                    }
                    catch (Exception ex)
                    {
                        text.setText("Error: "+ex.getMessage());
                        text.setEditable(false);
                        
                        log.warn("property read failed", ex);
                    }

                    this.add(label, new GridBagConstraints(0, y, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 10, 2, 2), 0, 0));
                    this.add(text,  new GridBagConstraints(1, y, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 10), 0, 0));
                    y++;
                }
            }
        }
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(BeanPanel.class);
}
