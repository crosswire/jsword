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
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.BooksEvent;
import org.crosswire.jsword.book.BooksListener;
import org.crosswire.jsword.book.Search;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyList;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.util.ConverterFactory;
import org.xml.sax.SAXException;

/**
 * All the methods in this class highlight some are of the API and how to use it.
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
     * @see Passage
     * @see PassageFactory
     */
    public void readPlainText() throws BookException, NoSuchVerseException
    {
        Passage ref = PassageFactory.createPassage("Gen 1 1"); //$NON-NLS-1$
        Books books = Books.installed();
        Book bible = books.getBookMetaData(BIBLE_NAME).getBook();

        BookData data = bible.getData(ref);
        String text = data.getPlainText();

        System.out.println("The plain text of Gen 1:1 is "+text); //$NON-NLS-1$
    }

    /**
     * This method demonstrates how to get styled text (in this case HTML) from
     * a verse, and print it to stdout. Reading from a Commentary is just the
     * same as reading from a Bible.
     * @see Book
     * @see Passage
     * @see PassageFactory
     * @see SAXEventProvider
     */
    public void readStyledText() throws NoSuchVerseException, BookException, TransformerException, SAXException
    {
        Passage ref = PassageFactory.createPassage("Gen 1 1"); //$NON-NLS-1$
        Book bible = Books.installed().getBookMetaData(BIBLE_NAME).getBook();

        BookData data = bible.getData(ref);
        SAXEventProvider osissep = data.getSAXEventProvider();

        Converter styler = ConverterFactory.getConverter();

        SAXEventProvider htmlsep = styler.convert(osissep);
        String text = XMLUtil.writeToString(htmlsep);

        System.out.println("The html text of Gen 1:1 is "+text); //$NON-NLS-1$

        // This just shuts eclipse up.
        osissep.hashCode();
    }

    /**
     * While Bible and Commentary are very similar, a Dictionary is read in a
     * slightly different way. It is also worth looking at the JavaDoc for
     * Book that has a way of treating Bible, Commentary and Dictionary the same.
     * @see Key
     * @see Book
     */
    public void readDictionary() throws BookException
    {
        // This just gets a list of all the known dictionaries and picks the
        // first. In a real world app you will probably have a better way
        // of doing this.
        List dicts = Books.installed().getBookMetaDatas(BookFilters.getDictionaries());
        BookMetaData bmd = (BookMetaData) dicts.get(0);
        Book dict = bmd.getBook();

        // If I want every key in the Dictionary then I do this (or something
        // like it - in the real world you want to call hasNext() on an iterator
        // before next() but the point is the same:
        KeyList keys = dict.getGlobalKeyList();
        Key first = (Key) keys.iterator().next();

        System.out.println("The first Key in the default dictionary is "+first); //$NON-NLS-1$
        
        BookData data = dict.getData(first);
        System.out.println("And the text against that key is "+data.getPlainText()); //$NON-NLS-1$
    }

    /**
     * An example of how to search for various bits of data.
     * @see Search
     */
    public void search() throws BookException
    {
        Book bible = Books.installed().getBookMetaData(BIBLE_NAME).getBook();

        // This does a standard operator search. See the search documentation
        // for more examples of how to search
        Search search = new Search("moses + aaron", false); //$NON-NLS-1$
        Key key = bible.find(search);

        System.out.println("The following verses contain both moses and aaron: " + key.getName()); //$NON-NLS-1$

        // Or you can do a best match search ...
        search = new Search("for god so loves the world", true); //$NON-NLS-1$
        key = bible.find(search);

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
        BookMetaData bmd;

        // The Default Bible - JSword does everything it can to make this work
        bmd = Books.installed().getBookMetaData(BIBLE_NAME);
        Book bible = bmd.getBook();

        // You can only get a Bible (or any other Book) via a MetaData object
        // to help save resources. It means you can find out all about a Book
        // without reading indexes off disk or similar.

        // If you want a greater selection of Books:
        List everything = Books.installed().getBookMetaDatas();

        // Or you can narrow the range a bit
        List bibles = Books.installed().getBookMetaDatas(BookFilters.getBibles());

        // There are implementations of BookFilter for all sorts of things in
        // the BookFilters class 

        // Assuming that we got some Bibles then we can use an Iterator or just
        // go to one direct ... Remember that we always go via a MetaData object.
        bmd = (BookMetaData) bibles.get(0);

        // The code above is safe - we know we are only going to get
        // BibleMetaData objects in the List if we've asked from them with a
        // BookFilter. However this *could* fail.
        bmd = (BookMetaData) everything.get(0);

        // If you asked for a mixed list then you should use this: 
        BookMetaData md = (BookMetaData) everything.get(0);

        // If you are wanting to get really fancy you can implement your own
        // Bookfilter easily
        List test = Books.installed().getBookMetaDatas(new BookFilter()
        {
            public boolean test(BookMetaData tbmd)
            {
                return tbmd.getName().equals("My Favorite Version"); //$NON-NLS-1$
            }
        });
        bmd = (BookMetaData) test.get(0);

        // This is exactly the same as getting an Iterator on the 'everything'
        // List from above except that you can pass the BookFilter around and
        // store it in a local variable.

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

        // NOWARN: Ignore this line: it just shuts eclipse up
        md.hashCode(); bible.hashCode();
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
