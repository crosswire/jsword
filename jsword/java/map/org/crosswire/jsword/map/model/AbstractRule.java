
package org.crosswire.jsword.map.model;

/**
* AbstractRule. 
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
public abstract class AbstractRule implements Rule
{
    /**
    * We sometimes need to take a single reply and multiply it up
    * according to the current scale.
    * @param single The single result to populate an array with
    * @return A suitably filled array of scale length
    */
    public Position[] scale(Position single)
    {
        int scale_copy = scale;

        Position[] reply = new Position[scale_copy];
        for (int i=0; i<scale_copy; i++)
        {
            reply[i] = single;
        }

        return reply;
    }

    /**
    * We sometimes need to take a single reply and multiply it up
    * according to the current scale.
    * @param single The single result to populate an array with
    * @return A suitably filled array of scale length
    */
    public Position[] scale(Position[] array)
    {
        int scale_copy = scale;

        if (array.length == scale_copy)
        {
            return array;
        }

        Position[] reply = new Position[scale_copy];

        int array_index = 0;
        for (int i=0; i<scale_copy; i++)
        {
            reply[i] = array[array_index];
            array_index++;
            if (array_index >= array.length)
                array_index = 0;
        }

        return reply;
    }

    /**
    * Each call to getDesiredPosition() returns an array of Positions,
    * this method sets the preferred length of that returned array.
    * @param scale The preferred length of the desired position array
    * @see getDesiredPosition(Map, int)
    */
    public void setScale(int scale)
    {
        this.scale = scale;
    }

    /**
    * Each call to getDesiredPosition() returns an array of Positions,
    * this method gets the preferred length of that returned array.
    * @return The preferred length of the desired position array
    * @see getDesiredPosition(Map, int)
    */
    public int getScale()
    {
        return scale;
    }

    /** The length of the desired position array */
    protected int scale = 50;
}
