package org.crosswire.common.swing;

import java.awt.Component;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A Panel customized to hold fields.
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
public class FormPane extends JPanel
{
    private static final String SUFFIX_COMP = "_comp"; //$NON-NLS-1$
    private static final String SUFFIX_LABEL = "_label"; //$NON-NLS-1$

    /**
     * Create a FormPane
     */
    public FormPane()
    {
        setLayout(new FieldLayout(15, 20));
        setBorder(BorderFactory.createEmptyBorder());
    }

    /**
     * Add a field to this panel
     * @param prompt The name for the field
     * @param comp The component to add alongside the label
     */
    public void addEntry(String prompt, Component comp)
    {
        JLabel label = new JLabel(prompt);

        add(label);
        add(comp);

        comps.put(prompt + SUFFIX_LABEL, label);
        comps.put(prompt + SUFFIX_COMP, comp);
    }

    /**
     * Add a field to this panel
     * @param prompt The name for the field
     */
    public void removeEntry(String prompt)
    {
        JLabel label = (JLabel) comps.get(prompt + SUFFIX_LABEL);
        Component comp = (Component) comps.get(prompt + SUFFIX_COMP);

        remove(label);
        remove(comp);

        comps.remove(prompt + SUFFIX_LABEL);
        comps.remove(prompt + SUFFIX_COMP);
    }

    /**
     * Is this panel empty
     */
    public boolean isEmpty()
    {
        return comps.size() == 0;
    }

    /**
     * Get a list of the labels
     */
    public String[] getFieldNames()
    {
        int count = getComponentCount() / 2;
        String[] list = new String[count];
        JLabel label;
        for (int i = 0; i < count; i++)
        {
            label = (JLabel) getComponent(i * 2);
            list[i] = label.getText();
        }

        return list;
    }

    /**
     * Get at list of the values in the fields
     */
    public String[] getFieldValues()
    {
        int count = getComponentCount() / 2;
        String[] list = new String[count];

        for (int i = 0; i < count; i++)
        {
            Component comp = getComponent(i * 2 + 1);
            list[i] = GuiUtil.getText(comp);
        }

        return list;
    }

    /**
     * A store of the available components
     */
    protected Hashtable comps = new Hashtable();
}
