package org.crosswire.jsword.book.search.index;

import java.util.Collection;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Search;
import org.crosswire.jsword.book.SentanceUtil;
import org.crosswire.jsword.book.search.Grammar;
import org.crosswire.jsword.book.search.Index;
import org.crosswire.jsword.book.search.Matcher;
import org.crosswire.jsword.book.search.Thesaurus;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.PassageTally;
import org.crosswire.jsword.passage.RestrictionType;

/**
 * An implementation of Matcher that uses an Index and a Thesaurus.
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
public class IndexMatcher implements Matcher
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.Matcher#init(org.crosswire.jsword.book.search.Index)
     */
    public void init(Index newIndex, Thesaurus newThesaurus)
    {
        this.index = newIndex;
        this.thesaurus = newThesaurus;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.Matcher#bestMatch(java.lang.String, org.crosswire.jsword.passage.Key)
     */
    public Key bestMatch(String sought, Key restriction) throws BookException
    {
        String[] words = SentanceUtil.getWords(sought);
        words = Grammar.stripSmallWords(words);
        // log.fine("words="+StringUtil.toString(words));

        PassageTally tally = new PassageTally();
        tally.blur(BLUR_BY, RestrictionType.NONE);

        for (int i = 0; i < words.length; i++)
        {
            tally.addAll(index.findWord(words[i]));
        }

        // This uses updatePassageTallyFlat() so that words like God
        // that have many startsWith() matches, and hence many verse
        // matches, do not end up with wrongly high scores.
        for (int i = 0; i < words.length; i++)
        {
            // log.fine("considering="+words[i]);
            String root = Grammar.getRoot(words[i]);

            // Check that the root is still a word. If not then we
            // use the full version. This catches misses like se is
            // the root of seed, and matches sea and so on ...
            Key ref = index.findWord(root);
            if (ref.isEmpty())
            {
                root = words[i];
            }

            // log.fine("  root="+root);
            Collection col = thesaurus.getSynonyms(root);
            String[] grWords = (String[]) col.toArray(new String[col.size()]);

            // log.fine("  gr_words="+StringUtil.toString(gr_words));
            PassageTally temp = new PassageTally();

            for (int j = 0; j < grWords.length; j++)
            {
                temp.addAll(index.findWord(grWords[j]));
            }

            temp.flatten();
            tally.addAll(temp);
        }

        if (!restriction.equals(Search.UNRESTRICTED))
        {
            tally.retainAll(restriction);
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

    /**
     * The index that we use to search for words in
     */
    private Index index;
}
