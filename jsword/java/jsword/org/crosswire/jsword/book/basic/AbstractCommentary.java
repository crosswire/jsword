
package org.crosswire.jsword.book.basic;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Commentary;
import org.crosswire.jsword.book.Key;
import org.crosswire.jsword.book.PassageKey;
import org.crosswire.jsword.book.Search;
import org.crosswire.jsword.book.data.BookData;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.Verse;

/**
 * An AbstractCommentary implements a few of the more generic methods of Commentary.
 * This class does a lot of work in helping make search easier, and implementing
 * some basic write methods. 
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
public abstract class AbstractCommentary implements Commentary
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getBookMetaData()
     */
    public BookMetaData getBookMetaData()
    {
        return getCommentaryMetaData();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Commentary#hasComments(org.crosswire.jsword.passage.Verse)
     */
    public boolean hasComments(Verse verse) throws BookException
    {
        Passage ref = PassageFactory.createPassage();
        ref.add(verse);
        BookData bdata = getComments(ref);
        
        return bdata != null;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getKey(java.lang.String)
     */
    public Key getKey(String text) throws BookException
    {
        try
        {
            return new PassageKey(text);
        }
        catch (NoSuchVerseException ex)
        {
            throw new BookException(I18N.NO_VERSE, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#find(org.crosswire.jsword.book.Search)
     */
    public Key find(Search search) throws BookException
    {
        Passage ref = findPassage(search);
        return new PassageKey(ref);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getData(org.crosswire.jsword.book.Key)
     */
    public BookData getData(Key key) throws BookException
    {
        if (key instanceof PassageKey)
        {
            Passage ref = ((PassageKey) key).getPassage();
            return getComments(ref);
        }
        else
        {
            return null;
        }
    }
}
