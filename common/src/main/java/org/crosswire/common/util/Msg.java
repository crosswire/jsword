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
final class Msg extends MsgBase
{
    static final Msg SECURITY = new Msg("CallContext.Security"); //$NON-NLS-1$
    static final Msg WRONG_TYPE = new Msg("EventListenerList.WrongType"); //$NON-NLS-1$
    static final Msg REASON = new Msg("LucidException.Reason"); //$NON-NLS-1$
    static final Msg CANT_STRIP = new Msg("NetUtil.CantStrip"); //$NON-NLS-1$
    static final Msg CREATE_DIR_FAIL = new Msg("NetUtil.CreateDirFail"); //$NON-NLS-1$
    static final Msg CREATE_FILE_FAIL = new Msg("NetUtil.CreateFileFail"); //$NON-NLS-1$
    static final Msg IS_DIR = new Msg("NetUtil.IsDir"); //$NON-NLS-1$
    static final Msg IS_FILE = new Msg("NetUtil.IsFile"); //$NON-NLS-1$
    static final Msg NOT_DIR = new Msg("NetUtil.NotDir"); //$NON-NLS-1$
    static final Msg NOT_FILE_URI = new Msg("NetUtil.NotFileURI"); //$NON-NLS-1$
    static final Msg NO_RESOURCE = new Msg("ResourceUtil.NoResource"); //$NON-NLS-1$
    static final Msg NOT_ASSIGNABLE = new Msg("ResourceUtil.NotAssignable"); //$NON-NLS-1$
    static final Msg MISSING_FILE = new Msg("WebResource.MissingFile"); //$NON-NLS-1$
    static final Msg UNEXPECTED_ERROR = new Msg("Reporter.Unexpected"); //$NON-NLS-1$

    /**
     * Passthrough ctor
     */
    private Msg(String name)
    {
        super(name);
    }
}
