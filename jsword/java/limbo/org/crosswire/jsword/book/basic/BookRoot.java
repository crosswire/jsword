package org.crosswire.jsword.book.basic;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.ResourceUtil;
import org.crosswire.jsword.util.Project;

/**
 * A simple method of finding a directory in which Books are stored.
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
public class BookRoot
{
    /**
     * System property to let people re-direct where the project directory is stored
     */
    private static final String PROP_HOMEDIR = "jsword.bible.dir"; //$NON-NLS-1$

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
            URL found = NetUtil.lengthenURL(new URL(NetUtil.PROTOCOL_FILE, null, sysprop), Project.DIR_VERSIONS);
            URL test = NetUtil.lengthenURL(found, Project.FILE_LOCATOR);

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
            URL found = ResourceUtil.getResource(Project.DIR_VERSIONS + File.separator + Project.FILE_LOCATOR);
            URL test = NetUtil.shortenURL(found, Project.FILE_LOCATOR);
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
        else
        {
            return NetUtil.lengthenURL(root, subdir);
        }
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(BookRoot.class);
}