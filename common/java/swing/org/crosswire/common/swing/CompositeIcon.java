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
package org.crosswire.common.swing;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.SwingConstants;

/**
 * CompositeIcon is an Icon implementation which draws two icons with a specified relative position.
 * LEFT, RIGHT, TOP, BOTTOM:
 *      specify how icon1 is drawn relative to icon2
 * CENTER:
 *      icon1 is drawn first, icon2 is drawn over it
 * and with horizontal and vertical orientations within the alloted space
 * It's useful with VTextIcon when you want an icon with your text: 
 *      if icon1 is the graphic icon and icon2 is the VTextIcon, 
 *      you get a similar effect to a JLabel with a graphic icon and text
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Lee Ann Rucker [LRucker at mac dot com] from http://www.macdevcenter.com/pub/a/mac/2002/03/22/vertical_text.html
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class CompositeIcon implements Icon
{

    /**
     * Create a CompositeIcon from the specified Icons,
     * using the default relative position (icon1 above icon2)
     * and orientations (centered horizontally and vertically).
     * 
     * @param icon1 Icon
     * @param icon2 Icon
     */
    public CompositeIcon(Icon icon1, Icon icon2)
    {
        this(icon1, icon2, SwingConstants.TOP);
    }

    /**
     * Create a CompositeIcon from the specified Icons,
     * using the specified relative position
     * and default orientations (centered horizontally and vertically).
     * 
     * @param icon1 Icon
     * @param icon2 Icon
     * @param position int
     */
    public CompositeIcon(Icon icon1, Icon icon2, int position)
    {
        this(icon1, icon2, position, SwingConstants.CENTER, SwingConstants.CENTER);
    }

    /**
     * Create a CompositeIcon from the specified Icons,
     * using the specified relative position and orientations.
     * 
     * @param icon1 Icon
     * @param icon2 Icon
     * @param position int
     * @param horizontalOrientation int
     * @param verticalOrientation int
     */
    public CompositeIcon(Icon icon1, Icon icon2, int position, int horizontalOrientation, int verticalOrientation)
    {
        this.icon1 = icon1;
        this.icon2 = icon2;
        this.position = position;
        this.horizontalOrientation = horizontalOrientation;
        this.verticalOrientation = verticalOrientation;
    }

    /* (non-Javadoc)
     * @see javax.swing.Icon#getIconHeight()
     */
    public int getIconHeight()
    {
        if (position == SwingConstants.TOP || position == SwingConstants.BOTTOM)
        {
            return icon1.getIconHeight() + icon2.getIconHeight();
        }

        return Math.max(icon1.getIconHeight(), icon2.getIconHeight());
    }

    /* (non-Javadoc)
     * @see javax.swing.Icon#getIconWidth()
     */
    public int getIconWidth()
    {
        if (position == SwingConstants.LEFT || position == SwingConstants.RIGHT)
        {
            return icon1.getIconWidth() + icon2.getIconWidth();
        }

        return Math.max(icon1.getIconWidth(), icon2.getIconWidth());
    }

    /* (non-Javadoc)
     * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
     */
    public void paintIcon(Component c, Graphics g, int x, int y)
    {
        int width = getIconWidth();
        int height = getIconHeight();
        if (position == SwingConstants.LEFT || position == SwingConstants.RIGHT)
        {
            Icon leftIcon;
            Icon rightIcon;
            if (position == SwingConstants.LEFT)
            {
                leftIcon = icon1;
                rightIcon = icon2;
            }
            else
            {
                leftIcon = icon2;
                rightIcon = icon1;
            }
            // "Left" orientation, because we specify the x position
            paintIcon(c, g, leftIcon, x, y, width, height, SwingConstants.LEFT, verticalOrientation);
            paintIcon(c, g, rightIcon, x + leftIcon.getIconWidth(), y, width, height, SwingConstants.LEFT, verticalOrientation);
        }
        else if (position == SwingConstants.TOP || position == SwingConstants.BOTTOM)
        {
            Icon topIcon;
            Icon bottomIcon;
            if (position == SwingConstants.TOP)
            {
                topIcon = icon1;
                bottomIcon = icon2;
            }
            else
            {
                topIcon = icon2;
                bottomIcon = icon1;
            }
            // "Top" orientation, because we specify the y position
            paintIcon(c, g, topIcon, x, y, width, height, horizontalOrientation, SwingConstants.TOP);
            paintIcon(c, g, bottomIcon, x, y + topIcon.getIconHeight(), width, height, horizontalOrientation, SwingConstants.TOP);
        }
        else
        {
            paintIcon(c, g, icon1, x, y, width, height, horizontalOrientation, verticalOrientation);
            paintIcon(c, g, icon2, x, y, width, height, horizontalOrientation, verticalOrientation);
        }
    }

    /**
     * Paints one icon in the specified rectangle with the given orientations.
     * 
     * @param c Component
     * @param g Graphics
     * @param icon Icon
     * @param x int
     * @param y int
     * @param width int
     * @param height int
     * @param hOrientation int
     * @param vOrientation int
     */
    private void paintIcon(Component c, Graphics g, Icon icon, int x, int y, int width, int height, int hOrientation, int vOrientation)
    {

        int xIcon;
        int yIcon;
        switch (hOrientation)
        {
        case SwingConstants.LEFT:
            xIcon = x;
            break;
        case SwingConstants.RIGHT:
            xIcon = x + width - icon.getIconWidth();
            break;
        default:
            xIcon = x + (width - icon.getIconWidth()) / 2;
            break;
        }
        switch (vOrientation)
        {
        case SwingConstants.TOP:
            yIcon = y;
            break;
        case SwingConstants.BOTTOM:
            yIcon = y + height - icon.getIconHeight();
            break;
        default:
            yIcon = y + (height - icon.getIconHeight()) / 2;
            break;
        }
        icon.paintIcon(c, g, xIcon, yIcon);
    }

    private Icon icon1;
    private Icon icon2;
    private int position;
    private int horizontalOrientation;
    private int verticalOrientation;
}
