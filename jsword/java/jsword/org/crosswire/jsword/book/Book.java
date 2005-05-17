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

import org.crosswire.common.activate.Activatable;
import org.crosswire.jsword.book.search.SearchRequest;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyFactory;

/**
 * Book is the most basic store of textual data - It can retrieve data
 * either as an XML document or as plain text - It uses Keys to refer
 * to parts of itself, and can search for words (returning Keys).
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public interface Book extends Activatable, KeyFactory, BookMetaData
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
}
