/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.common.config.swing;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.crosswire.common.config.Choice;
import org.crosswire.common.config.MultipleChoice;
import org.crosswire.common.util.Logger;

/**
 * Allow the user to choose from True/False.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class OptionsField implements Field
{
    /**
     * Some fields will need some extra info to display properly
     * like the options in an options field. FieldMap calls this
     * method with options provided by the choice.
     * @param param The options provided by the Choice
     */
    public void setChoice(Choice param)
    {
        if (param instanceof MultipleChoice)
        {
            MultipleChoice mc = (MultipleChoice) param;
            list = mc.getOptions();

            if (list == null)
            {
                throw new IllegalArgumentException("getOptions() returns null for option with help text " + mc.getHelpText()); //$NON-NLS-1$
            }
        }
        else
        {
            log.warn("Unknown Choice type: " + param.getClass().getName()); //$NON-NLS-1$
            list = new String[] { Msg.ERROR.toString() };
        }

        combo.setModel(new DefaultComboBoxModel(list));
    }

    /**
     * Return a string for use in the properties file
     * @return The current value
     */
    public String getValue()
    {
        String reply = (String) combo.getSelectedItem();

        if (reply == null)
        {
            reply = ""; //$NON-NLS-1$
        }

        return reply;
    }

    /**
     * Set the current value
     * @param value The new text
     */
    public void setValue(String value)
    {
        for (int i = 0; i < list.length; i++)
        {
            if (value.equals(list[i]))
            {
                combo.setSelectedItem(list[i]);
                return;
            }
        }

        if (list != null && list.length > 0)
        {
            combo.setSelectedItem(list[0]);
        }

        log.warn("Checked for options without finding: '" + value + "'. Defaulting to first option: " + combo.getSelectedItem());  //$NON-NLS-1$//$NON-NLS-2$
    }

    /**
     * Get the actual component that we can add to a Panel.
     * (This can well be this in an implementation).
     */
    public JComponent getComponent()
    {
        return combo;
    }

    /**
     * The component that we are wrapping in a field
     */
    private JComboBox combo = new JComboBox(new String[] { Msg.NO_OPTIONS.toString() });

    /**
     * The options
     */
    private String[] list;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(OptionsField.class);
}
