
package org.crosswire.jsword.book.remote;

import java.net.MalformedURLException;
import java.text.ParseException;

import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.basic.AbstractBibleMetaData;

/**
 * Simple implementation of BibleMetaData for testing
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
 * @version $Id: Bible.java,v 1.2 2002/10/08 21:36:07 joe Exp $
 */
public class FixtureBibleMetaData extends AbstractBibleMetaData
{
    /**
     * Basic constructor where we do all the string conversion for the user
     */
    public FixtureBibleMetaData(BookDriver driver, String name, String edition, String initials, String pubstr, String openstr, String licencestr) throws ParseException, MalformedURLException
    {
        super(driver, name, edition, initials, pubstr, openstr, licencestr);
    }

    /**
     * Constructor for FixtureBibleMetaData.
     * @param name
     */
    public FixtureBibleMetaData(BookDriver driver, String name)
    {
        super(driver, name);
    }

    /**
     * @see org.crosswire.jsword.book.BibleMetaData#getBible()
     */
    public Bible createBible()
    {
        return null;
    }

    /**
     * @see org.crosswire.jsword.book.BookMetaData#getDriverName()
     */
    public String getDriverName()
    {
        return "test";
    }

    /**
     * The expected speed at which this implementation gets correct answers.
     * @see org.crosswire.jsword.book.BookMetaData#getSpeed()
     */
    public int getSpeed()
    {
        return Books.SPEED_INACCURATE;
    }
}
