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

import java.util.Iterator;
import java.util.Set;

import org.crosswire.common.activate.Activatable;
import org.crosswire.common.util.Language;
import org.crosswire.jsword.index.IndexStatus;
import org.crosswire.jsword.index.IndexStatusListener;
import org.crosswire.jsword.index.search.SearchRequest;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.jdom2.Content;
import org.jdom2.Document;

/**
 * Book is the most basic store of textual data - It can retrieve data either as
 * an XML document or as plain text - It uses Keys to refer to parts of itself,
 * and can search for words (returning Keys).
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public interface Book extends Activatable, Comparable<Book> {
    /**
     * Get a complete list of index entries. Create a Key that encompasses all
     * of the known valid keys for the given context. For a dictionary this will
     * include all of the entries in the dictionary, for a Bible this will
     * probably include all the verses in the Bible, but a commentary may well
     * miss some out.
     * 
     * @return A Key that includes all of the known Keys
     */
    Key getGlobalKeyList();

    /**
     * Get a complete list of entries. Create a Key that encompasses all
     * of the existing entries in the book. For most modules this will be the
     * same as {@link #getGlobalKeyList}, however for a Bible, it will
     * get the references that are actually in the book.
     * 
     * @return A Key that includes all of the existing Keys
     */
    Key getScope();

    /**
     * Get a Key for the name, if possible. Otherwise return an empty Key.
     * 
     * @param name
     *            The string to translate into a Key
     * @return a valid key.
     */
    Key getValidKey(String name);

    /**
     * Someone has typed in a reference to find, but we need a Key to actually
     * look it up. So we create a Key from the string if such a translation is
     * possible. The returned Key may be a BranchKey if the string represents
     * more than one Key.
     * 
     * @param name
     *            The string to translate into a Key
     * @return The Key corresponding to the input text
     * @throws NoSuchKeyException
     *             If the name can not be parsed.
     */
    Key getKey(String name) throws NoSuchKeyException;

    /**
     * Fetch an empty Key to which we can add Keys. Not all implementations of
     * Key are able to hold any type of Key, It isn't reasonable to expect a Key
     * of Bible verses (=Passage) to hold a dictionary Key. So each KeyFactory
     * must be able to create you an empty Key to which you can safely add other
     * Keys it generates.
     * 
     * @return An empty Key that can hold other Keys from this factory.
     */
    Key createEmptyKeyList();

    /**
     * Meta-Information: What version of the Bible is this?
     * 
     * @return A Version for this Bible
     */
    BookMetaData getBookMetaData();

    /**
     * Set the meta-information for this book.
     * 
     * @param bmd the BookMetaData that describes this book.
     */
    void setBookMetaData(BookMetaData bmd);

    /**
     * Return an iterator that returns each key's OSIS in turn.
     * 
     * @param key
     *            the Items to locate
     * @param allowEmpty
     *            indicates whether empty keys should be present.
     * @param allowGenTitles
     *            indicates whether to generate titles
     * @return an iterator over the OSIS Content
     * @throws BookException
     *             If anything goes wrong with this method
     */
    Iterator<Content> getOsisIterator(Key key, boolean allowEmpty, boolean allowGenTitles) throws BookException;

    /**
     * Returns <tt>true</tt> if this book contains the specified element.
     * 
     * @param key
     *            element whose presence in this book is to be tested.
     * @return <tt>true</tt> if this book contains the specified element.
     */
    boolean contains(Key key);

    /**
     * Returns the raw text that getData(Key key) builds into OSIS.
     * 
     * @param key
     *            The item to locate
     * @return The found Book data
     * @throws BookException
     *             If anything goes wrong with this method
     */
    String getRawText(Key key) throws BookException;

    /**
     * A Book is writable if the file system allows the underlying files to be
     * opened for writing and if the driver for the book allows writing.
     * Ultimately, all drivers should allow writing. At this time writing is not
     * supported by drivers, so abstract implementations should return false and
     * let specific implementations return true otherwise.
     * 
     * @return true if the book is writable
     */
    boolean isWritable();

    /**
     * Store the raw text for the given key. This will replace/hide any raw text
     * that already is present. Note: it is the responsibility of the calling
     * program to ensure that the raw text matches the character set encoding
     * and markup of the module.
     * 
     * @param key
     *            The item to locate
     * @param rawData
     *            The text to store
     * @throws BookException
     *             If anything goes wrong with this method
     */
    void setRawText(Key key, String rawData) throws BookException;

    /**
     * Store an alias of one key to another. Some Bibles do not have a verse by
     * verse numbering system but rather meld several verses into one. Thus, any
     * verse in the range refers to the same verse. Also it may apply to
     * biblical commentaries that are indexed by Book, Chapter, Verse and that
     * discuss the Bible at a verse range level. For a dictionary, it may be
     * used for synonyms.
     * <p>
     * It should be an exception to set an alias when that alias already has raw
     * text. Also, it should be an exception to set an alias to an alias.
     * However, getRawText(Key) must be able to handle alias chains.
     * </p>
     * 
     * @param alias
     *            the key that aliases another
     * @param source
     *            the key that holds the text
     * @throws BookException
     *             If anything goes wrong with this method
     */
    void setAliasKey(Key alias, Key source) throws BookException;

    /**
     * Retrieval: For a given search spec find a list of references to it. If
     * there are no matches then null should be returned, otherwise a valid Key.
     * 
     * @param request
     *            The search spec.
     * @return the key that matches the search or null
     * @throws BookException
     *             If anything goes wrong with this method
     */
    Key find(SearchRequest request) throws BookException;

    /**
     * Retrieval: For a given search spec find a list of references to it. If
     * there are no matches then null should be returned, otherwise a valid Key.
     * 
     * @param request
     *            The search spec.
     * @return the key that matches the search or null
     * @throws BookException
     *             If anything goes wrong with this method
     */
    Key find(String request) throws BookException;

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
     * 
     * @return the book's driver
     */
    BookDriver getDriver();

    /**
     * The language of the book.
     * 
     * @return the common name for the language
     */
    Language getLanguage();

    /**
     * The abbreviation of this book - how people familiar with this book will know
     * it, for example "NIV", "KJV".
     * 
     * @return The book's initials
     */
    String getAbbreviation();

    /**
     * The internal name of this book.
     * 
     * @return The book's internal name
     */
    String getInitials();

    /**
     * Calculated field: Get an OSIS identifier for the OsisText.setOsisIDWork()
     * and the Work.setOsisWork() methods. The response will generally be of the
     * form [Bible][Dict..].getInitials
     * 
     * @return The OSIS id of this book
     */
    String getOsisID();

    /**
     * Return the likelihood that we have a match. This allows for calling the
     * book different things and still be found.
     * 
     * @param name one of many ways to name this book.
     * @return true if we have a match.
     */
    boolean match(String name);

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
     * expectation is that most books are not encrypted, abstract implementations
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
     * 
     * @param feature the type of the Feature to check
     * @return whether the feature is supported
     */
    boolean hasFeature(FeatureType feature);

    /**
     * Get a list of all the properties available to do with this Book. The
     * returned Properties will be read-only so any attempts to alter it will
     * fail.
     * 
     * @return the read-only properties for this book.
     */
    Set<String> getPropertyKeys();

    /**
     * Retrieve a single property for this book.
     * 
     * @param key
     *            the key of the property.
     * @return the value of the property
     */
    String getProperty(String key);

    /**
     * Set a property for this book.
     * 
     * @param key
     *            the key of the property.
     * @param value
     *            the value of the property
     */
    void putProperty(String key, String value);

    /**
     * Saves an entry to a particular configuration file.
     * 
     * @param key the entry that we are saving
     * @param value the value of the entry
     * @param forFrontend when {@code true} save to front end storage, else in shared storage
     */
    void putProperty(String key, String value, boolean forFrontend);

    /**
     * Has anyone generated a search index for this Book?
     * 
     * @return the status of the index for this book.
     * @see org.crosswire.jsword.index.IndexManager
     */
    IndexStatus getIndexStatus();

    /**
     * This method does not alter the index status, however it is for Indexers
     * that are responsible for indexing and have changed the status themselves.
     * 
     * @param status the status to set for this book
     * @see org.crosswire.jsword.index.IndexManager
     */
    void setIndexStatus(IndexStatus status);

    /**
     * Get an OSIS representation of information concerning this Book.
     * 
     * @return information for this book represented as OSIS
     */
    Document toOSIS();

    /**
     * Adds a <code>IndexStatusListener</code> to the listener list.
     * <p>
     * A <code>IndexStatusEvent</code> will get fired in response to
     * <code>setIndexStatus</code>.
     * 
     * @param li
     *            the <code>IndexStatusListener</code> to be added
     */
    void addIndexStatusListener(IndexStatusListener li);

    /**
     * Removes a <code>IndexStatusListener</code> from the listener list.
     * 
     * @param li
     *            the <code>IndexStatusListener</code> to be removed
     */
    void removeIndexStatusListener(IndexStatusListener li);
}
