package org.crosswire.common.swing.desktop;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.swing.CWAction;

/**
 * This toolbar allows for manipulating how it looks. That is it allows for:<ul>
 * <li>showing/hiding labels</li>
 * <li>small/large icons</li>
 * <li>showing/hiding toolbar</li>
 * </ul>
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
 * @author DM Smith [ dmsmith555 at yahoo dot com]
 * @version $Id$
 */
public class ToolBar extends JToolBar
{
    /**
     * ToolBar constructor.
     */
    public ToolBar(JFrame frame)
    {
        this.frame = frame;
        actions = new ActionFactory(ToolBar.class, this);
    }

    /**
     * Show or hide the tool bar.
     * @param show indicates whether the toolbar is visible
     */
    public void showToolBar(boolean show)
    {
        Container contentPane = frame.getContentPane();

        if (show)
        {
            // Honor the previous orientation
            // Don't know how to honor the last location
            if (getOrientation() == SwingConstants.HORIZONTAL)
            {
                contentPane.add(this, BorderLayout.NORTH);
            }
            else
            {
                contentPane.add(this, BorderLayout.WEST);
            }
        }
        else
        {
            contentPane.remove(this);
        }
        frame.validate();
    }


    /**
     * Set the tool tip text for the buttons on the tool bar.
     * @param show indicates whether the buttons should be labelled
     */
    public void showText(boolean show)
    {
        Component c;
        int i = 0;
        while ((c = getComponentAtIndex(i++)) != null)
        {
            if (c instanceof JButton)
            {
                JButton button = (JButton) c;
                if (show)
                {
                    Action action = button.getAction();
                    button.setText((String) action.getValue(Action.SHORT_DESCRIPTION));
                }
                else
                {
                    button.setText(null);
                }
            }
        }
    }

    /**
     * Sets the size of the tool bar button images.
     * @param large indicates whether large buttons should be used
     */
    public void showLargeIcons(boolean large)
    {
        Component c;
        int i = 0;
        while ((c = getComponentAtIndex(i++)) != null)
        {
            if (c instanceof JButton)
            {
                JButton button = (JButton) c;
                Action action = button.getAction();
                if (action instanceof CWAction)
                {
                    // Clear the button's computed disabled icon
                    // so the button can get it again.
                    button.setDisabledIcon(null);
                    if (large)
                    {
                        button.setIcon((Icon) action.getValue(CWAction.LARGE_ICON));
                    }
                    else
                    {
                        button.setIcon((Icon) action.getValue(Action.SMALL_ICON));
                    }
                }
            }
        }
    }

    /**
     * Build a menu item that an end user can use to toggle visibility of the toolbar
     * @return a check box that can be used to toggle the visibility of the toolbar
     */
    public JMenuItem getShowToggle()
    {
        JCheckBoxMenuItem toggle = new JCheckBoxMenuItem(actions.getAction(TOOLBAR_TOGGLE));
        toggle.setSelected(true);
        return toggle;
    }

    /**
     * Build a menu item that an end user can use to toggle the text
     * @return a check box that can be used to toggle the text
     */
    public JMenuItem getTextToggle()
    {
        return new JCheckBoxMenuItem(actions.getAction(TOOLBAR_TEXT));
    }

    /**
     * Build a menu item that an end user can use to toggle the size of the icons
     * @return a check box that can be used to toggle the size of the icons
     */
    public JMenuItem getIconSizeToggle()
    {
        return new JCheckBoxMenuItem(actions.getAction(TOOLBAR_LARGE));
    }

    /**
     * Show or hide the tool bar.
     */
    public void doToolBarToggle(ActionEvent ev)
    {
        JCheckBoxMenuItem toggle = (JCheckBoxMenuItem) ev.getSource();
        showToolBar(toggle.isSelected());
    }

    /**
     * Show or hide the tool bar text.
     */
    public void doToolBarText(ActionEvent ev)
    {
        JCheckBoxMenuItem toggle = (JCheckBoxMenuItem) ev.getSource();
        showText(toggle.isSelected());
    }

    /**
     * Show large or small tool bar icons.
     */
    public void doToolBarLarge(ActionEvent ev)
    {
        JCheckBoxMenuItem toggle = (JCheckBoxMenuItem) ev.getSource();
        showLargeIcons(toggle.isSelected());
    }

    private static final String TOOLBAR_TOGGLE = "ToolBarToggle"; //$NON-NLS-1$
    private static final String TOOLBAR_TEXT = "ToolBarText"; //$NON-NLS-1$
    private static final String TOOLBAR_LARGE = "ToolBarLarge"; //$NON-NLS-1$

    /**
     * The frame in which the toolbar is shown.
     * It must be border layout with the only other component being centered.
     */
    private JFrame frame;
    private ActionFactory actions;
}
