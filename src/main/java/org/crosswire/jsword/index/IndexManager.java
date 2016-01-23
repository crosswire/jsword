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
package org.crosswire.jsword.index;

import java.net.URI;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;

/**
 * Manages the life-cycle of an Index.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public interface IndexManager {
    /**
     * Detects if index data has been stored and is valid for this Bible.
     * 
     * @param book the book
     * @return true if the book has a usable index
     */
    boolean isIndexed(Book book);

    /**
     * Create a new Searcher.
     * 
     * @param book the book
     * @return an index that can be searched.
     * @throws BookException 
     */
    Index getIndex(Book book) throws BookException;

    /**
     * Detect or checking whether this book needs reindexing.
     * It is safe methods, you can always call it whether the book
     * is already indexed or not.
     * This check for <br>
     * <pre>
     * - isIndexed(Book book)
     * - Is index valid, eg index version changed incompatibly (due to internal structure change or search engine update)
     * -
     * </pre>
     * 
     * @param book the Book
     * @return true if no index present or current index is of incompatible/older version
     */
    boolean needsReindexing(Book book);

    /**
     * Read from the given source version to generate ourselves. On completion
     * of this method the index should be usable.
     * 
     * @param book The book that should be indexed
     */
    void scheduleIndexCreation(Book book);

    /**
     * We have downloaded a search index to a zip file. It should be installed
     * from here.
     * 
     * @param book
     *            The book that we downloaded an index for
     * @param tempDest
     *            The URI of a zip file to install
     * @throws BookException 
     */
    void installDownloadedIndex(Book book, URI tempDest) throws BookException;

    /**
     * Tidy up after yourself and remove all the files that make up any indexes
     * you created.
     * 
     * @param book the book who's index should be deleted.
     * @throws BookException 
     */
    void deleteIndex(Book book) throws BookException;

    /**
     * Close all indexes associated with this Index Manager
     */
    void closeAllIndexes();

    /**
     * Obtain the current IndexPolicy. Defaults to IndexPolicyAdapter.
     * 
     * @return the current IndexPolicy
     */
    IndexPolicy getIndexPolicy();

    /**
     * Set the desired IndexPolicy. Setting to null will cause the
     * IndexPolicyAdapter to be used.
     * 
     * @param policy the IndexPolicy to use when creating indexes.
     */
    void setIndexPolicy(IndexPolicy policy);
}
