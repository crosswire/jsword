
package org.crosswire.jsword.book.sword;

import org.crosswire.jsword.passage.BibleInfo;
import org.crosswire.jsword.passage.Verse;

/**
 * A verse index is constructed using a Verse, and can tell Sword packages what
 * testament and index the verse represents.
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
class VerseIndex
{
    /**
     * @param v
     */
    public VerseIndex(Verse v)
    {
        int ord = v.getOrdinal();
        int book = v.getBook();
        int chapter = v.getChapter();
        int verse = v.getVerse();

        if (ord >= SwordConstants.ORDINAL_MAT11)
        {
            // This is an NT verse
            testament = SwordConstants.TESTAMENT_NEW;
            book = book - BibleInfo.Names.Malachi;
        }
        else
        {
            // This is an OT verse
            testament = SwordConstants.TESTAMENT_OLD;
        };

        // work out the offset
        int bookOffset = SwordConstants.bks[testament][book];
        long chapOffset = SwordConstants.cps[testament][bookOffset + chapter];

        index = verse + chapOffset;
    }

    /**
     * @return int
     */
    public long getIndex()
    {
        return index;
    }

    /**
     * @return int
     */
    public int getTestament()
    {
        return testament;
    }

    private long index;
    private int testament;
}
