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
package org.crosswire.jsword.book.install.sword;

import java.net.URI;
import java.net.URISyntaxException;

import org.crosswire.common.progress.Progress;
import org.crosswire.common.util.LucidException;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.WebResource;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.install.InstallException;

/**
 * An implementation of Installer for reading data from Sword Web sites.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Mark Goodwin [goodwinster at gmail dot com]
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class HttpSwordInstaller extends AbstractSwordInstaller
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.Installer#getType()
     */
    public String getType()
    {
        return "sword-http"; //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.Installer#getSize(org.crosswire.jsword.book.Book)
     */
    public int getSize(Book book)
    {
        return NetUtil.getSize(toRemoteURI(book), proxyHost, proxyPort);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.Installer#toRemoteURI(org.crosswire.jsword.book.Book)
     */
    public URI toRemoteURI(Book book)
    {
        try
        {
            return new URI(NetUtil.PROTOCOL_HTTP, host, packageDirectory + '/' + book.getInitials() + ZIP_SUFFIX, null);
        }
        catch (URISyntaxException e)
        {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.sword.AbstractSwordInstaller#download(java.lang.String, java.lang.String, java.net.URI)
     */
    /* @Override */
    protected void download(Progress job, String dir, String file, URI dest) throws InstallException
    {
        URI uri;
        try
        {
            uri = new URI(NetUtil.PROTOCOL_HTTP, host, dir + '/' + file, null);
        }
        catch (URISyntaxException e1)
        {
            throw new InstallException(UserMsg.MISSING_FILE, e1, new Object[] { dir + '/' + file });
        }

        try
        {
            copy(job, uri, dest);
        }
        catch (LucidException ex)
        {
            throw new InstallException(UserMsg.MISSING_FILE, ex, new Object[] { uri.toString() });
        }
    }

    /**
     * @param job
     * @param uri
     * @param dest
     * @throws LucidException
     */
    private void copy(Progress job, URI uri, URI dest) throws LucidException
    {
        if (job != null)
        {
            job.setSectionName(UserMsg.JOB_DOWNLOADING.toString());
        }

        WebResource wr = new WebResource(uri, proxyHost, proxyPort);
        wr.copy(dest);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    /* @Override */
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

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    /* @Override */
    public int hashCode()
    {
        return super.hashCode();
    }
}
