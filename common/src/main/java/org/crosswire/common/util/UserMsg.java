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
 * ID: $Id: Msg.java 1672 2007-08-08 18:40:44Z dmsmith $
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
final class UserMsg extends MsgBase {
    static final UserMsg REASON = new UserMsg("LucidException.Reason"); //$NON-NLS-1$
    static final UserMsg CREATE_DIR_FAIL = new UserMsg("NetUtil.CreateDirFail"); //$NON-NLS-1$
    static final UserMsg CREATE_FILE_FAIL = new UserMsg("NetUtil.CreateFileFail"); //$NON-NLS-1$
    static final UserMsg IS_DIR = new UserMsg("NetUtil.IsDir"); //$NON-NLS-1$
    static final UserMsg IS_FILE = new UserMsg("NetUtil.IsFile"); //$NON-NLS-1$
    static final UserMsg NOT_DIR = new UserMsg("NetUtil.NotDir"); //$NON-NLS-1$
    static final UserMsg NOT_FILE_URI = new UserMsg("NetUtil.NotFileURI"); //$NON-NLS-1$
    static final UserMsg MISSING_FILE = new UserMsg("WebResource.MissingFile"); //$NON-NLS-1$
    static final UserMsg UNEXPECTED_ERROR = new UserMsg("Reporter.Unexpected"); //$NON-NLS-1$

    /**
     * Passthrough ctor
     */
    private UserMsg(String name) {
        super(name);
    }
}
