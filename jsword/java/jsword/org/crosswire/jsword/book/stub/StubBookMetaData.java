
package org.crosswire.jsword.book.stub;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.Properties;

import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BibleMetaData;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Commentary;
import org.crosswire.jsword.book.CommentaryMetaData;
import org.crosswire.jsword.book.Dictionary;
import org.crosswire.jsword.book.DictionaryMetaData;
import org.crosswire.jsword.book.Openness;
import org.crosswire.jsword.book.basic.AbstractBibleMetaData;

/**
 * Stubbed out implementation of BibleMetaData.
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
public class StubBookMetaData extends AbstractBibleMetaData implements BibleMetaData, DictionaryMetaData, CommentaryMetaData
{
    /**
     * Constructor for StubBookMetaData.
     */
    public StubBookMetaData(Properties prop) throws MalformedURLException, ParseException
    {
        super(prop);
    }

    /**
     * Constructor for StubBookMetaData.
     */
    public StubBookMetaData(String name, String edition, String initials, Date pub, Openness open, URL licence)
    {
        super(name, edition, initials, pub, open, licence);
    }

    /**
     * Constructor for StubBookMetaData.
     */
    public StubBookMetaData(String name, String edition, String initials, String pubstr, String openstr, String licencestr) throws ParseException, MalformedURLException
    {
        super(name, edition, initials, pubstr, openstr, licencestr);
    }

    /**
     * Constructor for StubBookMetaData.
     */
    public StubBookMetaData(String name)
    {
        super(name);
    }

    /**
     * Fetch a currently existing Bible, read-only
     * @param name The name of the version to create
     * @exception BookException If the name is not valid
     */
    public Bible getBible() throws BookException
    {
        return (Bible) getBookInternal();
    }

    /**
     * @see org.crosswire.jsword.book.DictionaryMetaData#getDictionary()
     */
    public Dictionary getDictionary() throws BookException
    {
        return (Dictionary) getBookInternal();
    }

    /**
     * @see org.crosswire.jsword.book.CommentaryMetaData#getCommentary()
     */
    public Commentary getCommentary() throws BookException
    {
        return (Commentary) getBookInternal();
    }

    private Book getBookInternal()
    {
        // DCL
        // I know double checked locking is theoretically broken however it isn't
        // practically broken 99% of the time, and even if the 1% comes up here
        // the only effect is some temporary wasted memory
        if (book == null)
        {
            synchronized(this)
            {
                if (book == null)
                    book = new StubBook(this);
            }
        }

        return book;
    }

    /**
     * @see org.crosswire.jsword.book.BookMetaData#getDriverName()
     */
    public String getDriverName()
    {
        return "Stub";
    }

    /**
     * The expected speed at which this implementation gets correct answers.
     * @see org.crosswire.jsword.book.BookMetaData#getSpeed()
     */
    public int getSpeed()
    {
        return Books.SPEED_INACCURATE;
    }

    /**
     * The cached bible so we don't have to create too many
     */
    private Book book = null;
}
