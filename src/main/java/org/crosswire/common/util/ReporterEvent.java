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

import java.util.EventObject;

import org.crosswire.common.icu.NumberShaper;

/**
 * An event indicating that some bit of data needs capturing.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class ReporterEvent extends EventObject {
    /**
     * Constructs an CaptureEvent object.
     * 
     * @param source
     *            The event originator (typically <code>this</code>)
     * @param ex
     *            An exception
     */
    public ReporterEvent(Object source, Throwable ex) {
        super(source);

        this.ex = ex;
        this.message = null;
    }

    /**
     * Constructs an CaptureEvent object.
     * 
     * @param source
     *            The event originator (typically <code>this</code>)
     * @param message
     *            An message to log
     */
    public ReporterEvent(Object source, String message) {
        super(source);

        this.ex = null;
        this.message = new NumberShaper().shape(message);
    }

    /**
     * Returns a string specifying the source of the message.
     * 
     * @return The Source as a String
     */
    public String getSourceName() {
        Object src = getSource();

        Class<?> clazz;
        if (src instanceof Class<?>) {
            clazz = (Class<?>) src;
        } else {
            clazz = src.getClass();
        }

        String full = clazz.getName();
        int lastDot = full.lastIndexOf('.');
        if (lastDot == -1) {
            return full;
        }
        return full.substring(lastDot + 1);
    }

    /**
     * Returns the exception.
     * 
     * @return the Exception
     */
    public Throwable getException() {
        return ex;
    }

    /**
     * Returns the message.
     * 
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * The thing that went wrong
     */
    private Throwable ex;

    /**
     * The message that is being passed around
     */
    private String message;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 4121978048640988213L;
}
