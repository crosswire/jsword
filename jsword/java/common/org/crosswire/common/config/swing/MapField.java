
package org.crosswire.common.config.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.crosswire.common.config.Choice;
import org.crosswire.common.swing.ExceptionPane;
import org.crosswire.common.swing.FieldLayout;
import org.crosswire.common.swing.MapTableModel;
import org.crosswire.common.util.Convert;

/**
 * A MapField allows editing of a Hashtable in a JTable.
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
public class MapField extends JPanel implements Field
{
    /**
     * Create a PropertyHashtableField for editing Hashtables.
     * @param sel_model The Choice to inform of our changes
     * @param superclass The type to check all new members against
     */
    public MapField()
    {
        JPanel buttons = new JPanel(new FlowLayout());

        table.setFont(new Font("Monospaced", Font.PLAIN, 12));
        table.setPreferredScrollableViewportSize(new Dimension(30, 100));
        table.setColumnSelectionAllowed(false);

        scroll.setViewportView(table);

        buttons.add(add);
        buttons.add(remove);
        buttons.add(update);

        // PENDING(joe): consider custom cell editors

        add.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                addEntry();
            }
        });

        remove.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                removeEntry();
            }
        });

        update.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                updateEntry();
            }
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
        //superclass = ((MapChoice) param).getSuperClass();
    }

    /**
     * Return a string version of the current value
     * @return The current value
     */
    public String getValue()
    {
        return Convert.hashtable2String(getMap());
    }

    /**
     * Return the actual Hashtable being edited
     * @return The current value
     */
    public Map getMap()
    {
        return table_model.getMap();
    }

    /**
     * Set the current value using a string
     * @param value The new text
     */
    public void setValue(String value)
    {
        setMap(Convert.string2Hashtable(value, superclass));
    }

    /**
     * Set the current value using a hashtable
     * @param value The new text
     */
    public void setMap(Map value)
    {
        table_model.setMap(value);
        table.setModel(table_model);
        table.getColumnModel().getColumn(0).setWidth(15);
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
     * PENDING(joe): Make this work
     */
    public void registerComboEditor(OptionsField field)
    {
    }

    /**
     * Pop up a dialog to allow editing of a new value
     */
    public void addEntry()
    {
        InputPane input = new InputPane();

        // PENDING(joe): Initial focus ...
        if (JOptionPane.showConfirmDialog(this, input, "New Class", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
        {
            String new_class = input.class_field.getText();
            String new_name = input.name_field.getText();

            if (isValid(new_class))
            {
                table_model.put(new_name, new_class);
            }
        }
    }

    /**
     * Pop up a dialog to allow editing of a current value
     */
    public void updateEntry()
    {
        InputPane input = new InputPane();
        input.name_field.setText(currentKey());
        input.class_field.setText(currentValue());

        if (JOptionPane.showConfirmDialog(this, input, "Edit Class", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
        {
            String new_class = input.class_field.getText();
            String new_name = input.name_field.getText();

            if (isValid(new_class))
            {
                table_model.remove(currentKey());
                table_model.put(new_name, new_class);
            }
        }
    }

    /**
     * Delete the current value in the hashtable
     * PENDING(joe): do we need an "Are you sure?"
     */
    public void removeEntry()
    {
        table_model.remove(currentKey());
    }

    /**
     * Create an instance of a class for the hashtable
     * @param name The name of the class to create
     * @return The instansiated object or null if the name is not valid
     */
    public boolean isValid(String name)
    {
        try
        {
            Class clazz = Class.forName(name);

            if (!superclass.isAssignableFrom(clazz))
                throw new ClassCastException("The class '"+name+"' does not inherit from '"+superclass+"'. Instansiation failed.");

            return true;
        }
        catch (ClassNotFoundException ex)
        {
            JOptionPane.showMessageDialog(this, "A class named '"+name+"' could not be found.");
        }
        catch (Exception ex)
        {
            ExceptionPane.showExceptionDialog(this, ex);
        }

        return false;
    }

    /**
     * What is the currently selected key?
     * @return The currently selected key
     */
    private final String currentKey()
    {
        return (String) table_model.getValueAt(table.getSelectedRow(), 0);
    }

    /**
     * What is the currently selected value?
     * @return The currently selected value
     */
    private final String currentValue()
    {
        return (String) table_model.getValueAt(table.getSelectedRow(), 1);
    }

    /**
     * A HashtableModel with named columns that is not ediatble
     */
    public class NamedMapTableModel extends MapTableModel
    {
        public NamedMapTableModel()
        {
            super(new Hashtable());
        }

        public String getColumnName(int col)
        {
            return (col == 0) ? "Name" : "Class";
        }

        public boolean isEditable(int col)
        {
            return false;
        }
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
            add(new JLabel("Class:"));
            add(class_field);

            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        }

        /** To edit a name (hashtable key) */
        JTextField name_field = new JTextField();

        /** To edit a class (hashtable value) */
        JTextField class_field = new JTextField(20);
    }

    /** The TableModel that points the JTable at the Hashtable */
    private NamedMapTableModel table_model = new NamedMapTableModel();

    /** The Table - displays the Hashtble */
    private JTable table = new JTable(table_model);

    /** The Scroller for the JTable */
    private JScrollPane scroll = new JScrollPane();

    /** Button bar: add */
    private JButton add = new JButton("Add");

    /** Button bar: remove */
    private JButton remove = new JButton("Remove");

    /** Button bar: update */
    private JButton update = new JButton("Update");

    /** The class that everything must inherit from */
    private Class superclass;
}
