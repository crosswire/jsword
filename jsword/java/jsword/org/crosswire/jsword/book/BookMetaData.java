
package org.crosswire.jsword.book;

import java.net.URL;
import java.util.Date;

/**
 * A BookMetaData represents a method of translating the Bible. All Books with
 * the same BookMetaData should return identical text for any call to
 * <code>Bible.getText(VerseRange)</code>. The implication of this is that
 * there may be many instances of the Version "NIV", as there are several
 * different versions of the NIV - Original American-English, Anglicized,
 * and Inclusive Language editions at least.
 *
 * <p>BookMetaData like Strings must be compared using <code>.equals()<code>
 * instead of ==. A Bible must have the ability to handle a version
 * unknown to JSword. So Books must be able to add versions to the
 * system, and the system must cope with versions that already exist.</p>
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
public interface BookMetaData
{
    /**
     * Accessor for the real Book to read data.
     * <p>Note that constructing a Book may well consume system resources far
     * more than the construction of a BookMetaData so you should only get a
     * Book if you intend to use it.
     * <p>For implementors of BookMetaData - the objects returned by 2
     * successive calls to getBook() should be the same (i.e. return true to an
     * == test) unless for some reason the objects are not thread safe. Since
     * Books are read-only once setup thread safety should not be hard.
     */
    public Book getBook();

    /**
     * Accessor for the driver that runs this Book.
     * Note this method should only be used to delete() Books. Everything else
     * you should want to do to a Book should be available in other ways.
     */
    public BookDriver getDriver();

    /**
     * The name of the version, for example "King James Version" or
     * "Bible in Basic English" or "Greek".
     * In general it should be possible to deduce the initials from the name by
     * removing all the non-capital letters. Although this is only a generalization.
     * This method should not return null or a blank string.
     * @return The name of this version
     */
    public String getName();

    /**
     * The edition of this version, for example "Anglicised" (NIV),
     * "Stephanus" (Greek).
     * For 2 versions to be equal both the name and the edition must be equal.
     * In general the text returned by this method should not include the word
     * "Edition". It is valid for an edition to be a blank string but not for it
     * to be null.
     * @return The name of the edition
     */
    public String getEdition();

    /**
     * The full name including edition of the version, for example
     * "New International Version, Anglicised (Ser)". The format is "name, edition (Driver)"
     * @return The full name of this version
     */
    public String getFullName();

    /**
     * Do the 2 versions have matching names.
     * @param version The version to compare to
     * @return true if the names match
     */
    public boolean isSameFamily(BookMetaData version);

    /**
     * The initials of this book - how people familiar with this book will know
     * it, for example "NIV", "KJV".
     * @return The versions initials
     */
    public String getInitials();

    /**
     * The name of the name, which could be helpful to distinguish similar
     * Books available through 2 BookDrivers.
     * @return The name name
     */
    public String getDriverName();

    /**
     * The expected speed at which this implementation gets correct answers.
     * This value is used by Books to decide the fastest implementation for a
     * given job.
     * <p>The valid values are defined in the Books class.
     * @see Books
     * @return a speed value between -1 and 10
     */
    public int getSpeed();

    /**
     * The date of first publishing.
     * This does not need to be accurate and 2 versions can be considered equal
     * even if they have different first publishing dates for that reason.
     * In general "1 Jan 1970" means published in 1970, and so on.
     * <b>A null return from this method is entirely valid</b> if the date of
     * first publishing is not known.
     * If the date is required in string form it should be in the format
     * YYYY-MM-DD so save US/UK confusion over MM/DD and DD/MM.
     * @return The date of first publishing
     */
    public Date getFirstPublished();

    /**
     * Is this version sold for commercial profit like the NIV, or kept
     * open like the NET version.
     * @return A STATUS_* constant
     */
    public Openness getOpenness();

    /**
     * Not sure about this one - Do we need a way of getting at the dist.
     * licence? Are we going to be able to tie it down to a single Version
     * policy like this? A null return is valid if the licence URL is not
     * known.
     * @return String detailing the users right to distribute this version
     */
    public URL getLicence();
}
