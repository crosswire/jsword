package org.crosswire.common.config.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;

import org.crosswire.common.config.Choice;
import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.swing.FieldLayout;
import org.crosswire.common.util.Convert;

/**
 * A StringArrayField allows editing of an array of Strings in a JList.
 * It allows the user to specify additional classes that extend the
 * functionality of the program.
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
public class StringArrayField extends JPanel implements Field
{
    /**
     * Create a PropertyHashtableField for editing String arrays.
     */
    public StringArrayField()
    {
        actions = new ActionFactory(StringArrayField.class, this);

        list_model = new DefaultComboBoxModel();
        list = new JList(list_model);

        JPanel buttons = new JPanel(new FlowLayout());

        list.setFont(new Font("Monospaced", Font.PLAIN, 12)); //$NON-NLS-1$
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // list.setPreferredScrollableViewportSize(new Dimension(30, 100));

        JScrollPane scroll = new JScrollPane();
        scroll.setViewportView(list);

        buttons.add(new JButton(actions.getAction(ADD)));
        buttons.add(new JButton(actions.getAction(REMOVE)));
        buttons.add(new JButton(actions.getAction(UPDATE)));

        Border title = BorderFactory.createTitledBorder(Msg.COMPONENT_EDITOR.toString());
        Border pad = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        setBorder(BorderFactory.createCompoundBorder(title, pad));

        setLayout(new BorderLayout());
        add(scroll, BorderLayout.CENTER);
        add(buttons, BorderLayout.PAGE_END);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.swing.Field#setChoice(org.crosswire.common.config.Choice)
     */
    public void setChoice(Choice param)
    {
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.swing.Field#getValue()
     */
    public String getValue()
    {
        return Convert.stringArray2String(getArray(), SEPARATOR);
    }

    /**
     * Return the actual Hashtable being edited
     * @return The current value
     */
    public String[] getArray()
    {
        String[] retcode = new String[list_model.getSize()];
        for (int i = 0; i < retcode.length; i++)
        {
            retcode[i] = (String) list_model.getElementAt(i);
        }

        return retcode;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.swing.Field#setValue(java.lang.String)
     */
    public void setValue(String value)
    {
        setArray(Convert.string2StringArray(value, SEPARATOR));
    }

    /**
     * Set the current value using a hashtable
     * @param value The new text
     */
    public void setArray(String[] value)
    {
        list_model = new DefaultComboBoxModel(value);
        list.setModel(list_model);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.swing.Field#getComponent()
     */
    public JComponent getComponent()
    {
        return this;
    }

    /**
     * Pop up a dialog to allow editing of a new value
     */
    public void doAddEntry()
    {
        InputPane input = new InputPane();

        if (JOptionPane.showConfirmDialog(this, input, Msg.NEW_CLASS.toString(), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
        {
            String new_name = input.name_field.getText();

            list_model.addElement(new_name);
        }
    }

    /**
     * Pop up a dialog to allow editing of a current value
     */
    public void doUpdateEntry()
    {
        InputPane input = new InputPane();
        input.name_field.setText(currentValue());

        if (JOptionPane.showConfirmDialog(this, input, Msg.EDIT_CLASS.toString(), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
        {
            String new_name = input.name_field.getText();

            list_model.removeElement(currentValue());
            list_model.addElement(new_name);
        }
    }

    /**
     * Delete the current value in the hashtable
     */
    public void doRemoveEntry()
    {
        list_model.removeElement(currentValue());
    }

    /**
     * What is the currently selected value?
     * @return The currently selected value
     */
    private final String currentValue()
    {
        return (String) list_model.getElementAt(list.getSelectedIndex());
    }

    /**
     * The panel for a JOptionPane that allows editing a name/class
     * combination.
     */
    public static class InputPane extends JPanel
    {
        /**
         * Simple ctor
         */
        public InputPane()
        {
            super(new FieldLayout(10, 10));

            add(new JLabel(Msg.NAME.toString() + ':')); //$NON-NLS-1$
            add(name_field);

            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        }

        /**
         * To edit a name (hashtable key)
         */
        protected JTextField name_field = new JTextField();
    }

    private static final String ADD = "AddStringEntry"; //$NON-NLS-1$
    private static final String REMOVE = "RemoveStringEntry"; //$NON-NLS-1$
    private static final String UPDATE = "UpdateStringEntry"; //$NON-NLS-1$

    /**
     * What character do we use to separate strings?
     */
    private static final String SEPARATOR = "#"; //$NON-NLS-1$

    private ActionFactory actions;

    /**
     * The TableModel that points the JTable at the Hashtable
     */
    private DefaultComboBoxModel list_model;

    /**
     * The Table - displays the Hashtble
     */
    private JList list;

    /**
     * SERIALUID(dm): A placeholder for the ultimate version id.
     */
    private static final long serialVersionUID = 1L;
}
