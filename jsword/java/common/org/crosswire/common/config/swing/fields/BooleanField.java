
package org.crosswire.common.config.swing.fields;

import java.awt.FlowLayout;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.crosswire.common.config.swing.Field;
import org.crosswire.common.util.Convert;

/**
* Allow the user to choose from True/False.
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
public class BooleanField extends JPanel implements Field
{
    /**
    * Give the values list (true/false) to the ComboBox
    */
    public BooleanField()
    {
        ButtonGroup group = new ButtonGroup();
        group.add(on);
        group.add(off);

        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        add(on);
        add(off);
    }

    /**
    * Some fields will need some extra info to display properly
    * like the options in an options field. FieldMap calls this
    * method with options provided by the choice.
    * @param param The options provided by the Choice
    */
    public void setOptions(Object param)
    {
    }

    /**
    * Return a string for use in the properties file
    * @return The current value
    */
    public String getValue()
    {
        return (String) Convert.boolean2String(on.isSelected());
    }

    /**
    * Set the current value
    * @param value The new text
    */
    public void setValue(String value)
    {
        on.setSelected(Convert.string2Boolean(value));
        off.setSelected(!Convert.string2Boolean(value));
    }

    /**
    * Get the actual component that we can add to a Panel.
    * (This can well be this in an implementation).
    */
    public JComponent getComponent()
    {
        return this;
    }

    /** The 'on' button */
    private JRadioButton on = new JRadioButton("Yes");

    /** The 'off' button */
    private JRadioButton off = new JRadioButton("No");
}
