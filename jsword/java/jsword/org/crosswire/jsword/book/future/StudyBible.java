
package org.crosswire.jsword.book.future;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.future.*;
import org.crosswire.jsword.passage.Passage;

/**
 * StudyBible is-an extension to Bible that knows about the original
 * Greek/Hebrew in the form of Strongs numbers.
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
public interface StudyBible
{
    /**
     * A WordStudy is-an extension to Bible that knows about the original
     * Greek/Hebrew in the form of Strongs numbers, and how these original
     * words were translated into English (or whatever).
     * <p>You might expect a method something like <code>
     * Passage getPassage(String word)</code> for completeness, however
     * this is a core part of the Bible interface and so redundant here.
     * <p>The different conversions go something like this:
     * <pre>
     *   .-------------------.
     *  |    -> Strongs -------> Passage ---> PageDOM -\
     *  |   /          `-._,-'                          \---> HTML
     * String          .-' `-.                          /
     *      '-> (Word) --------> Translation ----------/
     * </pre>
     * </p>
     * StudyBible: For a given strongs number find a list of references to it
     * @param number The number to search for
     * @return The references to the word
     */
    public Passage findPassage(Strongs number) throws BookException;

    /**
     * StudyBible: For a given word find a list words it is translated from
     * @param word The text to search for
     * @return The source numbers of that word
     */
    public Translation getTranslation(String word) throws BookException;

    /**
     * StudyBible: For a given number find a list of ways it is translated
     * @param number The strongs number to search for
     * @return The words that the number is translated to
     */
    public Translation getTranslations(Strongs number) throws BookException;

    /**
     * <p>This class is designed to work with a Translation class:
     * <br>The Translation class stores the following:<ul>
     * <li>word (either a String or a Strongs)
     * <li>a list of words it is translated from (Strongs or Strings)
     * <li>a list of words it is translated to (Strongs or Strings)
     * </ul>. 
     */
    class Translation
    {
    }
}
