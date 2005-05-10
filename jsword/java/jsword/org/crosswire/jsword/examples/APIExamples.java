/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $ID$
 */
package org.crosswire.jsword.examples;

import java.util.List;

import javax.xml.transform.TransformerException;

import org.crosswire.common.xml.Converter;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.BooksEvent;
import org.crosswire.jsword.book.BooksListener;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.util.ConverterFactory;
import org.xml.sax.SAXException;

/**
 * All the methods in this class highlight some are of the API and how to use it.
 *
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class APIExamples
{
    /**
     * The name of a Bible to find
     */
    private static final String BIBLE_NAME = "KJV"; //$NON-NLS-1$

    /**
     * The source to this method is an example of how to read the plain text of
     * a verse, and print it to stdout. Reading from a Commentary is just the
     * same as reading from a Bible.
     * @see Book
     */
    public void readPlainText() throws BookException, NoSuchKeyException
    {
        Books books = Books.installed();
        Book bible = books.getBook(BIBLE_NAME);

        Key key = bible.getKey("Gen 1 1"); //$NON-NLS-1$
        BookData data = bible.getData(key);
        String text = data.getVerseText();

        System.out.println("The plain text of Gen 1:1 is " + text); //$NON-NLS-1$
    }

    /**
     * This method demonstrates how to get styled text (in this case HTML) from
     * a verse, and print it to stdout. Reading from a Commentary is just the
     * same as reading from a Bible.
     * @see Book
     * @see SAXEventProvider
     */
    public void readStyledText() throws NoSuchKeyException, BookException, TransformerException, SAXException
    {
        Book bible = Books.installed().getBook(BIBLE_NAME);

        Key key = bible.getKey("Gen 1 1"); //$NON-NLS-1$
        BookData data = bible.getData(key);
        SAXEventProvider osissep = data.getSAXEventProvider();

        Converter styler = ConverterFactory.getConverter();

        SAXEventProvider htmlsep = styler.convert(osissep);
        String text = XMLUtil.writeToString(htmlsep);

        System.out.println("The html text of Gen 1:1 is " + text); //$NON-NLS-1$
    }

    /**
     * While Bible and Commentary are very similar, a Dictionary is read in a
     * slightly different way. It is also worth looking at the JavaDoc for
     * Book that has a way of treating Bible, Commentary and Dictionary the same.
     * @see Book
     */
    public void readDictionary() throws BookException
    {
        // This just gets a list of all the known dictionaries and picks the
        // first. In a real world app you will probably have a better way
        // of doing this.
        List dicts = Books.installed().getBooks(BookFilters.getDictionaries());
        Book dict = (Book) dicts.get(0);

        // If I want every key in the Dictionary then I do this (or something
        // like it - in the real world you want to call hasNext() on an iterator
        // before next() but the point is the same:
        Key keys = dict.getGlobalKeyList();
        Key first = (Key) keys.iterator().next();

        System.out.println("The first Key in the default dictionary is " + first); //$NON-NLS-1$

        BookData data = dict.getData(keys);
        System.out.println("And the text against that key is " + data.getPlainText()); //$NON-NLS-1$
    }

    /**
     * An example of how to search for various bits of data.
     */
    public void search() throws BookException
    {
        Book bible = Books.installed().getBook(BIBLE_NAME);

        // This does a standard operator search. See the search documentation
        // for more examples of how to search
        Key key = bible.find("moses + aaron"); //$NON-NLS-1$

        System.out.println("The following verses contain both moses and aaron: " + key.getName()); //$NON-NLS-1$

        // Or you can do a best match search, by enclosing the string in quotes ...
        key = bible.find("\"for god so loves the world\""); //$NON-NLS-1$

        System.out.println("Trying to find verses like John 3:16: " + key.getName()); //$NON-NLS-1$
    }

    /**
     * This is an example of the different ways to select a Book from the
     * selection available.
     * @see org.crosswire.common.config.Config
     * @see Books
     */
    public void pickBible()
    {
        // The Default Bible - JSword does everything it can to make this work
        Book book = Books.installed().getBook(BIBLE_NAME);

        // And you can find out more too:
        System.out.println(book.getLanguage());

        // If you want a greater selection of Books:
        List books = Books.installed().getBooks();
        book = (Book) books.get(0);

        // Or you can narrow the range a bit
        books = Books.installed().getBooks(BookFilters.getBibles());
        book = (Book) books.get(0);

        // There are implementations of BookFilter for all sorts of things in
        // the BookFilters class

        // If you are wanting to get really fancy you can implement your own
        // Bookfilter easily
        List test = Books.installed().getBooks(new BookFilter()
        {
            public boolean test(Book bk)
            {
                return bk.getName().equals("My Favorite Version"); //$NON-NLS-1$
            }
        });
        book = (Book) test.get(0);

        // If you want to know about new books as they arrive:
        Books.installed().addBooksListener(new BooksListener()
        {
            /* (non-Javadoc)
             * @see org.crosswire.jsword.book.BooksListener#bookAdded(org.crosswire.jsword.book.BooksEvent)
             */
            public void bookAdded(BooksEvent ev)
            {
            }

            /* (non-Javadoc)
             * @see org.crosswire.jsword.book.BooksListener#bookRemoved(org.crosswire.jsword.book.BooksEvent)
             */
            public void bookRemoved(BooksEvent ev)
            {
            }
        });
    }

    /**
     * Quick Demo
     */
    public static void main(String[] args) throws Exception
    {
        APIExamples examples = new APIExamples();

        examples.readPlainText();
        examples.readStyledText();
        examples.readDictionary();
        examples.search();
    }
}
