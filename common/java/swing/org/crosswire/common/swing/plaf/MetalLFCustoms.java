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

import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 * Provides customization to MetalLF Tabbed panes
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Willie Thean [williethean at yahoo dot com]
 */
public class MetalLFCustoms extends AbstractLFCustoms
{
    /**
     * Default constructor.
     */
    public MetalLFCustoms()
    {
        super();
    }

    /**
     * Install Metal platform specific UI defaults.
     */
    protected void initPlatformUIDefaults()
    {
        // Border panelSelectBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        Border panelSelectBorder = BorderFactory.createCompoundBorder(
            new MetalPanelBorder(MetalPanelBorder.TOP | MetalPanelBorder.LEFT | MetalPanelBorder.RIGHT),
            BorderFactory.createEmptyBorder(5, 5, 5, 5));

        Object[] metalUIDefaults = new Object[] {
            "TabbedPane.contentBorderInsets", new Insets(1, 0, 0, 0), //$NON-NLS-1$
            "SplitPane.dividerSize", new Integer(5), //$NON-NLS-1$
            "SelectPanel.border", panelSelectBorder //$NON-NLS-1$
        };

        UIManager.getDefaults().putDefaults(metalUIDefaults);
    }
}
