/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 */
package org.crosswire.jsword.index.lucene;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.index.Index;
import org.crosswire.jsword.index.query.Query;
import org.crosswire.jsword.index.query.QueryBuilderFactory;
import org.crosswire.jsword.index.search.DefaultSearchRequest;
import org.crosswire.jsword.index.search.SearchRequest;
import org.crosswire.jsword.index.search.Searcher;
import org.crosswire.jsword.passage.Key;

/**
 * The central interface to all searching.
 * 
 * Functionality the I envisage includes:
 * <ul>
 * <li>A simple search syntax that goes something like this.
 * <ul>
 * <li>aaron, moses (verses containing aaron and moses. Can also use &amp; or +)
 * <li>aaron/moses (verses containing aaron or moses. Can also use |)
 * <li>aaron - moses (verses containing aaron but not moses)
 * <li>aaron ~5 , moses (verses with aaron within 5 verses of moses)
 * <li>soundslike aaron (verses with words that sound like aaron. Can also use
 * sl ...)
 * <li>thesaurus happy (verses with words that mean happy. Can also use th ...)
 * <li>grammar have (words like has have had and so on. Can also use gr ...)
 * </ul>
 * <li>The ability to add soundslike type extensions.
 * </ul>
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class LuceneSearcher implements Searcher {
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.index.search.Searcher#init(org.crosswire.jsword.
     * index.search.Index)
     */
    public void init(Index newindex) {
        this.index = newindex;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.index.search.Searcher#search(java.lang.String)
     */
    public Key search(String request) throws BookException {
        return search(new DefaultSearchRequest(request));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.index.search.Searcher#search(org.crosswire.jsword
     * .index.search.SearchRequest)
     */
    public Key search(SearchRequest request) throws BookException {
        index.setSearchModifier(request.getSearchModifier());
        Query query = QueryBuilderFactory.getQueryBuilder().parse(request.getRequest());
        Key results = search(query);
        index.setSearchModifier(null);
        return results;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.index.search.Searcher#search(org.crosswire.jsword
     * .index.query.Query)
     */
    public Key search(Query query) throws BookException {
        return query.find(index);
    }

    /**
     * Accessor for the Bible to search.
     * 
     * @return The current Bible
     */
    protected Index getIndex() {
        return index;
    }

    /**
     * The index
     */
    private Index index;
}
