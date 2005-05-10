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
import java.awt.Component;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import org.crosswire.common.swing.FormPane;

/**
 * Some static methods for using the Config package.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class TabbedConfigEditor extends AbstractConfigEditor
{
    /**
     * <br />Danger - this method is not called by the TreeConfigEditor
     * constructor, it is called by the AbstractConfigEditor constructor so
     * any field initializers will be called AFTER THIS METHOD EXECUTES
     * so don't use field initializers.
     */
    protected void initializeGUI()
    {
        JComponent bar = new ButtonPane(this);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        add(BorderLayout.SOUTH, bar);
    }

    /**
     * Update the tree structure
     */
    protected void updateTree()
    {
        if (tab != null)
        {
            remove(tab);
        }
        tab = new JTabbedPane();

        Iterator it = config.getPaths();
        while (it.hasNext())
        {
            String path = (String) it.next();
            // log.fine("TAB: path="+path);

            JTabbedPane nest = tab;
            StringTokenizer st = new StringTokenizer(path, "."); //$NON-NLS-1$
            while (st.hasMoreTokens())
            {
                String name = st.nextToken();
                int index = nest.indexOfTab(name);
                // log.fine("  name="+name+" index="+index+" hasMoreTokens="+st.hasMoreTokens());

                // We don't want to create a tab for the last branch
                if (st.hasMoreTokens())
                {
                    if (index == -1)
                    {
                        JTabbedPane sub = new JTabbedPane();
                        sub.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                        nest.addTab(name, TAB_ICON, sub);
                        // log.fine("  Added named tab");
                        nest = sub;
                    }
                    else
                    {
                        Component comp = nest.getComponentAt(index);
                        if (comp instanceof JTabbedPane)
                        {
                            nest = (JTabbedPane) comp;
                            // log.fine("  Drilling to tab");
                        }
                        else
                        {
                            // log.fine("  Downgrading and adding tab");
                            JTabbedPane sub = new JTabbedPane();
                            sub.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                            nest.setComponentAt(index, sub);
                            nest = sub;
                            nest.addTab(Msg.BASIC.toString(), TAB_ICON, comp);
                        }
                    }
                }
                else
                {
                    if (index == -1)
                    {
                        // log.fine("  Adding named panel");
                        FormPane card = (FormPane) decks.get(path);
                        if (card != null)
                        {
                            card.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                            nest.addTab(name, TAB_ICON, card);
                        }
                    }
                    else
                    {
                        // log.fine("  Adding Basic panel");
                        Component comp = nest.getComponentAt(index);
                        if (comp instanceof JTabbedPane)
                        {
                            nest = (JTabbedPane) comp;
                            FormPane card = (FormPane) decks.get(path);
                            if (card != null)
                            {
                                card.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                                nest.addTab(Msg.BASIC.toString(), TAB_ICON, card);
                            }
                        }
                        // else log.fine("  Warning skipping comp="+comp.getClass().getName());
                    }
                }
            }
        }

        add(BorderLayout.CENTER, tab);
    }

    /**
     * The tabbed pane
     */
    private JTabbedPane tab;

    /**
     * Set this to task_small to get icons on the tabs. I don't like it
     */
    protected static final ImageIcon TAB_ICON = null;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3256444715753878326L;
}
