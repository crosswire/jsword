
package org.crosswire.jsword.map.model;

import java.util.Random;

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
public class BrownianRule extends AbstractRule
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
        float[] pos = map.getPositionArrayCopy(book, chapter);

        for (int i=0; i<pos.length; i++)
        {
            pos[i] += (float) (rand.nextGaussian() * HEAT);
        }

        return new Position(pos);
    }

    /**
     * The max random jiggle.
     * How much is it possible to a node to move randomly each turn.
     * A heat of 1.0 means that any node could move roughly anywhere across the
     * board each turn, so a heat of 0.001 is probably more useful.
     * To be precise the heat is the standard deviation in a gaussian
     * distribution.
     */
    private static final float HEAT = 0.01F;

    /**
     * The random number generator
     */
    private Random rand = new Random();
}
