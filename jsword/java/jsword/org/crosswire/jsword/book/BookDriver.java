
package org.crosswire.jsword.book;


/**
 * The BibleDriver class allows creation of new Books.
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
public interface BookDriver
{
    /**
     * This method should only be used by Bibles at startup to register the
     * Bibles known at start time.
     * Generally there will be a better way of doing whatever you want to do if
     * you use this method.
     * @return A list of the known Bibles
     */
    public BookMetaData[] getBooks();

    /**
     * Is this name capable of creating writing data in the correct format
     * as well as reading it?
     * @return true/false to indicate ability to write data
     */
    public boolean isWritable();

    /**
     * Create a new Book based on a source.
     * @param name The name of the version to create
     * @param li Somewhere to repost progress (can be null)
     * @return The new WritableBible
     * @exception BookException If the name is not valid
     */
    public Book create(Book source, ProgressListener li) throws BookException;
    
    /**
     * Delete this Book from the system.
     * Take care with this method for obvious reasons. For most implemenations
     * of Book etc, this method will throw up because most will be read-only.
     * @throws BookException If the Book can't be deleted.
     */
    public void delete(BookMetaData dead) throws BookException;
}
