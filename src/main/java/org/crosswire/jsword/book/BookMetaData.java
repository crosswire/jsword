/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005-2013
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.book;

import java.net.URI;
import java.util.Map;

import org.crosswire.common.util.Language;
import org.crosswire.jsword.book.sword.MissingDataFilesException;
import org.crosswire.jsword.index.IndexStatus;
import org.jdom2.Document;

/**
 * A BookMetaData represents a method of translating the Bible. All Books with
 * the same BookMetaData should return identical text for any call to
 * <code>Bible.getText(VerseRange)</code>. The implication of this is that there
 * may be many instances of the Version "NIV", as there are several different
 * versions of the NIV - Original American-English, Anglicized, and Inclusive
 * Language editions at least.
 * 
 * <p>
 * BookMetaData like Strings must be compared using <code>.equals()<code>
 * instead of ==. A Bible must have the ability to handle a book unknown to
 * JSword. So Books must be able to add versions to the system, and the system
 * must cope with books that already exist.
 * </p>
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public interface BookMetaData extends Comparable<BookMetaData> {
    /**
     * The name of the book, for example "King James Version" or
     * "Bible in Basic English" or "Greek". In general it should be possible to
     * deduce the initials from the name by removing all the non-capital
     * letters. Although this is only a generalization. This method should not
     * return null or a blank string.
     * 
     * @return The name of this book
     */
    String getName();

    /**
     * How this Book organizes it's keys.
     * 
     * @return the organization of keys of this Book
     */
    KeyType getKeyType();

    /**
     * What category of content is this, a Bible or a reference work like a
     * Dictionary or Commentary.
     * 
     * @return The category of book
     */
    BookCategory getBookCategory();

    /**
     * Accessor for the driver that runs this Book. Note this method should only
     * be used to delete() Books. Everything else you should want to do to a
     * Book should be available in other ways.
     */
    BookDriver getDriver();

    /**
     * The language of the book.
     * 
     * @return the book's language
     */
    Language getLanguage();

    /**
     * The initials of this book - how people familiar with this book will know
     * it, for example "NIV", "KJV".
     * 
     * @return The book's initials
     */
    String getInitials();

    /**
     * Calculated field: Get an OSIS identifier for the OsisText.setOsisIDWork()
     * and the Work.setOsisWork() methods. The response will generally be of the
     * form [Bible][Dict..].getInitials
     * 
     * @return The osis id of this book
     */
    String getOsisID();

    /**
     * Indicate whether this book is supported by JSword. Since the expectation
     * is that all books are supported, abstract implementations should return
     * true and let specific implementations return false if they cannot support
     * the book.
     * 
     * @return true if the book is supported
     */
    boolean isSupported();

    /**
     * Indicate whether this book is enciphered. Since the expectation is that
     * most books are unenciphered, abstract implementations should return false
     * and let specific implementations return true otherwise.
     * 
     * @return true if the book is enciphered
     */
    boolean isEnciphered();

    /**
     * Indicate whether this book is enciphered and without a key. Since the
     * expectation is that most books are unenciphered, abstract implementations
     * should return false and let specific implementations return true
     * otherwise.
     * 
     * @return true if the book is locked
     */
    boolean isLocked();

    /**
     * Unlocks a book with the given key.
     * 
     * @param unlockKey
     *            the key to try
     * @return true if the unlock key worked.
     */
    boolean unlock(String unlockKey);

    /**
     * Gets the unlock key for the module.
     * 
     * @return the unlock key, if any, null otherwise.
     */
    String getUnlockKey();

    /**
     * Indicate whether this book is questionable. A book may be deemed
     * questionable if it's quality or content has not been confirmed. Since the
     * expectation is that all books are not questionable, abstract
     * implementations should return false and let specific implementations
     * return true if the book is questionable.
     * 
     * @return true if the book is questionable
     */
    boolean isQuestionable();

    /**
     * Calculated field: The name of the name, which could be helpful to
     * distinguish similar Books available through 2 BookDrivers.
     * 
     * @return The driver name
     */
    String getDriverName();

    /**
     * Return the orientation of the script of the Book. If a book contains more
     * than one script, it refers to the dominate script of the book. This will
     * be used to present Arabic and Hebrew in their proper orientation. Note:
     * some languages have multiple scripts which don't have the same
     * directionality.
     * 
     * @return true if the orientation for the dominate script is LeftToRight.
     */
    boolean isLeftToRight();

    /**
     * Return whether the feature is supported by the book.
     */
    boolean hasFeature(FeatureType feature);

    /**
     * Get the base URI for library of this module.
     * 
     * @return the base URI or null if there is none
     */
    URI getLibrary();

    /**
     * Set the base URI for library of this module.
     * 
     * @param library
     *            the base URI or null if there is none
     * @throws MissingDataFilesException  indicates missing data files
     */
    void setLibrary(URI library) throws MissingDataFilesException;

    /**
     * Get the base URI for relative URIs in the document.
     * 
     * @return the base URI or null if there is none
     */
    URI getLocation();

    /**
     * Set the base URI for relative URIs in the document.
     * 
     * @param library
     *            the base URI or null if there is none
     */
    void setLocation(URI library);

    /**
     * Get a list of all the properties available to do with this Book. The
     * returned Properties will be read-only so any attempts to alter it will
     * fail.
     */
    Map<String, Object> getProperties();

    /**
     * @param key
     *            the key of the property.
     * @return the value of the property
     */
    Object getProperty(String key);

    /**
     * @param key
     *            the key of the property to set
     * @param value
     *            the value of the property
     */
    void putProperty(String key, Object value);

    /**
     * Has anyone generated a search index for this Book?
     * 
     * @see org.crosswire.jsword.index.IndexManager
     */
    IndexStatus getIndexStatus();

    /**
     * This method does not alter the index status, however it is for Indexers
     * that are responsible for indexing and have changed the status themselves.
     * 
     * @see org.crosswire.jsword.index.IndexManager
     */
    void setIndexStatus(IndexStatus status);

    /**
     * Get an OSIS representation of information concerning this Book.
     */
    Document toOSIS();

    /**
     * The key for the type in the properties map
     */
    String KEY_CATEGORY = "Category";

    /**
     * The key for the book in the properties map
     */
    String KEY_BOOK = "Book";

    /**
     * The key for the driver in the properties map
     */
    String KEY_DRIVER = "Driver";

    /**
     * The key for the name in the properties map
     */
    String KEY_NAME = "Description";

    /**
     * The key for the language in the properties map
     */
    String KEY_XML_LANG = "Lang";

    /**
     * The key for the font in the properties map
     */
    String KEY_FONT = "Font";

    /**
     * The key for the initials in the properties map
     */
    String KEY_INITIALS = "Initials";

    /**
     * The key for the URI locating where this book is installed
     */
    String KEY_LIBRARY_URI = "LibraryURI";

    /**
     * The key for the URI locating this book
     */
    String KEY_LOCATION_URI = "LocationURI";

    /**
     * The key for the indexed status in the properties map
     */
    String KEY_INDEXSTATUS = "IndexStatus";

    /**
     * The key for the Versification property.
     */
    String KEY_VERSIFICATION = "Versification";
}
