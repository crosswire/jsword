
package docs;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;

import javax.xml.transform.TransformerException;

import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BibleMetaData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.Defaults;
import org.crosswire.jsword.book.Dictionary;
import org.crosswire.jsword.book.Key;
import org.crosswire.jsword.book.Search;
import org.crosswire.jsword.book.data.BookData;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.util.Style;

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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class APIExamples
{
    /**
     * The source to this method is an example of how to read the plain text of
     * a verse, and print it to stdout. Reading from a Commentary is just the
     * same as reading from a Bible.
     * @see Bible
     * @see Passage
     * @see PassageFactory
     */
    public void readPlainText() throws BookException, NoSuchVerseException
    {
        Passage ref = PassageFactory.createPassage("Mat 1 1");
        Bible bible = Defaults.getBibleMetaData().getBible();

        BookData data = bible.getData(ref);
        String text = data.getPlainText();

        System.out.println("The plain text of Mat 1:1 is "+text);
    }

    /**
     * This method demonstrates how to get styled text (in this case HTML) from
     * a verse, and print it to stdout. Reading from a Commentary is just the
     * same as reading from a Bible.
     * @see Bible
     * @see Passage
     * @see PassageFactory
     * @see SAXEventProvider
     * @see Style
     */
    public void readStyledText() throws NoSuchVerseException, BookException, IOException, TransformerException
    {
        Passage ref = PassageFactory.createPassage("Mat 1 1");
        Bible bible = Defaults.getBibleMetaData().getBible();

        BookData data = bible.getData(ref);
        SAXEventProvider sep = data.getSAXEventProvider();

        // It would be normal to store 'styler' in a class variable (field)
        Style styler = new Style("html");
        String html = styler.applyStyleToString(sep, "plain");

        System.out.println("The html text of Mat 1:1 is "+html);
    }

    /**
     * While Bible and Commentary are very similar, a Dictionary is read in a
     * slightly different way. It is also worth looking at the JavaDoc for
     * Book that has a way of treating Bible, Commentary and Dictionary the same.
     * @see Dictionary
     * @see Key
     * @see org.crosswire.jsword.book.Book
     */
    public void readDictionary() throws BookException
    {
        Dictionary dict = Defaults.getDictionaryMetaData().getDictionary();

        // If I want every key in the Dictionary then I do this:
        SortedSet keys = dict.getIndex("");
        Key first = (Key) keys.first();

        System.out.println("The first Key in the default dictionary is "+first);
        
        BookData data = dict.getData(first);
        System.out.println("And the text against that key is "+data.getPlainText());
    }

    /**
     * An example of how to search for various bits of data.
     * @see Search
     */
    public void search() throws BookException
    {
        Bible bible = Defaults.getBibleMetaData().getBible();

        // This does a standard operator search. See the search documentation
        // for more examples of how to search
        Search search = new Search("moses + aaron", false);
        Passage ref = bible.findPassage(search);

        System.out.println("The following verses contain both moses and aaron: " + ref.getName());

        // Or you can do a best match search ...
        search = new Search("for god so loves the world", true);
        ref = bible.findPassage(search);

        System.out.println("Trying to find verses like John 3:16: " + ref.getName());
    }

    /**
     * This is an example of the different ways to select a Book from the
     * selection available.
     * @see org.crosswire.common.config.Config
     * @see Defaults
     * @see Books
     */
    public void pickBible()
    {
        BibleMetaData bmd;

        // The Default Bible - JSword does everything it can to make this work
        bmd = Defaults.getBibleMetaData();
        Bible bible = bmd.getBible();

        // You can only get a Bible (or any other Book) via a MetaData object
        // to help save resources. It means you can find out all about a Book
        // without reading indexes off disk or similar.

        // If you want a greater selection of Books:
        List everything = Books.getBooks();

        // Or you can narrow the range a bit
        List bibles = Books.getBooks(BookFilters.getBibles());

        // There are implementations of BookFilter for all sorts of things in
        // the BookFilters class 

        // Assuming that we got some Bibles then we can use an Iterator or just
        // go to one direct ... Remember that we always go via a MetaData object.
        bmd = (BibleMetaData) bibles.get(0);

        // The code above is safe - we know we are only going to get
        // BibleMetaData objects in the List if we've asked from them with a
        // BookFilter. However this *could* fail.
        bmd = (BibleMetaData) everything.get(0);

        // If you asked for a mixed list then you should use this: 
        BookMetaData md = (BookMetaData) everything.get(0);

        // If you are wanting to get really fancy you can implement your own
        // Bookfilter easily
        List test = Books.getBooks(new BookFilter()
        {
            public boolean test(BookMetaData tbmd)
            {
                return tbmd.getName().equals("My Favorite Version");
            }
        });
        bmd = (BibleMetaData) test.get(0);

        // This is exactly the same as getting an Iterator on the 'everything'
        // List from above except that you can pass the BookFilter around and
        // store it in a local variable.

        // If you want to make that Book the Default you can do:
        Defaults.setBibleMetaData(bmd);

        // The config system will pick up any changes so there is a good chance
        // that this setting will persist across sessions. See the config
        // documentation for more information.

        // This line just shuts eclipse up
        md.hashCode(); bible.hashCode();
    }
}
