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
 * © CrossWire Bible Society, 2008 - 2016
 *
 */
package org.crosswire.jsword.bridge;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.index.IndexManager;
import org.crosswire.jsword.index.IndexManagerFactory;
import org.crosswire.jsword.index.IndexStatus;
import org.crosswire.jsword.index.IndexStatusEvent;
import org.crosswire.jsword.index.IndexStatusListener;

/**
 * BookIndexer allows one to check the status of an index, build an index or
 * delete an index. This is similar to SWORD's mkfastmod.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class BookIndexer {

    public BookIndexer(Book book) {
        this.book = book;
        done = true; // not busy
        indexManager = IndexManagerFactory.getIndexManager();
        isl = new StatusListener(this);
    }

    public boolean isIndexed() {
        // If we are busy then the index is being created
        // or it is being deleted. So for all practical purposes
        // it is not indexed.
        return done && indexManager.isIndexed(book);
    }

    public void deleteIndex() throws BookException {
        if (done) {
            done = false;
            book.addIndexStatusListener(isl);
            indexManager.deleteIndex(book);
            while (!done) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // This is allowed
                }
            }
            book.removeIndexStatusListener(isl);
        }
    }

    public void createIndex() throws BookException {
        if (done) {
            done = false;
            book.addIndexStatusListener(isl);
            if (isIndexed()) {
                deleteIndex();
            }
            Thread work = new Thread(new Runnable() {
                public void run() {
                    indexManager.scheduleIndexCreation(book);
                }
            });
            work.start();
            while (!done) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // This is allowed
                }
            }
            book.removeIndexStatusListener(isl);
        }
    }

    protected void setDone(boolean state) {
        done = state;
    }





    /* //todo static function: Demo of how clients can reindex, after a upgrade on a user computer. If reindexAll successful, update Installed.Index.DefaultVersion prop on the client computer

    //note: Need separate method for downloadLatestIndexForAllBooksIfNeeded()
    public static void reindexAllBooksIfNeeded() throws Exception {

        Books myBooks = Books.installed();

        for(Book insBook: myBooks.getBooks()) {
            //reindex if needsReindexing(insBook) true
            if(indexManager.needsReindexing(insBook)) {
                createIndex(insBook);
                //reindex & update Books Installed.Index.Version
                InstalledIndex.instance().storeLatestVersionAsInstalledIndexMetadata(insBook);
            }
            //manage all Installed.Index.Version property values in  metadata file
        }
        //set Installed.Index.DefaultVersion={Latest Version}
        PropertyMap map = new PropertyMap();
        map.put(InstalledIndex.INSTALLED_INDEX_DEFAULT_VERSION , IndexMetadata.instance().getLatestIndexVersionStr());
        InstalledIndex.instance().storeInstalledIndexMetadata(map);


    }
    */



    protected Book book;
    protected IndexManager indexManager;
    private IndexStatusListener isl;
    private boolean done;

    /**
     * Listen for the end of indexing.
     */
    public static final class StatusListener implements IndexStatusListener {
        public StatusListener(BookIndexer indexer) {
            this.indexer = indexer;
        }

        public void statusChanged(IndexStatusEvent ev) {
            IndexStatus newStatus = ev.getIndexStatus();
            if (IndexStatus.DONE.equals(newStatus) || IndexStatus.UNDONE.equals(newStatus) || IndexStatus.INVALID.equals(newStatus)) {
                indexer.setDone(true);
            }
        }

        private BookIndexer indexer;
    }

    /**
     * Call with &lt;operation&gt; book. Where operation can be one of:
     * <ul>
     * <li>check - returns "TRUE" or "FALSE" indicating whether the index exists
     * or not</li>
     * <li>create - (re)create the index</li>
     * <li>delete - delete the index if it exists</li>
     * </ul>
     * And book is the initials of a book, e.g. KJV.
     * 
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            usage();
            return;
        }

        System.err.println("BookIndexer " + args[0] + " " + args[1]);

        String operation = args[0];
        Book b = Books.installed().getBook(args[1]);
        if (b == null) {
            System.err.println("Book not found");
            return;
        }

        BookIndexer indexer = new BookIndexer(b);
        if ("create".equalsIgnoreCase(operation)) {
            try {
                indexer.createIndex();
            } catch (BookException e) {
                System.err.println("Unable to re-index book.");
                e.printStackTrace();
            }
        } else if ("delete".equalsIgnoreCase(operation)) {
            try {
                indexer.deleteIndex();
            } catch (BookException e) {
                System.err.println("Unable to delete index for book.");
                e.printStackTrace();
            }
        } else if ("check".equalsIgnoreCase(operation)) {
            System.err.println(indexer.isIndexed());
        } else {
            usage();
        }
    }

    public static void usage() {
        System.err.println("Usage: BookIndexer operation book");
    }
}
