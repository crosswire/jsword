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
 * ID: $Id$
 */
package org.crosswire.common.config.swing;

import java.awt.event.ActionEvent;

/**
 * A listener for button presses in a ButtonPane.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public interface ButtonPaneListener
{
    /**
     * Someone has pressed OK
     * @param ev The button press event
     */
    public void okPressed(ActionEvent ev);

    /**
     * Someone has pressed cancel
     * @param ev The button press event
     */
    public void cancelPressed(ActionEvent ev);

    /**
     * Someone has pressed apply
     * @param ev The button press event
     */
    public void applyPressed(ActionEvent ev);
}
