
package org.crosswire.jsword.book.basic;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.Properties;

import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BibleMetaData;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.Openness;

/**
 * A default implmentation of BibleMetaData.
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
public abstract class AbstractBibleMetaData extends AbstractBookMetaData implements BibleMetaData
{
    /**
     * Basic constructor
     */
    public AbstractBibleMetaData(BookDriver driver, Properties prop) throws MalformedURLException, ParseException
    {
        super(driver, prop);
    }

    /**
     * Basic constructor where the user is expected to create correct
     * Date, Openness and URL objects
     */
    public AbstractBibleMetaData(BookDriver driver, String name, String edition, String initials, Date pub, Openness open, URL licence)
    {
        super(driver, name, edition, initials, pub, open, licence);
    }

    /**
     * Basic constructor where we do all the string conversion for the user
     */
    public AbstractBibleMetaData(BookDriver driver, String name, String edition, String initials, String pubstr, String openstr, String licencestr) throws ParseException, MalformedURLException
    {
        super(driver, name, edition, initials, pubstr, openstr, licencestr);
    }

    /**
     * Ctor for when we only know the book name
     */
    public AbstractBibleMetaData(BookDriver driver, String name)
    {
        super(driver, name);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getBook()
     */
    public Book getBook()
    {
        return getBible();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BibleMetaData#getBible()
     */
    public Bible getBible()
    {
        return bible;
    }

    /**
     * A simple create method that does not need to worry about checking that
     * a Bible has already been created. This method should only be called once
     * for many calls to getBible()
     * @see BibleMetaData#getBible()
     */
    protected void setBible(Bible bible)
    {
        this.bible = bible;
    }

    /**
     * The cached bible so we don't have to create too many
     */
    private Bible bible = null;
}
