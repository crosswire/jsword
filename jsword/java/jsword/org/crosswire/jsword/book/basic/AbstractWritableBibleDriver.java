
package org.crosswire.jsword.book.basic;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.WritableBibleDriver;

/**
* The AbstractBibleDriver class implements some of the WritableBibleDriver
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
public abstract class AbstractWritableBibleDriver extends AbstractBibleDriver implements WritableBibleDriver
{
    /**
    * Directory renaming is not implemented yet because it is very easy
    * to do manually, and because I am not 100% sure that we should not
    * have the dirver do it for us.
    * @param old_name The current name for the version
    * @param new_name The name we would like the driver to have
    */
    public void renameBible(String old_name, String new_name) throws BookException
    {
        throw new BookException("book_noren", new Object[] { old_name });
    }

    /**
    * Directory deletion is not implemented yet because it is very easy
    * to do manually, and because I am not 100% sure that we should not
    * have the dirver do it for us.
    * @param name The name of the version to delete
    */
    public void deleteBible(String name) throws BookException
    {
        throw new BookException("book_nodel", new Object[] { name });
    }
}