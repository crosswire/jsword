
package org.crosswire.jsword.book.stub;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.Properties;

import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.Bibles;
import org.crosswire.jsword.book.BookException;
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
 * @version $Id: Bible.java,v 1.2 2002/10/08 21:36:07 joe Exp $
 */
public class StubBibleMetaData extends AbstractBibleMetaData
{

    /**
     * Constructor for StubBibleMetaData.
     */
    public StubBibleMetaData(Properties prop) throws MalformedURLException, ParseException
    {
        super(prop);
    }

    /**
     * Constructor for StubBibleMetaData.
     */
    public StubBibleMetaData(String name, String edition, String initials, Date pub, Openness open, URL licence)
    {
        super(name, edition, initials, pub, open, licence);
    }

    /**
     * Constructor for StubBibleMetaData.
     */
    public StubBibleMetaData(String name, String edition, String initials, String pubstr, String openstr, String licencestr) throws ParseException, MalformedURLException
    {
        super(name, edition, initials, pubstr, openstr, licencestr);
    }

    /**
     * Constructor for StubBibleMetaData.
     */
    public StubBibleMetaData(String name)
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
        // DCL
        // I know double checked locking is theoretically broken however it isn't
        // practically broken 99% of the time, and even if the 1% comes up here
        // the only effect is some temporary wasted memory
        if (bible == null)
        {
            synchronized(this)
            {
                if (bible == null)
                    bible = new StubBible(this);
            }
        }

        return bible;
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
        return Bibles.SPEED_INACCURATE;
    }

    /**
     * The cached bible so we don't have to create too many
     */
    private Bible bible = null;
}
