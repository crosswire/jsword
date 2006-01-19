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

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.crosswire.common.config.Choice;
import org.crosswire.common.swing.ActionFactory;

/**
 * A Filename selection.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class FileField extends JPanel implements Field
{
    /**
     * Create a new FileField
     */
    public FileField()
    {
        ActionFactory actions = new ActionFactory(FileField.class, this);

        text = new JTextField();

        setLayout(new BorderLayout(10, 0));
        add(text, BorderLayout.CENTER);
        add(new JButton(actions.getAction(BROWSE)), BorderLayout.LINE_END);
    }

    /**
     * Open a browse dialog
     */
    public void doBrowse()
    {
        JFileChooser chooser = new JFileChooser(text.getText());
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (chooser.showOpenDialog(FileField.this) == JFileChooser.APPROVE_OPTION)
        {
            text.setText(chooser.getSelectedFile().getPath());
        }
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
        return text.getText();
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.swing.Field#setValue(java.lang.String)
     */
    public void setValue(String value)
    {
        text.setText(value);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.swing.Field#getComponent()
     */
    public JComponent getComponent()
    {
        return this;
    }

    private static final String BROWSE = "Browse"; //$NON-NLS-1$

    /**
     * The text field
     */
    protected JTextField text;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3258416148742484276L;
}
