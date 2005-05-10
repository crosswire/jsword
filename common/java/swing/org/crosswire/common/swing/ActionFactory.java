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
 * ID: $ID$
 */
package org.crosswire.common.swing;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.KeyStroke;

import org.crosswire.common.util.CWClassLoader;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.StringUtil;

/**
 * The ActionFactory is responsible for creating CWActions
 * and making them available to the program. Each Action is
 * constructed from resources of the form: simplename.field=value
 * where simplename is the ACTION_COMMAND_KEY value and
 * field is one of the CWAction constants, e.g. LargeIcon.
 *
 * The values for the icons are a path which can be found as a resource.
 *
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 * @author Joe Walker [joe at eireneh dot com]
 */
public class ActionFactory implements ActionListener
{
    /**
     * Constructor that distinguishes between the object to call and the type
     * to look up resources against. This is useful for when you are writing
     * a class with subclasses but wish to keep the resources registered in
     * the name of the superclass.
     */
    public ActionFactory(Class type, Object bean)
    {
        actions = new HashMap();

        buildActionMap(type);

        this.bean = bean;
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent ev)
    {
        String action = ev.getActionCommand();

        assert action != null;
        assert action.length() != 0;

        // Instead of cascading if/then/else
        // use reflecton to do a direct lookup and call
        String methodName = METHOD_PREFIX + action;
        try
        {
            try
            {
                Method doMethod = bean.getClass().getDeclaredMethod(methodName, new Class[] { ActionEvent.class });
                doMethod.invoke(bean, new Object[] { ev });
            }
            catch (NoSuchMethodException ex)
            {
                Method doMethod = bean.getClass().getDeclaredMethod(methodName, new Class[0]);
                doMethod.invoke(bean, new Object[0]);
            }
        }
        catch (Exception ex)
        {
            log.error("Could not execute method " + bean.getClass().getName() + "." + methodName + "()", ex); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
    }

    /**
     * Get the Action for the given actionName.
     * @param key the internal name of the CWAction
     * @return CWAction null if it does not exist
     */
    public Action getAction(String key)
    {
        Action action = (CWAction) actions.get(key);

        if (action != null)
        {
            return action;
        }
        log.info("Missing key: '" + key + "'. Known keys are: " + StringUtil.join(actions.keySet().toArray(), ", ")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assert false;

        CWAction getOutOfJailFreeAction = new CWAction();

        getOutOfJailFreeAction.putValue(Action.NAME, key);
        getOutOfJailFreeAction.putValue(Action.SHORT_DESCRIPTION, MISSING_RESOURCE);
        getOutOfJailFreeAction.putValue(Action.LONG_DESCRIPTION, MISSING_RESOURCE);
        getOutOfJailFreeAction.setEnabled(true);
        getOutOfJailFreeAction.addActionListener(this);

        return getOutOfJailFreeAction;
    }

    /**
     * Construct a JLabel from the Action.
     * Only Action.NAME and Action.MNEMONIC_KEY are used.
     * @param key the internal name of the CWAction
     * @return A label, asserting if missing resources or with default values otherwise
     */
    public JLabel createJLabel(String key)
    {
        Action action = getAction(key);

        assert action != null : "Missing resource: " + key; //$NON-NLS-1$

        JLabel label = new JLabel();
        if (action != null)
        {
            label.setText(action.getValue(Action.NAME).toString());
            Integer mnemonic = (Integer) action.getValue(Action.MNEMONIC_KEY);
            if (mnemonic != null)
            {
                label.setDisplayedMnemonic(mnemonic.intValue());
            }
        }
        else
        {
            label.setText(key);
        }

        return label;
    }

    /**
     * Build the map of actions from resources
     */
    private void buildActionMap(Class basis)
    {
        try
        {
            ResourceBundle resources = ResourceBundle.getBundle(basis.getName(), Locale.getDefault(), new CWClassLoader(basis));

            Enumeration en = resources.getKeys();
            while (en.hasMoreElements())
            {
                String key = (String) en.nextElement();
                if (key.endsWith(TEST))
                {
                    String actionName = key.substring(0, key.length() - TEST.length());

                    String name = getActionString(resources, actionName, Action.NAME);

                    String shortDesc = getOptionalActionString(resources, actionName, Action.SHORT_DESCRIPTION);
                    String longDesc = getOptionalActionString(resources, actionName, Action.LONG_DESCRIPTION);
                    if (shortDesc == null && longDesc != null)
                    {
                        shortDesc = longDesc;
                    }
                    if (longDesc == null && shortDesc != null)
                    {
                        longDesc = shortDesc;
                    }

                    Icon smallIcon = getIcon(resources, actionName, Action.SMALL_ICON);
                    Icon largeIcon = getIcon(resources, actionName, CWAction.LARGE_ICON);
                    Integer mnemonic = getMnemonic(resources, actionName);
                    KeyStroke accelerator = getAccelerator(resources, actionName);

                    String enabledStr = getOptionalActionString(resources, actionName, "Enabled"); //$NON-NLS-1$
                    boolean enabled = enabledStr == null ? true : Boolean.valueOf(enabledStr).booleanValue();
                    createAction(actionName, name, shortDesc, longDesc, mnemonic, accelerator, smallIcon, largeIcon, enabled);
                }
            }
        }
        catch (MissingResourceException ex)
        {
            log.error("Missing resource for class: " + basis.getName()); //$NON-NLS-1$
            throw ex;
        }
    }

    /**
     * Lookup an action/field combination, warning about missing resources
     * rather than excepting.
     */
    private String getActionString(ResourceBundle resources, String actionName, String field)
    {
        try
        {
            return resources.getString(actionName + '.' + field);
        }
        catch (MissingResourceException ex)
        {
            log.info("Missing key for " + actionName, ex); //$NON-NLS-1$
            return null;
        }
    }

    /**
     * Lookup an action/field combination, returning null for missing resoruces.
     */
    private String getOptionalActionString(ResourceBundle resources, String actionName, String field)
    {
        try
        {
            return resources.getString(actionName + '.' + field);
        }
        catch (MissingResourceException ex)
        {
            return null;
        }
    }

    /**
     * Get an icon for the string
     */
    private Icon getIcon(ResourceBundle resources, String actionName, String iconName)
    {
        Icon icon = null;
        String iconStr = getOptionalActionString(resources, actionName, iconName);
        if (iconStr != null && iconStr.length() > 0)
        {
            icon = GuiUtil.getIcon(iconStr);
        }
        return icon;
    }

    /**
     * Convert the string to a mnemonic
     */
    private Integer getMnemonic(ResourceBundle resources, String actionName)
    {
        Integer mnemonic = null;
        String mnemonicStr = getOptionalActionString(resources, actionName, Action.MNEMONIC_KEY);
        if (mnemonicStr != null && mnemonicStr.length() > 0)
        {
            try
            {
                mnemonic = new Integer(getInteger(mnemonicStr));
            }
            catch (NumberFormatException ex)
            {
                log.warn("Could not parse integer for mnemonic of action " + actionName, ex); //$NON-NLS-1$
            }
        }
        return mnemonic;
    }

    /**
     * Convert the string to a valid Accelerator (ie a KeyStroke)
     */
    private KeyStroke getAccelerator(ResourceBundle resources, String actionName)
    {
        // Create the KeyStroke for the action's shortcut/accelerator
        KeyStroke accelerator = null;
        String acceleratorStr = getOptionalActionString(resources, actionName, Action.ACCELERATOR_KEY);
        if (acceleratorStr != null && acceleratorStr.length() > 0)
        {
            String[] modifiers = StringUtil.split(getActionString(resources, actionName, Action.ACCELERATOR_KEY + ".Modifiers"), ','); //$NON-NLS-1$

            try
            {
                int shortcut = getInteger(acceleratorStr);
                int keyModifier = getModifier(modifiers);

                // Now we can create it
                accelerator = KeyStroke.getKeyStroke(shortcut, keyModifier);
            }
            catch (NumberFormatException nfe)
            {
                log.warn("Could not parse integer for accelerator of action " + actionName, nfe); //$NON-NLS-1$
            }
        }
        return accelerator;
    }

    /**
     * Convert the string to an integer. The string is either a single character
     * or it is hex number.
     * @return the integer value of the accelerator
     */
    private int getInteger(String str) throws NumberFormatException
    {
        int val = 0;
        int length = str.length();
        if (str.startsWith("0x")) //$NON-NLS-1$
        {
            val = Integer.parseInt(str.substring(2), 16);
        }
        else if (length == 1)
        {
            val = str.charAt(0);
        }
        else
        {
            val = Integer.parseInt(str);
        }

        return val;
    }

    /**
     * 
     */
    private int getModifier(String[] modifiers)
    {
        int keyModifier = 0;
        for (int j = 0; j < modifiers.length; j++)
        {
            String modifier = modifiers[j];
            if ("ctrl".equalsIgnoreCase(modifier)) //$NON-NLS-1$
            {
                // use this so MacOS users are happy
                keyModifier |= Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
            }
            else if ("shift".equalsIgnoreCase(modifier)) //$NON-NLS-1$
            {
                keyModifier |= InputEvent.SHIFT_MASK;
            }
            else if ("alt".equalsIgnoreCase(modifier)) //$NON-NLS-1$
            {
                keyModifier |= InputEvent.ALT_MASK;
            }
        }

        return keyModifier;
    }

    /**
     * Create a new CWAction
     * @param acronymn The internal name for this action. Must not be null.
     * @param name The label for buttons, menu items, ...
     * @param small_icon The icon used in labelling
     * @param large_icon The icon to use if large icons are needed
     * @param short_desc Tooltip text
     * @param long_desc Context sensitive help
     * @param mnemonic The java.awt.event.EventKey value for the mnemonic
     * @param accel The accelerator key
     * @param enabled Whether the CWAction is enabled initially
     * @return CWAction
     */
    private CWAction createAction(String acronymn, String name, String short_desc,
                    String long_desc, Integer mnemonic, KeyStroke accel,
                    Icon small_icon, Icon large_icon, boolean enabled)
    {
        CWAction cwAction = new CWAction();

        if (acronymn == null || acronymn.length() == 0)
        {
            log.warn("Acronymn is missing for CWAction"); //$NON-NLS-1$
        }
        else
        {
            cwAction.putValue(Action.ACTION_COMMAND_KEY, acronymn);
        }

        if (name == null || name.length() == 0)
        {
            log.warn("Name is missing for CWAction"); //$NON-NLS-1$
            cwAction.putValue(Action.NAME, "?"); //$NON-NLS-1$
        }
        else
        {
            cwAction.putValue(Action.NAME, name);
        }

        cwAction.putValue(CWAction.LARGE_ICON, large_icon);
        cwAction.putValue(Action.SMALL_ICON, small_icon);
        cwAction.putValue(Action.SHORT_DESCRIPTION, short_desc);
        cwAction.putValue(Action.LONG_DESCRIPTION, long_desc);
        cwAction.putValue(Action.MNEMONIC_KEY, mnemonic);
        cwAction.putValue(Action.ACCELERATOR_KEY, accel);
        cwAction.setEnabled(enabled);

        cwAction.addActionListener(this);

        actions.put(acronymn, cwAction);

        return cwAction;
    }

    /**
     * The tooltip for actions that we generate to paper around missing resources
     * Normally we would assert, but in live we might want to limp on.
     */
    private static final String MISSING_RESOURCE = "Missing Resource"; //$NON-NLS-1$

    /**
     * The prefix to methods that we call
     */
    private static final String METHOD_PREFIX = "do"; //$NON-NLS-1$

    /**
     * What we lookup
     */
    private static final String SEPARATOR = "."; //$NON-NLS-1$

    /**
     * The test string to find actions
     */
    private static final String TEST = SEPARATOR + Action.NAME;

    /**
     * The object to which we forward events
     */
    private Object bean;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(ActionFactory.class);

    /**
     * The map of known CWActions
     */
    private Map actions;
}
