package org.crosswire.jsword.book.readings;

import java.io.IOException;
import java.net.URL;

import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.ResourceUtil;
import org.crosswire.common.util.URLFilter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookType;
import org.crosswire.jsword.book.basic.AbstractBookDriver;

/**
 * A driver for the readings dictionary.
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
public class ReadingsBookDriver extends AbstractBookDriver
{
    /**
     * Setup the array of BookMetaDatas
     */
    public ReadingsBookDriver()
    {
        books = new Book[]
        {
            new ReadingsBook(this, Msg.TITLE.toString(), BookType.DICTIONARY),
        };
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookDriver#getBooks()
     */
    public Book[] getBooks()
    {
        return books;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookDriver#getDriverName()
     */
    public String getDriverName()
    {
        return "Readings"; //$NON-NLS-1$
    }

    /**
     * The meta data array
     */
    private Book[] books;

    /**
     * Get a list of the available readings sets
     */
    public static String[] getInstalledReadingsSets()
    {
        try
        {
            URL index = ResourceUtil.getResource(ReadingsBookDriver.class, "readings.txt"); //$NON-NLS-1$
            return NetUtil.listByIndexFile(index, new URLFilter()
            {
                public boolean accept(String name)
                {
                    return true;
                }
            });
        }
        catch (IOException ex)
        {
            return new String[0];
        }
    }

    /**
     * Accessor for the current readings set
     */
    public static String getReadingsSet()
    {
        if (set == null)
        {
            String[] readings = getInstalledReadingsSets();
            if (readings.length > 0)
            {
                set = readings[0];
            }
        }

        return set;
    }

    /**
     * Accessor for the current readings set
     */
    public static void setReadingsSet(String set)
    {
        ReadingsBookDriver.set = set;
    }

    /**
     * Resources subdir for readings sets
     */
    public static final String DIR_READINGS = "readings"; //$NON-NLS-1$

    /**
     * The current readings set
     */
    private static String set;
}
