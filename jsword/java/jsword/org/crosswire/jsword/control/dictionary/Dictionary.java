
package org.crosswire.jsword.control.dictionary;

import java.util.Iterator;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Bibles;
import org.crosswire.jsword.book.Bible;

/**
 * The Dictionary class is the beginnings of a powerful base of knowedge
 * about various Languages. We need to move the data from RawBible.Word
 * to here, but I'm not clear how to do that in a version independant
 * way.
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
public class Dictionary
{
    /**
     * Basic constructor
     */
    public Dictionary()
    {
    }

    /**
     * Find a list of words that start with the given word. I've made this
     * private for the time being until we can make this work sensibly.
     * @param word The word to search for
     * @return An array of matches
     */
    private Iterator getStartsWith(String word) throws BookException
    {
        Bible bible = Bibles.getDefaultBible();
        return bible.getStartsWith(word);
    }
}
