
package org.crosswire.jsword.map.model;

/**
 * LinkAttractionRule.
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
public class LinkAttractionRule extends AbstractRule
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
    public Position getDesiredPosition(Map map, int b, int c)
    {
        // Go through all the links and find their average positions when
        // weighted by their strength
        Link[] links = la.getLinks(b, c);
        int dimensions = map.getDimensions();
        float[] total_pos = new float[dimensions];
        int total_strength = 0;
        for (int i=0; i<links.length; i++)
        {
            int dest_book = links[i].getDestinationBook();
            int dest_chap = links[i].getDestinationChapter();
            float[] dest_pos = map.getPositionArrayCopy(dest_book, dest_chap);

            for (int d=0; d<total_pos.length; d++)
            {
                total_pos[d] = total_pos[d] + (dest_pos[d] * links[i].getStrength());
            }

            total_strength += links[i].getStrength();
        }
        
        // Now we know the total position and strength, we can work out the mean
        float[] pos = new float[dimensions];
        for (int d=0; d<pos.length; d++)
        {
            pos[d] = total_pos[d] / total_strength;
        }

        return new Position(pos);
    }

    /**
     * Returns the link array.
     * @return LinkArray
     */
    public LinkArray getLinkArray()
    {
        return la;
    }

    /**
     * Sets the link array.
     * @param la The link array to set
     */
    public void setLinkArray(LinkArray la)
    {
        this.la = la;
    }

    /**
     * The number of links we remember for a node
     */
    private LinkArray la;
}
