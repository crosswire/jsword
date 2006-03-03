/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.index.search;

import org.crosswire.jsword.index.query.QueryDecorator;
import org.crosswire.jsword.index.query.QueryDecoratorFactory;

/**
 * An Enumeration of the possible types of Searches.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public enum SearchType
{
    /** Find the words in the specified order. */
    PHRASE
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.index.search.SearchType#decorate(java.lang.String)
         */
        @Override
        public String decorate(String queryWords)
        {
            return SEARCH_SYNTAX.decoratePhrase(queryWords);
        }
    },

    /** Find all the words regardless of position. */
    ALL_WORDS
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.index.search.SearchType#decorate(java.lang.String)
         */
        @Override
        public String decorate(String queryWords)
        {
            return SEARCH_SYNTAX.decorateAllWords(queryWords);
        }
    },

    /** Find any of these words */
    ANY_WORDS
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.index.search.SearchType#decorate(java.lang.String)
         */
        @Override
        public String decorate(String queryWords)
        {
            return SEARCH_SYNTAX.decorateAnyWords(queryWords);
        }
    },

    /**
     * Find verses not containing these words.
     * Note this may require being added after words being sought.
     */
    NOT_WORDS
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.index.search.SearchType#decorate(java.lang.String)
         */
        @Override
        public String decorate(String queryWords)
        {
            return SEARCH_SYNTAX.decorateNotWords(queryWords);
        }
    },

    /** Find verses with words that start with the these beginnings. */
    START_WORDS
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.index.search.SearchType#decorate(java.lang.String)
         */
        @Override
        public String decorate(String queryWords)
        {
            return SEARCH_SYNTAX.decorateStartWords(queryWords);
        }
    },

    /** Find verses with words spelled something like */
    SPELL_WORDS
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.index.search.SearchType#decorate(java.lang.String)
         */
        @Override
        public String decorate(String queryWords)
        {
            return SEARCH_SYNTAX.decorateSpellWords(queryWords);
        }
    },

    /** Find verses in this range */
    RANGE
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.index.search.SearchType#decorate(java.lang.String)
         */
        @Override
        public String decorate(String queryWords)
        {
            return SEARCH_SYNTAX.decorateRange(queryWords);
        }
    };

    /**
     * Decorate a string with the given type of decoration.
     */
    public abstract String decorate(String queryWords);

    protected static final QueryDecorator SEARCH_SYNTAX = QueryDecoratorFactory.getSearchSyntax();

}
