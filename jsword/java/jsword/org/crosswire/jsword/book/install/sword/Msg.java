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
    static final Msg AUTH_REFUSED = new Msg("SwordInstaller.AuthRefused"); //$NON-NLS-1$
    static final Msg CONNECT_REFUSED = new Msg("SwordInstaller.ConnectRefused"); //$NON-NLS-1$
    static final Msg CWD_REFUSED = new Msg("SwordInstaller.CWDRefused"); //$NON-NLS-1$
    static final Msg DOWNLOAD_REFUSED = new Msg("SwordInstaller.DownloadRefused"); //$NON-NLS-1$
    static final Msg UNKNOWN_ERROR = new Msg("SwordInstaller.UnknownError"); //$NON-NLS-1$
    static final Msg CACHE_ERROR = new Msg("SwordInstaller.CacheError"); //$NON-NLS-1$
    static final Msg INVALID_URL = new Msg("SwordInstaller.InvalidURL"); //$NON-NLS-1$
    static final Msg INSTALLED = new Msg("SwordInstaller.Installed"); //$NON-NLS-1$
    static final Msg INSTALLING = new Msg("SwordInstaller.Installing"); //$NON-NLS-1$
    static final Msg INSTALL_DONE = new Msg("SwordInstaller.InstallDone"); //$NON-NLS-1$
    static final Msg JOB_INIT = new Msg("SwordInstaller.JobInit"); //$NON-NLS-1$
    static final Msg JOB_CONFIG = new Msg("SwordInstaller.JobConfig"); //$NON-NLS-1$
    static final Msg JOB_LOGIN = new Msg("SwordInstaller.JobLogin"); //$NON-NLS-1$
    static final Msg JOB_DOWNLOADING = new Msg("SwordInstaller.JobDownloading"); //$NON-NLS-1$
    static final Msg URL_FAILED = new Msg("SwordInstallerFactory.URLFailed"); //$NON-NLS-1$
    static final Msg URL_AT_COUNT = new Msg("SwordInstallerFactory.URLAtCount"); //$NON-NLS-1$
    static final Msg URL_COLON_COUNT = new Msg("SwordInstallerFactory.URLColonCount"); //$NON-NLS-1$
    static final Msg MISSING_FILE = new Msg("HttpSwordInstaller.MissingFile"); //$NON-NLS-1$

    /**
     * Passthrough ctor
     */
    private Msg(String name)
    {
        super(name);
    }
}
