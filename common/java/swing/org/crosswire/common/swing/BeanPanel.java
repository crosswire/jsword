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
package org.crosswire.common.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.StringUtil;

/**
 * A (supposedly) generic panel to display and allow editing of bean properties.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class BeanPanel extends JPanel
{
    /**
     * Simple ctor
     */
    public BeanPanel()
    {
        initialize();
    }

    /**
     * GUI init
     */
    private void initialize()
    {
        this.setLayout(new GridBagLayout());
    }

    /**
     * @param abean The new bean to introspect and edit
     */
    public void setBean(Object abean) throws IntrospectionException
    {
        this.bean = abean;

        removeAll();
        editors.clear();

        int y = 0;
        if (bean != null)
        {
            BeanInfo info = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] properties = info.getPropertyDescriptors();

            for (int i = 0; i < properties.length; i++)
            {
                PropertyDescriptor property = properties[i];

                if (!property.isHidden() && property.getWriteMethod() != null)
                {
                    JLabel label = new JLabel();
                    JTextField text = new JTextField();

                    String title = property.getDisplayName();
                    title = StringUtil.createTitle(title);
                    label.setText(title + ":"); //$NON-NLS-1$
                    label.setLabelFor(text);

                    Method writer = property.getWriteMethod();
                    text.getDocument().addDocumentListener(new CustomDocumentListener(text, writer));

                    try
                    {
                        Method reader = property.getReadMethod();
                        Object reply = reader.invoke(bean, null);
                        if (reply == null)
                        {
                            text.setText(""); //$NON-NLS-1$
                        }
                        else
                        {
                            text.setText(reply.toString());
                        }
                    }
                    catch (Exception ex)
                    {
                        text.setText(Msg.ERROR_READING.toString(ex.getMessage()));
                        log.warn("property read failed", ex); //$NON-NLS-1$
                    }

                    editors.add(text);

                    this.add(label, new GridBagConstraints(0, y, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 10, 2, 2), 0, 0));
                    this.add(text,  new GridBagConstraints(1, y, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 10), 0, 0));
                    y++;
                }
            }
        }

        GuiUtil.refresh(this);
    }

    /**
     * Accessor for the current bean
     */
    public Object getBean()
    {
        return bean;
    }

    /**
     * Should we allow our fields to be edited?
     */
    public void setEditable(boolean editable)
    {
        for (Iterator it = editors.iterator(); it.hasNext(); )
        {
            JTextField text = (JTextField) it.next();
            text.setEditable(editable);
        }
    }

    /**
     * The bean we are editing
     */
    protected Object bean;

    /**
     * A list of the current editors
     */
    private List editors = new ArrayList();

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(BeanPanel.class);

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3834589894202175795L;

    /**
     * Document Listener that updates the original bean
     */
    private final class CustomDocumentListener implements DocumentListener
    {
        /**
         * Simple ctor
         */
        protected CustomDocumentListener(JTextField text, Method writer)
        {
            this.text = text;
            this.writer = writer;
        }

        /* (non-Javadoc)
         * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
         */
        public void changedUpdate(DocumentEvent ev)
        {
            try
            {
                String data = text.getText();
                writer.invoke(bean, new Object[] { data });
            }
            catch (Exception ex)
            {
                log.error("Introspected set failed", ex); //$NON-NLS-1$
            }
        }

        /* (non-Javadoc)
         * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
         */
        public void insertUpdate(DocumentEvent ev)
        {
            changedUpdate(ev);
        }

        /* (non-Javadoc)
         * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
         */
        public void removeUpdate(DocumentEvent ev)
        {
            changedUpdate(ev);
        }

        /**
         * The text field that we are monitoring
         */
        private final JTextField text;

        /**
         * The method of updating the original bean
         */
        private final Method writer;
    }
}
