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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.crosswire.common.util.Logger;

/**
 * A default implementation of Key.
 * 
 * <p>
 * This implementation uses <tt>java.util.List</tt> to store keys.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class DefaultKeyList extends AbstractKeyList {
    /**
     * Simple ctor
     */
    public DefaultKeyList() {
        super(null);
    }

    /**
     * Simple ctor
     */
    public DefaultKeyList(Key parent, String name) {
        super(name);
        this.parent = parent;
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
     * @see org.crosswire.jsword.passage.Key#getCardinality()
     */
    public int getCardinality() {
        return keys.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#isEmpty()
     */
    /* @Override */
    public boolean isEmpty() {
        return keys.isEmpty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Key#contains(org.crosswire.jsword.passage
     * .Key)
     */
    /* @Override */
    public boolean contains(Key key) {
        return keys.contains(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#iterator()
     */
    public Iterator iterator() {
        return keys.iterator();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Key#add(org.crosswire.jsword.passage.Key)
     */
    public void addAll(Key key) {
        keys.add(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Key#remove(org.crosswire.jsword.passage.Key)
     */
    public void removeAll(Key key) {
        keys.remove(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#clear()
     */
    public void clear() {
        keys.clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#get(int)
     */
    public Key get(int index) {
        return (Key) keys.get(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Key#indexOf(org.crosswire.jsword.passage
     * .Key)
     */
    public int indexOf(Key that) {
        return keys.indexOf(that);
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
     * @see org.crosswire.jsword.passage.Key#blur(int)
     */
    public void blur(int by, RestrictionType restrict) {
        log.warn("attempt to blur a non-blur-able list");
    }

    /**
     * The parent of this key
     */
    private Key parent;

    /**
     * The store of Keys
     */
    private List keys = new ArrayList();

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = -1633375337613230599L;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(DefaultKeyList.class);
}
