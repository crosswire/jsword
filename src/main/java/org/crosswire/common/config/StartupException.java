/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.common.config;

import org.crosswire.common.util.LucidException;

/**
 * Something in the startup config files failed to start properly.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class StartupException extends LucidException {
    /**
     * @param msg what happened
     */
    public StartupException(String msg) {
        super(msg);
    }

    /**
     * @param msg what happened
     * @param cause what caused the problem
     */
    public StartupException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3616451198199345203L;
}
