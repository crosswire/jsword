/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.jsword.book.readings;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.ResourceUtil;
import org.crosswire.common.util.URIFilter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.basic.AbstractBookDriver;

/**
 * A driver for the readings dictionary.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class ReadingsBookDriver extends AbstractBookDriver {
    /**
     * Setup the array of BookMetaDatas
     */
    public ReadingsBookDriver() {
        List<Book> bookList = new ArrayList<Book>();
        String[] installedBooks = getInstalledReadingsSets();
        for (int i = 0; i < installedBooks.length; i++) {
            bookList.add(new ReadingsBook(this, installedBooks[i], BookCategory.DAILY_DEVOTIONS));
        }

        books = bookList.toArray(new Book[bookList.size()]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.BookDriver#getBooks()
     */
    public Book[] getBooks() {
        return books == null ? null : (Book[]) books.clone();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.BookDriver#getDriverName()
     */
    public String getDriverName() {
        return "Readings";
    }

    /**
     * Get the singleton instance of this driver.
     * 
     * @return this driver instance
     */
    public static BookDriver instance() {
        return INSTANCE;
    }

    /**
     * Get a list of the available readings sets
     *
     * @return all the installed reading sets
     */
    public String[] getInstalledReadingsSets() {
        try {
            URL index = ResourceUtil.getResource(ReadingsBookDriver.class, "readings.txt");
            return NetUtil.listByIndexFile(NetUtil.toURI(index), new ReadingsFilter());
        } catch (IOException ex) {
            return new String[0];
        }
    }

    /**
     * Get all files mentioned by readings.txt
     */
    static final class ReadingsFilter implements URIFilter {
        public boolean accept(String name) {
            return true;
        }
    }

    /**
     * The meta data array
     */
    private Book[] books;

    /**
     * Resources subdir for readings sets
     */
    public static final String DIR_READINGS = "readings";

    /**
     * A shared instance of this driver.
     */
    private static final BookDriver INSTANCE = new ReadingsBookDriver();
}
