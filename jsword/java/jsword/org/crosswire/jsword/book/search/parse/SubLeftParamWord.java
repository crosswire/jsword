package org.crosswire.jsword.book.search.parse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Passage;

/**
 * The Search Word for a Word to search for. The default if no other SearchWords
 * match.
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
public class SubLeftParamWord implements ParamWord
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.parse.ParamWord#getWord(org.crosswire.jsword.book.search.parse.Parser)
     */
    public String getWord(LocalParser engine) throws BookException
    {
        throw new BookException(Msg.LEFT_PARAM);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.parse.ParamWord#getPassage(org.crosswire.jsword.book.search.parse.Parser)
     */
    public Passage getPassage(LocalParser engine) throws BookException
    {
        Iterator it = engine.iterator();
        List output = new ArrayList();

        int parenLevel = 1;
        while (true)
        {
            if (!it.hasNext())
            {
                throw new BookException(Msg.LEFT_BRACKETS);
            }

            Word word = (Word) it.next();

            if (word instanceof SubLeftParamWord)
            {
                parenLevel++;
            }

            if (word instanceof SubRightParamWord)
            {
                parenLevel--;
            }

            if (parenLevel == 0)
            {
                break;
            }

            output.add(word);
        }

        LocalParser subEngine = new LocalParser();
        subEngine.init(engine.getIndex());
        subEngine.setSearchMap(engine.getSearchMap());
        Passage subRef = subEngine.search(output);

        return subRef;
    }
}
