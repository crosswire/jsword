
package org.crosswire.jsword.book.sword;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Commentary;
import org.crosswire.jsword.book.CommentaryMetaData;
import org.crosswire.jsword.book.Search;
import org.crosswire.jsword.book.basic.AbstractCommentary;
import org.crosswire.jsword.book.data.BibleData;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;

/**
 * A Sword version of a Commentary.
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
public class SwordCommentary extends AbstractCommentary implements Commentary
{
    /**
     * @param data
     */
    public SwordCommentary(SwordCommentaryMetaData data)
    {
        this.data = data;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Commentary#getCommentaryMetaData()
     */
    public CommentaryMetaData getCommentaryMetaData()
    {
        return data;
    }

    /** Our meta data */
    private SwordCommentaryMetaData data;

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Commentary#getComments(org.crosswire.jsword.passage.Passage)
     */
    public BibleData getComments(Passage ref) throws BookException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Bible#findPassage(org.crosswire.jsword.book.Search)
     */
    public Passage findPassage(Search word) throws BookException
    {
        return PassageFactory.createPassage();
    }
}
