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
package org.crosswire.jsword.book;

import java.io.Serializable;

import org.crosswire.common.util.MsgBase;

/**
 * A definition of how open a Bible is. Can is be freely copied or is
 * it proprietary.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class Openness implements Serializable
{
    /**
     * If the data of unknown distribution status
     */
    public static final Openness UNKNOWN = new Openness(Msg.OPEN_UNKNOWN);

    /**
     * If the data free of copyright restrictions
     */
    public static final Openness PD = new Openness(Msg.OPEN_PD);

    /**
     * Does the data have a licence that permits free use
     */
    public static final Openness FREE = new Openness(Msg.OPEN_FREE);

    /**
     * Is the data freely redistributable
     */
    public static final Openness COPYABLE = new Openness(Msg.OPEN_COPYABLE);

    /**
     * Is the data sold for commercial profit
     */
    public static final Openness COMMERCIAL = new Openness(Msg.OPEN_COMMERCIAL);

    /**
     * Prevent anyone else from doing this
     */
    private Openness(MsgBase msg)
    {
        name = msg.toString();
    }

    /**
     * Lookup method to convert from a String
     */
    public static Openness fromString(String name)
    {
        for (int i = 0; i < VALUES.length; i++)
        {
            Openness o = VALUES[i];
            if (o.name.equalsIgnoreCase(name))
            {
                return o;
            }
        }
        // cannot get here
        assert false;
        return null;
    }

    /**
     * Lookup method to convert from an integer
     */
    public static Openness fromInteger(int i)
    {
        return VALUES[i];
    }

    /**
     * Prevent subclasses from overriding canonical identity based Object methods
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public final boolean equals(Object o)
    {
        return super.equals(o);
    }

    /**
     * Prevent subclasses from overriding canonical identity based Object methods
     * @see java.lang.Object#hashCode()
     */
    public final int hashCode()
    {
        return super.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return name;
    }

    /**
     * The name of the Openness
     */
    private String name;

    // Support for serialization
    private static int nextObj;
    private final int obj = nextObj++;

    Object readResolve()
    {
        return VALUES[obj];
    }

    private static final Openness[] VALUES =
    {
        UNKNOWN,
        PD,
        FREE,
        COPYABLE,
        COMMERCIAL
    };

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3257844364125483320L;
}
