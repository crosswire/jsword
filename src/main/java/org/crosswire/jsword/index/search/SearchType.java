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
package org.crosswire.jsword.index.search;

import org.crosswire.jsword.index.query.QueryDecorator;
import org.crosswire.jsword.index.query.QueryDecoratorFactory;

/**
 * An Enumeration of the possible types of Searches.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public enum SearchType {
    /**
     * Find the words in the specified order.
     */
    PHRASE ("Phrase") {
        @Override
        public String decorate(String queryWords) {
            return SEARCH_SYNTAX.decoratePhrase(queryWords);
        }
    },

    /**
     * Find all the words regardless of position.
     */
    ALL_WORDS ("All") {
        @Override
        public String decorate(String queryWords) {
            return SEARCH_SYNTAX.decorateAllWords(queryWords);
        }
   },

    /**
     * Find any of these words
     */
    ANY_WORDS ("Any") {
        @Override
        public String decorate(String queryWords) {
            return SEARCH_SYNTAX.decorateAnyWords(queryWords);
        }
    },

    /**
     * Find verses not containing these words. Note this may require being added
     * after words being sought.
     */
    NOT_WORDS ("Not") {
        @Override
        public String decorate(String queryWords) {
            return SEARCH_SYNTAX.decorateNotWords(queryWords);
        }
    },

    /**
     * Find verses with words that start with the these beginnings.
     */
    START_WORDS ("Start") {
        @Override
        public String decorate(String queryWords) {
            return SEARCH_SYNTAX.decorateStartWords(queryWords);
        }
    },

    /**
     * Find verses with words spelled something like
     */
    SPELL_WORDS ("Spell") {
        @Override
        public String decorate(String queryWords) {
            return SEARCH_SYNTAX.decorateSpellWords(queryWords);
        }
    },

    /**
     * Find verses in this range
     */
    RANGE ("Range") {
        @Override
        public String decorate(String queryWords) {
            return SEARCH_SYNTAX.decorateRange(queryWords);
        }
    };

    /**
     * @param name
     *            The name of the BookCategory
     */
    SearchType(String name) {
        this.name = name;
    }

    /**
     * Decorate a string with the given type of decoration.
     * @param queryWords 
     * @return the decorated query
     */
    public abstract String decorate(String queryWords);

    /**
     * Lookup method to convert from a String
     * 
     * @param name 
     * @return the matching search type
     */
    public static SearchType fromString(String name) {
        for (SearchType v : values()) {
            if (v.name.equalsIgnoreCase(name)) {
                return v;
            }
        }

        throw new ClassCastException("Not a valid search type");
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * The name of the BookCategory
     */
    private String name;

    protected static final QueryDecorator SEARCH_SYNTAX = QueryDecoratorFactory.getSearchSyntax();
}
