
package org.crosswire.jsword.book.basic;

import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Key;
import org.crosswire.jsword.book.PassageKey;
import org.crosswire.jsword.book.data.BookData;
import org.crosswire.jsword.passage.Passage;

/**
 * An AbstractBible implements a few of the more generic methods of Bible.
 * @todo: probably delete this class, it doesn't do much now and I doubt it ever will
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
public abstract class AbstractBible implements Bible
{
    /**
     * To tie in with the Book find method
     * @param word The word to search for
     * @return The found key
     * @throws BookException If anything goes wrong with this method
     */
    public Key find(String word) throws BookException
    {
        Passage ref = findPassage(word);
        return new PassageKey(ref);
    }

    /**
     * Retrieval: Get BookData for the given Key.
     * @param key The position to search for
     * @return The found BookData document
     * @throws BookException If anything goes wrong with this method
     */
    public BookData getData(Key key) throws BookException
    {
        if (key instanceof PassageKey)
        {
            Passage ref = ((PassageKey) key).getPassage();
            return getData(ref);
        }
        else
        {
            return null;
        }
    }
}
