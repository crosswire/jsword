
package org.crosswire.jsword.control.search;

import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BookUtil;
import org.crosswire.jsword.control.dictionary.Dictionary;
import org.crosswire.jsword.control.dictionary.Grammar;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageConstants;
import org.crosswire.jsword.passage.PassageTally;

/**
 * Find the best match possible for a sentance in a Bible.
 *
 * <table border='1' cellPadding='3' cellSpacing='0' width="100%">
 * <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
 * Distribution Licence:<br />
 * Project B is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
 * <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
 * @see <{docs.Licence}>
 * @author Joe Walker
 */
public class Matcher
{
    /**
     * Create a new search engine. Bible should probably be Book however
     * Book does not yet have a well defined interface.
     * @param bible The book to search
     */
    public Matcher(Bible bible)
    {
        this.bible = bible;
    }

    /**
     * Try to find the best match for the given string
     * @param sought The string to be searched for
     * @return The matching verses
     */
    public PassageTally bestMatch(String sought) throws SearchException
    {
        String[] words = BookUtil.getWords(sought);
        words = Grammar.stripSmallWords(words);
        // log.fine("words="+StringUtil.toString(words));

        PassageTally tally = new PassageTally();
        tally.blur(2, PassageConstants.RESTRICT_NONE);

        try
        {
            BookUtil.updatePassageTally(bible, tally, words);

            // This uses updatePassageTallyFlat() so that words like God
            // that have many startsWith() matches, and hence many verse
            // matches, do not end up with wrongly high scores.
            for (int i=0; i<words.length; i++)
            {
                // log.fine("considering="+words[i]);
                String root = Grammar.getRoot(words[i]);

                // Check that the root is still a word. If not then we
                // use the full version. This catches misses like se is
                // the root of seed, and matches sea and so on ...
                Passage ref = bible.findPassage(root);
                if (ref.isEmpty())
                    root = words[i];

                // log.fine("  root="+root);
                String[] gr_words = BookUtil.toStringArray(bible.getStartsWith(root));

                // log.fine("  gr_words="+StringUtil.toString(gr_words));
                BookUtil.updatePassageTallyFlat(bible, tally, gr_words);
            }
        }
        catch (Exception ex)
        {
            throw new SearchException("search_missed", ex);
        }

        return tally;
    }

    /**
     * Accessor for the Bible to search.
     * @return The current Bible
     */
    public Bible getBible()
    {
        return bible;
    }

    /**
     * The Dictionary for looking words up in
     */
    private Dictionary dict = new Dictionary();

    /**
     * The book to search
     */
    private Bible bible;
}
