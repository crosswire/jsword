package org.crosswire.common.swing;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;
import javax.swing.event.EventListenerList;

import org.apache.commons.lang.StringUtils;
import org.crosswire.common.util.CWClassLoader;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.LogicError;

/**
 * The ActionFactory is responsible for creating CWActions
 * and making them available to the program. Each Action is
 * constructed from resources of the form: simplename.field=value
 * where simplename is the ACTION_COMMAND_KEY value and
 * field is one of the CWAction constants, e.g. LargeIcon.
 *
 * The values for the icons are a path which can be found as a resource.
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
 * @author DM Smith [dmsmith555 at hotmail dot com]
 * @version $Id$
 */
public class ActionFactory implements ActionListener
{
    /**
     * Constructor.
     */
    public ActionFactory()
    {
        actions = new HashMap();
        listeners = new EventListenerList();

        buildActionMap();
    }

    /**
     * Get the Action for the given actionName.
     * @param acronymn the internal name of the CWAction
     * @return CWAction null if it does not exist
     */
    public CWAction getAction(String acronymn)
    {
        return (CWAction) actions.get(acronymn);
    }

    /**
     * Forwards the ActionEvent to the registered listener.
     */
    public void actionPerformed(ActionEvent evt)
    {
        EventListener[] listenerList = listeners.getListeners(ActionListener.class);
        for (int i = 0; i < listenerList.length; i++)
        {
            ((ActionListener) listenerList[i]).actionPerformed(evt);
        }
    }

    /**
     * Add a <code>ActionListener</code> to be notified of <code>ActionEvent</code>s
     * @param listener the <code>ActionListener</code> to add
     */
    public void addActionListener(ActionListener listener)
    {
        listeners.add(ActionListener.class, listener);
    }

    /**
     * Remove a ActionListener
     * @param listener the <code>ActionListener</code> to remove
     */
    public void removeActionListener(ActionListener listener)
    {
        listeners.remove(ActionListener.class, listener);
    }

    /**
     * Build the map of actions from resources
     */
    private void buildActionMap()
    {
        ResourceBundle resources = ResourceBundle.getBundle(getClass().getName(), Locale.getDefault(), new CWClassLoader());

        // ActionNames is a comma separated list of actions in the file
        String names = getString(resources, "ActionNames", "ActionNames");
        String [] nameArray = StringUtils.split(names, ',');
        for (int i = 0; i < nameArray.length; i++)
        {
            String actionName = nameArray[i];

            String name = getActionString(resources, actionName, Action.NAME);
            String shortDesc = getActionString(resources, actionName, Action.SHORT_DESCRIPTION);
            String longDesc = getActionString(resources, actionName, Action.LONG_DESCRIPTION);
            Icon smallIcon = getIcon(resources, actionName, Action.SMALL_ICON);
            Icon largeIcon = getIcon(resources, actionName, CWAction.LARGE_ICON);
            Integer mnemonic = getMnemonic(resources, actionName);
            KeyStroke accelerator = getAccelerator(resources, actionName);

            String enabledStr = getActionString(resources, actionName, "Enabled");
            boolean enabled = Boolean.valueOf(enabledStr).booleanValue();

            createAction(actionName, name, shortDesc, longDesc, mnemonic,
                            accelerator, smallIcon, largeIcon, enabled);
        }
    }

    /**
     * A convienence method
     */
    private String getActionString(ResourceBundle resources, String actionName, String field)
    {
        String key = actionName + '.' + field;
        return getString(resources, actionName, key);
    }

    /**
     * Get a string out of a PropertyResourceBundle and eat the exceptions
     */
    private String getString(ResourceBundle resources, String actionName, String key)
    {
        try
        {
            return resources.getString(key);
        }
        catch (MissingResourceException e)
        {
            log.info("Missing key for " + actionName, e);
            return "";
        }
    }

    /**
     * Get an icon for the string
     */
    private Icon getIcon(ResourceBundle resources, String actionName, String iconName)
    {
        Icon icon = null;
        String iconStr = getActionString(resources, actionName, iconName);
        if (iconStr.length() > 0)
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
        String mnemonicStr = getActionString(resources, actionName, Action.MNEMONIC_KEY);
        if (mnemonicStr.length() > 0)
        {
            try
            {
                mnemonic = new Integer(getInteger(mnemonicStr));
            }
            catch (NumberFormatException e)
            {
                log.warn("Could not parse integer for mnemonic of action " + actionName, e);
            }
        }
        return mnemonic;
    }

    /**
     * Convert the string to a valid Accelerator (i.e. a KeyStroke)
     */
    private KeyStroke getAccelerator(ResourceBundle resources, String actionName)
    {
        // Create the KeyStroke for the action's shortcut/accelerator
        KeyStroke accelerator = null;
        String acceleratorStr = getActionString(resources, actionName, Action.ACCELERATOR_KEY);
        String [] modifiers = StringUtils.split(getActionString(resources, actionName, Action.ACCELERATOR_KEY + ".Modifiers"), ',');
        if (acceleratorStr.length() > 0)
        {
            try
            {
                int shortcut = getInteger(acceleratorStr);
                int keyModifier = getModifier(modifiers);

                // Now we can create it
                accelerator = KeyStroke.getKeyStroke(shortcut, keyModifier);
            }
            catch (NumberFormatException nfe)
            {
                log.warn("Could not parse integer for accelerator of action " + actionName, nfe);
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
        if (str.startsWith("0x"))
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
            if ("ctrl".equalsIgnoreCase(modifier))
            {
                // use this so MacOS users are happy
                keyModifier |= Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
            }
            else if ("shift".equalsIgnoreCase(modifier))
            {
                keyModifier |= InputEvent.SHIFT_MASK;
            }
            else if ("alt".equalsIgnoreCase(modifier))
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

        if (acronymn == null || acronymn.length() == 0)
        {
            throw new LogicError("Acronymn is missing for CWAction");
        }

        if (name == null || name.length() == 0)
        {
            throw new LogicError("Name is missing for CWAction (" + acronymn + ")");
        }

        CWAction cwAction = new CWAction();
        cwAction.putValue(Action.ACTION_COMMAND_KEY, acronymn);
        cwAction.putValue(Action.NAME, name);
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
     * The log stream
     */
    private static final Logger log = Logger.getLogger(ActionFactory.class);

    /**
     * The map of known CWActions
     */
    private Map actions;

    /**
     * The list of listeners for the CWActions provided by
     * this ActionFactory
     */
    private EventListenerList listeners;

}
