
package org.crosswire.jsword.book.basic;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.jsword.book.BibleDriver;
import org.crosswire.jsword.util.Project;

/**
* The AbstractBibleDriver class implements some of the BibleDriver
* methods, that various BibleDrivers may do in the same way.
*
* <table border='1' cellPadding='3' cellSpacing='0' width="100%">
* <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
* Distribution Licence:<br />
* Project B is free software; you can redistribute it
* and/or modify it under the terms of the GNU General Public License,
* version 2 as published by the Free Software Foundation.<br />
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* General Public License for more details.<br />
* The License is available on the internet
* <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
* <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
* MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
* The copyright to this program is held by it's authors.
* </font></td></tr></table>
* @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
* @see <{docs.Licence}>
* @author Joe Walker
* @version $Id$
*/
public abstract class AbstractBibleDriver implements BibleDriver
{
    /**
    * How many Bibles does this driver control?
    * @return A count of the Bibles
    */
    public int countBibles()
    {
        return getBibleNames().length;
    }

    /**
     * Search for versions directories
     */
    protected URL findBibleRoot() throws MalformedURLException
    {
        URL found;

        log.debug("Looking for Bibles:");

        // First see if there is a System property that can help us out
        String sysprop = System.getProperty("jsword.bible.dir");
        if (sysprop != null)
        {
            found = NetUtil.lengthenURL(new URL("file", null, sysprop), "versions");
            URL test = NetUtil.lengthenURL(found, "locator.properties");
            if (NetUtil.isFile(test))
            {
                log.debug("-- Found from system property jsword.bible.dir at "+sysprop+"");
                return found;
            }

            log.debug("-- Not found from system property jsword.bible.dir at "+sysprop+"");
        }
        else
        {
            log.debug("-- Unset system property jsword.bible.dir");
        }

        String locator = "/versions/locator.properties";
        found = Project.resource().getResource(locator);
        if (found == null)
            throw new MalformedURLException("Missing locator.properties.");

        return NetUtil.shortenURL(found, "/locator.properties");
    }

    /** The log stream */
    protected static Logger log = Logger.getLogger(AbstractBibleDriver.class);
}
