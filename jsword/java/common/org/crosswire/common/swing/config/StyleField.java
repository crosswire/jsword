
package org.crosswire.common.swing.config;

import java.awt.FlowLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.crosswire.common.config.swing.Field;

/**
* A PropertyNumberField is a PropertyTextField that only
* stores numbers.
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
public class StyleField extends JPanel implements Field
{
    /**
    * Create a new FileField
    */
    public StyleField()
    {
        font = new FontButtonField();
        font.setOptions("Font");
        foreground = new ColorField();
        foreground.setOptions("Fore");
        background = new ColorField();
        background.setOptions("Back");

        match.setColumns(7);
        iconfile.setColumns(8);

        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        add(new JLabel("Match:  "));
        add(match);
        add(new JLabel("  Style:  "));
        add(font.getComponent());
        add(foreground.getComponent());
        add(background.getComponent());
        // add(iconfile);
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
        return match.getText()+":"+
               font.getValue()+":"+
               foreground.getValue()+":"+
               background.getValue()+":"+
               iconfile.getText();
    }

    /**
    * Set the current value
    * @param value The new text
    */
    public void setValue(String value)
    {
        if (value == null || value.equals(""))
        {
            match.setText("");
            font.setValue("");
            foreground.setValue("");
            background.setValue("");
            iconfile.setText("");
            return;
        }

        int c1 = value.indexOf(":", 0);
        int c2 = value.indexOf(":", c1+1);
        int c3 = value.indexOf(":", c2+1);
        int c4 = value.indexOf(":", c3+1);

        if (c4 == -1) throw new IllegalArgumentException(value);

        match.setText(value.substring(0, c1));
        font.setValue(value.substring(c1+1, c2));
        foreground.setValue(value.substring(c2+1, c3));
        background.setValue(value.substring(c3+1, c4));
        iconfile.setText(value.substring(c4+1));
    }

    /**
    * Get the actual component that we can add to a Panel.
    * (This can well be this in an implementation).
    */
    public JComponent getComponent()
    {
        return this;
    }

    /** The match string */
    private JTextField match = new JTextField();

    /** The font */
    private FontButtonField font;

    /** The foreground color */
    private ColorField foreground;

    /** The background color */
    private ColorField background;

    /** The icon */
    private JTextField iconfile = new JTextField();
}

