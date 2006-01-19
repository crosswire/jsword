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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;

/**
 * Layout Components along with labels that describe them.
 * We ought to consider the Form layout idea that I dreamt
 * up earlier when considering updates to this.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author Ideas from JDJ article by Claude Duguay
 */
public class FieldLayout extends AbstractLayout
{
    /**
     * A default FieldLayout with intercomponent
     * spacing of 0.
     */
    public FieldLayout()
    {
    }

    /**
     * FieldLayout with specified intercomponent
     * spacings.
     * @param hgap The horizontal gaps
     * @param vgap The vertical gaps
     */
    public FieldLayout(int hgap, int vgap)
    {
        super(hgap, vgap);
    }

    /**
     * The minimum layout size for a given container
     */
    public Dimension minimumLayoutSize(Container target)
    {
        int left = 0;
        int right = 0;
        int height = 0;
        Insets insets = target.getInsets();
        int ncomponents = target.getComponentCount();

        for (int i = 0; i < ncomponents; i += 2)
        {
            Component label = target.getComponent(i);
            int w1 = label.getMinimumSize().width;
            int h1 = label.getMinimumSize().height;
            if (w1 > left)
            {
                left = w1;
            }

            if (i + 1 < ncomponents)
            {
                Component field = target.getComponent(i + 1);
                int w2 = field.getMinimumSize().width;
                int h2 = field.getMinimumSize().height;
                if (w2 > right)
                {
                    right = w2;
                }

                height += Math.max(h1, h2) + hgap;
            }
            else
            {
                height += h1;
            }
        }

        return new Dimension(insets.left + insets.right + left + right + vgap, insets.top + insets.bottom + height - hgap);
    }

    /**
     * The preferred layout size for a given container
     */
    public Dimension preferredLayoutSize(Container target)
    {
        int left = 0;
        int right = 0;
        int height = 0;
        Insets insets = target.getInsets();
        int ncomponents = target.getComponentCount();

        for (int i = 0; i < ncomponents; i += 2)
        {
            Component label = target.getComponent(i);
            int w1 = label.getPreferredSize().width;
            int h1 = label.getPreferredSize().height;
            if (w1 > left)
            {
                left = w1;
            }

            if (i + 1 < ncomponents)
            {
                Component field = target.getComponent(i + 1);
                int w2 = field.getPreferredSize().width;
                int h2 = field.getPreferredSize().height;
                if (w2 > right)
                {
                    right = w2;
                }

                height += Math.max(h1, h2) + hgap;
            }
            else
            {
                height += h1;
            }
        }

        return new Dimension(insets.left + insets.right + left + right + vgap, insets.top + insets.bottom + height - hgap);
    }

    /**
     * layout the specified container
     */
    public void layoutContainer(Container target)
    {
        int left = 0;
        Insets insets = target.getInsets();
        int ncomponents = target.getComponentCount();

        // Pre-calculate left position
        for (int i = 0; i < ncomponents; i += 2)
        {
            Component label = target.getComponent(i);
            int w = label.getPreferredSize().width;
            if (w > left)
            {
                left = w;
            }
        }

        int right = target.getSize().width - left - insets.left - insets.right - hgap;
        int vpos = insets.top;

        for (int i = 0; i < ncomponents; i += 2)
        {
            Component label = target.getComponent(i);
            int h1 = label.getPreferredSize().height;
            int h2 = 0;
            Component field = null;

            if (i + 1 < ncomponents)
            {
                field = target.getComponent(i + 1);
                h2 = field.getPreferredSize().height;
            }

            // In order to top align the label setBounds using height of h1 not h
            int h = Math.max(h1, h2);
            label.setBounds(insets.left, vpos, left, h);
            if (field != null)
            {
                field.setBounds(insets.left + left + hgap, vpos, right, h);
            }

            vpos += h + hgap;
        }
    }

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3617576015757719097L;
}
