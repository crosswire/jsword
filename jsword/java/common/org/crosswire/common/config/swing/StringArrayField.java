
package org.crosswire.common.config.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.Border;

import org.crosswire.common.config.Choice;
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class StringArrayField extends JPanel implements Field
{
    /**
     * Create a PropertyHashtableField for editing Hashtables.
     * @param sel_model The Choice to inform of our changes
     * @param superclass The type to check all new members against
     */
    public StringArrayField()
    {
        JPanel buttons = new JPanel(new FlowLayout());

        list.setFont(new Font("Monospaced", Font.PLAIN, 12));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // list.setPreferredScrollableViewportSize(new Dimension(30, 100));

        scroll.setViewportView(list);

        buttons.add(add);
        buttons.add(remove);
        buttons.add(update);

        // TODO: consider custom cell editors

        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) { addEntry(); }
        });
        remove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) { removeEntry(); }
        });
        update.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) { updateEntry(); }
        });

        Border title = BorderFactory.createTitledBorder("Component Editor");
        Border pad = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        setBorder(BorderFactory.createCompoundBorder(title, pad));

        setLayout(new BorderLayout());
        add("Center", scroll);
        add("South", buttons);
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
        return Convert.stringArray2String(getArray(), separator);
    }

    /**
     * Return the actual Hashtable being edited
     * @return The current value
     */
    public String[] getArray()
    {
        String[] retcode = new String[list_model.getSize()];
        for (int i=0; i<retcode.length; i++)
        {
            retcode[i] = (String) list_model.getElementAt(i);
        }

        return retcode;
    }

    /**
     * Set the current value using a string
     * @param value The new text
     */
    public void setValue(String value)
    {
        setArray(Convert.string2StringArray(value, separator));
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

    /**
     * Get the component for the JConfigure dialog.
     * In our case that is <code>this</code>
     * @return The editing Compoenent
     */
    public JComponent getComponent()
    {
        return this;
    }

    /**
     * Pop up a dialog to allow editing of a new value
     */
    public void addEntry()
    {
        InputPane input = new InputPane();

        // TODO: Initial focus ...
        if (JOptionPane.showConfirmDialog(this,
                                          input,
                                          "New Class",
                                          JOptionPane.OK_CANCEL_OPTION)
            == JOptionPane.OK_OPTION)
        {
            String new_name = input.name_field.getText();

            list_model.addElement(new_name);
        }
    }

    /**
     * Pop up a dialog to allow editing of a current value
     */
    public void updateEntry()
    {
        InputPane input = new InputPane();
        input.name_field.setText(currentValue());

        if (JOptionPane.showConfirmDialog(this,
                                          input,
                                          "Edit Class",
                                          JOptionPane.OK_CANCEL_OPTION)
            == JOptionPane.OK_OPTION)
        {
            String new_name = input.name_field.getText();

            list_model.removeElement(currentValue());
            list_model.addElement(new_name);
        }
    }

    /**
     * Delete the current value in the hashtable
     * TODO: do we need an "Are you sure?"
     */
    public void removeEntry()
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
        public InputPane()
        {
            super(new FieldLayout(10, 10));

            add(new JLabel("Name:"));
            add(name_field);

            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        }

        /** To edit a name (hashtable key) */
        JTextField name_field = new JTextField();
    }
    
    private String separator = "#";

    /** The TableModel that points the JTable at the Hashtable */
    private DefaultComboBoxModel list_model = new DefaultComboBoxModel();

    /** The Table - displays the Hashtble */
    private JList list = new JList(list_model);

    /** The Scroller for the JTable */
    private JScrollPane scroll = new JScrollPane();

    /** Button bar: add */
    private JButton add = new JButton("Add");

    /** Button bar: remove */
    private JButton remove = new JButton("Remove");

    /** Button bar: update */
    private JButton update = new JButton("Update");
}
