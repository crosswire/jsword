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
package org.crosswire.jsword.passage;

import java.util.Iterator;

import org.crosswire.common.util.ItemIterator;

/**
 * A simple default implementation of the Key interface.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class DefaultLeafKeyList implements Key {
    /**
     * Default ctor
     */
    public DefaultLeafKeyList(String name) {
        this(name, name, null);
    }

    /**
     * Default ctor
     */
    public DefaultLeafKeyList(String name, String osisName) {
        this(name, osisName, null);
    }

    /**
     * Default ctor
     */
    public DefaultLeafKeyList(String name, String osisName, Key parent) {
        this.name = name;
        this.parent = parent;
        this.osisName = osisName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#canHaveChildren()
     */
    public boolean canHaveChildren() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#getChildCount()
     */
    public int getChildCount() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#getName()
     */
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Key#getName(org.crosswire.jsword.passage
     * .Key)
     */
    public String getName(Key base) {
        return getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#getRootName()
     */
    public String getRootName() {
        return getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#getOSISRef()
     */
    public String getOsisRef() {
        return osisName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#getOSISId()
     */
    public String getOsisID() {
        return getOsisRef();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#getParent()
     */
    public Key getParent() {
        return parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#getCardinality()
     */
    public int getCardinality() {
        return 1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#isEmpty()
     */
    public boolean isEmpty() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Key#contains(org.crosswire.jsword.passage
     * .Key)
     */
    public boolean contains(Key key) {
        return this.equals(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#iterator()
     */
    public Iterator<Key> iterator() {
        return new ItemIterator<Key>(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Key#add(org.crosswire.jsword.passage.Key)
     */
    public void addAll(Key key) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Key#remove(org.crosswire.jsword.passage.Key)
     */
    public void removeAll(Key key) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Key#retain(org.crosswire.jsword.passage.Key)
     */
    public void retainAll(Key key) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#clear()
     */
    public void clear() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#get(int)
     */
    public Key get(int index) {
        if (index == 0) {
            return this;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Key#indexOf(org.crosswire.jsword.passage
     * .Key)
     */
    public int indexOf(Key that) {
        if (this.equals(that)) {
            return 0;
        }
        return -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#blur(int)
     */
    public void blur(int by, RestrictionType restrict) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        // Since this can not be null
        if (obj == null) {
            return false;
        }

        // We might consider checking for equality against all Keys?
        // However currently we don't.

        // Check that that is the same as this
        // Don't use instanceof since that breaks inheritance
        if (!obj.getClass().equals(this.getClass())) {
            return false;
        }

        // The real bit ...
        DefaultLeafKeyList that = (DefaultLeafKeyList) obj;
        return name.equals(that.name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Key obj) {
        DefaultLeafKeyList that = (DefaultLeafKeyList) obj;
        return name.compareTo(that.name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() {
        DefaultLeafKeyList clone = null;
        try {
            clone = (DefaultLeafKeyList) super.clone();
            if (parent != null) {
                clone.parent = (Key) parent.clone();
            }
        } catch (CloneNotSupportedException e) {
            assert false : e;
        }
        return clone;
    }

    /**
     * The parent of this key
     */
    private Key parent;

    /**
     * The human readable string that this key represents
     */
    private String name;

    /**
     * The OSIS version of this Key
     */
    private String osisName;

    /**
     *
     */
    private static final long serialVersionUID = -7462556005744186622L;

}
