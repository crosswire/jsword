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
package org.crosswire.common.swing.desktop;

import org.crosswire.common.swing.desktop.event.TitleChangedListener;

/**
 * A Titleable object has a title which may be gotten.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [ dmsmith555 at yahoo dot com]
 */
public interface Titleable
{
    /**
     * The title is something that can be displayed to a user
     * either in the tab of a TDIViewLayout or a title bar of
     * a MDIViewLayout window.
     * @return the title
     */
    String getTitle();

    /**
     * Register an interest in being notified of changes to the title.
     * @param listener
     */
    void addTitleChangedListener(TitleChangedListener listener);

    /**
     * Remove regristration of interest in listening for title changes
     * @param listener
     */
    void removeTitleChangedListener(TitleChangedListener listener);
}
