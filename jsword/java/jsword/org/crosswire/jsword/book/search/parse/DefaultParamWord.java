
package org.crosswire.jsword.book.search.parse;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Passage;

/**
 * The Search Word for a Word to search for. The default
 * if no other SearchWords match.
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
public class DefaultParamWord implements ParamWord
{
    /**
     * Create a the default rule with the (presumably) Bible
     * word that formed part of the original search string
     * @param text The word to search (or otherwise) for
     */
    public DefaultParamWord(String text)
    {
        this.text = text;
    }

    /**
     * Get a word for something else to word on.
     * @return The word to search for
     */
    public String getWord(LocalParser engine)
    {
        return text;
    }

    /**
     * To help error reporting.
     * @return The word to search for
     */
    public String toString()
    {
        return text;
    }

    /**
     * Get a Passage for something else to word on.
     * @return An array of alternative words
     */
    public Passage getPassage(LocalParser engine) throws BookException
    {
        return engine.wordSearch(text);
    }

    /**
     * The word that we represent
     */
    private String text = null;
}
