
package org.crosswire.jsword.control.search;

import org.crosswire.jsword.book.BookUtil;
import org.crosswire.jsword.control.dictionary.Grammar;
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class GrammarParamWord implements ParamWord
{
    /**
    * Get a word for something else to word on.
    */
    public GrammarParamWord()
    {
    }

    /**
    * Get a word for something else to word on.
    * @param engine The controller that can provide access to the search
    *               string or a default Bible.
    * @return The requested text
    * @exception SearchException If this action is not appropriate
    */
    public String getWord(Engine engine) throws SearchException
    {
        throw new SearchException("search_grammar_word");
    }

    /**
    * Get a Passage for something else to work on. WARNING the return from
    * this method is a PassageTally which is not a 100% match for the
    * Passage interface. Maybe this needs to be fixed somehow.
    * @param engine The controller that can provide access to the search
    *               string or a default Bible.
    * @return A Passage relevant to this command
    * @exception SearchException If this action is not appropriate
    */
    public Passage getPassage(Engine engine) throws SearchException
    {
        if (!engine.iterator().hasNext())
            throw new SearchException("search_grammar_blank");

        try
        {
            ParamWord param = (ParamWord) engine.iterator().next();
            String root = Grammar.getRoot(param.getWord(engine));
            String[] words = BookUtil.toStringArray(engine.getBible().getStartsWith(root));
            return BookUtil.getPassage(engine.getBible(), words);
        }
        catch (Exception ex)
        {
            throw new SearchException("search_grammar_other", ex);
        }
    }
}
