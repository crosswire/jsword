package org.crosswire.jsword.book.readings;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.Dictionary;
import org.crosswire.jsword.book.DictionaryMetaData;
import org.crosswire.jsword.book.Openness;
import org.crosswire.jsword.book.basic.AbstractBookMetaData;

/**
 * The meta data for a set of readings.
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
public class ReadingsDictionaryMetaData extends AbstractBookMetaData implements DictionaryMetaData
{
    /**
     * Simple constructor
     */
    public ReadingsDictionaryMetaData()
    {
        super("Daily Readings", "", "Readings", null, Openness.UNKNOWN, null);
    }

    /**
     * @see org.crosswire.jsword.book.DictionaryMetaData#getDictionary()
     */
    public Dictionary getDictionary() throws BookException
    {
        return new ReadingsDictionary(this);
    }

    /**
     * @see org.crosswire.jsword.book.BookMetaData#getDriverName()
     */
    public String getDriverName()
    {
        return "Readings";
    }

    /**
     * @see org.crosswire.jsword.book.BookMetaData#getSpeed()
     */
    public int getSpeed()
    {
        return Books.SPEED_MEDIUM;
    }

    /**
     * @see org.crosswire.jsword.book.BookMetaData#getBook()
     */
    public Book getBook() throws BookException
    {
        return getDictionary();
    }
}
