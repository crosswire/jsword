
package org.crosswire.jsword.book;

/**
 * Some common implementations of BookFilter.
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
public class BookFilters
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
     * Filter for all books
     */
    private static BookFilter allBookFilter = new AllBookFilter();

    /**
     * Filter for all books
     */
    private static class AllBookFilter implements BookFilter
    {
        public boolean test(Book book)
        {
            return true;
        }
    }

    /**
     * A filter that accepts everything that implements Bible
     */
    public static BookFilter getBibles()
    {
        return biblesBookFilter;
    }

    /**
     * Filter for all Bibles
     */
    private static BookFilter biblesBookFilter = new BiblesBookFilter();

    /**
     * Filter for all Bibles
     */
    private static class BiblesBookFilter implements BookFilter
    {
        public boolean test(Book book)
        {
            return book.getType().equals(BookType.BIBLE);
        }
    }

    /**
     * A filter that accepts everything that implements Dictionary
     */
    public static BookFilter getDictionaries()
    {
        return dictionariesBookFilter;
    }

    /**
     * Filter for all dictionaries
     */
    private static BookFilter dictionariesBookFilter = new DictionariesBookFilter();

    /**
     * Filter for all dictionaries
     */
    private static class DictionariesBookFilter implements BookFilter
    {
        public boolean test(Book book)
        {
            return book.getType().equals(BookType.DICTIONARY);
        }
    }

    /**
     * A filter that accepts everything that implements Commentary
     */
    public static BookFilter getCommentaries()
    {
        return commentariesBookFilter;
    }

    /**
     * Filter for all commentaries
     */
    private static BookFilter commentariesBookFilter = new CommentariesBookFilter();

    /**
     * Filter for all commentaries
     */
    private static class CommentariesBookFilter implements BookFilter
    {
        public boolean test(Book book)
        {
            return book.getType().equals(BookType.COMMENTARY);
        }
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
     * Filter for all Greek Definition Dictionaries
     */
    private static BookFilter greekDefinitionsBookFilter = new GreekDefinitionsBookFilter();

    /**
     * Filter for all Greek Definition Dictionaries
     */
    private static class GreekDefinitionsBookFilter implements BookFilter
    {
        public boolean test(Book book)
        {
            return book.hasFeature(FeatureType.GREEK_DEFINITIONS);
        }
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
     * Filter for all Greek Parse/Morphology Dictionaries
     */
    private static BookFilter greekParseBookFilter = new GreekParseBookFilter();

    /**
     * Filter for all Greek Parse/Morphology Dictionaries
     */
    private static class GreekParseBookFilter implements BookFilter
    {
        public boolean test(Book book)
        {
            return book.hasFeature(FeatureType.GREEK_PARSE);
        }
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
     * Filter for all Hebrew Definition Dictionaries
     */
    private static BookFilter hebrewDefinitionsBookFilter = new HebrewDefinitionsBookFilter();

    /**
     * Filter for all Hebrew Definition Dictionaries
     */
    private static class HebrewDefinitionsBookFilter implements BookFilter
    {
        public boolean test(Book book)
        {
            return book.hasFeature(FeatureType.HEBREW_DEFINITIONS);
        }
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
     * Filter for all Hebrew Parse/Morphology Dictionaries
     */
    private static BookFilter hebrewParseBookFilter = new HebrewParseBookFilter();

    /**
     * Filter for all Hebrew Parse/Morphology Dictionaries
     */
    private static class HebrewParseBookFilter implements BookFilter
    {
        public boolean test(Book book)
        {
            return book.hasFeature(FeatureType.HEBREW_PARSE);
        }
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
}
