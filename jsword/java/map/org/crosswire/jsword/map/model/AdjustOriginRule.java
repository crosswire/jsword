
package org.crosswire.jsword.map.model;

import org.crosswire.common.util.Logger;

/**
 * BrownianRule.
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
public class AdjustOriginRule extends AbstractRule
{
    /**
     * Specify where it would like a node to be positioned in space.
     * Rules return an array of positions where the average of them
     * specifies the real desired position. So to specify a single place
     * simply return an array of one position. The positions are added
     * to the results from all Rules so to specify a single position
     * more strongly, return an array conataining that position many
     * times.
     * @param map The Map to select a node from
     * @return An array of desired positions.
     */
    public Position getDesiredPosition(Map map, int book, int chapter)
    {
        int dimensions = map.getDimensions();
        if (cog == null || (book == 1 && chapter == 1))
        {
            cog = map.getCenterOfGravity();
            adjust = new float[dimensions];

            for (int i=0; i<dimensions; i++)
            {
                // div 2 is to provide some damping
                adjust[i] = (0.5F - cog.pos[i])/2;
            }
            
            log.debug("Setting COG adjust to x="+adjust[0]+", y="+adjust[1]);
        }

        float[] pos = map.getPositionArrayCopy(book, chapter);
        for (int i=0; i<dimensions; i++)
        {
            pos[i] += adjust[i];
        }

        return new Position(pos);
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(AdjustOriginRule.class);

    /**
     * The thing we are trying to move to middle,middle
     */
    private Position cog;

    /**
     * How do we need to move the axes?
     */
    private float[] adjust;
}
