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
package org.crosswire.common.config.swing;

import java.awt.Component;
import java.awt.event.ActionListener;

import org.crosswire.common.config.Config;

/**
 * Some static methods for using the Config package.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public interface ConfigEditor
{
    /**
     * The equivalent of a constructor, create a Config base with the set of
     * Fields that it will display.
     * @param config The configurable settings
     */
    void construct(Config config);

    /**
     * Create a dialog to house a TreeConfig component
     * using the default set of Fields
     * @param parent A component to use to find a frame to use as a dialog parent
     */
    void showDialog(Component parent, ActionListener al);
}
