package org.crosswire.common.config.swing;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.crosswire.common.config.Choice;
import org.crosswire.common.swing.NumericDocument;

/**
 * A PropertyNumberField is a PropertyTextField that only
 * stores numbers.
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
public class NumberField extends JPanel implements Field
{
	/**
     * Create a new FileField
     */
    public NumberField()
    {
        text.setDocument(new NumericDocument());
        text.setColumns(10);

        setLayout(new BorderLayout(10, 0));
        add(BorderLayout.WEST, text);
    }

    /**
     * Some fields will need some extra info to display properly
     * like the options in an options field. FieldMap calls this
     * method with options provided by the choice.
     * @param param The options provided by the Choice
     */
    public void setChoice(Choice param)
    {
    }

    /**
     * Return a string version of the current value
     * @return The current value
     */
    public String getValue()
    {
        return text.getText();
    }

    /**
     * Set the current value
     * @param value The new text
     */
    public void setValue(String value)
    {
        text.setText(value);
    }

    /**
     * Get the actual component that we can add to a Panel.
     * (This can well be this in an implementation).
     */
    public JComponent getComponent()
    {
        return this;
    }

    /**
     * The text field
     */
    private JTextField text = new JTextField();

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3256443594867750451L;
}
