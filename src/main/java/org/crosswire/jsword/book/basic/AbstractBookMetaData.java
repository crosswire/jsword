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
package org.crosswire.jsword.book.basic;

import java.net.URI;

import org.crosswire.common.util.Language;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.FeatureType;
import org.crosswire.jsword.book.KeyType;
import org.crosswire.jsword.index.IndexStatus;
import org.jdom2.Document;

/**
 * An implementation of the Property Change methods from BookMetaData.
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */

/**
 * @author DM Smith
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.<br>
 * The copyright to this program is held by its authors.
 */
public abstract class AbstractBookMetaData implements BookMetaData {

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getKeyType()
     */
    public KeyType getKeyType() {
        return KeyType.LIST;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getDriver()
     */
    public BookDriver getDriver() {
        return driver;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getDriverName()
     */
    public String getDriverName() {
        if (getDriver() == null) {
            return null;
        }

        return getDriver().getDriverName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#hasFeature(org.crosswire.jsword.book.FeatureType)
     */
    public boolean hasFeature(FeatureType feature) {
        return false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getOsisID()
     */
    public String getOsisID() {
        return getBookCategory().getName() + '.' + getInitials();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#isSupported()
     */
    public boolean isSupported() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#isEnciphered()
     */
    public boolean isEnciphered() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#isLocked()
     */
    public boolean isLocked() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#unlock(java.lang.String)
     */
    public boolean unlock(String unlockKey) {
        return false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getUnlockKey()
     */
    public String getUnlockKey() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#isQuestionable()
     */
    public boolean isQuestionable() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getLanguage()
     */
    public Language getLanguage() {
        return this.language;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#setLanguage(org.crosswire.common.util.Language)
     */
    public void setLanguage(Language language) {
        this.language = language;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getLibrary()
     */
    public URI getLibrary() {
        return library;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#setLibrary(java.net.URI)
     */
    public void setLibrary(URI library) throws BookException {
        this.library = library;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#setLocation(java.net.URI)
     */
    public void setLocation(URI location) {
        this.location = location;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getLocation()
     */
    public URI getLocation() {
        return location;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getIndexStatus()
     */
    public IndexStatus getIndexStatus() {
        return indexStatus;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#setIndexStatus(org.crosswire.jsword.index.IndexStatus)
     */
    public void setIndexStatus(IndexStatus newValue) {
        indexStatus = newValue;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#reload()
     */
    public void reload() throws BookException {
        // over ride this if partial loads are allowed
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#putProperty(java.lang.String, java.lang.String)
     */
    public void putProperty(String key, String value) {
        putProperty(key, value, false);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#toOSIS()
     */
    public Document toOSIS() {
        throw new UnsupportedOperationException("If you want to use this, implement it.");
    }

    /**
     * @param driver The driver to set.
     */
    public void setDriver(BookDriver driver) {
        this.driver = driver;
    }

    @Override
    public boolean equals(Object obj) {
        // Since this can not be null
        if (obj == null) {
            return false;
        }

        // We might consider checking for equality against all BookMetaDatas?
        // However currently we don't.

        // Check that that is the same as this
        // Don't use instanceof since that breaks inheritance
        if (!obj.getClass().equals(this.getClass())) {
            return false;
        }

        // The real bit ...
        BookMetaData that = (BookMetaData) obj;

        return getBookCategory().equals(that.getBookCategory()) && getName().equals(that.getName()) && getInitials().equals(that.getInitials());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    /* 
     * The sort order should be based on initials rather than name because name often begins with general words like 'The ...'
     * (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(BookMetaData obj) {
        int result = this.getBookCategory().compareTo(obj.getBookCategory());
        if (result == 0) {
            result = this.getAbbreviation().compareTo(obj.getAbbreviation());
        }
        if (result == 0) {
            result = this.getInitials().compareTo(obj.getInitials());
        }
        if (result == 0) {
            result = this.getName().compareTo(obj.getName());
        }
        return result;
    }

    @Override
    public String toString() {
        String internal = getInitials();
        String abbreviation = getAbbreviation();
        if (internal.equals(abbreviation)) {
            return internal;
        }
        StringBuffer buf = new StringBuffer(internal);
        buf.append('(');
        buf.append(abbreviation);
        buf.append(')');
        return buf.toString();
    }

    private BookDriver driver;
    private IndexStatus indexStatus = IndexStatus.UNDONE;
    private Language language;
    private URI library;
    private URI location;

}
