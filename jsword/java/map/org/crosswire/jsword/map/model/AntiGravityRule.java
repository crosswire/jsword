
package org.crosswire.jsword.map.model;

/**
* AntiGravityRule.
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
* @version D0.I0.T0
*/
public class AntiGravityRule extends AbstractRule
{
    /**
    * Specify where it would like a node to be positioned in space.
    * @param map The Map to select a node from
    * @param ord The ordinal number (1 - 31104) of the verse
    * @return An array of desired positions.
    */
    public Position[] getDesiredPosition(Map map, int ord)
    {
        if (scale == 0)
            return new Position[] { };

        // The start point
        float[] fpos = map.getPosition(ord);

        // Where we move away from
        float[] cog = map.getCenterOfGravity().getPosition();

        // The desired position
        float[] reply = new float[fpos.length];
        for (int i=0; i<fpos.length; i++)
        {
            float distance = fpos[i] - cog[i];
            reply[i] = (0.3F * (float) Math.atan(distance*GRADIENT)) + 0.5F;
        }

        return scale(new Position(reply));
    }

    /** How sharply do we fall away with the result curve */
    private static final float GRADIENT = 10F;
}
