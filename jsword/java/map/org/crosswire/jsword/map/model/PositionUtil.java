
package org.crosswire.jsword.map.model;

/**
 * Various position related utils.
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
public class PositionUtil
{
    /**
     * We shouldn't be creating these.
     */
    private PositionUtil()
    {
    }

    /**
     * Find the average position of an 2D array of Positions. All the positions
     * in the 2D array are considered equal.
     */
    public static Position average(Position[][] array, int dimensions)
    {
        double[] tot = new double[dimensions];
        int count = 0;

        for (int b=1; b<array.length; b++)
        {
            for (int c=1; c<array[b].length; c++)
            {
                for (int j=0; j<dimensions; j++)
                {
                    tot[j] += array[b][c].pos[j];
                }

                count++;
            }
        }

        float[] retcode = new float[dimensions];

        for (int j=0; j<dimensions; j++)
        {
            retcode[j] = (float) (tot[j] / count);
        }

        return new Position(retcode);
    }

    /**
     * Find the average position of an array of Positions
     */
    public static Position average(Position[] array, int dimensions)
    {
        double[] tot = new double[dimensions];

        // for all the array members and all the dimensions
        // add up the positions
        for (int i=0; i<array.length; i++)
        {
            for (int j=0; j<dimensions; j++)
            {
                tot[j] += array[i].pos[j];
            }
        }

        // divide by the number of members
        float[] retcode = new float[dimensions];
        for (int j=0; j<dimensions; j++)
        {
            retcode[j] = (float) (tot[j] / array.length);
        }

        return new Position(retcode);
    }
}
