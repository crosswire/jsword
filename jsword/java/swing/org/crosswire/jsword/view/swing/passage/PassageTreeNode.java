
package org.crosswire.jsword.view.swing.passage;

import java.util.Iterator;
import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageConstants;
import org.crosswire.jsword.passage.PassageEvent;
import org.crosswire.jsword.passage.PassageListener;
import org.crosswire.common.util.IteratorEnumeration;

/**
 * A PassageTreeNode extends TreeNode to Model a Passage.
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
public class PassageTreeNode implements TreeNode, PassageListener
{
    /**
     * Simple ctor
     */
    public PassageTreeNode(Passage ref, JTree tree)
    {
        this.ref = ref;
        this.tree = tree;
        ref.addPassageListener(this);
    }

    /**
     * Returns the child <code>TreeNode</code> at index i
     */
    public TreeNode getChildAt(int index)
    {
        return new VerseRangeTreeNode(ref.getVerseRangeAt(index, PassageConstants.RESTRICT_CHAPTER));
    }

    /**
     * Returns the number of children <code>TreeNode</code>s the receiver
     * contains.
     */
    public int getChildCount()
    {
        return ref.countRanges(PassageConstants.RESTRICT_CHAPTER);
    }

    /**
     * Returns the parent <code>TreeNode</code> of the receiver.
     */
    public TreeNode getParent()
    {
        return this;
    }

    /**
     * Returns the index of <code>node</code> in the receivers children.
     * If the receiver does not contain <code>node</code>, -1 will be
     * returned.
     */
    public int getIndex(TreeNode node)
    {
        int count = 0;
        Iterator it = ref.rangeIterator(PassageConstants.RESTRICT_NONE);

        while (it.hasNext())
        {
            if (it.next() == node)
            {
                return count;
            }

            count++;
        }

        return -1;
    }

    /**
     * Returns true if the receiver allows children.
     */
    public boolean getAllowsChildren()
    {
        return true;
    }

    /**
     * Returns true if the receiver is a leaf.
     */
    public boolean isLeaf()
    {
        return false;
    }

    /**
     * Sent after stuff has been added to the Passage.
     * More info about what and where can be had from the Event
     * @param ev a PassageEvent encapuslating the event information
     */
    public void versesAdded(PassageEvent ev)
    {
    }

    /**
     * Sent after stuff has been removed from the Passage.
     * More info about what and where can be had from the Event
     * @param ev a PassageEvent encapuslating the event information
     */
    public void versesRemoved(PassageEvent ev)
    {
    }

    /**
     * Sent after verses have been symultaneously added and removed from the Passage.
     * More info about what and where can be had from the Event
     * @param ev a PassageEvent encapuslating the event information
     */
    public void versesChanged(PassageEvent ev)
    {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.nodeStructureChanged(this);
    }

    /**
     * Returns the children of the reciever as an Enumeration.
     */
    public Enumeration children()
    {
        return new IteratorEnumeration(ref.rangeIterator(PassageConstants.RESTRICT_NONE));
    }

    /**
     * Returns the children of the reciever as an Enumeration.
     */
    public String toString()
    {
        return ref.getOverview();
    }

    /**
     * The Passage to be displayed
     */
    protected Passage ref = null;

    /**
     * The Passage to be displayed
     */
    protected JTree tree = null;
}
