
package org.crosswire.jsword.book;

import java.util.Iterator;

import org.crosswire.jsword.book.data.BookData;

/**
 * Book is the most basic store of textual data - It can retrieve data
 * either as an XML document or as plain text - It uses Keys to refer
 * to parts of itself, and can search for words (returning Keys).
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
public interface Book
{
    /**
     * Meta-Information: What version of the Bible is this?
     * @return A Version for this Bible
     */
    public BookMetaData getBookMetaData();

    /**
     * Someone has typed in a reference to find, but we need a Key to actually
     * look it up.
     * @param text The string to create a Key from
     * @return The Key corresponding to the input text
     * @throws BookException If there is a problem converting the text
     */
    public Key getKey(String text) throws BookException;

    /**
     * Retrieval: Add to the given document some mark-up for the specified
     * Verses.
     * @param ref The verses to search for
     * @return The found Book data
     * @throws BookException If anything goes wrong with this method
     */
    public BookData getData(Key ref) throws BookException;

    /**
     * Retrieval: For a given word find a list of references to it.
     * PENDING(joe): alter the search interface to subsume view.search
     * @param word The text to search for
     * @return The references to the word
     * @throws BookException If anything goes wrong with this method
     */
    public Key find(String word) throws BookException;

    /**
     * Retrieval: Return an array of words that are used by this Bible
     * that start with the given string.
     * For example calling: <code>getStartsWith("love")</code> will return
     * something like:
     * { "love", "loves", "lover", "lovely", ... }
     * <p>This is only needed to make your this name play well
     * in searches it is not vital for normal display. To save yourself
     * the bother of implementing this properly you could do:
     *   <code>return new String[] { base };</code>
     * <p>The Iterator can be converted into a String[] easily using the
     * toStringArray() method in BookUtil.
     * <p>A fully featured implementation will reply to getStartsWith("")
     * with every word.
     * @param base The word to base your word array on
     * @see BookUtil#toStringArray(Iterator)
     * @return An array of words starting with the base
     * @throws BookException If anything goes wrong with this method
     */
    public Iterator getStartsWith(String base) throws BookException;
}
