
package org.crosswire.common.swing.config;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;

import org.crosswire.common.config.swing.Field;
import org.crosswire.common.swing.FontChooser;
import org.crosswire.common.swing.GuiConvert;

/**
* A Rectangle Property viewer.
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
public class FontField extends FontChooser implements Field
{
    /**
    *
    */
    public FontField()
    {
        Border title = BorderFactory.createTitledBorder("Select Font");
        Border pad = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        setBorder(BorderFactory.createCompoundBorder(title, pad));
    }

    /**
    * This method does nothing because there is no configuring that this
    * class requires other than the current value.
    * @param obj The ignored paramter
    */
    public void setOptions(Object obj)
    {
    }

    /**
    * Return a string version of the current value
    * @return The current value
    */
    public String getValue()
    {
        return GuiConvert.font2String(getStyle());
    }

    /**
    * Set the current value
    * @param value The new text
    */
    public void setValue(String value)
    {
        setStyle(GuiConvert.string2Font(value));
    }

    /**
    * Get the actual component that we can add to a Panel.
    * (This can well be this in an implementation).
    */
    public JComponent getComponent()
    {
        return this;
    }
}

