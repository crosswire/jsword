package org.crosswire.jsword.book.install.sword;

import org.crosswire.common.util.MsgBase;

/**
 * Compile safe Msg resource settings.
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
class Msg extends MsgBase
{
    static final Msg AUTH_REFUSED = new Msg("Login failed: User={0}. FTP code={1}, {2}"); //$NON-NLS-1$
    static final Msg CONNECT_REFUSED = new Msg("Failed to connect to remote server: {0}. FTP code={1}, {2}"); //$NON-NLS-1$
    static final Msg CWD_REFUSED = new Msg("Failed to change to remote directory: {0}. FTP code={1}, {2}"); //$NON-NLS-1$
    static final Msg DOWNLOAD_REFUSED = new Msg("Failed to download index file: {0}. FTP code={1}, {2}"); //$NON-NLS-1$
    static final Msg UNKNOWN_ERROR = new Msg("Unexpected Error occured"); //$NON-NLS-1$
    static final Msg CACHE_ERROR = new Msg("Error loading from cache"); //$NON-NLS-1$
    static final Msg INVALID_URL = new Msg("Not enough / symbols in url: {0}"); //$NON-NLS-1$
    static final Msg URL_FAILED = new Msg("URL manipulation failed"); //$NON-NLS-1$
    static final Msg URL_AT_COUNT = new Msg("Too many @ symbols in url: {0}"); //$NON-NLS-1$
    static final Msg URL_COLON_COUNT = new Msg("Wrong number of : symbols in url: {0}"); //$NON-NLS-1$
    static final Msg INSTALLED = new Msg("Module already installed: {0}"); //$NON-NLS-1$
    static final Msg INSTALLING = new Msg("Installing module: {0}"); //$NON-NLS-1$
    static final Msg INSTALL_DONE = new Msg("Finished installing module: {0}"); //$NON-NLS-1$
    static final Msg JOB_INIT = new Msg("Initializing"); //$NON-NLS-1$
    static final Msg JOB_CONFIG = new Msg("Copying config file"); //$NON-NLS-1$
    static final Msg JOB_LOGIN = new Msg("Logging on to remote site"); //$NON-NLS-1$
    static final Msg JOB_DOWNLOADING = new Msg("Downloading files"); //$NON-NLS-1$

    /**
     * Passthrough ctor
     */
    private Msg(String name)
    {
        super(name);
    }
}
