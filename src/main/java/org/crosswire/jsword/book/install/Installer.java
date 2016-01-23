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
package org.crosswire.jsword.book.install;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookList;

import java.net.URI;
import java.util.List;

/**
 * An interface that allows us to download from a specific source of Bible data.
 * It is important that implementor of this interface define equals() and
 * hashCode() properly.
 * 
 * <p>
 * To start with I only envisage that we use Sword sourced Bible data however
 * the rest of the system is designed to be able to use data from e-Sword, OLB,
 * etc.
 * </p>
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
 */
public interface Installer extends BookList {
    /**
     * Get the type of the Installer.
     * 
     * @return the type of the installer
     */
    String getType();

    /**
     * Accessor for the URI
     * 
     * @return the source URI
     */
    String getInstallerDefinition();

    /**
     * @param book
     *            The book meta-data to get a URI from.
     * @return the remote URI for the BookMetaData
     */
    URI toRemoteURI(final Book book);

    /**
     * Get a list of BookMetaData objects that represent downloadable books. If
     * no list has been retrieved from the remote source using reloadIndex()
     * then we should just return an empty list and not attempt to contact the
     * remote source. See notes on reload for more information.
     * 
     * @return the list of books
     * @see Installer#reloadBookList()
     */
    List<Book> getBooks();

    /**
     * Get a Book matching the name from the local cache. Null if none is found.
     * 
     * @param book the book name
     * @return the instantiated book
     */
    Book getBook(final String book);

    /**
     * Return true if the book is not installed or there is a newer version to
     * install.
     * 
     * @param book
     *            The book meta-data to check on.
     * @return whether there is a newer version to install
     */
    int getSize(final Book book);

    /**
     * Return true if the book is not installed or there is a newer version to
     * install.
     * 
     * @param book
     *            The book meta-data to check on.
     * @return whether there is a newer version to install
     */
    boolean isNewer(final Book book);

    /**
     * Re-fetch a list of names from the remote source. <b>It would make sense
     * if the user was warned about the implications of this action. If the user
     * lives in a country that persecutes Christians then this action might give
     * the game away.</b>
     * 
     * @throws InstallException 
     */
    void reloadBookList() throws InstallException;

    /**
     * Download and install a book locally. The name should be one from an index
     * list retrieved from getIndex() or reloadIndex()
     * 
     *
     *
     * @param book
     *            The book to install
     * @throws InstallException 
     */
    void install(final Book book) throws InstallException;

    /**
     * Download a search index for the given Book. The installation of the
     * search index is the responsibility of the BookIndexer.
     * 
     * @param book
     *            The book to download a search index for.
     * @param tempDest
     *            A temporary URI for downloading to. Passed to the BookIndexer
     *            for installation.
     * @throws InstallException 
     */
    void downloadSearchIndex(final Book book, final URI tempDest) throws InstallException;

    /** remove the cached book list to clear memory
     */
    void close();
}
