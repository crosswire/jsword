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
 * ID: $ID$
 */
package org.crosswire.common.swing;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

/**
 * This is a hack to fix the setDividerLocation problem and other layout problems.
 * <p>
 * See Bug Parade 4101306, 4485465 for a description of the WIDE divider problem.
 * <p>
 * Bug Reports on JSplitpane setDividerLocation<br>
 * 4101306, 4125713, 4148530
 *<p>
 * From the javadoc for setDividerLocation(double):
 * -------------------------------------------<br>
 * <p>This method is implemented in terms of setDividerLocation(int).
 * This method immediately changes the size of the receiver based on
 * its current size. If the receiver is not correctly realized and on
 * screen, this method will have no effect (new divider location will
 * become (current size * proportionalLocation) which is 0).<br>
 * -------------------------------------------<br>
 * So, as you can see the JSplitPane MUST be visible invoking this method
 * otherwise it will not have the desired effect.
 * <p>
 * Another, Bug Report 4786896 notes that if the preferred sizes of the
 * two components plus the divider of the split pane adds up to more than
 * the preferred size of the JSplitPane, then JSplitPane will use the
 * minimum size of the components.
 * <p>
 * Since the preferred way of managing the sizes of containers is not with
 * pixel counts, the solution here is to set the preferred size to zero.
 *
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class FixedSplitPane extends JSplitPane
{
    /**
     * Constructor for FixedSplitPane
     */
    public FixedSplitPane()
    {
    }

    /**
     * Constructor for FixedSplitPane
     */
    public FixedSplitPane(int arg0)
    {
        super(arg0);
    }

    /**
     * Constructor for FixedSplitPane
     */
    public FixedSplitPane(int arg0, boolean arg1)
    {
        super(arg0, arg1);
    }

    /**
     * Constructor for FixedSplitPane
     */
    public FixedSplitPane(int arg0, Component arg1, Component arg2)
    {
        super(arg0, arg1, arg2);
    }

    /**
     * Constructor for FixedSplitPane
     */
    public FixedSplitPane(int arg0, boolean arg1, Component arg2, Component arg3)
    {
        super(arg0, arg1, arg2, arg3);
    }

    /* (non-Javadoc)
     * @see java.awt.Container#addImpl(java.awt.Component, java.lang.Object, int)
     */
    protected void addImpl(Component comp, Object constraints, int index)
    {
        if (comp instanceof JComponent)
        {
            ((JComponent) comp).setPreferredSize(DOT);
        }
        super.addImpl(comp, constraints, index);
    }

    /* (non-Javadoc)
     * @see javax.swing.JSplitPane#setBottomComponent(java.awt.Component)
     */
    public void setBottomComponent(Component comp)
    {
        if (comp instanceof JComponent)
        {
            ((JComponent) comp).setPreferredSize(DOT);
        }
        super.setBottomComponent(comp);
    }

    /* (non-Javadoc)
     * @see javax.swing.JSplitPane#setLeftComponent(java.awt.Component)
     */
    public void setLeftComponent(Component comp)
    {
        if (comp instanceof JComponent)
        {
            ((JComponent) comp).setPreferredSize(DOT);
        }
        super.setLeftComponent(comp);
    }

    /* (non-Javadoc)
     * @see javax.swing.JSplitPane#setRightComponent(java.awt.Component)
     */
    public void setRightComponent(Component comp)
    {
        if (comp instanceof JComponent)
        {
            ((JComponent) comp).setPreferredSize(DOT);
        }
        super.setRightComponent(comp);
    }

    /* (non-Javadoc)
     * @see javax.swing.JSplitPane#setTopComponent(java.awt.Component)
     */
    public void setTopComponent(Component comp)
    {
        if (comp instanceof JComponent)
        {
            ((JComponent) comp).setPreferredSize(DOT);
        }
        super.setTopComponent(comp);
    }

    /**
     * Validates this container and all of its subcomponents. The first time
     * this method is called, the initial divider position is set.
     */
    public void validate()
    {
        if (firstValidate)
        {
            firstValidate = false;
            if (hasProportionalLocation)
            {
                setDividerLocation(proportionalLocation);
            }
        }
        super.validate();
    }

    /**
     * Sets the divider location as a percentage of the JSplitPane's size.
     */
    public void setDividerLocation(double newProportionalLoc)
    {
        if (!firstValidate)
        {
            hasProportionalLocation = true;
            proportionalLocation = newProportionalLoc;
        }
        else
        {
            super.setDividerLocation(newProportionalLoc);
        }
    }

    private static final Dimension DOT = new Dimension(0, 0);
    private boolean firstValidate = true;
    private boolean hasProportionalLocation;
    private double proportionalLocation;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3761687909593789241L;
}
