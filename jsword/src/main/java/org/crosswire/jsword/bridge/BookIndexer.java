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
 * Copyright: 2008
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: BookIndexer.java 1466 2007-07-02 02:48:09Z dmsmith $
 */
package org.crosswire.jsword.bridge;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.index.IndexManager;
import org.crosswire.jsword.index.IndexManagerFactory;
import org.crosswire.jsword.index.IndexStatusEvent;
import org.crosswire.jsword.index.IndexStatusListener;

/**
 * BookIndexer allows one to check the status of an index, build an index or delete an index.
 * This is similar to SWORD's mkfastmod.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class BookIndexer
{

    public BookIndexer(Book book)
    {
        this.book = book;
        done = true; // not busy
        indexManager = IndexManagerFactory.getIndexManager();
        isl = new StatusListener(this);
    }

    public boolean isIndexed()
    {
        // If we are busy then the index is being created
        // or it is being deleted. So for all practical purposes
        // it is not indexed.
        return done && indexManager.isIndexed(book);
    }

    public void deleteIndex() throws BookException
    {
        if (done)
        {
            done = false;
            book.addIndexStatusListener(isl);
            indexManager.deleteIndex(book);
            while (!done)
            {
                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException e)
                {
                    // ok to be interrupted
                }
            }
            book.removeIndexStatusListener(isl);
        }
    }

    public void createIndex() throws BookException
    {
        if (done)
        {
            done = false;
            book.addIndexStatusListener(isl);
            if (isIndexed())
            {
                deleteIndex();
            }
            indexManager.scheduleIndexCreation(book);
            while (!done)
            {
                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException e)
                {
                    // ok to be interrupted
                }
            }
            book.removeIndexStatusListener(isl);
        }
    }

    protected void setDone(boolean state)
    {
        done = state;
    }

    private Book book;
    private IndexManager indexManager;
    private IndexStatusListener isl;
    private boolean done;

    /**
     * Listen for the end of indexing.
     */
    public static final class StatusListener implements IndexStatusListener
    {
        public StatusListener(BookIndexer indexer)
        {
            this.indexer = indexer;
        }

        public void statusChanged(IndexStatusEvent ev)
        {
            indexer.setDone(true);
        }

        private BookIndexer indexer;
    }

    /**
     * Call with &lt;operation&gt; book.
     * Where operation can be one of:
     * <ul>
     * <li>check - returns "TRUE" or "FALSE" indicating whether the index exists or not</li>
     * <li>create - (re)create the index</li>
     * <li>delete - delete the index if it exists</li>
     * </ul>
     * And book is the initials of a book, e.g. KJV.
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        if (args.length != 2)
        {
            usage();
            return;
        }

        System.err.println("BookIndexer " + args[0] + " " + args[1]); //$NON-NLS-1$ //$NON-NLS-2$

        String operation = args[0];
        Book b = Books.installed().getBook(args[1]);
        if (b == null)
        {
            System.err.println("Book not found"); //$NON-NLS-1$
            return;
        }

        BookIndexer indexer = new BookIndexer(b);
        if (operation.equalsIgnoreCase("create")) //$NON-NLS-1$
        {
            try
            {
                indexer.createIndex();
            }
            catch (BookException e)
            {
                System.err.println("Unable to re-index book."); //$NON-NLS-1$
                e.printStackTrace();
            }
        }
        else if (operation.equalsIgnoreCase("delete")) //$NON-NLS-1$
        {
            try
            {
                indexer.deleteIndex();
            }
            catch (BookException e)
            {
                System.err.println("Unable to delete index for book."); //$NON-NLS-1$
                e.printStackTrace();
            }
        }
        else if (operation.equalsIgnoreCase("check")) //$NON-NLS-1$
        {
            System.err.println(indexer.isIndexed());
        }
        else
        {
            usage();
        }
    }

    public static void usage()
    {
        System.err.println("Usage: BookIndexer operation book"); //$NON-NLS-1$
    }
}
