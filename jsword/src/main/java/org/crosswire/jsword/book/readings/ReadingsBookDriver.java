/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.book.readings;

import java.io.IOException;
import java.net.URL;

import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.ResourceUtil;
import org.crosswire.common.util.URLFilter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.basic.AbstractBookDriver;

/**
 * A driver for the readings dictionary.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
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
            new ReadingsBook(this, BookCategory.DAILY_DEVOTIONS),
        };
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookDriver#getBooks()
     */
    public Book[] getBooks()
    {
        Book[] copy = new Book[books.length];
        System.arraycopy(books, 0, copy, 0, books.length);
        return copy;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookDriver#getDriverName()
     */
    public String getDriverName()
    {
        return "Readings"; //$NON-NLS-1$
    }

    /**
     * Get the singleton instance of this driver.
     * @return this driver instance
     */
    public static BookDriver instance()
    {
        return INSTANCE;
    }

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
     * The meta data array
     */
    private Book[] books;

    /**
     * Resources subdir for readings sets
     */
    public static final String DIR_READINGS = "readings"; //$NON-NLS-1$

    /**
     * A shared instance of this driver.
     */
    private static final BookDriver INSTANCE = new ReadingsBookDriver();

    /**
     * The current readings set
     */
    private static String set;
}
