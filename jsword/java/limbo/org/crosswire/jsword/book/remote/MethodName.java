package org.crosswire.jsword.book.remote;

import java.io.Serializable;

/**
 * Set of constants for the types of RemoteMethod.
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class MethodName implements Serializable
{
    static final MethodName GETBIBLES = new MethodName("getBibles"); //$NON-NLS-1$
    static final MethodName GETDATA = new MethodName("getData"); //$NON-NLS-1$
    static final MethodName FINDPASSAGE = new MethodName("findPassage"); //$NON-NLS-1$

    /**
     * Only we should be doing this
     */
    private MethodName(String name)
    {
        this.name = name;
    }

    /**
     * Lookup method to convert from a String
     */
    public static MethodName fromString(String name)
    {
        for (int i = 0; i < VALUES.length; i++)
        {
            MethodName o = VALUES[i];
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
    public static MethodName fromInteger(int i)
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
     * The name of the MethodName
     */
    private String name;

    // Support for serialization
    private static int nextObj;
    private final int obj = nextObj++;

    Object readResolve()
    {
        return VALUES[obj];
    }

    private static final MethodName[] VALUES =
    {
        GETBIBLES,
        GETDATA,
        FINDPASSAGE
    };

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3905528202582701873L;
}