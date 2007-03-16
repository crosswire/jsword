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
package org.crosswire.jsword.book;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.crosswire.common.util.Logger;

/**
 * Some common implementations of BookFilter.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class BookFilters
{
    /**
     * Ensure we cant be created
     */
    private BookFilters()
    {
    }

    /**
     * A simple default filter that returns everything
     */
    public static BookFilter getAll()
    {
        return allBookFilter;
    }

    /**
     * A filter that accepts everything that implements Bible
     */
    public static BookFilter getBibles()
    {
        return biblesBookFilter;
    }

    /**
     * A filter that accepts everything that's not a Bible
     */
    public static BookFilter getNonBibles()
    {
        return noneBibleBookFilter;
    }

    /**
     * A filter that accepts everything that implements Dictionary
     */
    public static BookFilter getDictionaries()
    {
        return dictionariesBookFilter;
    }

    /**
     * A filter that accepts everything that implements Dictionary
     */
    public static BookFilter getGlossaries()
    {
        return glossariesBookFilter;
    }

    /**
     * A filter that accepts everything that implements DailyDevotionals
     */
    public static BookFilter getDailyDevotionals()
    {
        return dailyDevotionalsBookFilter;
    }

    /**
     * A filter that accepts everything that implements Commentary
     */
    public static BookFilter getCommentaries()
    {
        return commentariesBookFilter;
    }

    /**
     * A filter that accepts everything that implements GeneralBook
     */
    public static BookFilter getGeneralBooks()
    {
        return generalBookFilter;
    }

    /**
     * A filter that accepts everything that is a
     * Greek Definition Dictionary
     */
    public static BookFilter getGreekDefinitions()
    {
        return greekDefinitionsBookFilter;
    }

    /**
     * A filter that accepts everything that is a
     * Greek Parse/Morphology Dictionary
     */
    public static BookFilter getGreekParse()
    {
        return greekParseBookFilter;
    }

    /**
     * A filter that accepts everything that is a
     * Hebrew Definition Dictionary
     */
    public static BookFilter getHebrewDefinitions()
    {
        return hebrewDefinitionsBookFilter;
    }

    /**
     * A filter that accepts everything that is a
     * Hebrew Parse/Morphology Dictionary
     */
    public static BookFilter getHebrewParse()
    {
        return hebrewParseBookFilter;
    }

    /**
     * Filter for all books
     */
    private static BookFilter allBookFilter = new AllBookFilter();

    /**
     * Filter for all Bibles
     */
    private static BookFilter biblesBookFilter = new BookCategoryFilter(BookCategory.BIBLE);

    /**
     * Filter for all non-Bibles
     */
    private static BookFilter noneBibleBookFilter = new NotBookCategoryFilter(BookCategory.BIBLE);

    /**
     * Filter for all dictionaries
     */
    private static BookFilter dictionariesBookFilter = new BookCategoryFilter(BookCategory.DICTIONARY);

    /**
     * Filter for all glossaries
     */
    private static BookFilter glossariesBookFilter = new BookCategoryFilter(BookCategory.GLOSSARY);

    /**
     * Filter for all dictionaries
     */
    private static BookFilter dailyDevotionalsBookFilter = new BookCategoryFilter(BookCategory.DAILY_DEVOTIONS);

    /**
     * Filter for all commentaries
     */
    private static BookFilter commentariesBookFilter = new BookCategoryFilter(BookCategory.COMMENTARY);

    /**
     * Filter for all commentaries
     */
    private static BookFilter generalBookFilter = new BookCategoryFilter(BookCategory.GENERAL_BOOK);

    /**
     * Filter for all Greek Definition Dictionaries
     */
    private static BookFilter greekDefinitionsBookFilter = new BookFeatureFilter(FeatureType.GREEK_DEFINITIONS);

    /**
     * Filter for all Greek Parse/Morphology Dictionaries
     */
    private static BookFilter greekParseBookFilter = new BookFeatureFilter(FeatureType.GREEK_PARSE);

    /**
     * Filter for all Hebrew Definition Dictionaries
     */
    private static BookFilter hebrewDefinitionsBookFilter = new BookFeatureFilter(FeatureType.HEBREW_DEFINITIONS);

    /**
     * Filter for all Hebrew Parse/Morphology Dictionaries
     */
    private static BookFilter hebrewParseBookFilter = new BookFeatureFilter(FeatureType.HEBREW_PARSE);

    /**
     * Filter for all books
     */
    static class AllBookFilter implements BookFilter
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.BookFilter#test(org.crosswire.jsword.book.Book)
         */
        public boolean test(Book book)
        {
            return true;
        }
    }

    /**
     * Filter for books by category
     */
    static class BookCategoryFilter implements BookFilter
    {
        BookCategoryFilter(BookCategory category)
        {
            this.category = category;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.BookFilter#test(org.crosswire.jsword.book.Book)
         */
        public boolean test(Book book)
        {
            return book.getBookCategory().equals(category) && !book.isLocked();
        }

        private BookCategory category;
    }

    /**
     * Filter for books by category
     */
    static class NotBookCategoryFilter implements BookFilter
    {
        NotBookCategoryFilter(BookCategory category)
        {
            this.category = category;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.BookFilter#test(org.crosswire.jsword.book.Book)
         */
        public boolean test(Book book)
        {
            return !book.getBookCategory().equals(category) && !book.isLocked();
        }

        private BookCategory category;
    }

    /**
     * Filter for books by feature
     */
    static class BookFeatureFilter implements BookFilter
    {
        BookFeatureFilter(FeatureType feature)
        {
            this.feature = feature;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.BookFilter#test(org.crosswire.jsword.book.Book)
         */
        public boolean test(Book book)
        {
            return book.hasFeature(feature) && !book.isLocked();
        }

        private FeatureType feature;
    }

    /**
     * A filter that accepts Books that match two criteria.
     */
    public static BookFilter both(final BookFilter b1, final BookFilter b2)
    {
        return new BookFilter()
        {
            public boolean test(Book book)
            {
                return b1.test(book) && b2.test(book);
            }
        };
    }

    /**
     * A filter that accepts Books that match either of two criteria.
     */
    public static BookFilter either(final BookFilter b1, final BookFilter b2)
    {
        return new BookFilter()
        {
            public boolean test(Book book)
            {
                return b1.test(book) || b2.test(book);
            }
        };
    }

    /**
     * A filter that accepts Books that match by book driver.
     */
    public static BookFilter getBooksByDriver(final BookDriver driver)
    {
        return new BookFilter()
        {
            public boolean test(Book book)
            {
                return book.getDriver() == driver;
            }
        };
    }

    /**
     * A simple default filter that returns everything.
     * The match parameter is a set of name value pairs like this:
     * <br/>
     * <code>initials=ESV;type=Bible;driverName=Sword</code><br/>
     * Before the = there must be the name of a property on Book and after
     * the value to match (.toString()) is called on the results of the getter.
     * @param match a ; separated list of properties (of Book) to match
     * @see Book
     */
    public static BookFilter getCustom(String match)
    {
        return new CustomBookFilter(match);
    }

    /**
     * Custom Filter
     */
    static class CustomBookFilter implements BookFilter
    {
        /**
         * Ctor
         * @param match The match spec.
         * @see BookFilters#getCustom(String)
         */
        public CustomBookFilter(String match)
        {
            List cache = new ArrayList();
            String[] filters = match.split(";"); //$NON-NLS-1$
            for (int i = 0; i < filters.length; i++)
            {
                String[] parts = filters[i].split("="); //$NON-NLS-1$
                if (parts.length != 2 || parts[0].length() == 0 || parts[1].length() == 0)
                {
                    throw new IllegalArgumentException("Filter format is 'property=value', given: " + filters[i]); //$NON-NLS-1$
                }

                Test test = new Test();

                String gettername = "get" + Character.toTitleCase(parts[0].charAt(0)) + parts[0].substring(1); //$NON-NLS-1$
                try
                {
                    test.property = Book.class.getMethod(gettername, (Class[]) null);
                    test.result = parts[1];
                }
                catch (NoSuchMethodException ex)
                {
                    throw new IllegalArgumentException("Missing property: " + parts[0] + " in Book"); //$NON-NLS-1$ //$NON-NLS-2$
                }

                cache.add(test);
            }

            tests = (Test[]) cache.toArray(new Test[cache.size()]);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.BookFilter#test(org.crosswire.jsword.book.Book)
         */
        public boolean test(Book book)
        {
            for (int i = 0; i < tests.length; i++)
            {
                Test test = tests[i];
                try
                {
                    Object result = test.property.invoke(book, (Object[]) null);
                    if (!test.result.equals(result.toString()))
                    {
                        return false;
                    }
                }
                catch (IllegalArgumentException e)
                {
                    log.warn("Error while testing property " + test.property.getName() + " on " + book.getName(), e); //$NON-NLS-1$ //$NON-NLS-2$
                    return false;
                }
                catch (IllegalAccessException e)
                {
                    log.warn("Error while testing property " + test.property.getName() + " on " + book.getName(), e); //$NON-NLS-1$ //$NON-NLS-2$
                    return false;
                }
                catch (InvocationTargetException e)
                {
                    log.warn("Error while testing property " + test.property.getName() + " on " + book.getName(), e); //$NON-NLS-1$ //$NON-NLS-2$
                    return false;
                }
            }

            return true;
        }

        private Test[] tests;

        /**
         *
         */
        static class Test
        {
            protected String result;
            protected Method property;
        }
    }

    /**
     * The log stream
     */
    static final Logger log = Logger.getLogger(BookFilters.class);
}
