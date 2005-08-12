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

/**
 * Contains base UI defaults for all platforms.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Willie Thean [williethean at yahoo dot com]
 */
public abstract class AbstractLFCustoms
{
    /**
     * Constructor.
     */
    public AbstractLFCustoms()
    {
    }

    /**
     * Calling this method installs base and platform specfic UI defaults.
     */
    public void initUIDefaults()
    {
        initBaseUIDefaults();
        initPlatformUIDefaults();
    }

    /**
     * Init UI Defaults value applicable to all platforms.
     */
    private void initBaseUIDefaults()
    {
        // Specify defaults applicable to all platforms here
    }

    /**
     * This method does nothing. Subclass should override this to install platform
     * specific UI defaults.
     */
    protected void initPlatformUIDefaults()
    {
    }
}
