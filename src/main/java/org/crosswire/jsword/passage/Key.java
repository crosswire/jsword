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

import java.io.Serializable;

/**
 * A Key is a Key that can contain other Keys.
 * 
 * The interface is modeled on the java.util.Set interface customized because
 * KeyLists can only store other Keys and simplified by making add() and
 * remove() return void and not a boolean.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public interface Key extends Comparable<Key>, Iterable<Key>, Cloneable, Serializable {
    /**
     * A Human readable version of the Key. For Biblical passages this uses
     * short books names, and the shortest sensible rendering, for example
     * "Mat 3:1-4" and "Mar 1:1, 3, 5" and "3Jo, Jude"
     * 
     * @return a String containing a description of the Key
     */
    String getName();

    /**
     * Translate the Key into a human readable string, with the assumption that
     * the specified Key has just been output, so if we are in the same region,
     * we do not need to display the region name, and so on.
     * 
     * @param base
     *            The key to use to cut down unnecessary output.
     * @return The string representation
     */
    String getName(Key base);

    /**
     * A Human readable version of the Key's top level name. For Biblical
     * passages this uses short books names. For a dictionary it might return
     * A-Z.
     * 
     * @return a String containing a description of the Key
     */
    String getRootName();

    /**
     * The OSIS defined reference specification for this Key. When the key is a
     * single element, it is an OSIS book name with '.' separating the parts.
     * When the key is multiple elements, it uses a range notation. Note, this
     * will create a comma separated list of ranges, which is improper OSIS.
     * 
     * @return a String containing the OSIS description of the verses
     */
    String getOsisRef();

    /**
     * The OSIS defined id specification for this Key. When the key is a single
     * element, it is an OSIS book name with '.' separating the parts. When the
     * key is multiple elements, it uses a space to separate each.
     * 
     * @return a String containing the OSIS description of the verses
     */
    String getOsisID();

    /**
     * All keys have parents unless they are the root of a Key.
     * 
     * @return The parent of this tree, or null if this Key is the root.
     */
    Key getParent();

    /**
     * Returns false if the receiver is a leaf node and can not have children.
     * Any attempt to add()/remove() will throw
     * 
     * @return true if the key can have children
     */
    boolean canHaveChildren();

    /**
     * Returns the number of children that this node has. Leaf nodes return 0.
     * 
     * @return the number of children for the node
     */
    int getChildCount();

    /**
     * Returns the number of elements in this set (its cardinality). If this set
     * contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     * <p>
     * This method is potentially expensive, as it often requires cycling through all the keys in the set.</p>
     * @return the number of elements in this set (its cardinality).
     */
    int getCardinality();

    /**
     * Does this Key have 0 members
     * 
     * @return <tt>true</tt> if this set contains no elements.
     */
    boolean isEmpty();

    /**
     * Returns <tt>true</tt> if this set contains the specified element.
     * 
     * @param key
     *            element whose presence in this set is to be tested.
     * @return <tt>true</tt> if this set contains the specified element.
     */
    boolean contains(Key key);

    /**
     * Adds the specified element to this set if it is not already present.
     * 
     * @param key
     *            element to be added to this set.
     * @throws NullPointerException
     *             if the specified element is null
     */
    void addAll(Key key);

    /**
     * Removes the specified elements from this set if it is present.
     * 
     * @param key
     *            object to be removed from this set, if present.
     * @throws NullPointerException
     *             if the specified element is null
     */
    void removeAll(Key key);

    /**
     * Removes all but the specified element from this set.
     * 
     * @param key
     *            object to be left in this set.
     * @throws NullPointerException
     *             if the specified element is null
     */
    void retainAll(Key key);

    /**
     * Removes all of the elements from this set (optional operation). This set
     * will be empty after this call returns (unless it throws an exception).
     */
    void clear();

    /**
     * Gets a key from a specific point in this list of children.
     * 
     * @param index
     *            The index of the Key to retrieve
     * @return The specified key
     * @throws IndexOutOfBoundsException
     */
    Key get(int index);

    /**
     * Reverse a Key into the position the key holds in the list of children
     * 
     * @param that
     *            The Key to find
     * @return The index of the key or &lt; 0 if the key is not in the list
     */
    int indexOf(Key that);

    /**
     * Widen the range of the verses/keys in this list. This is primarily for
     * "find x within n verses of y" type applications.
     * 
     * @param by
     *            The number of verses/keys to widen by
     * @param restrict
     *            How should we restrict the blurring?
     * @see Passage
     */
    void blur(int by, RestrictionType restrict);

    /**
     * This needs to be declared here so that it is visible as a method on a
     * derived Key.
     * 
     * @return A complete copy of ourselves
     */
    Key clone();

    /**
     * This needs to be declared here so that it is visible as a method on a
     * derived Key.
     * 
     * @param obj 
     * @return true if equal
     */
    boolean equals(Object obj);

    /**
     * This needs to be declared here so that it is visible as a method on a
     * derived Key.
     * 
     * @return the hashcode
     */
    int hashCode();
}
