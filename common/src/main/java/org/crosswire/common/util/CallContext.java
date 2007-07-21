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
 * This singleton class provides a way for a method to determine
 * which class called it.
 * <p>
 * It has been tested to work in command line and WebStart environments.
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [ dmsmith555 at yahoo dot com]
 */
public final class CallContext extends SecurityManager
{
    /**
     * Prevent instantiation
     */
    private CallContext()
    {
    }

    /**
     * Singleton accessor
     */
    public static CallContext instance()
    {
        try
        {
            if (resolver == null)
            {
                resolver = new CallContext();
            }

            return resolver;
        }
        catch (SecurityException se)
        {
            throw new LucidRuntimeException(Msg.SECURITY, se);
        }
    }

    /*
     * (non-Javadoc)
     * @see java.lang.SecurityManager#getClassContext()
     */
    /* @Override */
    protected Class[] getClassContext()
    {
        return super.getClassContext();
    }

    /**
     * When called from a method it will return the class
     * calling that method.
      */
    public static Class getCallingClass()
    {
        return getCallingClass(1); // add 1 for this method
    }

    /**
     * When called from a method it will return the i-th class
     * calling that method, up the call chain.
     * If used with a -1 it will return the class making the call
     * -2 and -3 will return this class
     * @throws ArrayIndexOutOfBoundsException if the index is not valid
     */
    public static Class getCallingClass(int i)
    {
        return instance().getClassContext()[CALL_CONTEXT_OFFSET + i];
    }

    // may need to change if this class is redesigned
    /**
     * Offset needed to represent the caller of the method
     * that called this method.
     *
     */
    private static final int CALL_CONTEXT_OFFSET = 3;

    private static CallContext resolver;
}
