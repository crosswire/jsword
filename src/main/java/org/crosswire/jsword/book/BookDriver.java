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
package org.crosswire.jsword.book;

/**
 * The BibleDriver class allows creation of new Books.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public interface BookDriver extends BookProvider {
    /**
     * Is this name capable of creating writing data in the correct format as
     * well as reading it?
     * 
     * @return true/false to indicate ability to write data
     */
    boolean isWritable();

    /**
     * Create a new Book based on a source.
     * 
     * @param source
     *            The Book from which to copy data
     * @return The new WritableBible
     * @exception BookException
     *                If the name is not valid
     */
    Book create(Book source) throws BookException;

    /**
     * Is this book able to be deleted.
     * 
     * @param dead
     *            the book to be deleted
     * @return whether the book can be deleted.
     */
    boolean isDeletable(Book dead);

    /**
     * Delete this Book from the system. Take care with this method for obvious
     * reasons. For most implementations of Book etc, this method will throw up
     * because most will be read-only.
     * 
     * @param dead
     *            the book to be deleted
     * @throws BookException
     *             If the Book can't be deleted.
     */
    void delete(Book dead) throws BookException;

    /**
     * A short name for this BookDriver
     * 
     * @return a short name for this BookDriver
     */
    String getDriverName();
}
