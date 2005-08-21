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

import com.sun.java.swing.plaf.windows.WindowsTabbedPaneUI;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;

/**
 * Provides customization to WindowsLF Tabbed panes.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Willie Thean [williethean at yahoo dot com]
 */
public class WindowsBorderlessTabbedPaneUI extends WindowsTabbedPaneUI
{
    public static ComponentUI createUI(JComponent c) {
        return new WindowsBorderlessTabbedPaneUI();
    }

    /**
     * Return the content border insets based on <CODE>tabPlacement</CODE>.
     * E.g. 
     * <CODE>tabPlacement</CODE> = SwingConstants.TOP returns new Insets(1, 0, 0, 0)
     * <CODE>tabPlacement</CODE> = SwingConstants.RIGHT returns new Insets(0, 0, 0, 1)
     * @param tabPlacement tab placement of the tabbed pane
     * @return an Inset instance based on <CODE>tabPlacement</CODE>
     */
    protected Insets getContentBorderInsets(int tabPlacement)
    {
        if (tabPlacement == SwingConstants.TOP) 
        {
            return new Insets(1, 0, 0, 0);
        }
        else if (tabPlacement == SwingConstants.LEFT) 
        {
            return new Insets(0, 1, 0, 0);
        }
        else if (tabPlacement == SwingConstants.BOTTOM)
        {
            return new Insets(0, 0, 1, 0);
        }
        else if (tabPlacement == SwingConstants.RIGHT)
        {
            return new Insets(0, 0, 0, 1);
        }
        else {
            return new Insets(0, 0, 0, 0);
        }
    }
}
