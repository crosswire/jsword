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
package org.crosswire.jsword.book.basic;

import java.util.List;
import java.util.Map;

import org.crosswire.common.activate.Lock;
import org.crosswire.common.util.EventListenerList;
import org.crosswire.common.util.Language;
import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.FeatureType;
import org.crosswire.jsword.book.sword.processing.NoOpRawTextProcessor;
import org.crosswire.jsword.book.sword.processing.RawTextToXmlProcessor;
import org.crosswire.jsword.index.IndexStatus;
import org.crosswire.jsword.index.IndexStatusEvent;
import org.crosswire.jsword.index.IndexStatusListener;
import org.crosswire.jsword.index.search.DefaultSearchRequest;
import org.crosswire.jsword.index.search.SearchRequest;
import org.crosswire.jsword.index.search.Searcher;
import org.crosswire.jsword.index.search.SearcherFactory;
import org.crosswire.jsword.passage.Key;
import org.jdom.Content;
import org.jdom.Document;

/**
 * AbstractBook implements a few of the more generic methods of Book. This class
 * does a lot of work in helping make search easier, and implementing some basic
 * write methods.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public abstract class AbstractBook implements Book {
    public AbstractBook(BookMetaData bmd) {
        setBookMetaData(bmd);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.Book#getBookMetaData()
     */
    public final BookMetaData getBookMetaData() {
        return bmd;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.Book#setBookMetaData(org.crosswire.jsword.book
     * .BookMetaData)
     */
    public final void setBookMetaData(BookMetaData bmd) {
        this.bmd = bmd;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.common.activate.Activatable#activate(org.crosswire.common
     * .activate.Lock)
     */
    public void activate(Lock lock) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.common.activate.Activatable#deactivate(org.crosswire.common
     * .activate.Lock)
     */
    public void deactivate(Lock lock) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.Book#find(java.lang.String)
     */
    public Key find(String request) throws BookException {
        return find(new DefaultSearchRequest(request));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.Book#find(org.crosswire.jsword.index.search
     * .SearchRequest)
     */
    public Key find(SearchRequest request) throws BookException {
        if (searcher == null) {
            try {
                searcher = SearcherFactory.createSearcher(this);
            } catch (InstantiationException ex) {
                throw new BookException(JSOtherMsg.lookupText("Failed to initialize the search index"), ex);
            }
        }

        return searcher.search(request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.BookMetaData#getBook()
     */
    public Book getBook() {
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.BookMetaData#getDriver()
     */
    public BookDriver getDriver() {
        return bmd == null ? null : bmd.getDriver();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.BookMetaData#getDriverName()
     */
    public String getDriverName() {
        return bmd == null ? null : bmd.getDriverName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.Book#match(java.lang.String)
     */
    public boolean match(String name) {
        if (name == null) {
            return false;
        }
        if (name.equals(getInitials())) {
            return true;
        }
        if (name.equals(getName())) {
            return true;
        }
        if (name.equalsIgnoreCase(getInitials())) {
            return true;
        }
        if (name.equalsIgnoreCase(getName())) {
            return true;
        }
        if (name.startsWith(getInitials())) {
            return true;
        }
        if (name.startsWith(getName())) {
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.BookMetaData#getIndexStatus()
     */
    public IndexStatus getIndexStatus() {
        return bmd.getIndexStatus();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.BookMetaData#setIndexStatus(org.crosswire.jsword
     * .book.IndexStatus)
     */
    public void setIndexStatus(IndexStatus newStatus) {
        IndexStatus oldStatus = bmd.getIndexStatus();
        bmd.setIndexStatus(newStatus);
        firePropertyChange(oldStatus, newStatus);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.BookMetaData#getInitials()
     */
    public String getInitials() {
        return bmd.getInitials();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.BookMetaData#getLanguage()
     */
    public Language getLanguage() {
        return bmd.getLanguage();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.BookMetaData#getName()
     */
    public String getName() {
        return bmd.getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.BookMetaData#getOsisID()
     */
    public String getOsisID() {
        return bmd.getOsisID();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.BookMetaData#getProperties()
     */
    public Map<String, Object> getProperties() {
        return bmd.getProperties();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.Book#getProperty(java.lang.String)
     */
    public Object getProperty(String key) {
        return bmd.getProperty(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.Book#putProperty(java.lang.String,
     * java.lang.String)
     */
    public void putProperty(String key, Object value) {
        bmd.putProperty(key, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.BookMetaData#getType()
     */
    public BookCategory getBookCategory() {
        return bmd.getBookCategory();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.BookMetaData#isLeftToRight()
     */
    public boolean isLeftToRight() {
        return bmd.isLeftToRight();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.BookMetaData#isSupported()
     */
    public boolean isSupported() {
        return bmd.isSupported();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.BookMetaData#isEnciphered()
     */
    public boolean isEnciphered() {
        return bmd.isEnciphered();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.BookMetaData#isLocked()
     */
    public boolean isLocked() {
        return bmd.isLocked();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.BookMetaData#unlock(String)
     */
    public boolean unlock(String unlockKey) {
        boolean state = bmd.unlock(unlockKey);
        if (state) {
            // Double check.
            return isUnlockKeyValid();
        }
        return state;
    }

    /**
     * This is a heuristic that tries out the key.
     * 
     * @return true if there were no exceptions in reading the enciphered
     *         module.
     */
    private boolean isUnlockKeyValid() {
        try {
            Key key = getGlobalKeyList();
            if (key == null) {
                // weird key == null, assume it is valid
                return true;
            }

            if (key.getCardinality() > 0) {
                key = key.get(0);
            }

            getOsis(key, new NoOpRawTextProcessor());

            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    protected abstract List<Content> getOsis(Key key, RawTextToXmlProcessor noOpRawTextProcessor) throws BookException;

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.BookMetaData#getUnlockKey()
     */
    public String getUnlockKey() {
        return bmd.getUnlockKey();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.BookMetaData#isQuestionable()
     */
    public boolean isQuestionable() {
        return bmd.isQuestionable();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.BookMetaData#hasFeature(org.crosswire.jsword
     * .book.FeatureType)
     */
    public boolean hasFeature(FeatureType feature) {
        return bmd.hasFeature(feature);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.Book#addIndexStatusListener(org.crosswire.jsword
     * .index.IndexStatusListener)
     */
    public void addIndexStatusListener(IndexStatusListener listener) {
        if (listeners == null) {
            listeners = new EventListenerList();
        }
        listeners.add(IndexStatusListener.class, listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.Book#removeIndexStatusListener(org.crosswire
     * .jsword.index.IndexStatusListener)
     */
    public void removeIndexStatusListener(IndexStatusListener listener) {
        if (listeners == null) {
            return;
        }

        listeners.remove(IndexStatusListener.class, listener);
    }

    /**
     * Reports bound property changes. If <code>oldValue</code> and
     * <code>newValue</code> are not equal and the
     * <code>PropertyChangeEvent</code> listener list isn't empty, then fire a
     * <code>PropertyChange</code> event to each listener.
     * 
     * @param oldStatus
     *            the old value of the property (as an Object)
     * @param newStatus
     *            the new value of the property (as an Object)
     */
    protected void firePropertyChange(IndexStatus oldStatus, IndexStatus newStatus) {
        if (listeners != null) {
            if (oldStatus != null && newStatus != null && oldStatus.equals(newStatus)) {
                return;
            }

            Object[] listenerList = listeners.getListenerList();
            for (int i = 0; i <= listenerList.length - 2; i += 2) {
                if (listenerList[i] == IndexStatusListener.class) {
                    IndexStatusEvent ev = new IndexStatusEvent(this, newStatus);
                    IndexStatusListener li = (IndexStatusListener) listenerList[i + 1];
                    li.statusChanged(ev);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.BookMetaData#toOSIS()
     */
    public Document toOSIS() {
        return bmd.toOSIS();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        // Since this can not be null
        if (obj == null) {
            return false;
        }

        // We might consider checking for equality against all Books?
        // However currently we don't.

        // Check that that is the same as this
        // Don't use instanceof since that breaks inheritance
        if (!obj.getClass().equals(this.getClass())) {
            return false;
        }

        // The real bit ...
        Book that = (Book) obj;

        return bmd.equals(that.getBookMetaData());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return bmd.hashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Book obj) {
        return this.bmd.compareTo(obj.getBookMetaData());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
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

    /**
     * The list of property change listeners
     */
    private EventListenerList listeners;
}
