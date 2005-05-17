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
package org.crosswire.common.swing.desktop;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

/**
 * A mouse listener for a tabbed pane that can display a popup menu.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [ dmsmith555 at yahoo dot com]
 */
public class TabPopupListener extends MouseAdapter
{

    /**
     * Create a listener to mouse events on a JTabbedPane
     * and show a popup if requested.
     * @param tabbedPane The tab pane on which to listen for popup events
     * @param popupMenu the popup to display
     */
    public TabPopupListener(JTabbedPane tabbedPane, JPopupMenu popupMenu)
    {
        popup = popupMenu;
        tabs = tabbedPane;
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e)
    {
        doPopup(e);
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent e)
    {
        doPopup(e);
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent e)
    {
        doPopup(e);
    }

    /**
     * Popup if the mouse event indicates it should be shown
     * @param e
     */
    private void doPopup(MouseEvent e)
    {
        if (popup != null && e.isPopupTrigger())
        {
            // We only want the popup when we are over the tab an not the content of the tab.
            Component underMouse = SwingUtilities.getDeepestComponentAt((Component) e.getSource(), e.getX(), e.getY());
            if (underMouse == tabs)
            {
                // show the popup at the cursor
                popup.show(tabs, e.getX(), e.getY());
            }
        }
    }

    /**
     * The tabs for which the popup applies
     */
    private JTabbedPane tabs;

    /**
     * The popup for the tabs
     */
    private JPopupMenu popup;
}
