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
package org.crosswire.jsword.book.stub;

import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.basic.AbstractPassageBook;
import org.crosswire.jsword.book.basic.DefaultBookMetaData;
import org.crosswire.jsword.book.filter.Filter;
import org.crosswire.jsword.book.filter.FilterFactory;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.Verse;

/**
 * StubBook is a simple stub implementation of Book that is pretty much
 * always going to work because it has no dependancies on external files.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class StubBook extends AbstractPassageBook
{
    /**
     * Basic constructor for a StubBook
     */
    public StubBook(StubBookDriver driver, String name, BookCategory type)
    {
        BookMetaData bmd = new DefaultBookMetaData(driver, name, type);
        setBookMetaData(bmd);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractPassageBook#getFilter()
     */
    protected Filter getFilter()
    {
        return FilterFactory.getDefaultFilter();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractPassageBook#getText(org.crosswire.jsword.passage.Verse)
     */
    protected String getText(Key key)
    {
        return "stub implementation"; //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractPassageBook#setText(org.crosswire.jsword.passage.Verse, java.lang.String)
     */
    protected void setText(Verse verse, String text) throws BookException
    {
        throw new BookException(Msg.DRIVER_READONLY);
    }
}
