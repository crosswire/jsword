
package org.crosswire.jsword.book;

/**
 * A definition of how open a Bible is. Can is be freely copied or is
 * it proprietary.
 * 
 * <table border='1' cellPadding='3' cellSpacing='0' width="100%">
 * <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
 * Distribution Licence:<br />
 * Project B is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
 * <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
 * @see <{docs.Licence}>
 * @author Joe Walker
 * @version $Id$
 */
public class Openness
{
    /**
     * If the data of unknown distribution status
     */
    public static final Openness STATUS_UNKNOWN = new Openness("Unknown", -1);

    /**
     * If the data free of copyright restrictions
     */
    public static final Openness STATUS_PD = new Openness("Public Domain", 0);

    /**
     * Does the data have a licence that permits free use
     */
    public static final Openness STATUS_FREE = new Openness("Free", 1);

    /**
     * Is the data freely redistributable
     */
    public static final Openness STATUS_COPYABLE = new Openness("Copyable", 2);

    /**
     * Is the data sold for commercial profit
     */
    public static final Openness STATUS_COMMERCIAL = new Openness("Commercial", 3);

    /**
     * 
     */
    private Openness(String desc, int order)
    {
    }

}
