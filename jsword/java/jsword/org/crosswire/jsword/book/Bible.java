
package org.crosswire.jsword.book;

import java.util.Iterator;

import org.crosswire.jsword.book.data.BibleData;
import org.crosswire.jsword.book.data.BookData;
import org.crosswire.jsword.passage.Passage;

/**
 * Bible is the core interface to a Bible store.
 * <p>The methods of this interface come into 3 categories:
 * Meta-Information methods return information about the implementation
 * and its environment. Retrieval methods are the core methods that give
 * access to the real Biblical text. These are the core of the interface.
 * Generation methods are there to allow this Version to be generated.
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
public interface Bible extends Book
{
    /**
     * Meta-Information: What version of the Bible is this?
     * @return A Version for this Bible
     */
    public BookMetaData getMetaData();

    /**
     * Retrieval: Get BookData for the given Key. To get a plain string
     * you need to use:
     * <pre>
     *   String s = book.getData(ref).getPlainText();
     * </pre>
     * @param key The position to search for
     * @return The found BookData document
     * @throws BookException If anything goes wrong with this method
     */
    public BookData getData(Key key) throws BookException;

    /**
     * Retrieval: Get BibleData for the given Passage. To get a plain string
     * you need to use:
     * <pre>
     *   String s = book.getData(ref).getPlainText();
     * </pre>
     * @param ref The verses to search for
     * @return The found BibleData document
     * @throws BookException If anything goes wrong with this method
     */
    public BibleData getData(Passage ref) throws BookException;

    /**
     * Retrieval: To tie in with the Book find method.
     * We may decide that this is the preferred search mechanism over
     * findPassage(), but I'm not sure yet.
     * @param word The word to search for
     * @return The found key
     * @throws BookException If anything goes wrong with this method
     */
    public Key find(String word) throws BookException;

    /**
     * Retrieval: For a given word find a list of references to it
     * @param word The text to search for
     * @return The references to the word
     * @throws BookException If anything goes wrong with this method
     */
    public Passage findPassage(String word) throws BookException;

    /**
     * Retrieval: Return an array of words that are used by this Bible
     * that start with the given string. For example calling:
     * <code>getStartsWith("love")</code> will return something like:
     * { "love", "loves", "lover", "lovely", ... }
     * This is only needed to make your this driver play well
     * in searches it is not vital for normal display. To save yourself
     * the bother of implementing this properly you could do:
     *   <code>return new String[] { base };</code>
     * A fully featured implementation will reply to getStartsWith("")
     * with every word.
     * @param base The word to base your word array on
     * @return An array of words starting with the base
     * @throws BookException If anything goes wrong with this method
     */
    public Iterator getStartsWith(String base) throws BookException;
}
