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
 * ID: $Id$
 */
package org.crosswire.jsword.book.install.sword;

import org.crosswire.common.util.NetUtil;
import org.crosswire.jsword.book.install.Installer;
import org.crosswire.jsword.book.install.InstallerFactory;
import org.crosswire.jsword.book.install.sword.FTPMsg;

/**
 * A Factory for instances of FtpSwordInstaller.
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class FtpSwordInstallerFactory implements InstallerFactory
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.InstallerFactory#createInstaller()
     */
    public Installer createInstaller()
    {
        return new FtpSwordInstaller();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.InstallerFactory#createInstaller(java.lang.String)
     */
    public Installer createInstaller(String url)
    {
        String[] parts = url.split(NetUtil.SEPARATOR, 4);
        if (parts.length < 4)
        {
            throw new IllegalArgumentException(Msg.INVALID_URL.toString(url));
        }

        FtpSwordInstaller reply = new FtpSwordInstaller();
        // part[0] is the 'protocol' which we don't care about
        // part[1] is the blank between the first 2 slashes
        String part2 = parts[2];
        if (part2.indexOf(NetUtil.AUTH_SEPERATOR_USERNAME) >= 0)
        {
            String[] chop2 = part2.split(NetUtil.AUTH_SEPERATOR_USERNAME);
            if (chop2.length != 2)
            {
                throw new IllegalArgumentException(FTPMsg.URL_AT_COUNT.toString(url));
            }

            String[] chop3 = chop2[0].split(NetUtil.AUTH_SEPERATOR_PASSWORD);
            if (chop3.length != 2)
            {
                throw new IllegalArgumentException(FTPMsg.URL_COLON_COUNT.toString(url));
            }

            reply.setUsername(chop3[0]);
            reply.setPassword(chop3[1]);
            reply.setHost(chop2[1]);
        }
        else
        {
            reply.setHost(part2);
        }
        reply.setDirectory(NetUtil.SEPARATOR + parts[3]);

        return reply;
    }
}
