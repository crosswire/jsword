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
package org.crosswire.jsword.examples;

import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.ResourceUtil;
import org.crosswire.common.xml.Converter;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.common.xml.TransformingSAXEventProvider;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.BooksEvent;
import org.crosswire.jsword.book.BooksListener;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.index.search.DefaultSearchModifier;
import org.crosswire.jsword.index.search.DefaultSearchRequest;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageTally;
import org.crosswire.jsword.passage.RestrictionType;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.util.ConverterFactory;
import org.crosswire.jsword.versification.BibleInfo;
import org.xml.sax.SAXException;

/**
 * All the methods in this class highlight some are of the API and how to use it.
 *
 * @see gnu.lgpl.License for license details.
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
        BookData data = bible.getBookData(key);
        String text = OSISUtil.getCanonicalText(data.getOsis());

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
        BookData data = bible.getBookData(key);
        SAXEventProvider osissep = data.getSAXEventProvider();

        Converter styler = ConverterFactory.getConverter();

        TransformingSAXEventProvider htmlsep = (TransformingSAXEventProvider) styler.convert(osissep);

        // You can also pass parameters to the xslt. What you pass depends upon what the xslt can use.
        BookMetaData bmd = bible.getBookMetaData();
        boolean direction = bmd.isLeftToRight();
        htmlsep.setParameter("direction", direction ? "ltr" : "rtl"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        // Finally you can get the styled text.
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

        BookData data = dict.getBookData(keys);
        System.out.println("And the text against that key is " + OSISUtil.getPlainText(data.getOsis())); //$NON-NLS-1$
    }

    /**
     * An example of how to search for various bits of data.
     */
    public void search() throws BookException
    {
        Book bible = Books.installed().getBook(BIBLE_NAME);

        // This does a standard operator search. See the search documentation
        // for more examples of how to search
        Key key = bible.find("+moses +aaron"); //$NON-NLS-1$

        System.out.println("The following verses contain both moses and aaron: " + key.getName()); //$NON-NLS-1$

        // You can also trim the result to a more managable quantity.
        // The test here is not necessary since we are working with a bible. It is necessary if we don't know what it is.
        if (key instanceof Passage)
        {
            Passage remaining = ((Passage) key).trimVerses(5);
            System.out.println("The first 5 verses containing both moses and aaron: " + key.getName()); //$NON-NLS-1$
            System.out.println("The rest of the verses are: " + remaining.getName()); //$NON-NLS-1$
        }
    }

    /**
     * An example of how to perform a ranked search.
     * @throws BookException
     */
    void rankedSearch() throws BookException
    {
        Book bible = Books.installed().getBook(BIBLE_NAME);

        // For a more complex example:
        // Rank the verses and show the first 20
        boolean rank = true;

        DefaultSearchModifier modifier = new DefaultSearchModifier();
        modifier.setRanked(rank);

        Key results = bible.find(new DefaultSearchRequest("for god so loved the world", modifier)); //$NON-NLS-1$
        int total = results.getCardinality();
        int partial = total;

        // we get PassageTallys for rank searches
        if (results instanceof PassageTally || rank)
        {
            PassageTally tally = (PassageTally) results;
            tally.setOrdering(PassageTally.ORDER_TALLY);
            int rankCount = 20;
            if (rankCount > 0 && rankCount < total)
            {
                // Here we are trimming by ranges, where a range is a set of continuous verses.
                tally.trimRanges(rankCount, RestrictionType.NONE);
                partial = rankCount;
            }
        }
        System.out.println("Showing the first " + partial + " of " + total + " verses."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        System.out.println(results);
    }

    /**
     * An example of how to do a search and then get text for each range of verses.
     * @throws BookException
     * @throws SAXException
     */
    void searchAndShow() throws BookException, SAXException
    {
        Book bible = Books.installed().getBook(BIBLE_NAME);

        // Search for words like Melchezedik
        Key key = bible.find("melchesidec~"); //$NON-NLS-1$

        // Here is an example of how to iterate over the ranges and get the text for each
        // The key's iterator would have iterated over verses.

        // The following shows how to use a stylesheet of your own choosing
        String path = "xsl/cswing/simple.xsl"; //$NON-NLS-1$
        URL xslurl = ResourceUtil.getResource(path);

        Iterator rangeIter = ((Passage) key).rangeIterator(RestrictionType.CHAPTER); // Make ranges break on chapter boundaries.
        while (rangeIter.hasNext())
        {
            Key range = (Key) rangeIter.next();
            BookData data = bible.getBookData(range);
            SAXEventProvider osissep = data.getSAXEventProvider();
            SAXEventProvider htmlsep = new TransformingSAXEventProvider(NetUtil.toURI(xslurl), osissep);
            String text = XMLUtil.writeToString(htmlsep);
            System.out.println("The html text of " + range.getName() + " is " + text); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * An example of how to get the text of a book for export.
     * 
     * @throws NoSuchKeyException
     * @throws BookException
     */
    public void export() throws NoSuchKeyException, BookException
    {
        Book bible = Books.installed().getBook(BIBLE_NAME);
        Key keys = bible.getKey("Gen"); //$NON-NLS-1$
        // Get a verse iterator
        Iterator iter = keys.iterator();
        while (iter.hasNext())
        {
            Verse verse = (Verse) iter.next();
            BookData data = bible.getBookData(verse);
            System.out.println('|' + BibleInfo.getBookName(verse.getBook()) + '|' + verse.getChapter() + '|' + verse.getVerse() + '|' + OSISUtil.getCanonicalText(data.getOsis()));
        }
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
        books = Books.installed().getBooks(BookFilters.getOnlyBibles());
        book = (Book) books.get(0);

        // There are implementations of BookFilter for all sorts of things in
        // the BookFilters class

        // If you are wanting to get really fancy you can implement your own
        // BookFilter easily
        List test = Books.installed().getBooks(new MyBookFilter());
        book = (Book) test.get(0);

        if (book != null)
        {
            System.out.println(book.getInitials());
        }


        // If you want to know about new books as they arrive:
        Books.installed().addBooksListener(new MyBooksListener());
    }

    /**
     * A simple BookFilter that looks for a Bible by name.
     */
    static class MyBookFilter implements BookFilter
    {
        public boolean test(Book bk)
        {
            return bk.getName().equals("My Favorite Version"); //$NON-NLS-1$
        }
    }

    /**
     * A simple BooksListener that actually does nothing.
     */
    static class MyBooksListener implements BooksListener
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
    }

    /**
     * Quick Demo
     * @throws NoSuchKeyException 
     * @throws BookException 
     * @throws SAXException 
     * @throws TransformerException 
     */
    public static void main(String[] args) throws BookException, NoSuchKeyException, TransformerException, SAXException
    {
        APIExamples examples = new APIExamples();

        examples.readPlainText();
        examples.readStyledText();
        examples.readDictionary();
        examples.search();
        examples.rankedSearch();
        examples.searchAndShow();
        examples.export();
    }
}
