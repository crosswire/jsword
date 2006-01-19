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
package org.crosswire.jsword.book;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

/**
 * JUnit Test.
 * For when we don't actually want to do testing of responses
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class BookParentTst extends TestCase
{
    public BookParentTst(String s)
    {
        super(s);
    }

    protected BookMetaData[] bmds = null;
    protected Book[] bibles = null;

    protected void setUp() throws Exception
    {
        List lbmds = Books.installed().getBooks(BookFilters.getBibles());
        bibles = new Book[lbmds.size()];
        bmds = new BookMetaData[lbmds.size()];

        int i = 0;
        for (Iterator it = lbmds.iterator(); it.hasNext();)
        {
            bibles[i] = (Book) it.next();
            bmds[i] = bibles[i].getBookMetaData();
            i++;
        }
    }

    protected void tearDown() throws Exception
    {
    }

    public void testNothing()
    {
        assertTrue(true);
    }
}
