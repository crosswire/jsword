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
package org.crosswire.common.swing.plaf;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

/**
 * Customizations to Windows LF for tabs.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Willie Thean [williethean at yahoo dot com]
 */
public class WindowsLFCustoms extends AbstractLFCustoms
{
    /**
     * Default constructor.
     */
    public WindowsLFCustoms()
    {
        super();
    }

    /**
     * Install Windows platform specific UI defaults.
     */
    protected void initPlatformUIDefaults()
    {
        Border tabbedPanePanelBorder = null;
        Color standardBorderColor = null;
        Object windowsScrollPaneborder = UIManager.get("ScrollPane.border"); //$NON-NLS-1$
        if (windowsScrollPaneborder != null)
        {
            if (windowsScrollPaneborder instanceof LineBorder)
            {
                standardBorderColor = ((LineBorder) windowsScrollPaneborder).getLineColor();
                tabbedPanePanelBorder = new LineBorder(standardBorderColor);
            }
            else
            {
                tabbedPanePanelBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);
            }
        }

        Border panelSelectBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 0, 1, standardBorderColor),
            BorderFactory.createEmptyBorder(5, 5, 5, 5));

        Object[] windowsUIDefaults = new Object[]
        {
            "BibleViewPane.TabbedPaneUI", WindowsBorderlessTabbedPaneUI.createUI(null), //$NON-NLS-1$
            "TabbedPanePanel.border", tabbedPanePanelBorder, //$NON-NLS-1$
            "StandardBorder.color", standardBorderColor, //$NON-NLS-1$
            "SelectPanel.border", panelSelectBorder //$NON-NLS-1$
        };

        UIManager.getDefaults().putDefaults(windowsUIDefaults);
    }
}
