package org.crosswire.jsword.util;

import java.io.IOException;
import java.net.URL;

import org.crosswire.common.util.NetUtil;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.install.InstallException;
import org.crosswire.jsword.book.install.Installer;
import org.crosswire.jsword.book.search.IndexManager;
import org.crosswire.jsword.book.search.IndexManagerFactory;

/**
 * .
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
public class IndexDownloader
{
    /**
     * Prevent instansiation
     */
    private IndexDownloader()
    {
    }

    /**
     * Download and install a search index
     * @param bmd The book to get an index for
     */
    public static void downloadIndex(BookMetaData bmd, Installer installer) throws IOException, InstallException, BookException
    {
        // Get a temp home
        URL tempDownload = NetUtil.getTemporaryURL(TEMP_PREFIX, TEMP_SUFFIX);

        try
        {
            // Now we know what installer to use, download to the temp file
            installer.downloadSearchIndex(bmd, tempDownload);

            // And install from that file.
            IndexManager idxman = IndexManagerFactory.getIndexManager();
            idxman.installDownloadedIndex(bmd, tempDownload);
        }
        finally
        {
            // tidy up after ourselves
            if (tempDownload != null)
            {
                NetUtil.delete(tempDownload);
            }
        }
    }

    /**
     * Temp file prefix
     */
    private static final String TEMP_PREFIX = "jsword-index"; //$NON-NLS-1$

    /**
     * Temp file suffix
     */
    private static final String TEMP_SUFFIX = "dat"; //$NON-NLS-1$
}
