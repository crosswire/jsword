
package org.crosswire.jsword.book;

/**
 * The WritableBibleDriver is a specialization of BibleDriver that adds the
 * ability to record Bible data for later retrieval.
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public interface WritableBibleDriver extends BibleDriver
{
    /**
    * Create a new blank Bible ready for writing
    * @param name The name of the version to create
    * @return The new WritableBible
    * @exception BookException If the name is not valid
    */
    public WritableBible createBible(String name) throws BookException;

    /**
    * Rename a Book.
    * @param old_name The current name for the version
    * @param new_name The name we would like the driver to have
    * @exception BookException If the names are not valid
    */
    public void renameBible(String old_name, String new_name) throws BookException;

    /**
    * Delete  Book
    * @param name The name of the version to delete
    * @exception BookException If the name is not valid
    */
    public void deleteBible(String name) throws BookException;
}
