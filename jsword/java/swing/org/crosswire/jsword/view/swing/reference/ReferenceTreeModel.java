package org.crosswire.jsword.view.swing.reference;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.crosswire.common.util.EventListenerList;

/**
 * A TreeModel for the ReferencePane.
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class ReferenceTreeModel implements TreeModel
{
    /**
     * Simple ctor
     */
    public ReferenceTreeModel()
    {
        this.asksAllowsChildren = false;
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeModel#getRoot()
     */
    public Object getRoot()
    {
        return new ReferenceRootTreeNode(this);
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
     */
    public int getIndexOfChild(Object parent, Object child)
    {
        if (parent == null || child == null)
        {
            return -1;
        }
        
        TreeNode parentNode = (TreeNode) parent;
        TreeNode childNode = (TreeNode) child;

        return parentNode.getIndex(childNode);
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
     */
    public Object getChild(Object parent, int index)
    {
        TreeNode parentNode = (TreeNode) parent;
        return parentNode.getChildAt(index);
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
     */
    public int getChildCount(Object parent)
    {
        TreeNode parentNode = (TreeNode) parent;
        return parentNode.getChildCount();
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
     */
    public boolean isLeaf(Object node)
    {
        TreeNode treeNode = (TreeNode) node;

        if (asksAllowsChildren)
        {
            return !treeNode.getAllowsChildren();
        }

        return treeNode.isLeaf();
    }

    /**
     * Invoke this method if you've modified the TreeNodes upon which this
     * model depends.  The model will notify all of its listeners that the
     * model has changed.
     */
    public void reload()
    {
        reload(root);
    }

    /**
     * This sets the user object of the TreeNode identified by path
     * and posts a node changed.  If you use custom user objects in
     * the TreeModel you're going to need to subclass this and
     * set the user object of the changed node to something meaningful.
     */
    public void valueForPathChanged(TreePath path, Object newValue)
    {
        MutableTreeNode aNode = (MutableTreeNode) path.getLastPathComponent();

        aNode.setUserObject(newValue);
        nodeChanged(aNode);
    }

    /**
     * Invoked this to insert newChild at location index in parents children.
     * This will then message nodesWereInserted to create the appropriate
     * event. This is the preferred way to add children as it will create
     * the appropriate event.
     */
    public void insertNodeInto(MutableTreeNode newChild, MutableTreeNode parent, int index)
    {
        parent.insert(newChild, index);

        int[] newIndexs = new int[1];

        newIndexs[0] = index;
        nodesWereInserted(parent, newIndexs);
    }

    /**
     * Message this to remove node from its parent. This will message
     * nodesWereRemoved to create the appropriate event. This is the
     * preferred way to remove a node as it handles the event creation
     * for you.
     */
    public void removeNodeFromParent(MutableTreeNode node)
    {
        MutableTreeNode parent = (MutableTreeNode) node.getParent();

        if (parent == null)
            throw new IllegalArgumentException("node does not have a parent.");

        int[] childIndex = new int[1];
        Object[] removedArray = new Object[1];

        childIndex[0] = parent.getIndex(node);
        parent.remove(childIndex[0]);
        removedArray[0] = node;
        nodesWereRemoved(parent, childIndex, removedArray);
    }

    /**
     * Invoke this method after you've changed how node is to be
     * represented in the tree.
     */
    public void nodeChanged(TreeNode node)
    {
        if (listenerList != null && node != null)
        {
            TreeNode parent = node.getParent();

            if (parent != null)
            {
                int anIndex = parent.getIndex(node);
                if (anIndex != -1)
                {
                    int[] cIndexs = new int[1];

                    cIndexs[0] = anIndex;
                    nodesChanged(parent, cIndexs);
                }
            }
            else if (node == getRoot())
            {
                nodesChanged(node, null);
            }
        }
    }

    /**
     * Invoke this method if you've modified the TreeNodes upon which this
     * model depends.  The model will notify all of its listeners that the
     * model has changed below the node <code>node</code>.
     */
    public void reload(TreeNode node)
    {
        if (node != null)
        {
            fireTreeStructureChanged(this, getPathToRoot(node), null, null);
        }
    }

    /**
     * Invoke this method after you've inserted some TreeNodes into
     * node.  childIndices should be the index of the new elements and
     * must be sorted in ascending order.
     */
    public void nodesWereInserted(TreeNode node, int[] childIndices)
    {
        if (listenerList != null && node != null && childIndices != null && childIndices.length > 0)
        {
            int cCount = childIndices.length;
            Object[] newChildren = new Object[cCount];

            for (int counter = 0; counter < cCount; counter++)
            {
                newChildren[counter] = node.getChildAt(childIndices[counter]);
            }
            fireTreeNodesInserted(this, getPathToRoot(node), childIndices, newChildren);
        }
    }

    /**
     * Invoke this method after you've removed some TreeNodes from
     * node.  childIndices should be the index of the removed elements and
     * must be sorted in ascending order. And removedChildren should be
     * the array of the children objects that were removed.
     */
    public void nodesWereRemoved(TreeNode node, int[] childIndices, Object[] removedChildren)
    {
        if (node != null && childIndices != null)
        {
            fireTreeNodesRemoved(this, getPathToRoot(node), childIndices, removedChildren);
        }
    }

    /**
     * Invoke this method after you've changed how the children identified by
     * childIndicies are to be represented in the tree.
     */
    public void nodesChanged(TreeNode node, int[] childIndices)
    {
        if (node != null)
        {
            if (childIndices != null)
            {
                int cCount = childIndices.length;

                if (cCount > 0)
                {
                    Object[] cChildren = new Object[cCount];

                    for (int counter = 0; counter < cCount; counter++)
                    {
                        cChildren[counter] = node.getChildAt(childIndices[counter]);
                    }
                    fireTreeNodesChanged(this, getPathToRoot(node), childIndices, cChildren);
                }
            }
            else if (node == getRoot())
            {
                fireTreeNodesChanged(this, getPathToRoot(node), null, null);
            }
        }
    }

    /**
     * Invoke this method if you've totally changed the children of
     * node and its childrens children...  This will post a
     * treeStructureChanged event.
     */
    public void nodeStructureChanged(TreeNode node)
    {
        if (node != null)
        {
            fireTreeStructureChanged(this, getPathToRoot(node), null, null);
        }
    }

    /**
     * Builds the parents of node up to and including the root node,
     * where the original node is the last element in the returned array.
     * The length of the returned array gives the node's depth in the
     * tree.
     * 
     * @param aNode the TreeNode to get the path for
     */
    public TreeNode[] getPathToRoot(TreeNode aNode)
    {
        return getPathToRoot(aNode, 0);
    }

    /**
     * Builds the parents of node up to and including the root node,
     * where the original node is the last element in the returned array.
     * The length of the returned array gives the node's depth in the
     * tree.
     * 
     * @param aNode  the TreeNode to get the path for
     * @param depth  an int giving the number of steps already taken towards
     *        the root (on recursive calls), used to size the returned array
     * @return an array of TreeNodes giving the path from the root to the
     *         specified node 
     */
    protected TreeNode[] getPathToRoot(TreeNode aNode, int depth)
    {
        TreeNode[] retNodes;
        // This method recurses, traversing towards the root in order
        // size the array. On the way back, it fills in the nodes,
        // starting from the root and working back to the original node.

        // Check for null, in case someone passed in a null node, or
        // they passed in an element that isn't rooted at root.
        if (aNode == null)
        {
            if (depth == 0)
            {
                return null;
            }
            else
            {
                retNodes = new TreeNode[depth];
            }
        }
        else
        {
            depth++;
            if (aNode == root)
            {
                retNodes = new TreeNode[depth];
            }
            else
            {
                retNodes = getPathToRoot(aNode.getParent(), depth);
            }
            retNodes[retNodes.length - depth] = aNode;
        }
        return retNodes;
    }

    /**
     * Adds a listener for the TreeModelEvent posted after the tree changes.
     * @see ReferenceTreeModel#removeTreeModelListener(TreeModelListener)
     * @param l the listener to add
     */
    public void addTreeModelListener(TreeModelListener l)
    {
        listenerList.add(TreeModelListener.class, l);
    }

    /**
     * Removes a listener previously added with <B>addTreeModelListener()</B>.
     * @see ReferenceTreeModel#addTreeModelListener(TreeModelListener)
     * @param l the listener to remove
     */
    public void removeTreeModelListener(TreeModelListener l)
    {
        listenerList.remove(TreeModelListener.class, l);
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     *
     * @param source the node being changed
     * @param path the path to the root node
     * @param childIndices the indices of the changed elements
     * @param children the changed elements
     * @see EventListenerList
     */
    protected void fireTreeNodesChanged(Object source, Object[] path, int[] childIndices, Object[] children)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == TreeModelListener.class)
            {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path, childIndices, children);
                ((TreeModelListener) listeners[i + 1]).treeNodesChanged(e);
            }
        }
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     *
     * @param source the node where new elements are being inserted
     * @param path the path to the root node
     * @param childIndices the indices of the new elements
     * @param children the new elements
     * @see EventListenerList
     */
    protected void fireTreeNodesInserted(Object source, Object[] path, int[] childIndices, Object[] children)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == TreeModelListener.class)
            {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path, childIndices, children);
                ((TreeModelListener) listeners[i + 1]).treeNodesInserted(e);
            }
        }
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     *
     * @param source the node where elements are being removed
     * @param path the path to the root node
     * @param childIndices the indices of the removed elements
     * @param children the removed elements
     * @see EventListenerList
     */
    protected void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices, Object[] children)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == TreeModelListener.class)
            {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path, childIndices, children);
                ((TreeModelListener) listeners[i + 1]).treeNodesRemoved(e);
            }
        }
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     *
     * @param source the node where the tree model has changed
     * @param path the path to the root node
     * @param childIndices the indices of the affected elements
     * @param children the affected elements
     * @see EventListenerList
     */
    protected void fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == TreeModelListener.class)
            {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path, childIndices, children);
                ((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
            }
        }
    }

    /*
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     *
     * @param source the node where the tree model has changed
     * @param path the path to the root node
     * @see EventListenerList
     *
    private void fireTreeStructureChanged(Object source, TreePath path)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == TreeModelListener.class)
            {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path);
                ((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
            }
        }
    }

    /**
     * Root of the tree.
     */
    protected TreeNode root;

    /**
     * Listeners.
     */
    protected EventListenerList listenerList = new EventListenerList();

    /**
     * Determines how the <code>isLeaf</code> method figures
     * out if a node is a leaf node. If true, a node is a leaf 
     * node if it does not allow children. (If it allows 
     * children, it is not a leaf node, even if no children
     * are present.) That lets you distinguish between <i>folder</i>
     * nodes and <i>file</i> nodes in a file system, for example.
     * <p>
     * If this value is false, then any node which has no 
     * children is a leaf node, and any node may acquire 
     * children.
     */
    protected boolean asksAllowsChildren;
}
