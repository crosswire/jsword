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
package org.crosswire.jsword.examples;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.ResourceUtil;
import org.crosswire.common.xml.Converter;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.common.xml.TransformingSAXEventProvider;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.BooksEvent;
import org.crosswire.jsword.book.BooksListener;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.install.InstallException;
import org.crosswire.jsword.book.install.InstallManager;
import org.crosswire.jsword.book.install.Installer;
import org.crosswire.jsword.index.search.DefaultSearchModifier;
import org.crosswire.jsword.index.search.DefaultSearchRequest;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageTally;
import org.crosswire.jsword.passage.RestrictionType;
import org.crosswire.jsword.passage.VerseRange;
import org.crosswire.jsword.util.ConverterFactory;
import org.xml.sax.SAXException;

/**
 * All the methods in this class highlight some are of the API and how to use
 * it.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class APIExamples {
    /**
     * The name of a Bible to find
     */
    private static final String BIBLE_NAME = "KJV";

    /**
     * Get a particular installed book by initials.
     * 
     * @param bookInitials
     *            The book name to search for
     * @return The found book. Null otherwise.
     */
    public Book getBook(String bookInitials) {
        return Books.installed().getBook(bookInitials);
    }

    /**
     * Get just the canonical text of one or more book entries without any
     * markup.
     * 
     * @param bookInitials
     *            the book to use
     * @param reference
     *            a reference, appropriate for the book, of one or more entries
     * @return the plain text for the reference
     * @throws BookException 
     * @throws NoSuchKeyException 
     */
    public String getPlainText(String bookInitials, String reference) throws BookException, NoSuchKeyException {
        Book book = getBook(bookInitials);
        if (book == null) {
            return "";
        }

        Key key = book.getKey(reference);
        BookData data = new BookData(book, key);
        return OSISUtil.getCanonicalText(data.getOsisFragment());
    }

    /**
     * Obtain a SAX event provider for the OSIS document representation of one
     * or more book entries.
     * 
     * @param bookInitials
     *            the book to use
     * @param reference
     *            a reference, appropriate for the book, of one or more entries
     * @param maxKeyCount 
     * @return a SAX Event Provider to retrieve the reference
     * @throws BookException 
     * @throws NoSuchKeyException 
     */
    public SAXEventProvider getOSIS(String bookInitials, String reference, int maxKeyCount) throws BookException, NoSuchKeyException {
        if (bookInitials == null || reference == null) {
            return null;
        }

        Book book = getBook(bookInitials);

        Key key = null;
        if (BookCategory.BIBLE.equals(book.getBookCategory())) {
            key = book.getKey(reference);
            ((Passage) key).trimVerses(maxKeyCount);
        } else {
            key = book.createEmptyKeyList();

            int count = 0;
            for (Key aKey : book.getKey(reference)) {
                if (++count >= maxKeyCount) {
                    break;
                }
                key.addAll(aKey);
            }
        }

        BookData data = new BookData(book, key);

        return data.getSAXEventProvider();
    }

    /**
     * Obtain styled text (in this case HTML) for a book reference.
     * 
     * @param bookInitials
     *            the book to use
     * @param reference
     *            a reference, appropriate for the book, of one or more entries
     * @param maxKeyCount 
     * @return the styled text
     * @throws NoSuchKeyException 
     * @throws BookException 
     * @throws TransformerException 
     * @throws SAXException 
     * @see Book
     * @see SAXEventProvider
     */
    public String readStyledText(String bookInitials, String reference, int maxKeyCount) throws NoSuchKeyException, BookException, TransformerException,
            SAXException
    {
        Book book = getBook(bookInitials);
        SAXEventProvider osissep = getOSIS(bookInitials, reference, maxKeyCount);
        if (osissep == null) {
            return "";
        }

        Converter styler = ConverterFactory.getConverter();

        TransformingSAXEventProvider htmlsep = (TransformingSAXEventProvider) styler.convert(osissep);

        // You can also pass parameters to the XSLT. What you pass depends upon
        // what the XSLT can use.
        BookMetaData bmd = book.getBookMetaData();
        boolean direction = bmd.isLeftToRight();
        htmlsep.setParameter("direction", direction ? "ltr" : "rtl");

        // Finally you can get the styled text.
        return XMLUtil.writeToString(htmlsep);
    }

    /**
     * While Bible and Commentary are very similar, a Dictionary is read in a
     * slightly different way. It is also worth looking at the JavaDoc for Book
     * that has a way of treating Bible, Commentary and Dictionary the same.
     * 
     * @throws BookException 
     * @see Book
     */
    public void readDictionary() throws BookException {
        // This just gets a list of all the known dictionaries and picks the
        // first. In a real world app you will probably have a better way
        // of doing this.
        List<Book> dicts = Books.installed().getBooks(BookFilters.getDictionaries());
        Book dict = dicts.get(0);

        // If I want every key in the Dictionary then I do this (or something
        // like it - in the real world you want to call hasNext() on an iterator
        // before next() but the point is the same:
        Key keys = dict.getGlobalKeyList();
        Key first = keys.iterator().next();

        System.out.println("The first Key in the default dictionary is " + first);

        BookData data = new BookData(dict, first);
        System.out.println("And the text against that key is " + OSISUtil.getPlainText(data.getOsisFragment()));
    }

    /**
     * An example of how to search for various bits of data.
     * 
     * @throws BookException 
     */
    public void search() throws BookException {
        Book bible = Books.installed().getBook(BIBLE_NAME);

        // This does a standard operator search. See the search documentation
        // for more examples of how to search
        Key key = bible.find("+moses +aaron");

        System.out.println("The following verses contain both moses and aaron: " + key.getName());

        // You can also trim the result to a more manageable quantity.
        // The test here is not necessary since we are working with a bible. It
        // is necessary if we don't know what it
        // is.
        if (key instanceof Passage) {
            Passage remaining = ((Passage) key).trimVerses(5);
            System.out.println("The first 5 verses containing both moses and aaron: " + key.getName());
            if (remaining != null) {
                System.out.println("The rest of the verses are: " + remaining.getName());
            } else {
                System.out.println("There are only 5 verses containing both moses and aaron");
            }
        }
    }

    /**
     * An example of how to perform a ranked search.
     * 
     * @throws BookException
     */
    void rankedSearch() throws BookException {
        Book bible = Books.installed().getBook(BIBLE_NAME);

        // For a more complex example:
        // Rank the verses and show the first 20
        boolean rank = true;
        int max = 20;

        DefaultSearchModifier modifier = new DefaultSearchModifier();
        modifier.setRanked(rank);
        modifier.setMaxResults(max);

        Key results = bible.find(new DefaultSearchRequest("for god so loved the world", modifier));
        int total = results.getCardinality();
        int partial = total;

        // we get PassageTallys for rank searches
        if (results instanceof PassageTally || rank) {
            PassageTally tally = (PassageTally) results;
            tally.setOrdering(PassageTally.Order.TALLY);
            int rankCount = max;
            if (rankCount > 0 && rankCount < total) {
                // Here we are trimming by ranges, where a range is a set of
                // continuous verses.
                tally.trimRanges(rankCount, RestrictionType.NONE);
                partial = rankCount;
            }
        }
        System.out.println("Showing the first " + partial + " of " + total + " verses.");
        System.out.println(results);
    }

    /**
     * An example of how to do a search and then get text for each range of
     * verses.
     * 
     * @throws BookException
     * @throws SAXException
     */
    void searchAndShow() throws BookException, SAXException {
        Book bible = Books.installed().getBook(BIBLE_NAME);

        // Search for words like Melchezedik
        Key key = bible.find("melchesidec~");

        // Here is an example of how to iterate over the ranges and get the text
        // for each.
        // The key's iterator would have iterated over verses.

        // The following shows how to use a stylesheet of your own choosing
        String path = "xsl/cswing/simple.xsl";
        URL xslurl = ResourceUtil.getResource(path);
        // Make ranges  break  on  chapter
        Iterator<VerseRange> rangeIter = ((Passage) key).rangeIterator(RestrictionType.CHAPTER);
        // boundaries.
        while (rangeIter.hasNext()) {
            Key range = rangeIter.next();
            BookData data = new BookData(bible, range);
            SAXEventProvider osissep = data.getSAXEventProvider();
            SAXEventProvider htmlsep = new TransformingSAXEventProvider(NetUtil.toURI(xslurl), osissep);
            String text = XMLUtil.writeToString(htmlsep);
            System.out.println("The html text of " + range.getName() + " is " + text);
        }
    }

    /**
     * This is an example of the different ways to select a Book from the
     * selection available.
     * 
     * @see org.crosswire.common.config.Config
     * @see Books
     */
    public void pickBible() {
        // The Default Bible - JSword does everything it can to make this work
        Book book = Books.installed().getBook(BIBLE_NAME);

        // And you can find out more too:
        System.out.println(book.getLanguage());

        // If you want a greater selection of Books:
        List<Book> books = Books.installed().getBooks();
        book = books.get(0);

        // Or you can narrow the range a bit
        books = Books.installed().getBooks(BookFilters.getOnlyBibles());
        book = books.get(0);

        // There are implementations of BookFilter for all sorts of things in
        // the BookFilters class

        // If you are wanting to get really fancy you can implement your own
        // BookFilter easily
        List<Book> test = Books.installed().getBooks(new MyBookFilter("ESV"));
        book = test.get(0);

        if (book != null) {
            System.out.println(book.getInitials());
        }

        // If you want to know about new books as they arrive:
        Books.installed().addBooksListener(new MyBooksListener());
    }

    public void installBook() {
        // An installer knows how to install books
        Installer installer = null;

        InstallManager imanager = new InstallManager();

        // Ask the Install Manager for a map of all known module sites
        Map<String, Installer> installers = imanager.getInstallers();

        // Get all the installers one after the other
        String name = null;
        for (Map.Entry<String, Installer> mapEntry : installers.entrySet()) {
            name = mapEntry.getKey();
            installer = mapEntry.getValue();
            System.out.println(name + ": " + installer.getInstallerDefinition());
        }

        name = "CrossWire";
        // If we know the name of the installer we can get it directly
        installer = imanager.getInstaller(name);

        // Now we can get the list of books
        try {
            installer.reloadBookList();
        } catch (InstallException e) {
            e.printStackTrace();
        }

        // Get a list of all the available books
        List<Book> availableBooks = installer.getBooks();

        Book book = availableBooks.get(0);
        if (book != null) {
            System.out.println("Book " + book.getInitials() + " is available");
        }

        // get some available books. In this case, just one book.
        availableBooks = installer.getBooks(new MyBookFilter("ESV"));

        book = availableBooks.get(0);

        if (book != null) {
            System.out.println("Book " + book.getInitials() + " is available");

            // Delete the book, if present
            // At the moment, JSword will not re-install. Later it will, if the
            // remote version is greater.
            try {
                if (Books.installed().getBook("ESV") != null) {
                    // Make the book unavailable.
                    // This is normally done via listeners.
                    Books.installed().removeBook(book);

                    // Actually do the delete
                    // This should be a call on installer.
                    book.getDriver().delete(book);
                }
            } catch (BookException ex) {
                ex.printStackTrace();
            }

            try {
                // Now install it. Note this is a background task.
                installer.install(book);
            } catch (InstallException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * A simple BookFilter that looks for a Bible by name.
     */
    static class MyBookFilter implements BookFilter {
        MyBookFilter(String bookName) {
            name = bookName;
        }

        public boolean test(Book bk) {
            return bk.getInitials().equals(name);
        }

        private String name;
    }

    /**
     * A simple BooksListener that actually does nothing.
     */
    static class MyBooksListener implements BooksListener {
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.jsword.book.BooksListener#bookAdded(org.crosswire.jsword
         * .book.BooksEvent)
         */
        public void bookAdded(BooksEvent ev) {
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.jsword.book.BooksListener#bookRemoved(org.crosswire
         * .jsword.book.BooksEvent)
         */
        public void bookRemoved(BooksEvent ev) {
        }
    }

    /**
     * Quick Demo
     * @param args 
     * 
     * @throws NoSuchKeyException
     * @throws BookException
     * @throws SAXException
     * @throws TransformerException
     */
    public static void main(String[] args) throws BookException, NoSuchKeyException, TransformerException, SAXException {
        APIExamples examples = new APIExamples();

        examples.installBook();
        System.out.println("The plain text of Gen 1:1 is " + examples.getPlainText(BIBLE_NAME, "Gen 1:1"));
        System.out.println("The html text of Gen 1:1 is " + examples.readStyledText(BIBLE_NAME, "Gen 1:1", 100));
        examples.readDictionary();
        examples.search();
        examples.rankedSearch();
        examples.searchAndShow();
    }
}
