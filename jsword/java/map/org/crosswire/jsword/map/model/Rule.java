
package org.crosswire.jsword.map.model;

/**
 * A Rule has the ability to specify where it would like a node to be
 * positioned in space. 
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
public interface Rule
{
    /**
     * Specify where it would like a node to be positioned in space.
     * The Position is added to the results from all Rules and averaged
     * out. A reply of null indicated no preference.
     * @param map The Map to select a node from
     * @param book The book number
     * @param chapter The chapter 
     * @return Desired position.
     */
    public Position getDesiredPosition(Map map, int book, int chapter);

    /**
     * Specify where it would like a node to be positioned in space weighted
     * buy the current scale
     * @param map The Map to select a node from
     * @param book The book number
     * @param chapter The chapter 
     * @return Desired position.
     */
    public Position[] getScaledPosition(Map map, int book, int chapter);

    /**
     * Each call to getDesiredPosition() returns an array of Positions,
     * this method sets the preferred length of that returned array.
     * @param scale The preferred length of the desired position array
     * @see #getDesiredPosition(Map, int)
     */
    public void setScale(int scale);

    /**
     * Each call to getDesiredPosition() returns an array of Positions,
     * this method gets the preferred length of that returned array.
     * @return The preferred length of the desired position array
     * @see #getDesiredPosition(Map, int)
     */
    public int getScale();
}
