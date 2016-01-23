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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Key that knows where the data is in the real file.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class TreeKey extends AbstractKeyList {
    /**
     * Setup with the key name and positions of data in the file
     *
     * @param name the key for this TreeKey 
     * @param parent the parent node for this TreeKey
     */
    public TreeKey(String name, Key parent) {
        super(name);
        this.parent = parent;
        this.children = new ArrayList<Key>();
    }

    /**
     * Setup with the key name. Use solely for searching.
     *
     * @param text the key for this TreeKey 
      */
    public TreeKey(String text) {
        this(text, null);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#canHaveChildren()
     */
    public boolean canHaveChildren() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getChildCount()
     */
    public int getChildCount() {
        return children.size();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getCardinality()
     */
    public int getCardinality() {
        int cardinality = 1; // count this node
        for (Key child : children) {
            cardinality += child.getCardinality();
        }

        return cardinality;
    }

    @Override
    public boolean isEmpty() {
        return children.isEmpty();
    }

    @Override
    public boolean contains(Key key) {
        if (children.contains(key)) {
            return true;
        }

        for (Key child : children) {
            if (child.contains(key)) {
                return true;
            }
        }

        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<Key> iterator() {
        return new KeyIterator(this);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#addAll(org.crosswire.jsword.passage.Key)
     */
    public void addAll(Key key) {
        children.add(key);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#removeAll(org.crosswire.jsword.passage.Key)
     */
    public void removeAll(Key key) {
        children.remove(key);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#clear()
     */
    public void clear() {
        children.clear();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#get(int)
     */
    public Key get(int index) {
        return children.get(index);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#indexOf(org.crosswire.jsword.passage.Key)
     */
    public int indexOf(Key that) {
        return children.indexOf(that);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getParent()
     */
    public Key getParent() {
        return parent;
    }

    /** equality is tricky if comparing TreeKeys (as used by GenBooks) because some child keys can have the same name but different parents
     */
    @Override
    public boolean equals(Object obj) {
        // Since this can not be null
        if (obj == null) {
            return false;
        }

        // Check that that is the same as this
        // Don't use instanceOf since that breaks inheritance
        if (!obj.getClass().equals(this.getClass())) {
            return false;
        }

        TreeKey otherTreeKey = (TreeKey) obj;
        if (!getName().equals(otherTreeKey.getName())) {
            return false;
        }

        // names match so now work up the tree comparing parents
        if (getParent() == null) {
            return otherTreeKey.getParent() == null;
        }

        // KeyTrees nodes can have the same name but different parents
        return getParent().equals(otherTreeKey.getParent());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#blur(int, org.crosswire.jsword.passage.RestrictionType)
     */
    public void blur(int by, RestrictionType restrict) {
        log.warn("attempt to blur a non-blur-able list");
    }

    @Override
    public TreeKey clone() {
        return (TreeKey) super.clone();
    }

    @Override
    public String getRootName() {
        String rootName = getName();
        for (Key parentKey = this; parentKey != null && parentKey.getName().length() > 0; parentKey = parentKey.getParent()) {
            rootName = parentKey.getName();
        }
        return rootName;
    }

    @Override
    public String getOsisRef() {
        return getOsisID();
    }

    @Override
    public String getOsisID() {
        StringBuilder b = new StringBuilder(100);
        b.append(getName());
        for (Key parentKey = this.getParent(); parentKey != null && parentKey.getName().length() > 0; parentKey = parentKey.getParent()) {
            b.insert(0, "/");
            b.insert(0, parentKey.getName());
        }
        // Remove the leading .
        return b.toString();
    }

    /**
     * The parent of this key.
     */
    private Key parent;

    /**
     * The immediate children of this tree node.
     */
    private List<Key> children;

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(TreeKey.class);

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = -6560408145705717977L;
}
