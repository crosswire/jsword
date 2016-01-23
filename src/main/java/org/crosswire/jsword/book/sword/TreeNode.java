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
package org.crosswire.jsword.book.sword;

import java.io.Serializable;

/**
 * A node that knows where the data is in the real file and where it is in
 * relationship to other nodes.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
class TreeNode implements Cloneable, Serializable {
    /**
     * TreeNode default ctor.
     */
    TreeNode() {
        this(-1);
    }

    /**
     * Setup with the positions of data in the file
     * 
     * @param theOffset
     */
    TreeNode(int theOffset) {
        offset = theOffset;
        name = "";
        parent = -1;
        nextSibling = -1;
        firstChild = -1;
        userData = new byte[0];
    }

    /**
     * @return the offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     * @param newOffset
     *            the offset to set
     */
    public void setOffset(int newOffset) {
        offset = newOffset;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param newName
     *            the name to set
     */
    public void setName(String newName) {
        name = newName;
    }

    /**
     * @return the userData
     */
    public byte[] getUserData() {
        return userData.clone();
    }

    /**
     * @param theUserData
     *            the userData to set
     */
    public void setUserData(byte[] theUserData) {
        userData = theUserData.clone();
    }

    /**
     * @return the firstChild
     */
    public int getFirstChild() {
        return firstChild;
    }

    /**
     * @return whether there are children
     */
    public boolean hasChildren() {
        return firstChild != -1;
    }

    /**
     * @param firstChild
     *            the firstChild to set
     */
    public void setFirstChild(int firstChild) {
        this.firstChild = firstChild;
    }

    /**
     * @return the nextSibling
     */
    public int getNextSibling() {
        return nextSibling;
    }

    /**
     * @return if there are more siblings
     */
    public boolean hasNextSibling() {
        return nextSibling != -1;
    }

    /**
     * @param nextSibling
     *            the nextSibling to set
     */
    public void setNextSibling(int nextSibling) {
        this.nextSibling = nextSibling;
    }

    /**
     * @return the parent
     */
    public int getParent() {
        return parent;
    }

    /**
     * @param parent
     *            the parent to set
     */
    public void setParent(int parent) {
        this.parent = parent;
    }

    @Override
    public TreeNode clone() {
        TreeNode clone = null;
        try {
            clone = (TreeNode) super.clone();
        } catch (CloneNotSupportedException e) {
            assert false : e;
        }
        return clone;
    }

    /**
     * The offset of this TreeNode in the offset.
     */
    private int offset;

    /**
     * The name of this TreeNode. Note, this is not the path. To get the path,
     * one needs to traverse to the parent to construct the path.
     */
    private String name;

    /**
     * Optional, extra data associated with this TreeNode. For example, this is
     * used to store offset and length for a raw genbook.
     */
    private byte[] userData;

    /**
     * The offset of the parent record in the offset. Root nodes are indicated
     * with a value of -1. That is, this TreeNode does not have a parent.
     */
    private int parent;

    /**
     * The offset of the next sibling record in the offset. Final siblings are
     * indicated with a value of -1. That is, this TreeNode does not have a next
     * sibling.
     */
    private int nextSibling;

    /**
     * The offset of the first child record in the offset. Leaf nodes are
     * indicated with a value of -1. That is, this TreeNode does not have any
     * children.
     */
    private int firstChild;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = -2472601787934480762L;

}
