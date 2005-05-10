/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
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
package org.crosswire.jsword.book;

/**
 * The BibleDriver class allows creation of new Books.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
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
    public Book[] getBooks();

    /**
     * Is this name capable of creating writing data in the correct format
     * as well as reading it?
     * @return true/false to indicate ability to write data
     */
    public boolean isWritable();

    /**
     * Create a new Book based on a source.
     * @param source The Book from which to copy data
     * @return The new WritableBible
     * @exception BookException If the name is not valid
     */
    public Book create(Book source) throws BookException;

    /**
     * Is this book able to be deleted.
     * @param dead the book to be deleted
     * @return whether the book can be deleted.
     */
    public boolean isDeletable(Book dead);

    /**
     * Delete this Book from the system.
     * Take care with this method for obvious reasons. For most implemenations
     * of Book etc, this method will throw up because most will be read-only.
     * @throws BookException If the Book can't be deleted.
     */
    public void delete(Book dead) throws BookException;

    /**
     * A short name for this BookDriver
     */
    public String getDriverName();
}
