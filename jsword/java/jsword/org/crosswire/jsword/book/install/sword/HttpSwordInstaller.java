package org.crosswire.jsword.book.install.sword;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.crosswire.common.progress.Job;
import org.crosswire.common.util.IOUtil;
import org.crosswire.common.util.NetUtil;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.install.InstallException;
import org.crosswire.jsword.book.sword.SwordBookMetaData;

/**
 * An implementation of Installer for reading data from Sword Web sites.
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
 * @author Mark Goodwin [goodwinster at gmail dot com]
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class HttpSwordInstaller extends AbstractSwordInstaller implements Comparable
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.Installer#getURL()
     */
    public String getURL()
    {
        return PROTOCOL_WEB + ':' + host + directory;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.Installer#toURL(org.crosswire.jsword.book.BookMetaData)
     */
    public URL toRemoteURL(BookMetaData bmd)
    {
        if (!(bmd instanceof SwordBookMetaData))
        {
            assert false;
            return null;
        }

        SwordBookMetaData sbmd = (SwordBookMetaData) bmd;

        try
        {
            return new URL(NetUtil.PROTOCOL_HTTP, host, directory + '/' + PACKAGE_DIR + '/' + sbmd.getInitials() + ZIP_SUFFIX);
        }
        catch (MalformedURLException ex)
        {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.sword.AbstractSwordInstaller#download(java.lang.String, java.lang.String, java.net.URL)
     */
    protected void download(Job job, String dir, String file, URL dest) throws InstallException
    {
        try
        {
            URL url = new URL(NetUtil.PROTOCOL_HTTP, host, dir + '/' + file); //$NON-NLS-1$
            copy(job, url, dest);
        }
        catch (IOException ex)
        {
            throw new InstallException(Msg.UNKNOWN_ERROR, ex);
        }
    }

    /**
     * @param job
     * @param url
     * @param dest
     * @throws IOException
     */
    private void copy(Job job, URL url, URL dest) throws IOException
    {
        InputStream in = null;
        OutputStream out = null;

        try
        {
            if (job != null)
            {
                job.setProgress(Msg.JOB_DOWNLOADING.toString());
            }

            // Download the index file
            out = NetUtil.getOutputStream(dest);

            URLConnection urlConnection = url.openConnection();
            in = urlConnection.getInputStream();

            byte[] buf = new byte[4096];
            for (int count = 0; -1 != (count = in.read(buf)); )
            {
                out.write(buf, 0, count);
            }
        }
        finally
        {
            IOUtil.close(in);
            IOUtil.close(out);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object object)
    {
        if (!(object instanceof HttpSwordInstaller))
        {
            return false;
        }
        HttpSwordInstaller that = (HttpSwordInstaller) object;

        if (!super.equals(that))
        {
            return false;
        }

        return true;
    }

    /**
     * We need to be ablee to provide a URL as part of the API
     */
    private static final String PROTOCOL_WEB = "web"; //$NON-NLS-1$
}
