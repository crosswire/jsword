package org.crosswire.jsword.book;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * A BookMetaData represents a method of translating the Bible. All Books with
 * the same BookMetaData should return identical text for any call to
 * <code>Bible.getText(VerseRange)</code>. The implication of this is that
 * there may be many instances of the Version "NIV", as there are several
 * different versions of the NIV - Original American-English, Anglicized,
 * and Inclusive Language editions at least.
 *
 * <p>BookMetaData like Strings must be compared using <code>.equals()<code>
 * instead of ==. A Bible must have the ability to handle a book unknown to
 * JSword. So Books must be able to add versions to the system, and the system
 * must cope with books that already exist.</p>
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
public interface BookMetaData extends Comparable
{
    /**
     * The name of the book, for example "King James Version" or
     * "Bible in Basic English" or "Greek".
     * In general it should be possible to deduce the initials from the name by
     * removing all the non-capital letters. Although this is only a generalization.
     * This method should not return null or a blank string.
     * @return The name of this book
     */
    public String getName();

    /**
     * What type of content is this, a Bible or a reference work like a
     * Dictionary or Commentary
     * @return The type of book
     */
    public BookType getType();

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
     * The edition of this book, for example "Anglicised" (NIV),
     * "Stephanus" (Greek).
     * For 2 books to be equal both the name and the edition must be equal.
     * In general the text returned by this method should not include the word
     * "Edition". It is valid for an edition to be a blank string but not for it
     * to be null.
     * @return The name of the edition
     */
    public String getEdition();

    /**
     * The initials of this book - how people familiar with this book will know
     * it, for example "NIV", "KJV".
     * @return The book's initials
     */
    public String getInitials();

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
     * This does not need to be accurate and 2 books can be considered equal
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
     * Is this book sold for commercial profit like the NIV, or kept
     * open like the NET book.
     * @return A STATUS_* constant
     */
    public Openness getOpenness();

    /**
     * Not sure about this one - Do we need a way of getting at the dist.
     * licence? Are we going to be able to tie it down to a single book
     * policy like this? A null return is valid if the licence URL is not
     * known.
     * @return String detailing the users right to distribute this book
     */
    public URL getLicence();

    /**
     * Calculated field: Get an OSIS identifier for the OsisText.setOsisIDWork()
     * and the Work.setOsisWork() methods.
     * The response will generally be of the form [Bible][Dict..].getInitials
     * @return The osis id of this book
     */
    public String getOsisID();

    /**
     * Calculated field: The full name including edition of the book, for example
     * "New International Version, Anglicised (Ser)".
     * The format is "name, edition (Driver)"
     * @return The full name of this book
     */
    public String getFullName();

    /**
     * Calculated method: Do the 2 books have matching names.
     * @param book The book to compare to
     * @return true if the names match
     */
    public boolean isSameFamily(BookMetaData book);

    /**
     * Calculated field: The name of the name, which could be helpful to
     * distinguish similar Books available through 2 BookDrivers.
     * @return The name name
     */
    public String getDriverName();

    /**
     * Get a list of all the properties available to do with this Book.
     * The returned Properties will be read-only so any attempts to alter it
     * will fail.
     * This method is designed to support finding out more about a book
     * rather than as a covert method of
     */
    public Map getProperties();

    /**
     * The SPEED_* constants specify how fast a Book implementation is.
     * 
     * Important values include 5, were the remoting system will not remote
     * Books where getSpeed() >= 5 (to save re-remoting already remote Books).
     * 10 is also special - values > 10 indicate the data returned is likely to
     * be wrong (i.e. test data) So we should probably not ship systems with
     * BibleDrivers that return > 10.
     */
    public static final int SPEED_FASTEST = 10;

    /**
     * @see BookMetaData#SPEED_FASTEST
     */
    public static final int SPEED_FAST = 9;

    /**
     * @see BookMetaData#SPEED_FASTEST
     */
    public static final int SPEED_MEDIUM = 8;

    /**
     * @see BookMetaData#SPEED_FASTEST
     */
    public static final int SPEED_SLOW = 7;

    /**
     * @see BookMetaData#SPEED_FASTEST
     */
    public static final int SPEED_SLOWEST = 6;

    /**
     * @see BookMetaData#SPEED_FASTEST
     */
    public static final int SPEED_REMOTE_FASTEST = 5;

    /**
     * @see BookMetaData#SPEED_FASTEST
     */
    public static final int SPEED_REMOTE_FAST = 4;

    /**
     * @see BookMetaData#SPEED_FASTEST
     */
    public static final int SPEED_REMOTE_MEDIUM = 3;

    /**
     * @see BookMetaData#SPEED_FASTEST
     */
    public static final int SPEED_REMOTE_SLOW = 2;

    /**
     * @see BookMetaData#SPEED_FASTEST
     */
    public static final int SPEED_REMOTE_SLOWEST = 1;

    /**
     * @see BookMetaData#SPEED_FASTEST
     */
    public static final int SPEED_IGNORE = 0;

    /**
     * @see BookMetaData#SPEED_FASTEST
     */
    public static final int SPEED_INACCURATE = -1;

    /**
     * The key for the type in the properties map
     */
    public static final String KEY_TYPE = "Key"; //$NON-NLS-1$

    /**
     * The key for the book in the properties map
     */
    public static final String KEY_BOOK = "Book"; //$NON-NLS-1$

    /**
     * The key for the driver in the properties map
     */
    public static final String KEY_DRIVER = "Driver"; //$NON-NLS-1$

    /**
     * The key for the name in the properties map
     */
    public static final String KEY_NAME = "Name"; //$NON-NLS-1$

    /**
     * The key for the edition in the properties map
     */
    public static final String KEY_EDITION = "Edition"; //$NON-NLS-1$

    /**
     * The key for the initials in the properties map
     */
    public static final String KEY_INITIALS = "Initials"; //$NON-NLS-1$

    /**
     * The key for the speed in the properties map
     */
    public static final String KEY_SPEED = "Speed"; //$NON-NLS-1$

    /**
     * The key for the first pub in the properties map
     */
    public static final String KEY_FIRSTPUB = "FirstPublished"; //$NON-NLS-1$

    /**
     * The key for the openness in the properties map
     */
    public static final String KEY_OPENNESS = "Openness"; //$NON-NLS-1$

    /**
     * The key for the licence in the properties map
     */
    public static final String KEY_LICENCE = "Licence"; //$NON-NLS-1$

    /**
     * The default creation date.
     * Using new Date(0) is the same as FIRSTPUB_FORMAT.parse("1970-01-01")
     * but does not throw
     */
    public static final Date FIRSTPUB_DEFAULT = new Date(0L);

    /**
     * The default way for format published dates when converting to and from
     * strings
     */
    public static final DateFormat FIRSTPUB_FORMAT = new SimpleDateFormat("yyyy-MM-dd"); //$NON-NLS-1$
}
