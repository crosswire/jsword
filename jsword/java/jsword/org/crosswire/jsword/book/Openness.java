
package org.crosswire.jsword.book;

import org.apache.commons.lang.enum.Enum;

/**
 * A definition of how open a Bible is. Can is be freely copied or is
 * it proprietary.
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
public class Openness extends Enum
{
    /**
     * If the data of unknown distribution status
     */
    public static final Openness UNKNOWN = new Openness("Unknown");

    /**
     * If the data free of copyright restrictions
     */
    public static final Openness PD = new Openness("Public Domain");

    /**
     * Does the data have a licence that permits free use
     */
    public static final Openness FREE = new Openness("Free");

    /**
     * Is the data freely redistributable
     */
    public static final Openness COPYABLE = new Openness("Copyable");

    /**
     * Is the data sold for commercial profit
     */
    public static final Openness COMMERCIAL = new Openness("Commercial");

    /**
     * Find a constant given a name.
     */
    public static Openness get(String name)
    {
        return (Openness) Enum.getEnum(Openness.class, name);
    }

    /* (non-Javadoc)
     * @see org.apache.commons.lang.enum.Enum#toString()
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Prevent anyone else from doing this
     */
    private Openness(String desc)
    {
        super(desc);
    }
}
