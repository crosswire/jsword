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
package org.crosswire.jsword.book.remote;

import java.util.HashMap;
import java.util.Map;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.basic.AbstractBookDriver;
import org.jdom.Document;

/**
 * This represents all of the RemoteBooks.
 * 
 * LATER(joe): consider caching the data fetched.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public abstract class RemoteBookDriver extends AbstractBookDriver
{
    /**
     * Test the connection
     * @throws RemoterException if the ping fails
     */
    protected void ping() throws RemoterException
    {
        Remoter remoter = getRemoter();

        RemoteMethod method = new RemoteMethod(MethodName.GETBIBLES);
        remoter.execute(method);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookDriver#getDriverName()
     */
    public String getDriverName()
    {
        return getRemoter().getRemoterName();
    }

    /**
     * Accessor for the current remoter.
     * @return The remoter or null if none is available.
     */
    protected abstract Remoter getRemoter();

    /**
     * Get a list of the Books available from the name.
     * We cache the reply, for speed but we probably ought to have some way to
     * flush the cache because the list of Bibles on the server could change.
     * @return an array of book names
     */
    public Book[] getBooks()
    {
        synchronized (this)
        {
            if (rbmd == null)
            {
                try
                {
                    Remoter remoter = getRemoter();

                    RemoteMethod method = new RemoteMethod(MethodName.GETBIBLES);
                    Document doc = remoter.execute(method);

                    rbmd = Converter.convertDocumentToBooks(this, doc, remoter);
                }
                catch (Exception ex)
                {
                    log.warn("failed to remote getBibleNames", ex); //$NON-NLS-1$
                    rbmd = new Book[0];
                }
            }
        }

        return rbmd;
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(RemoteBookDriver.class);

    /**
     * The cache of Bible names.
     * At some stage it would be good to work out a way to clear the cache.
     */
    private Book[] rbmd;

    /**
     * The id to metadata map
     */
    private Map ids = new HashMap();

    /**
     * 
     */
    public void registerID(String id, BookMetaData bmd)
    {
        ids.put(bmd, id);
    }

    /**
     * 
     */
    public String getID(BookMetaData bmd)
    {
        return (String) ids.get(bmd);
    }
}
