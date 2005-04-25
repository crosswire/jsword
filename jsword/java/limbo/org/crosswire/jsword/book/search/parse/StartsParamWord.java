package org.crosswire.jsword.book.search.parse;

import java.util.Collection;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.search.Thesaurus;
import org.crosswire.jsword.book.search.ThesaurusFactory;
import org.crosswire.jsword.passage.Key;

/**
 * The Search Word for a Word to search for.
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
public class StartsParamWord implements ParamWord
{
    /**
     * Default ctor
     */
    public StartsParamWord() throws InstantiationException
    {
        thesaurus = ThesaurusFactory.createThesaurus();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.parse.ParamWord#getWord(org.crosswire.jsword.book.search.parse.Searcher)
     */
    public String getWord(IndexSearcher engine) throws BookException
    {
        throw new BookException(Msg.STARTS_WORD);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.parse.ParamWord#getKeyList(org.crosswire.jsword.book.search.parse.IndexSearcher)
     */
    public Key getKeyList(IndexSearcher engine) throws BookException
    {
        String word = engine.iterateWord();

        Collection col = thesaurus.getSynonyms(word);
        String[] words = (String[]) col.toArray(new String[col.size()]);

        return engine.getPassage(words);
    }

    /**
     * The source of thesaurus data
     */
    private Thesaurus thesaurus;
}
