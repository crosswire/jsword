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
package org.crosswire.jsword.book;

import java.util.Map;
import java.util.TreeMap;

import org.crosswire.common.config.ChoiceFactory;

/**
 * Handles the current default Books.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public final class Defaults {
    /**
     * Prevent construction.
     */
    private Defaults() {
    }

    /**
     * Determine whether the getBible should return the current Bible or the
     * user's chosen default.
     * 
     * @return true if the bible tracks the user's selection
     */
    public static boolean isCurrentBible() {
        return trackBible;
    }

    /**
     * Establish whether the getBible should return the current Bible or the
     * user's chosen default.
     * 
     * @param current whether getBible tracks the current Bible
     */
    public static void setCurrentBible(boolean current) {
        trackBible = current;
    }

    /**
     * If the user has chosen to remember the book (by type) then set the
     * current book for that type.
     * 
     * @param book the current Book
     */
    public static void setCurrentBook(Book book) {
        BookCategory type = book.getBookCategory();
        if (type.equals(BookCategory.BIBLE) && isCurrentBible()) {
            currentBible = book;
        }
    }

    /**
     * Get the current Bible, if set, else the default Bible.
     * 
     * @return the current Bible
     */
    public static Book getCurrentBible() {
        if (currentBible == null) {
            return bibleDeft.getDefault();
        }
        return currentBible;
    }

    /**
     * Set the default Bible.
     * 
     * @param book the default Bible
     */
    public static void setBible(Book book) {
        bibleDeft.setDefault(book);
    }

    /**
     * Unset the default Bible.
     */
    protected static void unsetBible() {
        bibleDeft.unsetDefault();
    }

    /**
     * @return the default Bible
     */
    public static Book getBible() {
        return bibleDeft.getDefault();
    }

    /**
     * @return the name of the default Bible
     */
    public static String getBibleByName() {
        return bibleDeft.getDefaultName();
    }

    /**
     * Set the default Bible by name.
     * 
     * @param name the name of the default Bible
     */
    public static void setBibleByName(String name) {
        bibleDeft.setDefaultByName(name);
    }

    /**
     * Set the default commentary.
     * 
     * @param book the default commentary
     */
    public static void setCommentary(Book book) {
        commentaryDeft.setDefault(book);
    }

    /**
     * Unset the default commentary.
     */
    protected static void unsetCommentary() {
        commentaryDeft.unsetDefault();
    }

    /**
     * @return the default commentary
     */
    public static Book getCommentary() {
        return commentaryDeft.getDefault();
    }

    /**
     * @return the name of the default commentary
     */
    public static String getCommentaryByName() {
        return commentaryDeft.getDefaultName();
    }

    /**
     * Set the default commentary by name.
     * 
     * @param name the default commentary's name
     */
    public static void setCommentaryByName(String name) {
        commentaryDeft.setDefaultByName(name);
    }

    /**
     * Set the default dictionary.
     * 
     * @param book the default dictionary
     */
    public static void setDictionary(Book book) {
        dictionaryDeft.setDefault(book);
    }

    /**
     * Unset the default dictionary.
     */
    protected static void unsetDictionary() {
        dictionaryDeft.unsetDefault();
    }

    /**
     * @return the default dictionary
     */
    public static Book getDictionary() {
        return dictionaryDeft.getDefault();
    }

    /**
     * @return the name of the default dictionary
     */
    public static String getDictionaryByName() {
        return dictionaryDeft.getDefaultName();
    }

    /**
     * Set the default dictionary by name.
     * 
     * @param name the name of the default dictionary
     */
    public static void setDictionaryByName(String name) {
        dictionaryDeft.setDefaultByName(name);
    }

    /**
     * Set the default daily devotional.
     * 
     * @param book the default daily devotional
     */
    public static void setDailyDevotional(Book book) {
        dictionaryDeft.setDefault(book);
    }

    /**
     * Unset the default daily devotional.
     */
    protected static void unsetDailyDevotional() {
        dailyDevotionalDeft.unsetDefault();
    }

    /**
     * @return the default daily devotional
     */
    public static Book getDailyDevotional() {
        return dailyDevotionalDeft.getDefault();
    }

    /**
     * @return the name of the default daily devotional
     */
    public static String getDailyDevotionalByName() {
        return dailyDevotionalDeft.getDefaultName();
    }

    /**
     * Set the default daily devotional by name.
     * 
     * @param name the name of the default daily devotional
     */
    public static void setDailyDevotionalByName(String name) {
        dailyDevotionalDeft.setDefaultByName(name);
    }

    /**
     * Set the default Greek Strong's Numbers dictionary.
     * 
     * @param book the default Greek Strong's Numbers dictionary.
     */
    public static void setGreekDefinitions(Book book) {
        greekDefinitionsDeft.setDefault(book);
    }

    /**
     * Unset the default Greek Strong's Numbers dictionary.
     */
    protected static void unsetGreekDefinitions() {
        greekDefinitionsDeft.unsetDefault();
    }

    /**
     * @return the default Greek Strong's Numbers dictionary
     */
    public static Book getGreekDefinitions() {
        return greekDefinitionsDeft.getDefault();
    }

    /**
     * @return the name of the default Greek Strong's Numbers dictionary
     */
    public static String getGreekDefinitionsByName() {
        return greekDefinitionsDeft.getDefaultName();
    }

    /**
     * Set the default Greek Strong's Numbers dictionary by name.
     * 
     * @param name the name of the default Greek Strong's Numbers dictionary
     */
    public static void setGreekDefinitionsByName(String name) {
        greekDefinitionsDeft.setDefaultByName(name);
    }

    /**
     * Set the default Hebrew Strong's Numbers dictionary.
     * 
     * @param book the default Hebrew Strong's Numbers dictionary.
     */
    public static void setHebrewDefinitions(Book book) {
        hebrewDefinitionsDeft.setDefault(book);
    }

    /**
     * Unset the default Hebrew Strong's Numbers dictionary.
     */
    protected static void unsetHebrewDefinitions() {
        hebrewDefinitionsDeft.unsetDefault();
    }

    /**
     * @return the default Hebrew Strong's Numbers dictionary
     */
    public static Book getHebrewDefinitions() {
        return hebrewDefinitionsDeft.getDefault();
    }

    /**
     * @return the name of the default Hebrew Strong's Numbers dictionary
     */
    public static String getHebrewDefinitionsByName() {
        return hebrewDefinitionsDeft.getDefaultName();
    }

    /**
     * Set the default Hebrew Strong's Numbers dictionary by name.
     * 
     * @param name the name of the default Hebrew Strong's Numbers dictionary
     */
    public static void setHebrewDefinitionsByName(String name) {
        hebrewDefinitionsDeft.setDefaultByName(name);
    }

    /**
     * Set the default Greek morphology dictionary.
     * 
     * @param book the default Greek morphology dictionary.
     */
    public static void setGreekParse(Book book) {
        greekParseDeft.setDefault(book);
    }

    /**
     * Unset the default Greek morphology dictionary.
     */
    protected static void unsetGreekParse() {
        greekParseDeft.unsetDefault();
    }

    /**
     * @return the default Greek morphology dictionary
     */
    public static Book getGreekParse() {
        return greekParseDeft.getDefault();
    }

    /**
     * Set the default Greek morphology dictionary by name.
     * 
     * @return the name of the default Greek morphology dictionary
     */
    public static String getGreekParseByName() {
        return greekParseDeft.getDefaultName();
    }

    /**
     * @param name the name of the default Greek morphology dictionary
     */
    public static void setGreekParseByName(String name) {
        greekParseDeft.setDefaultByName(name);
    }

    /**
     * Set the default Hebrew morphology dictionary by name.
     * 
     * @param book the default Hebrew morphology dictionary by name.
     */
    public static void setHebrewParse(Book book) {
        hebrewParseDeft.setDefault(book);
    }

    /**
     * Unset the default Hebrew morphology dictionary.
     */
    protected static void unsetHebrewParse() {
        hebrewParseDeft.unsetDefault();
    }

    /**
     * @return the default Hebrew morphology dictionary
     */
    public static Book getHebrewParse() {
        return hebrewParseDeft.getDefault();
    }

    /**
     * @return the name of the default Hebrew morphology dictionary
     */
    public static String getHebrewParseByName() {
        return hebrewParseDeft.getDefaultName();
    }

    /**
     * Set the default Hebrew morphology dictionary by name.
     * 
     * @param name the default Hebrew morphology dictionary by name.
     */
    public static void setHebrewParseByName(String name) {
        hebrewParseDeft.setDefaultByName(name);
    }

    /**
     * @return the default Bible
     */
    protected static DefaultBook getDefaultBible() {
        return bibleDeft;
    }

    /**
     * @return the default commentary
     */
    protected static DefaultBook getDefaultCommentary() {
        return commentaryDeft;
    }

    /**
     * @return the default dictionary
     */
    protected static DefaultBook getDefaultDictionary() {
        return dictionaryDeft;
    }

    /**
     * @return the default daily devotional
     */
    protected static DefaultBook getDefaultDailyDevotional() {
        return dailyDevotionalDeft;
    }

    /**
     * @return the default Greek Strong's Numbers dictionary
     */
    protected static DefaultBook getDefaultGreekDefinitions() {
        return greekDefinitionsDeft;
    }

    /**
     * @return the default Hebrew Strong's Numbers dictionary
     */
    protected static DefaultBook getDefaultHebrewDefinitions() {
        return hebrewDefinitionsDeft;
    }

    /**
     * @return the default Greek morphology dictionary
     */
    protected static DefaultBook getDefaultGreekParse() {
        return greekParseDeft;
    }

    /**
     * @return the default Hebrew morphology dictionary
     */
    protected static DefaultBook getDefaultHebrewParse() {
        return hebrewParseDeft;
    }

    /**
     * Create book lists for every type of book.
     */
    public static void refreshBooks() {
        // Create the array of Bibles
        Map<Book, String> bnames = getBookMap(BookFilters.getOnlyBibles());
        ChoiceFactory.getDataMap().put(BIBLE_KEY, bnames);

        // Create the array of Commentaries
        Map<Book, String> cnames = getBookMap(BookFilters.getCommentaries());
        ChoiceFactory.getDataMap().put(COMMENTARY_KEY, cnames);

        // Create the array of Dictionaries
        Map<Book, String> dnames = getBookMap(BookFilters.getDictionaries());
        ChoiceFactory.getDataMap().put(DICTIONARY_KEY, dnames);

        // Create the array of DailyDevotionals
        Map<Book, String> rnames = getBookMap(BookFilters.getDailyDevotionals());
        ChoiceFactory.getDataMap().put(DAILY_DEVOTIONALS_KEY, rnames);

        // Create the array of Dictionaries
        Map<Book, String> greekDef = getBookMap(BookFilters.getGreekDefinitions());
        ChoiceFactory.getDataMap().put(GREEKDEF_KEY, greekDef);

        // Create the array of Dictionaries
        Map<Book, String> hebrewDef = getBookMap(BookFilters.getHebrewDefinitions());
        ChoiceFactory.getDataMap().put(HEBREWDEF_KEY, hebrewDef);

        // Create the array of Dictionaries
        Map<Book, String> greekParse = getBookMap(BookFilters.getGreekParse());
        ChoiceFactory.getDataMap().put(GREEKPARSE_KEY, greekParse);

        // Create the array of Dictionaries
        Map<Book, String> hebrewParse = getBookMap(BookFilters.getHebrewParse());
        ChoiceFactory.getDataMap().put(HEBREWPARSE_KEY, hebrewParse);
    }

    /**
     * Go through all of the current books checking to see if we need to replace
     * the current defaults with one of these.
     */
    protected static void checkAllPreferable() {
        for (Book book : Books.installed().getBooks()) {
            checkPreferable(book);
        }
    }

    /**
     * Determine whether this Book become the default. It should, only if there
     * is not one.
     * 
     * @param book the book to check
     */
    protected static void checkPreferable(Book book) {
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
     * 
     * @param filter the filter to locate matching books
     * @return matching books
     */
    private static Map<Book, String> getBookMap(BookFilter filter) {
        Map<Book, String> books = new TreeMap<Book, String>(BookComparators.getDefault());

        for (Book book : Books.installed().getBooks(filter)) {
            books.put(book, book.getName());
        }

        return books;
    }

    /**
     * To keep us up to date with changes in the available Books
     */
    static class DefaultsBookListener implements BooksListener {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.BooksListener#bookAdded(org.crosswire.jsword.book.BooksEvent)
         */
        public void bookAdded(BooksEvent ev) {
            Book book = ev.getBook();
            checkPreferable(book);
            refreshBooks();
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.BooksListener#bookRemoved(org.crosswire.jsword.book.BooksEvent)
         */
        public void bookRemoved(BooksEvent ev) {
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

    private static final String BIBLE_KEY = "bible-names";
    private static final String COMMENTARY_KEY = "commentary-names";
    private static final String DICTIONARY_KEY = "dictionary-names";
    private static final String DAILY_DEVOTIONALS_KEY = "daily-devotional-names";
    private static final String GREEKDEF_KEY = "greekdef-names";
    private static final String HEBREWDEF_KEY = "hebrewdef-names";
    private static final String GREEKPARSE_KEY = "greekparse-names";
    private static final String HEBREWPARSE_KEY = "hebrewparse-names";

    /**
     * Indicates whether the last book of each type is used next time.
     */
    private static boolean trackBible = true;

    /**
     * The current bible being tracked.
     */
    private static Book currentBible;

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
     * The default Greek Parse Dictionary.
     */
    private static DefaultBook greekParseDeft = new DefaultBook(Books.installed(), BookFilters.getGreekParse());

    /**
     * The default Hebrew Parse Dictionary.
     */
    private static DefaultBook hebrewParseDeft = new DefaultBook(Books.installed(), BookFilters.getHebrewParse());

    /**
     * The default Greek Definitions Dictionary.
     */
    private static DefaultBook greekDefinitionsDeft = new DefaultBook(Books.installed(), BookFilters.getGreekDefinitions());

    /**
     * The default Hebrew Definitions Dictionary.
     */
    private static DefaultBook hebrewDefinitionsDeft = new DefaultBook(Books.installed(), BookFilters.getHebrewDefinitions());

    /**
     * Register with Books so we know how to provide valid defaults
     */
    static {
        Books.installed().addBooksListener(new DefaultsBookListener());
        checkAllPreferable();
    }

}
