
package org.crosswire.jsword.map.model;

import org.apache.log4j.Logger;

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
public class CircularBoundsRule extends AbstractRule
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
        if (map.getDimensions() != 2)
        {
            log.warn("CircularBoundsRule only works in 2 dimensions");
            return new Position(map.getPositionArrayCopy(book, chapter));
        }

        float[] arr = map.getPositionArrayCopy(book, chapter);

        float xcentrecoord = arr[0]-0.5F;
        float ycentrecoord = arr[1]-0.5F;

        float distance = (float) Math.sqrt(xcentrecoord*xcentrecoord + ycentrecoord*ycentrecoord);
        if (distance > RADIUS)
        {
            double angle = Math.atan(xcentrecoord/ycentrecoord);

            if (xcentrecoord > 0)
            {
                arr[0] = 1F - (float) (RADIUS * Math.cos(angle));
            }
            else
            {
                arr[0] = (float) (RADIUS * Math.cos(angle));
            }

            if (ycentrecoord > 0)
            {
                arr[1] = 1F - (float) (RADIUS * Math.sin(angle));
            }
            else
            {
                arr[1] = (float) (RADIUS * Math.sin(angle));
            }
        }

        return new Position(arr);
    }
    
    /** The log stream */
    protected static Logger log = Logger.getLogger(CircularBoundsRule.class);

    private static final float RADIUS = 0.45F;
}
