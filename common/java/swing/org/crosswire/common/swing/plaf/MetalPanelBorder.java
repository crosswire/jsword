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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;


/**
 * A class that provides a border that matches MetalBorders.ScrollPaneBorder.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Willie Thean [williethean at yahoo dot com]
 */
public final class MetalPanelBorder extends AbstractBorder implements UIResource
{
    public static final int TOP = 1;
    public static final int LEFT = 2;
    public static final int BOTTOM = 4;
    public static final int RIGHT = 8;

    /**
     * Default constructor.
     */
    public MetalPanelBorder()
    {
        insets = new Insets(insetTop, insetLeft, insetBottom, insetRight);
    }

    /**
     * Create a MetalPanelBorder instance where the border visbility
     * (top, left, bottom and right border) is controlled by the bit mask
     * <CODE>borderFlags</CODE>.
     * @param borderFlags Match flags, a bit mask that may include TOP, LEFT, BOTTOM, and RIGHT
     */
    public MetalPanelBorder(int borderFlags)
    {
        flags = 0 | borderFlags;

        if ((flags & TOP) != TOP)
        {
            insetTop = 0;
        }

        if ((flags & LEFT) != LEFT)
        {
            insetLeft = 0;
        }

        if ((flags & BOTTOM) != BOTTOM)
        {
            insetBottom = 0;
        }

        if ((flags & RIGHT) != RIGHT)
        {
            insetRight = 0;
        }

        insets = new Insets(insetTop, insetLeft, insetBottom, insetRight);
    }

    /* (non-Javadoc)
     * @see javax.swing.border.Border#paintBorder(java.awt.Component, java.awt.Graphics, int, int, int, int)
     */
    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h)
    {
        g.translate(x, y);

        if ((flags & TOP) == TOP)
        {
            g.setColor(MetalLookAndFeel.getControlDarkShadow());
            g.drawLine(0, 0, w - 2, 0);
        }

        if ((flags & LEFT) == LEFT)
        {
            g.drawLine(0, 0, 0, h - 2);
        }

        if ((flags & BOTTOM) == BOTTOM)
        {
            g.drawLine(0, h - 2, w - 2, h - 2);
            g.setColor(MetalLookAndFeel.getControlHighlight());
            g.drawLine(1, h - 1, w - 1, h - 1);
        }

        if ((flags & RIGHT) == RIGHT)
        {
            g.setColor(MetalLookAndFeel.getControlDarkShadow());
            g.drawLine(w - 2, h - 2, w - 2, 0);
            g.setColor(MetalLookAndFeel.getControlHighlight());
            g.drawLine(w - 1, h - 1, w - 1, 1);
        }

        g.translate(-x, -y);
    }

    /* (non-Javadoc)
     * @see javax.swing.border.Border#getBorderInsets(java.awt.Component)
     */
    public Insets getBorderInsets(Component c)
    {
        return insets;
    }

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 7929433986066846750L;

    private int insetTop = 1;
    private int insetLeft = 1;
    private int insetBottom = 2;
    private int insetRight = 2;

    private int flags;

    private Insets insets;
}
