
package org.crosswire.jsword.book.basic;

import org.apache.log4j.Logger;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BibleDriver;
import org.crosswire.jsword.book.BibleMetaData;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.events.ProgressListener;

/**
 * The AbstractBibleDriver class implements some of the BibleDriver
 * methods, that various BibleDrivers may do in the same way.
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
public abstract class AbstractBibleDriver implements BibleDriver
{
    /**
     * @see org.crosswire.jsword.book.BookDriver#getBooks()
     */
    public BookMetaData[] getBooks()
    {
        return getBibles();
    }

    /**
     * Method getBibleMetaDataFromName.
     * @param name
     * @return String
     */
    private BibleMetaData getBibleMetaDataFromName(String name)
    {
        BibleMetaData[] bbmds = getBibles();
        for (int i=0; i<bbmds.length; i++)
        {
            if (bbmds[i].getName().equals(name))
            {
                return bbmds[i];
            }
        }

        return null;
    }

    /**
     * Get a list of the Books available from the driver
     * @return an array of book names
     */
    public String[] getBibleNames()
    {
        BibleMetaData[] bmds = getBibles();
        String[] names = new String[bmds.length];

        for (int i=0; i<bmds.length; i++)
        {
            names[i] = bmds[i].getName();
        }
        
        return names;
    }

    /**
     * How many Bibles does this driver control?
     * @return A count of the Bibles
     */
    public int countBibles()
    {
        return getBibles().length;
    }

    /**
     * Is this driver capable of creating writing data in the correct format
     * as well as reading it?
     * @return true/false to indicate ability to write data
     */
    public boolean isWritable()
    {
        return false;
    }

    /**
     * Create a new Bible read from the source
     * @param name The name of the version to create
     * @exception BookException If the name is not valid
     */
    public Bible create(Bible source, ProgressListener li) throws BookException
    {
        throw new BookException("bible_driver_readonly");
    }

    /**
     * @see org.crosswire.jsword.book.BookDriver#create(org.crosswire.jsword.book.Book, org.crosswire.jsword.book.events.ProgressListener)
     */
    public Book create(Book source, ProgressListener li) throws BookException
    {
        if (!(source instanceof Bible))
            throw new BookException("bible_invalid_source");

        return create((Bible) source, li);
    }

    /** The log stream */
    protected static Logger log = Logger.getLogger(AbstractBibleDriver.class);
}
