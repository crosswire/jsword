
package org.crosswire.jsword.map.model;

/**
 * RectangularBoundsRule implements Rule and attempts to move the Node within the
 * space (0, 0, _) to (1, 1, _). 
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
public class RectangularBoundsRule extends AbstractRule
{
    /**
     * Specify where it would like a node to be positioned in space.
     * Rules return an array of positions where the average of them
     * specifies the real desired position. So to specify a single place
     * simply return an array of one position. The positions are added
     * to the results from all Rules so to specify a single position
     * more strongly, return an array conataining that position many
     * times.
     * <br />
     * I expect that any Rule will not return more than 30 positions.
     * This expectation may be useful in colouring how many times to
     * include your Position(s) in the array.
     * @param map The Map to select a node from
     * @param ord The ordinal number (1 - 31104) of the verse
     * @return An array of desired positions.
     */
    public Position getDesiredPosition(Map map, int book, int chapter)
    {
        float[] arr = map.getPositionArrayCopy(book, chapter);

        // force the coords to be inside (0, 0, ...) to (1, 1, ...)
        for (int i=0; i<arr.length; i++)
        {
            arr[i] = arr[i];
            if (arr[i] < MIN) arr[i] = MIN;
            if (arr[i] > MAX) arr[i] = MAX;
        }

        return new Position(arr);
    }
    
    private static final float MAX = 0.95F;
    private static final float MIN = 0.05F;
}
