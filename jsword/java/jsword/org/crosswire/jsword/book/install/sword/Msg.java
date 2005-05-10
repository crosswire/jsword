/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $ID$
 */
package org.crosswire.jsword.book.install.sword;

import org.crosswire.common.util.MsgBase;

/**
 * Compile safe Msg resource settings.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
class Msg extends MsgBase
{
    static final Msg UNKNOWN_ERROR = new Msg("SwordInstaller.UnknownError"); //$NON-NLS-1$
    static final Msg CACHE_ERROR = new Msg("SwordInstaller.CacheError"); //$NON-NLS-1$
    static final Msg INVALID_URL = new Msg("SwordInstaller.InvalidURL"); //$NON-NLS-1$
    static final Msg INSTALLING = new Msg("SwordInstaller.Installing"); //$NON-NLS-1$
    static final Msg JOB_INIT = new Msg("SwordInstaller.JobInit"); //$NON-NLS-1$
    static final Msg JOB_CONFIG = new Msg("SwordInstaller.JobConfig"); //$NON-NLS-1$
    static final Msg JOB_DOWNLOADING = new Msg("SwordInstaller.JobDownloading"); //$NON-NLS-1$
    static final Msg URL_FAILED = new Msg("SwordInstallerFactory.URLFailed"); //$NON-NLS-1$
    static final Msg MISSING_FILE = new Msg("HttpSwordInstaller.MissingFile"); //$NON-NLS-1$

    /**
     * Passthrough ctor
     */
    private Msg(String name)
    {
        super(name);
    }
}
