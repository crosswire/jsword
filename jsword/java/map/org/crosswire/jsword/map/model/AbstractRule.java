
package org.crosswire.jsword.map.model;

/**
 * AbstractRule. 
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
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public abstract class AbstractRule implements Rule
{
    /**
     * We sometimes need to take a single reply and multiply it up
     * according to the current scale.
     * @see org.crosswire.jsword.map.model.Rule#getScaledPosition(Map, int, int)
     */
    public Position[] getScaledPosition(Map map, int book, int chapter)
    {
        if (scale == 0)
        {
            return new Position[0];
        }

        Position single = getDesiredPosition(map, book, chapter);

        Position[] reply = new Position[scale];
        for (int i=0; i<scale; i++)
        {
            reply[i] = single;
        }

        return reply;
    }

    /**
     * Each call to getDesiredPosition() returns an array of Positions,
     * this method sets the preferred length of that returned array.
     * @param scale The preferred length of the desired position array
     */
    public void setScale(int scale)
    {
        this.scale = scale;
    }

    /**
     * Each call to getDesiredPosition() returns an array of Positions,
     * this method gets the preferred length of that returned array.
     * @return The preferred length of the desired position array
     */
    public int getScale()
    {
        return scale;
    }

    /** The length of the desired position array */
    private int scale = 0;
}
