package org.crosswire.jsword.book.search.parse;

import java.util.Collection;
import java.util.Iterator;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.SentanceUtil;
import org.crosswire.jsword.book.search.Grammar;
import org.crosswire.jsword.book.search.Thesaurus;
import org.crosswire.jsword.book.search.ThesaurusFactory;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.PassageTally;
import org.crosswire.jsword.passage.RestrictionType;

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
public class PhraseParamWord implements ParamWord
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.parse.ParamWord#getWord(org.crosswire.jsword.book.search.parse.Searcher)
     */
    public String getWord(IndexSearcher engine) throws BookException
    {
        throw new BookException(Msg.SINGLE_PARAM);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.parse.ParamWord#Key(org.crosswire.jsword.book.search.parse.Searcher)
     */
    public Key getKeyList(IndexSearcher engine) throws BookException
    {
        Iterator it = engine.iterator();
        StringBuffer buff = new StringBuffer();

        while (true)
        {
            if (!it.hasNext())
            {
                throw new BookException(Msg.LEFT_BRACKETS);
            }

            Word word = (Word) it.next();

            if (word instanceof PhraseParamWord)
            {
                break;
            }

            buff.append(word);
            buff.append(" "); //$NON-NLS-1$
        }

        return bestMatch(engine, buff.toString());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.Matcher#bestMatch(java.lang.String, org.crosswire.jsword.passage.Key)
     */
    public Key bestMatch(IndexSearcher engine, String sought) throws BookException
    {
        if (thesaurus == null)
        {
            try
            {
                thesaurus = ThesaurusFactory.createThesaurus();
            }
            catch (InstantiationException ex)
            {
                throw new BookException(Msg.NO_THESAURUS, ex);
            }
        }

        String[] words = SentanceUtil.getWords(sought);
        words = Grammar.stripSmallWords(words);
        // log.fine("words="+StringUtil.toString(words));

        PassageTally tally = new PassageTally();
        tally.blur(BLUR_BY, RestrictionType.NONE);

        for (int i = 0; i < words.length; i++)
        {
            tally.addAll(engine.getIndex().findWord(words[i]));
        }

        // This uses updatePassageTallyFlat() so that words like God
        // that have many startsWith() matches, and hence many verse
        // matches, do not end up with wrongly high scores.
        for (int i = 0; i < words.length; i++)
        {
            // log.fine("  root="+root);
            Collection col = thesaurus.getSynonyms(words[i]);
            String[] grWords = (String[]) col.toArray(new String[col.size()]);

            // log.fine("  gr_words="+StringUtil.toString(gr_words));
            PassageTally temp = new PassageTally();

            for (int j = 0; j < grWords.length; j++)
            {
                temp.addAll(engine.getIndex().findWord(grWords[j]));
            }

            temp.flatten();
            tally.addAll(temp);
        }

        return tally;
    }

    /**
     * How we get related words
     */
    private Thesaurus thesaurus;

    /**
     * How many verses do we blur by?
     */
    private static final int BLUR_BY = 2;
}
