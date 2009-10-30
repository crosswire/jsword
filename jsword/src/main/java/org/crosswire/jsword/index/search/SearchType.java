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

import java.io.Serializable;

import org.crosswire.jsword.index.query.QueryDecorator;
import org.crosswire.jsword.index.query.QueryDecoratorFactory;

/**
 * An Enumeration of the possible types of Searches.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public abstract class SearchType implements Serializable {
    /**
     * Find the words in the specified order.
     */
    public static final SearchType PHRASE = new SearchType("Phrase") //$NON-NLS-1$
    {
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.jsword.index.search.SearchType#decorate(java.lang.String
         * )
         */
        public String decorate(String queryWords) {
            return SEARCH_SYNTAX.decoratePhrase(queryWords);
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 4049921554181534256L;
    };

    /**
     * Find all the words regardless of position.
     */
    public static final SearchType ALL_WORDS = new SearchType("All") //$NON-NLS-1$
    {
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.jsword.index.search.SearchType#decorate(java.lang.String
         * )
         */
        public String decorate(String queryWords) {
            return SEARCH_SYNTAX.decorateAllWords(queryWords);
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3256721771208980279L;
    };

    /**
     * Find any of these words
     */
    public static final SearchType ANY_WORDS = new SearchType("Any") //$NON-NLS-1$
    {
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.jsword.index.search.SearchType#decorate(java.lang.String
         * )
         */
        public String decorate(String queryWords) {
            return SEARCH_SYNTAX.decorateAnyWords(queryWords);
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3257846580244853043L;
    };

    /**
     * Find verses not containing these words. Note this may require being added
     * after words being sought.
     */
    public static final SearchType NOT_WORDS = new SearchType("Not") //$NON-NLS-1$
    {
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.jsword.index.search.SearchType#decorate(java.lang.String
         * )
         */
        public String decorate(String queryWords) {
            return SEARCH_SYNTAX.decorateNotWords(queryWords);
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 4050480123318842929L;
    };

    /**
     * Find verses with words that start with the these beginnings.
     */
    public static final SearchType START_WORDS = new SearchType("Start") //$NON-NLS-1$
    {
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.jsword.index.search.SearchType#decorate(java.lang.String
         * )
         */
        public String decorate(String queryWords) {
            return SEARCH_SYNTAX.decorateStartWords(queryWords);
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3544667378161168437L;
    };

    /**
     * Find verses with words spelled something like
     */
    public static final SearchType SPELL_WORDS = new SearchType("Spell") //$NON-NLS-1$
    {
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.jsword.index.search.SearchType#decorate(java.lang.String
         * )
         */
        public String decorate(String queryWords) {
            return SEARCH_SYNTAX.decorateSpellWords(queryWords);
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3544387006957237044L;
    };

    /**
     * Find verses in this range
     */
    public static final SearchType RANGE = new SearchType("Range") //$NON-NLS-1$
    {
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.jsword.index.search.SearchType#decorate(java.lang.String
         * )
         */
        public String decorate(String queryWords) {
            return SEARCH_SYNTAX.decorateRange(queryWords);
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3257288028421959989L;
    };

    /**
     * @param name
     *            The name of the BookCategory
     */
    protected SearchType(String name) {
        this.name = name;
    }

    /**
     * Decorate a string with the given type of decoration.
     */
    public abstract String decorate(String queryWords);

    /**
     * Lookup method to convert from a String
     */
    public static SearchType fromString(String name) {
        for (int i = 0; i < VALUES.length; i++) {
            SearchType o = VALUES[i];
            if (o.name.equalsIgnoreCase(name)) {
                return o;
            }
        }
        throw new ClassCastException("Not a valid search type"); //$NON-NLS-1$
    }

    /**
     * Lookup method to convert from an integer
     */
    public static SearchType fromInteger(int i) {
        return VALUES[i];
    }

    /**
     * Prevent subclasses from overriding canonical identity based Object
     * methods
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public final boolean equals(Object o) {
        return super.equals(o);
    }

    /**
     * Prevent subclasses from overriding canonical identity based Object
     * methods
     * 
     * @see java.lang.Object#hashCode()
     */
    public final int hashCode() {
        return super.hashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return name;
    }

    /**
     * The name of the BookCategory
     */
    private String name;

    protected static final QueryDecorator SEARCH_SYNTAX = QueryDecoratorFactory.getSearchSyntax();

    // Support for serialization
    private static int nextObj;
    private final int obj = nextObj++;

    Object readResolve() {
        return VALUES[obj];
    }

    private static final SearchType[] VALUES = {
            PHRASE, ALL_WORDS, ANY_WORDS, NOT_WORDS, START_WORDS, SPELL_WORDS, RANGE,
    };

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3256721767014871089L;
}
