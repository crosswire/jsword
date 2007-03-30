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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.crosswire.common.config.ChoiceFactory;

/**
 * Handles the current default Books.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class Defaults
{
    /**
     * Prevent construction
     */
    private Defaults()
    {
    }

    /**
     * Determine whether the getBible should return the current Bible
     * or the user's chosen default.
     * @return true if the bible tracks the user's selection
     */
    public static boolean isCurrentBible()
    {
        return currentBible;
    }

    /**
     * Establish whether the getBible should return the current Bible
     * or the user's chosen default.
     * @param current
     */
    public static void setCurrentBible(boolean current)
    {
        currentBible = current;
    }

    /**
     * If the user has chosen to remember the book (by type)
     * then set the current book for that type.
     * @param book
     */
    public static void setCurrentBook(Book book)
    {
        BookCategory type = book.getBookCategory();
        if (type.equals(BookCategory.BIBLE) && isCurrentBible())
        {
            setBible(book);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#setDefault(org.crosswire.jsword.book.Book)
     */
    public static void setBible(Book book)
    {
        bibleDeft.setDefault(book);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#unsetDefault()
     */
    protected static void unsetBible()
    {
        bibleDeft.unsetDefault();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#getDefault()
     */
    public static Book getBible()
    {
        return bibleDeft.getDefault();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#getDefaultName()
     */
    public static String getBibleByName()
    {
        return bibleDeft.getDefaultName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#setDefaultByName(java.lang.String)
     */
    public static void setBibleByName(String name)
    {
        bibleDeft.setDefaultByName(name);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#setDefault(org.crosswire.jsword.book.Book)
     */
    public static void setCommentary(Book book)
    {
        commentaryDeft.setDefault(book);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#unsetDefault()
     */
    protected static void unsetCommentary()
    {
        commentaryDeft.unsetDefault();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#getDefault()
     */
    public static Book getCommentary()
    {
        return commentaryDeft.getDefault();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#getDefaultName()
     */
    public static String getCommentaryByName()
    {
        return commentaryDeft.getDefaultName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#setDefaultByName(java.lang.String)
     */
    public static void setCommentaryByName(String name)
    {
        commentaryDeft.setDefaultByName(name);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#setDefault(org.crosswire.jsword.book.Book)
     */
    public static void setDictionary(Book book)
    {
        dictionaryDeft.setDefault(book);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#unsetDefault()
     */
    protected static void unsetDictionary()
    {
        dictionaryDeft.unsetDefault();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#getDefault()
     */
    public static Book getDictionary()
    {
        return dictionaryDeft.getDefault();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#getDefaultName()
     */
    public static String getDictionaryByName()
    {
        return dictionaryDeft.getDefaultName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#setDefaultByName(java.lang.String)
     */
    public static void setDictionaryByName(String name)
    {
        dictionaryDeft.setDefaultByName(name);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#setDefault(org.crosswire.jsword.book.Book)
     */
    public static void setDailyDevotional(Book book)
    {
        dictionaryDeft.setDefault(book);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#unsetDefault()
     */
    protected static void unsetDailyDevotional()
    {
        dailyDevotionalDeft.unsetDefault();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#getDefault()
     */
    public static Book getDailyDevotional()
    {
        return dailyDevotionalDeft.getDefault();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#getDefaultName()
     */
    public static String getDailyDevotionalByName()
    {
        return dailyDevotionalDeft.getDefaultName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#setDefaultByName(java.lang.String)
     */
    public static void setDailyDevotionalByName(String name)
    {
        dailyDevotionalDeft.setDefaultByName(name);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#setDefault(org.crosswire.jsword.book.Book)
     */
    public static void setGreekDefinitions(Book book)
    {
        greekDefinitionsDeft.setDefault(book);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#unsetDefault()
     */
    protected static void unsetGreekDefinitions()
    {
        greekDefinitionsDeft.unsetDefault();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#getDefault()
     */
    public static Book getGreekDefinitions()
    {
        return greekDefinitionsDeft.getDefault();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#getDefaultName()
     */
    public static String getGreekDefinitionsByName()
    {
        return greekDefinitionsDeft.getDefaultName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#setDefaultByName(java.lang.String)
     */
    public static void setGreekDefinitionsByName(String name)
    {
        greekDefinitionsDeft.setDefaultByName(name);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#setDefault(org.crosswire.jsword.book.Book)
     */
    public static void setHebrewDefinitions(Book book)
    {
        hebrewDefinitionsDeft.setDefault(book);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#unsetDefault()
     */
    protected static void unsetHebrewDefinitions()
    {
        hebrewDefinitionsDeft.unsetDefault();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#getDefault()
     */
    public static Book getHebrewDefinitions()
    {
        return hebrewDefinitionsDeft.getDefault();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#getDefaultName()
     */
    public static String getHebrewDefinitionsByName()
    {
        return hebrewDefinitionsDeft.getDefaultName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#setDefaultByName(java.lang.String)
     */
    public static void setHebrewDefinitionsByName(String name)
    {
        hebrewDefinitionsDeft.setDefaultByName(name);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#setDefault(org.crosswire.jsword.book.Book)
     */
    public static void setGreekParse(Book book)
    {
        greekParseDeft.setDefault(book);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#unsetDefault()
     */
    protected static void unsetGreekParse()
    {
        greekParseDeft.unsetDefault();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#getDefault()
     */
    public static Book getGreekParse()
    {
        return greekParseDeft.getDefault();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#getDefaultName()
     */
    public static String getGreekParseByName()
    {
        return greekParseDeft.getDefaultName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#setDefaultByName(java.lang.String)
     */
    public static void setGreekParseByName(String name)
    {
        greekParseDeft.setDefaultByName(name);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#setDefault(org.crosswire.jsword.book.Book)
     */
    public static void setHebrewParse(Book book)
    {
        hebrewParseDeft.setDefault(book);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#unsetDefault()
     */
    protected static void unsetHebrewParse()
    {
        hebrewParseDeft.unsetDefault();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#getDefault()
     */
    public static Book getHebrewParse()
    {
        return hebrewParseDeft.getDefault();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#getDefaultName()
     */
    public static String getHebrewParseByName()
    {
        return hebrewParseDeft.getDefaultName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.DefaultBook#setDefaultByName(java.lang.String)
     */
    public static void setHebrewParseByName(String name)
    {
        hebrewParseDeft.setDefaultByName(name);
    }

    protected static DefaultBook getDefaultBible()
    {
        return bibleDeft;
    }

    protected static DefaultBook getDefaultCommentary()
    {
        return commentaryDeft;
    }

    protected static DefaultBook getDefaultDictionary()
    {
        return dictionaryDeft;
    }

    protected static DefaultBook getDefaultDailyDevotional()
    {
        return dailyDevotionalDeft;
    }

    protected static DefaultBook getDefaultGreekDefinitions()
    {
        return greekDefinitionsDeft;
    }

    protected static DefaultBook getDefaultHebrewDefinitions()
    {
        return hebrewDefinitionsDeft;
    }

    protected static DefaultBook getDefaultGreekParse()
    {
        return greekParseDeft;
    }

    protected static DefaultBook getDefaultHebrewParse()
    {
        return hebrewParseDeft;
    }

    public static void refreshBooks()
    {
        // Create the array of Bibles
        String[] bnames = getFullNameArray(BookFilters.getOnlyBibles());
        ChoiceFactory.getDataMap().put(BIBLE_KEY, bnames);

        // Create the array of Commentaries
        String[] cnames = getFullNameArray(BookFilters.getCommentaries());
        ChoiceFactory.getDataMap().put(COMMENTARY_KEY, cnames);

        // Create the array of Dictionaries
        String[] dnames = getFullNameArray(BookFilters.getDictionaries());
        ChoiceFactory.getDataMap().put(DICTIONARY_KEY, dnames);

        // Create the array of DailyDevotionals
        String[] rnames = getFullNameArray(BookFilters.getDailyDevotionals());
        ChoiceFactory.getDataMap().put(DAILY_DEVOTIONALS_KEY, rnames);

        // Create the array of Dictionaries
        String[] greekDef = getFullNameArray(BookFilters.getGreekDefinitions());
        ChoiceFactory.getDataMap().put(GREEKDEF_KEY, greekDef);

        // Create the array of Dictionaries
        String[] hebrewDef = getFullNameArray(BookFilters.getHebrewDefinitions());
        ChoiceFactory.getDataMap().put(HEBREWDEF_KEY, hebrewDef);

        // Create the array of Dictionaries
        String[] greekParse = getFullNameArray(BookFilters.getGreekParse());
        ChoiceFactory.getDataMap().put(GREEKPARSE_KEY, greekParse);

        // Create the array of Dictionaries
        String[] hebrewParse = getFullNameArray(BookFilters.getHebrewParse());
        ChoiceFactory.getDataMap().put(HEBREWPARSE_KEY, hebrewParse);
    }

    /**
     * Go through all of the current books checking to see if we need to replace
     * the current defaults with one of these.
     */
    protected static void checkAllPreferable()
    {
        Iterator iter = Books.installed().getBooks().iterator();
        while (iter.hasNext())
        {
            Book book = (Book) iter.next();
            checkPreferable(book);
        }
    }

    /**
     * Determine whether this Book become the default.
     * It should, only if there is not one.
     */
    protected static void checkPreferable(Book book)
    {
        assert book != null;

        bibleDeft.setDefaultConditionally(book);
        commentaryDeft.setDefaultConditionally(book);
        dictionaryDeft.setDefaultConditionally(book);
        dailyDevotionalDeft.setDefaultConditionally(book);
        greekDefinitionsDeft.setDefaultConditionally(book);
        greekParseDeft.setDefaultConditionally(book);
        hebrewDefinitionsDeft.setDefaultConditionally(book);
        hebrewParseDeft.setDefaultConditionally(book);
    }

    /**
     * Convert a filter into an array of names of Books that pass the filter.
     */
    private static String[] getFullNameArray(BookFilter filter)
    {
        List names = new ArrayList();

        Iterator iter = Books.installed().getBooks(filter).iterator();
        while (iter.hasNext())
        {
            Book book = (Book) iter.next();
            names.add(book.getFullName());
        }

        return (String[]) names.toArray(new String[names.size()]);
    }

    /**
     * To keep us up to date with changes in the available Books
     */
    static class DefaultsBookListener implements BooksListener
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.BooksListener#bookAdded(org.crosswire.jsword.book.BooksEvent)
         */
        public void bookAdded(BooksEvent ev)
        {
            Book book = ev.getBook();
            checkPreferable(book);
            refreshBooks();
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.BooksListener#bookRemoved(org.crosswire.jsword.book.BooksEvent)
         */
        public void bookRemoved(BooksEvent ev)
        {
            Book book = ev.getBook();

            getDefaultBible().unsetDefaultConditionally(book);
            getDefaultCommentary().unsetDefaultConditionally(book);
            getDefaultDailyDevotional().unsetDefaultConditionally(book);
            getDefaultDictionary().unsetDefaultConditionally(book);
            getDefaultGreekDefinitions().unsetDefaultConditionally(book);
            getDefaultGreekParse().unsetDefaultConditionally(book);
            getDefaultHebrewDefinitions().unsetDefaultConditionally(book);
            getDefaultHebrewParse().unsetDefaultConditionally(book);
        }
    }

    private static final String BIBLE_KEY = "bible-names"; //$NON-NLS-1$
    private static final String COMMENTARY_KEY = "commentary-names"; //$NON-NLS-1$
    private static final String DICTIONARY_KEY = "dictionary-names"; //$NON-NLS-1$
    private static final String DAILY_DEVOTIONALS_KEY = "daily-devotional-names"; //$NON-NLS-1$
    private static final String GREEKDEF_KEY = "greekdef-names"; //$NON-NLS-1$
    private static final String HEBREWDEF_KEY = "hebrewdef-names"; //$NON-NLS-1$
    private static final String GREEKPARSE_KEY = "greekparse-names"; //$NON-NLS-1$
    private static final String HEBREWPARSE_KEY = "hebrewparse-names"; //$NON-NLS-1$

    /**
     * Indicates whether the last book of each type is used next time.
     */
    private static boolean currentBible = true;

    /**
     * The default Bible
     */
    private static DefaultBook bibleDeft = new DefaultBook(Books.installed(), BookFilters.getOnlyBibles());

    /**
     * The default Commentary
     */
    private static DefaultBook commentaryDeft = new DefaultBook(Books.installed(), BookFilters.getCommentaries());

    /**
     * The default DailyDevotional
     */
    private static DefaultBook dailyDevotionalDeft = new DefaultBook(Books.installed(), BookFilters.getDailyDevotionals());

    /**
     * The default Dictionary
     */
    private static DefaultBook dictionaryDeft = new DefaultBook(Books.installed(), BookFilters.getDictionaries());

    /**
     * The default Greek Parse Dictinary.
     */
    private static DefaultBook greekParseDeft = new DefaultBook(Books.installed(), BookFilters.getGreekParse());

    /**
     * The default Hebrew Parse Dictinary.
     */
    private static DefaultBook hebrewParseDeft = new DefaultBook(Books.installed(), BookFilters.getHebrewParse());

    /**
     * The default Greek Definitions Dictinary.
     */
    private static DefaultBook greekDefinitionsDeft = new DefaultBook(Books.installed(), BookFilters.getGreekDefinitions());

    /**
     * The default Hebrew Definitions Dictionary.
     */
    private static DefaultBook hebrewDefinitionsDeft = new DefaultBook(Books.installed(), BookFilters.getHebrewDefinitions());

    /**
     * Register with Books so we know how to provide valid defaults
     */
    static
    {
        Books.installed().addBooksListener(new DefaultsBookListener());
        checkAllPreferable();
    }

}
