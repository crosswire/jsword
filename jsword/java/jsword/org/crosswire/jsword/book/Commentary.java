
package org.crosswire.jsword.book;

import org.crosswire.jsword.book.data.BookData;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.Verse;

/**
 * Commentary in the core interface to works that provide text on a verse-by-
 * verse basis.
 * <p>The interface is very similar to that of Bible with one addition -
 * hasData() which is useful because not all verses will have entries in all
 * implementations, and we mught speed-up UI responsiveness by know if there is
 * any point in calling getData().
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
 * @see org.crosswire.jsword.book.Bible
 */
public interface Commentary extends Book
{
    /**
     * The meta data about this Book.
     * @return A Version for this Commentary
     * @see Book#getBookMetaData()
     */
    public CommentaryMetaData getCommentaryMetaData();

    /**
     * Get BibleData for the given Passage.
     * To consider: Perhaps we should make the param to getData() be a Verse
     * too...
     * @param ref The verses to search for
     * @return The found BibleData document
     * @throws BookException If anything goes wrong with this method
     * @see Bible#getData(Passage)
     * @see Book#getData(Key)
     */
    public BookData getComments(Passage ref) throws BookException;

    /**
     * Does the specified verse have any data behind it.
     * @param verse The verse to query
     * @return The found BibleData document
     * @throws BookException If anything goes wrong with this method
     */
    public boolean hasComments(Verse verse) throws BookException;

    /**
     * Retrieval: For a given word find a list of references to it
     * @param search The text to search for
     * @return The references to the word
     * @throws BookException If anything goes wrong with this method
     * @see Book#find(Search)
     */
    public Passage findPassage(Search search) throws BookException;
}
