
package org.crosswire.jsword.book;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class Openness
{
    /**
     * A collection of all the Opennesses. This MUST come before all the
     * definitions below so that they can add themselves to this map.
     */
    private static final Map all = new HashMap();

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
     * debug the opennesses
     */
    public static void debugX()
    {
        System.out.println("all.length="+all.size());
        Iterator it = all.keySet().iterator();
        while (it.hasNext())
        {
            String desc = (String) it.next();
            Openness open = (Openness) all.get(desc);
            System.out.println(desc+".toString="+open.toString());
        }
    }

    /**
     * Get an openness by its name.
     */
    public static Openness get(String desc)
    {
        if (desc == null)
            return UNKNOWN;

        Openness reply = (Openness) all.get(desc);
        if (reply != null)
            return reply;

        return UNKNOWN;
    }

    /**
     * 
     */
    private Openness(String desc)
    {
        this.desc = desc;
        all.put(desc, this);
    }

    /**
     * String representation of this Object
     */
    public String toString()
    {
        return desc;
    }

    private String desc;
}
