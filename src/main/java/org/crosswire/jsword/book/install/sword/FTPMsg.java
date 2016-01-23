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
package org.crosswire.jsword.book.install.sword;

import org.crosswire.common.util.MsgBase;

/**
 * Compile safe Msg resource settings.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
final class FTPMsg extends MsgBase {
    static final MsgBase AUTH_REFUSED = new FTPMsg("SwordInstaller.AuthRefused");
    static final MsgBase CONNECT_REFUSED = new FTPMsg("SwordInstaller.ConnectRefused");
    static final MsgBase CWD_REFUSED = new FTPMsg("SwordInstaller.CWDRefused");
    static final MsgBase DOWNLOAD_REFUSED = new FTPMsg("SwordInstaller.DownloadRefused");
    static final MsgBase URL_AT_COUNT = new FTPMsg("SwordInstallerFactory.URLAtCount");
    static final MsgBase URL_COLON_COUNT = new FTPMsg("SwordInstallerFactory.URLColonCount");

    /**
     * Passthrough ctor
     */
    private FTPMsg(String name) {
        super();
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    private String name;
}
