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
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.book;

import java.util.Map;

import org.crosswire.common.activate.Activatable;
import org.crosswire.jsword.index.IndexStatus;
import org.crosswire.jsword.index.IndexStatusListener;
import org.crosswire.jsword.index.search.SearchRequest;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyFactory;
import org.jdom.Document;

/**
 * Book is the most basic store of textual data - It can retrieve data
 * either as an XML document or as plain text - It uses Keys to refer
 * to parts of itself, and can search for words (returning Keys).
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public interface Book extends Activatable, KeyFactory, Comparable
{
    /**
     * Meta-Information: What version of the Bible is this?
     * @return A Version for this Bible
     */
    BookMetaData getBookMetaData();

    /**
     * Set the meta-information for this book.
     */
    void setBookMetaData(BookMetaData bmd);

    /**
     * Retrieval: Add to the given document some mark-up for the specified
     * Verses.
     * @param key The verses to search for
     * @return The found Book data
     * @throws BookException If anything goes wrong with this method
     */
    BookData getData(Key key) throws BookException;

    /**
     * Returns the raw text that getData(Key key) builds into OSIS.
     * @param key The verses to search for
     * @return The found Book data
     * @throws BookException If anything goes wrong with this method
     */
    String getRawData(Key key) throws BookException;

    /**
     * Retrieval: For a given search spec find a list of references to it.
     * If there are no matches then null should be returned, otherwise a valid
     * Key.
     * @param request The search spec.
     * @throws BookException If anything goes wrong with this method
     */
    Key find(SearchRequest request) throws BookException;

    /**
     * Retrieval: For a given search spec find a list of references to it.
     * If there are no matches then null should be returned, otherwise a valid
     * Key.
     * @param request The search spec.
     * @throws BookException If anything goes wrong with this method
     */
    Key find(String request) throws BookException;

    /**
     * The name of the book, for example "King James Version" or
     * "Bible in Basic English" or "Greek".
     * In general it should be possible to deduce the initials from the name by
     * removing all the non-capital letters. Although this is only a generalization.
     * This method should not return null or a blank string.
     * @return The name of this book
     */
    String getName();

    /**
     * What category of content is this, a Bible or a reference work like a
     * Dictionary or Commentary.
     * @return The category of book
     */
    BookCategory getBookCategory();

    /**
     * Accessor for the driver that runs this Book.
     * Note this method should only be used to delete() Books. Everything else
     * you should want to do to a Book should be available in other ways.
     */
    BookDriver getDriver();

    /**
     * The language of the book is the common name for the iso639 code.
     * @return the common name for the language
     */
    String getLanguage();

    /**
     * The initials of this book - how people familiar with this book will know
     * it, for example "NIV", "KJV".
     * @return The book's initials
     */
    String getInitials();

    /**
     * Calculated field: Get an OSIS identifier for the OsisText.setOsisIDWork()
     * and the Work.setOsisWork() methods.
     * The response will generally be of the form [Bible][Dict..].getInitials
     * @return The osis id of this book
     */
    String getOsisID();

    /**
     * Calculated field: The full name of the book, for example
     * The format is "name, (Driver)"
     * @return The full name of this book
     */
    String getFullName();

    /**
     * Indicate whether this book is supported by JSword.
     * Since the expectation is that all books are supported,
     * abstract implementations should return true and let
     * specific implementations return false if they cannot
     * support the book.
     * 
     * @return true if the book is supported
     */
    public boolean isSupported();

    /**
     * Indicate whether this book is enciphered and without a key.
     * Since the expectation is that most books are unenciphered,
     * abstract implementations should return false and let
     * specific implementations return true otherwise.
     * 
     * @return true if the book is enciphered
     */
    public boolean isEnciphered();

    /**
     * Indicate whether this book is questionable. A book may
     * be deemed questionable if it's quality or content has not
     * been confirmed.
     * Since the expectation is that all books are not questionable,
     * abstract implementations should return false and let
     * specific implementations return true if the book is questionable.
     * 
     * @return true if the book is questionable
     */
    public boolean isQuestionable();

    /**
     * Calculated field: The name of the name, which could be helpful to
     * distinguish similar Books available through 2 BookDrivers.
     * @return The driver name
     */
    String getDriverName();

    /**
     * Return the orientation of the language of the Book. If a book contains more than one language,
     * it refers to the dominate language of the book. This will be used to present
     * Arabic and Hebrew in their propper orientation.
     * @return true if the orientation for the dominate language is LeftToRight.
     */
    boolean isLeftToRight();

    /**
     * Return whether the feature is supported by the book.
     */
    boolean hasFeature(FeatureType feature);

    /**
     * Get a list of all the properties available to do with this Book.
     * The returned Properties will be read-only so any attempts to alter it
     * will fail.
     */
    Map getProperties();

    /**
     * Has anyone generated a search index for this Book?
     * @see org.crosswire.jsword.index.IndexManager
     */
    IndexStatus getIndexStatus();

    /**
     * This method does not alter the index status, however it is for Indexers
     * that are responsible for indexing and have changed the status themselves.
     * @see org.crosswire.jsword.index.IndexManager
     */
    void setIndexStatus(IndexStatus status);

    /**
     * Get an OSIS representation of information concerning this Book.
     */
    Document toOSIS();

    /**
     * Adds a <code>IndexStatusListener</code> to the listener list.
     * <p>A <code>IndexStatusEvent</code> will get fired in response
     * to <code>setIndexStatus</code>.
     * @param li the <code>IndexStatusListener</code> to be added
     */
    void addIndexStatusListener(IndexStatusListener li);

    /**
     * Removes a <code>IndexStatusListener</code> from the listener list.
     * @param li the <code>IndexStatusListener</code> to be removed
     */
    void removeIndexStatusListener(IndexStatusListener li);

}
