
package org.crosswire.common.util;

import java.util.Enumeration;

/**
 * Helper class to enumerate through the objects in an array.
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
public class ArrayEnumeration implements Enumeration
{
    /**
     * Setup the Enumeration with an array to iterate through
     * @param array The array to Enumerate over
     */
    public ArrayEnumeration(Object[] array)
    {
        this(array, 0, array.length);
    }

    /**
     * Setup the Enumeration with an array to iterate through
     * @param array The array to Enumerate over
     */
    public ArrayEnumeration(Object[] array, int start)
    {
        this(array, start, array.length);
    }

    /**
     * Setup the Enumeration with an array to iterate through
     * @param array The array to Enumerate over
     */
    public ArrayEnumeration(Object[] array, int start, int end)
    {
        if (array == null)
            throw new NullPointerException("Array must not be null.");
        if (start < 0)
            throw new IllegalArgumentException("Start must not be less than 0.");
        if (end > array.length)
            throw new IllegalArgumentException("End must not be greater than the array length.");

        this.array = array;
        this.pos = start;
        this.end = end;
    }

    /**
     * Are there more items in the database?
     * @return true if we are not finished yet
     */
    public boolean hasMoreElements()
    {
        return pos < end;
    }

    /**
     * Get the next item from the database
     * @return The next object in the list
     */
    public Object nextElement()
    {
        return array[pos++];
    }

    /** The array to iterate through */
    protected Object[] array;

    /** The current position in the array */
    protected int pos;

    /** The place to stop when we reach */
    protected int end;
}
