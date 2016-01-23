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
 * © CrossWire Bible Society, 2015 - 2016
 */
package org.crosswire.common.util;

import java.util.Comparator;

/**
 * ChainLink allows for a doubly linked list embedded within objects.
 * This is meant for small lists.
 *
 * @param <E> the class to be chained
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public final class ChainLink<E> {
    /**
     * Create an empty ChainLink.
     * It is necessary to call {@code setItem()} for this to be useful.
     */
    public ChainLink() {
        this(null);
    }

    /**
     * Create a ChainLink containing an item.
     * 
     * @param item this ChainLink's item
     */
    public ChainLink(E item) {
        this.item = item;
        this.head = true;
        this.right = this;
        this.left = this;
    }

    /**
     * Set the item for this ChainLink.
     * 
     * @param item this ChainLink's item
     */
    public void setItem(E item) {
        this.item = item;
    }

    /**
     * Get the item from this ChainLink.
     * 
     * @return the item
     */
    public E getItem() {
        return item;
    }

    /**
     * One node is marked as the head of the chain.
     * 
     * @return whether this node is the head of the chain.
     */
    public boolean isHead() {
        return head;
    }

    /**
     * Get the node to the left of this node.
     * The left node of the head is the tail.
     * 
     * @return the node to the left
     */
    public ChainLink<E> getLeft() {
        return left;
    }

    /**
     * Get the node to the right of this node.
     * The right node of the tail is the head.
     * 
     * @return the node to the right
     */
    public ChainLink<E> getRight() {
        return right;
    }

    /**
     * Remove this ChainLink from the chain.
     * If the head is removed, then the node that was to the right is now the head.
     */
    public void remove() {
        // First ensure that
        // this node is removed from its current list
        // and that that list is still whole
        this.right.head = this.head;
        this.right.left = this.left;
        this.left.right = this.right;
        this.head = true;
        this.right = this;
        this.left = this;
    }

    /**
     * Add this node before the given node.
     * If the given node was the head, then this node is now the head.
     * 
     * @param node A location in the chain for insertion.
     */
    public void addBefore(ChainLink<E> node) {
        this.head = node.head; // This is head, iff node was
        this.right = node;
        this.left = node.left;
        node.head = false; // node cannot now be head
        node.left.right = this;
        node.left = this;
    }

    /**
     * Add this node after the given node.
     * If the given node was the head, it still is.
     * 
     * @param node A location in the chain for insertion.
     */
    public void addAfter(ChainLink<E> node) {
        this.head = false; // this can never be head
        this.left = node;
        this.right = node.right;
        node.right.left = this;
        node.right = this;
    }

    /**
     * Find the head of the chain.
     * Note, this is most efficient if this node is the head.
     * 
     * @return the head of the chain.
     */
    public ChainLink<E> findHead() {
        ChainLink<E> node = this;
        while (!node.head) {
            node = node.left;
            if (this == node) {
                // We made it all away round and nothing was marked head.
                node.head = true;
            }
        }
        return node;
    }

    /**
     * Find the tail of the chain.
     * Note, this is most efficient if this node is the head.
     * 
     * @return the tail of the chain.
     */
    public ChainLink<E> findTail() {
        return findHead().left;
    }

    /**
     * Add this node as the head of the chain.
     * Note, this is most efficient if anyNode is the head of the chain.
     * 
     * @param anyNode any node in the chain
     */
    public void addFirst(ChainLink<E> anyNode) {
        ChainLink<E> node = anyNode.findHead();
        addBefore(node);
    }

    /**
     * Add this node as the tail of the chain.
     * Note, this is most efficient if anyNode is the head of the chain.
     * 
     * @param anyNode any node in the chain
     */
    public void addLast(ChainLink<E> anyNode) {
        ChainLink<E> node = anyNode.findHead();
        addAfter(node.left);
    }

    /**
     * Locate the node or the closest node in the chain using the comparator.
     * This assumes that the collection is ordered on some characteristic that
     * comparator assumes.
     * 
     * @param element The node to look for
     * @param comparator The comparator that understands the ordering of the list
     * @return The node or the closest node.
     */
    public ChainLink<E> locate(E element, Comparator<E> comparator) {
        // this node might be anywhere in the list.
        // compare it to this one
        ChainLink<E> node = this;
        int cmp = comparator.compare(element, node.item);
        if (cmp < 0) {
            while (cmp < 0 && !node.head) {
                node = node.left;
                cmp = comparator.compare(element, node.item);
                if (node == this) {
                    node.head = true;
                }
            }
            return node;
        } else if (cmp > 0) {
            do {
                node = node.right;
                cmp = comparator.compare(element, node.item);
            } while (cmp > 0 && !node.head && node != this);
            return node;
        }
        return node;
    }

    private E item;
    private boolean head;
    private ChainLink<E> left;
    private ChainLink<E> right;
}
