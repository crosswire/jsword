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
package org.crosswire.jsword.book;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit Test. For when we don't actually want to do testing of responses
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class BookParentTst {
    protected BookMetaData[] bmds;
    protected Book[] bibles;

    @Before
    public void setUp() throws Exception {
        List<Book> lbmds = Books.installed().getBooks(BookFilters.getOnlyBibles());
        bibles = new Book[lbmds.size()];
        bmds = new BookMetaData[lbmds.size()];

        int i = 0;
        for (Book book : lbmds) {
            bmds[i] = book.getBookMetaData();
            i++;
        }
    }

    @Test
    public void testNothing() {
        Assert.assertTrue(true);
    }
}
