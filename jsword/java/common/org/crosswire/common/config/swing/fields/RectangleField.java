
package org.crosswire.common.config.swing.fields;

import java.awt.GridLayout;
import java.util.StringTokenizer;
import javax.swing.*;
import javax.swing.border.*;

import org.crosswire.common.config.*;
import org.crosswire.common.config.swing.*;

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
public class RectangleField extends JPanel implements Field
{
    /**
    * Create a RectangleField
    */
    public RectangleField()
    {
        setBorder(new EtchedBorder());
        setLayout(new GridLayout(2, 4));

        add(new JLabel("Left: ", JLabel.RIGHT));
        add(left = new TextField());

        add(new JLabel("Top: ", JLabel.RIGHT));
        add(top = new TextField());

        add(new JLabel("Width: ", JLabel.RIGHT));
        add(width = new TextField());

        add(new JLabel("Height: ", JLabel.RIGHT));
        add(height = new TextField());
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
    * Return a string version of the current value
    * @return The current value
    */
    public String getValue()
    {
        return left.getText() + "," + top.getText() + "," + width.getText() + "," + height.getText();
    }

    /**
    * Set the current value
    * @param value The new text
    */
    public void setValue(String value)
    {
        StringTokenizer tokenizer = new StringTokenizer(value, ",", false);

        left.setText(tokenizer.nextToken());
        top.setText(tokenizer.nextToken());
        width.setText(tokenizer.nextToken());
        height.setText(tokenizer.nextToken());
    }

    /**
    * Get the actual component that we can add to a Panel.
    * (This can well be this in an implementation).
    */
    public JComponent getComponent()
    {
        return this;
    }

    /** The Left Position */
    private TextField left = null;

    /** The Top Position */
    private TextField top = null;

    /** The Width */
    private TextField width = null;

    /** The Height */
    private TextField height = null;
}
