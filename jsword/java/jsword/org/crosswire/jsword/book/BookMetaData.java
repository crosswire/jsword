package org.crosswire.jsword.book;

import java.beans.PropertyChangeListener;
import java.util.Map;

import org.jdom.Document;

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
     * Accessor for the driver that runs this Book.
     * Note this method should only be used to delete() Books. Everything else
     * you should want to do to a Book should be available in other ways.
     */
    public BookDriver getDriver();

    /**
     * The language of the book is the common name for the iso639 code.
     * @return the common name for the language
     */
    public String getLanguage();

    /**
     * The initials of this book - how people familiar with this book will know
     * it, for example "NIV", "KJV".
     * @return The book's initials
     */
    public String getInitials();

    /**
     * Calculated field: Get an OSIS identifier for the OsisText.setOsisIDWork()
     * and the Work.setOsisWork() methods.
     * The response will generally be of the form [Bible][Dict..].getInitials
     * @return The osis id of this book
     */
    public String getOsisID();

    /**
     * Calculated field: The full name of the book, for example
     * The format is "name, (Driver)"
     * @return The full name of this book
     */
    public String getFullName();

    /**
     * Calculated field: The name of the name, which could be helpful to
     * distinguish similar Books available through 2 BookDrivers.
     * @return The driver name
     */
    public String getDriverName();

    /**
     * Return the orientation of the language of the Book. If a book contains more than one language,
     * it refers to the dominate language of the book. This will be used to present
     * Arabic and Hebrew in their propper orientation.
     * @return true if the orientation for the dominate language is LeftToRight.
     */
    public boolean isLeftToRight();

    /**
     * Return whether the feature is supported by the book.
     */
    public boolean hasFeature(FeatureType feature);

    /**
     * Get a list of all the properties available to do with this Book.
     * The returned Properties will be read-only so any attempts to alter it
     * will fail.
     */
    public Map getProperties();

    /**
     * Has anyone generated a search index for this Book?
     * @see org.crosswire.jsword.book.search.IndexManager
     */
    public IndexStatus getIndexStatus();

    /**
     * This method does not alter the index status, however it is for Indexers
     * that are responsible for indexing and have changed the status themselves.
     * @see org.crosswire.jsword.book.search.IndexManager
     */
    public void setIndexStatus(IndexStatus status);

    /**
     * Get an OSIS representation of information concerning this Book.
     */
    public Document toOSIS();

    /**
     * Adds a <code>PropertyChangeListener</code> to the listener list.
     * The listener is registered for all properties. However the only one likely
     * to change at the time of writing is the Index Status.
     * <p>A <code>PropertyChangeEvent</code> will get fired in response
     * to setting a bound property, such as <code>setIndexStatus</code>.
     * @param li the <code>PropertyChangeListener</code> to be added
     */
    public void addPropertyChangeListener(PropertyChangeListener li);

    /**
     * Removes a <code>PropertyChangeListener</code> from the listener list.
     * @param li the <code>PropertyChangeListener</code> to be removed
     */
    public void removePropertyChangeListener(PropertyChangeListener li);

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
    public static final String KEY_NAME = "Description"; //$NON-NLS-1$

    /**
     * The key for the name in the properties map
     */
    public static final String KEY_LANGUAGE = "Language"; //$NON-NLS-1$

    /**
     * The key for the initials in the properties map
     */
    public static final String KEY_INITIALS = "Initials"; //$NON-NLS-1$

    /**
     * The key for the indexed status in the properties map
     */
    public static final String KEY_INDEXSTATUS = "IndexStatus"; //$NON-NLS-1$
}
