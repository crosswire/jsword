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
 * Copyright: 2007
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: Msg.java 1701 2007-10-24 20:15:07Z dmsmith $
 */
package org.crosswire.jsword.book;

import org.crosswire.common.util.MsgBase;

/**
 * Compile safe Msg resource settings.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
final class UserMsg extends MsgBase
{

    static final UserMsg BIBLE = new UserMsg("BookCategory.Bible"); //$NON-NLS-1$
    static final UserMsg DICTIONARY = new UserMsg("BookCategory.Dictionary"); //$NON-NLS-1$
    static final UserMsg COMMENTARY = new UserMsg("BookCategory.Commentary"); //$NON-NLS-1$
    static final UserMsg READINGS = new UserMsg("BookCategory.Readings"); //$NON-NLS-1$
    static final UserMsg GLOSSARIES = new UserMsg("BookCategory.Glossaries"); //$NON-NLS-1$
    static final UserMsg UNORTHODOX = new UserMsg("BookCategory.Unorthodox"); //$NON-NLS-1$
    static final UserMsg GENERAL = new UserMsg("BookCategory.General"); //$NON-NLS-1$
    static final UserMsg OTHER = new UserMsg("BookCategory.Other"); //$NON-NLS-1$

    /**
     * Passthrough ctor
     */
    private UserMsg(String name)
    {
        super(name);
    }
}
