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

import java.awt.Component;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Hashtable;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import org.crosswire.common.util.StringUtil;

/**
 * Various Menu creation utilities.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class MenuUtil
{
    /**
     * Create the menubar for the app.  By default this pulls the
     * definition of the menu from the associated resource file.
     */
    public static void setResourceBundle(ResourceBundle resource)
    {
        MenuUtil.resource = resource;
    }

    /**
     *
     */
    public static void addActions(Action[] actions)
    {
        for (int i=0; i<actions.length; i++)
        {
            commands.put(actions[i].getValue(Action.NAME), actions[i]);
        }
    }

    /**
     * Create the menubar for the app.  By default this pulls the
     * definition of the menu from the associated resource file.
     */
    public static JMenuBar createMenubar()
    {
        JMenuBar menubar = new JMenuBar();

        String[] menu_names = StringUtil.split(getResourceString("menubar")); //$NON-NLS-1$
        for (int i=0; i<menu_names.length; i++)
        {
            JMenu menu = createMenu(menu_names[i]);
            if (menu != null) menubar.add(menu);
        }
        return menubar;
    }

    /**
     * Create a menu for the app.  By default this pulls the
     * definition of the menu from the associated resource file.
     */
    public static JMenu createMenu(String name)
    {
        String[] item_names = StringUtil.split(getResourceString(name));
        JMenu menu = new JMenu(getResourceString(name+SUFFIX_LABEL));
        for (int i=0; i<item_names.length; i++)
        {
            if (item_names[i].equals("-")) //$NON-NLS-1$
            {
                menu.addSeparator();
            }
            else
            {
                JMenuItem menuitem = createMenuItem(item_names[i]);
                menu.add(menuitem);
            }
        }
        return menu;
    }

    /**
     * This is the hook through which all menu items are
     * created.  It registers the result with the menuitem
     * hashtable so that it can be fetched with getMenuItem().
     */
    protected static JMenuItem createMenuItem(String name)
    {
        JMenuItem menuitem = new JMenuItem(getResourceString(name+SUFFIX_LABEL));
        URL url = getResource(name+SUFFIX_IMAGE);
        if (url != null)
        {
            menuitem.setHorizontalTextPosition(SwingConstants.RIGHT);
            menuitem.setIcon(new ImageIcon(url));
        }
        String action_name = getResourceString(name+SUFFIX_ACTIOIN);
        if (action_name == null) action_name = name;
        menuitem.setActionCommand(action_name);
        Action action = getAction(action_name);
        if (action != null)
        {
            menuitem.addActionListener(action);
            action.addPropertyChangeListener(createActionChangeListener(menuitem));
            menuitem.setEnabled(action.isEnabled());
        }
        else
        {
            menuitem.setEnabled(false);
        }
        menuitems.put(name, menuitem);
        return menuitem;
    }
    
    /**
     *
     */
    protected static Action getAction(String cmd)
    {
        return (Action) commands.get(cmd);
    }

    /**
     * Fetch the menu item that was created for the given
     * command.
     * @param cmd  Name of the action.
     * @return item created for the given command or null
     *  if one wasn't created.
     */
    protected JMenuItem getMenuItem(String cmd)
    {
        return (JMenuItem) menuitems.get(cmd);
    }

    /**
     * Create the toolbar.  By default this reads the
     * resource file for the definition of the toolbar.
     */
    public static Component createToolbar()
    {
        JToolBar toolbar = new JToolBar();
        String[] toolKeys = StringUtil.split(getResourceString("toolbar")); //$NON-NLS-1$
        for (int i = 0; i < toolKeys.length; i++)
        {
            if (toolKeys[i].equals("-")) //$NON-NLS-1$
            {
                toolbar.add(Box.createHorizontalStrut(5));
            }
            else
            {
                toolbar.add(createTool(toolKeys[i]));
            }
        }
        toolbar.add(Box.createHorizontalGlue());
        return toolbar;
    }

    /**
     * Hook through which every toolbar item is created.
     */
    protected static Component createTool(String key)
    {
        return createToolbarButton(key);
    }

    /**
     * Create a button to go inside of the toolbar.  By default this
     * will load an image resource.  The image filename is relative to
     * the classpath (including the '.' directory if its a part of the
     * classpath), and may either be in a JAR file or a separate file.
     *
     * @param key The key in the resource file to serve as the basis
     *  of lookups.
     */
    protected static JButton createToolbarButton(String key)
    {
        URL url = getResource(key + SUFFIX_IMAGE);
        JButton button = new JButton(new ImageIcon(url)
        {
			public float getAlignmentY()
            {
                return 0.5f;
            }
            private static final long serialVersionUID = 3256726182224539701L;
        });
        button.setRequestFocusEnabled(false);
        button.setMargin(new Insets(1, 1, 1, 1));

        String astr = getResourceString(key + SUFFIX_ACTIOIN);
        if (astr == null) astr = key;
        Action a = getAction(astr);
        if (a != null)
        {
            button.setActionCommand(astr);
            button.addActionListener(a);
        }
        else
        {
            button.setEnabled(false);
        }

        String tip = getResourceString(key + SUFFIX_TIP);
        if (tip != null) button.setToolTipText(tip);

        return button;
    }

    /**
     * Yarked from JMenu, ideally this would be public.
     * see JMenu#createActionChangeListener(javax.swing.JMenuItem)
     */
    protected static PropertyChangeListener createActionChangeListener(JMenuItem b)
    {
        return new ActionChangedListener(b);
    }

    /**
     * Get a string from a resource bundle
     */
    protected static String getResourceString(String name)
    {
        try
        {
            return resource.getString(name);
        }
        catch (MissingResourceException ex)
        {
            return null;
        }
    }

    /**
     * Get a URL from a resource bundle
     */
    protected static URL getResource(String key)
    {
        String name = getResourceString(key);
        if (name == null) return null;

        return resource.getClass().getResource(name);
    }

    /**
     * Yarked from JMenu, ideally this would be public.
     * @see JMenu
     */
    private static class ActionChangedListener implements PropertyChangeListener
    {
        /**
         * Ctor
         */
        protected ActionChangedListener(JMenuItem mi)
        {
            super();
            this.menuItem = mi;
        }

        /* (non-Javadoc)
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        public void propertyChange(PropertyChangeEvent ev)
        {
            String propertyName = ev.getPropertyName();

            if (ev.getPropertyName().equals(Action.NAME))
            {
                String text = (String) ev.getNewValue();
                menuItem.setText(text);
            }
            else if (propertyName.equals("enabled")) //$NON-NLS-1$
            {
                Boolean enabled = (Boolean) ev.getNewValue();
                menuItem.setEnabled(enabled.booleanValue());
            }
        }

        private JMenuItem menuItem;
    }

    /**
     * Suffix applied to the key used in resource file
     * lookups for an image.
     */
    public static final String SUFFIX_IMAGE = "Image"; //$NON-NLS-1$

    /**
     * Suffix applied to the key used in resource file
     * lookups for a label.
     */
    public static final String SUFFIX_LABEL = "Label"; //$NON-NLS-1$

    /**
     * Suffix applied to the key used in resource file
     * lookups for an action.
     */
    public static final String SUFFIX_ACTIOIN = "Action"; //$NON-NLS-1$

    /**
     * Suffix applied to the key used in resource file
     * lookups for tooltip text.
     */
    public static final String SUFFIX_TIP = "Tooltip"; //$NON-NLS-1$

    /*
     * Data
     */
    private static Hashtable menuitems = new Hashtable();
    private static Hashtable commands = new Hashtable();
    private static ResourceBundle resource = null;
}
