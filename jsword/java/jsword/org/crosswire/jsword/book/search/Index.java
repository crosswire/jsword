package org.crosswire.jsword.book.search;

import java.util.Collection;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Passage;

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
     * Return an array of words that are used by this Bible that start with the
     * given string. For example calling:
     * <code>getStartsWith("love")</code> will return something like:
     * { "love", "loves", "lover", "lovely", ... }
     * @param word The word to base your word array on
     * @return An array of words starting with the base
     */
    public Collection getStartsWith(String word) throws BookException;

    /**
     * For a given word find a list of references to it
     * @param word The text to search for
     * @return The references to the word
     */
    public Passage findWord(String word) throws BookException;
}