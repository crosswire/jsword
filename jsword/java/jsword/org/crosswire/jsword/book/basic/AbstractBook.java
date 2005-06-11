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
package org.crosswire.jsword.book.basic;

import java.beans.PropertyChangeListener;
import java.util.Map;

import org.crosswire.common.activate.Lock;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.FeatureType;
import org.crosswire.jsword.book.IndexStatus;
import org.crosswire.jsword.book.search.SearchRequest;
import org.crosswire.jsword.book.search.Searcher;
import org.crosswire.jsword.book.search.SearcherFactory;
import org.crosswire.jsword.book.search.basic.DefaultSearchRequest;
import org.crosswire.jsword.passage.Key;
import org.jdom.Document;

/**
 * AbstractBook implements a few of the more generic methods of Book.
 * This class does a lot of work in helping make search easier, and implementing
 * some basic write methods. 
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public abstract class AbstractBook implements Book
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getBookMetaData()
     */
    public final BookMetaData getBookMetaData()
    {
        return bmd;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#setBookMetaData(org.crosswire.jsword.book.BookMetaData)
     */
    public final void setBookMetaData(BookMetaData bmd)
    {
        this.bmd = bmd;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#activate(org.crosswire.common.activate.Lock)
     */
    public void activate(Lock lock)
    {
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#deactivate(org.crosswire.common.activate.Lock)
     */
    public void deactivate(Lock lock)
    {
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#find(java.lang.String)
     */
    public Key find(String request) throws BookException
    {
        return find(new DefaultSearchRequest(request));
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#find(org.crosswire.jsword.book.search.SearchRequest)
     */
    public Key find(SearchRequest request) throws BookException
    {
        if (searcher == null)
        {
            try
            {
                searcher = SearcherFactory.createSearcher(this);
            }
            catch (InstantiationException ex)
            {
                throw new BookException(Msg.INDEX_FAIL);
            }
        }

        return searcher.search(request);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getBook()
     */
    public Book getBook()
    {
        return this;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getDriver()
     */
    public BookDriver getDriver()
    {
        return bmd.getDriver();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getDriverName()
     */
    public String getDriverName()
    {
        return bmd.getDriverName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getFullName()
     */
    public String getFullName()
    {
        return bmd.getFullName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getIndexStatus()
     */
    public IndexStatus getIndexStatus()
    {
        return bmd.getIndexStatus();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#setIndexStatus(org.crosswire.jsword.book.IndexStatus)
     */
    public void setIndexStatus(IndexStatus status)
    {
        bmd.setIndexStatus(status);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getInitials()
     */
    public String getInitials()
    {
        return bmd.getInitials();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getLanguage()
     */
    public String getLanguage()
    {
        return bmd.getLanguage();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getName()
     */
    public String getName()
    {
        return bmd.getName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getOsisID()
     */
    public String getOsisID()
    {
        return bmd.getOsisID();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getProperties()
     */
    public Map getProperties()
    {
        return bmd.getProperties();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getType()
     */
    public BookCategory getType()
    {
        return bmd.getType();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#isLeftToRight()
     */
    public boolean isLeftToRight()
    {
        return bmd.isLeftToRight();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#hasFeature(org.crosswire.jsword.book.FeatureType)
     */
    public boolean hasFeature(FeatureType feature)
    {
        return bmd.hasFeature(feature);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#removePropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void removePropertyChangeListener(PropertyChangeListener li)
    {
        bmd.removePropertyChangeListener(li);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#addPropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void addPropertyChangeListener(PropertyChangeListener li)
    {
        bmd.addPropertyChangeListener(li);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#toOSIS()
     */
    public Document toOSIS()
    {
        return bmd.toOSIS();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        // Since this can not be null
        if (obj == null)
        {
            return false;
        }

        // We might consider checking for equality against all Books?
        // However currently we dont.

        // Check that that is the same as this
        // Don't use instanceof since that breaks inheritance
        if (!obj.getClass().equals(this.getClass()))
        {
            return false;
        }

        // The real bit ...
        Book that = (Book) obj;

        return bmd.equals(that.getBookMetaData());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return bmd.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object obj)
    {
        Book that = (Book) obj;
        return this.bmd.compareTo(that.getBookMetaData());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return bmd.toString();
    }

    /**
     * How do we perform searches
     */
    private Searcher searcher;

    /**
     * The meta data for this book
     */
    private BookMetaData bmd;
}
