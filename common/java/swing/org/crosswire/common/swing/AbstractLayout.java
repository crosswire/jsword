package org.crosswire.common.swing;

import java.awt.Dimension;
import java.awt.Container;
import java.awt.Component;
import java.awt.LayoutManager2;
import java.io.Serializable;

/**
 * AbstractLayout - support for DeckLayout.
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
 * @author Joe Walker [joe at eireneh dot com]
 * @author Claude Duguay (Idea in JDJ article)
 * @version $Id$
 */
public abstract class AbstractLayout implements LayoutManager2, Serializable
{
    /**
     * Defaults the horizontal and vertical gaps to 0
     */
    public AbstractLayout()
    {
        this(0, 0);
    }

    /**
     * Constructor that specifies the horizontal and vertical gaps
     */
    public AbstractLayout(int hgap, int vgap)
    {
        setHgap(hgap);
        setVgap(vgap);
    }

    /**
     * Get the horizontal gap between components.
     */
    public int getHgap()
    {
        return hgap;
    }

    /**
     * Get the vertical gap between components.
     */
    public int getVgap()
    {
        return vgap;
    }

    /**
     * Set the horizontal gap between components.
     * @param gap The horizontal gap to be set
     */
    public void setHgap(int gap)
    {
        hgap = gap;
    }

    /**
     * Set the vertical gap between components.
     * @param gap The vertical gap to be set
     */
    public void setVgap(int gap)
    {
        vgap = gap;
    }

    /**
     * Returns the maximum dimensions for this layout given
     * the component in the specified target container.
     * @param target The component which needs to be laid out
     */
    public Dimension maximumLayoutSize(Container target)
    {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Returns the alignment along the x axis. This specifies how
     * the component would like to be aligned relative to other
     * components. The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     */
    public float getLayoutAlignmentX(Container parent)
    {
        return 0.5f;
    }

    /**
     * Returns the alignment along the y axis. This specifies how
     * the component would like to be aligned relative to other
     * components. The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     */
    public float getLayoutAlignmentY(Container parent)
    {
        return 0.5f;
    }

    /**
     * Invalidates the layout, indicating that if the layout
     * manager has cached information it should be discarded.
     */
    public void invalidateLayout(Container target)
    {
    }

    /**
     * Adds the specified component with the specified name
     * to the layout. By default, we call the more recent
     * addLayoutComponent method with an object constraint
     * argument. The name is passed through directly.
     * @param name The name of the component
     * @param comp The component to be added
     */
    public void addLayoutComponent(String name, Component comp)
    {
        addLayoutComponent(comp, name);
    }

    /**
     * Add the specified component from the layout.
     * By default, we let the Container handle this directly.
     * @param comp The component to be added
     * @param constraints The constraints to apply when laying out.
     */
    public void addLayoutComponent(Component comp, Object constraints)
    {
    }

    /**
     * Removes the specified component from the layout.
     * By default, we let the Container handle this directly.
     * @param comp the component to be removed
     */
    public void removeLayoutComponent(Component comp)
    {
    }

    /**
     * Return a string representation of the layout manager
     */
    public String toString()
    {
        return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    /**
     * horizontal gap
     */
    protected int hgap;

    /**
     * vertical gap
     */
    protected int vgap;

    /**
     * SERIALUID(dm): A placeholder for the ultimate version id.
     */
    private static final long serialVersionUID = 1L;

}
