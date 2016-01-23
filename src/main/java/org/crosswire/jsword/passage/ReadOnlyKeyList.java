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
package org.crosswire.jsword.passage;

import java.util.Iterator;

import org.crosswire.jsword.JSOtherMsg;

/**
 * A read-only wrapper around any writable implementation of Key.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class ReadOnlyKeyList implements Key {
    /**
     * Simple ctor
     * 
     * @param keys the keys
     * @param ignore 
     */
    public ReadOnlyKeyList(Key keys, boolean ignore) {
        this.keys = keys;
        this.ignore = ignore;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getCardinality()
     */
    public int getCardinality() {
        return keys.getCardinality();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#canHaveChildren()
     */
    public boolean canHaveChildren() {
        return keys.canHaveChildren();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getChildCount()
     */
    public int getChildCount() {
        return keys.getChildCount();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#isEmpty()
     */
    public boolean isEmpty() {
        return keys.isEmpty();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#contains(org.crosswire.jsword.passage.Key)
     */
    public boolean contains(Key key) {
        return keys.contains(key);
    }

    /* (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<Key> iterator() {
        return keys.iterator();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#addAll(org.crosswire.jsword.passage.Key)
     */
    public void addAll(Key key) {
        if (ignore) {
            return;
        }

        throw new IllegalStateException(JSOtherMsg.lookupText("Cannot alter a read-only key list"));
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#removeAll(org.crosswire.jsword.passage.Key)
     */
    public void removeAll(Key key) {
        if (ignore) {
            return;
        }

        throw new IllegalStateException(JSOtherMsg.lookupText("Cannot alter a read-only key list"));
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#retainAll(org.crosswire.jsword.passage.Key)
     */
    public void retainAll(Key key) {
        if (ignore) {
            return;
        }

        throw new IllegalStateException(JSOtherMsg.lookupText("Cannot alter a read-only key list"));
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#clear()
     */
    public void clear() {
        if (ignore) {
            return;
        }

        throw new IllegalStateException(JSOtherMsg.lookupText("Cannot alter a read-only key list"));
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getName()
     */
    public String getName() {
        return keys.getName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getName(org.crosswire.jsword.passage.Key)
     */
    public String getName(Key base) {
        return keys.getName(base);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getRootName()
     */
    public String getRootName() {
        return keys.getRootName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getOsisRef()
     */
    public String getOsisRef() {
        return keys.getOsisRef();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getOsisID()
     */
    public String getOsisID() {
        return keys.getOsisID();
    }

    @Override
    public int hashCode() {
        return keys.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return keys.equals(obj);
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Key o) {
        return keys.compareTo(o);
    }

   /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#get(int)
     */
    public Key get(int index) {
        return keys.get(index);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#indexOf(org.crosswire.jsword.passage.Key)
     */
    public int indexOf(Key that) {
        return keys.indexOf(that);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getParent()
     */
    public Key getParent() {
        return keys.getParent();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#blur(int, org.crosswire.jsword.passage.RestrictionType)
     */
    public void blur(int by, RestrictionType restrict) {
        if (ignore) {
            return;
        }

        throw new IllegalStateException(JSOtherMsg.lookupText("Cannot alter a read-only key list"));
    }

    @Override
    public ReadOnlyKeyList clone() {
        ReadOnlyKeyList clone = null;
        try {
            clone = (ReadOnlyKeyList) super.clone();
            clone.keys = keys.clone();
        } catch (CloneNotSupportedException e) {
            assert false : e;
        }
        return clone;
    }

    /**
     * Do we ignore write requests or throw?
     */
    private boolean ignore;

    /**
     * The Key to which we proxy
     */
    private Key keys;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = -7947159638198641657L;
}
