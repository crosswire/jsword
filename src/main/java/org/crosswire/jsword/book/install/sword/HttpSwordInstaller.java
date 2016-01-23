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

import java.net.URI;
import java.net.URISyntaxException;

import org.crosswire.common.progress.Progress;
import org.crosswire.common.util.LucidException;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.WebResource;
import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.install.InstallException;

/**
 * An implementation of Installer for reading data from Sword Web sites.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Mark Goodwin
 * @author Joe Walker
 * @author DM Smith
 */
public class HttpSwordInstaller extends AbstractSwordInstaller {

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.Installer#getType()
     */
    public String getType() {
        return "sword-http";
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.Installer#getSize(org.crosswire.jsword.book.Book)
     */
    public int getSize(final Book book) {
        return NetUtil.getSize(toRemoteURI(book), proxyHost, proxyPort);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.Installer#toRemoteURI(org.crosswire.jsword.book.Book)
     */
    public URI toRemoteURI(final Book book) {
        try {
            return new URI(NetUtil.PROTOCOL_HTTP, host, packageDirectory + '/' + book.getInitials() + ZIP_SUFFIX, null);
        } catch (URISyntaxException e) {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.sword.AbstractSwordInstaller#download(org.crosswire.common.progress.Progress, java.lang.String, java.lang.String, java.net.URI)
     */
    @Override
    protected void download(Progress job, String dir, String file, URI dest) throws InstallException {
        URI uri;
        try {
            uri = new URI(NetUtil.PROTOCOL_HTTP, host, dir + '/' + file, null);
        } catch (URISyntaxException ex) {
            // TRANSLATOR: Common error condition: {0} is a placeholder for the URL of what could not be found.
            throw new InstallException(JSMsg.gettext("Unable to find: {0}", dir + '/' + file), ex);
        }

        try {
            copy(job, uri, dest);
        } catch (LucidException ex) {
            // TRANSLATOR: Common error condition: {0} is a placeholder for the URL of what could not be found.
            throw new InstallException(JSMsg.gettext("Unable to find: {0}", uri.toString()), ex);
        }
    }

    /**
     * @param job
     * @param uri
     * @param dest
     * @throws LucidException
     */
    private void copy(Progress job, URI uri, URI dest) throws LucidException {
        if (job != null) {
            // TRANSLATOR: Progress label for downloading one or more files.
            job.setSectionName(JSMsg.gettext("Downloading files"));
        }

        WebResource wr = new WebResource(uri, proxyHost, proxyPort);
        wr.copy(dest, job);
        wr.shutdown();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.sword.AbstractSwordInstaller#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof HttpSwordInstaller)) {
            return false;
        }
        HttpSwordInstaller that = (HttpSwordInstaller) object;

        if (!super.equals(that)) {
            return false;
        }

        return true;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.sword.AbstractSwordInstaller#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
