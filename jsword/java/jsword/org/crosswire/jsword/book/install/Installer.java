package org.crosswire.jsword.book.install;

import java.net.URL;
import java.util.List;

import org.crosswire.jsword.book.BookList;
import org.crosswire.jsword.book.BookMetaData;

/**
 * An interface that allows us to download from a specific source of Bible data.
 * It is important that implementor of this interface define equals() and
 * hashcode() properly.
 * 
 * <p>To start with I only envisage that we use Sword sourced Bible data
 * however the rest of the system is designed to be able to use data from
 * e-Sword, OLB, etc.</p>
 *
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public interface Installer extends BookList
{
    /**
     * Accessor for the URL
     * @return the source url
     */
    public String getURL();

    /**
     * Get a list of BookMetaData objects that represent downloadable modules.
     * If no list has been retrieved from the remote source using reloadIndex()
     * then we should just return an empty list and not attempt to contact the
     * remote source. See notes on reload for more information.
     * @see Installer#reloadBookList()
     */
    public List getBookMetaDatas();

    /**
     * Refetch a list of names from the remote source.
     * <b>It would make sense if the user was warned about the implications
     * of this action. If the user lives in a country that persecutes
     * Christians then this action might give the game away.</b>
     */
    public void reloadBookList() throws InstallException;

    /**
     * Download and install a module locally.
     * The name should be one from an index list retrieved from getIndex() or
     * reloadIndex()
     * @param bmd The module to install
     */
    public void install(BookMetaData bmd) throws InstallException;

    /**
     * Download a search index for the given Book.
     * The installation of the search index is the responsibility of the
     * IndexManager.
     * @param bmd The book to download a search index for.
     * @param tempDest A temporary URL for downloading to. Passed to the
     * IndexManager for installation.
     */
    public void downloadSearchIndex(BookMetaData bmd, URL tempDest) throws InstallException;

    /**
     * @param bmd The book meta-data to get a URL from.
     * @return the remote url for the BookMetaData
     */
    public URL toRemoteURL(BookMetaData bmd);

    /**
     * @param bmd The book meta-data to get a URL from.
     * @return the url for the directory for BookMetaData
     */
    public URL toLocalURL(BookMetaData bmd);

    /**
     * Return true if the module is not installed or there is a newer
     * version to install.
     * @param bmd The book meta-data to check on.
     * @return whether there is a newer version to install
     */
    public boolean isNewer(BookMetaData bmd);
}
