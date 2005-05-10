/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $ID$
 */
package org.crosswire.jsword.book.search.parse;

import java.util.Collection;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.search.Grammar;
import org.crosswire.jsword.book.search.Thesaurus;
import org.crosswire.jsword.book.search.ThesaurusFactory;
import org.crosswire.jsword.passage.Key;

/**
 * The Search Word for a Word to search for. The default
 * if no other SearchWords match.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class GrammarParamWord implements ParamWord
{
    /**
     * Default ctor
     */
    public GrammarParamWord() throws InstantiationException
    {
        thesaurus = ThesaurusFactory.createThesaurus();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.parse.ParamWord#getWord(org.crosswire.jsword.book.search.parse.IndexSearcher)
     */
    public String getWord(IndexSearcher engine) throws BookException
    {
        throw new BookException(Msg.GRAMMAR_WORD);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.parse.ParamWord#getPassage(org.crosswire.jsword.book.search.parse.IndexSearcher)
     */
    public Key getKeyList(IndexSearcher engine) throws BookException
    {
        String root = Grammar.getRoot(engine.iterateWord());

        Collection col = thesaurus.getSynonyms(root);
        String[] words = (String[]) col.toArray(new String[col.size()]);

        return engine.getPassage(words);
    }

    /**
     * The source of thesaurus data
     */
    private Thesaurus thesaurus;
}
