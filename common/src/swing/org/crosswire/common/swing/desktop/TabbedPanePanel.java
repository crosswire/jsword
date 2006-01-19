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
package org.crosswire.common.swing.desktop;

import java.awt.Insets;
import java.awt.LayoutManager;

import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 * A JPanel class where it's child components will paint on top of its border.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Willie Thean [williethean at yahoo dot com]
 */
public class TabbedPanePanel extends JPanel
{
    public TabbedPanePanel()
    {
        super();
        init();
    }

    public TabbedPanePanel(boolean isDoubleBuffered)
    {
        super(isDoubleBuffered);
        init();
    }

    public TabbedPanePanel(LayoutManager layout)
    {
        super(layout);
        init();
    }

    public TabbedPanePanel(LayoutManager layout, boolean isDoubleBuffered)
    {
        super(layout, isDoubleBuffered);
        init();
    }

    private void init()
    {
        this.setBorder(UIManager.getBorder("TabbedPanePanel.border")); //$NON-NLS-1$
    }

    /**
     * If we setBorder on this JPanel, the border width will be part of the insets. 
     * We return an insets of 0 so the child components will paint on top of the
     * border. 
     */
    public Insets getInsets()
    {
        return new Insets(0, 0, 0, 0);
    }

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 5254437923545591019L;
}
