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
package org.crosswire.common.util;


/**
 * EventExceptions are generally used for passing problems through the event
 * system which does not allow checked exceptions through.
 * 
 * <p>
 * So LucidRuntimeException is a LucidException in all but inheritance -
 * LucidException inherits from Exception and so is checked, where EventEception
 * inherits from RuntimeException and so is not checked. In general you would
 * create a subclass of LucidException before you used it, however
 * EventExceptions would be used directly.
 * </p>
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @see LucidException
 */
public class LucidRuntimeException extends RuntimeException {

    /**
     * All LucidRuntimeException are constructed with references to resources in
     * an i18n properties file.
     * 
     * @param msg
     *            The resource id to read
     */
    public LucidRuntimeException(String msg) {
        super(msg);
    }

    /**
     * All LucidRuntimeException are constructed with references to resources in
     * an i18n properties file.
     * 
     * @param msg   The resource id to read
     * @param cause The cause of the exception
     */
    public LucidRuntimeException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3906091143962965817L;

}
