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
 */
package org.crosswire.common.swing.plaf;

import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalTabbedPaneUI;


/**
 * Provides customization to MetalLF Tabbed panes.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Willie Thean [williethean at yahoo dot com]
 */
public class MetalBorderlessTabbedPaneUI extends MetalTabbedPaneUI
{
    public static ComponentUI createUI(JComponent x)
    {
        return new MetalBorderlessTabbedPaneUI();
    }

    /**
     * Return a new Insets(0, 0, 0, 0). <CODE>tabPlacement</CODE>. is ignored.
     * @param tabPlacement ignored
     * @return a new Insets(0, 0, 0, 0)
     */
    protected Insets getContentBorderInsets(int tabPlacement)
    {
        return new Insets(0, 0, 0, 0);
    }

}
