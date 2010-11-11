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
package org.crosswire.common.config;

import org.crosswire.common.util.LucidException;
import org.crosswire.common.util.MsgBase;

/**
 * Something in the startup config files failed to start properly.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class StartupException extends LucidException {
    /**
     * Construct the Exception with a message
     * 
     * @param msg
     *            The resource id to read
     */
    public StartupException(MsgBase msg) {
        super(msg);
    }

    /**
     * Construct the Exception with a message and a nested Exception
     * 
     * @param msg
     *            The resource id to read
     * @param ex
     *            The nested Exception
     */
    public StartupException(MsgBase msg, Throwable ex) {
        super(msg, ex);
    }

    /**
     * Construct the Exception with a message and some i18n params
     * 
     * @param msg
     *            The resource id to read
     * @param params
     *            An array of parameters
     */
    public StartupException(MsgBase msg, Object[] params) {
        super(msg, params);
    }

    /**
     * Construct the Exception with a message, a nested Exception and some i18n
     * params
     * 
     * @param msg
     *            The resource id to read
     * @param ex
     *            The nested Exception
     * @param params
     *            An array of parameters
     */
    public StartupException(MsgBase msg, Throwable ex, Object[] params) {
        super(msg, ex, params);
    }

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3616451198199345203L;
}
