package org.crosswire.jsword.book.search;

import java.io.IOException;

import org.crosswire.common.progress.Job;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;

/**
 * An index into a body of text that knows what words exist and where they are.
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
public interface Index
{
    /**
     * An initializer type method so we can configure the Search engine at
     * runtime. This method is run first of all, before anything else and should
     * do everything it can to ensure that future method calls will be error
     * free without consuming significant system resources.
     */
    public void init(Book book) throws BookException;

    /**
     * For a given word find a list of references to it.
     * If the <code>word</code> being searched for is null then an empty Key
     * <b>MUST</b> be returned. Users of this index may use this functionality
     * to get empty KeyLists which they then use to aggregate other searches
     * done on this index.
     * @param word The text to search for
     * @return The references to the word
     */
    public Key findWord(String word) throws BookException;

    /**
     * An index must be able to create KeyLists for users in a similar way to
     * the Book that it is indexing.
     * @param name The string to convert to a Key
     * @return A new Key representing the given string, if possible
     * @throws NoSuchKeyException If the string can not be turned into a Key
     */
    public Key getKey(String name) throws NoSuchKeyException;

    /**
     * Tidy up after yourself and remove all the files that make up any indexes
     * you created.
     */
    public void delete() throws BookException;

    /**
     * Detects if index data has been stored for this Bible already
     */
    public boolean isIndexed();

    /**
     * Read from the given source version to generate ourselves. On completion
     * of this method the index should be usable. If this is not the natural
     * way this emthod finishes then it should be possible to call loadIndexes()
     * @param ajob The place to report progress
     * @throws IOException if the load fails to read from disk
     * @throws BookException if there is a problem reading from the Bible
     */
    public void generateSearchIndex(Job ajob) throws IOException, BookException, NoSuchKeyException;
}
