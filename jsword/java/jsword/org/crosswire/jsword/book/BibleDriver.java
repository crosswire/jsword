
package org.crosswire.jsword.book;

/**
 * The BibleDriver class is an gateway to all the instances of the Books
 * controlled by this driver.
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
public interface BibleDriver extends BookDriver
{
    /**
     * A simple driver description name. This should be callable before
     * init() is called, so that we can find the friendly name of a
     * Bible without having to fully initialize it.
     * @return A short identifing string
     */
    public String getDriverName();

    /**
     * Get a list of the Books available from the driver
     * @return an array of book names
     */
    public String[] getBibleNames();

    /**
     * How many Bibles does this driver own
     * @return A count of the Bibles
     */
    public int countBibles();

    /**
     * Does the named Bible exist?
     * @param name The name of the version to test for
     * @return true if the Bible exists
     */
    public boolean exists(String name);

    /**
     * Fetch a currently existing Bible, read-only
     * @param name The name of the version to create
     * @exception BookException If the name is not valid
     */
    public Bible getBible(String name) throws BookException;
}
