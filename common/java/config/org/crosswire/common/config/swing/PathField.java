/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;

import org.crosswire.common.config.Choice;
import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.util.Convert;

/**
 * A StringArrayField allows editing of an array of Strings in a JList.
 * It allows the user to specify additional classes that extend the
 * functionality of the program.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class PathField extends JPanel implements Field
{
    /**
     * Create a PropertyHashtableField for editing String arrays.
     */
    public PathField()
    {
        model = new DefaultComboBoxModel();
        list = new JList(model);

        actions = new ActionFactory(PathField.class, this);

        JPanel buttons = new JPanel(new FlowLayout());

        list.setFont(new Font("Monospaced", Font.PLAIN, 12)); //$NON-NLS-1$
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // list.setPreferredScrollableViewportSize(new Dimension(30, 100));

        JScrollPane scroll = new JScrollPane();
        scroll.setViewportView(list);

        buttons.add(new JButton(actions.getAction(ADD)));
        buttons.add(new JButton(actions.getAction(REMOVE)));
        buttons.add(new JButton(actions.getAction(UPDATE)));

        Border title = BorderFactory.createTitledBorder(Msg.PATH_EDITOR.toString());
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
        return Convert.stringArray2String(getArray(), File.pathSeparator);
    }

    /**
     * Return the actual Hashtable being edited
     * @return The current value
     */
    public String[] getArray()
    {
        String[] retcode = new String[model.getSize()];
        for (int i = 0; i < retcode.length; i++)
        {
            retcode[i] = (String) model.getElementAt(i);
        }

        return retcode;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.swing.Field#setValue(java.lang.String)
     */
    public void setValue(String value)
    {
        setArray(Convert.string2StringArray(value, File.pathSeparator));
    }

    /**
     * Set the current value using a hashtable
     * @param value The new text
     */
    public void setArray(String[] value)
    {
        model = new DefaultComboBoxModel(value);
        list.setModel(model);
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
    public void doAddPathEntry()
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            String path = chooser.getSelectedFile().getPath();
            model.addElement(path);
        }
    }

    /**
     * Pop up a dialog to allow editing of a current value
     */
    public void doUpdatePathEntry()
    {
        JFileChooser chooser = new JFileChooser(currentValue());
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            String path = chooser.getSelectedFile().getPath();

            model.removeElement(currentValue());
            model.addElement(path);
        }
    }

    /**
     * Delete the current value in the hashtable
     */
    public void doRemovePathEntry()
    {
        model.removeElement(currentValue());
    }

    /**
     * What is the currently selected value?
     * @return The currently selected value
     */
    private final String currentValue()
    {
        return (String) model.getElementAt(list.getSelectedIndex());
    }

    private static final String ADD = "AddPathEntry"; //$NON-NLS-1$
    private static final String REMOVE = "RemovePathEntry"; //$NON-NLS-1$
    private static final String UPDATE = "UpdatePathEntry"; //$NON-NLS-1$

    private transient ActionFactory actions;

    /**
     * The TableModel that points the JTable at the Hashtable
     */
    private DefaultComboBoxModel model;

    /**
     * The Table - displays the Hashtble
     */
    private JList list;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3256443607736072242L;
}
