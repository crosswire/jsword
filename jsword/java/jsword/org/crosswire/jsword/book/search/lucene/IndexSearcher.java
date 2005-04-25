package org.crosswire.jsword.book.search.lucene;

import java.util.Iterator;
import java.util.List;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.search.Index;
import org.crosswire.jsword.book.search.SearchRequest;
import org.crosswire.jsword.book.search.Searcher;
import org.crosswire.jsword.book.search.basic.DefaultSearchRequest;
import org.crosswire.jsword.passage.Key;

/**
 * The central interface to all searching.
 *
 * Functionality the I invisage includes:<ul>
 * <li>A simple search syntax that goes something like this.<ul>
 * <li>aaron, moses     (verses containing aaron and moses. Can also use & or +)
 * <li>aaron/moses      (verses containing aaron or moses. Can also use |)
 * <li>aaron - moses    (verses containing aaron but not moses)
 * <li>aaron ~5 , moses (verses with aaron within 5 verses of moses)
 * <li>soundslike aaron (verses with words that sound like aaron. Can also use sl ...)
 * <li>thesaurus happy  (verses with words that mean happy. Can also use th ...)
 * <li>grammar have     (words like has have had and so on. Can also use gr ...)</ul>
 * <li>The ability to add soundslike type extensions.</ul>
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
public class IndexSearcher implements Searcher
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.Searcher#init(org.crosswire.jsword.book.search.Index)
     */
    public void init(Index newindex)
    {
        this.index = newindex;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.Searcher#search(java.lang.String)
     */
    public Key search(String request) throws BookException
    {
        return search(new DefaultSearchRequest(request));
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.Searcher#search(org.crosswire.jsword.book.search.SearchRequest)
     */
    public Key search(SearchRequest request) throws BookException
    {
        index.setSearchModifier(request.getSearchModifier());
        List output = QueryBuilder.tokenize(request.getRequest());
        Key results = search(output);
        index.setSearchModifier(null);
        return results;
    }

    /**
     * Take a search string and decipher it into a Key.
     * @return The matching verses
     */
    protected Key search(List matches) throws BookException
    {
        // Get an empty key
        Key key = index.find(null);
        Iterator iter = matches.iterator();
        while (iter.hasNext())
        {
            Query token = (Query) iter.next();
            key.addAll(token.find(index));
        }
        return key;
    }

    /**
     * Accessor for the Bible to search.
     * @return The current Bible
     */
    protected Index getIndex()
    {
        return index;
    }

    /**
     * The index
     */
    private Index index;
}
