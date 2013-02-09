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
package org.crosswire.jsword.index;

import java.net.URI;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;

/**
 * Manages the life-cycle of an Index.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public interface IndexManager {
    /**
     * Detects if index data has been stored for this Bible already
     */
    boolean isIndexed(Book book);

    /**
     * Create a new Searcher.
     */
    Index getIndex(Book book) throws BookException;

    /**
     * Read from the given source version to generate ourselves. On completion
     * of this method the index should be usable.
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
     */
    void installDownloadedIndex(Book book, URI tempDest) throws BookException;

    /**
     * Tidy up after yourself and remove all the files that make up any indexes
     * you created.
     */
    void deleteIndex(Book book) throws BookException;

    /**
     * Close all indexes associated with this Index Manager
     */
    void closeAllIndexes();
}
