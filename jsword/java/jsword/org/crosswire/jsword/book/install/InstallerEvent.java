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
package org.crosswire.jsword.book.install;

import java.util.EventObject;

/**
 * An InstallerEvent is fired whenever an Installer is added or removed from the
 * system.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class InstallerEvent extends EventObject
{
    /**
     * Basic constructor
     * @param installer The installer, or null if there is more than one change.
     * @param added True if the changed installer is an addition.
     */
    public InstallerEvent(Object source, Installer installer, boolean added)
    {
        super(source);

        this.installer = installer;
        this.added = added;
    }

    /**
     * Get the name of the changed Bible
     * @return The Bible bmd
     */
    public Installer getInstaller()
    {
        return installer;
    }

    /**
     * Is this an addition event?
     */
    public boolean isAddition()
    {
        return added;
    }

    /**
     * Is this an addition event?
     */
    private boolean added;

    /**
     * The name of the changed Bible
     */
    private transient Installer installer;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3257290248836102194L;
}
