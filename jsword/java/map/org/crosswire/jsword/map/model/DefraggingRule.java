
package org.crosswire.jsword.map.model;

import org.crosswire.jsword.passage.Books;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.common.util.Reporter;

/**
 * DefraggingRule attempts to keep all the nodes in straight lines.
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
public class DefraggingRule extends AbstractRule
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
     * @param ord The ordinal number (1 - 31104) of the verse
     * @return An array of desired positions.
     */
    public Position[] getDesiredPosition(Map map, int ord)
    {
        if (scale == 0)
            return new Position[] { };

        try
        {
            Position reply;
            Verse verse = new Verse(ord);
            int b = verse.getBook();
            int c = verse.getChapter();
            int v = verse.getVerse();

            boolean at_start = (v == 1 && c == 1);
            boolean at_end = (v == Books.versesInChapter(b, c) && c == Books.chaptersInBook(b));

            if (at_start || at_end)
            {
                // Where we are now
                float[] current = map.getPosition(ord);

                // What are we trying to get away from
                /*
                int other_ord;
                if (at_start)
                {
                    int end_chapter = Books.chaptersInBook(b);
                    int end_verse = Books.versesInChapter(b, end_chapter);
                    Verse end = new Verse(b, end_chapter, end_verse);
                    other_ord = end.getOrdinal();
                }
                else
                {
                    other_ord = new Verse(b, 1, 1).getOrdinal();
                }

                float[] opposite = map.getPosition(end_ord);
                float[] pos = new float[];

                // Um this is getting a little for me in n-dimensions
                */

                reply = new Position(current);
            }
            else
            {
                Position prev = new Position(map.getPosition(ord-1));
                Position next = new Position(map.getPosition(ord+1));
                Position[] both = new Position[] { prev, next };

                reply = Map.average(both);
            }

            return scale(reply);
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
            return new Position[] { };
        }
    }
}
