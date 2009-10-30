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
package org.crosswire.common.util;

/**
 * Compile safe Msg resource settings.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
final class Msg extends MsgBase {
    static final Msg SECURITY = new Msg("CallContext.Security"); //$NON-NLS-1$
    static final Msg WRONG_TYPE = new Msg("EventListenerList.WrongType"); //$NON-NLS-1$
    static final Msg CANT_STRIP = new Msg("NetUtil.CantStrip"); //$NON-NLS-1$
    static final Msg NO_RESOURCE = new Msg("ResourceUtil.NoResource"); //$NON-NLS-1$
    static final Msg NOT_ASSIGNABLE = new Msg("ResourceUtil.NotAssignable"); //$NON-NLS-1$

    /**
     * Passthrough ctor
     */
    private Msg(String name) {
        super(name);
    }
}
