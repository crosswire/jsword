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
 *
 */
package org.crosswire.jsword.index.lucene;

import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.index.query.QueryDecorator;

/**
 * LuceneQueryDecorator represents the extension of stock Lucene syntax with
 * passage ranges and with blurring (searching in nearby verses).
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class LuceneQueryDecorator implements QueryDecorator {
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.index.search.SearchSyntax#decorateAllWords(java.
     * lang.String)
     */
    public String decorateAllWords(String queryWords) {
        String[] words = queryWords.split(SPACE);
        StringBuilder search = new StringBuilder();
        search.append(PLUS);
        search.append(StringUtil.join(words, SPACE_PLUS));
        return search.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.index.search.SearchSyntax#decorateAnyWords(java.
     * lang.String)
     */
    public String decorateAnyWords(String queryWords) {
        // Don't need to do anything, this is the default behavior
        return queryWords;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.index.search.SearchSyntax#decoratePhrase(java.lang
     * .String)
     */
    public String decoratePhrase(String queryWords) {
        // This performs a best match
        StringBuilder search = new StringBuilder();
        search.append(QUOTE);
        search.append(queryWords);
        search.append(QUOTE);
        return search.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.index.search.SearchSyntax#decorateNotWords(java.
     * lang.String)
     */
    public String decorateNotWords(String queryWords) {
        String[] words = queryWords.split(SPACE);
        StringBuilder search = new StringBuilder();
        search.append(MINUS);
        search.append(StringUtil.join(words, SPACE_MINUS));
        return search.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.index.search.SearchSyntax#decorateRange(java.lang
     * .String)
     */
    public String decorateRange(String queryWords) {
        StringBuilder search = new StringBuilder();
        search.append(PLUS);
        search.append(OPEN);
        search.append(queryWords);
        search.append(CLOSE);
        return search.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.index.search.SearchSyntax#decorateSpellWords(java
     * .lang.String)
     */
    public String decorateSpellWords(String queryWords) {
        String[] words = queryWords.split(SPACE);
        StringBuilder search = new StringBuilder(StringUtil.join(words, FUZZY_SPACE));
        search.append(FUZZY);
        return search.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.index.search.SearchSyntax#decorateStartWords(java
     * .lang.String)
     */
    public String decorateStartWords(String queryWords) {
        String[] words = queryWords.split(SPACE);
        StringBuilder search = new StringBuilder(StringUtil.join(words, WILD_SPACE));
        search.append(WILD);
        return search.toString();
    }

    /**
     * In our parsing we use space quite a lot and this ensures there is only one.
     */
    private static final String SPACE = " ";
    private static final char QUOTE = '"';
    private static final char PLUS = '+';
    private static final String SPACE_PLUS = " +";

    private static final char MINUS = '-';
    private static final String SPACE_MINUS = " -";

    private static final char OPEN = '[';
    private static final char CLOSE = ']';

    private static final char FUZZY = '~';
    private static final String FUZZY_SPACE = "~ ";

    private static final char WILD = '*';
    private static final String WILD_SPACE = "* ";

}
