
package org.crosswire.jsword.map.model;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.LogicError;
import org.crosswire.jsword.passage.BibleInfo;
import org.crosswire.jsword.passage.NoSuchVerseException;

/**
 * AntiGravityRule.
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
public class AntiGravityRule extends AbstractRule
{
    /**
     * Specify where it would like a node to be positioned in space.
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

        // The start point
        float[] us = map.getPositionArrayCopy(book, chapter);

        float[] totals = new float[us.length];
        int count = 0;

        try
        {
            //log.debug(",b,c,x,y,c,tx,ty");

            // For each verse
            for (int b=1; b<=BibleInfo.booksInBible(); b++)
            {
                for (int c=1; c<=BibleInfo.chaptersInBook(b); c++)
                {
                    if (b != book || c != chapter)
                    {
                        float[] that = map.getPositionArrayCopy(b, c);
                        addDistanceToTotals(that, us, totals);

                        count++;

                        /*
                        if (book == 1 && chapter == 1)
                        {
                            log.debug(","+b+","+c+","+that[0]+","+that[1]+","+count+","+totals[0]+","+totals[1]);
                        }
                        //*/
                    }
                }
            }
    
            // Average the totals out, and add in the original position
            for (int d=0; d<totals.length; d++)
            {
                totals[d] = totals[d] / count;
                totals[d] += us[d];
            }
    
            return new Position(totals);
        }
        catch (NoSuchVerseException ex)
        {
            throw new LogicError(ex);
        }
    }

    /**
     * Run through the dimensions totting up the new positions
     */
    public static void addDistanceToTotals(float[] that, float[] us, float[] totals)
    {
        float xdiff = us[0] - that[0];
        float ydiff = us[1] - that[1];
        float idist = (float) Math.sqrt(xdiff*xdiff + ydiff*ydiff);

        float newsep = getNewDistance(idist);

        // so we know the old separation (idist) and the desired separation
        // (newsep) we just need to know the new desired positions to add
        // into the totals.
        totals[0] += (newsep/idist) * (that[0]-us[0]);
        totals[1] += (newsep/idist) * (that[1]-us[1]);
    }

    /**
     * Calculate the new ditance given an old distance
     */
    public static float getNewDistance(float idist)
    {
        if (idist > 0)
        {
            return (float) -Math.exp(-idist*STRENGTH) / 2;
        }
        else
        {
            return (float) Math.exp(idist*STRENGTH) / 2;
        }
    }

    /** The log stream */
    private static Logger log = Logger.getLogger(AntiGravityRule.class);

    /** How sharply do we fall away with the result curve */
    private static final float STRENGTH = 20F;
}
