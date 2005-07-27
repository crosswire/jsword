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
package org.crosswire.jsword.book.install;

import java.net.URL;
import java.util.List;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookList;

/**
 * An interface that allows us to download from a specific source of Bible data.
 * It is important that implementor of this interface define equals() and
 * hashcode() properly.
 * 
 * <p>To start with I only envisage that we use Sword sourced Bible data
 * however the rest of the system is designed to be able to use data from
 * e-Sword, OLB, etc.</p>
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public interface Installer extends BookList
{
    /**
     * Accessor for the URL
     * @return the source url
     */
    String getURL();

    /**
     * @param book The book meta-data to get a URL from.
     * @return the remote url for the BookMetaData
     */
    URL toRemoteURL(Book book);

    /**
     * Get a list of BookMetaData objects that represent downloadable books.
     * If no list has been retrieved from the remote source using reloadIndex()
     * then we should just return an empty list and not attempt to contact the
     * remote source. See notes on reload for more information.
     * @see Installer#reloadBookList()
     */
    List getBooks();

    /**
     * Return true if the book is not installed or there is a newer
     * version to install.
     * @param book The book meta-data to check on.
     * @return whether there is a newer version to install
     */
    boolean isNewer(Book book);

    /**
     * Refetch a list of names from the remote source.
     * <b>It would make sense if the user was warned about the implications
     * of this action. If the user lives in a country that persecutes
     * Christians then this action might give the game away.</b>
     */
    void reloadBookList() throws InstallException;

    /**
     * Download and install a book locally.
     * The name should be one from an index list retrieved from getIndex() or
     * reloadIndex()
     * @param book The book to install
     */
    void install(Book book) throws InstallException;

    /**
     * Download a search index for the given Book.
     * The installation of the search index is the responsibility of the
     * IndexManager.
     * @param book The book to download a search index for.
     * @param tempDest A temporary URL for downloading to. Passed to the
     * IndexManager for installation.
     */
    void downloadSearchIndex(Book book, URL tempDest) throws InstallException;
}
