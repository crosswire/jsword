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
package org.crosswire.jsword.book.remote;

import org.crosswire.common.util.LucidException;
import org.crosswire.common.util.MsgBase;

/**
 * For use in Remoter calls that fail.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class RemoterException extends LucidException
{
	/**
     * Constructor RemoterException.
     */
    public RemoterException(MsgBase message)
    {
        super(message);
    }

    /**
     * Constructor RemoterException.
     */
    public RemoterException(MsgBase message, Object[] params)
    {
        super(message, params);
    }

    /**
     * Constructor RemoterException.
     */
    public RemoterException(MsgBase message, Throwable cause)
    {
        super(message);
        this.cause = cause;
    }

    /**
     * Constructor RemoterException.
     */
    public RemoterException(MsgBase message, Throwable cause, Object[] params)
    {
        super(message, cause, params);
        this.cause = cause;
    }

    /**
     * Accessor for the cause of this exception
     */
    public Throwable getCause()
    {
        return cause;
    }

    /**
     * Accessor for the original type
     */
    public Class getOriginalType()
    {
        return originalType;
    }

    /**
     * The cause of this exception
     */
    private Throwable cause;

    /**
     * The original type of that caused this
     */
    private Class originalType;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3689064041213408309L;
}
