
package org.crosswire.jsword.book.search;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.Properties;

import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Openness;
import org.crosswire.jsword.book.basic.AbstractBibleMetaData;

/**
 * This class does not currently add anything to the inheritance hierachy,
 * however it seems more sensible to leave it in to simply things.
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
public abstract class SearchableBibleMetaData extends AbstractBibleMetaData
{
    /**
     * Constructor for SearchableBibleMetaData.
     */
    public SearchableBibleMetaData(BookDriver driver, Properties prop) throws MalformedURLException, ParseException
    {
        super(driver, prop);
    }

    /**
     * Constructor for SearchableBibleMetaData.
     */
    public SearchableBibleMetaData(BookDriver driver, String name, String edition, String initials, Date pub, Openness open, URL licence)
    {
        super(driver, name, edition, initials, pub, open, licence);
    }

    /**
     * Constructor for SearchableBibleMetaData.
     */
    public SearchableBibleMetaData(BookDriver driver, String name, String edition, String initials, String pubstr, String openstr, String licencestr) throws ParseException, MalformedURLException
    {
        super(driver, name, edition, initials, pubstr, openstr, licencestr);
    }

    /**
     * Constructor for SearchableBibleMetaData.
     * @param name
     */
    public SearchableBibleMetaData(BookDriver driver, String name)
    {
        super(driver, name);
    }

    /**
     * Pass the delete on to the Search index.
     */
    public void delete() throws BookException
    {
        SearchableBible bible = (SearchableBible) getBible();
        bible.searcher.delete();
    }
}
