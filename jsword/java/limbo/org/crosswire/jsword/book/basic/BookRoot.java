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
package org.crosswire.jsword.book.basic;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.ResourceUtil;

/**
 * A simple method of finding a directory in which Books are stored.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class BookRoot
{
    /**
     * Search for versions directories
     */
    public static URL findBibleRoot(String subdir) throws MalformedURLException
    {
        URL root = null;

        // First see if there is a System property that can help us out
        String sysprop = System.getProperty(PROP_HOMEDIR);
        log.debug("Testing system property " + PROP_HOMEDIR + "=" + sysprop); //$NON-NLS-1$ //$NON-NLS-2$

        if (sysprop != null)
        {
            URL found = NetUtil.lengthenURL(new URL(NetUtil.PROTOCOL_FILE, null, sysprop), DIR_VERSIONS);
            URL test = NetUtil.lengthenURL(found, FILE_LOCATOR);

            if (NetUtil.isFile(test))
            {
                log.debug("Found BibleRoot using system property " + PROP_HOMEDIR + " at " + test); //$NON-NLS-1$ //$NON-NLS-2$
                root = found;
            }
            else
            {
                log.warn("Missing " + PROP_HOMEDIR + " under: " + test.toExternalForm()); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

        // If not then try a wild guess
        if (root == null)
        {
            URL found = ResourceUtil.getResource(DIR_VERSIONS + File.separator + FILE_LOCATOR);
            URL test = NetUtil.shortenURL(found, FILE_LOCATOR);
            if (NetUtil.isFile(test))
            {
                log.debug("Found BibleRoot from current directory: " + test.toExternalForm()); //$NON-NLS-1$
                root = test;
            }
            else
            {
                log.warn("Missing BibleRoot from current directory: " + test.toExternalForm()); //$NON-NLS-1$
            }
        }

        if (root == null)
        {
            return null;
        }
        return NetUtil.lengthenURL(root, subdir);
    }

    /**
     * System property to let people re-direct where the project directory is stored
     */
    private static final String PROP_HOMEDIR = "jsword.bible.dir"; //$NON-NLS-1$

    /**
     * A file so we know if we have the right versions directory
     */
    public static final String FILE_LOCATOR = "locator.properties"; //$NON-NLS-1$

    /**
     * Versions subdirectory of the project directory
     */
    public static final String DIR_VERSIONS = "versions"; //$NON-NLS-1$

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(BookRoot.class);
}