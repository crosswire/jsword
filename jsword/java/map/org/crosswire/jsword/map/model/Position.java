
package org.crosswire.jsword.map.model;

import java.io.Serializable;

/**
* A Position is simply an array of floats that specify a place for a
* Node to be.
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
public class Position implements Serializable
{
    /**
    * Basic constructor
    */
    public Position(float[] pos)
    {
        this.pos = pos;
    }

    /**
    * Accessor for the array of positions
    * @return The array of positions
    */
    public float[] getPosition()
    {
        return pos;
    }

    /**
    * Accessor for the array of positions
    * @return The array of positions
    */
    public void setPosition(float[] pos)
    {
        this.pos = pos;
    }

    /** The array of floats */
    protected float[] pos;

    /** Serialization ID - a serialization of pos */
    static final long serialVersionUID = -2737633670295539140L;
}
