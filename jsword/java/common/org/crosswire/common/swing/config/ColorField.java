
package org.crosswire.common.swing.config;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.crosswire.common.config.swing.Field;
import org.crosswire.common.swing.GuiConvert;

/**
* A Filename selection.
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
public class ColorField extends JPanel implements Field
{
    /**
    * Create a new FileField
    */
    public ColorField()
    {
        edit.setText(name);
        edit.setIcon(new CustomIcon());
        edit.setMargin(new Insets(1, 2, 1, 1));
        edit.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ex)
            {
                color = JColorChooser.showDialog(ColorField.this, name, color);
                //text.setText(Convert.color2String(color));
                edit.repaint();
            }
        });

        setLayout(new BorderLayout());
        add(edit, BorderLayout.WEST);
        //add(text, BorderLayout.EAST);
    }

    /**
    * This method does nothing because there is no configuring that this
    * class requires other than the current value.
    * @param obj The ignored paramter
    */
    public void setOptions(Object obj)
    {
        this.name = (String) obj;
    }

    /**
    * Return a string version of the current value
    * @return The current value
    */
    public String getValue()
    {
        return GuiConvert.color2String(color);
    }

    /**
    * Set the current value
    * @param value The new text
    */
    public void setValue(String value)
    {
        color = GuiConvert.string2Color(value);
        //text.setText(value);
        edit.repaint();
    }

    /**
    * Get the actual component that we can add to a Panel.
    * (This can well be this in an implementation).
    */
    public JComponent getComponent()
    {
        return this;
    }

    /** The name of the Color selection */
    protected String name = "Edit";

    /** What to do when we are clicked */
    private Runnable runner;

    /** The browse button */
    protected JButton edit = new JButton(name);

    /** Some feedback on the color */
    //private JLabel text = new JLabel();

    /** The current Color */
    protected Color color = Color.white;

    /** The icon square size */
    private static final int SIZE = 16;

    /**
    * The CustomIcon that shows the selected color
    */
    class CustomIcon implements Icon
    {
        /**
        * Returns the icon's height.
        */
        public int getIconHeight()
        {
            return SIZE;
        }

        /**
        * Returns the icon's width.
        */
        public int getIconWidth()
        {
            return SIZE;
        }

        /**
        * Draw the icon at the specified location.
        */
        public void paintIcon(Component c, Graphics g, int x, int y)
        {
            if (color == null)
            {
                g.setColor(Color.black);
                g.drawRect(x, y, SIZE, SIZE);
                g.drawLine(x, y, x+SIZE, y+SIZE);
                g.drawLine(x+SIZE, y, x, y+SIZE);
            }
            else
            {
                g.setColor(color);
                g.fillRect(x, y, SIZE, SIZE);
                g.setColor(Color.black);
                g.drawRect(x, y, SIZE, SIZE);
            }
        }
    }
}

